package com.kiven.test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;

public class Main {
	private String queue = "queue";
	
	public Main() throws Exception{
		
		QueueConsumer consumer = new QueueConsumer(queue);
		Thread consumerThread = new Thread(consumer);
		consumerThread.start();

		
		Producer producer = new Producer(queue);
		for (int i = 0; i < 1000; i++) {
			HashMap<String, Integer> message = new HashMap<String, Integer>();
			message.put("message number", i);
			
			producer.sendMessage(message);
			
			System.out.println("Message Number "+ i +" sent.");
			//Thread.sleep(1000);
		}
	}
	
	
	/**
	 * @param args
	 * @throws SQLException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws Exception{
	  new Main();
	}
}

