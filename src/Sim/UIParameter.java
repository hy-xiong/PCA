package Sim;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import com.esri.arcgis.geodatabase.IFeatureClass;
import com.esri.arcgis.geodatabase.IField;
import com.esri.arcgis.geodatabase.IFields;
import com.esri.arcgis.interop.AutomationException;

public class UIParameter extends JDialog implements ActionListener, ItemListener{

	private final JPanel contentPane = new JPanel();
	private final JComboBox layerComboBox;
	private final JList attributesList;
	private final JList selectedAttributesList;
	
	private ArrayList<IFeatureClass> _features;
	private HashMap<String, IFeatureClass> _nameToFeatureMap;
	//Map each field's name to its index in a feature
	private String[] _fieldsNameInOneFeature; 
	private Object[] _returnValues;

	/**
	 * Create the dialog.
	 */
	public UIParameter(ArrayList<IFeatureClass> features) throws AutomationException, IOException {
		/*Get input parameters*/
		_features = features;
		
		/*Set UI*/
		this.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		setBounds(100, 100, 433, 389);
		getContentPane().setLayout(new BorderLayout());
		contentPane.setLayout(null);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		/*OK and Cancel default for a dialog*/
		getContentPane().add(contentPane, BorderLayout.CENTER);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okBtn = new JButton("OK");
				okBtn.setActionCommand("OK");
				buttonPane.add(okBtn);
				getRootPane().setDefaultButton(okBtn);
				okBtn.addActionListener(this);
			}
			{
				JButton cancelBtn = new JButton("Cancel");
				cancelBtn.setActionCommand("Cancel");
				buttonPane.add(cancelBtn);
				cancelBtn.addActionListener(this);
			}
		}
		
		/*JLabels*/
		JLabel layerLbl = new JLabel("Layer");
		layerLbl.setBounds(74, 14, 35, 14);
		contentPane.add(layerLbl);
		
		JLabel attributesLbl = new JLabel("Attributes");
		attributesLbl.setBounds(66, 59, 62, 14);
		contentPane.add(attributesLbl);
		
		JLabel selectedAttributesLbl = new JLabel("Selected Attributes");
		selectedAttributesLbl.setBounds(274, 59, 106, 14);
		contentPane.add(selectedAttributesLbl);
		
		
		/*JComboBox for layer selection*/
		layerComboBox = new JComboBox();
		layerComboBox.setBounds(119, 11, 236, 20);
		contentPane.add(layerComboBox);
		/*Add each layer's name to JComboBox and a hashMap to connect to corresponding feature class*/
		_nameToFeatureMap = new HashMap<String, IFeatureClass>();
		for(IFeatureClass feature : features){
			String feature_name = feature.getAliasName();
			_nameToFeatureMap.put(feature_name, feature);
			layerComboBox.addItem(feature_name);
		}
		
		
		/*JButtons for analysis*/
		JButton addBtn = new JButton(">");
		addBtn.setBounds(188, 116, 41, 23);
		contentPane.add(addBtn);
		addBtn.setActionCommand("Select");
		addBtn.addActionListener(this);
		
		JButton removeBtn = new JButton("<");
		removeBtn.setBounds(188, 173, 41, 23);
		contentPane.add(removeBtn);
		removeBtn.setActionCommand("Remove");
		removeBtn.addActionListener(this);
		
		JButton clearBtn = new JButton("Clear");
		clearBtn.setBounds(318, 283, 89, 23);
		contentPane.add(clearBtn);
		clearBtn.setActionCommand("Clear Selection");
		clearBtn.addActionListener(this);
		
		
		/*JList with scroll panel for candidate attributes*/
		JPanel panel = new JPanel();
		panel.setBounds(10, 84, 168, 186);
		contentPane.add(panel);
		panel.setLayout(new BorderLayout());
		//Add "final" to bypass the non-final local variable used in anonymous innner class
		attributesList = new JList();
		JScrollPane scrollPane1 = new JScrollPane();
		panel.add(scrollPane1);
		scrollPane1.setViewportView(attributesList);
		
		
		/*JList with scroll panel for selected attributes*/
		JPanel panel2 = new JPanel();
		panel2.setBounds(239, 84, 168, 184);
		contentPane.add(panel2);
		panel2.setLayout(new BorderLayout());
		selectedAttributesList = new JList();
		JScrollPane scrollPane2 = new JScrollPane();
		panel2.add(scrollPane2);
		scrollPane2.setViewportView(selectedAttributesList);
	}

	@Override
	public void itemStateChanged(ItemEvent e){
		if(e.getStateChange() == ItemEvent.SELECTED){
			try {
				DefaultListModel listModel = new DefaultListModel();
				IFeatureClass feature = _nameToFeatureMap.get((String)layerComboBox.getSelectedItem());
				IFields fields = feature.getFields();
				int fieldsSize = fields.getFieldCount();
				_fieldsNameInOneFeature = new String[fieldsSize]; 
				for(int i = 0; i < fieldsSize; i++){
					String fieldName = fields.getField(i).getName();
					_fieldsNameInOneFeature[i] = fieldName;
					listModel.addElement(fieldName);
				}
				attributesList.setModel(listModel);
				if(selectedAttributesList.getModel().getSize() > 0){
					DefaultListModel tempListModel = (DefaultListModel) selectedAttributesList.getModel();
					tempListModel.removeAllElements();
				}
			} catch (Exception ex) {ex.printStackTrace();}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("OK")){
			if(selectedAttributesList.getModel().getSize() > 0){
				_returnValues = new Object[2];
				DefaultListModel listModel = (DefaultListModel) selectedAttributesList.getModel();
				int size = listModel.getSize();
				//record every selected field's index in its feature class
				int[] fieldsIndices = new int[size]; 
				for(int i = 0; i < size; i++){
					String fieldName = (String) listModel.get(i);
					for(int k = 0; k < _fieldsNameInOneFeature.length; k++){
						if(_fieldsNameInOneFeature[k].equals(fieldName)){
							fieldsIndices[i] = k;
						}
					}
				}
				//Set return value
				_returnValues[0] = _nameToFeatureMap.get((String) layerComboBox.getSelectedItem()); //return selected feature class
				_returnValues[1] = fieldsIndices; //return selected fields' indices in their feature class
			}
			this.setVisible(false);
		}else if(e.getActionCommand().equals("Cancel")){
			this.setVisible(false);
		}else if(e.getActionCommand().equals("Select")){
			this.MoveItems(attributesList, selectedAttributesList, false);
		}else if(e.getActionCommand().equals("Remove")){
			this.MoveItems(selectedAttributesList, attributesList, false);
		}else if(e.getActionCommand().equals("Clear Selection")){
			this.MoveItems(selectedAttributesList, attributesList, true);
		}else{}
	}
	
	/*Methods to move items between lists*/
	private void MoveItems(JList sourceList, JList targetList, boolean moveAll){
		int[] Indexlist;
		if(moveAll == false){
			Indexlist = sourceList.getSelectedIndices();
		}else{
			int size = sourceList.getModel().getSize();
			if(size == 0)
				Indexlist = new int[0];
			else{
				Indexlist = new int[size];
				for(int i = 0; i < size; i++)
					Indexlist[i] = i;
			}
		}
		if(Indexlist.length > 0){
			DefaultListModel listModel_candidate = (DefaultListModel) sourceList.getModel();
			DefaultListModel listModel_selected;
			if(targetList.getModel().getSize() == 0){
				listModel_selected = new DefaultListModel();
				targetList.setModel(listModel_selected);
			} else{
				listModel_selected =(DefaultListModel) targetList.getModel();
			}
			//import items to selected attribute list
			for(int i : Indexlist){listModel_selected.addElement(listModel_candidate.get(i));}
			//remove items from candidate attribute list
			for(int i = 0; i < Indexlist.length; i++){listModel_candidate.remove(Indexlist[i] - i);}
		}
	}
	
	/**Get user selected parameters from this UI
	 * @return user selected layer (object[0] - IFeatureClass) and fields' indices (Object[1] - int[])*/
	public Object[] getReturnValues(){
		return _returnValues;
	}
}
