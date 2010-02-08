package com.matis.prokaria;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class FishWorker {
	int[]			matrix;
	int[] 			mmatrix;
	int[] 			fmatrix;
	List<Tuple> 	tupleList;
	List<String>				parameterNames = new ArrayList<String>();
	List<Class>					parameterTypes = new ArrayList<Class>();
	
	List<String> 	markers = new ArrayList<String>();
	List<Fish>		fishes = new ArrayList<Fish>();
	List<Fish> 		malefish = new ArrayList<Fish>();
	List<Fish> 		femalefish = new ArrayList<Fish>();
	List<Float>		ffactor = new ArrayList<Float>();
	List<Float>		mfactor = new ArrayList<Float>();
	
	int							curInd = 1;
	
	List<Map<Integer,Integer>>	freq = new ArrayList<Map<Integer,Integer>>();
	int							freqcount = 0;
	
	public static int delta(int i, int j) {
		return (i == j) ? 1 : 0;
	}
	
	public class Tuple implements Comparable<Tuple> {
		Fish 	male;
		Fish 	female;
		int 	rank;
		double	khrank;
		double	lrm;

		public Tuple(Fish f1, Fish f2, int r, double khr, double lrmval) {
			male = f2;
			female = f1;
			rank = r;
			khrank = khr;
			lrm = lrmval;
		}
		
		public double current() {
			if( curInd == 0 ) return khrank;
			else return lrm;
		}

		@Override
		public int compareTo(Tuple o) {
			// TODO Auto-generated method stub
			return 0;
		}
	};
	
	public class Fish {
		String 		name;
		Object[]	params;
		float		factor;
		boolean		male;
		
		public Fish( String name, Object[] params, float factor, boolean male ) {
			this.name = name;
			this.params = params;
			this.factor = factor;
			this.male = male;
		}
		
		public boolean equals( Fish f2 ) {
			return name.equals( f2.name );
		}
		
		public boolean equals( String f2 ) {
			return name.equals( f2 );
		}
		
		public String toString() {
			return name;
		}
	};

	public double calcAsym() {
		int mr = malefish.size();
		int fr = femalefish.size();
		int cl = markers.size();
		
		//SysAlleleFreq freq = pop.getFreq();
		for (int i = 0; i < fr; i++) {
			//Fish n1 = femalefish.get(i);
			//float  ff = ffactor.get(i);
			for (int k = 0; k < mr; k++) {
				//Fish n2 = malefish.get(k);
				//float  mf = mfactor.get(k);
				double sum = 0;
				double sumW = 0;
				for (int u = 0; u < cl; u+=2) {
					int a = fmatrix[i * cl + u];
					int b = fmatrix[i * cl + u + 1];
					int c = mmatrix[k * cl + u];
					int d = mmatrix[k * cl + u + 1];
					if (a == -1 || b == -1 || c == -1 || d == -1)
					  continue; //ignore
			
					// PRECOND: (x != -1 || x2 != -1) && (y != -1 || y2 != -1)
					
					Map<Integer,Integer>	fmap = freq.get(u);
					
					double pa = fmap.containsKey(a) ? fmap.get(a)/freqcount : 0.0;
					double pb = fmap.containsKey(b) ? fmap.get(b)/freqcount : 0.0;
					
					double top = pa * (delta(b, c) + delta(b, d)) + pb * (delta(a, c) + delta(a, d)) - 4. * pa * pb;
					double bot = 2. * pa * pb;
					double w = (1. + delta(a, b)) * (pa + pb) - 4. * pa * pb;
					if ((float)bot == 0f)
						return 1.0/Float.MAX_VALUE;
					sum += top / bot;
					sumW += w / bot;
			    }
			    if ((float)sumW == 0f)
			      return 1.0/Float.MAX_VALUE;
			    
			    double val = sum / sumW;
			}
		}
	    return 0.0;
	}
	
	public void parseData(String data, int start) {
		String[] split = data.split("\\n");
		String[] vals = split[0].split("\\t");
		
		boolean div = vals[0].contains("/");
		
		int r = (split.length - 1);
		int c = (vals.length - start);
		if( div ) c *= 2;

		markers.clear();
		for (int i = 0; i < c; i++) {
			markers.add(vals[i + start]);
		}

		malefish.clear();
		femalefish.clear();
		mfactor.clear();
		ffactor.clear();

		String startstr = null;
		for (int i = 0; i < r; i++) {
			vals = split[i + 1].split("\\t");

			String val = vals[0];
			if( startstr == null ) startstr = val;
			if( val.equalsIgnoreCase("female") || val.equals(startstr) ) {
				Fish f = new Fish( vals[1], new Object[0], 0.0f, false );
				femalefish.add( f );
				ffactor.add( Float.parseFloat( vals[2] ) );
			} else {
				Fish f = new Fish( vals[1], new Object[0], 0.0f, true );
				malefish.add( f );
				mfactor.add( Float.parseFloat( vals[2] ) );
			}
		}

		fmatrix = new int[femalefish.size() * c];
		mmatrix = new int[malefish.size() * c];

		int f = 0;
		int m = 0;
		
		for( int i = 0; i < c; i++ ) {
			freq.add( new HashMap<Integer,Integer>() );
		}

		for (int i = 0; i < r; i++) {
			vals = split[i + 1].split("\\t");

			String val = vals[0];
			if( val.equalsIgnoreCase("female") || val.equals(startstr) ) {
				for (int k = 0; k < c; k++) {
					Map<Integer,Integer> fmap = freq.get(k);
					int ival = Integer.parseInt(vals[k + start]);
					int value = 1;
					if( fmap.containsKey(ival) ) value = fmap.get(ival);
						
					fmap.put(ival, value);
					freqcount++;
					fmatrix[f * c + k] = ival;
				}
				f++;
			} else {
				for (int k = 0; k < c; k++) {
					Map<Integer,Integer> fmap = freq.get(k);
					int ival = Integer.parseInt(vals[k + start]);
					int value = 1;
					if( fmap.containsKey(ival) ) value = fmap.get(ival);
						
					fmap.put(ival, value);
					freqcount++;
					mmatrix[m * c + k] = ival;
				}
				m++;
			}
		}
	}
	
	public void kinWrite( File f ) throws IOException {
		FileWriter	fw = new FileWriter( f );
		fw.write( "id" );
		fw.write( "\tgid" );
		
		int i = 0;
		for( String marker : markers ) {
			if( i % 2 == 0 ) fw.write( "\t"+marker );
			else fw.write( "/"+marker );
			
			i++;
		}
		
		int msize = mmatrix.length/malefish.size();
		int count = 0;
		for( Fish male : malefish ) {
			fw.write( "\n"+male );
			fw.write( "\tmale" );
			
			for( i = count; i < count+msize; i++ ) {
				if( i%2 == 0 ) fw.write( "\t"+mmatrix[i] );
				else fw.write( "/"+mmatrix[i] );
			}
			
			count+=msize;
		}
		
		int fsize = fmatrix.length/femalefish.size();
		count = 0;
		for( Fish female : femalefish ) {
			fw.write( "\n"+female );
			fw.write( "\tfemale" );
			
			for( i = count; i < count+fsize; i++ ) {
				if( i%2 == 0 ) fw.write( "\t"+fmatrix[i] );
				else fw.write( "/"+fmatrix[i] );
			}
			
			count+=fsize;
		}
		
		fw.close();
	}
	
	public int findFish( String name ) {
		return findFish( fishes, name );
	}
	
	public int findFish( List<Fish> list, String name ) {
		int i = 0;
		for( Fish f : list ) {
			if( f.name.equals(name) ) return i;	
			i++;
		}
		return -1;
	}

	public void plainStuff( XSSFWorkbook wb ) throws IOException {
		XSSFSheet		ws = wb.getSheetAt(0);
		
		int i = 0;
		XSSFRow			wr = ws.getRow( i++ );
		
		int start = 0;
		XSSFCell wc = wr.getCell(start);
		while( wc != null && wc.getStringCellValue() != null && wc.getStringCellValue().length() > 0 ) {
			start++;
			wc = wr.getCell(start);
		}
		
		int k = start+1;
		wc = wr.getCell( k );
		while( wc != null && wc.getStringCellValue() != null && wc.getStringCellValue().length() > 0 ) {
			markers.add( wc.getStringCellValue() );
			wc = wr.getCell( ++k );
		}
		
		wr = ws.getRow( i++ );
		while( wr != null ) {
			boolean male = wr.getCell(1).getStringCellValue().equals("male");
			
			List<Object>	oList = new ArrayList<Object>();
			int u = 2;
			XSSFCell cell = wr.getCell(u++);
			while( cell != null && (cell.getCellType() != XSSFCell.CELL_TYPE_STRING || cell.getStringCellValue().length() > 0) ) {
				int celltype = cell.getCellType();
				if( celltype == XSSFCell.CELL_TYPE_STRING ) {
					oList.add( cell.getStringCellValue() );
				} else if( celltype == XSSFCell.CELL_TYPE_NUMERIC ) {
					oList.add( cell.getNumericCellValue() );
				} else if( celltype == XSSFCell.CELL_TYPE_FORMULA ) {
					
				}
				cell = wr.getCell(u++);
			}
			//(float)wr.getCell(2).getNumericCellValue();
			//(int)wr.getCell(3).getNumericCellValue();
			
			Fish f = new Fish( wr.getCell(0).getStringCellValue(), oList.toArray( new Object[0] ), 0.0f, male );
			if( male ) {
				malefish.add( f );
				mfactor.add( 0.0f );
			}
			else {
				femalefish.add( f );
				ffactor.add( 0.0f );
			}
			wr = ws.getRow( i++ );
		}
		
		fmatrix = new int[femalefish.size() * markers.size()];
		mmatrix = new int[malefish.size() * markers.size()];
		
		int f = 0;
		int m = 0;
		
		i = 1;
		wr = ws.getRow( i++ );
		while( wr != null ) {
			boolean male = wr.getCell(1).getStringCellValue().equals("male");
			if( male ) {
				k = 0;
				wc = wr.getCell(start+k+1);
				while( k < markers.size() ) {
					mmatrix[m * markers.size() + k] = (int)wc.getNumericCellValue();
					k++;
					wc = wr.getCell(start+k+1);
				}
				m++;
			} else {
				k = 0;
				wc = wr.getCell(start+k+1);
				while( k < markers.size() ) {
					fmatrix[f * markers.size() + k] = (int)wc.getNumericCellValue();
					k++;
					wc = wr.getCell(start+k+1);
				}
				f++;
			}
			wr = ws.getRow( i++ );
		}
		
		initFreqs();
	}
	
	public void initFreqs() {
		freq.clear();		
		
		int rm = mmatrix.length/markers.size();
		int rf = fmatrix.length/markers.size();
		
		freqcount = (rm + rf)*2;
		
		for( int i = 0; i < markers.size()/2; i++ ) {
			Map<Integer,Integer> fmap = new HashMap<Integer,Integer>();
			freq.add( fmap );
			
			for( int r = 0; r < rm; r++ ) {
				int v1 = mmatrix[ r*markers.size()+i*2 ];
				int v2 = mmatrix[ r*markers.size()+i*2+1 ];
				
				int val = 0;
				if( fmap.containsKey(v1) ) val = fmap.get(v1);	
				fmap.put(v1, val+1);
				
				val = 0;
				if( fmap.containsKey(v2) ) val = fmap.get(v2);	
				fmap.put(v2, val+1);
			}
			
			for( int r = 0; r < rf; r++ ) {
				int v1 = fmatrix[ r*markers.size()+i*2 ];
				int v2 = fmatrix[ r*markers.size()+i*2+1 ];
				
				int val = 0;
				if( fmap.containsKey(v1) ) val = fmap.get(v1);	
				fmap.put(v1, val+1);
				
				val = 0;
				if( fmap.containsKey(v2) ) val = fmap.get(v2);	
				fmap.put(v2, val+1);
			}
		}
	}
	
	public List<Tuple> calcData() {
		int mr = malefish.size();
		int fr = femalefish.size();
		int cl = markers.size();

		List<Tuple> tupleList = new ArrayList<Tuple>();

		double h = 0.0;
		for( int i = 0; i < cl; i+=2 ) {
			double sum = 0.0;
			for( int m = 0; m < mr; m++ ) {
				int a = mmatrix[m * cl + i];
				int b = mmatrix[m * cl + i + 1];
				
				sum += (a == b) ? 0 : 1;
			}
			
			for( int f = 0; f < fr; f++ ) {
				int a = fmatrix[f * cl + i];
				int b = fmatrix[f * cl + i + 1];
				
				sum += (a == b) ? 0 : 1;
			}
			sum /= (fr + mr);
			
			h += sum;
		}
		h /= (cl/2);
		
		for (int i = 0; i < fr; i++) {
			Fish n1 = femalefish.get(i);
			float  ff = ffactor.get(i);
			//n1.factor = ff;
			for (int k = 0; k < mr; k++) {
				Fish n2 = malefish.get(k);
				float  mf = mfactor.get(k);
				//n2.factor = mf;
				int rank = 0;
				double sum = 0;
				
				double sum1 = 0.0;
				double sumW1 = 0.0;
				double sum2 = 0.0;
				double sumW2 = 0.0;
				//double val = 0.0;
				double lrm = -1.0;
				for (int u = 0; u < cl; u+=2) {
					int a = fmatrix[i * cl + u];
					int b = fmatrix[i * cl + u + 1];
					int c = mmatrix[k * cl + u];
					int d = mmatrix[k * cl + u + 1];
					
					rank += Math.abs(a - c);
					rank += Math.abs(b - d);
					
					int L = u/2;
					Map<Integer,Integer>	fmap = freq.get( L );
					double pa = fmap.containsKey(a) ? (double)fmap.get(a)/(double)freqcount : 0.0;
					double pb = fmap.containsKey(b) ? (double)fmap.get(b)/(double)freqcount : 0.0;
					double pc = fmap.containsKey(c) ? (double)fmap.get(c)/(double)freqcount : 0.0;
					double pd = fmap.containsKey(d) ? (double)fmap.get(d)/(double)freqcount : 0.0;
					
					double top1 = pa * (delta(b, c) + delta(b, d)) + pb * (delta(a, c) + delta(a, d)) - 4. * pa * pb;
					double bot1 = 2. * pa * pb;
					double w1 = (1. + delta(a, b)) * (pa + pb) - 4. * pa * pb;
					if ((float)bot1 == 0f) {
						lrm = 1.0/Float.MAX_VALUE;
						//break;
					}
					sum1 += top1 / bot1;
					sumW1 += w1 / bot1;
					
					double top2 = pc * (delta(d, a) + delta(d, b)) + pd * (delta(c, a) + delta(c, b)) - 4. * pc * pd;
					double bot2 = 2. * pc * pd;
					double w2 = (1. + delta(c, d)) * (pc + pd) - 4. * pc * pd;
					if ((float)bot2 == 0f) {
						lrm = 1.0/Float.MAX_VALUE;
						//break;
					}
					sum2 += top2 / bot2;
					sumW2 += w2 / bot2;
					
					double x2 = (a == b ? 4: 2); // sum_j x_j^2
				    double y2 = (c == d ? 4: 2); // sum_j y_j^2
				    double xy = (a == c ? 1: 0)
				        + (a == d ? 1: 0)
				        + (b == c ? 1: 0)
				        + (b == d ? 1: 0);
				    double dist = x2 - 2. * xy + y2;
				    sum += dist;
				}
				
				if( lrm == -1.0 ) {
					double s1 = sum1/sumW1;
					double s2 = sum2/sumW2;
					
					if( s1 == Double.MAX_VALUE || s2 == Double.MAX_VALUE ) lrm = Double.MAX_VALUE;
					else {
						lrm = (s1 + s2)/2.0;
					}
				}
				
				double dij = sum / (2. * cl);
				double val = 1.0 - dij/h;
				//System.err.println( i + "  " + k + "   " + dij + "  " + val );
				tupleList.add( new Tuple(n1, n2, rank, val, lrm) );
			}
		}
		//unsortedTupleList = tupleList;

		//tupleList = new ArrayList<Tuple>( tupleList );
		//Collections.sort(tupleList);

		return tupleList;
	}
	
	public void writeWorkbook( XSSFWorkbook wb ) {
		XSSFSheet sheet = wb.createSheet("Fish");
		XSSFRow row = sheet.createRow(0);
		
		XSSFFont	font = wb.createFont();
		font.setBold( true );
		
		CellStyle boldstyle = wb.createCellStyle();
	    boldstyle.setFont( font );
	    
		XSSFCell cell = row.createCell(0);
		cell.setCellValue( "Name" );
		cell.setCellStyle( boldstyle );
		cell = row.createCell(1);
		cell.setCellValue( "Gender" );
		cell.setCellStyle( boldstyle );
		cell = row.createCell(2);
		cell.setCellValue( "Weight" );
		cell.setCellStyle( boldstyle );
		cell = row.createCell(3);
		cell.setCellValue( "Room" );
		cell.setCellStyle( boldstyle );
		
		int i = 4;
		for( String marker : markers ) {
			cell = row.createCell(++i);
			cell.setCellValue( marker );
			cell.setCellStyle( boldstyle );
		}
		
		int r = 0;
		for( Fish male : malefish ) {
			int start = r*markers.size();
			
			row = sheet.createRow(++r);
			cell = row.createCell(0);
			cell.setCellValue( male.name );
			cell = row.createCell(1);
			cell.setCellValue( "male" );
			
			int c = 2;
			while( c < male.params.length+2 ) {
				cell = row.createCell(c);
				if( parameterTypes.get(c-2) == String.class ) cell.setCellValue( (String)male.params[c-2] );
				else if( parameterTypes.get(c-2) == Double.class ) {
					Object obj = male.params[c-2];
					if( !(obj instanceof Double) ) cell.setCellValue( (String)obj );
					else cell.setCellValue( (Double)obj );
				}
				c++;
			}
			
			for( i = 0; i < markers.size(); i++ ) {
				cell = row.createCell(i+3+parameterNames.size());
				cell.setCellValue( mmatrix[i+start] );
			}
		}
		int femr = 0;
		for( Fish female : femalefish ) {
			int start = (femr++)*markers.size();
			
			row = sheet.createRow(++r);
			cell = row.createCell(0);
			cell.setCellValue( female.name );
			cell = row.createCell(1);
			cell.setCellValue( "female" );
			int c = 2;
			while( c < female.params.length+2 ) {
				cell = row.createCell(c);
				if( parameterTypes.get(c-2) == String.class ) cell.setCellValue( (String)female.params[c-2] );
				else if( parameterTypes.get(c-2) == Double.class ) {
					Object obj = female.params[c-2];
					if( !(obj instanceof Double) ) cell.setCellValue( (String)obj );
					else cell.setCellValue( (Double)obj );
				}
				c++;
			}
			
			for( i = 0; i < markers.size(); i++ ) {
				cell = row.createCell(i+3+parameterNames.size());
				cell.setCellValue( fmatrix[i+start] );
			}
		}
		
		sheet = wb.createSheet("Male Genotypes");
		row = sheet.createRow(0);
		i = 0;
		for( String marker : markers ) {
			cell = row.createCell(++i);
			cell.setCellValue( marker );
			//cell.setCellStyle( CellStyle.)
		}
		
		r = 0;
		for( Fish male : malefish ) {
			int start = r*markers.size();
			row = sheet.createRow(++r);
			cell = row.createCell(0);
			cell.setCellValue( male.toString() );
			for( i = 0; i < markers.size(); i++ ) {
				cell = row.createCell(i+1);
				cell.setCellValue( mmatrix[i+start] );
			}
		}
		
		sheet = wb.createSheet("Female Genotypes");
		row = sheet.createRow(0);
		i = 0;
		for( String marker : markers ) {
			cell = row.createCell(++i);
			cell.setCellValue( marker );
			//cell.setCellStyle( CellStyle.)
		}
		
		r = 0;
		for( Fish female : femalefish ) {
			int start = r*markers.size();
			row = sheet.createRow(++r);
			cell = row.createCell(0);
			cell.setCellValue( female.toString() );
			for( i = 0; i < markers.size(); i++ ) {
				cell = row.createCell(i+1);
				cell.setCellValue( fmatrix[i+start] );
			}
		}
		
		CellStyle greenstyle = wb.createCellStyle();
	    greenstyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
	    greenstyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
	    
	    CellStyle yellowstyle = wb.createCellStyle();
	    yellowstyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
	    yellowstyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
	    
	    CellStyle redstyle = wb.createCellStyle();
	    redstyle.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
	    redstyle.setFillPattern(CellStyle.SOLID_FOREGROUND);

		
		sheet = wb.createSheet("Pairwise relatedness");
		row = sheet.createRow(0);
		cell = row.createCell(0);
		cell.setCellValue("Male");
		cell = row.createCell(1);
		cell.setCellValue("Female");
		cell = row.createCell(2);
		cell.setCellValue("LRM(1999)");
		cell = row.createCell(3);
		cell.setCellValue("Konolov&Heg(2008)");
		//cell = row.createCell(4);
		//cell.setCellValue("Simmi");
		cell = row.createCell(5);
		cell.setCellValue("Male Performance factor");
		cell = row.createCell(6);
		cell.setCellValue("Female Performance factor");
		
		i = 0;
		for( Tuple t : tupleList ) {
			row = sheet.createRow(++i);
			cell = row.createCell(0);
			cell.setCellValue(t.male.name);
			cell = row.createCell(1);
			cell.setCellValue(t.female.name);
			cell = row.createCell(2);
			cell.setCellValue(t.lrm);
			if( t.lrm < 0.03 ) {
				cell.setCellStyle( greenstyle );
			} else if( t.lrm < 0.06 ) {
				cell.setCellStyle( yellowstyle );
			} else {
				cell.setCellStyle( redstyle );
			}
			cell = row.createCell(3);
			cell.setCellValue(t.khrank);
			/*if( t.khrank < 0.03 ) {
				cell.setCellStyle( redstyle );
			} else if( t.khrank < 0.06 ) {
				cell.setCellStyle( yellowstyle );
			} else {
				cell.setCellStyle( greenstyle );
			}*/
			//cell = row.createCell(4);
			//cell.setCellValue(t.rank);
			cell = row.createCell(5);
			cell.setCellValue(t.male.factor);
			cell = row.createCell(6);
			cell.setCellValue(t.female.factor);
		}
		
		sheet = wb.createSheet("Male relatedness matrix");
		row = sheet.createRow(0);
		i = 0;
		for( Fish male : malefish ) {
			cell = row.createCell(++i);
			cell.setCellValue(male.name);
		}
		i = 0;
		int cl = 0;
		for( Tuple t : tupleList ) {
			if( cl % malefish.size() == 0 ) {
				row = sheet.createRow(++i);
				cl = 0;
				cell = row.createCell(cl);
				cell.setCellValue(t.female.name);
			}
			cell = row.createCell(++cl);
			cell.setCellValue(t.lrm);
			if( t.lrm < 0.03 ) {
				cell.setCellStyle( greenstyle );
			} else if( t.lrm < 0.06 ) {
				cell.setCellStyle( yellowstyle );
			} else {
				cell.setCellStyle( redstyle );
			}
		}
		
		sheet = wb.createSheet("Female relatedness matrix");
		row = sheet.createRow(0);
		i = 0;
		for( Fish female : femalefish ) {
			cell = row.createCell(++i);
			cell.setCellValue(female.name);
		}
		i = 0;
		cl = 0;
		for( int k = 0; k < tupleList.size(); k++ ) {
			int rr = k/femalefish.size();
			int cc = k%femalefish.size();
			Tuple t = tupleList.get( cc*malefish.size() + rr );
			if( cl % femalefish.size() == 0 ) {
				row = sheet.createRow(++i);
				cl = 0;
				cell = row.createCell(cl);
				cell.setCellValue(t.male.name);
			}
			cell = row.createCell(++cl);
			cell.setCellValue(t.khrank);
			if( t.khrank < 0.03 ) {
				cell.setCellStyle( redstyle );
			} else if( t.khrank < 0.06 ) {
				cell.setCellStyle( yellowstyle );
			} else {
				cell.setCellStyle( greenstyle );
			}
		}
	}

	public void wbStuffNoSex( XSSFWorkbook wb ) throws IOException {
		XSSFSheet		ws = wb.getSheetAt(0);
		
		fishes.clear();
		markers.clear();
		
		int i = 0;
		XSSFRow			wr = ws.getRow( i++ );
		
		int k = 2;
		XSSFCell wc = wr.getCell(k++);
		while( wc != null ) {
			markers.add( wc.getStringCellValue() );
			wc = wr.getCell(k++);
		}
		if( markers.size() % 2 == 1 ) markers.remove( markers.size()-1 );
		
		wr = ws.getRow( i++ );
		while( wr != null && wr.getCell(0) != null ) {
			Fish f = new Fish( wr.getCell(0).getStringCellValue(), new Object[0], 0.0f, false );
			fishes.add( f );
			
			wr = ws.getRow( i++ );
		}
		matrix = new int[fishes.size() * markers.size()];
		i = 1;
		wr = ws.getRow( i++ );
		while( wr != null ) {
			k = 2;
			wc = wr.getCell(k++);
			while( wc != null && k < markers.size()+2 ) {
				int val = 0;
				if( wc.getCellType() != XSSFCell.CELL_TYPE_NUMERIC ) val = wc.getStringCellValue().hashCode();
				else val = (int)wc.getNumericCellValue();
					
				matrix[(i - 2) * markers.size() + (k -2)] = val;
				wc = wr.getCell(k++);
			}
			
			wr = ws.getRow( i++ );
		}
	}
	
	public void initGenotypes() {
		fmatrix = new int[femalefish.size() * markers.size()];
		mmatrix = new int[malefish.size() * markers.size()];
		
		Fish tmpf = new Fish( "", new Object[0], 0.0f, false );
		int i = 0;
		while( i < fishes.size() ) {
			Fish fish = fishes.get(i);
			String name = fish.name;
			tmpf.name = name;
			
			int ind =  findFish( malefish, name ); //malefish.indexOf( tmpf );
			if( ind != -1 ) {
				for( int k = 0; k < markers.size(); k++ ) {
					mmatrix[ind * markers.size() + k] = matrix[i * markers.size() + k];
				}
			} else {
				ind = findFish( femalefish, name );
				if( ind != -1 ) {
					for( int k = 0; k < markers.size(); k++ ) {
						fmatrix[ind * markers.size() + k] = matrix[i * markers.size() + k];
					}
				}
			}
			i++;
		}
	}
	
	public void retainFish( List<Fish> list, Set<String> fishnames ) {
		Set<Fish>	remfish = new HashSet<Fish>();
		for( Fish f : list ) {
			if( !fishnames.contains( f.name ) ) {
				remfish.add( f );
			}
		}
		list.removeAll( remfish );
	}
	
	public String getName( XSSFRow wr, int cellInd ) {
		XSSFCell	cell =  wr.getCell( cellInd );
		int celltype = cell.getCellType();
		
		String name = null;
		if( celltype == XSSFCell.CELL_TYPE_NUMERIC ) {
			double val = cell.getNumericCellValue();
			name = Integer.toString((int)val);
		} else {
			name = cell.getStringCellValue();
		}
		
		return name;
	}
	
	public boolean summaryCheck( XSSFWorkbook wb, int sheetInd ) {
		XSSFSheet 		sheet = wb.getSheetAt( sheetInd );
		XSSFRow			row = sheet.getRow(0);
		
		boolean b = false;
		int i = 0;
		XSSFCell 		cell = row.getCell(i++);
		while( cell != null ) {
			if( cell.getStringCellValue().equalsIgnoreCase("sex") ) {
				b = true;
				break;
			}
			cell = row.getCell(i++);
		}
		
		return b;
	}
	
	public void xssfStuff( XSSFWorkbook wb, int sheetInd ) throws FileNotFoundException, IOException {
		FormulaEvaluator 	evaluator = wb.getCreationHelper().createFormulaEvaluator();
		XSSFSheet 			sheet = wb.getSheetAt( sheetInd );
		
		int r = 0;
		XSSFRow 		row = sheet.getRow(r);
		
		int sexind = -1;
		int sampind = -1;
		int pind = -1;
		int c = 0;
		XSSFCell		cell = row.getCell(c);
		while( cell != null && cell.getCellType() != XSSFCell.CELL_TYPE_BLANK && (cell.getCellType() != XSSFCell.CELL_TYPE_STRING || cell.getStringCellValue().length() > 0) ) {
			String cellval = cell.getStringCellValue();
			if( cellval.equalsIgnoreCase("sex") ) sexind = c;
			else if( cellval.equalsIgnoreCase("sample") ) sampind = c;
			else if( cellval.equalsIgnoreCase("performance") ) {
				pind = c;
			}
			
			if( pind > 0 && c > pind ) {
				parameterNames.add( cellval );
				parameterTypes.add( String.class );
			}
			
			c++;
			cell = row.getCell(c);
		}
		
		row = sheet.getRow( ++r );
		//Fish cmpf = new Fish( "", 0.0f, 0, true );
		List<Object>	objList = new ArrayList<Object>();
		while( row != null ) {
			cell = row.getCell(sexind);
			if( cell != null ) {
				String 	sex = cell.getStringCellValue();
				cell = row.getCell(sampind);
				
				String 	name = null;
				int type = cell.getCellType();
				if( type == HSSFCell.CELL_TYPE_NUMERIC ) {
					name = Integer.toString( (int)cell.getNumericCellValue() );
				} else {
					name = cell.getStringCellValue();
				}
				
				int 	k = pind+1;
				cell = row.getCell( k );
				while( cell != null && cell.getCellType() != XSSFCell.CELL_TYPE_BLANK && (cell.getCellType() != XSSFCell.CELL_TYPE_STRING || cell.getStringCellValue().length() > 0) ) {
					type = cell.getCellType();
					if( type == XSSFCell.CELL_TYPE_STRING ) {
						objList.add( cell.getStringCellValue() );
						parameterTypes.set( k-pind-1, String.class );
					} else if( type == XSSFCell.CELL_TYPE_NUMERIC ) {
						objList.add( cell.getNumericCellValue() );
						parameterTypes.set( k-pind-1, Double.class );
					}
					k++;
					cell = 	row.getCell( k );
				}
				
				/*cell = 	row.getCell(wind);
				type = cell.getCellType();
				double 	weight = 0.0;
				if( type == HSSFCell.CELL_TYPE_NUMERIC ) {
					weight = cell.getNumericCellValue();
				} else {
					String	val = cell.getStringCellValue();
					try {
						weight = Double.parseDouble(val);
					} catch( Exception e ) {
				
					}
				}
				cell = 	row.getCell(locind);
				int		loc = (int)cell.getNumericCellValue();*/
				
				float		performance = 0.0f;
				if( pind != -1 ) {
					cell = 	row.getCell(pind);
					if( cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC ) {
						performance = (float)cell.getNumericCellValue();
					} else if( cell.getCellType() == XSSFCell.CELL_TYPE_STRING ) {
						String cellval = cell.getStringCellValue();
						try {
							float f = Float.parseFloat( cellval );
							performance = f;
						} catch( Exception e ) {
							
						}
					} else if( cell.getCellType() == XSSFCell.CELL_TYPE_FORMULA ) {
						CellValue cv = evaluator.evaluate( cell );
						if( cv.getCellType() == XSSFCell.CELL_TYPE_NUMERIC ) {
							double cellval = cv.getNumberValue();
							performance = (float)cellval;
							/*try {
								float f = Float.parseFloat( cellval );
								performance = f;
							} catch( Exception e ) {
								
							}*/
						}
					}
				}
	
				FishWorker.Fish 		tmpf = null;
				Object[] 				objArr = objList.toArray( new Object[0] );
				if( sex.equalsIgnoreCase("male") ) {
					int 		mind = findFish( name ); //fishes.indexOf( cmpf );
					if( mind != -1 ) {
						tmpf = fishes.get(mind);
						tmpf.params = objArr;
						//tmpf.weight = (float)weight;
						//tmpf.loc = loc;
						tmpf.male = true;
						tmpf.factor = performance;
					} else {
						tmpf = new Fish( name, objArr, performance, true );
					}
					malefish.add( tmpf );
					mfactor.add( performance );
				} else if( sex.equalsIgnoreCase("female") ) {
					int 		find = findFish( name );
					if( find != -1 ) {
						tmpf = fishes.get(find);
						tmpf.params = objArr;
						//tmpf.weight = (float)weight;
						//tmpf.loc = loc;
						tmpf.male = true;
						tmpf.factor = performance;
					} else {
						tmpf = new Fish( name, objArr, performance, true );
					}
					femalefish.add( tmpf );
					ffactor.add( performance );
				} else {
					
				}
				
				objList.clear();
			}
			
			row = sheet.getRow( ++r );	
		}
		
		if( fishes.size() > 0 ) {
			initGenotypes();
		}
	}
	
	public void hssfStuff( File f ) throws FileNotFoundException, IOException {
		HSSFWorkbook 	workbook = new HSSFWorkbook( new FileInputStream(f) );
		HSSFSheet 		sheet = workbook.getSheetAt(0);
		
		int r = 0;
		HSSFRow 		row = sheet.getRow(r);
		
		int sexind = -1;
		int sampind = -1;
		int locind = -1;
		int wind = -1;
		int c = 0;
		HSSFCell		cell = row.getCell(c);
		while( cell != null ) {
			String cellval = cell.getStringCellValue();
			if( cellval.equalsIgnoreCase("sex") ) sexind = c;
			else if( cellval.equalsIgnoreCase("sample") ) sampind = c;
			else if( cellval.equalsIgnoreCase("room") ) locind = c;
			else if( cellval.equalsIgnoreCase("weight") ) wind = c;
			
			c++;
			cell = row.getCell(c);
		}
		
		row = sheet.getRow( ++r );
		//Fish cmpf = new Fish( "", 0.0f, 0, true );
		List<Object>	objList = new ArrayList<Object>();
		while( row != null ) {
			cell = row.getCell(sexind);
			if( cell != null ) {
				String 	sex = cell.getStringCellValue();
				cell = row.getCell(sampind);
				
				String 	name = null;
				int type = cell.getCellType();
				if( type == HSSFCell.CELL_TYPE_NUMERIC ) {
					name = Integer.toString( (int)cell.getNumericCellValue() );
				} else {
					name = cell.getStringCellValue();
				}
				
				//cell = row.getCell( pind+1 );
				/*cell = 	row.getCell(wind);
				type = cell.getCellType();
				double 	weight = 0.0;
				if( type == HSSFCell.CELL_TYPE_NUMERIC ) {
					weight = cell.getNumericCellValue();
				} else {
					String	val = cell.getStringCellValue();
					try {
						weight = Double.parseDouble(val);
					} catch( Exception e ) {
				
					}
				}
				cell = 	row.getCell(locind);
				int		loc = (int)cell.getNumericCellValue();*/
				
	
				FishWorker.Fish 		tmpf = null;
				Object[]				objArr = objList.toArray( new Object[0] );
				if( sex.equalsIgnoreCase("male") ) {
					int 		mind = findFish( name ); //fishes.indexOf( cmpf );
					if( mind != -1 ) {
						tmpf = fishes.get(mind);
						tmpf.params = objArr;
						/*tmpf.weight = (float)weight;
						tmpf.loc = loc;*/
						tmpf.male = true;
					} else {
						tmpf = new Fish( name, objArr, 0.0f, true );
					}
					malefish.add( tmpf );
					mfactor.add( 0.0f );
				} else if( sex.equalsIgnoreCase("female") ) {
					int 		find = findFish( name );
					if( find != -1 ) {
						tmpf = fishes.get(find);
						tmpf.params = objArr;
						/*tmpf.weight = (float)weight;
						tmpf.loc = loc;*/
						tmpf.male = true;
					} else {
						tmpf = new Fish( name, objArr, 0.0f, true );
					}
					femalefish.add( tmpf );
					ffactor.add( 0.0f );
				} else {
					
				}
				objList.clear();
			}
			
			row = sheet.getRow( ++r );	
		}
		
		if( fishes.size() > 0 ) {
			initGenotypes();
		}
	}
	
	public void loadFiles( File[] ff ) throws IOException {
		markers.clear();
		fishes.clear();
		malefish.clear();
		femalefish.clear();
		
		if( ff.length > 1 ) {				
			for( File f : ff ) {
				String path = f.getAbsolutePath();
				if( path.endsWith(".xlsx" ) ) {
					XSSFWorkbook 	wb = new XSSFWorkbook( path );
					
					boolean b = summaryCheck( wb, 0 );
					boolean b2 = malefish.size() == 0 && femalefish.size() == 0;
					
					if( b ) {
						xssfStuff( wb, 0 );
					} else {
						if( b2 ) {
							wbStuffNoSex( wb );
						} else {
							wbStuff( wb, 0 );
						}
					}
				} else if( path.endsWith(".xls") ) {
					hssfStuff( f );
				} else if( path.endsWith(".txt") ) {
					char[]	cc = new char[1024];
					String val = "";
					FileReader fr = new FileReader( f );
					int r = fr.read( cc );
					while( r > 0 ) {
						val += new String( cc, 0, r );
						r = fr.read( cc );
					}
					parseData( val, 3 );
				}
			}
			tupleList = calcData();
		} else {
			File f = ff[0];
			String path = f.getAbsolutePath();
			if( path.endsWith(".xlsx" ) ) {
				loadSingleStream( new FileInputStream( f ) );
			}
		}
	}
	
	public XSSFWorkbook loadSingleStream( InputStream stream ) throws FileNotFoundException, IOException {
		XSSFWorkbook wb = new XSSFWorkbook( stream );
		
		if( wb.getNumberOfSheets() > 1 ) {
			boolean b = summaryCheck( wb, 0 );
		
			if( b ) {
				xssfStuff( wb, 0 );
				wbStuff( wb, 1 );
			} else {
				b = summaryCheck( wb, 1 );
				if( b ) {
					xssfStuff( wb, 1 );
					wbStuff( wb, 0 );
				}
			}	
		} else {
			plainStuff( wb );
		}
		tupleList = calcData();
		
		return wb;
	}
	
	public void initGenotypes( XSSFSheet ws, int i ) {
		int oldi = i;
		Set<String>	nameSet = new HashSet<String>();
		XSSFRow wr = ws.getRow( i++ );
		while( wr != null ) {
			String name = getName( wr, 0 );
			if( name != null ) nameSet.add( name );
			name = getName( wr, 1 );
			if( name != null ) nameSet.add( name );
			
			wr = ws.getRow( i++ );
		}
		
		retainFish( malefish, nameSet );
		retainFish( femalefish, nameSet );
		
		nameSet.clear();
		for( Fish f : malefish ) {
			nameSet.add( f.name );
		}
		for( Fish f : femalefish ) {
			nameSet.add( f.name );
		}
		
		fmatrix = new int[femalefish.size() * markers.size()];
		mmatrix = new int[malefish.size() * markers.size()];
		
		//Fish tmpf =new Fish( "", 0.0f, 0, false );
		
		int u = -1;
		i = oldi;
		wr = ws.getRow( i++ );
		while( wr != null ) {
			int k = 2;
			
			String name = null;
			if( u == -1 ) {
				name = getName( wr, 0 );
				if( nameSet.contains( name ) ) u = 0;
				else u = 1;
			}
			
			name = getName( wr, u );
			
			//tmpf.name = name;
			
			int ind = findFish( malefish, name ); //malefish.indexOf( tmpf );
			if( ind != -1 ) {
				XSSFCell wc = wr.getCell(k);
				while( k < markers.size()+2 ) {
					if( wc != null ) {
						int val = -1;
						if( wc.getCellType() != XSSFCell.CELL_TYPE_NUMERIC ) {
							//System.err.println( wc.getStringCellValue() );
							val = wc.getStringCellValue().hashCode();
						}
						else val = (int)wc.getNumericCellValue();
						
						mmatrix[ind * markers.size() + (k-2)] = val;
					}
					wc = wr.getCell(++k);
				}
			} else {
				ind = findFish( femalefish, name );
				if( ind != -1 ) {
					XSSFCell wc = wr.getCell(k);
					while( k < markers.size()+2 ) {
						if( wc != null ) {
							int val = -1;
							if( wc.getCellType() != XSSFCell.CELL_TYPE_NUMERIC ) val = wc.getStringCellValue().hashCode();
							else val = (int)wc.getNumericCellValue();
							
							fmatrix[ind * markers.size() + (k-2)] = val;
							//else System.err.println( "what!! " + wc.getStringCellValue() );
						}
						wc = wr.getCell(++k);
					}
				}
			}
			wr = ws.getRow( i++ );
		}
		
		initFreqs();
	}
	
	public void wbStuff( XSSFWorkbook wb, int sheetInd ) throws IOException {
		XSSFSheet		ws = wb.getSheetAt( sheetInd );
		
		markers.clear();
		//malefish.clear();
		//femalefish.clear();
		//mfactor.clear();
		//ffactor.clear();
		
		int i = 0;
		XSSFRow			wr = ws.getRow( i++ );
		
		int k = 2;
		XSSFCell wc = wr.getCell(k++);
		while( wc != null && wc.getStringCellValue().length() > 0 ) {
			markers.add( wc.getStringCellValue() );
			wc = wr.getCell(k++);
		}
		if( markers.size() % 2 == 1 ) markers.remove( markers.size()-1 );
		
		initGenotypes( ws, i );
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if( args.length > 0 ) {
			System.out.println("hoho");
		} else {
			FishWorker fish = new FishWorker();
			try {
				fish.loadSingleStream( System.in );
				XSSFWorkbook wb = new XSSFWorkbook();
				fish.writeWorkbook(wb);
				wb.write( System.out );
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
