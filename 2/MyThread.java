import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpMethod;

public class MyThread implements Runnable{

	private int queueNo;
	private String queueInfoUri;
	private String outputUri;
	//private Worker worker;
	
	
	public MyThread(int queueNo, String queueInfoUri, String outputUri) {
		super();
		this.queueNo = queueNo;
		this.queueInfoUri = queueInfoUri;
		this.outputUri = outputUri;
	}


	@Override
	public void run() {
		
		String inputData = null;
		InputQueueData queueData = null;
		
		this.worker = new Worker(queueNo);
		HttpClient httpClient = new HttpClient();
		try {
			httpClient.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		while(true) {
			
			//큐 입력정보 조회
			ContentResponse contentRes;
			try {
				contentRes = httpClient.newRequest(queueInfoUri).method(HttpMethod.GET).send();
				inputData = contentRes.getContentAsString();
				queueData = Convertor.convertFromJsonStringToQueueData(inputData);
			} catch (InterruptedException | TimeoutException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			// 큐정보 요청
			String outputStr = null;
			outputStr = worker.run(queueData.getTimestamp(), queueData.getValue());
			String outputJsonStr = null;
			
			//큐 결과 송신
			if(null != outputStr) {
				outputJsonStr = Convertor.convertOutput(outputStr);
				try {
					
					Request request = httpClient.newRequest(outputUri).method(HttpMethod.POST);
			        request.header(HttpHeader.CONTENT_TYPE, "application/json");
			        request.content(new StringContentProvider(outputJsonStr,"utf-8"));
			        contentRes = request.send();
			        
				} catch (InterruptedException | TimeoutException | ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
