package mytest2.info;

public class InputData {
	
	private Long timestamp;
	private int queueNo;
	private String value;
	
	public InputData(Long timestamp, int queueNo, String value) {
		super();
		this.timestamp = timestamp;
		this.queueNo = queueNo;
		this.value = value;
	}

	public int getQueueNo() {
		return queueNo;
	}

	public void setQueueNo(int queueNo) {
		this.queueNo = queueNo;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}
	
	
	
}
