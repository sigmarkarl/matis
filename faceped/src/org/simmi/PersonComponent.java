package org.simmi;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;

public class PersonComponent extends JComponent implements MouseListener, MouseMotionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String 	name;
	Date	birth;
	boolean	male;
	Image	image;
	
	PersonComponent	mother = null;
	PersonComponent	father = null;
	
	Set<PersonComponent>	children = new HashSet<PersonComponent>();
	
	Point	p;
	boolean selected = false;
	
	static int 				size = 40;
	static PersonComponent	drag;
	static Color paleColor = new Color( 255,255,255,128 );
	static List<PersonComponent>	selectedList = new ArrayList<PersonComponent>();
	
	public PersonComponent( String name, boolean male, Point startPoint, final String imageUrl ) {
		super();
		
		this.setOpaque( false );
		
		this.male = male;
		this.name = name;
		this.setBounds( startPoint.x, startPoint.y, size, size);
		
		if( imageUrl != null ) {
			new Thread() {
				public void run() {
	        		Image img;
					try {
						img = ImageIO.read( new URL( imageUrl ) );
						PersonComponent.this.setImage( img );
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}.start();
		}
		
		this.addMouseListener( this );
		this.addMouseMotionListener( this );
		
		JPopupMenu popup = new JPopupMenu();
		popup.add( new AbstractAction("Add Mother") {
			public void actionPerformed(ActionEvent e) {
				Point p = PersonComponent.this.getLocation();
				p.translate(0, 2*PersonComponent.this.getHeight());
				PersonComponent.this.setParent( new PersonComponent("mother",false,p,null) );
			}
		});
		popup.add( new AbstractAction("Add Father") {
			public void actionPerformed(ActionEvent e) {
				Point p = PersonComponent.this.getLocation();
				p.translate(0, 2*PersonComponent.this.getHeight());
				PersonComponent.this.setParent( new PersonComponent("father",true,p,null) );
			}
		});
		popup.add( new AbstractAction("Add Daughter") {
			public void actionPerformed(ActionEvent e) {
				Point p = PersonComponent.this.getLocation();
				p.translate(0, 2*PersonComponent.this.getHeight());
				PersonComponent.this.addChild( new PersonComponent("daughter",false,p,null) );
			}
		});
		popup.add( new AbstractAction("Add Son") {
			public void actionPerformed(ActionEvent e) {
				Point p = PersonComponent.this.getLocation();
				p.translate(0, 2*PersonComponent.this.getHeight());
				PersonComponent.this.addChild( new PersonComponent("son",true,p,null) );
			}
		});
		this.setComponentPopupMenu(popup);
	}
	
	public void setImage( Image img ) {
		image = img;
		repaint();
	}
	
	public PersonComponent getFather() {
		return this.father;
	}
	
	public PersonComponent getMother() {
		return this.mother;
	}
	
	public void setFather( PersonComponent father ) {
		if( !father.isChildOf( this ) ) {
			if( this.father != null ) {
				this.father.getChildren().remove( this );
			}
			
			this.father = father;
			father.children.add( this );
			
			if( this.getParent() != null ) {
				this.getParent().add( father );
				this.getParent().repaint();
			}
		}
	}
	
	public void setMother( PersonComponent mother ) {
		if( !mother.isChildOf( this ) ) {
			if( this.mother != null ) {
				this.mother.getChildren().remove( this );
			}
			
			this.mother = mother;
			mother.children.add( this );
			
			if( this.getParent() != null ) {
				this.getParent().add( mother );
				this.getParent().repaint();
			}
		}
	}
	
	public void setParent( PersonComponent parent ) {
		if( parent.male ) this.setFather( parent );
		else this.setMother( parent );
	}
	
	public void addChild( PersonComponent child ) {
		//children.add( child );
		if( male ) child.setFather( this );
		else child.setMother( this );
		
		if( this.getParent() != null ) {
			this.getParent().add( child );
			this.getParent().repaint();
		}
	}
	
	public boolean hasAncestor( PersonComponent ancestor ) {
		return (father != null && ancestor == father || father.hasAncestor(ancestor)) || (mother != null && ancestor == mother || mother.hasAncestor(ancestor));
	}
	
	public boolean isAncestorOf( PersonComponent person ) {
		return person.hasAncestor( this );
	}
	
	public boolean hasChild( PersonComponent child ) {
		if( children.contains( child ) ) return true;
		else {
			for( PersonComponent c : children ) {
				if( c.hasChild(child) ) return true;
			}
		}
		return false;
	}
	
	public boolean isChildOf( PersonComponent person ) {
		return person.hasChild( this );
	}
	
	public Set<PersonComponent>	getChildren() {
		return children;
	}
	
	public void paintComponent( Graphics g ) {
		super.paintComponent(g);
		
		Graphics2D	g2 = (Graphics2D)g;
		g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
		g2.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
		
		if( image != null ) {
			int val = image.getWidth(this)*size/image.getHeight(this);
			g.drawImage( image, 0, 0, size, size, this);
			
			if( male ) {
				g.setColor( Color.blue );
				g.fillRect(size-10, 1, 8, 8 );
			} else {
				g.setColor( Color.red );
				g.fillOval(size-10, 1, 8, 8 );
			}
		} else {
			if( male ) {
				g.setColor( Color.blue );
				g.fillRect(1, 1, this.getWidth()-2, this.getHeight()-2 );
			} else {
				g.setColor( Color.red );
				g.fillOval(1, 1, this.getWidth()-2, this.getHeight()-2 );
			}
		}
		
		if( selected ) {
			g2.setColor( paleColor );
			g2.fillRect( 0, 0, this.getWidth(), this.getHeight() );
		}
	}

	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	boolean dragging = false;
	public void mousePressed(MouseEvent e) {
		dragging = e.getModifiers() == MouseEvent.BUTTON1_MASK && e.getClickCount() != 2;
		p = e.getPoint();
		this.selected = true;
		this.getParent().setComponentZOrder(this, 0);
		drag = this;
		this.repaint();
	}

	public void mouseReleased(MouseEvent e) {
		Point p = e.getPoint();
		p.translate(drag.getX(), drag.getY());
		Component c = this.getParent().getComponentAt( p );
		if( c != null && c instanceof PersonComponent && c != drag ) {
			PersonComponent person = (PersonComponent)c;
			drag.addChild( person );
		}
		this.p = null;
		//drag.addChild( this );
		this.getParent().repaint();
	}
	
	public void moveRelative( int x, int y, boolean recursive ) {
		Point	loc = this.getLocation();
		this.setLocation(loc.x+x, loc.y+y);
		if( recursive ) {
			for( PersonComponent c : this.getChildren() ) {
				c.moveRelative( x, y, recursive );
			}
		}
	}

	public void mouseDragged(MouseEvent e) {
		if( dragging ) {
			Point	np = e.getPoint();
			if( selectedList.size() == 0 || !selectedList.contains(this) ) moveRelative( np.x-p.x, np.y-p.y, true );
			else {
				for( PersonComponent pc : selectedList ) {
					pc.moveRelative( np.x-p.x, np.y-p.y, false );
				}
			}
		} else {
			p = e.getPoint();
		}
		this.getParent().repaint();
	}

	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}
