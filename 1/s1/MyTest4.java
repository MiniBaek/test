package com.test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import com.google.gson.Gson;

public class MyServer implements Runnable{

	int port;
	String routingRuleInfoStr;
	
	public MyServer(int port, String routingRuleInfo) {
		this.port = port;
		this.routingRuleInfoStr = routingRuleInfo;
	}
	
	public MyServer() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {

		MyServer myServer = new MyServer();
		String routingRuleInfoStr;
		RoutingInfo routingInfo;
		int port;
			
		String[] proxFileNmArray = {"Proxy-1.json","Proxy-2.json","Proxy-3.json"};
		for(String fileName : proxFileNmArray) {
			routingRuleInfoStr = myServer.readFile(fileName);
			routingInfo = new Gson().fromJson(routingRuleInfoStr, RoutingInfo.class);
			port = routingInfo.getPort();
			Thread t2 = new Thread(new MyServer(port, routingRuleInfoStr));
			t2.start();
		}
	}
	
	@Override
	public void run() {
		try {

			//Server 인스턴스 생성
			Server server = new Server();

			//ServerConnector 생성
			ServerConnector http = new ServerConnector(server);

			//서버기동에 필요한 정보를 ServerConnector에 설정
			http.setHost("127.0.0.1"); //host
			http.setPort(port); //port

			//서버에 ServerConnector추가.
			server.addConnector(http);

			// ServletHandler 생성
			ServletHandler servletHandler = new ServletHandler();

			ServletHolder myServletHolder = new ServletHolder(MyServlet.class); 

			myServletHolder.setInitParameter("routingRuleInfo", routingRuleInfoStr);

			// ServletHolder를 context Path 와 함께 핸들러에 등록.
			servletHandler.addServletWithMapping(myServletHolder, "/"); 		

			server.setHandler(servletHandler);
			server.start();
			server.join();

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public String readFile(String fileName){

		StringBuffer sb = new StringBuffer();
		String str;
		BufferedReader bufferedReader;
		try {
			bufferedReader = new BufferedReader(new FileReader(fileName));
			while ((str = bufferedReader.readLine()) != null) {
				sb.append(str);
			}
			bufferedReader.close();

		} catch (Exception e) {
			e.printStackTrace();
			return "";
		} 
		return sb.toString();
	}
}
