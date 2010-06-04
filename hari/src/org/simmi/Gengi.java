package org.simmi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Gengi {
	static Connection con;
	
	public static Connection connect() throws SQLException, ClassNotFoundException {
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");		
		String connectionUrl = "jdbc:sqlserver://navision.rf.is:1433;databaseName=order;user=simmi;password=mirodc30;";
		//String connectionUrl = "jdbc:sqlserver://navision.rf.is:1433;databaseName=order;integratedSecurity=true;";
		return DriverManager.getConnection(connectionUrl);
	}
	
	public static class Timi {
		Float	val;
		String	date;
		
		public Timi( float val, String date ) {
			this.val = val;
			this.date = date;
		}
	}
	
	public static class Timarod {
		String		type;
		List<Timi>	data;
		
		public Timarod( String type ) {
			this.type = type;
			this.data = new ArrayList<Timi>();
		}
		
		public void add( float val, String date ) {
			data.add( new Timi( val, date ) );
		}
	};
	
	public static void main(String[] args) {
		try {
			List<Timarod>	tlist = new ArrayList<Timarod>();
			
			File f = new File( args[0] );
			FileInputStream	fis = new FileInputStream(f);
			//FileReader	fr = new FileReader( f );
			
			byte[]	cbuf = new byte[(int)f.length()];
			int rr = fis.read(cbuf);
			
			String s = new String( cbuf, "ISO-8859-1" );
			int r = s.indexOf("<tr>", 0);
			int tlok = s.indexOf("</table>", 0);
			int filled = 0;
			while( r != -1 ) {
				int n = s.indexOf( "</tr>", r );
				int h = s.indexOf("<th", r);
				int k = 0;
				if( h != -1 && h < n ) {
					int d = s.indexOf("<th", r);
					if( d != -1 && d < n ) {
						int nn = s.indexOf(">", d);
						int nnn = s.indexOf("</th>", d);
						//date = s.substring(nn+1,nnn);
						d = s.indexOf("<th", nnn);
					}
					while( d != -1 && d < n && d < tlok ) {
						int nn = s.indexOf("</th>", d);
						//System.err.print(s.substring(d+4,nn)+"\t");
						String name = s.substring(d+4,nn);
						if( filled == 0 ) tlist.add( new Timarod( name ) );
						else if( filled == 1 ) {
							if( k < tlist.size() ) {
								String type = tlist.get(k).type;
								tlist.get(k).type = type + " " + name;
							}
						}
						d = s.indexOf("<th", nn);
						
						k++;
					}
					
					filled++;
				} else {
					int d = s.indexOf("<td", r);
					String date = "";
					if( d != -1 && d < n ) {
						int nn = s.indexOf(">", d);
						int nnn = s.indexOf("</td>", d);
						date = s.substring(nn+1,nnn);
						d = s.indexOf("<td", nnn);
					}
					while( d != -1 && d < n && d < tlok ) {
						int nn = s.indexOf(">", d);
						int nnn = s.indexOf("</td>", d);
						String val = s.substring(nn+1,nnn);
						//System.err.print(s.substring(nn+1,nnn)+"\t");
						d = s.indexOf("<td", nnn);
						
						float fl = -1.0f;
						if( val.length() > 0 ) {
							try {
								fl = Float.parseFloat( val.replace(',', '.') );
							} catch( Exception e ) {
								
							}
						}
						tlist.get(k).add(fl, date);
						
						k++;
					}
				}
				
				r = s.indexOf("<tr>", n);
			}
			
			String sql = "insert into [hafsjor].[dbo].[timaradir] (heiti) values (?)";
			PreparedStatement ps = connect().prepareStatement(sql);
			for( Timarod tr : tlist ) {
				ps.setString(1, tr.type);					
				ps.execute();
			}
			ps.close();
			
			sql = "insert into [hafsjor].[dbo].[gildi_timarada] (dagsetning, visitala, gildi) select ?,nr,? from [hafsjor].[dbo].[timaradir] where [heiti] = ?";
			ps = connect().prepareStatement(sql);
			int itr = 0;
			for( Timarod tr : tlist ) {
				int it = 0;
				for( Timi t : tr.data ) {
					//System.err.println(t.date);
					String[] ss = t.date.split("\\.");
					if( ss.length > 2 && ss.length < 12 ) {
						String date = ss[1]+"."+ss[0]+"."+ss[2];
						ps.setString(1, date);
						ps.setFloat(2, t.val);
						ps.setString(3, tr.type);
						ps.execute();
					} else {
						System.err.println( "failed "+t.date+"  "+tr.type );
					}
					it++;
				}
				itr++;
			}
			ps.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}
