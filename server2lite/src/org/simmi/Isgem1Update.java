package org.simmi;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.Database.FileFormat;
import com.healthmarketscience.jackcess.DatabaseBuilder;
import com.healthmarketscience.jackcess.util.ImportUtil;
import com.sun.jna.Platform;

public class Isgem1Update {
	Connection con;
	
	JProgressBar	pb;
	JDialog			dialog;
	
	public Isgem1Update() throws ClassNotFoundException, SQLException {
		//Class.forName("net.sourceforge.jtds.jdbc.Driver");
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		//String connectionUrl = "jdbc:jtds:sqlserver://navision.rf.is:1433/isgem2";
		String connectionUrl = "jdbc:sqlserver://navision.rf.is:1433;databaseName=isgem2";
		
		/*if( Platform.isWindows() ) {
			connectionUrl = "jdbc:sqlserver://navision.rf.is:1433;databaseName=isgem2;integratedSecurity=false;";
			InputStream is;
			if( Platform.is64Bit() ) {
				is = this.getClass().getResourceAsStream("auth/x64/sqljdbc_auth.dll");
			} else {
				is = this.getClass().getResourceAsStream("auth/x86/sqljdbc_auth.dll");
			}
		}*/
		con = DriverManager.getConnection(connectionUrl,"simmi","mirodc30");
		
		SwingUtilities.invokeLater( new Runnable() {
			@Override
			public void run() {
				pb = new JProgressBar();
				pb.setIndeterminate( true );
				JComponent	c = new JComponent() {
					public void setBounds( int x, int y, int w, int h ) {
						super.setBounds(x, y, w, h);
						pb.setBounds(10, 10, w-20, 20);
					}
				};
				c.add( pb );
				
				dialog = new JDialog();
				//dialog.getContentPane().setLayout( new BorderLayout() );
				dialog.setModal( true );
				dialog.setDefaultCloseOperation( JDialog.DISPOSE_ON_CLOSE );
				dialog.setSize(400, 150);
				dialog.setTitle("Updating ISGEM1");
				dialog.add( c );
				dialog.setVisible(true);		
			}
		});
	}
	
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
		try {
			Isgem1Update i1u = new Isgem1Update();
			i1u.load();
			i1u.save();
			i1u.dialog.setVisible( false );
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.exit( 0 );
	}
	
	Map<String,float[]> enMap = new HashMap<String,float[]>();

	private void save() throws IOException, SQLException {
		System.err.println( "about to write" );
		
		Database isdb = DatabaseBuilder.create( FileFormat.V2003, new File("ISDB.mdb") );
		
		String sql = "select * from [ISDB].[dbo].[EFNI]";
		PreparedStatement	ps = con.prepareStatement( sql );
		ResultSet	rs = ps.executeQuery();
		new ImportUtil.Builder(isdb, "efni").importResultSet(rs);
		//isdb.copyTable("efni", rs);
		rs.close();
		ps.close();
		
		sql = "select * from [ISDB].[dbo].[FAEDA]";
		ps = con.prepareStatement( sql );
		rs = ps.executeQuery();
		new ImportUtil.Builder(isdb, "faeda").importResultSet(rs);//isdb.copyTable("faeda", rs);
		rs.close();
		ps.close();
		
		sql = "select * from [ISDB].[dbo].[HEIMILD]";
		ps = con.prepareStatement( sql );
		rs = ps.executeQuery();
		new ImportUtil.Builder(isdb, "heimild").importResultSet(rs);//isdb.copyTable("heimild", rs);
		rs.close();
		ps.close();
		
		sql = "select * from [ISDB].[dbo].[MAELING]";
		ps = con.prepareStatement( sql );
		rs = ps.executeQuery();
		new ImportUtil.Builder(isdb, "maeling").importResultSet(rs);//isdb.copyTable("maeling", rs);
		rs.close();
		ps.close();
		
		/*String sql = "select * from [ISDB].[dbo].[table]";
		PreparedStatement	ps = con.prepareStatement( sql );
		ResultSet	rs = ps.executeQuery();
		new ImportUtil.Builder(isdb, "table").importResultSet(rs);
		//isdb.copyTable("efni", rs);
		rs.close();
		ps.close();*/
		
		isdb.close();
	}
	
