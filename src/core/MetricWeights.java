package core;

public class MetricWeights {
	public static double SIGNIFICANCE_THRESHOLD=0.001;
	public static double DOIWeight=1.00;
	public static double TRWeight=0.90;
	public static double TFWeight=0.50;
	
	//content suggest
	public static double TEXT_RELEVANCE_WEIGHT = 1.0000;
	public static double CODE_RELEVANCE_WEIGHT = 0.5900;// 0.3132;
	public static double CONTENT_DENSITY_WEIGHT = 1.000;// 0.45769;//0.1673;
	public static double CONTENT_RELEVANCE_WEIGHT = 1.0000;
	public static double CONTENT_SCORE_THRESHOLD;
}
