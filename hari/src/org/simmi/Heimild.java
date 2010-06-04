package org.simmi;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
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

public class Heimild extends JApplet {
	Connection 		con;
	List<Heimildin>	heimList;
	
	public class Heimildin {
		String	Heiti;
		String	Tegund;
		String	Vefslóð;
		String	Uppruni;
		String	Slóð;
		
		public Heimildin( String heiti, String tegund, String vefslod, String uppruni, String slod ) {
			this.Heiti = heiti;
			this.Tegund = tegund;
			this.Vefslóð = vefslod;
			this.Uppruni = uppruni;
			this.Slóð = slod;
		}
	}
	
	public Connection connect() throws SQLException, ClassNotFoundException {
		if( con == null || con.isClosed() ) {
			con =  Uthlutun.connect();
		}
		return con;
	}
	
	public Heimild() {
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
			heimList = loadHeimildir();
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
	
	public List<Heimildin> loadHeimildir() throws IOException, SQLException {
		List<Heimildin>	heimlist = new ArrayList<Heimildin>();
		
		String sql = "select [heiti], [tegund], [vefslod], [uppruni], [slod] from [hafsjor].[dbo].[heimildir]"; // where [user] = '"+user+"'";
		PreparedStatement 	ps = con.prepareStatement(sql);
		ResultSet 			rs = ps.executeQuery();

		while (rs.next()) {
			heimlist.add( new Heimildin( rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5) ) );
		}
		
		rs.close();
		ps.close();
		
		return heimlist;
	}
	
	public void excelOpen( List<?>	datalist ) throws IOException {
		File f = File.createTempFile("tmp_", ".xlsx");
		
		XSSFWorkbook workbook = new XSSFWorkbook();
		
		Sheet 	sheet = workbook.createSheet("Heimildir");
		int r = 0;
		Row		row = sheet.createRow(r++);
		
		Class cls = null;
		if( cls == null && datalist.size() > 0 ) cls = datalist.get(0).getClass();
		
		int c = 0;
		for( Field fld : cls.getDeclaredFields() ) {
			Cell cell = row.createCell(c++);
			cell.setCellValue( fld.getName() );
		}
		
		for( Heimildin heim : heimList ) {
			row = sheet.createRow(r++);
			
			c = 0;
			Cell cell = row.createCell(c++);
			cell.setCellValue( heim.Heiti );
			cell = row.createCell(c++);
			cell.setCellValue( heim.Tegund );
			cell = row.createCell(c++);
			cell.setCellValue( heim.Vefslóð );
			cell = row.createCell(c++);
			cell.setCellValue( heim.Uppruni );
			cell = row.createCell(c++);
			cell.setCellValue( heim.Slóð );
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
	
	private static final String Boundary = "--7d021a37605f0";
	public static void upload(URL url, String filename, byte[] data, int length) throws Exception {
        HttpURLConnection theUrlConnection = (HttpURLConnection)url.openConnection();
        theUrlConnection.setDoOutput(true);
        theUrlConnection.setDoInput(true);
        theUrlConnection.setUseCaches(false);
        theUrlConnection.setChunkedStreamingMode(1024);

        theUrlConnection.setRequestProperty("Content-Type", "multipart/form-data; boundary="+Boundary);

        DataOutputStream httpOut = new DataOutputStream(theUrlConnection.getOutputStream());

        String str = "--" + Boundary + "\r\n"
                   + "Content-Disposition: form-data;name=\"uploadedfile\"; filename=\"" + filename + "\"\r\n"
                   + "Content-Type: application/binary\r\n"
                   + "\r\n";

        httpOut.write(str.getBytes());
        httpOut.write( data, 0, length );
        
        /*int numBytesToRead = 1024;
        int availableBytesToRead;
        while ((availableBytesToRead = uploadFileReader.available()) > 0) {
            byte[] bufferBytesRead;
            bufferBytesRead = availableBytesToRead >= numBytesToRead ? new byte[numBytesToRead] : new byte[availableBytesToRead];
            uploadFileReader.read(bufferBytesRead);
            httpOut.write(bufferBytesRead);
            httpOut.flush();
        }*/
        
        //httpOut.write(("--" + Boundary + "--\r\n").getBytes());
        httpOut.write(("--" + Boundary + "--\r\n").getBytes());
        httpOut.flush();	
        httpOut.close();

        InputStream is = theUrlConnection.getInputStream();
        StringBuilder response = new StringBuilder();
        byte[] respBuffer = new byte[4096];
        while (is.read(respBuffer) >= 0) {
            response.append(new String(respBuffer).trim());
        }
        is.close();
        System.out.println(response.toString());
    }
	
	public void addHeimild( String urlstr, JTable table ) throws Exception {
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
				//URL upurl = new URL( "http://localhost:5002/" );
				upload(upurl, name, bb, r);
				/*HttpURLConnection connection = (HttpURLConnection)upurl.openConnection();
				connection.setDoInput(true);
				connection.setDoOutput(true);
				connection.setRequestMethod("POST");
				
				connection.getOutputStream().write(bb, 0, r);*/
			}
			String[] ss = name.split("\\.");
			String teg = "."+ss[ ss.length-1 ];
			String sql = "insert into [hafsjor].[dbo].[heimildir] (heiti,tegund,slod) values ('"+name+"','"+teg+"','"+tpath+"')";
			
			PreparedStatement ps = connect().prepareStatement(sql);
			ps.execute();
			
			heimList.add( new Heimildin( name, teg, null, null, tpath ) );
			table.tableChanged( new TableModelEvent( table.getModel() ) );
		} else {
			JOptionPane.showMessageDialog( Heimild.this, "Invalid path" );
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
		
		Frame f = (Frame)SwingUtilities.getAncestorOfClass( Frame.class, Heimild.this );
		
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
				ad.setLocationRelativeTo( Heimild.this );
				ad.setVisible( true );
				
				if( ad.appr ) {
					try {
						addHeimild( ad.fld.getText(), table );
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
		popup.add( new AbstractAction("Opna heimild") {
			@Override
			public void actionPerformed(ActionEvent e) {
				int r = table.getSelectedRow();
				if( r != -1 ) {
					try {
						Heimildin hm = heimList.get( r );
						String path = hm.Slóð + hm.Heiti;
						openHeimild( path );
					} catch (IOException e1) {
						e1.printStackTrace();
					} catch (URISyntaxException e1) {
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
					excelOpen( heimList );
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
		
		TableModel	model = createModel( heimList );
		table.setModel( model );
		
		this.add( scrollpane );
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}
}
