package org.simmi;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class Order extends JApplet {
	JScrollPane	scrollpane = new JScrollPane();
	JScrollPane	pscrollpane = new JScrollPane();
	JTable		table = new JTable();
	JTable		ptable = new JTable();
	TableModel	model;
	TableModel	pmodel;
	JComponent	c;
	Connection	con;
	String		user;
	List<Pnt>	pntlist;
	
	final Color bg = Color.white;//new Color( 0,0,0,0 );
	
	public class Pnt {
		int		ordno;
		String	name;
		String	user;
		int		quant;
		
		public Pnt( int ordno, String name, String user, int quant ) {
			this.ordno = ordno;
			this.name = name;
			this.user = user;
			this.quant = quant;
		}
	}
	
	public class Ord {
		String  Nafn;
		String	Framleiðandi;
		String	Byrgir;
		Integer	cat;
		
		public Ord( String name, String prdc, String selr, Integer cat ) {
			this.Nafn = name;
			this.Framleiðandi = prdc;
			this.Byrgir = selr;
			this.cat = cat;
		}
	};
	
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
	
	public TableModel createModel( final List<?> datalist ) {
		Class cls = null;
		if( cls == null && datalist.size() > 0 ) cls = datalist.get(0).getClass();
		return createModel( datalist, cls );
	}
	
	public TableModel createModel( final List<?> datalist, final Class cls ) {
		
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
	
	public List<Pnt>	loadPnt() throws SQLException {
		List<Pnt>	pntList = new ArrayList<Pnt>();
		
		String sql = "select [ordno], [name], [user], [quant] from [order].[dbo].[order]";
		
		PreparedStatement 	ps = con.prepareStatement(sql);
		ResultSet 			rs = ps.executeQuery();

		while (rs.next()) {
			pntList.add( new Pnt( rs.getInt(1), rs.getString(2), rs.getString(3), rs.getInt(4) ) );
		}
		
		rs.close();
		ps.close();
		
		return pntList;
	}
	
	public List<Ord> loadOrders() throws IOException {
		InputStream			is = this.getClass().getResourceAsStream("/orders.txt");
		InputStreamReader 	ir = new InputStreamReader( is );
		BufferedReader		br = new BufferedReader( ir );
		
		List<Ord>	ordlist = new ArrayList<Ord>();
		String line = br.readLine();
		while( line != null ) {
			String[] spl = line.split("[\t]");
			int i = -1;
			try {
				i = Integer.parseInt(spl[4]);
			} catch( Exception e ) {
				
			}
			ordlist.add( new Ord( spl[0], spl[3], spl[6], i ) );
			
			line = br.readLine();
		}
		return ordlist;
	}
	
	public String getUser() {
		return user;
	}
	
	public int getOrdno() {
		int ordno = 0;
		
		if( pntlist != null ) {
			for( Pnt p : pntlist ) {
				if( p.ordno > ordno ) ordno = p.ordno;
			}
			ordno++;
		}
		
		return ordno;
	}
	
	public void order( int ordno, String name, String user, int quant ) throws SQLException {
		String ord = ordno+",'"+name+"','"+user+"',"+quant+",0";
		String sql = "insert into [order].[dbo].[order] values ("+ord+")";
		
		PreparedStatement 	ps = con.prepareStatement(sql);
		boolean				b = ps.execute();
		
		if( !b ) {
			pntlist.add( new Pnt( ordno, name, user, quant ) );
		}
		
		ps.close();
	}
	
	public void disorder( int ordno ) throws SQLException {
		String sql = "delete from [order].[dbo].[order] where ordno = "+ordno;
		
		PreparedStatement 	ps = con.prepareStatement(sql);
		boolean				b = ps.execute();
		
		if( !b ) {
			Set<Pnt>	remset = new HashSet<Pnt>();
			for( Pnt p : pntlist ) {
				if( p.ordno == ordno ) {
					remset.add( p );
				}
			}
			pntlist.removeAll( remset );
		}
		
		ps.close();
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
		
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			String connectionUrl = "jdbc:sqlserver://navision.rf.is:1433;databaseName=order;user=simmi;password=drsmorc.311;";
			con = DriverManager.getConnection(connectionUrl);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		final JButton	addbtn = new JButton( new AbstractAction("Panta >>") {
			public void actionPerformed(ActionEvent e) {
				int[] rr = table.getSelectedRows();
				for( int r : rr ) {
					int			ordno = getOrdno();
					String		name = (String)table.getValueAt(r, 1);
					String 		user = getUser();
					int 		quant = 1;
					try {
						order( ordno, name, user, quant );
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
		final JButton	rembtn = new JButton(new AbstractAction("<< Afpanta") {
			public void actionPerformed(ActionEvent e) {
				int[] rr = table.getSelectedRows();
				for( int r : rr ) {
					int			ordno = (Integer)table.getValueAt(r, 0);
					String		name = (String)table.getValueAt(r, 1);
					String 		user = (String)table.getValueAt(r, 2);
					int 		quant = (Integer)table.getValueAt(r, 3);
					try {
						disorder( ordno );
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
		
		try {
			List<Ord> ordlist = loadOrders();
			model = createModel( ordlist );
			
			pntlist = loadPnt();
			pmodel = createModel( pntlist, Pnt.class );
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		final String domain = System.getenv("USERDOMAIN");
		/*Map<String,String>	env = System.getenv();
		for( String e : env.keySet() ) {
			System.err.println( e );
		}*/
		//System.err.println( domain );
		user = System.getProperty("user.name");
		c = new JComponent() {
			public void paintComponent( Graphics g ) {
				super.paintComponent(g);
				
				Graphics2D g2 = (Graphics2D)g;
				g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
				
				Font f = g.getFont();
				g.setFont( f.deriveFont( f.getSize()+5.0f ) );
				
				String str = "Welcome "+user;
				if( domain != null ) str += " on "+domain;
				int strw = g.getFontMetrics().stringWidth( str );
				g.drawString( str, (this.getWidth()-strw)/2, 20 );
			}
			
			public void setBounds( int x, int y, int w, int h ) {
				super.setBounds(x, y, w, h);
				
				scrollpane.setBounds( (int)(0.05*w), (int)(0.05*h), (int)(0.3*w), (int)(0.9*h) );
				pscrollpane.setBounds( (int)(0.65*w), (int)(0.05*h), (int)(0.3*w), (int)(0.9*h) );
				addbtn.setBounds( (int)(0.37*w), (int)(0.35*h), 100, 25 );
				rembtn.setBounds( (int)(0.37*w), (int)(0.35*h)+30, 100, 25 );
			}
		};

		this.setBackground( bg );
		this.getContentPane().setBackground( bg );
		
		//table.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );
		//table.setColumnSelectionAllowed( true );
		table.setAutoCreateRowSorter( true );
		table.setModel( model );
		ptable.setAutoCreateRowSorter( true );
		ptable.setModel( pmodel );
		scrollpane.setViewportView( table );
		pscrollpane.setViewportView( ptable );
		c.add( scrollpane );
		c.add( pscrollpane );
		c.add( addbtn );
		c.add( rembtn );
		this.add( c );
	}
	
	public final Color b1 = new Color( 0,100,255 );
	public final Color b2 = new Color( 200,200,255 );
	
	public void paint( Graphics g ) {
		super.paint( g );
	}
}
