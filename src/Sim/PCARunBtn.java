package Sim;

import java.awt.EventQueue;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.esri.arcgis.addins.desktop.Button;
import com.esri.arcgis.arcmapui.IMxDocument;
import com.esri.arcgis.carto.IFeatureLayer;
import com.esri.arcgis.carto.ILayer;
import com.esri.arcgis.carto.IMap;
import com.esri.arcgis.framework.IApplication;
import com.esri.arcgis.geodatabase.IFeatureClass;
import com.esri.arcgis.geodatabase.IField;
import com.esri.arcgis.geodatabase.IFields;
import com.esri.arcgis.interop.AutomationException;

public class PCARunBtn extends Button{
	
	IMxDocument iMxd;
	IApplication iApp;
	
	/*This initializes the button and gets a reference to the hosting ArcGIS application*/
	public void init(IApplication app){
		this.iApp = app;
	}
	
	@Override
	public void onClick(){
		try {
			/*Collect features and their correpsonding fields from current .mxd*/
			iMxd = (IMxDocument) iApp.getDocument();
			ArrayList<IFeatureClass> iFeatures = this.getFeatureClassFromLayers(iMxd);
			/*Open an UI and transfer parameters to this UI*/
			UIParameter ui = new UIParameter(iFeatures);
			ui.setVisible(true);
			/*Retrieve analysis parameters from dialog*/
			Object[] parameters = ui.getReturnValues();
			IFeatureClass feature = (IFeatureClass) parameters[0];
			int[] fieldsIndices = (int[]) parameters[1];
			//Test
			String s = "";
			for(int i : fieldsIndices) {s += i + ", ";}
			System.out.println(feature.getAliasName() + ": " + s);
		} catch (Exception e) {e.printStackTrace();}
	}
	
	
	/**Retrieve all layers in a map frame from a .mxd document*/
	private ArrayList<IFeatureClass> getFeatureClassFromLayers(IMxDocument mxd) throws Exception{
		IMap map = mxd.getFocusMap();
		ArrayList<IFeatureClass> features = new ArrayList<IFeatureClass>();
		ILayer layer;
		for(int i = 0; i < map.getLayerCount(); i++){
			layer = map.getLayer(i);
			if(layer instanceof IFeatureLayer){
				IFeatureLayer featurelayer = (IFeatureLayer) layer;
				features.add(featurelayer.getFeatureClass());
			}
		}
		return features;
	}
}
