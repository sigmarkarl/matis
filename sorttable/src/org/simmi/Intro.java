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
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JApplet;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

public class Intro extends JApplet {
	JComponent 			c;
	JComponent			subc;
	//JEditorPane			e;
	JEditorPane			ed;
	BufferedImage		img;
	Image				mimg;
	Image				fimg;
	Image				kimg;
	Image[]				imgs = new Image[5];
	final Color			darkGray = new Color( 230,230,230 );
	final Color			lightGray = new Color( 250,250,250 );
	List<FancyButton> 	buttons;
	Timer				timer;
	float 				fval = 4.0f;
	final Dimension 	enormButtonDim = new Dimension(300, 300);
	final Dimension 	largeButtonDim = new Dimension(120, 150);
	final Dimension 	buttonDim = new Dimension(100, 128);
	MouseListener		ml;
	
	final Color			bgColor = new Color( 255, 255, 200 );
	
	String[] bNames = {"Matargrunnur", "Myndrænt", "Upskriftir", "Plan", "Innkaup"};
	Color[] bColors = { new Color(250,250,200), new Color(250,200,250), new Color(200,250,250), new Color(200,250,200), new Color(200,200,250) };
	
	public Intro() {
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
			
			/*url = this.getClass().getResource("/isgem.png");
			imgs[0] = ImageIO.read( url );
			url = this.getClass().getResource("/isgem_img.png");
			imgs[1] = ImageIO.read( url );
			url = this.getClass().getResource("/isgem_mynd2.png");
			imgs[2] = ImageIO.read( url );
			url = this.getClass().getResource("/isgem_mynd2.png");
			imgs[3] = ImageIO.read( url );
			url = this.getClass().getResource("/isgem_base.png");
			imgs[4] = ImageIO.read( url );*/
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		buttons = new ArrayList<FancyButton>();
		ml = new MouseListener() {
			public void mouseReleased(MouseEvent e) {}
			public void mousePressed(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {
				FancyButton	fb = (FancyButton)e.getSource();
				fb.setPreferredSize( buttonDim );
			}
			public void mouseEntered(MouseEvent e) {
				FancyButton	fb = (FancyButton)e.getSource();
				fb.setPreferredSize( largeButtonDim );
				if( timer != null ) timer.start();
			}
			public void mouseClicked(MouseEvent e) {
				FancyButton	fb = (FancyButton)e.getSource();
				fb.setPreferredSize( enormButtonDim );
			}
		};
		
		int i = 0;
		for( String name : bNames ) {
			buttons.add( new FancyButton( name, fimg, new Font( "Arial", Font.PLAIN, 10 ), bColors[i] ) );
			i++;
		}
		//buttons.add( new FancyButton() ) {
	}
	
	public void selectTabIndex( int index ) {
		if( st != null ) st.selectTabIndex(index);
	}
	
	public void selectTabName( String name ) {
		if( st != null ) st.selectTabName( name );
	}
	
	public void updateFriends( String sessionKey, String currentUser ) {
		if( st != null ) st.updateFriends(sessionKey, currentUser);
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
				
				Image timg = null;
				if( this == buttons.get(0) ) timg = imgs[0];
				else if( this == buttons.get(1) ) timg = imgs[1];
				else if( this == buttons.get(2) ) timg = imgs[2];
				else if( this == buttons.get(3) ) timg = imgs[3];
				else if( this == buttons.get(4) ) timg = imgs[4];
				
				if( timg != null ) g2.drawImage( timg, (w-150)/2, (h-120)/2, 150, 120, this);
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
			int cw = 22*w/24;
			int ch = 16*h/18;
			c.setBounds( Math.max(0, (w-cw)/2 ), Math.max(0, (h-ch)/2 ), cw, ch );
			//e.setBounds( (w-cw)/2, (h+ch)/2, cw, 30 );
			if( ed != null ) ed.setBounds( (w-cw)/2, (h+ch)/2, cw, 60 );
		}
		super.setBounds(x, y, w, h);
	}
	
	public void paint( Graphics g ) {
		super.paint(g);
	}
	
