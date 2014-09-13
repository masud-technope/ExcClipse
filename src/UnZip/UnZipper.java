package UnZip;


//this class is responsible for unzipping the zipped data

import java.io.*;
import java.util.zip.GZIPInputStream;
public class UnZipper {

	//public String unzip_the_response(byte[] responseBody)
	public String unzip_the_response(InputStream responseBody)
	{
		String responseString="";
		try
		{
			//ByteArrayInputStream byteArrayInputStream=new ByteArrayInputStream(responseBody);
			//GZIPInputStream gzipInputStream=new GZIPInputStream(byteArrayInputStream);
			GZIPInputStream gzipInputStream=new GZIPInputStream(responseBody);
			ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
			int value=0;
			while(value!=-1)
			{
				value=gzipInputStream.read();
				byteArrayOutputStream.write(value);
				
			}
			//converting byte array to string
			responseString=byteArrayOutputStream.toString();
			responseString=responseString.substring(0,responseString.length()-1);
			
			
		}catch(Exception exc){}
		return responseString;
	}
	
	
	
	
}
