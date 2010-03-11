package org.simmi;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import org.simmi.RecipePanel.Recipe;

public class GraphPanel extends JTabbedPane {
	JComponent		energy;
	JComponent		energyPart;
	VitaminPanel	vitaminv;
	VitaminPanel	vitaminf;
	boolean			hringur = false;
	
	JCompatTable	table, leftTable, topTable;
	TableModel	topModel;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8830688610876166912L;

	public float stuffYou( String whr ) {
		int r = leftTable.getSelectedRow();
		if( r >= 0 && r < leftTable.getRowCount() ) {
			//r = leftTable.convertRowIndexToModel(r);
			return stuffYou( r, whr );
		}
		return -1.0f;
	}
	
	public float stuffYou( int row, String whr ) {
		float f = -1.0f;
		
		TableColumn	tc = null;
		try {
			tc = table.getColumn(whr);
		} catch( Exception e ) {
			
		}
		
		if( tc != null ) {
			int col = tc.getModelIndex();
		/*String val = (String)topModel.getValueAt(0, col);
		while( !(val != null && val.contains(whr)) ) {
			col++;
			if( col < topModel.getColumnCount() ) {
				val = (String)topModel.getValueAt(0, col);
			} else break;
		}
		if( col < model.getColumnCount() ) {*/
			Float ff = (Float)table.getValueAt(row, col);
			if( ff != null ) f = ff;
		} else {
			System.err.println(whr);
		}
			
		return f;
	}
	
