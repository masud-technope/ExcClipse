package metrics;

import java.util.ArrayList;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import core.MetricWeights;
import similarity.CosineSimilarityMeasure;
import similarity.LCS;
import utility.MyTokenizer;
import utility.RegexMatcher;

public class RelevanceCalculator {
	Element element;
	String contextdesc;
	String strace;
	String ccontext;
	ArrayList<String> codeTags = new ArrayList<>();

	public RelevanceCalculator(Element element, String contextdesc,
			String strace, String ccontext) {
		this.element = element;
		this.contextdesc = contextdesc;
		this.strace = strace;
		this.ccontext = ccontext;
		this.populateCodeTags();
	}

	protected void populateCodeTags() {
		codeTags.add("code");
		codeTags.add("pre");
		codeTags.add("blockquote");
	}

	public Element getRelevance() {
		// get relevance for different elements
		double codeRelAvreage = 0;
		codeRelAvreage = getCodeTagAvgRelevance(element);
		double textRelAverage = 0;
		textRelAverage = getTextTagRelevance(element);
		double contentRelevance = 0;
		contentRelevance = codeRelAvreage * MetricWeights.CODE_RELEVANCE_WEIGHT
				+ textRelAverage * MetricWeights.TEXT_RELEVANCE_WEIGHT;
		element.attr("crel", String.format("%.2f", codeRelAvreage));
		element.attr("trel", String.format("%.2f", textRelAverage));
		element.attr("contrel", String.format("%.4f", contentRelevance));
		return element;
	}

	protected double getCodeTagAvgRelevance(Element element) {
		// code tag relevance
		double total_relevance = 0;
		double avg_relevance = 0;
		Elements codetagElems = element.select("code,pre,blockquote,textarea");
		int tagNumber = codetagElems.size();
		for (Element elem : codetagElems) {
			// System.out.println(elem.text());
			// System.out.println("=================");

			String content = elem.text();
			if (RegexMatcher.matches_stacktrace(content)) {
				total_relevance += getStackTraceMatching(content);
			} else {
				total_relevance += getCodeContextMatching(content);
			}
		}
		if (tagNumber == 0)
			return 0;
		if (tagNumber == 1)
			return total_relevance;
		else {
			avg_relevance = total_relevance / tagNumber;
		}
		return avg_relevance;
	}

	protected double getTextTagRelevance(Element element) {

		double textMatching = 0;
		try {
			textMatching = getTextMatching(element.text());
			if (Double.isNaN(textMatching))
				textMatching = 0;
		} catch (Exception exc) {
			// handle the exception
		}
		return textMatching;
	}

	protected double getStackTraceMatching(String tstrace) {
		// lexical similarity between stack traces
		CosineSimilarityMeasure cosmeasure = new CosineSimilarityMeasure(
				strace, tstrace);
		return cosmeasure.get_cosine_similarity_score(false);
	}

	protected double getCodeContextMatching(String tccontext) {
		// lexical similarity between source code
		MyTokenizer cand_tokenizer = new MyTokenizer(tccontext);
		ArrayList<String> cand_tokens = cand_tokenizer.tokenize_code_item();
		cand_tokenizer = new MyTokenizer(ccontext);
		ArrayList<String> query_tokens = cand_tokenizer.tokenize_code_item();

		LCS lcsmaker = new LCS(query_tokens, cand_tokens);
		ArrayList<String> lcs = lcsmaker.getLCS_Dynamic(query_tokens.size(),
				cand_tokens.size());
		double normalized_matching_score = 0;
		if (lcs.size() == 0)
			return 0;
		else {
			normalized_matching_score = (lcs.size() * 1.0)
					/ query_tokens.size();
		}
		return normalized_matching_score;
	}

	protected double getTextMatching(String textContent) {
		// lexical similarity between texts
		CosineSimilarityMeasure cosmeasure = new CosineSimilarityMeasure(
				contextdesc, textContent);
		return cosmeasure.get_cosine_similarity_score(true);
	}
}
