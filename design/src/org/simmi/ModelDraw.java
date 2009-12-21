package org.simmi;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.Random;
import java.util.Set;

import javax.media.opengl.GL;

public class ModelDraw {	
	static {
    	System.loadLibrary("relatron");
    }
	
	//float v = 0.0f;
	
	private static int 		mx, my;
    private static double 	dx, dy, dz;
    private static double 	rx, ry, rz;
    private static double 	x, y, z;
    private static double 	a, da;
    
    ByteBuffer		normBuffer;
    DoubleBuffer	normMatrix;
    
    ByteBuffer		projectionBuffer;
	DoubleBuffer 	projectionMatrix;
	
	public static native int countFaces( String fname );
	public static native int countVertices( String fname );
	public static native void getVertices( ByteBuffer vertexBuffer, String fname );
	
	//public static native int countFaces( String fname );
		/*Lib3dsFile file = Lib3dsLibrary.lib3ds_file_load( fname );
		//Lib3dsMesh[] meshes = file.meshes.castToArray();
		//System.err.println( meshes.length );
		//f = lib3ds_file_open( filename );
		//fprintf( stderr, "opening %s %d\n", filename, f->nmaterials );
		/*int totalFaces = 0;
		int i;
	    for (i = 0; i < f->nmeshes; ++i) {
	    	Lib3dsMesh* mesh = f->meshes[i];
	    	totalFaces += mesh->nfaces;
	    }

	    for (i = 0; i < f->nmaterials; ++i) {
	    	fprintf( stderr, "%s\n", f->materials[i]->texture1_map.name );
	    	fprintf( stderr, "%s\n", f->materials[i]->texture2_map.name );
	    	//fprintf( stderr, "%s\n", f->materials[i]-> );
	    }

	    fprintf( stderr, "done %d file\n", totalFaces );

		return totalFaces;

		return 50;
	}*/
	
	int			matSize;
	int 		faces = 0;
	//int vertices = 0;
	ByteBuffer	vertexBuffer = null;
	//ByteBuffer	indexBuffer = null;
	
	FloatBuffer	dataBuffer = null;
	ByteBuffer	colorBuffer = null;
	FloatBuffer modelBuffer;
	FloatBuffer lightBuffer;
	
	int 		size = 6;
    int 		px, py;
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
		
		String fname = "";
		try {
			fname = f.getCanonicalPath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		faces = ModelDraw.countFaces( fname );
		//vertices = ModelDraw.countVertices( f.getCanonicalPath() );
		//System.err.println( faces );
		
		normBuffer = ByteBuffer.allocateDirect( 8*16 );
		normBuffer.order( ByteOrder.nativeOrder() );
		normMatrix = normBuffer.asDoubleBuffer();
		
		for( int i = 0; i < normMatrix.limit(); i++ ) {
			if( i%5 == 0 ) normMatrix.put(i, 1.0);
			else normMatrix.put(i, 0.0);
		}
		
		projectionBuffer = ByteBuffer.allocateDirect( 8*16 );
		projectionBuffer.order( ByteOrder.nativeOrder() );
		projectionMatrix = projectionBuffer.asDoubleBuffer();
		
		for( int i = 0; i < projectionMatrix.limit(); i++ ) {
			if( i%5 == 0 ) projectionMatrix.put(i, 1.0);
			else projectionMatrix.put(i, 0.0);
		}
		
		vertexBuffer = ByteBuffer.allocateDirect( faces*4*10*3 );
		//indexBuffer = ByteBuffer.allocateDirect( faces*4*3 );
		vertexBuffer.order( ByteOrder.nativeOrder() );
		//indexBuffer.order( ByteOrder.nativeOrder() );
		
		//fileBuffer = ByteBuffer.allocateDirect( 4*faces*6*3 );
		//fileBuffer.order( ByteOrder.nativeOrder() );
		
		
		
		ModelDraw.getVertices( vertexBuffer, fname );
		
		FloatBuffer ff = vertexBuffer.asFloatBuffer();
		float val = 1000.0f;
		for( int k = 0; k < 10*3*faces; k+=10 ) {
			ff.put(k+7, ff.get(k+7)/val);
			ff.put(k+8, ff.get(k+8)/val);
			ff.put(k+9, ff.get(k+9)/val);
		}
		
		/*for( int k = 0; k < 3*3*faces; k+=3 ) {
			ff.put(k+0, ff.get(k+0)/val);
			ff.put(k+1, ff.get(k+1)/val);
			ff.put(k+2, ff.get(k+2)/val);
		}*/
		
		initBuffers();
		initLight();
		
		x = 50.0;
		y = -100.0;
		z = -200.0;
		//loadData();
	}
	
