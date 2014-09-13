package contentextract;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import core.MetricWeights;

public class RelevanceFilter {

	Element body;
	ArrayList<String> deleteTags;
	ArrayList<Integer> tobeDeleted;

	public RelevanceFilter(Element body) {
		// initialization
		this.body = body;
		this.deleteTags = new ArrayList<>();
		this.tobeDeleted = new ArrayList<>();
		this.populateDeleteTags();
		this.collectContentSocreThreshold();
	}

	protected void populateDeleteTags() {
		// code for populating the noise tags
		this.deleteTags.add("script");
		this.deleteTags.add("style");
		this.deleteTags.add("noscript");
	}

	protected void collectContentSocreThreshold() {
		double contentscore = Double.parseDouble(this.body.attr("contscore"));
		MetricWeights.CONTENT_SCORE_THRESHOLD = contentscore;
		System.out.println("Content score threshold:"
				+ MetricWeights.CONTENT_SCORE_THRESHOLD);
	}

	protected Element filterDOMTree() {
		// filter DOM tree for noise
		Elements children = this.body.children();
		System.out.println("Total children:" + children.size());
		for (int i = 0; i < children.size(); i++) {
			Element child = children.get(i);
			// System.out.println("Tag:"+child.tagName());
			if (this.deleteTags.contains(child.tagName())) {
				this.tobeDeleted.add(i);
				continue;
			} else {
				try {
					double contscore = Double.parseDouble(child
							.attr("contscore"));
					// double
					// contscore=Double.parseDouble(child.attr("scoresum"));
					if (contscore >= MetricWeights.CONTENT_SCORE_THRESHOLD) {
						child = discoverSubTree(child);
						child = cascadeMarkContent(child);
						child = cleanSubTree(child);
					} else {
						if(children.size()>1){
							this.tobeDeleted.add(i);
						}
					}
				} catch (Exception exc) {
					this.tobeDeleted.add(i);
				}
			}
		}
		// showMetrics(body);
		// now delete the noise children
		Element modifiedBody = deleteChildren(this.body);
		// now show the score statistics of the children
		return modifiedBody;
	}

	public Element provideRelevantSection() {
		// extracting the most relevant content
		Element modifiedBody = filterDOMTree();
		double maxScore = 0;
		int maxChildIndex = 0;
		Elements children = modifiedBody.children();
		System.out.println("Children survived:" + children.size());

		if (children.size() == 1)
			return modifiedBody;

		for (int i = 0; i < children.size(); i++) {
			Element elem = children.get(i);
			try {
				double score = Double.parseDouble(elem.attr("contscore"));
				double relevance = Double.parseDouble(elem.attr("contrel"));

				if (score > maxScore && relevance > 0) {
					maxScore = score;
					maxChildIndex = i;
				}
			} catch (Exception e) {
				// do I need to handle?
			}
		}
		if (maxScore == 0)
			maxChildIndex = 0;

		this.tobeDeleted.clear();
		for (int i = 0; i < children.size(); i++) {
			if (i != maxChildIndex) {
				this.tobeDeleted.add(i);
			}
		}
		Element relevantBody = deleteChildren(modifiedBody);
		return relevantBody;
	}

	protected Element discoverSubTree(Element subTreeRoot) {
		// discover the tree and mark as content
		try {
			String scoreStr = subTreeRoot.attr("contscore");
			// String scoreStr = subTreeRoot.attr("scoresum");
			Double score = Double.parseDouble(scoreStr);
			if (score >= MetricWeights.CONTENT_SCORE_THRESHOLD) {
				Element maxElem = getMaximumScoredElement(subTreeRoot);
				// Element maxElem = getMaximumScoreSumElement(subTreeRoot);
				maxElem = markAsContent(maxElem);
				for (Element elem : subTreeRoot.children()) {
					elem = discoverSubTree(elem);
				}
			} else {
				// do not discover, leave it
			}
		} catch (Exception e2) {
			// handle the exception
		}
		return subTreeRoot;
	}

	protected Element getMaximumScoredElement(Element root) {
		double maxScore = 0;

		Elements elements = root.getAllElements();
		Element maxScoreElem = elements.first();
		for (Element elem : elements) {
			String scoreStr = elem.attr("contscore").trim();
			if (scoreStr.isEmpty())
				continue;
			double score = Double.parseDouble(scoreStr);
			if (score > maxScore) {
				maxScore = score;
				maxScoreElem = elem;
			}
		}
		return maxScoreElem;
	}

	protected Element getMaximumScoreSumElement(Element root) {
		double maxScore = 0;

		Elements elements = root.getAllElements();
		Element maxScoreElem = elements.first();
		for (Element elem : elements) {
			String scoreStr = elem.attr("scoresum").trim();
			if (scoreStr.isEmpty())
				continue;
			double score = Double.parseDouble(scoreStr);
			if (score > maxScore) {
				maxScore = score;
				maxScoreElem = elem;
			}
		}
		return maxScoreElem;
	}

	protected Element markAsContent(Element elem) {
		// mark as content
		elem.attr("iscontent", "1");
		return elem;
	}

	protected Element markAsNoise(Element elem) {
		// mark as noise
		elem.attr("iscontent", "0");
		return elem;
	}

	protected Element cascadeMarkContent(Element elem) {
		// cascading the content mark
		Elements elements = elem.children();
		for (Element child : elements) {
			cascadeMarkContent(child);
		}
		try {
			int iscontent = Integer.parseInt(elem.attr("iscontent"));
			if (iscontent == 1) {
				markAsContent(elem.parent());
				for (Element elem2 : elem.children()) {
					elem2 = markAsContent(elem2);
				}
			}
		} catch (Exception e3) {
			// handle it
		}
		return elem;
	}

	protected Element cleanSubTree(Element subTree) {
		// code for cleaning the sub tree
		Elements elems = subTree.children();
		List<Node> childNodes = subTree.childNodes();

		ArrayList<Element> temp = new ArrayList<>(elems);
		ArrayList<Node> tempNodes = new ArrayList<>(childNodes);

		for (Element elem : temp) {
			cleanSubTree(elem);
			// now deal with leaf node
			String iscontentStr = elem.attr("iscontent").trim();
			if (iscontentStr.isEmpty()) {
				elems.remove(elem);
				// delete the corresponding child node
				if (tempNodes.contains((Node) elem)) {
					tempNodes.remove((Node) elem);
				}
			} else {
				// they are content node
			}
		}
		if (tempNodes.size() > 0) {
			String modifiedcontent = new String();
			for (Node node : tempNodes) {
				modifiedcontent += node.toString();
			}
			subTree.html(modifiedcontent);
		} else {
			subTree.html(" ");
		}
		return subTree;
	}

	protected Element deleteChildren(Element bodyElem) {
		// deleting the noise blocks
		Elements children = bodyElem.children();
		System.out.println("Delete them:" + this.tobeDeleted);
		ArrayList<Element> temp = new ArrayList<>(children);
		try {
			for (Integer index : this.tobeDeleted) {
				Element elem = temp.get(index);
				children.remove(elem);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		bodyElem.html(children.toString());
		return bodyElem;
	}

}
