package org.simmi;

import java.util.HashSet;
import java.util.Set;

import javax.swing.RowFilter;
import javax.swing.table.TableModel;

public class MyFilter extends RowFilter<TableModel,Integer> {
	String			filterText;
	int				fInd = 0;
	Set<String> 	cropped = new HashSet<String>();
	TableModel		leftModel;
	
	public MyFilter( TableModel leftModel ) {
		super();
		
		this.leftModel = leftModel;
	}
	
	public boolean include( javax.swing.RowFilter.Entry<? extends TableModel, ? extends Integer> entry ) {
		// String filterText = field.getText();
		String gval = (String)leftModel.getValueAt(entry.getIdentifier(), 0);
		String val = fInd == 0 ? gval : (String) leftModel.getValueAt(entry.getIdentifier(), 1);
		if (filterText != null) {
			if (val != null)
				return val.toString().matches(filterText);
			return false;
		} else {
			boolean b = cropped.contains(gval);
			if (b)
				return false;
		}
		return true;
	}
}
