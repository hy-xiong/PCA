package Toolkits;

/**Class for census feature dataset
 * @author finix*/
public class CensusFeature {
	/**Dataset Name*/
	private String _datasetName;
	/**Path directory of the dataset*/
	private String _pathDir;
	
	/**Constructor
	 * @param safeFileName Name of the dataset
	 * @param pathDir Directory of the dataset*/
	public CensusFeature(String safeFileName, String pathDir){
		_datasetName = safeFileName;
		_pathDir = pathDir;
	}
	
	/**Get Attribute Value from the feature dataset
	 * @param attributeName Name of the desired attribute
	 * @param return An array of all the values of this attribute*/
	public String[] GetAttributeValue (String attributeName){
		//Undone
		return null;
	}
}
