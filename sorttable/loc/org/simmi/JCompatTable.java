package org.simmi;

import java.awt.Rectangle;

import javax.swing.DropMode;
import javax.swing.JTable;
import javax.swing.SortOrder;

public class JCompatTable extends JTable {
	MySorter	sorter;
	MyFilter	filter;
	
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
	
	public void updateFilter() {
		sorter.setRowFilter( filter );
	}
	
	public MyFilter setFilter(MyFilter filter) {
		this.filter = filter;
		return this.filter;
	}
	
	public void setFilter( MyFilter filter, JCompatTable table ) {
		this.filter = filter;
	}
	
	public void setRowSelectionIntervalSuper( int r1, int r2 ) {
		super.setRowSelectionInterval(r1, r2);
	}
	
	public void scrollRectToVisible( Rectangle rect ) {
		super.scrollRectToVisible( rect );
	}
	
	public void setBounds( int x, int y, int w, int h ) {		 
		super.setBounds(x, y, w, h);
	}

	public void setSortOrder(Object object, SortOrder ascending) {
		
	}

	public Object getSortedColumn() {
		return null;
	}
}
