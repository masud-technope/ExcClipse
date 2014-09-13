package gaecore;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import core.Result;

public class YahooJSONDecoder {

	/**
	 * @param args
	 */
	
	public ArrayList<Result> parse_josn_code(String jsonString)
	{	
		ArrayList<Result> searchResult=new ArrayList<Result>();
		try
		{
		 //code for parsing Yahoo JSON results
		  JSONParser parser=new JSONParser();
		  Object object=parser.parse(jsonString);
		  JSONObject jsonObject=(JSONObject)object;
		  Object responseObj=jsonObject.get("bossresponse");
		  Object webObj=((JSONObject)responseObj).get("web");
		  Object resultObj=((JSONObject)webObj).get("results");
		  JSONArray ja=(JSONArray)resultObj;
          int resultCount=ja.size();
          // Loop over each result and print the title, summary, and URL

		for (int i = 0; i < resultCount; i++)
		  {
		  JSONObject resultObject = (JSONObject)ja.get(i);
		  Result resultItem=new Result();
		  //title
		  String title=resultObject.get("title").toString();
		  title=title.replaceAll("\\<.*?>","");;
		  resultItem.title=title;
          //URL
		  resultItem.resultURL=resultObject.get("url").toString();
          //description
		  String description=resultObject.get("abstract").toString();
		  description=description.replaceAll("\\<.*?>","");
		  resultItem.description=description;
		  
          searchResult.add(resultItem);
		  }
		}catch(Exception exc){}
		//returning ArrayList
		return searchResult;	
	}
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
