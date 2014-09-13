package gaecore;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import core.Result;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.basic.DefaultOAuthConsumer;

/**
 * Sample code to use Yahoo! Search BOSS
 * Please include the following libraries 
 * 1. Apache Log4j
 * 2. oAuth Signpost
 * 
 * @author xyz
 */
public class YahooAPI2 {

	//private static final Logger log = Logger.getLogger(YahooAPI2.class);
	protected static String yahooServer = "http://yboss.yahooapis.com/ysearch/";
	// Please provide your consumer key here
	public static String consumer_key = "dj0yJmk9VUtEV2ZrWW1rcEE5JmQ9WVdrOVRFMVBkWE5rTlRBbWNHbzlNakE0TnpZNE56azJNZy0tJnM9Y29uc3VtZXJzZWNyZXQmeD1mNw--";

	// Please provide your consumer secret here
	public static String consumer_secret = "ec6fd229372f15867cae39f5a53c6cc2447ac0f7";

	/** The HTTP request object used for the connection */
	private static StHttpRequest httpRequest = new StHttpRequest();

	/** Encode Format */
	private static final String ENCODE_FORMAT = "UTF-8";

	/** Call Type */
	private static final String callType = "web";
	private static final int HTTP_STATUS_OK = 200;

	/**
	 * 
	 * @return
	 */

	private String searchQuery = "";
	public YahooAPI2() {
		//BasicConfigurator.configure();
	}

	protected String prepare_search_query(String searchQuery)
	{
		//code for preparing search query
		try
		{
		searchQuery=URLEncoder.encode(searchQuery,"UTF-8");
		searchQuery=searchQuery.replaceAll("\\+", "%20");
		}catch(Exception exc){}
		return searchQuery;
	}
	
	public String returnHttpData(String searchQuery)
			throws UnsupportedEncodingException, Exception {

		String responseData = "";
		if (this.isConsumerKeyExists() && this.isConsumerSecretExists()) {
			// Start with call Type
			String params = callType;
			// response data
			// Add query
			params = params+"?q=";
			
			//preparing search query
			searchQuery=prepare_search_query(searchQuery);
			
			// Encode Query string before concatenating
			params = params +searchQuery; //"\""+URLEncoder.encode(searchQuery, ENCODE_FORMAT)+"\"";
			// System.out.println(URLEncoder.encode(this.searchQuery, "UTF-8"));
			// adding format
			// params=params.concat("&format=xml");
			// Create final URL
			String url = yahooServer + params;
			System.out.println("Sent the url >>" + url);
			// Create oAuth Consumer
			OAuthConsumer consumer = new DefaultOAuthConsumer(consumer_key,consumer_secret);
			// Set the HTTP request correctly
			httpRequest.setOAuthConsumer(consumer);

			try {
				// log.info("sending get request to" + URLDecoder.decode(url,
				// ENCODE_FORMAT));
				//System.out.println(url);
				int responseCode = httpRequest.sendGetRequest(url);
				// Send the request
				if (responseCode == HTTP_STATUS_OK) {
					// log.info("Response ");
					responseData = httpRequest.getResponseBody();
					// log.info(httpRequest.getResponseBody());
				} else {
					responseData = httpRequest.getResponseBody();
					//System.out.println(responseCode);
					// log.error("Error in response due to status code = " +
					// responseCode);
				}
			} catch (UnsupportedEncodingException e) {
				//log.error("Encoding/Decording error");
			} catch (IOException e) {
				//log.error("Error with HTTP IO", e);
			} catch (Exception e) {
				//log.error(httpRequest.getResponseBody(), e);
				return "";
			}
		} else {
			//log.error("Key/Secret does not exist");
		}
		return responseData;
	}


	private boolean isConsumerKeyExists() {
		if (consumer_key.isEmpty()) {
			//log.error("Consumer Key is missing. Please provide the key");
			return false;
		}
		return true;
	}

	private boolean isConsumerSecretExists() {
		if (consumer_secret.isEmpty()) {
			//log.error("Consumer Secret is missing. Please provide the key");
			return false;
		}
		return true;
	}

	/**
	 * @param args
	 */
	public ArrayList<Result> get_Yahoo_Results(String searchQuery) {	
		ArrayList<Result> results = null;
		try {
			String jsonString = this.returnHttpData(searchQuery);
			//System.out.println("Returned results:"+jsonString);
			YahooJSONDecoder decoder = new YahooJSONDecoder();
			results = decoder.parse_josn_code(jsonString);
			System.out.println("Results from Yahoo: "+results.size());
		} catch (Exception exc) {
			System.err.println("Failed to get results from Yahoo!");
		}
		return results;
	}

	public static void main(String[] args) {
		try {
			String searchQuery = "java.io.InterruptedIOException currentThread flush socketWrite SocketOutputStream write socketWrite0 out";
			YahooAPI2 signPostTest = new YahooAPI2();
			//signPostTest.returnHttpData(searchQuery);
			//String responseBody = signPostTest.returnHttpData(searchQuery);
			ArrayList list=signPostTest.get_Yahoo_Results(searchQuery);
			System.out.println("Yahoo search ");
			System.out.println("Result size:"+list.size());

		} catch (Exception e) {
			//log.info("Error", e);
			System.out.println("Failed to get results from Yahoo:"+e.getMessage());
		}
	}

}
