package com.kiven.test;

import java.io.IOException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class Declare {
	private final static String host = Public.host;
    private final static String QUEUE_NAME = Public.QUEUE_NAME;
    private final static String EXCHANGE_NAME = Public.EXCHANGE_NAME;
    private final static String ROUTINGKEY = Public.ROUTINGKEY;
    
    private static ConnectionFactory factory;
    private static Connection connection;
    
    public Declare(){
    	// 创建链接工程
		factory = new ConnectionFactory();
        factory.setHost(host);
        
        try {
        	// 创建一个新的消息队列服务器实体的连接
			connection = factory.newConnection();
			// 创建一个新的消息读写的通道
			Channel channel = connection.createChannel();
	        // 声明exchange模式并且为持久化exchange
	        channel.exchangeDeclare(EXCHANGE_NAME, "topic", true);
	        // declare a queue (声明一个队列)
	        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
	        //绑定
	        channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, ROUTINGKEY);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}
