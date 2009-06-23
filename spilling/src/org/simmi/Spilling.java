package org.simmi;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JApplet;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;

public class Spilling extends JApplet {
	
	
	public void init() {
		final JComponent	c = new JComponent() {
			public void paintComponent( Graphics g ) {
				super.paintComponent( g );
			}
		};
		
		final JPopupMenu	popup = new JPopupMenu();
		popup.add( new AbstractAction("Add Person") {

			@Override
			public void actionPerformed(ActionEvent e) {
				Corp 		corp = new Corp("",p.x,p.y);
				c.add( corp );
			}
			
		});
		c.setComponentPopupMenu(popup);
		
		this.add( c );
	}
}
