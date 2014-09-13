package htmlview;

import java.io.BufferedReader;
import java.io.Reader;
import java.io.StringReader;
import net.barenca.jastyle.ASFormatter;
import net.barenca.jastyle.FormatterHelper;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import utility.ContentLoader;

public class CustomizePageContent {
	public static Document customizerContent(Document doc,
			Element modifiedBody, boolean addlegend) {
		// changing the body
		if (addlegend)
			modifiedBody = addLegend(modifiedBody);
		doc.body().html(modifiedBody.html());
		doc = customizeCodes(doc);
		doc.head().html(getStyles());
		doc.head()
				.children()
				.first()
				.before("<script src=\"https://google-code-prettify.googlecode.com/svn/loader/run_prettify.js\"></script>");
		return doc;
	}

	protected static Element addLegend(Element body) {
		// add the legend
		String legend = "<table align=\"center\" width=\"100%\"><tr>"
				+ "<td class=\"rel1\"><strong>Most relevant section</strong></td>"
				+ "<td class=\"rel2\"><strong>Relevant section</strong></td>"
				+ "<td class=\"rel3\"><strong>Less relevant section</strong></td>"
				+ "</tr></table>";
		body.children().first().before(legend);
		return body;
	}

	protected static String getStyles() {
		String styleTag = "<style type=\"text/css\">";
		String cssURL = "http://www.usask.ca/~mor543/contentsuggest/style.css";
		// String content=ContentLoader.loadFileContent("style.css");
		String content = ContentLoader.downloadPageContent(cssURL);
		styleTag += content;
		styleTag += "</style>";
		return styleTag;
	}

	protected static Document customizeCodes(Document doc) {
		// customize the code elements
		Element body = doc.body();
		body = addCodeElementClasses(body);
		return doc;
	}

	protected static Element addCodeElementClasses(Element body) {
		// adding classes to code elements
		Elements elems = body.select("pre,code,textarea,blockquote");
		for (Element elem : elems) {
			elem.addClass("prettyprint");
			String[] lines = elem.text().split("\n");
			if(lines.length==1){
				String text=elem.text();
				if(text.contains("at ")){
					lines=text.split("at ");
				}
			}
			String newStr = new String();
			for (String line : lines) {
				newStr += line + "<br>";
			}
			elem.html(newStr);
		}
		return body;
	}
}
