package org.simmi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class TextReader {
	public static List<Object[]> readText( InputStream is, int[] cols ) throws IOException {
		//File			file = new File( filename );
		//FileReader		freader = new FileReader( file );
		InputStreamReader	ireader = new InputStreamReader( is );
		BufferedReader		breader = new BufferedReader( ireader );
		
		List<Object[]>		ret = new ArrayList<Object[]>();
		
		String line = breader.readLine();
		line = breader.readLine();
		while( line != null ) {
			String[] split = line.split("\t");
			
			Object[] obj = new Object[ cols.length ];
			int k = 0;
			for( int i : cols ) {
				if( i < split.length ) obj[k++] = split[i];
			}
			ret.add( obj );
			
			line = breader.readLine();
		}
		ireader.close();
		
		return ret;
	}
}
