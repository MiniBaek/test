package mytest5.server;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class MyServer {

	public static void main(String[] args) {

		MyServer myServer = new MyServer();

		// 프로그램 실행시 파라메터가 있는지 확인.(예 :port번호)
		//int port = Integer.parseInt(args[0]);
		int port = 8001;

		// 서빌릿에서 사용해야항 설정 파일이 있으면 여기로 읽어서 넘긴다.
		Map<String, String> initParams = new HashMap<String, String>();
		//String initParamStr = myServer.readFile(".//INFO/InitParam.json");
		//initParams.put("initParamStr", initParamStr);

		//서버기동을 위한 메소드 호출
		myServer.start(port, initParams); 
	}

	/**
	 * 서버 시작
	 * @param port
	 * @param serverConf
	 */
	public void start(int port, Map<String, String> initParamsMap) {

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

			// ServletHolder에 초기화 파라미터 Set
			if(null != initParamsMap && initParamsMap.size()>0) {
				for (Entry<String, String> entrySet : initParamsMap.entrySet()) {
					myServletHolder.setInitParameter(entrySet.getKey(), entrySet.getValue());
				}
			}

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