	private void load() throws SQLException {
		con.prepareCall("delete from [ISDB].[dbo].[HEIMILD]").execute();
		con.prepareCall("delete from [ISDB].[dbo].[EFNI]").execute();
		con.prepareCall("delete from [ISDB].[dbo].[MAELING]").execute();
		con.prepareCall("delete from [ISDB].[dbo].[FAEDA]").execute();
		
		String sql = "select OriginalReferenceCode, Citation, Remarks from [Isgem2].[dbo].[Reference] order by OriginalReferenceCode";
		PreparedStatement	ps = con.prepareStatement( sql );
		ResultSet	rs = ps.executeQuery();
		
		PreparedStatement	ips = con.prepareStatement( "insert into [ISDB].[dbo].[HEIMILD] values (?,?,?)" );
		
		while( rs.next() ) {
			String origRefCode = rs.getString( 1 );
			String cita = rs.getString( 2 );
			String rem = rs.getString( 3 );
			
			ips.setString(1, origRefCode);
			ips.setString(2, cita == null ? cita : cita.substring(0, Math.min(30, cita.length())));
			ips.setString(3, rem == null ? rem : rem.substring(0, Math.min(200, rem.length())) );
			ips.execute();
		}
		
		ips.close();
		rs.close();
		ps.close();
		
		sql = "select OriginalComponentCode, OriginalComponentName, EnglishComponentName, Unit, GrunnefniIS, YflIS, UflIS, InndratturIS, FAgroupIS, EuroFIRComponentIdentifier, WebPublishReady from [Isgem2].[dbo].[Component] order by OriginalComponentCode";
		ps = con.prepareStatement( sql );
		rs = ps.executeQuery();
		
		ips = con.prepareStatement( "insert into [ISDB].[dbo].[EFNI] values (?,?,?,?,?,?,?,?,?,?,?)" );
		
		while( rs.next() ) {
			String origCompCode = rs.getString( 1 );
			String name = rs.getString( 2 );
			String en_name = rs.getString( 3 );
			String unit = rs.getString( 4 );
			String gr_efni = rs.getString( 5 );
			int yfl = rs.getInt( 6 );
			int ufl = rs.getInt( 7 );
			int inndr = rs.getInt( 8 );
			String fagrp = rs.getString( 9 );
			String eurof = rs.getString( 10 );
			String wread = rs.getString( 11 );
			
			eurof = eurof.replaceAll( "[\\[\\]]", "" );
			
			if( wread.equals("J") ) {				
				ips.setString(1, origCompCode);
				ips.setString(2, name);
				ips.setString(3, en_name);
				ips.setString(4, unit);
				ips.setString(5, gr_efni);
				ips.setInt(6, yfl);
				ips.setInt(7, ufl);
				ips.setInt(8, inndr);
				ips.setString(9, fagrp);
				ips.setString(10, eurof);
				ips.setString(11, wread);
				ips.execute();
			}
		}
		
		ips.close();
		rs.close();
		ps.close();
		
		//where OriginalFoodCode = '0571' and OriginalComponentCode = '0015'
		sql = "select OriginalFoodCode, OriginalComponentCode, DateOfGeneration, SelectedValue, StandardDeviation, Minimum, Maximum, N, NoofPrimarySampleUnits, OriginalReferenceCode, Remarks, QI_Eurofir, null, null, DateOfAnalysisDisp from [Isgem2].[dbo].[ComponentValue] order by OriginalFoodCode, OriginalComponentCode";
		ps = con.prepareStatement( sql );
		rs = ps.executeQuery();
		
		ips = con.prepareStatement( "insert into [ISDB].[dbo].[MAELING] values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)" );
		
		int cnt = 0;
		String last = "";
		float[]	fcur = null;
		while( rs.next() ) {
			String origFoodCode = rs.getString( 1 );
			String origCompCode = rs.getString( 2 );
			Date dat = rs.getDate( 3 );
			String sel = rs.getString( 4 );
			String std = rs.getString( 5 );
			String minnst = rs.getString( 6 );
			String mest = rs.getString( 7 );
			String maeling = rs.getString( 8 );
			String syni = rs.getString( 9 );
			String heim = rs.getString( 10 );
			String ath = rs.getString( 11 );
			String gaedak = rs.getString( 12 );
			String maeliadf = rs.getString( 13 );
			String synat = rs.getString( 14 );
			String maeliar = rs.getString( 15 );
			
			if( !origFoodCode.equals(last) ) {
				fcur = new float[200];
				enMap.put(origFoodCode, fcur);
			}
		
			String val = sel;
			if( val == null || val.equals("em") || val.equals("sn") || val.equals("eyða") || val.contains("<") ) val = "0.0";
			if( val.endsWith( ",") ) val = sel.substring(0, sel.length()-1);
			val = val.replace(',', '.');
			int i = Integer.parseInt( origCompCode );
			float f = 0.0f;
			try {
				Float.parseFloat( val );
			} catch( Exception e ) {}
			fcur[i] = f;
		
			last = origFoodCode;
			
			/*ips.setString(1, "");
			ips.setString(2, "");
			ips.setString(3, "");
			ips.setString(4, "");
			ips.setString(5, "");
			ips.setString(6, "");
			ips.setString(7, "");
			ips.setString(8, "");*/
			//ips.setString(9, "");
			//ips.setString(10, "");
			//ips.setString(11, "");
			/*ips.setString(12, "");
			ips.setString(13, "");
			ips.setString(14, "");
			ips.setString(15, "");*/
			
			
			/*ips.setString(2, "");
			ips.setDate(3, dat);
			ips.setString(4, "");
			ips.setString(5, "");
			ips.setString(6, "");
			ips.setString(7, "");
			ips.setString(8, "");
			ips.setString(9, "");
			ips.setString(10, "");
			ips.setString(11, "");//ath == null ? ath : ath.substring(0, Math.min(50, ath.length())) );
			ips.setString(12, "");
			ips.setString(13, "");
			ips.setString(14, "");
			ips.setString(15, "");//maeliar == null ? maeliar : maeliar.substring(0, Math.min(4, maeliar.length())) );*/
			
			System.err.println("         number: "+cnt);
			System.err.println(origFoodCode);
			System.err.println(origCompCode);
			System.err.println(dat);
			System.err.println(sel);
			System.err.println(std);
			System.err.println(minnst);
			System.err.println(mest);
			System.err.println(maeling);
			System.err.println(syni);
			System.err.println(heim);
			System.err.println( ath == null ? ath : ath.substring(0, Math.min(50, ath.length())) );
			System.err.println( gaedak);
			System.err.println( maeliadf);
			System.err.println( synat);
			System.err.println( maeliar == null ? maeliar : maeliar.substring(0, Math.min(4, maeliar.length())) );
			
			ips.setString(1, origFoodCode);
			ips.setString(2, origCompCode);
			ips.setDate(3, dat);
			ips.setString(4, sel);
			ips.setString(5, std);
			ips.setString(6, minnst);
			ips.setString(7, mest);
			ips.setString(8, "" ); //maeling );// == null ? maeling : maeling.substring(0, Math.min(50, maeling.length())));
			ips.setString(9, syni);
			ips.setString(10, heim);
			ips.setString(11, ath == null ? ath : ath.substring(0, Math.min(50, ath.length())) );
			ips.setString(12, gaedak);
			ips.setString(13, maeliadf);
			ips.setString(14, synat);
			ips.setString(15, maeliar == null ? maeliar : maeliar.substring(0, Math.min(4, maeliar.length())) );
			ips.execute();
			
			cnt++;
		}
		
		ips.close();
		rs.close();
		ps.close();
		
		sql = "select OriginalFoodCode, DateOfGeneration, GeneratedBy, null, EnglishFoodName, OriginalFoodName, ScientificFoodName, Remarks, WastePortion, FoodGroupIS1, FoodGroupIS2, FoodGroupIS3, NitrogenProteinFactor, FattyAcidFactor, WebPublishReady, LangualCodes from [Isgem2].[dbo].[Food] order by OriginalFoodCode";
		ps = con.prepareStatement( sql );
		rs = ps.executeQuery();
		
		ips = con.prepareStatement( "insert into [ISDB].[dbo].[FAEDA] values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)" );
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream pst = new PrintStream( baos );
		while( rs.next() ) {
			String origFoodCode = rs.getString( 1 );
			String dat = rs.getString( 2 );
			String gen = rs.getString( 3 );
			String skra = rs.getString( 4 );
			String eng = rs.getString( 5 );
			String name = rs.getString( 6 );
			String sci = rs.getString( 7 );
			String rem = rs.getString( 8 );
			String was = rs.getString( 9 );
			String fg1 = rs.getString( 10 );
			String fg2 = rs.getString( 11 );
			String fg3 = rs.getString( 12 );
			String npf = rs.getString( 13 );
			String faf = rs.getString( 14 );
			String web = rs.getString( 15 );
			String lang = rs.getString( 16 );
			
			if( web.equals("J") ) {
				fcur = enMap.get(origFoodCode);
				
				if( fcur != null ) {
					float xKJ = 17 * fcur[1] + 37 * fcur[2] + 17 * fcur[10] + 8 * fcur[13] + 29 * fcur[14];
		            float xkkal = 4 * fcur[1] + 9 * fcur[2] + 4 * fcur[10] + 2 * fcur[13] + 7 * fcur[14];
		            float xMainIngr = fcur[1] + fcur[2] + fcur[10] + fcur[13] + fcur[14] + fcur[15] + fcur[16];
		
		            float xProtein, xFat, xCarb, xFiber, xAlcohol;
		            if( xKJ > 0 ) {
		                xProtein = 17 * fcur[1] * 100.0f / xKJ;
		                xFat = 37 * fcur[2] * 100.0f / xKJ;
		                xCarb = 17 * fcur[10] * 100.0f / xKJ;
		                xFiber = 8 * fcur[13] * 100.0f / xKJ;
		                xAlcohol = 29 * fcur[14] * 100.0f / xKJ;
		            } else {
		                xProtein = 0.0f;
		                xFat = 0.0f;
		                xCarb = 0.0f;
		                xFiber = 0.0f;
		                xAlcohol = 0.0f;
		            }
		
		            float xFa0tot = fcur[2];
		            float xFa1sat = fcur[47] + fcur[48] + fcur[49] + fcur[50] + fcur[51] + fcur[54] + fcur[55] + fcur[56] + fcur[59] + fcur[60] + fcur[61] + fcur[62] + fcur[73] + fcur[81] + fcur[88] + fcur[90] + fcur[91] + fcur[93] + fcur[105] + fcur[106] + fcur[110] + fcur[123];
		            float xFa2cmu = fcur[58] + fcur[64] + fcur[65] + fcur[66] + fcur[74] + fcur[83] + fcur[89] + fcur[99] + fcur[107] + fcur[108] + fcur[109] + fcur[111] + fcur[112] + fcur[113] + fcur[114] + fcur[119] + fcur[120] + fcur[124] + fcur[125] + fcur[126] + fcur[130] + fcur[131] + fcur[132] + fcur[133] + fcur[135];
		            float xFa3cpu = fcur[68] + fcur[72] + fcur[76] + fcur[77] + fcur[78] + fcur[79] + fcur[80] + fcur[84] + fcur[85] + fcur[86] + fcur[87] + fcur[97] + fcur[98] + fcur[115] + fcur[116] + fcur[117] + fcur[118] + fcur[121] + fcur[128] + fcur[129] + fcur[134] + fcur[136];
		            float xFa4cpun6 = fcur[68] + fcur[76] + fcur[77] + fcur[79] + fcur[84] + fcur[86] + fcur[121] + fcur[134];
		            float xFa5cpun3 = fcur[72] + fcur[78] + fcur[80] + fcur[85] + fcur[87] + fcur[97] + fcur[98] + fcur[116] + fcur[118] + fcur[129];
		            float xFa6cpun3 = fcur[78] + fcur[80] + fcur[85] + fcur[87] + fcur[98] + fcur[129];
		            float xFa7tmu = fcur[52] + fcur[57] + fcur[63] + fcur[67] + fcur[71] + fcur[75] + fcur[82] + fcur[95] + fcur[122] + fcur[127];
					
					ips.setString(1, origFoodCode);
					ips.setString(2, dat);
					ips.setString(3, gen);
					ips.setString(4, skra);
					ips.setString(5, eng == null ? eng : eng.substring(0, Math.min(35, eng.length())));
					ips.setString(6, name == null ? name : name.substring(0, Math.min(35, name.length())));
					ips.setString(7, sci);
					ips.setString(8, rem == null ? rem : rem.substring(0, Math.min(35, rem.length())) );
					ips.setString(9, was);
					ips.setString(10, fg1);
					ips.setString(11, fg2);
					ips.setString(12, fg3);
					
					//ips.setString(13, npf);
					float f_npf = 0.0f;
					float f_faf = 0.0f;
					try {
						f_npf = Float.parseFloat( npf );
						f_faf = Float.parseFloat( faf );
					} catch( Exception e ) {
						
					}
					ips.setFloat(13, f_npf);
					//ips.setString(14, faf);
					ips.setFloat(14, f_faf);
					
					//pst.printf("%.1f", xProtein);
					//ips.setString(15, baos.toString());
					ips.setFloat(15, xProtein);
					
					//baos.reset();
					//pst.printf("%.1f", xFat);
					//ips.setString(16, baos.toString());
					ips.setFloat(16, xFat);
					
					//baos.reset();
					//pst.printf("%.1f", xCarb);
					//ips.setString(17, baos.toString());
					ips.setFloat(17, xCarb);
					
					//baos.reset();
					/*pst.printf("%.1f", xFiber);
					ips.setString(18, baos.toString());
					baos.reset();*/
					//pst.printf("%.1f", xAlcohol);
					//ips.setString(18, baos.toString());
					ips.setFloat(18, xAlcohol);
					
					//baos.reset();
					//pst.printf("%.1f", xKJ);
					//ips.setString(19, baos.toString());
					ips.setFloat(19, xKJ);
					
					//baos.reset();
					//pst.printf("%.1f", xkkal);
					//ips.setString(20, baos.toString());
					ips.setFloat(20, xkkal);
					
					baos.reset();
					pst.printf("%.1f", xFa0tot);
					ips.setString(21, baos.toString());
					baos.reset();
					pst.printf("%.1f", xFa1sat);
					ips.setString(22, baos.toString());
					baos.reset();
					pst.printf("%.1f", xFa2cmu);
					ips.setString(23, baos.toString());
					baos.reset();
					pst.printf("%.1f", xFa3cpu);
					ips.setString(24, baos.toString());
					baos.reset();
					pst.printf("%.1f", xFa4cpun6);
					ips.setString(25, baos.toString());
					baos.reset();
					pst.printf("%.1f", xFa5cpun3);
					ips.setString(26, baos.toString());
					baos.reset();
					pst.printf("%.1f", xFa6cpun3);
					ips.setString(27, baos.toString());
					baos.reset();
					pst.printf("%.1f", xFa7tmu);
					ips.setString(28, baos.toString());
					baos.reset();
					
					ips.setString(29, web);
					ips.setString(30, lang);
					ips.execute();
				} else {
					System.err.println( name + "   " + origFoodCode );
				}
			}
		}
		
		ips.close();
		rs.close();
		ps.close();
	}
}
