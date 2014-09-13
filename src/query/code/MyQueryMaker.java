package query.code;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import visitor.code.CodeFragment;
import visitor.code.CodeObject;
import visitor.code.Dependence;
import core.code.StaticData;

public class MyQueryMaker {
	
	int exceptionID;
	String queryContextCode;
	String exceptionName;
	int selectedLine;
	CodeFragment queryFragment=null;
	
	public MyQueryMaker(int exceptionID, String exceptionName) {
		// initialization
		this.exceptionID = exceptionID;
		this.queryContextCode=getContextCode();
		this.exceptionName=exceptionName;
	}
	
	public MyQueryMaker(String exceptionName, String contextCode, int selectedLine)
	{
		//initialization
		this.exceptionName=exceptionName;
		this.queryContextCode=contextCode;
		this.selectedLine=selectedLine;
	}
	
	
	protected String getContextCode() {
		// code for collecting the code
		String content = new String();
		try {
			String ccontextFile = StaticData.Surf_Data_Base + "/ccontext/"
					+ exceptionID + ".txt";
			File f = new File(ccontextFile);
			Scanner scanner = new Scanner(f);
			while (scanner.hasNext()) {
				String line = scanner.nextLine();
				content += line + "\n";
			}
		} catch (Exception exc) {
			// handle the exception
		}
		return content;
	}
	
	
	protected HashMap<CodeObject, Integer> getCodeObjectSortedTokens()
	{
		//analyzing the code tokens
		InputDocProcessor processor = new InputDocProcessor(exceptionName,
				queryContextCode,this.selectedLine);
		queryFragment = processor.extractInputDocumentInfo();
		// The map contains the actions and interactions of a class
		HashMap<CodeObject, Integer> LinkCount = new HashMap<>();

		// collecting from simple codeobject dict
		for (CodeObject cobject : queryFragment.codeObjectMap.values()) {
			int fieldcount = cobject.fields.size();
			int methodcount = cobject.methods.size();
			if (LinkCount.containsKey(cobject)) {
				int count = LinkCount.get(cobject).intValue();
				count += fieldcount + methodcount;
				LinkCount.put(cobject, count);
			} else {
				int count = fieldcount + methodcount;
				LinkCount.put(cobject, count);
			}
		}
		// collecting from dependencies
		for (Dependence dep : queryFragment.dependencies) {
			CodeObject fromObj = dep.fromObject;
			CodeObject toObject = dep.destObject;
			// adding from object
			if (LinkCount.containsKey(fromObj)) {
				int count = LinkCount.get(fromObj).intValue();
				count++;
				LinkCount.put(fromObj, count);

			} else {
				LinkCount.put(fromObj, 1);
			}
			// adding destination object
			if (LinkCount.containsKey(toObject)) {
				int count = LinkCount.get(toObject).intValue();
				count++;
				LinkCount.put(toObject, count);
			} else {
				LinkCount.put(toObject, 1);
			}
		}
		// now sort the item
		HashMap<CodeObject, Integer> sorted = MyItemSorter
				.sortCodeObjectMap(LinkCount);
		return sorted;
	}
	
	public ArrayList<String> getRecommendedQueries()
	{
		HashMap<CodeObject, Integer> sorted=getCodeObjectSortedTokens();
		ArrayList<String> exceptions=recommendException(queryFragment);
		ArrayList<String> queries=new ArrayList<>();
		for(String excName:exceptions){
			String classes=new String();
			int icount=0;
			for(CodeObject cobject:sorted.keySet()){
				int activity=sorted.get(cobject);
				if(activity>0){
					classes+=" "+cobject.className;
					icount++;
				}
				if(icount==2)break;
			}
			if(!classes.isEmpty()){
				queries.add(excName+" "+classes);
			}
		}
		//returning the recommendation
		return queries;
	}
	
	
	
	
	
	public String getGitHubSearchQuery() {
		// code for GitHub query
		String query = new String();
		HashMap<CodeObject, Integer> sorted=getCodeObjectSortedTokens();
		try {
			for (CodeObject cobject : sorted.keySet()) {
				query = cobject.className;
				break;
			}
			if(this.exceptionName.equals("Exception")){
				//in case of generic exception
				this.exceptionName=recommendException(queryFragment).get(0);
				query=this.exceptionName+" "+query;
			}else{
				//in case of specified exception
				query=this.exceptionName+" "+query;
			}
			query = query.trim();
		} catch (Exception exc) {
			// handle the exception`
		}
		return query;
	}
	
	protected String refineCanonicalName(String className)
	{
		//code for refining the canonical name
		String temp=className;
		if(temp.indexOf('<')>0){
			className=temp.substring(0,className.indexOf('<'));
		}
		if(temp.indexOf('[')>0){
			className=temp.substring(0,className.indexOf('['));
		}
		return className;
	}
	
	
	protected ArrayList<String> recommendException(CodeFragment queryFragment) {
		String recommededExceptionName = new String();
		HashMap<String, Integer> checkedExceptions = new HashMap<>();
		HashMap<String, CodeObject> extractedObjects = queryFragment.codeObjectMap;
		for (String key : extractedObjects.keySet()) {
			CodeObject cobject = extractedObjects.get(key);
			try{
			String canonical=refineCanonicalName(cobject.canonicalClassName);
			Class<?> myclass =ClassLoader.class.forName(canonical);
			HashSet<String> usedMethods = cobject.methods;
			Method[] allMethods = myclass.getMethods();
			for (Method method : allMethods) {
				if (usedMethods.contains(method.getName())) {
					Class<?>[] exceptions = method.getExceptionTypes();
					for (Class<?> excep : exceptions) {
						try {
							Object excepObj = excep.newInstance();
							// checked exceptions
							if (excepObj instanceof Exception
									&& !(excepObj instanceof RuntimeException)) {
								String exceptionName = excep.getSimpleName();
								if (checkedExceptions
										.containsKey(exceptionName)) {
									int count = checkedExceptions.get(
											exceptionName).intValue();
									count++;
									checkedExceptions.put(exceptionName, count);
								} else {
									checkedExceptions.put(exceptionName, 1);
								}
							}
						} catch (Exception e) {

						}
					}
				}
			}
			}catch(Exception e){}
			
		}
		// now sort the exception count
		HashMap<String, Integer> sorted = MyItemSorter
				.sortExceptionMap(checkedExceptions);
		ArrayList<String> exceptions=new ArrayList<>();
		exceptions.addAll(sorted.keySet());
		return exceptions;

	}
	
	
	public static void main(String[] args){
		int exceptionID=113;
		String exceptionName="IIOException";
		MyQueryMaker maker=new MyQueryMaker(exceptionID, exceptionName);
		String query=maker.getGitHubSearchQuery();
		System.out.println("Returned query: "+query);
	}
}
