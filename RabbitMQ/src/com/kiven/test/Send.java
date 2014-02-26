package com.kiven.test;
import java.io.IOException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

/**
 * 发送消息类
 * @author Kiven
 *
 */
public class Send{
	private final static String host = Public.host;
    private final static String QUEUE_NAME = Public.QUEUE_NAME;
    private final static String EXCHANGE_NAME = Public.EXCHANGE_NAME;
    private final static String ROUTINGKEY = Public.ROUTINGKEY;
    /**
     * 线程设置
     */
    static int threads = Public.threads;		// 运行的测试线程数   
    static int runs = Public.runs;  			// 每个线程运行的次数
    static int size = Public.size;				//写入数据的大小,单位：字节
    static int msgNums = 0;
    
    private static ConnectionFactory factory;
    private static Connection connection;
    
    
    static long sendTime = 0;
    static long recvTime = 0;
    
    static Integer myLock;   		// 锁定一下计数器
    static byte[] testdata;

    public static void main(String[] args) throws Exception {
    	//设置数据大小
    	testdata = new byte[size];
		for(int i=0;i<size;i++){
			testdata[i] = 'A';
		}

		// 创建链接工程
		factory = new ConnectionFactory();
        factory.setHost(host);
        // 创建一个新的消息队列服务器实体的连接
        connection = factory.newConnection();
        
        
        
        myLock = new Integer(threads);
    	
        System.out.println("正在测试...");
        
        for (int i = 0; i < threads; i++) {
            new SendThread().start();
        }
        
    }
    
    private static class SendThread extends Thread{
		public void run(){
			try{
				// 创建一个新的消息读写的通道
				Channel channel = connection.createChannel();
		        // 声明exchange模式并且为持久化exchange
		        channel.exchangeDeclare(EXCHANGE_NAME, "topic", true);
		        // declare a queue (声明一个队列)  
		        // 参数分别为
		        // 1,队列的名字  
		        // 2,是否为一个持久的队列，持久的队列在服务重新启动的后依然存在  
		        // 3,如果为true，那么就在此次连接中建立一个独占的队列  
		        // 4,是否为自动删除  
		        // 5,队列的一些其他构造参数 
		        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
		        //绑定
		        channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, ROUTINGKEY);	        
		        //==========================发送消息开始=================================
	    		long startTime = System.currentTimeMillis();
	    		int counts = 0;
	    		for(int i=0;i<runs;i++){
	        		try {
	        			//发送消息  MessageProperties.PERSISTENT_TEXT_PLAIN:将消息设为持久化
	        			channel.basicPublish(EXCHANGE_NAME, ROUTINGKEY, MessageProperties.PERSISTENT_TEXT_PLAIN, testdata);
	        			counts++;
	        			///System.out.println(" [x] Sent '" + new String(testdata,"utf-8") + "'");
	    			} catch (Exception e) {
	    				e.printStackTrace();
	    			}
	    		}
	    		long endTime = System.currentTimeMillis() - startTime;
	    		
	    		synchronized (myLock) {
	    			//发送消息总时间
	            	sendTime += endTime;
	            	msgNums += counts;
	            	
	            	myLock--;
	    			if(myLock.equals(0)){
	    				try {
	    					channel.close();
	    			        connection.close();
						} catch (Exception e) {
							e.printStackTrace();
						}
		    			System.out.println("测试完成!\n启动线程数:【" + threads + "】\t每个线程发送消息数:【" + runs + "】\t所有线程实际发送消息数:【" + msgNums + "】\t发送消息包大小:【"+size+" byte】");   
		                System.out.println("发送消息处理时间:【" + sendTime + " ms】\t处理发送消息速度(QPS):每秒【" + runs * threads * 1000 / sendTime + " 次】\t发送消息的平均时间:【"+sendTime/(runs*threads)+" ms】");
	    			}
	            }
	    		//==========================发送消息结束=================================
			}catch(Exception e){
				
			}	
		}		
    }

    
    public static void req() throws IOException{
    	ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        
        myLock = new Integer(threads);
        
        channel.basicPublish("", QUEUE_NAME, null, testdata);
        System.out.println(" [x] Sent '" + testdata + "'");

        channel.close();
        connection.close();
    }
}