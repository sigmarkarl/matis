package org.simmi;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.JApplet;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class RayTrace extends JApplet {
	BufferedImage 	img;
	JComponent		c;
	
	float[] ls = new float[8];
	float[]	xs = new float[14];
	
	float camx = 512.0f;
	float camy = 0.0f;
	float camz = 0.0f;
	
	float 	t = 0.0f;
	float	o = 0.0f;
	
	/*public void setBounds( int x, int y, int w, int h ) {
		if( c != null ) c.setBounds(x, y, w, h);
		super.setBounds( x,y,w,h );
	}*/
	
	public void init() {
		Window window = SwingUtilities.windowForComponent(this);
		if (window instanceof JFrame) {
			JFrame frame = (JFrame) window;
			if (!frame.isResizable())
				frame.setResizable(true);
		}	

		ls[0] = (float)(Math.cos(t)*100.0);
		ls[1] = 0.0f;
		ls[2] = (float)(Math.sin(t)*100.0);
		ls[3] = 0.5f;
		ls[4] = 100.0f;
		ls[5] = 100.0f;
		ls[6] = -100.0f;
		ls[7] = 0.1f;
		
		xs[0] = 0.0f;
		xs[1] = 0.0f;
		xs[2] = 255.0f;
		xs[3] = 64.0f;
		xs[4] = 255.0f;
		xs[5] = 0.0f;
		xs[6] = 0.0f;

		xs[7] = 50.0f;
		xs[8] = 50.0f;
		xs[9] = -100.0f;
		xs[10] = 48.0f;
		xs[11] = 0.0f;
		xs[12] = 255.0f;
		xs[13] = 0.0f;

		img = new BufferedImage( 256, 256, BufferedImage.TYPE_INT_RGB );
		final Graphics2D g2 = img.createGraphics();
		
		c = new JComponent() {
			private static final long serialVersionUID = 1L;
			
			public void paintComponent( Graphics g ) {
				super.paintComponent( g );
				//g2.setColor( Color.red );
				//g2.fillRect(0, 0, img.getWidth(), img.getHeight());
				imgDraw( g );
				g.drawImage( img, 0, 0, this.getWidth(), this.getHeight(), this );
			}
		};
		this.setLayout( new BorderLayout() );
		this.add( c );
		
		new Timer(100, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				camx = (float)(512.0*Math.cos( o ));
				camz = (float)(512.0*Math.sin( o ));
				o+=0.1;
				c.repaint();
			}
		}).start();
	}
	
	public void imgDraw( Graphics g ) {
		for( int x = 0; x < img.getWidth(); x++ ) {
			for( int y = 0; y < img.getHeight(); y++ ) {
				//float* fp = NULL;
				int	fp = -1;
				float xv = 0.0f;
				float yv = 0.0f;
				float zv = 0.0f;
				float nl = 10000000.0f;
				for( int i = 0; i < xs.length; i+=7 ) {
					//float* ff = &xs[i];

					float x3 = xs[0+i];
					float y3 = xs[1+i];
					float z3 = xs[2+i];
					float r = xs[3+i];
					//float cb = ff[4];
					//float cg = ff[5];
					//float cr = ff[6];

					float xf = camx-x3;
					float yf = camy-y3;
					float zf = camz-z3;

					float c = x3*x3 + y3*y3 + z3*z3 + camx*camx + camy*camy + camz*camz - 2*(x3*camx + y3*camy + z3*camz) - r*r;

					float x2 = x-128.0f;
					float y2 = y-128.0f;
					float z2 = 0.0f;

					float xd = x2-camx;
					float yd = y2-camy;
					float zd = z2-camz;
					float a = xd*xd + yd*yd + zd*zd;
					float b = 2*( xd*xf + yd*yf + zd*zf );

					if( b*b > 4*a*c ) {
						float s = (float)Math.sqrt( b*b - 4*a*c );
						float u = (-b - s)/(2*a);

						float nxv = camx + u*(x2-camx);
						float nyv = camy + u*(y2-camy);
						float nzv = camz + u*(z2-camz);

						float ax = (nxv-x3);
						float ay = (nyv-y3);
						float az = (nzv-z3);

						float al = ax*ax + ay*ay + az*az;
						if( al < nl ) {
							xv = nxv;
							yv = nyv;
							zv = nzv;
							fp = i;
							nl = al;
						}
					}
				}

				int color = 0;
				if( fp != -1 ) {
					float x3 = xs[0+fp];
					float y3 = xs[1+fp];
					float z3 = xs[2+fp];
					float cb = xs[3+fp];
					float cg = xs[4+fp];
					float cr = xs[5+fp];
					for( int v = 0; v < 8; v+=4 ) {
						//float *vv = &ls[v];
						float xl = ls[0+v];
						float yl = ls[1+v];
						float zl = ls[2+v];
						float cl = ls[3+v];

						float cx = (xv-xl);
						float cy = (yv-yl);
						float cz = (zv-zl);

						float ax = (xv-x3);
						float ay = (yv-y3);
						float az = (zv-z3);

						float bx = (xl-x3);
						float by = (yl-y3);
						float bz = (zl-z3);

						float av = (ax*ax + ay*ay + az*az);
						float bv = (bx*bx + by*by + bz*bz);
						float cv = (cx*cx + cy*cy + cz*cz);
						float abs = ax*bx + ay*by + az*bz;
						float cosa = (float)((abs > 0 ? abs : -abs) / Math.sqrt( av*bv ));

						int c = (int)( (cl*cb*cosa) + ((int)(cl*cg*cosa)<<8) + ((int)(cl*cr*cosa)<<16) );
						color += c;
					}
				}

				if( color > 0 /*&& cv < bv*/ ) {
					img.setRGB( x, y, color );
				} else img.setRGB( x, y, 0 );
			}
		}
	}
	
	public static void main(String[] args) {
		
	}
}
