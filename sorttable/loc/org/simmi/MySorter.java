package org.simmi;

import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public class MySorter extends TableRowSorter<TableModel> {
	public MySorter( TableModel model ) {
		super( model );
	}
	
	public int convertRowIndexToModelSuper(int index) {
		return super.convertRowIndexToModel( index );
	}

	public int convertRowIndexToViewSuper(int index) {
		return super.convertRowIndexToView( index );
	}

	public void setRowFilter(MyFilter filter) {
		super.setRowFilter( filter );
	}
}
