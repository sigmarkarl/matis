package org.simmi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class Citation {
	Connection con;
	
	public Citation() throws ClassNotFoundException, SQLException {
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		String connectionUrl = "jdbc:sqlserver://navision.rf.is:1433;databaseName=isgem2;user=simmi;password=drsmorc.311;";
		con = DriverManager.getConnection(connectionUrl);
	}
	
	public static void main(String[] args) {
		try {
			Citation cita = new Citation();
			File f = new File( "/home/sigmar/isgemref.xls" ); //args[0] );
			cita.load( f );
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
		FileInputStream fis = new FileInputStream(f);
		HSSFWorkbook 	wb = new HSSFWorkbook( fis );
		HSSFSheet		sheet = wb.getSheetAt(0);
		
		PreparedStatement	ips = con.prepareStatement( "update [Isgem2].[dbo].[Reference] set NewCitation = ? where OriginalReferenceCode = ?" );
		
		int i = 6;
		HSSFRow			row = sheet.getRow(i);
		while( row != null ) {
			HSSFCell cell1 = row.getCell(0);
			HSSFCell cell2 = row.getCell(7);
			
			if( cell1 != null && cell2 != null ) {
				ips.setString(1, cell2.getStringCellValue() );
				if( cell1.getCellType() == HSSFCell.CELL_TYPE_NUMERIC ) ips.setInt(2, (int)cell1.getNumericCellValue() );
				else if( cell1.getCellType() == HSSFCell.CELL_TYPE_STRING ) ips.setString(2, cell1.getStringCellValue() );
				ips.execute();
			}
			i++;
			row = sheet.getRow(i);
		}
		ips.close();
	}
}
