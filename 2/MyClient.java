package mytest2.client;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;

import mytest2.info.ConfigurationInfo;
import mytest2.util.Convertor;

public class MyClient {

	private static ConfigurationInfo configuration;
	
	public static void main(String[] args) throws Exception {
		
		MyClient myTestMain = new MyClient();
		
		// controller로 부터 Queue 정보를 가져온다.
		configuration = myTestMain.requestConfigurationInfo();
		
		// 실시간 스트리밍 서비스를 시작한다.
		myTestMain.startStreamingSystem();
	}
	
	/**
	 * 스트리밍 서비스 시작
	 * 큐 갯수 별로 thread로 실행
	 */
	private void startStreamingSystem() {
		
		for(int i=0 ; i<configuration.getInputQueueCount() ; i++) {
			Thread t = new Thread(new ThreadQueueProcess(
					i 											//큐번호
					, configuration.getInputQueueURIs().get(i)	//입력할 큐 정보 조회용 URI
					, configuration.getOutputQueueURI() 		//결과 출력용 URI
					));
			t.start();
		}
	}

	/**
	 * Queue 정보를 수신
	 * @return
	 * @throws Exception
	 */
	private ConfigurationInfo requestConfigurationInfo() throws Exception {
			
		//Controller로부터 HTTP 응답으로 수신
		String uri = "http://127.0.0.1:8080/queueInfo";
		
		//큐 정보 조회
		HttpClient httpClient = new HttpClient();
		httpClient.start();
		ContentResponse contentResponse = httpClient.GET(uri);
		
		String configurationStr = contentResponse.getContentAsString();
		
		ConfigurationInfo configuration = Convertor.convertFromJsonStringToConfiguration(configurationStr);
		
		return configuration;
		
	}

}
