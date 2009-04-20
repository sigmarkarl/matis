/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rawimg;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

/**
 *
 * @author sigmar
 */
public class Main {
    static final int FASTI = 4096;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        ByteBuffer bbuf = ByteBuffer.allocate(4096 * 4096 * 2);
        bbuf.order( ByteOrder.nativeOrder() );
        File f = new File("c:\\00009.sim");
        try {
            FileInputStream fis = new FileInputStream(f);
            fis.skip(0);
            int read = fis.read( bbuf.array() );
            System.err.println( read );

        } catch (FileNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

        final BufferedImage img = new BufferedImage( FASTI, FASTI, BufferedImage.TYPE_INT_RGB );

        ShortBuffer rgb = bbuf.asShortBuffer();
        for( int x = 0; x < FASTI; x++ ) {
            for( int y = 0; y < FASTI; y++ ) {
                int val = (int)rgb.get( y*FASTI+x );
                if( val < 0 ) val = Short.MAX_VALUE - val;
                int col = (val/8);
                col += (val/8) << 8;
                col += (val/8) << 16;
                img.setRGB( x, y, col );
            }
        }

        JScrollPane scrollPane = new JScrollPane();
        JComponent c = new JComponent() {
            public void paintComponent( Graphics g ) {
                super.paintComponent(g);
                g.drawImage( img, 0, 0, this );
            }
        };
        
        c.setPreferredSize( new Dimension( FASTI, FASTI ) );
        scrollPane.setViewportView(c);
        JFrame frame = new JFrame();
        frame.add( scrollPane );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setSize(800, 600);
        frame.setVisible( true );
    }
}
