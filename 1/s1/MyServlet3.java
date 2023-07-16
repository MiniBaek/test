package com.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.http.HttpMethod;

import com.google.gson.Gson;
import com.lgcns.test.RoutingInfo.RouteInfo;

public class MyServlet extends HttpServlet {

	RoutingInfo routingInfo;

	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

		String reqUrl = req.getRequestURL().toString();
		String reqQuery = req.getQueryString();
		//1-3 요청 URI 
		String reqUri = req.getRequestURI();

		routingInfo = new Gson().fromJson(getInitParameter("routingRuleInfo"), RoutingInfo.class) ;

		String targetUrl = null;

		for(RouteInfo info : routingInfo.getRoutes()) {
			if(reqUri.startsWith(info.getPathPrefix())) { 
				targetUrl = info.getUrl();
				break;
			}
		}

		// service 호출
		HttpClient httpClient;
		ContentResponse contentResponse = null;
		try {
			httpClient = new HttpClient();
			httpClient.start();

			String sendUrl = targetUrl + reqUri + (reqQuery!=null?"?" + reqQuery:"");

			Request request = httpClient.newRequest(sendUrl);
			request.method(HttpMethod.GET);

			//요청 / 응답
			contentResponse = request.send();
		} catch (Exception e) {

		}

		res.setStatus(contentResponse.getStatus());
		res.getWriter().write(contentResponse.getContentAsString()); 

	}

	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

		//1-3 요청 URI 
		String reqUri = req.getRequestURI();

		routingInfo = new Gson().fromJson(getInitParameter("routingRuleInfo"), RoutingInfo.class) ;

		String targetUrl = null;

		for(RouteInfo info : routingInfo.getRoutes()) {
			if(reqUri.startsWith(info.getPathPrefix())) { 
				targetUrl = info.getUrl();
				break;
			}
		}

		// service 호출
		HttpClient httpClient;
		ContentResponse contentResponse = null;
		try {
			httpClient = new HttpClient();
			httpClient.start();

			String sendUrl = targetUrl + reqUri;

			Request request = httpClient.newRequest(sendUrl);
			request.method(HttpMethod.POST);

			//요청 / 응답
			contentResponse = request.send();
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
}
