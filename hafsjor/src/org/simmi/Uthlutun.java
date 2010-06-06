package org.simmi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class Uthlutun {
	static Connection con;
	
	public static Connection connect() throws SQLException, ClassNotFoundException {
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");		
		String connectionUrl = "jdbc:sqlserver://navision.rf.is:1433;databaseName=order;user=simmi;password=mirodc30;";
		//String connectionUrl = "jdbc:sqlserver://navision.rf.is:1433;databaseName=order;integratedSecurity=true;";
		return DriverManager.getConnection(connectionUrl);
	}
	
	static String[] fiskar = new String[] {"Þorskur", "Ýsa", "Ufsi", "Karfi", "Langa", "Keila", "Steinbítur", "Skötuselur", "Grálúða", "Skarkoli", "Þykkvalúra", "Langlúra", "Sandkoli", "Skrápflúra", "Síld", "Humar", "Úthafsrækja", "Þorskígildi"};
	
	public static class Skip {
		String nr;
		String nafn;
		String eink;
		String flokkur;
		SortedMap<String,String> hafnir = new TreeMap<String,String>();
		String tonn;
		String heimild;
		
		Map<String,Map<String,Float>>	aflamark = new HashMap<String,Map<String,Float>>();
		
		public Skip( String nr, String nafn, String eink, String flokkur, String hofn, String tonn, String heimild ) {
			this.nr = nr;
			this.nafn = nafn;
			this.eink = eink;
			this.flokkur = flokkur;
			this.tonn = tonn;
			this.heimild = heimild;
			
			String year = heimild.substring(heimild.length()-8, heimild.length()-4);
			hafnir.put(year, hofn);
		}
		
		public void addHofn( String year, String hofn ) {
			if( hafnir.containsKey( year ) ) {
				System.err.println("erm");
			}
			hafnir.put( year, hofn );
		}
		
		public void addAflamark( String fiskur, String year, float value ) {
			Map<String,Float>	am;
			if( !aflamark.containsKey( year ) ) {
				am = new HashMap<String,Float>();
			} else {
				am = aflamark.get(year);
			}
			am.put(fiskur, value);
		}
	};
	
	public static void insertAflamark( Map<String,Skip>	skipMap ) throws SQLException, ClassNotFoundException {
		con = connect();
		String	sql1 = "select ?,sk.nr,ft.nr,? from [hafsjor].[dbo].[skip] sk, [hafsjor].[dbo].[fisktegund] ft where sk.skipsnumer = ? and ft.heiti = ?";
		String 	sql = "insert into [hafsjor].[dbo].[aflamark] (artal,skip,tegund,magn) " + sql1; 
		PreparedStatement ps = con.prepareStatement( sql );
		for( String skipname : skipMap.keySet() ) {
			Skip skip = skipMap.get( skipname );
			
			for( String year : skip.aflamark.keySet() ) {
				Map<String,Float>	am = skip.aflamark.get(year);
				for( String fiskur : am.keySet() ) {
					Float f = am.get(fiskur);
					ps.setString( 1, year );
					ps.setFloat( 2, f );
					ps.setString( 3, skip.nr );
					ps.setString( 4, fiskur );
					
					if( fiskur.equals("Þorskígildi") ) {
						System.err.println( year + "  " + fiskur + "  " + skip.nafn );
					}
					
					ps.execute();
				}
			}
		}
		ps.close();
		con.close();
	}
	
	public static void insertSkip( Map<String,Skip> skipMap, Map<String,String> varpFlokkur ) throws SQLException, ClassNotFoundException {
		int count = 0;
		con = connect();
		String	sql1 = "select ?,?,?,hh.nr,?,ut.nr,hm.nr from [hafsjor].[dbo].[heimahofn] hh, [hafsjor].[dbo].[utgerdarflokkur] ut, [hafsjor].[dbo].[heimildir] hm where hh.nafn = ? and ut.nafn = ? and hm.heiti = ?";
		String 	sql = "insert into [hafsjor].[dbo].[skip] (skipsnumer,heiti,einkst,heimahofn,bruttoton,utgerdarflokkur,heimild) " + sql1; 
			//"insert into [hafjor].[dbo].[skip] sk (skipsnumer,heiti,einkst,heimahofn,bruttoton) select where hh.nafn = ?";
		PreparedStatement ps = con.prepareStatement( sql );
		for( String skipname : skipMap.keySet() ) {
			Skip skip = skipMap.get( skipname );
			
			if( skip.hafnir.size() > 9 ) {
				for( String y : skip.hafnir.keySet() ) {
					System.err.print( y + " " );
				}
				System.err.println( skip.nafn + " " + skipname );
			}
			
			ps.setString( 1, skip.nr );
			ps.setString( 2, skip.nafn );
			ps.setString( 3, skip.eink );
			ps.setDouble( 4, Double.parseDouble(skip.tonn) );
			
			String hofn = skip.hafnir.get( skip.hafnir.lastKey() );
			ps.setString( 5, hofn );
			ps.setString( 6, varpFlokkur.get(skip.flokkur) );
			ps.setString( 7, skip.heimild );
			ps.execute();
			
			/*count++;
			if( count % 100 == 0 ) {
				ps.close();
				ps = con.prepareStatement( sql );
			}*/
			
			/*System.err.println( count + " done " + skipname );
			if( skipname.contains("1964") ) {
				System.err.println();
			}*/
		}
		ps.close();
		
		sql1 = "select hh.nr,sk.nr,hm.nr,? from [hafsjor].[dbo].[heimahofn] hh, [hafsjor].[dbo].[heimildir] hm, [hafsjor].[dbo].[skip] sk where sk.skipsnumer = ? and hh.nafn = ? and hm.heiti = ?";
		sql = "insert into [hafsjor].[dbo].[heimahofn_skip] (heimahofn,skip,heimild,artal) " + sql1; 
			//"insert into [hafjor].[dbo].[skip] sk (skipsnumer,heiti,einkst,heimahofn,bruttoton) select where hh.nafn = ?";
		ps = con.prepareStatement( sql );
		for( String skipname : skipMap.keySet() ) {
			Skip skip = skipMap.get( skipname );
			
			for( String year : skip.hafnir.keySet() ) {
				String hofn = skip.hafnir.get( year );
				ps.setString( 1, "01-01-"+year );
				ps.setString( 2, skip.nr );
				ps.setString( 3, hofn );
				
				int y = Integer.parseInt(year);
				ps.setString( 4, "uthlutun_"+(y-1)+"_"+year+".xls" );
				ps.execute();
			}
		}
		ps.close();
		con.close();
	}
	
	/**
	 * @param args
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 */
	public static void main(String[] args) throws SQLException, ClassNotFoundException {
		File	fdir = new File( args[0] );
		
		Map<String,String>	varpFlokkur = new HashMap<String,String>();
		varpFlokkur.put("Krókaaflamarksbátur", "Krókaaflamarksbátur");
		varpFlokkur.put("Smábátur með aflamark", "Smábátur með aflamark");
		varpFlokkur.put("Skuttogari", "Skuttogari");
		varpFlokkur.put("Krókaflamarksheimild", "Krókaflamarksheimild");
		varpFlokkur.put("Aflamarksheimild", "Aflamarksheimild");
		varpFlokkur.put("Skip með aflamark", "Skip með aflamark");
		varpFlokkur.put("Krókaaflamark", "Krókaaflamarksbátur");
		
		Set<String>	fiskset = new HashSet<String>( Arrays.asList(fiskar) );
		
		if( fdir.isDirectory() ) {
			File[] files = fdir.listFiles( new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					if( name.endsWith( ".xls" ) ) return true;
					return false;
				}
			});
			
			Map<String,Skip> skipMap = new HashMap<String,Skip>();
			for( File file : files ) {
				String fname = file.getName();
				String year = fname.substring(fname.length()-8, fname.length()-4);
				System.err.println( fname );
				FileInputStream stream;
				
				Map<Integer,String>	cellind = new HashMap<Integer,String>();
				try {					
					stream = new FileInputStream( file );
					Workbook wb = WorkbookFactory.create( stream );
					
					Sheet 	sheet = wb.getSheetAt( 0 );
					
					int 	r = 0;
					Row		row = sheet.getRow( r++ );
					
					int hofnind = -1;
					int btonnind = -1;
					int flkind = -1;
					int einkind = -1;
					
					//DataFormatter df;
					//df.
					
					boolean start = false;
					while( (row != null || !start) && r < 1000000 ) {
						Cell cell = row == null ? null : row.getCell(0);
						if( cell != null ) {
							String strc = cell.getCellType() == Cell.CELL_TYPE_STRING ? cell.getStringCellValue() : "";
							if( start ) {
								cell = row.getCell(0);
								String nr = "";
								if( cell.getCellType() == Cell.CELL_TYPE_NUMERIC ) {
									nr = Integer.toString( (int)cell.getNumericCellValue() );
								} else {
									nr = cell.getStringCellValue();
								}
								cell = row.getCell(1);
								String nafn = cell == null ? null : cell.getStringCellValue();
								if( nafn != null && nafn.length() > 0 ) {
									String eink = "";
									if( einkind != -1 ) {
										cell = row.getCell( einkind );
										eink = cell.getStringCellValue();
									} else {
										String[] split = nafn.split(" ");
										if( split.length > 1 ) {
											nafn = split[0];
											for( int i = 1; i < split.length-2; i++ ) {
												nafn += " "+split[i];
											}
											eink = split[split.length-2]+" "+split[split.length-1];
										} else {
											nafn = split[0];
											eink = split[split.length-1];
										}
									}
									
									//System.err.println( "bb "+btonnind );
									if( flkind != -1 ) cell = row.getCell( flkind );
									else cell = row.getCell(4);
									String flokkur = cell.getStringCellValue();
									
									if( hofnind != -1 ) cell = row.getCell( hofnind );
									else cell = row.getCell(6);
									String hofn = cell.getStringCellValue();
									
									if( btonnind != -1 ) cell = row.getCell( btonnind );
									else cell = row.getCell(7);
									String tonn = "";
									
									if( cell.getCellType() == Cell.CELL_TYPE_NUMERIC ) {
										tonn = Double.toString( cell.getNumericCellValue() );
									} else {
										tonn = cell.getStringCellValue();
									}
									
									String skeit = nr;
									Skip skip;
									if( skipMap.containsKey(skeit) ) {
										skip = skipMap.get( skeit );
										skip.heimild = fname.toLowerCase();
										skip.addHofn( year, hofn );
									} else {
										skip = new Skip( nr, nafn, eink, flokkur, hofn, tonn, fname.toLowerCase() );
										skipMap.put( skeit, skip );
									}
									
									for( Integer itg : cellind.keySet() ) {
										String fiskur = cellind.get(itg);
										Cell vcell = row.getCell(itg);
										if( vcell != null && vcell.getCellType() == Cell.CELL_TYPE_NUMERIC ) {
											double nv = vcell.getNumericCellValue();
											
											if( nv > 0.0 ) {
												Map<String,Float>	am;
												if( !skip.aflamark.containsKey(year) ) {
													am = new HashMap<String,Float>();
													skip.aflamark.put( year, am );
												} else {
													am = skip.aflamark.get( year );
												}
												am.put(fiskur, (float)nv);
											}
										}
									}
									
									//System.err.println( cell.getStringCellValue() );
									/*ResultSet rs = ps.executeQuery();
									while( rs.next() ) {
										System.err.println( "erm " + rs.getString(1) );
									}
									rs.close();*/
								}
							} else if( strc.contains("Nr") ) {
								start = true;
								
								int i = 1;
								cell = row.getCell( i++ );
								while( cell != null ) { //&& (hofnind == -1 || btonnind == -1 || flkind == -1) ) {
									String cellname = cell.getStringCellValue();
									//System.err.println(cellname);
									if( cellname.contains("Heimahöfn") || cellname.contains("Heiti Hafnar") ) hofnind = i-1;
									else if( cellname.contains("Útgerðarflokkur") ) flkind = i-1;
									else if( cellname.contains("tonn") ) btonnind = i-1;
									else if( cellname.contains("Einks") ) einkind = i-1;
									else if( fiskset.contains( cellname ) ) {
										cellind.put( i-1, cellname );
									}
									
									cell = row.getCell( i++ );
								}
							}
						}
						
						row = sheet.getRow(r++);
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (InvalidFormatException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			//System.err.println( "ermermerm " + skipMap.get("1964") + "  " + skipMap.size() );
			//int count = 0;
			
			Set<String>	ermSet = new HashSet<String>();
			FileReader fr;
			try {
				fr = new FileReader("/home/sigmar/table_export.csv");
				BufferedReader			br = new BufferedReader( fr );
				String line = br.readLine();
				while( line != null ) {
					ermSet.add( line );
					line = br.readLine();
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.err.println( ermSet.size() + "  " + skipMap.keySet().size() );
			
			Set<String>	newSet = new HashSet<String>( skipMap.keySet() );
			newSet.removeAll( ermSet );
			for( String erm : newSet ) {
				System.err.println( "fuck " + erm );
			}
			
			//insertSkip( skipMap, varpFlokkur );
			insertAflamark( skipMap );
		}
	}
}
