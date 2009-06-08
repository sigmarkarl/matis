package org.simmi;

import java.io.IOException;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Parentage {
	public Parentage() {
		super();
	}
	
	public void load() throws IOException {
		XSSFWorkbook	workbook = new XSSFWorkbook("/home/sigmar/Desktop/hestar.xlsx");
		XSSFSheet		m_sheet = workbook.getSheet("Mæður");
		XSSFSheet		b_sheet = workbook.getSheet("Afkvæmi");
		
		int m = 1;
		XSSFRow 	b_row = m_sheet.getRow(1);
		XSSFRow 	m_row = m_sheet.getRow(m++);
		
		int c = 2;
		
		int dcount = 0;
		int	mcount = 0;
		
		XSSFCell	b_a1 = b_row.getCell(c);
		XSSFCell	b_a2 = b_row.getCell(c+1);
		XSSFCell	m_a1 = m_row.getCell(c-1);
		XSSFCell	m_a2 = m_row.getCell(c);
		while( m_row != null ) {
			String ba1 = b_a1.getStringCellValue();
			String ba2 = b_a2.getStringCellValue();
			String ma1 = m_a1.getStringCellValue();
			String ma2 = m_a2.getStringCellValue();
			
			boolean mb = ma1.equals("") || ma2.equals("") || ba1.equals("") || ba2.equals("");
			if( ma1.equals( ba1 ) || ma1.equals( ba2 ) || ma2.equals( ba1 ) || ma2.equals( ba2 ) 
					|| ma1.equals("") || ma2.equals("") || ba1.equals("") || ba2.equals("") );
			
			c += 2;
			b_a1 = b_row.getCell(c);
			b_a2 = b_row.getCell(c+1);
			m_a1 = m_row.getCell(c-1);
			m_a2 = m_row.getCell(c);
			m_row = m_sheet.getRow(m++);
		}
	}
}
