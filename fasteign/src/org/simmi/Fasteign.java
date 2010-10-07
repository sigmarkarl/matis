package org.simmi;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.RowSorterListener;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Fasteign extends JApplet {
	MySorter currentSorter;
	Image xlimg;
	JTable medtable;
	JTable medheadertable;
	boolean sel = false;
	boolean msel = false;

	static Map<String, Integer> mmap = new HashMap<String, Integer>();

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
		mmap.put("maí", 5);
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

		public Ibud(String nafn) {
			this.nafn = nafn;
		}

		public Ibud(String nafn, int verd, int fastm, int brunm, String teg, int ferm, int herb, String dat, String url) throws ParseException {
			this.nafn = nafn;
			this.verd = verd;
			this.fastm = fastm;
			this.brunm = brunm;
			this.teg = teg;
			this.ferm = ferm;
			this.herb = herb;
			this.dat = DateFormat.getDateInstance().parse(dat);
		}

		public void set(int i, Object obj) {
			try {
				if (obj instanceof String) {
					String val = obj.toString();
					val = val.replaceAll("\\.", "");
					if (i == 0)
						verd = Integer.parseInt(val);
					else if (i == 1)
						fastm = Integer.parseInt(val);
					else if (i == 2)
						brunm = Integer.parseInt(val);
					else if (i == 3)
						teg = val;
					else if (i == 4)
						ferm = Integer.parseInt(val);
					else if (i == 5)
						herb = Integer.parseInt(val);
					else if (i == 6) {
						String[] split = val.split(" ");
						if (split.length >= 3 && mmap.containsKey(split[1])) {
							int year = Integer.parseInt(split[2]);
							int month = mmap.get(split[1]);
							int day = Integer.parseInt(split[0]);
							Calendar cal = Calendar.getInstance();
							cal.set(year, month - 1, day);
							dat = cal.getTime();
						}

					}
				} // else dat = (Date)obj;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public String getUrlString() {
			return url;
		}
		
		public boolean equals( Object o ) {
			return o instanceof Ibud && url.equals( ((Ibud)o).url );
		}

		public String toString() {
			return nafn + "\t" + verd + "\t" + fastm + "\t" + brunm + "\t" + teg + "\t" + ferm + "\t" + herb + "\t" + dat + "\t" + url;
		}
	}

	public boolean stuff(String urlstr) throws IOException, InterruptedException {
		URL url = new URL(urlstr);
		InputStream stream = url.openStream();

		String str = "";
		byte[] bb = new byte[1024];
		int r = stream.read(bb);
		while (r > 0) {
			str += new String(bb, 0, r);
			r = stream.read(bb);
		}

		stream.close();

		String[] buds = { "estate-verd", "estate-fasteignamat", "estate-brunabotamat", "estate-teg_eign", "estate-fermetrar", "estate-fjoldi_herb", "estate-sent_dags" };

		int count = 0;

		String[] vals = str.split("fast-nidurstada clearfix");
		System.err.println(vals.length);
		String h2 = "<h2 style=\"margin-bottom: 0.91em; font-size:1.5em;\">";
		for (String val : vals) {
			int ind = val.indexOf("<a href=\"");
			int stop = val.indexOf("\"", ind + 10);

			String sub = val.substring(ind + 9, stop);
			if (sub.contains("/mm/fasteignir")) {
				String suburlstr = "http://www.mbl.is" + sub;
				url = new URL(suburlstr);
				stream = url.openStream();

				str = "";
				r = stream.read(bb);
				while (r > 0) {
					str += new String(bb, 0, r, "ISO-8859-1");
					r = stream.read(bb);
				}
				stream.close();

				ind = str.indexOf(h2);
				stop = str.indexOf("</h2>", ind);
				String ibud = str.substring(ind + h2.length(), stop).trim();
				Ibud ib = new Ibud(ibud);
				ib.url = suburlstr;
				if( !iblist.contains(ib) ) {
					iblist.add(ib);
					int i = 0;
					for (String bud : buds) {
						ind = str.indexOf(bud);
						int start = str.indexOf("fst-rvalue\">", ind);
						stop = str.indexOf("</td>", start);
						String sval = str.substring(start + 12, stop).trim();
	
						ib.set(i++, sval);
					}
				}
				count++;
			}

			Thread.sleep(200);
		}

		return count == 25;
	}

	public void calc(String urlstr) {
		try {
			// String base =
			// "http://www.mbl.is/mm/fasteignir/leit.html?simmi;svaedi=101_101&tegund=fjolbyli&tegund=einbyli&tegund=haedir&fermetrar_fra=70&fermetrar_til=150&herbergi_fra=&herbergi_til=&verd_fra=10&verd_til=40&gata=&lysing=";
			// String base =
			// "http://www.mbl.is/mm/fasteignir/leit.html?simmi;svaedi=200_200&svaedi=201_201&svaedi=202_202&svaedi=203_203&tegund=fjolbyli&fermetrar_fra=70&fermetrar_til=150&herbergi_fra=&herbergi_til=&verd_fra=10&verd_til=40&gata=&lysing=";
			// String base =
			// "http://www.mbl.is/mm/fasteignir/leit.html?simmi;svaedi=108_108&tegund=fjolbyli&fermetrar_fra=70&fermetrar_til=150&herbergi_fra=&herbergi_til=&verd_fra=10&verd_til=40&gata=&lysing=";

			int i = 0;
			while (true) {
				if (!stuff(urlstr.replace("offset", "offset=" + i)))
					break;
				i += 25;
			}

			for (Ibud ib : iblist) {
				System.err.println(ib);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public class MyFilter extends RowFilter<TableModel, Integer> {
		String filterText;
		int fInd = 0;
		Set<String> cropped = new HashSet<String>();
		TableModel leftModel;

		public MyFilter(TableModel leftModel) {
			super();
			this.leftModel = leftModel;
		}

		public boolean include(javax.swing.RowFilter.Entry<? extends TableModel, ? extends Integer> entry) {
			String gval = (String) leftModel.getValueAt(entry.getIdentifier(), 0);
			String val = fInd == 0 ? gval : (String) leftModel.getValueAt(entry.getIdentifier(), 1);
			if (filterText != null) {
				if (val != null) {
					boolean b = val.matches(filterText);
					// if( b ) System.err.println( val + "  " + filterText +
					// "  " + b );
					return b;
				}
				return false;
			} else {
				boolean b = cropped.contains(gval);
				if (b)
					return false;
			}
			return true;
		}
	};

	public class MySorter extends TableRowSorter<TableModel> {
		public MySorter(TableModel model) {
			super(model);
		}

		public int convertRowIndexToModel(int index) {
			if (currentSorter != null) {
				return currentSorter.convertRowIndexToModelSuper(index);
			}
			return index;
		}

		public int convertRowIndexToView(int index) {
			if (currentSorter != null) {
				return currentSorter.convertRowIndexToViewSuper(index);
			}
			return index;
		}

		public int convertRowIndexToModelSuper(int index) {
			return super.convertRowIndexToModel(index);
		}

		public int convertRowIndexToViewSuper(int index) {
			return super.convertRowIndexToView(index);
		}

		public void setRowFilter(MyFilter filter) {
			super.setRowFilter(filter);
		}
	}

	public void createModels(final JTable table, final JTable ptable) {
		TableModel model = new TableModel() {
			@Override
			public void addTableModelListener(TableModelListener l) {
			}

			@Override
			public Class<?> getColumnClass(int columnIndex) {
				if (columnIndex == 0)
					return String.class;
				else if (columnIndex == 3)
					return Date.class;
				return Integer.class;
			}

			@Override
			public int getColumnCount() {
				return 4;
			}

			@Override
			public String getColumnName(int columnIndex) {
				switch (columnIndex) {
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
				Ibud ib = iblist.get(rowIndex);
				switch (columnIndex) {
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
			public void removeTableModelListener(TableModelListener l) {
			}

			@Override
			public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			}
		};
		table.setModel(model);
		final MySorter rowSorter = new MySorter(model);
		table.setRowSorter(rowSorter);

		rowSorter.addRowSorterListener(new RowSorterListener() {
			@Override
			public void sorterChanged(RowSorterEvent e) {
				currentSorter = rowSorter;
				ptable.repaint();
			}
		});

		currentSorter = rowSorter;

		TableModel pmodel = new TableModel() {
			@Override
			public void addTableModelListener(TableModelListener l) {
			}

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
				switch (columnIndex) {
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
				if (columnIndex == 0) {
					return (double) ib.verd;
				} else if (columnIndex == 1) {
					return (double) ib.fastm;
				} else if (columnIndex == 2) {
					return (double) ib.brunm;
				} else if (columnIndex == 3) {
					return (double) ib.verd / (double) ib.ferm;
				} else if (columnIndex == 4) {
					return (double) ib.fastm / (double) ib.ferm;
				} else if (columnIndex == 5) {
					return (double) ib.verd / (double) ib.fastm;
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
		ptable.setModel(pmodel);
		final MySorter prowSorter = new MySorter(pmodel);
		ptable.setRowSorter(prowSorter);

		prowSorter.addRowSorterListener(new RowSorterListener() {
			@Override
			public void sorterChanged(RowSorterEvent e) {
				currentSorter = prowSorter;
				table.repaint();
			}
		});

		// medtable.tableChanged( new TableModelEvent( medtable.getModel() ) );
		medtable.setModel(new TableModel() {
			@Override
			public void addTableModelListener(TableModelListener l) {
			}

			@Override
			public Class<?> getColumnClass(int columnIndex) {
				return Double.class;
			}

			@Override
			public int getColumnCount() {
				return ptable.getColumnCount();
			}

			@Override
			public String getColumnName(int columnIndex) {
				return null;
			}

			@Override
			public int getRowCount() {
				return 2;
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				TableModel pmodel = ptable.getModel();
				if (pmodel != null) {
					int[] rr = ptable.getSelectedRows();
					if (rr.length > 1) {
						int r = rr.length;
						if (rowIndex == 0) {
							double retval = 0.0;
							for (int i = 0; i < r; i++) {
								double val = (Double) ptable.getValueAt(rr[i], columnIndex);
								if (val > 0 && val != Double.POSITIVE_INFINITY) {
									retval += val;
								} else
									r--;
							}
							if (r > 0)
								return retval / r;
						} else {
							List<Double> ld = new ArrayList<Double>();
							for (int i = 0; i < r; i++) {
								double d = (Double) ptable.getValueAt(rr[i], columnIndex);
								if (d > 0)
									ld.add(d);
							}
							Collections.sort(ld);

							if (ld.size() % 2 == 0) {
								return (ld.get(ld.size() / 2 - 1) + ld.get(ld.size() / 2)) / 2;
							} else {
								return ld.get(ld.size() / 2);
							}
						}
					} else {
						int r = ptable.getRowCount();
						if (rowIndex == 0) {
							double retval = 0.0;
							for (int i = 0; i < r; i++) {
								double val = (Double) ptable.getValueAt(i, columnIndex);
								if (val > 0 && val != Double.POSITIVE_INFINITY) {
									retval += val;
								} else
									r--;
							}
							if (r > 0)
								return retval / r;
						} else {
							List<Double> ld = new ArrayList<Double>();
							for (int i = 0; i < r; i++) {
								double d = (Double) ptable.getValueAt(i, columnIndex);
								if (d > 0)
									ld.add(d);
							}
							Collections.sort(ld);

							if (ld.size() % 2 == 0) {
								return (ld.get(ld.size() / 2 - 1) + ld.get(ld.size() / 2)) / 2;
							} else {
								return ld.get(ld.size() / 2);
							}
						}
					}
				}

				return -1.0;
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
		});

		medheadertable.setModel(new TableModel() {
			@Override
			public int getRowCount() {
				return 2;
			}

			@Override
			public int getColumnCount() {
				return 1;
			}

			@Override
			public String getColumnName(int columnIndex) {
				return "Reiknað";
			}

			@Override
			public Class<?> getColumnClass(int columnIndex) {
				return String.class;
			}

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return false;
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				return rowIndex == 0 ? "Meðaltal" : "Miðgildi";
			}

			@Override
			public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			}

			@Override
			public void addTableModelListener(TableModelListener l) {
			}

			@Override
			public void removeTableModelListener(TableModelListener l) {
			}
		});
	}

	public void excelExport() throws IOException {
		File f = File.createTempFile("tmp", ".xlsx");
		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFSheet sheet = wb.createSheet("Fasteignir");
		int i = 0;
		int c = 0;
		XSSFRow row = sheet.createRow(i++);
		XSSFCell cell = row.createCell(c++);
		cell.setCellValue("Nafn");
		cell = row.createCell(c++);
		cell.setCellValue("Verð");
		cell = row.createCell(c++);
		cell.setCellValue("Fasteignamat");
		cell = row.createCell(c++);
		cell.setCellValue("Brunabótamat");
		cell = row.createCell(c++);
		cell.setCellValue("Tegund");
		cell = row.createCell(c++);
		cell.setCellValue("Fermetrar");
		cell = row.createCell(c++);
		cell.setCellValue("Herbergi");
		cell = row.createCell(c++);
		cell.setCellValue("Dagsetning");
		cell = row.createCell(c++);
		cell.setCellValue("Slóð");
		for (Ibud ib : iblist) {
			row = sheet.createRow(i++);
			c = 0;
			cell = row.createCell(c++);
			cell.setCellValue(ib.nafn);
			cell = row.createCell(c++);
			cell.setCellValue(ib.verd);
			cell = row.createCell(c++);
			cell.setCellValue(ib.fastm);
			cell = row.createCell(c++);
			cell.setCellValue(ib.brunm);
			cell = row.createCell(c++);
			cell.setCellValue(ib.teg);
			cell = row.createCell(c++);
			cell.setCellValue(ib.ferm);
			cell = row.createCell(c++);
			cell.setCellValue(ib.herb);
			cell = row.createCell(c++);
			cell.setCellValue(ib.dat.toString());
			cell = row.createCell(c++);
			cell.setCellValue(ib.url);
		}
		wb.write(new FileOutputStream(f));
		Desktop.getDesktop().open(f);
	}

	public Fasteign() {
		URL url = this.getClass().getResource("/xlsx.png");
		try {
			xlimg = ImageIO.read(url);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void stuff(int i) throws IOException, URISyntaxException {
		Ibud ib = iblist.get(i);
		String uristr = ib.getUrlString();
		if (uristr != null) {
			URI uri = new URI(uristr);
			Desktop.getDesktop().browse(uri);
		}
	}
	
	public void henda( JTable table, JTable ptable ) {
		Set<Ibud> ibs = new HashSet<Ibud>();
		int[] rr = table.getSelectedRows();
		for (int r : rr) {
			int realr = table.convertRowIndexToModel(r);
			if (realr != -1) {
				ibs.add(iblist.get(realr));
			}
		}
		iblist.removeAll(ibs);
		table.tableChanged(new TableModelEvent(table.getModel()));
		ptable.tableChanged(new TableModelEvent(ptable.getModel()));
		medtable.tableChanged(new TableModelEvent(medtable.getModel()));
	}

	List<Ibud> iblist = new ArrayList<Ibud>();
	String base = "http://www.mbl.is/mm/fasteignir/leit.html?offset;svaedi=&tegund=&fermetrar_fra=&fermetrar_til=&herbergi_fra=&herbergi_til=&verd_fra=5&verd_til=100&gata=&lysing=";

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

		this.setBackground(Color.white);
		this.getContentPane().setBackground(Color.white);

		this.setLayout(new BorderLayout());

		JComponent topcomp = new JComponent() {

		};
		topcomp.setLayout(new FlowLayout());

		// JLabel title = new JLabel("Hvað á íbúðin að kosta?");
		// topcomp.add(title);

		JLabel loc = new JLabel("Veldu svæði:");
		topcomp.add(loc);
		final JComboBox loccomb = new JComboBox();
		loccomb.addItem("101 Miðbær");
		loccomb.addItem("103 Kringlan/Hvassaleiti");
		loccomb.addItem("104 Vogar");
		loccomb.addItem("105 Austurbær");
		loccomb.addItem("107 Vesturbær");
		loccomb.addItem("108 Austurbær");
		loccomb.addItem("109 Bakkar/Seljahverfi");
		loccomb.addItem("110 Árbær/Selás");
		loccomb.addItem("111 Berg/Hólar/Fell");
		loccomb.addItem("112 Grafarvogur");
		loccomb.addItem("113 Grafarholt");
		loccomb.addItem("116 Kjalarnes");
		loccomb.addItem("170 Seltjarnarnes");
		loccomb.addItem("190 Vogar");
		loccomb.addItem("110 Árbær/Selás");
		loccomb.addItem("200 Kópavogur");
		loccomb.addItem("201 Kópavogur");
		loccomb.addItem("202 Kópavogur");
		loccomb.addItem("203 Kópavogur");
		loccomb.addItem("210 Garðabær");
		loccomb.addItem("211 Garðabær (Arnarnes)");
		loccomb.addItem("220 Hafnarfjörður");
		loccomb.addItem("221 Hafnarfjörður");
		loccomb.addItem("225 Álftanes");
		loccomb.addItem("220 Hafnarfjörður");
		topcomp.add(loccomb);

		JLabel typ = new JLabel("Veldu tegund:");
		topcomp.add(typ);
		final JComboBox typcomb = new JComboBox();
		typcomb.addItem("Fjölbýli");
		typcomb.addItem("Einbýli");
		typcomb.addItem("Hæðir");
		typcomb.addItem("Parhús/Raðhús");
		topcomp.add(typcomb);

		JLabel big = new JLabel("Veldu stærð:");
		topcomp.add(big);
		final JTextField bigfield = new JTextField("100");
		topcomp.add(bigfield);

		JLabel bigdiff = new JLabel("+/-");
		topcomp.add(bigdiff);
		final JTextField bigdifffield = new JTextField("30");
		topcomp.add(bigdifffield);

		JComponent botcomp = new JComponent() {};
		botcomp.setLayout(new FlowLayout());

		final JProgressBar pgbar = new JProgressBar();
		botcomp.add(pgbar);
		JComponent c = new JComponent() {};
		c.setPreferredSize( new Dimension(500,30) );
		botcomp.add( c );

		JButton excelbutton = new JButton(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					excelExport();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		excelbutton.setToolTipText("Opna töflu í Excel");
		excelbutton.setIcon(new ImageIcon(xlimg.getScaledInstance(24, 24, Image.SCALE_SMOOTH)));
		botcomp.add( new JLabel("Opna töflu í Excel") );
		botcomp.add(excelbutton);

		final JTable table = new JTable() {
			public void setRowSelectionInterval(int r1, int r2) {
				sel = false;
				super.setRowSelectionInterval(r1, r2);
			}

			public void setColumnSelectionInterval(int c1, int c2) {
				sel = false;
				super.setColumnSelectionInterval(c1, c2);
			}

			public void addRowSelectionInterval(int r1, int r2) {
				sel = false;
				super.addRowSelectionInterval(r1, r2);
			}

			public void addColumnSelectionInterval(int c1, int c2) {
				sel = false;
				super.addColumnSelectionInterval(c1, c2);
			}
		};

		final JTable ptable = new JTable() {
			public void setRowSelectionInterval(int r1, int r2) {
				sel = true;
				super.setRowSelectionInterval(r1, r2);
			}

			public void setColumnSelectionInterval(int c1, int c2) {
				sel = true;
				super.setColumnSelectionInterval(c1, c2);
			}

			public void addRowSelectionInterval(int r1, int r2) {
				sel = true;
				super.addRowSelectionInterval(r1, r2);
			}

			public void addColumnSelectionInterval(int c1, int c2) {
				sel = true;
				super.addColumnSelectionInterval(c1, c2);
			}
		};

		JPopupMenu popup = new JPopupMenu();
		popup.add(new AbstractAction("Henda") {
			@Override
			public void actionPerformed(ActionEvent e) {
				henda( table, ptable );
			}
		});
		table.setComponentPopupMenu(popup);
		ptable.setComponentPopupMenu(popup);
		
		table.addKeyListener( new KeyAdapter() {
			public void keyPressed( KeyEvent e ) {
				if( e.getKeyCode() == KeyEvent.VK_DELETE ) henda( table, ptable );
			}
		});

		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				boolean ss = sel;
				sel = true;
				if (ss) {
					int[] rr = table.getSelectedRows();
					if (rr.length > 1)
						medtable.tableChanged(new TableModelEvent(medtable.getModel()));
					if (rr != null && rr.length > 0) {
						for (int r : rr) {
							if (r == rr[0])
								ptable.setRowSelectionInterval(r, r);
							else
								ptable.addRowSelectionInterval(r, r);
							sel = true;
						}
					}
				}
			}
		});

		table.getColumnModel().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				boolean ss = sel;
				sel = true;
				if (ss) {
					int[] rr = table.getSelectedRows();
					if (rr.length > 1)
						medtable.tableChanged(new TableModelEvent(medtable.getModel()));
					if (rr != null && rr.length > 0) {
						for (int r : rr) {
							if (r == rr[0])
								ptable.setRowSelectionInterval(r, r);
							else
								ptable.addRowSelectionInterval(r, r);
							sel = true;
						}
					}
				}
			}
		});

		ptable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				boolean ss = sel;
				sel = false;
				if (!ss) {
					int[] rr = ptable.getSelectedRows();
					if (rr.length > 1)
						medtable.tableChanged(new TableModelEvent(medtable.getModel()));
					if (rr != null && rr.length > 0) {
						for (int r : rr) {
							if (r == rr[0])
								table.setRowSelectionInterval(r, r);
							else
								table.addRowSelectionInterval(r, r);
							sel = false;
						}
					}
				}
			}
		});

		ptable.getColumnModel().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				boolean ss = sel;
				sel = false;
				if (!ss) {
					int[] rr = ptable.getSelectedRows();
					if (rr.length > 1)
						medtable.tableChanged(new TableModelEvent(medtable.getModel()));
					if (rr != null && rr.length > 0) {
						for (int r : rr) {
							if (r == rr[0])
								table.setRowSelectionInterval(r, r);
							else
								table.addRowSelectionInterval(r, r);
							sel = false;
						}
					}
				}
			}
		});
		// ptable.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );

		table.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				sel = true;
				if (e.getClickCount() == 2) {
					int i = table.convertRowIndexToModel(table.getSelectedRow());
					try {
						stuff(i);
					} catch (IOException e1) {
						e1.printStackTrace();
					} catch (URISyntaxException e1) {
						e1.printStackTrace();
					}
				}
			}
		});

		ptable.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				sel = false;
				if (e.getClickCount() == 2) {
					int i = ptable.convertRowIndexToModel(ptable.getSelectedRow());
					try {
						stuff(i);
					} catch (IOException e1) {
						e1.printStackTrace();
					} catch (URISyntaxException e1) {
						e1.printStackTrace();
					}
				}
			}
		});

		ptable.getColumnModel().addColumnModelListener(new TableColumnModelListener() {
			public void columnAdded(TableColumnModelEvent e) {
			}

			public void columnMarginChanged(ChangeEvent e) {
				Enumeration<TableColumn> tcs = ptable.getColumnModel().getColumns();
				int i = 0;
				while (tcs.hasMoreElements()) {
					TableColumn tc = tcs.nextElement();
					TableColumnModel tcm = medtable.getColumnModel();
					if( i < tcm.getColumnCount() ) {
						tcm.getColumn(i).setPreferredWidth(tc.getPreferredWidth());
					}
					i++;
				}
			}

			public void columnMoved(TableColumnModelEvent e) {
				medtable.moveColumn(e.getFromIndex(), e.getToIndex());
			}

			public void columnRemoved(TableColumnModelEvent e) {
			}

			public void columnSelectionChanged(ListSelectionEvent e) {
			}
		});

		JButton button = new JButton(new AbstractAction("Leita") {
			@Override
			public void actionPerformed(ActionEvent e) {
				String loc = loccomb.getSelectedItem().toString();
				String[] split = loc.split(" ");
				String pnr = split[0];
				String val = base.replace("svaedi=", "svaedi=" + pnr + "_" + pnr);
				String teg = typcomb.getSelectedItem().toString().toLowerCase();
				teg = teg.replace("æ", "ae");
				teg = teg.replace("ö", "o");
				teg = teg.replace("ý", "y");
				val = val.replace("tegund=", "tegund=" + teg);
				String diffstr = bigdifffield.getText();
				int diff = Integer.parseInt(diffstr);
				int ferm = Integer.parseInt(bigfield.getText());
				val = val.replace("fermetrar_fra=", "fermetrar_fra=" + (ferm - diff));
				val = val.replace("fermetrar_til=", "fermetrar_til=" + (ferm + diff));

				final String tstr = val;
				pgbar.setIndeterminate(true);
				Thread t = new Thread() {
					public void run() {
						calc(tstr);
						createModels(table, ptable);
						pgbar.setIndeterminate(false);
					}
				};
				t.start();
			}
		});
		topcomp.add(button);

		this.add(topcomp, BorderLayout.NORTH);
		// this.add( botcomp, BorderLayout.SOUTH );

		JSplitPane splitpane = new JSplitPane();
		JScrollPane scrollpane = new JScrollPane();
		JScrollPane pricepane = new JScrollPane();
		scrollpane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);

		// scrollpane.setViewportView( table );
		pricepane.setViewportView(ptable);
		pricepane.setRowHeaderView(table);
		scrollpane.setViewport(pricepane.getRowHeader());
		pricepane.getViewport().setBackground(Color.white);
		scrollpane.getViewport().setBackground(Color.white);

		ptable.getColumnModel().addColumnModelListener(new TableColumnModelListener() {
			@Override
			public void columnSelectionChanged(ListSelectionEvent e) {
			}

			@Override
			public void columnRemoved(TableColumnModelEvent e) {
			}

			@Override
			public void columnMoved(TableColumnModelEvent e) {
			}

			@Override
			public void columnMarginChanged(ChangeEvent e) {
				Enumeration<TableColumn> tcs = ptable.getColumnModel().getColumns();
				int i = 0;
				while (tcs.hasMoreElements()) {
					TableColumn tc = tcs.nextElement();
					// topTable.getColumnModel().getColumn(i++).setPreferredWidth(tc.getPreferredWidth());
				}

				/*
				 * public void columnMoved(TableColumnModelEvent e) {
				 * topTable.moveColumn(e.getFromIndex(), e.getToIndex()); }
				 * 
				 * public void columnRemoved(TableColumnModelEvent e) {}
				 * 
				 * public void columnSelectionChanged(ListSelectionEvent e) { }
				 */
			}

			@Override
			public void columnAdded(TableColumnModelEvent e) {
			}
		});

		// Dimension d = new Dimension(100,40);

		medheadertable = new JTable() {
			public void setRowSelectionInterval(int r1, int r2) {
				sel = true;
				super.setRowSelectionInterval(r1, r2);
			}

			public void setColumnSelectionInterval(int c1, int c2) {
				sel = true;
				super.setColumnSelectionInterval(c1, c2);
			}

			public void addRowSelectionInterval(int r1, int r2) {
				sel = true;
				super.addRowSelectionInterval(r1, r2);
			}

			public void addColumnSelectionInterval(int c1, int c2) {
				sel = true;
				super.addColumnSelectionInterval(c1, c2);
			}
		};
		// medheadertable.setPreferredSize( d );
		medtable = new JTable() {
			public void setRowSelectionInterval(int r1, int r2) {
				sel = false;
				super.setRowSelectionInterval(r1, r2);
			}

			public void setColumnSelectionInterval(int c1, int c2) {
				sel = false;
				super.setColumnSelectionInterval(c1, c2);
			}

			public void addRowSelectionInterval(int r1, int r2) {
				sel = false;
				super.addRowSelectionInterval(r1, r2);
			}

			public void addColumnSelectionInterval(int c1, int c2) {
				sel = false;
				super.addColumnSelectionInterval(c1, c2);
			}
		};

		medtable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				boolean ss = msel;
				msel = true;
				if (ss) {
					int[] rr = medtable.getSelectedRows();
					if (rr != null && rr.length > 0) {
						for (int r : rr) {
							if (r == rr[0])
								medheadertable.setRowSelectionInterval(r, r);
							else
								medheadertable.addRowSelectionInterval(r, r);
							msel = true;
						}
					}
				}
			}
		});

		medheadertable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				boolean ss = msel;
				msel = false;
				if (!ss) {
					int[] rr = medheadertable.getSelectedRows();
					if (rr != null && rr.length > 0) {
						for (int r : rr) {
							if (r == rr[0])
								medtable.setRowSelectionInterval(r, r);
							else
								medtable.addRowSelectionInterval(r, r);
							msel = false;
						}
					}
				}
			}
		});

		// medtable.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );
		JScrollPane medscroll = new JScrollPane();
		// medtable.setTableHeader( null );
		// medscroll.setPreferredSize( d );

		JComponent comp = new JComponent() {
		};
		comp.setLayout(new BorderLayout());
		comp.add(pricepane);
		comp.add(medtable, BorderLayout.SOUTH);

		JComponent pcomp = new JComponent() {
		};
		pcomp.setLayout(new BorderLayout());
		pcomp.add(scrollpane);
		pcomp.add(medheadertable, BorderLayout.SOUTH);

		this.add(botcomp, BorderLayout.SOUTH);

		splitpane.setLeftComponent(pcomp);
		splitpane.setRightComponent(comp);

		this.add(splitpane);

		// calc();
	}
}
