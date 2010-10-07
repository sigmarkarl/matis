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

import javax.swing.JComponent;
import javax.swing.JTable;

public class VitaminPanel extends JComponent {
	RdsPanel	rdsPanel;
	String		lang;
	GraphPanel	parent;
	
	Color[] c1s;
	Color[] c2s;
	
	Color aGray1 = new Color( 220, 220, 220, 224 );
	Color aGray2 = new Color( 220, 220, 220, 64 );
	
	JTable 		leftTable;
	boolean		log = false;
	boolean		waterSol = true;
	
	public VitaminPanel( boolean waterSol, final JTable leftTable, final GraphPanel parent, final RdsPanel rdsPanel, final String lang ) {
		super();		
		this.parent = parent;
		this.lang = lang;
		this.rdsPanel = rdsPanel;
		this.leftTable = leftTable;
		
		this.waterSol = waterSol;
		
		Color cv = new Color( 250, 250, 100 );
		Color b1 = new Color( 200, 200, 100 );
		Color b2 = new Color( 150, 150, 100 );
		Color b6 = new Color( 100, 100, 100 );
		Color b12 = new Color( 50, 50, 100 );
		
		Color cvn = new Color( 250, 250, 150 );
		Color b1n = new Color( 200, 200, 150 );
		Color b2n = new Color( 150, 150, 150 );
		Color b6n = new Color( 100, 100, 150 );
		Color b12n = new Color( 50, 50, 150 );
		
		Color[] c1 = { cv, b1 ,b2, b6, b12 };
		Color[] c2 = { cvn, b1n ,b2n, b6n, b12n };
		
		c1s = c1;
		c2s = c2;
		
		this.addMouseListener( new MouseAdapter() {
			public void mousePressed( MouseEvent e ) {
				log = !log;
				VitaminPanel.this.repaint();
			}
		});
	}
	
