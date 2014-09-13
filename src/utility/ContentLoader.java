package utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.URL;

public class ContentLoader {
	
	public static String loadFileContent(String fileName) {
		// code for loading the file name
		String fileContent = new String();
		try {
			File f = new File(fileName);
			BufferedReader bufferedReader = new BufferedReader(
					new FileReader(f));
			while (bufferedReader.ready()) {
				String line = bufferedReader.readLine();
				fileContent += line + "\n";
			}
		} catch (Exception ex) {
			// handle the exception
		}
		return fileContent;
	}
	
	public static String downloadPageContent(String pageURL) {
		// downloading page content
		String content = new String();
		try {
			URL u = new URL(pageURL);
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(u.openStream()));
			int value = 0;
			while ((value = bufferedReader.read()) != -1) {
				char c = (char) value;
				content += c;
			}
			bufferedReader.close();
		} catch (Exception exc) {
			// handle the exception
		}
		return content;

	}
}
