package org.simmi;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import javax.swing.JComponent;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.TableModel;

public class GraphPanel extends JTabbedPane {
	JComponent	energy;
	JComponent	energyPart;
	VitaminPanel	vitaminv;
	VitaminPanel	vitaminf;
	boolean		hringur = false;
	
	JTable		table, leftTable, topTable;
	TableModel	topModel;
	TableModel	model;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8830688610876166912L;

	public float stuffYou( String whr ) {
		int r = leftTable.getSelectedRow();
		if( r >= 0 && r < leftTable.getRowCount() ) return stuffYou( r, whr );
		return -1.0f;
	}
	
	public float stuffYou( int row, String whr ) {
		float f = -1.0f;
		
		int col = 0;
		String val = (String)topModel.getValueAt(0, col);
		while( !(val != null && val.contains(whr)) ) {
			col++;
			if( col < topModel.getColumnCount() ) {
				val = (String)topModel.getValueAt(0, col);
			} else break;
		}
		if( col < model.getColumnCount() ) {
			Float ff = (Float)model.getValueAt(row, col);
			if( ff != null ) f = ff;
		}
		return f;
	}
	
	public GraphPanel( final RdsPanel rdsPanel, final String lang, JTable[]	tables, TableModel model, TableModel topModel ) {
		super( JTabbedPane.RIGHT );
		
		table = tables[0];
		leftTable = tables[1];
		topTable = tables[2];
		this.topModel = topModel;
		this.model = model;
		
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		final PrintStream ps = new PrintStream( baos );
		
		energy = new JComponent() {
			public void paintComponent( Graphics g ) {
				super.paintComponent( g );
				
				Graphics2D g2 = (Graphics2D)g;
				g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
				g2.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
				
				int row = leftTable.getSelectedRow();
				if( row != -1 ) {
					Object oval = leftTable.getValueAt(row, 1);
					
					g2.setColor(Color.darkGray);
					g2.setFont( new Font("Arial", Font.BOLD, this.getHeight()/30 ) );
					if( oval != null ) {
						g2.drawString( oval.toString(), 10, this.getHeight()/20 );
					}
					
					String enStr = "";
					if( lang.equals("IS") ) {
						enStr = "Orka (í 100g)";
					} else {
						enStr = "Energy (in 100g)";
					}
					g2.drawString( enStr, 10, (int)(this.getHeight()/11.7) );
					g2.setFont( new Font("Arial", Font.BOLD, this.getHeight()/30 ) );
					
					if( row >= 0 ) {					
						float alc = 0.0f;
						float prt = 0.0f;
						float cbh = 0.0f;
						float fat = 0.0f;
						
						if( lang.equals("IS") ) {
							alc = stuffYou(row, "Alcohol");
							prt = stuffYou(row, "Protein, total");
							cbh = stuffYou(row, "Carbohydrates, total");
							fat = stuffYou(row, "Fat, total");
						} else {
							alc = stuffYou(row, "ALC");
							prt = stuffYou(row, "PROCNT");
							cbh = stuffYou(row, "CHOCDF");
							fat = stuffYou(row, "FAT");
						}
						
						if( lang.equals("IS") ) {
							float f = (17.0f*prt + 17.0f*cbh + 37.0f*fat + 29.0f*alc)*10.0f;
							if( f > 0 ) {
								float fv = Math.round( f )/100.0f;
								float fcal = f/4.184f;
								float fvcal = Math.round( fcal )/100.0f;
								
								ps.flush();
								baos.reset();
								ps.printf( "%.1f %s", fvcal, " kCal" );
								String calStr = baos.toString();
								int strw = g.getFontMetrics().stringWidth(calStr);
								g2.drawString(calStr, 10, this.getHeight()/8 );
								
								ps.flush();
								baos.reset();
								ps.printf(" (%.1f %s)", fv, "kJ" );
								String kjStr = baos.toString();
								g2.drawString(kjStr, 15+strw, this.getHeight()/8 );
							}
						} else {
							float f = stuffYou( row, "ENERC_KJ" );
							if( f > 0 ) {
								g2.drawString(f+" kJ", 10, this.getHeight()/10 );
							}
							
							f = stuffYou( row, "ENERC_KCAL" );
							if( f > 0 ) {
								g2.drawString(f+" kCal", 10, this.getHeight()/7 );
							}
						}
						
						float total = alc + prt + cbh + fat;
						
						if( total > 0 ) {
							if( GraphPanel.this.getSelectedComponent() != energy ) {
								int w = this.getWidth();
								int h = this.getHeight();
								
								int t = Math.min(w, h);
								t = (3*t)/4;
								
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
								nn = (int)(360.0f);
								g2.drawArc( (w-t)/2, (h-t)/2, t, t, n, nn-n );
								
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
								
								g2.setColor( Color.darkGray );
								g2.drawRoundRect( (19*w)/20, (1*this.getHeight())/25-hh/2, hh, hh, a, a );
								g2.drawRoundRect( (19*w)/20, (2*this.getHeight())/25-hh/2, hh, hh, a, a );
								g2.drawRoundRect( (19*w)/20, (3*this.getHeight())/25-hh/2, hh, hh, a, a );
								g2.drawRoundRect( (19*w)/20, (4*this.getHeight())/25-hh/2, hh, hh, a, a );
								
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
							} else {
								int w = this.getWidth();
								int h = this.getHeight();
								
								//int t = Math.min(w, h);
								int th = (3*h)/4;
								int tw = (3*w)/4;
								int tnrw = (1*w)/2;
								int bil = w/30;
								
								g.setColor( Color.darkGray );
								g.drawLine( (w-tw)/2, (h-th)/2, (w-tw)/2, (h+th)/2);
								g.drawLine( (w-tw)/2, (h+th)/2, (w+tw)/2, (h+th)/2);
								
								g2.setFont( new Font("Arial", Font.BOLD, this.getHeight()/40 ) );
								for( int i = 1; i < 10; i++ ) {
									int hh = (h+th)/2-(i*th)/10;
									String str = (i*10.0f)+"g";
									int strw = g.getFontMetrics().stringWidth(str);
									g.setColor( Color.darkGray );
									g.drawString( str, (w-tw)/2-strw-10, hh);
									g.setColor( Color.lightGray );
									g.drawLine( (w-tw)/2, hh, (w+tw)/2, hh );
								}
								
								Paint p = g2.getPaint();
								
								Color aGray1 = new Color( 220, 220, 220, 224 );
								Color aGray2 = new Color( 220, 220, 220, 64 );
								
								Color r1 = new Color( 200, 100, 100 );
								Color g1 = new Color( 100, 200, 100 );
								Color b1 = new Color( 100, 100, 200 );
								Color y1 = new Color( 200, 200, 100 );
								
								Color r2 = new Color( 250, 150, 150 );
								GradientPaint gp = new GradientPaint( (w-tnrw)/2-bil, 0,r1, (w-tnrw)/2+bil, 0, r2 );
								g2.setPaint( gp );
								//int n = (int)((alc*360.0f)/total);
								int val = (int)(th*alc/100.0);
								g2.fillRect( (w-tnrw)/2-bil, (h+th)/2-val, 2*bil, val );
								
								gp = new GradientPaint( (w-tnrw)/2-2*bil/3, 0, aGray1, (w-tnrw)/2, 0, aGray2 );
								g2.setPaint( gp );
								g2.fillRect( (w-tnrw)/2-2*bil/3, (h+th)/2-val, 2*bil/3, val );
								
								g2.setPaint(p);
								g2.setColor( Color.darkGray );
								g2.drawRect( (w-tnrw)/2-bil, (h+th)/2-val, 2*bil, val );
								
								Color gn = new Color( 150, 250, 150 );
								gp = new GradientPaint( (w-tnrw/3)/2-bil, 0, g1, (w-tnrw/3)/2+bil, 0, gn );
								g2.setPaint( gp );
								val = (int)(th*prt/100.0);
								g2.fillRect( (w-tnrw/3)/2-bil, (h+th)/2-val, 2*bil, val );
								
								gp = new GradientPaint( (w-tnrw/3)/2-2*bil/3, 0, aGray1, (w-tnrw/3)/2, 0, aGray2 );
								g2.setPaint( gp );
								g2.fillRect( (w-tnrw/3)/2-2*bil/3, (h+th)/2-val, 2*bil/3, val );
								
								g2.setPaint(p);
								g2.setColor( Color.darkGray );
								g2.drawRect( (w-tnrw/3)/2-bil, (h+th)/2-val, 2*bil, val );
								
								Color b2 = new Color( 150, 150, 250 );
								gp = new GradientPaint( (w+tnrw/3)/2-bil, 0, b1, (w+tnrw/3)/2+bil, 0, b2 );
								g2.setPaint( gp );
								val = (int)(th*cbh/100.0);
								g2.fillRect( (w+tnrw/3)/2-bil, (h+th)/2-val, 2*bil, val );
								
								gp = new GradientPaint( (w+tnrw/3)/2-2*bil/3, 0, aGray1, (w+tnrw/3)/2, 0, aGray2 );
								g2.setPaint( gp );
								g2.fillRect( (w+tnrw/3)/2-2*bil/3, (h+th)/2-val, 2*bil/3, val );
								
								g2.setPaint(p);
								g2.setColor( Color.darkGray );
								g2.drawRect( (w+tnrw/3)/2-bil, (h+th)/2-val, 2*bil, val );
								
								Color y2 = new Color( 250, 250, 150 );
								gp = new GradientPaint( (w+tnrw)/2-bil, 0, y1, (w+tnrw)/2+bil, 0, y2 );
								g2.setPaint( gp );
								val = (int)(th*fat/100.0);
								g2.fillRect( (w+tnrw)/2-bil, (h+th)/2-val, 2*bil, val );
								
								gp = new GradientPaint( (w+tnrw)/2-2*bil/3, 0, aGray1, (w+tnrw)/2, 0, aGray2 );
								g2.setPaint( gp );
								g2.fillRect( (w+tnrw)/2-2*bil/3, (h+th)/2-val, 2*bil/3, val );
								
								g2.setPaint(p);
								g2.setColor( Color.darkGray );
								g2.drawRect( (w+tnrw)/2-bil, (h+th)/2-val, 2*bil, val );
								
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
								int hh = this.getHeight()/30;
								g2.setColor( r1 );
								g2.fillRoundRect( (19*w)/20, (1*this.getHeight())/25-hh/2, hh, hh, a, a );
								g2.setColor( g1 );
								g2.fillRoundRect( (19*w)/20, (2*this.getHeight())/25-hh/2, hh, hh, a, a );
								g2.setColor( b1 );
								g2.fillRoundRect( (19*w)/20, (3*this.getHeight())/25-hh/2, hh, hh, a, a );
								g2.setColor( y1 );
								g2.fillRoundRect( (19*w)/20, (4*this.getHeight())/25-hh/2, hh, hh, a, a );
								
								g2.setColor( Color.darkGray );
								g2.drawRoundRect( (19*w)/20, (1*this.getHeight())/25-hh/2, hh, hh, a, a );
								g2.drawRoundRect( (19*w)/20, (2*this.getHeight())/25-hh/2, hh, hh, a, a );
								g2.drawRoundRect( (19*w)/20, (3*this.getHeight())/25-hh/2, hh, hh, a, a );
								g2.drawRoundRect( (19*w)/20, (4*this.getHeight())/25-hh/2, hh, hh, a, a );
								
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
							}
						}
					}
				}
			}
		};
		
		energy.addMouseListener( new MouseAdapter() {
			public void mousePressed( MouseEvent e ) {
				if( GraphPanel.this.getSelectedComponent() == energy ) GraphPanel.this.setSelectedComponent( energyPart );
				else GraphPanel.this.setSelectedComponent( energy );
				energy.repaint();
			}
		});
		
		SkifuGraph perc = new SkifuGraph( "Hlutföll", new String[] {"Alcohol", "Protein, total", "Carbohydrates, total", "Fat, total", "Ash", "Water"},
			new String[] {"Alkóhól", "Prótín", "Kolvetni", "Fita", "Aska", "Vatn"}, tables );
		
		vitaminv = new VitaminPanel( true, leftTable, this, rdsPanel, lang );
		vitaminf = new VitaminPanel( false, leftTable, this, rdsPanel, lang );
		
		this.addTab("Hlutföll", perc);
		this.addTab("Orka", energy);
		this.addTab("Orkuhlutföll", energyPart);
		this.addTab("Vatnsl-Vítm", vitaminv);
		this.addTab("Fitul-Vítm", vitaminf);
	}
}
