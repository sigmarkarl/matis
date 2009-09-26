package org.simmi;

import javax.swing.DropMode;
import javax.swing.RowSorter;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableModel;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.Filter;
import org.jdesktop.swingx.decorator.FilterPipeline;
import org.jdesktop.swingx.decorator.PatternFilter;
import org.jdesktop.swingx.decorator.FilterPipeline.IdentityFilter;

public class JCompatTable extends JXTable {
	
	MySorter	sorter;
	MyFilter	filter;
	
	TableModel	model;
	
	public JCompatTable( TableModel model ) {
		super( model );		
		sorter = new MySorter( model );
		if( MyFilter.pfilt == null ) {
			MyFilter.pfilt = new PatternFilter();
		}
	}
	
	public JCompatTable() {
		super();
		if( MyFilter.pfilt == null ) {
			MyFilter.pfilt = new PatternFilter();
		}
		
		//this.getColumnExt(1).
	}
	
	public void setRowSelectionInterval(int r1, int r2) {
		if( r1 >= 0 && r2 < this.getRowCount() ) super.setRowSelectionInterval(r1, r2);
	}
	
	public void tableChanged( TableModelEvent e ) {
		super.tableChanged( e );
	}
	
	public void setModel( TableModel model ) {
		super.setModel( model );
		this.sorter = new MySorter( model );
		this.model = model;
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
		return sorter;
	}

	public void setDropMode2(DropMode insertRows) {
	}

	public void updateFilter() {
		//System.err.println("subbi");
		filter.updateFilter();
	}
	
	public void setFilters( FilterPipeline fp ) {
		super.setFilters( fp );
	}
	
	public void setFilter( final MyFilter filter ) {
		if( !filter.isAssigned() ) {
			this.filter = filter;
			this.setFilters( filter );
		} else {
			IdentityFilter ifilt = new IdentityFilter() {
				public int getSize() {
					return filter.getOutputSize();
				}
				
				public int convertRowIndexToModel( int row ) {
					return filter.convertRowIndexToModel( row );
				}
				
				public int convertRowIndexToView( int row ) {
					return filter.convertRowIndexToView( row );
				}
			};
			Filter[] fa = new Filter[] { ifilt };
			MyFilter fp = new MyFilter( fa );
			this.filter = fp;
			this.setFilters( fp );
		}
	}
}
