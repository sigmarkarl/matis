package org.simmi;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Window;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jdesktop.swingx.JXDatePicker;

public class StarlimsReport extends JApplet {
	Map<String,Date>		invset;
	List<Folder>			folderList;
	Map<String,Integer>		folderMap = new HashMap<String,Integer>();
	List<Order>				orderList;
	JSplitPane				splitPane;
	JTable					table;
	JTable					resultTable;
	TableModel				model;
	TableModel				resultModel;
	Connection				con;
	Color					bgcolor;
	//Display 				display = null;
	JComboBox				combo1;
	JComboBox				combo2;
	JXDatePicker			datepicker1;
	JXDatePicker			datepicker2;
	//Map<String,Map<String,Result>>		results = new HashMap<String,Map<String,Result>>();
	
	public class Folder {
		String	name;
		String	client;
		String	id;
		String	address;
		String	city;
		String	project;
		Date	date;
		Date 	invoiced;
		
		public Folder( String name ,String client, String id, String address, String city, String project, Date sqldate, Date b ) {
			this.name = name;
			this.client = client;
			this.id = id;
			this.address = address;
			this.city = city;
			this.project = project;
			this.date = sqldate; //new java.util.Date( sqldate.getTime() );
			this.invoiced = b;
		}
	}
	
	public class Order {
		String	ordno;
		String	spec;
		Double	price;
		String	testcode;
		Boolean	e_report;
		Integer	f_i;
		
		public Order( String ordno, String spec, double price, String testcode, int f_i ) {
			this.ordno = ordno;
			this.spec = spec;
			this.price = price;
			this.testcode = testcode;
			this.f_i = f_i;
			this.e_report = true;
		}
	}
	
	public class Result {
		String	spec;
		Double	price;
		Integer	f_i;
		
		public Result( String spec, int f_i, double price ) {
			this.spec = spec;
			this.price = price;
			this.f_i = f_i;
		}
	}
	
	public static void main(String[] args) {
		/*StarlimsReport	slrp = new StarlimsReport();
		
		Display display = new Display();
		Shell shell = new Shell( display );
		shell.setSize(800, 600);
		
		Composite composite = new Composite(shell, SWT.EMBEDDED | SWT.PUSH);
		
		Frame frame = SWT_AWT.new_Frame( composite );
		slrp.initGUI( frame );
		
		FillLayout fillLayout = new FillLayout();
		fillLayout.type = SWT.VERTICAL;
		shell.setLayout( fillLayout );
		
		Label label = new Label(shell, SWT.PUSH);
		label.setSize(800, 50);
		label.setText( "Simmi" );
		
		shell.layout();
		shell.pack();
		shell.open ();
		while (!shell.isDisposed ()) {
			if (!display.readAndDispatch ()) display.sleep ();
		}
		display.dispose ();*/
	}
	
	public void init() {
		initGUI( this );
	}
	
