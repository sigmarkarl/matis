package org.simmi;

import java.awt.Color;
import java.awt.Window;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JApplet;
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

public class StarlimsReport extends JApplet {
	List<String>	folderList;
	List<Order>		orderList;
	JSplitPane		splitPane;
	JTable			table;
	JTable			resultTable;
	TableModel		model;
	TableModel		resultModel;
	Connection		con;
	Color			bgcolor;
	
	public class Order {
		String	ordno;
		double	price;
		String	testcode;
		
		public Order( String ordno, double price, String testcode ) {
			this.ordno = ordno;
			this.price = price;
			this.testcode = testcode;
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
			load();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		model = new TableModel() {
			
			@Override
			public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void removeTableModelListener(TableModelListener l) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				return folderList.get(rowIndex);
			}
			
			@Override
			public int getRowCount() {
				return folderList.size();
			}
			
			@Override
			public String getColumnName(int columnIndex) {
				return "StarLims Folder";
			}
			
			@Override
			public int getColumnCount() {
				return 1;
			}
			
			@Override
			public Class<?> getColumnClass(int columnIndex) {
				return String.class;
			}
			
			@Override
			public void addTableModelListener(TableModelListener l) {
				// TODO Auto-generated method stub
				
			}
		};
		
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
		
		this.add( splitPane );
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
	
	public void loadResults( String folderno ) throws SQLException {
		String sql = "select ot.[ORDNO], mi.[Unit Price], mi.[Description] from [srv-starlimsdat].[PROD_UPG_DATA].[dbo].[ORDTASK] ot, [srv-starlimsdat].[PROD_UPG_DATA].[dbo].[NL_TESTS_ARTICLES] ta, [MATIS].[dbo].[Matís ohf_$Item] mi where ot.[ORDNO] like '"+folderno+"%'"
			+" and ta.[TESTCODE] = ot.[TESTCODE] and convert(int,mi.[No_]) = convert(int,ta.[ARTICLENO])";
		
		PreparedStatement 	ps = con.prepareStatement(sql);
		ResultSet 			rs = ps.executeQuery();

		orderList = new ArrayList<Order>();
		while (rs.next()) {
			orderList.add( new Order( rs.getString(1), rs.getDouble(2), rs.getString(3) ) );
		}
		rs.close();
		ps.close();
		
		if( orderList.size() > 0 ) {
			resultTable.setModel( createModel( orderList ) );
		}
	}
	
	public void load() throws ClassNotFoundException, SQLException {
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		String connectionUrl = "jdbc:sqlserver://navision.rf.is:1433;databaseName=MATIS;user=simmi;password=drsmorc.311;";
		con = DriverManager.getConnection(connectionUrl);
		
		String sql = "select FOLDERNO from [srv-starlimsdat].[PROD_UPG_DATA].[dbo].[Folders]";
		
		PreparedStatement 	ps = con.prepareStatement(sql);
		ResultSet 			rs = ps.executeQuery();

		folderList = new ArrayList<String>();
		while (rs.next()) {
			folderList.add( rs.getString(1) );
		}
		rs.close();
		ps.close();
	}
}
