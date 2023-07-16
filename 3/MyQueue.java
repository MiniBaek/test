package com.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class MyQueue {

	private String name;
	private int maxSize;
	private LinkedList<String> queue;
	private Map<String, String> waitingMap;
	private List<String> waitingList;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getMaxSize() {
		return maxSize;
	}
	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}
	public LinkedList<String> getQueue() {
		return queue;
	}
	public void setQueue(LinkedList<String> queue) {
		this.queue = queue;
	}

	public Map<String, String> getWaitingMap() {
		return waitingMap;
	}
	public void setWaitingMap(Map<String, String> waitingMap) {
		this.waitingMap = waitingMap;
	}


	public List<String> getWaitingList() {
		return waitingList;
	}
	public void setWaitingList(List<String> waitingList) {
		this.waitingList = waitingList;
	}
	public MyQueue(String name, int maxSize) {
		super();
		this.name = name;
		this.maxSize = maxSize;
		this.queue = new LinkedList<>();
		this.waitingMap = new HashMap<String, String>();
		this.waitingList = new ArrayList<String>();
	}

}
