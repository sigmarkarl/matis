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
import java.util.Set;
import java.util.Map.Entry;

import javax.imageio.ImageIO;
import javax.swing.JApplet;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFComment;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Report extends JApplet {
	JComponent 		pdfComp;
	JComponent 		xlsComp;
	BufferedImage 	pdfImg;
	BufferedImage 	xlsImg;
	XSSFWorkbook 	workbook;
	Map<Long,String>	ledgerMap;

	public Report() {
		super();
		
		ledgerMap = new HashMap<Long,String>();
		ledgerMap.put((long)103, "1050");
		ledgerMap.put((long)106, "1050");
		ledgerMap.put((long)20001, "1040");
		ledgerMap.put((long)20002, "1040");

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

		load(args[0]);
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
		long	no;
		String	nostr;
		String 	subno;
		String 	type;
		String 	name;
		double 	c;
		double 	p;

		public Cost(String nostr, String type, String name, double c, double p, String subno) {
			this.nostr = nostr;
			this.type = type;
			this.name = name;
			this.c = c;
			this.p = p;
			this.subno = subno;
			
			no = -1;
			try {
				no = Long.parseLong(nostr);
			} catch( Exception e ) {
				System.err.println( "nostr " + nostr );
			}
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

		workbook.write(new FileOutputStream("/home/sigmar/Desktop/simmi2.xlsx"));
	}

	public void load(String filename) {
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			String connectionUrl = "jdbc:sqlserver://navision.rf.is:1433;databaseName=MATIS;user=simmi;password=drsmorc.311;";
			Connection con = DriverManager.getConnection(connectionUrl);
	
			workbook = new XSSFWorkbook(filename);
			XSSFSheet sheet = workbook.getSheet("Skýrsla sviðstjóra v2");
			XSSFRow row = sheet.getRow(0);
			XSSFCell cell = row.getCell(1);
			String cellv = cell.getStringCellValue();
			String[] splt = cellv.split(":");
			String str = null;
			if( splt.length > 1 ) {
				str = splt[1].trim();
			}
			row = sheet.getRow(2);
			
			XSSFCell[] lastGoodCell = null;
			XSSFCell[] lastGoodCell2 = null;
			int i = 2;
			if( str != null ) {
				String sql = "SELECT No_ FROM \"Matís ohf_$Job\" where \"Global Dimension 1 Code\" = '"+str+"' and \"Job Posting Group\" != 'INNSELD' and \"Completion Date\" < '1900-01-01'";
				PreparedStatement ps = con.prepareStatement(sql);
				ResultSet rs = ps.executeQuery();
		
				cell = row.getCell(i);
				while (rs.next()) {
					String val = rs.getString(1);
					
					if( cell == null ) {
						if( lastGoodCell != null ) {
							for( int k = 2; k < 100; k++ ) {
								XSSFRow		rr = sheet.getRow(k);
								if( rr != null ) {
									XSSFCell 	cc = lastGoodCell[k];
									XSSFCell 	cc2 = lastGoodCell2[k];
									XSSFCell 	oc = rr.createCell(i);
									XSSFCell 	oc2 = rr.createCell(i+1);
									
									XSSFComment comment;
									XSSFCellStyle style;
									String rawValue;
									int type;
									if( cc != null ) {
										comment = cc2.getCellComment();
										if( comment != null ) oc2.setCellComment( comment );
										style = cc2.getCellStyle();
										if( style != null ) oc2.setCellStyle( style );
										type = cc2.getCellType();
										oc2.setCellType( type );
										rawValue = cc2.getRawValue();
										if( rawValue != null ) oc2.setRawValue( rawValue );
										
										comment = cc.getCellComment();
										if( comment != null ) oc.setCellComment( comment );
										style = cc.getCellStyle();
										if( style != null ) oc.setCellStyle( style );
										type = cc.getCellType();
										oc.setCellType( type );
										rawValue = cc.getRawValue();
										if( rawValue != null ) oc.setRawValue( rawValue );
									}
									
									if( k >= 2 && k <= 6 ) {
										sheet.addMergedRegion( new CellRangeAddress(k, k, i, i+1) );
									}
								}
							}
						}
						cell = row.getCell(i);
					} else if( lastGoodCell == null ) {
						lastGoodCell = new XSSFCell[100];
						lastGoodCell2 = new XSSFCell[100];
						for( int k = 2; k < 100; k++ ) {
							XSSFRow	rr = sheet.getRow(k);
							if( rr != null ) {
								lastGoodCell[k] = rr.getCell(i);
								lastGoodCell2[k] = rr.getCell(i+1);
							}
						}
					}
					cell.setCellValue( val );
					
					i += 2;
					cell = row.getCell(i);
				}
				rs.close();
			}
				
			List<Object> jobstr = new ArrayList<Object>();
			i = 2;
			cell = row.getCell(i);
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
	
			str = "(";
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
	
			String sql = "select * from dbo.job_excel where No_ in " + str;
			PreparedStatement ps = con.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
	
			// List<Job> jobs = new ArrayList<Job>();
			//i = 0;
			while (rs.next()) {
				i = 0;
				String job = rs.getString(1);
				while( i < jobstr.size() && !job.contains( jobstr.get(i).toString() ) ) i++;
				
				if( i < jobstr.size() ) {
					row = sheet.getRow(3);
					cell = row.getCell(2 + 2*i);
					if( cell == null ) cell = row.createCell(2 + 2*i);
					cell.setCellType(Cell.CELL_TYPE_STRING);
					cell.setCellValue(rs.getString(2));
		
					row = sheet.getRow(4);
					cell = row.getCell(2 + 2*i);
					if( cell == null ) cell = row.createCell(2 + 2*i);
					cell.setCellType(Cell.CELL_TYPE_STRING);
					cell.setCellValue(rs.getString(3));
		
					row = sheet.getRow(5);
					cell = row.getCell(2 + 2*i);
					if( cell == null ) cell = row.createCell(2 + 2*i);
					cell.setCellType(Cell.CELL_TYPE_STRING);
					cell.setCellValue(rs.getDate(4).toString());
		
					row = sheet.getRow(6);
					cell = row.getCell(2 + 2*i);
					if( cell == null ) cell = row.createCell(2 + 2*i);
					cell.setCellType(Cell.CELL_TYPE_STRING);
					cell.setCellValue(rs.getDate(5).toString());
					// jobs.add( new Job( rs.getString(1), rs.getString(2),
					// rs.getString(3), rs.getDate(4), rs.getDate(5) ) );
					//i += 2;
				} else {
					System.err.println( job + " not found" );
				}
			}
			rs.close();
			ps.close();
			
			String startDate = "2009-01-01";
			String endDate = "2009-04-01";
			
			row = sheet.getRow(7);
			cell = row.getCell(1);
			String dateVal = cell.getStringCellValue();
			String[] splVal = dateVal.split(":");
			if( splVal.length > 0 ) {
				String[] dDelta = splVal[1].split("-");
				String[] startSplit = dDelta[0].trim().split("\\.");
				String[] endSplit = dDelta[1].trim().split("\\.");
				
				if( startSplit[2].length() == 2 ) {
					startDate = "20" + startSplit[2] + "-" + startSplit[1] + "-" + startSplit[0];
				}
				
				if( endSplit[2].length() == 2 ) {
					endDate = "20" + endSplit[2] + "-" + endSplit[1] + "-" + endSplit[0];
				}
			}
	
			/********** plan ***************/
			int k = 3;
			for (Object o : jobstr) {
				sql = "select be.No_, be.Type, bl.Description, sum(be.\"Total Cost\") as Cost, sum(be.\"Total Price\") as Price from dbo.\"Matís ohf_$Job Budget Entry\" be, dbo.\"Matís ohf_$Job Budget Line\" bl where be.\"Job No_\" = '"
						+ o.toString()
						+ "' and be.No_ = bl.No_ and bl.\"Job No_\" = be.\"Job No_\" and be.Date >= '"+startDate+"' and be.Date <= '"+endDate+"' group by be.No_, be.Type, bl.Description";
	
				System.err.println( "executing job " + o.toString() );
				
				ps = con.prepareStatement(sql);
				rs = ps.executeQuery();
	
				costMap.clear();
				costList.clear();
				while (rs.next()) {
					String nostr = rs.getString(1);
					Cost cost = new Cost(nostr, rs.getString(2), rs.getString(3), rs.getDouble(4), rs.getDouble(5), null);
					costMap.put(nostr, cost);
					costList.add(cost);
				}
				rs.close();
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
									if( cell == null ) cell = row.createCell(k);
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
									if( cell == null ) cell = row.createCell(k);
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
										if( c.type.contains("0") ) tot += c.p;
										else tot += c.c;
									}
									cell = row.getCell(k);
									if( cell == null ) cell = row.createCell(k);
									cell.setCellValue(tot);
								} else if (dstr.equals("Kostnaður v/ vinnu (útselt) - verkb")) {
									double tot = 0.0;
									for (String no : costMap.keySet()) {
										Cost c = costMap.get(no);
										if( c.type.contains("0") ) tot += c.p;
									}
									cell = row.getCell(k);
									if( cell == null ) cell = row.createCell(k);
									cell.setCellValue(tot);
								} else if (dstr.equals("Afkoma (v/útselds taxta)")) {
									double tot = 0.0;
									double ctot = 0.0;
									for (String no : costMap.keySet()) {
										Cost c = costMap.get(no);
										if( c.type.contains("0") ) tot += c.p;
										//else if( c.type.contains("1") ) ctot += c.p;
										else tot += c.c;
										
										if (c.no >= 1000 && c.no < 2000 ) {
											ctot += c.p;
										}
									}
									cell = row.getCell(k);
									if( cell == null ) cell = row.createCell(k);
									cell.setCellValue(ctot-tot);
								}
							}
						}
					}
				}
	
				sql = "select be.No_, be.Type, bl.Description, sum(be.\"Total Cost\") as Cost, sum(\"Total Price\") as Price from dbo.\"Matís ohf_$Job Budget Entry\" be, dbo.\"Matís ohf_$Job Budget Line\" bl where be.\"Job No_\" = '"
						+ o.toString()
						+ "' and be.No_ = bl.No_ and bl.\"Job No_\" = be.\"Job No_\" and be.Date <= '"+endDate+"' group by be.No_, be.Type, bl.Description";
	
				ps = con.prepareStatement(sql);
				rs = ps.executeQuery();
	
				costMap.clear();
				costList.clear();
				int count = 0;
				while (rs.next()) {
					count++;
					//System.err.print( (count) + " " );
					String nostr = rs.getString(1);
					Cost cost = new Cost(nostr, rs.getString(2), rs.getString(3), rs.getDouble(4), rs.getDouble(5), null);
					costMap.put(nostr, cost);
					costList.add(cost);
				}
				rs.close();
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
									if( cell == null ) cell = row.createCell(k);
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
									if( cell == null ) cell = row.createCell(k);
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
										if( c.type.contains("0") ) tot += c.p;
										else tot += c.c;
									}
									cell = row.getCell(k);
									if( cell == null ) cell = row.createCell(k);
									cell.setCellValue(tot);
								} else if (dstr
										.equals("Kostnaður v/ vinnu (útselt) - verkb")) {
									double tot = 0.0;
									for (String no : costMap.keySet()) {
										Cost c = costMap.get(no);
										if( c.type.contains("0") ) tot += c.p;									
									}
									cell = row.getCell(k);
									if( cell == null ) cell = row.createCell(k);
									cell.setCellValue(tot);
								} else if (dstr.equals("Afkoma (v/útselds taxta)")) {
									double tot = 0.0;
									double ctot = 0.0;
									for (String no : costMap.keySet()) {
										Cost c = costMap.get(no);
										if( c.type.contains("0") ) tot += c.p;
										//else if( c.type.contains("1") ) ctot += c.p;
										else tot += c.c;
										
										if (c.no >= 1000 && c.no < 2000 ) {
											ctot += c.p;
										}
									}
									cell = row.getCell(k);
									if( cell == null ) cell = row.createCell(k);
									cell.setCellValue(ctot-tot);
								}
							}
						}
					}
				}
	
				k += 2;
			}
	
			System.err.println( "fetching real data" );
			/************ real ******************/
	
			k = 2;
			for (Object o : jobstr) {
				// sql =
				// "select be.No_, be.Type, bl.Description, sum(be.\"Total Cost\"), sum(\"Total Price\") as Cost from dbo.\"Matís ohf_$Job Ledger Entry\" be, dbo.\"Matís ohf_$Job Budget Line\" bl where be.\"Job No_\" = '"+o.toString()+"' and be.No_ = bl.No_ and bl.\"Job No_\" = be.\"Job No_\" and be.\"Posting Date\" >= '2009-01-01' and be.\"Posting Date\" < '2009-04-01' group by be.No_, be.Type, bl.Description";
				sql = "select ge.\"Sales Account\", le.Type, sum(le.\"Total Cost\") as Cost, sum(le.\"Total Price\") as Price from dbo.\"Matís ohf_$Job Ledger Entry\" le, dbo.\"Matís ohf_$General Posting Setup\" ge "
						+ "where le.\"Job No_\" = '" + o.toString()
						+ "' and le.\"Posting Date\" >= '"+startDate+"' and le.\"Posting Date\" <= '"+endDate+"' "
						+ "and ge.\"Gen_ Bus_ Posting Group\" = le.\"Gen_ Bus_ Posting Group\" and ge.\"Gen_ Prod_ Posting Group\" = le.\"Gen_ Prod_ Posting Group\" group by ge.\"Sales Account\", le.Type";
	
				System.err.println( "executing real " + o.toString() );
				
				ps = con.prepareStatement(sql);
				rs = ps.executeQuery();
	
				costMap.clear();
				costList.clear();
				while (rs.next()) {
					String nostr = rs.getString(1);
					Cost cost = new Cost(nostr, rs.getString(2), "", rs.getDouble(3), rs.getDouble(4), null);
					costMap.put(nostr, cost);
					costList.add(cost);
				}
				rs.close();
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
									if( cell == null ) cell = row.createCell(k);
									if (d >= 1000 && d < 2000)
										cell.setCellValue(cost.p);
									else
										cell.setCellValue(cost.c);
								} /*else if( ledgerMap.containsValue(dstr) ) {
									Set<Entry<Long,String>>	entr = ledgerMap.entrySet();
									double tot = 0.0;
									for (Cost c : costList) {
										boolean check = false;
										if( c.type.equals("1") ) {
											for( Entry<Long,String> e : entr ) {
												if( e.getValue().equals(dstr) && c.no == d ) {
													check = true;
													break;
												}
											}
											if( check ) {
												tot += c.p;
											}
										}
									}
									cell = row.getCell(k);
									if( cell == null ) cell = row.createCell(k);
									cell.setCellValue(tot);
								}*/ else {
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
											if( c.type.contains("1") ) {
												tot += c.p;
											}
										}
									}
									cell = row.getCell(k);
									if( cell == null ) cell = row.createCell(k);
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
										if( c.type.contains("0") ) tot += c.p;
										else tot += c.c;
									}
									cell = row.getCell(k);
									if( cell == null ) cell = row.createCell(k);
									cell.setCellValue(tot);
								} else if (dstr.equals("Kostnaður v/ vinnu (útselt) - verkb")) {
									double tot = 0.0;
									for (String no : costMap.keySet()) {
										Cost c = costMap.get(no);
										if( c.type.contains("0") ) tot += c.p;
									}
									cell = row.getCell(k);
									if( cell == null ) cell = row.createCell(k);
									cell.setCellValue(tot);
								} else if (dstr.equals("Afkoma (v/útselds taxta)")) {
									double tot = 0.0;
									double ctot = 0.0;
									for (String no : costMap.keySet()) {
										Cost c = costMap.get(no);
										if( c.type.contains("0") ) tot += c.p;
										//else if( c.type.contains("1") ) ctot += c.p;
										else tot += c.c;
										
										if (c.no >= 1000 && c.no < 2000 ) {
											ctot += c.p;
										}
									}
									cell = row.getCell(k);
									if( cell == null ) cell = row.createCell(k);
									cell.setCellValue(-ctot-tot);
								}
							}
						}
					}
				}
	
				sql = "select ge.\"Sales Account\", le.Type, sum(le.\"Total Cost\") as Cost, sum(le.\"Total Price\") as Price from dbo.\"Matís ohf_$Job Ledger Entry\" le, dbo.\"Matís ohf_$General Posting Setup\" ge where le.\"Job No_\" = '"
						+ o.toString() + "' and le.\"Posting Date\" <= '"+endDate+"' "
						+ "and ge.\"Gen_ Bus_ Posting Group\" = le.\"Gen_ Bus_ Posting Group\" and ge.\"Gen_ Prod_ Posting Group\" = le.\"Gen_ Prod_ Posting Group\" group by ge.\"Sales Account\", le.Type";
	
				ps = con.prepareStatement(sql);
				rs = ps.executeQuery();
	
				costMap.clear();
				costList.clear();
				while (rs.next()) {
					String nostr = rs.getString(1);
					Cost cost = new Cost(nostr, rs.getString(2), "", rs.getDouble(3), rs.getDouble(4), null);
					costMap.put(nostr, cost);
					costList.add(cost);
				}
				rs.close();
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
									if( cell == null ) cell = row.createCell(k);
									if (d >= 1000 && d < 2000)
										cell.setCellValue(cost.p);
									else
										cell.setCellValue(cost.c);
								} /*else if( ledgerMap.containsValue(dstr) ) {
									Set<Entry<Long,String>>	entr = ledgerMap.entrySet();
									double tot = 0.0;
									for (Cost c : costList) {
										boolean check = false;
										if( c.type.equals("1") ) {
											for( Entry<Long,String> e : entr ) {
												if( e.getValue().equals(dstr) && e.getKey().equals(c.no) ) {
													check = true;
													break;
												}
											}
											if( check ) {
												tot += c.p;
											}
										}
									}
									cell = row.getCell(k);
									if( cell == null ) cell = row.createCell(k);
									cell.setCellValue(tot);
								}*/ else {
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
											if( c.type.contains("1") ) {
												tot += c.p;
											}
										}
									}
									cell = row.getCell(k);
									if( cell == null ) cell = row.createCell(k);
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
										
										if( c.type.contains("0") ) tot += c.p;
										else tot += c.c;
									}
									cell = row.getCell(k);
									if( cell == null ) cell = row.createCell(k);
									cell.setCellValue(tot);
								} else if (dstr.equals("Kostnaður v/ vinnu (útselt) - verkb")) {
									double tot = 0.0;
									for (String no : costMap.keySet()) {
										Cost c = costMap.get(no);
										if( c.type.contains("0") ) tot += c.p;
									}
									cell = row.getCell(k);
									if( cell == null ) cell = row.createCell(k);
									cell.setCellValue(tot);
								} else if (dstr.equals("Afkoma (v/útselds taxta)")) {
									double tot = 0.0;
									double ctot = 0.0;
									for (String no : costMap.keySet()) {
										Cost c = costMap.get(no);
										if( c.type.contains("0") ) tot += c.p;
										//else if( c.type.contains("1") ) ctot += c.p;
										else tot += c.c;
										
										if( c.no >= 1000 && c.no < 2000 ) {
											ctot += c.p;
										}
									}
									cell = row.getCell(k);
									if( cell == null ) cell = row.createCell(k);
									cell.setCellValue(-ctot-tot);
								}
							}
						}
					}
				}
	
				//sheet.autoSizeColumn(k);
				//sheet.autoSizeColumn(k+1);
				
				k += 2;
			}
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
						load( f.getCanonicalPath() );
						
						int i = 0;
						File nf = File.createTempFile("tmp", ".xlsx");
						/*File nf = new File( System.getProperty("user.home"), "tmp"+i+".xlsx" );
						while( !nf.canWrite() && i < 10 ) {
							i++;
							nf = new File( System.getProperty("user.home"), "tmp"+i+".xlsx" );
						}*/
						workbook.write( new FileOutputStream( nf ) );
						Desktop.getDesktop().open( nf );
						
						//JFileChooser fc = new JFileChooser( System.getProperty("user.home") );
						/*if( fc.showSaveDialog( Report.this.xlsComp ) == JFileChooser.APPROVE_OPTION ) {
							workbook.write( new FileOutputStream( fc.getSelectedFile() ) );
						}*/
					} else {
						if( true ) {
							obj = support.getTransferable().getTransferData(DataFlavor.stringFlavor);
							if ( obj != null ) {
								String stuff = obj.toString();
								if (stuff.contains("file://")) {
									URL url = new URL(stuff);
									File f = new File( URLDecoder.decode( url.getFile(), "UTF-8" ) );
									//URLDecoder.decode( f, "UTF-8" )
									load( f.getCanonicalPath() );
									int i = 0;
									File nf = File.createTempFile("tmp", ".xlsx");
									//File nf = new File( System.getProperty("user.home"), "tmp"+i+".xlsx" );
									/*while( nf.exists() && !nf.canWrite() && i < 10 ) {
										i++;
										nf = new File( System.getProperty("user.home"), "tmp"+i+".xlsx" );
									}*/
									workbook.write( new FileOutputStream( nf ) );
								}
							}
						} else {
							//char[] cc = new char[256];
							//Reader r = DataFlavor.getTextPlainUnicodeFlavor().getReaderForText(support.getTransferable());
							//int read = r.read(cc);
							obj = support.getTransferable().getTransferData(DataFlavor.stringFlavor);
							if ( obj != null ) {
								String stuff = obj.toString();
								if (stuff.contains("file://")) {
									URL url = new URL(stuff);
									File f = new File( URLDecoder.decode( url.getFile(), "UTF-8" ) );
									//URLDecoder.decode( f, "UTF-8" )
									load( f.getCanonicalPath() );
									JFileChooser fc = new JFileChooser( f.getParentFile() );
									if( fc.showSaveDialog( Report.this.xlsComp ) == JFileChooser.APPROVE_OPTION ) {
										workbook.write( new FileOutputStream( fc.getSelectedFile() ) );
									}
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
