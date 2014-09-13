//code to convert JSON string returned by Bing
package gaecore;
import java.util.ArrayList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;
import core.Result;


public class BingJSONDecoder {

	
	public ArrayList parse_json_code(String json)
	{
		String[] results=null;
		ArrayList<Result> searchResult=new ArrayList<Result>();
		try
		{
			//code for JSON extraction
			JSONParser parser=new JSONParser();
			Object object=parser.parse(json);
			Object dObj=((JSONObject)object).get("d");
			//System.out.println(dObj);
			Object resultObj=((JSONObject)dObj).get("results");
			//System.out.println(resultObj);
			JSONArray resultArray=(JSONArray)resultObj;
			
			int array_size=resultArray.size();
			results=new String[array_size];
			
			
			for(int i=0;i<resultArray.size();i++)
			{
				JSONObject jsobj=(JSONObject)resultArray.get(i);
				
				String result=jsobj.get("Title")+"\n"+jsobj.get("Description")+"\n"+jsobj.get("Url");
				results[i]=result;
				
				//creating the object
				Result myresult=new Result();
				//title
				String title=jsobj.get("Title").toString();
				myresult.title=title.replaceAll("\\<.*?>","");
				//description
				String description=jsobj.get("Description").toString();
				description=description.replaceAll("\\<.*?>","");
				myresult.description=description;
				
				myresult.resultURL=jsobj.get("Url").toString();
				//adding to array list
				searchResult.add(myresult);
				
				
				//System.out.println(jsobj.get("Title"));
				//System.out.println(jsobj.get("Description"));
				//System.out.println(jsobj.get("Url"));
				//System.out.println();
				
			}
			
		}catch(Exception exc){
			
		}
		//returning the resutlts
		return searchResult;
	
	}
}
