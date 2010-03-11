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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
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
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.event.TableModelListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import org.jdesktop.swingx.JXDatePicker;

public class HabitsPanel extends JComponent {
	int 							min = 0;
	final Color 					paleGreen = new Color( 20,230,60,96 ); 
	Map<String,Week>				eatList = new HashMap<String,Week>();
	JSplitPane						splitpane;
	JToolBar						toolbar;
	JXDatePicker					datepicker;
	Calendar						cal;
	
	Week							currentWeekObj;
	int								currentWeek;
	int								currentYear;
	
	JTable							timelineDataTable;
	boolean							sel = false;
	
	class Week {
		String[]	d = new String[7];
		
		public Week() {
			for( int i = 0; i < d.length; i++ ) {
				d[i] = "";
			}
		}
		
		public int getMin() {
			int ret = 0;
			for( String s : d ) {
				ret = Math.max( s.length() == 0 ? 0 : s.split("\t").length, ret ); 
			}
			
			return ret;
		}
	};
	
	public long getCurrentTime() {
		Date date = datepicker.getDate();
		long time = System.currentTimeMillis();
		if( date != null ) time = date.getTime();
		
		return time;
	}
	
	public String getCurrentCardName() {
		long time = getCurrentTime();
		cal.setTimeInMillis( time );
		int woy = cal.get( Calendar.WEEK_OF_YEAR );
		int yer = cal.get( Calendar.YEAR );
		return woy+"_"+yer;
	}
	
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
			public void setRowSelectionInterval(int r1, int r2) {
				sel = true;
				super.setRowSelectionInterval(r1, r2);
			}

			public void setColumnSelectionInterval(int c1, int c2) {
				sel = true;
				super.setColumnSelectionInterval(c1, c2);
			}

			public void addRowSelectionInterval(int r1, int r2) {
				sel = true;
				super.addRowSelectionInterval(r1, r2);
			}

			public void addColumnSelectionInterval(int c1, int c2) {
				sel = true;
				super.addColumnSelectionInterval(c1, c2);
			}

