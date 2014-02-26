package com.performance.main;

import java.io.IOException;

import com.rabbitmq.client.QueueingConsumer;

/**
 * 读取队列的程序端，实现了Runnable接口。
 * @author kiven
 *
 */
public class MyConsumer extends EndPoint implements Runnable{
	
	public MyConsumer() throws IOException{
		super();
		consumer = new QueueingConsumer(channel);
	}
	
	public void run() {
		int msgNum = 0;
		try {
			//取消 autoACK
			channel.basicConsume(QUEUE_NAME, false, consumer);
			while(true){
				try {
	    			//获取消息，如果没有消息，这一步将会一直阻塞 
	    	        QueueingConsumer.Delivery delivery = consumer.nextDelivery();
	    	        String message = new String(delivery.getBody());
	    	        msgNum++;
	    	        System.out.println(message+" received.");
	    	        //确认消息已经收到.必须
	    	        channel.basicAck(delivery.getEnvelope().getDeliveryTag(),false); 
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}