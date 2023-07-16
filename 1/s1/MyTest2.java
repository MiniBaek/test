package com.test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class RunManager {

	static String servicefileName = null;
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		while(true) {

			Scanner scanner = new Scanner(System.in);
			String command = scanner.nextLine();
			String serviceContents = doProcess(command);
			System.out.println(serviceContents);

		}
	}

	public static String doProcess(String command) throws IOException{
		
		List<String> readStrings = new ArrayList<String>();

		String name = command.split(" ")[0];
		String path = command.split(" ")[1];

		getServiceFileInfo(name, path);
		
		BufferedReader reader = new BufferedReader(new FileReader(servicefileName));
		String str;
		while ((str = reader.readLine()) != null) {
			readStrings.add(str);
		}

		return readStrings.get(0);
	}


	private static void getServiceFileInfo(String fileName, String path) throws IOException {

		//Proxy file 읽기
		if(!fileName.endsWith(".txt")) {
			fileName = fileName + ".txt";
		}
		
		BufferedReader reader = new BufferedReader(new FileReader(fileName));
		String str;
		while ((str = reader.readLine()) != null) {
			if(str.startsWith(path)) {
				if(str.split("#")[1].startsWith("Service")) {
					servicefileName = str.split("#")[1];
					break;
				}else {
					getServiceFileInfo(str.split("#")[1], path);
					break;
				}
			}
		}
	}



}
