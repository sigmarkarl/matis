package org.simmi;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class Server2Lite {
	static {
		System.loadLibrary("sqlite");
	}
	
	public native int test();
	public native int exec( String sql );
	
	Connection con;
	
	public Server2Lite() throws ClassNotFoundException, SQLException {
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		String connectionUrl = "jdbc:sqlserver://navision.rf.is:1433;databaseName=isgem2;user=simmi;password=drsmorc.311;";
		con = DriverManager.getConnection(connectionUrl);
	}
	
	public void load() throws SQLException {
		String sql = "select * from isgem2.sys.tables";
		PreparedStatement ps = con.prepareStatement( sql );
		ResultSet rs = ps.executeQuery();
		
		Map<String,String>	tmap = new HashMap<String,String>();
		
		while( rs.next() ) {
			String tname = rs.getString(1);
			
			sql = "select co.name, ty.name, ty.max_length from isgem2.sys.columns co, isgem2.sys.tables ta, isgem2.sys.types ty where ty.user_type_id=co.user_type_id and ta.object_id=co.object_id and ta.name='"+tname+"'";
			PreparedStatement 	psub = con.prepareStatement( sql );
			ResultSet 			rsub = psub.executeQuery();
			
			String val = "";
			while( rsub.next() ) {
				String coname = rsub.getString(1);
				String tyname = rsub.getString(2);
				String mlname = rsub.getString(3);
				
				if( val.length() == 0 ) val += coname + " " + tyname;
				else val += "," + coname + " " + tyname;
				//exec( "create table "+tname );
			}
			tmap.put( tname, val );
			sql = "create table "+tname+" ("+val+")";
			exec( sql );
			
			rsub.close();
			psub.close();
			
		}
		
		rs.close();
		ps.close();
		
		for( String tname : tmap.keySet() ) {
			sql = "select * from isgem2.dbo."+tname;
			ps = con.prepareStatement( sql );
			rs = ps.executeQuery();
			
			String val = tmap.get(tname);
			while( rs.next() ) {
				String[] split = val.split(",");
				int i = 0;
				String vstr = "";
				for( String str : split ) {
					String[] vspl = str.split(" ");
					String res = "";
					if( vspl[1].contains("char") ) res += "\""+rs.getString(++i)+"\"";
					else if( vspl[1].contains("date") ) res += "\""+rs.getDate(++i)+"\"";
					else if( vspl[1].contains("float") ) res += rs.getFloat(++i);
					else if( vspl[1].contains("double") ) res += rs.getDouble(++i);
					else if( vspl[1].contains("int") ) res += rs.getInt(++i);
					else res += "\""+rs.getString(++i)+"\"";
					
					if( vstr.length() == 0 ) vstr += res;
					else vstr += ","+res;
				}
				exec( "insert into "+tname+" values ("+vstr+")" );
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
