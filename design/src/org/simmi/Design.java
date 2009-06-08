package org.simmi;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.net.MalformedURLException;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLEventListener;
import javax.swing.JApplet;

public class Design extends JApplet {	
	int 			mouseState = -1;
    int				ex, ey;
    
    boolean			d3 = true;	
	boolean 		showing = true;
	
	ModelDraw		md;
	
	public void init() {
		final GLCanvas canvas = new GLCanvas();
		
		md = new ModelDraw( this.getClass().getResource("/vinland.3ds") );
		canvas.addGLEventListener( new GLEventListener() {
			
			@Override
			public void reshape(GLAutoDrawable drawable, int y, int x, int w, int h) {
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
	        		gl.glTranslatef(0.0f,0.0f,-300.0f);
	        		gl.glMatrixMode( GL.GL_MODELVIEW );
            	} else {
            		gl.glMatrixMode( GL.GL_MODELVIEW );
	            	gl.glLoadIdentity();
	    			gl.glScalef(0.025f, 0.025f, 0.025f);
            	}
            	
            	md.initMatrix( gl );
			}
			
			@Override
			public void init(GLAutoDrawable drawable) {
				GL gl = drawable.getGL();
				md.initLights( gl );
				md.initMatrix( gl );
			}
			
			@Override
			public void displayChanged(GLAutoDrawable arg0, boolean arg1, boolean arg2) {
				// TODO Auto-generated method stub
				
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
						md.mouseRightDrag(gl, ex, ey);
					} else if( mouseState == 10 ) {
						//scatterMouseUp( ex, ey, gl );
					} else if( mouseState == 11 ) {
						//scatterRightMouseUp( ex, ey, glu, gl );
					} else {
						md.mouseDown(gl, ex, ey, mouseState);
					}
				}
				mouseState = -1;
				
				md.draw( gl );
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
					mouseState = 2;
					canvas.repaint();
				}
			}
		});
			
		this.add( canvas );
	}
}