	public XSSFWorkbook loadXlsx( String path ) throws IOException {
		XSSFWorkbook workbook = new XSSFWorkbook( path );
		
		//Map<String, CellStyle> styles = createStyles(workbook);
		
		Map<String,Map<String,Result>>	results = new HashMap<String,Map<String,Result>>();
		for( int r = 0; r < resultTable.getRowCount(); r++ ) {
			Integer fi = (Integer)resultTable.getValueAt(r, 5);
			String  ordno = (String)resultTable.getValueAt(r, 0);
			String  spec = (String)resultTable.getValueAt(r, 1);
			Double	price = (Double)resultTable.getValueAt(r, 2);
			Boolean include = (Boolean)resultTable.getValueAt(r, 4);
			
			if( include ) {
				Folder folder = folderList.get(fi);
				
				Map<String,Result> subres = null;
				String key = folder.client + "/" + folder.project;
				if( !results.containsKey( key ) ) {
					subres = new HashMap<String,Result>();
					results.put( key, subres );
				} else subres = results.get( key );
				
				Result res = null;
				if( !subres.containsKey( ordno ) ) {
					res = new Result( spec, fi, price );
					subres.put( ordno, res );
				} else res = subres.get( ordno );
				
				res.price += price;
			}
		}
		//Map<String,Result> subres = new HashMap<String,Result>();
		int start = 0;
		for( String sheetname : results.keySet() ) {
			Map<String,Result> subres = results.get( sheetname );
			
			Result rr = null;
			for( String val : subres.keySet() ) {
				rr = subres.get(val);
				break;
			}
			
			Folder ff = null;
			if( rr != null ) {
				ff = folderList.get( rr.f_i );
			}
			
			String[] split = sheetname.split("/");
			
			String client = split[0];
			String project = "project";
			if( split.length > 1 ) project = split[1];
			String address = ff.address;
			String id = ff.id;
			String city = ff.city;
			
			XSSFSheet 	sheet = workbook.getSheet("Sheet1");
			//workbook.create
			//XSSFRow		row = sheet.createRow( 0 );
			XSSFRow row = sheet.getRow( start+1 );
			if( row == null ) row = sheet.createRow( start+1 );
			
			XSSFCell cell = row.getCell(4);
			if( cell == null ) {
				cell = row.createCell( 4 );
			}
			cell.setCellValue("Frá:");
			
			cell = row.getCell(5);
			if( cell == null ) cell = row.createCell( 5 );
			if( datepicker1.getDate() != null ) cell.setCellValue( datepicker1.getDate().toString() );
			
			row = sheet.getRow( start+2 );
			if( row == null ) row = sheet.createRow( start+2 );
			
			cell = row.getCell(4);
			if( cell == null ) {
				cell = row.createCell( 4 );
			}
			cell.setCellValue("Til:");
			
			cell = row.getCell( 5 );
			if( cell == null ) cell = row.createCell( 5 );
			if( datepicker2.getDate() != null ) cell.setCellValue( datepicker2.getDate().toString() );
			
			row = sheet.getRow( start+3 );
			if( row == null ) row = sheet.createRow( start+3 );
			cell = row.getCell(1);
			if( cell == null ) cell = row.createCell(1);
			cell.setCellValue( id );
			
			row = sheet.getRow( start+4 );
			if( row == null ) row = sheet.createRow( start+4 );
			cell = row.getCell(1);
			if( cell == null ) cell = row.createCell(1);
			cell.setCellValue( client );
			
			row = sheet.getRow( start+5 );
			if( row == null ) row = sheet.createRow( start+5 );
			cell = row.getCell(1);
			if( cell == null ) cell = row.createCell(1);
			cell.setCellValue( project );
			
			row = sheet.getRow( start+6 );
			if( row == null ) row = sheet.createRow( start+6 );
			cell = row.getCell(1);
			if( cell == null ) cell = row.createCell(1);
			cell.setCellValue( address );
			
			row = sheet.getRow( start+7 );
			if( row == null ) row = sheet.createRow( start+7 );
			cell = row.getCell(1);
			if( cell == null ) cell = row.createCell(1);
			cell.setCellValue( city );
			
			row = sheet.getRow( start+9 );
			if( row == null ) row = sheet.createRow( start+9 );
			String[] strs = { "Reikn", "Sýnatökunr", "Mótt.dags", "Afgr.dags", "Flokkur", "Aths.", "Upphæð Kr."};
			int i = 0;
			XSSFCellStyle	style = null;
			for( String s : strs ) {
				cell = row.getCell( i );
				if( cell == null ) cell = row.createCell( i );
				cell.setCellValue(s);
				/*if( style == null ) {
					style = (XSSFCellStyle)cell.getCellStyle().clone();
					style.setFont( style.c)
				}
				cell.setCellStyle( style );*/
				i++;
			}
			
			/*row = sheet.getRow( start+6 );
			i = 1;
			clients.clear();
			for( int r : rr ) {
				clients.add( (String)table.getValueAt(r, 3) );
			}
			
			for( String client : clients ) {
				cell = row.getCell(i);
				if( cell == null ) cell = row.createCell(i);
				i++;
				cell.setCellValue( client );
			}
			
			row = sheet.getRow( start+7 );
			i = 1;
			clients.clear();
			for( int r : rr ) {
				clients.add( (String)table.getValueAt(r, 4) );
			}
			
			for( String client : clients ) {
				cell = row.getCell(i);
				if( cell == null ) cell = row.createCell(i);
				i++;
				cell.setCellValue( client );
			}*/
			
			int r = start+10;
			double total = 0.0;
			for( String restr : subres.keySet() ) {
				Result res = subres.get( restr );
				
				row = sheet.createRow( r );
				
				cell = row.getCell(1);
				if( cell == null ) cell = row.createCell(1);
				cell.setCellValue( restr );						
				
				cell = row.getCell(2);
				if( cell == null ) cell = row.createCell(2);
				cell.setCellValue( folderList.get( res.f_i ).date.toString() );
				
				cell = row.getCell(3);
				if( cell == null ) cell = row.createCell(3);
				cell.setCellValue( folderList.get( res.f_i ).invoiced.toString() );
				
				cell = row.getCell(4);
				if( cell == null ) cell = row.createCell(4);
				cell.setCellValue( res.spec );
				
				cell = row.getCell(6);
				if( cell == null ) cell = row.createCell(6);
				cell.setCellValue( res.price );
				
				total += res.price;
				
				r++;
			}
			
			start = r + 1;
			
			row = sheet.getRow( start );
			if( row == null ) row = sheet.createRow( start );
			cell = row.getCell(4);
			if( cell == null ) {
				cell = row.createCell(4);			
			}
			cell.setCellValue( "Samtals reikningsfært" );
			
			cell = row.getCell(6);
			if( cell == null ) {
				cell = row.createCell(6);			
			}
			cell.setCellValue( total );
			
			start += 2;
		}
		
		return workbook;
	}
	
