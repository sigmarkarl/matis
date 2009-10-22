package org.simmi;

import java.awt.Component;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JApplet;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

public class Flanking extends JApplet {
	JScrollPane	scrollpane = new JScrollPane();
	JTable		table = new JTable();
	TableModel	model;
	
	class Repeat implements Comparable<Repeat> {
		int	start;
		int stop;
		int length;
		
		@Override
		public int compareTo(Repeat o) {
			return start-o.start;
		}
	};
	
	class Sequence {
		String 			name;
		Integer			length;
		String			htmlsequence;
		List<Repeat>	_repeats;
		
		public boolean hasRepeats( int length ) {
			for( Repeat r : _repeats ) {
				if( r.length == length ) return true;
			}
			return false;
		}
		
		public boolean hasRepeatsMoreThan( int length ) {
			for( Repeat r : _repeats ) {
				if( r.length > length ) return true;
			}
			return false;
		}
		
		public Sequence( String name, int length, String sequence ) {
			int LEN = 4;
			
			this.name = name;
			this.length = length;
			//this.htmlsequence = sequence;
			
			this._repeats = new ArrayList<Repeat>();
			
			for( int k = 0; k < sequence.length()-LEN*4; k++ ) {
				for( int i = 2; i < 5; i++ ) {
					boolean yes = true;
					for( Repeat r : _repeats ) {
						if( r.length != i && k >= r.start && k <= r.stop ) {
							yes = false;
							break;
						}
					}
					
					if( yes ) {
						for( int l = k; l < k+i; l++ ) {
							if( sequence.charAt(l) != sequence.charAt(l+i) || sequence.charAt(l) != sequence.charAt(l+i*2) || sequence.charAt(l) != sequence.charAt(l+i*3) ) {
								yes = false;
								break;
							}
						}
						
						if( yes ) {
							Repeat r = new Repeat();
							r.start = k;
							r.length = i;
							
							int u = LEN;
							while( yes ) {
								for( int l = k; l < k+i; l++ ) {
									int val = l+u*i;
									if( val >= sequence.length() || sequence.charAt(l) != sequence.charAt(val) ) {
										yes = false;
										break;
									}
								}
								if( yes ) u++;
							}
							
							r.stop = k+i*u;
							
							k = r.stop;
							
							_repeats.add( r );
						}
					}
				}
			}
			
			Collections.sort( _repeats );
			
			StringBuffer buf = new StringBuffer( sequence );
			
			int offset = 0;
			for( Repeat r : _repeats ) {
				//int startval = r.start+offset;
				/*char c = buf.charAt( startval );
				if( c != 'A' && c != 'C' && c != 'G' && c != 'T' ) {
					System.err.println( startval + "   " + buf.toString() );
				}*/
				if( r.length == 2 ) {
					buf.insert(r.start+offset, "<b><font color=#00aa00>");
				} else if( r.length == 3 ) {
					buf.insert(r.start+offset, "<b><font color=#0000aa>");
				} else if( r.length == 4 ) {
					buf.insert(r.start+offset, "<b><font color=#aa0000>");
				} else if( r.length == 5 ) {
					buf.insert(r.start+offset, "<b><font color=#aa00aa>");
				}
				offset += 23;
				buf.insert(r.stop+offset, "</font></b>");
				offset += 11;
			}
			htmlsequence = "<html>"+buf.toString()+"</html>";
		}
	};
	
	String[] loadFna( String filename ) throws IOException {
		File file = new File( filename );
		ByteBuffer bb = ByteBuffer.allocate( (int)file.length() );
		FileInputStream	stream = new FileInputStream( file );
		
		int total = 0;
		int r = stream.read( bb.array() );
		while( r > 0 ) {
			total += r;
			r = stream.read( bb.array(), total, bb.limit()-total );
		}
		
		String s = new String( bb.array() );
		String[] split = s.split(">");
		
		return split;
	}
	
	static {
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public TableModel createModel( final List<?> datalist ) {
		Class cls = null;
		if( cls == null && datalist.size() > 0 ) cls = datalist.get(0).getClass();
		return createModel( datalist, cls );
	}
	
	public TableModel createModel( final List<?> datalist, final Class cls ) {
		//System.err.println( cls );
		return new TableModel() {
			@Override
			public void addTableModelListener(TableModelListener l) {}

			@Override
			public Class<?> getColumnClass(int columnIndex) {
				return cls.getDeclaredFields()[columnIndex].getType();
			}

			@Override
			public int getColumnCount() {
				int cc = cls.getDeclaredFields().length-2;
				return cc;
			}

			@Override
			public String getColumnName(int columnIndex) {
				return cls.getDeclaredFields()[columnIndex].getName().replace("e_", "");
			}

			@Override
			public int getRowCount() {
				return datalist.size();
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				Object ret = null;
				try {
					if( columnIndex >= 0 ) {
						Field f = cls.getDeclaredFields()[columnIndex];
						ret = f.get( datalist.get(rowIndex) );
						
						if( ret != null && ret.getClass() != f.getType() ) {
							System.err.println( ret.getClass() + "  " + f.getType() );
							ret = null;
						}
					}
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return ret;
			}

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				Field[] ff = cls.getDeclaredFields();
				Field 	f = ff[columnIndex];
				return f.getName().startsWith("e_");
			}

			@Override
			public void removeTableModelListener(TableModelListener l) {}

			@Override
			public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
				Object o = datalist.get( rowIndex );
				Field f = cls.getDeclaredFields()[columnIndex];
				try {
					f.set( o, aValue );
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
	}
	
	String[] split;
	public void init() {
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			split = loadFna( "/home/sigmar/sild/2.TCA.454Reads.fna" );
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		List<Sequence>	seqList = new ArrayList<Sequence>();
		
		int max = 0;
		int row = 0;
		for( String s : split ) {
			int i = s.indexOf('\n');
			if( i > 0 ) {
				String head = s.substring(0, i);
				String foot = s.substring(i+1);
				String[] spl = head.split("[ ]+");
				String seq = foot.replace("\n", "");
				int seqlen = seq.length();
				if( seqlen > 100 ) {
					if( seqlen > max ) {
						max = seqlen;
						row = seqList.size();
					}
					Sequence seqobj = new Sequence( spl[0], seqlen, seq );
					if( seqobj.hasRepeatsMoreThan(3) ) seqList.add( seqobj );
				}
			}
		}
		System.err.println( seqList.size() );
		
		model = createModel( seqList );
		
		table.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );
		table.setAutoCreateRowSorter( true );
		table.setModel( model );
		scrollpane.setViewportView( table );
		
		TableColumn tc = table.getColumnModel().getColumn(2);
		TableCellRenderer tcr = table.getDefaultRenderer(model.getColumnClass(2));
		Component c = tcr.getTableCellRendererComponent(table,model.getValueAt(row,2),false,false,row,2);
		tc.setPreferredWidth(c.getPreferredSize().width);
		
		this.add( scrollpane );
	}
}
