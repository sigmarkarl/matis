package org.simmi;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;

public class TreeDraw extends JComponent {	
	BufferedImage 	img;
	String			yaml;
	Stroke			oldStroke;
	Font			bFont;
	Font			dFont;
	Font			lFont;
	Paint			oldPaint;
	Paint			gradientPaint;
	
	private class Node {
		String 		name;
		double		h;
		Color		color;
		List<Node>	nodes;
		
		public Node() {
			nodes = new ArrayList<Node>();
		}
		
		public String toString() {
			String str = "";
			if( nodes.size() > 0 ) {
				str += "(";
				int i = 0;
				for( i = 0; i < nodes.size()-1; i++ ) {
					str += nodes.get(i)+",";
				}
				str += nodes.get(i)+")";
			}
			
			return str+name;
		}
		
		public int countLeaves() {
			int total = 0;
			for( Node node : nodes ) {
				total += node.countLeaves();
			}	
			return Math.max( 1, total );
		}
		
		public int countMaxHeight() {
			int val = 0;
			for( Node node : nodes ) {
				val = Math.max( val, node.countMaxHeight() );
			}
			return val+1;
		}
	}
	
	List<Color>	colors = new ArrayList<Color>();
	int			ci = 0;
	
	Stroke		hStroke;
	Stroke		vStroke;
	
	public TreeDraw( String str, int w, int h, boolean equalHeight ) {
		super();
		
		Random	rnd = new Random();
		for( int i = 0; i < 100; i++ ) {
			colors.add( new Color( (int)(rnd.nextFloat()*255), (int)(rnd.nextFloat()*255), (int)(rnd.nextFloat()*255) ) );
		}
		
		loc = 0;
		
		Node resultnode = parseTreeRecursive( str );
		int leaves = resultnode.countLeaves();
		int levels = resultnode.countMaxHeight();
		System.err.println( resultnode + "  " + leaves + "  " + levels );
		
		img = new BufferedImage( w, h, BufferedImage.TYPE_INT_ARGB );
		Graphics2D g2 = img.createGraphics();
		g2.setRenderingHint( RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY );
		g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
		g2.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
		
		boolean paint = resultnode.name != null && resultnode.name.length() > 0;
		
		oldStroke = g2.getStroke();
		vStroke = new BasicStroke( h/300 );
		hStroke = new BasicStroke( w/600 );
		dFont = new Font("Arial", Font.BOLD, w/128);
		lFont = new Font("Arial", Font.BOLD, w/72);
		bFont = new Font("Arial", Font.BOLD, w/96);
		oldPaint = g2.getPaint();
		//gradientPaint = new GradientPaint();
		
		Color color = colors.get(ci);
		if( resultnode.color != null ) {
			color = resultnode.color;
		}
		int k = w/32;
		int ny;
		
		this.h = h;
		this.w = w;
		dh = h/levels;
		dw = w/leaves;
		int starty = h/25;
		if( equalHeight ) {
			ny = starty+dh*(h/dh-levels);
		} else {
			ny = (int)(starty+(h*resultnode.h)/100000.0);
		}
		GradientPaint shadeColor = createGradient( color, ny-k/2, h );
		
		drawFramesRecursive( g2, resultnode, 0, 0, w/2, starty, paint ? shadeColor : null, leaves, equalHeight );
		
		yaml = "";
		ci = 0;
		g2.setFont( dFont );
		drawTreeRecursive( g2, resultnode, 0, 0, w/2, starty, equalHeight );
		if( resultnode.name != null && resultnode.name.length() > 0 ) {	
			int strw = g2.getFontMetrics().stringWidth( resultnode.name );
			g2.setColor( Color.red );
			g2.fillRoundRect( w/2-(5*strw)/8, starty-starty/2, (5*strw)/4, starty, starty, starty );
			g2.setColor( Color.white );
			g2.drawString(resultnode.name, (w-strw)/2, h/18 );
		}
	}
	
	int w;
	int h;
	int dw;
	int dh;
	
	public GradientPaint createGradient( Color color, int h1, int h2 ) {
		return new GradientPaint( 0, h1, 
				new Color( Math.min(255,190+color.getRed()), Math.min(255, 190+color.getGreen()), Math.min(255, 190+color.getBlue()), 255 ), 0, h2, 
				new Color( (int)Math.min(255,50+color.getRed()), (int)Math.min(255, 50+color.getGreen()), (int)Math.min(255, 50+color.getBlue()) ) );
	}
	
