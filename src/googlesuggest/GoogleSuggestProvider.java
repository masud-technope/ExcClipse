package googlesuggest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class GoogleSuggestProvider {

	String searchQuery;
	public GoogleSuggestProvider(String searchQuery)
	{
		this.searchQuery=searchQuery;
	}

	public ArrayList<String> provideGoogleSuggestions() {
		// provide Google suggestion
		ArrayList<String> suggestions=null;
		String url = "http://suggestqueries.google.com/complete/search?client=firefox&q="
				+ this.searchQuery;
		try {
			URL u = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) u.openConnection();
			String jsonResponse = new String();
			if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				BufferedReader breader = new BufferedReader(
						new InputStreamReader(conn.getInputStream()));
				String line = null;
				while ((line = breader.readLine()) != null) {
					jsonResponse += line;
				}
			}
			//System.out.println(jsonResponse);
			suggestions=parseJSONResponse(jsonResponse);
			//System.out.println(suggestions);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return suggestions; //.toArray(new String[suggestions.size()]);

	}
	
	protected ArrayList<String> parseJSONResponse(String jsonStr)
	{
		//parsing JSON strings
		ArrayList<String> suggestions=new ArrayList<>();
		JSONParser parser=new JSONParser();
		try {
			JSONArray jarray=(JSONArray) parser.parse(jsonStr);
			JSONArray jarray2=(JSONArray) jarray.get(1); //getting the suggestions
			for(Object obj:jarray2){
				suggestions.add(obj.toString());
			}
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return suggestions;
	}
	
	
	
	public static void main(String[] args){
		GoogleSuggestProvider provider=new GoogleSuggestProvider("ConcurrentModificationException");
		provider.provideGoogleSuggestions();
	}
}
