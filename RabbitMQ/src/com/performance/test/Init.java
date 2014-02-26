package com.performance.test;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class Init {
	static String host = "172.16.217.148";
	static String QUEUE_NAME = "";
    static String EXCHANGE_NAME = "";
    static String ROUTINGKEY = "";
    
	public Init() {
		try{
			ConnectionFactory factory;
			Connection connection;
			// 创建链接工程
			factory = new ConnectionFactory();
	        factory.setHost(host);
	        // 创建一个新的消息队列服务器实体的连接
	        connection = factory.newConnection();
	        
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
		}catch(Exception e){
			
		}
	}
}
