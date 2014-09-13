package contentextract;

public class ContentManager {

	String stacktarce;
	String codecontext;
	String pageContent;

	public ContentManager() {
		// default
		this.pageContent = new String();
	}

	public ContentManager(String pageContent) {
		this.pageContent = pageContent;
		ContextProvider cxtProvider = new ContextProvider();
		this.stacktarce = cxtProvider.getCurrentStackTrace();
		this.codecontext = cxtProvider.getCurrentCodeContext();
	}

	public String getMainContent() {
		// collecting main page content
		DOMManager domManager = new DOMManager(pageContent, this.stacktarce,
				this.codecontext);
		String maincontent = domManager.getMainContent();
		return maincontent;
	}

	public String getSuggestedContent() {
		// collecting the suggested page part
		RelevantContManager relevantManager = new RelevantContManager(
				pageContent, this.stacktarce, this.codecontext);
		String relevantContent = relevantManager.getRelevantContent();
		return relevantContent;
	}

}
