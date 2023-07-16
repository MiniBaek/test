package mytest5.server;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpStatus;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class MyServlet extends HttpServlet {
	
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

		//1 요청 URI 
		String[] reqUri = req.getRequestURI().split("/");

		if(reqUri[1].equals("requestDate")) { 

			//2-1 요청정보 : Path parameter : reqUri 를 파싱해서 사용
			String param = reqUri[2];

			//2-2. BIZ Logic 처리 : 요청한 parameter 값을 이용하여 Biz 처리 (파일, DB, Client to request 다른서버 등.)
			String resData = getToday(param);
			
			//2-3 응답처리 : BIZ 처리결과에 따라 작성
			res.setStatus(HttpStatus.OK_200);
			res.getWriter().write(resData); //text, json...
			
		}else if (reqUri[1].equals("requestFile")) {
			
			//2-1 요청정보
			String param = reqUri[2]; //filename
			
			//2-2 Biz
			try {
				File file = new File("C:\\mytest/server/requestFile/" + param);
				byte[] fileContent = Files.readAllBytes(file.toPath());
				
				ServletOutputStream stream = res.getOutputStream(); 
				stream.flush();
				stream.write(fileContent);
				stream.flush();
				stream.close();
				res.setStatus(HttpStatus.OK_200); 
			} catch (Exception e) {
				res.setStatus(HttpStatus.EXPECTATION_FAILED_417); 
			}
			
		}else if (reqUri[1].equals("downloadFileList")) {
			
			//2-1 요청정보
			String param = reqUri[2];
			
			//2-2 Biz
			
			// 특정 디렉토리에서 파일목록가져오기
			File dir = new File("C:\\mytest\\server\\" + param);
			File files[] = dir.listFiles();
						
			// 파일목록을 MAP 에 담는다.
			LinkedHashMap<String, byte[]> fileList = new LinkedHashMap<String, byte[]>();
			for(File file : files) {
				Path  path = file.toPath();
				byte[] fileContent = Files.readAllBytes(path);
				fileList.put(file.getName(), fileContent);
			}
			
			// MAP을 ObjectOutputStream
			ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(byteOut);
			out.writeObject(fileList);
			
			ServletOutputStream stream = res.getOutputStream(); 
			stream.flush();
			stream.write(byteOut.toByteArray());
			stream.flush();
			stream.close();
			
			//2-3 응답
			res.setStatus(HttpStatus.OK_200); 
		}
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

		String[] reqUri = req.getRequestURI().split("/");
		
		if(reqUri[1].equals("registJson")) {

			//2-1 요청정보 Request Body 파싱
			String requestBodyStr = getRequestBodyStr(req);
			MyRequestInfo myRequestInfo = new Gson().fromJson(requestBodyStr, MyRequestInfo.class);
			String name = myRequestInfo.getName();
			String age = myRequestInfo.getName();

			//2-2. Biz Logic 처리 : 요청한 parameter 값을 이용하여 Biz 처리 (파일, DB, Client to request 다른서버 등.)
			
			//2-3. 응답처리 : Biz 처리결과에 따른 응답객체 작성
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("added Name", name);
			jsonObject.addProperty("result", "success");
			String result = new Gson().toJson(jsonObject);
			res.setStatus(HttpStatus.OK_200);
			res.getWriter().write(result); //text, json...
			
		}else if(reqUri[1].equals("uploadFile")) {
			
			//2-1 요청정보 Request Body 파싱
			String requestBodyStr = getRequestBodyStr(req);
			
			//2-2. Biz Logic 처리
			String fileName =null;
			try {
				fileName = "rcvFile_" + System.currentTimeMillis()+ ".txt";
				File file = new File("C:\\mytest\\server\\" + fileName);
				FileWriter fw = new FileWriter(file,false);
				fw.write(requestBodyStr);
				fw.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			//2-3. 응답처리
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("fileName", fileName);
			jsonObject.addProperty("result", "success");
			String result = new Gson().toJson(jsonObject);
			res.setStatus(HttpStatus.OK_200);
			res.getWriter().write(result); //text, json...
		}
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
    
    private String getToday(String type) {
		String result = null;
		SimpleDateFormat sdf;
		if("1".equals(type)) {
			sdf = new SimpleDateFormat("yyyyMMdd");
		}else {
			sdf = new SimpleDateFormat("yyyy-MM-dd");
		}
		
		Date now = new Date();
		String nowTime = sdf.format(now);
		
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("date", nowTime);
		result = new Gson().toJson(jsonObject);
		
		return result;
	}
    
    class MyRequestInfo{
    	
    	String name;
    	int age;
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public int getAge() {
			return age;
		}
		public void setAge(int age) {
			this.age = age;
		}
    }
}
