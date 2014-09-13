package contentextract;

import java.io.Serializable;

public class DOMElement implements Serializable {

	public int nodeID;
	public String tagName = new String();
	public int textCharCount = 0;
	public int tagCount = 0;
	public int linkCharCount = 0;
	public int linkTagCount = 0;
	public int codeCharCount = 0;
	public int codeTagCount = 0;

	public double textDensity = 0;
	public double linkDensity = 0;
	public double codeDensity = 0;

	public double ContentDensity = 0;

	// public double compositeTextDensity=0;
	// public double textDensitySum=0;
	// public double compositeDensitySum=0;

	public DOMElement parentNode;
	public boolean isContentNode;
	public boolean deleteIt;

	public DOMElement() {
		// node property initialization
		this.isContentNode = false;
		this.deleteIt = false;
	}

}
