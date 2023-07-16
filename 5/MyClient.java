package mytest5.client;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ServletOutputStream;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.BytesContentProvider;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpMethod;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class MyClient {

	public static void main(String[] args) throws FileNotFoundException, IOException {

		MyClient myClient = new MyClient();
		String BASE_URL = "http://127.0.0.1:8001";
		String path = null;
		String param = null;
		int status = 0;
		String result = null;
		
		// request 1 : 단순정보 조회요청 : GET (ex : 날짜)
		path = "/requestDate";
		param = "1";
		result = myClient.requestDate(BASE_URL, path, "/" + param);
		System.out.println("CLIENT : request 1 결과 : " + result);
		
		// request 2 : 파일 데이터요청 : GET 
		path = "/requestFile";
		param = "fileDownloadSample.txt";
		status = myClient.requestFile(BASE_URL, path, "/" + param);
		System.out.println("CLIENT : request 2 결과 : " + status);
		
		// request 3 : 파일 여러개(파일명포함) 다운로드요청 : GET 
		path = "/downloadFileList";
		param = "samples";
		status = myClient.downloadFileList(BASE_URL, path, "/" + param);
		System.out.println("CLIENT : request 3 결과 : " + status);
		
		// request 4 : Json 업로드 : POST 
		path = "/registJson";
		result = myClient.uploadJson(BASE_URL, path);
		System.out.println("CLIENT : request 4 결과 : " + result);
		
		// request 5 : 파일업로드 : POST 
		path = "/uploadFile";
		result = myClient.uploadFile(BASE_URL, path);
		System.out.println("CLIENT : request 5 결과 : " + result);
		
		// request 6 : 파일업로드 : POST 
		//path = "/uploadFileList";
		//status = myClient.uploadFileList(BASE_URL, path);
		//System.out.println("CLIENT : request 6 결과 결과 : " + status);
	}

	private String uploadJson(String baseUrl, String path) {
		
		String name = "bjm";
		int age = 42;
		
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("name", name);
		jsonObject.addProperty("age", age);
		String memberInfo = new Gson().toJson(jsonObject);
		
		HttpClient httpClient;
		ContentResponse contentResponse = null;
		
		try {
			httpClient = new HttpClient();
			httpClient.start();
			
			Request request = httpClient.newRequest(baseUrl + path);
			request.method(HttpMethod.POST);
			request.header(HttpHeader.CONTENT_TYPE, "application/json");
			request.content(new StringContentProvider(memberInfo, "utf-8"));
			
			contentResponse = request.send();
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return contentResponse.getContentAsString();
		
	}

	private String uploadFile(String baseUrl, String path) {
		
		HttpClient httpClient = new HttpClient();
		ContentResponse contentRes = null;
		
		try {
			httpClient.start();
			Request request = httpClient.newRequest(baseUrl + path);
			request.method(HttpMethod.POST);
			
			// 방법1 : using java.nio.file.Paths
			request.file(Paths.get("c://mytest/client/upload/fileUploadSample.txt"), "text/plain");
			
			// 방법2 : using the PathContentProvider utility class
			//request.content(new PathContentProvider(Paths.get("file_to_upload.txt")), "text/plain");
			
			// 방법3 : FileInputStream via the InputStreamContentProvider utility class
			//request.content(new InputStreamContentProvider(new FileInputStream("file_to_upload.txt")), "text/plain");
			
			contentRes = request.send();
			httpClient.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return contentRes.getContentAsString();
	}
	
	private int uploadFileList(String baseUrl, String path) {
		
		// 특정 디렉토리에서 파일목록가져오기
		File dir = new File("C:\\mytest\\client\\uploadFiles\\");
		File files[] = dir.listFiles();
		LinkedHashMap<String, byte[]> fileList = new LinkedHashMap<String, byte[]>();			
		// 파일목록을 MAP 에 담는다.
		for(File file : files) {
			Path filePath = file.toPath();
			byte[] fileContent = null;
			try {
				fileContent = Files.readAllBytes(filePath);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			fileList.put(file.getName(), fileContent);
		}
		
		// MAP을 ObjectOutputStream
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		ObjectOutputStream out;
		try {
			out = new ObjectOutputStream(byteOut);
			out.writeObject(fileList);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		HttpClient httpClient = new HttpClient();
		ContentResponse contentRes = null;
		
		try {
			httpClient.start();
			Request request = httpClient.newRequest(baseUrl + path);
			request.method(HttpMethod.POST);
			request.content(new BytesContentProvider(byteOut.toByteArray()), "text/plain");
			contentRes = request.send();
			httpClient.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return contentRes.getStatus();
	}

	private int downloadFileList(String baseUrl, String path, String param) {
		
		HttpClient httpClient = new HttpClient();
		ContentResponse contentRes = null;
		
		try {
			httpClient.start();
			Request request = httpClient.newRequest(baseUrl + path + param);
			request.method(HttpMethod.GET);
			contentRes = request.send();
			httpClient.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		byte [] myByteArray = contentRes.getContent();
		//System.out.println(myByteArray.length);
		try {
			ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(myByteArray);
			ObjectInputStream in = new ObjectInputStream(                                                                                                                                                                byteArrayInputStream);
			
			Map<String, byte[]> data = (Map<String, byte[]>) in.readObject();
			
			for(String key : data.keySet()) {
				FileOutputStream fos = new FileOutputStream("C:\\mytest\\client\\" + key);
				fos.write(data.get(key));
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return contentRes.getStatus();
	}
	
	private int requestFile(String baseUrl, String path, String param) throws FileNotFoundException, IOException {
		
		HttpClient httpClient = new HttpClient();
		ContentResponse contentRes = null;
		
		try {
			httpClient.start();
			Request request = httpClient.newRequest(baseUrl + path + param);
			request.method(HttpMethod.GET);
			contentRes = request.send();
			httpClient.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		byte [] myByteArray = contentRes.getContent();
		String rcvDir = "C:\\mytest\\client\\";
		
        try (FileOutputStream fos = new FileOutputStream("C:\\mytest\\client\\" + param)) {
            fos.write(myByteArray);
        }
		
		return  contentRes.getStatus();
	}
	
	private String requestDate(String baseUrl, String path, String param) {

		HttpClient httpClient = new HttpClient();
		ContentResponse contentRes = null;

		try {
			httpClient.start();
			Request request = httpClient.newRequest(baseUrl + path + param);
			request.method(HttpMethod.GET);
			contentRes = request.send();
			httpClient.stop();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return contentRes.getContentAsString();
	}

}
