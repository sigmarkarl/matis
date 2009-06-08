package org.simmi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class KinTool {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		File f = new File( args[0] );
		File of = new File( args[1] );
		try {
			FileReader fr = new FileReader( f );
			FileWriter fw = new FileWriter( of );
			
			BufferedReader br = new BufferedReader( fr );
			String line = br.readLine();
			
			int i;
			String[] split = line.split("\t");	
			int slen = split.length;
			fw.write( split[0]+"\t"+split[1] );
			for( i = 2; i < split.length; i+=2 ) {
				fw.write( "\t"+split[i] );
			}
			fw.write("\n");
			
			line = br.readLine();
			while( line != null ) {
				split = line.split("\t");
				if( split.length > 2 ) {
					fw.write( split[0]+"\t"+split[1] );
					for( i = 2; i < slen; i+=2 ) {
						if( i < split.length ) {
							if( split[i].length() == 0 ) split[i] = "0";
							if( split[i+1].length() == 0 ) split[i+1] = "0";
							
							fw.write( "\t"+split[i]+"/"+split[i+1] );
						} else {
							fw.write( "\t0/0" );
						}
					}
					fw.write("\n");
				}
				
				fw.flush();
				System.err.println( split[0] );
				line = br.readLine();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
