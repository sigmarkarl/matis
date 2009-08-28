package org.simmi;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class StarlimsReport extends JApplet {
	Set<String>		invset;
	List<Folder>	folderList;
	Map<String,Integer>	folderMap = new HashMap<String,Integer>();
	List<Order>		orderList;
	JSplitPane		splitPane;
	JTable			table;
	JTable			resultTable;
	TableModel		model;
	TableModel		resultModel;
	Connection		con;
	Color			bgcolor;
	
	public class Folder {
		String	name;
		String	client;
		String	project;
		Date	date;
		Boolean b;
		
		public Folder( String name ,String client, String project, Date date, boolean b ) {
			this.name = name;
			this.client = client;
			this.project = project;
			this.date = date;
			this.b = b;
		}
	}
	
	public class Order {
		String	ordno;
		double	price;
		String	testcode;
		int		f_i;
		
		public Order( String ordno, double price, String testcode, int f_i ) {
			this.ordno = ordno;
			this.price = price;
			this.testcode = testcode;
			this.f_i = f_i;
		}
	}
	
	public void init() {
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
					for( int r : rr ) {
						String folder = (String)table.getValueAt(r, 0);
						loadResult( folder );
					}
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				if( orderList.size() > 0 ) {
					resultTable.setModel( createModel( orderList ) );
				}
				
			}
		});
		
		JButton	report = new JButton( new AbstractAction("Report") {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					File nf = File.createTempFile("tmp", ".xlsx");
					InputStream inputStream = this.getClass().getResourceAsStream( "/Book1.xlsx" );
					FileOutputStream fos = new FileOutputStream( nf );
					
					byte[] bb = new byte[1024];
					int read = inputStream.read(bb);
					while( read > 0 ) {
						fos.write( bb, 0, read );
						read = inputStream.read( bb );
					}
					fos.close();
					
					//XSSFWorkbook workbook = new XSSFWorkbook( "/home/sigmar/workspace/starlimsreports/src/Book1.xlsx" );
					XSSFWorkbook workbook = new XSSFWorkbook( nf.getAbsolutePath() );
					
					//Map<String, CellStyle> styles = createStyles(workbook);
					
					XSSFSheet 	sheet = workbook.getSheet("Sheet1");
					XSSFRow		row = sheet.createRow( 0 );
					row = sheet.createRow( 1 );
					XSSFCell	cell = row.createCell(1);
					cell.setCellValue( "Yfirlit yfir sýni rannsökuð" );
					
					row = sheet.createRow( 2 );
					row = sheet.createRow( 3 );
					
					row = sheet.createRow( 4 );
					int i = 1;
					Set<String>	clients = new HashSet<String>();
					int[] rr = table.getSelectedRows();
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
						projects.add( (String)table.getValueAt(r, 2) );
					}
					
					for( String project : projects ) {
						cell = row.createCell(i++);
						cell.setCellValue( project );
					}
					
					row = sheet.createRow( 8 );
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
						cell.setCellStyle( style );*/
						i++;
					}
					
					int r = 9;
					for( Order o : orderList ) {
						row = sheet.createRow( r );
						
						cell = row.createCell(1);
						cell.setCellValue( o.ordno );						
						
						cell = row.createCell(2);
						//cell.setCellType( XSSFCell.)
						//cell.setCellValue( new java.util.Date( folderList.get( o.f_i ).date.getTime() ) );
						cell.setCellValue( folderList.get( o.f_i ).date.toString() );
						
						cell = row.createCell(4);
						cell.setCellValue( o.testcode );
						
						cell = row.createCell(6);
						cell.setCellValue( o.price );
						
						r++;
					}
				
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
		
		buttons.add( load, BorderLayout.WEST );
		buttons.add( report, BorderLayout.EAST );
		
		this.setLayout( new BorderLayout() );
		this.add( splitPane );
		this.add( buttons, BorderLayout.SOUTH );
	}
	
	private static Map<String, CellStyle> createStyles(Workbook wb){
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
    }
	
	public TableModel createModel( final List<?> datalist ) {
		final Class cls = datalist.get(0).getClass();
		return new TableModel() {
			@Override
			public void addTableModelListener(TableModelListener l) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public Class<?> getColumnClass(int columnIndex) {
				return cls.getDeclaredFields()[columnIndex].getClass();
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
				String ret = "";
				try {
					Field f = cls.getDeclaredFields()[columnIndex];
					if( ret != null ) {
						Object obj = f.get( datalist.get(rowIndex) );
						if( obj != null ) ret = obj.toString();
						/*else {
							System.err.println("null obj "+rowIndex + "  " + columnIndex);
						}*/
					} else {
						System.err.println("null field"+rowIndex + "  " + columnIndex);
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
				return false;
			}

			@Override
			public void removeTableModelListener(TableModelListener l) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
				// TODO Auto-generated method stub
				
			}
		};
	}
	
	public void loadInvoice() throws SQLException {
		String sql = "select f.FOLDERNO from [srv-starlimsdat].[PROD_UPG_DATA].[dbo].[NL_INVOICES] f";
		
		PreparedStatement 	ps = con.prepareStatement(sql);
		ResultSet 			rs = ps.executeQuery();

		invset = new HashSet<String>();
		while (rs.next()) {
			invset.add( rs.getString(1) );
		}
		rs.close();
		ps.close();
	}
	
	public void loadResult( String folderno ) throws SQLException {
		String sql = "select ot.[ORDNO], mi.[Unit Price], mi.[Description] from [srv-starlimsdat].[PROD_UPG_DATA].[dbo].[ORDTASK] ot, [srv-starlimsdat].[PROD_UPG_DATA].[dbo].[NL_TESTS_ARTICLES] ta, [MATIS].[dbo].[Matís ohf_$Item] mi where ot.[ORDNO] like '"+folderno+"%'"
			+" and ta.[TESTCODE] = ot.[TESTCODE] and convert(int,mi.[No_]) = convert(int,ta.[ARTICLENO])";
		
		PreparedStatement 	ps = con.prepareStatement(sql);
		ResultSet 			rs = ps.executeQuery();

		if( orderList == null ) orderList = new ArrayList<Order>();
		while (rs.next()) {
			orderList.add( new Order( rs.getString(1), rs.getDouble(2), rs.getString(3), folderMap.get(folderno) ) );
		}
		rs.close();
		ps.close();
	}
	
	public void loadResults( String folderno ) throws SQLException {
		String sql = "select ot.[ORDNO], mi.[Unit Price], mi.[Description] from [srv-starlimsdat].[PROD_UPG_DATA].[dbo].[ORDTASK] ot, [srv-starlimsdat].[PROD_UPG_DATA].[dbo].[NL_TESTS_ARTICLES] ta, [MATIS].[dbo].[Matís ohf_$Item] mi where ot.[ORDNO] like '"+folderno+"%'"
			+" and ta.[TESTCODE] = ot.[TESTCODE] and convert(int,mi.[No_]) = convert(int,ta.[ARTICLENO])";
		
		PreparedStatement 	ps = con.prepareStatement(sql);
		ResultSet 			rs = ps.executeQuery();

		orderList = new ArrayList<Order>();
		while (rs.next()) {
			orderList.add( new Order( rs.getString(1), rs.getDouble(2), rs.getString(3), folderMap.get(folderno) ) );
		}
		rs.close();
		ps.close();
		
		if( orderList.size() > 0 ) {
			resultTable.setModel( createModel( orderList ) );
		}
	}
	
	public void load() throws SQLException {		
		String sql = "select f.FOLDERNO, rc.COMPNAME, f.RASPROJECTNO, f.DRAWDATE from [srv-starlimsdat].[PROD_UPG_DATA].[dbo].[Folders] f, [srv-starlimsdat].[PROD_UPG_DATA].[dbo].[RASCLIENTS] rc where f.[RASCLIENTID] = rc.[RASCLIENTID]";
		
		PreparedStatement 	ps = con.prepareStatement(sql);
		ResultSet 			rs = ps.executeQuery();

		folderList = new ArrayList<Folder>();
		while (rs.next()) {
			String folder = rs.getString(1);
			folderMap.put( folder, folderList.size() );
			folderList.add( new Folder( folder, rs.getString(2), rs.getString(3), rs.getDate(4), invset.contains( folder ) ) );
		}
		rs.close();
		ps.close();
	}
}
