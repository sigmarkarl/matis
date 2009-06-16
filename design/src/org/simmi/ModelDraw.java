package org.simmi;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.Random;
import java.util.Set;

import javax.media.opengl.GL;

public class ModelDraw {
	public static native int countFaces( String fname );
	public static native int countVertices( String fname );
	public static native void getVertices( ByteBuffer vertexBuffer, String fname );
	
	static {
    	System.loadLibrary("3ds");
    }
	
	int			matSize;
	
	int faces = 0;
	//int vertices = 0;
	ByteBuffer	vertexBuffer = null;
	//ByteBuffer	indexBuffer = null;
	
	FloatBuffer	dataBuffer = null;
	ByteBuffer	colorBuffer = null;
	FloatBuffer modelBuffer;
	FloatBuffer lightBuffer;
	
	int 		size = 6;
    int 		x, y;
    int			total;
    
    //ByteBuffer	fileBuffer;
	
	public ModelDraw( URL url ) {
		ByteBuffer directBuffer = ByteBuffer.allocateDirect( 25000000 );
		directBuffer.order( ByteOrder.nativeOrder() );
		InputStream inputStream;
		try {
			inputStream = url.openStream();
			if( inputStream != null ) {
				byte[]	bb = new byte[8096];
				total = 0;
				int read = inputStream.read( bb );
				while( read != -1 ) {
					directBuffer.put( bb, 0, read ); //put( total++, (byte)read );
					total += read;
					read = inputStream.read( bb );
				}
				inputStream.close();
				System.err.println( "done reading " + total );
			} else {
				System.err.println( "not found" );
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		File f = new File( System.getProperty("user.home"), "tmp.3ds" );
		//File f = File.createTempFile("tmp", ".3ds");
		if( !f.exists() ) {
			FileOutputStream fos;
			try {
				fos = new FileOutputStream(f);
				
				byte[] bb = new byte[ total ];
				for( int i = 0; i < total; i++ ) {
					bb[i] = directBuffer.get(i);
				}
				//BufferedWriter bw = new BufferedWriter( new OutputStreamWriter(fos) );
				fos.write( bb );
				//bw.flush();
				fos.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		try {
			faces = ModelDraw.countFaces( f.getCanonicalPath() );
			//vertices = ModelDraw.countVertices( f.getCanonicalPath() );
			
			//System.err.println( faces + "   " + vertices );
			
			vertexBuffer = ByteBuffer.allocateDirect( faces*4*6*3 );
			//indexBuffer = ByteBuffer.allocateDirect( faces*4*3 );
			vertexBuffer.order( ByteOrder.nativeOrder() );
			//indexBuffer.order( ByteOrder.nativeOrder() );
			
			//fileBuffer = ByteBuffer.allocateDirect( 4*faces*6*3 );
			//fileBuffer.order( ByteOrder.nativeOrder() );
			ModelDraw.getVertices( vertexBuffer, f.getCanonicalPath() );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		FloatBuffer ff = vertexBuffer.asFloatBuffer();
		float val = 1000.0f;
		for( int k = 0; k < 6*3*faces; k+=6 ) {
			ff.put(k+3, ff.get(k+3)/val);
			ff.put(k+4, ff.get(k+4)/val);
			ff.put(k+5, ff.get(k+5)/val);
		}
		
		/*for( int k = 0; k < 3*3*faces; k+=3 ) {
			ff.put(k+0, ff.get(k+0)/val);
			ff.put(k+1, ff.get(k+1)/val);
			ff.put(k+2, ff.get(k+2)/val);
		}*/
		
		initBuffers();
		initLight();
		//loadData();
	}
	
	public void mouseDown( GL gl, int ex, int ey, int ebutton ) {
		x = ex;
		y = ey;
	}
	 
	public void mouseRightDrag( GL gl, int ex, int ey ) {
		float fy = ey-y;
		float fx = x-ex;
		gl.glMatrixMode( GL.GL_MODELVIEW );
		gl.glLoadIdentity();
		float a = (float)Math.sqrt(fy*fy+fx*fx);
		if( a > 0.1f ) gl.glRotated( 0.2*a, fy/a, fx/a, 0 );
		gl.glMultMatrixf( modelBuffer );
	}
	
	public void initBuffers() {
		int matSqure = matSize*matSize;
		
		//vertexBuffer = FloatBuffer.allocate( size*(matSqure)*10 );
		colorBuffer = ByteBuffer.allocate( matSqure );
		Arrays.fill( colorBuffer.array(), (byte)0 );
		
		modelBuffer = FloatBuffer.allocate( 16 );
		lightBuffer = FloatBuffer.allocate( 19 );
		
		Random random = new Random();
		dataBuffer = FloatBuffer.allocate( matSqure );
		for( int i = 0; i < dataBuffer.limit(); i++ ) {
			dataBuffer.put(i, random.nextFloat());
		}
	}
	
	public void initMatrix( GL gl ) {
		gl.glGetFloatv( GL.GL_MODELVIEW_MATRIX, modelBuffer );
	}
	
	public void initLights( GL gl ) {
		int light = 1;
		float[]	exp_cut = {0.0f, 500.0f}; 
		FloatBuffer f = lightBuffer;
		f.rewind();
		gl.glEnable( GL.GL_LIGHTING );
		gl.glEnable( GL.GL_LIGHT0 );
		gl.glShadeModel( GL.GL_SMOOTH );
		gl.glHint( GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);
		int lightNumber = light+GL.GL_LIGHT0;
		gl.glEnable( lightNumber );
		gl.glLightfv(lightNumber, GL.GL_POSITION, f );
		gl.glLightfv(lightNumber, GL.GL_AMBIENT, f );
		gl.glLightfv(lightNumber, GL.GL_DIFFUSE, f );
		gl.glLightfv(lightNumber, GL.GL_SPECULAR, f );
		gl.glLightfv(lightNumber, GL.GL_SPOT_DIRECTION, f );
		gl.glLightfv(lightNumber, GL.GL_SPOT_EXPONENT, exp_cut, 0 );
		gl.glLightfv(lightNumber, GL.GL_SPOT_CUTOFF, exp_cut, 1 );
	}
	
	public void initLight( ) {
		FloatBuffer fLightBuffer = lightBuffer;
		fLightBuffer.put(0, 0.0f);
		fLightBuffer.put(1, 0.0f);
		fLightBuffer.put(2, 100.0f);
		fLightBuffer.put(3, 1.0f);
		
		fLightBuffer.put(4, 0.3f);
		fLightBuffer.put(5, 0.3f);
		fLightBuffer.put(6, 0.3f);
		fLightBuffer.put(7, 1.0f);
		
		fLightBuffer.put(8, 0.8f);
		fLightBuffer.put(9, 0.8f);
		fLightBuffer.put(10, 0.8f);
		fLightBuffer.put(11, 1.0f);
		
		fLightBuffer.put(12, 0.5f);
		fLightBuffer.put(13, 0.5f);
		fLightBuffer.put(14, 0.5f);
		fLightBuffer.put(15, 1.0f);
		
		fLightBuffer.put(16, 0.0f); //-normMatrix.get(2));
		fLightBuffer.put(17, 0.0f); //-normMatrix.get(6));
		fLightBuffer.put(18, 1.0f); //-normMatrix.get(10));
	}
	
	public void loadData() {
		makeSurface();
	}
	
	public void makeSurface() {
		makeSurface( 0,0,matSize-1,matSize-1 );
	}
	
	public void makeSurface( int x1, int y1, int x2, int y2 ) {
    	makeSurface( x1, y1, x2, y2, false, null );
    }
	
	public void makeSurface( int x1, int y1, int x2, int y2, boolean selecting, Set<Integer> selectFilter ) {
		int xx1 = Math.max( 0, x1 );
		int xx2 = Math.min( matSize-1, x2 );
		int yy1 = Math.max( 0, y1 );
		int yy2 = Math.min( matSize-1, y2 );
		//makeSurface( vertexBuffer, selecting, xx1, yy1, xx2, yy2, 0, selectFilter);		
	}
	 
	public void makeSurface( FloatBuffer floatBuffer, boolean selecting, int x1, int y1, int x2, int y2, int offset, Set<Integer> selectFilter ) {
		for( int r = y1; r < y2; r++ ) {
			for( int c = x1; c < x2; c++ ) {
				int i = r*matSize+c + offset;
				int k = 10*size*i;

				float fasti = 0.2f;
				
				float c1 = fasti*(c-matSize/2.0f);
				float c2 = fasti*(c+1-matSize/2.0f);
				float r1 = fasti*(r-matSize/2.0f);
				float r2 = fasti*(r+1-matSize/2.0f);
				
				boolean select = false;
				if( selectFilter != null && selectFilter.contains(i) ) select = true;
				float h1 = (float)Math.log(dataBuffer.get(i)+1.0f);
				byte cl1 = colorBuffer.get(i);
				
				i = (r+1)*matSize+c + offset;
				if( selectFilter != null && selectFilter.contains(i) ) select = true;
				float h2 = (float)Math.log(dataBuffer.get(i)+1.0f);
				byte cl2 = colorBuffer.get(i);
				i = (r+1)*matSize+c+1 + offset;
				if( selectFilter != null && selectFilter.contains(i) ) select = true;;
				float h3 = (float)Math.log(dataBuffer.get(i)+1.0f);
				byte cl3 = colorBuffer.get(i);
				
				float Qx = c1-c1;
				float Qy = r2-r1;
				float Qz = h2-h1;
				float Px = c2-c1;
				float Py = r2-r1;
				float Pz = h3-h1;

				float nx = Py*Qz - Pz*Qy;
				float ny = Pz*Qx - Px*Qz;
				float nz = Px*Qy - Py*Qx;
				
				float div = (float)Math.sqrt( nx*nx+ny*ny+nz*nz );
				if( selecting ) {
					if( selectFilter == null ) div /= 2.0;
					else if( select ) {
						div /= 2.0;
					}
				}
				
				nx /= div;
				ny /= div;
				nz /= div;
				
				float clr = cl1 == 1 ? 1.0f : 0.5f;
				float clg = cl1 == 2 ? 1.0f : 0.5f;
				float clb = cl1 == 3 ? 1.0f : 0.5f;
				floatBuffer.put(k++, clr);
				floatBuffer.put(k++, clg);
				floatBuffer.put(k++, clb);
				floatBuffer.put(k++, 0.5f);
				
				floatBuffer.put(k++, nx);
				floatBuffer.put(k++, ny);
				floatBuffer.put(k++, nz);
				floatBuffer.put(k++, c1);
				floatBuffer.put(k++, r1);
				floatBuffer.put(k++, h1);
				
				clr = cl2 == 1 ? 1.0f : 0.5f;
				clg = cl2 == 2 ? 1.0f : 0.5f;
				clb = cl2 == 3 ? 1.0f : 0.5f;
				floatBuffer.put(k++, clr);
				floatBuffer.put(k++, clg);
				floatBuffer.put(k++, clb);
				floatBuffer.put(k++, 0.5f);
				
				floatBuffer.put(k++, nx);
				floatBuffer.put(k++, ny);
				floatBuffer.put(k++, nz);
				floatBuffer.put(k++, c1);
				floatBuffer.put(k++, r2);
				floatBuffer.put(k++, h2);
				
				clr = cl3 == 1 ? 1.0f : 0.5f;
				clg = cl3 == 2 ? 1.0f : 0.5f;
				clb = cl3 == 3 ? 1.0f : 0.5f;
				floatBuffer.put(k++, clr);
				floatBuffer.put(k++, clg);
				floatBuffer.put(k++, clb);
				floatBuffer.put(k++, 0.5f);
				
				floatBuffer.put(k++, nx);
				floatBuffer.put(k++, ny);
				floatBuffer.put(k++, nz);
				floatBuffer.put(k++, c2);
				floatBuffer.put(k++, r2);
				floatBuffer.put(k++, h3);
				
				select = false;
				
				i = (r+1)*matSize+c+1 + offset;
				if( selectFilter != null && selectFilter.contains(i) ) select = true;
				
				h1 = (float)Math.log(dataBuffer.get(i)+1.0f);
				cl1 = colorBuffer.get(i);
				i = r*matSize+c+1 + offset;
				
				if( selectFilter != null && selectFilter.contains(i) ) select = true;
				
				h2 = (float)Math.log(dataBuffer.get(i)+1.0f);
				cl2 = colorBuffer.get(i);
				i = (r)*matSize+c + offset;
				
				if( selectFilter != null && selectFilter.contains(i) ) select = true;
				
				h3 = (float)Math.log(dataBuffer.get(i)+1.0f);
				cl3 = colorBuffer.get(i);
				
				Qx = c2-c2;
				Qy = r1-r2;
				Qz = h2-h1;
				Px = c1-c2;
				Py = r1-r2;
				Pz = h3-h1;

				nx= Py*Qz - Pz*Qy;
				ny = Pz*Qx - Px*Qz;
				nz = Px*Qy - Py*Qx;
				
				div = (float)Math.sqrt( nx*nx+ny*ny+nz*nz );
				if( selecting ) {
					if( selectFilter == null ) div /= 2.0;
					else if( select ) {
						div /= 2.0;
					}
				}
				nx /= div;
				ny /= div;
				nz /= div;
				
				clr = cl1 == 1 ? 1.0f : 0.5f;
				clg = cl1 == 2 ? 1.0f : 0.5f;
				clb = cl1 == 3 ? 1.0f : 0.5f;
				floatBuffer.put(k++, clr);
				floatBuffer.put(k++, clg);
				floatBuffer.put(k++, clb);
				floatBuffer.put(k++, 0.5f);
				
				floatBuffer.put(k++, nx);
				floatBuffer.put(k++, ny);
				floatBuffer.put(k++, nz);
				floatBuffer.put(k++, c2);
				floatBuffer.put(k++, r2);
				floatBuffer.put(k++, h1);
				
				clr = cl2 == 1 ? 1.0f : 0.5f;
				clg = cl2 == 2 ? 1.0f : 0.5f;
				clb = cl2 == 3 ? 1.0f : 0.5f;
				floatBuffer.put(k++, clr);
				floatBuffer.put(k++, clg);
				floatBuffer.put(k++, clb);
				floatBuffer.put(k++, 0.5f);
				
				floatBuffer.put(k++, nx);
				floatBuffer.put(k++, ny);
				floatBuffer.put(k++, nz);
				floatBuffer.put(k++, c2);
				floatBuffer.put(k++, r1);
				floatBuffer.put(k++, h2);
				
				clr = cl3 == 1 ? 1.0f : 0.5f;
				clg = cl3 == 2 ? 1.0f : 0.5f;
				clb = cl3 == 3 ? 1.0f : 0.5f;
				floatBuffer.put(k++, clr);
				floatBuffer.put(k++, clg);
				floatBuffer.put(k++, clb);
				floatBuffer.put(k++, 0.5f);
				
				floatBuffer.put(k++, nx);
				floatBuffer.put(k++, ny);
				floatBuffer.put(k++, nz);
				floatBuffer.put(k++, c1);
				floatBuffer.put(k++, r1);
				floatBuffer.put(k++, h3);
			}
		}
	}

	public void draw( GL gl ) {
		gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		gl.glClearDepth(1.0f);
		gl.glClear( GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT );
		
		gl.glEnable( GL.GL_COLOR_MATERIAL );
		gl.glEnable( GL.GL_DEPTH_TEST );
		gl.glEnable( GL.GL_LIGHTING );
		
		if( vertexBuffer != null ) {
			gl.glEnable( GL.GL_CULL_FACE );
			gl.glTranslatef(50.0f, -100.0f, 0.0f);
			gl.glCullFace( GL.GL_BACK );
			gl.glInterleavedArrays( GL.GL_N3F_V3F, 0, vertexBuffer );
			gl.glDrawArrays( GL.GL_TRIANGLES, 0, 3*faces );
			gl.glCullFace( GL.GL_FRONT );
			gl.glInterleavedArrays( GL.GL_N3F_V3F, 0, vertexBuffer );
			gl.glDrawArrays( GL.GL_TRIANGLES, 0, 3*faces );
			gl.glTranslatef(-50.0f, 100.0f, 0.0f);
		}
		
		gl.glDisable( GL.GL_COLOR_MATERIAL );
		gl.glDisable( GL.GL_DEPTH_TEST );
		gl.glDisable( GL.GL_LIGHTING );
	}
}
