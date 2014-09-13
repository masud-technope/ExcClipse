package gaecore;


//code for accessing Google Search API
//Author: Md. Masudur Rahman
//02 October 2012

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.*;
import java.util.ArrayList;
import core.Result;

public class GoogleAPI {

	/**
	 * @param args
	 */
	//result set
	ArrayList<Result> Google_Results=null;
	
	
	//constructor of API
	public GoogleAPI()
	{
		Google_Results=new ArrayList<Result>();
	}
	
	
	protected String get_the_search_results(int startIndex, String main_query) {
		// code for getting results
		String responseBody = "";
		try {
			String api_key = "AIzaSyCfaOdjZIlt_VuFeKGQQhiZo1Y7JvMnWek";
			// String
			//String google="https://www.googleapis.com/customsearch/v1?key="+api_key+"&cx=017576662512468239146:omuauf_lfve&q=";
			String google = "http://ajax.googleapis.com/ajax/services/search/web?v=1.0&rsz=8&start="
					+ startIndex + "&q=";
			// google="http://ajax.googleapis.com/ajax/services/search/local?";
			String search = "Cancer";
			String charset = "UTF-8";
			search = main_query;// JOptionPane.showInputDialog(null,"Enter your search query");
			URL url = new URL(google + URLEncoder.encode(search, charset));
			// search string
			//String searchURL = google + URLEncoder.encode(search, charset);
			BufferedReader breader=new BufferedReader(new InputStreamReader(url.openStream()));
			String line=null;
			while((line=breader.readLine())!=null)
			{
				responseBody+=line;
			}
			breader.close();
		} catch (Exception exc) {

			// exc.printStackTrace();
			// System.err.println("Failed to get the results:"+exc.getMessage());
		}
		// returning the response
		return responseBody;
	}
	
	
	protected String get_the_search_results1(int startIndex, String main_query) {
		// code for getting results
		String responseBody = "";
		try {
			String search_engine_id="009677617567335087610:gsenrsieabe";
			//017576662512468239146:omuauf_lfve
			String api_key = "AIzaSyCfaOdjZIlt_VuFeKGQQhiZo1Y7JvMnWek";
			String google="https://www.googleapis.com/customsearch/v1?key="+api_key+"&num=10&start="+startIndex+"&cx="+search_engine_id+"&q=";
			String charset = "UTF-8";
			String searchURL = google + URLEncoder.encode(main_query, charset);
			URL url = new URL(searchURL);
			BufferedReader breader=new BufferedReader(new InputStreamReader(url.openStream()));
			String line=null;
			while((line=breader.readLine())!=null)
			{
				responseBody+=line;
			}
			breader.close();
		} catch (Exception exc) {
			//System.err.println(exc.getMessage());
		}
		// returning the response
		return responseBody;
	}
	
	
	public ArrayList<Result> find_Google_Results(String main_query)
	{
		// TODO Auto-generated method stub
		try
		{
		     for(int start=1;start<25;start+=10)
		     {
		    	 String responseBody=get_the_search_results1(start, main_query);
		    	 //System.out.println(responseBody);
		    	 GoogleJSONDecoder gooJson=new GoogleJSONDecoder();
			     ArrayList<Result> tempList=gooJson.parse_json_code2(responseBody);
			     //now add them to the main list
			     for(int k=0;k<tempList.size();k++)
			     {
			    	 Google_Results.add((Result)tempList.get(k));
			     }
			     if(tempList.size()<10)break; //if results less than page size
		     }
		     System.out.println("Results from Google:"+Google_Results.size());
		}catch(Exception exc){
			System.out.println("Failed to collect results from Google:");
			exc.printStackTrace();
		}
		//returning the results
		return Google_Results;

	}
	
	protected void show_google_results(int queryNo)
	{
		//code for showing google results
		for(int i=0;i<this.Google_Results.size();i++)
		{
			Result result=(Result)this.Google_Results.get(i);
			System.out.println(result.resultURL);
		}
	}
	
	
	public static void main(String args[])
	{
		String fileName = "D:/My MSc/CMPT 811/SurfClipse Tool Demo/ranking/secounts/query.txt";
		try {
			//Scanner scanner = new Scanner(new File(fileName));
			int count=0;
			//while (scanner.hasNext()) {
				//String searchQuery ="java.io.InterruptedIOException: SocketOutputStream";
				String searchQuery="java.io.InterruptedIOException currentThread flush socketWrite SocketOutputStream write socketWrite0 out";
				int queryNo = 75;
				//if(queryNo<=59)continue;
				GoogleAPI googAPI = new GoogleAPI();
				googAPI.Google_Results = googAPI.find_Google_Results(searchQuery);
				googAPI.show_google_results(queryNo);
				//googAPI.save_google_results(queryNo);
				//Thread.sleep(3000);
			//}
		} catch (Exception exc) {
		}
		
	}
}
