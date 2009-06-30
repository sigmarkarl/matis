package org.simmi;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.AbstractAction;
import javax.swing.JApplet;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Spilling extends JApplet implements MouseListener, MouseMotionListener {
	JComponent		c = null;
	Rectangle 		selRect = null;
	static Color 	selColor = new Color( 0,128,255,32 );
	
	public void init() {
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
			//SwingUtilities.updateComponentTreeUI( this );
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
		this.getContentPane().setBackground( Color.white );
		
		c = new JComponent() {
			public void paintComponent( Graphics g ) {
				super.paintComponent( g );
				
				Graphics2D	g2 = (Graphics2D)g;
				g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
				g2.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
				
				for( Component c : this.getComponents() ) {
					if( c instanceof Corp ) {
						Corp corp = (Corp)c;
						int strWidth = g.getFontMetrics().stringWidth( corp.name );
						g.drawString( corp.name, c.getX()+(c.getWidth()-strWidth)/2, c.getY()+c.getHeight()+15 );
						
						g.setColor( Color.darkGray );
						for( Corp cc : corp.getLinks() ) {
							int x1 = c.getX()+c.getWidth()/2;
							int y1 = c.getY()+c.getHeight()/2;
							int x2 = cc.getX()+cc.getWidth()/2;
							int y2 = cc.getY()+cc.getHeight()/2;
							g.drawLine( x1, y1, x2, y2 );
							
							String str = corp.connections.get(cc);
							int strw = g.getFontMetrics().stringWidth( str );
							
							int x = (x1+x2)/2;
							int y = (y1+y2)/2;
							double t = Math.atan2( y2-y1, x2-x1 );
							g2.rotate(t, x, y);
							g.drawString( corp.connections.get(cc), x-strw/2, y-5 );
							g2.rotate(-t, x, y);
						}
					}
				}
				
				Corp c = Corp.drag;
				if( c != null && c.p != null ) {
					g2.drawLine( c.getX()+c.getWidth()/2, c.getY()+c.getHeight()/2, c.getX()+c.p.x, c.getY()+c.p.y );
				}
				
				if( selRect != null ) {
					g2.setColor( selColor );
					g2.fillRect( selRect.x, selRect.y, selRect.width, selRect.height );
				}
			}
		};
		c.addMouseListener( this );
		c.addMouseMotionListener( this );
		
		final JPopupMenu	popup = new JPopupMenu();
		popup.add( new AbstractAction("Add Person") {

			@Override
			public void actionPerformed(ActionEvent e) {
				Corp 		corp = new Corp("unkown","person",m.x,m.y);
				c.add( corp );
				c.repaint();
			}
		});
		popup.add( new AbstractAction("Add Corp") {

			@Override
			public void actionPerformed(ActionEvent e) {
				Corp 		corp = new Corp("unknown","corp",m.x,m.y);
				c.add( corp );
				c.repaint();
			}
		});
		c.setComponentPopupMenu(popup);
		
		this.add( c );
	}

	Point	m = new Point(0,0);
	@Override
	public void mouseDragged(MouseEvent e) {
		Point np = e.getPoint();
		if( e.isShiftDown() ) {
			Rectangle rect = c.getVisibleRect();			
			rect.translate( p.x-np.x, p.y-np.y );
			c.scrollRectToVisible( rect );
		} else {
			selRect = new Rectangle( Math.min(p.x, np.x), Math.min(p.y, np.y), Math.abs(p.x-np.x), Math.abs(p.y-np.y) );
			c.repaint();
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		m = e.getPoint();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	Point p;
	@Override
	public void mousePressed(MouseEvent e) {
		p = e.getPoint();
		Corp.selectedList.clear();
		for( Component comp : c.getComponents() ) {
			if( comp instanceof Corp ) {
				((Corp)comp).selected = false;
			}
		}
		c.remove( Corp.textfield );
		c.repaint();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if( selRect != null ) {
			for( Component cc : c.getComponents() ) {
				if( cc instanceof Corp ) {
					Corp corp = (Corp)cc;
					if( corp.getBounds().intersects(selRect) ) {
						corp.selected = true;
						Corp.selectedList.add(corp);
					}
					else corp.selected = false;
				}
			}
			selRect = null;
		}
		c.repaint();
	}
}
