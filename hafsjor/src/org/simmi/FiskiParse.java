package org.simmi;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
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
	
	public void updateDb() throws SQLException, ClassNotFoundException, IOException, InterruptedException {
		Set<String>	skipnrset = new HashSet<String>();
		
		String	sql = "select skipsnumer from [hafsjor].[dbo].[skip]"; // where heiti = 'Sæfari'";
		PreparedStatement 	ps = connect().prepareStatement(sql);
		ResultSet 			rs = ps.executeQuery();
		while( rs.next() ) {
			skipnrset.add( rs.getString(1) );
		}
		rs.close();
		ps.close();
		
		run( skipnrset );
	}
	
	public void insertFile( String filename ) throws IOException, SQLException, ClassNotFoundException, InterruptedException {
		Set<String> skipnrset = new HashSet<String>();
		
		BufferedReader br = new BufferedReader( new FileReader( filename ) );
		String line = br.readLine();
		while( line != null ) {
			skipnrset.add( line.trim() );
			line = br.readLine();
		}
		br.close();
		
		runInsert(skipnrset);
	}
	
	public void runInsert( Set<String> skipnrset ) throws SQLException, ClassNotFoundException, IOException, InterruptedException {
		String 				sql = "insert into [hafsjor].[dbo].[skip] (" +
				"skipsnumer, " +
				"heiti, " +
				"einkst, " +
				"heimahofn, " +
				"bruttoton, " +
				//"skipstjori = ?, " +
				"utgerdarflokkur, " +
				//"heimild = ?, " +
				"kennitala_eiganda, " +
				"kennitala_utgerdar " +
				") select ?,?,?,hh.nr,?,uf.nr,?,? from [hafsjor].[dbo].[heimahofn] hh, [hafsjor].[dbo].[utgerdarflokkur] uf where uf.nafn = ? and hh.nafn = ?"; // and heiti = 'Sæfari'";
		PreparedStatement	ps = connect().prepareStatement(sql);
		
		String 				sql2 = "insert into [hafsjor].[dbo].[skip] (" +
		"skipsnumer, " +
		"heiti, " +
		"einkst, " +
		"heimahofn, " +
		"bruttoton, " +
		//"skipstjori = ?, " +
		"utgerdarflokkur, " +
		//"heimild = ?, " +
		"kennitala_eiganda, " +
		"kennitala_utgerdar " +
		") select ?1,?2,?3,hh.nr,?4,uf.nr,?5,?6 from [hafsjor].[dbo].[heimahofn] hh, [hafsjor].[dbo].[utgerdarflokkur] uf where uf.nafn = ?7 and hh.nafn = ?8"; // and heiti = 'Sæfari'";
		
		String[] 	ff = {
				"Skipaskránúmer</strong>&nbsp;:&nbsp;</p></td><td>", 
				"Nafn</strong>&nbsp;:&nbsp;</p></td><td>",
				"Nafn</strong>&nbsp;:&nbsp;</p></td><td>",
				"Heimahöfn</strong>&nbsp;:&nbsp;</p></td><td>",
				//"Brúttórúmml</strong>&nbsp;:&nbsp;</p></td><td>",
				"Brúttótonn</strong>&nbsp;:&nbsp;</p></td><td>",
				//"Lengd(m)</strong>&nbsp;:&nbsp;</p></td><td>",
				"Útgerðarflokkur</strong>&nbsp;:&nbsp;</p></td><td>",
				//"Eigandi</strong>&nbsp;:&nbsp;</p></td><td>",
				"Kennitala eiganda</strong>&nbsp;:&nbsp;</p></td><td>",
				//"Útgerð</strong>&nbsp;:&nbsp;</p></td><td>",
				"Kennitala útgerðar</strong>&nbsp;:&nbsp;</p></td><td>"};
				//"Nafn</strong>&nbsp;:&nbsp;</p></td><td>"};
		
		String end = "</td></tr>";
		
		//Map<>
		int[] varp = {1,2,3,8,4,7,5,6};
		Class[] cls = {String.class,String.class,String.class,String.class,Float.class,String.class,String.class,String.class,String.class};
		
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
			
			int ind1 = 0;
			int i = 0;
			for( String fs : ff ) {
				ind1 = str.indexOf( fs, ind1 );
				int ind2 = str.indexOf( end, ind1 );
			
				int start = ind1+fs.length();
				String nm = str.substring(start, ind2);
			
				sql2 = sql2.replace("?"+varp[i], "'"+nm+"'");
				
				if( cls[i] == String.class ) {
					System.err.println( nm + "  " + nm.length() );
					ps.setString(varp[i], nm);
				} else if( cls[i] == Float.class ) {
					float f = Float.parseFloat(nm);
					System.err.println( f + " (float) " + nm.length() );
					ps.setFloat(varp[i], f);
				}
				i++;
			}
			//ps.setString(count+1, "simsim");
			System.err.println(sql2);
			ps.execute();
			
			System.err.println( "done "+(count++) );
			if( count % 10 == 0 ) Thread.sleep(2000);
			if( count % 100 == 0 ) Thread.sleep(10000);
		}
	}
	
	public void run( Set<String> skipnrset ) throws SQLException, ClassNotFoundException, IOException, InterruptedException {
		String 				sql = "update [hafsjor].[dbo].[skip] set kennitala_eiganda = ?, kennitala_utgerdar = ? where skipsnumer = ?"; // and heiti = 'Sæfari'";
		PreparedStatement	ps = connect().prepareStatement(sql);
		
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
			//new FiskiParse().insertDb();
			new FiskiParse().insertFile("/home/sigmar/skiplist.txt");
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