			public Component prepareRenderer( TableCellRenderer renderer, int row, int column ) {
				Component c = super.prepareRenderer( renderer, row, column );
				//Color bc = c.getBackground();
				/*Object val = this.getValueAt(0, column);
				if( val.equals("Sunnudagur") || val.equals("Laugardagur") ) {
					c.setBackground( paleGreen );
				} else if( this.getSelectedRow() != row ) {
					c.setBackground( Color.white );
				} else {
					c.setBackground( this.getSelectionBackground() );
				}*/
				return c;
			}
		};
		
		timelineDataTable = new JTable() {
			public void setRowSelectionInterval(int r1, int r2) {
				sel = false;
				super.setRowSelectionInterval(r1, r2);
			}

			public void setColumnSelectionInterval(int c1, int c2) {
				sel = false;
				super.setColumnSelectionInterval(c1, c2);
			}

			public void addRowSelectionInterval(int r1, int r2) {
				sel = false;
				super.addRowSelectionInterval(r1, r2);
			}

			public void addColumnSelectionInterval(int c1, int c2) {
				sel = false;
				super.addColumnSelectionInterval(c1, c2);
			}
			
			@Override
			public Component prepareRenderer( TableCellRenderer renderer, int row, int column ) {
				Component c = super.prepareRenderer( renderer, row, column );
				//Color bc = c.getBackground();
				/*Object val = timelineTable.getValueAt(0, column);
				if( val.equals("Sat") || val.equals("Sun") ) {
					c.setBackground( paleGreen );
				} else if( this.getSelectedRow() != row ) {
					c.setBackground( Color.white );
				} else {
					c.setBackground( this.getSelectionBackground() );
				}*/
				return c;
			}
		};
		timelineDataTable.setColumnSelectionAllowed( true );
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
		
		timelineDataScroll.setVerticalScrollBarPolicy( ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED );
		timelineDataScroll.setHorizontalScrollBarPolicy( ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER );
		
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
		cal = new GregorianCalendar();
		cal.set( Calendar.YEAR, now.get( Calendar.YEAR ) );
		cal.set( Calendar.MONTH, 0 );
		cal.set( Calendar.DAY_OF_MONTH, 1 );
		
		//Date d = new Date(Date.);
		TableModel	timelineModel = new TableModel() {
			public void addTableModelListener(TableModelListener arg0) {
				
			}

			public Class<?> getColumnClass(int arg0) {
				return String.class;
			}

			public int getColumnCount() {
				// TODO Auto-generated method stub
				return 7;
			}

			public String getColumnName(int arg0) {
				final long time = getCurrentTime();
				cal.setTimeInMillis( time );
				return cal.get( Calendar.WEEK_OF_YEAR ) + "";
			}

			public int getRowCount() {
				return 2;
			}

			public Object getValueAt(int arg0, int arg1) {
				if( arg0 == 0 ) {
					if( arg1 == 0 ) return "Sunnudagur";
					else if( arg1 == 1 ) return "Mánudagur";
					else if( arg1 == 2 ) return "Þriðjudagur";
					else if( arg1 == 3 ) return "Miðvikudagur";
					else if( arg1 == 4 ) return "Fimmtudagur";
					else if( arg1 == 5 ) return "Föstudagur";
					else if( arg1 == 6 ) return "Laugardagur";
				} else {					
					final long time = getCurrentTime();
					cal.setTimeInMillis( time );
					int weekday = cal.get( Calendar.DAY_OF_WEEK )-1;
					int val = (arg1-weekday);
					cal.setTimeInMillis( time+val*Timer.ONE_DAY );
					int mday = cal.get( Calendar.DAY_OF_MONTH );
					int mnum = cal.get( Calendar.MONTH );
					
					if( mnum == 0 ) return mday + ". Janúar";
					else if( mnum == 1 ) return mday + ". Febrúar";
					else if( mnum == 2 ) return mday + ". Mars";
					else if( mnum == 3 ) return mday + ". Apríl";
					else if( mnum == 4 ) return mday + ". Maí";
					else if( mnum == 5 ) return mday + ". Júní";
					else if( mnum == 6 ) return mday + ". Júlí";
					else if( mnum == 7 ) return mday + ". Ágúst";
					else if( mnum == 8 ) return mday + ". September";
					else if( mnum == 9 ) return mday + ". Október";
					else if( mnum == 10 ) return mday + ". Nóvember";
					else if( mnum == 11 ) return mday + ". Desember";
				}
				//cal.setTimeInMillis( time+arg1*Timer.ONE_DAY );
				//String str = CompatUtilities.getDateString( cal, arg0 == 1 );
				return "";
			}

			public boolean isCellEditable(int arg0, int arg1) {
				return false;
			}

			public void removeTableModelListener(TableModelListener arg0) {}

			public void setValueAt(Object arg0, int arg1, int arg2) {}
		};
		timelineTable.setModel( timelineModel );
		timelineTable.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );
		timelineTable.setRowSelectionAllowed( false );
		timelineTable.setColumnSelectionAllowed( true );
		
		timelineTable.getColumnModel().getSelectionModel().addListSelectionListener( new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				boolean ss = sel;
				sel = false;
				if (!ss) {
					int rc = timelineDataTable.getRowCount();
					if( rc > 0 ) timelineDataTable.setRowSelectionInterval(0, rc-1);
					
					int selcol = timelineTable.getSelectedColumn();
					timelineDataTable.setColumnSelectionInterval(selcol, selcol);
					
					int[] selcols = timelineTable.getSelectedColumns();
					for( int i : selcols ) {
						timelineDataTable.addColumnSelectionInterval( i, i );
					}
					sel = false;
				}
			}
		});
		timelineDataTable.getColumnModel().getSelectionModel().addListSelectionListener( new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				boolean ss = sel;
				sel = true;
				if (ss) {
					int selcol = timelineDataTable.getSelectedColumn();
					if( selcol >= 0 && selcol < timelineTable.getColumnCount() ) {
						timelineTable.setColumnSelectionInterval(selcol, selcol);
						
						int[] selcols = timelineDataTable.getSelectedColumns();
						for( int i : selcols ) {
							timelineTable.addColumnSelectionInterval( i, i );
						}
						sel = true;
					}
				}
			}
		});
		
		timelineDataTable.setRowHeight( 25 );
		
		try {
			load();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		//timelineDataTable.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );
		
		try {
			dropTarget.addDropTargetListener( new DropTargetListener(){
				public void dropActionChanged(DropTargetDragEvent dtde) {
					// TODO Auto-generated method stub
					
				}
			
				public void drop(DropTargetDropEvent dtde) {
					String tstr = getCurrentCardName();
					
					Point loc = dtde.getLocation();
					Point offset = timelineDataScroll.getViewport().getViewPosition();
					Point p = new Point( offset.x + loc.x, offset.y + loc.y );
					int c = timelineDataTable.columnAtPoint( p );
					Week list = eatList.get( tstr );
					if( list == null ) {
						list = new Week();
						eatList.put( tstr, list );
					}
					String val;
					try {
						val = dtde.getTransferable().getTransferData( DataFlavor.stringFlavor ).toString();
						if( val != null ) {
							String[] spl = val.split("\n");
							for( String str : spl ) {
								String[] subspl = str.split("\t");
								if( subspl.length >= 1 ) {
									if( list.d[c].length() == 0 ) list.d[c] += subspl[0];
									else list.d[c] += "\t" + subspl[0];
								}
							}
							
							timelineDataTable.revalidate();
							timelineDataTable.repaint();
							save( tstr );
						}
						
						int minsplit = list.d[c].split("\t").length;
						if( minsplit > min ) {
							min = minsplit;
						}
					} catch (UnsupportedFlavorException e) {
						e.printStackTrace();
					} catch (IOException e) {
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
			e.printStackTrace();
		}
		
		JComponent drawer = new JComponent() {
			public void paintComponent( Graphics g ) {
				super.paintComponent( g );
			}
		};
		JScrollPane timelineDrawScroll = new JScrollPane( drawer );
		
		datepicker.addPropertyChangeListener( new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				try {
					load();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		splitpane.setTopComponent( timelineTabPane );
		splitpane.setBottomComponent( timelineDrawScroll );
	}
	
	public void setCurrentWeek( final Week w ) {
		min = w.getMin();
		TableModel timelineDataModel = new TableModel() {
			public void addTableModelListener(TableModelListener l) {}

			public Class<?> getColumnClass(int columnIndex) {
				return String.class;
			}

			public int getColumnCount() {
				return 7;
			}

			public String getColumnName(int arg0) {
				final long time = getCurrentTime();
				cal.setTimeInMillis( time );
				return cal.get( Calendar.WEEK_OF_YEAR ) + "";
			}

			public int getRowCount() {
				return min;
			}

			public Object getValueAt(int rowIndex, int columnIndex) {
				if( w != null ) {
					String[] split = w.d[columnIndex].split("\t");
					if( rowIndex < split.length ) {
						return split[rowIndex];
					}
				}
				return null;
			}

			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return false;
			}

			public void removeTableModelListener(TableModelListener l) {}
			public void setValueAt(Object aValue, int rowIndex, int columnIndex) {}
		};
		System.err.println(min);
		timelineDataTable.setModel( timelineDataModel );
	}
	
	public void load() throws IOException {
		String tstr = getCurrentCardName();
		min = 0;
		Week w = null;
		if( eatList.containsKey( tstr ) ) {
			w = eatList.get( tstr );
		} else {
			w = new Week();
			File f = new File( System.getProperty("user.home"), ".isgem" );
			f = new File( f, "weeks" );
			if( f.exists() ) {
				f = new File( f, tstr );
				if( f.exists() ) {
					FileReader		fr = new FileReader( f );
					BufferedReader	br = new BufferedReader( fr );
					
					int i = 0;
					String line = br.readLine();
					while( line != null ) {
						w.d[i++] += line;
						line = br.readLine();
					}
				}
			}
			eatList.put( tstr, w );
		}
		setCurrentWeek( w );
	}
	
	public void save( String name ) throws IOException {
		File f = new File( System.getProperty("user.home"), ".isgem" );
		f = new File( f, "weeks" );
		if( !f.exists() ) f.mkdirs();
		
		f = new File( f, name );
		FileWriter	fw = new FileWriter( f );
		for( int c = 0; c < timelineDataTable.getColumnCount(); c++ ) {
			for( int r = 0; r < timelineDataTable.getRowCount(); r++ ) {
				Object val = timelineDataTable.getValueAt(r, c);
				if( val != null ) {
					if( r != 0 ) fw.write( "\t" + val.toString() );
					else fw.write( val.toString() );
				}
			}
			fw.write("\n");
		}
		fw.close();
	}
}
