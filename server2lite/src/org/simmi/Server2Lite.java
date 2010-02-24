package org.simmi;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Server2Lite {	
	Connection con;
	Connection dcon;
	
	public Server2Lite() throws ClassNotFoundException, SQLException {
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		String connectionUrl = "jdbc:sqlserver://navision.rf.is:1433;databaseName=isgem2;user=simmi;password=drsmorc.311;";
		con = DriverManager.getConnection(connectionUrl);
		
		//Class.forName("org.sqlite.JDBC");
		//connectionUrl = "jdbc:sqlite:isgem.db";
		
		Class.forName("com.mysql.jdbc.Driver");
		connectionUrl = "jdbc:mysql://localhost:3306/isgem"; //?useUnicode=yes&characterEncoding=UTF-8";
		dcon = DriverManager.getConnection(connectionUrl, "root", "drsmorc.311");
	}
	
	public void load() throws SQLException {
		String sql = "select * from isgem2.sys.tables";
		PreparedStatement ps = con.prepareStatement( sql );
		ResultSet rs = ps.executeQuery();
		
		Map<String,String>	tmap = new HashMap<String,String>();
		
		while( rs.next() ) {
			String tname = rs.getString(1);
			
			if( !tname.contains("sys") ) {
				sql = "select co.name, ty.name, co.max_length from isgem2.sys.columns co, isgem2.sys.tables ta, isgem2.sys.types ty where ty.user_type_id=co.user_type_id and ta.object_id=co.object_id and ta.name='"+tname+"'";
				PreparedStatement 	psub = con.prepareStatement( sql );
				ResultSet 			rsub = psub.executeQuery();
				
				String val = "";
				while( rsub.next() ) {
					String coname = rsub.getString(1);
					String tyname = rsub.getString(2);
					String mlname = rsub.getString(3);
					
					if( coname.equalsIgnoreCase("Precision") ) coname = "Prec";
					
					if( tyname.equals("nchar") ) {
						int v = Integer.parseInt( mlname );
						if( v > 255 ) tyname = "varchar";
					}
					else if( tyname.contains("char") && mlname.equals("-1") ) mlname = "4000";
					else if( tyname.equals("uniqueidentifier") ) {
						tyname = "nchar";
						mlname = "255";
					}
					else if( tyname.equals("image") ) tyname = "varbinary(4000)";
					
					if( val.length() == 0 ) val += coname + " " + tyname;
					else val += "," + coname + " " + tyname;
					
					 if( tyname.contains("char") ) val += "(" + mlname + ")";
					 //else if( tyname.equals("uniqueidentifier") ) val += " int";
					//exec( "create table "+tname );
				}
				tmap.put( tname, val );
				sql = "create table "+tname+" ("+val+")";
				
				System.err.println( sql );
				
				PreparedStatement dps = dcon.prepareStatement( sql );
				dps.execute();
				dps.close();
				
				rsub.close();
				psub.close();
			}
			
		}
		
		rs.close();
		ps.close();
		
		String[]	ss = {"0001", "0002", "0003", "0004", "0005", "0006", "0008", "0009", "0010", "0011", "0012", "0013", "0014", "0016", "0017", "0020", "0021", "0023", "0024", "0029", "0031", "0032", "0033", "0034", "0035", "0036", "0037", "0038", "0039", "0040", "0041", "0044", "0137", "0138"};
		final Set<String>	sset = new HashSet<String>( Arrays.asList(ss) );
		
		int k = 0;
		for( String tname : tmap.keySet() ) {
			sql = "select * from isgem2.dbo."+tname;
			ps = con.prepareStatement( sql );
			rs = ps.executeQuery();
			
			String val = tmap.get(tname);
			while( rs.next() ) {
				String[] split = val.split(",");
				
				// SelectedValue numeric em -> -1
				if( !tname.equals("ComponentValue") || sset.contains( rs.getString(3) ) ) {
					int i = 0;
					String vstr = "";
					for( String str : split ) {
						String[] vspl = str.split(" ");
						String res = "";
						if( vspl[1].contains("char") ) {
							String sval = rs.getString(++i);
							if( sval == null || sval.equals("null") ) res += "null";
							else {
								sval = sval.replace("\"", "\\\"");
								res += "\""+sval+"\"";
							}
						}
						else if( vspl[1].contains("date") ) {
							Date date = rs.getDate(++i);
							if( date == null || date.toString().equalsIgnoreCase("null") ) res += "null";
							else res += "\""+date+"\"";
						}
						else if( vspl[1].contains("float") ) res += rs.getFloat(++i);
						else if( vspl[1].contains("double") ) res += rs.getDouble(++i);
						else if( vspl[1].contains("real") ) res += rs.getDouble(++i);
						else if( vspl[1].contains("int") ) res += rs.getInt(++i);
						else {
							String sval = rs.getString(++i);
							if( sval == null || sval.equalsIgnoreCase("null") ) res += "null";
							else {
								sval = sval.replace("\"", "\\\"");
								res += "\""+sval+"\"";
							}
						}
						
						if( vstr.length() == 0 ) vstr += res;
						else vstr += ","+res;
					}
					
					String dsql = "insert into "+tname+" values ("+vstr+")";
					if( k++ % 100 == 0 ) System.err.println( dsql );
					
					PreparedStatement dps = dcon.prepareStatement( dsql );
					dps.execute();
					dps.close();
					//exec( "insert into "+tname+" values ("+vstr+")" );
				}
			}
			
			rs.close();
			ps.close();
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Server2Lite s2l = new Server2Lite();
			//s2l.test();
			s2l.load();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
