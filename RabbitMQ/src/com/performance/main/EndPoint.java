package com.performance.main;

import java.io.IOException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

/**
 * 抽象类
 * @author Kiven
 * 我们首先写一个类，将产生产者和消费者统一为 EndPoint类型的队列。不管是生产者还是消费者， 连接队列的代码都是一样的，这样可以通用一些。
 */
public abstract class EndPoint{
	
	public String host = "172.16.217.148";
	public Channel channel;
	public Connection connection;
	public QueueingConsumer consumer ;;
    
	public String QUEUE_NAME = "ABC";
	public String EXCHANGE_NAME = "ABC";
	public String ROUTINGKEY = "ABC";	
	
    public EndPoint() throws IOException{
         //Create a connection factory
         ConnectionFactory factory = new ConnectionFactory();    
         //hostname of your rabbitmq server
         factory.setHost(host);
         // 创建一个新的消息队列服务器实体的连接
	     connection = factory.newConnection();
	     // 创建一个新的消息读写的通道
	     channel = connection.createChannel();
	     // 声明exchange模式并且为持久化exchange
	     channel.exchangeDeclare(EXCHANGE_NAME, "topic", true);
	     channel.queueDeclare(QUEUE_NAME, true, false, false, null);
	     //绑定
	     channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, ROUTINGKEY);
    }
	
    /**
     * 关闭channel和connection。并非必须，因为隐含是自动调用的。 
     * @throws IOException
     */
     public void close() throws IOException{
         this.channel.close();
         this.connection.close();
     }
}

