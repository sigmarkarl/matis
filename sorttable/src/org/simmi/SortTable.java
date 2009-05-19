package org.simmi;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.DefaultRowSorter;
import javax.swing.JApplet;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.JViewport;
import javax.swing.RowFilter;
import javax.swing.RowSorter;
import javax.swing.ScrollPaneConstants;
import javax.swing.SortOrder;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.plaf.TableUI;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.simmi.RecipePanel.Recipe;
import org.simmi.RecipePanel.RecipeIngredient;

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
	RecipePanel 			recipe;
	JEditorPane				ed;
	
	ImagePanel				imgPanel;
	JComponent				graph;
	Image					img;
	
	TableModel 				model;
	TableModel 				topModel;
	
	DetailPanel 			detail;
	int						fInd = 1;
	
	Set<String>				cropped = new HashSet<String>();
	
	TableRowSorter<TableModel> 	tableSorter;
	TableRowSorter<TableModel>  leftTableSorter;
	MySorter			 	 	currentSorter;
	
	List<Object[]>			stuff;
	//List<Object[]>			header;
	Map<String,Integer>		ngroupMap;
	List<String>			ngroupList;
	List<String>			ngroupGroups;
	
	Map<String,Integer>	foodInd = new HashMap<String,Integer>();
	Map<String,Integer>	foodNameInd = new HashMap<String,Integer>();
	
	String					lang;
	boolean					hringur = false;
	
	RowFilter<TableModel,Integer>	filter;
	
	//static String lof = "org.jvnet.substance.skin.SubstanceRavenGraphiteLookAndFeel";
	static String lof = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
	static void updateLof() {
		try {
			UIManager.setLookAndFeel(lof);
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
	static {
		updateLof();
		System.setProperty("file.encoding", "UTF8");
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
			br = new BufferedReader( new InputStreamReader( inputStream, "UTF-8" ) );
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
		
		List<Object>[] nutList = new List[2];
		//List<Object>[]	nutList = nutList;
		for( int i = 0; i < nutList.length; i++ ) {
			List<Object> list = new ArrayList<Object>();
			nutList[i] = list;
			list.add( null );
			list.add( null );
		}
		ngroupMap = new HashMap<String,Integer>();
		ngroupList = new ArrayList<String>();
		ngroupGroups = new ArrayList<String>();
		
		if( loc.equals("IS") ) {
			inputStream = this.getClass().getResourceAsStream( "Component.txt" );
			br = new BufferedReader( new InputStreamReader( inputStream, "UTF-8" ) );
			line = br.readLine();
			int i = 0;
			while( line != null ) {
				String[] split = line.split("[\t]");
				if( split.length > 3 ) {
					String sName = null;
					if( split[4] != null && split[4].length() > 0 ) {
						sName = split[4];
					}
					String nName = split[3];
					ngroupMap.put( split[2], i++ );
					ngroupList.add( nName );// + " ("+split[1].substring(1, split[1].length()-1)+")" );
					ngroupGroups.add( split[8] );
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
					ngroupGroups.add( split[5] );
					//List<Object>	lobj = nutList.get(i).get(i)
					nutList[0].add( sName );
					String mName = split[1].substring(1, split[1].length()-1);
					nutList[1].add( mName );
				}
				line = br.readLine();
			}
		}
		
		List<Object[]>	result = new ArrayList<Object[]>();
		for( List l : nutList ) {
			result.add( l.toArray( new Object[0] ) );
		}
		
		int i = 0;
		int k = 0;
		if( loc.equals("IS") ) {
			inputStream = this.getClass().getResourceAsStream( "Food.txt" );
			br = new BufferedReader( new InputStreamReader( inputStream, "UTF-8" ) );
			line = br.readLine();
			while( line != null ) {
				String[] split = line.split("\\t");
				foodInd.put(split[1], k);
				foodNameInd.put(split[2], k);
				k++;
				
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
			br = new BufferedReader( new InputStreamReader( inputStream, "UTF-8" ) );
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
						if( f != -1.0f ) objs[ 2+ngroupOffset ] = f;
						else objs[ 2+ngroupOffset ] = null;
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

	String filterText;
	public void updateFilter() {
		//currentSorter = (MySorter)leftTableSorter;
		if( field.getText().length() > 0 ) {
			fInd = 1;
			filterText = "(?i).*"+field.getText()+".*";
			
			//leftTableSorter.modelStructureChanged();
			leftTableSorter.setRowFilter( filter ); //RowFilter.regexFilter("(?i)"+field.getText(), 1) );
			//tableSorter.modelStructureChanged();
			tableSorter.setRowFilter( filter );
			if( leftTable.getRowCount() == 0 ) {
				fInd = 0;
				leftTableSorter.setRowFilter( filter );
				tableSorter.setRowFilter( filter );
			}
		} else {
			filterText = null;
			leftTableSorter.setRowFilter( filter );
			tableSorter.setRowFilter( filter );
		}
		//table.tableChanged( new TableModelEvent( table.getModel() ) );
	}

	public class TreeTableCellRenderer extends JTree implements TableCellRenderer {
		protected int visibleRow;

		public void setBounds(int x, int y, int w, int h) {
			super.setBounds(x, 0, w, table.getHeight());
		}

		public void paint(Graphics g) {
			g.translate(0, -visibleRow * getRowHeight());
			super.paint(g);
		}

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			visibleRow = row;
			//( value.toString() );
			return this;
		}
	};
	 
	String sessionKey = null;
	String currentUser = null;
	public String lastResult;
	public void init() {
		try {
			UIManager.setLookAndFeel(lof);
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
		
		this.getContentPane().setBackground( Color.white );
		this.setBackground( Color.white );
		System.setProperty("file.encoding", "UTF8");
		
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
		
		try {
			sessionKey = SortTable.this.getParameter("fb_sig_session_key");
	        currentUser = SortTable.this.getParameter("fb_sig_user");
		} catch( Exception e ) {
			e.printStackTrace();
		}
		
		SwingUtilities.invokeLater( new Runnable(){
			@Override
			public void run() {		
				initGui( sessionKey, currentUser );
			}
		});
	}
	
	public void initGui( String sessionKey, String currentUser ) {
		SortTable.this.getRootPane().setBackground( Color.white );
		SortTable.this.requestFocus();
		
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
			public void columnAdded(TableColumnModelEvent e) {}

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
			public void columnSelectionChanged(ListSelectionEvent e) {}
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
		topTable.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );
		
		final CharBuffer	cb = CharBuffer.allocate(1000000);
		//ba.order( ByteOrder.LITTLE_ENDIAN );
		leftTable.getSelectionModel().addListSelectionListener( new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				int row = leftTable.getSelectedRow();
				if( row >= 0 && row < leftTable.getRowCount() ) {
					final String oStr = leftTable.getValueAt(row, 1).toString();
					final String str = oStr.replaceAll("[ ,]+", "+");
					//int row = e.getFirstIndex();
					if( tabbedPane.getSelectedComponent() == graph ) {
						graph.repaint();
					} else if( tabbedPane.getSelectedComponent() == detail ) {
						if( !str.equals(lastResult) ) {
							lastResult = str;
							imgPanel.img = null;
							imgPanel.repaint();
							//imgPanel.runThread( str );
							imgPanel.tryName( oStr );
						}
						detail.detailTable.tableChanged( new TableModelEvent( detail.detailModel ) );
					}
				}
			}
		});
		//System.err.println( leftTable.getColumnModel() );
		topLeftTable = new JTable() {
			public Component prepareRenderer( TableCellRenderer renderer, int row, int column ) {
				Component c = super.prepareRenderer( renderer, row, column );
				((JLabel)c).setHorizontalAlignment( SwingConstants.RIGHT );
				return c;
			}
		};
		topLeftTable.addMouseListener( new MouseAdapter() {
			public void mousePressed( MouseEvent e ) {
				/*TableModel old = table.getModel();
				TableModel oldTop = topTable.getModel();
				
				table.setModel( nullModel );
				topTable.setModel( nullModel );
				
				table.setModel( old );
				topTable.setModel( oldTop );
				
				topTable.tableChanged( new TableModelEvent( topTable.getModel() ) );
				table.tableChanged( new TableModelEvent( table.getModel() ) );
				
				topTable.revalidate();
				topTable.repaint();
				table.revalidate();
				table.repaint();*/
				
				/*if( e.getClickCount() == 2 ) {
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
				}*/
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
		
		final Image matisLogo;
		URL codeBase = null;
		try {
			codeBase = SortTable.this.getCodeBase();
		} catch( Exception e ) {
			
		}
		
		if( codeBase == null ) {
			Image img = null;
			
			try {
				URL url = new URL("http://test.matis.is/isgem/Matis_logo.jpg");
				img = ImageIO.read( url.openStream() );
			} catch (MalformedURLException e2) {
				e2.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			matisLogo = img;
		} else {
			matisLogo = SortTable.this.getImage( codeBase, "matis.png" );
		}
		
		/*JComponent logoPaint = new JComponent() {
			public void paintComponent( Graphics g ) {
				Graphics2D g2 = (Graphics2D)g;
				g2.setRenderingHint( RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY );
				g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
				g2.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC );
				g2.drawImage( matisLogo, 0, 0, this.getWidth(), this.getHeight(), this );
			}
		};*/
		
		//logoPaint.setPreferredSize( new Dimension( 32, 32 ) );
		topLeftComp.add( topLeftTable );
		//topLeftComp.add( logoPaint, BorderLayout.WEST );
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
		
		imgPanel = new ImagePanel( leftTable );
		tabbedPane = new JTabbedPane(JTabbedPane.BOTTOM, JTabbedPane.SCROLL_TAB_LAYOUT);
		tabbedPane.addChangeListener( new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				if( tabbedPane.getSelectedComponent() == rightSplitPane ) {
					leftScrollPane.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_NEVER );
					leftSplitPane.setDividerLocation(60);
				} else {
					leftScrollPane.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_ALWAYS );
					leftSplitPane.setDividerLocation(0);
				}
			}
			
		});
		
		FriendsPanel fp = new FriendsPanel( sessionKey, currentUser );
		RdsPanel rdsPanel = new RdsPanel( fp, SortTable.this );
		
		rightSplitPane = new LinkedSplitPane( JSplitPane.VERTICAL_SPLIT, topScrollPane, scrollPane );
		leftSplitPane = new LinkedSplitPane( JSplitPane.VERTICAL_SPLIT, topLeftScrollPane, leftComponent );
		rightSplitPane.setLinkedSplitPane( leftSplitPane );
		leftSplitPane.setLinkedSplitPane( rightSplitPane );
		
		leftSplitPane.setOneTouchExpandable( true );
		
		HabitsPanel eat = new HabitsPanel( lang );
		try {
			recipe = new RecipePanel( fp, lang, table, leftTable, foodNameInd );
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
		
		detail = new DetailPanel( rdsPanel, lang, imgPanel, table, topTable, leftTable, stuff, ngroupList, ngroupGroups, foodNameInd, recipe.recipes );
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
				int cc = detail.countVisible();
				return cc;
				//return stuff.get(0).length-2;
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
				int realColumnIndex = detail.convertIndex( columnIndex );
				return obj[ realColumnIndex+2 ];
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
				return stuff.size()-2 + recipe.recipes.size();
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				Object[]	obj = null;
				if( rowIndex < stuff.size()-2 ) {
					obj = stuff.get(rowIndex+2);
					if( columnIndex >= 0 ) return obj[ columnIndex ];
				}
				else {
					int r = rowIndex - (stuff.size()-2);
					if( r < recipe.recipes.size() ) {
						Recipe rep = recipe.recipes.get(r);
						if( columnIndex == 0 ) {
							return "Uppskrift - "+rep.group;
						} else {
							return rep.name + " - " + rep.author;
						}
					}
				}
				
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
		
		leftTable.addMouseListener( new MouseAdapter() {
			public void mousePressed( MouseEvent e ) {
				Point	p = e.getPoint();
				leftTable.requestFocus();
				if( e.getClickCount() == 2 ) {
					if( tabbedPane.getSelectedComponent() == recipe && leftTable.columnAtPoint(p) == 1 ) {
						if( recipe.currentRecipe != null ) {
							//recipe.currentRecipe.ingredients.add( new RecipePanel.RecipeIngredient() );
							
							recipe.currentRecipe.destroy();
							
							int r = leftTable.getSelectedRow();
							int rr = leftTable.convertRowIndexToModel( r );
							Object val = leftTable.getValueAt(r, 1);
							if( val != null ) {
								recipe.currentRecipe.addIngredient( val.toString(), 100, "g" );
							}
							recipe.recipeDetailTable.revalidate();
							recipe.recipeDetailTable.repaint();
							
							try {
								recipe.currentRecipe.save();
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}
					} else {
						int r = leftTable.getSelectedRow();
						int rr = leftTable.convertRowIndexToModel(r);
						
						String str = (String)leftModel.getValueAt(rr, 0);
						cropped.add(str);
						
						leftTableSorter.setRowFilter( filter );
						tableSorter.setRowFilter( filter );
						
						//tableSorter.sort();
						leftTableSorter.sort();
						//tableSorter.modelStructureChanged();
						//leftTableSorter.modelStructureChanged();
						//leftTable.tableChanged( new TableModelEvent( leftModel ) );						
					}
					/*int r = leftTable.getSelectedRow();
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
					}*/
				}
			}
		});
		
		//TreeTableCellRenderer treeCellRenderer = new TreeTableCellRenderer();
		//leftTable.getColumnModel().getColumn(0).setCellRenderer( treeCellRenderer );
		
		model = new TableModel() {
			//Object[] retObj = {};
			
			@Override
			public void addTableModelListener(TableModelListener l) {}

			@Override
			public Class<?> getColumnClass(int columnIndex) {
				return Float.class;
			}

			@Override
			public int getColumnCount() {
				int cc = topModel.getColumnCount();
				return cc;
			}

			@Override
			public String getColumnName(int columnIndex) {
				int realColumnIndex = detail.convertIndex( columnIndex );
				if( realColumnIndex != -1 ) return ngroupList.get(realColumnIndex);
				return null;
			}

			@Override
			public int getRowCount() {
				return stuff.size()-2 + recipe.recipes.size();
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				Object[]	obj = null;
				if( rowIndex < stuff.size()-2 ) {
					obj = stuff.get(rowIndex+2);
					int realColumnIndex = detail.convertIndex( columnIndex );
					if( realColumnIndex != -1 ) return obj[ realColumnIndex+2 ];
				} else {
					float ret = 0.0f;
					int i = rowIndex - (stuff.size()-2);
					Recipe rep = recipe.recipes.get(i);
					float tot = 0.0f;
					for( RecipeIngredient rip : rep.ingredients ) {
						if( foodNameInd.containsKey(rip.stuff) ) {
							int k = foodNameInd.get( rip.stuff );
							obj = stuff.get(k+2);
							int realColumnIndex = detail.convertIndex( columnIndex );
							if( realColumnIndex != -1 ) {
								Object		val = obj[ realColumnIndex+2 ];
								if( val != null && val instanceof Float ) {
									float d = rip.measure;
									if( !rip.unit.equals("g") ) {
										String ru = rip.unit;
										int f = ru.indexOf("(");
										int n = ru.indexOf(")");
										if( n > f && f != -1 ) {
											String subbi = ru.substring(f+1, n);
											if( subbi.endsWith("g") ) subbi = subbi.substring(0, subbi.length()-1);
											
											float fl = 0.0f;
											try {
												fl = Float.parseFloat( subbi );
											} catch( Exception e ) {
												
											}
											d *= fl;
										}
									}
									tot += d;
									
									float f = (((Float)val) * d) / 100.0f;
									ret += f;
								}
							}
						}
					}
					
					if( ret != 0.0f ) return (ret * 100.0f) / tot;
				}
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
		table.setModel( model );
		
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
		/*tableSorter.addRowSorterListener( new RowSorterListener() {
			@Override
			public void sorterChanged(RowSorterEvent e) {
				
			}
		});*/
		
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
				String gval = (String)leftModel.getValueAt( entry.getIdentifier(), 0 );
				String val = fInd == 0 ? gval : (String)leftModel.getValueAt( entry.getIdentifier(), 1 );
				if( filterText != null ) {			
					if( val != null ) return val.toString().matches( filterText );
					return false;
				} else {
					boolean b = cropped.contains( gval );
					if( b ) return false;
				}
				
				return true;
			}
		};
		tableSorter.setRowFilter( filter );
		
		graph = new GraphPanel( rdsPanel, lang, new JTable[] {table, leftTable, topTable}, model, topModel );
		
		if( lang.equals("IS") ) {
			tabbedPane.addTab( "Listi", rightSplitPane );
			//tabbedPane.addTab( "Myndir", imgPanel );
			tabbedPane.addTab( "Gröf", graph );
			tabbedPane.addTab( "Nánar", detail );
			tabbedPane.addTab( "Rds", rdsPanel );
			tabbedPane.addTab( "Uppskriftir", recipe );
			if( fp != null ) tabbedPane.addTab( "Vinir", fp );
			tabbedPane.addTab( "Mataræði og Hreyfing", eat );
			tabbedPane.addTab( "Innkaup og kostnaður", buy );
			
			tabbedPane.setEnabledAt( tabbedPane.getTabCount()-2, false );
			tabbedPane.setEnabledAt( tabbedPane.getTabCount()-1, false );
		} else {
			tabbedPane.addTab( "List", rightSplitPane );
			//tabbedPane.addTab( "Image", imgPanel );
			tabbedPane.addTab( "Graph", graph );
			tabbedPane.addTab( "Detail", detail );
			tabbedPane.addTab( "Rds", rdsPanel );
			tabbedPane.addTab( "Recipes", recipe );
			if( fp != null ) tabbedPane.addTab( "Friends", fp );
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
		
		ed = new JEditorPane();
		ed.setContentType("text/html");
		ed.setEditable( false );
		ed.setText("<html><body><center><table cellpadding=0><tr><td><img src=\"http://test.matis.is/isgem/Matis_logo.jpg\" hspace=\"5\" width=\"32\" height=\"32\">"
			+"</td><td align=\"center\"><a href=\"http://www.matis.is\">Matís ehf.</a> - Borgartún 21 | 105 Reykjavík - Sími 422 50 00 | Fax 422 50 01 - <a href=\"mailto:matis@matis.is\">matis@matis.is</a><br><a href=\"http://www.matis.is/ISGEM/is/skyringar/\">Hjálp</a> - "
			+((sessionKey != null && sessionKey.length() > 1)?"<a href=\"http://test.matis.is/isgem/applet.php\">Allur glugginn</a>":"<a href=\"http://apps.facebook.com/matisgem\">Facebook</a>")
			//+" - <a href=\"dark\">Dark</a> - <a href=\"light\">Light</a>"
			+"</td></tr></table></center></body></html>");
		Dimension d = new Dimension(1000,42);
		ed.setPreferredSize( d );
		ed.setSize( d );
		ed.addHyperlinkListener( new HyperlinkListener() {
			@Override
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
					if( e.getDescription().equals("dark") ) {
						lof = "org.jvnet.substance.skin.SubstanceRavenGraphiteLookAndFeel";
						updateLof();
					} else if( e.getDescription().equals("light") ) {
						lof = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
						updateLof();
					} else {
						try {
							Desktop.getDesktop().browse( e.getURL().toURI() );
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (URISyntaxException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}
			}
		});
		
		SortTable.this.setLayout( new BorderLayout() );
		splitPane.setBorder( new EmptyBorder(0, 0, 0, 0) );
		SortTable.this.add( splitPane );
		SortTable.this.add( ed, BorderLayout.SOUTH );
		splitPane.setDividerLocation( 1.0/3.0 );
		splitPane.setDividerLocation(300);
		//this.add( panel, BorderLayout.SOUTH );
		//this.add( field, BorderLayout.SOUTH );
		
		//splitPane.setBackground( Color.white );
		//tabbedPane.setBackground( Color.white );
		
		//scrollPane.setColumnHeaderView( topTable );
		//topScrollPane.setViewport( scrollPane.getColumnHeader() );
		
		//SwingUtilities.updateComponentTreeUI( this );
		
		SortTable.this.getContentPane().setBackground( Color.white );
		SortTable.this.setBackground( Color.white );
	}
	
	public JScrollPane getScrollPane() {
		return scrollPane;
	}
	
	public JSplitPane getSplitPane() {
		return splitPane;
	}
	
	public JEditorPane getEditor() {
		return ed;
	}
	
	public ImagePanel getImagePanel() {
		return imgPanel;
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
		
		final SortTable sortTable = new SortTable();
		
		try {
			UIManager.setLookAndFeel(lof);
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
		
		System.setProperty("file.encoding", "UTF8");
		sortTable.lang = "IS";
		
		ToolTipManager.sharedInstance().setInitialDelay(0);
		try {
			sortTable.stuff = sortTable.parseData( sortTable.lang );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sortTable.getContentPane().setBackground( Color.white );
		sortTable.setBackground( Color.white );
		
		SwingUtilities.invokeLater( new Runnable(){
			@Override
			public void run() {
				JFrame frame = new JFrame();
				frame.setBackground( Color.white );
				frame.getContentPane().setBackground( Color.white );
				sortTable.initGui( null, null );
				frame.setLayout( new BorderLayout() );
				frame.add( sortTable.getSplitPane() );
				frame.add( sortTable.getEditor(), BorderLayout.SOUTH );
				frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
				frame.setSize(800, 600);
				frame.setVisible( true );
			}
		});
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

	public void sortByColumn(String str) {
		DefaultRowSorter sorter = ((DefaultRowSorter)table.getRowSorter());
		List <RowSorter.SortKey> sortKeys = new ArrayList<RowSorter.SortKey>();
		int c = -1;
		System.err.println( str );
		while( c < model.getColumnCount() && !model.getColumnName(++c).contains(str) );
		if( c < model.getColumnCount() ) {
			sortKeys.add(new RowSorter.SortKey(c, SortOrder.DESCENDING));
			sorter.setSortKeys(sortKeys);
		}
	}
}
