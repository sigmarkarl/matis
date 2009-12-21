package org.simmi;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Fasteign extends JApplet {
	TableRowSorter<TableModel>	currentSorter;
	
	static Map<String,Integer>	mmap = new HashMap<String,Integer>();
	
	static {
		mmap.put("Janúar", 1);
		mmap.put("Febrúar", 2);
		mmap.put("Mars", 3);
		mmap.put("Apríl", 4);
		mmap.put("Maí", 5);
		mmap.put("Júní", 6);
		mmap.put("Júlí", 7);
		mmap.put("Ágúst", 8);
		mmap.put("September", 9);
		mmap.put("Október", 10);
		mmap.put("Nóvember", 11);
		mmap.put("Desember", 12);
		
		mmap.put("janúar", 1);
		mmap.put("febrúar", 2);
		mmap.put("mars", 3);
		mmap.put("apríl", 4);
		mmap.put("naí", 5);
		mmap.put("júní", 6);
		mmap.put("júlí", 7);
		mmap.put("ágúst", 8);
		mmap.put("september", 9);
		mmap.put("október", 10);
		mmap.put("nóvember", 11);
		mmap.put("desember", 12);
	}
	
	class Ibud {
		String nafn;
		int verd;
		int fastm;
		int brunm;
		String teg;
		int ferm;
		int herb;
		Date dat;
		String url;
		
		public Ibud( String nafn ) {
			this.nafn = nafn;
		}
		
		public Ibud( String nafn, int verd, int fastm, int brunm, String teg, int ferm, int herb, String dat, String url ) throws ParseException {
			this.nafn = nafn;
			this.verd = verd;
			this.fastm = fastm;
			this.brunm = brunm;
			this.teg = teg;
			this.ferm = ferm;
			this.herb = herb;
			this.dat = DateFormat.getDateInstance().parse( dat );
		}
		
		public void set( int i, Object obj ) {
			try {
				if( obj instanceof String ) {
					String val = obj.toString();
					val = val.replaceAll("\\.", "");
					if( i == 0 ) verd = Integer.parseInt(val);
					else if( i == 1 ) fastm = Integer.parseInt(val);
					else if( i == 2 ) brunm = Integer.parseInt(val);
					else if( i == 3 ) teg = val;
					else if( i == 4 ) ferm = Integer.parseInt(val);
					else if( i == 5 ) herb = Integer.parseInt(val);
					else if( i == 6 ) {
						String[] split = val.split(" ");
						if( split.length >= 3 && mmap.containsKey(split[1]) ) {
							int year = Integer.parseInt( split[2] );
							int month = mmap.get( split[1] );
							int day = Integer.parseInt( split[0] );
							Calendar cal = Calendar.getInstance();
							cal.set(year, month-1, day);
							dat = cal.getTime();
						}
						
					}
				} //else dat = (Date)obj;
			} catch( Exception e ) {
				e.printStackTrace();
			}
		}
		
		public String toString() {
			return nafn + "\t" + verd + "\t" + fastm + "\t" + brunm + "\t" + teg + "\t" + ferm + "\t" + herb + "\t" + dat;
		}
	}
	
	public boolean stuff( String urlstr ) throws IOException, InterruptedException {
		URL url = new URL( urlstr );
		InputStream stream = url.openStream();
		
		String str = "";		
		byte[]	bb = new byte[1024];
		int r = stream.read( bb );
		while( r > 0 ) {
			str += new String( bb, 0, r );
			r = stream.read( bb );
		}
		
		stream.close();
		
		String[] buds = {"estate-verd","estate-fasteignamat","estate-brunabotamat","estate-teg_eign","estate-fermetrar","estate-fjoldi_herb","estate-sent_dags"};
		
		int count = 0;
		
		String[] vals = str.split("fast-nidurstada clearfix");
		System.err.println(vals.length);
		String h2 = "<h2 style=\"margin-bottom: 0.91em; font-size:1.5em;\">";
		for( String val : vals ) {
			int ind = val.indexOf("<a href=\"");
			int stop = val.indexOf("\"", ind+10);
			
			String sub = val.substring(ind+9, stop);
			if( sub.contains("/mm/fasteignir") ) {
				url = new URL( "http://www.mbl.is"+sub );
				stream = url.openStream();
				
				str = "";		
				r = stream.read( bb );
				while( r > 0 ) {
					str += new String( bb, 0, r, "ISO-8859-1" );
					r = stream.read( bb );
				}
				stream.close();
				
				ind = str.indexOf(h2);
				stop = str.indexOf("</h2>",ind);
				String ibud = str.substring( ind+h2.length(), stop ).trim();
				Ibud ib = new Ibud( ibud );
				iblist.add( ib );
				count++;
				int i = 0;
				for( String bud : buds ) {
					ind = str.indexOf(bud);
					int start = str.indexOf("fst-rvalue\">", ind);
					stop = str.indexOf("</td>", start);
					String sval = str.substring(start+12, stop).trim();
					
					ib.set( i++, sval );
				}
			}
			
			Thread.sleep(200);
		}
		
		return count == 25;
	}
	
	public void calc( String urlstr ) {
		try {
			//String base = "http://www.mbl.is/mm/fasteignir/leit.html?simmi;svaedi=101_101&tegund=fjolbyli&tegund=einbyli&tegund=haedir&fermetrar_fra=70&fermetrar_til=150&herbergi_fra=&herbergi_til=&verd_fra=10&verd_til=40&gata=&lysing=";
			//String base = "http://www.mbl.is/mm/fasteignir/leit.html?simmi;svaedi=200_200&svaedi=201_201&svaedi=202_202&svaedi=203_203&tegund=fjolbyli&fermetrar_fra=70&fermetrar_til=150&herbergi_fra=&herbergi_til=&verd_fra=10&verd_til=40&gata=&lysing=";
			//String base = "http://www.mbl.is/mm/fasteignir/leit.html?simmi;svaedi=108_108&tegund=fjolbyli&fermetrar_fra=70&fermetrar_til=150&herbergi_fra=&herbergi_til=&verd_fra=10&verd_til=40&gata=&lysing=";
			
			int i = 0;
			while( true ) {
				if( !stuff( urlstr.replace("offset", "offset="+i) ) ) break;
				i += 25;
				break;
			}
			
			for( Ibud ib : iblist ) {
				System.err.println( ib );
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void createModels( JTable table, JTable ptable ) {
		TableModel model = new TableModel() {
			@Override
			public void addTableModelListener(TableModelListener l) {}

			@Override
			public Class<?> getColumnClass(int columnIndex) {
				if( columnIndex == 0 ) return String.class;
				else if( columnIndex == 3 ) return Date.class;
				return Integer.class; 
			}

			@Override
			public int getColumnCount() {
				return 4;
			}

			@Override
			public String getColumnName(int columnIndex) {
				switch( columnIndex ) {
				case 0:
					return "Gata";
				case 1:
					return "Fermetrar";
				case 2:
					return "Herbergi";
				case 3:
					return "Dagsetning";
				default:
					return "";
				}
			}

			@Override
			public int getRowCount() {
				return iblist.size();
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				Ibud ib = iblist.get( rowIndex );
				switch( columnIndex ) {
				case 0:
					return ib.nafn;
				case 1:
					return ib.ferm;
				case 2:
					return ib.herb;
				case 3:
					return ib.dat;
				default:
					return null;
				}
			}

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return false;
			}

			@Override
			public void removeTableModelListener(TableModelListener l) {}

			@Override
			public void setValueAt(Object aValue, int rowIndex, int columnIndex) {}
		};
		table.setModel( model );
		
		TableModel	pmodel = new TableModel() {
			@Override
			public void addTableModelListener(TableModelListener l) {}

			@Override
			public Class<?> getColumnClass(int columnIndex) {
				return Double.class;
			}

			@Override
			public int getColumnCount() {
				return 6;
			}

			@Override
			public String getColumnName(int columnIndex) {
				switch( columnIndex ) {
					case 0:
						return "Verð";
					case 1:
						return "Fasteignamat";
					case 2:
						return "Brunabótamat";
					case 3:
						return "Fermetraverð";
					case 4:
						return "Fermetraverð fasteignamats";
					case 5:
						return "Verð/fasteignamat";
					default:
						return "";
				}
			}

			@Override
			public int getRowCount() {
				return iblist.size();
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				Ibud ib = iblist.get(rowIndex);
				if( columnIndex == 0 ) {
					return (double)ib.verd;
				} else if( columnIndex == 1 ) {
					return (double)ib.fastm;
				} else if( columnIndex == 2 ) {
					return (double)ib.brunm;
				} else if( columnIndex == 3 ) {
					return (double)ib.verd/(double)ib.ferm;
				} else if( columnIndex == 4 ) {
					return (double)ib.fastm/(double)ib.ferm;
				} else if( columnIndex == 5 ) {
					return (double)ib.verd/(double)ib.fastm;
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
			public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
				
			}
		};
		ptable.setModel( pmodel );
	}
	
	public void excelExport() throws IOException {
		File f = File.createTempFile("tmp", ".xlsx");
		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFSheet sheet = wb.createSheet("Fasteignir");
		int i = 0;
		for( Ibud ib : iblist ) {
			XSSFRow row = sheet.createRow( i++ );
			int c = 0;
			XSSFCell cell = row.createCell(c++);
			cell.setCellValue( ib.nafn );
			cell = row.createCell(c++);
			cell.setCellValue( ib.verd );
			cell = row.createCell(c++);
			cell.setCellValue( ib.fastm );
			cell = row.createCell(c++);
			cell.setCellValue( ib.brunm );
			cell = row.createCell(c++);
			cell.setCellValue( ib.teg );
			cell = row.createCell(c++);
			cell.setCellValue( ib.ferm );
			cell = row.createCell(c++);
			cell.setCellValue( ib.herb );
			cell = row.createCell(c++);
			cell.setCellValue( ib.dat );
		}
		wb.write( new FileOutputStream(f) );
		Desktop.getDesktop().open( f );
	}
	
	List<Ibud>	iblist = new ArrayList<Ibud>();
	String		base = "http://www.mbl.is/mm/fasteignir/leit.html?offset;svaedi=&tegund=&fermetrar_fra=&fermetrar_til=&herbergi_fra=&herbergi_til=&verd_fra=5&verd_til=100&gata=&lysing=";
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
		
		this.setLayout( new BorderLayout() );
		
		JComponent	topcomp = new JComponent() {
			
		};
		topcomp.setLayout( new FlowLayout() );
		
		JLabel 			title = new JLabel("Hvað á íbúðin að kosta?");
		topcomp.add( title );
		
		JLabel 			loc = new JLabel("Veldu svæði:");
		topcomp.add( loc );
		final JComboBox		loccomb = new JComboBox();
		loccomb.addItem("101 Miðbær");
		loccomb.addItem("103 Kringlan/Hvassaleiti");
		loccomb.addItem("104 Vogar");
		loccomb.addItem("105 Austurbær");
		loccomb.addItem("107 Vesturbær");
		loccomb.addItem("108 Austurbær");
		loccomb.addItem("109 Bakkar/Seljahverfi");
		loccomb.addItem("110 Árbær/Selás");
		topcomp.add( loccomb );
		
		JLabel 			typ = new JLabel("Veldu tegund:");
		topcomp.add( typ );
		final JComboBox		typcomb = new JComboBox();
		typcomb.addItem("Fjölbýli");
		typcomb.addItem("Einbýli");
		typcomb.addItem("Hæðir");
		typcomb.addItem("Parhús/Raðhús");
		topcomp.add( typcomb );
		
		JLabel 			big = new JLabel("Veldu stærð:");
		topcomp.add( big );
		final JTextField		bigfield = new JTextField("100");
		topcomp.add( bigfield );
		
		JLabel 			bigdiff = new JLabel("+/-");
		topcomp.add( bigdiff );
		final JTextField		bigdifffield = new JTextField("30");
		topcomp.add( bigdifffield );
		
		JComponent	botcomp = new JComponent() {
			
		};
		botcomp.setLayout( new FlowLayout() );
		
		final JProgressBar pgbar = new JProgressBar();
		botcomp.add( pgbar );
		
		JButton	excelbutton = new JButton( new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					excelExport();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		botcomp.add( excelbutton );
		
		final JTable		table = new JTable();
		final JTable		ptable = new JTable();
		ptable.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );
		
		TableRowSorter<TableModel>	rowSorter = new TableRowSorter<TableModel>();
		table.setRowSorter(sorter)
		
		JButton			button = new JButton( new AbstractAction("Leita") {
			@Override
			public void actionPerformed(ActionEvent e) {
				String loc = loccomb.getSelectedItem().toString();
				String[] split = loc.split(" ");
				String pnr = split[0];
				String val = base.replace("svaedi=", "svaedi="+pnr+"_"+pnr);
				String teg = typcomb.getSelectedItem().toString().toLowerCase();
				teg = teg.replace("æ", "ae");
				teg = teg.replace("ö", "o");
				teg = teg.replace("ý", "y");
				val = val.replace("tegund=", "tegund="+teg);
				String diffstr = bigdifffield.getText();
				int diff = Integer.parseInt( diffstr );
				int ferm = Integer.parseInt( bigfield.getText() );
				val = val.replace("fermetrar_fra=", "fermetrar_fra="+(ferm-diff));
				val = val.replace("fermetrar_til=", "fermetrar_til="+(ferm+diff));
				
				final String tstr = val;
				pgbar.setIndeterminate( true );
				Thread t = new Thread() {
					public void run() {
						calc( tstr );
						createModels( table, ptable );
						pgbar.setIndeterminate( false );
					}
				};
				t.start();
			}
		});
		topcomp.add( button );
		
		this.add( topcomp, BorderLayout.NORTH );
		this.add( botcomp, BorderLayout.SOUTH );
		
		JSplitPane	splitpane = new JSplitPane();
		JScrollPane	scrollpane = new JScrollPane();
		JScrollPane	pricepane = new JScrollPane();
		
		scrollpane.setViewportView( table );
		pricepane.setViewportView( ptable );
		
		splitpane.setLeftComponent( scrollpane );
		splitpane.setRightComponent( pricepane );
		
		this.add(splitpane);
		
		//calc();
	}
}
