package org.simmi;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.RowSorterListener;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import org.simmi.RecipePanel.Recipe;
import org.simmi.RecipePanel.RecipeIngredient;

public class DetailPanel extends JSplitPane {
	JTable 			detailTable;
	TableModel		detailModel;
	List<Boolean>	showColumn;
	TableModel		nullModel;
	Map<String,String>	groupMap;
	
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
	
	public DetailPanel( final RdsPanel rdsp, final String lang, final ImagePanel imgPanel, final JTable table, final JTable topTable, final JTable leftTable, final List<Object[]> stuff, final List<String> ngroupList, final List<String> ngroupGroups, final Map<String,Integer> foodInd, final List<Recipe> recipes ) {
		super( JSplitPane.VERTICAL_SPLIT );
		this.setOneTouchExpandable( true );
		this.setDividerLocation( 300 );
		
		groupMap = new HashMap<String,String>();
		groupMap.put("1", "Annað");
		groupMap.put("2", "Fituleysanleg vítamín");
		groupMap.put("3", "Vatnsleysanleg vítamín");
		groupMap.put("4", "Steinefni");
		groupMap.put("5", "Málmar");
		groupMap.put("6", "Fitusýrur");
		groupMap.put("7", "Nítröt");
		
		JScrollPane	detailScroll = new JScrollPane();
		
		showColumn = new ArrayList<Boolean>();
		for( int i = 0; i < stuff.get(0).length-2; i++ ) {
			if( !ngroupGroups.get(i).equals("6") ) showColumn.add( Boolean.TRUE );
			else showColumn.add( Boolean.FALSE );
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
				if( columnIndex == 3 ) return Float.class;
				if( columnIndex == 4 ) return Float.class;
				else if( columnIndex == 5 ) return Boolean.class;
				return String.class;
			}
	
			@Override
			public int getColumnCount() {
				return 6;
			}
	
			@Override
			public String getColumnName(int columnIndex) {
				if( lang.equals("IS") ) {
					if( columnIndex == 0 ) return "Næringarefni";
					else if( columnIndex == 1 ) return "Næringarefnaflokkur";
					else if( columnIndex == 2 ) return "Eining";
					else if( columnIndex == 3 ) return "Mæligildi";
					else if( columnIndex == 4 ) return "RDS";
					return "Sýna dálk";
				} else {
					if( columnIndex == 0 ) return "Name";
					else if( columnIndex == 1 ) return "Group";
					else if( columnIndex == 2 ) return "Unit";
					else if( columnIndex == 3 ) return "Measure";
					else if( columnIndex == 4 ) return "Rds";
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
					String ind = ngroupGroups.get( rowIndex );
					if( ind.equals("1") ) {
						String colVal = ngroupList.get( rowIndex );
						if( colVal.equals("Fita, alls") || colVal.equals("Prótein, alls") || colVal.equals("Kolvetni, alls") || colVal.equals("Alkóhól") ) {
							return "Orkuefni";
						}
					}
					
					if( groupMap.containsKey(ind) ) {
						return groupMap.get( ind );
					} else return "Óþekkt";
				} else if( columnIndex == 2 ) {
					Object[]	obj = stuff.get(1);
					return obj[ rowIndex+2 ];
					//return topModel.getValueAt( 1, rowIndex );
				} else if( columnIndex == 3 ) {
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
				} else if( columnIndex == 4 ) {
					String efni = ngroupList.get( rowIndex );
					if( efni.equals("Fita, alls") ) {
						String 	val = rdsp.getRds( "Orka-Venjul/kCal" );
						int		kcal = Integer.parseInt( val );
						double	kj = 4.184*kcal;
						double	d = kj*0.3;
						double	g = d / 37.0;
						return (float)(Math.floor(10.0*g)/10.0);
					} else if( efni.equals("Prótein, alls") ) {
						String 	val = rdsp.getRds( "Orka-Venjul/kCal" );
						int		kcal = Integer.parseInt( val );
						double	kj = 4.184*kcal;
						double	d = kj*0.15;
						double	g = d / 17.0;
						return (float)(Math.floor(10.0*g)/10.0);
					} else if( efni.equals("Kolvetni, alls") ) {
						String 	val = rdsp.getRds( "Orka-Venjul/kCal" );
						int		kcal = Integer.parseInt( val );
						double	kj = 4.184*kcal;
						double	d = kj*0.55;
						double	g = d / 17.0;
						return (float)(Math.floor(10.0*g)/10.0);
					}/*else if( efni.equals("Alkóhól, alls") ) {
						String 	val = rdsp.getRds( "Orka-Venjul/kCal" );
						int		kcal = Integer.parseInt( val );
						double	kj = 4.184*kcal;
						double	d = kj*0.3;
						double	g = d / 37.0;
					}*/
					String[] split = efni.split("[,-]+");
					Object[]	obj = stuff.get(1);
					Object ostr = obj[ rowIndex+2 ];
					String rdsStr = split[0]+"/"+ostr;
					String val = rdsp.getRds( rdsStr );
					if( val != null ) {
						return Float.parseFloat(val);
					}
					return null;
				} else {
					return showColumn.get( rowIndex );
				}
				return null;
			}
	
			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				if( columnIndex == 5 ) return true;
				return false;
			}
	
			@Override
			public void removeTableModelListener(TableModelListener l) {
				// TODO Auto-generated method stub
				
			}
	
			@Override
			public void setValueAt(Object value, int rowIndex, int columnIndex) {
				if( columnIndex == 5 ) showColumn.set( rowIndex, (Boolean)value );
			}
			
		};
		detailTable.setModel( detailModel );
		detailTable.getRowSorter().addRowSorterListener( new RowSorterListener() {
			@Override
			public void sorterChanged(RowSorterEvent e) {
				updateModels( table, topTable );
			}
		});
		detailScroll.setViewportView( detailTable );
		
		for( int rowIndex = 0; rowIndex < detailModel.getRowCount(); rowIndex++ ) {
			String efni = ngroupList.get( rowIndex );
			String[] split = efni.split("[,-]+");
			Object[]	obj = stuff.get(1);
			Object ostr = obj[ rowIndex+2 ];
			String rdsStr = split[0]+"/"+ostr;
			rdsStr = rdsStr.replace(" ", "");
			String val = rdsp.getRds( rdsStr );
			if( val != null ) {
				String ind = ngroupGroups.get( rowIndex );
				if( groupMap.containsKey(ind) ) {
					String grp = groupMap.get( ind );
					rdsp.detailMapping.put(rdsStr, grp);
				} 
			}
		}
		
		this.setTopComponent( imgPanel );
		this.setBottomComponent( detailScroll );
	}
}
