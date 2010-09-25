package org.simmi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

public class DiyaHelper {

	static class InnerHelper {
		public InnerHelper( String name ) {
			this.name = name;
		}
		
		public String toString() {
			return name + " " + longName;
		}
		
		String	name;
		String	longName;
	}
	
	public static void usage() {
		System.out.println( "usage: java -jar diyahelper.jar [outfile] [gbkfile] [resultsfile] > [aminoacid fasta file]" );
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if( args.length < 4 ) {
			usage();
		} else {
			String blastfilename = args[0];
			String genebankfilename = args[1];
			String outfilename = args[2];
			String aafa = args[3];
			
			Map<String,InnerHelper>	inMap = new HashMap<String,InnerHelper>();
			File f = new File( blastfilename );
			try {
				BufferedReader	bufferedReader = new BufferedReader( new FileReader(f) );
				
				String line = bufferedReader.readLine();
				InnerHelper current = null;
				while( line != null ) {
					if( line.startsWith("Query=") ) {
						if( current != null ) inMap.put( current.name, current );
						String newname = line.substring(6).trim();
						current = new InnerHelper( newname );
					} else if( line.startsWith(">" ) ) {
						if( current != null ) {
							current.longName = line.substring(2);
							line = bufferedReader.readLine();
							while( line != null && !line.startsWith("Length=") ) {
								current.longName += line;
								line = bufferedReader.readLine();
							}
							if( line == null ) break;
							//int i = current.longName.indexOf("RepID=");
							//current.repid = current.longName.substring(i+6);
							//current.longName = current.longName.substring(0, i-1);
						}
					}
					line = bufferedReader.readLine();
				}
				bufferedReader.close();
				
				for( String name : inMap.keySet() ) {
					System.err.println( inMap.get( name ) );
				}
				
				f = new File( genebankfilename );
				bufferedReader = new BufferedReader( new FileReader(f) );
				f = new File( outfilename );
				FileWriter	fw = new FileWriter( f );
				
				line = bufferedReader.readLine();
				while( line != null ) {
					String[] split = line.split("[ ]+");
					if( split.length > 1 && split[1].equals("CDS") ) {
						fw.write( line + "\n" );
						line = bufferedReader.readLine();
						String res = "/note";
						if( line != null ) res = line.trim(); 
						String name = null;
						String writeline;
						while( !res.startsWith("/note") ) {
							if( res.startsWith("/locus_tag") ) {
								writeline = line;
								String val = res.substring(12, res.length()-1);
								InnerHelper in = inMap.get( val );
								if( in != null ) name = in.longName;
								line = bufferedReader.readLine();
							} else if( res.startsWith("/product") ) {
								if( name != null ) writeline = "                     /product=\""+name+"\"";
								else writeline = line + "\"";
								
								line = bufferedReader.readLine();
								while( line != null && !line.trim().startsWith("/") ) {
									line = bufferedReader.readLine();
								}
							} /*else if( !res.startsWith("/") ) {
								fw.write( line+"\n" );
								line = bufferedReader.readLine();
							}*/ else {
								writeline = line;
								line = bufferedReader.readLine();
							}
							fw.write( writeline + "\n" );
							
							res = "/note";
							if( line != null ) res = line.trim();
						}
						if( line == null ) break;
						
						fw.write("                     /note=\""+name+"\"\n");
					} else {
						fw.write( line+"\n" );
					}
					line = bufferedReader.readLine();
				}
				bufferedReader.close();
				fw.close();
				
				PrintStream old = System.out;
				PrintStream	ps = new PrintStream(aafa);
				System.setOut(ps);
				GBK2AminoFasta.main( new String[] { outfilename } );
				ps.close();
				System.setOut( old );
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
