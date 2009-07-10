package org.simmi;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JApplet;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Spilling extends JApplet implements MouseListener, MouseMotionListener, KeyListener {
	JComponent			c = null;
	Rectangle 			selRect = null;
	static Color 		selColor = new Color( 0,128,255,32 );
	Corp				linkCorp = null;
	Corp				linkCorp2 = null;
	
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
		
		Corp.prop = new Prop();
		Corp.prop.setBounds(0, 0, 400, 75);
		
		JScrollPane	scrollpane = new JScrollPane();
		c = new JComponent() {
			public void paintComponent( Graphics g ) {
				super.paintComponent( g );
				
				g.setColor( Color.white );
				g.fillRect(0, 0, this.getWidth(), this.getHeight());
				
				Graphics2D	g2 = (Graphics2D)g;
				g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
				g2.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
				
				Font	oldFont = g2.getFont();
				for( Component c : this.getComponents() ) {
					if( c instanceof Corp ) {
						Corp corp = (Corp)c;
						int strWidth = g.getFontMetrics().stringWidth( corp.name );
						
						g.setColor( Color.black );
						g.drawString( corp.name, c.getX()+(c.getWidth()-strWidth)/2, c.getY()+c.getHeight()+15 );
						
						g.setColor( Color.darkGray );
						for( Corp cc : corp.getLinks() ) {
							int x1 = c.getX()+c.getWidth()/2;
							int y1 = c.getY()+c.getHeight()/2;
							int x2 = cc.getX()+cc.getWidth()/2;
							int y2 = cc.getY()+cc.getHeight()/2;
							g.drawLine( x1, y1, x2, y2 );
							
							Set<String> strset = corp.connections.get(cc);
							int x = (x1+x2)/2;
							int y = (y1+y2)/2;
							double t = Math.atan2( y2-y1, x2-x1 );
							g2.rotate(t, x, y);
							int k = 0;
							for( String str : strset ) {
								int strw = g.getFontMetrics().stringWidth( str );
								//if( corp.selectedLink != null ) System.err.println( corp.selectedLink );
								if( cc == linkCorp2 && str.equals( corp.selectedLink ) ) {
									g2.setFont( g2.getFont().deriveFont( Font.BOLD ) );
								}
								g.drawString( str, x-strw/2, y-5-k );
								if( g2.getFont() != oldFont ) g2.setFont( oldFont );
								k += 10;
							}
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
		c.addKeyListener( this );
		
		try {
			loadAll();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		c.setPreferredSize( new Dimension(this.getWidth(), this.getHeight()) );
		c.addContainerListener( new ContainerListener() {
			@Override
			public void componentRemoved(ContainerEvent e) {
				Component c = e.getChild();
				if( c instanceof Prop ) {
					Prop p = (Prop)c;
					if( p.currentCorp != null ) {
						p.currentCorp.name = p.name.getText();
						p.currentCorp.kt = p.kt.getText();
						
						try {
							p.currentCorp.save();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}
			}
			
			@Override
			public void componentAdded(ContainerEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		final JPopupMenu	popup = new JPopupMenu();
		popup.add( new AbstractAction("Add Person") {

			@Override
			public void actionPerformed(ActionEvent e) {
				Corp 		corp = new Corp("unknown","person",m.x,m.y);
				try {
					corp.save();
				} catch (IOException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				c.add( corp );
				c.repaint();
			}
		});
		popup.add( new AbstractAction("Add Corp") {

			@Override
			public void actionPerformed(ActionEvent e) {
				Corp 		corp = new Corp("unknown","corp",m.x,m.y);
				try {
					corp.save();
				} catch (IOException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				c.add( corp );
				c.repaint();
			}
		});
		c.setComponentPopupMenu(popup);
		
		scrollpane.setViewportView( c );
		this.add( scrollpane );
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
	
	public void saveAll() throws IOException {
		for( Component c : this.getComponents() ) {
			if( c instanceof Corp ) {
				Corp corp = (Corp)c;
				corp.save();
			}
		}
	}
	
	public void reloadAll() throws IOException {
		for( Component c : this.getComponents() ) {
			if( c instanceof Corp ) {
				Corp corp = (Corp)c;
				corp.load();
			}
		}
	}
	
	public void loadAll() throws IOException {
		String homedir = System.getProperty("user.home");
		File dir = new File( homedir, "spoil" );
		if( dir.exists() ) {
			File[] ff = dir.listFiles();
			for( File f : ff ) {
				Corp corp = new Corp( Integer.parseInt(f.getName()) );
				c.add( corp );
				//addFile( f );
			}
			
			for( Integer i : Corp.corpMap.keySet() ) {
				Corp c = Corp.corpMap.get(i);
				c.load();
			}
		}
	}
	
	public void addCorp( int id ) throws IOException {
		c.add( load( id ) );
	}
	
	public void addFile( File save ) throws IOException {
		c.add( loadFile( save ) );
	}
	
	public Corp load( int id ) throws IOException {
		Corp corp = new Corp( id );
		if( Corp.idx > id ) Corp.idx = id;
		corp.load();
		return corp;
	}
	
	public Corp loadFile( File save ) throws IOException {
		int id = Integer.parseInt( save.getName() );
		Corp corp = new Corp( id );
		if( Corp.idx > id ) Corp.idx = id;
		corp.loadFile( save );
		return corp;
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
		c.requestFocus();
		
		p = e.getPoint();
		Corp.selectedList.clear();
		linkCorp = null;
		linkCorp2 = null;
		for( Component comp : c.getComponents() ) {
			if( comp instanceof Corp ) {
				Corp corp = ((Corp)comp);
				corp.selected = false;
				corp.selectedLink = null;
				for( Corp corp2 : corp.connections.keySet() ) {						
					double x1 = corp.x + corp.getWidth()/2;
					double x2 = corp2.x + corp2.getWidth()/2;
					double y1 = corp.y + corp.getHeight()/2;
					double y2 = corp2.y + corp2.getHeight()/2;
					double xx = (x1 + x2) / 2.0;
					double yy = (y1 + y2) / 2.0;
					
					double h1 = Math.atan2( x1-xx, y1-yy );
					//if( h1 < 0 ) h1 += 2*Math.PI;
					double h2 = Math.atan2( p.x-xx, p.y-yy );
					//if( h2 < 0 ) h2 += 2*Math.PI;
					double h = h2 - h1;
					if( h > Math.PI ) h -= 2*Math.PI;
					if( h < -Math.PI ) h += 2*Math.PI;
					System.err.println( (x1) + "  " + (y1) );
					System.err.println( (x2) + "  " + (y2) );
					System.err.println( (x1-xx) + "  " + (y1-yy) );
					System.err.println( h1+ "  " + h2 + "  " + h );
					if( h < 0 ) {
						Set<String>	strset = corp.connections.get(corp2);
						if( strset != null && strset.size() > 0 && p.distance( xx, yy ) < 32 ) {
							linkCorp = corp;
							linkCorp2 = corp2;
							corp.selectedLink = corp.connections.get(corp2).iterator().next();
						}
					}
				}
			}
		}
		//c.remove( Corp.textfield );
		if( Corp.prop != null ) {
			Corp.prop.currentCorp = null;
			c.remove( Corp.prop );
		}
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
		} else {
			if( Corp.drag != null ) {
				Corp corp = new Corp();
				c.add( corp );
				Corp.drag.addLink( corp );
				Corp.drag = null;
			}
		}
		c.repaint();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if( linkCorp != null ) {
			if( e.getKeyCode() == KeyEvent.VK_DELETE ) {
				Set<String>	strset = linkCorp.connections.get( linkCorp2 );
				strset.remove( linkCorp.selectedLink );
				if( strset.size() == 0 ) linkCorp.connections.remove( linkCorp2 );
				linkCorp.selectedLink = null;
				linkCorp = null;
				linkCorp2 = null;
			} else if( e.getKeyCode() == KeyEvent.VK_ENTER ) {
				try {
					linkCorp.save();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				linkCorp.selectedLink = null;
				linkCorp = null;
				linkCorp2 = null;
			} else if( e.getKeyCode() == KeyEvent.VK_BACK_SPACE ) {
				Set<String>	strset = linkCorp.connections.get( linkCorp2 );
				strset.remove( linkCorp.selectedLink );
				if( linkCorp.selectedLink.length() > 0 ) {
					linkCorp.selectedLink = linkCorp.selectedLink.substring(0, linkCorp.selectedLink.length()-1);
					strset.add( linkCorp.selectedLink );
				} /*else if( strset.size() == 0 ) {
					linkCorp.connections.remove( linkCorp2 );
				}*/
			} else if( e.getKeyCode() != KeyEvent.VK_ALT && e.getKeyCode() != KeyEvent.VK_CONTROL && e.getKeyCode() != KeyEvent.VK_SHIFT ) {
				Set<String>	strset = linkCorp.connections.get( linkCorp2 );
				strset.remove( linkCorp.selectedLink );
				if( linkCorp.selectedLink.equals("link") ) linkCorp.selectedLink = "";
				linkCorp.selectedLink += e.getKeyChar();
				strset.add( linkCorp.selectedLink );
			}
			c.repaint();
		} else {
			if( e.getKeyCode() == KeyEvent.VK_DELETE ) {
				Set<Corp>	delset = new HashSet<Corp>();
				for( Integer i : Corp.corpMap.keySet() ) {
					Corp c = Corp.corpMap.get(i);
					if( c.selected ) delset.add( c );
				}
				
				for( Corp c : delset ) {
					c.delete();
				}
			}
			c.repaint();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
}
