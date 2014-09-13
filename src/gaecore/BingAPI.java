package gaecore;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Scanner;
import org.apache.commons.codec.binary.Base64;
import core.Result;




public class BingAPI {

	/**
	 * @param args
	 */
	
	ArrayList<Result> Bing_Results;
	
	@SuppressWarnings("unchecked")
	public ArrayList<Result> find_Bing_Results(String main_query)
	{
		
		// TODO Auto-generated method stub
		ArrayList<Result> BingResults=null;
	    try
	    {
		//required Credentials for login into the server
	    String accountKey="MaCbHW5ue0b0fJ0ArvxXMgvONyLNaBA+Ck9HFmLA/3U=";
	    //String clientKey="51f12a6c-c88b-40c9-b127-c2ccef0f5a4a";
	    String query=main_query;//JOptionPane.showInputDialog(null,"Enter your search query");
	    String charset="UTF-8";
	    String searchURL="https://api.datamarket.azure.com/Bing/Search/Web?Query='"+URLEncoder.encode(query,charset)+"'&$format=JSON&$top=30";	
	    URL url=new URL(searchURL);
	    HttpURLConnection urlConn=(HttpURLConnection)url.openConnection();
	    String basic_auth = new String(Base64.encodeBase64((accountKey + ":" + accountKey).getBytes()));
	    urlConn.addRequestProperty("Authorization", "Basic "+basic_auth);
	    urlConn.setRequestMethod("GET");
	    if(urlConn.getResponseCode()==HttpURLConnection.HTTP_OK)
	    {
	    	  BufferedReader breader=new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
	    	  String line=null;
	    	  String responseString=new String();
	    	  while((line=breader.readLine())!=null)
	    	  {
	    		  responseString+=line;
	    	  }
		      //System.out.println(responseString);
		      BingJSONDecoder jsdecoder=new BingJSONDecoder();
		      BingResults=jsdecoder.parse_json_code(responseString);
		      System.out.println("Results from Bing:"+BingResults.size());
	    }
	    }catch(Exception exc){
	    	System.err.println("Failed to retrieve results from Bing :"+exc.getMessage());
	    } 
	    return BingResults;
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			String fileName = "D:/My MSc/CMPT 811/SurfClipse Tool Demo/ranking/secounts/query.txt";
			BingAPI bingAPI = new BingAPI();
			// String josnString=yahooAPI.returnHttpData(searchQuery);
			//Scanner scanner = new Scanner(new File(fileName));
			String searchQuery = "Disable DTD warning for Ant scripts in Eclipse?";
			//YahooAPIDemo demo = new YahooAPIDemo();
			int queryNo = 0;
			//while (scanner.hasNext()) {
				//searchQuery = scanner.nextLine().trim();
				ArrayList results = bingAPI.find_Bing_Results(searchQuery);
				bingAPI.Bing_Results = results;
				System.out.println("Result found:"+bingAPI.Bing_Results.size());
				queryNo++;
				//bingAPI.save_bing_results(queryNo);
			//}

			/*
			 * System.out.println("ArrayList size="+results.size()); //browsing
			 * through the results for(int i=0;i<results.size();i++) { Result
			 * result=(Result)results.get(i); System.out.println(result.title+
			 * " "+result.resultURL); }
			 */
		} catch (Exception exc) {
			System.err.println(exc.getMessage());
		}

	}

	


}
