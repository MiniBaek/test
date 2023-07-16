package mytest4.server;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import mytest4.util.MyFileIOUtils;

public class MyServer {

	public static void main(String[] args) {
		
		MyServer myServer = new MyServer();
		
		// 서버기동 : Parameter : Port
		int port = 0;
		try {
			if(null != args[0]) {
				port = Integer.parseInt(args[0]); //프로그램 실행시 파라미터로 Port를 받은경우.
			}
		} catch (Exception e) {
			port = 8001;
		}
		
		// 서블릿에서 활용할 파라미터가 있으면 여기서 로딩한다.
		Map<String, String> paramMap = new HashMap<String, String>();
				
		try {
			paramMap.put("serverName", "Test Server");
			paramMap.put("targetDeviceInfoStr", MyFileIOUtils.readAllContentOfFile(".//INFO/TargetDeviceInfo.json"));
			paramMap.put("commandInfo", MyFileIOUtils.readAllContentOfFile(".//INFO/CommandInfo.json"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		myServer.start(port, paramMap);
	}
	
	/**
	 * 서버 시작
	 * @param port
	 * @param serverConf
	 */
	public void start(int port, Map<String, String> serverConfMap) {

		try {
			// Create server instance & set base information.
			Server server = new Server();
			ServerConnector http = new ServerConnector(server);
			http.setHost("127.0.0.1");
			http.setPort(port);
			server.addConnector(http);

			// Create a servlet handler
			ServletHandler servletHandler = new ServletHandler();

			// Servlet Class를 ServletHolder 에 등록
			ServletHolder myServletHolder = new ServletHolder(MyServlet.class); 
			
			// Servlet 초기화시 필요한 파라미터가 있으면 이를 통해 담음.
			myServletHolder.setInitParameter("server_name", serverConfMap.get("serverName"));
			myServletHolder.setInitParameter("targetDeviceInfoStr", serverConfMap.get("targetDeviceInfoStr"));
			myServletHolder.setInitParameter("commandInfo", serverConfMap.get("commandInfo"));
			
			// ServletHolder를 Path 와 함께 핸들러에 등록.
			servletHandler.addServletWithMapping(myServletHolder, "/"); 		
			
			server.setHandler(servletHandler);
			server.start();
			server.join();
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

}
