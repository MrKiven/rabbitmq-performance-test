package com.kiven.test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/*
 * 日志类
 * 当运行遇到错误后会生成一个error的日志文件,方便查找问题
 */
public class ToLog {
	
	static GregorianCalendar time = new GregorianCalendar();
	
	private static final String getToday = time.get(Calendar.YEAR)+"-"+(time.get(Calendar.MONTH)+1)+"-"+time.get(Calendar.DAY_OF_MONTH)+"-"+time.get(Calendar.HOUR_OF_DAY)+time.get(Calendar.MINUTE)+time.get(Calendar.SECOND)+"-";
	
	private static final String filePath = "log\\"+getToday+".log";
	
	//写入文件
	public void toLog(String message){
//		StackTraceElement stack[] = (new Throwable()).getStackTrace();
//		StackTraceElement s = stack[1];
//		
//		String headerMessage = s.getClassName()+"."+s.getMethodName()+"()"+"★LineNum:"+s.getLineNumber()+"\r\nMessage:";
//		
//		headerMessage = addDateTimeHeader(headerMessage);
//		message = headerMessage + message;
		
		FileWriter fw = null;
		File file = null;
		
		try{
			file = new File(filePath);
			fw = new FileWriter(file,true);
			fw.write(message + "\r\n");
		}catch(IOException ie){
			ie.printStackTrace();
		}finally{
			try{
				fw.close();
			}catch(IOException ie){
				ie.printStackTrace();
			}
		}
	}

	@SuppressWarnings("deprecation")
	public String addDateTimeHeader(String headerMessage) {
		String dateTimeHeader = new Date().toLocaleString()+"★";
		return dateTimeHeader += headerMessage;
	}
	
	
//	public static void main(String args[]){
//		ToLog log = new ToLog();
//		String message = "这只是测试";
//		log.toLog(message);
//	}
}
