package com.kiven.test;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;
import com.rabbitmq.client.QueueingConsumer;
public class ThreadPools {
	static ToLog tolog = new ToLog();
	
	private final static String host = Public.host;
	private final static String QUEUE_NAME = Public.QUEUE_NAME;
	private final static String EXCHANGE_NAME = Public.EXCHANGE_NAME;
	private final static String ROUTINGKEY = Public.ROUTINGKEY;
	
    static int size = Public.size;				//写入数据的大小,单位：字节
    static int msgNums = 0;
    
    private static ConnectionFactory factory_send,pactory_recv;
    private static Connection conn_send,conn_recv;
    
    static byte[] testdata;
    
    
    public static void main(String[] args) {
    	//设置数据大小
    	testdata = new byte[size];
		for(int i=0;i<size;i++){
			testdata[i] = 'A';
		}
		
		// 创建链接工程
		factory_send = new ConnectionFactory();
		pactory_recv = new ConnectionFactory();
        factory_send.setHost(host);
        pactory_recv.setHost(host);
        try{
	        // 创建一个新的消息队列服务器实体的连接
	        conn_send = factory_send.newConnection();
	        conn_recv = pactory_recv.newConnection();
        }catch(Exception e){
        	
        }
        
        System.out.println("正在测试...");
        
		// 创建一个可重用固定线程数的线程池
		ExecutorService pool = Executors.newFixedThreadPool(7);
		
		
		// 创建实现了Runnable接口对象，Thread对象当然也实现了Runnable接口
		Thread t1 = new S("发送1");
		Thread t3 = new S("发送2");
		Thread t7 = new S("发送3");
		Thread t2 = new R("接收1");
		Thread t4 = new R("接收2");
		Thread t5 = new R("接收3");
		Thread t6 = new R("接收4");
		
		// 将线程放入池中进行执行
		pool.execute(t1);
		pool.execute(t3);
		pool.execute(t7);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		pool.execute(t2);
		pool.execute(t4);
		pool.execute(t5);
		pool.execute(t6);

		// 关闭线程池
		pool.shutdown();
    }
    
    
    //======内部类
    //发送消息
    static class S extends Thread {
    	public S(String name) { 
            super(name); 
        } 
        public void run() {        	
        	// 创建一个新的消息读写的通道
    		try {
				Channel channel = conn_send.createChannel();
				// 声明exchange模式并且为持久化exchange
		        //channel.exchangeDeclare(EXCHANGE_NAME, "topic", true);
		        // declare a queue (声明一个队列)  
		        // 参数分别为
		        // 1,队列的名字  
		        // 2,是否为一个持久的队列，持久的队列在服务重新启动的后依然存在  
		        // 3,如果为true，那么就在此次连接中建立一个独占的队列  
		        // 4,是否为自动删除  
		        // 5,队列的一些其他构造参数 
		        //channel.queueDeclare(QUEUE_NAME, true, false, false, null);
		        //绑定
		        //channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, ROUTINGKEY);
				
				long startTime = System.currentTimeMillis();
	    		int count = 0;
	        	while(true){
	        		if(System.currentTimeMillis()-startTime<=5000){
		        		//发送消息  MessageProperties.PERSISTENT_TEXT_PLAIN:将消息设为持久化
		    			channel.basicPublish(EXCHANGE_NAME, ROUTINGKEY, MessageProperties.PERSISTENT_TEXT_PLAIN, testdata);
		    			count++;
	        		}else{
    					startTime = System.currentTimeMillis();
    					tolog.toLog(this.getName() + "  5秒发送的消息数:" + count);
    					tolog.toLog("发送消息QPS:" + count/5);
    					tolog.toLog("==================================");
    					tolog.toLog("\r\n");
    					count = 0;
    				}
	        	}
    		} catch (IOException e) {
				e.printStackTrace();
			}
        }
    }

    //接受消息
    static class R extends Thread{
    	public R(String name) { 
            super(name); 
        } 
    	public void run(){
    		try{
	    		// 创建一个新的消息读写的通道
	    		Channel channel = conn_recv.createChannel();
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
	    		int count = 0;
	    		long startTime = System.currentTimeMillis();
	    		while(true){
	    			try{
	    				if(System.currentTimeMillis()-startTime<=5000){
		    				QueueingConsumer.Delivery delivery = consumer.nextDelivery();
		    				String message = new String(delivery.getBody());
		    				//确认消息已经收到.必须
		    				channel.basicAck(delivery.getEnvelope().getDeliveryTag(),false);
		    				count++;
	    				}else{
	    					startTime = System.currentTimeMillis();
	    					tolog.toLog(this.getName() + "  5秒收到的消息数:" + count);
	    					tolog.toLog("接收消息QPS:" + count/5);
	    					tolog.toLog("==================================");
	    					tolog.toLog("\r\n");
	    					count = 0;
	    				}
	    			}catch(Exception e){
	    				
	    			}
	    		}
	    		//==========================接受消息结束=================================
    		}catch(Exception e){
    			
    		}
    	}
    }
}



