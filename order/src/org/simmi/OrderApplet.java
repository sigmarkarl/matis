package org.simmi;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JApplet;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.Timer;
import javax.swing.ToolTipManager;

public class OrderApplet extends JApplet {
	JComponent 			c;
	JComponent			subc;
	JEditorPane			e;
	Image				img;
	Image				mimg;
	Image				fimg;
	Image				kimg;
	final Color			darkGray = new Color( 230,230,230 );
	final Color			lightGray = new Color( 250,250,250 );
	List<FancyButton> 	buttons;
	Timer				timer;
	float 				fval = 4.0f;
	final Dimension 	enormButtonDim = new Dimension(300, 300);
	final Dimension 	largeButtonDim = new Dimension(120, 150);
	final Dimension 	buttonDim = new Dimension(100, 128);
	MouseListener		ml;
	boolean				showing = true;
	int					val = 4;
	
	final Color			bgColor = new Color( 255, 255, 200 );
	
	String[] bNames = {"Dót", "Vinnsla", "Pökkun Geymsla", "Flutningur", "Dreifing"};
	Color[] bColors = { new Color(250,250,200), new Color(250,200,250), new Color(200,250,250), new Color(200,250,200), new Color(200,200,250) };
	
	public OrderApplet() {
		super();
		try {
			URL url = this.getClass().getResource("/smooth.png");
			img = ImageIO.read( url );
			url = this.getClass().getResource("/matis.png");
			mimg = ImageIO.read( url );
			url = this.getClass().getResource("/fiskur.png");
			fimg = ImageIO.read( url );
			url = this.getClass().getResource("/bkl.png");
			kimg = ImageIO.read( url );
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		buttons = new ArrayList<FancyButton>();
		ml = new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {}
		
			@Override
			public void mousePressed(MouseEvent e) {}
		
			@Override
			public void mouseExited(MouseEvent e) {
				FancyButton	fb = (FancyButton)e.getSource();
				fb.setPreferredSize( buttonDim );
				val = 4;
				if( !timer.isRunning() ) {
					timer.start();
				}
			}
		
			@Override
			public void mouseEntered(MouseEvent e) {
				FancyButton	fb = (FancyButton)e.getSource();
				fb.setPreferredSize( largeButtonDim );
				val = 4;
				if( !timer.isRunning() ) {
					timer.start();
				}
			}
		
			@Override
			public void mouseClicked(MouseEvent e) {
				FancyButton	fb = (FancyButton)e.getSource();
				fb.setPreferredSize( enormButtonDim );
			}
		};
		
		int i = 0;
		for( String name : bNames ) {
			FancyButton button = new FancyButton( name, fimg, new Font( "Arial", Font.PLAIN, 10 ), bColors[i] );
			buttons.add( button );
			i++;
		}
		//buttons.add( new FancyButton() ) {
	}
	
	public class FancyButton extends JComponent {
		Image 	img;
		String 	text;
		Color	color;
		
		public FancyButton( String name, Font font, Color color ) {
			super();
			this.text = name;
			this.color = color;
			init( font );
		}
		
		public FancyButton( String name, Image img, Font font, Color color ) {
			this( name, font, color );
			this.img = img;
		}
		
		public boolean isVisible() {
			return super.isVisible() && showing;
		}
		
		public boolean isShowing() {
			return super.isVisible() && showing; 
		}
		
		public void paintComponent( Graphics g ) {
			super.paintComponent( g );
			
			int h = this.getHeight();
			int w = this.getWidth();
			
			Graphics2D g2 = (Graphics2D)g;
			g2.setRenderingHint( RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY );
			g2.setRenderingHint( RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE );
			g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
			g2.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
			g2.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC );
			g2.setRenderingHint( RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE );
			
