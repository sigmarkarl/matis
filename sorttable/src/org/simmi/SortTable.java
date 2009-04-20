package org.simmi;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.FlavorMap;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TooManyListenersException;

import javax.imageio.ImageIO;
import javax.swing.DropMode;
import javax.swing.JApplet;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.RowFilter;
import javax.swing.ScrollPaneConstants;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public class SortTable extends JApplet {
	JScrollPane				scrollPane;
	JScrollPane				leftScrollPane;
	JScrollPane				topScrollPane;
	JScrollPane				topLeftScrollPane;
	LinkedSplitPane			leftSplitPane;
	LinkedSplitPane			rightSplitPane;
	JSplitPane				splitPane;
	JTable					table;
	JTable					leftTable;
	JTable					topTable;
	JTable					topLeftTable;
	JTextField				field;
	JTabbedPane				tabbedPane;
	
	JComponent				panel;
	JComponent				graph;
	Image					img;
	
	TableModel 				model;
	TableModel 				topModel;
	
	DetailPanel 			detail;
	
	
	TableRowSorter<TableModel> 	tableSorter;
	TableRowSorter<TableModel>  leftTableSorter;
	MySorter			 	 	currentSorter;
	
	List<Object[]>			stuff;
	//List<Object[]>			header;
	Map<String,Integer>		ngroupMap;
	List<String>			ngroupList;
	
	String					lang;
	boolean					hringur = false;
	
	RowFilter<TableModel,Integer>	filter;
	
	static {
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public class MySorter extends TableRowSorter<TableModel> {
		public MySorter( TableModel model ) {
			super( model );
		}
		
		public int convertRowIndexToModelSuper(int index) {
			return super.convertRowIndexToModel( index );
		}

		public int convertRowIndexToViewSuper(int index) {
			return super.convertRowIndexToView( index );
		}
	}
	
	public class LinkedSplitPane extends JSplitPane {
		LinkedSplitPane	pane;
		
		public LinkedSplitPane( int or, Component lt, Component rt ) {
			super( or, lt, rt );
		}
		
		public void setDividerLocationSuper( int d ) {
			super.setDividerLocation( d );
		}
		
		public void setDividerLocation( int d ) {
			pane.setDividerLocationSuper( d );
			super.setDividerLocation( d );
		}
		
		public void setDividerLocationSuper( double d ) {
			super.setDividerLocation( d );
		}
		
		public void setDividerLocation( double d ) {
			pane.setDividerLocationSuper( d );
			super.setDividerLocation( d );
		}
		
		public void setLinkedSplitPane( LinkedSplitPane pane ) {
			this.pane = pane;
		}
	}
	
	public SortTable() {
		super();
	}
	
	public class Nut {
		public Nut( String name, String unit ) {
			this.name = name;
			this.unit = unit;
		}
		
		String name;
		String unit;		
	}
	
	public List<Object[]> parseData( String loc ) throws IOException {
		Map<String,String>	fgroupMap = new HashMap<String,String>();
		
		InputStream inputStream;
		BufferedReader br;
		String line;
		if( loc.equals("IS") ) {
			inputStream = this.getClass().getResourceAsStream( "thsGroups.txt" );
			br = new BufferedReader( new InputStreamReader( inputStream ) );
			line = br.readLine();
			while( line != null ) {
				String[] split = line.split("\\t");
				if( split.length > 1 && split[0].contains(".") ) {
					fgroupMap.put( split[0], split[1] );
				}
				line = br.readLine();
			}
		} else {
			inputStream = this.getClass().getResourceAsStream( "FD_GROUP.txt" );
			br = new BufferedReader( new InputStreamReader( inputStream ) );
			line = br.readLine();
			while( line != null ) {
				String[] split = line.split("\\^");
				if( split.length == 2 ) {
					fgroupMap.put( split[0], split[1].substring(1, split[1].length()-1) );
				}
				line = br.readLine();
			}
		}
		
		List[]	nutList = new List[2];
		for( int i = 0; i < nutList.length; i++ ) {
			List<Object> list = new ArrayList<Object>();
			nutList[i] = list;
			list.add( null );
			list.add( null );
		}
		ngroupMap = new HashMap<String,Integer>();
		ngroupList = new ArrayList<String>();
		
		if( loc.equals("IS") ) {
			inputStream = this.getClass().getResourceAsStream( "Component.txt" );
			br = new BufferedReader( new InputStreamReader( inputStream ) );
			line = br.readLine();
			int i = 0;
			while( line != null ) {
				String[] split = line.split("\\t");
				if( split.length > 3 ) {
					String sName = null;
					if( split[4] != null && split[4].length() > 0 ) {
						sName = split[4];
					}
					String nName = split[3];
					ngroupMap.put( split[2], i++ );
					ngroupList.add( nName );// + " ("+split[1].substring(1, split[1].length()-1)+")" );
					//List<Object>	lobj = nutList.get(i).get(i)
					nutList[0].add( sName );
					String mName = split[6];
					nutList[1].add( mName );
				}
				line = br.readLine();
			}
		} else {
			inputStream = this.getClass().getResourceAsStream( "NUTR_DEF.txt" );
			br = new BufferedReader( new InputStreamReader( inputStream ) );
			line = br.readLine();
			int i = 0;
			while( line != null ) {
				String[] split = line.split("\\^");
				if( split.length > 3 ) {
					String sName = null;
					if( split[2] != null && split[2].length() > 0 ) {
						sName = split[2].substring(1, split[2].length()-1);
					}
					String nName = split[3].substring(1, split[3].length()-1);
					ngroupMap.put( split[0], i++ );
					ngroupList.add( nName );// + " ("+split[1].substring(1, split[1].length()-1)+")" );
					//List<Object>	lobj = nutList.get(i).get(i)
					nutList[0].add( sName );
					String mName = split[1].substring(1, split[1].length()-1);
					nutList[1].add( mName );
					
					//System.err.println( mName );
					//System.err.println( nName + " " + sName + "  " + mName + "  " + split[0] + "  " + split[1] + "  " + split[2] + "  " + split[3] );
				}
				line = br.readLine();
			}
		}
		
		List<Object[]>	result = new ArrayList<Object[]>();
		for( List l : nutList ) {
			result.add( l.toArray( new Object[0] ) );
		}
		
		Map<String,Integer>	foodInd = new HashMap<String,Integer>();
		int i = 0;
		int k = 0;
		if( loc.equals("IS") ) {
			inputStream = this.getClass().getResourceAsStream( "Food.txt" );
			br = new BufferedReader( new InputStreamReader( inputStream ) );
			line = br.readLine();
			while( line != null ) {
				String[] split = line.split("\\t");
				foodInd.put(split[1], k++);
				
				String val = split[6];
				split[6] = fgroupMap.get( val );
				Object[] array = new Object[ 2+ngroupList.size() ];
				array[0] = split[6];
				array[1] = split[2];
				for( i = 2; i < array.length; i++ ) {
					array[i] = null;
				}
				result.add( array );
				line = br.readLine();
			}
		} else {
			inputStream = this.getClass().getResourceAsStream( "FOOD_DES.txt" );
			br = new BufferedReader( new InputStreamReader( inputStream ) );
			line = br.readLine();
			while( line != null ) {
				String[] split = line.split("\\^");
				String val = split[1];
				split[1] = fgroupMap.get( val );
				Object[] array = new Object[ 2+ngroupList.size() ];
				array[0] = split[1];
				array[1] = split[2].substring(1, split[2].length()-1);
				for( i = 2; i < array.length; i++ ) {
					array[i] = null;
				}
				result.add( array );
				line = br.readLine();
			}
		}
		
		int 	start = -1;
		String 	prev = "";
		if( loc.equals("IS") ) {
			inputStream = this.getClass().getResourceAsStream( "result.txt" );
			br = new BufferedReader( new InputStreamReader( inputStream ) );
			line = br.readLine();
			while( line != null ) {
				String[] split = line.split("\\t");
				
				if( split.length > 4 ) {
					if( foodInd.containsKey( split[1] ) ) {
						start = foodInd.get(split[1]);
					} else {
						System.err.println( split[1] );
					}
					Object[]	objs = result.get(start+2);
					if( split[5].length() > 0 ) {
						String replc = split[5].replace(',', '.');
						replc = replc.replace("<", "");
						float f = -1.0f;
						try {
							f = Float.parseFloat( replc );
						} catch( Exception e ) {
							System.err.println( line );
						}
						int ngroupOffset = ngroupMap.get(split[2]);
						objs[ 2+ngroupOffset ] = f;
					}
				}
				line = br.readLine();
			}
		} else {
			inputStream = this.getClass().getResourceAsStream( "NUT_DATA.txt" );
			br = new BufferedReader( new InputStreamReader( inputStream ) );
			line = br.readLine();
			while( line != null ) {
				String[] split = line.split("\\^");
				if( !split[0].equals(prev) ) {
					prev = split[0];
					start++;
				}
				result.get(start+2)[ 2+ngroupMap.get(split[1]) ] = Float.parseFloat(split[2]);
				//split[1] = fgroupMap.get( val );
				//result.add( split );
				line = br.readLine();
			}
		}
		
		return result;
	}
	
	public class SimTableColumn extends TableColumn {
		TableColumn link;
		
		public SimTableColumn() {
			super();
		}
		
		public void setPreferredWidth( int preferredWidth ) {
			super.setPreferredWidth(preferredWidth);
			link.setPreferredWidth(preferredWidth);
		}
	}

	String filterText;
	public void updateFilter() {
		//currentSorter = (MySorter)leftTableSorter;
		if( field.getText().length() > 0 ) {
			filterText = "(?i).*"+field.getText()+".*";
			leftTableSorter.setRowFilter( filter ); //RowFilter.regexFilter("(?i)"+field.getText(), 1) );
			tableSorter.setRowFilter( filter );
		}
		else {
			filterText = null;
			leftTableSorter.setRowFilter( null );
			tableSorter.setRowFilter( null );
		}
		//table.tableChanged( new TableModelEvent( table.getModel() ) );
	}

	public String lastResult;
	public void init() {
		lang = "IS";
		/*String loc = this.getParameter("loc");
		if( loc != null ) {
			lang = loc;
		}*/
		
		ToolTipManager.sharedInstance().setInitialDelay(0);
		try {
			stuff = parseData( lang );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.getRootPane().setBackground( Color.white );
		this.requestFocus();
		
		scrollPane = new JScrollPane();
		leftScrollPane = new JScrollPane();
		topScrollPane = new JScrollPane();
		topLeftScrollPane = new JScrollPane();
		table = new JTable() {
			public void sorterChanged( RowSorterEvent e ) {
				currentSorter = (MySorter)e.getSource();
				leftTable.repaint();
				super.sorterChanged( e );
			}
			
			/*public void moveColumn( int column, int targetColumn ) {
				super.moveColumn(column, targetColumn);
				topTable.moveColumn(column, targetColumn);
			}*/
		};
		table.setColumnSelectionAllowed( true );
		/*table.setTransferHandler( new TransferHandler() {
			
		});*/
		
		table.getColumnModel().addColumnModelListener( new TableColumnModelListener() {

			@Override
			public void columnAdded(TableColumnModelEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void columnMarginChanged(ChangeEvent e) {
				Enumeration<TableColumn> 	tcs = table.getColumnModel().getColumns();
				int i = 0;
				while( tcs.hasMoreElements() ) {
					TableColumn tc = tcs.nextElement();
					topTable.getColumnModel().getColumn(i++).setPreferredWidth(tc.getPreferredWidth());
				}
			}

			@Override
			public void columnMoved(TableColumnModelEvent e) {
				topTable.moveColumn( e.getFromIndex(), e.getToIndex() );		
			}

			@Override
			public void columnRemoved(TableColumnModelEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void columnSelectionChanged(ListSelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		leftTable = new JTable() {
			public void sorterChanged( RowSorterEvent e ) {
				currentSorter = (MySorter)e.getSource();
				table.repaint();
				super.sorterChanged( e );
			}
			
			public String getToolTipText() {
				return super.getToolTipText();
			}
			
			public String getToolTipText( MouseEvent me ) {
				Point p = me.getPoint();
				int r = rowAtPoint(p);
				int c = columnAtPoint(p);
				Object ret = super.getValueAt(r, c);
				if( ret != null ) {
					return ret.toString(); //super.getToolTipText( me );
				}
				return "";
			}
				
			public Point getToolTipLocation( MouseEvent e ) {
				return e.getPoint(); //super.getToolTipLocation(e);
			}
		};
		leftTable.setDragEnabled( true );
		leftTable.setToolTipText(" ");
		//leftTable.en
		topTable = new JTable();
		
		final ByteBuffer	ba = ByteBuffer.allocate(1000000);
		final CharBuffer	cb = CharBuffer.allocate(1000000);
		//ba.order( ByteOrder.LITTLE_ENDIAN );
		final String startTag = "imgurl";
		leftTable.getSelectionModel().addListSelectionListener( new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				int row = leftTable.getSelectedRow();
				if( row >= 0 && row < leftTable.getRowCount() ) {
					final String str = leftTable.getValueAt(row, 1).toString().replaceAll("[ ,]+", "+");
					//int row = e.getFirstIndex();
					if( tabbedPane.getSelectedComponent() == panel && !str.equals(lastResult) ) {
						lastResult = str;
						Thread t = new Thread() {
							public void run() {
								URL url;
								try {
									//url = new URL("http://localhost/labbi.html");
									//url = new URL("http://search.live.com/images/results.aspx?q="+str);
									url = new URL("http://images.google.com/images?hl=en&q="+str ); //+"&btnG=Search+Images&gbv=2" ); //&btnG=Search+Images" );//hl=en&q=Orange");//+str);
									System.err.println( "searching for " + str );
									URLConnection connection = null;
									connection = url.openConnection();
									//Proxy proxy = new Proxy( Type.HTTP, new InetSocketAddress("proxy.decode.is",8080) );
									//connection = url.openConnection( proxy );
									//connection.setDoOutput( true );
									if( connection instanceof HttpURLConnection ) {
										HttpURLConnection httpConnection = (HttpURLConnection)connection;
										httpConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.6) Gecko/2009020518 Ubuntu/9.04 (jaunty) Firefox/3.0.6" );
										httpConnection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,**;q=0.8" );
										httpConnection.setRequestProperty("Accept-Language", "en-us,en;q=0.5" );
										//httpConnection.setRequestProperty("Accept-Encoding", "gzip,deflate" );
										httpConnection.setRequestProperty("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7" );
										httpConnection.setRequestProperty("Keep-Alive", "300" );
									}
									InputStream stream = connection.getInputStream();
									//stream = new GZIPInputStream( stream );
									
									int total = 0;
									int read = stream.read(ba.array(), total, ba.limit()-total );
									total = 0;
									while( read > 0 ) {
										total += read;
										read = stream.read( ba.array(), total, ba.limit()-total );
									}
									stream.close();
									
									String result = new String( ba.array(), 0, total);
									int index = result.indexOf( startTag );
									int val = result.indexOf("http:", index); //index+startTag.length();
									
									int stop = result.indexOf( "\\x26", val );
									if( stop == -1 ) {
										stop = result.indexOf( '&', val );
									}
									
									String urlstr = result.substring(val, val+20);
									if( stop != -1 ) {
										urlstr = result.substring( val, stop );
									}
									System.err.println( urlstr );
									
									/*while( index > 0 && (result.charAt(val) != 'h' || !(urlstr.endsWith("jpg") || urlstr.endsWith("png") || urlstr.endsWith("gif"))) ) {
										index = result.indexOf( startTag, val );
										val = index+startTag.length();
										stop = result.indexOf( '&', val );
										urlstr = result.substring( val, stop );
									}*/
									
									if( stop > 0 ) {
										urlstr = urlstr.replace("%20", " ").replace("%2520", " ");
										url = new URL( urlstr );
										connection = url.openConnection();
										stream = connection.getInputStream();
										img = ImageIO.read(stream);
										panel.repaint();
									}
								} catch (MalformedURLException e1) {
									e1.printStackTrace();
								} catch (IOException e2) {
									e2.printStackTrace();
								}
							}
						};
						t.start();
					} else if( tabbedPane.getSelectedComponent() == graph ) {
						graph.repaint();
					} else if( tabbedPane.getSelectedComponent() == detail ) {
						detail.detailTable.tableChanged( new TableModelEvent( detail.detailModel ) );
					}
				}
			}
		});
		//System.err.println( leftTable.getColumnModel() );
		leftTable.addMouseListener( new MouseAdapter() {
			public void mousePressed( MouseEvent e ) {
				leftTable.requestFocus();
				if( e.getClickCount() == 2 ) {
					int r = leftTable.getSelectedRow();
					if( r >= 0 && r < leftTable.getRowCount() ) {
						for( int start = 0; start < table.getColumnCount()-1; start++ ) {
							float min = Float.NEGATIVE_INFINITY;
							int ind = start;
							for( int i = start; i < table.getColumnCount(); i++ ) {
								Object val = table.getValueAt(r, i);
								if( val != null ) {
									float f = (Float)val;
									if( f > min ) {
										min = f;
										ind = i;
									}
								}
							}
							if( ind > start ) {
								table.moveColumn(ind, start);
							}
						}
					}
				}
			}
		});
		topLeftTable = new JTable();
		topLeftTable.addMouseListener( new MouseAdapter() {
			public void mousePressed( MouseEvent e ) {
				if( e.getClickCount() == 2 ) {
					int r = topLeftTable.getSelectedRow();
					if( r >= 0 && r < topLeftTable.getRowCount() ) {
						for( int start = 0; start < topTable.getColumnCount()-1; start++ ) {
							String min = "";
							int ind = start;
							for( int i = start; i < topTable.getColumnCount(); i++ ) {
								Object val = topTable.getValueAt(r, i);
								if( val != null ) {
									String s = val.toString();
									if( s.compareTo(min) > 0 ) {
										min = s;
										ind = i;
									}
								}
							}
							if( ind > start ) {
								table.moveColumn(ind, start);
							}
						}
					}
				}
			}
		});
		
		//table.setAutoCreateRowSorter( true );
		//leftTable.setRowSorter( table.getRowSorter() );
		//leftTable.setAutoCreateRowSorter( true );
		
		JComponent topComp = new JComponent() {};
		topComp.setLayout( new BorderLayout() );
		
		JComponent topLeftComp = new JComponent() {};
		topLeftComp.setLayout( new BorderLayout() );
		
		topTable.setShowGrid( true );
		topTable.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );
		table.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );
		scrollPane.setViewportView( table );
		
		//table.getTableHeader().setVisible( false );
		//table.setTableHeader( null );
		
		scrollPane.setVerticalScrollBarPolicy( ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS );
		scrollPane.setHorizontalScrollBarPolicy( ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS );
		scrollPane.setRowHeaderView( leftTable );
		leftScrollPane.setViewport( scrollPane.getRowHeader() );
		leftScrollPane.setVerticalScrollBarPolicy( ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER );
		leftScrollPane.setHorizontalScrollBarPolicy( ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS );
		
		topComp.add( topTable );
		topComp.add( table.getTableHeader(), BorderLayout.SOUTH );
		
		topLeftComp.add( topLeftTable );
		topLeftComp.add( leftTable.getTableHeader(), BorderLayout.SOUTH );
		//topScrollPane.setViewportView( topTable );
		//scrollPane.setColumnHeader( topScrollPane.getViewport() );
		//scrollPane.setColumnHeaderView( topTable );
		
		JViewport	spec = new JViewport() {
			public void setView( Component view ) {
				if( !(view instanceof JTableHeader) ) super.setView( view );
			}
		};
		spec.setView( topComp );
		
		JViewport	leftSpec = new JViewport() {
			public void setView( Component view ) {
				if( !(view instanceof JTableHeader) ) super.setView( view );
			}
		};
		leftSpec.setView( topLeftComp );
		scrollPane.setColumnHeader( spec );
		topTable.setTableHeader( null );
		topScrollPane.setViewport( scrollPane.getColumnHeader() );
		topScrollPane.setVerticalScrollBarPolicy( ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS );
		topScrollPane.setHorizontalScrollBarPolicy( ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER );
		
		leftScrollPane.setColumnHeader( leftSpec );
		topLeftScrollPane.setViewport( leftScrollPane.getColumnHeader() );
		topLeftScrollPane.setVerticalScrollBarPolicy( ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER );
		topLeftScrollPane.setHorizontalScrollBarPolicy( ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER );
		
		field = new JTextField();
		field.setPreferredSize( new Dimension( 100, 29 ) );
		JComponent leftComponent = new JComponent() {
			
		};
		leftComponent.setLayout( new BorderLayout() );
		leftComponent.add( leftScrollPane );
		leftComponent.add( field, BorderLayout.SOUTH );
		
		panel = new JComponent() {
			public void paintComponent( Graphics g ) {
				super.paintComponent(g);
				if( img != null ) g.drawImage(img, 0, 0, this.getWidth(), this.getHeight(), this );
			}
		};
		tabbedPane = new JTabbedPane(JTabbedPane.BOTTOM, JTabbedPane.SCROLL_TAB_LAYOUT);
		tabbedPane.addChangeListener( new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				if( tabbedPane.getSelectedComponent() == rightSplitPane ) {
					leftScrollPane.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_NEVER );
				} else {
					leftScrollPane.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_ALWAYS );
				}
			}
			
		});
		
		graph = new GraphPanel( lang, new JTable[] {table, leftTable, topTable} );
		
		rightSplitPane = new LinkedSplitPane( JSplitPane.VERTICAL_SPLIT, topScrollPane, scrollPane );
		leftSplitPane = new LinkedSplitPane( JSplitPane.VERTICAL_SPLIT, topLeftScrollPane, leftComponent );
		rightSplitPane.setLinkedSplitPane( leftSplitPane );
		leftSplitPane.setLinkedSplitPane( rightSplitPane );
		
		HabitsPanel eat = new HabitsPanel( lang );
		RecipePanel recipe = null;
		try {
			recipe = new RecipePanel( lang );
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		CostPanel	buy = new CostPanel();
		
		splitPane = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, leftSplitPane, tabbedPane );
		splitPane.setOneTouchExpandable( true );
		
		field.getDocument().addDocumentListener( new DocumentListener() {

			@Override
			public void changedUpdate(DocumentEvent e) {
				updateFilter();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				updateFilter();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				updateFilter();
			}
		});
		//final JFrame f = new JFrame();
		//f.setAlwaysOnTop(true);
		//f.setUndecorated( true );
		//f.add( field );
		//Component c = KeyboardFocusManager.getCurrentKeyboardFocusManager().
		//System.err.println(c);
		/*leftTable.addKeyListener( new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
				if( !f.isVisible() ) {
					f.setBounds(10, 10, 200, 25);
					f.setVisible( true );
				}
				try {
					if( e.getKeyCode() != KeyEvent.VK_BACK_SPACE ) field.getDocument().insertString(field.getCaretPosition(), Character.toString(e.getKeyChar()), null);
				} catch (BadLocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				//System.err.println( field.getText() );
				//leftTableSorter.setRowFilter( RowFilter.regexFilter(field.getText(), 1) );
				//System.err.println(e.getKeyChar());				
			}

			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});*/
		
		//scrollPane.setCorner(JScrollPane.LOWER_LEFT_CORNER, field);
		//leftScrollPane.set
		
		/*table.setDefaultRenderer( Float.class, new TableCellRenderer() {
			public Component getTableCellRendererComponent(JTable table,
					Object value, boolean isSelected, boolean hasFocus,
					int row, int column) {
				
				return null;
			}
		});*/

		TableModel topLeftModel = new TableModel() {

			@Override
			public void addTableModelListener(TableModelListener l) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public Class<?> getColumnClass(int columnIndex) {
				// TODO Auto-generated method stub
				return String.class;
			}

			@Override
			public int getColumnCount() {
				// TODO Auto-generated method stub
				return 1;
			}

			@Override
			public String getColumnName(int columnIndex) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public int getRowCount() {
				// TODO Auto-generated method stub
				return 2;
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				if( lang.equals("IS") ) {
					if( rowIndex == 0 ) return "Nafn efnis";
					return "Eining";
				} else {
					if( rowIndex == 0 ) return "Name";
					return "Unit";
				}
			}

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void removeTableModelListener(TableModelListener l) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void setValueAt(Object value, int rowIndex, int columnIndex) {
				// TODO Auto-generated method stub
				
			}
		};
		topLeftTable.setModel( topLeftModel );
		
		topModel = new TableModel() {
			@Override
			public void addTableModelListener(TableModelListener l) {
			}

			@Override
			public Class<?> getColumnClass(int columnIndex) {
				return String.class;
			}

			@Override
			public int getColumnCount() {
				return stuff.get(0).length-2;
			}

			@Override
			public String getColumnName(int columnIndex) {
				return null;
			}

			@Override
			public int getRowCount() {
				return 2;
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				Object[]	obj = stuff.get(rowIndex);
				return obj[ columnIndex+2 ];
			}

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return false;
			}

			@Override
			public void removeTableModelListener(TableModelListener l) {
			}

			@Override
			public void setValueAt(Object value, int rowIndex, int columnIndex) {				
			}
			
		};
		topTable.setModel( topModel );
		
		final TableModel leftModel = new TableModel() {
			@Override
			public void addTableModelListener(TableModelListener l) {
			}

			@Override
			public Class<?> getColumnClass(int columnIndex) {
				return String.class;
			}

			@Override
			public int getColumnCount() {
				return 2;
			}

			@Override
			public String getColumnName(int columnIndex) {
				if( lang.equals("IS") ) {
					if( columnIndex == 0 ) return "Matar hópur";
					else if( columnIndex == 1 ) return "Matur";
					return "Óþekkt";
				} else {
					if( columnIndex == 0 ) return "Food Group";
					else if( columnIndex == 1 ) return "Food Name";
					return "Unknown";
				}
			}

			@Override
			public int getRowCount() {
				return stuff.size()-2;
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				Object[]	obj = stuff.get(rowIndex+2);
				if( columnIndex >= 0 ) return obj[ columnIndex ];
				
				return null;
			}

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return false;
			}

			@Override
			public void removeTableModelListener(TableModelListener l) {
			}

			@Override
			public void setValueAt(Object value, int rowIndex, int columnIndex) {				
			}
			
		};
		leftTable.setModel( leftModel );
		
		model = new TableModel() {
			@Override
			public void addTableModelListener(TableModelListener l) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public Class<?> getColumnClass(int columnIndex) {
				return Float.class;
			}

			@Override
			public int getColumnCount() {
				return stuff.get(0).length-2;
			}

			@Override
			public String getColumnName(int columnIndex) {
				return ngroupList.get(columnIndex);
			}

			@Override
			public int getRowCount() {
				return stuff.size()-2;
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				Object[]	obj = stuff.get(rowIndex+2);
				return obj[ columnIndex+2 ];
			}

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void removeTableModelListener(TableModelListener l) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void setValueAt(Object value, int rowIndex, int columnIndex) {
				// TODO Auto-generated method stub
				
			}
			
		};
		table.setModel( model );
		detail = new DetailPanel( lang, model, topModel, leftTable );
		
		tableSorter = new MySorter( model ) {
			@Override
			public int convertRowIndexToModel(int index) {
				return currentSorter.convertRowIndexToModelSuper( index );
			}

			@Override
			public int convertRowIndexToView(int index) {
				return currentSorter.convertRowIndexToViewSuper( index );
				//leftTableSorter.
			}
			
			@Override
			public int getViewRowCount() {
				return leftTableSorter.getViewRowCount();
			}
		};
		table.setRowSorter( tableSorter );
		
		currentSorter = (MySorter)tableSorter;
		
		leftTableSorter = new MySorter( leftModel ) {
			@Override
			public int convertRowIndexToModel(int index) {
				return currentSorter.convertRowIndexToModelSuper( index );
			}

			@Override
			public int convertRowIndexToView(int index) {
				return currentSorter.convertRowIndexToViewSuper( index );
				//super.
				//currentSorter.
			}
		};
		leftTable.setRowSorter( leftTableSorter );
		
		filter = new RowFilter<TableModel,Integer>() {
			@Override
			public boolean include(javax.swing.RowFilter.Entry<? extends TableModel, ? extends Integer> entry) {
				//String filterText = field.getText();
				if( filterText != null ) {
					//System.err.println( leftModel.getValueAt( entry.getIdentifier(), 1 ) + " " + filterText );
					return leftModel.getValueAt( entry.getIdentifier(), 1 ).toString().matches( filterText );
				}
				return true;
			}
		};
		tableSorter.setRowFilter( filter );
		
		if( lang.equals("IS") ) {
			tabbedPane.addTab( "Listi", rightSplitPane );
			tabbedPane.addTab( "Myndir", panel );
			tabbedPane.addTab( "Gröf", graph );
			tabbedPane.addTab( "Nánar", detail );
			tabbedPane.addTab( "Uppskriftir", recipe );
			tabbedPane.addTab( "Mataræði og Hreyfing", eat );
			tabbedPane.addTab( "Innkaup og kostnaður", buy );
		} else {
			tabbedPane.addTab( "List", rightSplitPane );
			tabbedPane.addTab( "Image", panel );
			tabbedPane.addTab( "Graph", graph );
			tabbedPane.addTab( "Detail", detail );
			tabbedPane.addTab( "Recipes", recipe );
			tabbedPane.addTab( "Eating and training", eat );
			tabbedPane.addTab( "Cost of buying", buy );
		}
		//RowFilter<TableModel, Integer> rf = RowFilter.regexFilter("Milk",1);
		//leftTableSorter.setRowFilter( rf );
		//panel.setLayout( new BorderLayout() );
		//Dimension d = new Dimension(300,300);
		//panel.setPreferredSize( d );
		//panel.setSize( d );
		//panel.setMinimumSize( d );
		//imagePanel.setPreferredSize( d );
		//imagePanel.setSize( d );
		//panel.add( imagePanel, BorderLayout.WEST );
		
		this.setLayout( new BorderLayout() );
		this.add( splitPane );
		//this.add( panel, BorderLayout.SOUTH );
		//this.add( field, BorderLayout.SOUTH );
		
		//scrollPane.setColumnHeaderView( topTable );
		//topScrollPane.setViewport( scrollPane.getColumnHeader() );
	}
	
	public JScrollPane getScrollPane() {
		return scrollPane;
	}
	
	public JSplitPane getSplitPane() {
		return splitPane;
	}
	
	public JComponent getImagePanel() {
		return panel;
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		/*URL url;
		try {
			url = new URL("http://www.google.com");
			//InputStream stream = url.openStream();
			Proxy proxy = new Proxy( Type.HTTP, new InetSocketAddress("proxy.decode.is",8080) );
			URLConnection connection = url.openConnection( proxy );
			InputStream stream = connection.getInputStream();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		JFrame frame = new JFrame();
		SortTable sortTable = new SortTable();
		sortTable.init();
		frame.setLayout( new BorderLayout() );
		frame.add( sortTable.getSplitPane() );
		//frame.add( sortTable.getImagePanel(), BorderLayout.SOUTH );
		//frame.add( sortTable.getSearchField(), BorderLayout.SOUTH );
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		frame.setSize(800, 600);
		frame.setVisible( true );
	}
	
	public JTextField getSearchField() {
		return field;
	}
	
	public static String detectEncoding(InputStream in) throws IOException {
		String encoding = null;
		in.mark(400);
		int ignoreBytes = 0;
		boolean readEncoding = false;
		byte[] buffer = new byte[400];
		int read = in.read(buffer, 0, 4);
		switch (buffer[0]) {
		case (byte) 0x00:
			if (buffer[1] == (byte) 0x00 && buffer[2] == (byte) 0xFE
					&& buffer[3] == (byte) 0xFF) {
				ignoreBytes = 4;
				encoding = "UTF_32BE";
			} else if (buffer[1] == (byte) 0x00 && buffer[2] == (byte) 0x00
					&& buffer[3] == (byte) 0x3C) {
				encoding = "UTF_32BE";
				readEncoding = true;
			} else if (buffer[1] == (byte) 0x3C && buffer[2] == (byte) 0x00
					&& buffer[3] == (byte) 0x3F) {
				encoding = "UnicodeBigUnmarked";
				readEncoding = true;
			}
			break;
		case (byte) 0xFF:
			if (buffer[1] == (byte) 0xFE && buffer[2] == (byte) 0x00
					&& buffer[3] == (byte) 0x00) {
				ignoreBytes = 4;
				encoding = "UTF_32LE";
			} else if (buffer[1] == (byte) 0xFE) {
				ignoreBytes = 2;
				encoding = "UnicodeLittleUnmarked";
			}
			break;

		case (byte) 0x3C:
			readEncoding = true;
			if (buffer[1] == (byte) 0x00 && buffer[2] == (byte) 0x00
					&& buffer[3] == (byte) 0x00) {
				encoding = "UTF_32LE";
			} else if (buffer[1] == (byte) 0x00 && buffer[2] == (byte) 0x3F
					&& buffer[3] == (byte) 0x00) {
				encoding = "UnicodeLittleUnmarked";
			} else if (buffer[1] == (byte) 0x3F && buffer[2] == (byte) 0x78
					&& buffer[3] == (byte) 0x6D) {
				encoding = "ASCII";
			}
			break;
		case (byte) 0xFE:
			if (buffer[1] == (byte) 0xFF) {
				encoding = "UnicodeBigUnmarked";
				ignoreBytes = 2;
			}
			break;
		case (byte) 0xEF:
			if (buffer[1] == (byte) 0xBB && buffer[2] == (byte) 0xBF) {
				encoding = "UTF8";
				ignoreBytes = 3;
			}
			break;
		case (byte) 0x4C:
			if (buffer[1] == (byte) 0x6F && buffer[2] == (byte) 0xA7
					&& buffer[3] == (byte) 0x94) {
				encoding = "CP037";
			}
			break;
		}
		if (encoding == null) {
			encoding = System.getProperty("file.encoding");
		}
		if (readEncoding) {
			read = in.read(buffer, 4, buffer.length - 4);
			Charset cs = Charset.forName(encoding);
			String s = new String(buffer, 4, read, cs);
			int pos = s.indexOf("encoding");
			if (pos == -1) {
				encoding = System.getProperty("file.encoding");
			} else {
				char delim;
				int start = s.indexOf(delim = '\'', pos);
				if (start == -1)
					start = s.indexOf(delim = '"', pos);
				//if (start == -1)
					//notifyEncodingError(buffer);
				int end = s.indexOf(delim, start + 1);
				//if (end == -1)
					//notifyEncodingError(buffer);
				encoding = s.substring(start + 1, end);
			}
		}

		//in.reset();
		//while (ignoreBytes-- > 0)
		//	in.read();
		return encoding;
	}

}
