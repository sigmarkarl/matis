package org.simmi;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Graphics;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JApplet;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Report extends JApplet {
	JComponent 		pdfComp;
	JComponent 		xlsComp;
	BufferedImage 	pdfImg;
	BufferedImage 	xlsImg;
	XSSFWorkbook 	workbook;

	public Report() {
		super();

		try {
			xlsImg = ImageIO.read(this.getClass().getResource("/xlsx.png"));
			pdfImg = ImageIO.read(this.getClass().getResource("/pdf.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Report(String[] args) {
		super();

		/*
		 * try { loadXlsx("/home/sigmar/Desktop/Kennitölur 270509.xlsx"); }
		 * catch (IOException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 */

		try {
			load(args[0]);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	class Job {
		String no;
		String desc;
		String boss;
		Date start;
		Date end;

		public Job(String no, String desc, String boss, Date start, Date end) {
			this.no = no;
			this.desc = desc;
			this.boss = boss;
			this.start = start;
			this.end = end;
		}
	};

	class Cost {
		long no;
		String type;
		String name;
		double c;
		double p;

		public Cost(long no, String type, String name, double c, double p) {
			this.no = no;
			this.type = type;
			this.name = name;
			this.c = c;
			this.p = p;
		}
	};

	Map<String, Cost> costMap = new HashMap<String, Cost>();
	List<Cost> costList = new ArrayList<Cost>();

	public void loadXlsx(String fname) throws IOException {
		XSSFWorkbook workbook = new XSSFWorkbook(fname);
		XSSFSheet sheet = workbook.getSheet("Skýrsla sviðstjóra v2");
		XSSFRow row = sheet.getRow(12);

		if (row == null)
			row = sheet.createRow(12);
		XSSFCell cell = row.getCell(2);
		if (cell == null)
			cell = row.createCell(12);
		cell.setCellType(Cell.CELL_TYPE_STRING);
		cell.setCellValue("siiiiimmmmmmmmmmmmmmmmmmmmmmmmmmmmmi");

		workbook
				.write(new FileOutputStream("/home/sigmar/Desktop/simmi2.xlsx"));
	}

	public void load(String filename) throws ClassNotFoundException,
			SQLException, IOException {
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

		String connectionUrl = "jdbc:sqlserver://navision.rf.is:1433;databaseName=MATIS;user=simmi;password=drsmorc.311;";
		Connection con = DriverManager.getConnection(connectionUrl);

		System.err.println("blind "+filename);
		try {
		workbook = new XSSFWorkbook(filename);
		} catch( Exception e ) {
			e.printStackTrace();
		}
		XSSFSheet sheet = workbook.getSheet("Skýrsla sviðstjóra v2");
		XSSFRow row = sheet.getRow(2);

		System.err.println("sko");
		
		List<Object> jobstr = new ArrayList<Object>();
		int i = 2;
		XSSFCell cell = row.getCell(i);
		while (cell != null
				&& ((cell.getCellType() == Cell.CELL_TYPE_NUMERIC && cell
						.getNumericCellValue() > 0) || (cell.getCellType() == Cell.CELL_TYPE_STRING && cell
						.getStringCellValue().length() > 0))) {
			if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
				jobstr.add((int) cell.getNumericCellValue());
			} else if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
				String val = cell.getStringCellValue();
				if (!val.equals("xxx"))
					jobstr.add(val);
			}
			i += 2;
			cell = row.getCell(i);
		}

		String str = "(";
		for (Object o : jobstr) {
			if (o instanceof Integer) {
				str += "'" + Integer.toString((Integer) o) + "'";
			} else if (o instanceof String) {
				String res = (String) o;
				str += "'" + res + "'";
			}
			if (!o.equals(jobstr.get(jobstr.size() - 1)))
				str += ", ";
		}
		str += ")";

		System.err.println(str);

		String sql = "select * from dbo.job_excel where No_ in " + str;
		PreparedStatement ps = con.prepareStatement(sql);
		ResultSet rs = ps.executeQuery();

		// List<Job> jobs = new ArrayList<Job>();
		i = 0;
		while (rs.next()) {
			row = sheet.getRow(3);
			cell = row.getCell(2 + i);
			if (cell != null) {
				cell.setCellType(Cell.CELL_TYPE_STRING);
				cell.setCellValue(rs.getString(2));
			}

			row = sheet.getRow(4);
			cell = row.getCell(2 + i);
			if (cell != null) {
				cell.setCellType(Cell.CELL_TYPE_STRING);
				cell.setCellValue(rs.getString(3));
			}

			row = sheet.getRow(5);
			cell = row.getCell(2 + i);
			if (cell != null) {
				cell.setCellType(Cell.CELL_TYPE_STRING);
				cell.setCellValue(rs.getDate(4).toString());
			}

			row = sheet.getRow(6);
			cell = row.getCell(2 + i);
			if (cell != null) {
				cell.setCellType(Cell.CELL_TYPE_STRING);
				cell.setCellValue(rs.getDate(5).toString());
			}
			// jobs.add( new Job( rs.getString(1), rs.getString(2),
			// rs.getString(3), rs.getDate(4), rs.getDate(5) ) );
			i += 2;
		}
		ps.close();

		/********** plan ***************/
		int k = 3;
		for (Object o : jobstr) {
			sql = "select be.No_, be.Type, bl.Description, sum(be.\"Total Cost\"), sum(be.\"Total Price\") as Cost from dbo.\"Matís ohf_$Job Budget Entry\" be, dbo.\"Matís ohf_$Job Budget Line\" bl where be.\"Job No_\" = '"
					+ o.toString()
					+ "' and be.No_ = bl.No_ and bl.\"Job No_\" = be.\"Job No_\" and be.Date >= '2009-01-01' and be.Date < '2009-04-01' group by be.No_, be.Type, bl.Description";

			ps = con.prepareStatement(sql);
			rs = ps.executeQuery();

			costMap.clear();
			costList.clear();
			while (rs.next()) {
				String nostr = rs.getString(1);
				Cost cost = new Cost(Long.parseLong(nostr), rs.getString(2), rs
						.getString(3), rs.getDouble(4), rs.getDouble(5));
				costMap.put(nostr, cost);
				costList.add(cost);
			}

			ps.close();

			for (i = 9; i < 40; i++) {
				row = sheet.getRow(i);
				if (row != null) {
					boolean unsucc = true;

					cell = row.getCell(0);
					if (cell != null) {
						int d = (int) cell.getNumericCellValue();
						String dstr = Integer.toString(d);
						if (d > 0) {
							if (costMap.containsKey(dstr)) {
								Cost cost = costMap.get(dstr);
								cell = row.getCell(k);
								if (d >= 1000 && d < 2000)
									cell.setCellValue(cost.p);
								else
									cell.setCellValue(cost.c);
							} else {
								double tot = 0.0;

								if (dstr.endsWith("99")) {
									for (Cost c : costList) {
										if (c.no >= d - 999 && c.no < d + 1)
											tot += c.c;
									}
								} else if (dstr.endsWith("98")) {
									for (Cost c : costList) {
										if (c.no >= d - 98 && c.no < d + 2)
											tot += c.c;
									}
								} else if (dstr.equals("1993")) {
									for (Cost c : costList) {
										if (c.no >= d - 993 && c.no < d + 7)
											tot += c.p;
									}
								}
								cell = row.getCell(k);
								cell.setCellValue(tot);
							}

							unsucc = false;
						}

					}

					if (unsucc) {
						cell = row.getCell(1);
						if (cell != null
								&& cell.getCellType() == Cell.CELL_TYPE_STRING) {
							String dstr = cell.getStringCellValue();
							if (dstr.equals("Kostnaður samtals")) {
								double tot = 0.0;
								for (String no : costMap.keySet()) {
									Cost c = costMap.get(no);
									tot += c.c;
								}
								cell = row.getCell(k);
								cell.setCellValue(tot);
							} else if (dstr
									.equals("Raun launakostnaður -fjárhagsbókh")) {
								double tot = 0.0;
								for (String no : costMap.keySet()) {
									Cost c = costMap.get(no);
									if (c.type.contains("0"))
										tot += c.c;
								}
								cell = row.getCell(k);
								cell.setCellValue(tot);
							}
						}
					}
				}
			}

			sql = "select be.No_, be.Type, bl.Description, sum(be.\"Total Cost\"), sum(\"Total Price\") as Cost from dbo.\"Matís ohf_$Job Budget Entry\" be, dbo.\"Matís ohf_$Job Budget Line\" bl where be.\"Job No_\" = '"
					+ o.toString()
					+ "' and be.No_ = bl.No_ and bl.\"Job No_\" = be.\"Job No_\" group by be.No_, be.Type, bl.Description";

			ps = con.prepareStatement(sql);
			rs = ps.executeQuery();

			costMap.clear();
			costList.clear();
			while (rs.next()) {
				String nostr = rs.getString(1);
				Cost cost = new Cost(Long.parseLong(nostr), rs.getString(2), rs
						.getString(3), rs.getDouble(4), rs.getDouble(5));
				costMap.put(nostr, cost);
				costList.add(cost);
			}

			ps.close();

			for (i = 40; i < 100; i++) {
				row = sheet.getRow(i);
				if (row != null) {
					boolean unsucc = true;

					cell = row.getCell(0);
					if (cell != null) {
						int d = (int) cell.getNumericCellValue();
						String dstr = Integer.toString(d);
						if (d > 0) {
							if (costMap.containsKey(dstr)) {
								Cost cost = costMap.get(dstr);
								cell = row.getCell(k);
								if (d >= 1000 && d < 2000)
									cell.setCellValue(cost.p);
								else
									cell.setCellValue(cost.c);
							} else {
								double tot = 0.0;

								if (dstr.endsWith("99")) {
									for (Cost c : costList) {
										if (c.no >= d - 999 && c.no < d + 1)
											tot += c.c;
									}
								} else if (dstr.endsWith("98")) {
									for (Cost c : costList) {
										if (c.no >= d - 98 && c.no < d + 2)
											tot += c.c;
									}
								} else if (dstr.equals("1993")) {
									for (Cost c : costList) {
										if (c.no >= d - 993 && c.no < d + 7)
											tot += c.p;
									}
								}
								cell = row.getCell(k);
								cell.setCellValue(tot);
							}

							unsucc = false;
						}
					}

					if (unsucc) {
						cell = row.getCell(1);
						if (cell != null
								&& cell.getCellType() == Cell.CELL_TYPE_STRING) {
							String dstr = cell.getStringCellValue();
							if (dstr.equals("Kostnaður samtals")) {
								double tot = 0.0;
								for (String no : costMap.keySet()) {
									Cost c = costMap.get(no);
									tot += c.c;
								}
								cell = row.getCell(k);
								cell.setCellValue(tot);
							} else if (dstr
									.equals("Raun launakostnaður -fjárhagsbókh")) {
								double tot = 0.0;
								for (String no : costMap.keySet()) {
									Cost c = costMap.get(no);
									if (c.type.contains("0"))
										tot += c.c;
								}
								cell = row.getCell(k);
								cell.setCellValue(tot);
							}
						}
					}
				}
			}

			k += 2;
		}

		/************ real ******************/

		k = 2;
		for (Object o : jobstr) {
			// sql =
			// "select be.No_, be.Type, bl.Description, sum(be.\"Total Cost\"), sum(\"Total Price\") as Cost from dbo.\"Matís ohf_$Job Ledger Entry\" be, dbo.\"Matís ohf_$Job Budget Line\" bl where be.\"Job No_\" = '"+o.toString()+"' and be.No_ = bl.No_ and bl.\"Job No_\" = be.\"Job No_\" and be.\"Posting Date\" >= '2009-01-01' and be.\"Posting Date\" < '2009-04-01' group by be.No_, be.Type, bl.Description";
			sql = "select le.No_, le.Type, sum(le.\"Total Cost\"), sum(le.\"Total Price\") as Cost from dbo.\"Matís ohf_$Job Ledger Entry\" le where le.\"Job No_\" = '"
					+ o.toString()
					+ "' and le.\"Posting Date\" >= '2009-01-01' and le.\"Posting Date\" < '2009-04-01' group by le.No_, le.Type";

			ps = con.prepareStatement(sql);
			rs = ps.executeQuery();

			costMap.clear();
			costList.clear();
			while (rs.next()) {
				String nostr = rs.getString(1);
				Cost cost = new Cost(Long.parseLong(nostr), rs.getString(2),
						"", rs.getDouble(3), rs.getDouble(4));
				costMap.put(nostr, cost);
				costList.add(cost);
			}

			ps.close();

			for (i = 9; i < 40; i++) {
				row = sheet.getRow(i);
				if (row != null) {
					boolean unsucc = true;

					cell = row.getCell(0);
					if (cell != null) {
						int d = (int) cell.getNumericCellValue();
						String dstr = Integer.toString(d);
						if (d > 0) {
							if (costMap.containsKey(dstr)) {
								Cost cost = costMap.get(dstr);
								cell = row.getCell(k);
								if (d >= 1000 && d < 2000)
									cell.setCellValue(cost.p);
								else
									cell.setCellValue(cost.c);
							} else {
								double tot = 0.0;

								if (dstr.endsWith("99")) {
									for (Cost c : costList) {
										if (c.no >= d - 999 && c.no < d + 1)
											tot += c.c;
									}
								} else if (dstr.endsWith("98")) {
									for (Cost c : costList) {
										if (c.no >= d - 98 && c.no < d + 2)
											tot += c.c;
									}
								} else if (dstr.equals("1993")) {
									for (Cost c : costList) {
										if (c.no >= d - 993 && c.no < d + 7)
											tot += c.p;
									}
								}
								cell = row.getCell(k);
								cell.setCellValue(tot);
							}

							unsucc = false;
						}

					}

					if (unsucc) {
						cell = row.getCell(1);
						if (cell != null
								&& cell.getCellType() == Cell.CELL_TYPE_STRING) {
							String dstr = cell.getStringCellValue();
							if (dstr.equals("Kostnaður samtals")) {
								double tot = 0.0;
								for (String no : costMap.keySet()) {
									Cost c = costMap.get(no);
									tot += c.c;
								}
								cell = row.getCell(k);
								cell.setCellValue(tot);
							} else if (dstr
									.equals("Raun launakostnaður -fjárhagsbókh")) {
								double tot = 0.0;
								for (String no : costMap.keySet()) {
									Cost c = costMap.get(no);
									if (c.type.contains("0"))
										tot += c.c;
								}
								cell = row.getCell(k);
								cell.setCellValue(tot);
							}
						}
					}
				}
			}

			sql = "select le.No_, le.Type, sum(le.\"Total Cost\"), sum(le.\"Total Price\") as Cost from dbo.\"Matís ohf_$Job Ledger Entry\" le where le.\"Job No_\" = '"
					+ o.toString() + "' group by le.No_, le.Type";

			ps = con.prepareStatement(sql);
			rs = ps.executeQuery();

			costMap.clear();
			costList.clear();
			while (rs.next()) {
				String nostr = rs.getString(1);
				Cost cost = new Cost(Long.parseLong(nostr), rs.getString(2),
						"", rs.getDouble(3), rs.getDouble(4));
				costMap.put(nostr, cost);
				costList.add(cost);
			}

			ps.close();

			for (i = 40; i < 100; i++) {
				row = sheet.getRow(i);
				if (row != null) {
					boolean unsucc = true;

					cell = row.getCell(0);
					if (cell != null) {
						int d = (int) cell.getNumericCellValue();
						String dstr = Integer.toString(d);
						if (d > 0) {
							if (costMap.containsKey(dstr)) {
								Cost cost = costMap.get(dstr);
								cell = row.getCell(k);
								if (d >= 1000 && d < 2000)
									cell.setCellValue(cost.p);
								else
									cell.setCellValue(cost.c);
							} else {
								double tot = 0.0;

								if (dstr.endsWith("99")) {
									for (Cost c : costList) {
										if (c.no >= d - 999 && c.no < d + 1)
											tot += c.c;
									}
								} else if (dstr.endsWith("98")) {
									for (Cost c : costList) {
										if (c.no >= d - 98 && c.no < d + 2)
											tot += c.c;
									}
								} else if (dstr.equals("1993")) {
									for (Cost c : costList) {
										if (c.no >= d - 993 && c.no < d + 7)
											tot += c.p;
									}
								}
								cell = row.getCell(k);
								cell.setCellValue(tot);
							}

							unsucc = false;
						}
					}

					if (unsucc) {
						cell = row.getCell(1);
						if (cell != null
								&& cell.getCellType() == Cell.CELL_TYPE_STRING) {
							String dstr = cell.getStringCellValue();
							if (dstr.equals("Kostnaður samtals")) {
								double tot = 0.0;
								for (String no : costMap.keySet()) {
									Cost c = costMap.get(no);
									tot += c.c;
								}
								cell = row.getCell(k);
								cell.setCellValue(tot);
							} else if (dstr
									.equals("Raun launakostnaður -fjárhagsbókh")) {
								double tot = 0.0;
								for (String no : costMap.keySet()) {
									Cost c = costMap.get(no);
									if (c.type.contains("0"))
										tot += c.c;
								}
								cell = row.getCell(k);
								cell.setCellValue(tot);
							}
						}
					}
				}
			}

			k += 2;
		}

		// workbook.write( new FileOutputStream("/mnt/tmp/simmi.xlsx") );
	}

	@Override
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

		xlsComp = new JComponent() {
			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(xlsImg, 0, 0, this);
			}
		};

		TransferHandler th = new TransferHandler() {
			@Override
			public int getSourceActions(JComponent c) {
				return TransferHandler.COPY_OR_MOVE;
			}

			@Override
			public boolean canImport(TransferHandler.TransferSupport support) {
				return true;
			}

			@Override
			protected Transferable createTransferable(JComponent c) {
				return new Transferable() {

					@Override
					public Object getTransferData(DataFlavor arg0)
							throws UnsupportedFlavorException, IOException {
						if (arg0 == DataFlavor.getTextPlainUnicodeFlavor()) {
							return null;
						} else {
							return null;
						}
					}

					@Override
					public DataFlavor[] getTransferDataFlavors() {
						return new DataFlavor[] { DataFlavor.getTextPlainUnicodeFlavor() };
					}

					@Override
					public boolean isDataFlavorSupported(DataFlavor arg0) {
						if (arg0 == DataFlavor.getTextPlainUnicodeFlavor()) {
							return true;
						}
						return false;
					}
				};
			}

			@Override
			public boolean importData(TransferHandler.TransferSupport support) {
				Object obj = null;

				int b = Arrays.binarySearch(support.getDataFlavors(),
						DataFlavor.javaFileListFlavor,
						new Comparator<DataFlavor>() {
							@Override
							public int compare(DataFlavor o1, DataFlavor o2) {
								return o1 == o2 ? 1 : 0;
							}
						});
				System.err.println(b);
				if (b != -1) {
					try {
						obj = support.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
					} catch (UnsupportedFlavorException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				//System.err.println( "simsim " + obj.getClass() );

				try {
					System.err.println("try");
					if (obj != null && obj instanceof List) {
						System.err.println("next");
						List<File>	l = (List<File>)obj;
						File f = l.get(0);
						try {
							load( f.getCanonicalPath() );
						} catch (ClassNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						File nf = new File( System.getProperty("user.home"), "tmp.xlsx" );
						workbook.write( new FileOutputStream( nf ) );
						Desktop.getDesktop().open( nf );
						
						//JFileChooser fc = new JFileChooser( System.getProperty("user.home") );
						/*if( fc.showSaveDialog( Report.this.xlsComp ) == JFileChooser.APPROVE_OPTION ) {
							workbook.write( new FileOutputStream( fc.getSelectedFile() ) );
						}*/
					} else {
						//char[] cc = new char[256];
						//Reader r = DataFlavor.getTextPlainUnicodeFlavor().getReaderForText(support.getTransferable());
						//int read = r.read(cc);
						obj = support.getTransferable().getTransferData(DataFlavor.getTextPlainUnicodeFlavor());
						if ( obj != null ) {
							String stuff = obj.toString();
							if (stuff.contains("file://")) {
								URL url = new URL(stuff);
								try {
									File f = new File( URLDecoder.decode( url.getFile(), "UTF-8" ) );
									//URLDecoder.decode( f, "UTF-8" )
									load( f.getCanonicalPath() );
									JFileChooser fc = new JFileChooser( f.getParentFile() );
									if( fc.showSaveDialog( Report.this.xlsComp ) == JFileChooser.APPROVE_OPTION ) {
										workbook.write( new FileOutputStream( fc.getSelectedFile() ) );
									}
								} catch (ClassNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (SQLException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}
					}
				} catch (UnsupportedFlavorException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

				return true;
			}
		};
		xlsComp.setTransferHandler(th);

		pdfComp = new JComponent() {
			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(pdfImg, 0, 0, this);
			}
		};

		/*
		 * try { Desktop.getDesktop().browse( new URI("http://test.matis.is") );
		 * } catch (IOException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } catch (URISyntaxException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); }
		 */
	}

	public class TransparentBackground extends JComponent { 
		//implements ComponentListener, WindowFocusListener, Runnable {
		/*private JFrame _frame;
		private BufferedImage _background;
		private long _lastUpdate = 0;
		private boolean _refreshRequested = true;
		private Robot _robot;
		private Rectangle _screenRect;
		private ConvolveOp _blurOp;

		// constructor
		// -------------------------------------------------------------

		public TransparentBackground(JFrame frame) {
			_frame = frame;
			try {
				_robot = new Robot();
			} catch (AWTException e) {
				e.printStackTrace();
				return;
			}

			Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
			_screenRect = new Rectangle(dim.width, dim.height);

			float[] my_kernel = { 0.10f, 0.10f, 0.10f, 0.10f, 0.20f, 0.10f,
					0.10f, 0.10f, 0.10f };
			_blurOp = new ConvolveOp(new Kernel(3, 3, my_kernel));

			updateBackground();
			_frame.addComponentListener(this);
			_frame.addWindowFocusListener(this);
			new Thread(this).start();
		}

		// protected
		// ---------------------------------------------------------------

		protected void updateBackground() {
			_background = _robot.createScreenCapture(_screenRect);
		}

		protected void refresh() {
			if (_frame.isVisible() && this.isVisible()) {
				repaint();
				_refreshRequested = true;
				_lastUpdate = System.currentTimeMillis();
			}
		}

		// JComponent
		// --------------------------------------------------------------

		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g;
			Point pos = this.getLocationOnScreen();
			BufferedImage buf = new BufferedImage(getWidth(), getHeight(),
					BufferedImage.TYPE_INT_RGB);
			buf.getGraphics().drawImage(_background, -pos.x, -pos.y, null);

			Image img = _blurOp.filter(buf, null);
			g2.drawImage(img, 0, 0, null);
			g2.setColor(new Color(255, 255, 255, 192));
			g2.fillRect(0, 0, getWidth(), getHeight());
		}

		// ComponentListener
		// -------------------------------------------------------
		public void componentHidden(ComponentEvent e) {
		}

		public void componentMoved(ComponentEvent e) {
			repaint();
		}

		public void componentResized(ComponentEvent e) {
			repaint();

		}

		public void componentShown(ComponentEvent e) {
			repaint();
		}

		// WindowFocusListener
		// -----------------------------------------------------
		public void windowGainedFocus(WindowEvent e) {
			refresh();
		}

		public void windowLostFocus(WindowEvent e) {
			refresh();
		}

		// Runnable
		// ----------------------------------------------------------------
		public void run() {
			try {
				while (true) {
					Thread.sleep(100);
					long now = System.currentTimeMillis();
					if (_refreshRequested && ((now - _lastUpdate) > 1000)) {
						if (_frame.isVisible()) {
							Point location = _frame.getLocation();
							_frame.setLocation(-_frame.getWidth(), -_frame
									.getHeight());
							updateBackground();
							_frame.setLocation(location);
							refresh();
						}
						_lastUpdate = now;
						_refreshRequested = false;
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}*/
		
		public void setBounds(int x, int y, int w, int h) {
			super.setBounds(x, y, w, h);
			xlsComp.setBounds(this.getWidth() / 2 - xlsImg.getWidth() - 10,
					(this.getHeight() - xlsImg.getHeight()) / 2, xlsImg
							.getWidth(), xlsImg.getHeight());
			pdfComp.setBounds(this.getWidth() / 2 + 10,
					(this.getHeight() - pdfImg.getHeight()) / 2, pdfImg
							.getWidth(), pdfImg.getHeight());
		}
	};

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		JFrame f = new JFrame("Report Generator");
		f.setSize(400, 300);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//f.setBackground(Color.white);
		f.getContentPane().setBackground(Color.white);

		Report r = new Report();
		r.init();
		Report.TransparentBackground c = r.new TransparentBackground();

		c.add( r.xlsComp );
		c.add( r.pdfComp );
		
		f.add(c);

		f.setVisible(true);
	}

}
