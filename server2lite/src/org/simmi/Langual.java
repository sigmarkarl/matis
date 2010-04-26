package org.simmi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Langual {
	Connection con;
	
	public Langual() throws ClassNotFoundException, SQLException {
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		String connectionUrl = "jdbc:sqlserver://navision.rf.is:1433;databaseName=isgem2;user=simmi;password=drsmorc.311;";
		con = DriverManager.getConnection(connectionUrl);
	}
	
	public static void main(String[] args) {
		try {
			Langual lang = new Langual();
			File f = new File( "/home/sigmar/workspace/server2lite/langual.txt" ); //args[0] );
			lang.load( f );
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void load( File f ) throws IOException, SQLException { 		
		FileReader 		fr = new FileReader(f);
		BufferedReader 	br = new BufferedReader( fr );
		
		PreparedStatement	ips = con.prepareStatement( "update [Isgem2].[dbo].[Food] set LangualCodes = ? where OriginalFoodCode = ?" );
		
		String line = br.readLine();
		line = br.readLine();
		while( line != null ) {
			String[] split = line.split("[\t]+");
			if( split.length == 4 ) ips.setString(1, split[split.length-1] );
			else ips.setString(1, null);
			ips.setString(2, split[0] );
			ips.execute();
			
			line = br.readLine();
		}
		ips.close();
	}
}
