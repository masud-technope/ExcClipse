package gaecore;
//code for accessing stack over flow search API data

import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import UnZip.*;
import java.net.*;
import core.Result;

public class SOAPIDemo {

	/**
	 * @param args
	 */
	
	ArrayList<Result> SO_Results=null;
	public ArrayList<Result> find_SO_results(String main_query)
	{
		// TODO Auto-generated method stub
		try
		{
		String searchQuery="C#";
		searchQuery=main_query;	//JOptionPane.showInputDialog(null,"Enter your question or keyword");
		String charset="UTF-8";
		//String urlString="http://api.stackoverflow.com/1.1/search?intitle="+URLEncoder.encode(searchQuery,charset);
		String urlString="https://api.stackexchange.com/2.1/search/advanced?key=CSU*LgYG4mIzQI7WLit4XA((&order=desc&site=stackoverflow&pagesize=30&sort=relevance&q="+URLEncoder.encode(searchQuery,charset);
		URL url=new URL(urlString);
		InputStream soStream=url.openStream();
		try
		{
			  //Unzipping the response
		      UnZipper unzipper=new UnZipper();
		      String responseString=unzipper.unzip_the_response(soStream);
		      //System.out.println(responseString);
		      //now decode the string
		      SOJSONDecoder jsdec=new SOJSONDecoder();
		      SO_Results=jsdec.parse_json_code2(responseString);
		      System.out.println("Results from SO: "+SO_Results.size());
			
		}catch(Exception exc){
			System.err.println("Failed to get results from SO: "+exc.getMessage());
		}
		}catch(Exception exc){
			exc.printStackTrace();
		}
	
		//returning the results
		return SO_Results;
	}
	
	
	
	public static void main(String args[])
	{
		String fileName = "D:/My MSc/CMPT 811/SurfClipse Tool Demo/ranking/secounts/query.txt";
		try {
			//Scanner scanner = new Scanner(new File(fileName));
			int count=0;
			 //while (scanner.hasNext()) {
				String searchQuery="Problems occurred when invoking code from plug-in: org.eclipse.ui.workbench";//scanner.nextLine(); //"ADT requires 'org.eclipse.wst.sse.core 0.0.0' but it could not be found";
				int queryNo = ++count;
				//if(queryNo<=59)continue;
				SOAPIDemo soAPI = new SOAPIDemo();
				soAPI.SO_Results = soAPI.find_SO_results(searchQuery);
				System.out.println("Results extracted:"+soAPI.SO_Results.size());
				//soAPI.save_so_results(queryNo);
				//Thread.sleep(3000);
		} catch (Exception exc) {
		}
		
		
	}
	
	
	
}

