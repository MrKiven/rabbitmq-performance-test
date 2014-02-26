package com.kiven.test;

import java.io.IOException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;
/**
 * 接受消息类
 * @author Kiven
 *
 */
public class Recv {
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
    
    private static ConnectionFactory factory;
    private static Connection connection;
    
    
    static long sendTime = 0;
    static long recvTime = 0;
    static Integer myLock;   		// 锁定一下计数器

    public static void main(String[] argv) throws Exception{
    	
    	// 创建链接工程
		factory = new ConnectionFactory();
        factory.setHost(host);
        // 创建一个新的消息队列服务器实体的连接
        connection = factory.newConnection();
        
        myLock = new Integer(threads);
    	
        System.out.println("正在测试...");
        
        for (int i = 0; i < threads; i++) {
            new RecvThread().start();
        }
    }
    
    
    private static class RecvThread extends Thread{
    	public void recv() throws IOException{
    		// 创建一个新的消息读写的通道
    		Channel channel = connection.createChannel();
    		// 声明路由模式
            //channel.exchangeDeclare(EXCHANGE_NAME, "topic",true);
            // declare a queue (声明一个队列)  
            //channel.queueDeclare(QUEUE_NAME, true, false, false, null);
            // 绑定
	        //channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, ROUTINGKEY);
	        
	        QueueingConsumer consumer = new QueueingConsumer(channel);
	        
	        //取消 autoAck  
            boolean autoAck = false ; 
    		channel.basicConsume(QUEUE_NAME, autoAck, consumer);
    		
    		//==========================接受消息开始=================================
    		long startTime = System.currentTimeMillis();
    		for(int i=0;i<runs;i++){
        		try {
        			//获取消息，如果没有消息，这一步将会一直阻塞 
        	        QueueingConsumer.Delivery delivery = consumer.nextDelivery();
        	        String message = new String(delivery.getBody());
        	        System.out.println(message);
        	        //确认消息已经收到.必须
        	        channel.basicAck(delivery.getEnvelope().getDeliveryTag(),false); 
                    size = message.getBytes().length;
    			} catch (Exception e) {
    				e.printStackTrace();
    			}
    		}
    		long endTime = System.currentTimeMillis() - startTime;
//    		int count = 0;
//    		while(true){
//    			try{
//    				if(System.currentTimeMillis()-startTime<=5000){
//	    				QueueingConsumer.Delivery delivery = consumer.nextDelivery();
//	    				String message = new String(delivery.getBody());
//	    				channel.basicAck(delivery.getEnvelope().getDeliveryTag(),false);
//	    				count++;
//    				}else{
//    					startTime = System.currentTimeMillis();
//    					System.out.println("5秒收到的消息数:" + count);
//    					count = 0;
//    				}
//    			}catch(Exception e){
//    				
//    			}
//    		}
    		
    		
    		synchronized (myLock) {
    			//接受消息总时间
    			recvTime += endTime;
    			
    			myLock--;
    			if(myLock.equals(0)){
    				try {
    					channel.close();
    			        connection.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
	    			System.out.println("测试完成!\n启动线程数:【" + threads + "】\t接收消息总数:【" + runs*threads + "】\t接收消息包大小:【"+size+" byte】");   
	                System.out.println("接收消息处理时间:【" + recvTime + " ms】\t处理接收消息速度(QPS):每秒【" + runs * threads * 1000 / recvTime + " 次】\t接收消息的平均时间:【"+recvTime/(runs*threads)+" ms】");
    			}
            }
    		//==========================接受消息结束=================================
    	}
    	
    	public void run(){
    		try {
				recv();
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
		
    }
    
    public static void res() throws Exception{
    	ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        QueueingConsumer consumer = new QueueingConsumer(channel);
        channel.basicConsume(QUEUE_NAME, true, consumer);

        while (true) {
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
            String message = new String(delivery.getBody());
            System.out.println(" [x] Received '" + message + "'");
            Thread.sleep(1000);
        }
    }
}