	Font font;
	public GraphPanel( final RdsPanel rdsPanel, final RecipePanel rpsPanel, final int stuffsize, final String lang, JCompatTable[]	tables, TableModel topModel ) {
		super( JTabbedPane.RIGHT, JTabbedPane.WRAP_TAB_LAYOUT );
		
		table = tables[0];
		leftTable = tables[1];
		topTable = tables[2];
		this.topModel = topModel;
		
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		final PrintStream ps = new PrintStream( baos );
		
		font = new Font("Arial", Font.BOLD, this.getHeight()/40 );
		energyPart = new JComponent() {
			public void paintComponent( Graphics g ) {
				super.paintComponent( g );
				
				Graphics2D g2 = (Graphics2D)g;
				g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
				g2.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
				
				int row = leftTable.getSelectedRow();				
				if( row != -1 ) {
					Object oval = leftTable.getValueAt(row, 0);
					
					g2.setColor(Color.darkGray);
					
					int htest = this.getHeight()/40;
					if( font.getSize() != htest ) font = new Font("Arial", Font.BOLD, this.getHeight()/40 );
					g2.setFont( font );
					if( oval != null ) {
						String sval = oval.toString();
						if( sval.length() > 25 ) {
							sval = sval.substring(0, 22)+"...";
						}
						g2.drawString( sval, 10, this.getHeight()/25 );
					}
					
					String enStr = "";
					if( lang.equals("IS") ) {
						enStr = "Orka (í 100g)";
					} else {
						enStr = "Energy (in 100g)";
					}
					g2.drawString( enStr, 10, (int)(this.getHeight()/13.7) );
							
					float alc = 0.0f;
					float prt = 0.0f;
					float cbh = 0.0f;
					float fat = 0.0f;
					float fib = 0.0f;
					
					if( lang.equals("IS") ) {
						alc = stuffYou(row, "Alkóhól");
						prt = stuffYou(row, "Prótein, alls");
						cbh = stuffYou(row, "Kolvetni, alls");
						fat = stuffYou(row, "Fita, alls");
						fib = stuffYou(row, "Trefjaefni");
					} else {
						alc = stuffYou(row, "ALC");
						prt = stuffYou(row, "PROCNT");
						cbh = stuffYou(row, "CHOCDF");
						fat = stuffYou(row, "FAT");
						fib = stuffYou(row, "FIB");
					}
					
					double alco = alc*29.0;
					double prto = prt*17.0;
					double cbho = cbh*17.0;
					double fato = fat*37.0;
					double fibo = fib*8.0;
					double f = (alco + prto + cbho + fato + fibo)*100.0;
					
					if( lang.equals("IS") ) {
						if( f > 0 ) {
							double fv = Math.round( f )/100.0;
							
							double fcal = alc*7.0 + prt*4.0 + cbh*4.0 + fat*9.0 + 2.0*fib; 
							fcal *= 100.0;
							//double fcal = f/4.184;
							double fvcal = Math.round( fcal )/100.0;
							
							ps.flush();
							baos.reset();
							ps.printf( "%.1f %s", fvcal, " kcal" );
							String calStr = baos.toString();
							int strw = g.getFontMetrics().stringWidth(calStr);
							g2.drawString(calStr, 10, this.getHeight()/9 );
							
							ps.flush();
							baos.reset();
							ps.printf(" (%.1f %s)", fv, "kJ" );
							String kjStr = baos.toString();
							g2.drawString(kjStr, 15+strw, this.getHeight()/9 );
						}
					} else {
						float ff = stuffYou( row, "ENERC_KJ" );
						if( ff > 0 ) {
							g2.drawString(ff+" kJ", 10, this.getHeight()/10 );
						}
						
						ff = stuffYou( row, "ENERC_KCAL" );
						if( ff > 0 ) {
							g2.drawString(ff+" kcal", 10, this.getHeight()/7 );
						}
					}
					
					float total = alc + prt + cbh + fat + fib;
					
					if( total > 0 ) {
						String title = GraphPanel.this.getTitleAt( GraphPanel.this.getSelectedIndex() );
						int w = this.getWidth();
						int h = this.getHeight();
						
						int torig = Math.min(w, h);
						int t = (3*torig)/4;
						
						Paint p = g2.getPaint();
						
						Color r1 = new Color( 200, 100, 100 );
						Color r2 = new Color( 200, 150, 150 );
						GradientPaint gp = new GradientPaint( (w-t)/2, (h-t)/2, r1, (w+t)/2, (h+t)/2, r2 );
						g2.setPaint( gp );
						int n = (int)((alc*360.0f)/total);
						g2.fillArc( (w-t)/2, (h-t)/2, t, t, 0, n );
						
						Color g1 = new Color( 100, 200, 100 );
						Color gn = new Color( 150, 200, 150 );
						gp = new GradientPaint( (w-t)/2, (h-t)/2, g1, (w+t)/2, (h+t)/2, gn );
						g2.setPaint( gp );
						int nn = (int)(((alc+prt)*360.0f)/total);
						g2.fillArc( (w-t)/2, (h-t)/2, t, t, n, nn-n );
						
						Color b1 = new Color( 100, 100, 200 );
						Color b2 = new Color( 150, 150, 200 );
						gp = new GradientPaint( (w-t)/2, (h-t)/2, b1, (w+t)/2, (h+t)/2, b2 );
						g2.setPaint( gp );
						n = nn;
						nn = (int)(((alc+prt+cbh)*360.0f)/total);
						g2.fillArc( (w-t)/2, (h-t)/2, t, t, n, nn-n );
						
						Color y1 = new Color( 200, 200, 100 );
						Color y2 = new Color( 200, 200, 150 );
						gp = new GradientPaint( (w-t)/2, (h-t)/2, y1, (w+t)/2, (h+t)/2, y2 );
						g2.setPaint( gp );
						n = nn;
						nn = (int)(((alc+prt+cbh+fat)*360.0f)/total);
						g2.fillArc( (w-t)/2, (h-t)/2, t, t, n, nn-n );
						
						Color o1 = new Color( 200, 150, 50 );
						Color o2 = new Color( 250, 180, 100 );
						gp = new GradientPaint( (w-t)/2, (h-t)/2, o1, (w+t)/2, (h+t)/2, o2 );
						g2.setPaint( gp );
						n = nn;
						nn = (int)(360.0f);
						g2.fillArc( (w-t)/2, (h-t)/2, t, t, n, nn-n );
						
						g2.setPaint(p);
						
						g2.setColor( Color.darkGray );
						n = (int)((alc*360.0f)/total);
						g2.drawArc( (w-t)/2, (h-t)/2, t, t, 0, n );
						nn = (int)(((alc+prt)*360.0f)/total);
						g2.drawArc( (w-t)/2, (h-t)/2, t, t, n, nn-n );
						n = nn;
						nn = (int)(((alc+prt+cbh)*360.0f)/total);
						g2.drawArc( (w-t)/2, (h-t)/2, t, t, n, nn-n );
						n = nn;
						nn = (int)(((alc+prt+cbh+fat)*360.0f)/total);
						g2.drawArc( (w-t)/2, (h-t)/2, t, t, n, nn-n );
						n = nn;
						nn = (int)(360.0f);
						g2.drawArc( (w-t)/2, (h-t)/2, t, t, n, nn-n );
						
						float[] val = { (float)alc, (float)prt, (float)cbh, (float)fat, (float)fib };
						g2.setFont( new Font("Arial", Font.BOLD, this.getHeight()/40 ) );
						float tot = 0.0f;
						float last = 0.0f;
						int i = 0;
						double tt = (5*torig)/6;
						for( float ff : val ) {
							tot += ff;
							if( ff > 1.0f ) {
								double dn = -(last*2.0*Math.PI)/total;
								double dnn = -((tot)*2.0*Math.PI)/total;
								double hrn = (dn+dnn)/2.0;
								
								double cmpt = (100.0*ff)/total;
								String fstr =  Double.toString( Math.floor( 10.0*cmpt ) / 10.0 );
								int strw = g2.getFontMetrics().stringWidth( fstr );
								int strh = g2.getFontMetrics().getHeight();
								g2.drawString( fstr, (int)((w+tt*Math.cos(hrn)-strw)/2.0), (int)((h+tt*Math.sin(hrn)+strh)/2.0) );
								//g2.drawArc( (w-t)/2, (h-t)/2, t, t, n, nn-n );
							}
							last = tot;
							i++;
						}
						
						int a = 10;
						int hh = this.getHeight()/30;
						g2.setColor( r1 );
						g2.fillRoundRect( (19*w)/20, (1*this.getHeight())/25-hh/2, hh, hh, a, a );
						g2.setColor( g1 );
						g2.fillRoundRect( (19*w)/20, (2*this.getHeight())/25-hh/2, hh, hh, a, a );
						g2.setColor( b1 );
						g2.fillRoundRect( (19*w)/20, (3*this.getHeight())/25-hh/2, hh, hh, a, a );
						g2.setColor( y1 );
						g2.fillRoundRect( (19*w)/20, (4*this.getHeight())/25-hh/2, hh, hh, a, a );
						g2.setColor( o1 );
						g2.fillRoundRect( (19*w)/20, (5*this.getHeight())/25-hh/2, hh, hh, a, a );
						
						g2.setColor( Color.darkGray );
						g2.drawRoundRect( (19*w)/20, (1*this.getHeight())/25-hh/2, hh, hh, a, a );
						g2.drawRoundRect( (19*w)/20, (2*this.getHeight())/25-hh/2, hh, hh, a, a );
						g2.drawRoundRect( (19*w)/20, (3*this.getHeight())/25-hh/2, hh, hh, a, a );
						g2.drawRoundRect( (19*w)/20, (4*this.getHeight())/25-hh/2, hh, hh, a, a );
						g2.drawRoundRect( (19*w)/20, (5*this.getHeight())/25-hh/2, hh, hh, a, a );
						
						g2.setFont( new Font("Arial", Font.BOLD, this.getHeight()/40 ) );
						g2.setColor( Color.darkGray );
						String str;
						if( lang.equals("IS") ) str = "Alkóhól";
						else str = "Alcohol";
						int strw = g2.getFontMetrics().stringWidth(str);
						g2.drawString(str, (19*w)/20-strw-hh/2, (1*this.getHeight())/25+hh/4 );
						if( lang.equals("IS") ) str = "Prótein";
						else str = "Protein";
						strw = g2.getFontMetrics().stringWidth(str);
						g2.drawString(str, (19*w)/20-strw-hh/2, (2*this.getHeight())/25+hh/4 );
						if( lang.equals("IS") ) str = "Kolvetni";
						else str = "Carbohydrates";
						strw = g2.getFontMetrics().stringWidth(str);
						g2.drawString(str, (19*w)/20-strw-hh/2, (3*this.getHeight())/25+hh/4 );
						if( lang.equals("IS") ) str = "Fita";
						else str = "Fat";
						strw = g2.getFontMetrics().stringWidth(str);
						g2.drawString(str, (19*w)/20-strw-hh/2, (4*this.getHeight())/25+hh/4 );
						if( lang.equals("IS") ) str = "Trefjaefni";
						else str = "Dietary fiber";
						strw = g2.getFontMetrics().stringWidth(str);
						g2.drawString(str, (19*w)/20-strw-hh/2, (5*this.getHeight())/25+hh/4 );
					}
				}
			}
		};
		
		energy = new JComponent() {
			public void paintComponent( Graphics g ) {
				super.paintComponent( g );
				
				Graphics2D g2 = (Graphics2D)g;
				g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
				g2.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
				
				int row = leftTable.getSelectedRow();
				//int rrow = row;
				//if( row != -1 ) rrow = leftTable.convertRowIndexToModel(row);
				if( row != -1 ) {
					Object oval = leftTable.getValueAt(row, 0);
					
					g2.setColor(Color.darkGray);
					
					int htest = this.getHeight()/40;
					if( font.getSize() != htest ) font = new Font("Arial", Font.BOLD, this.getHeight()/40 );
					g2.setFont( font );
					if( oval != null ) {
						String sval = oval.toString();
						if( sval.length() > 25 ) {
							sval = sval.substring(0, 22)+"...";
						}
						g2.drawString( sval, 10, this.getHeight()/25 );
					}
					
					float alc = 0.0f;
					float prt = 0.0f;
					float cbh = 0.0f;
					float fat = 0.0f;
					float fib = 0.0f;
					
					if( lang.equals("IS") ) {
						alc = stuffYou(row, "Alkóhól");
						prt = stuffYou(row, "Prótein, alls");
						cbh = stuffYou(row, "Kolvetni, alls");
						fat = stuffYou(row, "Fita, alls");
						fib = stuffYou(row, "Trefjaefni");
					} else {
						alc = stuffYou(row, "ALC");
						prt = stuffYou(row, "PROCNT");
						cbh = stuffYou(row, "CHOCDF");
						fat = stuffYou(row, "FAT");
						fib = stuffYou(row, "FIB");
					}
					double sum = alc + prt + cbh + fat + fib;
					
					double	wgh = 100.0;
					int rrow = leftTable.convertRowIndexToModel(row);
					//int stuffsize = leftTable.getModel().getRowCount();
					if( rrow >= stuffsize - 2 ) {
						int i = rrow - (stuffsize-2);
						Recipe rep = rpsPanel.recipes.get(i);
						wgh = rep.getWeight();
					}
					
					String enStr = "";
					if( lang.equals("IS") ) {
						enStr = "Orka (í "+wgh+"g)";
					} else {
						enStr = "Energy (in "+wgh+"g)";
					}
					g2.drawString( enStr, 10, (int)(this.getHeight()/13.7) );
					
					double alco = alc*29.0;
					double prto = prt*17.0;
					double cbho = cbh*17.0;
					double fato = fat*37.0;
					double fibo = fib*8.0;
					double f = (prto + cbho + fato + alco + fibo)*100.0;
					
					if( lang.equals("IS") ) {
						if( f > 0 ) {
							double fv = Math.round( f )/100.0;
							//double fcal = f/4.184;
							double fcal = alc*7.0 + prt*4.0 + cbh*4.0 + fat*9.0 + 2.0*fib;
							fcal *= 100.0;
							double fvcal = Math.round( fcal )/100.0;
							
							ps.flush();
							baos.reset();
							ps.printf( "%.1f %s", fvcal, " kcal" );
							String calStr = baos.toString();
							int strw = g.getFontMetrics().stringWidth(calStr);
							g2.drawString(calStr, 10, this.getHeight()/9 );
							
							ps.flush();
							baos.reset();
							ps.printf(" (%.1f %s)", fv, "kJ" );
							String kjStr = baos.toString();
							g2.drawString(kjStr, 15+strw, this.getHeight()/9 );
						}
					} else {
						float ff = stuffYou( row, "ENERC_KJ" );
						if( ff > 0 ) {
							g2.drawString(ff+" kJ", 10, this.getHeight()/10 );
						}
						
						ff = stuffYou( row, "ENERC_KCAL" );
						if( ff > 0 ) {
							g2.drawString(ff+" kcal", 10, this.getHeight()/7 );
						}
					}
					
					float total = alc + prt + cbh + fat + fib;
					
					if( total > 0 ) {
						int w = this.getWidth();
						int h = this.getHeight();
						
						//int t = Math.min(w, h);
						int th = (3*h)/4;
						int tw = (3*w)/4;
						int tnrw = (1*w)/2;
						int bil = w/30;
						
						g.setColor( Color.darkGray );
						g.drawLine( (w-tw)/2, (h-th)/2+10, (w-tw)/2, (h+th)/2);
						g.drawLine( (w-tw)/2, (h+th)/2, (w+tw)/2, (h+th)/2);
						
						g2.setFont( new Font("Arial", Font.BOLD, this.getHeight()/40 ) );
						
						double	logf = Math.log10( wgh )-1;
						double	flor = Math.floor( logf );
						double	ceil = Math.ceil( logf );
						double	fval = Math.pow( 10.0, flor );
						double	cval = Math.pow( 10.0, ceil );
						double	dval = (wgh/cval);
						
						double jump = 1.0;
						if( dval < 2.5 ) jump = 0.25;
						else if( dval < 5.0 ) jump = 0.5;
						
						int hh = (h+th)/2;
						String str = "g";
						int strw = g.getFontMetrics().stringWidth(str);
						g.setColor( Color.darkGray );
						g.drawString( str, (w-tw)/2-strw-10, hh);
						for( double i = jump; i < dval; i+=jump ) {
							hh = (int)((h+th)/2 - (i*th) / dval );
							str = i*cval+"";
							strw = g.getFontMetrics().stringWidth(str);
							g.setColor( Color.darkGray );
							g.drawString( str, (w-tw)/2-strw-10, hh);
							g.setColor( Color.lightGray );
							g.drawLine( (w-tw)/2, hh, (w+tw)/2, hh );
						}
						
						Stroke stroke = g2.getStroke();
						float[] ff = {1.0f, 0.0f, 0.0f, 0.0f, 1.0f};
						BasicStroke bs = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 1.0f, ff, 0.0f);
						g2.setStroke( bs );
						
						logf = Math.log10( wgh*9.0 )-1;
						flor = Math.floor( logf );
						ceil = Math.ceil( logf );
						fval = Math.pow( 10.0, flor );
						cval = Math.pow( 10.0, ceil );
						dval = (wgh*9.0)/cval;
						
						jump = 1.0;
						if( dval < 0.25 ) jump = 2.5;
						else if( dval < 0.5 ) jump = 5.0;
						
						hh = (h+th)/2;
						str = "kcal";
						strw = g.getFontMetrics().stringWidth(str);
						g.setColor( Color.darkGray );
						g.drawString( str, (w+tw)/2+10, hh);
						for( double i = jump; i < dval; i+=jump ) {
							hh = (int)((h+th)/2 - (i*th) / dval);
							str = i*cval+"";
							strw = g.getFontMetrics().stringWidth(str);
							g.setColor( Color.darkGray );
							g.drawString( str, (w+tw)/2+10, hh);
							g.setColor( Color.lightGray );
							g.drawLine( (w-tw)/2, hh, (w+tw)/2, hh );
						}
						
						g2.setStroke( stroke );
						Paint p = g2.getPaint();
						
						Color aGray1 = new Color( 220, 220, 220, 224 );
						Color aGray2 = new Color( 220, 220, 220, 64 );
						
						Color r1 = new Color( 200, 100, 100 );
						Color g1 = new Color( 100, 200, 100 );
						Color b1 = new Color( 100, 100, 200 );
						Color y1 = new Color( 200, 200, 100 );
						Color o1 = new Color( 200, 150, 50 );
						//Color o2 = new Color( 250, 180, 100 );
						
						Color r2 = new Color( 250, 150, 150 );
						GradientPaint gp = new GradientPaint( (w-tnrw)/2-bil, 0,r1, (w-tnrw)/2+bil, 0, r2 );
						g2.setPaint( gp );
						//int n = (int)((alc*360.0f)/total);
						int val = (int)(th*alc/wgh);
						g2.fillRect( (w-tnrw)/2-bil, (h+th)/2-val, bil, val );
						int val2 = (int)(th*alco/(37.0*wgh));
						g2.fillRect( (w-tnrw)/2, (h+th)/2-val2, bil, val2 );
						
						gp = new GradientPaint( (w-tnrw)/2-2*bil/3, 0, aGray1, (w-tnrw)/2, 0, aGray2 );
						g2.setPaint( gp );
						g2.fillRect( (w-tnrw)/2-5*bil/6, (h+th)/2-val, 1*bil/3, val );
						g2.fillRect( (w-tnrw)/2+1*bil/6, (h+th)/2-val2, 1*bil/3, val2 );
						
						g2.setPaint(p);
						g2.setColor( Color.darkGray );
						g2.drawRect( (w-tnrw)/2-bil, (h+th)/2-val, bil, val );
						g2.drawRect( (w-tnrw)/2, (h+th)/2-val2, bil, val2 );
						
						Color gn = new Color( 150, 250, 150 );
						gp = new GradientPaint( (w-tnrw/2)/2-bil, 0, g1, (w-tnrw/3)/2+bil, 0, gn );
						g2.setPaint( gp );
						val = (int)(th*prt/wgh);
						g2.fillRect( (w-tnrw/2)/2-bil, (h+th)/2-val, bil, val );
						val2 = (int)(th*prto/(37.0*wgh));
						g2.fillRect( (w-tnrw/2)/2, (h+th)/2-val2, bil, val2 );
						
						gp = new GradientPaint( (w-tnrw/2)/2-2*bil/3, 0, aGray1, (w-tnrw/3)/2, 0, aGray2 );
						g2.setPaint( gp );
						g2.fillRect( (w-tnrw/2)/2-5*bil/6, (h+th)/2-val, 1*bil/3, val );
						g2.fillRect( (w-tnrw/2)/2+1*bil/6, (h+th)/2-val2, 1*bil/3, val2 );
						
						g2.setPaint(p);
						g2.setColor( Color.darkGray );
						g2.drawRect( (w-tnrw/2)/2-bil, (h+th)/2-val, bil, val );
						g2.drawRect( (w-tnrw/2)/2, (h+th)/2-val2, bil, val2 );
						
						Color b2 = new Color( 150, 150, 250 );
						gp = new GradientPaint( (w)/2-bil, 0, b1, (w+tnrw/3)/2+bil, 0, b2 );
						g2.setPaint( gp );
						val = (int)(th*cbh/wgh);
						g2.fillRect( (w)/2-bil, (h+th)/2-val, bil, val );
						val2 = (int)(th*cbho/(37.0*wgh));
						g2.fillRect( (w)/2, (h+th)/2-val2, bil, val2 );
						
						gp = new GradientPaint( (w+tnrw/3)/2-2*bil/3, 0, aGray1, (w+tnrw/3)/2, 0, aGray2 );
						g2.setPaint( gp );
						g2.fillRect( (w)/2-5*bil/6, (h+th)/2-val, 1*bil/3, val );
						g2.fillRect( (w)/2+1*bil/6, (h+th)/2-val2, 1*bil/3, val2 );
						
						g2.setPaint(p);
						g2.setColor( Color.darkGray );
						g2.drawRect( (w)/2-bil, (h+th)/2-val, bil, val );
						g2.drawRect( (w)/2, (h+th)/2-val2, bil, val2 );
						
						Color y2 = new Color( 250, 250, 150 );
						gp = new GradientPaint( (w+tnrw/2)/2-bil, 0, y1, (w+tnrw)/2+bil, 0, y2 );
						g2.setPaint( gp );
						val = (int)(th*fat/wgh);
						g2.fillRect( (w+tnrw/2)/2-bil, (h+th)/2-val, bil, val );
						val2 = (int)(th*fato/(37.0*wgh));
						g2.fillRect( (w+tnrw/2)/2, (h+th)/2-val2, bil, val2 );
						
						gp = new GradientPaint( (w+tnrw/2)/2-2*bil/3, 0, aGray1, (w+tnrw)/2, 0, aGray2 );
						g2.setPaint( gp );
						g2.fillRect( (w+tnrw/2)/2-5*bil/6, (h+th)/2-val, 1*bil/3, val );
						g2.fillRect( (w+tnrw/2)/2+1*bil/6, (h+th)/2-val2, 1*bil/3, val2 );
						
						g2.setPaint(p);
						g2.setColor( Color.darkGray );
						g2.drawRect( (w+tnrw/2)/2-bil, (h+th)/2-val, bil, val );
						g2.drawRect( (w+tnrw/2)/2, (h+th)/2-val2, bil, val2 );
						
						Color o2 = new Color( 250, 180, 100 );
						gp = new GradientPaint( (w+tnrw)/2-bil, 0, o1, (w+tnrw)/2+bil, 0, o2 );
						g2.setPaint( gp );
						val = (int)(th*fib/wgh);
						g2.fillRect( (w+tnrw)/2-bil, (h+th)/2-val, bil, val );
						val2 = (int)(th*fibo/(37.0*wgh));
						g2.fillRect( (w+tnrw)/2, (h+th)/2-val2, bil, val2 );
						
						gp = new GradientPaint( (w+tnrw)/2-2*bil/3, 0, aGray1, (w+tnrw)/2, 0, aGray2 );
						g2.setPaint( gp );
						g2.fillRect( (w+tnrw)/2-5*bil/6, (h+th)/2-val, 1*bil/3, val );
						g2.fillRect( (w+tnrw)/2+1*bil/6, (h+th)/2-val2, 1*bil/3, val2 );
						
						g2.setPaint(p);
						g2.setColor( Color.darkGray );
						g2.drawRect( (w+tnrw)/2-bil, (h+th)/2-val, bil, val );
						g2.drawRect( (w+tnrw)/2, (h+th)/2-val2, bil, val2 );
						
						/*g2.setColor( Color.darkGray );
						n = (int)((alc*360.0f)/total);
						g2.drawArc( (w-t)/2, (h-t)/2, t, t, 0, n );
						nn = (int)(((alc+prt)*360.0f)/total);
						g2.drawArc( (w-t)/2, (h-t)/2, t, t, n, nn-n );
						n = nn;
						nn = (int)(((alc+prt+cbh)*360.0f)/total);
						g2.drawArc( (w-t)/2, (h-t)/2, t, t, n, nn-n );
						n = nn;
						nn = (int)(360.0f);
						g2.drawArc( (w-t)/2, (h-t)/2, t, t, n, nn-n );*/
						
						int a = 10;
						hh = this.getHeight()/30;
						g2.setColor( r1 );
						g2.fillRoundRect( (19*w)/20, (1*this.getHeight())/25-hh/2, hh, hh, a, a );
						g2.setColor( g1 );
						g2.fillRoundRect( (19*w)/20, (2*this.getHeight())/25-hh/2, hh, hh, a, a );
						g2.setColor( b1 );
						g2.fillRoundRect( (15*w)/20, (1*this.getHeight())/25-hh/2, hh, hh, a, a );
						g2.setColor( y1 );
						g2.fillRoundRect( (15*w)/20, (2*this.getHeight())/25-hh/2, hh, hh, a, a );
						g2.setColor( o1 );
						g2.fillRoundRect( (11*w)/20, (1*this.getHeight())/25-hh/2, hh, hh, a, a );
						
						g2.setColor( Color.darkGray );
						g2.drawRoundRect( (19*w)/20, (1*this.getHeight())/25-hh/2, hh, hh, a, a );
						g2.drawRoundRect( (19*w)/20, (2*this.getHeight())/25-hh/2, hh, hh, a, a );
						g2.drawRoundRect( (15*w)/20, (1*this.getHeight())/25-hh/2, hh, hh, a, a );
						g2.drawRoundRect( (15*w)/20, (2*this.getHeight())/25-hh/2, hh, hh, a, a );
						g2.drawRoundRect( (11*w)/20, (1*this.getHeight())/25-hh/2, hh, hh, a, a );
						
						g2.setFont( new Font("Arial", Font.BOLD, this.getHeight()/40 ) );
						g2.setColor( Color.darkGray );
						if( lang.equals("IS") ) str = "Alkóhól";
						else str = "Alcohol";
						strw = g2.getFontMetrics().stringWidth(str);
						g2.drawString(str, (19*w)/20-strw-hh/2, (1*this.getHeight())/25+hh/4 );
						if( lang.equals("IS") ) str = "Prótein";
						else str = "Protein";
						strw = g2.getFontMetrics().stringWidth(str);
						g2.drawString(str, (19*w)/20-strw-hh/2, (2*this.getHeight())/25+hh/4 );
						if( lang.equals("IS") ) str = "Kolvetni";
						else str = "Carbohydrates";
						strw = g2.getFontMetrics().stringWidth(str);
						g2.drawString(str, (15*w)/20-strw-hh/2, (1*this.getHeight())/25+hh/4 );
						if( lang.equals("IS") ) str = "Fita";
						else str = "Fat";
						strw = g2.getFontMetrics().stringWidth(str);
						g2.drawString(str, (15*w)/20-strw-hh/2, (2*this.getHeight())/25+hh/4 );
						if( lang.equals("IS") ) str = "Trefjaefni";
						else str = "Dietary fiber";
						strw = g2.getFontMetrics().stringWidth(str);
						g2.drawString(str, (11*w)/20-strw-hh/2, (1*this.getHeight())/25+hh/4 );
					}
				}
			}
		};
		
