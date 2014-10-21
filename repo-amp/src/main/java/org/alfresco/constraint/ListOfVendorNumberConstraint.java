package org.alfresco.constraint;

import java.util.ArrayList;
import java.util.List;
import org.alfresco.repo.dictionary.constraint.ListOfValuesConstraint;

import java.io.Serializable;
import javax.faces.model.SelectItem;

public class ListOfVendorNumberConstraint extends ListOfValuesConstraint implements Serializable {

 
	private static final long serialVersionUID=1;
 
	private List<String> allowedLabels;
 
	public void setAllowedValues(List allowedValues) {}
	public void setCaseSensitive(boolean caseSensitive) {}
 
	public void initialize() {
    	super.setCaseSensitive(false);
    	this.loadDB();
    }
 
	public List<String> getAllowedLabels() {
		return this.allowedLabels;
	}
 
	public void setAllowedLabels(List<String> allowedLabels) {
		this.allowedLabels=allowedLabels;
	}
 
    public List<SelectItem> getSelectItemList() {
		List<SelectItem> result = new ArrayList<SelectItem>(this.getAllowedValues().size());
		for(int i=0;i<this.getAllowedValues().size();i++) {
			result.add(new SelectItem((Object)this.getAllowedValues().get(i),this.allowedLabels.get(i)));
		}
		return result;
	}

    protected void loadDB() {


        List<String> av = new ArrayList<String>();
        List<String> al = new ArrayList<String>();
//This is where you will place your code to connect with the datalist       
	     String query = "PATH:\"app:company_home/st:sites/cm:VendorList/cm:dataLists\"";

//        try {
//		
//         	while (rs.next()) {
//                av.add(rs.getString("ProjectNumber"));
//                al.add(rs.getString("ProjectNumber"));
//            }
//        } catch (Exception e) {
//        }
//
//        super.setAllowedValues(av);
//        this.setAllowedLabels(al);
    }
}