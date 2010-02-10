package org.simmi;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.Console;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.imageio.ImageIO;
import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptException;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.NativeLongByReference;

public class Simlab implements ScriptEngineFactory {
	static {
		Native.register("csimlab");
	}
	public Reader							reader;
	public Map<String,simlab.ByValue>		datalib = new HashMap<String,simlab.ByValue>();
	public simlab.ByValue					data = new simlab.ByValue();
	
	public static native int init();
	public static native int welcome();
	public static native int jcmd( simlab.ByValue sl );
	public static native int jcmdstr( final String s );
	public static native int jstore( String s, simlab.ByValue sl );
	//public static native int cmd( simlab ... s );
	public static native int crnt( simlab.ByValue s );
	public static native int jcrnt( Buffer bb, int type, int length );
	public static native long getlen();
	public static native long gettype();
	//public static native long getaddess();
	//public static native Pointer getpointer();
	public static native simlab.ByValue getdata();
	//public static native bsimlab.ByValue stuff3();
	public static native Pointer stuff2();
	
	public static long	BYTELEN = 8;
	public static long	USHORTLEN = 16;
	public static long	SHORTLEN = 17;
	public static long	UINTLEN = 32;
	public static long	INTLEN = 32;
	public static long	LONGLEN = 64;
	public static long	DOUBLELEN = 66;
	
	boolean where = false;
	
	public int jwelcome() {
		//System.err.println( "Welcome to JSimlab 2.0" );
		//Native.
		
		return 0;
	}
	
	public static class simlab extends Structure {
		public static class ByValue extends simlab implements Structure.ByValue {
			public ByValue() {
				super();
			}
			
			public ByValue( long len, long typ, NativeLongByReference nat ) {
				super( len, typ, nat );
			}
		}
		public NativeLongByReference 	buffer;
		public long 					length;
		public long 					type;
		
		public simlab() {
			length = 0;
			type = 0;
			buffer = new NativeLongByReference();
		}
		
		public simlab( long len, long typ, NativeLongByReference nat ) {
			length = len;
			type = typ;
			buffer = nat;
		}
		
		/*public boolean equals( simlab sl ) {
			return length == sl.length && type == sl.type;
		}*/
	}
	
	/*public static class psimlab extends Structure {
		public static class ByValue extends psimlab implements Structure.ByValue { }
		public Pointer buffer;
		public long length;
		public long type;
	}
	
	public static class bsimlab extends Structure {
		public static class ByValue extends bsimlab implements Structure.ByValue { }
		
		public bsimlab() {
			
		}
		
		public bsimlab( ByteBuffer buffer, long length, long type ) {
			this.buffer = buffer;
			this.length = length;
			this.type = type;
		}
		
		public ByteBuffer buffer;
		public long length;
		public long type;
	}*/
	
	public static int jcrnt_local() {
		simlab.ByValue ps = new simlab.ByValue( 0, 66, new NativeLongByReference( new NativeLong(14) ) );
		return crnt( ps );
	}
	
	/*public static void jerm() {
		simlab s = erm();
		//System.err.println( s.buffer + "   " + s.type + "   " + s.length );
	}*/

	ByteBuffer 	bb;
	Set<Buffer>	bset = new HashSet<Buffer>();
	public void loadimage( final simlab.ByValue ... sl ) throws IOException {
		String urlstr = null;
		if( sl.length == 0 ) {
			JFileChooser	chooser = new JFileChooser();
			if( chooser.showOpenDialog( null ) == JFileChooser.APPROVE_OPTION ) {
				urlstr = chooser.getSelectedFile().toURI().toString();
			}
		} else {
			urlstr = sl[0].buffer.getPointer().getString( 0 ); 
		}
		
		if( urlstr != null ) {
			URL url = new URL( urlstr );
			BufferedImage img = ImageIO.read(url);
			System.out.println( img.getWidth() + " " + img.getHeight() );
			bb = ByteBuffer.allocateDirect( img.getWidth()*img.getHeight()*4 );
			bset.add( bb );
			IntBuffer	ib = bb.asIntBuffer();
			for( int y = 0; y < img.getHeight(); y++ ) {
				for( int x = 0; x < img.getWidth(); x++ ) {
					ib.put( img.getRGB(x, y) );
					//img.get
				}
			}
			
			NativeLongByReference nat = new NativeLongByReference();
			nat.setPointer( Native.getDirectBufferPointer( bb ) );
			data = new simlab.ByValue( bb.limit()/4, UINTLEN, nat);
			//ps.buffer.
			//simlab ps = (simlab)Structure.newInstance( simlab.class );
			//ps.buffer = 14;
			//ps.length = 0;
			//ps.type = 32;
			//crnt( ps );
			//simlab s;
			//s.
		}
	}
	
