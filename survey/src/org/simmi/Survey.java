package org.simmi;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;

public class Survey extends JApplet {
	JComponent 			c;
	JComponent			subc;
	JComponent			nexc;
	JEditorPane			e;
	Image				img;
	Image				mimg;
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
	int					step = 0;
	
	JLabel				label;
	JButton 			button;
	JTextField			email;
	JEditorPane			editor;
	
	JButton				stepForw;
	JButton				stepBackw;
	
	List<String>		flist;
	BufferedImage		surveyImage = null;
	
	SurveyPanel			surveyPanel;
	JComponent			surveyImagePanel;
	
	List<Integer>		res;
	
	String				title = "Vinsamlegast skráðu netfangið þitt";
	
	Font				cfont = new Font( "Monospace", Font.BOLD, 12 );
	
	final Color			bgColor = new Color( 255, 255, 200 );
	
	String[] bNames = {"Dót", "Vinnsla", "Pökkun Geymsla", "Flutningur", "Dreifing"};
	Color[] bColors = { new Color(250,250,200), new Color(250,200,250), new Color(200,250,250), new Color(200,250,200), new Color(200,200,250) };
	
	public class Step extends JComponent {
		public Step() {
			
		}
	};
	
	public Survey() {
		super();
		try {
			URL url = this.getClass().getResource("/smooth.png");
			img = ImageIO.read( url );
			url = this.getClass().getResource("/matis.png");
			mimg = ImageIO.read( url );
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
		
		/*Graphics2D g2 = (Graphics2D)g;
		g2.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
		g2.setFont( cfont );
		g2.setColor( Color.gray );
		String str = "Allur réttur áskilinn 2009, Matís ohf.";
		int strw = g2.getFontMetrics().stringWidth( str );
		
		g.drawString(str, (this.getWidth()-strw)/2, this.getHeight()/2+320);*/
	}
	
	public ImageIcon configStepButton( BufferedImage sfImg, Color c1, Color c2, int xo, int yo, boolean forw ) {
		Graphics2D g2 = (Graphics2D)sfImg.getGraphics();
		g2.setRenderingHint( RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY );
		g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
		g2.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
		
		Paint paint = g2.getPaint();
		Stroke stroke = g2.getStroke();
		
		GradientPaint gp = new GradientPaint( 0.0f, 0.0f, c1, sfImg.getWidth(), sfImg.getHeight(), c2 );
		g2.setPaint(gp);
		g2.fillOval(1+xo, 1+yo, sfImg.getWidth()-3-xo*2, sfImg.getHeight()-3-yo*2 );
		g2.setPaint(paint);
		
		BasicStroke bs = new BasicStroke( 2.0f );
		g2.setColor( Color.lightGray );
		g2.setStroke( bs );
		g2.drawOval(1+xo, 1+yo, sfImg.getWidth()-3-xo*2, sfImg.getHeight()-3-yo*2 );
		
		g2.setColor( Color.white );
		if( forw ) {
			g2.drawLine(10,10,20,15);
			g2.drawLine(10,20,20,15);
		} else {
			g2.drawLine(10,15,20,10);
			g2.drawLine(10,15,20,20);
		}
		
		g2.setStroke(stroke);
		g2.dispose();
		
		return new ImageIcon( sfImg );
	}
	
	public void configButton( BufferedImage image, Font font, int xo, int yo, Color c1, Color c2 ) {
		Graphics2D g2 = (Graphics2D)image.getGraphics();
		
		g2.setRenderingHint( RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY );
		g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
		g2.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
		
		Paint 				paint = g2.getPaint();
		Stroke				stroke = g2.getStroke();
		
		Stroke				bs = new BasicStroke(2.0f);
		GradientPaint		gp = new GradientPaint( 0.0f, 0.0f, c1, image.getWidth(), image.getHeight(), c2 );
		g2.setPaint( gp );
		g2.fillRoundRect(1+xo, 4+yo, image.getWidth()-3-xo*2, image.getHeight()-8-yo*2, 24, 24);
		g2.setPaint(paint);
		
		g2.setStroke( bs );
		g2.setColor( Color.lightGray );
		g2.drawRoundRect(1+xo, 4+yo, image.getWidth()-3-xo*2, image.getHeight()-8-yo*2, 24, 24);
		g2.setStroke( stroke );
		
		g2.setFont( font );
		g2.setColor( Color.white );
		
		String str = "Hefja könnun";
		int strw = g2.getFontMetrics().stringWidth( str );
		g2.drawString(str, (image.getWidth()-strw)/2, image.getHeight()-10);
		g2.dispose();
	}
	
	public void loadImage() {
		String iName = flist.get(step-1);
		URL imgUrl = this.getClass().getResource("/"+iName);
		try {
			surveyImage = ImageIO.read( imgUrl );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void updateBtnGrp() {
		int base = (step-1)*4;
		BtnGrp[] bg = surveyPanel.btnGrps;
		
		int v = res.get( base );
		if( v != -1 ) bg[0].setSelected( v );
		else bg[0].bg.clearSelection();
		v = res.get( base+1 );
		if( v != -1 ) bg[1].setSelected( v );
		else bg[1].bg.clearSelection();
		v = res.get( base+2 );
		if( v != -1 ) bg[2].setSelected( v );
		else bg[2].bg.clearSelection();
		v = res.get( base+3 );
		if( v != -1 ) bg[3].setSelected( v );
		else bg[3].bg.clearSelection();
	}
	
	public void stepInc() {
		//boolean fromFirst = (step == 0);
		step = Math.min( flist.size(), step+1 );
		if( step > 0 ) {
			updateBtnGrp();
			
			loadImage();
			/*if( fromFirst ) {
				Rectangle r = c.getBounds();
				c.setBounds(r.x, r.y, r.width, r.height);
			}*/
		}
		
		Rectangle r = c.getBounds();
		c.setBounds(r.x, r.y, r.width, r.height);
		c.revalidate();
		c.invalidate();
		c.repaint();
	}
	
	public void stepDec() {
		if( step == 0 ) {
		} else if( step == 1 ) {
			step = 0;
			surveyImage = null;
			Rectangle r = c.getBounds();
			c.setBounds(r.x, r.y, r.width, r.height);
			subc.repaint();
		} else {
			step--;
			updateBtnGrp();
			loadImage();
			c.repaint();
		}
	}
	
	public class SurveyPanel extends JComponent {
		BtnGrp[]	btnGrps;
		JLabel[]	labels;
		
		public SurveyPanel() {
			btnGrps = new BtnGrp[4];
			labels = new JLabel[4];
			
			this.setLayout( new GridLayout(10, 1) );
			
			labels[0] = new JLabel("Hversu aðlaðandi finnst þér þessi vara?");
			btnGrps[0] = new BtnGrp("Alls ekki aðlaðandi", "Mjög aðlaðandi", 0);
			labels[1] = new JLabel("Hversu náttúruleg finnst þér þessi vara?");
			btnGrps[1] = new BtnGrp("Alls ekki náttúruleg", "Mjög náttúruleg", 1);
			labels[2] = new JLabel("Hversu trúverðug finnst þér þessi vara?");
			btnGrps[2] = new BtnGrp("Alls ekki trúverðug", "Mjög trúverðug", 2);
			labels[3] = new JLabel("Hversu handhæg finnst þér þessi vara?");
			btnGrps[3] = new BtnGrp("Alls ekki handhæg", "Mjög handhæg", 3);
			
			for( int i = 0; i < labels.length; i++ ) {
				this.add( labels[i] );
				this.add( btnGrps[i] );
			}
		} 
	};
	
	public class BtnGrp extends JComponent {
		JRadioButton[]	rad = new JRadioButton[9];
		ButtonGroup 	bg;
		String 	sf;
		String 	sl;
		int		index;
		
		public BtnGrp( String sf, String sl, int ind ) {
			super();
			
			this.index = ind;
			
			this.sf = sf;
			this.sl = sl;
			
			this.setFont( new Font("Arial", Font.PLAIN, 11 ) );
			this.setLayout( new GridLayout(1,rad.length) );
			
			bg = new ButtonGroup();
			for( int i = 0; i < rad.length; i++ ) {
				rad[i] = new JRadioButton( new AbstractAction() {
					@Override
					public void actionPerformed(ActionEvent e) {
						updateStuff( (JRadioButton)e.getSource() );
					}
				});
				bg.add( rad[i] );
				this.add( rad[i] );
			}
		}
		
		public int getSelectedIndex() {
			int k = 0;
			while( !rad[k].isSelected() ) k++;
			if( k < rad.length ) return k;
			return -1;
		}
		
		public void updateStuff( JRadioButton radi ) {
			int i = -1;
			while( rad[++i] != radi );
			res.set( (step-1)*4+index, i );
		}
		
		public void setSelected( int v ) {
			rad[v].setSelected( true );
		}
		
		public void paintComponent( Graphics g ) {
			super.paintComponent( g );
			
			Graphics2D g2 = (Graphics2D)g;
			g2.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
			
			g.drawString(sf, 0, 8);
			int strw = g.getFontMetrics().stringWidth( sl );
			g.drawString(sl, this.getWidth()-strw-20, 8);
		}
	};
	
	static String lof = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
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
		
		ToolTipManager.sharedInstance().setInitialDelay( 0 );
		
		this.setLayout( null );
		this.getContentPane().setLayout(null);
		this.setBackground( bgColor );
		this.getContentPane().setBackground( bgColor );
		
		Dimension d	= new Dimension(900,30);
		
		e = new JEditorPane();
		e.setEditable( false );
		e.setOpaque( false );
		e.setBackground( new Color(0,0,0,0) );
		e.setPreferredSize( d );
		e.setSize( d );
		e.setContentType("text/html");
		//e.setText("<html><body><center>ALlur réttur áskilinn 2009, Matís ohf</center></body></html>");
		//style='margin:0px 0px 0px 0px; background-color: #FFFFC8;'
		e.setText("<html><body><center><span style=\"color:gray\">Allur réttur áskilinn 2009, Matís ohf.</span></center></body></html>");
		
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
				
				String str = "MARKAÐSKÖNNUN MATÍS";
				g2.drawImage( mimg, 15, 15, 30, 30, this );
				g2.setFont( g2.getFont().deriveFont( Font.BOLD, 16.0f ) );
				g2.setColor( Color.gray );
				g2.drawString( str, 50, 36 );
				
				if( step == 0 ) {
					g2.setFont( g2.getFont().deriveFont( Font.BOLD, 14.0f ) );
					int strw = g2.getFontMetrics().stringWidth( title );
					g2.setColor( Color.gray );
					g2.drawString( title, (this.getWidth()-strw)/2, this.getHeight()-30 );
				} else {
					str = "Velkomin(n) "+email.getText();
					g2.setFont( g2.getFont().deriveFont( Font.BOLD, 14.0f ) );
					int strw = g2.getFontMetrics().stringWidth( str );
					g2.setColor( Color.gray );
					g2.drawString( str, (this.getWidth()-strw)/2, this.getHeight()-30 );
				}
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
				
				if( step > 0 ) {
					String str = "Skref "+step+" af "+flist.size();
					int strw = g2.getFontMetrics().stringWidth( str );
					g2.drawString(str, (w-strw)/2, h - 60);
				}
				/*String str = "Hvað viltu panta?";
				int strw = g2.getFontMetrics().stringWidth(str);
				g2.drawString(str, (w-strw)/2, 500);*/
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
					
					if( step > 0 ) {
						surveyPanel.setBounds(55, 135, 360, 475);
						surveyImagePanel.setBounds(455, 115, 410, 475);
						
						stepForw.setBounds( this.getWidth()/2+20, this.getHeight()-50, 30, 30 );
						stepBackw.setBounds( this.getWidth()/2-50, this.getHeight()-50, 30, 30 );
						
						label.setBounds( 0,0,0,0 );
						email.setBounds( 0,0,0,0 );
						button.setBounds( 0,0,0,0 );
						editor.setBounds( 0,0,0,0 );
					} else {
						surveyPanel.setBounds(0,0,0,0);
						surveyImagePanel.setBounds(0,0,0,0);
						
						stepForw.setBounds( 0,0,0,0 );
						stepBackw.setBounds( 0,0,0,0 );
						
						label.setBounds( w/2-150, h/2-150-40, 300, 30 );
						email.setBounds( w/2-125, h/2-150, 250, 25);
						button.setBounds( w/2-75, h/2-150+40, 150, 25);
						editor.setBounds( w/2-400, h/2, 800, 300 );
					}
				}
				super.setBounds(x, y, w, h );
			}
		};
		c.setFont( new Font( "Arial", Font.BOLD, 14 ) );
		c.setLayout( null );
		c.add( subc );
		
