package org.simmi;

import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public class MySorter extends TableRowSorter<TableModel> {
	public MySorter( TableModel model ) {
		super( model );
	}
	
	public int convertRowIndexToModelSuper(int index) {
		return index;//super.convertRowIndexToModel( index );
	}

	public int convertRowIndexToViewSuper(int index) {
		return index;//super.convertRowIndexToView( index );
	}

	public void setRowFilter(MyFilter filter) {
		//super.setRowFilter( filter );
	}

	public int getViewRowCount() {
		// TODO Auto-generated method stub
		return 0;
	}
}
