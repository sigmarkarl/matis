package org.simmi;

import java.awt.Graphics;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JApplet;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class DimmaLims extends JApplet {
	static String lof = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
	
	public class User {
		String	name;
		String	deptlist;
		String	email;
		String	jobdesc;
		
		public User( String name, String depl, String email, String jdesc ) {
			this.name = name;
			this.deptlist = depl;
			this.email = email;
			this.jobdesc = jdesc;
		}
	};
	
	public class Test {
		String	testcatcode;
		String	testno;
		
		public Test( String testcatcode, String testno ) {
			this.testcatcode = testcatcode;
			this.testno = testno;
		}
	};
	
	public class Program {
		String	prodgrp;
		String	progname;
		String	mtemp;
		
		public Program( String prodgrp, String progname, String mtmp ) {
			this.prodgrp = prodgrp;
			this.progname = progname;
			this.mtemp = mtmp;
		}
	};
	
	public class Result {
		String	analyte;
		Date	date;
		String	fres;
		String	firstuser;
		String	folderno;
		String 	testno;
		
		public Result( String	analyte, Date	date, String	fres, String	firstuser, String	folderno, String 	testno ) {
			this.analyte = analyte;
			this.date = date;
			this.fres = fres;
			this.firstuser = firstuser;
			this.folderno = folderno;
			this.testno = testno;
		}
	};
	
	public class Order {
		String	clsampno;
		Date	duedat;
		String	fno;
		
		public Order( String	analyte, Date	duedat, String	fno ) {
			this.clsampno = analyte;
			this.duedat = duedat;
			this.fno = fno;
		}
	};
	
	public TableModel createClassModel( final List<?> datalist ) {
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
		
		final List<User>	userlist = new ArrayList<User>();
		final List<Test>	testlist = new ArrayList<Test>();
		final List<Program>	proglist = new ArrayList<Program>();
		final List<Result>	resllist = new ArrayList<Result>();
		final List<Order>	ordrlist = new ArrayList<Order>();
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			String connectionUrl = "jdbc:sqlserver://130.208.252.230:1433;databaseName=PROD_UPG_DATA;user=limsadmin;password=starlims;";
			Connection con = DriverManager.getConnection(connectionUrl);
			
			String sql = "select FULLNAME, DEPTLIST, EMAIL, JOBDESCRIPTION from USERS";
			PreparedStatement ps = con.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				String name = rs.getString(1);
				String deptlist = rs.getString(2);
				String email = rs.getString(3);
				String jobdesc = rs.getString(4);
				
				userlist.add( new User( name, deptlist, email, jobdesc ) );
			}
			rs.close();
			ps.close();
			
			sql = "select TESTCATCODE, TESTNO from TESTS";
			ps = con.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				String catcode = rs.getString(1);
				String no = rs.getString(2);
				
				testlist.add( new Test( catcode, no ) );
			}
			rs.close();
			ps.close();
			
			sql = "select PRODGROUP, PROGNAME, METADATA_TEMPLATE from SAMPLE_PROGRAMS";
			ps = con.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				String prodgrp = rs.getString(1);
				String progname = rs.getString(2);
				String mdtmp = rs.getString(3);
				
				proglist.add( new Program( prodgrp, progname, mdtmp ) );
			}
			rs.close();
			ps.close();
			
			sql = "select ANALYTE, DATEENTER, FINAL, FIRSTUSER, FOLDERNO, TESTNO from RESULTS";
			ps = con.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				String 	analyte = rs.getString(1);
				Date	date = rs.getDate(2);
				String 	fres = rs.getString(3);
				String 	firstuser = rs.getString(4);
				String 	folderno = rs.getString(5);
				String 	testno = rs.getString(6);
				
				resllist.add( new Result( analyte, date, fres, firstuser, folderno, testno ) );
			}
			rs.close();
			ps.close();
			
			sql = "select CLSAMPNO, DUEDAT, FOLDERNO from ORDERS";
			ps = con.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				String 	clsampno = rs.getString(1);
				Date	duedat = rs.getDate(2);
				String 	fno = rs.getString(3);
				
				ordrlist.add( new Order( clsampno, duedat, fno ) );
			}
			rs.close();
			ps.close();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		final TableModel usermodel = createClassModel( userlist );
		final TableModel testmodel = createClassModel( testlist );
		final TableModel progmodel = createClassModel( proglist );
		final TableModel reslmodel = createClassModel( resllist );
		final TableModel ordrmodel = createClassModel( ordrlist );
		
		//System.err.println( usermodel.getRowCount() + "  " + usermodel.getColumnCount() );
		
		SwingUtilities.invokeLater( new Runnable() {
			
			@Override
			public void run() {
				final JTabbedPane	tabbedpane = new JTabbedPane();
				
				final JScrollPane	userscrollpane = new JScrollPane();
				final JScrollPane	testscrollpane = new JScrollPane();
				final JScrollPane	progscrollpane = new JScrollPane();
				final JScrollPane	reslscrollpane = new JScrollPane();
				final JScrollPane	ordrscrollpane = new JScrollPane();
				JComponent c = new JPanel() {
					public void setBounds( int x, int y, int w, int h ) {
						super.setBounds(x, y, w, h);
						tabbedpane.setBounds(10, 30, this.getWidth()-20, this.getHeight()-40 );
					}
					
					public void paintComponent( Graphics g ) {
						super.paintComponent( g );
						g.drawString( "DimmaLims", 10, 20 );
					}
				};
				tabbedpane.add( userscrollpane, "Users" );
				tabbedpane.add( testscrollpane, "Tests" );
				tabbedpane.add( progscrollpane, "Programs" );
				tabbedpane.add( reslscrollpane, "Results" );
				tabbedpane.add( ordrscrollpane, "Orders" );
				c.setLayout( null );
				
				JTable		usertable = new JTable();
				JTable		testtable = new JTable();
				JTable		progtable = new JTable();
				JTable		resltable = new JTable();
				JTable		ordrtable = new JTable();
				
				usertable.setAutoCreateRowSorter( true );
				testtable.setAutoCreateRowSorter( true );
				progtable.setAutoCreateRowSorter( true );
				resltable.setAutoCreateRowSorter( true );
				ordrtable.setAutoCreateRowSorter( true );
				
				usertable.setModel( usermodel );
				testtable.setModel( testmodel );
				progtable.setModel( progmodel );
				resltable.setModel( reslmodel );
				ordrtable.setModel( ordrmodel );
				userscrollpane.setViewportView( usertable );
				testscrollpane.setViewportView( testtable );
				progscrollpane.setViewportView( progtable );
				reslscrollpane.setViewportView( resltable );
				ordrscrollpane.setViewportView( ordrtable );
				c.add( tabbedpane );
				
				DimmaLims.this.add( c );
			}
		});
	}
}
