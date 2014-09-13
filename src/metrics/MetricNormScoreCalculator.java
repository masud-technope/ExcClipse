package metrics;

import java.util.ArrayList;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import core.MetricWeights;

public class MetricNormScoreCalculator {

	double maxContentDensity = 1;
	double maxContentRelevance = 1;
	Element body;
	boolean normalize = false;

	public MetricNormScoreCalculator(Element body, boolean normalizeScore) {
		// initialization
		this.body = body;
		if (normalizeScore) {
			this.collectMaximumValues();
			this.normalize = normalizeScore;
		}
	}

	protected void collectMaximumValues() {
		// collecting the max values
		Elements elems = this.body.getAllElements();
		for (Element elem : elems) {
			try {
				double contentDensity = Double.parseDouble(elem
						.attr("contdensity"));
				double contRelevance = Double.parseDouble(elem.attr("contrel"));
				if (contentDensity > maxContentDensity) {
					this.maxContentDensity = contentDensity;
				}
				if (contRelevance > maxContentRelevance) {
					this.maxContentRelevance = contRelevance;
				}
			} catch (Exception e) {
				// not sure if I need to handle
			}
		}
	}

	public Element normalizeAndCalcScoreSubTree(Element elem) {
		Elements elements = elem.children();
		ArrayList<Element> temp = new ArrayList<>(elements);
		for (Element elem2 : elements) {
			normalizeAndCalcScoreSubTree(elem2);
		}
		try {
			double contDensity = Double.parseDouble(elem.attr("contdensity"));
			double contRelevance = Double.parseDouble(elem.attr("contrel"));
			contDensity = contDensity / maxContentDensity;
			contRelevance = contRelevance / maxContentRelevance;
			elem.attr("contdensity", String.format("%.4f", contDensity));
			elem.attr("contrel", String.format("%.4f", contRelevance));
			double contentScore = contDensity
					* MetricWeights.CONTENT_DENSITY_WEIGHT + contRelevance
					* MetricWeights.CONTENT_RELEVANCE_WEIGHT;
			elem.attr("contscore", String.format("%.4f", contentScore));
		} catch (Exception e) {
			// no need to handle for now
		}

		if (temp.size() > 0) {
			elem.html(elements.toString());
		}

		return elem;
	}

	public Element normalizeMetricAndCalculateScore() {
		Elements elements = this.body.getAllElements();
		for (Element elem : elements) {
			try {
				double contDensity = Double.parseDouble(elem
						.attr("contdensity"));
				double contRelevance = Double.parseDouble(elem.attr("contrel"));
				contDensity = contDensity / maxContentDensity;
				contRelevance = contRelevance / maxContentRelevance;
				elem.attr("contdensity", String.format("%.4f", contDensity));
				elem.attr("contrel", String.format("%.4f", contRelevance));
				double contentScore = contDensity
						* MetricWeights.CONTENT_DENSITY_WEIGHT + contRelevance
						* MetricWeights.CONTENT_RELEVANCE_WEIGHT;
				elem.attr("contscore", String.format("%.4f", contentScore));

			} catch (Exception e) {
				// no need to handle
			}
		}
		return this.body;
	}

	public Element getElementScoreSum(Element body) {
		// getting element score sum
		for (Element child : body.children()) {
			child = getScoreSum(child);
		}
		return body;
	}

	protected Element getScoreSum(Element elem) {
		// getting element score sum
		Elements children = elem.children();
		for (Element child : children) {
			child = getElementScoreSum(child);
		}
		double sum = 0;
		for (Element elem2 : elem.children()) {
			try {
				double score = Double.parseDouble(elem2.attr("contscore"));
				sum += score;
			} catch (Exception e) {
				// handle the exception
			}
			elem.attr("scoresum", String.format("%.2f", sum));
		}
		return elem;
	}

}
