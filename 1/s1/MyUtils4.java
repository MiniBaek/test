package com.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpMethod;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lgcns.test.RoutingInfo.RouteInfo;

public class MyUtils {

	static RoutingInfo routingInfo;

	/**
	 * 파일을 라인별로 읽는다
	 * @param fileName
	 * @return 라인별로 읽은 문자열을 Merge 에서 문자열로 반환
	 * @throws IOException
	 */
	public static String readAllOfFile(String fileName) throws IOException{

		String str;
		StringBuffer sb = new StringBuffer();
		BufferedReader reader = new BufferedReader(new FileReader(fileName));
		while ((str = reader.readLine()) != null) {
			sb.append(str);
		}
		reader.close();

		return sb.toString();
	}

	/**
	 * Json String으로 부터 오브젝트(RoutingRuleInfo.class) 로 변환
	 * @param jsonStr
	 * @return RoutingRuleInfo
	 * @throws IOException
	 */
	public static RoutingInfo convertToObjFromJsonString(String jsonStr) throws IOException {

		Gson gson = new Gson();
		RoutingInfo routingInfo = gson.fromJson(jsonStr, RoutingInfo.class);

		return routingInfo;
	}

	/**
	 * Http 요청 기본
	 * @param uri
	 * @param httpMethod
	 * @return ContentResponse
	 */
	public static ContentResponse requestBase(String uri, HttpMethod httpMethod) {

		HttpClient httpClient;
		ContentResponse contentRes = null;

		try {
			httpClient = new HttpClient();
			httpClient.start();

			Request request = httpClient.newRequest(uri);
			request.method(httpMethod);

			contentRes = request.send();

		} catch (Exception e) {
			e.printStackTrace();	
		}

		return contentRes;
	}

	/**
	 * Http 요청 기본
	 * @param uri
	 * @param httpMethod
	 * @param header
	 * @return ContentResponse
	 */
	public static ContentResponse requestBase(String uri, HttpMethod httpMethod, Map<String,String> header) {

		HttpClient httpClient;
		ContentResponse contentRes = null;

		try {
			httpClient = new HttpClient();
			httpClient.start();

			Request request = httpClient.newRequest(uri);
			request.method(httpMethod);

			request.getHeaders().clear();
			for (String key : header.keySet()) {
				request.header(key, (String)header.get(key));
			}

			contentRes = request.send();

		} catch (Exception e) {
			e.printStackTrace();	
		}

		return contentRes;
	}

	/**
	 * POST 요청 : requestBase를 호출
	 * @param targetUri
	 * @return : requestBase를 호출한 결과응답
	 */
	public static ContentResponse requestByPostMethodWithoutBody(String targetUri) {

		return requestBase(targetUri, HttpMethod.POST);
	}

	/**
	 * POST 요청 : requestBase를 호출. Header 변경필요시.
	 * @param targetUri
	 * @param header
	 * @return : requestBase를 호출한 결과응답
	 */
	public static ContentResponse requestByPostMethodWithoutBody(String targetUri, Map<String,String> header) {

		return requestBase(targetUri, HttpMethod.POST, header);
	}

	/**
	 * GET 요청 : requestBase를 호출
	 * @param targetUri
	 * @return : requestBase를 호출한 결과응답
	 */
	public static ContentResponse requestByGetMethod(String targetUri) {

		return requestBase(targetUri, HttpMethod.GET);
	}

	/**
	 * GET 요청 : requestBase를 호출. Header 변경필요시.
	 * @param targetUri
	 * @param header
	 * @return
	 */
	public static ContentResponse requestByGetMethod(String targetUri, Map<String,String> header) {

		return requestBase(targetUri, HttpMethod.GET, header);
	}

