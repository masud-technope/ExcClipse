package gaecore;


//Stack Overflow JSON Decoder


import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;
import core.Result;
public class SOJSONDecoder {
	
	
	public ArrayList parse_json_code2(String jsonString)
	{
		//code for parsing SOJSON code
		String[] results=null;
		ArrayList<Result> searchResult=new ArrayList<Result>();
		try
		{
			
			String base_url="http://stackoverflow.com";
		
			//code for JSON extraction
			JSONParser parser=new JSONParser();
			Object object=parser.parse(jsonString);
			Object dObj=((JSONObject)object).get("items");
			//System.out.println(object);
			//Object resultObj=((JSONObject)dObj).get("results");
			//System.out.println(resultObj);
			JSONArray resultArray=(JSONArray)dObj;
			
			int array_size=resultArray.size();
			results=new String[array_size];
			
			for(int i=0;i<resultArray.size();i++)
			{
				JSONObject jsobj=(JSONObject)resultArray.get(i);
				//System.out.println(jsobj);
				
				//creating object
				Result myresult=new Result();
				//title
				String title=jsobj.get("title").toString();
				myresult.title=title.replaceAll("\\<.*?>","");
				//System.out.println(jsobj.get("title"));
				String answer_url=jsobj.get("link").toString();
				//answer_url=base_url+answer_url;
				//result URL
				myresult.resultURL=answer_url;
				//System.out.println(answer_url);
				
				//collecting SO scores
				int post_score= Integer.parseInt(jsobj.get("score").toString());
				myresult.SOVoteScore=post_score;
				boolean is_answered=Boolean.parseBoolean(jsobj.get("is_answered").toString());
				//myresult.SOAcceptedScore=is_answered==true?1:0;
				int view_count=Integer.parseInt(jsobj.get("view_count").toString());
				myresult.SOViewCountScore=view_count;
				
				//System.out.println(jsobj.get("unescapedUrl"));
				//String view_count=jsobj.get("view_count").toString();
				//System.out.print("Total Viewed:"+view_count);
				//System.out.println();
				//adding class object
				
				searchResult.add(myresult);
				String result=jsobj.get("title")+"\n"+base_url+answer_url;
				results[i]=result;
			}
			
		}catch(Exception exc){
		
			//System.out.println(exc.getMessage());
			//exc.printStackTrace();
		}
		
		//returning the results
		return searchResult;
		
		
	}
	
	public ArrayList parse_json_code(String json)
	{
		String[] results=null;
		ArrayList<Result> searchResult=new ArrayList<Result>();
		try
		{
			
			String base_url="http://stackoverflow.com";
		
			//code for JSON extraction
			JSONParser parser=new JSONParser();
			Object object=parser.parse(json);
			Object dObj=((JSONObject)object).get("questions");
			//System.out.println(object);
			//Object resultObj=((JSONObject)dObj).get("results");
			//System.out.println(resultObj);
			JSONArray resultArray=(JSONArray)dObj;
			
			int array_size=resultArray.size();
			results=new String[array_size];
			
			for(int i=0;i<resultArray.size();i++)
			{
				JSONObject jsobj=(JSONObject)resultArray.get(i);
				//System.out.println(jsobj);
				
				//creating object
				Result myresult=new Result();
				//title
				myresult.title=jsobj.get("title").toString();
				//System.out.println(jsobj.get("title"));
				String answer_url=jsobj.get("question_answers_url").toString();
				answer_url=base_url+answer_url;
				//result URL
				myresult.resultURL=answer_url;
				//System.out.println(answer_url);
				//System.out.println(jsobj.get("unescapedUrl"));
				//String view_count=jsobj.get("view_count").toString();
				//System.out.print("Total Viewed:"+view_count);
				//System.out.println();
				
				//adding class object
				searchResult.add(myresult);
				
				
				
				String result=jsobj.get("title")+"\n"+base_url+answer_url;
				results[i]=result;
				
				
			}
			
		}catch(Exception exc){
		
			//System.out.println(exc.getMessage());
			//exc.printStackTrace();
		}
		
		//returning the results
		return searchResult;
	}
	

}
