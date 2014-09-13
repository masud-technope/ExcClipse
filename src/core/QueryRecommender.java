package core;

import java.util.ArrayList;

public class QueryRecommender {
	
	String stackTrace;
	String codeContext;
	public QueryRecommender()
	{
		//default constructor
	}
	
	protected String normalizeExceptionName(String excepName)
	{
		//normalize the exception name
		String[] tokens=excepName.split("\\.");
		return tokens[tokens.length-1].toLowerCase();
	}
	
	public ArrayList<String> recommendQueries()
	{
		//recommending queries
		ContextProvider contextProvider=new ContextProvider();
		String stack=contextProvider.extract_stacktrace_from_console();
		String ccode=contextProvider.extract_source_code_context(stack);
		StackTraceManager stManager=new StackTraceManager(stack, ccode);
		stManager.collectTokenScores();
		String errorMessage=stManager.errorMessage;
		//customizing query length
		if(errorMessage.length()>20){
			stManager.queryLength=2;
		}
		
		ArrayList<QueryPhrase> phrases=stManager.calculateFinalScores();
		String exceptionName=normalizeExceptionName(stManager.exceptionName);
		ArrayList<String> queries=new ArrayList<>();
		int count=0;
		for(QueryPhrase phrase:phrases){
			String query=new String();
			String temp=exceptionName+" "+errorMessage+" "+phrase.queryStatement;
			
			if(temp.length()>128){ //Google's query limit
				query=exceptionName+" "+phrase.queryStatement;
			}
			else query=exceptionName+" "+errorMessage.toLowerCase()+" "+phrase.queryStatement;
			queries.add(query);
			count++;
			if(count==5)break;
		}
		return queries;
	}
	
	

}
