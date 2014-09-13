package gaecore;




//code for Google JSON decoder
//Author: Md. Masudur Rahman

import java.util.ArrayList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;
import core.Result;
public class GoogleJSONDecoder {
	
	public ArrayList parse_json_code(String json)
	{
		String[] results=null;
		ArrayList<Result> searchResult=new ArrayList<Result>();
		try
		{
		
			//code for JSON extraction
			JSONParser parser=new JSONParser();
			Object object=parser.parse(json);
			Object dObj=((JSONObject)object).get("responseData");
			//System.out.println(dObj);
			Object resultObj=((JSONObject)dObj).get("results");
			//System.out.println(resultObj);
			JSONArray resultArray=(JSONArray)resultObj;
			
			int array_size=resultArray.size();
			results=new String[array_size];
			
			for(int i=0;i<resultArray.size();i++)
			{
				JSONObject jsobj=(JSONObject)resultArray.get(i);
				
				//creating object
				Result myresult=new Result();
				
				//title
				String title=jsobj.get("titleNoFormatting").toString();
				title=title.replaceAll("\\<.*?>","");
				myresult.title=title;
				//System.out.println(jsobj.get("titleNoFormatting"));
				
				String content=jsobj.get("content").toString();
				content=content.replaceAll("\\<.*?>","");
				//description
				myresult.description=content;
				//URL
				myresult.resultURL=jsobj.get("unescapedUrl").toString();
				//System.out.println(content);
				//System.out.println(jsobj.get("unescapedUrl"));
				//System.out.println();	
				//adding to array list
				searchResult.add(myresult);
				
				
				String result=jsobj.get("titleNoFormatting")+"\n"+content+"\n"+jsobj.get("unescapedUrl");
				results[i]=result;
				
			}
			
		}catch(Exception exc){
			
		}
		return searchResult;
	
	}
	public ArrayList parse_json_code2(String json)
	{
		String[] results=null;
		ArrayList<Result> searchResult=new ArrayList<Result>();
		try
		{
		
			//code for JSON extraction
			JSONParser parser=new JSONParser();
			Object object=parser.parse(json);
			Object dObj=((JSONObject)object).get("items");
			//System.out.println(dObj);
			//Object resultObj=((JSONObject)dObj).get("results");
			//System.out.println(resultObj);
			JSONArray resultArray=(JSONArray)dObj;
			
			int array_size=resultArray.size();
			results=new String[array_size];
			
			for(int i=0;i<resultArray.size();i++)
			{
				JSONObject jsobj=(JSONObject)resultArray.get(i);
				
				//creating object
				Result myresult=new Result();
				//title
				myresult.title=jsobj.get("title").toString();
				//System.out.println(jsobj.get("titleNoFormatting"));
				
				String content=jsobj.get("snippet").toString();
				content=content.replaceAll("\\<.*?>","");
				//description
				myresult.description=content;
				//URL
				myresult.resultURL=jsobj.get("link").toString();
				
				//System.out.println(content);
				//System.out.println(jsobj.get("unescapedUrl"));
				//System.out.println();	
				//adding to array list
				searchResult.add(myresult);
				
				
				//String result=jsobj.get("titleNoFormatting")+"\n"+content+"\n"+jsobj.get("unescapedUrl");
				//results[i]=result;
				
			}
			
		}catch(Exception exc){
			
		}
		return searchResult;
	
	}
	
	
	
	
	
	
	

}
