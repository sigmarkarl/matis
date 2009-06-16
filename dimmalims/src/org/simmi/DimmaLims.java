package org.simmi;

import java.awt.Graphics;

import javax.swing.JApplet;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class DimmaLims extends JApplet {
	static String lof = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
	
	public void init() {
		try {
			UIManager.setLookAndFeel(lof);
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
		
		final TableModel model = new TableModel() {
			@Override
			public void addTableModelListener(TableModelListener l) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public Class<?> getColumnClass(int columnIndex) {
				return String.class;
			}

			@Override
			public int getColumnCount() {
				return 3;
			}

			@Override
			public String getColumnName(int columnIndex) {
				// TODO Auto-generated method stub
				return "simmi";
			}

			@Override
			public int getRowCount() {
				return 10;
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				return "hallo";
			}

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void removeTableModelListener(TableModelListener l) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
				// TODO Auto-generated method stub
				
			}
		};
		
		SwingUtilities.invokeLater( new Runnable() {
			
			@Override
			public void run() {
				final JScrollPane	scrollpane = new JScrollPane();
				JComponent c = new JPanel() {
					public void setBounds( int x, int y, int w, int h ) {
						super.setBounds(x, y, w, h);
						scrollpane.setBounds(10, 30, this.getWidth()-20, this.getHeight()-40 );
					}
					
					public void paintComponent( Graphics g ) {
						super.paintComponent( g );
						
						g.drawString( "DimmaLims", 10, 20 );
					}
				};
				c.setLayout( null );
				
				JTable		table = new JTable();
				table.setModel( model );
				scrollpane.setViewportView( table );
				c.add( scrollpane );
				
				DimmaLims.this.add( c );
			}
		});
	}
}