	public void dumpimage( final String urlstr, final long w ) throws URISyntaxException, IOException {
		final long t = getlen();
		final long h = (t/w);
		BufferedImage	bi = new BufferedImage( (int)w, (int)h, BufferedImage.TYPE_INT_ARGB );
		
		simlab ps = getdata();
		final Pointer ptr = ps.buffer.getPointer();
		IntBuffer	ib = ptr.getByteBuffer(0, (int)t*4).asIntBuffer(); //getIntArray(0, t);
		for( int i = 0; i < t; i++ ) {
			bi.setRGB( (int)(i%w), (int)(i/w), ib.get(i) );
		}
		
		File f = new File( new URI( urlstr ) );
		String format = urlstr.substring( urlstr.length()-3 );
		
		ImageIO.write( bi, format, f );
	}
	
	private AudioFormat getFormat() {
	    float sampleRate = 44100;
	    int sampleSizeInBits = 16;
	    int channels = 1;
	    boolean signed = true;
	    boolean bigEndian = true;
	    return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
	}
	
	public void record() {
		simlab	data = getdata();
		
		final AudioFormat format = getFormat();
	    DataLine.Info info = new DataLine.Info( TargetDataLine.class, format );
		try {			
			TargetDataLine line = (TargetDataLine)AudioSystem.getLine(info);
			line.open(format);
		    line.start();
		     
		    int length = (int)data.length;
		    ByteBuffer bb = data.buffer.getPointer().getByteBuffer(0, 8*length);
		      
		    int bufferSize = (int)format.getSampleRate() * format.getFrameSize();
	        ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
	        ShortBuffer	sb = buffer.asShortBuffer();
	        
	        int total = 0;
		    while( total < length ) {
		    	int r = line.read( buffer.array(), 0, buffer.limit() );
		    	if( data.type == 66 ) {
			    	DoubleBuffer	db = bb.asDoubleBuffer();
				    for( int i = total; i < total+r/2; i++ ) {
				    	if( i >= db.limit() ) System.err.println( db.limit() + "   " + i );
				    	db.put( i, sb.get(i-total) );
				    }
			    }
		    	total += r/2;
		    }
		    
		    line.stop();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
	}
	
	public void play2() {
		simlab bb = getdata();
		Pointer	bbb = stuff2();
		//bsimlab bb3 = stuff3();
		
		long type = bb.type;
		
		if( type == 66 ) {
			for( int i = 0; i < 10; i++ ) {
				double d = bb.buffer.getPointer().getDouble(i*8);
			}
					
			if( bbb != null ) {
				DoubleBuffer db = bbb.getByteBuffer(0, 10*8).asDoubleBuffer();
				for( int i = 0; i < 10; i++ ) {
					double d = db.get(i);
				}
			} else {
				
			}
		} else if( type == 33 ) {
			for( int i = 0; i < 10; i++ ) {
				double d = bb.buffer.getPointer().getInt(i);
			}
		} else if( type == 16 ) {
			Pointer ptr = bb.buffer.getPointer();
			for( int i = 0; i < 10; i++ ) {
				short s = ptr.getShort(i*2);
				char d = ptr.getChar(i);
				byte b = ptr.getByte(i);
			}
		}
	}
	
	public void play() {
		simlab	data = getdata();
		
		int length = (int)data.length;
		ByteBuffer bb = data.buffer.getPointer().getByteBuffer(0, length);
		
		ByteBuffer audio = ByteBuffer.allocate( length*2 );
		ShortBuffer saudio = audio.asShortBuffer();
		
		if( data.type == 66 ) {
			DoubleBuffer db = bb.asDoubleBuffer();
			for( int i = 0; i < db.limit(); i++ ) {
				saudio.put(i, (short)db.get(i) );
			}
		}
		    
	    InputStream input = new ByteArrayInputStream(audio.array());
	    final AudioFormat format = getFormat();
	    final AudioInputStream ais = new AudioInputStream(input, format, audio.limit() / format.getFrameSize());
	    DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
	    SourceDataLine line;
		try {
			line = (SourceDataLine)
			AudioSystem.getLine(info);
			line.open(format);
		    line.start();

		    int bufferSize = (int) format.getSampleRate() * format.getFrameSize();
		    byte buffer[] = new byte[bufferSize];
	      
		    int count;
		    while ((count = ais.read(buffer, 0, buffer.length)) != -1) {
		    	if (count > 0) {
		    		line.write(buffer, 0, count);
		        }
		    }
		    line.drain();
		    line.close();
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	
	public void image( final simlab.ByValue ... sl ) {
		final String name = sl[0].buffer.getPointer().getString(0);
		final long w = sl[1].buffer.getValue().longValue();
		final long t = data.length;
		final long h = (t/w);
		final BufferedImage bi = new BufferedImage( (int)w, (int)h, BufferedImage.TYPE_INT_RGB );
		final Pointer ptr = data.buffer.getPointer();
		
		IntBuffer	ib = ptr.getByteBuffer(0, (int)t*4).asIntBuffer(); //getIntArray(0, t);
		for( int i = 0; i < t; i++ ) {
			bi.setRGB( (int)(i%w), (int)(i/w), ib.get(i) );
		}
		
		SwingUtilities.invokeLater( new Runnable() {
			@Override
			public void run() {
				JFrame	frame = new JFrame( name );
				SimComp c = new SimComp( name, bi, ptr, (int)w, (int)h );
				frame.add(c);
				//datalib.put(name, current);
				compmap.put(data.buffer.getPointer(),c);
				
				frame.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
				frame.setVisible( true );	
			}
		});
	}
	
	public void update( String name, final long w ) {
		SimComp sc = compmap.get(name);
		sc.h = (int)((sc.h*sc.w)/w);
		sc.w = (int)w;
		sc.bi = new BufferedImage( sc.w, sc.h, BufferedImage.TYPE_INT_RGB );
		sc.reload();
	}
	
	public void trans( final simlab.ByValue ... sl ) {
		long w = sl[0].buffer.getValue().longValue();
		crnt( data );
		jcmdstr( "trans "+w );
		SimComp sc = compmap.get( data.buffer.getPointer() );
		if( sc != null ) {
			if( data.type == 32L ) {
				if( w > 0 ) {
					sc.w = (int)((sc.h*sc.w)/w);
					sc.h = (int)w;
				} else {
					sc.h = (int)((sc.h*sc.w)/-w);
					sc.w = (int)-w;
				}
				sc.bi = new BufferedImage( sc.w, sc.h, BufferedImage.TYPE_INT_RGB );
			}
			sc.reload();
		}
	}
	
	/*public void echo( final simlab ... sl ) {
		String val = sl[0].buffer.getPointer().getString(0);
		System.out.println( val );
	}*/
	
	public void str( final String s ) {
		byte[]		bb = s.getBytes();
		ByteBuffer	buffer = ByteBuffer.allocateDirect( bb.length );
		buffer.put( bb );
		jcrnt( buffer, 8, bb.length );
	}
	
	public void write( final String s ) {
		try {
			//URL 			url = new URL( s );
			//URLConnection 	c = url.openConnection();
			//c.setDoOutput( true );
			//OutputStream	out = c.getOutputStream();
			FileOutputStream out = new FileOutputStream( s );
			simlab ps = getdata();
			byte[] bb = ps.buffer.getPointer().getByteArray( 0, (int)ps.length );
			out.write( bb );
			out.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void store( final simlab.ByValue ... ps ) {
		String name = ps[0].buffer.getPointer().getString(0);
		datalib.put(name, data);
		jstore( name, data );
	}
	
	public void fetch( final simlab.ByValue ... ps ) {
		//String name = ps[0].buffer.getPointer().getString(0);
		//data = datalib.get(name);
		
		data = ps[0];
	}
	
	public void cmd( final simlab.ByValue ... sl ) {		
		String s = sl[0].buffer.getPointer().getString(0);
		StringTokenizer	st = new StringTokenizer(s);
		if( st.hasMoreTokens() ) {
			String fname = st.nextToken(" (_\n");			
			Method m = null;
			try {
				//clist.toArray( new Class<?>[ clist.size() ]
				m = Simlab.class.getMethod( fname, simlab.ByValue[].class );
				//List<Class<?>>	clist = new ArrayList<Class<?>>();
				List<simlab.ByValue>	olist = new ArrayList<simlab.ByValue>();
				while( st.hasMoreTokens() ) {
					String str = st.nextToken(" ,)\n");
					if( str.startsWith("\"") ) {
						//clist.add( str.getClass() );
						String 	val = str.substring(1, str.length()-1);
						byte[]				bytes = Native.toByteArray( val );
						tempbuffer.position(0);
						tempbuffer.put( bytes );
						Pointer				ptr = Native.getDirectBufferPointer( tempbuffer );
						NativeLongByReference		lbr = new NativeLongByReference();
						lbr.setPointer( ptr );
						
						//LongByReference lbr = new Longbyre
						simlab.ByValue	nsl = new simlab.ByValue( bytes.length, BYTELEN, lbr );
						olist.add( nsl );
					} else if( str.contains(".") ) {
						double 	d = Double.parseDouble(str);
						simlab.ByValue	nsl = new simlab.ByValue( 0, DOUBLELEN, new NativeLongByReference() );
						//nsl.type = DOUBLELEN;
						//nsl.length = 0;
						//nsl.buffer = new DoubleByReference(d);
						//clist.add( double.class );
						olist.add( nsl );
					} else {
						boolean lb = true;
						try{
							Long 		l = Long.parseLong(str);
							simlab.ByValue		nsl = new simlab.ByValue( 0, LONGLEN, new NativeLongByReference( new NativeLong(l) ) );
							//clist.add( long.class );
							olist.add( nsl );
						} catch( Exception e ) {
							lb = false;
						}
						
						if( !lb ) {
							simlab.ByValue sb = datalib.get(str);
							olist.add( sb );
						}
					}
				}
				
				Object[] args = new Object[] { olist.toArray( new simlab.ByValue[ olist.size() ] ) };
				if( where ) data = getdata();
				where = false;
				m.invoke( Simlab.this, args );
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				//e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			
			if( m == null ) {
				byte[]						bytes = Native.toByteArray( fname );
				Pointer						ptr = Native.getDirectBufferPointer( tempbuffer );
				NativeLongByReference		lbr = new NativeLongByReference();
				lbr.setPointer( ptr );
				
				if( !where ) crnt( data );
				where = true;
				
				simlab.ByValue slbv = new simlab.ByValue( bytes.length, BYTELEN, lbr );
				jcmd( slbv );
				for( Pointer key : compmap.keySet() ) {
					SimComp sc = compmap.get( key );
					if( sc != null ) sc.reload();
				}
			}
			
			/*ByteBuffer buf = ByteBuffer.allocateDirect(6);
			buf.put(0, 's');
			buf.put(1, 'i');
			buf.put(2, 'm');
			buf.put(3, 'm');
			buf.put(4, 'i');
			buf.put(5, '\n');
			
			simlab sl = new simlab();
			
			Pointer ptr = Native.getDirectBufferPointer( buf );
			//Pointer p = new Pointer();
			sl.buffer = ptr.;
			sl.type = 8;
			sl.length = s.length();
			
			cmd( sl );*/
		}
	}
	
	private int bytelength( long type, long length ) {
		if( type < 8 ) return (int)(type*length)/8;
		return (int)( (type/8)*length );
	}
	
	public int type( final simlab.ByValue ... type ) {
		long val = type[0].buffer.getValue().longValue();
		long newtype;
		long oldtype;

		if( val < 8 ) newtype = val;
		else newtype = (val/8)*8;
		if( data.type < 8 ) oldtype = data.type;
		else oldtype = (data.type/8)*8;

		data = new simlab.ByValue( (long)(((long)data.length*oldtype)/(long)newtype), val, data.buffer );
		
		return 1;
	}
	
	public int resize( final simlab.ByValue ... len ) {
		long lenval = ((NativeLongByReference)(len[0].buffer)).getValue().longValue();
		int bytelen = bytelength( data.type, lenval );
		
		ByteBuffer bb = ByteBuffer.allocateDirect( bytelen );	
		NativeLongByReference nat = new NativeLongByReference();
		nat.setPointer( Native.getDirectBufferPointer( bb ) );
		
		data = new simlab.ByValue( lenval, data.type, nat );

		return 1;
	}
	
	public int var( final simlab.ByValue ... sl ) {
		for( String s : datalib.keySet() ) {
			simlab.ByValue slbv = datalib.get( s );
			System.out.println( s + "  " + slbv.type + "  " + slbv.length );
		}
		
		return 0;
	}
	
	public int printtype( final simlab.ByValue ... sl ) {
		System.out.println( data.type );
		
		return 0;
	}
	
	public int printlen( final simlab.ByValue ... sl ) {
		System.out.println( data.length );
		
		return 0;
	}
	
	public int printval( final simlab.ByValue ... sl ) {
		if( data.length > 0 ) {
			System.out.println( data.buffer.getValue() );
		} else {
			System.out.println( data.buffer.hashCode() );
		}
		
		return 0;
	}
	
	ByteBuffer	tempbuffer = ByteBuffer.allocateDirect(256);
	public void parse( final simlab ... sl ) {
		Reader	oldreader = reader;
		
		if( sl.length > 0 ) {
			NativeLongByReference lbr = sl[0].buffer;
			String path = lbr.getPointer().getString(0);
			
			if( path != null && !path.equals("this") ) {
				try {			
					URL url = new URL( path );
					reader = new InputStreamReader( url.openStream() );
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		try {
			Pointer ptr = Native.getDirectBufferPointer( tempbuffer );
			//simlab.ByValue	psl = new simlab.ByValue( data.);
			NativeLongByReference nat = new NativeLongByReference();
			nat.setPointer( ptr );
			
			BufferedReader br = new BufferedReader( reader );
			String line = br.readLine().trim();
			while( line != null && !line.equalsIgnoreCase("quit") ) {
				byte[]	bb = Native.toByteArray( line );
				tempbuffer.position(0);
				tempbuffer.put( bb );
				
				simlab.ByValue	psl = new simlab.ByValue( bb.length, BYTELEN, nat );
				//psl.length = bb.length;
				cmd( psl );
				line = br.readLine().trim();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		reader = oldreader;
	}
	
	Simple	engine = new Simple();
	class SimComp extends JComponent {
		String			name;
		BufferedImage	bi;
		Pointer			ptr;
		int				w;
		int				h;
		
		public SimComp( String name, BufferedImage bi, Pointer ptr, int w, int h ) {
			this.name = name;
			this.bi = bi;
			this.ptr = ptr;
			this.w = w;
			this.h = h;
		}
		
		public void paintComponent( Graphics g ) {
			super.paintComponent( g );
			g.drawImage( bi, 0, 0, this.getWidth(), this.getHeight(), this );
		}
		
		public void reload() {
			int t = w*h;
			int[] ib = ptr.getIntArray(0, t);
			for( int i = 0; i < t; i++ ) {
				//if( i%13 == 0 ) System.err.println( ib[i] );
				bi.setRGB( (int)(i%w), (int)(i/w), ib[i] );
			}
			repaint();
		}
		
		public void repaint() {
			super.repaint();
		}
	};
	
	Map<Pointer,SimComp>	compmap = new HashMap<Pointer,SimComp>();
	public class Simple implements ScriptEngine {
		@Override
		public Bindings createBindings() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object eval(String script) throws ScriptException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object eval(Reader reader) throws ScriptException {
			Simlab.this.reader = reader;
			Simlab.this.parse();
			System.exit(0);
			
			return null;
		}

		@Override
		public Object eval(String script, ScriptContext context) throws ScriptException {
			return null;
		}

		@Override
		public Object eval(Reader reader, ScriptContext context) throws ScriptException {
			return null;
		}

		@Override
		public Object eval(String script, Bindings n) throws ScriptException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object eval(Reader reader, Bindings n) throws ScriptException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object get(String key) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Bindings getBindings(int scope) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public ScriptContext getContext() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public ScriptEngineFactory getFactory() {
			return Simlab.this;
		}

		@Override
		public void put(String key, Object value) {
			
		}

		@Override
		public void setBindings(Bindings bindings, int scope) {
			
		}

		@Override
		public void setContext(ScriptContext context) {
			
		}
	};
	
	public static void test() {
		Map<Point,String>	map = new HashMap<Point,String>();
		Point	p = new Point(0,0);
		
		map.put(p, "simmi1");
		
		p.x = 1;
		
		map.put(p, "simmi2");
		
		for( Point pp : map.keySet() ) {
			System.err.println( pp.hashCode());
			System.err.println( pp.x + "  " + pp.y );
			System.err.println( map.get(pp) );
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//test();
		//System.exit(0);
		
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
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
		
		init();
		welcome();
		
		Console console = System.console();
		/*ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
		List<ScriptEngineFactory> scriptEngineFactories = scriptEngineManager.getEngineFactories();
		for( ScriptEngineFactory scriptEngineFactory : scriptEngineFactories ) {
			System.err.println( scriptEngineFactory.getEngineName() );
		}*/
		
		Simlab  simlab = new Simlab();
		ScriptEngine engine = simlab.getScriptEngine();
		try {
			engine.eval( console.reader() );
		} catch (ScriptException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getEngineName() {
		return "Simlab";
	}

	@Override
	public String getEngineVersion() {
		return "1.0";
	}

	@Override
	public List<String> getExtensions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getLanguageName() {
		return "Simple";
	}

	@Override
	public String getLanguageVersion() {
		return "1.0";
	}

	@Override
	public String getMethodCallSyntax(String obj, String m, String... args) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getMimeTypes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getOutputStatement(String toDisplay) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getParameter(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getProgram(String... statements) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ScriptEngine getScriptEngine() {
		return engine;
	}
}