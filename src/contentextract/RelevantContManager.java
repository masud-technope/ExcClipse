package contentextract;

import htmlview.CustomizePageContent;
import htmlview.HTMLPageViewer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Scanner;

import metrics.MetricNormScoreCalculator;
import net.barenca.jastyle.ASFormatter;
import net.barenca.jastyle.FormatterHelper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import utility.ContentLoader;
//import core.StaticData;

public class RelevantContManager {

	String htmlFileName;
	int exceptionID;
	int fileID;

	String pageContent;
	String strace;
	String ccontext;
	static HashMap<Integer, Integer> exceptionPointer = new HashMap<>();

	@Deprecated
	public RelevantContManager(String fileName, int exceptionID, int fileID) {
		this.htmlFileName = fileName;
		this.exceptionID = exceptionID;
		this.fileID = fileID;
	}

	public RelevantContManager(String pageContent, String strace,
			String ccontext) {
		this.pageContent = pageContent;
		this.strace = strace;
		this.ccontext = ccontext;
	}

	protected String getRelevantContent() {
		// collecting relevant page part
		Document document = Jsoup.parse(pageContent);
		DOMParser parser = new DOMParser(document, strace, ccontext);
		Element body = parser.parseDocument();

		// normalization
		MetricNormScoreCalculator normcalc = new MetricNormScoreCalculator(
				body, true);
		Element finalBody = normcalc.normalizeMetricAndCalculateScore();
		// Element finalBodySum=normcalc.getElementScoreSum(finalBody);
		/*
		 * for(int i=0;i<finalBody.children().size();i++){
		 * System.out.println(finalBody.children().get(i));
		 * System.out.println("============="); }
		 */

		RelevanceFilter filterer = new RelevanceFilter(finalBody);
		Element refinedBody = filterer.provideRelevantSection();

		Document modifiedDoc = CustomizePageContent.customizerContent(document,
				refinedBody, false);

		return modifiedDoc.toString();
	}

	protected void extractRelevantContent() {
//		String content = ContentLoader.loadFileContent(htmlFileName);
//		Document document = Jsoup.parse(content);
//		DOMParser parser = new DOMParser(document, exceptionID);
//		Element body = parser.parseDocument();
//
//		// normalization
//		MetricNormScoreCalculator normcalc = new MetricNormScoreCalculator(
//				body, true);
//		Element finalBody = normcalc.normalizeMetricAndCalculateScore();
//		// Element finalBodySum=normcalc.getElementScoreSum(finalBody);
//
//		// now filter the HTML body
//		// DOMFilter filterer=new DOMFilter(finalBodySum);
//
//		RelevanceFilter filterer = new RelevanceFilter(finalBody);
//		Element refinedBody = filterer.provideRelevantSection();

		// System.out.println(refinedBody);

		// showPerformance(refinedBody.text());
		// System.out.println(refinedBody);
		// System.out.println("Node recorded:"+parser.nodeMap.keySet().size());
		// Document modifiedDoc=CustomizePageContent.customizerContent(document,
		// refinedBody);
		// new HTMLPageViewer(modifiedDoc.toString()).view();
		//saveMainContent(refinedBody.text());
		// saveMainContentHTML(modifiedDoc.html());
	}

	/*protected void saveMainContent(String content) {
		String textFileName = new String();// this.htmlFileName.split("\\.")[0]
											// + ".txt";
		textFileName = StaticData.CE_Data_Home
				+ "/source/coll500/relevant-text/" + fileID + ".txt";
		// save the content
		try {
			FileWriter fwriter = new FileWriter(new File(textFileName));
			fwriter.write(content);
			fwriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/

	/*protected static void getIndexPointer() {
		// developing exception index
		String indexFile = StaticData.CE_Data_Home + "/source/sourceIndex.txt";
		try {
			Scanner scanner = new Scanner(new File(indexFile));
			while (scanner.hasNext()) {
				String line = scanner.nextLine();
				String[] parts = line.split("\\s+");
				int id = Integer.parseInt(parts[0]);
				int excepID = Integer.parseInt(parts[1]);
				exceptionPointer.put(id, excepID);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/

	/*public static void main(String[] args) {
		String url = "http://www.wowza.com/forums/showthread.php?337-Malformed-URL-exception";
		String pagecontent = ContentLoader.downloadPageContent(url);
		String strace = StaticData.CE_Data_Home + "/test/strace/1.txt";
		String straceStr = ContentLoader.loadFileContent(strace);
		String ccontext = StaticData.CE_Data_Home + "/test/ccontext/1.txt";
		String code = ContentLoader.loadFileContent(ccontext);

		RelevantContManager relManager = new RelevantContManager(pagecontent,
				straceStr, code);
		String content = relManager.getRelevantContent();
		// System.out.println(content);

	}*/
}
