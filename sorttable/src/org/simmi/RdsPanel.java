package org.simmi;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

public class RdsPanel extends JSplitPane {
	List<Object[]>				rows = new ArrayList<Object[]>();
	final Color 				paleGreen = new Color( 20,230,60,96 ); 
	final FriendsPanel 			fp;
	TableModel					model;
	SortTable					st;
	Map<String,String>			detailMapping = new HashMap<String,String>();
	final String 				vurl = "http://www.fa.is/deildir/Efnafraedi/Naeringarfr/naervefur/Templates/glaerur/vatnsvit.htm";
	final String 				furl = "http://www.fa.is/deildir/Efnafraedi/Naeringarfr/naervefur/Templates/glaerur/fituvit.htm";
	JCompatTable				table;
	
	public String getRds( String colname ) {
		String ret = null;
		
		int r = table.getSelectedRow();
		
		int base = 4;
		if( r >= 0 ) {
			base = r+2;
		} else {
			int age = fp.getSelectedAge();
			String sex = fp.getSelectedSex();
			if( sex != null && sex.equals("Karl") ) base += 8;
			if( age < 14 ) base+=1;
			else if( age < 18 ) base+=2;
			else if( age < 31 ) base+=3;
			else if( age < 61 ) base+=4;
			else base+=5;
		}
		
		int i = 0;
		String colName = model.getColumnName(i);
		
		boolean one = false;
		if( colname.length() == 1 ) {
			String[] spl = colName.split("[- ]+");
			colName = spl[0];
			while( !colName.equals(colname) ) {
				i++;
				if( i == model.getColumnCount() ) break;
				
				colName = model.getColumnName(i);
				spl = colName.split("[- ]+");
				colName = spl[0];
			}
		} else {
			while( !colName.contains(colname) ) {
				i++;
				if( i == model.getColumnCount() ) break;
				colName = model.getColumnName(i);
			}
		}
		
		if( i < model.getColumnCount() ) {
			Object[] obj = rows.get( base );
			ret = (String)obj[i];
		}
		
		return ret;
	}
	
	public RdsPanel( final FriendsPanel fp, final SortTable st ) {
		super( JSplitPane.VERTICAL_SPLIT );
		
		this.fp = fp;
		this.st = st;
		
		InputStream inputStream = this.getClass().getResourceAsStream( "/rdsage.txt" );
		try {
			BufferedReader br = new BufferedReader( new InputStreamReader( inputStream, "UTF-8" ) );
			String line = br.readLine();
			String[] split = line.split("[\t]+");
			rows.add( split );
			line = br.readLine();
			while( line != null ) {
				split = line.split("[\t]+");
				rows.add( split );
				line = br.readLine();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		table = new JCompatTable() {
			public Component prepareRenderer( TableCellRenderer renderer, int row, int column ) {
				Component c = super.prepareRenderer(renderer, row, column);
				int age = fp.getSelectedAge();
				String sex = fp.getSelectedSex();
				int base = 4;
				if( sex != null && sex.equals("Karl") ) base += 8;
				if( age < 14 ) base+=1;
				else if( age < 18 ) base+=2;
				else if( age < 31 ) base+=3;
				else if( age < 61 ) base+=4;
				else base+=5;
				
				int r = table.convertRowIndexToModel(row);
				if( r != -1 && r == base ) c.setBackground( paleGreen );
				else if( this.getSelectedRow() != row ) {
					c.setBackground( Color.white );
				} else {
					c.setBackground( this.getSelectionBackground() );
				}
				return c;
			}
		};
		table.setAutoCreateRowSorter( true );
		table.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );
		model = new TableModel() {
			public void addTableModelListener(TableModelListener l) {}
			public Class<?> getColumnClass(int columnIndex) {
				return String.class;
			}
			public int getColumnCount() {
				return rows.get(0).length;
			}
			public String getColumnName(int columnIndex) {
				return rows.get(0)[columnIndex] + " - " + rows.get(1)[columnIndex];
			}
			public int getRowCount() {
				return Math.max( 0, rows.size()-2 );
			}
			public Object getValueAt(int rowIndex, int columnIndex) {
				return rows.get( rowIndex + 2 )[columnIndex];
			}
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return false;
			}
			public void removeTableModelListener(TableModelListener l) {}
			public void setValueAt(Object aValue, int rowIndex, int columnIndex) {}
		};
		
		table.setColumnSelectionAllowed( true );
		table.addMouseListener( new MouseAdapter() {
			public void mousePressed( MouseEvent e ) {
				if( e.getClickCount() == 2 ) {
					Point p = e.getPoint();
					int c = table.columnAtPoint(p);
					if( c < table.getColumnCount() ) {
						Object obj = table.getColumnName(c);
						if( obj != null ) {
							String[] split = ((String)obj).split("\\/");
							st.sortByColumn( split[0] );
						}
					}
				}
			}
		});
		
		table.setModel( model );
		table.setColumnSelectionAllowed( true );
		
		final JEditorPane editor = new JEditorPane();
		editor.setEditable( false );
		editor.setContentType("text/html");
		new Thread() {
			public void run() {
				try {
					editor.setPage(vurl);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}.start();
		
		table.getSelectionModel().addListSelectionListener( new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent e) {
				int c = table.getSelectedColumn();
				String cName = table.getColumnName(c);
				if( detailMapping.containsKey(cName) ) {
					String vfl = detailMapping.get(cName);
					try {
						if( vfl.contains("Vatnsl") && !vurl.equals(editor.getPage().toString()) ) {
							editor.setPage(vurl);
						} else if( vfl.contains("Fitul") && !furl.equals(editor.getPage().toString()) ) {
							editor.setPage(furl);
						}
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
		
		JScrollPane tableScrollPane = new JScrollPane( table );
		JScrollPane editorScroll = new JScrollPane( editor );
		
		this.setTopComponent( tableScrollPane );
		this.setBottomComponent( editorScroll );
		
		this.setDividerLocation( 300 );
	}
}
