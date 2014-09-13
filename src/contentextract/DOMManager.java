package contentextract;

import htmlview.CustomizePageContent;
import htmlview.HTMLPageViewer;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;
import metrics.MetricNormScoreCalculator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import utility.ContentLoader;
//import core.StaticData;

public class DOMManager {

	String htmlFileName;
	int exceptionID;
	int fileID;

	String pageContent;
	String strace;
	String ccontext;

	static HashMap<Integer, Integer> exceptionPointer = new HashMap<>();

	@Deprecated
	public DOMManager(String fileName, int exceptionID, int fileID) {
		this.htmlFileName = fileName;
		this.exceptionID = exceptionID;
		this.fileID = fileID;
	}

	public DOMManager(String pageContent, String strace, String ccontext) {
		this.pageContent = pageContent;
		this.strace = strace;
		this.ccontext = ccontext;
	}

	@Deprecated
	protected void extractMainContent() {
//		String content = ContentLoader.loadFileContent(htmlFileName);
//		Document document = Jsoup.parse(content);
//		DOMParser parser = new DOMParser(document, exceptionID);
//		Element body = parser.parseDocument();
//
//		// normalization
//		MetricNormScoreCalculator normcalc = new MetricNormScoreCalculator(
//				body, false);
//		Element finalBody = normcalc.normalizeMetricAndCalculateScore();
//		// Element finalBodySum=normcalc.getElementScoreSum(finalBody);
//
//		// now filter the HTML body
//		// DOMFilter filterer=new DOMFilter(finalBodySum,true);
//
//		DOMFilter filterer = new DOMFilter(finalBody);
//		Element refinedBody = filterer.filterDOMTree();

		// System.out.println(refinedBody);

		// showPerformance(refinedBody.text());
		// System.out.println(refinedBody);
		// System.out.println("Node recorded:"+parser.nodeMap.keySet().size());
		// Document modifiedDoc=CustomizePageContent.customizerContent(document,
		// refinedBody);
		// new HTMLPageViewer(modifiedDoc.toString()).view();
		//saveMainContent(refinedBody.text());
		// saveMainContentHTML(refinedBody.toString());
		// saveMainContentHTML(finalBody.toString());
	}

	protected String getMainContent() {
		// extracting the main content
		Document document = Jsoup.parse(pageContent);
		DOMParser parser = new DOMParser(document, strace, ccontext);
		Element body = parser.parseDocument();

		// normalization
		MetricNormScoreCalculator normcalc = new MetricNormScoreCalculator(
				body, false);
		Element finalBody = normcalc.normalizeMetricAndCalculateScore();
		// Element finalBodySum=normcalc.getElementScoreSum(finalBody);

		// now filter the HTML body
		// DOMFilter filterer=new DOMFilter(finalBodySum,true);

		DOMFilter filterer = new DOMFilter(finalBody);
		Element refinedBody = filterer.filterDOMTree();

		DOMVisualizer visualizer = new DOMVisualizer(refinedBody);
		Element visualBody = visualizer.visualizeElements();

		Document modifiedDoc = CustomizePageContent.customizerContent(document,
				visualBody, true);
		return modifiedDoc.toString();

	}

	/*protected void showPerformance(String extracted) {
		String goldFile = StaticData.CE_Data_Home
				+ "/source/coll500/main-html/9.html";
		try {
			Document doc1 = Jsoup.parse(new File(goldFile), "UTF-8");
			// PerformanceCalc calc=new PerformanceCalc(doc1.text(), extracted);
			// calc.calculatePerformance();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/

	/*protected void saveMainContent(String content) {
		String textFileName = new String();// this.htmlFileName.split("\\.")[0]
											// + ".txt";
		textFileName = StaticData.CE_Data_Home + "/source/coll500/main-text/"
				+ fileID + ".txt";
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

	/*protected void saveMainContentHTML(String html) {
		// saving modified HTML doc
		String textFileName = new String();// this.htmlFileName.split("\\.")[0]
											// + ".txt";
		textFileName = StaticData.CE_Data_Home + "/source/coll500/main-html/"
				+ fileID + ".html";
		// save the content
		try {
			FileWriter fwriter = new FileWriter(new File(textFileName));
			fwriter.write(html);
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
		long start = System.currentTimeMillis();
		getIndexPointer();
		for (int i = 1; i <= 500; i++) {
			int fileID = i;
			int exceptionID = exceptionPointer.get(fileID).intValue();
			String fileName = StaticData.CE_Data_Home + "/source/coll500/org/"
					+ fileID + ".html";
			DOMManager manager = new DOMManager(fileName, exceptionID, fileID);
			manager.extractMainContent();
			long end = System.currentTimeMillis();
			System.out.println("Completed:" + i);
			// System.out.println("Time required:"+(end-start)/1000);
		}
	}*/
}
