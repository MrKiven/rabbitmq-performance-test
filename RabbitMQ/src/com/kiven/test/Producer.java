package com.kiven.test;

import java.io.IOException;
import java.io.Serializable;
import org.apache.commons.lang3.SerializationUtils;

/**
 * 生产者
 * @author Kiven
 * 生产者类的任务是向队列里写一条消息。我们使用Apache Commons Lang把可序列化的Java对象转换成 byte 数组。
 */
public class Producer extends EndPoint{
	
	public Producer(String endPointName) throws IOException{
		super(endPointName);
	}

	public void sendMessage(Serializable object) throws IOException {
	    channel.basicPublish("",endPointName, null, SerializationUtils.serialize(object));
	}
}
