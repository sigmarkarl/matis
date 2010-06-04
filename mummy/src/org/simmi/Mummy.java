package org.simmi;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JApplet;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
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
	JSplitPane splitpane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
	JToolBar toolbar = new JToolBar();
	JComponent c = new JComponent() {};
	SequencePane seqpane;
	SequencePane topseq;
	Overview ov;
	JTextField textfield = new JTextField();

	// List<FindResult> findres = new ArrayList<FindResult>();

	public Mummy() {}

	/*
	 * class FindDialog extends JDialog { JTextArea textarea = new JTextArea();
	 * 
	 * public FindDialog() { JScrollPane scrollpane = new JScrollPane( textarea
	 * ); this.add( scrollpane ); } };
	 */

	class Offset implements Comparable<Offset> {
		int a;
		int b;
		int c;

		public Offset(int a, int b, int c) {
			this.a = a;
			this.b = b;
			this.c = c;
		}

		@Override
		public int compareTo(Offset o) {
			return this.a - o.a;
		}
	}

	class Coff implements Comparable<Coff> {
		public Coff(int bestMatchIndex, List<Offset> loffset) {
			this.bestMatchIndex = bestMatchIndex;
			this.loffset = loffset;
		}

		public int size() {
			return loffset.size();
		}

		public void add(Offset o) {
			loffset.add(o);
		}

		public Offset getBestOffset() {
			if (bestMatchIndex < loffset.size())
				return loffset.get(bestMatchIndex);

			return null;
		}

		boolean rc;
		int bestMatchIndex;
		List<Offset> loffset;

		Sequence seq1;
		Sequence seq2;

		@Override
		public int compareTo(Coff o) {
			return o.getAlignLength() - getAlignLength();
		}

		public int getAlignLength() {
			return getAlignStop() - getAlignStart();
		}

		public int getAlignCount() {
			return getLastAlignIndex() - getFirstAlignIndex() + 1;
		}

		public int getAlignStart() {
			return loffset.get(bestMatchIndex).a;
		}

		public int getAlignStop() {
			int i = getLastAlignIndex();
			Offset o = loffset.get(i);
			return o.a + o.c;
		}

		public int getFirstAlignIndex() {
			return bestMatchIndex;
		}

		public int getLastAlignIndex() {
			int i = bestMatchIndex;
			Offset lastoffset = loffset.get(i++);
			if (i < loffset.size()) {
				Offset offset = loffset.get(i++);
				int adiff = offset.a - lastoffset.a;
				int bdiff = offset.b - lastoffset.b;
				while (i < loffset.size() && bdiff > 0 && bdiff > adiff - gdiff && bdiff < adiff + gdiff) {
					lastoffset = offset;
					offset = loffset.get(i++);

					adiff = offset.a - lastoffset.a;
					bdiff = offset.b - lastoffset.b;
				}
				return i - 2;
			}
			return i - 1;
		}

		public int getDerivedStop(int limit) {
			int i = getLastAlignIndex();
			Offset o = loffset.get(i);
			return o.a + limit - o.b;
		}

		public int getDerivedStart() {
			Offset o = loffset.get(bestMatchIndex);
			return o.a - o.b;
		}
	}

	class Subsequence implements Comparable<Sequence> {
		ByteBuffer bb;
		String tag;
		private int offset;
		private int start;
		private int stop;
		private int length;
		Color color;
		boolean rc;

		public Subsequence(String tag, ByteBuffer bb, int offset, int start, int stop) {
			this.bb = bb;
			this.tag = tag;
			this.offset = offset;
			this.start = start;
			this.stop = stop;
			this.length = stop - start;
		}

		public void setColor(Color color) {
			this.color = color;
		}

		public void setReverseCompliment(boolean rc) {
			this.rc = rc;
		}

		public byte get(int index) {
			int rind = index + start;
			if (rind < 0 || rind >= bb.limit()) {
				System.err.println("erm");
				return 0;
			}
			return bb.get(rind);
		}

		public int getOffset() {
			return offset;
		}

		public int getStop() {
			return offset + length;
		}

		public int getLength() {
			return length;
		}

		@Override
		public int compareTo(Sequence o) {
			return offset - o.offset;
		}
	};

	public class Sequence implements Comparable<Sequence> {
		public Sequence(String name, ByteBuffer bb, int offset, Map<String, Coff> offsetMap, String gid) {
			/*
			 * if( offsetList == null ) offsetList = new ArrayList<Offset>();
			 * if( offsetList.size() == 0 ) { offsetList.add( new Offset(0,0,0)
			 * ); }
			 */

			this.name = name;
			this.bb = bb;
			this.offset = offset;
			this.offsetMap = offsetMap;
			this.setGid(gid);
			this.match = 0;

			this.lsubseq = new ArrayList<Subsequence>();
			this.findseq = new ArrayList<Subsequence>();
		}

		String name;
		ByteBuffer bb;
		int offset;
		Map<String, Coff> offsetMap;
		String gid = "";
		List<Subsequence> lsubseq;
		List<Subsequence> findseq;
		boolean rc;
		int match;
		int index;

		public String toString() {
			return name;
		}

		public boolean equals(Sequence seq) {
			return seq.name.equals(name);
		}

		public void addSubsequence(String tag, int start, int stop, int offset, Color c, boolean rc) {
			// Subsequence subseq = new Subsequence( ByteBuffer.wrap(
			// bb.array(), start, stop-start ), offset, stop-start );
			Subsequence subseq = new Subsequence(tag, bb, offset, start, stop);
			subseq.setColor(c);
			subseq.setReverseCompliment(rc);
			lsubseq.add(subseq);
		}

		public void addSubsequence(String tag, int start, int stop, int offset) {
			// Subsequence subseq = new Subsequence( ByteBuffer.wrap(
			// bb.array(), start, stop-start ), offset, stop-start );
			Subsequence subseq = new Subsequence(tag, bb, offset, start, stop);
			lsubseq.add(subseq);
		}

		public byte get(int i) {
			return bb.get(i);
		}

		public void find(String val) {
			byte[] bb = val.getBytes();
			findseq.clear();

			for (Subsequence subseq : lsubseq) {
				for (int i = subseq.start; i < subseq.stop - bb.length; i++) {
					boolean found = true;
					for (int count = 0; count < bb.length; count++) {
						byte b = bb[count];
						if (b != subseq.bb.get(i + count)) {
							found = false;
							break;
						}
					}
					if (found) {
						Subsequence nsub = new Subsequence("search result", subseq.bb, subseq.getOffset() + (i - subseq.start), i, i + bb.length);
						findseq.add(nsub);
						i += bb.length;
					}
				}
			}
		}

		public void setGid(String gid) {
			if( this.gid.equals("40") ) {
				System.err.println("off " + name);
			}
			
			if( gid.equals("0") ) {
				//System.err.println("new " + name);
			}
			
			this.gid = gid;
		}
		
		public void setIndex( int ind ) {
			this.index = ind;
		}
		
		public int getIndex() {
			return index;
		}

		public String getGid() {
			return gid;
		}

		public int getOffset() {
			return offset;
		}

		public void setOffset(int offset) {
			this.offset = offset;
		}

		public int getAlignStart(String name) {
			Coff coff = offsetMap.get(name);
			if (coff != null) {
				return coff.getAlignStart();
			}
			return 0;
		}

		public int getAlignStop(String name) {
			Coff coff = offsetMap.get(name);
			if (coff != null) {
				return coff.getAlignStop();
			}
			return 0;
		}

		public int getFirstAlignIndex(String name) {
			Coff coff = offsetMap.get(name);
			if (coff != null) {
				return coff.getFirstAlignIndex();
			}

			return 0;
		}

		public int getLastAlignIndex(String name) {
			Coff coff = offsetMap.get(name);
			if (coff != null) {
				return coff.getLastAlignIndex();
			}

			return 0;
		}

		public int getDerivedLength(String name) {
			return getDerivedStop(name) - getDerivedStart(name);
		}

		public int getDerivedStop(String name) {
			Coff coff = offsetMap.get(name);
			if (coff != null) {
				return coff.getDerivedStop(bb.limit());
			}
			return 0;
		}

		public int getDerivedStart(String name) {
			Coff coff = offsetMap.get(name);
			if (coff != null) {
				return coff.getDerivedStart();
			}
			return 0;
		}

		public int getStop() {
			return getOffset() + getLength();
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
		JTable seqtable;
		List<Sequence> sequencelist;
		Map<String, List<Sequence>> seqgroups;
		final String a = "A";
		final String c = "C";
		final String g = "G";
		final String t = "T";
		Dimension prefsize;
		Sequence refseq;
		int offval = 1;

		public SequencePane(final JTable seqtable, final List<Sequence> seqlist, Sequence refseq) {
			super();

			this.refseq = refseq;
			// this.setBackground( Color.white );
			this.sequencelist = seqlist;
			initGroups();
			this.seqtable = seqtable;
			int size = getLength();
			int seqtabHeight = 16;
			if (seqtable != null)
				seqtable.getHeight();
			prefsize = new Dimension(10 * size, seqtabHeight);
			setSize(prefsize);
			setPreferredSize(prefsize);
		}

		public SequencePane(final JTable seqtable, final List<Sequence> seqlist, Sequence refseq, boolean grid) {
			super();

			setShowGrid(grid);
			this.refseq = refseq;
			// this.setBackground( Color.white );
			this.sequencelist = seqlist;
			initGroups();
			this.seqtable = seqtable;
			int size = getLength();
			int seqtabHeight = grid ? 32 : 16;
			if (seqtable != null)
				seqtable.getHeight();
			prefsize = new Dimension(10 * size, seqtabHeight);
			setSize(prefsize);
			setPreferredSize(prefsize);
		}

		public void initGroups() {
			seqgroups = new HashMap<String, List<Sequence>>();
			for (Sequence seq : this.sequencelist) {
				List<Sequence> lseq;
				if (seqgroups.containsKey(seq.getGid())) {
					lseq = seqgroups.get(seq.getGid());
				} else {
					lseq = new ArrayList<Sequence>();
					seqgroups.put(seq.getGid(), lseq);
				}
				lseq.add(seq);
			}
		}

		public void find(String val) {
			for (Sequence seq : sequencelist) {
				seq.find(val);
			}
			this.repaint();
		}

		public int getUnitHeight() {
			return seqtable.getRowHeight();
		}

		public void setShowGrid(boolean grid) {
			if (grid)
				offval = 2;
			else
				offval = 1;
		}

		public int getLength() {
			int size = 0;
			for (Sequence seq : sequencelist) {
				int end = seq.bb.limit() + seq.getOffset();
				if (end > size)
					size = end;
			}
			if (refseq != null) {
				int refend = refseq.getOffset() + refseq.bb.limit();
				if (refend > size)
					size = refend;
			}
			return size;
		}

		public Dimension getPreferredSize() {
			if (seqtable != null) {
				prefsize.height = seqtable.getHeight();
			} else
				prefsize.height = offval > 1 ? 32 : 16;
			return prefsize;
		}

		Color c1 = new Color(200, 100, 100, 100);
		Color c2 = new Color(100, 100, 200, 100);
		Color c3 = new Color(50, 200, 50, 100);

		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			Rectangle clip = g.getClipBounds();

			if (offval > 1) {
				int xstart = clip.x / 10;
				int xstop = (clip.x + clip.width) / 10;
				for (int x = xstart; x < xstop; x++) {
					if (x % 10 == 0) {
						String s = Integer.toString(x);
						int strw = g2.getFontMetrics().stringWidth(s);
						g2.drawString(s, x * 10 - strw / 2 + 5, 12);
						g2.drawLine(x * 10 + 5, 15, x * 10 + 5, 20);
					} else {

					}
				}
			}

			if (seqtable != null && refseq != null) {
				Sequence seq0 = refseq;// sequencelist.get(0);
				int rh = seqtable.getRowHeight();

				int mx = 0;
				for (int y = 0; y < seqtable.getRowCount(); y++) {
					if (seqtable.isRowSelected(y)) {
						g.setColor(Color.blue.darker());
						g.fillRect(clip.x, y * rh, clip.width, rh);
					}
					int i = seqtable.convertRowIndexToModel(y);
					Sequence seq = sequencelist.get(i);

					// if( mx < seq.offset+seq.bb.limit() ) mx =
					// seq.offset+seq.bb.limit();

					if (seq.offsetMap != null) {
						int alignIndStart = seq.getFirstAlignIndex(seq0.name);
						int alignIndStop = seq.getLastAlignIndex(seq0.name);

						int alignStart = seq.getAlignStart(seq0.name);
						int alignStop = seq.getAlignStop(seq0.name);

						int seqstart = seq.getDerivedStart(seq0.name);
						int seqstop = seq.getDerivedStop(seq0.name);

						int xstart = Math.max(seqstart, clip.x / 10);
						int xstop = Math.min(seqstop, (clip.x + clip.width) / 10);
						// System.err.println( xstop );
						if (xstop > mx)
							mx = xstop;

						// Offset los = null;
						Offset os = null;
						Offset nos = null;
						int dist = 0;
						for (int x = xstart; x < xstop; x++) {
							if (x < alignStart) {
								g.setColor(Color.blue);
								g.drawLine(x * 10, (y + offval) * rh, (x + 1) * 10, (y + offval) * rh);
								g.drawLine(x * 10, (y + offval - 1) * rh, (x + 1) * 10, (y + offval - 1) * rh);
								// g.drawString("M", x*10, (y+offval)*rh);
							} else if (x > alignStop) {
								g.setColor(Color.green);
								g.drawLine(x * 10, (y + offval) * rh, (x + 1) * 10, (y + offval) * rh);
								g.drawLine(x * 10, (y + offval - 1) * rh, (x + 1) * 10, (y + offval - 1) * rh);
								// g.drawString("M", x*10, (y+offval)*rh);
							} else {
								List<Offset> offsetList = seq.offsetMap.get(seq0.name).loffset;
								int f = alignIndStart;
								while (f < alignIndStop - 1 && offsetList.get(f + 1).a < x)
									f++;

								if (os != offsetList.get(f)) {
									if (nos != null && os != null) {
										dist = (nos.b - os.b) - (nos.a - os.a);
										System.err.println("dist " + dist);
									}
									os = offsetList.get(f);
								}
								if (f + 1 < offsetList.size()) {
									nos = offsetList.get(f + 1);
								}
								// Offset nos = null;

								int bind = x - os.a + os.b;

								if (bind >= 0 && bind < seq.bb.limit() && (nos == null || x - os.a <= nos.b - os.b)) {
									byte b = seq.bb.get(bind);
									byte bc = 0;
									int xval = x - seq0.getOffset();
									if (xval < seq0.bb.limit() && xval >= 0) {
										bc = seq0.bb.get(xval);
									}
									if (b != bc) {
										g.setColor(Color.red);
									} else {
										g.setColor(Color.black);
									}

									if (b == 'a' || b == 'A')
										g.drawString(this.a, x * 10, (y + offval) * rh - 3);
									else if (b == 'c' || b == 'C')
										g.drawString(this.c, x * 10, (y + offval) * rh - 3);
									else if (b == 'g' || b == 'G')
										g.drawString(this.g, x * 10, (y + offval) * rh - 3);
									else if (b == 't' || b == 'T')
										g.drawString(this.t, x * 10, (y + offval) * rh - 3);
									else
										g.drawString("N", x * 10, (y + offval) * rh - 3);

									if (x == os.a) {
										g.drawLine(x * 10 - 1, (y + offval - 1) * rh - 3, x * 10 - 1, (y + offval) * rh + 3);
										g.drawLine((x + 1) * 10 - 1, (y + offval) * rh + 3, (x + 1) * 10 + 3, (y + offval) * rh + 3);
									} else if (x == nos.a) {
										g.drawLine((x + 1) * 10 - 1, (y + offval - 1) * rh - 3, (x + 1) * 10 - 1, (y + offval) * rh + 3);
										g.drawLine((x + 1) * 10 - 1, (y + offval - 1) * rh - 3, (x + 1) * 10 - 5, (y + offval - 1) * rh - 3);
									}
								} else {
									g.setColor(Color.black);
									g.drawString("-", x * 10, (y + offval) * rh - 3);
								}

								if (nos == null || nos.a - os.a < nos.b - os.b) {
									g.setColor(Color.black);
									g.drawLine(x * 10, (y + offval) * rh, (x + 1) * 10, (y + offval) * rh);
									g.drawLine(x * 10, (y + offval - 1) * rh, (x + 1) * 10, (y + offval - 1) * rh);
								}

								if (dist > 0) {
									g.setColor(Color.black);
									g.drawLine(x * 10 + 2, (y + offval - 1) * rh + 2, (x + 1) * 10 - 2, (y + offval - 1) * rh + 2);
									g.drawLine(x * 10 + 2, (y + offval) * rh - 2, (x + 1) * 10 - 2, (y + offval) * rh - 2);
									dist--;
								}
							}
						}
					} else {
						drawNormal(g2, seq, clip, y);
					}
				}
			} else if (seqtable != null) {
				int rh = seqtable.getRowHeight();

				// Set<String> gidone = new HashSet<String>();
				for (int y = 0; y < seqtable.getRowCount(); y++) {
					if (seqtable.isRowSelected(y)) {
						g.setColor(Color.blue.darker());
						g.fillRect(clip.x, y * rh, clip.width, rh);
						g.setColor(Color.black);
					}

					int i = seqtable.convertRowIndexToModel(y);
					Sequence seq = sequencelist.get(i);

					int subi = 0;

					if (seq.lsubseq != null) {
						for (Subsequence subseq : seq.lsubseq) {
							// System.err.println( "seqlens " + seq.bb.limit() +
							// "  " + subseq.bb.limit() );

							int xstart = Math.max(subseq.getOffset(), clip.x / 10);
							int xstop = Math.min(subseq.getStop(), (clip.x + clip.width) / 10);

							List<Sequence> lseq = seqgroups.get(seq.getGid());
							for (int x = xstart; x < xstop; x++) {
								byte b = subseq.get(x - subseq.getOffset());

								if (subi % 2 == 0) {
									g.setColor(c1);
								} else {
									g.setColor(c2);
								}
								g.fillRect(x * 10, (y) * rh, 10, rh);

								g.setColor(Color.black);
								if (x == subseq.getOffset()) {
									g.drawString(subseq.getOffset() + "", x * 10, y * rh);
								}

								if (lseq != null) {
									for (Sequence gseq : lseq) {
										for (Subsequence sgseq : gseq.lsubseq) {
											if (x >= sgseq.getOffset() && x < sgseq.getStop()) {
												byte nb = sgseq.get(x - sgseq.getOffset());
												if (b != nb) {
													g.setColor(Color.red);
													break;
												}
											}
										}
									}
								}

								if (b == 'a' || b == 'A')
									g.drawString(this.a, x * 10, (y + offval) * rh - 3);
								else if (b == 'c' || b == 'C')
									g.drawString(this.c, x * 10, (y + offval) * rh - 3);
								else if (b == 'g' || b == 'G')
									g.drawString(this.g, x * 10, (y + offval) * rh - 3);
								else if (b == 't' || b == 'T')
									g.drawString(this.t, x * 10, (y + offval) * rh - 3);
								else
									g.drawString("N", x * 10, (y + offval) * rh - 3);
							}

							subi++;
						}
					} else {
						drawNormal(g, seq, clip, y);
					}

					for (Subsequence subseq : seq.findseq) {
						int xstart = Math.max(subseq.getOffset(), clip.x / 10);
						int xstop = Math.min(subseq.getStop(), (clip.x + clip.width) / 10);

						if (xstop > xstart) {
							g.setColor(c3);
							g.fillRect(xstart * 10, (y) * rh, 10 * (xstop - xstart), rh);
						}
					}

					/*
					 * if( !gidone.contains( seq.gid ) ) { gidone.add( seq.gid
					 * ); //List<Sequence> lgseq = this.seqgroups.get( seq.gid
					 * );
					 * 
					 * int xstart = clip.x/10;//Math.max( seq.getOffset(),
					 * clip.x/10 ); int xstop =
					 * (clip.x+clip.width)/10;//Math.min(
					 * seq.offset+seq.bb.limit(), (clip.x+clip.width)/10 );
					 * 
					 * List<Sequence> lseq = seqgroups.get(seq.gid); for( int x
					 * = xstart; x < xstop; x++ ) { //byte b = seq.bb.get( x -
					 * seq.getOffset() ); g.setColor( Color.black ); if( lseq !=
					 * null ) { for( Sequence subseq : lseq ) { if( x >=
					 * subseq.offset && x < subseq.offset+subseq.bb.limit() ) {
					 * byte nb = subseq.bb.get( x - subseq.getOffset() ); if( b
					 * != nb ) { g.setColor( Color.red ); break; } } } } } }
					 */
				}
				// gidone.clear();
			} else {
				int y = 0;
				for (Sequence seq : sequencelist) {
					drawNormal(g, seq, clip, y++);
				}
			}
			if (ov != null)
				ov.repaint();
		}

		public byte revComp(byte b) {
			if (b == 'A')
				return 'T';
			else if (b == 'C')
				return 'G';
			else if (b == 'G')
				return 'C';
			else if (b == 'T')
				return 'A';
			else if (b == 'a')
				return 't';
			else if (b == 'c')
				return 'g';
			else if (b == 'g')
				return 'c';
			else if (b == 't')
				return 'a';
			return 'N';
		}

		public void drawNormal(Graphics g, Sequence seq, Rectangle clip, int y) {
			int rh = 16;
			int[] xx1 = { 0, 10, 0 };
			int[] xx2 = { 10, 0, 10 };
			int[] yy = { 0, 5, 10 };

			if (seq.lsubseq != null && seq.lsubseq.size() > 0) {
				for (Subsequence subseq : seq.lsubseq) {
					for (int x = Math.max(subseq.getOffset(), clip.x / 10); x < Math.min(subseq.getStop(), (clip.x + clip.width) / 10); x++) {
						if (subseq.color != null) {
							g.setColor(subseq.color);
							g.translate(x * 10 - 1, (y + offval - 1) * rh + 3);
							if (subseq.rc) {
								g.fillPolygon(xx2, yy, 3); // g.fillRect(x*10,
															// (y+offval-1)*rh,
															// 10, rh);
							} else {
								g.fillPolygon(xx1, yy, 3);
							}
							g.translate(-x * 10 + 1, -(y + offval - 1) * rh - 3);
							g.setColor(Color.black);
						}

						if ((x - subseq.getOffset()) % 100 == 0) {
							g.setColor(Color.white);
							int strlen = g.getFontMetrics().stringWidth(subseq.tag);
							g.fillRect(x * 10, (y + offval - 2) * rh, strlen, rh);
							g.setColor(Color.black);
							g.drawString(subseq.tag, x * 10, (y + offval - 1) * rh - 4);
						}

						byte b;
						if (seq.rc) {
							b = revComp(subseq.bb.get(x - subseq.getOffset()));
						} else {
							b = subseq.bb.get(x - subseq.getOffset());
						}

						g.setColor(Color.black);
						List<Sequence> lseq = seqgroups.get(seq.getGid());
						if (lseq != null) {
							for (Sequence gseq : lseq) {
								for (Subsequence sgseq : gseq.lsubseq) {
									if (x >= sgseq.getOffset() && x < sgseq.getStop()) {
										byte nb = sgseq.get(x - sgseq.getOffset());
										if (b != nb) {
											g.setColor(Color.red);
											break;
										}
									}
								}
							}
						}

						if (b == 'a' || b == 'A')
							g.drawString(this.a, x * 10, (y + offval) * rh - 3);
						else if (b == 'c' || b == 'C')
							g.drawString(this.c, x * 10, (y + offval) * rh - 3);
						else if (b == 'g' || b == 'G')
							g.drawString(this.g, x * 10, (y + offval) * rh - 3);
						else if (b == 't' || b == 'T')
							g.drawString(this.t, x * 10, (y + offval) * rh - 3);
						else
							g.drawString("N", x * 10, (y + offval) * rh - 3);
					}
				}
			} else {
				int allmatch = 0;
				for (int x = Math.max(seq.getOffset(), clip.x / 10); x < Math.min(seq.getOffset() + seq.bb.limit(), (clip.x + clip.width) / 10); x++) {
					byte b;
					// = seq.bb.get( x - seq.getOffset() );
					if (seq.rc) {
						b = revComp(seq.bb.get(seq.bb.limit() - (x - seq.getOffset() + 1)));
					} else {
						b = seq.bb.get(x - seq.getOffset());
					}

					g.setColor(Color.black);
					List<Sequence> lseq = seqgroups.get(seq.getGid());
					
					boolean nomatch = false;
					if (lseq != null) {
						boolean match = false;
						for (Sequence gseq : lseq) {
							if (seq != gseq && x >= gseq.getOffset() && x < gseq.getStop()) {
								byte nb;// = gseq.get( x - gseq.getOffset() );
								if (gseq.rc) {
									nb = revComp(gseq.bb.get(gseq.bb.limit() - (x - gseq.getOffset() + 1)));
								} else {
									nb = gseq.bb.get(x - gseq.getOffset());
								}
								
								if (b != nb && b != nb - 32 && b - 32 != nb) {
									nomatch = true;
									break;
								}/*else if( !match ) {
							
									allmatch++;
									match = true;
								}
								
								if( match && nomatch ) {
									break;
								}*/
							}
						}
						
						/*if( !match ) allmatch = 0;
						
						if( nomatch && allmatch < 10 ) {
							g.setColor( Color.red );
						}*/
					}

					if( b > 95 && nomatch ) g.setColor( Color.red );
					
					if (b == 'a' || b == 'A')
						g.drawString(this.a, x * 10, (y + offval) * rh - 3);
					else if (b == 'c' || b == 'C')
						g.drawString(this.c, x * 10, (y + offval) * rh - 3);
					else if (b == 'g' || b == 'G')
						g.drawString(this.g, x * 10, (y + offval) * rh - 3);
					else if (b == 't' || b == 'T')
						g.drawString(this.t, x * 10, (y + offval) * rh - 3);
					else
						g.drawString("N", x * 10, (y + offval) * rh - 3);
				}
			}
		}
	}

	class Overview extends JComponent {
		JTable table;
		SequencePane pane;
		Color color = new Color(150, 150, 150, 150);
		long clength;
		long oldw = 0;
		List<int[]> redlist = new ArrayList<int[]>();

		public Overview(JTable table, SequencePane pane, long clength) {
			this.table = table;
			this.pane = pane;
			this.clength = clength;
		}

		private void updateMismatch(long w) {
			if (oldw != w && pane.refseq != null) {
				redlist.clear();
				List<Sequence> lseq = pane.sequencelist;
				Sequence rseq = pane.refseq;
				for (int i = 0; i < lseq.size(); i++) {
					Sequence seq = lseq.get(i);

					if (seq.offsetMap != null) {
						int seqstart = seq.getDerivedStart(rseq.name);
						int seqstop = seq.getDerivedStop(rseq.name);
						long x1 = ((long) seqstart * w) / clength;
						long x2 = (((long) seqstop) * w) / clength;

						int[] bb = new int[(int) (x2 - x1) / 32 + 1];
						for (long x = x1 + 1; x < x2; x++) {
							int s1 = (int) ((x * clength) / w);
							int s2 = (int) (((x + 1) * clength) / w);

							boolean set = false;
							for (int k = s1; k < s2; k++) {
								int ind = k - seq.getOffset();
								if (ind < 0 || ind >= seq.bb.limit())
									continue; // System.err.println( "ind " +
												// ind );
								if (k < 0 || k >= rseq.bb.limit())
									continue; // System.err.println( "rind " + k
												// );
								if (rseq.bb.get(k) != seq.bb.get(ind))
									set = true;
							}
							int xd32 = (int) (x - x1) / 32;
							int xm32 = (int) (x - x1) % 32;
							if (set)
								bb[xd32] = bb[xd32] | (1 << xm32);
							else
								bb[xd32] = ~(~bb[xd32] | (1 << xm32));
						}
						redlist.add(bb);
					}
				}

				oldw = w;
			}
		}

		public void setBounds(int x, int y, int w, int h) {
			super.setBounds(x, y, w, h);

			updateMismatch(w);
		}

		public void paintComponent(Graphics g) {
			super.paintComponent(g);

			long w = (long) this.getWidth();
			long h = (long) this.getHeight();

			List<Sequence> lseq = pane.sequencelist;
			g.setColor(Color.green.darker());
			// long conslen = lseq.get(0).bb.limit();

			// int mx = 0;
			// int mx2 = 0;
			Sequence rseq = pane.refseq;
			if (rseq != null) {
				for (int y = 0; y < table.getRowCount(); y++) {
					int i = table.convertRowIndexToModel(y);
					Sequence seq = lseq.get(i);

					if (seq.offsetMap != null) {
						int seqstart = seq.getDerivedStart(rseq.name);
						int seqstop = seq.getDerivedStop(rseq.name);
						int x1 = (int) (((long) seqstart * w) / clength);
						int x2 = (int) ((((long) seqstop) * w) / clength);

						int y2 = 2 * y;

						// if(x2>mx) mx = x2;
						// if(seqend>mx2) mx2 = seqend;
						// if( y == table.getRowCount()-1 )
						// System.err.println("erm "+x2 + "   " +
						// (seq.offset+seq.bb.limit()) + "  " + clength + "  " +
						// rseq.bb.limit());

						int[] val = redlist.get(i);
						for (int x = x1; x < x2; x++) {
							int xd32 = (x - x1) / 32;
							int xm32 = (x - x1) % 32;

							if ((val[xd32] & (1 << xm32)) != 0)
								g.setColor(Color.red);
							else
								g.setColor(Color.green);
							g.drawLine(x, y2, x + 1, y2);
							g.drawLine(x, y2 + 1, x + 1, y2 + 1);
						}
					}
				}
			} else {
				for (int y = 0; y < table.getRowCount(); y++) {
					int i = table.convertRowIndexToModel(y);
					Sequence seq = lseq.get(i);

					if (seq.lsubseq != null) {
						for (Subsequence subseq : seq.lsubseq) {
							int x1 = (int) (((long) subseq.getOffset() * w) / clength);
							int x2 = Math.max(x1 + 1, (int) (((long) subseq.getStop() * w) / clength));
							int y1 = (int) ((y * h) / table.getRowCount());
							int y2 = (int) (((y + 1) * h) / table.getRowCount());

							g.setColor(Color.green);

							for (int yy = y1; yy <= y2; yy++) {
								g.drawLine(x1, yy, x2, yy);
							}
						}
					} else {
						// int seqstart = seq.getDerivedStart( rseq.name );
						// int seqstop = seq.getDerivedStop( rseq.name );
						int x1 = (int) (((long) seq.offset * w) / clength);
						int x2 = (int) ((((long) seq.getOffset() + seq.getLength()) * w) / clength);
						int y1 = (int) ((y * h) / table.getRowCount());
						int y2 = (int) (((y + 1) * h) / table.getRowCount());

						g.setColor(Color.green);

						for (int yy = y1; yy <= y2; yy++) {
							g.drawLine(x1, yy, x2, yy);
						}
					}

					if (seq.findseq != null) {
						for (Subsequence subseq : seq.findseq) {
							int x1 = (int) (((long) subseq.getOffset() * w) / clength);
							int x2 = Math.max(x1 + 1, (int) (((long) subseq.getStop() * w) / clength));
							int y1 = (int) ((y * h) / table.getRowCount());
							int y2 = (int) (((y + 1) * h) / table.getRowCount());

							g.setColor(Color.blue);
							for (int yy = y1; yy <= y2; yy++) {
								g.drawLine(x1, yy, x2, yy);
							}
						}
					}
				}
			}

			Rectangle r = pane.getVisibleRect();
			g.setColor(color);

			// h = 2*table.getRowCount();

			int px = (int) (((long) r.x * w) / (long) pane.getWidth());
			int pw = Math.max(2, (int) (((long) r.width * w) / (long) pane.getWidth()));
			int py = pane.getHeight() == 0 ? 0 : (int) (((long) r.y * h) / pane.getHeight());
			int ph = pane.getHeight() == 0 ? 100 : Math.max(1, (int) (((long) r.height * h) / pane.getHeight()));
			g.fillRect(px, 0, pw, this.getHeight());

			g.setColor(Color.black);
			g.fillRect(px, py, pw, ph);
		}
	}

	public List<Sequence> joinSeq(String name, String gid, List<Sequence> lseq) {
		List<Sequence> rseq = new ArrayList<Sequence>();

		int size = 0;
		for (Sequence seq : lseq) {
			size += seq.bb.limit();
		}
		ByteBuffer bb = ByteBuffer.allocate(size);
		size = 0;
		for (Sequence seq : lseq) {
			// int length = seq.bb.limit();
			// bb.put( seq.bb.array(), size, length );
			// size += length;
			bb.put(seq.bb.array());
		}
		Sequence seq = new Sequence(name, bb, 0, null, gid);
		size = 0;

		int i = 0;
		for (Sequence nseq : lseq) {
			int length = nseq.bb.limit();

			Color color = i % 2 == 0 ? Color.red : Color.blue;
			if (nseq.offset == -1)
				seq.addSubsequence(nseq.name, size, size + length, size, color, i % 2 == 0);
			else
				seq.addSubsequence(nseq.name, size, size + length, nseq.offset, color, i % 2 == 0);
			size += length;
			i++;
		}

		rseq.add(seq);

		return rseq;
	}

	public void calcSubseq() {
		for (String s : seqpane.seqgroups.keySet()) {
			List<Sequence> lseq = seqpane.seqgroups.get(s);
			Collections.sort(lseq);

			for (Sequence seq : lseq) {
				// System.err.println( seq.gid + "  " + seq.offset );

				if (seq.offsetMap != null) {
					int off = 0;
					for (Sequence seq2 : lseq) {
						if (seq != seq2) {
							Coff coff = seq.offsetMap.get(seq2.name);

							if (coff != null) {
								int fi = seq.getFirstAlignIndex(seq2.name);
								int li = seq.getLastAlignIndex(seq2.name);

								Offset fo = coff.loffset.get(fi);
								seq.addSubsequence("subsequence", 0, Math.min(fo.b, seq.bb.limit()), seq.offset + off);
								for (int i = fi; i < li; i++) {
									// Offset fo = coff.loffset.get(i);
									Offset no = coff.loffset.get(i + 1);

									int boff = Math.abs(no.b - fo.b);
									int aoff = Math.abs(no.a - fo.a);
									int addon = boff < aoff ? aoff - boff : 0;
									off += addon;

									if (seq.getGid().equals("12") && addon > 0) {
										System.err.println((i - fi) + "  " + fo.b + "  " + off);
									}

									int start = fo.b;
									int stop = no.b;// fo.b + no.b;
													// //Math.max(aoff, boff);

									if (start < seq.bb.limit() && stop <= seq.bb.limit() + 2 && start > 0 && stop > start) {
										seq.addSubsequence("subsequence", start, Math.min(stop, seq.bb.limit()), seq.offset + start + off);
									} else {
										System.err.println("not added " + start + "  " + seq.bb.limit() + "  " + stop);
									}

									fo = no;
								}
								Offset lo = coff.loffset.get(li);
								seq.addSubsequence("subsequence", lo.b, seq.bb.limit(), seq.offset + lo.b + off);
							} else {
								// System.err.println( seq2.name + "   " +
								// seq.name );
							}
						}
					}
				} else {
					seq.addSubsequence("sequence", 0, seq.bb.limit(), seq.offset);
				}
			}
		}
	}

	public void initGui() throws IOException {
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

		JScrollPane scrollpane = new JScrollPane();
		scrollpane.setBackground(Color.white);
		scrollpane.getViewport().setBackground(Color.white);

		final JTable lefttable = new JTable();
		JScrollPane leftpane = new JScrollPane();
		leftpane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);

		scrollpane.setRowHeaderView(lefttable);
		leftpane.setViewport(scrollpane.getRowHeader());
		JSplitPane subsplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		subsplit.setRightComponent(scrollpane);
		subsplit.setLeftComponent(leftpane);

		// final List<Sequence> bbl = load( "/home/sigmar/fass/wholeHB27.fas",
		// null );
		// final List<Sequence> lseq1 = load( "/home/sigmar/fass/wholeHB27.fas",
		// null );
		// final List<Sequence> lseq2 = load(
		// "/home/sigmar/fass/Strain346AllContigs.fas", "/home/sigmar/fass/out"
		// );		 

		// final List<Sequence> lseq2 = load(
		// "/home/sigmar/fass/assembly2/454LargeContigs.fna",
		// "/home/sigmar/fass/assembly3/454LargeContigs.fna",
		// "/home/sigmar/fass/a23", "/home/sigmar/fass/a32" );

		// final List<Sequence> lseq2 = load( "/home/sigmar/fass/seq_2120.fa",
		// null );
		// final List<Sequence> lseq1 = loadWithGbkFeatures(
		// "/home/sigmar/fass/seq_2120.fa", "/home/sigmar/fass/2120.gbk" );

		//final List<Sequence> lseq1 = load( "/home/sigmar/fass/seq_4063.fa",
		// null );
		
		//final List<Sequence> lseq2 = loadWithGbkFeatures( "/home/sigmar/fass/seq_4063.fa", "/home/sigmar/fass/4063.gbk" );

		// final List<Sequence> lseq2 = load(
		// "/home/sigmar/fass/assembly1/454LargeContigs.fna" );
		// final List<Sequence> lseq2 = load(
		// "/home/sigmar/fass/assembly2/454LargeContigs.fna",
		// "/home/sigmar/fass/ab12" );
		// final List<Sequence> lseq1 = joinSeq( "consensus", "gid", lseq2 );
		// lseq1.addAll( lseq2 );

		// seqpane = new SequencePane( lefttable, lseq2, lseq1.get(0) );

		// lseq2.retainAll( seqset );
		
		/*for (String seqname : seqset.keySet()) {
			Sequence seq = seqset.get(seqname);
			lseq2.add(seq);
		}*/

		/*
		 * for( Sequence seq : lseq2 ) { System.err.println( "name " + seq.name
		 * ); }
		 */
		
		final List<Sequence>	lseq2 = Erfitt.stuff( this );

		seqpane = new SequencePane(lefttable, lseq2, null);
		topseq = new SequencePane(null, lseq2, null, true);
		topseq.setShowGrid(true);

		// calcSubseq();

		scrollpane.getVerticalScrollBar().setBlockIncrement(seqpane.getUnitHeight());
		scrollpane.getHorizontalScrollBar().setBlockIncrement(500);

		scrollpane.setViewportView(seqpane);
		scrollpane.setColumnHeaderView(topseq);
		lefttable.setAutoCreateRowSorter(true);
		lefttable.setModel(new TableModel() {
			@Override
			public void addTableModelListener(TableModelListener l) {
			}

			@Override
			public Class<?> getColumnClass(int columnIndex) {
				if (columnIndex == 1 || columnIndex == 2 || columnIndex == 4 || columnIndex == 5)
					return Integer.class;

				return String.class;
			}

			@Override
			public int getColumnCount() {
				return 6;
			}

			@Override
			public String getColumnName(int columnIndex) {
				if (columnIndex == 0)
					return "Contig name";
				else if (columnIndex == 1)
					return "Length";
				else if (columnIndex == 2)
					return "Offset";
				else if (columnIndex == 3)
					return "Group";
				else if (columnIndex == 4)
					return "Match";
				else if (columnIndex == 5)
					return "Index";
				return "";
			}

			@Override
			public int getRowCount() {
				return lseq2.size();
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				Sequence seq = lseq2.get(rowIndex);
				if (columnIndex == 0)
					return seq.name + (seq.rc ? " Reverse" : "");
				else if (columnIndex == 1)
					return seq.bb.limit();
				else if (columnIndex == 2)
					return seq.getOffset();
				else if (columnIndex == 3)
					return seq.getGid();
				else if (columnIndex == 4)
					return seq.match;
				else if (columnIndex == 5)
					return seq.index;

				return "";
			}

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return false;
			}

			@Override
			public void removeTableModelListener(TableModelListener l) {
			}

			@Override
			public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			}
		});

		lefttable.getRowSorter().addRowSorterListener(new RowSorterListener() {
			@Override
			public void sorterChanged(RowSorterEvent e) {
				ov.repaint();
				seqpane.repaint();
			}
		});
		lefttable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				seqpane.repaint();
			}
		});

		lefttable.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent me) {
				if (me.getClickCount() == 2) {
					int r = lefttable.getSelectedRow();
					if (r >= 0) {
						Rectangle visrect = seqpane.getVisibleRect();
						visrect.x = 10 * (Integer) lefttable.getValueAt(r, 1);
						seqpane.scrollRectToVisible(visrect);
					}
					seqpane.repaint();
				}
			}
		});

		lefttable.getTableHeader().setPreferredSize(new Dimension(100, 32));
		leftpane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

		// final FindDialog fd = new FindDialog();
		AbstractAction action = new AbstractAction("Find") {
			@Override
			public void actionPerformed(ActionEvent e) {
				seqpane.find(textfield.getText());
				ov.repaint();
				// fd.setVisible( true );
			}
		};
		textfield.setPreferredSize(new Dimension(500, 25));
		toolbar.add(action);
		toolbar.add(textfield);

		ov = new Overview(lefttable, seqpane, seqpane.getLength());
		ov.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				int x = (int) (((long) seqpane.getWidth() * (long) e.getX()) / (long) ov.getWidth());
				int y = seqpane.getHeight() * e.getY() / ov.getHeight();
				Rectangle vr = seqpane.getVisibleRect();
				vr.x = x;
				vr.y = y - vr.height / 2;
				seqpane.scrollRectToVisible(vr);
			}
		});
		splitpane.setTopComponent(subsplit);
		splitpane.setBottomComponent(ov);

		c.setLayout(new BorderLayout());
		c.add(this.splitpane);
		c.add(this.toolbar, BorderLayout.NORTH);

		final ClipboardOwner co = new ClipboardOwner() {
			@Override
			public void lostOwnership(Clipboard clipboard, Transferable contents) {
			}
		};

		JPopupMenu popup = new JPopupMenu();
		popup.add(new AbstractAction("Copy sequence") {
			@Override
			public void actionPerformed(ActionEvent e) {
				int r = lefttable.getSelectedRow();
				if (r != -1) {
					r = lefttable.convertRowIndexToModel(r);
					Sequence seq = seqpane.sequencelist.get(r);

					StringSelection stringSelection = new StringSelection(new String(seq.bb.array()));
					Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
					cb.setContents(stringSelection, co);
				}
			}
		});
		popup.add(new AbstractAction("Copy as fasta") {
			@Override
			public void actionPerformed(ActionEvent e) {
				int r = lefttable.getSelectedRow();
				if (r != -1) {
					r = lefttable.convertRowIndexToModel(r);
					Sequence seq = seqpane.sequencelist.get(r);

					String val = "> " + seq.name + "\n";
					for (int i = 0; i < seq.bb.limit(); i += 70) {
						val += new String(seq.bb.array(), i, Math.min(70, seq.bb.limit() - i)) + "\n";
					}

					StringSelection stringSelection = new StringSelection(val);
					Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
					cb.setContents(stringSelection, co);
				}
			}
		});
		lefttable.setComponentPopupMenu(popup);
	}

	public Map<String, Map<String, Coff>> offsetRead(String filename, String prefix, String subprefix, Set<String> filter, Set<String> subfilter) throws IOException {
		Map<String, Map<String, Coff>> offsetMap = new HashMap<String, Map<String, Coff>>();
		Map<String, Coff> suboffsetMap = null;
		Coff offsetList = null;// new ArrayList<Offset>();
		if (filename != null) {
			File f = new File(filename);
			FileReader fr = new FileReader(f);
			BufferedReader br = new BufferedReader(fr);

			String current = "";
			String line = br.readLine();
			while (line != null) {
				if (line.startsWith(">")) {
					if (current.length() > 0) {
						// System.err.println(current);
						String oname = prefix + current;
						offsetMap.put(oname, suboffsetMap);
					}
					current = line.substring(1).trim();
					suboffsetMap = new HashMap<String, Coff>();
					// offsetList = new ArrayList<Offset>();
				} else if (current.length() > 0) {
					String[] split = line.trim().split("[ \t]+");
					if (split[0].length() > 1) {
						String sv = split[0];
						String sname = subprefix + sv;		
						
						if( subfilter == null || subfilter.contains(sname) ) {
							int lv = Integer.parseInt(split[1]);
							int ls = Integer.parseInt(split[2]);
							int ll = Integer.parseInt(split[3]);
	
							if (suboffsetMap.containsKey(sname)) {
								offsetList = suboffsetMap.get(sname);
							} else {
								offsetList = new Coff(0, new ArrayList<Offset>());
								suboffsetMap.put(sname, offsetList);
							}
	
							offsetList.loffset.add(new Offset(lv, ls, ll));
						}
					}
				}

				line = br.readLine();
			}
			
			String pname = prefix + current;
			if (current.length() > 0 && (filter == null || filter.contains(pname)) ) {
				offsetMap.put(pname, suboffsetMap);
			}

			br.close();
			fr.close();
		}

		return offsetMap;
	}

	public Map<String, Coff> calcOff(Sequence seq, Coff off, boolean reverse, Map<String, Sequence> seqMap, Map<String, Coff> boffMap) {
		Map<String, Coff> ret = new HashMap<String, Coff>();
		for (String s : boffMap.keySet()) {
			off = boffMap.get(s);
			off.seq1 = seq;
			off.seq2 = seqMap.get(s);
			off.rc = reverse;
			Collections.sort(off.loffset);
			off.bestMatchIndex = checkLongestCons(off.loffset);
			if (off.bestMatchIndex < off.size()) {
				// Offset toff = off.getBestOffset();
				// int loff = toff.a - toff.b;
				// seq.setOffset( 0 ); //loff; // = new Sequence( seq.name, tb,
				// loff, coff, off );

				if (off.size() == 0)
					off.add(new Offset(0, 0, 0));
				/*
				 * seq.offsetMap = new HashMap<String,Coff>(); Coff coff = new
				 * Coff( bestMatchIndex, off ); seq.offsetMap.put("cons", coff);
				 */
			}
			if (off.rc)
				ret.put(s + " Reverse", off);
			else
				ret.put(s, off);
		}
		return ret;
	}

	public List<Sequence> load(String fname1, String fname2, String foffset1, String foffset2, String pref1, String pref2) throws IOException {
		return load( fname1, fname2, foffset1, foffset2, pref1, pref2, null, null );
	}
	
	public List<Sequence> load(String fname1, String fname2, String foffset1, String foffset2, String pref1, String pref2, Set<String> seq1Filter, Set<String> seq2Filter) throws IOException {
		File f = new File(fname1);
		ByteBuffer bb = ByteBuffer.allocate((int) f.length());
		FileInputStream fis = new FileInputStream(f);
		int r1 = fis.read(bb.array());
		fis.close();

		List<Sequence> lseq1 = fetchSeq(bb, pref1, seq1Filter);

		f = new File(fname2);
		bb = ByteBuffer.allocate((int) f.length());
		fis = new FileInputStream(f);
		int r2 = fis.read(bb.array());
		fis.close();

		List<Sequence> lseq2 = fetchSeq(bb, pref2, seq2Filter);

		Map<String, Sequence> seqMap = new HashMap<String, Sequence>();
		for (Sequence seq : lseq1) {
			seqMap.put(seq.name, seq);
		}
		for (Sequence seq : lseq2) {
			seqMap.put(seq.name, seq);
		}

		Map<String, Map<String, Coff>> offsetMap2 = offsetRead(foffset1, pref2, pref1, seq2Filter, seq1Filter);
		Map<String, Map<String, Coff>> offsetMap1 = offsetRead(foffset2, pref1, pref2, seq1Filter, seq2Filter);

		Coff off = null;
		for (Sequence seq : lseq1) {
			if (offsetMap1.containsKey(seq.name)) {
				seq.offsetMap = calcOff(seq, off, false, seqMap, offsetMap1.get(seq.name));
				// seq.offsetMap = offsetMap1.get(seq.name);
			}

			if (offsetMap1.containsKey(seq.name + " Reverse")) {
				seq.offsetMap.putAll(calcOff(seq, off, true, seqMap, offsetMap1.get(seq.name + " Reverse")));
			}
		}

		for (Sequence seq : lseq2) {
			if (offsetMap2.containsKey(seq.name)) {
				seq.offsetMap = calcOff(seq, off, false, seqMap, offsetMap2.get(seq.name));
			}

			if (offsetMap2.containsKey(seq.name + " Reverse")) {
				seq.offsetMap.putAll(calcOff(seq, off, true, seqMap, offsetMap2.get(seq.name + " Reverse")));
			}

			/*
			 * if( offsetMap2.containsKey(seq.name) ) { seq.offsetMap =
			 * offsetMap2.get(seq.name); for( String s : seq.offsetMap.keySet()
			 * ) { off = seq.offsetMap.get(s);
			 * 
			 * off.seq1 = seq; off.seq2 = seqMap.get(s);
			 * 
			 * Collections.sort( off.loffset ); off.bestMatchIndex =
			 * checkLongestCons( off.loffset ); if( off.bestMatchIndex <
			 * off.size() ) { //Offset toff = off.getBestOffset(); //int loff =
			 * toff.a - toff.b; //seq.setOffset( 0 );//loff; if( off.size() == 0
			 * ) off.add( new Offset(0,0,0) ); } } } else { System.err.println(
			 * "not existing2 " + seq.name ); }
			 */
		}

		/*
		 * int gid = 1; for( Sequence seq1 : lseq1 ) { if(
		 * !finishedSet.contains( seq1.name ) ) { seq1.setOffset( 0 );
		 * 
		 * Set<Sequence> sset = new HashSet<Sequence>(); alignDuo( seq1, lseq1,
		 * lseq2, sset );
		 * 
		 * int minoffset = 0; for( Sequence seq : sset ) { if( seq.getOffset() <
		 * minoffset ) minoffset = seq.getOffset(); }
		 * 
		 * //System.err.println( minoffset ); seq1.setOffset( -minoffset );
		 * seq1.gid = ""+gid; for( Sequence seq : sset ) { if( seq != seq1 ) {
		 * seq.setOffset( seq.getOffset() - minoffset ); seq.gid = ""+gid; //if(
		 * seq.getOffset() < 0 ) System.err.println( "eeeeeeeerm " +
		 * seq.getOffset() + "   " + minoffset ); } } gid++; } }
		 */
		lseq1.addAll(lseq2);

		return lseq1;
	}

	Set<String> finishedSet = new HashSet<String>();

	public void alignDuo(Sequence seq1, List<Sequence> lseq1, List<Sequence> lseq2, Set<Sequence> seqSet) {
		seqSet.add(seq1);
		finishedSet.add(seq1.name);

		int dlen1 = 0;
		String dstr1 = null;
		Sequence dseq1 = null;
		// Offset doff1 = null;
		int dlen2 = 0;
		String dstr2 = null;
		Sequence dseq2 = null;
		// Offset doff2 = null;
		for (String s : seq1.offsetMap.keySet()) {
			Coff coff = seq1.offsetMap.get(s);
			Sequence seq2 = null;
			for (Sequence seq : lseq2) {
				if (seq.name.equals(s)) {
					seq2 = seq;
					break;
				}
			}

			if (seq2 != null) {
				// int astart = seq1.getAlignStart( s );
				// int astop = seq1.getAlignStart( s );

				int fi = seq1.getFirstAlignIndex(s);
				int li = seq1.getLastAlignIndex(s);

				int where = 0;
				Offset fo = coff.loffset.get(fi);
				Offset lo = coff.loffset.get(li);

				/*
				 * if( fo.a > seq1.bb.limit() || lo.a > seq1.bb.limit() ) {
				 * System.err.println( "seq1 - a wrong" ); } if( fo.a >
				 * seq2.bb.limit() || lo.a > seq2.bb.limit() ) {
				 * System.err.println( "seq2 - a wrong" ); } if( fo.b >
				 * seq1.bb.limit() || lo.b > seq1.bb.limit() ) {
				 * System.err.println( "seq1 - b wrong" ); } if( fo.b >
				 * seq2.bb.limit() || lo.b > seq2.bb.limit() ) {
				 * System.err.println( "seq2 - b wrong" ); }
				 */

				if (fo.a > fo.b) {
					// int total = fo.b + lo.b + lo.c;
					int val = 0;
					for (int i = fi; i < li; i++) {
						val += coff.loffset.get(i).c;
					}

					if (val >= 50)
						where = -1;

					// int val = (fo.a - fo.b);
					// seq2.offset = seq1.offset - val;
				} else if (seq1.bb.limit() - lo.b < seq2.bb.limit() - lo.a) {
					// int total = fo.b + lo.b + lo.c;
					int val = 0;
					for (int i = fi; i < li; i++) {
						val += coff.loffset.get(i).c;
					}

					if (val >= 50)
						where = 1;

					// int val = (seq2.bb.limit() - lo.a) - (seq1.bb.limit() -
					// lo.b);
					// seq2.offset = seq1.offset - val;
				}

				int nlen = lo.a - fo.a + lo.c;

				// System.err.println( nlen + "  " + s );
				if (where == -1) {
					if (nlen > dlen1) {
						dlen1 = nlen;
						dstr1 = s;
						dseq1 = seq2;
					}
				} else if (where == 1) {
					if (nlen > dlen2) {
						dlen2 = nlen;
						dstr2 = s;
						dseq2 = seq2;
					}
				}
			}
			// int v = checkLongestCons( loffset1 );
		}

		if (dstr1 != null && !finishedSet.contains(dstr1)) {
			// System.err.println( dstr1 + " left of " + seq1.name );

			Coff coff = seq1.offsetMap.get(dstr1);
			int fi = seq1.getFirstAlignIndex(dstr1);
			Offset fo = coff.loffset.get(fi);
			int val = fo.a - fo.b;
			dseq1.setOffset(seq1.getOffset() - val);

			alignDuo(dseq1, lseq2, lseq1, seqSet);
		}

		if (dstr2 != null && !finishedSet.contains(dstr2)) {
			// System.err.println( dstr2 + " right of " + seq1.name );

			Coff coff = seq1.offsetMap.get(dstr2);
			int fi = seq1.getLastAlignIndex(dstr2);
			Offset fo = coff.loffset.get(fi);
			int val = fo.a - fo.b;
			dseq2.setOffset(seq1.getOffset() - val);

			alignDuo(dseq2, lseq2, lseq1, seqSet);
		}

		// if( dnam != null ) System.err.println( dnam );
	}

	public List<Sequence> loadWithGbkFeatures(String fname, String ffeatures) throws IOException {
		Color redcolor = new Color(200, 100, 100, 100);

		File f = new File(fname);
		ByteBuffer bb = ByteBuffer.allocate((int) f.length());
		FileInputStream fis = new FileInputStream(f);
		int r = fis.read(bb.array());
		fis.close();

		List<Sequence> lseq = fetchSeq(bb, "", null);
		Sequence seq = lseq.get(0);
		lseq.clear();

		List<Integer> offlist = new ArrayList<Integer>();
		for (int i = 0; i < seq.bb.limit() - 3; i++) {
			if (seq.bb.get(i) == 'N') {
				offlist.add(i);
				i += 3;
			}
		}

		f = new File(ffeatures);
		FileReader fr = new FileReader(f);
		BufferedReader br = new BufferedReader(fr);
		String line = br.readLine();
		while (line != null) {
			String[] split = line.trim().split("[ ]+");
			if (split.length > 1 && split[0].equals("CDS")) {
				String res = split[1];
				int i1 = res.indexOf('(');
				if (i1 != -1) {
					int i2 = res.indexOf(')', i1);
					res = res.substring(i1 + 1, i2);
				}
				String[] rsplit = res.split("\\.\\.");
				int start = Integer.parseInt(rsplit[0]);
				int stop = Integer.parseInt(rsplit[1]);

				String tag = "tag";
				line = br.readLine();
				while (line != null) {
					String prod = line.trim();
					line = br.readLine();
					if (prod.startsWith("/product")) {
						int end = prod.length();
						int ind = prod.indexOf("n=");
						if (ind != -1) {
							end = ind - 1;
						}
						int startv = 9;
						if (prod.contains("UniRef"))
							startv = 26;
						tag = prod.substring(startv, end);
						break;
					}
				}
				// seq.addSubsequence( tag, start, stop, start, redcolor );
				ByteBuffer nbb = ByteBuffer.wrap(Arrays.copyOfRange(seq.bb.array(), start, stop));

				int val = Collections.binarySearch(offlist, start);
				lseq.add(new Sequence(tag, nbb, start, null, Integer.toString(val)));
			} else
				line = br.readLine();
			// line = br.readLine();
		}

		return lseq;
	}

	public List<Sequence> load(String fname) throws IOException {
		return load(fname, null);
	}

	public List<Sequence> load(String fname, String foffset) throws IOException {
		File f = new File(fname);
		ByteBuffer bb = ByteBuffer.allocate((int) f.length());
		FileInputStream fis = new FileInputStream(f);
		int r = fis.read(bb.array());

		Map<String, List<Offset>> offsetMap = new HashMap<String, List<Offset>>();
		List<Offset> offsetList = null;// new ArrayList<Offset>();
		if (foffset != null) {
			f = new File(foffset);
			FileReader fr = new FileReader(f);
			BufferedReader br = new BufferedReader(fr);

			String current = "";
			String line = br.readLine();
			while (line != null) {
				if (line.startsWith(">")) {
					if (current.length() > 0)
						offsetMap.put(current, offsetList);
					current = line.substring(1).trim();
					offsetList = new ArrayList<Offset>();
				} else if (current.length() > 0) {
					String[] split = line.trim().split("[ \t]+");
					if (split[0].length() > 1) {
						int lv = Integer.parseInt(split[0]);
						int ls = Integer.parseInt(split[1]);
						int ll = Integer.parseInt(split[2]);
						offsetList.add(new Offset(lv, ls, ll));
						// offsetMap.put( current, lv-ls );
					} else {
						System.err.println(line);
					}
				}

				line = br.readLine();
			}
			if (current.length() > 0)
				offsetMap.put(current, offsetList);
		}

		List<Sequence> lseq = fetchSeq(bb, "", null);
		List<Offset> off = null;
		for (Sequence seq : lseq) {
			if (offsetMap.containsKey(seq.name)) {
				off = offsetMap.get(seq.name);
				Collections.sort(off);
				int bestMatchIndex = checkLongestCons(off);
				if (bestMatchIndex < off.size()) {
					Offset toff = off.get(bestMatchIndex);
					int loff = toff.a - toff.b;
					seq.setOffset(loff); // = new Sequence( seq.name, tb, loff,
											// coff, off );
					if (off.size() == 0)
						off.add(new Offset(0, 0, 0));
					seq.offsetMap = new HashMap<String, Coff>();

					Coff coff = new Coff(bestMatchIndex, off);
					seq.offsetMap.put("cons", coff);
				}
			}
		}

		// Collections.sort( lseq );

		/*
		 * int c = 0; for( i = 0; i < bb.limit(); i++ ) { byte b = bb.get(i);
		 * c++; if( b == '\n' ) break; } i++; for( ; i < bb.limit(); i++ ) {
		 * byte b = bb.get(i); if( b == '\n' ) c++; else bb.put( i-c, b ); }
		 */

		return lseq;
	}

	private byte toLower( byte b ) {
		if( b < 95 ) {
			byte rb = (byte)(b+32);
			if( rb != 97 && rb != 99 && rb != 103 && rb != 116 ) {
				System.err.println();
			}
			return rb;
		}
		return b;
	}
	
	public List<Sequence> fetchSeq(ByteBuffer bb, String prefix, Set<String> seqFilter) {
		List<Sequence> retseq = new ArrayList<Sequence>();

		int i = 0;
		int start = 0;
		int c = 0;
		String name = "";
		while (i < bb.limit()) {
			byte b = bb.get(i++);
			if (b == '>') {
				int strstart = i;
				int stop = i - 2;

				while (bb.get(i++) != ' ')
					;
				int strstop = i - 1;

				int length = stop - start - c;
				if (length > 0) {
					ByteBuffer tb = ByteBuffer.allocate(length);
					int cc = 0;
					for (int k = start; k < stop; k++) {
						byte bt = bb.get(k);
						if (bt == '\n' || bt == '\r') {
							cc++;
						} else {
							int ind = k - cc - start;
							if (ind < 0 || ind >= tb.limit())
								System.err.println("ind " + ind);
							else
								tb.put( ind, toLower( bt ) );
						}
					}

					String pname = prefix + name;
					if( seqFilter == null || seqFilter.contains(pname) ) {
						Sequence seq = new Sequence(pname, tb, -1, null, "");
						retseq.add(seq);
					}
				}
				while (bb.get(i++) != '\n')
					;
				start = i;
				c = 0;
				name = new String(bb.array(), strstart, strstop - strstart);
			} else if (b == '\n' || b == '\r')
				c++;
		}

		int stop = i - 2;
		int length = stop - start - c;
		if (length > 0) {
			ByteBuffer tb = ByteBuffer.allocate(length);
			int cc = 0;
			for (int k = start; k < stop; k++) {
				byte bt = bb.get(k);
				if (bt == '\n' || bt == '\r')
					cc++;
				else {
					int ind = k - cc - start;
					if (ind < 0 || ind >= tb.limit())
						System.err.println("ind " + ind);
					else
						tb.put(ind, bt);
				}
			}

			String pname = prefix + name;
			if( seqFilter == null || seqFilter.contains(pname) ) {
				Sequence seq = new Sequence(pname, tb, -1, null, "");
				retseq.add(seq);
			}
		}

		return retseq;
	}

	int gdiff = 0;
	public int checkLongestCons(List<Offset> offlist) {
		int ret = 0;

		if (offlist != null) {
			int count = 0;
			int subret = 0;
			int lastcount = 0;
			int prev = 0;
			int aprev = 0;
			int i = 0;
			int maxind = 0;
			int maxval = 0;
			for (Offset off : offlist) {
				int adiff = off.b - prev;
				int bdiff = off.a - aprev;

				if (off.c > maxval) {
					maxval = off.c;
					maxind = i;
				}

				if (bdiff > 0 && bdiff > adiff - gdiff && bdiff < adiff + gdiff) {
					count++;
				} else {
					if (count > lastcount) {
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
			if (count > lastcount) {
				ret = subret;
				lastcount = count;
			}

			if (lastcount <= 1) {
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
		this.add(c);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Mummy mummy = new Mummy();
			JFrame frame = new JFrame("Mummy - Fasta Viewer / Aligner");
			frame.setBackground(Color.white);
			frame.getContentPane().setBackground(Color.white);
			frame.setLayout(new BorderLayout());
			frame.getContentPane().setLayout(new BorderLayout());
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setSize(800, 600);
			frame.add(mummy.c);
			mummy.initGui();
			frame.setVisible(true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}