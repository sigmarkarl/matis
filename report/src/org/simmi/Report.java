package org.simmi;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JApplet;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Report extends JApplet {

	public Report( String[] args ) {
		super();
		
		/*try {
			loadXlsx("/home/sigmar/Desktop/Kennitölur 270509.xlsx");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		try {
			load( args );
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
		String 	no;
		String 	desc;
		String 	boss;
		Date	start;
		Date	end;
		
		public Job( String no, String desc, String boss, Date start, Date end ) {
			this.no = no;
			this.desc = desc;
			this.boss = boss;
			this.start = start;
			this.end = end;
		}
	};
	
	class Cost {
		long	no;
		String	type;
		String	name;
		double	c;
		double	p;
		
		public Cost( long no, String type, String name, double c, double p ) {
			this.no = no;
			this.type = type;
			this.name = name;
			this.c = c;
			this.p = p;
		}
	};
	Map<String,Cost>	costMap = new HashMap<String,Cost>();
	List<Cost>			costList = new ArrayList<Cost>();
	
	public void loadXlsx( String fname ) throws IOException {
		XSSFWorkbook 	workbook = new XSSFWorkbook( fname );
		XSSFSheet		sheet = workbook.getSheet("Skýrsla sviðstjóra v2");
		XSSFRow 		row = sheet.getRow(12);
		
		if (row == null)
            row = sheet.createRow(12);
        XSSFCell cell = row.getCell(2);
        if (cell == null)
            cell = row.createCell(12);
        cell.setCellType(XSSFCell.CELL_TYPE_STRING);
        cell.setCellValue("siiiiimmmmmmmmmmmmmmmmmmmmmmmmmmmmmi");
        
        workbook.write( new FileOutputStream("/home/sigmar/Desktop/simmi2.xlsx") );
	}
	
	public void load( String[] args ) throws ClassNotFoundException, SQLException, IOException {
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		
		String connectionUrl = "jdbc:sqlserver://navision.rf.is:1433;databaseName=MATIS;user=simmi;password=drsmorc.311;";
		Connection con = DriverManager.getConnection(connectionUrl);
		
		XSSFWorkbook 	workbook = new XSSFWorkbook( "/home/sigmar/Desktop/Kennitölur 270509.xlsx" );
		XSSFSheet		sheet = workbook.getSheet("Skýrsla sviðstjóra v2");
		XSSFRow 		row = sheet.getRow(2);
		
		List<Object>	jobstr = new ArrayList<Object>();
		int i = 2;
        XSSFCell cell = row.getCell(i);
        while( cell != null && 
        		( (cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC && cell.getNumericCellValue() > 0) ||
        		  (cell.getCellType() == XSSFCell.CELL_TYPE_STRING && cell.getStringCellValue().length() > 0) ) ) {
        	if( cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC ) {
        		jobstr.add( (int)cell.getNumericCellValue() );
        	} else if( cell.getCellType() == XSSFCell.CELL_TYPE_STRING ) {
        		String val = cell.getStringCellValue();
        		if( !val.equals("xxx") ) jobstr.add( val );
        	}
        	i += 2;
        	cell = row.getCell(i);
        }
        
        //Object[] oargs = jobstr.toArray( new Object[0] ); 
		String str = "(";
		for( Object o : jobstr ) {
			if( o instanceof Integer ) {
				str += "'"+Integer.toString( (Integer)o )+"'";
			} else if( o instanceof String ) {
				String res = (String)o;
				str += "'"+res+"'";
			}
			if( !o.equals( jobstr.get(jobstr.size()-1) ) ) str += ", ";
		}
		str += ")";
		
		System.err.println( str );
		
		String sql = "select * from dbo.job_excel where No_ in "+str;
		PreparedStatement ps = con.prepareStatement( sql );
		ResultSet rs = ps.executeQuery();
		
		//List<Job>	jobs = new ArrayList<Job>();
		i = 0;
		while( rs.next() ) {
			row = sheet.getRow(3);
			cell = row.getCell(2+i);
			if( cell != null ) {
				cell.setCellType( XSSFCell.CELL_TYPE_STRING );
				cell.setCellValue( rs.getString(2) );
			}
			
			row = sheet.getRow(4);
			cell = row.getCell(2+i);
			if( cell != null ) {
				cell.setCellType( XSSFCell.CELL_TYPE_STRING );
				cell.setCellValue( rs.getString(3) );
			}
			
			row = sheet.getRow(5);
			cell = row.getCell(2+i);
			if( cell != null ) {
				cell.setCellType( XSSFCell.CELL_TYPE_STRING );
				cell.setCellValue( rs.getDate(4).toString() );
			}
			
			row = sheet.getRow(6);
			cell = row.getCell(2+i);
			if( cell != null ) {
				cell.setCellType( XSSFCell.CELL_TYPE_STRING );
				cell.setCellValue( rs.getDate(5).toString() );
			}
 			//jobs.add( new Job( rs.getString(1), rs.getString(2), rs.getString(3), rs.getDate(4), rs.getDate(5) ) );
			i += 2;
		}
		ps.close();
		
		String type = "('2310','2311','2498','2710','2999')";
		
		int k = 2;
		for( Object o : jobstr ) {
			sql = "select be.No_, be.Type, bl.Description, sum(be.\"Total Cost\"), sum(\"Total Price\") as Cost from dbo.\"Matís ohf_$Job Budget Entry\" be, dbo.\"Matís ohf_$Job Budget Line\" bl where be.\"Job No_\" = '"+o.toString()+"' and be.No_ = bl.No_ and bl.\"Job No_\" = be.\"Job No_\" and be.Date >= '2009-01-01' group by be.No_, be.Type, bl.Description";
			
			ps = con.prepareStatement( sql );
			rs = ps.executeQuery();
			
			costMap.clear();
			costList.clear();
			while( rs.next() ) {
				String nostr = rs.getString(1);
				Cost cost = new Cost( Long.parseLong(nostr), rs.getString(2), rs.getString(3), rs.getDouble(4), rs.getDouble(5) );
				costMap.put( nostr, cost );
				costList.add( cost );
			}
			
			ps.close();
			
			for( i = 9; i < 40; i++ ) {
				row = sheet.getRow(i);
				if( row != null ) {
					boolean unsucc = true;
					
					cell = row.getCell(0);
					if( cell != null ) {
						int 	d = (int)cell.getNumericCellValue();
						String 	dstr = Integer.toString(d);
						if( d > 0 ) {
							if( costMap.containsKey( dstr) ) {
								Cost cost = costMap.get( dstr );
								cell = row.getCell( k );
								if( d >= 1000 && d < 2000 ) cell.setCellValue( cost.p );
								else cell.setCellValue( cost.c );
							} else {
								double tot = 0.0;
								
								if( dstr.endsWith("99") ) {
									for( Cost c : costList ) {
										if( c.no >= d-999 && c.no < d+1 ) tot += c.c;
									}
								} else if( dstr.endsWith("98") ) {
									for( Cost c : costList ) {
										if( c.no >= d-98 && c.no < d+2 ) tot += c.c;
									}
								} else if( dstr.equals("1993") ) {
									for( Cost c : costList ) {
										if( c.no >= d-993 && c.no < d+7 ) tot += c.p;
									}
								}
								cell = row.getCell( k );
								cell.setCellValue( tot );		
							}
							
							unsucc = false;
						}
						
					}
					
					if( unsucc ) {
						cell = row.getCell(1);
						if( cell != null && cell.getCellType() == XSSFCell.CELL_TYPE_STRING ) {
							String 	dstr = cell.getStringCellValue();
							if( dstr.equals("Kostnaður samtals") ) {
								double tot = 0.0;
								for( String no : costMap.keySet() ) {
									Cost c = costMap.get(no);
									tot += c.c;
								}
								cell = row.getCell(k);
								cell.setCellValue(tot);
							} else if( dstr.equals("Raun launakostnaður -fjárhagsbókh") ) {
								double tot = 0.0;
								for( String no : costMap.keySet() ) {
									Cost c = costMap.get(no);
									if( c.type.contains("0") ) tot += c.c;
								}
								cell = row.getCell(k);
								cell.setCellValue(tot);
							}
						}
					}
				}
			}
			
			sql = "select be.No_, be.Type, bl.Description, sum(be.\"Total Cost\"), sum(\"Total Price\") as Cost from dbo.\"Matís ohf_$Job Budget Entry\" be, dbo.\"Matís ohf_$Job Budget Line\" bl where be.\"Job No_\" = '"+o.toString()+"' and be.No_ = bl.No_ and bl.\"Job No_\" = be.\"Job No_\" group by be.No_, be.Type, bl.Description";
			
			ps = con.prepareStatement( sql );
			rs = ps.executeQuery();
			
			costMap.clear();
			costList.clear();
			while( rs.next() ) {
				String nostr = rs.getString(1);
				Cost cost = new Cost( Long.parseLong(nostr), rs.getString(2), rs.getString(3), rs.getDouble(4), rs.getDouble(5) );
				costMap.put( nostr, cost );
				costList.add( cost );
			}
			
			ps.close();
			
			for( i = 40; i < 100; i++ ) {
				row = sheet.getRow(i);
				if( row != null ) {
					boolean unsucc = true;
					
					cell = row.getCell(0);
					if( cell != null ) {
						int 	d = (int)cell.getNumericCellValue();
						String 	dstr = Integer.toString(d);
						if( d > 0 ) {
							if( costMap.containsKey( dstr) ) {
								Cost cost = costMap.get( dstr );
								cell = row.getCell( k );
								if( d >= 1000 && d < 2000 ) cell.setCellValue( cost.p );
								else cell.setCellValue( cost.c );
							} else {
								double tot = 0.0;
								
								if( dstr.endsWith("99") ) {
									for( Cost c : costList ) {
										if( c.no >= d-999 && c.no < d+1 ) tot += c.c;
									}
								} else if( dstr.endsWith("98") ) {
									for( Cost c : costList ) {
										if( c.no >= d-98 && c.no < d+2 ) tot += c.c;
									}
								} else if( dstr.equals("1993") ) {
									for( Cost c : costList ) {
										if( c.no >= d-993 && c.no < d+7 ) tot += c.p;
									}
								}
								cell = row.getCell( k );
								cell.setCellValue( tot );		
							}
							
							unsucc = false;
						}
					}
					
					if( unsucc ) {
						cell = row.getCell(1);
						if( cell != null && cell.getCellType() == XSSFCell.CELL_TYPE_STRING ) {
							String 	dstr = cell.getStringCellValue();
							if( dstr.equals("Kostnaður samtals") ) {
								double tot = 0.0;
								for( String no : costMap.keySet() ) {
									Cost c = costMap.get(no);
									tot += c.c;
								}
								cell = row.getCell(k);
								cell.setCellValue(tot);
							} else if( dstr.equals("Raun launakostnaður -fjárhagsbókh") ) {
								double tot = 0.0;
								for( String no : costMap.keySet() ) {
									Cost c = costMap.get(no);
									if( c.type.contains("0") ) tot += c.c;
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
		
		workbook.write( new FileOutputStream("/home/sigmar/Desktop/simmi.xlsx") );
	}
	
	public void init() {
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new Report( args );
	}

}
