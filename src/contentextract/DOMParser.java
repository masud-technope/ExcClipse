package contentextract;

import java.util.ArrayList;
import java.util.HashMap;
import metrics.DensityCalculator;
import metrics.RelevanceCalculator;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import querysuggest.QueryManager;

public class DOMParser {
	// HTML document information
	Document document;
	Element body;
	double bodyTextDensity = 0;
	double bodyLinkDensity = 0;
	ArrayList<String> deleteTags;

	// context specific information
	int exceptionID = 0;
	String contextDescription;
	String stackTrace;
	String codeContext;

	// node scores
	public HashMap<Integer, Double> nodeMap;
	static int nodeID = 0;

	/*@Deprecated
	public DOMParser(Document document, int caseID) {
		// document specific initialization
		this.document = document;
		this.body = this.document.body();
		this.collectBodyStats();
		this.deleteTags = new ArrayList<>();
		this.populateDeleteTags();

		// context specific initialization
		this.exceptionID = caseID;
		QueryManager manager = new QueryManager(this.exceptionID);
		this.stackTrace = manager.getStackTrace();
		this.codeContext = manager.getContextCode();
		this.contextDescription = manager.getContextDescription();

		// node score storage
		this.nodeMap = new HashMap<>();

	}*/

	public DOMParser(Document document, String strace, String ccontext) {
		// document specific initialization
		this.document = document;
		this.body = this.document.body();
		this.collectBodyStats();
		this.deleteTags = new ArrayList<>();
		this.populateDeleteTags();

		// context specific initialization
		QueryManager manager = new QueryManager(strace, ccontext);
		this.stackTrace = strace;
		this.codeContext = ccontext;
		this.contextDescription = manager.getContextDescription();

		// node score storage
		this.nodeMap = new HashMap<>();
		this.document = cleanDeleteTags(this.document);
	}

	protected Document cleanDeleteTags(Document doc) {
		// clean the deleted tags at first
		Elements elements = doc.select("script,style,noscript");
		for (Element elem : elements) {
			elem.remove();
		}
		return doc;
	}

	protected void collectBodyStats() {
		// collecting body statistics
		String bodyText = this.body.text();
		int tagNumber = this.body.getAllElements().size();
		this.bodyTextDensity = bodyText.length() / tagNumber;
		Elements links = this.body.select("a,input,button,select");
		int linkTextLength = 0;
		for (Element link : links) {
			linkTextLength += link.text().length();
		}
		this.bodyLinkDensity = linkTextLength / tagNumber;
	}

	protected void populateDeleteTags() {
		// code for populating the noise tags
		this.deleteTags.add("script");
		this.deleteTags.add("style");
		this.deleteTags.add("noscript");
	}

	protected Element parseDocument() {
		Element parentNode = this.body;
		Elements bodyBlocks = this.body.children();
		for (Element element : bodyBlocks) {
			if (!this.deleteTags.contains(element.tagName())) {
				element = parseDOMNode(element);
				element = recordNodeStats(element);
			}
		}
		// System.out.println(bodyBlocks);
		// also record the body statistics
		parentNode = recordNodeStats(parentNode);
		// System.out.println(parentNode);
		return parentNode;
	}

	protected Element parseDOMNode(Element element) {
		// parsing the DOM node
		Elements elements = element.children();
		for (Element elem : elements) {
			if (!this.deleteTags.contains(elem.tagName())) {
				elem = parseDOMNode(elem);
			}
		}
		// recording node statistics
		if (!this.deleteTags.contains(element.tagName()))
			element = recordNodeStats(element);
		return element;
	}

	protected Element recordNodeStats(Element elem) {
		// code for recording node statistics
		DensityCalculator calculator = new DensityCalculator(elem,
				contextDescription, this.bodyTextDensity, this.bodyLinkDensity);
		Element elem1 = calculator.getDensities();

		// storing node scores
		nodeID++;
		double tDensity = Double.parseDouble(elem1.attr("tdensity"));
		double lDensity = Double.parseDouble(elem1.attr("ldensity"));
		double cDensity = Double.parseDouble(elem1.attr("cdensity"));
		double contDensity = Double.parseDouble(elem1.attr("contdensity"));
		// System.out.println(nodeID+" "+tDensity+" "+lDensity+" "+cDensity+" "+contDensity);
		// this.nodeMap.put(nodeID, contentDensity);

		RelevanceCalculator relcalc = new RelevanceCalculator(elem1,
				contextDescription, stackTrace, codeContext);
		return relcalc.getRelevance();
	}

	@Deprecated
	protected void showDOM(Element pageBody) {
		// code for showing the DOM
		System.out.println(pageBody);
	}
}
