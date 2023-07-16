package com.test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class RunManager {

	public static void main(String[] args) throws IOException {
		
		while(true) {
			
			Scanner scanner = new Scanner(System.in);
			String proxyName = scanner.nextLine();
			List<String> targetList = getProxyInfo(proxyName);
			for(String s : targetList) {
				System.out.println(s);
				
			}
		}
	}
	
	public static List<String> getProxyInfo(String fileName) throws IOException{
		List<String> readStrings = new ArrayList<String>();

		if(!fileName.endsWith(".txt")) {
			fileName = fileName + ".txt";
		}
		BufferedReader reader = new BufferedReader(new FileReader(fileName));

		String str1, str2;
		while ((str1 = reader.readLine()) != null) {
			
			BufferedReader reader2 = new BufferedReader(new FileReader(str1));
			while ((str2 = reader2.readLine()) != null) {
				readStrings.add(str2);
			}
		}

		reader.close();

		return readStrings;
	}

}
