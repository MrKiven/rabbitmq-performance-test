package com.performance.main;

import java.io.IOException;
import java.sql.SQLException;

public class Main {
	int size = 1024;
	int threads = 1;
	int runs = 10000;
	
	public Main() throws Exception{
		byte[] message = new byte[size];
		for(int i=0;i<size;i++){
			message[i] = 'A';
		}
		
//		MyConsumer consumer = new MyConsumer();
//		Thread consumerThread = new Thread(consumer);
//		consumerThread.start();

		
		MyProducer producer = new MyProducer();
		for (int i = 0; i < runs; i++) {			
			producer.sendMessage(message);			
			//System.out.println(" [x] Sent '" + new String(message,"utf-8") + "'");
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