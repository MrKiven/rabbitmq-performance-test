package com.performance.test;

import java.io.IOException;
import java.util.Random;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ShutdownListener;
import com.rabbitmq.client.ShutdownSignalException;

public class P {

	public static void main(String[] args) throws Exception {
		try {
			ConnectionFactory factory = new ConnectionFactory();
			factory.setHost("172.16.217.148");
			Connection conn = factory.newConnection();
			conn.addShutdownListener(new ShutdownListener() {

				@Override
				public void shutdownCompleted(ShutdownSignalException cause) {
					cause.printStackTrace();
					System.exit(1);
				}
			});

			for (int i = 0; i < 5; i++) {
				new TestThread(conn).start();
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
		}
	}

	static class TestThread extends Thread {
		private Random r;
		private Connection conn;

		public TestThread(Connection conn) {
			this.conn = conn;
			this.r = new Random();
		}

		@Override
		public void run() {
			int i = 0;
			while (true) {
				try {
					Channel c = conn.createChannel();

					if ((i++ % 1000) == 0) {
						System.out.println("Opened " + this.toString() + ", i = " + i);
					}
					c.queueDelete("non-existing-" + r.nextInt());
				} catch (IOException e) {
				}
			}
		}
	}
}
