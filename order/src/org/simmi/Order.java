package org.simmi;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.EventObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.AbstractCellEditor;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.SpinnerListModel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public class Order extends JApplet {
	JScrollPane	scrollpane = new JScrollPane();
	JScrollPane	pscrollpane = new JScrollPane();
	JScrollPane ascrollpane = new JScrollPane();
	JTable		table = new JTable();
	JTable		ptable = new JTable();
	JTable		atable = new JTable();
	
	Map<String,Integer>	modelRowMap = new HashMap<String,Integer>();
	
	JScrollPane	scrolled = new JScrollPane();
	JEditorPane	ed = new JEditorPane();
	
	JLabel		vorur = new JLabel( "Vörur", JLabel.CENTER );
	JLabel		pantanir = new JLabel( "Pantanir", JLabel.CENTER );
	JLabel		afgreitt = new JLabel( "Afgreitt", JLabel.CENTER );
	
	JLabel		label = new JLabel( "Framl:", JLabel.CENTER );
	JLabel		plabel = new JLabel( "Byrgir:", JLabel.CENTER );
	
	JComboBox	combo = new JComboBox();
	JComboBox	pcombo = new JComboBox();
	
	JComboBox	vcombo;
	JComboBox	stcombo;
	
	JButton		newItem = new JButton( "Ný vara" );
	JButton		delItem = new JButton( "Eyða vöru" );
	JButton		pantanytt = new JButton();
	
	JButton		afgreida = new JButton();
	
	TableModel	model;
	TableModel	pmodel;
	TableModel	amodel;
	
	TableModel	nullmodel;
	JComponent	c;
	Connection	con;
	String		user;
	
	List<Pontun>	pntlist;
	List<Vara>		ordlist;
	
	int				ordno = 0;
	
	Pontun			currentSel = null;
	
	Map<String,Image>	faces = new HashMap<String,Image>();
	
	VDialog 		d = new VDialog();
	Image			image = null;
	
	byte[] bb = new byte[1024];	
	Map<String,String>	pMap = new HashMap<String,String>();
	
	final Color bg = Color.white;//new Color( 0,0,0,0 );
	
	public class VDialog extends JDialog {
		JLabel	name = new JLabel("Nafn:", JLabel.RIGHT );
		JLabel	cat = new JLabel("Cat:", JLabel.RIGHT );
		JLabel	frml = new JLabel("Framleiðandi:", JLabel.RIGHT );
		JLabel	brgr = new JLabel("Byrgir:", JLabel.RIGHT );
		
		JTextField	catField = new JTextField();
		JTextField	nameField = new JTextField();
		JComboBox	frmlCombo = new JComboBox();
		JComboBox	brgrCombo = new JComboBox();
		
		boolean		appr = false;
		
		JButton ok = new JButton( new AbstractAction("OK") {
			@Override
			public void actionPerformed(ActionEvent e) {
				appr = true;
				VDialog.this.setVisible( false );
			}
		});
		
		JButton cancel = new JButton( new AbstractAction("Cancel") {
			@Override
			public void actionPerformed(ActionEvent e) {
				appr = false;
				VDialog.this.setVisible( false );
			}
		});
		
		public VDialog() {
			super();
			
			init();
		}
		
		public void setBounds( int x, int y, int w, int h ) {
			super.setBounds(x, y, w, h);
			
			name.setBounds(0, 15, 95, 25);
			cat.setBounds(275, 15, 95, 25);
			frml.setBounds(0, 45, 95, 25);
			brgr.setBounds(275, 45, 95, 25);
			
			nameField.setBounds(100, 15, 225, 25);
			catField.setBounds(375, 15, 150, 25);
			frmlCombo.setBounds(100, 45, 150, 25);
			brgrCombo.setBounds(375, 45, 150, 25);
			
			ok.setBounds(140, 100, 100, 25);
			cancel.setBounds(310, 100, 100, 25);
		}
		
		private void init() {
			this.setLayout( null );
			
			this.setUndecorated( true );
			this.setModal( true );
			this.setResizable( false );
			this.setTitle("Ný Vara");
			/*this.getRootPane().setBackground( Color.white );
			this.getRootPane().setForeground( Color.white );
			this.setBackground( Color.white );
			this.setForeground( Color.white );*/
			this.getContentPane().setBackground( Color.white );
			//this.getContentPane().setForeground( Color.white );
			
			this.add( name );
			this.add( cat );
			this.add( frml );
			this.add( brgr );
			
			this.add( nameField );
			this.add( catField );
			this.add( frmlCombo );
			this.add( brgrCombo );
			
			this.add(ok);
			this.add(cancel);
			
			frmlCombo.setEditable( true );
			brgrCombo.setEditable( true );
		}
	}
	
	public class Pontun {
		Boolean		e_Mikilvægt;
		Integer		_Númer;
		String		Nafn;
		String		PantaðAf;
		Integer		e_Magn;
		Date		Pantað;
		Date		_Afgreitt;
		String		_Lýsing;
		String		VerkNúmer;
		String		Staðsetning;
		
		public Pontun( boolean urgent, int ordno, String name, String user, int quant, Date orddate, Date purdate, String description, String vn, String st ) {
			this.e_Mikilvægt = urgent;
			this._Númer = ordno;
			this.Nafn = name;
			this.PantaðAf = user;
			this.e_Magn = quant;
			this.Pantað = orddate;
			this._Afgreitt = purdate;
			this._Lýsing = description;
			this.VerkNúmer = vn;
			this.Staðsetning = st;
		}
	}
	
	public class Vara {
		String  Nafn;
		String	Framleiðandi;
		String	Byrgir;
		Integer	_Cat;
		
		public Vara( String name, String prdc, String selr, int cat ) {
			this.Nafn = name;
			this.Framleiðandi = prdc;
			this.Byrgir = selr;
			this._Cat = cat;
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
		//System.err.println( cls );
		return new TableModel() {
			@Override
			public void addTableModelListener(TableModelListener l) {}

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
				return cls.getDeclaredFields()[columnIndex].getName().replace("e_", "");
			}

			@Override
			public int getRowCount() {
				return datalist.size();
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				Object ret = null;
				try {
					if( columnIndex >= 0 ) {
						Field f = cls.getDeclaredFields()[columnIndex];
						ret = f.get( datalist.get(rowIndex) );
						
						if( ret != null && ret.getClass() != f.getType() ) {
							System.err.println( ret.getClass() + "  " + f.getType() );
							ret = null;
						}
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
				Field[] ff = cls.getDeclaredFields();
				Field 	f = ff[columnIndex];
				//System.err.println( ff.length + "  " + columnIndex + "  " + f.getName() );
				return f.getName().startsWith("e_") && this.getValueAt(rowIndex, 3).equals(user);
			}

			@Override
			public void removeTableModelListener(TableModelListener l) {}

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
	
	public List<Pontun>	loadPnt() throws SQLException {
		List<Pontun>	pntList = new ArrayList<Pontun>();
		
		String sql = "select [ordno], [name], [user], [quant], [orddate], [purdate], [urgent], [description], [jobid], [location] from [order].[dbo].[Pontun]";// where [user] = '"+user+"'";
		
		PreparedStatement 	ps = con.prepareStatement(sql);
		ResultSet 			rs = ps.executeQuery();

		while (rs.next()) {
			pntList.add( new Pontun( rs.getBoolean(7), rs.getInt(1), rs.getString(2), rs.getString(3), rs.getInt(4), rs.getDate(5), rs.getDate(6), rs.getString(8), rs.getString(9), rs.getString(10) ) );
		}
		
		rs.close();
		ps.close();
		
		sql = "select max([ordno]) from [order].[dbo].[Pontun]";
		
		ps = con.prepareStatement(sql);
		rs = ps.executeQuery();

		while (rs.next()) {
			ordno = rs.getInt( 1 );
		}
		
		rs.close();
		ps.close();
		
		return pntList;
	}
	
	public List<Vara> loadOrders() throws IOException, SQLException {
		InputStream			is = this.getClass().getResourceAsStream("/orders.txt");
		InputStreamReader 	ir = new InputStreamReader( is );
		BufferedReader		br = new BufferedReader( ir );
		
		List<Vara>	ordlist = new ArrayList<Vara>();
		
		String sql = "select [cat], [name], [byrgir], [framl] from [order].[dbo].[Vara]"; // where [user] = '"+user+"'";
		PreparedStatement 	ps = con.prepareStatement(sql);
		ResultSet 			rs = ps.executeQuery();

		while (rs.next()) {
			ordlist.add( new Vara( rs.getString(2), rs.getString(4), rs.getString(3), rs.getInt(1) ) );
		}
		
		rs.close();
		ps.close();
		
		String line = br.readLine();
		while( line != null ) {
			String[] spl = line.split("[\t]");
			int i = -1;
			try {
				i = Integer.parseInt(spl[4]);
			} catch( Exception e ) {
				
			}
			ordlist.add( new Vara( spl[0], spl[3], spl[6], i ) );
			
			line = br.readLine();
		}
		br.close();
		ir.close();
		
		return ordlist;
	}
	
	public String getUser() {
		return user;
	}
	
	/*public int getOrdno() {
		int l_ordno = ordno;
		
		if( pntlist != null ) {
			for( Pnt p : pntlist ) {
				if( p.ordno > l_ordno ) l_ordno = p.ordno;
			}
			l_ordno++;
		}
		
		return l_ordno;
	}*/
	
	public void updateorder( Pontun order ) throws SQLException {
		//String ord = ordno+",'"+name+"','"+user+"',"+quant+",GetDate(),null,0,null";
		String sql = "update [order].[dbo].[Pontun] set [quant] = "+order.e_Magn+", [Description] = '"+order._Lýsing+"' where [ordno] = "+order._Númer;
		
		PreparedStatement 	ps = con.prepareStatement(sql);
		boolean				b = ps.execute();
		
		ps.close();
	}
	
	public void updateorder( Pontun order, int quant ) throws SQLException {
		//String ord = ordno+",'"+name+"','"+user+"',"+quant+",GetDate(),null,0,null";
		String sql = "update [order].[dbo].[Pontun] set [quant] = "+quant+" where [ordno] = "+order._Númer;
		
		PreparedStatement 	ps = con.prepareStatement(sql);
		boolean				b = ps.execute();
		
		order.e_Magn = quant;
		//pntlist.get(index)
		/*if( !b ) {
			pntlist.add( new Pnt( false, ordno, name, user, quant, new Date( System.currentTimeMillis() ), null, "" ) );
		}*/
		
		ps.close();
	}
	
	public void order( String name, int quant, String verknr, String location ) throws SQLException {
		ordno++;
		String ord = ordno+",'"+name+"','"+user+"',"+quant+",GetDate(),null,0,null,'"+verknr.substring(0, 10)+"','"+location+"'";
		String sql = "insert into [order].[dbo].[Pontun] values ("+ord+")";
		
		PreparedStatement 	ps = con.prepareStatement(sql);
		boolean				b = ps.execute();
		
		if( !b ) {
			pntlist.add( new Pontun( false, ordno, name, user, quant, new Date( System.currentTimeMillis() ), null, "", verknr, location ) );
		}
		
		ps.close();
	}
	
	public boolean disorder( int ordno ) throws SQLException {
		String sql = "delete from [order].[dbo].[Pontun] where ordno = "+ordno;
		
		PreparedStatement 	ps = con.prepareStatement(sql);
		boolean				b = ps.execute();
		
		ps.close();
		
		return b;
	}
	
	public String loadEmp() throws IOException {
		URL url = new URL( "http://www.matis.is/um-matis-ohf/starfsfolk/svid/" );
		InputStream stream = url.openStream();
		
		int r = stream.read(bb);
		String ret = "";
		while( r > 0 ) {
			ret += new String( bb, 0, r );
			r = stream.read(bb);
		}
		
		return ret;
	}
	
	public void removeSelectedRows() {
		int[] 			rr = ptable.getSelectedRows();
		Set<Pontun>		remset = new HashSet<Pontun>();
		for( int r : rr ) {
			int 	rval = ptable.convertRowIndexToModel( r );
			if( rval != -1 ) {
				int			ordno = (Integer)pmodel.getValueAt(rval, 1);
				String 		username = (String)ptable.getValueAt(r, 2);
				
				if( username.equals(user) ) {
					try {
						if( !disorder( ordno ) ) {
							for( Pontun p : pntlist ) {
								if( p._Númer == ordno ) {
									remset.add( p );
								}
							}
						}
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
				}
			}
		}	
		pntlist.removeAll( remset );
		ptable.tableChanged( new TableModelEvent(pmodel) );
		//ptable.setModel( nullmodel );
		//ptable.setModel( pmodel );
	}
	
	public void updateVerk() throws SQLException {
		String sql = "select [No_], [Description] from [MATIS].[dbo].[Matís ohf_$Job] where [Blocked] = 0"; // where [user] = '"+user+"'";
		
		PreparedStatement 	ps = con.prepareStatement(sql);
		ResultSet 			rs = ps.executeQuery();

		while (rs.next()) {
			vcombo.addItem( rs.getString(1) + " - " + rs.getString(2) );
		}
		
		rs.close();
		ps.close();
	}
	
	private void panta(String name) {
		int 		quant = 1;
		Pontun 		tpnt = null;
		for( Pontun pnt : pntlist ) {
			if( pnt.Nafn.equals( name ) && pnt.PantaðAf.equals(user) ) {
				tpnt = pnt;
				break;
			}
		}
		
		try {
			if( tpnt != null ) {
				updateorder( tpnt, tpnt.e_Magn+1 );
			} else {
				order( name, quant, (String)vcombo.getSelectedItem(), (String)stcombo.getSelectedItem() );
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public void init() {
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
		vcombo = new JComboBox() {
			private boolean layingOut = false;

			public void doLayout() {
		        try {
		            layingOut = true;
		            super.doLayout();
		        }
		        finally {
		            layingOut = false;
		        }
		    }

		    public Dimension getSize() {
		        Dimension sz = super.getSize();
		        if (!layingOut) {
		            sz.width = Math.max(sz.width, this.getPreferredSize().width);
		        }
		        return sz;
		    }
		};
		
		stcombo = new JComboBox() {
			private boolean layingOut = false;

			public void doLayout() {
		        try {
		            layingOut = true;
		            super.doLayout();
		        }
		        finally {
		            layingOut = false;
		        }
		    }

		    public Dimension getSize() {
		        Dimension sz = super.getSize();
		        if (!layingOut) {
		            sz.width = Math.max(sz.width, this.getPreferredSize().width);
		        }
		        return sz;
		    }
		};
		
		try {
			String val = loadEmp();
			String[] vals = val.split( "<h3>" );
			
			for( int i = 1; i < vals.length; i++ ) {
				String sstr = vals[i];
				int hind = sstr.indexOf("</h3>");
				String svid = sstr.substring(0, hind);
				
				String[] subs = sstr.split("<a href=\"/um-matis-ohf/");
				if( subs.length > 1 ) {
					String currentStr = null;
					for( String str : subs ) {
						currentStr = str;
						int ind = str.indexOf("</a>");
						if( ind > 0 ) {
							String person = str.substring(0,ind);
							if( !person.contains("fyrirtaeki") ) {
								break;
							} else {
								pMap.put(person, svid);
							}
						}
					}
					if( !subs[ subs.length-1 ].equals(currentStr) ) break;
				}
			}
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			String connectionUrl = "jdbc:sqlserver://navision.rf.is:1433;databaseName=MATIS;user=simmi;password=drsmorc.311;";
			con = DriverManager.getConnection(connectionUrl);
			
			updateVerk();
			
			con.close();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		stcombo.addItem("Borgartún");
		stcombo.addItem("Skúlagata");
		stcombo.addItem("Gylfaflöt");
		
		try {
			//String connectionUrl = "jdbc:sqlserver://navision.rf.is:1433;databaseName=order;integratedSecurity=true;";
			String connectionUrl = "jdbc:sqlserver://navision.rf.is:1433;databaseName=order;user=simmi;password=drsmorc.311;";
			con = DriverManager.getConnection(connectionUrl);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		nullmodel = new TableModel() {
			@Override
			public void addTableModelListener(TableModelListener l) {}

			@Override
			public Class<?> getColumnClass(int columnIndex) {return null;}

			@Override
			public int getColumnCount() {return 0;}

			@Override
			public String getColumnName(int columnIndex) {return null;}

			@Override
			public int getRowCount() {return 0;}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {return null;}

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {return false;}

			@Override
			public void removeTableModelListener(TableModelListener l) {}

			@Override
			public void setValueAt(Object aValue, int rowIndex, int columnIndex) {}
		};
		
		pantanytt.setAction( new AbstractAction("Panta nýja vöru") {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Vara v = newItem();
					panta( v.Nafn );
					ptable.tableChanged( new TableModelEvent( pmodel ) );
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		});
			
		final JButton	addbtn = new JButton( new AbstractAction("Panta >>") {
			public void actionPerformed(ActionEvent e) {
				int[] rr = table.getSelectedRows();
				for( int r : rr ) { 
					String		name = (String)table.getValueAt(r, 0);
					//String		name = (String)table.getValueAt(r, 0);					
					panta(name);
				}
				ptable.tableChanged( new TableModelEvent( pmodel ) );
				//ptable.setModel( nullmodel );
				//ptable.setModel( pmodel );
			}
		});
		final JButton	rembtn = new JButton(new AbstractAction("<< Afpanta") {
			public void actionPerformed(ActionEvent e) {
				removeSelectedRows();
			}
		});
		
		afgreida.setAction( new AbstractAction("<< Afgreiða") {
			@Override
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		
		final String domain = System.getenv("USERDOMAIN");
		/*Map<String,String>	env = System.getenv();
		for( String e : env.keySet() ) {
			System.err.println( e );
		}*/
		//System.err.println( domain );
		user = System.getProperty("user.name");
		
		try {
			ordlist = loadOrders();
			model = createModel( ordlist );
			
			pntlist = loadPnt();
			pmodel = createModel( pntlist, Pontun.class );
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		c = new JComponent() {
			public void paintComponent( Graphics g ) {
				super.paintComponent(g);
				
				Graphics2D g2 = (Graphics2D)g;
				g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
				
				Font f = g.getFont();
				g.setFont( f.deriveFont( f.getSize()+5.0f ) );
				
				String str = "Velkomin(n) "+user;
				if( domain != null ) str += " on "+domain;
				int strw = g.getFontMetrics().stringWidth( str );
				g.drawString( str, (this.getWidth()-strw)/2, 20 );
				
				if( user != null ) {
					Image face = getImage(user);
					if( face != null ) g.drawImage( face, (this.getWidth()-face.getWidth(this))/2, 50, this );
				}
				
				if( image != null ) {
					g.drawImage( image, (this.getWidth()-image.getWidth(this))/2, 50, this );
				}
			}
			
			public void setBounds( int x, int y, int w, int h ) {
				super.setBounds(x, y, w, h);
				
				vcombo.setBounds( (int)(0.5*w)-75, 260, 150, 25 );
				stcombo.setBounds( (int)(0.5*w)-75, 290, 150, 25 );
				
				label.setBounds( (int)(0.00*w), 50, (int)(0.15*w), 25 );
				plabel.setBounds( (int)(0.25*w), 50, (int)(0.15*w), 25 );
				combo.setBounds( (int)(0.00*w), 70, (int)(0.15*w), 25 );
				pcombo.setBounds( (int)(0.25*w), 70, (int)(0.15*w), 25 );
				newItem.setBounds( (int)(0.00*w), 110+(int)(0.40*h), (int)(0.15*w), 25 );
				delItem.setBounds( (int)(0.25*w), 110+(int)(0.40*h), (int)(0.15*w), 25 );
				
				vorur.setBounds( (int)(0.00*w), 30, (int)(0.40*w), 25 );
				pantanir.setBounds( (int)(0.60*w), 30, (int)(0.40*w), 25 );
				afgreitt.setBounds( (int)(0.00*w), (int)(0.60*h)-30, (int)(0.40*w), 25 );
				
				scrollpane.setBounds( (int)(0.00*w), 100, (int)(0.40*w), (int)(0.40*h) );
				ascrollpane.setBounds( (int)(0.00*w), (int)(0.6*h), (int)(0.40*w), (int)(0.40*h) );
				pscrollpane.setBounds( (int)(0.60*w), 100, (int)(0.40*w), (int)(0.40*h) );
				
				scrolled.setBounds( (int)(0.60*w)+150, (int)(0.6*h), (int)(0.40*w)-150, (int)(0.40*h) );
				
				addbtn.setBounds( (int)(0.5*w)-75, 340, 150, 25 );
				rembtn.setBounds( (int)(0.5*w)-75, 370, 150, 25 );
				pantanytt.setBounds( (int)(0.5*w)-75, 400, 150, 25 );
				
				afgreida.setBounds( (int)(0.5*w)-75, (int)(0.6*h)+100, 150, 25 );
			}
		};

		this.setBackground( bg );
		this.getContentPane().setBackground( bg );
		
		combo.addItem("Allir");
		pcombo.addItem("Allir");
		
		Set<String>	comboOptions = new TreeSet<String>();
		Set<String>	pcomboOptions = new TreeSet<String>();
		
		for( int r = 0; r < model.getRowCount(); r++ ) {
			String str = (String)model.getValueAt(r, 1);
			if( str != null && str.length() > 0 ) {
				comboOptions.add(str);
			}
		}
		
		for( int r = 0; r < model.getRowCount(); r++ ) {
			String str = (String)model.getValueAt(r, 2);
			if( str != null && str.length() > 0 ) {
				pcomboOptions.add(str);
			}
		}
		
		for( String str : comboOptions ) {
			d.frmlCombo.addItem( str );
			combo.addItem(str);
		}
		
		for( String str : pcomboOptions ) {
			d.brgrCombo.addItem( str );
			pcombo.addItem(str);
		}
		
		combo.addItemListener( new ItemListener() {	
			@Override
			public void itemStateChanged(ItemEvent e) {
				pcombo.setSelectedItem("Allir");
				((TableRowSorter<TableModel>)table.getRowSorter()).setRowFilter( new RowFilter<TableModel, Integer>() {
					@Override
					public boolean include(javax.swing.RowFilter.Entry<? extends TableModel, ? extends Integer> entry) {
						String sel = (String)combo.getSelectedItem();
						if( sel.equals("Allir") ) return true; 
						int r = entry.getIdentifier();
						Object str = (String)model.getValueAt( r, 1 );
						if( str.equals( sel ) ) return true;
						return false;
					}
				});
			}
		});
		
		pcombo.addItemListener( new ItemListener() {	
			@Override
			public void itemStateChanged(ItemEvent e) {
				combo.setSelectedItem("Allir");
				((TableRowSorter<TableModel>)table.getRowSorter()).setRowFilter( new RowFilter<TableModel, Integer>() {
					@Override
					public boolean include(javax.swing.RowFilter.Entry<? extends TableModel, ? extends Integer> entry) {
						String sel = (String)pcombo.getSelectedItem();
						if( sel.equals("Allir") ) return true; 
						int r = entry.getIdentifier();
						Object str = (String)model.getValueAt( r, 2 );
						if( str.equals( sel ) ) return true;
						
						return false;
					}
				});
			}
		});
		
		//table.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );
		//table.setColumnSelectionAllowed( true );
		table.setAutoCreateRowSorter( true );
		table.setModel( model );
		ptable.setAutoCreateRowSorter( true );
		ptable.setModel( pmodel );
		
		for( int r = 0; r < model.getRowCount(); r++ ) {
			modelRowMap.put( (String)model.getValueAt(r, 0), r );
		}
		
		ptable.getSelectionModel().addListSelectionListener( new ListSelectionListener() {
			//String oldname = "";
			
			public void valueChanged(ListSelectionEvent e) {
				if( currentSel != null && !currentSel.equals(ed.getText()) ) {
					currentSel._Lýsing = ed.getText().replaceAll("'", "");
					try {
						updateorder(currentSel);
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				
				int r = ptable.getSelectedRow();
				if( r != -1 ) {
					int mr = ptable.convertRowIndexToModel(r);
					Pontun pnt = pntlist.get(mr);
					currentSel = pnt;
					ed.setText( currentSel._Lýsing );
					
					if( pnt.PantaðAf.equals(user) ) {
						ed.setEditable( true );
					} else {
						ed.setEditable( false );
					}
					
					String name = (String)ptable.getValueAt(r, 2);
					String vara = (String)ptable.getValueAt(r, 1);
					
					if( vara != null && modelRowMap.containsKey( vara ) ) {
						int nr = modelRowMap.get( vara );
						nr = table.convertRowIndexToView( nr );
						table.setRowSelectionInterval( nr, nr );
						table.scrollRectToVisible( table.getCellRect(nr, 0, false) );
					}
					
					/*if( !name.equals( oldname ) ) {
						ll
						oldname = name;
					}*/
				}
			}
		});
		
		//table.getColumnModel().getColumn(0).
		//ptable.getColumn("Magn").setCellEditor( new SpinnerEditor(items));
		//ptable.getColumn("Magn").setCellRenderer( TableColumn)
		
		ptable.addKeyListener( new KeyAdapter() {
			public void keyPressed( KeyEvent e ) {
				if( e.getKeyCode() == KeyEvent.VK_DELETE ) {
					removeSelectedRows();
				}
			}
		});
		
		ptable.getSelectionModel().addListSelectionListener( new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				
			}
		});
		
		Set<TableColumn>			remcol = new HashSet<TableColumn>();
		Enumeration<TableColumn>	taben = table.getColumnModel().getColumns();
		while( taben.hasMoreElements() ) {
			TableColumn tc = taben.nextElement();
			if( tc.getIdentifier().toString().startsWith("_") ) {
				remcol.add( tc );
			}
		}
		
		for( TableColumn tc : remcol ) {
			table.removeColumn( tc );
		}
		
		remcol.clear();
		taben = ptable.getColumnModel().getColumns();
		while( taben.hasMoreElements() ) {
			TableColumn tc = taben.nextElement();
			if( tc.getIdentifier().toString().startsWith("_") ) {
				remcol.add( tc );
			}
		}
		
		for( TableColumn tc : remcol ) {
			ptable.removeColumn( tc );
		}
		
		newItem.setAction( new AbstractAction("Ný vara") {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					newItem();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		delItem.setAction( new AbstractAction("Eyða vöru") {
			@Override
			public void actionPerformed(ActionEvent e) {
				int r = table.getSelectedRow();
				if( r != -1 ) {
					int rr = table.convertRowIndexToModel(r);
					int cat = (Integer)model.getValueAt(rr, 3);
					try {
						delItem( cat );
						for( Vara v : ordlist ) {
							if( v._Cat == cat ) {
								combo.removeItem( v.Framleiðandi );
								pcombo.removeItem( v.Byrgir );
								
								ordlist.remove( v );
								table.tableChanged( new TableModelEvent(model) );
								break;
							}
						}
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
		
		scrolled.setViewportView( ed );
		
		scrollpane.setViewportView( table );
		pscrollpane.setViewportView( ptable );
		ascrollpane.setViewportView( atable );
		
		c.add( combo );
		c.add( pcombo );
		c.add( vcombo );
		c.add( stcombo );
		c.add( label );
		c.add( plabel );
		c.add( vorur );
		c.add( pantanir );
		c.add( afgreitt );
		c.add( scrollpane );
		c.add( pscrollpane );
		c.add( ascrollpane );
		c.add( scrolled );
		
		c.add( afgreida );
		
		c.add( newItem );
		c.add( delItem );
		c.add( pantanytt );
		
		c.add( addbtn );
		c.add( rembtn );
		this.add( c );
	}
	
	final Image getImage( String name ) {
		if( faces.containsKey( name ) ) {
			return faces.get(name);
		} else {
			for( String person : pMap.keySet() ) {
				if( person.toLowerCase().contains(name.toLowerCase()) ) {
					int ind = person.indexOf('"');
					String link = person.substring(0, ind);
					try {
						URL url = new URL( "http://www.matis.is/um-matis-ohf/"+link );
						InputStream stream = url.openStream();
						
						String ret = "";
						int r = stream.read(bb);
						while( r > 0 ) {
							ret += new String( bb, 0, r );
							r = stream.read(bb);
						}
						stream.close();
						
						int i = ret.indexOf("<img src=\"");
						if( i >= 0 ) {
							i += 10;
							int e = ret.indexOf("\"", i);
							String urlstr = "http://www.matis.is"+ret.substring(i, e);
							//urlstr = URLEncoder.encode( urlstr, "UTF-8" );
							urlstr = urlstr.replace(" ", "%20");
							
							url = new URL( urlstr );
							Image image = ImageIO.read( url );
							
							faces.put(user, image);
							
							return image;
						}
						
						/*String[] ss = ret.split("<div class=\"boxbody\">");
						for( int i = 1; i < 2; i++ ) {
							String s = ss[i];
							ind = s.indexOf("></ul>");
							String sub = "<html>"+s.substring(0, ind)+"></ul>";
							sub = sub.replace("</div>", "");
							sub = sub.replace("<img src=\"","<img src=\"http://www.matis.is");
							sub = sub.replace("Sigmar St","Sigmar%20St");
							sub += "</html>";
							
							ed.setEditable( false );
							ed.setContentType("text/html");
							ed.setText( sub );
						}*/
						//ed.setPage( url );
					} catch (MalformedURLException e1) {
						e1.printStackTrace();
					} catch (IOException e2) {
						e2.printStackTrace();
					}
				}
			}
		}
		return null;
	}
	
	protected Vara queryVara() {
		d.setSize(550, 140);
		d.setLocationRelativeTo( this );
		d.setVisible( true );
		
		String str = d.catField.getText();
		if( d.appr && str != null ) {
			int val = 0;
			try {
				val = Integer.parseInt( str );
			} catch( Exception e ) {
				e.printStackTrace();
			}
			if( val != 0 ) return new Vara( d.nameField.getText(), (String)d.frmlCombo.getSelectedItem(), (String)d.brgrCombo.getSelectedItem(), val );
		}
		
		return null;
	}
	
	protected Vara newItem() throws SQLException {
		Vara v = queryVara();
		
		if( v != null ) {
			String ord = "'"+v.Nafn+"','"+v.Framleiðandi+"','"+v.Byrgir+"',"+v._Cat+",'"+user+"'";
			String sql = "insert into [order].[dbo].[Vara] values ("+ord+")";
			
			PreparedStatement 	ps = con.prepareStatement(sql);
			boolean				b = ps.execute();
			
			if( !b ) {
				ordlist.add( v );
				
				int i;
				String str = null;
				for( i = 0; i < combo.getItemCount(); i++ ) {
					str = (String)combo.getItemAt(i);
					if( str.compareTo(v.Framleiðandi) >= 0 ) break;
				}
					
				if( !str.equals(v.Framleiðandi) ) {
					combo.insertItemAt(v.Framleiðandi, i);
				}
				
				str =null;
				for( i = 0; i < pcombo.getItemCount(); i++ ) {
					str = (String)pcombo.getItemAt(i);
					if( str.compareTo(v.Byrgir) >= 0 ) break;
				}
				
				if( !str.equals(v.Byrgir) ) {
					pcombo.insertItemAt(v.Byrgir, i);
				}
				
				table.tableChanged( new TableModelEvent(model) );
				
				int mr = model.getRowCount()-1;
				modelRowMap.put( (String)model.getValueAt( mr, 0), mr );
			}
			
			ps.close();
		}
		
		return v;
	}
	
	protected boolean delItem( int cat ) throws SQLException {
		String sql = "delete from [order].[dbo].[Vara] where cat = "+cat;
		
		PreparedStatement 	ps = con.prepareStatement(sql);
		boolean				b = ps.execute();
		
		ps.close();
		
		return b;
	}

	public class SpinnerEditor extends AbstractCellEditor implements TableCellEditor {
		 final JSpinner spinner = new JSpinner();
		
		 // Initializes the spinner.
		 public SpinnerEditor(String[] items) {
		     spinner.setModel(new SpinnerListModel(java.util.Arrays.asList(items)));
		 }
		
		 // Prepares the spinner component and returns it.
		 public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		     spinner.setValue(value);
		     return spinner;
		 }
		
		 // Enables the editor only for double-clicks.
		 public boolean isCellEditable(EventObject evt) {
		     if (evt instanceof MouseEvent) {
		         return ((MouseEvent)evt).getClickCount() >= 2;
		     }
		     return true;
		 }
		
		 // Returns the spinners current value.
		 public Object getCellEditorValue() {
		     return spinner.getValue();
		 }
	 }

	
	public final Color b1 = new Color( 0,100,255 );
	public final Color b2 = new Color( 200,200,255 );
	
	public void paint( Graphics g ) {
		super.paint( g );
	}
}
