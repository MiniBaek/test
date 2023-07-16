package mytest4.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpMethod;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import mytest4.info.CommandInfo;
import mytest4.info.DeviceResponseInfo;
import mytest4.info.ServerRequestInfo;
import mytest4.info.ServerResponse;
import mytest4.info.TargetDeviceInfo;
import mytest4.util.MyServletUtils;

public class MyServlet extends HttpServlet {

	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		
	}
	
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		
		String param1 = getInitParameter("targetDeviceInfoStr");
		TargetDeviceInfo targetDeviceInfo = new Gson().fromJson(param1, TargetDeviceInfo.class);
		String param2 = getInitParameter("commandInfo");
		CommandInfo commandInfo = new Gson().fromJson(param2, CommandInfo.class);
		
		String reqUri = req.getRequestURI();
		String bodyStr = MyServletUtils.getHttpBodyInString(req);
		
		if(reqUri.startsWith("/fromServer")) {
			
			// 서버요청정보
			ServerRequestInfo serverRequestInfo = new Gson().fromJson(bodyStr, ServerRequestInfo.class);
			
			// 디바이스에게 보낼 forwardCommand 찾기
			String forwardCommand = null;
			
			for(Map<String,String> map : commandInfo.getCommandList()) {
				forwardCommand = null;
				if(null != serverRequestInfo.getCommand()) {
					if(serverRequestInfo.getCommand().startsWith(map.get("command"))) {
						forwardCommand = map.get("forwardCommand");
						break;
					}
				}
			}
			
			//대상장비에 보낼 메시지 작성 : forwardComand#param
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("command", forwardCommand);
			jsonObject.addProperty("param", serverRequestInfo.getParam());
			
			String deviceRequestInfoJson = new Gson().toJson(jsonObject);
			
			// 장비로부터 수신한 응답을 담을 List
			List<String> deviceResultList = new ArrayList<String>();
			
			// 대상장비별 처리
			for(String device : serverRequestInfo.getTargetDevices()) {
				
				String hostName = null;
				String port = null;
				for(Map<String,String> map : targetDeviceInfo.getTargetDeviceList()) {
					if(device.equals(map.get("name"))) {
						hostName = map.get("hostName");
						port = map.get("port");
						break;
					}
				}
				String deviceUrl = "http://" + hostName + ":" + port + "/" + "fromNode";
				System.out.println(deviceUrl);

				// 호출
				ContentResponse contentResponse = callDevice(deviceUrl, HttpMethod.POST, deviceRequestInfoJson);
				
				// 디바이스응답처리
				
				DeviceResponseInfo deviceResponseInfo = new Gson().fromJson(contentResponse.getContentAsString(), DeviceResponseInfo.class); 
				deviceResultList.add(deviceResponseInfo.getResult());
			}
			
			// 서버응답메시지 작성
			ServerResponse serverResponse = new ServerResponse(deviceResultList);
			String serverResponseStr = new Gson().toJson(serverResponse);
			
			res.setStatus(200);
			res.getWriter().write(serverResponseStr);
			
		}
	}

	private ContentResponse callDevice(String deviceUrl, HttpMethod httpMethod, String sendData) {

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
