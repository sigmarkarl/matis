package org.simmi;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class SffHandler {

	public SffHandler( String name ) throws IOException {
        super();

        File f = new File( name );
        FileInputStream fis = new FileInputStream( f );
        DataInputStream	dis = new DataInputStream( fis );
        
        System.err.println( "Filesize: "+f.length() );
        System.err.println( "Header: "+Integer.toString( dis.readInt(), 16 ) );
        int v1 = dis.read();
        int v2 = dis.read();
        int v3 = dis.read();
        int v4 = dis.read();
        System.err.println( "Version: "+v1+"."+v2+"."+v3+"."+v4);
        System.err.println( "Offset: "+dis.readLong() );
        System.err.println( "Length: "+dis.read	 );
    }

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) {
		try {
			SffHandler sffHandler = new SffHandler( "/home/sigmar/sff/EBO6PME01.sff" );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
