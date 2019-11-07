package Sim;

/**Class containing a factor pattern derived from one single PCA analysis
 * @author finix*/
public class FactorPattern {
	/**Records for all variables used for this PCA analysis*/
	private String[] _vars;
	/**Records for all census tracts used for this PCA analysis*/
	private int[] _sampleIDs;
	/**Records for total # of factors*/
	private int _factorAmount;
	/**Records for correlation matrix derived during PCA analysis*/
	private double[][] _correlationMatrix;
	/**Records for factor pattern derived from PCA analysis*/
	private double[][] _pattern;
	
	/**Constructor
	 * @param vars Records of input variables for PCA analysis
	 * @param sampleIDs Records of input census tracts for PCA analysis*/
	public FactorPattern(String[] vars, int[] sampleIDs){
		_vars = vars;
		_sampleIDs = sampleIDs;
	}
	
	/**Test Constructor*/
	public FactorPattern(){}
	
	/**Set factor amount
	 * @param num # of factors*/
	public void SetFactorAmount(int num){
		_factorAmount = num;
	}
	/**Get factor amount
	 * @return # of factors*/
	public int GetFactorAmount(){
		return _factorAmount;
	}
	
	/**Set correlation matrix
	 * @param correlationMatrix correlation matrix*/
	public void SetCorrelationMatrix(double[][] correlationMatrix){
		_correlationMatrix = correlationMatrix;
	}
	/**Get correlation matrix
	 * @return correlation matrix*/
	public double[][] GetCorrelationMatrix(){
		return _correlationMatrix;
	}
	
	/**Set factor pattern
	 * @param factorPattern factor pattern matrix*/
	public void SetFactorPattern(double[][] pattern){
		_pattern = pattern;
	}
	/**Get factor pattern
	 * @return factor pattern matrix*/
	public double[][] GetFactorPatten(){
		return _pattern;
	}
}
