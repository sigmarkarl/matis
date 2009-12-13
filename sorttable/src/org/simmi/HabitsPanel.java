package org.simmi;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TooManyListenersException;

import javax.management.timer.Timer;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.JViewport;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.event.TableModelListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import org.jdesktop.swingx.JXDatePicker;

public class HabitsPanel extends JComponent {
	int 						min = 0;
	final Color 				paleGreen = new Color( 20,230,60,96 ); 
	List<List<String>>			eatList = new ArrayList<List<String>>();
	JSplitPane					splitpane;
	JToolBar					toolbar;
	JXDatePicker				datepicker;
	
	public HabitsPanel( String lang ) {
		super();
		
		datepicker = new JXDatePicker();
		/*DateSelectionModel mod = new DateSelectionModel() {
			
		};
		//datepicker.setMonthView(new JXM)*/
		this.setLayout( new BorderLayout() );
		toolbar = new JToolBar();
		toolbar.add( datepicker );
		this.add( toolbar, BorderLayout.NORTH );
		splitpane = new JSplitPane( JSplitPane.VERTICAL_SPLIT );
		this.add( splitpane );
		
		JTabbedPane timelineTabPane = new JTabbedPane( JTabbedPane.RIGHT );
		final JTable		timelineTable = new JTable() {
			public Component prepareRenderer( TableCellRenderer renderer, int row, int column ) {
				Component c = super.prepareRenderer( renderer, row, column );
				//Color bc = c.getBackground();
				Object val = this.getValueAt(0, column);
				if( val.equals("Sat") || val.equals("Sun") ) {
					c.setBackground( paleGreen );
				} else if( this.getSelectedRow() != row ) {
					c.setBackground( Color.white );
				} else {
					c.setBackground( this.getSelectionBackground() );
				}
				return c;
			}
		};
		
		final JTable		timelineDataTable = new JTable() {
			@Override
			public Component prepareRenderer( TableCellRenderer renderer, int row, int column ) {
				Component c = super.prepareRenderer( renderer, row, column );
				//Color bc = c.getBackground();
				Object val = timelineTable.getValueAt(0, column);
				if( val.equals("Sat") || val.equals("Sun") ) {
					c.setBackground( paleGreen );
				} else if( this.getSelectedRow() != row ) {
					c.setBackground( Color.white );
				} else {
					c.setBackground( this.getSelectionBackground() );
				}
				return c;
			}
		};
		final JScrollPane timelineDataScroll = new JScrollPane( timelineDataTable );
		
		timelineDataTable.getColumnModel().addColumnModelListener( new TableColumnModelListener() {
			public void columnAdded(TableColumnModelEvent e) {}

			public void columnMarginChanged(ChangeEvent e) {
				Enumeration<TableColumn> 	tcs = timelineDataTable.getColumnModel().getColumns();
				int i = 0;
				while( tcs.hasMoreElements() ) {
					TableColumn tc = tcs.nextElement();
					timelineTable.getColumnModel().getColumn(i++).setPreferredWidth(tc.getPreferredWidth());
				}
			}

			public void columnMoved(TableColumnModelEvent e) {
				timelineTable.moveColumn( e.getFromIndex(), e.getToIndex() );		
			}

			public void columnRemoved(TableColumnModelEvent e) {}

			public void columnSelectionChanged(ListSelectionEvent e) {}
		});
		
		JComponent topComp = new JComponent() {};
		topComp.setLayout( new BorderLayout() );
		
		//topTable.setShowGrid( true );
		//topTable.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );
		//table.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );
		//scrollPane.setViewportView( table );
		
		//table.getTableHeader().setVisible( false );
		//table.setTableHeader( null );
		
		/*scrollPane.setVerticalScrollBarPolicy( ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS );
		scrollPane.setHorizontalScrollBarPolicy( ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS );
		scrollPane.setRowHeaderView( leftTable );
		leftScrollPane.setViewport( scrollPane.getRowHeader() );
		leftScrollPane.setVerticalScrollBarPolicy( ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER );
		leftScrollPane.setHorizontalScrollBarPolicy( ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS );*/
		
		topComp.add( timelineTable );
		topComp.add( timelineDataTable.getTableHeader(), BorderLayout.SOUTH );
		
		JViewport	spec = new JViewport() {
			public void setView( Component view ) {
				if( !(view instanceof JTableHeader) ) super.setView( view );
			}
		};
		spec.setView( topComp );
		
		timelineDataScroll.setVerticalScrollBarPolicy( ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS );
		timelineDataScroll.setHorizontalScrollBarPolicy( ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS );
		
		timelineDataScroll.setColumnHeader( spec );
		
		JScrollPane timelineScroll = new JScrollPane();
		timelineScroll.setViewport( timelineDataScroll.getColumnHeader() );
		timelineScroll.setHorizontalScrollBarPolicy( ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER );
		
		DropTarget dropTarget = new DropTarget() {
			public boolean isActive() {
				return true;
			}
		};
		timelineDataScroll.setDropTarget( dropTarget );
		
		JSplitPane	timelineSplit = new JSplitPane( JSplitPane.VERTICAL_SPLIT, timelineScroll, timelineDataScroll );
		
		if( lang.equals("IS") ) {
			timelineTabPane.addTab("Inn", timelineSplit);
			timelineTabPane.addTab("Út", null);
		} else {
			timelineTabPane.addTab("In", timelineSplit);
			timelineTabPane.addTab("Out", null);
		}
		
		Calendar now = Calendar.getInstance();
		final Calendar cal = new GregorianCalendar();
		cal.set( Calendar.YEAR, now.get( Calendar.YEAR ) );
		cal.set( Calendar.MONTH, 0 );
		cal.set( Calendar.DAY_OF_MONTH, 1 );
		final long time = cal.getTimeInMillis();
		
		//Date d = new Date(Date.);
		TableModel	timelineModel = new TableModel() {
			public void addTableModelListener(TableModelListener arg0) {
				
			}

			public Class<?> getColumnClass(int arg0) {
				return String.class;
			}

			public int getColumnCount() {
				// TODO Auto-generated method stub
				return 365;
			}

			public String getColumnName(int arg0) {
				cal.setTimeInMillis( time+arg0*Timer.ONE_DAY );
				return cal.get( Calendar.WEEK_OF_YEAR ) + "";
			}

			public int getRowCount() {
				return 2;
			}

			public Object getValueAt(int arg0, int arg1) {
				cal.setTimeInMillis( time+arg1*Timer.ONE_DAY );
				String str = CompatUtilities.getDateString( cal, arg0 == 1 );
				return str;
			}

			public boolean isCellEditable(int arg0, int arg1) {
				return false;
			}

			public void removeTableModelListener(TableModelListener arg0) {}

			public void setValueAt(Object arg0, int arg1, int arg2) {}
		};
		timelineTable.setModel( timelineModel );
		timelineTable.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );
		
