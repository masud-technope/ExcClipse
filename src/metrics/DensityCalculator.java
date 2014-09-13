package metrics;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import similarity.CosineSimilarityMeasure;

public class DensityCalculator {

	Element element;
	String contextDesc;
	double bodyTextDensity;
	double bodyLinkDensity;
	final double e = 2.71828;

	public DensityCalculator(Element element, String contextDesc,
			double bodyTextDensity, double bodyLinkDensity) {
		this.element = element;
		this.contextDesc = contextDesc;
		this.bodyTextDensity = bodyTextDensity;
		this.bodyLinkDensity = bodyLinkDensity;
	}

	public Element getDensities() {
		// code for getting densities
		double textDensity = getTextDensity(this.element);
		double linkDensity = getLinkDensity(this.element);
		double codeDensity = getCodeDensity(this.element);
		// now update the element attributes
		this.element.attr("tdensity", String.format("%.2f", textDensity));
		this.element.attr("ldensity", String.format("%.2f", linkDensity));
		this.element.attr("cdensity", String.format("%.2f", codeDensity));

		// content density
		double contentDensity = getContentDensity(this.element);
		this.element.attr("contdensity", String.format("%.4f", contentDensity));

		return this.element;
	}

	protected double getTextDensity(Element element) {
		double textDensity = 0;
		int textCharNumber = element.text().length();
		int tagNumber = element.getAllElements().size() - 1; // only the inner
																// nodes
		tagNumber = tagNumber - getNonClosedItemCount(element);
		if (tagNumber == 0) {
			textDensity = textCharNumber;
		} else {
			textDensity = (double) textCharNumber / tagNumber;
		}
		return textDensity;
	}

	boolean checkRelevantLink(String linkText) {
		// check if the link is relevant or noise
		boolean isNoise = true;
		CosineSimilarityMeasure cosMeasure = new CosineSimilarityMeasure(
				contextDesc, linkText);
		double similarity = cosMeasure.get_cosine_similarity_score(true);
		if (similarity > .75) { // if it is 75% similar
			isNoise = false;
		}
		return isNoise;
	}

	protected double getLinkDensity(Element element) {
		double linkDensity = 0;
		Elements linkTags = element.select("a,input,select,button");
		int tagNumber = element.getAllElements().size() - 1;// only the inner
															// nodes
		tagNumber = tagNumber - getNonClosedItemCount(element);
		int linkCharNumber = 0;
		for (Element elem : linkTags) {
			try {
				String innerText = elem.text();
				if (innerText.isEmpty()) {
					innerText = elem.attr("value");
				}
				// check if it a relevant one or not
				boolean isNoise = checkRelevantLink(innerText);
				if (isNoise) {
					linkCharNumber += innerText.length();
				}
			} catch (Exception e) {
				// no need to handle
			}
		}
		if (tagNumber == 0) {
			linkDensity = linkCharNumber;
		} else {
			linkDensity = (double) linkCharNumber / tagNumber;
		}
		return linkDensity;
	}

	protected int getNonClosedItemCount(Element elem) {
		// get non-closed items count
		Elements elems = elem.select("br,hr,img,tr,tbody");
		return elems.size();
	}

	protected double getCodeDensity(Element element) {
		double codeDensity = 0;
		Elements codeTags = element.select("code,pre,blockquote");
		int tagNumber = element.getAllElements().size() - 1; // only the inner
																// nodes
		tagNumber = tagNumber - getNonClosedItemCount(element);
		int codeCharNumber = 0;
		for (Element elem : codeTags) {
			try {
				String codeText = elem.text();
				codeCharNumber += codeText.length();
			} catch (Exception exc) {
				// do not handle it
			}
		}
		if (tagNumber == 0) {
			codeDensity = codeCharNumber;
		} else {
			codeDensity = (double) codeCharNumber / tagNumber;
		}
		return codeDensity;
	}

	protected double getContentDensity(Element element) {
		// calculating code density
		double contentDensity = 0;
		double textDensity = Double.parseDouble(element.attr("tdensity"));
		if (textDensity == 0)
			textDensity = 1;
		double linkDensity = Double.parseDouble(element.attr("ldensity"));
		if (linkDensity == 0)
			linkDensity = 1;
		double codeDensity = Double.parseDouble(element.attr("cdensity"));
		// if(codeDensity==0)codeDensity=1;

		double firstPart = textDensity + codeDensity / textDensity;
		double nonLinkDensity = textDensity > linkDensity ? textDensity
				- linkDensity : 1;

		double logbase = getLogBase(textDensity, linkDensity, nonLinkDensity);
		double logPart = Math.log(textDensity / linkDensity + codeDensity
				/ textDensity);
		logPart = logPart / Math.log(logbase);

		contentDensity = firstPart * logPart;
		if (contentDensity < 0)
			contentDensity = 0;
		return contentDensity;
	}

	protected double getLogBase(double textDensity, double linkDensity,
			double nonLinkDensity) {
		// code for getting the log base
		if (nonLinkDensity == 0)
			nonLinkDensity = 1;
		if (bodyTextDensity == 0)
			bodyTextDensity = 1;
		double logbase = Math.log((textDensity * linkDensity / nonLinkDensity)
				+ (bodyLinkDensity * textDensity / bodyTextDensity) + e);
		return logbase;
	}

}
