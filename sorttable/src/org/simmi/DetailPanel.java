package org.simmi;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class DetailPanel extends JScrollPane {
	JTable 			detailTable;
	TableModel		detailModel;
	List<Boolean>	showColumn;
	
	public DetailPanel( final String lang, final TableModel model, final TableModel topModel, final JTable leftTable ) {
		showColumn = new ArrayList<Boolean>();
		for( int i = 0; i < model.getColumnCount(); i++ ) {
			showColumn.add( Boolean.TRUE );
		}
		
		detailTable = new JTable();
		
		JPopupMenu popup = new JPopupMenu();
		Action action = new AbstractAction("Sýna Alla") {
			@Override
			public void actionPerformed(ActionEvent e) {
				for( int i : detailTable.getSelectedRows() ) {
					showColumn.set(i, Boolean.TRUE );
				}
				detailTable.revalidate();
				detailTable.repaint();
			}
		};
		//if( lang.equals("EN") ) action.
		popup.add( action );
		action = new AbstractAction("Fela Alla") {
			@Override
			public void actionPerformed(ActionEvent e) {
				for( int i : detailTable.getSelectedRows() ) {
					showColumn.set(i, Boolean.FALSE );
				}
				detailTable.revalidate();
				detailTable.repaint();
			}
		};
		popup.add( action );
		action = new AbstractAction("Viðsnúa Vali") {
			@Override
			public void actionPerformed(ActionEvent e) {
				int[] rows = detailTable.getSelectedRows();
				detailTable.selectAll();
				for( int r : rows ) {
					detailTable.removeRowSelectionInterval(r, r);
				}
			}
		};
		popup.add( action );
		detailTable.setComponentPopupMenu( popup );
		
		detailTable.setAutoCreateRowSorter( true );
		detailModel = new TableModel() {
	
			@Override
			public void addTableModelListener(TableModelListener l) {
				// TODO Auto-generated method stub
				
			}
	
			@Override
			public Class<?> getColumnClass(int columnIndex) {
				if( columnIndex == 2 ) return Float.class;
				else if( columnIndex == 3 ) return Boolean.class;
				return String.class;
			}
	
			@Override
			public int getColumnCount() {
				return 4;
			}
	
			@Override
			public String getColumnName(int columnIndex) {
				if( lang.equals("IS") ) {
					if( columnIndex == 0 ) return "Nafn";
					else if( columnIndex == 1 ) return "Eining";
					else if( columnIndex == 2 ) return "Mæligildi";
					return "Sýna dálk";
				} else {
					if( columnIndex == 0 ) return "Name";
					else if( columnIndex == 1 ) return "Unit";
					else if( columnIndex == 2 ) return "Measure";
					return "Show Column";
				}
			}
	
			@Override
			public int getRowCount() {
				if( model != null ) return model.getColumnCount();
				return 0;
			}
	
			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				if( model != null ) {
					if( columnIndex == 0 ) return model.getColumnName( rowIndex );
					else if( columnIndex == 1 ) return topModel.getValueAt( 1, rowIndex );
					else if( columnIndex == 2 ) {
						Object val = model.getValueAt( leftTable.getSelectedRow(), rowIndex );
						if( val instanceof Float ) {
							return val;
						}
						return null;
					} else {
						return showColumn.get( rowIndex );
					}
				}
				return 0;
			}
	
			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				if( columnIndex == 3 ) return true;
				return false;
			}
	
			@Override
			public void removeTableModelListener(TableModelListener l) {
				// TODO Auto-generated method stub
				
			}
	
			@Override
			public void setValueAt(Object value, int rowIndex, int columnIndex) {
				if( columnIndex == 3 ) showColumn.set( rowIndex, (Boolean)value );
			}
			
		};
		detailTable.setModel( detailModel );
		this.setViewportView( detailTable );
	}
}
