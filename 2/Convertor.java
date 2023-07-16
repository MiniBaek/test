import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class Convertor {

	/**
	 * 콘솔을 통해 입력 받은 문자열을 파싱
	 * @param inputStr
	 * @return QueueData
	 * @throws Exception
	 */
	public static InputQueueData convertFromStringToQueueData(String inputStr) throws Exception {
		
		final int LENGTH = 3;
		InputQueueData inputQueueData = null;
		
		if(null != inputStr && inputStr.length()>0) {
			
			String[] splitStr = inputStr.split(" ");
			if(splitStr.length == LENGTH) {
				
				//명령어 파싱 : queueNo, value ex) 0 VIEW_AD1
				try {
					inputQueueData = new InputQueueData(
							Long.valueOf(inputStr.split(" ")[0])
							, Integer.parseInt(inputStr.split(" ")[1])
							, inputStr.split(" ")[2]
									);
				} catch (Exception e) {
					throw new Exception("입력문자열모맷오류");
				}
				
			}else {
				throw new Exception("입력문자열모맷오류");
			}
		}else {
			throw new Exception("입력문자열길이오류");
		}
		
		if(null == inputQueueData) {
			throw new Exception("입력큐정보ISNULL");
		}
		
		return inputQueueData;
	}

	/**
	 * Controller 에서 제공하는 Queue 정보를 수신하여 QueueInfo Object로 변환(from Json String)
	 * @param queueInfoStr
	 * @return
	 */
	public static ConfigurationInfo convertFromJsonStringToConfiguration(String queueInfoStr) {
		
		//String sample = "{ \"inputQueueCount\":3,\"inputQueueURIs\":[\"http://127.0.0.1:8010/input\",\"http://127.0.0.1:8011/input\",\"http://127.0.0.1:8012/input\"],\"outputQueueURI\":\"http://127.0.0.1:9010/output\" }\r\n";
		Gson gson = new Gson();
		ConfigurationInfo queueInfo = gson.fromJson(queueInfoStr, ConfigurationInfo.class);
		
		return queueInfo;
	}

	/**
	 * 각 Queue 에 입력될 정보를 수신하여 QueueData Object 로 변환
	 * @param inputData
	 * @return
	 */
	public static InputQueueData convertFromJsonStringToQueueData(String inputData) {
		
		Gson gson = new Gson();
		InputQueueData inputQueueData = gson.fromJson(inputData, InputQueueData.class);
		
		return inputQueueData;
	}

	/**
	 * Worker 에서 수신한 결과를 포맷에 맞게 변환(String -> Json String)
	 * @param outputStr
	 * @return
	 */
	public static String convertOutput(String outputStr) {
		
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("result", outputStr);
		
		Gson gson = new Gson();
		String result = gson.toJson(jsonObject);
		
		return result;
	}
}
