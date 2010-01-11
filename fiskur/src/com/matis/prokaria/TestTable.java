package com.matis.prokaria;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class TestTable extends JTable {
	public Component prepare( TableCellRenderer renderer, int row, int column ) {
		return this.prepareRenderer(renderer, row, column);
	}
}