	public void initGUI( Container cont ) {
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
		
		bgcolor = new Color( 255,255,255 );
		Window window = SwingUtilities.windowForComponent(this);
		if (window instanceof JFrame) {
			JFrame frame = (JFrame)window;
			if (!frame.isResizable()) frame.setResizable(true);
		}
		
		this.getContentPane().setBackground( bgcolor );
		this.setBackground( bgcolor );
		System.setProperty("file.encoding", "UTF8");
		
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			String connectionUrl = "jdbc:sqlserver://navision.rf.is:1433;databaseName=MATIS;user=simmi;password=drsmorc.311;";
			con = DriverManager.getConnection(connectionUrl);
			loadInvoice();
			load();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		model = createModel( folderList );
		
		table = new JTable();
		table.setAutoCreateRowSorter( true );
		JScrollPane	scrollPane = new JScrollPane( table );
		table.setModel( model );
		
		final RowFilter<Object, Object> filter = new RowFilter<Object, Object>() {
		      public boolean include(Entry entry) {
		    	  TableModel	model = (TableModel)entry.getModel();
		    	  //System.err.println( "ok " + entry.getIdentifier() );
		    	  
		    	  if( combo1 != null ) {
			    	  Object project = model.getValueAt( (Integer)entry.getIdentifier(), 5);
			    	  Object combsel = combo2.getSelectedItem();
			    	  
			    	  Date date = ((Date)model.getValueAt( (Integer)entry.getIdentifier(), 7));
			    	  //System.err.println( "date " + date );			    	  
			    	  
			    	  boolean d1 = datepicker1.getDate() == null || date != null && date.getTime() >= datepicker1.getDate().getTime();
			    	  boolean d2 = datepicker2.getDate() == null || date != null && date.getTime() <= datepicker2.getDate().getTime();
			    	  
			    	  Object selit = combo1.getSelectedItem();
			    	  
			    	  if( model.getValueAt( (Integer)entry.getIdentifier(), 7 ) != null && 
			    			  (selit.equals("Allir") || 
			    					  (selit.equals( model.getValueAt( (Integer)entry.getIdentifier(), 1) ) &&
			    							  combsel != null && combsel.equals( project ) ) ) && d1 && d2 ) return true;
			    	  
			    	  return false;
		    	  }
		        //Integer population = (Integer) entry.getValue(1);
		        //return population.intValue() > 3;
		    	  
		    	  return true;
		      }
		};
		
		final TableRowSorter<TableModel>	sorter = (TableRowSorter<TableModel>)table.getRowSorter();
		sorter.setRowFilter(filter);
		
		splitPane = new JSplitPane();
		splitPane.setLeftComponent( scrollPane );
		
		resultTable = new JTable();
		resultTable.setAutoCreateRowSorter( true );
		scrollPane = new JScrollPane( resultTable );
		splitPane.setRightComponent( scrollPane );
		
		table.getSelectionModel().addListSelectionListener( new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				int r = table.getSelectedRow();
				if( r >= 0 && r < table.getRowCount() ) {
					try {
						loadResults( (String)table.getValueAt( r, 0 ) );
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				
				//System.err.println( resultTable.getDefaultEditor( Boolean.class ) );
			}
		});
		
		JComponent buttons = new JComponent() {
			
		};
		buttons.setLayout( new BorderLayout() );
		
		JButton	load = new JButton( new AbstractAction("Load") {
			@Override
			public void actionPerformed(ActionEvent e) {
				int[] rr = table.getSelectedRows();
				
				try {
					Set<String>	folders = new HashSet<String>();
					for( int r : rr ) {
						String folder = (String)table.getValueAt(r, 0);
						folders.add( folder );
					}
					loadAllResults( folders );
					/*for( int r : rr ) {
						String folder = (String)table.getValueAt(r, 0);
						loadResults( folder );
					}*/
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				if( orderList != null && orderList.size() > 0 ) {
					resultTable.setModel( createModel( orderList ) );
				}
				
			}
		});
		
		TransferHandler th = new TransferHandler() {
			@Override
			public int getSourceActions(JComponent c) {
				return TransferHandler.COPY_OR_MOVE;
			}
	
			@Override
			public boolean canImport(TransferHandler.TransferSupport support) {
				return true;
			}
	
			@Override
			protected Transferable createTransferable(JComponent c) {
				return new Transferable() {
	
					@Override
					public Object getTransferData(DataFlavor arg0)
							throws UnsupportedFlavorException, IOException {
						if (arg0 == DataFlavor.getTextPlainUnicodeFlavor()) {
							return null;
						} else {
							return null;
						}
					}
	
					@Override
					public DataFlavor[] getTransferDataFlavors() {
						return new DataFlavor[] { DataFlavor.getTextPlainUnicodeFlavor() };
					}
	
					@Override
					public boolean isDataFlavorSupported(DataFlavor arg0) {
						if (arg0 == DataFlavor.getTextPlainUnicodeFlavor()) {
							return true;
						}
						return false;
					}
				};
			}
	
			@Override
			public boolean importData(TransferHandler.TransferSupport support) {
				Object obj = null;
	
				int b = Arrays.binarySearch(support.getDataFlavors(),
						DataFlavor.javaFileListFlavor,
						new Comparator<DataFlavor>() {
							@Override
							public int compare(DataFlavor o1, DataFlavor o2) {
								return o1 == o2 ? 1 : 0;
							}
						});
				System.err.println(b);
				if (b != -1) {
					try {
						obj = support.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
					} catch (UnsupportedFlavorException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
	
				try {
					if (obj != null && obj instanceof List) {
						List<File>	l = (List<File>)obj;
						File f = l.get(0);
						XSSFWorkbook workbook = loadXlsx( f.getCanonicalPath() );
						
						int i = 0;
						File nf = File.createTempFile("tmp", ".xlsx");
						workbook.write( new FileOutputStream( nf ) );
						Desktop.getDesktop().open( nf );
					} else {
						if( true ) {
							obj = support.getTransferable().getTransferData(DataFlavor.stringFlavor);
							if ( obj != null ) {
								String stuff = obj.toString();
								if (stuff.contains("file://")) {
									URL url = new URL(stuff);
									File f = new File( URLDecoder.decode( url.getFile(), "UTF-8" ) );
									//URLDecoder.decode( f, "UTF-8" )
									//XSSFWorkbook	wb = new XSSFWorkbook();
									XSSFWorkbook workbook = loadXlsx( f.getCanonicalPath() );
									int i = 0;
									File nf = File.createTempFile("tmp", ".xlsx");
									FileOutputStream fos = new FileOutputStream( nf ); 
									workbook.write( fos );
									fos.close();
									Desktop.getDesktop().open( nf );
								}
							}
						}
					}
				} catch (UnsupportedFlavorException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
	
				return true;
			}
		};
		
		JComponent reportComp = new JComponent() {};
		reportComp.setLayout( new FlowLayout() );
		
		InputStream inputStream = this.getClass().getResourceAsStream( "/xlsx.png" );
		try {
			final BufferedImage img = ImageIO.read( inputStream );
			JComponent dd = new JComponent() {
				public void paintComponent( Graphics g ) {
					super.paintComponent( g );
					if( img != null ) g.drawImage( img, 0, 0, this.getWidth(), this.getHeight(), this );
				}
			};
			dd.setTransferHandler( th );
			dd.setPreferredSize( new Dimension(30,30) );
			reportComp.add( dd );
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		JButton	report = new JButton( new AbstractAction("Report") {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					File nf = File.createTempFile("tmp", ".xlsx");
					InputStream inputStream = this.getClass().getResourceAsStream( "/erm.xlsx" );
					FileOutputStream fos = new FileOutputStream( nf );
					
					byte[] bb = new byte[1024];
					int read = inputStream.read(bb);
					while( read > 0 ) {
						fos.write( bb, 0, read );
						read = inputStream.read( bb );
					}
					fos.close();
					
					//XSSFWorkbook workbook = new XSSFWorkbook( "/home/sigmar/workspace/starlimsreports/src/Book1.xlsx" );
					XSSFWorkbook workbook = loadXlsx( nf.getAbsolutePath() ); 
					
					/*new XSSFWorkbook( nf.getAbsolutePath() );
					
					//Map<String, CellStyle> styles = createStyles(workbook);
					
					XSSFSheet 	sheet = workbook.getSheet("Sheet1");
					XSSFRow		row = sheet.createRow( 0 );
					row = sheet.createRow( 1 );
					XSSFCell	cell = row.createCell(1);
					cell.setCellValue( "Yfirlit yfir sýni rannsökuð" );
					
					cell = row.createCell(3);
					cell.setCellValue("Frá:");
					
					cell = row.createCell(4);
					if( datepicker1.getDate() != null ) cell.setCellValue( datepicker1.getDate().toString() );
					
					cell = row.createCell(5);
					cell.setCellValue("Til:");
					
					cell = row.createCell(6);
					if( datepicker2.getDate() != null ) cell.setCellValue( datepicker2.getDate().toString() );
					
					row = 	sheet.createRow( 2 );
					row = sheet.createRow( 3 );
					int i = 1;
					Set<String>	clients = new HashSet<String>();
					int[] rr = table.getSelectedRows();
					for( int r : rr ) {
						clients.add( (String)table.getValueAt(r, 2) );
					}
					
					for( String client : clients ) {
						cell = row.createCell(i++);
						cell.setCellValue( client );
					}
					
					row = sheet.createRow( 4 );
					i = 1;
					clients.clear();
					for( int r : rr ) {
						clients.add( (String)table.getValueAt(r, 1) );
					}
					
					for( String client : clients ) {
						cell = row.createCell(i++);
						cell.setCellValue( client );
					}
					
					row = sheet.createRow( 5 );
					i = 1;
					Set<String>	projects = new HashSet<String>();
					rr = table.getSelectedRows();
					for( int r : rr ) {
						projects.add( (String)table.getValueAt(r, 5) );
					}
					
					for( String project : projects ) {
						cell = row.createCell(i++);
						cell.setCellValue( project );
					}
					
					row = sheet.createRow( 6 );
					i = 1;
					clients.clear();
					for( int r : rr ) {
						clients.add( (String)table.getValueAt(r, 3) );
					}
					
					for( String client : clients ) {
						cell = row.createCell(i++);
						cell.setCellValue( client );
					}
					
					row = sheet.createRow( 7 );
					i = 1;
					clients.clear();
					for( int r : rr ) {
						clients.add( (String)table.getValueAt(r, 4) );
					}
					
					for( String client : clients ) {
						cell = row.createCell(i++);
						cell.setCellValue( client );
					}
					
					row = sheet.createRow( 9 );
					String[] strs = { "Reikn", "Sýnatökunr", "Mótt.dags", "Afgr.dags", "Flokkur", "Aths.", "Upphæð Kr."};
					i = 0;
					XSSFCellStyle	style = null;
					for( String s : strs ) {
						cell = row.createCell(i);
						cell.setCellValue(s);
						/*if( style == null ) {
							style = (XSSFCellStyle)cell.getCellStyle().clone();
							style.setFont( style.c)
						}
						cell.setCellStyle( style );*
						i++;
					}
					
					int r = 10;
					for( Order o : orderList ) {
						row = sheet.createRow( r );
						
						cell = row.createCell(1);
						cell.setCellValue( o.ordno );						
						
						cell = row.createCell(2);
						//cell.setCellType( XSSFCell.)
						//cell.setCellValue( new java.util.Date( folderList.get( o.f_i ).date.getTime() ) );
						cell.setCellValue( folderList.get( o.f_i ).date.toString() );
						
						cell = row.createCell(3);
						cell.setCellValue( folderList.get( o.f_i ).invoiced.toString() );
						
						cell = row.createCell(4);
						cell.setCellValue( o.testcode );
						
						cell = row.createCell(6);
						cell.setCellValue( o.price );
						
						r++;
					}*/
				
					File tnf = File.createTempFile("tmp", ".xlsx");
					fos = new FileOutputStream( tnf );
					workbook.write( fos );
					fos.close();
					Desktop.getDesktop().open( tnf );
				} catch (IOException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
			}
		});
		
		reportComp.add( report );
		
		//final Canvas canvas = new Canvas();
		//canvas.setBounds(0, 0, 100, 100);
		//canvas.setBackground( Color.gray );
		//buttons.add( canvas );
		
		JComponent comp = new JComponent() {
			
		};
		comp.setLayout( new FlowLayout() );
		
		comp.add( new JLabel("Client:") );
		
		final Map<String,Set<String>> clients = new HashMap<String,Set<String>>();
		for( Folder f : folderList ) {
			Set<String>	set = null;
			if( !clients.containsKey( f.client ) ) {
				set = new HashSet<String>();
				clients.put( f.client, set );
			} else set = clients.get( f.client );
			
			set.add( f.project );
		}
		
		combo2 = new JComboBox();
		combo1 = new JComboBox();
		combo1.addItem("Allir");
		for( String key : clients.keySet() ) {
			combo1.addItem( key );
		}
		combo1.addItemListener( new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				combo2.removeAllItems();
				if( clients.containsKey( e.getItem() ) ) {
					Set<String> ss = clients.get( e.getItem() );
					for( String s : ss ) {
						combo2.addItem( s );
					}
				}
				sorter.setRowFilter( filter );
			}
		});
		combo2.addItemListener( new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				sorter.setRowFilter( filter );
			}
		});
		comp.add( combo1 );
		comp.add( new JLabel("Project:") );
		comp.add( combo2 );
		
		comp.add( new JLabel("From:") );
		datepicker1 = new JXDatePicker();
		comp.add( datepicker1 );
		comp.add( new JLabel("To:") );
		datepicker2 = new JXDatePicker();
		comp.add( datepicker2 );
		
		datepicker1.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sorter.setRowFilter( filter );
			}
		});
		
		datepicker2.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sorter.setRowFilter( filter );
			}
		});
		
		buttons.add( comp );
		buttons.add( load, BorderLayout.WEST );
		buttons.add( reportComp, BorderLayout.EAST );
		
		cont.setLayout( new BorderLayout() );
		cont.add( splitPane );
		cont.add( buttons, BorderLayout.SOUTH );
		
		//canvas.addNotify();
		
		/*SwingUtilities.invokeLater( new Runnable() {
			@Override
			public void run() {
				display = new Display();
				//display = Display.getCurrent();
				Shell	shell = SWT_AWT.new_Shell( display, canvas );
				shell.setLayout( new FillLayout() );
				shell.setBackground(new org.eclipse.swt.graphics.Color(null, 100,0,50));
				shell.setSize(50, 50);
				
				Label label = new Label(shell, SWT.None);
				label.setText( "Simmi" );
				
				shell.layout();
				//shell.pack();
				//shell.open();
				
				StarlimsReport.this.validate();
				canvas.validate();
				canvas.repaint();
			}
		});*/
				
		/*JComponent	comp = new JComponent() {
			
		};
		comp.setLayout( new FlowLayout() );
		comp.add( new JLabel("From:") );
		comp.add( new )
		comp.add( new JLabel("To:") );*/
	}
	
	/*private static Map<String, CellStyle> createStyles(Workbook wb){
        Map<String, CellStyle> styles = new HashMap<String, CellStyle>();
        DataFormat df = wb.createDataFormat();

        CellStyle style;
        Font headerFont = wb.createFont();
        headerFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        style = createBorderedStyle(wb);
        style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        style.setFont(headerFont);
        styles.put("header", style);

        style = createBorderedStyle(wb);
        style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        style.setFont(headerFont);
        style.setDataFormat(df.getFormat("d-mmm"));
        styles.put("header_date", style);

        Font font1 = wb.createFont();
        font1.setBoldweight(Font.BOLDWEIGHT_BOLD);
        style = createBorderedStyle(wb);
        style.setAlignment(CellStyle.ALIGN_LEFT);
        style.setFont(font1);
        styles.put("cell_b", style);

        style = createBorderedStyle(wb);
        style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setFont(font1);
        styles.put("cell_b_centered", style);

        style = createBorderedStyle(wb);
        style.setAlignment(CellStyle.ALIGN_RIGHT);
        style.setFont(font1);
        style.setDataFormat(df.getFormat("d-mmm"));
        styles.put("cell_b_date", style);

        style = createBorderedStyle(wb);
        style.setAlignment(CellStyle.ALIGN_RIGHT);
        style.setFont(font1);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        style.setDataFormat(df.getFormat("d-mmm"));
        styles.put("cell_g", style);

        Font font2 = wb.createFont();
        font2.setColor(IndexedColors.BLUE.getIndex());
        font2.setBoldweight(Font.BOLDWEIGHT_BOLD);
        style = createBorderedStyle(wb);
        style.setAlignment(CellStyle.ALIGN_LEFT);
        style.setFont(font2);
        styles.put("cell_bb", style);

        style = createBorderedStyle(wb);
        style.setAlignment(CellStyle.ALIGN_RIGHT);
        style.setFont(font1);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        style.setDataFormat(df.getFormat("d-mmm"));
        styles.put("cell_bg", style);

        Font font3 = wb.createFont();
        font3.setFontHeightInPoints((short)14);
        font3.setColor(IndexedColors.DARK_BLUE.getIndex());
        font3.setBoldweight(Font.BOLDWEIGHT_BOLD);
        style = createBorderedStyle(wb);
        style.setAlignment(CellStyle.ALIGN_LEFT);
        style.setFont(font3);
        style.setWrapText(true);
        styles.put("cell_h", style);

        style = createBorderedStyle(wb);
        style.setAlignment(CellStyle.ALIGN_LEFT);
        style.setWrapText(true);
        styles.put("cell_normal", style);

        style = createBorderedStyle(wb);
        style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setWrapText(true);
        styles.put("cell_normal_centered", style);

        style = createBorderedStyle(wb);
        style.setAlignment(CellStyle.ALIGN_RIGHT);
        style.setWrapText(true);
        style.setDataFormat(df.getFormat("d-mmm"));
        styles.put("cell_normal_date", style);

        style = createBorderedStyle(wb);
        style.setAlignment(CellStyle.ALIGN_LEFT);
        style.setIndention((short)1);
        style.setWrapText(true);
        styles.put("cell_indented", style);

        style = createBorderedStyle(wb);
        style.setFillForegroundColor(IndexedColors.BLUE.getIndex());
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        styles.put("cell_blue", style);

        return styles;
    }

    private static CellStyle createBorderedStyle(Workbook wb){
        CellStyle style = wb.createCellStyle();
        style.setBorderRight(CellStyle.BORDER_THIN);
        style.setRightBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderBottom(CellStyle.BORDER_THIN);
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderLeft(CellStyle.BORDER_THIN);
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderTop(CellStyle.BORDER_THIN);
        style.setTopBorderColor(IndexedColors.BLACK.getIndex());
        return style;
    }*/
    
    private TableModel createTestModel() {
    	TableModel	model = new TableModel() {

			@Override
			public void addTableModelListener(TableModelListener l) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public Class<?> getColumnClass(int columnIndex) {				
				return Boolean.class;
			}

			@Override
			public int getColumnCount() {
				return 1;
			}

			@Override
			public String getColumnName(int columnIndex) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public int getRowCount() {
				return 50;
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				return Boolean.TRUE;
			}

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return true;
			}

			@Override
			public void removeTableModelListener(TableModelListener l) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
				
			}
			
		};
		
		return model;
    }
	
	public TableModel createModel( final List<?> datalist ) {
		final Class cls = datalist.get(0).getClass();
		
		//return createTestModel();
		
		return new TableModel() {
			@Override
			public void addTableModelListener(TableModelListener l) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public Class<?> getColumnClass(int columnIndex) {
				return cls.getDeclaredFields()[columnIndex].getType();
			}

			@Override
			public int getColumnCount() {
				int cc = cls.getDeclaredFields().length-1;
				return cc;
			}

			@Override
			public String getColumnName(int columnIndex) {
				return cls.getDeclaredFields()[columnIndex].getName();
			}

			@Override
			public int getRowCount() {
				return datalist.size();
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				Object ret = null;
				try {
					Field f = cls.getDeclaredFields()[columnIndex];
					ret = f.get( datalist.get(rowIndex) );
					
					if( ret != null && ret.getClass() != f.getType() ) {
						System.err.println( ret.getClass() + "  " + f.getType() );
						ret = null;
					}
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return ret;
			}

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return cls.getDeclaredFields()[columnIndex].getName().startsWith( "e_" );
			}

			@Override
			public void removeTableModelListener(TableModelListener l) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
				Object o = datalist.get( rowIndex );
				Field f = cls.getDeclaredFields()[columnIndex];
				try {
					f.set( o, aValue );
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
	}
	
	public void loadInvoice() throws SQLException {
		String sql = "select f.FOLDERNO, f.[INVOICE_DATETIME] from [srv-starlimsdat].[PROD_UPG_DATA].[dbo].[NL_INVOICES] f";
		
		PreparedStatement 	ps = con.prepareStatement(sql);
		ResultSet 			rs = ps.executeQuery();

		invset = new HashMap<String,Date>();
		while (rs.next()) {
			invset.put( rs.getString(1), rs.getDate(2) );
		}
		rs.close();
		ps.close();
	}
	
	public void loadResult( String folderno ) throws SQLException {
		String sql = "select ot.[ORDNO], sp.PROGNAME, mi.[Unit Price], mi.[Description] from [srv-starlimsdat].[PROD_UPG_DATA].[dbo].[ORDTASK] ot, [srv-starlimsdat].[PROD_UPG_DATA].[dbo].[NL_TESTS_ARTICLES] ta, [MATIS].[dbo].[Matís ohf_$Item] mi, [srv-starlimsdat].[PROD_UPG_DATA].[dbo].[SAMPLE_PROGRAMS] sp where sp.[SP_CODE] = ot.[SP_CODE] and ot.[ORDNO] like '"+folderno+"%'"
			+" and ta.[TESTCODE] = ot.[TESTCODE] and convert(int,mi.[No_]) = convert(int,ta.[ARTICLENO])";
		
		PreparedStatement 	ps = con.prepareStatement(sql);
		ResultSet 			rs = ps.executeQuery();

		if( orderList == null ) orderList = new ArrayList<Order>();
		while (rs.next()) {
			orderList.add( new Order( rs.getString(1), rs.getString(2), rs.getDouble(3), rs.getString(4), folderMap.get(folderno) ) );
		}
		rs.close();
		ps.close();
	}
	
	public void loadAllResults( Set<String> folders ) throws SQLException {
		String folderstr = "(";
		
		int i = 0;
		for( String folder : folders ) {
			i++;
			if( i == folders.size() ) folderstr += "'"+folder+"'";
			else folderstr += "'"+folder+"',";
		}
		folderstr += ")";
		
		String sql = "select ot.[ORDNO], sp.PROGNAME, mi.[Unit Price], mi.[Description], ot.[FOLDERNO] from [srv-starlimsdat].[PROD_UPG_DATA].[dbo].[ORDTASK] ot, [srv-starlimsdat].[PROD_UPG_DATA].[dbo].[NL_TESTS_ARTICLES] ta, [MATIS].[dbo].[Matís ohf_$Item] mi, [srv-starlimsdat].[PROD_UPG_DATA].[dbo].[SAMPLE_PROGRAMS] sp where sp.[SP_CODE] = ot.[SP_CODE] and ot.[FOLDERNO] in "+folderstr
			+" and ta.[TESTCODE] = ot.[TESTCODE] and convert(int,mi.[No_]) = convert(int,ta.[ARTICLENO])";
		
		PreparedStatement 	ps = con.prepareStatement(sql);
		ResultSet 			rs = ps.executeQuery();

		orderList = new ArrayList<Order>();
		while (rs.next()) {
			orderList.add( new Order( rs.getString(1), rs.getString(2), rs.getDouble(3), rs.getString(4), folderMap.get(rs.getString(5)) ) );
		}
		rs.close();
		ps.close();
		
		if( orderList.size() > 0 ) {
			resultTable.setModel( createModel( orderList ) );
		}
	}
	
	public void loadResults( String folderno ) throws SQLException {
		String sql = "select ot.[ORDNO], sp.PROGNAME, mi.[Unit Price], mi.[Description] from [srv-starlimsdat].[PROD_UPG_DATA].[dbo].[ORDTASK] ot, [srv-starlimsdat].[PROD_UPG_DATA].[dbo].[NL_TESTS_ARTICLES] ta, [MATIS].[dbo].[Matís ohf_$Item] mi, [srv-starlimsdat].[PROD_UPG_DATA].[dbo].[SAMPLE_PROGRAMS] sp where sp.[SP_CODE] = ot.[SP_CODE] and ot.[FOLDERNO] = '"+folderno+"'"
			+" and ta.[TESTCODE] = ot.[TESTCODE] and convert(int,mi.[No_]) = convert(int,ta.[ARTICLENO])";
		
		PreparedStatement 	ps = con.prepareStatement(sql);
		ResultSet 			rs = ps.executeQuery();

		orderList = new ArrayList<Order>();
		while (rs.next()) {
			orderList.add( new Order( rs.getString(1), rs.getString(2), rs.getDouble(3), rs.getString(4), folderMap.get(folderno) ) );
		}
		rs.close();
		ps.close();
		
		if( orderList.size() > 0 ) {
			resultTable.setModel( createModel( orderList ) );
		}
	}
	
	public void load() throws SQLException {
		String sql = "select f.FOLDERNO, rc.COMPNAME, f.RASCLIENTID, rc.ADRESS, rc.CITY, f.RASPROJECTNO, f.DRAWDATE from [srv-starlimsdat].[PROD_UPG_DATA].[dbo].[Folders] f, [srv-starlimsdat].[PROD_UPG_DATA].[dbo].[RASCLIENTS] rc where f.[RASCLIENTID] = rc.[RASCLIENTID]";
		
		PreparedStatement 	ps = con.prepareStatement(sql);
		ResultSet 			rs = ps.executeQuery();

		folderList = new ArrayList<Folder>();
		while (rs.next()) {
			String folder = rs.getString(1);
			folderMap.put( folder, folderList.size() );
			folderList.add( new Folder( folder, rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getDate(7), invset.get( folder ) ) );
		}
		rs.close();
		ps.close();
	}
}
