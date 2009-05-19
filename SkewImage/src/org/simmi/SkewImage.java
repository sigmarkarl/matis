package org.simmi;

import java.awt.Font;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.IOException;
import java.net.URL;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLException;
import javax.swing.JApplet;
import javax.swing.Timer;

import com.sun.opengl.util.j2d.TextRenderer;
import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureCoords;
import com.sun.opengl.util.texture.TextureIO;

public class SkewImage extends JApplet {
	static Image img;
	static Texture t0;
	static Texture t1;
	static Texture t2;
	static Texture t3;
	boolean	running = false;
	double	horn = 0.0;
	double 	oldHorn = 0.0;
	Point	clickPoint;
	
	public void init() {
		final GLCanvas canvas = new GLCanvas();
		
		final Timer animate = new Timer( 50, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				horn += 0.05;
				if( horn > 2*Math.PI ) horn -= 2*Math.PI;
				canvas.display();
			}
		});
		
		canvas.addMouseListener( new MouseListener(){
			@Override
			public void mouseReleased(MouseEvent e) {
				Point relPoint = e.getPoint();
				horn = oldHorn + (relPoint.y - clickPoint.y)*0.005;
				canvas.display();
			}
		
			@Override
			public void mousePressed(MouseEvent e) {
				clickPoint = e.getPoint();
				oldHorn = horn;
				//si.running = !si.running;
				/*if( !si.running ) animate.stop();
				else animate.start();*/
			}
		
			@Override
			public void mouseExited(MouseEvent e) {
				//animate.stop();
			}
		
			@Override
			public void mouseEntered(MouseEvent e) {
				//if( si.running ) animate.start();
			}
		
			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		canvas.addMouseMotionListener( new MouseMotionListener() {
			@Override
			public void mouseMoved(MouseEvent e) {
				
			}
		
			@Override
			public void mouseDragged(MouseEvent e) {
				Point	dragPoint = e.getPoint();
				horn = oldHorn + (dragPoint.y - clickPoint.y)*0.005;
				canvas.display();
			}
		});
		
		canvas.addMouseWheelListener( new MouseWheelListener() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				if( !running ) {
					if( e.getWheelRotation() < 0 ) horn+=0.05;
					else {
						horn-=0.05;
					}
					if( horn > 2*Math.PI ) horn -= 2*Math.PI;
					else if( horn < -2*Math.PI ) horn += 2*Math.PI;
					canvas.display();
				}
			}
		});
		
		canvas.addGLEventListener( new GLEventListener(){
			float faspect = 1.0f;
			float halfWidth = 0.5f;
			float halfHeight = 0.5f;
			TextRenderer	renderer;
			
			@Override
			public void reshape(GLAutoDrawable arg0, int x, int y, int w, int h) {
				GL gl = arg0.getGL();
				
				gl.glMatrixMode( GL.GL_PROJECTION );
				gl.glLoadIdentity();
				if (w > h) {
					double aspect = 0.01*(double)w/(double)h;
				    gl.glFrustum(-aspect, aspect, -0.01, 0.01, 0.01, 5000.0);
				} else {
					double aspect = 0.01*(double)h/(double)w;
				    gl.glFrustum (-0.01, 0.01, -aspect, aspect, 0.01, 5000.0);
				}
				gl.glTranslatef(0.0f, 0.0f, -1.0f);
			}
		
			@Override
			public void init(GLAutoDrawable arg0) {
				GL gl = arg0.getGL();
				gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
				gl.glClearDepth(1.0f);
				gl.glEnable( GL.GL_DEPTH_TEST );				
				gl.glEnable(GL.GL_TEXTURE_2D);
				gl.glTexEnvf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_ENV_MODE, GL.GL_REPLACE);
				
				renderer = new TextRenderer(new Font("SansSerif", Font.BOLD, 36), true, true);
				
				try {
					URL url = this.getClass().getResource("/h0.png");
					t0 = TextureIO.newTexture( url, false, ".png" );
					t0.setTexParameteri(GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
				    t0.setTexParameteri(GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
				    faspect = t0.getAspectRatio();
				    
				    url = this.getClass().getResource("/h1.png");
					t1 = TextureIO.newTexture( url, false, ".png" );
					t1.setTexParameteri(GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
				    t1.setTexParameteri(GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
				    faspect = t1.getAspectRatio();
				    
				    url = this.getClass().getResource("/h2.png");
					t2 = TextureIO.newTexture( url, false, ".png" );
					t2.setTexParameteri(GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
				    t2.setTexParameteri(GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
				    faspect = t2.getAspectRatio();
				    
				    url = this.getClass().getResource("/h3.png");
					t3 = TextureIO.newTexture( url, false, ".png" );
					t3.setTexParameteri(GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
				    t3.setTexParameteri(GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
				    faspect = t3.getAspectRatio();
				    
				    if( faspect >= 0 ) {
				    	halfWidth = 0.5f;
				    	halfHeight = 0.5f/faspect;
				    } else {
				    	halfHeight = 0.5f;
				    	halfWidth = 0.5f*faspect;
				    }
				} catch (GLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		
			@Override
			public void displayChanged(GLAutoDrawable arg0, boolean arg1, boolean arg2) {}
		
			@Override
			public void display(GLAutoDrawable arg0) {								
				GL gl = arg0.getGL();
				gl.glClear( GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT );
				
				TextureCoords tc = t0.getImageTexCoords();			
				t0.bind();
				gl.glBegin( GL.GL_QUADS );
				float cs = (float)(0.5*Math.cos(horn));
				float sn = (float)(0.5*Math.sin(horn));
				gl.glTexCoord2f(tc.left(), tc.bottom()); gl.glVertex3f( -halfWidth, -halfHeight+cs, sn);
				gl.glTexCoord2f(tc.right(), tc.bottom()); gl.glVertex3f( +halfWidth, -halfHeight+cs, sn); //-halfHeight, 0);
				gl.glTexCoord2f(tc.right(), tc.top()); gl.glVertex3f( +halfWidth, halfHeight+cs, sn);
				gl.glTexCoord2f(tc.left(), tc.top()); gl.glVertex3f( -halfWidth, halfHeight+cs, sn);
				gl.glEnd();
				
				renderer.begin3DRendering();
				renderer.setColor( 1.0f, 1.0f, 1.0f, 1.0f );
				renderer.draw3D( "Kjallari", -0.5f, cs+halfHeight*1.1f, sn, 0.001f );
				renderer.end3DRendering();
				
				tc = t1.getImageTexCoords();
				t1.bind();
				gl.glBegin( GL.GL_QUADS );
				cs = (float)(0.5*Math.cos(horn+Math.PI/2));
				sn = (float)(0.5*Math.sin(horn+Math.PI/2));
				gl.glTexCoord2f(tc.left(), tc.bottom()); gl.glVertex3f( -halfWidth, -halfHeight+cs, sn);
				gl.glTexCoord2f(tc.right(), tc.bottom()); gl.glVertex3f( -halfWidth, +halfHeight+cs, sn); //-halfHeight, 0);
				gl.glTexCoord2f(tc.right(), tc.top()); gl.glVertex3f( +halfWidth, +halfHeight+cs, sn);
				gl.glTexCoord2f(tc.left(), tc.top()); gl.glVertex3f( +halfWidth, -halfHeight+cs, sn);
				gl.glEnd();
				renderer.begin3DRendering();
				renderer.setColor( 1.0f, 1.0f, 1.0f, 1.0f );
				renderer.draw3D( "1. hæð", -0.5f, cs+halfHeight*1.1f, sn, 0.001f );
				renderer.end3DRendering();
				
				tc = t2.getImageTexCoords();
				t2.bind();
				gl.glBegin( GL.GL_QUADS );
				cs = (float)(0.5*Math.cos(horn+Math.PI));
				sn = (float)(0.5*Math.sin(horn+Math.PI));
				gl.glTexCoord2f(tc.left(), tc.bottom()); gl.glVertex3f( -halfWidth, -halfHeight+cs, sn);
				gl.glTexCoord2f(tc.right(), tc.bottom()); gl.glVertex3f( +halfWidth, -halfHeight+cs, sn); //-halfHeight, 0);
				gl.glTexCoord2f(tc.right(), tc.top()); gl.glVertex3f( +halfWidth, halfHeight+cs, sn);
				gl.glTexCoord2f(tc.left(), tc.top()); gl.glVertex3f( -halfWidth, halfHeight+cs, sn);
				gl.glEnd();
				renderer.begin3DRendering();
				renderer.setColor( 1.0f, 1.0f, 1.0f, 1.0f );
				renderer.draw3D( "2. hæð", -0.5f, cs+halfHeight*1.1f, sn, 0.001f );
				renderer.end3DRendering();
				
				tc = t3.getImageTexCoords();
				t3.bind();
				gl.glBegin( GL.GL_QUADS );
				cs = (float)(0.5*Math.cos(horn+3*Math.PI/2));
				sn = (float)(0.5*Math.sin(horn+3*Math.PI/2));
				gl.glTexCoord2f(tc.left(), tc.bottom()); gl.glVertex3f( -halfWidth, -halfHeight+cs, sn);
				gl.glTexCoord2f(tc.right(), tc.bottom()); gl.glVertex3f( +halfWidth, -halfHeight+cs, sn); //-halfHeight, 0);
				gl.glTexCoord2f(tc.right(), tc.top()); gl.glVertex3f( +halfWidth, halfHeight+cs, sn);
				gl.glTexCoord2f(tc.left(), tc.top()); gl.glVertex3f( -halfWidth, halfHeight+cs, sn);
				gl.glEnd();
				renderer.begin3DRendering();
				renderer.setColor( 1.0f, 1.0f, 1.0f, 1.0f );
				renderer.draw3D( "3. hæð", -0.5f, cs+halfHeight*1.1f, sn, 0.001f );
				renderer.end3DRendering();
			}
		});
		
		this.add( canvas );
	}
}
