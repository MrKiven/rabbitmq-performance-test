package com.performance.main;

import java.io.IOException;

import com.rabbitmq.client.MessageProperties;

/**
 * 生产者
 * @author Kiven
 * 生产者类的任务是向队列里写一条消息。我们使用Apache Commons Lang把可序列化的Java对象转换成 byte 数组。
 */
public class MyProducer extends EndPoint{
	
	public MyProducer() throws IOException{
		super();
	}

	public void sendMessage(byte[] message) throws IOException {
	    channel.basicPublish(EXCHANGE_NAME,ROUTINGKEY, MessageProperties.PERSISTENT_TEXT_PLAIN, message);
	}
}