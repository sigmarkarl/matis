/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.simmi;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sigmar
 */
public class SffHandler {
    public SffHandler( String name ) throws FileNotFoundException, IOException {
        super();

        File f = new File( name );
        FileInputStream fis = new FileInputStream( f );
        DataInputStream dis = new DataInputStream( fis );

        int header = dis.readInt();
        int v1 = dis.read();
        int v2 = dis.read();
        int v3 = dis.read();
        int v4 = dis.read();
        long offset = dis.readLong();
        int length = dis.readInt();

        int numReads = dis.readInt();
        int headerLength = dis.readUnsignedShort();
        int keyLength = dis.readUnsignedShort();
        int numFlowsPerRead = dis.readUnsignedShort();
        int flowgramFormatCode = dis.readUnsignedByte();
        int flowgramBytesPerFlow = 2 * flowgramFormatCode;

        byte[] flowChars = new byte[ numFlowsPerRead ];
        dis.read( flowChars );

        byte[] keySequence = new byte[ keyLength ];
        dis.read( keySequence );

        int tell = 31 + numFlowsPerRead + keyLength;

        if( 8*Math.ceil(tell/8.0) != headerLength ) {
            System.err.println( "error" );
        }

        System.err.println( Integer.toString( header, 16 ) + " " + v1 + "." + v2 + "." + v3 + "." + v4 + " " + offset + " " + length );
        System.err.println( numReads + " " + headerLength + " " + keyLength + " " + numFlowsPerRead );
        System.err.println( new String( flowChars ) );
        System.err.println( new String( keySequence ) );

        byte[]  padding = new byte[0];
        int padLen = headerLength - tell;
        if( padLen > 0 ) {
            if( padLen > padding.length ) padding = new byte[ padLen ];
            dis.read( padding, 0, padLen );
        }

        tell = headerLength;

        int i = 0;
        int b = 0;

        //int[]   blocks = {0,99};

        int[] flowgramValues = new int[0];
        int[] flowgramIndexs = new int[0];
        byte[] sequence = new byte[0];
        int[] qual = new int[0];
        int numOutputReads = numReads;
        while( b < numOutputReads ) {

            /*if( offset == tell ) {
                byte[]  indexBytes = new byte[ length ];
                dis.read( indexBytes );
            }*/

            int readHeaderLength = dis.readUnsignedShort();
            int nameLength = dis.readUnsignedShort();
            int numBases = dis.readInt();
            int readDataLength = numFlowsPerRead * flowgramBytesPerFlow + 3*numBases;
            int readDataLengthRounded = (int)(8*Math.ceil( readDataLength/8.0 ));

            System.err.println( "start" );
            System.err.println( readHeaderLength );
            System.err.println( nameLength );
            System.err.println( numBases );

            int qualLeft = dis.readUnsignedShort();
            int qualRight = dis.readUnsignedShort();
            int adapterLeft = dis.readUnsignedShort();
            int adapterRight = dis.readUnsignedShort();

            byte[] headerSub = new byte[ nameLength ];
            dis.read( headerSub );

            System.err.println( new String(headerSub) );

            padLen = readHeaderLength - 16 - nameLength;
            if( padLen > 0 ) {
                System.err.println( padLen );
                if( padLen > padding.length ) padding = new byte[ padLen ];
                dis.read( padding, 0, padLen );
            }

            if( numFlowsPerRead > flowgramValues.length ) flowgramValues = new int[ numFlowsPerRead ];
            for( int k = 0; k < numFlowsPerRead; k++ ) {
                flowgramValues[k] = dis.readUnsignedShort();
            }

            if( numBases > flowgramIndexs.length ) flowgramIndexs = new int[ numBases ];
            for( int k = 0; k < numBases; k++ ) {
                flowgramIndexs[k] = dis.readUnsignedByte();
            }

            if( numBases > sequence.length ) sequence = new byte[ numBases ];
            dis.read( sequence, 0, numBases );
            System.err.println( new String( sequence, 0, numBases ) );

            if( numBases > qual.length ) qual = new int[ numBases ];
            for( int k = 0; k < numBases; k++ ) {
                qual[k] = dis.readUnsignedByte();
            }

            padLen = readDataLengthRounded - readDataLength;
            if( padLen > 0 ) {
                if( padLen > padding.length ) padding = new byte[ padLen ];
                dis.read( padding, 0, padLen );
            }

            b++;
            i++;
        }

        dis.close();
        fis.close();
    }

    public static void main( String[] args ) {
        try {
            SffHandler sffHandler = new SffHandler("c:\\EBO6PME02.sff");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SffHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SffHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
