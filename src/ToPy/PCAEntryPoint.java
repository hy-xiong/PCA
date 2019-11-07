package ToPy;

import java.lang.Integer;//Import this package to let PVM use it to create Integer Objects in JVM
import py4j.GatewayServer;
import flanagan.analysis.PCA;

/**Class for PCA analysis
 * Primary function is designed as static class for PVM in Python, 
 * but py4j cannot use static class in JVM.
 * So it is not implemented as a static class, values are copied to JVM objects from Python side
 * @author finix*/
public class PCAEntryPoint {
	public static void main(String[] args){
		PCAEntryPoint PCAEP = new PCAEntryPoint();
		GatewayServer gatewayServer = new GatewayServer(PCAEP);
		gatewayServer.start();
		System.out.println("Gateway Server Started");
		
		/*//Test Data
		String p = "D:\\Dropbox\\Haoyi Vulnerability\\Simulation\\Output\\FeatureDataInfo\\Attribute_Table.txt";
		File f = new File(p);
		try{
			BufferedReader br= new BufferedReader(new FileReader(f));
			br.readLine();
			br.readLine();
			String line = "";
			ArrayList<double[]> input = new ArrayList<double[]>();
			while((line = br.readLine()) != null){
				String[] lineSplit = line.split("\\s");
				double[] temp = new double[lineSplit.length - 1];
				for(int i = 1; i < lineSplit.length; i++)
					temp[i-1] = Double.parseDouble(lineSplit[i]);
				input.add(temp);
			}
			br.close();
			
			double[][] inData = new double[input.size()][];
			for(int i = 0; i < input.size(); i++)
				inData[i] = input.get(i);
			int numFields = inData[0].length;
			double[][] CorM = new double[numFields][numFields];
			double[][] FP = new double[numFields][numFields];
			double[] ComM = new double[numFields];
			int[] nExtracted = new int[1];
			PCAEP.OriginalSolution(inData, CorM, FP, ComM, nExtracted);
			double[][] RFP = new double[nExtracted[0]][numFields];
			double[] RComM = new double[numFields];
			PCAEP.RotatedSolution(inData, nExtracted, RFP, RComM);
		}
		catch(Exception e){
			e.printStackTrace();
		}
*/	}

	public void OriginalSolution(double[][] inData, double[][] PyCorM, double[] PyKMO, double[] PyChiSquareBarlett, int[] PyDofBarlett,
			double[] PyEigenValues, double[] PyPropPct, double[] PyCumPropPct, double[][] PyEigenVectors , double[][] PyFP, 
			double[] PyComM, int[] nExtracted){
		/*Input data into PCA package*/
		PCA pca = new PCA();
		pca.enterScoresAsRowPerPerson(inData);
		pca.useCorrelationMatrix();
		
		/*Correlation Matrix*/
		double[][] temp_CorM = pca.rawCorrelationCoefficients();
		temp_CorM = this.Remove_row_col_from_2d_array(temp_CorM, temp_CorM.length - 1, temp_CorM[0].length - 1);
		this.ReportArray(temp_CorM, "Correlation Matrix");
		this.CloneData(temp_CorM, PyCorM);
		
		/*KMO and Barlett Test of Sphericity*/
		double temp_overallKMO = pca.overallKMO();
		double temp_chiSquareBarlett = pca.chiSquareBartlett();
		int temp_dofBarlett = pca.dofBartlett();
		System.out.println("Overall KMO: " + temp_overallKMO);
		System.out.println("Chi-Square Barlett Test of Sphericity: " + temp_chiSquareBarlett);
		System.out.println("Degrees of freedom of Barlett Test: " + temp_dofBarlett + "\n");
		PyKMO[0] = temp_overallKMO;
		PyChiSquareBarlett[0] = temp_chiSquareBarlett;
		PyDofBarlett[0] = temp_dofBarlett;
		
		/*Eigenvalue & Eigenvectors*/
		double[] temp_eigenValue = pca.orderedEigenValues();
		double[][] temp_eigenVector = pca.orderedEigenVectorsAsRows();
		double[] temp_proportionPct = pca.proportionPercentage();
		double[] temp_cumu_proportionPct = pca.cumulativePercentage();
		this.ReportArray(temp_eigenValue, "EigenValues");
		this.ReportArray(temp_eigenVector, "Eigenvectors");
		this.ReportArray(temp_proportionPct, "Proportion Percentage Explained");
		this.ReportArray(temp_cumu_proportionPct, "Cumulative Proportion Percentage Explained");
		this.CloneData(temp_eigenValue, PyEigenValues);
		this.CloneData(temp_eigenVector, PyEigenVectors);
		this.CloneData(temp_proportionPct, PyPropPct);
		this.CloneData(temp_cumu_proportionPct, PyCumPropPct);
		
		
		/*Original Factor Pattern*/
		double[][] temp_FP = pca.loadingFactorsAsRows();
		this.ReportArray(temp_FP, "Original Factor Pattern");
		this.CloneData(temp_FP, PyFP);
		
		/*# of extracted factors*/
		nExtracted[0] = pca.nEigenOneOrGreater();
		System.out.println("Number of Extracted Factors: " + nExtracted[0] + "\n");
		
		/*Communality of extracted original factors*/
		double[] temp_ComM = new double[temp_FP[0].length];
		for(int i = 0; i < nExtracted[0]; i++){
			for(int k = 0; k < temp_FP[i].length; k++){
				temp_ComM[k] += Math.pow(temp_FP[i][k], 2.0);
			}
		}
		this.ReportArray(temp_ComM, "Communality of Original Extracted Factors");
		this.CloneData(temp_ComM, PyComM);
	}
	
