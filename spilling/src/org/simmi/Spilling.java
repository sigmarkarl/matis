package org.simmi;

import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JApplet;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Spilling extends JApplet implements MouseListener, MouseMotionListener, KeyListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JComponent			c = null;
	Rectangle 			selRect = null;
	static Color 		selColor = new Color( 0,128,255,32 );
	Corp				linkCorp = null;
	Corp				linkCorp2 = null;
	boolean 			toggle = false;
	static boolean		birta = true;
	boolean				drawLinks = true;
	boolean				drawLinkNames = true;
	boolean				drawCorpNames = true;
	boolean				drawPersonNames = true;
	boolean				d3 = true;
	
	static double hhx, hhy, hhz;
	double cx;
	double cy;
	double cz;
	boolean shift = false;
	static boolean	fixed = false;
	
	Point 	np;
	Point	p;
	
	public Thread springThread() {
		return new Thread() {
			public void run() {
				while( toggle ) {
					c.repaint();
					
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
	}
	
	public void spring() {
		final double damp = 0.97;
		final double u = 1000.0;
		final double gorm = 0.0;
		final double k = 0.001;
		
		for( Corp corp : Corp.corpList ) {
			double fx = 0;
			double fy = 0;
			double fz = 0;
			for( Corp c : Corp.corpList ) {
				double dx = corp.getx() - c.getx();
				double dy = corp.gety() - c.gety();
				double dz = corp.getz() - c.getz();
				double d = dx*dx + dy*dy + dz*dz;
				double r = Math.sqrt( d );
				double r3 = r*r*r;
				
				if( r3 > 0.1 ) {
					fx += (u*dx)/r3;
					fy += (u*dy)/r3;
					fz += (u*dz)/r3;
				}
			}
			for( Corp c : corp.connections.keySet() ) {
				double dx = corp.getx() - c.getx();
				double dy = corp.gety() - c.gety();
				double dz = corp.getz() - c.getz();
				
				fx -= k*(dx-gorm);
				fy -= k*(dy-gorm);
				fz -= k*(dz-gorm);
			}
			
			corp.vx = (corp.vx+fx)*damp;
			corp.vy = (corp.vy+fy)*damp;
			corp.vz = (corp.vz+fz)*damp;
		
			corp.setx( corp.getx() + corp.vx );
			corp.sety( corp.gety() + corp.vy );
			corp.setz( corp.getz() + corp.vz );
			
			//corp.setBounds( (int)(corp.x-Corp.size/2), (int)(corp.y-Corp.size/2), Corp.size, Corp.size );
		}
		//c.repaint();
	}
	
	static void backtrack( int x, int y, int w, int h, Corp c, double cx, double cy, double cz ) {
		double cosx = Math.cos(hhx);
		double sinx = Math.sin(hhx);
		double cosy = Math.cos(hhy);
		double siny = Math.sin(hhy);
		double cosz = Math.cos(hhz);
		double sinz = Math.sin(hhz);
		
		double d = dzoomval;
		double zval = d;
		double mval = d * 1.5;
		
		double dx = x - w/2;
		double dy = y - h/2;
		double dz = zoomval;
			
		double lz = dz;
		double lx = ( dx * (zval + dz) ) / mval;
		double ly = ( dy * (zval + dz) ) / mval;
		
		double nz = lz - zoomval;
		double nx = lx * cosz - ly * sinz;
		double ny = ly * cosz + lx * sinz;
		
		double xx = nx;
		double zz = nz * cosx - ny * sinx;
		double yy = ny * cosx + nz * sinx;
		
		c.sety( yy + cy );
		c.setx( xx * cosy + zz * siny + cx );
		c.setz( zz * cosy - xx * siny + cz );
	}
	
	void backtrack( Point p, Corp c ) {		
		//double hx = hhx;
		//double hy = hhy;
		//double hz = hhz;
		
		//System.err.println( "doing " + hx + "  " + hy + "  " + hz );		
		/*if (np != null && p != null) {
			if (!shift)
				hy += (np.x - p.x) / 100.0;
			hx += (np.y - p.y) / 100.0;
			if (shift)
				hz += (np.x - p.x) / 100.0;
		}*/
		
		//System.err.println( p + "    " + np );
		
		double len = Corp.corpList.size();
		cx = 0.0;
		cy = 0.0;
		cz = 0.0;
		for( Corp rel : Corp.corpList ) {
			cx += rel.getx();
			cy += rel.gety();
			cz += rel.getz();
		}
		cx /= len;
		cy /= len;
		cz /= len;
		
		backtrack( p.x, p.y, this.getWidth(), this.getHeight(), c, cx, cy, cz );
		
		/*double dx = p.x - this.getWidth()/2;
		double dy = p.y - this.getHeight()/2;
		double dz = zoomval;
		
		//System.err.println( "dfuckxyz " + dx + "  " + dy + "  " + dz );
			
		double lz = dz;
		double lx = ( dx * (zval + dz) ) / mval;
		double ly = ( dy * (zval + dz) ) / mval;
		
		//System.err.println( "lfuckxyz " + lx + "  " + ly + "  " + lz );
		
		double nz = lz - zoomval;
		double nx = lx * cosz - ly * sinz;
		double ny = ly * cosz + lx * sinz;
		
		//System.err.println( "nfuckxyz " + nx + "  " + ny + "  " + nz );
		
		double xx = nx;
		double zz = nz * cosx - ny * sinx;
		double yy = ny * cosx + nz * sinx;
		
		//System.err.println( "xfuckxyz " + xx + "  " + yy + "  " + zz + "  " + cosx + "   " + sinx );
		
		c.sety( yy + cy );
		c.setx( xx * cosy + zz * siny + cx );
		c.setz( zz * cosy - xx * siny + cz );
		
		//System.err.println( "fock " + c.x + "  " + c.y + "  " + c.z + "  point  " + p.x + "   " + p.y );*/
	}
	
	public void backtest( double x, double y ) {		
		double hx = hhx;
		double hy = hhy;
		double hz = hhz;
		
		System.err.println( "testing " + hx + "  " + hy + "  " + hz );
		
		double d = dzoomval;
		double zval = d;
		double mval = d * 1.5;
		
		if (np != null && p != null) {
			if (!shift)
				hy += (np.x - p.x) / 100.0;
			hx += (np.y - p.y) / 100.0;
			if (shift)
				hz += (np.x - p.x) / 100.0;
		}
		
		System.err.println( p + "    " + np );

		double cosx = Math.cos(hx);
		double sinx = Math.sin(hx);
		double cosy = Math.cos(hy);
		double siny = Math.sin(hy);
		double cosz = Math.cos(hz);
		double sinz = Math.sin(hz);
		
		double len = Corp.corpList.size();
		cx = 0.0;
		cy = 0.0;
		cz = 0.0;
		for( Corp rel : Corp.corpList ) {
			cx += rel.getx();
			cy += rel.gety();
			cz += rel.getz();
		}
		cx /= len;
		cy /= len;
		cz /= len;
		
		System.err.println( "xyz " + x + "  " + y + "  " + cz );
		
		double dx = x - this.getWidth()/2;
		double dy = y - this.getHeight()/2;
		double dz = cz;
		
		System.err.println( "dxyz " + dx + "  " + dy + "  " + dz );
		
		double lz = dz;
		double lx = ( dx * (zval + dz) ) / mval;
		double ly = ( dy * (zval + dz) ) / mval;
		
		System.err.println( "lxyz " + lx + "  " + ly + "  " + lz );
		
		double nz = lz - zoomval;
		double nx = lx * cosz - ly * sinz;
		double ny = ly * cosz + lx * sinz;
		
		System.err.println( "nxyz " + nx + "  " + ny + "  " + nz );
		
		double xx = nx;
		double zz = nz * cosx - ny * sinx;
		double yy = ny * cosx + nz * sinx;
		
		System.err.println( "xxyz " + xx + "  " + yy + "  " + zz + "  " + cosx + "   " + sinx );
		
		double ry = yy + cy;
		double rx = xx * cosy + zz * siny + cx;
		double rz = zz * cosy - xx * siny + cz;
		
		System.err.println( "erm " + cx + "  " + cy + "  " + cz );
		System.err.println( "erm " + hx + "  " + hy + "  " + hz );
		System.err.println( "erm " + cosx + "  " + cosy + "  " + cosz );
		
		System.err.println( "rxyz " + rx + "  " + ry + "  " + rz );
		
		xx = (rx - cx) * cosy - (rz - cz) * siny;
		yy = (ry - cy);
		zz = (rx - cx) * siny + (rz - cz) * cosy;
		
		System.err.println( "xxyz " + xx + "  " + yy + "  " + zz );

		nx = xx;
		ny = yy * cosx - zz * sinx;
		nz = yy * sinx + zz * cosx; // cz;
		
		System.err.println( "nxyz " + nx + "  " + ny + "  " + nz );

		lx = nx * cosz + ny * sinz;
		ly = ny * cosz - nx * sinz;
		lz = nz + zoomval;
		
		System.err.println( "lxyz " + lx + "  " + ly + "  " + lz );

		dz = lz;
		dx = (lx * mval) / (zval + dz);
		dy = (ly * mval) / (zval + dz);
		
		System.err.println( "dxyz " + dx + "  " + dy + "  " + dz );
		
		x = dx+this.getWidth()/2;
		y = dy+this.getHeight()/2;
		double z = dz;
		
		System.err.println( "xyz " + x + "  " + y + "  " + z );
	}
	
	static double zoomval = 500.0;
	static double dzoomval = 500.0;
	public void depth() {
		double hx = hhx;
		double hy = hhy;
		double hz = hhz;
		
		double d = dzoomval;
		double zval = d;
		double mval = d * 1.5;
		
		if (np != null && p != null) {
			if (!shift)
				hy += (np.x - p.x) / 100.0;
			hx += (np.y - p.y) / 100.0;
			if (shift)
				hz += (np.x - p.x) / 100.0;
		}

		double cosx = Math.cos(hx);
		double sinx = Math.sin(hx);
		double cosy = Math.cos(hy);
		double siny = Math.sin(hy);
		double cosz = Math.cos(hz);
		double sinz = Math.sin(hz);
		
		if( !fixed ) {
			double len = Corp.corpList.size();
			cx = 0.0;
			cy = 0.0;
			cz = 0.0;
			for( Corp rel : Corp.corpList ) {
				cx += rel.getx();
				cy += rel.gety();
				cz += rel.getz();
			}
			cx /= len;
			cy /= len;
			cz /= len;
		}
		
		for( Corp rel : Corp.corpList ) {
			double xx = (rel.getx() - cx) * cosy - (rel.getz() - cz) * siny;
			double yy = (rel.gety() - cy);
			double zz = (rel.getx() - cx) * siny + (rel.getz() - cz) * cosy;

			double nx = xx;
			double ny = yy * cosx - zz * sinx;
			double nz = yy * sinx + zz * cosx; // cz;

			double lx = nx * cosz + ny * sinz;
			double ly = ny * cosz - nx * sinz;
			double lz = nz + zoomval;

			double dz = lz;
			double dx = (lx * mval) / (zval + dz);
			double dy = (ly * mval) / (zval + dz);

			int size = (int) (d * 50 / (zval + dz));
			
			int x = (int)dx+this.getWidth()/2;
			int y = (int)dy+this.getHeight()/2;
			
			rel.depz = dz;

			/*if( rel == Corp.corpList.get(Corp.corpList.size()-1) ) {
				System.err.println( "hoho " + cx + "  " + cy + "  " + cz );
				System.err.println( "hoho " + hx + "  " + hy + "  " + hz );
				System.err.println( "hoho " + cosx + "  " + cosy + "  " + cosz );
				System.err.println( "heyr " + rel.x + "   " + rel.y + "   " + rel.z );
				System.err.println( "xx " + xx + "   " + yy + "   " + zz );
				System.err.println( "nx " + nx + "   " + ny + "   " + nz );
				System.err.println( "lx " + lx + "   " + ly + "   " + lz );
				System.err.println( "dx " + dx + "   " + dy + "   " + dz );
				System.err.println( "x " + x + "   " + y );
			}*/
			//System.err.println( "set bounds " + x + "   " + y + "   " + rel.x + "   " + rel.y + "   " + rel.z );
			rel.setBounds( x, y, size, size );
		}
	}
	
	public void excelLoad( InputStream is ) throws IOException {
		final int fasti = 1000;
		final int zoffset = 0;
		XSSFWorkbook workbook = new XSSFWorkbook( is );
		XSSFSheet corpSheet = workbook.getSheet("Adilar");
		//XSSFSheet linkSheet = workbook.getSheet("Links");
		
		Corp.corpMap.clear();
		c.removeAll();
		
		Random rand = new Random();
		int i = 0;
		XSSFRow 	corpRow = corpSheet.getRow( ++i );
		while( i < 500 ) {
			XSSFCell cell = null;
			if( corpRow != null ) cell = corpRow.getCell(7);
			if( cell != null ) {
				String name = cell.getStringCellValue();
				
				cell = corpRow.getCell(9);
				String kt = "";
				if( cell != null ) {
					int ctype = cell.getCellType();
					if( ctype == XSSFCell.CELL_TYPE_NUMERIC ) {
						kt = Integer.toString( (int)cell.getNumericCellValue() );
					} else if( ctype == XSSFCell.CELL_TYPE_STRING ) { 
						kt = cell.getStringCellValue();
					}
				}
				
				cell = corpRow.getCell(10);
				String desc = "";
				if( cell != null ) desc = cell.getStringCellValue();
				
				cell = corpRow.getCell(11);
				String home = "";
				if( cell != null ) home = cell.getStringCellValue();
				
				cell = corpRow.getCell(16);
				String father = "";
				if( cell != null ) father = cell.getStringCellValue();
				
				cell = corpRow.getCell(17);
				String mother = "";
				if( cell != null ) mother = cell.getStringCellValue();
				
				cell = corpRow.getCell(18);
				String maki = "";
				if( cell != null ) maki = cell.getStringCellValue();
				
				Corp corp = null;
				if( !Corp.corpMap.containsKey(name) ) {
					corp = new Corp( name );
					corp.type = "person";
					corp.text = desc;
					corp.home = home;
					corp.kt = kt;
					corp.setx( rand.nextInt(fasti) );
					corp.sety( rand.nextInt(fasti) );
					corp.setz( rand.nextInt(fasti)-zoffset );
					c.add( corp );
					//corp.setBounds( (int)(corp.x-Corp.size/2), (int)(corp.y-Corp.size/2), Corp.size, Corp.size );
				} else {
					corp = Corp.corpMap.get(name);
					corp.text += "\n\n"+desc;
				}
				
				if( father.length() > 0 ) {
					Corp fcorp = null;
					if( Corp.corpMap.containsKey(father) ) {
						fcorp = Corp.corpMap.get(father);
					} else {
						fcorp = new Corp( father );
						fcorp.type = "person";
						fcorp.setx( rand.nextInt(fasti) );
						fcorp.sety( rand.nextInt(fasti) );
						fcorp.setz( rand.nextInt(fasti)-zoffset );
						c.add( fcorp );
					}
					
					corp.connections.put( fcorp, new HashSet<String>( Arrays.asList( new String[] {"barn"} ) ) );
					fcorp.connections.put( corp, new HashSet<String>( Arrays.asList( new String[] {"faðir"} ) ) );
				}
				
				if( mother.length() > 0 ) {
					Corp mcorp = null;
					if( Corp.corpMap.containsKey(mother) ) {
						mcorp = Corp.corpMap.get(mother);
					} else {
						mcorp = new Corp( mother );
						mcorp.type = "person";
						mcorp.setx( rand.nextInt(fasti) );
						mcorp.sety( rand.nextInt(fasti) );
						mcorp.setz( rand.nextInt(fasti)-zoffset );
						c.add( mcorp );
					}
					
					corp.connections.put( mcorp, new HashSet<String>( Arrays.asList( new String[] {"barn"} ) ) );
					mcorp.connections.put( corp, new HashSet<String>( Arrays.asList( new String[] {"móðir"} ) ) );
				}
				
				if( maki.length() > 0 ) {
					Corp mcorp = null;
					if( Corp.corpMap.containsKey(maki) ) {
						mcorp = Corp.corpMap.get(maki);
					} else {
						mcorp = new Corp( maki );
						mcorp.type = "person";
						mcorp.setx( rand.nextInt(fasti) );
						mcorp.sety( rand.nextInt(fasti) );
						mcorp.setz( rand.nextInt(fasti)-zoffset );
						c.add( mcorp );
					}
					
					corp.connections.put( mcorp, new HashSet<String>( Arrays.asList( new String[] {"maki"} ) ) );
					mcorp.connections.put( corp, new HashSet<String>( Arrays.asList( new String[] {"maki"} ) ) );
				}
				
				int l = 0;
				while( l < 5 ) {
					cell = corpRow.getCell(l);
					if( cell != null && cell.getCellType() == XSSFCell.CELL_TYPE_STRING ) {
						String	id = cell.getStringCellValue();
						
						if( id.length() > 0 ) {
							Corp link = null;
							if( !Corp.corpMap.containsKey(id) ) {
								link = new Corp( id );
								link.type = "corp";
								link.setx( rand.nextInt(fasti) );
								link.sety( rand.nextInt(fasti) );
								link.setz( rand.nextInt(fasti)-zoffset );
								c.add( link );
								//link.setBounds( (int)(link.x-Corp.size/2), (int)(link.y-Corp.size/2), Corp.size, Corp.size );
							} else {
								link = Corp.corpMap.get( id );
							}
							
							corp.connections.put( link, new HashSet<String>( Arrays.asList( new String[] {"link"} ) ) );
							link.connections.put( corp, new HashSet<String>( Arrays.asList( new String[] {"link"} ) ) );
						}
					}
					l++;
				}
			}
			
			corpRow = corpSheet.getRow( ++i );
		}
		Spilling.this.repaint();
	}
	
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
		
		Window window = SwingUtilities.windowForComponent(this);
		if (window instanceof JFrame) {
			JFrame frame = (JFrame)window;
			if (!frame.isResizable()) frame.setResizable(true);
		}
		
		this.getContentPane().setBackground( Color.white );
		
		Corp.prop = new Prop();
		Corp.prop.setBounds(0, 0, 400, 75);
		
		JScrollPane	scrollpane = new JScrollPane();
		c = new JComponent() {
			long last = 0;
			
			public boolean isVisible() {
				return super.isVisible() && birta;
			}
			
			public void paintComponent( Graphics g ) {
				super.paintComponent( g );
				
				if( !shift ) {
					birta = false;
					if( toggle ) {
						spring();
					}
					depth();
					birta = true;
				}
				
				g.setColor( Color.white );
				g.fillRect(0, 0, this.getWidth(), this.getHeight());
				
				Graphics2D	g2 = (Graphics2D)g;
				g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
				g2.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
				
				Font	oldFont = g2.getFont();
				if( oldFont.getSize() != 7 ) {
					oldFont = oldFont.deriveFont(8.0f);
					g2.setFont( oldFont );
				}
				if( drawLinks ) {
					for( Component c : this.getComponents() ) {
						if( c instanceof Corp ) {
							Corp corp = (Corp)c;						
							Rectangle inrect = this.getVisibleRect();
							for( Corp cc : corp.getLinks() ) {
								int x1 = c.getX()+c.getWidth()/2;
								int y1 = c.getY()+c.getHeight()/2;
								int x2 = cc.getX()+cc.getWidth()/2;
								int y2 = cc.getY()+cc.getHeight()/2;
								
								if( cc.depz > 0.0 && (inrect.contains(x1, y1) || inrect.contains(x2, y2)) ) {
									g.setColor( Color.gray );
									g.drawLine( x1, y1, x2, y2 );
									
									if( drawLinkNames && !toggle && p == null ) {
										Set<String> strset = corp.connections.get(cc);
										int x = (x1+x2)/2;
										int y = (y1+y2)/2;
										double t = Math.atan2( y2-y1, x2-x1 );
										g2.rotate(t, x, y);
										int k = 0;
										g.setColor( Color.black );
										for( String str : strset ) {
											if( !str.equals("link") ) {
												int strw = g.getFontMetrics().stringWidth( str );
												//if( corp.selectedLink != null ) System.err.println( corp.selectedLink );
												if( cc == linkCorp2 && str.equals( corp.selectedLink ) ) {
													g2.setFont( oldFont.deriveFont( Font.BOLD ) );
												}
												g.drawString( str, x-strw/2, y-5-k );
												if( g2.getFont() != oldFont ) g2.setFont( oldFont );
												k += 10;
											}
										}
										g2.rotate(-t, x, y);
									}
								}
							}
						}
					}
				}
				
				for( Component c : this.getComponents() ) {
					if( c instanceof Corp ) {
						Corp corp = (Corp)c;
						int strWidth = g.getFontMetrics().stringWidth( corp.getName() );
						g.setColor( Color.black );
						if( (corp.type.equals("person") && drawPersonNames) || (corp.type.equals("corp") && drawCorpNames) ) {
							if( corp.getName().length() > 50 ) g.drawString( corp.getName().substring(0, 50), c.getX()+(c.getWidth()-strWidth)/2, c.getY()+c.getHeight()+15 );
							else g.drawString( corp.getName(), c.getX()+(c.getWidth()-strWidth)/2, c.getY()+c.getHeight()+15 );
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
				
				if( toggle ) {
					long val = System.currentTimeMillis();
					long diff = val - last;
					val = last;
					
					if( diff < 100 ) {
						try {
							Thread.sleep(100-diff);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					this.repaint();
				}
			}
		};
		c.addMouseListener( this );
		c.addMouseMotionListener( this );
		c.addKeyListener( this );
		
		try {
			loadAll();
		} catch (IOException e2) {
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
						p.currentCorp.setName( p.name.getText() );
						p.currentCorp.kt = p.kt.getText();
						p.currentCorp.text = p.text.getText();
						if( p.currentCorp.text.length() > 0 ) p.currentCorp.setToolTipText( p.currentCorp.text );
						
						try {
							p.currentCorp.save();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				}
			}
			
			@Override
			public void componentAdded(ContainerEvent e) {}
		});
		
		final JPopupMenu	popup = new JPopupMenu();
		popup.add( new AbstractAction("Show/Hide Links") {
			@Override
			public void actionPerformed(ActionEvent e) {
				drawLinks = !drawLinks;
				c.repaint();
			}
		});
		popup.add( new AbstractAction("Show/Hide Links Names") {
			@Override
			public void actionPerformed(ActionEvent e) {
				drawLinkNames = !drawLinkNames;
				c.repaint();
			}
		});
		popup.add( new AbstractAction("Show/Hide Person Names") {
			@Override
			public void actionPerformed(ActionEvent e) {
				drawPersonNames = !drawPersonNames;
				c.repaint();
			}
		});
		popup.add( new AbstractAction("Show/Hide Corp Names") {
			@Override
			public void actionPerformed(ActionEvent e) {
				drawCorpNames = !drawCorpNames;
				c.repaint();
			}
		});
		popup.addSeparator();
		popup.add( new AbstractAction("Enable/Disable autosave") {
			@Override
			public void actionPerformed(ActionEvent e) {
				Corp.autosave = !Corp.autosave;
			}
		});
		popup.addSeparator();
		popup.add( new AbstractAction("Add Person") {
			@Override
			public void actionPerformed(ActionEvent e) {
				Corp 		corp = new Corp("unknown","person",0.0,0.0);
				
				//backtest( m.x, m.y );
				
				//System.err.println( m );
				backtrack( m, corp );
				try {
					corp.save();
				} catch (IOException e2) {
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
					e2.printStackTrace();
				}
				c.add( corp );
				c.repaint();
			}
		});
		popup.addSeparator();
		
		popup.add( new AbstractAction("Load sample data") {
			@Override
			public void actionPerformed(ActionEvent e) {
				InputStream is = this.getClass().getResourceAsStream("/Greining2.xlsx");
				try {
					excelLoad( is );
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		
		popup.add( new AbstractAction("Import from dirty Excel") {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				if( fc.showOpenDialog( Spilling.this ) == JFileChooser.APPROVE_OPTION ) {
					File f = fc.getSelectedFile();
					try {
						excelLoad( new FileInputStream( f ) );
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					} catch (IOException e2) {
						e2.printStackTrace();
					}
				}
			}
		});
		popup.add( new AbstractAction("Import from Excel") {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				if( fc.showOpenDialog( Spilling.this ) == JFileChooser.APPROVE_OPTION ) {
					File f = fc.getSelectedFile();
					XSSFWorkbook workbook;
					try {
						workbook = new XSSFWorkbook( f.getCanonicalPath() );
						XSSFSheet corpSheet = workbook.getSheet("Corps");
						XSSFSheet linkSheet = workbook.getSheet("Links");
						
						Corp.corpMap.clear();
						c.removeAll();
						
						int i = 0;
						int l = 0;
						XSSFRow 	corpRow = corpSheet.getRow( ++i );
						while( corpRow != null ) {
							XSSFCell	cell = corpRow.getCell(0);
							String 	name = cell.getStringCellValue();
							cell = corpRow.getCell(1);
							String 	type = cell.getStringCellValue();
							cell = corpRow.getCell(2);
							String 	kt = cell.getStringCellValue();
							cell = corpRow.getCell(3);
							String 	text = cell.getStringCellValue();
							cell = corpRow.getCell(4);
							double x = cell.getNumericCellValue();
							cell = corpRow.getCell(5);
							double y = cell.getNumericCellValue();
							cell = corpRow.getCell(6);
							double z = cell.getNumericCellValue();
							
							Corp corp = new Corp( name );
							corp.setName( name );
							corp.type = type;
							corp.kt = kt;
							corp.text = text;
							corp.setx( x );
							corp.sety( y );
							corp.setz( z );
							c.add( corp );
							corp.setBounds( (int)(corp.getx()-corp.size/2), (int)(corp.gety()-corp.size/2), corp.size, corp.size );
							
							corpRow = corpSheet.getRow( ++i );
						}
						System.err.println( c.getComponentCount() );
						
						XSSFRow 	linkRow = linkSheet.getRow( ++l );
						while( linkRow != null ) {
							XSSFCell cell = linkRow.getCell(0);
							int 	id1 = (int)cell.getNumericCellValue();
							cell = linkRow.getCell(1);
							int		id2 = (int)cell.getNumericCellValue();
							cell = linkRow.getCell(2);
							String 	str = cell.getStringCellValue();
							
							Corp p1 = Corp.corpMap.get(id1);
							Corp p2 = Corp.corpMap.get(id2);
							
							String[] ss = str.split("\n");
							Set<String> 	value = new HashSet<String>( Arrays.asList(ss) );
							p1.connections.put( p2, value );
							
							linkRow = linkSheet.getRow( ++l );
						}
						Spilling.this.repaint();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		popup.add( new AbstractAction("Open in Excel") {
			@Override
			public void actionPerformed(ActionEvent e) {
				//JFileChooser fc = new JFileChooser();
				//if( fc.showSaveDialog( Spilling.this ) == JFileChooser.APPROVE_OPTION ) {
				//File f = fc.getSelectedFile();
				try {
					File nf = File.createTempFile("tmp", ".xlsx");
					XSSFWorkbook 	workbook = new XSSFWorkbook();
					
					XSSFSheet corpSheet = workbook.createSheet("Corps");
					XSSFSheet linkSheet = workbook.createSheet("Links");
					
					int i = 0;
					int l = 0;
					XSSFRow 	corpRow = corpSheet.createRow( i );
					XSSFCell cell = corpRow.createCell( 0 );
					cell.setCellValue( "id" );
					cell = corpRow.createCell( 1 );
					cell.setCellValue( "name" );
					cell = corpRow.createCell( 2 );
					cell.setCellValue( "type" );
					cell = corpRow.createCell( 3 );
					cell.setCellValue( "kt" );
					cell = corpRow.createCell( 4 );
					cell.setCellValue( "desc" );
					cell = corpRow.createCell( 5 );
					cell.setCellValue( "x" );
					cell = corpRow.createCell( 6 );
					cell.setCellValue( "y" );
					cell = corpRow.createCell( 7 );
					cell.setCellValue( "z" );
					cell = corpRow.createCell( 8 );
					cell.setCellValue( "image" );
					
					XSSFRow 		linkRow = linkSheet.createRow( l );
					cell = linkRow.createCell( 0 );
					cell.setCellValue( "id1" );
					cell = linkRow.createCell( 1 );
					cell.setCellValue( "id1" );
					cell = linkRow.createCell( 2 );
					cell.setCellValue( "desc" );
					
					for( String name : Corp.corpMap.keySet() ) {
						Corp 		corp = Corp.corpMap.get( name );
						corpRow = corpSheet.createRow( ++i );
						
						cell = corpRow.createCell( 0 );
						cell.setCellValue( corp.getName() );
						cell = corpRow.createCell( 1 );
						cell.setCellValue( corp.type );
						cell = corpRow.createCell( 2 );
						cell.setCellValue( corp.kt );
						cell = corpRow.createCell( 3 );
						cell.setCellValue( corp.text );
						cell = corpRow.createCell( 4 );
						cell.setCellValue( corp.getx() );
						cell = corpRow.createCell( 5 );
						cell.setCellValue( corp.gety() );
						cell = corpRow.createCell( 6 );
						cell.setCellValue( corp.getz() );
						cell = corpRow.createCell( 7 );
						if( corp.imageNames.size() > 0 && corp.imageNames.get(0) != null ) {
							cell.setCellValue( corp.imageNames.get(0) );
						}
						
						for( Corp cp : corp.connections.keySet() ) {
							Set<String>		link = corp.connections.get(cp);
							linkRow = linkSheet.createRow( ++l );
							cell = linkRow.createCell( 0 );
							cell.setCellValue( corp.getName() );
							cell = linkRow.createCell( 1 );
							cell.setCellValue( cp.getName() );
							cell = linkRow.createCell( 2 );
							String val = "";
							for( String str : link ) {
								val += str+"\n";
							}
							cell.setCellValue( val );
						}
					}
					workbook.write( new FileOutputStream( nf ) );
					Desktop.getDesktop().open( nf );
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		popup.addSeparator();
		popup.add( new AbstractAction("Spring Graph") {
			@Override
			public void actionPerformed(ActionEvent e) {
				//if( !toggle ) springThread().start();
				
				toggle = !toggle;
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
		np = e.getPoint();
		if( shift ) {
			selRect = new Rectangle( Math.min(p.x, np.x), Math.min(p.y, np.y), Math.abs(p.x-np.x), Math.abs(p.y-np.y) );
			
			/*Rectangle rect = c.getVisibleRect();			
			rect.translate( p.x-np.x, p.y-np.y );
			c.scrollRectToVisible( rect );*/
		}
		c.repaint();
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
				if( !f.isDirectory() ) {
					Corp corp = new Corp( f.getName() );
					c.add( corp );
				}
				//addFile( f );
			}
			
			for( String name : Corp.corpMap.keySet() ) {
				Corp c = Corp.corpMap.get( name );
				c.load();
			}
		}
	}
	
	public void addCorp( String name ) throws IOException {
		c.add( load( name ) );
	}
	
	public void addFile( File save ) throws IOException {
		c.add( loadFile( save ) );
	}
	
	public Corp load( String name ) throws IOException {
		Corp corp = new Corp( name );
		corp.load();
		return corp;
	}
	
	public Corp loadFile( File save ) throws IOException {
		String name = save.getName();
		Corp corp = new Corp( name );
		corp.loadFile( save );
		return corp;
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		m = e.getPoint();
	}

	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {
		toggle = false;
		p = e.getPoint();
		np = p;
		shift = (e.getModifiersEx() & MouseEvent.SHIFT_DOWN_MASK) != 0;
		
		c.requestFocus();
		
		Corp.selectedList.clear();
		linkCorp = null;
		linkCorp2 = null;
		for( Component comp : c.getComponents() ) {
			if( comp instanceof Corp ) {
				Corp corp = ((Corp)comp);
				corp.selected = false;
				corp.selectedLink = null;
				for( Corp corp2 : corp.connections.keySet() ) {						
					double x1 = corp.getX() + corp.getWidth()/2;
					double x2 = corp2.getX() + corp2.getWidth()/2;
					double y1 = corp.getY() + corp.getHeight()/2;
					double y2 = corp2.getY() + corp2.getHeight()/2;
					double xx = (x1 + x2) / 2.0;
					double yy = (y1 + y2) / 2.0;
					
					double h1 = Math.atan2( x1-xx, y1-yy );
					//if( h1 < 0 ) h1 += 2*Math.PI;
					double h2 = Math.atan2( p.x-xx, p.y-yy );
					//if( h2 < 0 ) h2 += 2*Math.PI;
					double h = h2 - h1;
					if( h > Math.PI ) h -= 2*Math.PI;
					if( h < -Math.PI ) h += 2*Math.PI;
					/*System.err.println( (x1) + "  " + (y1) );
					System.err.println( (x2) + "  " + (y2) );
					System.err.println( (x1-xx) + "  " + (y1-yy) );
					System.err.println( h1+ "  " + h2 + "  " + h );*/
					if( h < 0 ) {
						Set<String>	strset = corp.connections.get(corp2);
						if( strset != null && strset.size() > 0 && p.distance( xx, yy ) < 32 ) {
							linkCorp = corp;
							linkCorp2 = corp2;
							
							System.err.println("found link " + linkCorp.getName() + "  " + linkCorp2.getName() );
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
		}
		
		if( p != null && !shift ) {
			double d = (np.x - p.x) / 100.0 + hhy + Math.PI;
			double h = Math.floor( d / (2.0*Math.PI) );
			double nd = d - h * (2.0*Math.PI) - Math.PI;
			hhy = nd; //(np.x - p.x) / 100.0 + hhy;
			/*} else {
				double d = (np.x - p.x) / 100.0 + hhz + Math.PI;
				double h = Math.floor( d / (2.0*Math.PI) );
				double nd = d - h * (2.0*Math.PI) - Math.PI;
				hhz = nd;
			}*/
			
			d = (np.y - p.y) / 100.0 + hhx + Math.PI;
			h = Math.floor( d / (2.0*Math.PI) );
			nd = d - h * (2.0*Math.PI) - Math.PI;
			hhx = nd;
		}
		
		p = null;
		c.repaint();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		//System.err.println( KeyEvent.getModifiersExText(e.getModifiersEx()) );
		if( e.getKeyChar() == '+' ) {
			zoomval -= 100;
		} else if( e.getKeyChar() == '-' ) {
			zoomval += 100;
		} else if( e.getKeyChar() == '*' ) {
			dzoomval -= 100;
		} else if( e.getKeyChar() == '/' ) {
			dzoomval += 100;
		} else if( linkCorp != null ) {
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
		} else {
			if( e.getKeyCode() == KeyEvent.VK_DELETE ) {
				Set<Corp>	delset = new HashSet<Corp>();
				for( String name : Corp.corpMap.keySet() ) {
					Corp c = Corp.corpMap.get(name);
					if( c.selected ) delset.add( c );
				}
				
				for( Corp c : delset ) {
					c.delete();
				}
			}
		}
		
		c.repaint();
	}

	@Override
	public void keyReleased(KeyEvent e) {}

	@Override
	public void keyTyped(KeyEvent e) {}
}