	public void paintComponent( Graphics g ) {
		super.paintComponent( g );
		
		g.setColor( Color.white );
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		
		Graphics2D g2 = (Graphics2D)g;
		g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
		g2.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
		
		g2.setColor(Color.darkGray);		
		int row = leftTable.getSelectedRow();
		if( row >= 0 ) {
			Object oval = leftTable.getValueAt(row, 0);
			
			g2.setColor(Color.darkGray);
			g2.setFont( new Font("Arial", Font.BOLD, this.getHeight()/40 ) );
			if( oval != null ) {
				g2.drawString(oval.toString(), 10, this.getHeight()/12 );
			}
			
			if( lang.equals("IS") ) {
				g2.drawString("Vítamín (hlutfall af RDS í 100g)", 10, this.getHeight()/25 );
			} else {
				g2.drawString("Vitamin (suggested daily intake proportion in 100g)", 10, this.getHeight()/25 );
				//g2.drawString("Vitamin (suggested daily intake proportion in 100g)", 10, this.getHeight()/12 );
			}
			g2.setFont( new Font("Arial", Font.BOLD, this.getHeight()/30 ) );
			
			int w = this.getWidth();
			int h = this.getHeight();
			
			//int t = Math.min(w, h);
			int th = (3*h)/5;
			int tw = (3*w)/4;
			int tnrw = (1*w)/2;
			int bil = w/40;
			
			g.translate(0, 30);
			
			g.setColor( Color.darkGray );
			g.drawLine( (w-tw)/2, (h-th)/2, (w-tw)/2, (h+th)/2);
			g.drawLine( (w-tw)/2, (h+th)/2, (w+tw)/2, (h+th)/2);
			
			g2.setFont( new Font("Arial", Font.BOLD, this.getHeight()/40 ) );
			int k = 0;
			if( log ) {
				for( double i = 0.01; i <= 100.0; i*=10.0 ) {
					int hh = (h+th)/2-((k++)*th)/4;
					String str = (i)+"";
					int strw = g.getFontMetrics().stringWidth(str);
					g.setColor( Color.darkGray );
					g.drawString( str, (w-tw)/2-strw-10, hh);
					g.setColor( Color.lightGray );
					g.drawLine( (w-tw)/2, hh, (w+tw)/2, hh );
				}
			}
			Paint p = g2.getPaint();
			
			double[] dvals = null;
			if( waterSol ) {
				float cc = (float)Math.max( parent.stuffYou( lang.equals("IS") ? "C-vítamín" : "Vitamin C" ), 0.0 );
				float bt1 = (float)Math.max( parent.stuffYou( lang.equals("IS") ? "B1-vítamín, þíamín" : "Vitamin B-1" ), 0.0 );
				float bt2 = (float)Math.max( parent.stuffYou( lang.equals("IS") ? "B2-vítamín, ríbóflavín" : "Vitamin B-2" ), 0.0 );
				float bt6 = (float)Math.max( parent.stuffYou( lang.equals("IS") ? "B6-vítamín" : "Vitamin B-6" ), 0.0 );
				float bt12 = (float)Math.max( parent.stuffYou( lang.equals("IS") ? "B12-vítamín" : "Vitamin B-12" ), 0.0 );
				String ccval = rdsPanel.getRds("C - mg");
				String bb1val = rdsPanel.getRds("B1 - mg");
				String bb2val = rdsPanel.getRds("B2 - mg");
				String bb6val = rdsPanel.getRds("B6 - mg");
				String bb12val = rdsPanel.getRds("B12 - ug");
			
				if( cc >= 0.0f && ccval != null ) {
					float cval = Float.parseFloat( ccval );
					float b1val = Float.parseFloat( bb1val );
					float b2val = Float.parseFloat( bb2val );
					float b6val = Float.parseFloat( bb6val );
					float b12val = Float.parseFloat( bb12val );
					
					double ccv = cc / cval;
					double b1v = bt1 / b1val;
					double b2v = bt2 / b2val;
					double b6v = bt6 / b6val;
					double b12v = bt12 / b12val;
					
					double[] dvalstmp =  { ccv, b1v, b2v, b6v, b12v };
					dvals = dvalstmp;
				}
			} else {
				float va = (float)Math.max( parent.stuffYou( lang.equals("IS") ? "A-vítamín, RJ" : "Vitamin A" ), 0.0 );
				float vd = (float)Math.max( parent.stuffYou( lang.equals("IS") ? "D-vítamín" : "Vitamin D" ), 0.0 );
				float ve = (float)Math.max( parent.stuffYou( lang.equals("IS") ? "E-vítamín, a-TJ" : "Vitamin E" ), 0.0 );
				//float vk = (float)Math.max( parent.stuffYou( "K-vítamín" ), 0.0 );
				String vas = rdsPanel.getRds("A - RJ");
				String vds = rdsPanel.getRds("D - ug");
				String ves = rdsPanel.getRds("E - α-TJ");
				//String vks = rdsPanel.getRds("K - mg");
			
				if( va >= 0.0f && vas != null ) {
					float vaval = Float.parseFloat( vas );
					float vdval = 1.0f;
					if( vds != null ) vdval = Float.parseFloat( vds );
					float veval = 1.0f;
					if( ves != null ) veval = Float.parseFloat( ves );
					//float vkval = 1.0f;
					//if( vks != null ) vkval = Float.parseFloat( vks );
					
					double div = va / vaval;
					double vav = div;//Math.log10( div );
					
					div = vd / vdval;
					double vdv = div;//Math.log10( div );
					
					div = ve / veval;
					double vev = div;//Math.log10( div );
					
					//div = vk / vkval;
					//double vkv = div;//Math.log10( div );
					
					double[] dvalstmp =  { vav, vdv, vev };
					dvals = dvalstmp;
				}
			}
				
			//double[] dvals = { ccv, b1v, b2v, b6v, b12v };
			if( dvals != null ) {				
				if( log ) {
					int start = (h)/2;
					for( int i = 0; i < dvals.length; i++ ) {
						GradientPaint gp = new GradientPaint( (w-tnrw)/2-bil, 0, c1s[i], (w-tnrw)/2+bil, 0, c2s[i] );
						g2.setPaint( gp );
						double lval = dvals[i];
						lval = Math.max( 0.01, lval );
						lval = Math.min( 100.0, lval );
						lval = Math.log10( lval );
						int val = (int)(th*lval/4.0);
						
						int hs = Math.min( start, start-val );
						int hl = Math.max( start, start-val ) - hs;
						
						int wstart = (w-tnrw)/2+(i*tnrw)/(dvals.length-1);
						
						g2.fillRect( wstart-bil, hs, 2*bil, hl );
						
						gp = new GradientPaint( (w-tnrw)/2-2*bil/3, 0, aGray1, (w-tnrw)/2, 0, aGray2 );
						g2.setPaint( gp );
						g2.fillRect( wstart-2*bil/3, hs, 2*bil/3, hl );
						
						g2.setPaint(p);
						g2.setColor( Color.darkGray );
						g2.drawRect( wstart-bil, hs, 2*bil, hl );
					}
				} else {
					int iv = 100;
					for( double dd : dvals ) {
						int v = (int)(dd*100.0);
						if( v > iv ) iv = v;
					}
					
					double istuff = (iv/10.0);
					int jval = (int)(istuff/10);
					//int jval = (int)Math.log10( istuff );
					//System.err.println( istuff + "  " + jval );
					
					for( double i = 0.0; i <= iv; i+=jval*10 ) {
						int hh = (int)((h+th)/2-((k)*th)/ (int)(istuff) );
						k += jval;
						String str = ((int)i)+"";
						if( i % 100 == 0 ) str += "%";
						int strw = g.getFontMetrics().stringWidth(str);
						g.setColor( Color.darkGray );
						g.drawString( str, (w-tw)/2-strw-10, hh);
						g.setColor( Color.lightGray );
						g.drawLine( (w-tw)/2, hh, (w+tw)/2, hh );
					}
					 
					int start = (h+th)/2;
					for( int i = 0; i < dvals.length; i++ ) {
						GradientPaint gp = new GradientPaint( (w-tnrw)/2-bil, 0, c1s[i], (w-tnrw)/2+bil, 0, c2s[i] );
						g2.setPaint( gp );
						double dval = dvals[i];
						//dval = Math.max(0.0, dval);
						//dval = Math.min(1.0, dval);
						int val = (int)((100.0*dval*th)/iv);
						val = Math.max( val, 0 );
						
						int hs = Math.min( start, start-val );
						int hl = Math.max( start, start-val ) - hs;
						
						int wstart = (w-tnrw)/2+(i*tnrw)/(dvals.length-1);
						
						g2.fillRect( wstart-bil, hs, 2*bil, hl );
						
						gp = new GradientPaint( (w-tnrw)/2-2*bil/3, 0, aGray1, (w-tnrw)/2, 0, aGray2 );
						g2.setPaint( gp );
						g2.fillRect( wstart-2*bil/3, hs, 2*bil/3, hl );
						
						g2.setPaint(p);
						g2.setColor( Color.darkGray );
						g2.drawRect( wstart-bil, hs, 2*bil, hl );
					}
				}
				
				/*Color b1n = new Color( 200, 200, 150 );
				gp = new GradientPaint( (w-tnrw/3)/2-bil, 0, b1, (w-tnrw/3)/2+bil, 0, b1n );
				g2.setPaint( gp );
				
				val = (int)(th*b1v/4.0);
				hs = Math.min( start, start-val );
				hl = Math.max( start, start-val ) - hs;
				g2.fillRect( (w-tnrw/3)/2-bil, hs, 2*bil, hl );
				
				gp = new GradientPaint( (w-tnrw/3)/2-2*bil/3, 0, aGray1, (w-tnrw/3)/2, 0, aGray2 );
				g2.setPaint( gp );
				g2.fillRect( (w-tnrw/3)/2-2*bil/3, hs, 2*bil/3, hl );
				
				g2.setPaint(p);
				g2.setColor( Color.darkGray );
				g2.drawRect( (w-tnrw/3)/2-bil, hs, 2*bil, hl );
				
				Color b2n = new Color( 150, 150, 150 );
				gp = new GradientPaint( (w+tnrw/3)/2-bil, 0, b2, (w+tnrw/3)/2+bil, 0, b2n );
				g2.setPaint( gp );
				
				val = (int)(th*b2v/4.0);
				hs = Math.min( start, start-val );
				hl = Math.max( start, start-val ) - hs;
				g2.fillRect( (w+tnrw/3)/2-bil, hs, 2*bil, hl );
				
				gp = new GradientPaint( (w+tnrw/3)/2-2*bil/3, 0, aGray1, (w+tnrw/3)/2, 0, aGray2 );
				g2.setPaint( gp );
				g2.fillRect( (w+tnrw/3)/2-2*bil/3, hs, 2*bil/3, hl );
				
				g2.setPaint(p);
				g2.setColor( Color.darkGray );
				g2.drawRect( (w+tnrw/3)/2-bil, hs, 2*bil, hl );
				
				Color b6n = new Color( 100, 100, 150 );
				gp = new GradientPaint( (w+tnrw)/2-bil, 0, b6, (w+tnrw)/2+bil, 0, b6n );
				g2.setPaint( gp );
				
				val = (int)(th*b6v/4.0);
				hs = Math.min( start, start-val );
				hl = Math.max( start, start-val ) - hs;
				g2.fillRect( (w+tnrw)/2-bil, hs, 2*bil, hl );
				
				gp = new GradientPaint( (w+tnrw)/2-2*bil/3, 0, aGray1, (w+tnrw)/2, 0, aGray2 );
				g2.setPaint( gp );
				g2.fillRect( (w+tnrw)/2-2*bil/3, hs, 2*bil/3, hl );
				
				g2.setPaint(p);
				g2.setColor( Color.darkGray );
				g2.drawRect( (w+tnrw)/2-bil, hs, 2*bil, hl );
				
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
			} else if( !log ) {
				for( int i = 0; i <= 100; i+=10 ) {
					int hh = (h+th)/2-((k++)*th)/10	;
					String str = (i)+"";
					if( i % 100 == 0 ) str += "%";
					int strw = g.getFontMetrics().stringWidth(str);
					g.setColor( Color.darkGray );
					g.drawString( str, (w-tw)/2-strw-10, hh);
					g.setColor( Color.lightGray );
					g.drawLine( (w-tw)/2, hh, (w+tw)/2, hh );
				}
			}
			
			g.translate(0, -30);
			
			String str;
			g2.setFont( new Font("Arial", Font.BOLD, this.getHeight()/40 ) );
			int aa = 10;
			int hh = this.getHeight()/30;
			for( int i = 0; i < (waterSol ? 5 : 3); i++ ) {
				g2.setColor( c1s[i] );
				g2.fillRoundRect( (19*w)/20, ((i+1)*this.getHeight())/25-hh/2, hh, hh, aa, aa );
				g2.setColor( Color.darkGray );
				g2.drawRoundRect( (19*w)/20, ((i+1)*this.getHeight())/25-hh/2, hh, hh, aa, aa );
			}
			
			if( waterSol ) {
				if( lang.equals("IS") ) str = "C - vítamín";
				else str = "C";
				int strw = g2.getFontMetrics().stringWidth(str);
				g2.drawString(str, (19*w)/20-strw-hh/2, (1*this.getHeight())/25+hh/4 );
				if( lang.equals("IS") ) str = "B1 - þíamín";
				else str = "B-1";
				strw = g2.getFontMetrics().stringWidth(str);
				g2.drawString(str, (19*w)/20-strw-hh/2, (2*this.getHeight())/25+hh/4 );
				if( lang.equals("IS") ) str = "B2 - ríbólfavín";
				else str = "B-2";
				strw = g2.getFontMetrics().stringWidth(str);
				g2.drawString(str, (19*w)/20-strw-hh/2, (3*this.getHeight())/25+hh/4 );
				if( lang.equals("IS") ) str = "B6 - pýridoxín";
				else str = "B-6";
				strw = g2.getFontMetrics().stringWidth(str);
				g2.drawString(str, (19*w)/20-strw-hh/2, (4*this.getHeight())/25+hh/4 );
				if( lang.equals("IS") ) str = "B12 - kóbalamín";
				else str = "B-12";
				strw = g2.getFontMetrics().stringWidth(str);
				g2.drawString(str, (19*w)/20-strw-hh/2, (5*this.getHeight())/25+hh/4 );
			} else {
				if( lang.equals("IS") ) str = "A - retínól";
				else str = "A";
				int strw = g2.getFontMetrics().stringWidth(str);
				g2.drawString(str, (19*w)/20-strw-hh/2, (1*this.getHeight())/25+hh/4 );
				if( lang.equals("IS") ) str = "D - kalsíferól";
				else str = "D";
				strw = g2.getFontMetrics().stringWidth(str);
				g2.drawString(str, (19*w)/20-strw-hh/2, (2*this.getHeight())/25+hh/4 );
				if( lang.equals("IS") ) str = "E - tokóferól";
				else str = "E";
				strw = g2.getFontMetrics().stringWidth(str);
				g2.drawString(str, (19*w)/20-strw-hh/2, (3*this.getHeight())/25+hh/4 );
				/*if( lang.equals("IS") ) str = "K - menakvínon";
				else str = "K";
				strw = g2.getFontMetrics().stringWidth(str);
				g2.drawString(str, (19*w)/20-strw-hh/2, (4*this.getHeight())/25+hh/4 );*/
			}
		}
	}
}