		/*energy.addMouseListener( new MouseAdapter() {
			public void mousePressed( MouseEvent e ) {
				if( GraphPanel.this.getSelectedComponent() == energy ) GraphPanel.this.setSelectedComponent( energyPart );
				else GraphPanel.this.setSelectedComponent( energy );
				energy.repaint();
			}
		});*/
		
		String[] comb;
		String[] cnames;
		if( lang.equals("IS") ) {
			comb = new String[] {"Alkóhól", "Prótein, alls", "Kolvetni, alls", "Fita, alls", "Trefjaefni", "Steinefni, alls", "Vatn"};
			cnames = new String[] {"Alkóhól", "Prótein", "Kolvetni", "Fita", "Trefjaefni", "Steinefni", "Vatn"};
		} else {
			comb = new String[] {"Alcohol, ethyl", "Protein", "Carbohydrate, by difference", "Total lipid (fat)", "Dietary fiber", "Ash", "Water"};
			cnames = new String[] {"Alcohol", "Protein", "Carbohydrades", "Fat", "Dietary fiber", "Ash", "Water"};
		}
		
		SkifuGraph perc = new SkifuGraph( "Hlutföll", comb, cnames, tables );
		
		vitaminv = new VitaminPanel( true, leftTable, this, rdsPanel, lang );
		vitaminf = new VitaminPanel( false, leftTable, this, rdsPanel, lang );
		
