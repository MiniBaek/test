package mytest4.client;

import java.io.IOException;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpMethod;

import mytest4.util.MyFileIOUtils;

public class MyFrontClient {

	public static void main(String[] args) throws IOException {
		
		MyFrontClient myFrontClient = new MyFrontClient();
		
		String sendData = MyFileIOUtils.readAllContentOfFile(".//SEND/SendData.json");
		String deviceUrl = "localhost:8001/fromServer";
		String result = myFrontClient.callServer(deviceUrl, HttpMethod.POST, sendData).getContentAsString();
		System.out.println(result);
		
	}

	private ContentResponse callServer(String deviceUrl, HttpMethod httpMethod, String sendData) {

		HttpClient httpClient;
		ContentResponse contentResponse = null;
		
		try {
			httpClient = new HttpClient();
			httpClient.start();
			
			Request request = httpClient.newRequest(deviceUrl);
			request.method(httpMethod);
			request.header(HttpHeader.CONTENT_TYPE, "application/json");
			request.content(new StringContentProvider(sendData, "utf-8"));
			
			contentResponse = request.send();
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return contentResponse;
	}
	
}
