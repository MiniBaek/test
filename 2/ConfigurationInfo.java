package mytest2.info;
import java.util.List;

public class ConfigurationInfo {

	private int inputQueueCount;
	private List<String> inputQueueURIs;
	private String outputQueueURI;
	
	public ConfigurationInfo(int inputQueueCount, List<String> inputQueueURIs, String outputQueueURI) {
		super();
		this.inputQueueCount = inputQueueCount;
		this.inputQueueURIs = inputQueueURIs;
		this.outputQueueURI = outputQueueURI;
	}

	public int getInputQueueCount() {
		return inputQueueCount;
	}

	public void setInputQueueCount(int inputQueueCount) {
		this.inputQueueCount = inputQueueCount;
	}

	public List<String> getInputQueueURIs() {
		return inputQueueURIs;
	}

	public void setInputQueueURIs(List<String> inputQueueURIs) {
		this.inputQueueURIs = inputQueueURIs;
	}

	public String getOutputQueueURI() {
		return outputQueueURI;
	}

	public void setOutputQueueURI(String outputQueueURI) {
		this.outputQueueURI = outputQueueURI;
	}
}
