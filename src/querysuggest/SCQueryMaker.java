package querysuggest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import utility.StackTraceUtils;
import core.StackTraceElem;

public class SCQueryMaker {

	/**
	 * @param args
	 */

	String oldQuery;
	String stackTrace;
	String cContext;
	String searchQuery;

	public SCQueryMaker(String oldQuery, String stackTrace, String cContext) {
		this.oldQuery = oldQuery;
		this.stackTrace = stackTrace;
		this.cContext = cContext;
		this.searchQuery = new String();
	}

	public String getSearchQuery() {
		// code for getting search query
		StackTraceUtils utils = new StackTraceUtils(this.stackTrace);
		// error or exception name
		String errorMessage = utils.get_error_message();
		System.out.println("Error:" + errorMessage);
		// String exceptionName=utils.extract_exception_name();
		// System.out.println("Exception:" +exceptionName);
		HashSet<String> mytokens = new HashSet<>();
		HashSet<String> tokens1 = collect_strace_references();
		HashSet<String> tokens2 = collect_tokens_from_context_code();
		mytokens.addAll(tokens1);
		mytokens.addAll(tokens2);
		// creating the search query
		searchQuery += errorMessage;
		// String[] existingtokens=errorMessage.split("\\s+");

		// int total_tokens=10; //total allowed tokens
		// int allwoed_limit=total_tokens-existingtokens.length;
		// collect top 5
		int limit = mytokens.size();// <5?mytokens.size():5;
		for (int i = 0; i < limit; i++) {
			String token = (String) mytokens.toArray()[i];
			searchQuery += " " + token;
		}
		return searchQuery;
	}

	protected HashSet<String> collect_strace_references() {
		// code for collecting stack trace references
		// token hash set will contain tokens
		StackTraceUtils utils = new StackTraceUtils(this.stackTrace);
		HashSet<String> tokenhset = new HashSet<String>();
		// now analyze the trace lines
		// collect top
		ArrayList<StackTraceElem> elems = utils.get_trace_elem_for_lines();
		int traceLimit = elems.size();// <3?elems.size():3;
		for (int i = 0; i < traceLimit; i++) {
			StackTraceElem elem = elems.get(i);
			tokenhset.add(elem.className);
			tokenhset.add(elem.methodName);
		}
		return tokenhset;
	}

	protected HashSet<String> collect_tokens_from_context_code() {
		// code for collecting tokens from context code
		HashSet<String> ccontext_tokens = new HashSet<>();
		IslandParser parser = new IslandParser(this.cContext);
		ArrayList<String> method_call_tokens = parser.collect_method_calls();
		ArrayList<String> import_tokens = parser.collect_import_stmt_tokens();
		ArrayList<String> masterList = new ArrayList<>();
		masterList.addAll(method_call_tokens);
		masterList.addAll(import_tokens);
		HashMap<String, Integer> tokendict = new HashMap<>();
		for (String token : masterList) {
			if (!tokendict.containsKey(token)) {
				tokendict.put(token, new Integer(1));
			} else {
				Integer val = tokendict.get(token);
				int count = val.intValue() + 1;
				tokendict.put(token, new Integer(count));
			}
		}
		// now sort the hash map
		List<Entry<String, Integer>> list = new LinkedList<>(
				tokendict.entrySet());
		Collections.sort(list, new CustomComparator_counter());
		int limit = list.size();// <3?list.size():3;
		for (int i = 0; i < limit; i++) {
			Entry<String, Integer> entry = list.get(i);
			// System.out.println(entry.getKey()+" "+entry.getValue());
			String key = entry.getKey();
			ccontext_tokens.add(key);
		}
		return ccontext_tokens;
	}

	public class CustomComparator_counter implements
			Comparator<Entry<String, Integer>> {
		@Override
		public int compare(Entry<String, Integer> e1, Entry<String, Integer> e2) {
			if (e1.getValue() > e2.getValue())
				return -1;
			else if (e1.getValue() < e2.getValue())
				return 1;
			else
				return 0;
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
