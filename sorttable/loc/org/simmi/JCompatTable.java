package org.simmi;

import javax.swing.DropMode;
import javax.swing.JTable;

public class JCompatTable extends JTable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public int convertRowIndexToModel( int viewRowIndex ) {
		return super.convertRowIndexToModel(viewRowIndex);
	}
	
	public int convertRowIndexToView( int modelRowIndex ) {
		return super.convertRowIndexToView( modelRowIndex );
	}

	public void setAutoCreateRowSorter(boolean b) {
		super.setAutoCreateRowSorter( b );
	}
	
	public void setDropMode2( DropMode mode ) {
		super.setDropMode( mode );
	}
}
