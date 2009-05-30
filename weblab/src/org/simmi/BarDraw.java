package org.simmi;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JComponent;

public class BarDraw extends JComponent {
	double[]	data;
	double		min, max;
	String[]	names;
	String		title;
	
	public BarDraw( String title, String[] names, double[] data ) {
		this.data = data;
		this.title = title;
		
		min = Double.POSITIVE_INFINITY;
		max = Double.NEGATIVE_INFINITY;
		
		for( double d : data ) {
			if( d < min ) min = d;
			if( d > max ) max = d;
		}
		
		this.names = names;
		
		this.setPreferredSize( new Dimension(800,600) );
	}
	
	public void paintComponent( Graphics g ) {
		super.paintComponent( g );
		
		Graphics2D g2 = (Graphics2D)g;
		g2.setRenderingHint( RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY );
		g2.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
		
		int w = this.getWidth();
		int h = this.getHeight();
		
		g.setColor( Color.white );
		g.fillRect( 0, 0, w, h );
		
		int fw = (2*this.getWidth())/4;
		int fh = (2*this.getHeight())/4;
		
		int hval = (h-fh)/2;
		g2.setColor( Color.darkGray );
		g2.setFont( new Font("Arial", Font.BOLD, 16) );
		int strw = g2.getFontMetrics().stringWidth( title );
		g2.drawString( title, (w-strw)/2, hval-30 );
		
		int hwidth = (1*this.getWidth())/(names.length*10);
		
		g2.setFont( new Font("Arial", Font.BOLD, 10) );
		hval = (h+fh)/2;
		int wval = (w-fw)/2;
		int k = 0;
		g2.setColor( Color.darkGray );
		for( String name : names ) {
			String[]	split = name.split("/");
			int u = 1;
			for( String s : split ) {
				strw = g2.getFontMetrics().stringWidth(s);
				g2.drawString( s, wval+((k)*fw)/(names.length-1)-strw/2, hval+20*(u++) );
			}
			k++;
		}
		
		k = 0;
		for( double d : data ) {
			int hheight = (int)((d*fh)/max);
			g2.setColor( Color.blue );
			g2.fillRect( wval+((k)*fw)/(data.length-1)-hwidth, hval-hheight, hwidth*2, hheight );
			g2.setColor( Color.black );
			g2.drawRect( wval+((k)*fw)/(data.length-1)-hwidth, hval-hheight, hwidth*2, hheight );
			
			k++;
		}
	}
}
