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
	JTable						table;
	
	public String getRds( String colname ) {
		String ret = null;
		
		int age = fp.getSelectedAge();
		String sex = fp.getSelectedSex();
		int base = 4;
		if( sex != null && sex.equals("Karl") ) base += 8;
		if( age < 14 ) base+=1;
		else if( age < 18 ) base+=2;
		else if( age < 31 ) base+=3;
		else if( age < 61 ) base+=4;
		else base+=5;
		
		int i = 0;
		String colName = model.getColumnName(i);
		while( !colname.contains(colName) ) {
			i++;
			if( i == model.getColumnCount() ) break;
			colName = model.getColumnName(i);
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
		
		InputStream inputStream = this.getClass().getResourceAsStream( "rdsage.txt" );
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
		
		table = new JTable() {
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
			@Override
			public void addTableModelListener(TableModelListener l) {}

			@Override
			public Class<?> getColumnClass(int columnIndex) {
				return String.class;
			}

			@Override
			public int getColumnCount() {
				return rows.get(0).length;
			}

			@Override
			public String getColumnName(int columnIndex) {
				return rows.get(0)[columnIndex] + "/" + rows.get(1)[columnIndex];
			}

			@Override
			public int getRowCount() {
				return Math.max( 0, rows.size()-2 );
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				return rows.get( rowIndex + 2 )[columnIndex];
			}

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return false;
			}

			@Override
			public void removeTableModelListener(TableModelListener l) {}

			@Override
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
		try {
			editor.setPage(vurl);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		table.getSelectionModel().addListSelectionListener( new ListSelectionListener(){
			@Override
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