		surveyPanel = new SurveyPanel();
		surveyPanel.revalidate();
		surveyPanel.invalidate();
		surveyPanel.repaint();
		
		byte[] all = new byte[1024];
		InputStream is = this.getClass().getResourceAsStream( "/fnames.txt" );
		int r = -1;
		try {
			r = is.read(all);
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		String rstr = new String( all, 0, r );
		String[] split = rstr.split("\n");
		List<String>	lstr = Arrays.asList( split );
		Collections.shuffle( lstr );
		flist = lstr; //.subList(0, 1);
		
		res = new ArrayList<Integer>();
		for( String str : flist ) {
			res.add(-1);
			res.add(-1);
			res.add(-1);
			res.add(-1);
		}
		
		surveyImagePanel = new JComponent() {
			public void paintComponent( Graphics g ) {
				super.paintComponent(g);
				
				if( surveyImage != null ) {
					Graphics2D g2 = (Graphics2D)g;
					g2.setRenderingHint( RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
					g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					
					int iw = surveyImage.getWidth();
					int ih = surveyImage.getHeight();
					int w = this.getWidth();
					int h = this.getHeight();
					if( w*ih > iw*h ) {
						int rw = (h*iw)/ih;
						int rh = h;
						g2.drawImage( surveyImage, (w-rw)/2, 0, rw, rh, this );
					} else {
						int rw = w;
						int rh = (w*ih)/iw;
						g2.drawImage( surveyImage, 0, (h-rh)/2, rw, rh, this );
					}
				}
			}
		};
		
		Color lightGray = new Color(240,240,240);
		Color gray = new Color(210,210,210);
		
		stepForw = new JButton( new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stepInc();
				surveyImagePanel.repaint();
			}
		});
		stepForw.setContentAreaFilled( false );
		stepForw.setFocusPainted( false );
		stepForw.setBorderPainted( false );
		stepForw.setOpaque( false );
		stepForw.setBorder( new EmptyBorder(0, 0, 0, 0) );
		