	/**
	 * Request 로 부터 Body 를 가져온다.
	 * @param request
	 * @return : Body 내용을 String으로 변환
	 * @throws IOException
	 */
	public static String getHttpBodyInString(HttpServletRequest request) throws IOException {

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

	/**
	 * Json String 을 Map으로 변환
	 * @param jsonStr
	 * @return Map<String, String>
	 */
	public static Map<String, String> convertFromJsonStringToMap(String jsonStr) {
		Gson gson = new Gson();
		Map<String, String> map = gson.fromJson(jsonStr, Map.class);
		return map;
	}

	/**
	 * 파일 쓰기
	 * @param fileName
	 * @param targetContents
	 * @param mode
	 * @throws IOException
	 */
	public static void wirteFile(String fileName, String targetContents, boolean mode) throws IOException{

		File file = new File(fileName);

		try {
			FileWriter writer = new FileWriter(file,true);
			writer.write(targetContents);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 파일쓰기
	 * @param fileName
	 * @param targetContents
	 * @throws IOException
	 */
	public static void wirteFile(String fileName, String targetContents) throws IOException{

		File file = new File(fileName);

		try {
			FileWriter writer = new FileWriter(file,false);
			writer.write(targetContents);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Trace Result 출력
	 * @param traceResult
	 */
	public static void printTraceResult(TraceResult traceResult) {
		Gson gson = new Gson();
		String jsonStr = gson.toJson(traceResult);
		System.out.println("TRACE : " + jsonStr);
	}

	/**
	 * Recursive 형태의 오브젝트를 찾아서 업데이트
	 * @param traceResult
	 * @param key
	 * @param newTrace
	 */
	public static void updateTraceResult(TraceResult traceResult, String key, TraceResult newTrace) {
		if(traceResult.getTarget().equals(key)) {
			if(!traceResult.getTarget().equals(newTrace.getTarget())) {

				if(null == traceResult.getServices()) {
					List<TraceResult> serviceList = new ArrayList<TraceResult>();
					serviceList.add(newTrace);
					traceResult.setServices(serviceList);
				}else {
					if(isDupCheck(traceResult.getServices(),newTrace)) {
						//System.out.println("이미존재함");
					}else {
						traceResult.getServices().add(newTrace);
					}
				}
			}
		}else {
			if(null != traceResult.getServices()) {
				for(TraceResult service : traceResult.getServices()) {
					updateTraceResult(service, key, newTrace);
				}
			}
		}
	}

	/**
	 * Recursive 형태의 오브젝트를 찾아서 업데이트
	 * @param traceResult
	 * @param modifiedTrace
	 */
	private static void updateStatusOfTraceResult(TraceResult traceResult, TraceResult modifiedTrace) {
		if(traceResult.getTarget().equals(modifiedTrace.getTarget())) {
			traceResult.setStatus(modifiedTrace.getStatus());
		}else {
			if(null != traceResult.getServices()) {
				for(TraceResult service : traceResult.getServices()) {
					updateStatusOfTraceResult(service, modifiedTrace);
				}
			}
		}
	}

	public static boolean isDupCheck(List<TraceResult> services, TraceResult newTrace) {
		// TODO Auto-generated method stub
		for(TraceResult tr : services) {
			if(tr.getTarget().equals(newTrace.getTarget())) {
				return true;
			}
		}
		return false;
	}

	public synchronized static String addNewTraceResult(String requestId, String lastReqUrl, TraceResult newTrace) {

		Gson gson = new GsonBuilder()
				.setPrettyPrinting()
				.create();

		String fileName = "c://tmp/history_" + requestId + ".json";
		String historyFileStr = null;

		try {

			historyFileStr = readAllOfFile(fileName);
			System.out.println("현재 파일 내용 : " + historyFileStr);

			if(historyFileStr.length() == 0) {
				throw new Exception();
			}
		} catch (Exception e) {
			//e.printStackTrace();
			historyFileStr = null;
		}

		if(null == historyFileStr) {
			//신규생성
			historyFileStr = gson.toJson(newTrace);
		}else {
			TraceResult traceResult = gson.fromJson(historyFileStr, TraceResult.class);
			updateTraceResult(traceResult, lastReqUrl, newTrace);
			historyFileStr = gson.toJson(traceResult);
		}

		try {
			if(null != historyFileStr && historyFileStr.length() > 0) {
				wirteFile(fileName, historyFileStr);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}

		//System.out.println("파일에 쓸내용 : " + historyFileStr);
		return historyFileStr;
	}

	public synchronized static String updateStatusOfTraceResult(String requestId, TraceResult modifiedTrace) {

		Gson gson = new GsonBuilder()
				.setPrettyPrinting()
				.create();

		String fileName = "c://tmp/history_" + requestId + ".json";

		String historyFileStr = null;
		try {

			historyFileStr = readAllOfFile(fileName);
			System.out.println("현재 파일 내용 : " + historyFileStr);

			if(historyFileStr.length() == 0) {
				throw new Exception();
			}
		} catch (Exception e) {
			//e.printStackTrace();
			historyFileStr = null;
		}

		if(null == historyFileStr) {
			//신규생성
			historyFileStr = gson.toJson(modifiedTrace);
		}else {
			TraceResult traceResult = gson.fromJson(historyFileStr, TraceResult.class);
			updateStatusOfTraceResult(traceResult, modifiedTrace);
			historyFileStr = gson.toJson(traceResult);
		}

		try {
			if(null != historyFileStr && historyFileStr.length() > 0) {
				wirteFile(fileName, historyFileStr);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}

		//System.out.println("파일에 쓸내용 : " + historyFileStr);
		return historyFileStr;
	}

}
