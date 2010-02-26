package org.simmi;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JApplet;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class Mummy extends JApplet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JComponent	c;
	
	public Mummy() {
		c = new JComponent() {
			public void paintComponent( Graphics g ) {
				super.paintComponent( g );
			}
			
			public void setBounds( int x, int y, int w, int h ) {
				super.setBounds(x, y, w, h);
			}
		};
		c.setLayout( new BorderLayout() );
	}
	
	class Sequence implements Comparable<Sequence> {
		public Sequence( String name, ByteBuffer bb, int offset ) {
			this.name = name;
			this.bb = bb;
			this.offset = offset;
		}
		
		String 		name;
		ByteBuffer	bb;
		int			offset;
		
		@Override
		public int compareTo(Sequence o) {
			return offset - o.offset;
		}
	}
	
	class SequencePane extends JComponent {
		List<Sequence>	sequencelist;
		final String	a = "A";
		final String	c = "C";
		final String	g = "G";
		final String	t = "T";
		
		public SequencePane( final List<Sequence> seqlist ) {
			sequencelist = seqlist;
			int size = 0;
			for( Sequence seq : sequencelist ) {
				int end = seq.bb.limit() + seq.offset;
				if( end > size ) size = end;
			}
			Dimension d = new Dimension( 10*size, 10 );
			setSize( d );
			setPreferredSize( d );
		}
		
		public void paintComponent( Graphics g ) {
			super.paintComponent( g );
			Graphics2D g2 = (Graphics2D)g;
			g2.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
			Rectangle clip = g.getClipBounds();
			Sequence seq0 = sequencelist.get(0);
			for( int y = 0; y < sequencelist.size(); y++ ) {
				Sequence seq = sequencelist.get(y);
				for( int x = Math.max( seq.offset, clip.x/10 ); x < Math.min( seq.offset+seq.bb.limit(), (clip.x+clip.width)/10 ); x++ ) {
					byte b = seq.bb.get( x - seq.offset );
					byte bc = 0;
					if( x - seq0.offset < seq0.bb.limit() ) bc = seq0.bb.get( x - seq0.offset );
					if( b != bc ) {
						g.setColor( Color.red );
					} else {
						g.setColor( Color.black );
					}
					if( b == 'a' || b == 'A' ) g.drawString(this.a, x*10, (y+1)*10);
					else if( b == 'c' || b == 'C' ) g.drawString(this.c, x*10, (y+1)*10);
					else if( b == 'g' || b == 'G' ) g.drawString(this.g, x*10, (y+1)*10);
					else if( b == 't' || b == 'T' ) g.drawString(this.t, x*10, (y+1)*10);
					else g.drawString("N", x*10, (y+1)*10);
				}
			}
		}
	}
	
	public void initGui() throws IOException {
		JScrollPane	scrollpane = new JScrollPane();
		c.add( scrollpane );
		
		JTable	lefttable = new JTable();
		
		//final List<Sequence> bbl = load( "/home/sigmar/fass/wholeHB27.fas", null );
		final List<Sequence> lseq1 = load( "/home/sigmar/fass/wholeHB27.fas", null );
		final List<Sequence> lseq2 = load( "/home/sigmar/fass/Strain346AllContigs.fas", null );
		lseq1.addAll( lseq2 );
		SequencePane pane = new SequencePane( lseq1 );
		scrollpane.setViewportView( pane );
	}
	
	public List<Sequence> load( String fname, String foffset ) throws IOException {
		File f = new File( fname );
		ByteBuffer bb = ByteBuffer.allocate( (int)f.length() );
		FileInputStream fis = new FileInputStream( f );
		int r = fis.read( bb.array() );
		
		
		Map<String,Integer>	offsetMap = new HashMap<String,Integer>();
		if( foffset != null ) {
			f = new File( foffset );
			FileReader			fr = new FileReader( f );
			BufferedReader 		br = new BufferedReader( fr );
			
			String line = br.readLine();
			while( line != null ) {
				if( line.startsWith(">") ) {
					
				}
				
				line = br.readLine();
			}
		}
		
		
		
		List<Sequence>	lseq = new ArrayList<Sequence>();
		
		int i = 0;
		int start = 0;
		int c = 0;
		String name = "";
		while( i < bb.limit() ) {
			byte b = bb.get(i++);
			if( b == '>' ) {
				int strstart = i+1;
				while( bb.get(i++) != ' ' ) ;
				int strstop = i-2;
				
				int stop = i-2;
				int length = stop-start-c;
				if( length > 0 ) {
					ByteBuffer tb = ByteBuffer.allocate( length );
					int cc = 0;
					for( int k = start; k < stop; k++ ) {
						byte bt = bb.get(k);
						if( bt == '\n' || bt == '\r' ) cc++;
						else {
							int ind = k-cc-start;
							if( ind < 0 || ind >= tb.limit() ) System.err.println( ind );
							else tb.put( ind, bt );
						}
					}
					lseq.add( new Sequence( name ,tb, 0 ) );
				}
				while( bb.get(i++) != '\n' );
				start = i;
				c = 0;
				name = new String( bb.array(), strstart, strstop );
			} else if( b == '\n' || b == '\r' ) c++;
		}
		
		int stop = i-2;
		int length = stop-start-c;
		if( length > 0 ) {
			ByteBuffer tb = ByteBuffer.allocate( length );
			int cc = 0;
			for( int k = start; k < stop; k++ ) {
				byte bt = bb.get(k);
				if( bt == '\n' || bt == '\r' ) cc++;
				else {
					int ind = k-cc-start;
					if( ind < 0 || ind >= tb.limit() ) System.err.println( ind );
					else tb.put( ind, bt );
				}
			}
			lseq.add( new Sequence( name, tb, 0 ) );
		}
		
		Collections.sort( lseq );
		
		/*int c = 0;
		for( i = 0; i < bb.limit(); i++ ) {
			byte b = bb.get(i);
			c++;
			if( b == '\n' ) break;
		}
		i++;
		for( ; i < bb.limit(); i++ ) {
			byte b = bb.get(i);
			if( b == '\n' ) c++;
			else bb.put( i-c, b );
		}*/
		
		return lseq;
	}
	
	public void init() {
		try {
			initGui();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.add( c );
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Mummy mummy = new Mummy();
			JFrame	frame = new JFrame("Mummy");
			frame.setLayout( new BorderLayout() );
			frame.getContentPane().setLayout( new BorderLayout() );
			frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
			frame.setSize(800, 600);
			frame.add( mummy.c );
			mummy.initGui();
			frame.setVisible( true );
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}