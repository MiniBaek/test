package com.test;

import java.util.List;

public class TraceResult {

	String target;
	String status;
	List<TraceResult> services;

	public TraceResult() {
		super();
	}
	
	public TraceResult(String target, String status, List<TraceResult> services) {
		super();
		this.target = target;
		this.status = status;
		this.services = services;
	}
	
	public List<TraceResult> getServices()
	{
		return this.services;
	}

	public void setServices(List<TraceResult> services) {
		this.services = services;
	}
	
	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
}

