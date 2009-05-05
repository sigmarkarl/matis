package org.simmi;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;

import javax.swing.JComponent;

public class VitaminPanel extends JComponent {
	RdsPanel	rdsPanel;
	String		lang;
	GraphPanel	parent;
	
	public VitaminPanel( final GraphPanel parent, final RdsPanel rdsPanel, final String lang ) {
		super();
		this.parent = parent;
		this.lang = lang;
		this.rdsPanel = rdsPanel;
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
		if( lang.equals("IS") ) {
			g2.drawString("Vítamín (hlutfall af RDS)", 10, this.getHeight()/20 );
		} else {
			g2.drawString("Vitamin (RDS proportion)", 10, this.getHeight()/20 );
		}
		g2.setFont( new Font("Arial", Font.BOLD, this.getHeight()/30 ) );
		
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
		int k = 0;
		for( double i = 0.01; i <= 100.0; i*=10.0 ) {
			int hh = (h+th)/2-((k++)*th)/4;
			String str = (i)+"";
			int strw = g.getFontMetrics().stringWidth(str);
			g.setColor( Color.darkGray );
			g.drawString( str, (w-tw)/2-strw-10, hh);
			g.setColor( Color.lightGray );
			g.drawLine( (w-tw)/2, hh, (w+tw)/2, hh );
		}
		
		Paint p = g2.getPaint();
		
		Color aGray1 = new Color( 220, 220, 220, 224 );
		Color aGray2 = new Color( 220, 220, 220, 64 );
		
		Color cv = new Color( 200, 200, 100 );
		Color b1 = new Color( 150, 150, 100 );
		Color b2 = new Color( 100, 100, 100 );
		Color b6 = new Color( 50, 50, 100 );
		
		float cc = parent.stuffYou( "Vitamin C" );
		float bt1 = parent.stuffYou( "Vitamin B1" );
		float bt2 = parent.stuffYou( "Vitamin B2" );
		float bt6 = parent.stuffYou( "Vitamin B6" );
		String ccval = rdsPanel.getRds("C/mg");
		String bb1val = rdsPanel.getRds("B1/mg");
		String bb2val = rdsPanel.getRds("B2/mg");
		String bb6val = rdsPanel.getRds("B6/mg");
		
		if( cc >= 0.0f && ccval != null ) {
			float cval = Float.parseFloat( ccval );
			float b1val = Float.parseFloat( bb1val );
			float b2val = Float.parseFloat( bb2val );
			float b6val = Float.parseFloat( bb6val );
			
			double div = cc / cval;
			div = Math.max( 0.01, div );
			div = Math.min( 100.0, div );
			double ccv = Math.log10( div );
			
			div = bt1 / b1val;
			div = Math.max( 0.01, div );
			div = Math.min( 100.0, div );
			double b1v = Math.log10( div );
			
			div = bt2 / b2val;
			div = Math.max( 0.01, div );
			div = Math.min( 100.0, div );
			double b2v = Math.log10( div );
			
			div = bt6 / b6val;
			div = Math.max( 0.01, div );
			div = Math.min( 100.0, div );
			double b6v = Math.log10( div );
			
			Color cvn = new Color( 250, 250, 150 );
			GradientPaint gp = new GradientPaint( (w-tnrw)/2-bil, 0, cv, (w-tnrw)/2+bil, 0, cvn );
			g2.setPaint( gp );
			//int n = (int)((alc*360.0f)/total);
			int val = (int)(th*ccv/4.0);
			int start = (h)/2;
			
			int hs = Math.min( start, start-val );
			int hl = Math.max( start, start-val ) - hs;
			g2.fillRect( (w-tnrw)/2-bil, hs, 2*bil, hl );
			
			gp = new GradientPaint( (w-tnrw)/2-2*bil/3, 0, aGray1, (w-tnrw)/2, 0, aGray2 );
			g2.setPaint( gp );
			g2.fillRect( (w-tnrw)/2-2*bil/3, hs, 2*bil/3, hl );
			
			g2.setPaint(p);
			g2.setColor( Color.darkGray );
			g2.drawRect( (w-tnrw)/2-bil, hs, 2*bil, hl );
			
			Color b1n = new Color( 200, 200, 150 );
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
			
			int aa = 10;
			int hh = this.getHeight()/30;
			g2.setColor( cv );
			g2.fillRoundRect( (19*w)/20, (1*this.getHeight())/25-hh/2, hh, hh, aa, aa );
			g2.setColor( b1 );
			g2.fillRoundRect( (19*w)/20, (2*this.getHeight())/25-hh/2, hh, hh, aa, aa );
			g2.setColor( b2 );
			g2.fillRoundRect( (19*w)/20, (3*this.getHeight())/25-hh/2, hh, hh, aa, aa );
			g2.setColor( b6 );
			g2.fillRoundRect( (19*w)/20, (4*this.getHeight())/25-hh/2, hh, hh, aa, aa );
			
			g2.setColor( Color.darkGray );
			g2.drawRoundRect( (19*w)/20, (1*this.getHeight())/25-hh/2, hh, hh, aa, aa );
			g2.drawRoundRect( (19*w)/20, (2*this.getHeight())/25-hh/2, hh, hh, aa, aa );
			g2.drawRoundRect( (19*w)/20, (3*this.getHeight())/25-hh/2, hh, hh, aa, aa );
			g2.drawRoundRect( (19*w)/20, (4*this.getHeight())/25-hh/2, hh, hh, aa, aa );
			
			g2.setFont( new Font("Arial", Font.BOLD, this.getHeight()/40 ) );
			g2.setColor( Color.darkGray );
			String str;
			if( lang.equals("IS") ) str = "C";
			else str = "C";
			int strw = g2.getFontMetrics().stringWidth(str);
			g2.drawString(str, (19*w)/20-strw-hh/2, (1*this.getHeight())/25+hh/4 );
			if( lang.equals("IS") ) str = "B1 - þíamín";
			else str = "B2";
			strw = g2.getFontMetrics().stringWidth(str);
			g2.drawString(str, (19*w)/20-strw-hh/2, (2*this.getHeight())/25+hh/4 );
			if( lang.equals("IS") ) str = "B2 - ríbólfavín";
			else str = "B5";
			strw = g2.getFontMetrics().stringWidth(str);
			g2.drawString(str, (19*w)/20-strw-hh/2, (3*this.getHeight())/25+hh/4 );
			if( lang.equals("IS") ) str = "B6";
			else str = "B6";
			strw = g2.getFontMetrics().stringWidth(str);
			g2.drawString(str, (19*w)/20-strw-hh/2, (4*this.getHeight())/25+hh/4 );
		}
	}
}
