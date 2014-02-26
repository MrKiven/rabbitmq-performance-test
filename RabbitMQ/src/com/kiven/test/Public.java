package com.kiven.test;


public class Public {
	public static String host = "172.16.177.180";
	public static String QUEUE_NAME = "MyTest";
	public static String EXCHANGE_NAME = "MyExchange";
	public static String ROUTINGKEY = "MyRoutingKey";
	
    /**
     * 线程设置
     */
	public static int threads = 1;				// 运行的测试线程数   
	public static int runs = 1;  			// 每个线程运行的次数
	public static int size = 10;				//写入数据的大小,单位：字节
	
}