	public void drawFramesRecursive( Graphics2D g2, Node node, int x, int y, int startx, int starty, GradientPaint sc, int total, boolean equalHeight ) {		
		if( node.nodes.size() > 0 ) {			
			if( sc != null ) {// paint && !(allNull || nullNodes) ) {
				g2.setPaint( sc );
				
				int k = w/32;
				int yoff = starty-(1*k)/4;
				
				g2.fillRect( x+k/4, yoff, dw*total-k/2, h-yoff-h/17 ); //ny-yoff );
				g2.setPaint( oldPaint );
			}
			
			total = 0;
			for( Node resnode : node.nodes ) {
				int nleaves = resnode.countLeaves();
				int nlevels = resnode.countMaxHeight();
				
				int ny;
				int k = w/32;
				
				if( equalHeight ) {
					ny = h/25+dh*(h/dh-nlevels);
				} else {
					ny = (int)(starty+(h*resnode.h)/100000.0);
				}
				
				boolean paint = resnode.name != null && resnode.name.length() > 0;
				
				ci++;
				Color color = colors.get(ci);
				if( resnode.color != null ) {
					color = resnode.color;
				}
				GradientPaint shadeColor = createGradient(color, ny-k/2, h);
				
				drawFramesRecursive( g2, resnode, x+dw*total, y+h, (dw*nleaves)/2, ny, paint ? shadeColor : null, nleaves, equalHeight );
				total += nleaves;
			}
		}
	}

	public void drawTreeRecursive( Graphics2D g2, Node node, int x, int y, int startx, int starty, boolean equalHeight ) {		
		if( node.nodes.size() > 0 ) {			
			int total = 0;
			for( Node resnode : node.nodes ) {
				int nleaves = resnode.countLeaves();
				int nlevels = resnode.countMaxHeight();
				
				int nx =  dw*total+(dw*nleaves)/2;
				int ny;
				
				int k = w/32;
				int yoff = starty-k/2;
				
				if( equalHeight ) {
					ny = h/25+dh*(h/dh-nlevels);
				} else {
					ny = /*h/25+*/(int)(starty+(h*resnode.h)/(maxh*3.2));
				}
				
				boolean nullNodes = resnode.nodes == null || resnode.nodes.size() == 0;
				boolean paint = resnode.name != null && resnode.name.length() > 0;
				
				ci++;
				Color color = colors.get(ci);
				if( resnode.color != null ) {
					color = resnode.color;
				}
				drawTreeRecursive( g2, resnode, x+dw*total, y+h, (dw*nleaves)/2, ny, equalHeight );
				//drawTreeRecursive( g2, resnode, w, h, dw, dh, x+dw*total, y+h, (dw*nleaves)/2, ny, paint ? shadeColor : null );
				
				g2.setColor( Color.darkGray );
				g2.setStroke( vStroke );
				g2.drawLine( x+startx, starty, x+nx, starty);
				g2.setStroke( hStroke );
				g2.drawLine( x+nx, starty, x+nx, ny );
				g2.setStroke( oldStroke );
				
				if( paint ) {
					if( nullNodes ) {
						g2.setColor( Color.black );
						g2.setFont( bFont );
						
						String[] split = resnode.name.split("_");
						int t = 0;
						int mstrw = 0;
						for( String str : split ) {
							int strw = g2.getFontMetrics().stringWidth( str );
							mstrw = Math.max( mstrw, strw );
							g2.drawString(str, x+nx-strw/2, ny+4+h/25+(t++)*bFont.getSize() );
						}
						int x1 = (x+nx-mstrw/2);
						int x2 = (x+nx+mstrw/2);
						int y1 = ny+4+h/25+(-1)*bFont.getSize();
						int y2 = ny+4+h/25+(split.length-1)*bFont.getSize();
						yaml += resnode.name + ": [" + x1 + "," + y1 + "," + x2 + "," + y2 + "]\n";
					} else {
						g2.setColor( color );
						g2.fillOval( x+nx-k/2, ny-k/2, k, k );
						g2.setColor( Color.white );
						if( resnode.name.length() > 2 ) {
							g2.setFont( lFont );
							int strw = g2.getFontMetrics().stringWidth( resnode.name );
							g2.drawString(resnode.name, x+nx-strw/2, ny+8 );
						} else {
							g2.setFont( bFont );
							int strw = g2.getFontMetrics().stringWidth( resnode.name );
							g2.drawString(resnode.name, x+nx-strw/2, ny+9 );
						}
					}
				}
				total += nleaves;
			}
		}
	}
	
