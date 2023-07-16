package com.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpMethod;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lgcns.test.RoutingInfo.RouteInfo;

public class MyServlet extends HttpServlet {

	RoutingInfo routingInfo;

	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

		routingInfo = new Gson().fromJson(getInitParameter("routingRuleInfo"), RoutingInfo.class) ;

		// 요청정보
		String reqQuery = req.getQueryString();
		String reqUri = req.getRequestURI();
		System.out.println("1.수신 : " + reqUri);

		if (reqUri.startsWith("/trace")) {

			Gson gson = new GsonBuilder()
					.setPrettyPrinting()
					.create();

			String requestId = reqUri.split("/")[2];
			String fileName = "c://tmp/history_" + requestId + ".json";
			String fileContents = MyUtils.readAllOfFile(fileName);
			TraceResult traceResult = gson.fromJson(fileContents, TraceResult.class);
			res.setStatus(200);
			res.getWriter().write(gson.toJson(traceResult));
			
		}else {
			
			// 헤더정보 가져오기
			Map<String, String> header = getRequestHeader(req);
			String x_requestId = header.get("x-requestId");
			String lastReqUrl = header.get("x-url");
			System.out.println("2.수신x_requestId : " + x_requestId);
			
			// PROXY Server 수신이력생성
			TraceResult proxyTrace = new TraceResult(req.getRequestURL().toString(), "100", null);
			MyUtils.addNewTraceResult(x_requestId, lastReqUrl, proxyTrace);
			System.out.println("3-1.프록시서버수신이력생성");

			//마지막요청 URL정보 업데이트
			lastReqUrl = req.getRequestURL().toString();

			// 대상URL 찾기
			String targetUrl = null;
			for(RouteInfo info : routingInfo.getRoutes()) {
				if(reqUri.startsWith(info.getPathPrefix())) { 
					targetUrl = info.getUrl();
					break;
				}
			}

			if(null != targetUrl) {

				// 대상 호출
				HttpClient httpClient;
				ContentResponse contentResponse = null;
				try {

					httpClient = new HttpClient();
					httpClient.start();

					String sendUrl = targetUrl + reqUri + (reqQuery!=null?"?" + reqQuery:"");

					System.out.println("호출URL : " + sendUrl);
					
					//호출할 대상URL 이력생성
					TraceResult serviceTrace = new TraceResult(sendUrl, "100", null);
					MyUtils.addNewTraceResult(x_requestId, lastReqUrl, serviceTrace);
					System.out.println("3-2.서비스호출이력생성");

					// GET 방식으로 대상 URI에 요청하여 응답을 수신한다.
					Request request = httpClient.newRequest(sendUrl);
					request.getHeaders().clear();
					header.put("x-url", sendUrl);
					header.put("x_requestId", x_requestId);
					for (String key : header.keySet()) {
						request.header(key, (String)header.get(key));
					}
					
					request.method(HttpMethod.GET);

					//요청 / 응답
					contentResponse = request.send();

					//proxy Trace 상태업데이트
					proxyTrace.setStatus(contentResponse.getStatus()+"");
					MyUtils.updateStatusOfTraceResult(x_requestId, proxyTrace);
					System.out.println("3-3.프록시호출이력생성업데이트");

					//Service Trace 상태업데이트
					serviceTrace.setStatus(contentResponse.getStatus()+"");
					MyUtils.updateStatusOfTraceResult(x_requestId, serviceTrace);
					System.out.println("3-4.서비스호출이력생성업데이트");

				} catch (Exception e) {

				}

				if(null != contentResponse) {
					res.setStatus(contentResponse.getStatus());
					res.getWriter().write(contentResponse.getContentAsString()); 
				}else {
					res.setStatus(201);
					res.getWriter().write("null");
				}
			}
		}

	}

	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

		//1-3 요청 URI 
		String reqUri = req.getRequestURI();
		routingInfo = new Gson().fromJson(getInitParameter("routingRuleInfo"), RoutingInfo.class) ;

		// 헤더정보 가져오기
		Map<String, String> header = getRequestHeader(req);
		String x_requestId = header.get("x-requestId");
		String lastReqUrl = header.get("x-url");

		// PROXY Server 수신이력생성
		TraceResult proxyTrace = new TraceResult(req.getRequestURL().toString(), "100", null);
		MyUtils.addNewTraceResult(x_requestId, lastReqUrl, proxyTrace);

		// 마지막요청 URL정보 업데이트
		lastReqUrl = req.getRequestURL().toString();

		//request body 내용을 Json String 향태로 수신
		String jsonStr = MyUtils.getHttpBodyInString(req);

		// 수신한 JSON String을 Map으로변환한다.
		Map<String, String> map = MyUtils.convertFromJsonStringToMap(jsonStr);


		String targetUrl = null;
		for(RouteInfo info : routingInfo.getRoutes()) {
			if(reqUri.startsWith(info.getPathPrefix())) { 
				targetUrl = info.getUrl();
				break;
			}
		}

		//호출할 URL 이력생성
		TraceResult serviceTrace = new TraceResult(targetUrl, "100", null);
		MyUtils.addNewTraceResult(x_requestId, lastReqUrl, serviceTrace);

		// service 호출
		HttpClient httpClient;
		ContentResponse contentResponse = null;
		try {
			httpClient = new HttpClient();
			httpClient.start();

			header.put("x-url", targetUrl);
			String sendUrl = targetUrl + reqUri;

			Request request = httpClient.newRequest(sendUrl);
			request.method(HttpMethod.POST);

			//요청 / 응답
			contentResponse = request.send();

			//proxy Trace 상태업데이트
			proxyTrace.setStatus(contentResponse.getStatus()+"");
			MyUtils.updateStatusOfTraceResult(x_requestId, proxyTrace);

			//Service Trace 상태업데이트
			serviceTrace.setStatus(contentResponse.getStatus()+"");
			MyUtils.updateStatusOfTraceResult(x_requestId, serviceTrace);

		} catch (Exception e) {

		}

		res.setStatus(contentResponse.getStatus());
		res.getWriter().write(contentResponse.getContentAsString()); 


	}

	public String getRequestBodyStr(HttpServletRequest request) throws IOException {

		String body = null;
		StringBuilder stringBuilder = new StringBuilder();
		BufferedReader bufferedReader = null;

		try {
			InputStream inputStream = request.getInputStream();
			if (inputStream != null) {
				bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
				char[] charBuffer = new char[128];
				int bytesRead = -1;
				while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
					stringBuilder.append(charBuffer, 0, bytesRead);
				}
			}
		} catch (IOException ex) {
			throw ex;
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException ex) {
					throw ex;
				}
			}
		}

		body = stringBuilder.toString();
		return body;
	}

	public static Map<String, String> getResponseHeader(ContentResponse response) {
		Map header = new HashMap();
		HttpFields httpFields = response.getHeaders();
		Enumeration fieldNames = httpFields.getFieldNames();
		while (fieldNames.hasMoreElements()) {
			String key = (String)fieldNames.nextElement();

			if (key.startsWith("x-")) {
				header.put(key, httpFields.get(key));
			}
		}
		return header;
	}

	public static Map<String, String> getRequestHeader(HttpServletRequest request) {
		Map header = new HashMap();
		Enumeration headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String key = (String)headerNames.nextElement();

			if (key.startsWith("x-")) {
				header.put(key, request.getHeader(key));
			}
		}
		return header;
	}

	/**
	 * Response 로 부터 Header 정보를 가져온다
	 * @param response
	 * @return 헤더정보
	 */
	public Map<String, String> getHeader(ContentResponse response) {
		Map header = new HashMap();
		HttpFields httpFields = response.getHeaders();
		Enumeration fieldNames = httpFields.getFieldNames();
		while (fieldNames.hasMoreElements()) {
			String key = (String)fieldNames.nextElement();

			if (key.startsWith("x-")) {
				header.put(key, httpFields.get(key));
			}
		}
		return header;
	}

	/**
	 * Request 로 부터 Header 정보를 가져온다
	 * @param request
	 * @return 헤더정보
	 */
	public static Map<String, String> getHeader(HttpServletRequest request) {
		Map header = new HashMap();
		Enumeration headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String key = (String)headerNames.nextElement();

			if (key.startsWith("x-")) {
				header.put(key, request.getHeader(key));
			}
		}
		return header;
	}

	/**
	 * Request 에 Header를 추가
	 * @param request
	 * @param header
	 */
	public static void addHeader(Request request, Map<String, String> header) {
		request.getHeaders().clear();
		for (String key : header.keySet())
			request.header(key, (String)header.get(key));
	}
}
