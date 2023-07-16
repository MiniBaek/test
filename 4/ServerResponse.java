package mytest4.info;

import java.util.List;

public class ServerResponse {

	private List<String> result;

	public ServerResponse(List<String> deviceResultList) {
		this.result = deviceResultList;
	}

	public List<String> getResult() {
		return result;
	}

	public void setResult(List<String> result) {
		this.result = result;
	}
	
}
