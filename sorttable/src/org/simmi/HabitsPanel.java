package org.simmi;

import java.awt.Graphics;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import javax.management.timer.Timer;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class HabitsPanel extends JSplitPane {
	int min = 1;
	
	public HabitsPanel( String lang ) {
		super( JSplitPane.VERTICAL_SPLIT );
		
		JTabbedPane timelineTabPane = new JTabbedPane( JTabbedPane.RIGHT );
		JTable		timelineTable = new JTable();
		JScrollPane timelineScroll = new JScrollPane(timelineTable);
		
		JTable		timelineDataTable = new JTable();
		JScrollPane timelineDataScroll = new JScrollPane( timelineDataTable );
		
		JSplitPane	timelineSplit = new JSplitPane( JSplitPane.VERTICAL_SPLIT, timelineScroll, timelineDataScroll );
		
		if( lang.equals("IS") ) {
			timelineTabPane.addTab("Inn", timelineScroll);
			timelineTabPane.addTab("Út", null);
		} else {
			timelineTabPane.addTab("In", timelineScroll);
			timelineTabPane.addTab("Out", null);
		}
		
		Calendar now = Calendar.getInstance();
		final Calendar cal = new GregorianCalendar();
		cal.set( Calendar.YEAR, now.get( Calendar.YEAR ) );
		cal.set( Calendar.MONTH, 0 );
		cal.set( Calendar.DAY_OF_MONTH, 1 );
		final long time = cal.getTimeInMillis();
		
		//Date d = new Date(Date.);
		TableModel	timelineModel = new TableModel() {
			@Override
			public void addTableModelListener(TableModelListener arg0) {
				
			}

			@Override
			public Class<?> getColumnClass(int arg0) {
				return String.class;
			}

			@Override
			public int getColumnCount() {
				// TODO Auto-generated method stub
				return 365;
			}

			@Override
			public String getColumnName(int arg0) {
				cal.setTimeInMillis( time+arg0*Timer.ONE_DAY );
				return cal.get( Calendar.WEEK_OF_YEAR ) + "";
			}

			@Override
			public int getRowCount() {
				// TODO Auto-generated method stub
				return 2;
			}

			@Override
			public Object getValueAt(int arg0, int arg1) {
				cal.setTimeInMillis( time+arg1*Timer.ONE_DAY );
				String str;
				if( arg0 == 1 ) str = cal.get(Calendar.DAY_OF_MONTH) + ". "+cal.getDisplayName( Calendar.MONTH, Calendar.SHORT, Locale.getDefault());
				else str = cal.getDisplayName( Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault());
				return str;
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
		};
		timelineTable.setModel( timelineModel );
		timelineTable.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );
		
		TableModel	timelineDataModel = new TableModel() {
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
				return 365;
			}

			@Override
			public String getColumnName(int columnIndex) {
				return null;
			}

			@Override
			public int getRowCount() {
				// TODO Auto-generated method stub
				return min;
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				// TODO Auto-generated method stub
				return null;
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
		
		JComponent drawer = new JComponent() {
			public void paintComponent( Graphics g ) {
				super.paintComponent( g );
			}
		};
		JScrollPane timelineDrawScroll = new JScrollPane( drawer );
		
		this.setTopComponent( timelineTabPane );
		this.setBottomComponent( timelineDrawScroll );
	}
}
