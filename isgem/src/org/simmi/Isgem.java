package org.simmi;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JApplet;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class Isgem extends JApplet {
	static String lof = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
	
	Connection 		con;
	
	int curfc = -1;
	
	JSplitPane		splitpane;
	JScrollPane		scrollpane;
	JScrollPane		detailscroll;
	JTable			table;
	JTable			detailtable;
	TableModel		model;
	TableModel		detailmodel;
	TableModel		nullmodel;
	
	public class Food {
		Integer	fcode;
		String 	name;
		String 	ename;
		String 	sname;
		String 	gname;
		
		public Food( Integer fcode, String name, String ename, String sname, String gname ) {
			this.fcode = fcode;
			this.name = name;
			this.ename = ename;
			this.sname = sname;
			this.gname = gname;
		}
	};
	
	public class Component {
		String	code;
		String	val;
		String	unit;
		
		public Component( String code, String val, String unit ) {
			this.code = code;
			this.val = val;
			this.unit = unit;
		}
	};
	
	public TableModel createClassModel( final List<?> datalist ) {
		if( datalist.size() == 0 ) return null;
		
		final Class cls = datalist.get(0).getClass();
		return new TableModel() {
			@Override
			public void addTableModelListener(TableModelListener l) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public Class<?> getColumnClass(int columnIndex) {
				return cls.getDeclaredFields()[columnIndex].getDeclaringClass();
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
				Object ret = "";
				try {
					Field f = cls.getDeclaredFields()[columnIndex];
					if( ret != null ) {
						Object obj = f.get( datalist.get(rowIndex) );
						if( obj != null ) ret = obj;
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
		this.getContentPane().setBackground( Color.white );
		
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
		
		nullmodel = new TableModel( ) {
			@Override
			public void setValueAt(Object aValue, int rowIndex, int columnIndex) {}
			
			@Override
			public void removeTableModelListener(TableModelListener l) {				
			}
			
			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return false;
			}
			
			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				return null;
			}
			
			@Override
			public int getRowCount() {
				return 0;
			}
			
			@Override
			public String getColumnName(int columnIndex) {
				return "";
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
			}
		};
		
		final List<Food>		foodlist = new ArrayList<Food>();
		final List<Component>	complist = new ArrayList<Component>();
		
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			String connectionUrl = "jdbc:sqlserver://navision.rf.is;databaseName=isgem2;user=simmi;password=drsmorc.311;";
			con = DriverManager.getConnection(connectionUrl);
			
			String sql = "select f.OriginalFoodCode, f.OriginalFoodName, f.EnglishFoodName, f.ScientificFoodName, g.Descriptor from Food f, thsGroups g where g.Code = f.OriginalFoodGroupCode";
			PreparedStatement ps = con.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				Integer	fcode = rs.getInt(1);
				String 	name = rs.getString(2);
				String 	ename = rs.getString(3);
				String 	sname = rs.getString(4);
				String 	gname = rs.getString(5);
				
				foodlist.add( new Food( fcode, name, ename, sname, gname ) );
			}
			rs.close();
			ps.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		table = new JTable();
		//table.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );
		table.setAutoCreateRowSorter( true );
		
		detailtable = new JTable();
		//detailtable.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );
		detailtable.setAutoCreateRowSorter( true );
		
		scrollpane = new JScrollPane( table );
		detailscroll = new JScrollPane( detailtable );
		
		splitpane = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, scrollpane, detailscroll );
		splitpane.setDividerLocation( 500 );
		
		model = createClassModel( foodlist );
		//detailmodel = createModel();
		
		table.setModel( model );
		
		table.addKeyListener( new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
				
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				if( e.getKeyCode() == KeyEvent.VK_DELETE ) {
					if( detailmodel != null && detailmodel.getRowCount() > 0 ) {
						JOptionPane.showMessageDialog( Isgem.this, "Please delete components first" );
					} else {
						int cf = JOptionPane.showConfirmDialog( Isgem.this, "Are you sure?", "Please confirm", JOptionPane.YES_NO_OPTION );
						if( cf == JOptionPane.YES_OPTION ) {
							int r = table.getSelectedRow();
							if( r > -1 ) {
								try {
									int fc = (Integer)table.getValueAt( r, 0 );
									String sql = "delete from Food where OriginalFoodCode = "+fc;
									PreparedStatement ps = con.prepareStatement(sql);
									ps.execute();
									ps.close();
									
									foodlist.clear();
									sql = "select f.OriginalFoodCode, f.OriginalFoodName, f.EnglishFoodName, f.ScientificFoodName, g.Descriptor from Food f, thsGroups g where g.Code = f.OriginalFoodGroupCode";
									ps = con.prepareStatement(sql);
									ResultSet rs = ps.executeQuery();
									while (rs.next()) {
										Integer	fcode = rs.getInt(1);
										String 	name = rs.getString(2);
										String 	ename = rs.getString(3);
										String 	sname = rs.getString(4);
										String 	code = rs.getString(5);
										
										foodlist.add( new Food( fcode, name, ename, sname, code ) );
									}
									rs.close();
									ps.close();
									
									//model = createClassModel( foodlist );
									//table.setModel( model );							
									table.tableChanged( new TableModelEvent( model ) );
									//table.repaint();
								} catch (SQLException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
							}
						}
					}
				}
			}
		});
		
		detailtable.addKeyListener( new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
				int r = detailtable.getSelectedRow();
				if( r > -1 ) {
					if( JOptionPane.showConfirmDialog( Isgem.this, "Are you sure?", "Please confirm", JOptionPane.YES_NO_OPTION ) == JOptionPane.YES_OPTION ) {
						try {
							String rc = (String)detailtable.getValueAt( r, 0 );
							String sql = "delete from ComponentValue where OriginalReferenceCode = '"+rc+"' and OriginalFoodCode = "+curfc;
							PreparedStatement ps = con.prepareStatement(sql);
							ps.execute();
							ps.close();
							
							complist.clear();
							sql = "select c.OriginalComponentName, cv.SelectedValue, cv.Unit from ComponentValue cv, Component c where cv.OriginalComponentCode = c.OriginalComponentCode and OriginalFoodCode = "+curfc;
							ps = con.prepareStatement(sql);
							ResultSet rs = ps.executeQuery();
							while (rs.next()) {
								String code = rs.getString(1);
								String val = rs.getString(2);
								String unit = rs.getString(3);
								
								complist.add( new Component( code, val, unit ) );
							}
							rs.close();
							ps.close();
							
							detailmodel = createClassModel( complist );
							if( detailmodel == null ) detailmodel = nullmodel;
							detailtable.setModel( detailmodel );							
							detailtable.tableChanged( new TableModelEvent( detailmodel ) );
							detailtable.repaint();
						} catch (SQLException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				
			}

			@Override
			public void keyTyped(KeyEvent e) {
				
			}
		});
		
		table.getSelectionModel().addListSelectionListener( new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				complist.clear();
				int r = table.getSelectedRow();
				int fc = -1;
				if( r > -1 ) {
					fc = (Integer)table.getValueAt( r, 0 );
					try {
						String sql = "select c.OriginalComponentName, cv.SelectedValue, cv.Unit from ComponentValue cv, Component c where cv.OriginalComponentCode = c.OriginalComponentCode and OriginalFoodCode = "+fc;
						PreparedStatement ps = con.prepareStatement(sql);
						ResultSet rs = ps.executeQuery();
						while (rs.next()) {
							String code = rs.getString(1);
							String val = rs.getString(2);
							String unit = rs.getString(3);
							
							complist.add( new Component( code, val, unit ) );
						}
						rs.close();
						ps.close();
					} catch (SQLException se) {
						// TODO Auto-generated catch block
						se.printStackTrace();
					}
				}
				
				detailmodel = createClassModel( complist );
				curfc = fc; 
				if( detailmodel == null ) detailmodel = nullmodel;
				detailtable.setModel( detailmodel );
				detailtable.tableChanged( new TableModelEvent( detailmodel ) );
				detailtable.repaint();
			}
		});
		
		this.add( splitpane );
	}
}
