package org.simmi;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.RowSorterListener;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import org.simmi.RecipePanel.Recipe;
import org.simmi.RecipePanel.RecipeIngredient;

public class DetailPanel extends JScrollPane {
	JTable 			detailTable;
	TableModel		detailModel;
	List<Boolean>	showColumn;
	TableModel		nullModel;
	
	public int countVisible() {
		int count = 0;
		for( boolean b : showColumn ) {
			if( b ) count++;
		}
		return count;
	}
	
	public int convertIndex( int c ) {
		int count = 0;
		int i = 0;
		for( int k = 0; k < showColumn.size(); k++ ) {
			int ind = detailTable.convertRowIndexToModel(k);
			if( showColumn.get(ind) ) count++;
			if( count == c+1 ) return detailTable.convertRowIndexToModel(i);
			
			i++;
		}
		
		return -1;
	}
	
	public void updateModels( final JTable table, final JTable topTable ) {
		TableModel oldTop = topTable.getModel();
		TableModel old = table.getModel();
		
		topTable.setModel( nullModel );
		table.setModel( nullModel );
		
		topTable.setModel( oldTop );
		table.setModel( old );
	}
	
	public DetailPanel( final String lang, final JTable table, final JTable topTable, final JTable leftTable, final List<Object[]> stuff, final List<String> ngroupList, final Map<String,Integer> foodInd, final List<Recipe> recipes ) {
		showColumn = new ArrayList<Boolean>();
		for( int i = 0; i < stuff.get(0).length-2; i++ ) {
			showColumn.add( Boolean.TRUE );
		}
		
		nullModel = new TableModel() {
			@Override
			public void addTableModelListener(TableModelListener l) {}

			@Override
			public Class<?> getColumnClass(int columnIndex) {
				return null;
			}

			@Override
			public int getColumnCount() {
				return 0;
			}

			@Override
			public String getColumnName(int columnIndex) {
				return null;
			}

			@Override
			public int getRowCount() {
				return 0;
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				return null;
			}

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return false;
			}

			@Override
			public void removeTableModelListener(TableModelListener l) {}

			@Override
			public void setValueAt(Object value, int rowIndex, int columnIndex) {}
		};
		
		detailTable = new JTable() {
			@Override
			public void sorterChanged( RowSorterEvent e ) {
				super.sorterChanged(e);
			}
		};
		detailTable.getDefaultEditor( Boolean.class ).addCellEditorListener( new CellEditorListener() {

			@Override
			public void editingCanceled(ChangeEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void editingStopped(ChangeEvent e) {
				updateModels( table, topTable );
			}
			
		});
		
		JPopupMenu popup = new JPopupMenu();
		Action action = new AbstractAction("Sýna Alla") {
			@Override
			public void actionPerformed(ActionEvent e) {
				for( int i : detailTable.getSelectedRows() ) {
					int k = detailTable.convertRowIndexToModel(i);
					showColumn.set(k, Boolean.TRUE );
				}
				detailTable.revalidate();
				detailTable.repaint();
				
				updateModels( table, topTable );
			}
		};
		//if( lang.equals("EN") ) action.
		popup.add( action );
		action = new AbstractAction("Fela Alla") {
			@Override
			public void actionPerformed(ActionEvent e) {
				for( int i : detailTable.getSelectedRows() ) {
					int k = detailTable.convertRowIndexToModel(i);
					showColumn.set(k, Boolean.FALSE );
				}
				detailTable.revalidate();
				detailTable.repaint();
				
				updateModels( table, topTable );
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
				return showColumn.size();
			}
	
			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				if( columnIndex == 0 ) return ngroupList.get( rowIndex );
				else if( columnIndex == 1 ) {
					Object[]	obj = stuff.get(1);
					return obj[ rowIndex+2 ];
					//return topModel.getValueAt( 1, rowIndex );
				}
				else if( columnIndex == 2 ) {
					int rsel = leftTable.getSelectedRow();
					if( rsel >= 0 && rsel < leftTable.getRowCount() ) {
						int lsel = leftTable.convertRowIndexToModel( rsel );
						
						if( lsel < stuff.size()-2 ) {
							Object[]		obj = stuff.get(lsel+2);
							Object val = 	obj[ rowIndex+2 ];
							
							//Object val = model.getValueAt( lsel, rowIndex );
							if( val instanceof Float ) {
								return val;
							}
						} else {
							float ret = 0.0f;
							int i = lsel - (stuff.size()-2);
							Recipe rep = recipes.get(i);
							float tot = 0.0f;
							for( RecipeIngredient rip : rep.ingredients ) {
								if( foodInd.containsKey(rip.stuff) ) {
									int k = foodInd.get( rip.stuff );
									Object[] 	obj = stuff.get(k+2);
									Object		val = obj[ rowIndex+2 ];
									if( val != null && val instanceof Float ) {
										float d = rip.measure;
										if( rip.unit.equals("mg") ) d /= 1000.0f;
										tot += d;
										
										float f = (((Float)val) * d) / 100.0f;
										ret += f;
									}
								}
							}
							
							if( ret != 0.0f ) return (ret * 100.0f) / tot;
								
							return null;
						}
					}
				} else {
					return showColumn.get( rowIndex );
				}
				return null;
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
		detailTable.getRowSorter().addRowSorterListener( new RowSorterListener() {
			@Override
			public void sorterChanged(RowSorterEvent e) {
				updateModels( table, topTable );
			}
		});
		this.setViewportView( detailTable );
	}
}