		for( int i = 0; i < 365; i++ ) {
			eatList.add(null);
		}
		
		TableModel	timelineDataModel = new TableModel() {
			public void addTableModelListener(TableModelListener l) {}

			public Class<?> getColumnClass(int columnIndex) {
				return String.class;
			}

			public int getColumnCount() {
				return 365;
			}

			public String getColumnName(int arg0) {
				cal.setTimeInMillis( time+arg0*Timer.ONE_DAY );
				return cal.get( Calendar.WEEK_OF_YEAR ) + "";
			}

			public int getRowCount() {
				// TODO Auto-generated method stub
				return min;
			}

			public Object getValueAt(int rowIndex, int columnIndex) {
				List<String> dayList = eatList.get(columnIndex);
				if( dayList != null && rowIndex < dayList.size() ) {
					return dayList.get(rowIndex);
				}
				return null;
			}

			public boolean isCellEditable(int rowIndex, int columnIndex) {
				// TODO Auto-generated method stub
				return false;
			}

			public void removeTableModelListener(TableModelListener l) {
				// TODO Auto-generated method stub
				
			}

			public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
				// TODO Auto-generated method stub
				
			}
		};
		timelineDataTable.setModel( timelineDataModel );
		timelineDataTable.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );
		
		try {
			dropTarget.addDropTargetListener( new DropTargetListener(){
				public void dropActionChanged(DropTargetDragEvent dtde) {
					// TODO Auto-generated method stub
					
				}
			
				public void drop(DropTargetDropEvent dtde) {
					Point loc = dtde.getLocation();
					Point offset = timelineDataScroll.getViewport().getViewPosition();
					Point p = new Point( offset.x + loc.x, offset.y + loc.y );
					int c = timelineDataTable.columnAtPoint( p );
					List<String> list = eatList.get(c);
					if( list == null ) {
						list = new ArrayList<String>();
						eatList.set( c, list );
					}
					String val;
					try {
						val = dtde.getTransferable().getTransferData( DataFlavor.stringFlavor ).toString();
						if( val != null ) {
							String[] spl = val.split("\n");
							for( String str : spl ) {
								String[] subspl = str.split("\t");
								if( subspl.length > 1 ) list.add( subspl[1] );
							}
						}
						if( list.size() > min ) {
							min = list.size();
						}
						
						timelineDataTable.revalidate();
						timelineDataTable.repaint();
					} catch (UnsupportedFlavorException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			
				public void dragOver(DropTargetDragEvent dtde) {
					// TODO Auto-generated method stub
					
				}
			
				public void dragExit(DropTargetEvent dte) {
					// TODO Auto-generated method stub
					
				}
			
				public void dragEnter(DropTargetDragEvent dtde) {
					// TODO Auto-generated method stub
					
				}
			});
		} catch (TooManyListenersException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		JComponent drawer = new JComponent() {
			public void paintComponent( Graphics g ) {
				super.paintComponent( g );
			}
		};
		JScrollPane timelineDrawScroll = new JScrollPane( drawer );
		
		splitpane.setTopComponent( timelineTabPane );
		splitpane.setBottomComponent( timelineDrawScroll );
	}
}
