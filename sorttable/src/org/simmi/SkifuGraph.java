package org.simmi;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;

import javax.swing.JComponent;
import javax.swing.JTable;

public class SkifuGraph extends JComponent {
	JTable		table, leftTable, topTable;
	String		title;
	String[]	columnNames;
	Color[]		darker = {	new Color( 200, 100, 100 ), new Color( 100, 200, 100 ), new Color( 100, 100, 200 ), 
							new Color( 200, 200, 100 ), new Color( 200, 100, 200 ), new Color( 100, 200, 200 ), new Color( 100, 100, 100 )};
	Color[]		brighter = {new Color( 250, 150, 150 ), new Color( 150, 250, 150 ), new Color( 150, 150, 250 ),
							new Color( 250, 250, 150 ), new Color( 250, 150, 250 ), new Color( 150, 250, 250 ), new Color( 150, 150, 150 )};
	
	public SkifuGraph( String title, String[] columnNames, JTable[] tables ) {
		super();
		this.title = title;
		this.columnNames = columnNames;
		
		table = tables[0];
		leftTable = tables[1];
		topTable = tables[2];
	}
	
	public float stuffYou( int row, String whr ) {
		float f = 0.0f;
		
		int col = 0;
		Object val = topTable.getValueAt(0, col);
		while( !(val != null && val.equals(whr)) && col < topTable.getColumnCount()-1 ) {
			col++;
			val = topTable.getValueAt(0, col);
		}
		if( col < table.getColumnCount()-1 ) {
			Float ff = (Float)table.getValueAt(row, col);
			if( ff != null ) {
				f = ff;
			}
		} else {
			System.err.println( whr+" not found" );
		}
		
		return f;
	}
	
	public void paintComponent( Graphics g ) {
		super.paintComponent( g );
		
		g.setColor( Color.white );
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		
		Graphics2D g2 = (Graphics2D)g;
		g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
		g2.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
		
		g2.setColor(Color.darkGray);
		g2.setFont( new Font("Arial", Font.BOLD, this.getHeight()/20 ) );
		g2.drawString(title, 10, this.getHeight()/20 );
		g2.setFont( new Font("Arial", Font.BOLD, this.getHeight()/30 ) );
		
		int row = leftTable.getSelectedRow();
		if( row >= 0 ) {			
			float[] val = new float[ columnNames.length ];
			for( int i = 0; i < val.length; i++ ) {
				val[i] = stuffYou(row, columnNames[i]);
			}
			
			/*if( lang.equals("IS") ) {
				float f = (17.0f*prt + 17.0f*cbh + 37.0f*fat + 29.0f*alc)*10.0f;
				float fv = Math.round( f )/100.0f;
				if( f > 0 ) {
					g2.drawString(fv+" kJ", 10, this.getHeight()/10 );
				}
				
				f = f/4.184f;
				fv = Math.round( f )/100.0f;
				if( f > 0 ) {
					g2.drawString(fv+" kCal", 10, this.getHeight()/7 );
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
			}*/
			
			float total = 0.0f;
			for( float f : val ) {
				total += f;
			}
			
			if( total > 0 ) {
				int w = this.getWidth();
				int h = this.getHeight();
				
				int t = Math.min(w, h);
				t = (3*t)/4;
				
				Paint p = g2.getPaint();
				
				float tot = 0.0f;
				float last = 0.0f;
				int i = 0;
				for( float f : val ) {
					//System.err.print(f+"\t");
					tot += f;
					Color r1 = darker[i];
					Color r2 = brighter[i];
					GradientPaint gp = new GradientPaint( (w-t)/2, (h-t)/2, r1, (w+t)/2, (h+t)/2, r2 );
					g2.setPaint( gp );
					int n = (int)((last*360.0f)/total);
					int nn = (int)(((tot)*360.0f)/total);
					g2.fillArc( (w-t)/2, (h-t)/2, t, t, n, nn-n );
					last = tot;
					i++;
				}
				Color r1 = darker[i];
				Color r2 = brighter[i];
				GradientPaint gp = new GradientPaint( (w-t)/2, (h-t)/2, r1, (w+t)/2, (h+t)/2, r2 );
				g2.setPaint( gp );
				int n = (int)((last*360.0f)/total);
				int nn = (int)(((total)*360.0f)/total);
				g2.fillArc( (w-t)/2, (h-t)/2, t, t, n, nn-n );
				//System.err.println();
				
				g2.setPaint(p);
				
				g2.setColor( Color.darkGray );
				tot = 0.0f;
				last = 0.0f;
				i = 0;
				for( float f : val ) {
					tot += f;
					n = (int)((last*360.0f)/total);
					nn = (int)(((tot)*360.0f)/total);
					g2.drawArc( (w-t)/2, (h-t)/2, t, t, n, nn-n );
					last = tot;
					i++;
				}
				n = (int)Math.floor((last*360.0f)/total);
				nn = (int)Math.ceil(((total)*360.0f)/total);
				g2.drawArc( (w-t)/2, (h-t)/2, t, t, n, nn-n );
				last = tot;
				
				int a = 10;
				int hh = this.getHeight()/30;
				g2.setFont( new Font("Arial", Font.BOLD, this.getHeight()/40 ) );
				for( i = 0; i < 4; i++ ) {
					g2.setColor( darker[i] );
					g2.fillRoundRect( (19*w)/20, ((i+1)*this.getHeight())/25-hh/2, hh, hh, a, a );
					g2.setColor( Color.darkGray );
					g2.drawRoundRect( (19*w)/20, ((i+1)*this.getHeight())/25-hh/2, hh, hh, a, a );
					String str = columnNames[i];
					int strw = g2.getFontMetrics().stringWidth(str);
					g2.drawString(str, (19*w)/20-strw-hh/2, ((i+1)*this.getHeight())/25+hh/4 );
				}
				for( i = 4; i < 6; i++ ) {
					g2.setColor( darker[i] );
					g2.fillRoundRect( (19*w)/20, this.getHeight()-((i-2)*this.getHeight())/25-hh/2, hh, hh, a, a );
					g2.setColor( Color.darkGray );
					g2.drawRoundRect( (19*w)/20, this.getHeight()-((i-2)*this.getHeight())/25-hh/2, hh, hh, a, a );
					String str = columnNames[i];
					int strw = g2.getFontMetrics().stringWidth(str);
					g2.drawString(str, (19*w)/20-strw-hh/2, this.getHeight()-((i-2)*this.getHeight())/25+hh/4 );
				}
				g2.setColor( darker[i] );
				g2.fillRoundRect( (19*w)/20, this.getHeight()-((i-5)*this.getHeight())/25-hh/2, hh, hh, a, a );
				g2.setColor( Color.darkGray );
				g2.drawRoundRect( (19*w)/20, this.getHeight()-((i-5)*this.getHeight())/25-hh/2, hh, hh, a, a );
				String str = "Annað";
				int strw = g2.getFontMetrics().stringWidth(str);
				g2.drawString(str, (19*w)/20-strw-hh/2, this.getHeight()-((i-5)*this.getHeight())/25+hh/4 );
			}
		}
	}
}
