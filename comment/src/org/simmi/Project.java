package org.simmi;

import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.AbstractAction;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public class Project extends JApplet {
	Connection	con;
	
	JSplitPane	splitpane = new JSplitPane();
	
	JScrollPane	scrollpane = new JScrollPane();
	JTable		table = new JTable();
	JScrollPane scrollarea = new JScrollPane();
	JTextArea	textarea = new JTextArea();
	JScrollPane rscrollarea = new JScrollPane();
	JTextArea	rtextarea = new JTextArea();
	
	JSplitPane	rsplitpane = new JSplitPane( JSplitPane.VERTICAL_SPLIT );
	
	JButton		vista = new JButton();	
	JComboBox	combo = new JComboBox();
	
	Set<String>	fset = new TreeSet<String>();
	
	TableModel	model = null;
	
	String		currentSelection = null;
	
	class Proj {
		String Nafn;
		String _OrderComment;
		String _ResultComment;
		
		public Proj( String name ) {
			Nafn = name;
		}
		
		public Proj( String name, String ocomment, String rcomment ) {
			Nafn = name;
			_OrderComment = ocomment;
			_ResultComment = rcomment;
		}
	};
	
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
				int cc = cls.getDeclaredFields().length-1;
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
	
	Map<String,Proj>	orderMap = new HashMap<String,Proj>();
	public List<Proj> getProjects() throws SQLException {
		List<Proj>	folderList = new ArrayList<Proj>();
		
		String sql = "select [ordno], [mycomment], [attachmenttype] from [PROD_UPG_DATA].[dbo].[RES_ATTACHMENTS] where [attachmenttype] = 'COMMENTORDER' or [attachmenttype] = 'COMMENTRESULT'";
		
		PreparedStatement 	ps = con.prepareStatement(sql);
		ResultSet 			rs = ps.executeQuery();
		
		while (rs.next()) {
			String foldername = rs.getString(1);
			
			fset.add( foldername.substring(0, foldername.length()-4) );
			
			Proj	project = null;
			if( orderMap.containsKey( foldername ) ) {
				project = orderMap.get(foldername);
			} else {
				project = new Proj( rs.getString(1) );
				orderMap.put(foldername, project);
				folderList.add( project );
			}
			
			if( project != null ) {
				String comment = rs.getString(2);
				String attype = rs.getString(3);
				if( attype.contains("COMMENTORDER") ) {
					project._OrderComment = comment;
				} else {
					project._ResultComment = comment;
				}
			}
		}
		
		rs.close();
		ps.close();
		
		return folderList;
	}
	
	public void setBounds( int x, int y, int w, int h ) {
		super.setBounds( x, y, w, h );
		
		scrollpane.setBounds(0, 0, w/3, h);
		scrollarea.setBounds(w/3, 0, (2*w)/3, h);
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
		
		Window window = SwingUtilities.windowForComponent(this);
		if (window instanceof JFrame) {
			JFrame frame = (JFrame)window;
			if (!frame.isResizable()) frame.setResizable(true);
		}
		
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			String connectionUrl = "jdbc:sqlserver://130.208.252.230:1433;databaseName=PROD_UPG_DATA;user=limsadmin;password=starlims;";
			con = DriverManager.getConnection(connectionUrl);
			
			List<Proj> folders = getProjects();
			model = createModel( folders );
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		table.setAutoCreateRowSorter( true );
		if( model != null ) table.setModel( model );
		
		Set<TableColumn>			remcol = new HashSet<TableColumn>();
		Enumeration<TableColumn>	taben = table.getColumnModel().getColumns();
		while( taben.hasMoreElements() ) {
			TableColumn tc = taben.nextElement();
			if( tc.getIdentifier().toString().startsWith("_") ) {
				remcol.add( tc );
			}
		}
		
		for( TableColumn tc : remcol ) {
			table.removeColumn( tc );
		}
		
		table.getSelectionModel().addListSelectionListener( new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if( currentSelection != null ) {
					Proj	project = orderMap.get(currentSelection);
					if( project != null ) {
						project._OrderComment = textarea.getText();
						project._ResultComment = rtextarea.getText();
					}
				}
				
				int r = table.getSelectedRow();
				if( r != -1 ) {
					currentSelection = (String)table.getValueAt(r, 0);
					r = table.convertRowIndexToModel(r);
					String t = (String)model.getValueAt(r, 1);
					if( t != null ) textarea.setText( t );
					else {
						textarea.setText("");
					}
					
					t = (String)model.getValueAt(r, 2);
					if( t != null ) rtextarea.setText( t );
					else {
						rtextarea.setText("");
					}
				}
			}
		});
		
		scrollpane.setViewportView( table );
		scrollarea.setViewportView( textarea );
		rscrollarea.setViewportView( rtextarea );
		
		vista.setAction( new AbstractAction("Vista") {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Proj project = orderMap.get(currentSelection);
					if( project != null ) save( project, rtextarea.getText(), textarea.getText() );
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		for( String s : fset ) {
			combo.addItem( s );
		}
		
		combo.addItemListener( new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				String s = (String)combo.getSelectedItem();
				TableRowSorter<TableModel> trs = (TableRowSorter<TableModel>)table.getRowSorter();
				
				String filterText = "(?i).*" + s + ".*";
				trs.setRowFilter( RowFilter.regexFilter(filterText) );
			}
		});
		
		JComponent comp = new JComponent() {
			
		};
		comp.setLayout( new BorderLayout() );
		comp.add( scrollarea );
		comp.add( vista, BorderLayout.SOUTH );
		
		JComponent comp2 = new JComponent() {
			
		};
		comp2.setLayout( new BorderLayout() );
		comp2.add( scrollpane );
		comp2.add( combo, BorderLayout.NORTH );
		
		rsplitpane.setTopComponent( rscrollarea );
		rsplitpane.setBottomComponent( comp );
		
		splitpane.setLeftComponent( comp2 );
		splitpane.setRightComponent( rsplitpane );
		
		splitpane.setDividerLocation( 0.33 );
		
		this.add( splitpane );
	}
	
	public void destroy() {
		if( con != null )
			try {
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	public void save( Proj project, String rval, String oval ) throws SQLException {
		if( !rval.equals(project._ResultComment) ) {
			String sql = "update [PROD_UPG_DATA].[dbo].[RES_ATTACHMENTS] set [mycomment] = '"+rval+"' where [attachmenttype] = 'COMMENTRESULT' and [ordno] = '"+currentSelection+"'";
		
			PreparedStatement 	ps = con.prepareStatement(sql);
			boolean				b = ps.execute();
		
			ps.close();
			
			project._ResultComment = rval;
		}
		
		if( !oval.equals(project._OrderComment) ) {
			String sql = "update [PROD_UPG_DATA].[dbo].[RES_ATTACHMENTS] set [mycomment] = '"+oval+"' where [attachmenttype] = 'COMMENTORDER' and [ordno] = '"+currentSelection+"'";
		
			PreparedStatement ps = con.prepareStatement(sql);
			boolean b = ps.execute();
		
			ps.close();
			
			project._OrderComment = oval;
		}
	}
}
