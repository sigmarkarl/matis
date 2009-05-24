package org.simmi;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLEventListener;
import javax.swing.JApplet;
import javax.swing.JComponent;
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
		
		subSplit = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, new JComponent() { public void paintComponent( Graphics g ) { super.paintComponent(g); g.setColor( Color.white ); g.fillRect(0, 0, this.getWidth(), this.getHeight() ); } }, canvas );
		editor = new JTextArea();
		splitPane = new JSplitPane( JSplitPane.VERTICAL_SPLIT, subSplit, editor );
		
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
