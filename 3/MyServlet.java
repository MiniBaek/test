package com.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpStatus;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class MyServlet extends HttpServlet {

	Map<String, MyQueue> queueMap = new HashMap<String, MyQueue>();
	int messageId = 0;

	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

		// 요청 URI 
		String reqUri = req.getRequestURI();
		String[] reqParam = req.getRequestURI().split("/");

		if(reqUri.startsWith("/RECEIVE")) { 

			String name = reqParam[2];
			String resultMessage = doBizForRcv(name);

			res.setStatus(HttpStatus.OK_200);
			res.getWriter().write(resultMessage);
		}
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

		//1.요청 URI 
		String reqUri = req.getRequestURI();
		String[] pathParam = reqUri.split("/");

		//2. Request Body 파싱
		String requestBodyStr = getRequestBodyStr(req);

		//3. Path별 처리
		if(reqUri.startsWith("/CREATE")) {
			
			//3-1. 요청정보확인
			QueueInfo reqBody = new Gson().fromJson(requestBodyStr, QueueInfo.class);
			String name = pathParam[2];
			int maxSize = reqBody.getQueueSize();

			//3-2. BIZ처리
			String resultMsg = doBizForCreate(name, maxSize);
			
			//3-3. 응답처리
			res.setStatus(HttpStatus.OK_200);
			res.getWriter().write(resultMsg);

		}else if(reqUri.startsWith("/SEND")) {

			//3-1. 요청정보확인
			SendMessageInfo reqBody = new Gson().fromJson(requestBodyStr, SendMessageInfo.class);
			String name = pathParam[2];
			String message = reqBody.getMessage();

			//3-2. BIZ처리
			String resultMsg = doBizForSend(name, message);

			//3. 응답처리
			res.setStatus(HttpStatus.OK_200);
			res.getWriter().write(resultMsg);

		}else if(reqUri.startsWith("/ACK")) {

			//3-1. 요청정보확인
			String name = pathParam[2];
			String reqMessageId = pathParam[3];
			
			//3-2. BIZ처리
			String resultMsg = doBizForAck(name, reqMessageId);

			//3. 응답처리
			res.setStatus(HttpStatus.OK_200);
			res.getWriter().write(resultMsg);

		}else if(reqUri.startsWith("/FAIL")) {

			//3-1. 요청정보확인
			String name = pathParam[2];
			String reqMessageId = pathParam[3];

			//3-2. BIZ처리
			String resultMsg = doBizForFail(name, reqMessageId);
			
			//3. 응답처리
			res.setStatus(HttpStatus.OK_200);
			res.getWriter().write(resultMsg);
		}
	}
	
	public String doBizForFail(String name, String reqMessageId) {

		JsonObject jsonObject = new JsonObject();
		String message =null;
		
		if(null != queueMap.get(name)){
			if(queueMap.get(name).getWaitingMap().size()>0) {
				
				message = queueMap.get(name).getWaitingMap().get(reqMessageId);
				queueMap.get(name).getWaitingMap().remove(reqMessageId);
				
				int index = queueMap.get(name).getWaitingList().indexOf(message);
				queueMap.get(name).getWaitingList().remove(index);
			}
		}
		
		jsonObject.addProperty("Result", "OK");
		String jsonStr = new Gson().toJson(jsonObject);
		
		return jsonStr;

	}
	

	private String doBizForAck(String name, String reqMessageId) {
		
		JsonObject jsonObject = new JsonObject();
		Stack<String> stack = new Stack<String>();
		
		String tempMessage = null;
		String targetMessage = queueMap.get(name).getWaitingMap().get(reqMessageId);
		
		if(null != queueMap.get(name)){
			if(queueMap.get(name).getWaitingList().size()>0) {
				
				do {
					//poll
					tempMessage = queueMap.get(name).getQueue().poll();

					//대상메시지와 동일한가?
					if(targetMessage.equals(tempMessage)) {
						//동일하면 삭제 대상. poll 중지
						break;
					}else {
						//push
						stack.push(tempMessage);
					}

				}while(!targetMessage.equals(tempMessage));
				
				//stack 내용 복구
				while(!stack.isEmpty()) {
					tempMessage = stack.pop();
					queueMap.get(name).getQueue().addFirst(tempMessage);
				}
				
				//waiting 삭제
				queueMap.get(name).getWaitingMap().remove(reqMessageId);
				queueMap.get(name).getWaitingList().remove(targetMessage); 
				jsonObject.addProperty("Result", "OK");
				
			}else {
				jsonObject.addProperty("Result", "OK");
			}
		}else {
			jsonObject.addProperty("Result", "OK");
		}

		return new Gson().toJson(jsonObject);
		
	}

	private String doBizForSend(String name, String message) {
		JsonObject jsonObject = new JsonObject();
		boolean result;
		String resultMsg = null;
		
		if(null != queueMap.get(name)){
			if(queueMap.get(name).getQueue().size() == queueMap.get(name).getMaxSize()) {
				resultMsg = "Queue Full";
			}else {
				result = queueMap.get(name).getQueue().add(message);
				resultMsg = "OK";
			}
		}else {
			resultMsg = "Queue not Exist";
		}
		jsonObject.addProperty("Result", resultMsg);
		return new Gson().toJson(jsonObject);
	}

	private String doBizForCreate(String name, int maxSize) {
		
		JsonObject jsonObject = new JsonObject();
		String resultMsg;
		
		if(null == queueMap.get(name)) {
			MyQueue myQueue = new MyQueue(name, maxSize);
			queueMap.put(name, myQueue);
			
			resultMsg = "OK";
		}else{
			resultMsg = "Queue Exist";
		}
		
		jsonObject.addProperty("Result", resultMsg);
		
		return new Gson().toJson(jsonObject);
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

	private String doBizForRcv(String name) {

		JsonObject jsonObject = new JsonObject();
		Stack<String> stack = new Stack<String>();
		String rcvMessage = null;
		String resMsg = null;
		int newMessageId = messageId++;

		if(null != queueMap.get(name)){

			if(queueMap.get(name).getQueue().size()>0) {
				String tempMessage = null;
				while(true) {
					tempMessage = this.queueMap.get(name).getQueue().poll();
					if(null == tempMessage) {
						break;
					}

					stack.push(tempMessage);

					if(this.queueMap.get(name).getWaitingList().size()>0) {
						if(this.queueMap.get(name).getWaitingList().contains(tempMessage)) {
							//있으면 다음
						}else {
							//없으면 Receive 대상.대기열에 추가
							this.queueMap.get(name).getWaitingList().add(tempMessage);

							//대기열맵에 ID와 메시지 추가
							if(null == this.queueMap.get(name).getWaitingMap().get(newMessageId)) {
								this.queueMap.get(name).getWaitingMap().put((newMessageId)+"", tempMessage);
							}else {
								//System.out.println("이미존재하는 ID in waintingMap");
							}

							rcvMessage = tempMessage;
							break;
						}
					}else {
						//없으면 Receive 대상.대기열에 추가
						this.queueMap.get(name).getWaitingList().add(tempMessage);

						//대기열맵에 ID와 메시지 추가
						if(null == this.queueMap.get(name).getWaitingMap().get(newMessageId)) {
							this.queueMap.get(name).getWaitingMap().put((newMessageId)+"", tempMessage);
						}else {
							//System.out.println("이미존재하는 ID in waintingMap");
						}

						rcvMessage = tempMessage;
						break;
					}
				}

				if(null == tempMessage) {
					jsonObject.addProperty("Result", "No Message");
				}else {
					jsonObject.addProperty("Result", "OK");
					jsonObject.addProperty("MessageId", newMessageId);
					jsonObject.addProperty("Message", rcvMessage);
				}
				resMsg = new Gson().toJson(jsonObject);

				//stack 내용 복구
				String recoveryMessage = null;
				while(!stack.isEmpty()) {
					recoveryMessage = stack.pop();
					this.queueMap.get(name).getQueue().addFirst(recoveryMessage);
				}

			}else {
				//메시지없음
				jsonObject.addProperty("Result", "No Message");
				resMsg = new Gson().toJson(jsonObject);
			}
		}else {
			//메시지없음
			jsonObject.addProperty("Result", "No Message");
			resMsg = new Gson().toJson(jsonObject);
		}

		return resMsg;
	}
}
