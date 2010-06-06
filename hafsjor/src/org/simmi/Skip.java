package org.simmi;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Skip extends JApplet {
	Connection 		con;
	List<Skipin>	skipList;
	
	public class Skipin {
		String	Skipsnúmer;
		String	Heiti;
		String	Einkennisstafir;
		String	Heimahöfn;
		String	Útgerðarflokkur;
		String	Kennitala_eiganda;
		String	Kennitala_útgerðar;
		
		public Skipin( String skipsnumer, String heiti, String einkennisstafir, String heimahofn, String utgerdarflokkur, String kte, String ktu ) {
			this.Skipsnúmer = skipsnumer;
			this.Heiti = heiti;
			this.Einkennisstafir = einkennisstafir;
			this.Heimahöfn = heimahofn;
			this.Útgerðarflokkur = utgerdarflokkur;
			this.Kennitala_eiganda = kte;
			this.Kennitala_útgerðar = ktu;
		}
	}
	
	public Connection connect() throws SQLException, ClassNotFoundException {
		if( con == null || con.isClosed() ) {
			con =  Uthlutun.connect();
		}
		return con;
	}
	
	public Skip() {
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
		
		try {
			connect();
			skipList = loadSkip();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	};
	
	public TableModel createModel( final List<?> datalist ) {
		Class cls = null;
		if( cls == null && datalist.size() > 0 ) cls = datalist.get(0).getClass();
		return createModel( datalist, cls );
	}
	
	public TableModel createModel( final List<?> datalist, final Class cls ) {
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
				return cls.getDeclaredFields()[columnIndex].getName().replace("e_", "").replace("_", " ");
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
					e.printStackTrace();
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
				return ret;
			}

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				Field[] ff = cls.getDeclaredFields();
				Field 	f = ff[columnIndex];
				//this.getColumnCount() > 5 ? this.getValueAt(rowIndex, 5).equals(user) : 
				boolean editable = false; //f.getName().startsWith("e_") && ( (this.getValueAt(rowIndex, 4).equals(user)) || userCheck() );
				return editable;
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
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		};
	}
	
	public List<Skipin> loadSkip() throws IOException, SQLException {
		List<Skipin>	skiplist = new ArrayList<Skipin>();
		
		String sql = "select sk.[skipsnumer], sk.[heiti], sk.[einkst], hh.[nafn], ut.[nafn], sk.[kennitala_eiganda], sk.[kennitala_utgerdar] from [hafsjor].[dbo].[skip] sk, [hafsjor].[dbo].[heimahofn] hh, [hafsjor].[dbo].[utgerdarflokkur] ut where hh.[nr] = sk.[heimahofn] and ut.[nr] = sk.[utgerdarflokkur]"; // where [user] = '"+user+"'";
		PreparedStatement 	ps = con.prepareStatement(sql);
		ResultSet 			rs = ps.executeQuery();

		while (rs.next()) {
			skiplist.add( new Skipin( rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7) ) );
		}
		
		rs.close();
		ps.close();
		
		return skiplist;
	}
	
	public void excelOpen( List<?>	datalist ) throws IOException {
		File f = File.createTempFile("tmp_", ".xlsx");
		
		XSSFWorkbook workbook = new XSSFWorkbook();
		
		Sheet 	sheet = workbook.createSheet("Skip");
		int r = 0;
		Row		row = sheet.createRow(r++);
		
		Class cls = null;
		if( cls == null && datalist.size() > 0 ) cls = datalist.get(0).getClass();
		
		int c = 0;
		for( Field fld : cls.getDeclaredFields() ) {
			Cell cell = row.createCell(c++);
			cell.setCellValue( fld.getName() );
		}
		
		for( Skipin skip : skipList ) {
			row = sheet.createRow(r++);
			
			c = 0;
			Cell cell = row.createCell(c++);
			cell.setCellValue( skip.Skipsnúmer );
			cell = row.createCell(c++);
			cell.setCellValue( skip.Heiti );
			cell = row.createCell(c++);
			cell.setCellValue( skip.Einkennisstafir );
			cell = row.createCell(c++);
			cell.setCellValue( skip.Heimahöfn );
			cell = row.createCell(c++);
			cell.setCellValue( skip.Útgerðarflokkur );
			cell = row.createCell(c++);
			cell.setCellValue( skip.Kennitala_eiganda );
			cell = row.createCell(c++);
			cell.setCellValue( skip.Kennitala_útgerðar );
		}
		
		workbook.write( new FileOutputStream( f ) );
		
		Desktop.getDesktop().open( f );
	}
	
	public class ADialog extends JDialog {
		JLabel		lab = new JLabel();
		JTextField	fld = new JTextField();
		//JButton		but = new JButton();
		boolean		appr = 	false;
		
		public ADialog() {
			super();
			
			init();
		}
		
		public ADialog( Frame f ) {
			super( f );
			
			init();
		}
		
		public void init() {
			this.setLayout( null );
			
			this.setUndecorated( true );
			this.setModal( true );
			this.setResizable( false );
			this.setTitle("Bæta við heimild");
			this.getContentPane().setBackground( Color.white );
			
			lab.setText("Slóð:");
			lab.setHorizontalAlignment( JLabel.RIGHT );
			
			this.add( ok );
			this.add( cancel );
			//this.add( but );
			this.add( fld );
			this.add( lab );
		}
		
		JButton ok = new JButton( new AbstractAction("OK") {
			@Override
			public void actionPerformed(ActionEvent e) {
				appr = true;
				ADialog.this.setVisible( false );
			}
		});
		
		JButton cancel = new JButton( new AbstractAction("Cancel") {
			@Override
			public void actionPerformed(ActionEvent e) {
				appr = false;
				ADialog.this.setVisible( false );
			}
		});
		
		public void setBounds( int x, int y, int w, int h ) {
			super.setBounds( x,y,w,h );
			
			lab.setBounds(0, 15, 95, 25);
			//but.setBounds(100, 15, 225, 25);
			fld.setBounds(100, 15, 225, 25);
			ok.setBounds(70, 70, 100, 25);
			cancel.setBounds(240, 70, 100, 25);
		}
	};
		
	public void addSkip( String urlstr, JTable table ) throws Exception {
		URL url = new URL( urlstr );
		InputStream is = url.openStream();
		
		//Object cont = url.getContent();
		byte[]	bb = new byte[10000000];
		int r = is.read( bb );
		
		if( r > 0 ) {
			String path = url.getPath();
			String heit = url.getQuery();
			String file = url.getFile();
			String host = url.getHost();
			String prot = url.getProtocol();
			
			String[] 	split = path.split("\\/");
			String		name = split[split.length-1];
			String		tpath = urlstr.substring(0,urlstr.length()-name.length());
			
			if( prot.contains("file") ) {
				tpath = "http://www.matis.is/hafsjor/uploads/";
				
				URL upurl = new URL( "http://www.matis.is/hafsjor/upload.php" );
			}
			String[] ss = name.split("\\.");
			String teg = "."+ss[ ss.length-1 ];
			String sql = "insert into [hafsjor].[dbo].[skip] (heiti,tegund,slod) values ('"+name+"','"+teg+"','"+tpath+"')";
			
			PreparedStatement ps = connect().prepareStatement(sql);
			ps.execute();
			
			skipList.add( new Skipin( name, teg, null, null, tpath, null, null ) );
			table.tableChanged( new TableModelEvent( table.getModel() ) );
		} else {
			JOptionPane.showMessageDialog( Skip.this, "Invalid path" );
		}
		
		//System.err.println();
	}
	
	public void openHeimild( String urlstr ) throws IOException, URISyntaxException {
		URL url = new URL( urlstr );
		Desktop.getDesktop().browse( url.toURI() );
	}
	
	public void init() {
		final JTable		table = new JTable();
		table.setAutoCreateRowSorter( true );
		
		Window window = SwingUtilities.windowForComponent(this);
		if (window instanceof JFrame) {
			JFrame frame = (JFrame)window;
			if (!frame.isResizable()) frame.setResizable(true);
		}
		
		Frame f = (Frame)SwingUtilities.getAncestorOfClass( Frame.class, Skip.this );
		
		final ADialog ad;
		if( f != null ) {
			ad = new ADialog( f );
		} else {
			ad = new ADialog();
		}
		
		JPopupMenu	popup = new JPopupMenu();
		popup.add( new AbstractAction("Bæta við heimild") {
			@Override
			public void actionPerformed(ActionEvent e) {
				ad.setSize(350, 100);
				ad.setLocationRelativeTo( Skip.this );
				ad.setVisible( true );
				
				if( ad.appr ) {
					try {
						addSkip( ad.fld.getText(), table );
					} catch (IOException e1) {
						e1.printStackTrace();
					} catch (SQLException e1) {
						e1.printStackTrace();
					} catch (ClassNotFoundException e1) {
						e1.printStackTrace();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		popup.addSeparator();
		popup.add( new AbstractAction("Opna í excel") {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					excelOpen( skipList );
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});		
		
		JScrollPane	scrollpane = new JScrollPane( table );
		scrollpane.setBorder( BorderFactory.createEmptyBorder() );
		scrollpane.getViewport().setBackground( Color.white );
		
		scrollpane.setComponentPopupMenu( popup );
		table.setComponentPopupMenu( popup );
		
		TableModel	model = createModel( skipList );
		table.setModel( model );
		
		this.add( scrollpane );
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}
}
