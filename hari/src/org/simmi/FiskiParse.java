package org.simmi;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class FiskiParse {
	Connection 		con;
	final String 	slod = "http://www.fiskistofa.is/veidar/aflastada/einstokskip?skipnr=";
	
	public FiskiParse() {
		
	}
	
	public Connection connect() throws SQLException, ClassNotFoundException {
		if( con == null || con.isClosed() ) {
			con =  Uthlutun.connect();
		}
		return con;
	}
	
	public void run() throws SQLException, ClassNotFoundException, IOException, InterruptedException {
		Set<String>	skipnrset = new HashSet<String>();
		
		String	sql = "select skipsnumer from [hafsjor].[dbo].[skip]"; // where heiti = 'Sæfari'";
		PreparedStatement 	ps = connect().prepareStatement(sql);
		ResultSet 			rs = ps.executeQuery();
		while( rs.next() ) {
			skipnrset.add( rs.getString(1) );
		}
		rs.close();
		ps.close();
		
		sql = "update [hafsjor].[dbo].[skip] set kennitala_eiganda = ?, kennitala_utgerdar = ? where skipsnumer = ?"; // and heiti = 'Sæfari'";
		ps = connect().prepareStatement(sql);
		
		String 	f1 = "Kennitala eiganda</strong>&nbsp;:&nbsp;</p></td><td>";
		String	f2 = "Kennitala útgerðar</strong>&nbsp;:&nbsp;</p></td><td>";
		
		int count = 0;
		byte[]	buff = new byte[1024];
		for( String sn : skipnrset ) {
			URL url = new URL( slod + sn );
			InputStream is = url.openStream();
			
			String	str = "";
			int r = is.read( buff );
			while( r > 0 ) {
				str += new String( buff, 0, r, "ISO-8859-1" );
				r = is.read( buff );
			}
			
			int ind1 = str.indexOf( f1 );
			int ind2 = str.indexOf( f2, ind1 );
			
			int start = ind1+f1.length();
			String kt1 = str.substring(start, start+10);
			start = ind2+f2.length();
			String kt2 = str.substring(start, start+10);
			
			ps.setString(1, kt1);
			ps.setString(2, kt2);
			ps.setString(3, sn);
			
			ps.execute();
			
			System.err.println( "done "+(count++) );
			if( count % 10 == 0 ) Thread.sleep(2000);
			if( count % 100 == 0 ) Thread.sleep(10000);
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			new FiskiParse().run();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