	public void paintComponent( Graphics g ) {
		super.paintComponent( g );
		Graphics2D g2 = (Graphics2D)g;
		g2.setRenderingHint( RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY );
		g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
		g.drawImage( img, 0, 0, this.getWidth(), this.getHeight(), this );
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		JFrame	frame = new JFrame();
		
		String imgType = "png";
		int x = 1600;
		int y = 600;
		boolean	equalHeight = false;
		boolean show = false;
		boolean help = false;
		String coords = null;
		Reader reader = new InputStreamReader( System.in );
		OutputStream out = System.out;
		
		for( int i = 0; i < args.length; i++ ) {
			if( args[i].equals("--in") ) {
				File file = new File( args[++i] );
				try {
					reader = new FileReader( file );
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if( args[i].equals("--out") ) {
				try {
					out = new FileOutputStream( args[++i] );
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if( args[i].equals("--type") ) {
				imgType = args[++i];
			} else if( args[i].equals("--x") ) {
				x = Integer.parseInt( args[++i] );
			} else if( args[i].equals("--y") ) {
				y = Integer.parseInt( args[++i] );
			} else if( args[i].equals("--equalHeights") ) {
				equalHeight = true;
			} else if( args[i].equals("--show") ) {
				show = true;
			} else if( args[i].equals("--help") ) {
				help = true;
			} else if( args[i].equals("--coords") ) {
				coords = args[++i];
			}
		}
		
		if( help ) {
			System.err.print( "Usage: java -jar treedraw.jar" );
			System.err.println( " --in [optional, filename, default:stdin] --out [optional, filename, default:stdout], --type [optional, imagetype, default:\"PNG\"]" );
			System.err.println( "--x [optional, integer, default:1600 --y [optional, integer, default:800] --show [optional, show image, no output], --equalHeights [optional, do not use heights] --coords [optional, coords yaml]" );
		} else {
			
			/*if( args.length > 2 ) {
				File file = new File( args[2] );
				if( file.exists() ) {
					try {
						reader = new FileReader( file );
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				x = Integer.parseInt(args[0]);
				y = Integer.parseInt(args[1]);
			} else if( args.length > 1) {
				reader = new InputStreamReader( System.in );
				x = Integer.parseInt(args[0]);
				y = Integer.parseInt(args[1]);
			} else if( args.length > 0) {
				File file = new File( args[0] );
				if( file.exists() ) {
					try {
						reader = new FileReader( file );
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
				}
			}*/
			
			if( reader != null ) {
				char[] cbuf = new char[1024];
				try {
					int read = reader.read(cbuf);
					if( read > 0 ) {
						String str = new String( cbuf, 0, read ).replaceAll("[\r\n]+", "");
						TreeDraw treedraw = new TreeDraw( str, x, y, equalHeight );
						frame.add( treedraw );
						if( !show ) ImageIO.write( treedraw.img, imgType, out );
						if( coords != null ) {
							File f = new File( coords );
							FileWriter fw = new FileWriter( f );
							fw.write( treedraw.yaml );
							fw.close();
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			frame.setSize( 800,600 );
			frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
			if( show ) frame.setVisible( true );
		}
	}
	
	double minh = Double.MAX_VALUE;
	double maxh = 0.0;
	int loc;
	private Node parseTreeRecursive(String str) {
		Node ret = new Node();
		while( loc < str.length() && str.charAt(loc) != ')' ) {
			loc++;
			char c = str.charAt(loc);
			if( c == '(' ) {
				Node node = parseTreeRecursive(str);
				ret.nodes.add( node );
			} else {
				Node node = new Node();
				int end = loc+1;
				char n = str.charAt(end);
				while( end < str.length() && n != ',' && n != ')' ) {
					n = str.charAt(++end);
				}
				String code = str.substring( loc, end );
				if( code.contains(":") ) {
					String[] split = code.split(":");
					node.name = split[0].replaceAll("'", "");
					if( split.length > 2 ) {
						String color = split[2].substring(1);
						int r = Integer.parseInt( color.substring(0, 2), 16 );
						int g = Integer.parseInt( color.substring(2, 4), 16 );
						int b = Integer.parseInt( color.substring(4, 6), 16 );
						node.color = new Color( r,g,b );
					} else node.color = null;
					node.h = Double.parseDouble( split[1] );
					if( node.h < minh ) minh = node.h;
					if( node.h > maxh ) maxh = node.h;
				} else {
					node.name = code.replaceAll("'", "");;
				}
				loc = end;
				
				ret.nodes.add( node );
			}
		}
		
		if( loc < str.length() ) {
			loc++;
			int end = loc;
			char n = str.charAt(end);
			while( end < str.length() && n != ',' && n != ';' && n != ')' ) {
				n = str.charAt(++end);
			}
			String code = str.substring( loc, end );
			if( code.contains(":") ) {
				String[] split = code.split(":");
				ret.name = split[0].replaceAll("'", "");;
				if( split.length > 2 ) {
					String color = split[2].substring(1);
					int r = Integer.parseInt( color.substring(0, 2), 16 );
					int g = Integer.parseInt( color.substring(2, 4), 16 );
					int b = Integer.parseInt( color.substring(4, 6), 16 );
					ret.color = new Color( r,g,b );
				} else ret.color = null;
				ret.h = Double.parseDouble( split[1] );
				if( ret.h < minh ) minh = ret.h;
				if( ret.h > maxh ) maxh = ret.h;
			} else {
				ret.name = code.replaceAll("'", "");;
			}
			loc = end;
		}
		
		return ret;
	}
}
