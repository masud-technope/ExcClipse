package querysuggest;

//import utility.ContentLoader;
//import core.StaticData;

public class QueryManager {

	int exceptionID = 0;
	public String stackTrace;
	public String contextCode;

	/*@Deprecated
	public QueryManager(int exceptionID) {
		// initialization
		this.exceptionID = exceptionID;
		this.stackTrace = getStackTrace();
		this.contextCode = new String();
	}*/

	public QueryManager(String strace, String ccontext) {
		// initialization
		this.stackTrace = strace;
		this.contextCode = new String();
	}

	/*@Deprecated
	public String getStackTrace() {
		// get stack trace
		String filePath = StaticData.CE_Data_Home + "/strace/"
				+ this.exceptionID + ".txt";
		return ContentLoader.loadFileContent(filePath);
	}*/

	/*@Deprecated
	public String getContextCode() {
		// get context code
		String filePath = StaticData.CE_Data_Home + "/ccontext/"
				+ this.exceptionID + ".txt";
		return ContentLoader.loadFileContent(filePath);
	}*/

	public String getContextDescription() {
		// get context representation as a string
		SCQueryMaker maker = new SCQueryMaker("", stackTrace, contextCode);
		return maker.getSearchQuery();
	}

}
