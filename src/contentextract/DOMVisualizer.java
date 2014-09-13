package contentextract;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class DOMVisualizer {

	Element body;

	public DOMVisualizer(Element body) {
		this.body = body;
	}

	protected Element visualizeElements() {
		// visualize different elements
		Elements children = this.body.children();
		int maxID = 0;
		double maxScore = 0;
		int minID = 0;
		double minScore = 1.0;
		for (int i = 0; i < children.size(); i++) {
			Element elem = children.get(i);
			double relScore = 0;
			double totalScore = 0;
			String relScoreStr = elem.attr("contrel").trim();
			String totalScoreStr = elem.attr("contscore").trim();

			if (!totalScoreStr.isEmpty()) {
				relScore = Double.parseDouble(relScoreStr);
				totalScore = Double.parseDouble(totalScoreStr);
				if (totalScore > maxScore && relScore > 0) {
					maxScore = totalScore;
					maxID = i;
				}
				if (totalScore < minScore && relScore > 0) {
					minScore = totalScore;
					minID = i;
				}
			}
		}
		// now assign the relevance classes.
		for (int i = 0; i < children.size(); i++) {
			Element elem = children.get(i);
			if (i == maxID) {
				elem.addClass("rel1");
			} else if (i == minID) {
				elem.addClass("rel3");
			} else {
				elem.addClass("rel2");
			}
		}
		// returning the visualized body
		return this.body;
	}

}
