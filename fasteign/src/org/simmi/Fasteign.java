package org.simmi;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JApplet;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Fasteign extends JApplet {
	class Ibud {
		String nafn;
		int verd;
		int fastm;
		int brunm;
		String teg;
		int ferm;
		int herb;
		String dat;
		
		public Ibud( String nafn ) {
			this.nafn = nafn;
		}
		
		public Ibud( String nafn, int verd, int fastm, int brunm, String teg, int ferm, int herb, String dat ) {
			this.nafn = nafn;
			this.verd = verd;
			this.fastm = fastm;
			this.brunm = brunm;
			this.teg = teg;
			this.ferm = ferm;
			this.herb = herb;
			this.dat = dat;
		}
		
		public void set( int i, String val ) {
			val = val.replaceAll("\\.", "");
			try {
				if( i == 0 ) verd = Integer.parseInt(val);
				else if( i == 1 ) fastm = Integer.parseInt(val);
				else if( i == 2 ) brunm = Integer.parseInt(val);
				else if( i == 3 ) teg = val;
				else if( i == 4 ) ferm = Integer.parseInt(val);
				else if( i == 5 ) herb = Integer.parseInt(val);
				else if( i == 6 ) dat = val;
			} catch( Exception e ) {
				e.printStackTrace();
			}
		}
		
		public String toString() {
			return nafn + "\t" + verd + "\t" + fastm + "\t" + brunm + "\t" + teg + "\t" + ferm + "\t" + herb + "\t" + dat;
		}
	}
	
	public void stuff( String urlstr ) throws IOException, InterruptedException {
		URL url = new URL( urlstr );
		InputStream stream = url.openStream();
		
		String str = "";		
		byte[]	bb = new byte[1024];
		int r = stream.read( bb );
		while( r > 0 ) {
			str += new String( bb, 0, r );
			r = stream.read( bb );
		}
		
		stream.close();
		
		String[] buds = {"estate-verd","estate-fasteignamat","estate-brunabotamat","estate-teg_eign","estate-fermetrar","estate-fjoldi_herb","estate-sent_dags"};
		
		String[] vals = str.split("fast-nidurstada clearfix");
		System.err.println(vals.length);
		String h2 = "<h2 style=\"margin-bottom: 0.91em; font-size:1.5em;\">";
		for( String val : vals ) {
			int ind = val.indexOf("<a href=\"");
			int stop = val.indexOf("\"", ind+10);
			
			String sub = val.substring(ind+9, stop);
			if( sub.contains("/mm/fasteignir") ) {
				url = new URL( "http://www.mbl.is"+sub );
				stream = url.openStream();
				
				str = "";		
				r = stream.read( bb );
				while( r > 0 ) {
					str += new String( bb, 0, r, "ISO-8859-1" );
					r = stream.read( bb );
				}
				stream.close();
				
				ind = str.indexOf(h2);
				stop = str.indexOf("</h2>",ind);
				String ibud = str.substring( ind+h2.length(), stop ).trim();
				Ibud ib = new Ibud( ibud );
				iblist.add( ib );
				int i = 0;
				for( String bud : buds ) {
					ind = str.indexOf(bud);
					int start = str.indexOf("fst-rvalue\">", ind);
					stop = str.indexOf("</td>", start);
					String sval = str.substring(start+12, stop).trim();
					
					ib.set( i++, sval );
				}
			}
			
			Thread.sleep(200);
		}
	}
	
	List<Ibud>	iblist = new ArrayList<Ibud>();
	public void init() {
		try {
			//String base = "http://www.mbl.is/mm/fasteignir/leit.html?simmi;svaedi=101_101&tegund=fjolbyli&tegund=einbyli&tegund=haedir&fermetrar_fra=70&fermetrar_til=150&herbergi_fra=&herbergi_til=&verd_fra=10&verd_til=40&gata=&lysing=";
			//String base = "http://www.mbl.is/mm/fasteignir/leit.html?simmi;svaedi=200_200&svaedi=201_201&svaedi=202_202&svaedi=203_203&tegund=fjolbyli&fermetrar_fra=70&fermetrar_til=150&herbergi_fra=&herbergi_til=&verd_fra=10&verd_til=40&gata=&lysing=";
			String base = "http://www.mbl.is/mm/fasteignir/leit.html?simmi;svaedi=108_108&tegund=fjolbyli&fermetrar_fra=70&fermetrar_til=150&herbergi_fra=&herbergi_til=&verd_fra=10&verd_til=40&gata=&lysing=";
			
			for( int i = 0; i <= 50; i+=25 ) {
				System.err.println("offset " + i);
				stuff( base.replace("simmi", "offset="+i) );
			}
			
			for( Ibud ib : iblist ) {
				System.err.println( ib );
			}
			
			File f = File.createTempFile("tmp", ".xlsx");
			XSSFWorkbook wb = new XSSFWorkbook();
			XSSFSheet sheet = wb.createSheet("Fasteignir");
			int i = 0;
			for( Ibud ib : iblist ) {
				XSSFRow row = sheet.createRow( i++ );
				int c = 0;
				XSSFCell cell = row.createCell(c++);
				cell.setCellValue( ib.nafn );
				cell = row.createCell(c++);
				cell.setCellValue( ib.verd );
				cell = row.createCell(c++);
				cell.setCellValue( ib.fastm );
				cell = row.createCell(c++);
				cell.setCellValue( ib.brunm );
				cell = row.createCell(c++);
				cell.setCellValue( ib.teg );
				cell = row.createCell(c++);
				cell.setCellValue( ib.ferm );
				cell = row.createCell(c++);
				cell.setCellValue( ib.herb );
				cell = row.createCell(c++);
				cell.setCellValue( ib.dat );
			}
			wb.write( new FileOutputStream(f) );
			Desktop.getDesktop().open( f );
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