	SortTable	st;
	public void init() {
		this.setBackground( bgColor );
		this.setLayout( null );
		this.getContentPane().setLayout(null);
		this.getContentPane().setBackground( bgColor );
		
		Window window = SwingUtilities.windowForComponent(this);
		if (window instanceof JFrame) {
			JFrame frame = (JFrame)window;
			if (!frame.isResizable()) frame.setResizable(true);
		}
		
		st = new SortTable();
		st.firstInit();
		try {
			st.sessionKey = Intro.this.getParameter("fb_sig_session_key");
	        st.currentUser = Intro.this.getParameter("fb_sig_user");
		} catch( Exception e ) {
			e.printStackTrace();
		}
		
		/*Dimension d	= new Dimension(900,30);
		e = new JEditorPane();
		e.setEditable( false );
		e.setBackground( new Color(0,0,0,0) );
		e.setPreferredSize( d );
		e.setSize( d );
		e.setContentType("text/html");
		//e.setText("<html><body><center>Copyright 2009, Matis, ohf</center></body></html>");
		e.setText("<html><body><center><span style=\"color:white\">Copyright 2009, Matis, ohf</span></center></body></html>");*/
		
		ed = new JEditorPane();
		ed.setContentType("text/html");
		ed.setEditable(false);
		ed.setText("<html><body><center><table cellpadding=0><tr><td><img src=\"http://test.matis.is/isgem/Matis_logo.jpg\" hspace=\"5\" width=\"32\" height=\"32\">"
						+ "</td><td align=\"center\"><a href=\"http://www.matis.is\">Matís ohf.</a> - Borgartún 21 | 105 Reykjavík - Sími 422 50 00 | Fax 422 50 01 - <a href=\"mailto:matis@matis.is\">matis@matis.is</a><br><a href=\"http://www.matis.is/ISGEM/is/skyringar/\">Hjálp</a> - "
						+ ((st.sessionKey != null && st.sessionKey.length() > 1) ? "<a href=\"http://test.matis.is/isgem\">Allur glugginn</a>"
								: "<a href=\"http://apps.facebook.com/matisgem\">Facebook</a>")
						// +" - <a href=\"dark\">Dark</a> - <a href=\"light\">Light</a>"
						+ "</td></tr></table></center></body></html>");
		Dimension d = new Dimension(1000, 42);
		ed.setPreferredSize(d);
		ed.setSize(d);
		ed.addHyperlinkListener(new HyperlinkListener() {
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
					if (e.getDescription().equals("dark")) {
						CompatUtilities.lof = "org.jvnet.substance.skin.SubstanceRavenGraphiteLookAndFeel";
						CompatUtilities.updateLof();
					} else if (e.getDescription().equals("light")) {
						CompatUtilities.lof = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
						CompatUtilities.updateLof();
					} else {
						try {
							CompatUtilities.browse( e.getURL() );
						} catch (URISyntaxException e1) {
							e1.printStackTrace();
						} catch (IOException e1) {
							e1.printStackTrace();
						}					
					}
				}
			}
		});
		
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
				
				String str = "ÍSGEM - Facebook";
				g2.drawImage( mimg, 15, 15, 30, 30, this );
				g2.setFont( g2.getFont().deriveFont( Font.BOLD, 16.0f ) );
				g2.setColor( Color.lightGray );
				g2.drawString( str, 50, 36 );
				
				if( st != null ) {
					int i = st.tabbedPane.getSelectedIndex();
					if( i >= 0 ) {
						str = st.tabbedPane.getTitleAt( i );
						//g2.drawImage( mimg, 15, 15, 30, 30, this );
						int strw = g2.getFontMetrics().stringWidth( str );
						g2.setFont( g2.getFont().deriveFont( Font.BOLD, 24.0f ) );
						g2.setColor( Color.lightGray );
						g2.drawString( str, (this.getWidth()-strw)/2, 64 );
						
						g2.setFont( g2.getFont().deriveFont( Font.BOLD, 12.0f ) );
						if( str.equals("Fæða") ) {
							str = "Veljið dálka til að raða fæðutegundum";
							strw = g2.getFontMetrics().stringWidth( str );
							g2.drawString( str, this.getWidth()-strw-20, 70 );
							str = "eftir magni næringarefna";
							strw = g2.getFontMetrics().stringWidth( str );
							g2.drawString( str, this.getWidth()-strw-20, 85 );
						} else if( str.equals("Samsetning") ) {
							str = "Samantekt á næringarefnum í fæðutegundum";
							strw = g2.getFontMetrics().stringWidth( str );
							g2.drawString( str, this.getWidth()-strw-20, 70 );
							str = "Veljið fæðutegundina í töflunni vinstra megin";
							strw = g2.getFontMetrics().stringWidth( str );
							g2.drawString( str, this.getWidth()-strw-20, 85 );
						}
					}
				}
			}
		};
		
		/*d = new Dimension(884,100);
		subc.setPreferredSize( d );
		subc.setSize( d );*/
		
		c = new JComponent() {
			public void paintComponent( Graphics g ) {
				super.paintComponent(g);
				
				int h = this.getHeight();
				int w = this.getWidth();
				
				Graphics2D g2 = (Graphics2D)g;
				g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
				
				Rectangle cliprect = g2.getClipBounds();
				//System.err.println( g.getClipBounds() );
				g2.drawImage( img, 0, 0, w, h, this );
				
				/*int x1 = (cliprect.x*img.getWidth())/w-1;
				int y1 = (cliprect.y*img.getHeight())/h-1;
				int x2 = ((cliprect.x+cliprect.width)*img.getWidth())/w+1;
				int y2 = ((cliprect.y+cliprect.height)*img.getHeight())/h+1;
				
				int ix1 = (x1*w)/img.getWidth();
				int iy1 = (y1*h)/img.getHeight();
				int ix2 = (x2*w)/img.getWidth();
				int iy2 = (y2*h)/img.getHeight();
				
				g2.drawImage( img, ix1, iy1, ix2-ix1, iy2-iy1, x1, y1, x2-x1, y2-y1, this );*/
			}
			
			public void setBounds( int x, int y, int w, int h) {				
				if( subc != null ) {
					//int cw = (9*w)/10;
					//int ch = (9*h)/10;
					subc.setBounds( 8, 8, w-16, 100 );
					
					/*int i = 0;
					int b = 0;
					for( FancyButton fb : buttons ) {
						b += fb.getWidth()+5;
					}
					b -= 5;*/
					
					if( st.splitPane != null ) st.splitPane.setBounds(10, 115, w-20, h-125 );
					/*for( FancyButton fb : buttons ) {
						fb.setLocation( w/2-b/2+i, (h-buttonDim.height)/2 );
						i+=fb.getWidth()+5;
					}*/
				}
				super.setBounds(x, y, w, h );
			}
		};
		c.setLayout( null );
		
		SwingUtilities.invokeLater( new Runnable(){
			public void run() {				
				try {
					Dimension d	= new Dimension(900,600);
					c.setPreferredSize( d );
					c.setSize( d );
					st.initGui( st.sessionKey, st.currentUser );
					c.add( subc );
					c.add(st.splitPane);
					
					st.tabbedPane.addChangeListener( new ChangeListener() {
						public void stateChanged(ChangeEvent e) {
							subc.repaint();
						}
					});
					
					Intro.this.add( c );
					
					if( ed != null ) {
						ed.setBackground( new Color(0,0,0,0) );
						Intro.this.add( ed );
					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}				
			}
		});
		
		/*for( FancyButton fb : buttons ) {
			c.add( fb );
		}*/
		
		/*final int val = 2;
		timer = new Timer( 50, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean update = false;
				for( FancyButton fb : buttons ) {
					if( !fb.getSize().equals( fb.getPreferredSize() ) ) {
						update = true;
						
						Dimension d = fb.getPreferredSize();
						fb.setSize( fb.getWidth()+((val-1)*(d.width-fb.getWidth()))/val, fb.getHeight()+((val-1)*(d.height-fb.getHeight()))/2 );
					}
				}
				
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
			}
		});
		timer.setInitialDelay( 0 );
		timer.setCoalesce( true );*/
	}
}
