package org.simmi;

import javax.swing.DropMode;
import javax.swing.RowSorter;
import javax.swing.event.RowSorterEvent;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.jdesktop.swingx.JXTable;

public class JCompatTable extends JXTable {
	RowSorter	rowSorter;
	
	public JCompatTable( TableModel model ) {
		super( model );
		
		 rowSorter = new TableRowSorter( model );
	}
	
	public JCompatTable() {
		super();
	}
	
	public void setModel( TableModel model ) {
		super.setModel( model );
		rowSorter = new TableRowSorter( model );
	}
	
	/**
	 * 
	 *
	private static final long serialVersionUID = 1L;

	public int convertRowIndexToModel( int viewRowIndex ) {
		return viewRowIndex;
	}
	
	public int convertRowIndexToView( int modelRowIndex ) {
		return modelRowIndex;
	}*/

	public void sorterChanged(RowSorterEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void setRowSorter(MySorter leftTableSorter) {
		// TODO Auto-generated method stub
		
	}
	
	public void setAutoCreateRowSorter(boolean b) {
		
	}

	public RowSorter getRowSorter() {
		return rowSorter;
	}

	public void setDropMode2(DropMode insertRows) {	
	}
}
