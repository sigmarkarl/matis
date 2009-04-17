package com.matis.prokaria;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JApplet;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class Fiskur extends JApplet {
	JComponent		c;
	List<String>	markers;
	List<String>	fish;
	int[]			matrix;
	List<Tuple>		tupleList;
	
	public Fiskur() {
		super();
		
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
	
	public void parseData( String data ) {
		String[] split = data.split("\\n");
		String[] vals = split[0].split("\\t");
		int r = (split.length-1);
		int c = (vals.length-1);
		
		markers = new ArrayList<String>();
		for( int i = 0; i < c; i++ ) {
			markers.add( vals[i] );
		}
		
		matrix = new int[ r*c ];
		fish = new ArrayList<String>();
		for( int i = 0; i < r; i++ ) {
			vals = split[i+1].split("\\t");
			fish.add( vals[0] );
			for( int k = 0; k < c; k++ ) {
				matrix[ i*c+k ] = Integer.parseInt(vals[k+1]);
			}
		}
	}
	
	public class Tuple implements Comparable<Tuple> {
		String	fish1;
		String	fish2;
		int		rank;
		
		public Tuple( String f1, String f2, int r ) {
			fish1 = f1;
			fish2 = f2;
			rank = r;
		}

		@Override
		public int compareTo(Tuple arg0) {
			return arg0.rank - rank;
		}
	};
	
	public List<Tuple> calcData() {
		int r = fish.size();
		int c = markers.size();
		
		List<Tuple> tupleList = new ArrayList<Tuple>();
		
		for( int i = 0; i < r-1; i++ ) {
			String n1 = fish.get(i);
			for( int k = i+1; k < r; k++ ) {
				String n2 = fish.get(k);
				int rank = 0;
				for( int u = 0; u < c; u++ ) {
					rank += Math.abs( matrix[ i*c+u ]-matrix[ k*c+u ] );
				}
				tupleList.add( new Tuple( n1, n2, rank ) );			
			}
		}
		
		Collections.sort( tupleList );
		
		return tupleList;
	}
	
	public void start() {
		super.start();
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
		
		c = new JComponent() {
			public void paintComponent( Graphics g ) {
				super.paintComponent( g );
			}
		};
		c.setLayout( new BorderLayout() );
		this.getRootPane().setBackground( Color.white );
		
		final JTable table = new JTable();
		table.setAutoCreateRowSorter( true );
		table.setModel( new TableModel() {

			@Override
			public void addTableModelListener(TableModelListener arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public Class<?> getColumnClass(int arg0) {
				if( arg0 < 2 ) return String.class;
				else if( arg0 == 2 ) return Integer.class;
				
				return Object.class;
			}

			@Override
			public int getColumnCount() {
				return 3;
			}

			@Override
			public String getColumnName(int arg0) {
				if( arg0 == 0 ) return "Fish 1";
				else if( arg0 == 1 ) return "Fish 2";
				else if( arg0 == 2 ) return "Similarity Rank";
				
				return null;
			}

			@Override
			public int getRowCount() {
				if( tupleList != null ) {
					return tupleList.size();
				}
				
				return 0;
			}

			@Override
			public Object getValueAt(int arg0, int arg1) {
				if( tupleList != null ) {
					Tuple t = tupleList.get(arg0);
					if( arg1 == 0 ) return t.fish1;
					if( arg1 == 1 ) return t.fish2;
					else return t.rank;
				}
				return null;
			}

			@Override
			public boolean isCellEditable(int arg0, int arg1) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void removeTableModelListener(TableModelListener arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void setValueAt(Object arg0, int arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
			
		});
		JScrollPane	scrollPane = new JScrollPane();
		scrollPane.setViewportView( table );
		c.add( scrollPane );
		
		ActionMap map = table.getActionMap();
        map.put(TransferHandler.getCutAction().getValue(Action.NAME), TransferHandler.getCutAction());
        map.put(TransferHandler.getCopyAction().getValue(Action.NAME), TransferHandler.getCopyAction());
        map.put(TransferHandler.getPasteAction().getValue(Action.NAME), TransferHandler.getPasteAction());
        
        InputMap imap = table.getInputMap();
        imap.put(KeyStroke.getKeyStroke("ctrl X"),
            TransferHandler.getCutAction().getValue(Action.NAME));
        imap.put(KeyStroke.getKeyStroke("ctrl C"),
            TransferHandler.getCopyAction().getValue(Action.NAME));
        imap.put(KeyStroke.getKeyStroke("ctrl V"),
            TransferHandler.getPasteAction().getValue(Action.NAME));

        this.add( c );
        //c.requestFocus();
        
		table.setTransferHandler( new TransferHandler() {
			public int getSourceActions(JComponent c) {
				return TransferHandler.COPY;
			}
			
			public boolean canImport( TransferHandler.TransferSupport support ) {
				return true;
			}
			
			protected Transferable createTransferable( JComponent c ) {
				return new Transferable() {

					@Override
					public Object getTransferData(DataFlavor arg0) throws UnsupportedFlavorException, IOException {
						String ret = "";
						int[] rr = table.getSelectedRows();
						for( int r : rr ) {
							ret += table.getValueAt(r, 0).toString() + "\t";
							ret += table.getValueAt(r, 1).toString() + "\t";
							ret += table.getValueAt(r, 2).toString() + "\n";
						}
						return ret;
					}

					@Override
					public DataFlavor[] getTransferDataFlavors() {
						return new DataFlavor[] { DataFlavor.stringFlavor };
					}

					@Override
					public boolean isDataFlavorSupported(DataFlavor arg0) {
						if( arg0 == DataFlavor.stringFlavor ) return true;
						return false;
					}
				};
			}
			
			public boolean importData( TransferHandler.TransferSupport support ) {
				try {
					Object obj = support.getTransferable().getTransferData( DataFlavor.stringFlavor );
					if( obj != null ) {
						String stuff = obj.toString();
						parseData( stuff );
						tupleList = calcData();
						
						//System.err.println( stuff.substring(0, 50) );
						table.revalidate();
						table.invalidate();
						table.repaint();
					}
				} catch (UnsupportedFlavorException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				return true;
			}
		});
	}
}
