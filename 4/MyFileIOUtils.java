package mytest4.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class MyFileIOUtils {

	public static String readAllContentOfFile(String fileName) throws IOException {
		
		StringBuffer sb = new StringBuffer();
		String str;
		BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));
		while ((str = bufferedReader.readLine()) != null) {
			sb.append(str);
		}
		bufferedReader.close();
		
		return sb.toString();
	}
}
