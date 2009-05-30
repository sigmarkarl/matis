package org.simmi;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLEventListener;
import javax.swing.JApplet;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class WebLab extends JApplet {
	JSplitPane		splitPane;
	JSplitPane		subSplit;
	GLCanvas		canvas;
	JTextArea		editor;
	boolean			d3 = true;
    int 			mouseState = -1;
    int				ex, ey;
	
	static String 	lof = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
	static Color	transpc = new Color(0,0,0,0);
	
	public void init() {
		try {
			UIManager.setLookAndFeel( lof );
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
		
		this.setBackground( transpc );
		this.getContentPane().setBackground( transpc );
		
		ToolTipManager.sharedInstance().setInitialDelay( 0 );
		
		canvas = new GLCanvas();
		
		double[] kdata = { 0.0, 0.0 };
		double[] adata;
		int		kk = 0;
		int 	kvk = 0;
		
		double	minw = Double.POSITIVE_INFINITY;
		double	maxw = Double.NEGATIVE_INFINITY;
		
		int lgroups = 5;
		int groups = 6;
		
		double[]	groupData = new double[ groups ];
		int[]		groupNum = new int[ groups ];
		
		double[]	lgroupData = new double[ lgroups ];
		int[]		lgroupNum = new int[ lgroups ];
		
		for( int i = 0; i < lgroups; i++ ) {
			lgroupData[i] = 0.0;
			lgroupNum[i] = 0;
		}
		
		int[] ii = {6,7,8,25,30};
		try {
			InputStream is = this.getClass().getResourceAsStream("/fisk.txt");
			List<Object[]>	lobj = TextReader.readText( is, ii );
			for( Object[] obj : lobj ) {
				String kyn = (String)obj[2];
				if( kyn != null ) {
					String wstr = obj[1].toString();
					double wval = Double.parseDouble( wstr );
					
					if( wval < minw ) minw = wval;
					if( wval > maxw ) maxw = wval;
					
					String str = obj[3].toString();
					if( kyn.equals("kvk") ) {
						if( str.length() > 1 ) {
							kdata[0] += Double.parseDouble( str );
							kvk++;
						}
					} else if( kyn.equals("kk") ) {
						if( str.length() > 1 ) {
							kdata[1] += Double.parseDouble( str );
							kk++;
						}
					}
				}
			}
			
			for( Object[] obj : lobj ) {
				String kyn = (String)obj[2];
				if( kyn != null ) {
					String wstr = obj[1].toString();
					double wval = Double.parseDouble( wstr );
					
					String pstr = obj[3].toString();
					if( pstr.length() > 1 ) {
						double pval = Double.parseDouble( pstr );
						int i = (int)((groups*(wval-minw))/(maxw-minw));
						
						if( i < groups ) {
							groupNum[i]++;
							groupData[i] += pval;
						}
					}
					
					String lstr = (String)obj[4];
					if( lstr != null && lstr.length() > 1 ) {
						double lval = Double.parseDouble( lstr );
						int i = (int)((lgroups*(wval-minw))/(maxw-minw));
						
						if( i < lgroups ) {
							lgroupNum[i]++;
							lgroupData[i] += lval;
						}
					}
				}
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		kdata[0] /= kvk;
		kdata[1] /= kk;
		
		String[] wnames = new String[groups];
		String last = ""+minw;
		
		ByteArrayOutputStream	baos = new ByteArrayOutputStream();
		PrintStream	ps = new PrintStream( baos );
		for( int i = 0; i < groups; i++ ) {
			double next = ((i+1)*(maxw-minw))/groups+minw;
			ps.printf( "%.2f", (float)next );
			String str = baos.toString();
			wnames[i] = last+"-"+str+"/"+groupNum[i];
			last = str;
			baos.reset();
			ps.flush();
		}
		
		for( int i = 0; i < groups; i++ ) {
			if( groupNum[i] > 0 ) {
				groupData[i] /= groupNum[i];
			}
			else groupData[i] = 0.0;
		}
		
		String[] lnames = new String[lgroups];
		last = ""+minw;
		for( int i = 0; i < lgroups; i++ ) {
			double next = ((i+1)*(maxw-minw))/lgroups+minw;
			ps.printf( "%.2f", (float)next );
			String str = baos.toString();
			lnames[i] = last+"-"+str+"/"+lgroupNum[i];
			last = str;
			baos.reset();
			ps.flush();
		}
		
		for( int i = 0; i < lgroups; i++ ) {
			if( lgroupNum[i] > 0 ) {
				lgroupData[i] /= lgroupNum[i];
			}
			else lgroupData[i] = 0.0;
		}
		
		String[] names = { "kvk ("+kvk+")", "kk ("+kk+")" };
		
		BarDraw histDraw = new BarDraw("Average parasites/kg per weight", wnames, groupData );
		BarDraw liverDraw = new BarDraw("Average liver fat content (%) per weight", lnames, lgroupData );
		BarDraw barDraw = new BarDraw( "Average parasites/kg per sex", names, kdata );
		
		final BarDraw[]	bds = { histDraw, liverDraw, barDraw };
		
		subSplit = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, barDraw, canvas );
		editor = new JTextArea();
		splitPane = new JSplitPane( JSplitPane.VERTICAL_SPLIT, subSplit, editor );
		
		MouseAdapter	ma = new MouseAdapter() {
			int i = 0;
			public void mousePressed( MouseEvent e ) {
				i = (i+1)%bds.length;
				
				BarDraw bd = bds[i];
				subSplit.setLeftComponent( bd );
			}
		};
		
		for( BarDraw bd : bds ) {
			bd.addMouseListener( ma );
		}
		
		
		final SurfaceDraw	surfaceDraw = new SurfaceDraw( 128 );
		canvas.addGLEventListener( new GLEventListener() {
			@Override
			public void reshape( GLAutoDrawable drawable, int x, int y, int w, int h ) {
				GL gl = drawable.getGL();
				((Component)drawable).setMinimumSize(new Dimension(0,0));
				
				gl.glMatrixMode( GL.GL_PROJECTION );
            	gl.glLoadIdentity();
            	if( d3 ) {
	            	if (w > h) {
	            		double aspect = w / h;
	            	    gl.glFrustum(-aspect, aspect, -1.0, 1.0, 1.0, 500.0);
	            	} else {
	            		double aspect = h / w;
	            	    gl.glFrustum (-1.0, 1.0, -aspect, aspect, 1.0, 500.0);
	            	}
	        		gl.glTranslatef(0.0f,0.0f,-20.0f);
	        		gl.glMatrixMode( GL.GL_MODELVIEW );
            	} else {
            		gl.glMatrixMode( GL.GL_MODELVIEW );
	            	gl.glLoadIdentity();
	    			gl.glScalef(0.025f, 0.025f, 0.025f);
            	}
            	
            	surfaceDraw.initMatrix( gl );
			}
			
			@Override
			public void init( GLAutoDrawable drawable ) {
				GL gl = drawable.getGL();
				surfaceDraw.initLights( gl );
				surfaceDraw.initMatrix( gl );
			}
			
			@Override
			public void displayChanged(GLAutoDrawable arg0, boolean arg1, boolean arg2) {
				
			}
			
			@Override
			public void display(GLAutoDrawable arg0) {
				GL gl = arg0.getGL();
				
				if( mouseState != -1 ) {						
					if( mouseState == 0 ) {
						//Scatter3D.scatterMouseMove(ex, ey, glu, gl);
					} else if( mouseState == 1 ) {
						//Scatter3D.scatterMouseDrag(ex, ey, glu, gl);
					} else if( mouseState == 2 ) {
						surfaceDraw.mouseRightDrag(gl, ex, ey);
					} else if( mouseState == 10 ) {
						//scatterMouseUp( ex, ey, gl );
					} else if( mouseState == 11 ) {
						//scatterRightMouseUp( ex, ey, glu, gl );
					} else {
						surfaceDraw.mouseDown(gl, ex, ey, mouseState);
					}
				}
				mouseState = -1;
				
				surfaceDraw.draw( gl );
			}
		});
		canvas.addMouseListener( new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				mouseState = 2 + e.getButton();
				ex = e.getX();
				ey = e.getY();
				canvas.repaint();
			}

			public void mouseReleased(MouseEvent e) {
				if( e.getButton() == MouseEvent.BUTTON3 ) {
					mouseState = 11;
				} else {
					mouseState = 10;
				}
				canvas.repaint();
			}
		});
		
		canvas.addMouseMotionListener( new MouseMotionAdapter() {
			public void mouseMoved(MouseEvent e) {
				ex = e.getX();
				ey = e.getY();
				mouseState = 0;
				canvas.repaint();
			}
			
			public void mouseDragged( MouseEvent e ) {
				if( (e.getModifiersEx() & MouseEvent.BUTTON3_DOWN_MASK) != 0 ) {
					ex = e.getX();
					ey = e.getY();
					mouseState = 2;
					canvas.repaint();
				} else {
					ex = e.getX();
					ey = e.getY();
					mouseState = 1;
					canvas.repaint();
				}
			}
		});
		
		this.add( splitPane );
		splitPane.setDividerLocation(300);
	}
}
