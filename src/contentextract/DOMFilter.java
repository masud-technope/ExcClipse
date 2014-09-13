package contentextract;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import core.MetricWeights;
//import core.StaticData;

public class DOMFilter {

	Element body;
	ArrayList<String> deleteTags;
	ArrayList<Integer> tobeDeleted;

	public DOMFilter(Element body) {
		// initialization
		this.body = body;
		this.deleteTags = new ArrayList<>();
		this.tobeDeleted = new ArrayList<>();
		this.populateDeleteTags();
		collectContentSocreThreshold();
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

	public Element filterDOMTree() {
		// filter DOM tree for noise
		Elements children = this.body.children();
		for (int i = 0; i < children.size(); i++) {
			Element child = children.get(i);
			// System.out.println("Tag:"+child.tagName());
			if (this.deleteTags.contains(child.tagName())) {
				this.tobeDeleted.add(i);
				continue;
			} else {
				try {
					double contscore = 0;
					contscore = Double.parseDouble(child.attr("contscore"));
					// else
					// contscore=Double.parseDouble(child.attr("scoresum"));
					if (contscore >= MetricWeights.CONTENT_SCORE_THRESHOLD) {
						child = discoverSubTree(child);
						child = cascadeMarkContent(child);
						child = cleanSubTree(child);
					} else {
						if (children.size() > 1)
							this.tobeDeleted.add(i);
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

	/*protected void showMetrics(Element body) {
		// showing metric values
		String wfileName = StaticData.CE_Data_Home + "/weight/metrics4.txt";
		try {
			FileWriter fwriter = new FileWriter(new File(wfileName), true);
			Elements children = body.children();
			for (int i = 0; i < children.size(); i++) {
				Element child = children.get(i);
				// if(!this.tobeDeleted.contains(i)){
				// System.out.println(child.attr("trel") + " "
				// + child.attr("crel") + " " + 1);
				try {
					double trel = Double.parseDouble(child.attr("contdensity"));
					double crel = Double.parseDouble(child.attr("contrel"));
					if (trel > 0 || crel > 0) {
						fwriter.write(trel + "\t" + crel + "\t"
								+ (this.tobeDeleted.contains(i) ? 0 : 1) + "\n");
					}
				} catch (Exception e) {
				}
				// }
			}
			fwriter.close();
		} catch (Exception e) {

		}
	}*/

	protected Element discoverSubTree(Element subTreeRoot) {
		// discover the tree and mark as content
		try {
			String scoreStr = new String();
			// if(!useScoreSum)
			scoreStr = subTreeRoot.attr("contscore");
			// else scoreStr = subTreeRoot.attr("scoresum");
			Double score = Double.parseDouble(scoreStr);
			if (score >= MetricWeights.CONTENT_SCORE_THRESHOLD) {
				Element maxElem = null;
				// if(useScoreSum)
				maxElem = getMaximumScoreSumElement(subTreeRoot);
				// else maxElem = getMaximumScoredElement(subTreeRoot);

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

	protected Element cleanSubTreeNodes(Element subTree) {
		// cleaning sub tree nodes
		List<Node> childNodes = subTree.childNodes();
		ArrayList<Node> tempNodes = new ArrayList<>(childNodes);
		Elements elems = subTree.children();
		for (Element elem : elems) {
			cleanSubTreeNodes(elem);
			String iscontentStr = elem.attr("iscontent").trim();
			if (iscontentStr.isEmpty()) {
				tempNodes.remove(elem);
			} else {
				// they are content node
			}
		}
		if (childNodes.size() > 0) {
			String modifiedcontent = new String();
			for (Node node : childNodes) {
				modifiedcontent += node.toString();
			}
			subTree.html(modifiedcontent);
		} else {
			subTree.html(" ");
		}
		return subTree;
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