	public void mouseDown( GL gl, int ex, int ey, int ebutton ) {
		px = ex;
		py = ey;
	}
	 
	public void mouseRightDrag( GL gl, int ex, int ey ) {
		float fy = ey-py;
		float fx = px-ex;
		gl.glMatrixMode( GL.GL_MODELVIEW );
		gl.glLoadIdentity();
		//gl.glTranslatef(0.0f, 0.0f, -v);
		float a = (float)Math.sqrt(fy*fy+fx*fx);
		if( a > 0.1f ) gl.glRotated( 0.2*a, fy/a, fx/a, 0 );
		//gl.glTranslatef(0.0f, 0.0f, v);
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
		//gl.glEnable( GL.GL_LIGHT0 );
		gl.glShadeModel( GL.GL_SMOOTH );
		gl.glHint( GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);
		int lightNumber = light+GL.GL_LIGHT0;
		gl.glEnable( lightNumber );
		f.position(0);
		gl.glLightfv(lightNumber, GL.GL_POSITION, f );
		f.position(4);
		gl.glLightfv(lightNumber, GL.GL_AMBIENT, f );
		f.position(8);
		gl.glLightfv(lightNumber, GL.GL_DIFFUSE, f );
		f.position(12);
		gl.glLightfv(lightNumber, GL.GL_SPECULAR, f );
		f.position(16);
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
	
	public void printMatrix( DoubleBuffer matrix ) {
		for( int i = 0; i < 16; i+=4 ) {
			System.err.println( matrix.get(i) + "  " + matrix.get(i+1) + "  " + matrix.get(i+2) + "  " + matrix.get(i+3) );
		}
	}

	int ik = 0;
	public void draw( GL gl, char keychar, int w, int h ) {
		gl.glMatrixMode( GL.GL_PROJECTION );
		gl.glLoadIdentity();
		//gl.glLoadMatrixd( projectionMatrix );
		
		double r = 1.5;
		switch (keychar) {
			case ' ':
				dx = 5.05f*normMatrix.get(2);
				dy = 5.05f*normMatrix.get(6);
				dz = 5.05f*normMatrix.get(10);
				break;
			case 'W':
				dx = -1.01f*normMatrix.get(2);
				dy = -1.01f*normMatrix.get(6);
				dz = -1.01f*normMatrix.get(10);
				break;
			case 'S':
				dx = 1.01f*normMatrix.get(2);
				dy = 1.01f*normMatrix.get(6);
				dz = 1.01f*normMatrix.get(10);							
				break;
			case 'A':
				dx = -1.01f*normMatrix.get(0);
				dy = -1.01f*normMatrix.get(4);
				dz = -1.01f*normMatrix.get(8);
				break;
			case 'D':
				dx = 1.01f*normMatrix.get(0);
				dy = 1.01f*normMatrix.get(4);
				dz = 1.01f*normMatrix.get(8);
				break;
			case 'Q':
				dx = -1.01f*normMatrix.get(1);
				dy = -1.01f*normMatrix.get(5);
				dz = -1.01f*normMatrix.get(9);
				break;
			case 'E':
				dx = 1.01f*normMatrix.get(1);
				dy = 1.01f*normMatrix.get(5);
				dz = 1.01f*normMatrix.get(9);
				break;
			case 'w':
				rx = normMatrix.get(0);
				ry = normMatrix.get(4);
				rz = normMatrix.get(8);
				gl.glRotated(r, rx, ry, rz);
				break;
			case 's':
				rx = normMatrix.get(0);
				ry = normMatrix.get(4);
				rz = normMatrix.get(8);
				gl.glRotated(-r, rx, ry, rz);
				break;
			case 'a':
				rx = normMatrix.get(1);
				ry = normMatrix.get(5);
				rz = normMatrix.get(9);
				gl.glRotated(r, rx, ry, rz);
				break;
			case 'd':
				rx = normMatrix.get(1);
				ry = normMatrix.get(5);
				rz = normMatrix.get(9);
				gl.glRotated(-r, rx, ry, rz);
				break;
			case 'q':
				rx = normMatrix.get(2);
				ry = normMatrix.get(6);
				rz = normMatrix.get(10);
				gl.glRotated(-r, rx, ry, rz);
				break;
			case 'e':
				rx = normMatrix.get(2);
				ry = normMatrix.get(6);
				rz = normMatrix.get(10);
				gl.glRotated(r, rx, ry, rz);
				break;
			default:
				dx = 0.0;
				dy = 0.0;
				dz = 0.0;
				rx = 0.0;
				ry = 0.0;
				rz = 0.0;
		}
		
		/*if( ik++ % 20 == 0 ) {
			printMatrix( projectionMatrix );
			System.err.println("n");
			printMatrix( normMatrix );
		}*/
		
		//printMatrix( normMatrix );
		//printMatrix( projectionMatrix );
		
		gl.glGetDoublev( GL.GL_PROJECTION_MATRIX, projectionMatrix );
		gl.glLoadMatrixd( normMatrix );
		gl.glMultMatrixd( projectionMatrix );
		gl.glGetDoublev( GL.GL_PROJECTION_MATRIX, normMatrix );
		
		//Gravitron.storeProjectMatrix( projectionBuffer );
		//Gravitron.loadMatrix( normBuffer );
		//Gravitron.multMatrix( projectionBuffer );
		//Gravitron.storeProjectMatrix( normBuffer );
		
		x += dx;
		y += dy;
		z += dz;
		a += da;
		
		//gl.glMatrixMode( GL.GL_PROJECTION );
		gl.glLoadIdentity();
		if (w > h) {
    		//double aspect = w / h;
			double aspect = 1.0;
    	    gl.glFrustum(-aspect, aspect, -1.0, 1.0, 1.0, 5000.0);
    	} else {
    		//double aspect = h / w;
    		double aspect = 1.0;
    	    gl.glFrustum (-1.0, 1.0, -aspect, aspect, 1.0, 5000.0);
    	}
		gl.glMultMatrixd( normMatrix );
		//gl.glTranslatef(0.0f,0.0f,-200.0f);
		//gl.glMultMatrixf( normMatrix );
		gl.glTranslated(x, y, z);
		
		gl.glMatrixMode( GL.GL_MODELVIEW );
		
		gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		gl.glClearDepth(1.0f);
		gl.glClear( GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT );
		
		gl.glEnable( GL.GL_COLOR_MATERIAL );
		gl.glEnable( GL.GL_DEPTH_TEST );
		gl.glEnable( GL.GL_LIGHTING );
		gl.glEnable( GL.GL_LIGHT0 );
		//gl.glEnable( GL.GL_LIGHT1 );
		
		if( vertexBuffer != null ) {
			gl.glEnable( GL.GL_CULL_FACE );
			//gl.glTranslatef(50.0f, -100.0f, -v);
			gl.glCullFace( GL.GL_BACK );
			gl.glInterleavedArrays( GL.GL_C4F_N3F_V3F, 0, vertexBuffer );
			gl.glDrawArrays( GL.GL_TRIANGLES, 0, 3*faces );
			gl.glCullFace( GL.GL_FRONT );
			gl.glInterleavedArrays( GL.GL_C4F_N3F_V3F, 0, vertexBuffer );
			gl.glDrawArrays( GL.GL_TRIANGLES, 0, 3*faces );
			//gl.glTranslatef(-50.0f, 100.0f, v);
		}
		
		gl.glDisable( GL.GL_COLOR_MATERIAL );
		gl.glDisable( GL.GL_DEPTH_TEST );
		gl.glDisable( GL.GL_LIGHTING );
		gl.glDisable( GL.GL_LIGHT0 );
		//gl.glDisable( GL.GL_LIGHT1 );
	}
}