			//System.err.println( this.getSize() );
			Dimension d = this.getSize();
			if( Math.abs( d.width-enormButtonDim.width ) < 2 && Math.abs(d.height-enormButtonDim.height) < 2 ) {
				Color cd = color.darker();
				Paint p = g2.getPaint();
				GradientPaint gp = new GradientPaint(0.0f, 0.0f, cd.darker(), w, h, color );
				g2.setPaint( gp );
				g2.fillRoundRect(0, 0, w, h, 24, 24);
				g2.setPaint( p );
				
				Stroke stroke = g2.getStroke();
				BasicStroke bs = new BasicStroke( 2.0f );
				g2.setStroke( bs );
				g2.setColor( cd );
				g2.drawRoundRect(0, 0, w-1, h-1, 24, 24);
				g2.setStroke( stroke );
				
				gp = new GradientPaint(5.0f, 5.0f, color, w-10.0f, 30.0f, cd.darker() );
				g2.setPaint( gp );
				g2.fillRoundRect(5, 5, w-10, 30, 24, 24);
				g2.setPaint( p );
				
				g2.setStroke( bs );
				g2.setColor( cd );
				g2.drawRoundRect(5, 5, w-10, 30, 24, 24);
				g2.setStroke( stroke );
				
				g2.setFont( g2.getFont().deriveFont( Font.BOLD, 16.0f ) );
				g2.setColor( Color.gray );
				g2.drawString( text, 17, 28 );
				g2.setColor( Color.white );
				g2.drawString( text, 15, 26 );
				
				if( this == buttons.get(0) ) {
					g2.drawImage( kimg, (w-kimg.getWidth(this))/2, (h-kimg.getHeight(this))/2, this);
				}
			} else {
				Paint p = g2.getPaint();
				GradientPaint gp = new GradientPaint(0.0f, 0.0f, lightGray, 0.0f, h, darkGray );
				g2.setPaint( gp );
				g2.fillRoundRect(0, 0, w, h, 24, 24);
				g2.setPaint( p );
				
				Stroke stroke = g2.getStroke();
				BasicStroke bs = new BasicStroke( 2.0f );
				g2.setStroke( bs );
				g2.setColor( lightGray );
				g2.drawRoundRect(0, 0, w-1, h-1, 24, 24);
				g2.setStroke( stroke );
				
				String[] split = text.split(" ");
				int i = 0;
				g2.setColor( Color.gray );
				for( String s : split ) {
					g2.drawString( s, 8, h-split.length*11+i*11-2 );
					i++;
				}
				
				int iw = (5*w)/6;
				int ih = img.getHeight(this)*iw/img.getWidth(this);
				g2.drawImage( img, (w-iw)/2, 10, iw, ih, this );
			}
		}
		
