package com.adcc.tool.amqssimulator;

import com.adcc.tool.amqssimulator.view.AMQSMainView;

import com.ibm.mq.MQException;
import org.apache.log4j.PropertyConfigurator;

/**
 * 程序启动类
 */
public class App {
	public static void main( String[] args ){
		PropertyConfigurator.configure("./conf/log4j.properties");
		MQException.log = null;
		AMQSMainView view = new AMQSMainView();
		view.open();
	}
}