		BufferedImage sfImg = new BufferedImage( 30, 30, BufferedImage.TYPE_INT_ARGB );
		stepForw.setIcon( configStepButton( sfImg, lightGray, gray, 1, 1, true ) );
		BufferedImage sfImgPress = new BufferedImage( 30, 30, BufferedImage.TYPE_INT_ARGB );
		stepForw.setPressedIcon( configStepButton( sfImgPress, gray, lightGray, 1, 1, true ) );
		BufferedImage sfImgHover = new BufferedImage( 30, 30, BufferedImage.TYPE_INT_ARGB );
		stepForw.setRolloverIcon( configStepButton( sfImgHover, lightGray, gray, 0, 0, true ) );
		BufferedImage sfImgHoversel = new BufferedImage( 30, 30, BufferedImage.TYPE_INT_ARGB );
		stepForw.setRolloverSelectedIcon( configStepButton( sfImgHoversel, lightGray, gray, 0, 0, true ) );
		
		stepBackw = new JButton( new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stepDec();
				surveyImagePanel.repaint();
			}
		});
		stepBackw.setContentAreaFilled( false );
		stepBackw.setFocusPainted( false );
		stepBackw.setBorderPainted( false );
		stepBackw.setOpaque( false );
		stepBackw.setBorder( new EmptyBorder(0, 0, 0, 0) );
		
		sfImg = new BufferedImage( 30, 30, BufferedImage.TYPE_INT_ARGB );
		stepBackw.setIcon( configStepButton( sfImg, lightGray, gray, 1, 1, false ) );
		sfImgPress = new BufferedImage( 30, 30, BufferedImage.TYPE_INT_ARGB );
		stepBackw.setPressedIcon( configStepButton( sfImgPress, gray, lightGray, 1, 1, false ) );
		sfImgHover = new BufferedImage( 30, 30, BufferedImage.TYPE_INT_ARGB );
		stepBackw.setRolloverIcon( configStepButton( sfImgHover, lightGray, gray, 0, 0, false ) );
		sfImgHoversel = new BufferedImage( 30, 30, BufferedImage.TYPE_INT_ARGB );
		stepBackw.setRolloverSelectedIcon( configStepButton( sfImgHoversel, lightGray, gray, 0, 0, false ) );
		
		button = new JButton( new AbstractAction("") {
			@Override
			public void actionPerformed(ActionEvent e) {
				stepInc();
				Rectangle r = c.getBounds();
				c.setBounds( r.x, r.y, r.width, r.height );
			}
		});
		URL hoUrl = this.getClass().getResource("/hefja_out.png");
		URL hiUrl = this.getClass().getResource("/hefja_in.png");
		button.setBorderPainted( false );
		button.setContentAreaFilled( false );
		button.setFocusPainted(false);
		button.setOpaque( false );
		button.setBorder( new EmptyBorder(0, 0, 0, 0) );
		
		Font font = new Font( "Verdana", Font.BOLD, 11 );
		BufferedImage 		pressed = new BufferedImage(150, 30, BufferedImage.TYPE_INT_ARGB );
		configButton( pressed, font, 1, 1, gray, lightGray );
		BufferedImage 		normal = new BufferedImage(150, 30, BufferedImage.TYPE_INT_ARGB );
		configButton( normal, font, 1, 1, lightGray, gray );
		BufferedImage 		hover = new BufferedImage(150, 30, BufferedImage.TYPE_INT_ARGB );
		configButton( hover, font, 0, 0, lightGray, gray );
		BufferedImage 		hoverSel = new BufferedImage(150, 30, BufferedImage.TYPE_INT_ARGB );
		configButton( hoverSel, font, 0, 0, gray, lightGray );
		
		button.setIcon( new ImageIcon( normal ) );
		button.setPressedIcon( new ImageIcon( pressed ) );
		button.setRolloverIcon( new ImageIcon( hover ) );
		button.setRolloverSelectedIcon( new ImageIcon( hoverSel ) );
		
		
		/*try {
			
			//button.setIcon( new ImageIcon( ImageIO.read( hiUrl ) ) );
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}*/
		
		byte[] bb = new byte[8192];
		InputStream in = this.getClass().getResourceAsStream("/intro.txt");
		r = -1;
		try {
			r = in.read( bb );
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String text = new String( bb, 0, r );
		editor = new JEditorPane();
		editor.setOpaque( false );
		editor.setBackground( new Color(0,0,0,0) );
		editor.setEditable( false );
		editor.setContentType("text/html");
		editor.setText( text );
		
		email = new JTextField();
		label = new JLabel("Vinsamlegast skráðu póstfangið þitt", JLabel.CENTER);
		c.add( email );
		c.add( button );
		c.add( stepForw );
		c.add( stepBackw );
		//c.add( label );
		c.add( editor );
		c.add( surveyPanel );
		c.add( surveyImagePanel );
		
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
				Survey.this.repaint();
			}
		};
		timer = new Timer( 50, al );
		timer.setInitialDelay( 0 );
		timer.setCoalesce( true );
		
		Rectangle rc = this.getBounds();
		this.setBounds(rc.x, rc.y, rc.width, rc.height);
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
