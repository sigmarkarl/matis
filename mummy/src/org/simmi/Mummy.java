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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JApplet;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.RowSorterListener;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class Mummy extends JApplet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JSplitPane		splitpane = new JSplitPane( JSplitPane.VERTICAL_SPLIT );
	SequencePane	seqpane;
	SequencePane	topseq;
	Overview		ov;
	
	public Mummy() {}
	
	class Sequence implements Comparable<Sequence> {
		public Sequence( String name, ByteBuffer bb, int offset ) {
			this.name = name;
			this.bb = bb;
			this.offset = offset;
		}
		
		String 		name;
		ByteBuffer	bb;
		int		offset;
		
		@Override
		public int compareTo(Sequence o) {
			return offset - o.offset;
		}
	}
	
	class SequencePane extends JComponent {
		JTable			seqtable;
		List<Sequence>	sequencelist;
		final String	a = "A";
		final String	c = "C";
		final String	g = "G";
		final String	t = "T";
		Dimension		prefsize;
		
		public SequencePane( final JTable seqtable, final List<Sequence> seqlist ) {
			super();
			
			//this.setBackground( Color.white );
			this.sequencelist = seqlist;
			this.seqtable = seqtable;
			int size = 0;
			for( Sequence seq : sequencelist ) {
				int end = seq.bb.limit() + seq.offset;
				if( end > size ) size = end;
			}
			int seqtabHeight = 16;
			if( seqtable != null ) seqtable.getHeight();
			prefsize = new Dimension( 10*size, seqtabHeight );
			setSize( prefsize );
			setPreferredSize( prefsize );
		}
		
		public Dimension getPreferredSize() {
			if( seqtable != null ) {
				prefsize.height = seqtable.getHeight();
			} else prefsize.height = 16;
			return prefsize;
		}
		
		public void paintComponent( Graphics g ) {
			super.paintComponent( g );
			Graphics2D g2 = (Graphics2D)g;
			g2.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
			Rectangle clip = g.getClipBounds();
			
			if( seqtable != null ) {
				Sequence seq0 = sequencelist.get(0);			
				int rh = seqtable.getRowHeight();
				for( int y = 0; y < seqtable.getRowCount(); y++ ) {
					if( seqtable.isRowSelected(y) ) {
						g.setColor( Color.blue.darker() );
						g.fillRect(clip.x, y*rh, clip.width, rh);
					}
					int i = seqtable.convertRowIndexToModel(y);
					//System.err.println( i + "  " + y );
					//seqtable.row
					Sequence seq = sequencelist.get(i);
					for( int x = Math.max( seq.offset, clip.x/10 ); x < Math.min( seq.offset+seq.bb.limit(), (clip.x+clip.width)/10 ); x++ ) {
						byte b = seq.bb.get( x - seq.offset );
						byte bc = 0;
						if( x - seq0.offset < seq0.bb.limit() ) bc = seq0.bb.get( x - seq0.offset );
						if( b != bc ) {
							g.setColor( Color.red );
						} else {
							g.setColor( Color.black );
						}
						if( b == 'a' || b == 'A' ) g.drawString(this.a, x*10, (y+1)*rh);
						else if( b == 'c' || b == 'C' ) g.drawString(this.c, x*10, (y+1)*rh);
						else if( b == 'g' || b == 'G' ) g.drawString(this.g, x*10, (y+1)*rh);
						else if( b == 't' || b == 'T' ) g.drawString(this.t, x*10, (y+1)*rh);
						else g.drawString("N", x*10, (y+1)*rh);
					}
				}
			} else {
				int y = 0;
				int rh = 16;
				for( Sequence seq : sequencelist ) {
					for( int x = Math.max( seq.offset, clip.x/10 ); x < Math.min( seq.offset+seq.bb.limit(), (clip.x+clip.width)/10 ); x++ ) {
						byte b = seq.bb.get( x - seq.offset );
						if( b == 'a' || b == 'A' ) g.drawString(this.a, x*10, (y+1)*rh);
						else if( b == 'c' || b == 'C' ) g.drawString(this.c, x*10, (y+1)*rh);
						else if( b == 'g' || b == 'G' ) g.drawString(this.g, x*10, (y+1)*rh);
						else if( b == 't' || b == 'T' ) g.drawString(this.t, x*10, (y+1)*rh);
						else g.drawString("N", x*10, (y+1)*rh);
					}
					y++;
				}
			}
			if( ov != null ) ov.repaint();
		}
	}
	
	class Overview extends JComponent {
		JTable			table;
		SequencePane	pane;
		Color			color = new Color( 150,150,150,150 );
		
		public Overview( JTable	table, SequencePane	pane ) {
			this.table = table;
			this.pane = pane;
		}
		
		public void paintComponent( Graphics g ) {
			super.paintComponent(g);
			
			int w = this.getWidth();
			
			List<Sequence>	lseq = pane.sequencelist;
			g.setColor( Color.green.darker() );
			long conslen = lseq.get(0).bb.limit();
			
			for( int y = 0; y < table.getRowCount(); y++ ) {
				int i = table.convertRowIndexToModel( y );
				Sequence seq = lseq.get(i);
				int x1 = (int)( ( (long)seq.offset * w ) / conslen );
				int x2 = (int)( ( ((long)seq.offset + seq.bb.limit()) * w ) / conslen );
				
				int y2 = 2*y;
				g.drawLine(x1, y2, x2, y2);
				g.drawLine(x1, y2+1, x2, y2+1);
			}
			
			Rectangle r = pane.getVisibleRect();
			g.setColor( color );
			
			int px = (int)( ((long)r.x*w)/(long)pane.getWidth() );
			int pw = Math.max( 1, (int)( ((long)r.width*w)/(long)pane.getWidth() ) );
			g.fillRect( px, 0, pw, this.getHeight() );
		}
	}
	
	public void initGui() throws IOException {		
		JScrollPane	scrollpane = new JScrollPane();
		scrollpane.setBackground( Color.white );
		scrollpane.getViewport().setBackground( Color.white );
		
		JTable		lefttable = new JTable();
		JScrollPane	leftpane = new JScrollPane();
		leftpane.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_NEVER );
		
		scrollpane.setRowHeaderView( lefttable );
		leftpane.setViewport( scrollpane.getRowHeader() );
		JSplitPane	subsplit = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT );
		subsplit.setRightComponent( scrollpane );
		subsplit.setLeftComponent( leftpane );
		
		//final List<Sequence> bbl = load( "/home/sigmar/fass/wholeHB27.fas", null );
		final List<Sequence> lseq1 = load( "/home/sigmar/fass/wholeHB27.fas", null );
		final List<Sequence> lseq2 = load( "/home/sigmar/fass/Strain346AllContigs.fas", "/home/sigmar/fass/out" );
		//lseq1.addAll( lseq2 );
		seqpane = new SequencePane( lefttable, lseq2 );
		topseq = new SequencePane( null, lseq1 );
		
		scrollpane.setViewportView( seqpane );
		scrollpane.setColumnHeaderView( topseq );
		lefttable.setAutoCreateRowSorter( true );
		lefttable.setModel( new TableModel() {
			@Override
			public void addTableModelListener(TableModelListener l) {}

			@Override
			public Class<?> getColumnClass(int columnIndex) {
				if( columnIndex == 1 ) return Integer.class;
				
				return String.class;
			}

			@Override
			public int getColumnCount() {
				return 2;
			}

			@Override
			public String getColumnName(int columnIndex) {
				if( columnIndex == 0 ) return "Contig name";
				else if( columnIndex == 1 ) return "Offset";
				return "";
			}

			@Override
			public int getRowCount() {
				return lseq1.size();
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				Sequence seq = lseq1.get(rowIndex);
				if( columnIndex == 0 ) return seq.name;
				else if( columnIndex == 1 ) return seq.offset;
				
				return "";
			}

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return false;
			}

			@Override
			public void removeTableModelListener(TableModelListener l) {}

			@Override
			public void setValueAt(Object aValue, int rowIndex, int columnIndex) {}
		});
		
		lefttable.getRowSorter().addRowSorterListener( new RowSorterListener() {
			@Override
			public void sorterChanged(RowSorterEvent e) {
				ov.repaint();
				seqpane.repaint();
			}
		});
		lefttable.getSelectionModel().addListSelectionListener( new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				seqpane.repaint();
			}
		});
		
		ov = new Overview( lefttable, seqpane );
		//ov.setPreferredSize( new Dimension(500, 300) );
		splitpane.setTopComponent( subsplit );
		/*JComponent c = new JComponent() {};
		c.setLayout( new BorderLayout() );
		c.add( ov );*/
		splitpane.setBottomComponent( ov );
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
			
			String current = "";
			String line = br.readLine();
			while( line != null ) {
				if( line.startsWith(">") ) {
					current = line.substring(1).trim();
				} else if( current.length() > 0 ) {
					String[] split = line.trim().split("[ \t]+");
					if( split[0].length() > 0 ) {
						int lv = Integer.parseInt(split[0]);
						int ls = Integer.parseInt(split[1]); 
						offsetMap.put( current, lv-ls );
					} else {
						System.err.println( line );
					}
					
					current = "";
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
				int strstart = i;
				int stop = i-2;
				
				while( bb.get(i++) != ' ' ) ;
				int strstop = i-1;
				
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
					int off = 0;
					if( offsetMap.containsKey(name) ) {
						off = offsetMap.get(name);
					}
					lseq.add( new Sequence( name, tb, off ) );
				}
				while( bb.get(i++) != '\n' );
				start = i;
				c = 0;
				name = new String( bb.array(), strstart, strstop-strstart );
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
			int off = 0;
			if( offsetMap.containsKey(name) ) {
				off = offsetMap.get(name);
			}
			lseq.add( new Sequence( name ,tb, off ) );
		}
		
		//Collections.sort( lseq );
		
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
		this.add( this.splitpane );
	}
	
	/**
	 * @param args
	 */
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
			Mummy mummy = new Mummy();
			JFrame	frame = new JFrame("Mummy - Fasta Viewer / Aligner");
			frame.setBackground( Color.white );
			frame.getContentPane().setBackground( Color.white );
			frame.setLayout( new BorderLayout() );
			frame.getContentPane().setLayout( new BorderLayout() );
			frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
			frame.setSize(800, 600);
			frame.add( mummy.splitpane );
			mummy.initGui();
			frame.setVisible( true );
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}