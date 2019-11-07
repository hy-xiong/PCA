package Toolkits;

import Sim.FactorPattern;
import flanagan.analysis.PCA;

/**Class for PCA analysis
 * @author finix*/
public class PCAAnalysis {
	/**Census feature object*/
	private CensusFeature _CF;
	/**An integer 2-d array containing indices of each census polygon for each cluster region*/
	private int[][] _sampleSets;
	/**An arrary of variables used for PCA analysis*/
	private String[] _vars;
	/**An factor pattern object storing all the outputs from this PCA analysis*/
	private FactorPattern _FP;
	
	/**Constructor
	 * @param CF Input census feature object
	 * @param sampleSet Input sample sets
	 * @param vars Input variables for PCA*/
	public PCAAnalysis(CensusFeature CF, int[][] sampleSets, String[] vars){
		_CF = CF;
		_sampleSets = sampleSets;
		_vars = vars;
		_FP = new FactorPattern(); //will be revised after get the regionalization results
	}
	
	/**Test Constructor for the functionality of this PCA class
	 * @param inputData 2-d data used for testing PCA functionality*/
	public PCAAnalysis(){
		String path =System.getProperty("user.dir") + "\\Data\\PCATestData2.txt";
		/*Run PCA*/
		PCA pca = new PCA();
		pca.readScoresAsRowPerPersonA(path);
		pca.useCorrelationMatrix();
		
		/*Gen correlation matrix, Use this function in the following function*/
		double[][] CM = pca.rawCorrelationCoefficients(); //Parenthesis in the correlation matrix mean the original variable index
		CM = this.Remove_row_col_from_2d_array(CM, CM.length - 1, CM[0].length - 1);
		this.Report_2d_array_values(CM);
		
		/*Gen factor pattern*/
		double[][] FP = pca.loadingFactorsAsRows();
		this.Report_2d_array_values(FP);
		
		/*Compute Communality*/
		double[] communalityMatrix = new double[FP[0].length];
		int nExtracted = pca.nEigenOneOrGreater();
		for(int i = 0; i < nExtracted; i++){
			for(int k = 0; k < FP[i].length; k++){
				communalityMatrix[k] += Math.pow(FP[i][k], 2.0);
			}
		}
		this.Report_1d_array_values(communalityMatrix);
		
		/*Perform varimax rotation*/
		pca.varimaxRotation(nExtracted); //Kaiser Criterion
		double[][] RFP = pca.rotatedLoadingFactorsAsRows(); 
		this.Report_2d_array_values(RFP); 
	}
	
	/**Generate correlation matrix based on the inputs*/
	public void GenCorrelationMatrix(){
		int size = _vars.length;
		double[][] CM = new double[size][size];
		//undone
		_FP.SetCorrelationMatrix(CM);
	}
	
	/**Generate factor pattern based on the inputs*/
	public void GenVarimaxRotatedFactorPattern(){
		int rows = _vars.length;
		double[][] FP = new double[rows][];
		//undone
		_FP.SetFactorPattern(FP);
	}
	
	/**Get the factor pattern produced by the PCA analysis
	 * @return a factor pattern object*/
	public FactorPattern GetFactorPattern(){
		return _FP;
	}
	
	/**Print a 1d array in the console
	 * @param d_arr 2d array*/
	private void Report_1d_array_values(double[] d_arr){
		String line = "\n";
		if(d_arr == null){
			throw new IllegalArgumentException("Null Input Array");
		}
		else{
			for(int i = 0; i < d_arr.length; i++){
					line = line + d_arr[i] + "\t";
			}
			line = line + "\n";
			System.out.println(line);
		}
	}
	
	/**Print a 2d array in the console
	 * @param d_arr 2d array*/
	private void Report_2d_array_values(double[][] d_arr){
		String line = "\n";
		if(d_arr == null){
			throw new IllegalArgumentException("Null Input Array");
		}
		else{
			for(int i = 0; i < d_arr.length; i++){
				for(int j = 0; j < d_arr[i].length; j++){
					line = line + d_arr[i][j] + "\t";
				}
				line = line + "\n";
			}
			System.out.println(line);
		}
	}
	
	/**Remove a specific row or col from a 2d array
	 * @param d_arr 2d array
	 * @param row the row number which need to be removed, -1 means no remove
	 * @param col the col number which need to be removed, -1 means no remove*/
	private double[][] Remove_row_col_from_2d_array(double[][] d_arr, int row, int col){
		double[][] d_arr_new = new double [d_arr.length - 1][d_arr[0].length - 1];
		for(int i = 0; i < d_arr_new.length; i++){
			if(i == row){
				for(int j = 0; j < d_arr_new[i].length; j++){
					if (j == col){
						d_arr_new[i][j] = d_arr[i+1][j + 1];
					}else{
						d_arr_new[i][j] = d_arr[i+1][j];
					}
				}
			}else{
				for(int j = 0; j < d_arr_new[i].length; j++){
					if (j == col){
						d_arr_new[i][j] = d_arr[i][j + 1];
					}else{
						d_arr_new[i][j] = d_arr[i][j];
					}
				}
			}
		}
		return d_arr_new;
	}
}
