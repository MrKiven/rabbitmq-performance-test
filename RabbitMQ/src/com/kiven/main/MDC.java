package com.kiven.main;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.QueueingConsumer;

public class MDC {
	private int thdnum = 10;
	
	private String host = "172.16.217.148";
	private String user ="guest";
	private String pwd ="guest";
	
	public void receiver(){
		MC mc = new MC(host,user,pwd);
		for(int i =0;i<thdnum;++i){
			new Thread(mc,i+"").start();
		}
	}
	
	public static void main(String args[]){
		new MDC().receiver();
	}
	
	class MC implements Runnable{
		private String host,user,pwd;
		
		public MC(){
			
		}
		
		public MC(String _host,String _user,String _pwd){
			host = _host;
			user = _user;
			pwd = _pwd;
		}
		
		public void run(){
			fetch(Thread.currentThread().getName());
		}
		
		public void fetch(String thdno){
			String exchangeName = "directexchange";
			String queueName = thdno;
			int rcvNum = 0;
			
			ConnectionFactory factory = new ConnectionFactory();
			factory.setHost(host);
			factory.setUsername(user);
			factory.setPassword(pwd);
			
			try{
				Connection connection = factory.newConnection();
				Channel channel = connection.createChannel();
				channel.exchangeDeclare(exchangeName, "direct");
				channel.queueDeclare(queueName,true,false,false,null);
				
				QueueingConsumer consumer = new QueueingConsumer(channel);
				channel.basicConsume(queueName, true,consumer);
				
				long last,current = System.currentTimeMillis();
				boolean flag = true;
				while(flag){
					last = current;
					QueueingConsumer.Delivery delivery = consumer.nextDelivery(30000);
					current = System.currentTimeMillis();
					//主要是进行超时判断，超时后自动关闭，不要让他一直执行
					if(current - last >= 30000){
						flag = false;
					}else if(delivery != null){
						Envelope envelope = delivery.getEnvelope();						
						++rcvNum;
					}
				}
				channel.close();
				connection.close();
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				System.out.println("thread id:" + thdno + " receive message num:" + rcvNum);
			}
		}
	}
}