		public void init( Font font ) {
			this.setFont( font );
			
			this.setPreferredSize( buttonDim );
			this.setSize( buttonDim );
			
			this.addMouseListener( ml );
		}
	}
	
	public void setBounds( int x, int y, int w, int h ) {
		if( c != null ) {
			c.setLocation( Math.max(0, (w-c.getWidth())/2 ), Math.max(0, (h-c.getHeight())/2 ) );
			e.setLocation( (w-c.getWidth())/2, (h+c.getHeight())/2 );
		}
		super.setBounds(x, y, w, h);
	}
	
	public void paint( Graphics g ) {
		super.paint(g);
	}
	
	public void init() {
		ToolTipManager.sharedInstance().setInitialDelay( 0 );
		
		this.setLayout( null );
		this.getContentPane().setLayout(null);
		this.setBackground( bgColor );
		this.getContentPane().setBackground( bgColor );
		
		Dimension d	= new Dimension(900,30);
		e = new JEditorPane();
		e.setEditable( false );
		e.setForeground( Color.white );
		e.setBackground( bgColor );
		e.setPreferredSize( d );
		e.setSize( d );
		e.setContentType("text/html");
		//e.setText("<html><body><center>Copyright 2009, Matis, ohf</center></body></html>");
		e.setText("<html><body><center><span style=\"color:gray\">Copyright 2009, Matis, ohf</span></center></body></html>");
		
		subc = new JComponent() {
			public void paintComponent( Graphics g ) {
				super.paintComponent(g);
				
				int h = this.getHeight();
				int w = this.getWidth();
				
				Graphics2D g2 = (Graphics2D)g;
				g2.setRenderingHint( RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY );
				g2.setRenderingHint( RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE );
				g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
				g2.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
				g2.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC );
				
				Paint p = g2.getPaint();
				GradientPaint gp = new GradientPaint(0.0f, 0.0f, lightGray, 0.0f, h, darkGray );
				g2.setPaint( gp );
				g2.fillRoundRect(0, 0, w, h, 24, 24);
				g2.setPaint( p );
				//g2.drawImage( img, 0, 0, w, h, 0, h, w, 0, this );
				
				Stroke stroke = g2.getStroke();
				BasicStroke bs = new BasicStroke( 2.0f );
				g2.setStroke( bs );
				g2.setColor( lightGray );
				g2.drawRoundRect(0, 0, w-1, h-1, 24, 24);
				g2.setStroke( stroke );
				
				String str = "PÖNTUNARKERFI MATÍS";
				g2.drawImage( mimg, 15, 15, 30, 30, this );
				g2.setFont( g2.getFont().deriveFont( Font.BOLD, 16.0f ) );
				g2.setColor( Color.gray );
				g2.drawString( str, 50, 36 );
			}
			
			public boolean isVisible() {
				return showing && super.isVisible();
			}
			
			public boolean isShowing() {
				return showing && super.isShowing();
			}
		};
		d = new Dimension(884,100);
		subc.setPreferredSize( d );
		subc.setSize( d );
		
		c = new JComponent() {
			public void paintComponent( Graphics g ) {
				super.paintComponent(g);
				
				int h = this.getHeight();
				int w = this.getWidth();
				
				Graphics2D g2 = (Graphics2D)g;
				g2.drawImage( img, 0, 0, w, h, this );
				
				String str = "Hvað viltu panta?";
				int strw = g2.getFontMetrics().stringWidth(str);
				g2.drawString(str, (w-strw)/2, 500);
			}
			
			public boolean isVisible() {
				return showing && super.isVisible();
			}
			
			public boolean isShowing() {
				return showing && super.isShowing();
			}
			
			public void setBounds( int x, int y, int w, int h) {				
				if( subc != null ) {
					subc.setLocation( Math.max(0, (w-subc.getWidth())/2 ), 8 );
					
					int i = 0;
					int b = 0;
					for( FancyButton fb : buttons ) {
						b += fb.getWidth()+5;
					}
					b -= 5;
					
					for( FancyButton fb : buttons ) {
						fb.setLocation( w/2-b/2+i, (h-buttonDim.height)/2 );
						i+=fb.getWidth()+5;
					}
				}
				super.setBounds(x, y, w, h );
			}
		};
		c.setFont( new Font( "Arial", Font.BOLD, 14 ) );
		c.setLayout( null );
		c.add( subc );
		
		d	= new Dimension(900,600);
		c.setPreferredSize( d );
		c.setSize( d );
		this.add( c );
		this.add( e );
		for( FancyButton fb : buttons ) {
			c.add( fb );
		}
		
		ActionListener al = new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				System.err.println("next");
				
				boolean update = false;
				showing = false;
				for( FancyButton fb : buttons ) {
					if( !fb.getSize().equals( fb.getPreferredSize() ) ) {
						update = true;
						
						Dimension d = fb.getPreferredSize();
						fb.setSize( fb.getWidth()+((1)*(d.width-fb.getWidth()))/val, fb.getHeight()+((1)*(d.height-fb.getHeight()))/val );
						
						//System.err.println( fb.getSize() + "   " + d );
					}
				}
				val = Math.max( 1, val-1 );
				
				if( update ) {
					int i = 0;
					int b = 0;
					for( FancyButton fb : buttons ) {
						b += fb.getWidth()+5;
					}
					b -= 5;
					
					for( FancyButton fb : buttons ) {
						fb.setLocation( c.getWidth()/2-b/2+i, (c.getHeight()-fb.getHeight())/2 );
						i+=fb.getWidth()+5;
					}
				} else {
					timer.stop();
				}
				showing = true;
				OrderApplet.this.repaint();
			}
		};
		timer = new Timer( 50, al );
		timer.setInitialDelay( 0 );
		timer.setCoalesce( true );
	}
	
	public boolean isVisible() {
		return showing && super.isVisible();
	}
	
	public boolean isShowing() {
		return showing && super.isShowing();
	}
	
	public void update( Graphics g ) {
		super.update(g);
	}
	
	public static void main(String[] args) {
		
	}
}
