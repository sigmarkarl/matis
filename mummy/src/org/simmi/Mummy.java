package org.simmi;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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

import javax.swing.AbstractAction;
import javax.swing.JApplet;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
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
	JToolBar		toolbar = new JToolBar();
	JComponent c = new JComponent() {
		
	};
	SequencePane	seqpane;
	SequencePane	topseq;
	Overview		ov;
	
	public Mummy() {}
	
	class FindDialog extends JDialog {
		JTextArea	textarea = new JTextArea();
		
		public FindDialog() {
			JScrollPane	scrollpane = new JScrollPane( textarea );
			this.add( scrollpane );
		}
	};
	
	class Offset implements Comparable<Offset> {
		int a;
		int b;
		int c;
		
		public Offset( int a, int b, int c ) {
			this.a = a;
			this.b = b;
			this.c = c;
		}
		
		@Override
		public int compareTo(Offset o) {
			return this.a - o.a;
		}
	}
	
	class Coff {
		public Coff( int bestMatchIndex, List<Offset> loffset ) {
			this.bestMatchIndex = bestMatchIndex;
			this.loffset = loffset;
		}
		
		public int size() {
			return loffset.size();
		}
		
		public void add( Offset o ) {
			loffset.add( o );
		}
		
		public Offset getBestOffset() {
			if( bestMatchIndex < loffset.size() ) return loffset.get( bestMatchIndex );
			
			return null;
		}
		
		int				bestMatchIndex;
		List<Offset>	loffset;
	}
	
	class Sequence implements Comparable<Sequence> {
		public Sequence( String name, ByteBuffer bb, int offset, Map<String,Coff> offsetMap ) {
			/*if( offsetList == null ) offsetList = new ArrayList<Offset>();
			if( offsetList.size() == 0 ) {
				offsetList.add( new Offset(0,0,0) );
			}*/
			
			this.name = name;
			this.bb = bb;
			this.offset = offset;
			this.offsetMap = offsetMap;
		}
		
		String 						name;
		ByteBuffer					bb;
		int							offset;
		Map<String,Coff>			offsetMap;
		
		public int getAlignStart( String name ) {
			Coff coff = offsetMap.get( name );
			return coff.loffset.get(coff.bestMatchIndex).a;
		}
		
		public int getAlignStop( String name ) {
			int i = getLastAlignIndex( name );
			List<Offset>	offsetList = offsetMap.get( name ).loffset;
			return offsetList.get(i).a;
		}
		
		public int getFirstAlignIndex( String name ) {
			return offsetMap.get( name ).bestMatchIndex;
		}
		
		public int getLastAlignIndex( String name ) {
			Coff 			coff = offsetMap.get( name );
			int 			i = coff.bestMatchIndex;
			List<Offset>	offsetList = coff.loffset;
			Offset 			lastoffset = offsetList.get( i++ );
			if( i < offsetList.size() ) {
				Offset offset = offsetList.get( i++ );
				int adiff = offset.a - lastoffset.a;
				int bdiff = offset.b - lastoffset.b;
				while( i < offsetList.size() && bdiff > 0 && bdiff > adiff-500 && bdiff < adiff+500 ) {
					lastoffset = offset;
					offset = offsetList.get( i++ );
					
					adiff = offset.a - lastoffset.a;
					bdiff = offset.b - lastoffset.b;
				}
				return i-2;
			}
			
			return i-1;
		}
		
		public int getDerivedLength( String name ) {
			return getDerivedStop( name ) - getDerivedStart( name );
		}
		
		public int getDerivedStop( String name ) {		
			int i = getLastAlignIndex( name );
			List<Offset>	offsetList = offsetMap.get( name ).loffset;
			Offset o = offsetList.get(i);
			return o.a + bb.limit() - o.b;
		}
		
		public int getDerivedStart( String name ) {
			Coff coff = offsetMap.get( name );
			Offset offset = coff.loffset.get( coff.bestMatchIndex );
			return offset.a - offset.b;
		}
		
		public int getLength() {
			return bb.limit();
		}
		
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
		Sequence		refseq;
		int				offval = 1;
		
		public SequencePane( final JTable seqtable, final List<Sequence> seqlist, Sequence refseq ) {
			super();
			
			this.refseq = refseq;
			//this.setBackground( Color.white );
			this.sequencelist = seqlist;
			this.seqtable = seqtable;
			int size = getLength();
			int seqtabHeight = 16;
			if( seqtable != null ) seqtable.getHeight();
			prefsize = new Dimension( 10*size, seqtabHeight );
			setSize( prefsize );
			setPreferredSize( prefsize );
		}
		
		public SequencePane( final JTable seqtable, final List<Sequence> seqlist, Sequence refseq, boolean grid ) {
			super();
			
			setShowGrid( grid );
			this.refseq = refseq;
			//this.setBackground( Color.white );
			this.sequencelist = seqlist;
			this.seqtable = seqtable;
			int size = getLength();
			int seqtabHeight = grid ? 32 : 16;
			if( seqtable != null ) seqtable.getHeight();
			prefsize = new Dimension( 10*size, seqtabHeight );
			setSize( prefsize );
			setPreferredSize( prefsize );
		}
		
		public int getUnitHeight() {
			return seqtable.getRowHeight();
		}
		
		public void setShowGrid( boolean grid ) {
			if( grid ) offval = 2;
			else offval = 1;
		}
		
		public int getLength() {
			int size = 0;
			for( Sequence seq : sequencelist ) {
				int end = seq.bb.limit() + seq.offset;
				if( end > size ) size = end;
			}
			if( refseq != null ) {
				int refend = refseq.offset + refseq.bb.limit();
				if( refend > size ) size = refend;
			}
			return size;
		}
		
		public Dimension getPreferredSize() {
			if( seqtable != null ) {
				prefsize.height = seqtable.getHeight();
			} else prefsize.height = offval > 1 ? 32 : 16;
			return prefsize;
		}
		
		public void paintComponent( Graphics g ) {
			super.paintComponent( g );
			Graphics2D g2 = (Graphics2D)g;
			g2.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
			Rectangle clip = g.getClipBounds();
			
			if( offval > 1 ) {
				int xstart = clip.x/10;
				int xstop = (clip.x+clip.width)/10;
				for( int x = xstart; x < xstop; x++ ) {
					if( x%10 == 0 ) {
						String s = Integer.toString(x);
						int strw = g2.getFontMetrics().stringWidth( s );
						g2.drawString( s, x*10-strw/2+5, 12 );
						g2.drawLine(x*10+5, 15, x*10+5, 20);
					} else {
						
					}
				}
			}
			
			if( seqtable != null ) {
				Sequence seq0 = refseq;//sequencelist.get(0);			
				int rh = seqtable.getRowHeight();
				
				int mx = 0;
				for( int y = 0; y < seqtable.getRowCount(); y++ ) {
					if( seqtable.isRowSelected(y) ) {
						g.setColor( Color.blue.darker() );
						g.fillRect(clip.x, y*rh, clip.width, rh);
					}
					int i = seqtable.convertRowIndexToModel(y);
					Sequence seq = sequencelist.get(i);
					
					//if( mx < seq.offset+seq.bb.limit() ) mx = seq.offset+seq.bb.limit();
					
					if( seq.offsetMap != null ) {
						int alignIndStart = seq.getFirstAlignIndex( seq0.name );
						int alignIndStop = seq.getLastAlignIndex( seq0.name );
						
						int alignStart = seq.getAlignStart( seq0.name );
						int alignStop = seq.getAlignStop( seq0.name );
						
						int seqstart = seq.getDerivedStart( seq0.name );
						int seqstop = seq.getDerivedStop( seq0.name );
						
						int xstart = Math.max( seqstart, clip.x/10 );
						int xstop = Math.min( seqstop, (clip.x+clip.width)/10 );
						//System.err.println( xstop );
						if( xstop > mx ) mx = xstop;
						
						//Offset los = null;
						Offset os = null;
						Offset nos = null;
						int dist = 0;
						for( int x = xstart; x < xstop; x++ ) {
							if( x < alignStart ) {
								g.setColor( Color.blue );
								g.drawLine(x*10, (y+offval)*rh, (x+1)*10, (y+offval)*rh);
								g.drawLine(x*10, (y+offval-1)*rh, (x+1)*10, (y+offval-1)*rh);
								//g.drawString("M", x*10, (y+offval)*rh);
							} else if( x > alignStop ) {
								g.setColor( Color.green );
								g.drawLine(x*10, (y+offval)*rh, (x+1)*10, (y+offval)*rh);
								g.drawLine(x*10, (y+offval-1)*rh, (x+1)*10, (y+offval-1)*rh);
								//g.drawString("M", x*10, (y+offval)*rh);
							} else {
								List<Offset>	offsetList = seq.offsetMap.get( seq0.name ).loffset;
								int f = alignIndStart;
								while( f < alignIndStop-1 && offsetList.get(f+1).a < x ) f++;
								
								if( os != offsetList.get(f) ) {
									if( nos != null && os != null ) {
										dist = (nos.b - os.b) - (nos.a - os.a);
										System.err.println( dist );
									}
									os = offsetList.get(f);
								}
								if( f+1 < offsetList.size() ) {
									nos = offsetList.get(f+1);
								}
								//Offset nos = null;
								
								int bind = x - os.a + os.b;
								
								if( bind >= 0 && bind < seq.bb.limit() && (nos == null || x - os.a <= nos.b - os.b ) ) {					
									byte b = seq.bb.get( bind );
									byte bc = 0;
									int xval = x - seq0.offset;
									if( xval < seq0.bb.limit() && xval >= 0 ) {
										bc = seq0.bb.get( xval );
									}
									if( b != bc ) {
										g.setColor( Color.red );
									} else {
										g.setColor( Color.black );
									}
									
									if( b == 'a' || b == 'A' ) g.drawString(this.a, x*10, (y+offval)*rh-3);
									else if( b == 'c' || b == 'C' ) g.drawString(this.c, x*10, (y+offval)*rh-3);
									else if( b == 'g' || b == 'G' ) g.drawString(this.g, x*10, (y+offval)*rh-3);
									else if( b == 't' || b == 'T' ) g.drawString(this.t, x*10, (y+offval)*rh-3);
									else g.drawString("N", x*10, (y+offval)*rh-3);
									
									if( x == os.a ) {
										g.drawLine(x*10-1, (y+offval-1)*rh-3, x*10-1, (y+offval)*rh+3);
										g.drawLine((x+1)*10-1, (y+offval)*rh+3, (x+1)*10+3, (y+offval)*rh+3);
									} else if( x == nos.a ) {
										g.drawLine((x+1)*10-1, (y+offval-1)*rh-3, (x+1)*10-1, (y+offval)*rh+3);
										g.drawLine((x+1)*10-1, (y+offval-1)*rh-3, (x+1)*10-5, (y+offval-1)*rh-3);
									}
								} else {
									g.setColor( Color.black );
									g.drawString("-", x*10, (y+offval)*rh-3);
								}
								
								if(nos == null || nos.a - os.a < nos.b - os.b ) {
									g.setColor( Color.black );
									g.drawLine(x*10, (y+offval)*rh, (x+1)*10, (y+offval)*rh);
									g.drawLine(x*10, (y+offval-1)*rh, (x+1)*10, (y+offval-1)*rh);
								}
								
								if( dist > 0 ) {
									g.setColor( Color.black );
									g.drawLine(x*10+2, (y+offval-1)*rh+2, (x+1)*10-2, (y+offval-1)*rh+2);
									g.drawLine(x*10+2, (y+offval)*rh-2, (x+1)*10-2, (y+offval)*rh-2);
									dist--;
								}
							}
						}
					}
				}
			} else {
				int y = 0;
				int rh = 16;
				for( Sequence seq : sequencelist ) {
					for( int x = Math.max( seq.offset, clip.x/10 ); x < Math.min( seq.offset+seq.bb.limit(), (clip.x+clip.width)/10 ); x++ ) {
						byte b = seq.bb.get( x - seq.offset );
						if( b == 'a' || b == 'A' ) g.drawString(this.a, x*10, (y+offval)*rh);
						else if( b == 'c' || b == 'C' ) g.drawString(this.c, x*10, (y+offval)*rh);
						else if( b == 'g' || b == 'G' ) g.drawString(this.g, x*10, (y+offval)*rh);
						else if( b == 't' || b == 'T' ) g.drawString(this.t, x*10, (y+offval)*rh);
						else g.drawString("N", x*10, (y+offval)*rh);
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
		long			clength;
		long			oldw = 0;
		List<int[]>		redlist = new ArrayList<int[]>();
		
		public Overview( JTable	table, SequencePane	pane, long clength ) {
			this.table = table;
			this.pane = pane;
			this.clength = clength;
		}
		
		private void updateMismatch( long w ) {
			if( oldw != w ) {
				redlist.clear();
				List<Sequence>	lseq = pane.sequencelist;
				Sequence		rseq = pane.refseq;
				for( int i = 0; i < lseq.size(); i++ ) {
					Sequence seq = lseq.get(i);
					
					if( seq.offsetMap != null ) {
						int seqstart = seq.getDerivedStart( rseq.name );
						int seqstop = seq.getDerivedStop( rseq.name );
						long x1 = ( (long)seqstart * w ) / clength;
						long x2 = ( ((long)seqstop) * w ) / clength;
						
						int[] bb = new int[(int)(x2-x1)/32+1];
						for( long x = x1+1; x < x2; x++ ) {
							int s1 = (int)( (x*clength) / w );
							int s2 = (int)( ((x+1)*clength) / w );
							
							boolean set = false;
							for( int k = s1; k < s2; k++ ) {
								int ind = k-seq.offset;
								if( ind < 0 || ind >= seq.bb.limit() ) continue;// System.err.println( "ind " + ind );
								if( k < 0 || k >= rseq.bb.limit() ) continue; //System.err.println( "rind " + k );
								if( rseq.bb.get(k) != seq.bb.get(ind) ) set = true;
							}
							int xd32 = (int)(x-x1)/32;
							int xm32 = (int)(x-x1)%32;
							if( set ) bb[xd32] = bb[xd32]|(1<<xm32);
							else bb[xd32] = ~(~bb[xd32]|(1<<xm32));
						}
						redlist.add( bb );
					}
				}
				
				oldw = w;
			}
		}
		
		public void setBounds( int x, int y, int w, int h ) {
			super.setBounds(x, y, w, h );
			
			updateMismatch( w );
		}
		
		public void paintComponent( Graphics g ) {
			super.paintComponent(g);
			
			long w = (long)this.getWidth();
			
			List<Sequence>	lseq = pane.sequencelist;
			g.setColor( Color.green.darker() );
			//long conslen = lseq.get(0).bb.limit();
			
			//int mx = 0;
			//int mx2 = 0;
			Sequence rseq = pane.refseq;
			for( int y = 0; y < table.getRowCount(); y++ ) {
				int i = table.convertRowIndexToModel( y );
				Sequence seq = lseq.get(i);
				
				if( seq.offsetMap != null ) {
					int seqstart = seq.getDerivedStart( rseq.name );
					int seqstop = seq.getDerivedStop( rseq.name );
					int x1 = (int)( ( (long)seqstart * w ) / clength );
					int x2 = (int)( ( ((long)seqstop) * w ) / clength );
					
					int y2 = 2*y;
					
					//if(x2>mx) mx = x2;
					//if(seqend>mx2) mx2 = seqend;
					//if( y == table.getRowCount()-1 ) System.err.println("erm "+x2 + "   " + (seq.offset+seq.bb.limit()) + "  " + clength + "  " + rseq.bb.limit());
					
					int[] val = redlist.get(i);
					for( int x = x1; x < x2; x++ ) {
						int xd32 = (x-x1)/32;
						int xm32 = (x-x1)%32;
						
						if( ( val[xd32]&(1<<xm32) ) != 0 ) g.setColor( Color.red );
						else g.setColor( Color.green );
						g.drawLine(x, y2, x+1, y2);
						g.drawLine(x, y2+1, x+1, y2+1);
					}
				}
			}
			
			Rectangle r = pane.getVisibleRect();
			g.setColor( color );
			
			int px = (int)( ((long)r.x*w)/(long)pane.getWidth() );
			int pw = Math.max( 2, (int)( ((long)r.width*w)/(long)pane.getWidth() ) );
			g.fillRect( px, 0, pw, this.getHeight() );
		}
	}
	
	public void initGui() throws IOException {
		JScrollPane	scrollpane = new JScrollPane();
		scrollpane.setBackground( Color.white );
		scrollpane.getViewport().setBackground( Color.white );
		
		final JTable		lefttable = new JTable();
		JScrollPane	leftpane = new JScrollPane();
		leftpane.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_NEVER );
		
		scrollpane.setRowHeaderView( lefttable );
		leftpane.setViewport( scrollpane.getRowHeader() );
		JSplitPane	subsplit = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT );
		subsplit.setRightComponent( scrollpane );
		subsplit.setLeftComponent( leftpane );
		
		//final List<Sequence> bbl = load( "/home/sigmar/fass/wholeHB27.fas", null );
		//final List<Sequence> lseq1 = load( "/home/sigmar/fass/wholeHB27.fas", null );
		//final List<Sequence> lseq2 = load( "/home/sigmar/fass/Strain346AllContigs.fas", "/home/sigmar/fass/out" );
		
		final List<Sequence> lseq2 = load( "/home/sigmar/fass/assembly1/454LargeContigs.fna", "/home/sigmar/fass/assembly2/454LargeContigs.fna", "/home/sigmar/fass/a12", "/home/sigmar/fass/a21" );
		
		//lseq1.addAll( lseq2 );
		seqpane = new SequencePane( lefttable, lseq2, lseq2.get(0) );
		topseq = new SequencePane( null, lseq2, null, true );
		topseq.setShowGrid( true );
		
		scrollpane.getVerticalScrollBar().setBlockIncrement( seqpane.getUnitHeight() );
		scrollpane.getHorizontalScrollBar().setBlockIncrement(10);
		
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
				return lseq2.size();
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				Sequence seq = lseq2.get(rowIndex);
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
				int r = lefttable.getSelectedRow();
				if( r >= 0 ) {
					Rectangle visrect = seqpane.getVisibleRect();
					visrect.x = 10*(Integer)lefttable.getValueAt(r, 1);
					seqpane.scrollRectToVisible( visrect );
				}
				seqpane.repaint();
			}
		});
		
		final FindDialog	fd = new FindDialog();
		AbstractAction action = new AbstractAction("Find") {
			@Override
			public void actionPerformed(ActionEvent e) {
				fd.setVisible( true );
			}
		};
		toolbar.add( action );
		
		ov = new Overview( lefttable, seqpane, lseq2.get(0).getLength() );
		ov.addMouseListener( new MouseAdapter() {
			public void mousePressed( MouseEvent e ) {
				int x = (int)(  ((long)seqpane.getWidth() * (long)e.getX()) / (long)ov.getWidth()  );
				int y = seqpane.getUnitHeight() * e.getY() / 2;
				Rectangle vr = seqpane.getVisibleRect();
				vr.x = x;
				vr.y = y - vr.height/2;
				seqpane.scrollRectToVisible( vr );
			}
		});
		splitpane.setTopComponent( subsplit );
		splitpane.setBottomComponent( ov );
		
		c.setLayout( new BorderLayout() );
		c.add( this.splitpane );
		c.add( this.toolbar, BorderLayout.NORTH );
	}
	
	public Map<String,Map<String,Coff>> offsetRead( String filename, String prefix, String subprefix ) throws IOException {
		Map<String,Map<String,Coff>>	offsetMap = new HashMap<String,Map<String,Coff>>();
		Map<String,Coff>				suboffsetMap = null;
		Coff							offsetList = null;//new ArrayList<Offset>();
		if( filename != null ) {
			File f = new File( filename );
			FileReader			fr = new FileReader( f );
			BufferedReader 		br = new BufferedReader( fr );
			
			String current = "";
			String line = br.readLine();
			while( line != null ) {
				if( line.startsWith(">") ) {
					if( current.length() > 0 ) offsetMap.put( prefix+current, suboffsetMap );
					current = line.substring(1).trim();
					suboffsetMap = new HashMap<String,Coff>();
					//offsetList = new ArrayList<Offset>();
				} else if( current.length() > 0 ) {
					String[] split = line.trim().split("[ \t]+");
					if( split[0].length() > 1 ) {
						String sv = split[0];
						int lv = Integer.parseInt(split[1]);
						int ls = Integer.parseInt(split[2]);
						int ll = Integer.parseInt(split[3]);
						
						if( suboffsetMap.containsKey(sv) ) {
							offsetList = suboffsetMap.get( sv );
						} else {
							offsetList = new Coff( 0, new ArrayList<Offset>() );
							suboffsetMap.put( subprefix+sv, offsetList );
						}
						
						offsetList.loffset.add( new Offset(lv,ls,ll) );
					}
				}
				
				line = br.readLine();
			}
			if( current.length() > 0 ) offsetMap.put( current, suboffsetMap );
			
			br.close();
			fr.close();
		}
		
		return offsetMap;
	}
	
	public List<Sequence> load( String fname1, String fname2, String foffset1, String foffset2 ) throws IOException {
		File 			f = new File( fname1 );
		ByteBuffer 		bb = ByteBuffer.allocate( (int)f.length() );
		FileInputStream fis = new FileInputStream( f );
		int 			r1 = fis.read( bb.array() );
		fis.close();
		
		List<Sequence>	lseq1 = fetchSeq( bb, "l1_" );
		
		f = new File( fname2 );
		bb = ByteBuffer.allocate( (int)f.length() );
		fis = new FileInputStream( f );
		int 			r2 = fis.read( bb.array() );
		fis.close();
		
		List<Sequence>	lseq2 = fetchSeq( bb, "l2_" );
		
		Map<String,Map<String,Coff>>	offsetMap2 = offsetRead( foffset1, "l2_", "l1_" );
		Map<String,Map<String,Coff>>	offsetMap1 = offsetRead( foffset2, "l1_", "l2_" );
		
		Coff 	off = null;
		for( Sequence seq : lseq1 ) {
			if( offsetMap1.containsKey(seq.name) ) {
				seq.offsetMap = offsetMap1.get(seq.name);
				for( String s : seq.offsetMap.keySet() ) {
					off = seq.offsetMap.get(s);
					Collections.sort( off.loffset );
					off.bestMatchIndex = checkLongestCons( off.loffset );
					if( off.bestMatchIndex < off.size() ) {
						Offset toff = off.getBestOffset();
						int loff = toff.a - toff.b;
						seq.offset = loff; // = new Sequence( seq.name, tb, loff, coff, off );
						
						if( off.size() == 0 ) off.add( new Offset(0,0,0) );
						/*seq.offsetMap = new HashMap<String,Coff>();
						Coff coff = new Coff( bestMatchIndex, off );
						seq.offsetMap.put("cons", coff);*/
					}
				}
			} else {
				System.err.println( "not existing " + seq.name );
			}
		}
		
		for( Sequence seq : lseq2 ) {
			if( offsetMap2.containsKey(seq.name) ) {
				seq.offsetMap = offsetMap1.get(seq.name);
				for( String s : seq.offsetMap.keySet() ) {
					off = seq.offsetMap.get(s);
					Collections.sort( off.loffset );
					off.bestMatchIndex = checkLongestCons( off.loffset );
					if( off.bestMatchIndex < off.size() ) {
						Offset toff = off.getBestOffset();
						int loff = toff.a - toff.b;
						seq.offset = loff;
						if( off.size() == 0 ) off.add( new Offset(0,0,0) );
					}
				}
			}
		}
		
		for( Sequence seq1 : lseq1 ) {
			for( String s : seq1.offsetMap.keySet() ) {
				Coff	loffset1 = seq1.offsetMap.get( s );
				seq1.getDerivedLength( s );
				//int v = checkLongestCons( loffset1 );
			}
		}
		
		lseq1.addAll( lseq2 );
		
		return lseq1;
	}
	
	public List<Sequence> load( String fname, String foffset ) throws IOException {
		File f = new File( fname );
		ByteBuffer bb = ByteBuffer.allocate( (int)f.length() );
		FileInputStream fis = new FileInputStream( f );
		int r = fis.read( bb.array() );
		
		Map<String,List<Offset>>	offsetMap = new HashMap<String,List<Offset>>();
		List<Offset>				offsetList = null;//new ArrayList<Offset>();
		if( foffset != null ) {
			f = new File( foffset );
			FileReader			fr = new FileReader( f );
			BufferedReader 		br = new BufferedReader( fr );
			
			String current = "";
			String line = br.readLine();
			while( line != null ) {
				if( line.startsWith(">") ) {
					if( current.length() > 0 ) offsetMap.put( current, offsetList );
					current = line.substring(1).trim();
					offsetList = new ArrayList<Offset>();
				} else if( current.length() > 0 ) {
					String[] split = line.trim().split("[ \t]+");
					if( split[0].length() > 1 ) {
						int lv = Integer.parseInt(split[0]);
						int ls = Integer.parseInt(split[1]);
						int ll = Integer.parseInt(split[2]);
						offsetList.add( new Offset(lv,ls,ll) );
						//offsetMap.put( current, lv-ls );
					} else {
						System.err.println( line );
					}
					
					//current = "";
				}
				
				line = br.readLine();
			}
			if( current.length() > 0 ) offsetMap.put( current, offsetList );
		}
		
		List<Sequence>	lseq = fetchSeq( bb, "" );
		
		List<Offset> 	off = null;
		for( Sequence seq : lseq ) {
			if( offsetMap.containsKey(seq.name) ) {
				off = offsetMap.get(seq.name);
				Collections.sort( off );
				int bestMatchIndex = checkLongestCons( off );
				if( bestMatchIndex < off.size() ) {
					Offset toff = off.get( bestMatchIndex );
					int loff = toff.a - toff.b;
					seq.offset = loff; // = new Sequence( seq.name, tb, loff, coff, off );
					if( off.size() == 0 ) off.add( new Offset(0,0,0) );
					seq.offsetMap = new HashMap<String,Coff>(); 
					
					Coff coff = new Coff( bestMatchIndex, off );
					seq.offsetMap.put("cons", coff);
				}
			}
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
	
	public List<Sequence> fetchSeq( ByteBuffer bb, String prefix ) {
		List<Sequence>	retseq = new ArrayList<Sequence>();
		
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
					
					Sequence seq = new Sequence( prefix+name, tb, 0, null );
					retseq.add( seq );
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
		
			Sequence seq = new Sequence( prefix+name, tb, 0, null );
			retseq.add( seq );
		}
		
		return retseq;
	}
	
	public int checkLongestCons( List<Offset>	offlist ) {
		int ret = 0;
		
		if( offlist != null ) {
			int count = 0;
			int subret = 0;
			int lastcount = 0;
			int prev = 0;
			int	aprev = 0;
			int i = 0;
			int maxind = 0;
			int maxval = 0;
			for( Offset off : offlist ) {
				int adiff = off.b - prev;
				int bdiff = off.a - aprev;
				
				if( off.c > maxval ) {
					maxval = off.c;
					maxind = i;
				}
				
				if( bdiff > 0 && bdiff > adiff-500 && bdiff < adiff+500 ) {
					count++;
				} else {
					if( count > lastcount ) {
						ret = subret;
						lastcount = count;
					}
					count = 0;
					
					subret = i;
				}
				
				prev = off.b;
				aprev = off.a;
				i++;
			}
			
			if( lastcount <= 1 ) {
				ret = maxind;
			}
		}
		
		return ret;
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
			frame.add( mummy.c );
			mummy.initGui();
			frame.setVisible( true );
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}