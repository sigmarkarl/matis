package org.simmi;

import java.sql.Date;
import java.util.Calendar;

import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class HabitsPanel extends JSplitPane {
	
	public HabitsPanel() {
		super( JSplitPane.VERTICAL_SPLIT );
		
		JTable		timelineTable = new JTable();
		JScrollPane timelineScroll = new JScrollPane(timelineTable);
		
		//Calendar.getInstance().set
		//Date d = new Date(Date.);
		TableModel	timelineModel = new TableModel() {
			@Override
			public void addTableModelListener(TableModelListener arg0) {
				
			}

			@Override
			public Class<?> getColumnClass(int arg0) {
				return Date.class;
			}

			@Override
			public int getColumnCount() {
				// TODO Auto-generated method stub
				return 365;
			}

			@Override
			public String getColumnName(int arg0) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public int getRowCount() {
				// TODO Auto-generated method stub
				return 1;
			}

			@Override
			public Object getValueAt(int arg0, int arg1) {
				// TODO Auto-generated method stub
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
		};
	}
}