	public void RotatedSolution(double[][] inData, int[] nExtracted, double[][] PyRFP, double[] PyRComM){
		/*Input data into PCA package*/
		PCA pca = new PCA();
		pca.enterScoresAsRowPerPerson(inData);
		pca.useCorrelationMatrix();
		
		/*Rotated Factor Pattern, Kaiser Criterion*/
		pca.varimaxRotation(nExtracted[0]);
		double[][] temp_RFP =  pca.rotatedLoadingFactorsAsRows(); 
		this.ReportArray(temp_RFP, "Rotated Factor Pattern");
		this.CloneData(temp_RFP, PyRFP);
		
		/*Communality of extracted rotated factors*/
		double[] temp_ComM = new double[temp_RFP[0].length];
		for(int i = 0; i < nExtracted[0]; i++){
			for(int k = 0; k < temp_RFP[i].length; k++){
				temp_ComM[k] += Math.pow(temp_RFP[i][k], 2.0);
			}
		}
		this.ReportArray(temp_ComM, "Communality of Extracted Rotated Factors");
		this.CloneData(temp_ComM, PyRComM);
	}
	
	/**Copy data from a 2d array to another
	 * @param inData 2d input array
	 * @param outData 2d output array*/
	private void CloneData(double[][] inData, double[][] outData){
		for(int i = 0; i < inData.length; i++){
			for(int k = 0; k < inData[0].length; k++){
				outData[i][k] = inData[i][k];
			}
		}
	}
	
	/**Copy data from a 1d array to another
	 * @param inData 1d input array
	 * @param outData 1d output array*/
	private void CloneData(double[] inData, double[] outData){
		for(int i = 0; i < inData.length; i++)
			outData[i] = inData[i];
	}
	
	/**Print a 1d array in the console
	 * @param d_arr 1d array*/
	private void ReportArray(double[] d_arr, String title){
		String line = "";
		if(d_arr == null){
			throw new IllegalArgumentException("Null Input Array");
		}
		else{
			for(int i = 0; i < d_arr.length; i++){
					line = line + d_arr[i] + "\t";
			}
			line = line + "\n";
			System.out.println(title + ":\n" + line);
		}
	}
	
	/**Print a 1d array in the console
	 * @param d_arr 1d array*/
	private void ReportArray(int[] i_arr, String title){
		String line = "";
		if(i_arr == null){
			throw new IllegalArgumentException("Null Input Array");
		}
		else{
			for(int i = 0; i < i_arr.length; i++){
					line = line + i_arr[i] + "\t";
			}
			line = line + "\n";
			System.out.println(title + ":\n" + line);
		}
	}
	
	/**Print a 2d array in the console
	 * @param d_arr 2d array*/
	private void ReportArray(double[][] d_arr, String title){
		String line = "";
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
			System.out.println(title + ":\n" + line);
		}
	}
	
	/**Remove a specific row or col from a 2d array
	 * @param d_arr 2d array
	 * @param row the row number which need to be removed, -1 means no remove
	 * @param col the col number which need to be removed, -1 means no remove*/
	private double[][] Remove_row_col_from_2d_array(double[][] d_arr, int row, int col){
		double[][] d_arr_new;
		//Remove both a row and a col
		if((row != -1) && (col != -1)){
			d_arr_new = new double [d_arr.length - 1][d_arr[0].length - 1];
			for(int i = 0; i < d_arr_new.length; i++){
				if(i < row){
					for(int j = 0; j < d_arr_new[i].length; j++){
						if (j < col){
							d_arr_new[i][j] = d_arr[i][j];
						}else{
							d_arr_new[i][j] = d_arr[i][j + 1];
						}
					}
				}else{
					for(int j = 0; j < d_arr_new[i].length; j++){
						if (j < col){
							d_arr_new[i][j] = d_arr[i + 1][j];
						}else {
							d_arr_new[i][j] = d_arr[i + 1][j + 1];
						}
					}
				}
			}
		}
		//Remove a row
		else if ((row != -1) && (col == -1)){
			d_arr_new = new double [d_arr.length - 1][d_arr[0].length];
			for(int i = 0; i < d_arr_new.length; i++){
				if(i < row)
					d_arr_new[i] = d_arr[i];
				else
					d_arr_new[i] = d_arr[i+1];
			}
		}
		//Remove a col
		else if ((row == -1) && (col != -1)){
			d_arr_new = new double [d_arr.length][d_arr[0].length - 1];
			for(int i = 0; i < d_arr_new.length; i++){
				for(int j = 0; j < d_arr_new[i].length; j++){
					if(j < col)
						d_arr_new[i][j] = d_arr[i][j];
					else
						d_arr_new[i][j] = d_arr[i][j+1];
				}
			}
		}
		//Do nothing
		else 
			return d_arr;
		return d_arr_new;
	}
}
