package com.kiven.main;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class MDP {
	/**
	 * 
	 */
	private int msgnum = 1000000;
	private int thdnum = 10;
	private int perthdnum = 0;
	
	private String host = "172.16.217.148";
	private String user ="guest";
	private String pwd ="guest";
	
	public void dipatcher(){
		perthdnum = msgnum / thdnum;
		MP mp = new MP(perthdnum,host,user,pwd);
		
		for(int i =0;i<thdnum;++i){
			new Thread(mp,i+"").start();
		}
	}
	
	public static void main(String args[]){
		new MDP().dipatcher();
	}
	
	class MP implements Runnable{
		private int perthdnum;
		private String host,user,pwd;
		
		public MP(){
			perthdnum = 1000;
		}
		
		public MP(int _perthdnum,String _host,String _user,String _pwd){
			perthdnum = _perthdnum;
			host = _host;
			user = _user;
			pwd = _pwd;
		}
		
		public void run(){
			publish(Thread.currentThread().getName());
		}
		
		public void publish(String thdno){
			String exchangeName = "directexchange";
			String queueName = thdno;
			String routingKey = thdno;
			int sendNum = 0;
			
			ConnectionFactory factory = new ConnectionFactory();
			factory.setHost(host);
			factory.setUsername(user);
			factory.setPassword(pwd);
			
			try{
				Connection connection = factory.newConnection();
				Channel channel = connection.createChannel();
				channel.exchangeDeclare(exchangeName, "direct");
				channel.queueDeclare(queueName,true,false,false,null);
				channel.queueBind(queueName, exchangeName, routingKey);
				
				for(int i=0;i<perthdnum;++i){
					//每一个message确保唯一
					byte[] messageBodyBytes = ("thread id:" + thdno + " message id:" + (i + perthdnum*Integer.parseInt(thdno))).getBytes();
					channel.basicPublish(exchangeName, routingKey, new AMQP.BasicProperties.Builder().contentType("text/plain").deliveryMode(2).build(), messageBodyBytes);
					//System.out.println(" [x] Sent '" + new String(messageBodyBytes,"utf-8") + "'");
					sendNum = i+1;
				}
				channel.close();
				connection.close();
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				System.out.println("thread id:" + thdno + " send message num:" + sendNum);
			}
		}
	}
}