		if( lang.equals("IS") ) {
			this.addTab(null, new VerticalTextIcon( "Hlutföll",  tabPlacement==JTabbedPane.RIGHT ), perc);
			this.addTab(null, new VerticalTextIcon( "Orka",  tabPlacement==JTabbedPane.RIGHT ), energy);
			this.addTab(null, new VerticalTextIcon( "Orkuhlutföll",  tabPlacement==JTabbedPane.RIGHT ), energyPart);
			this.addTab(null, new VerticalTextIcon( "Vatnsl-Vítm",  tabPlacement==JTabbedPane.RIGHT ), vitaminv);
			this.addTab(null, new VerticalTextIcon( "Fitul-Vítm",  tabPlacement==JTabbedPane.RIGHT ), vitaminf);
		} else {
			this.addTab(null, new VerticalTextIcon( "Range",  tabPlacement==JTabbedPane.RIGHT ), perc);
			this.addTab(null, new VerticalTextIcon( "Energy",  tabPlacement==JTabbedPane.RIGHT ), energy);
			this.addTab(null, new VerticalTextIcon( "Energy-Range",  tabPlacement==JTabbedPane.RIGHT ), energyPart);
			this.addTab(null, new VerticalTextIcon( "Water-Vitam",  tabPlacement==JTabbedPane.RIGHT ), vitaminv);
			this.addTab(null, new VerticalTextIcon( "Fat-Vitam",  tabPlacement==JTabbedPane.RIGHT ), vitaminf);
		}
	}
	
	public class VerticalTextIcon implements Icon, SwingConstants {
	    private Font font = UIManager.getFont("Label.font");
	    private FontMetrics fm = Toolkit.getDefaultToolkit().getFontMetrics(font); 
	 
	    private String text; 
	    private int width, height; 
	    private boolean clockwize; 
	 
	    public VerticalTextIcon(String text, boolean clockwize){ 
	        this.text = text; 
	        width = SwingUtilities.computeStringWidth(fm, text); 
	        height = fm.getHeight();
	        this.clockwize = clockwize; 
	    } 
	 
	    public void paintIcon(Component c, Graphics g, int x, int y){
	    	fm = g.getFontMetrics();
	    	width = SwingUtilities.computeStringWidth(fm, text);
	    	height = fm.getHeight();
	    	
	        Graphics2D g2 = (Graphics2D)g;
	        g2.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
	        Font oldFont = g.getFont(); 
	        Color oldColor = g.getColor();
	        AffineTransform oldTransform = g2.getTransform(); 
	 
	        g.setFont(font); 
	        g.setColor(Color.black); 
	        if(clockwize){ 
	            g2.translate(x+getIconWidth(), y); 
	            g2.rotate(Math.PI/2); 
	        }else{ 
	            g2.translate(x, y+getIconHeight()); 
	            g2.rotate(-Math.PI/2); 
	        } 
	        g.drawString(text, 0, fm.getLeading()+fm.getAscent()); 
	 
	        g.setFont(oldFont); 
	        g.setColor(oldColor); 
	        g2.setTransform(oldTransform); 
	    } 
	 
	    public int getIconWidth(){ 
	        return height; 
	    } 
	 
	    public int getIconHeight(){ 
	        return width; 
	    }
	}
}
