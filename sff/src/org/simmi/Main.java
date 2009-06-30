/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.simmi;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

/**
 *
 * @author sigmar
 */
public class Main {
    static final int FASTI = 4096;
    
    static List<File>	sffList = new ArrayList<File>();
    
    public static void recFile( File f ) {
    	File[] ff = f.listFiles( new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				if( name.endsWith("pif") ) return true;
				return false;
			}
		});
    	sffList.addAll( Arrays.asList(ff) );
    	ff = f.listFiles( new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				if( pathname.isDirectory() ) return true;
				return false;
			}
		});
    	for( File file : ff ) {
    		recFile( file );
    	}
    };
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
    	File f = new File( args[0] );
    	recFile( f );
    	
        final ByteBuffer bbuf = ByteBuffer.allocate(4096 * 4096 * 2);
        bbuf.order( ByteOrder.nativeOrder() );
        //File f = new File("c:\\00009.sim");

        final BufferedImage img = new BufferedImage( FASTI, FASTI, BufferedImage.TYPE_INT_RGB );

        TableModel	model = new TableModel() {
			@Override
			public void addTableModelListener(TableModelListener l) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public Class<?> getColumnClass(int columnIndex) {
				return File.class;
			}

			@Override
			public int getColumnCount() {
				return 1;
			}

			@Override
			public String getColumnName(int columnIndex) {
				return "Filename";
			}

			@Override
			public int getRowCount() {
				return sffList.size();
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				return sffList.get(rowIndex);
			}

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return false;
			}

			@Override
			public void removeTableModelListener(TableModelListener l) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			
			}
        };
        final JTable		table = new JTable();
        table.setModel( model );
        JScrollPane	tablescroll = new JScrollPane( table );
        
        table.getSelectionModel().addListSelectionListener( new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				int r = table.getSelectedRow();
				 try {
		            FileInputStream fis = new FileInputStream( sffList.get(r) );
		            fis.skip(0);
		            int read = fis.read( bbuf.array() );
		            System.err.println( read );
		        } catch (FileNotFoundException ex) {
		            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
		        } catch (IOException ioe) {
					// TODO Auto-generated catch block
					ioe.printStackTrace();
				}
		        
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
			}
		});
        
        JScrollPane scrollpane = new JScrollPane();
        JComponent c = new JComponent() {
            public void paintComponent( Graphics g ) {
                super.paintComponent(g);
                g.drawImage( img, 0, 0, this );
            }
        };
        
        JSplitPane	splitpane = new JSplitPane();
        splitpane.setRightComponent( scrollpane );
        splitpane.setLeftComponent( tablescroll );
        
        c.setPreferredSize( new Dimension( FASTI, FASTI ) );
        scrollpane.setViewportView(c);
        JFrame frame = new JFrame();
        frame.add( splitpane );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setSize(800, 600);
        frame.setVisible( true );
    }
}
