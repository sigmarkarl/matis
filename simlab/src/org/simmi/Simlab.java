package org.simmi;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Console;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.PrintStream;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
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
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import org.simmi.Simlab.simlab.ByValue;

import com.sun.jna.Function;
import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

public class Simlab implements ScriptEngineFactory {
	static {
		String jnalib = System.getProperty("jna.library.path");
		if (jnalib == null || jnalib.length() == 0) {
			System.setProperty("jna.library.path", ".");
			boolean iswin = Platform.isWindows();

			String filename;
			if (!iswin)
				filename = "libcsimlab.so";
			else
				filename = "csimlab.dll";

			File f = new File(filename);
			if (!f.exists()) {
				InputStream is;
				if (iswin)
					is = Simlab.class.getResourceAsStream("/org/simmi/" + filename);
				else
					is = Simlab.class.getResourceAsStream("/org/simmi/" + filename);

				if (is != null) {
					try {
						FileOutputStream fos = new FileOutputStream(filename);
						byte[] bb = new byte[1024];
						int r = is.read(bb);
						while (r > 0) {
							fos.write(bb, 0, r);
							r = is.read(bb);
						}
						fos.close();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					System.err.println("null");
				}
				// Native.re
			}
		}
		Native.register("csimlab");
	}
	public Reader reader;
	public BufferedReader bufferedreader;
	public Map<String, simlab.ByValue> datalib = new HashMap<String, simlab.ByValue>();
	public simlab.ByValue data = new simlab.ByValue();
	public simlab.ByValue nulldata = new simlab.ByValue(0, 0, 0);
	public List<Object> objects = new ArrayList<Object>();

	public static native int jcmd(simlab.ByValue sl);

	public static native int jcmdstr(final String s);

	public static native int jstore(String s, simlab.ByValue sl);

	// public static native int cmd( simlab ... s );
	public static native int crnt(simlab.ByValue s);

	public static native int jcrnt(Buffer bb, int type, int length);

	public static native long getlen();

	public static native long gettype();

	public static native simlab.ByValue getdata();

	// public static native bsimlab.ByValue stuff3();
	public static native Pointer stuff2();

	public native int matmul(simlab.ByValue v, simlab.ByValue s);

	public native int sqr();

	public native int prim();

	public native int fibo();

	public native int gcd(simlab.ByValue v);

	public native int flip(simlab.ByValue c);

	public native int shift(simlab.ByValue v, simlab.ByValue c);

	public native int init();

	public native int add(simlab.ByValue val);

	public native int sub(simlab.ByValue val);

	public native int mul(simlab.ByValue val);

	public native int simlab_div(simlab.ByValue val);

	public native int simlab_floor();

	public native int simlab_ceil();

	public native int mod(simlab.ByValue val);

	public synchronized native int set(simlab.ByValue val);

	public native int set(simlab.ByValue val, simlab.ByValue map);

	public native int poly(simlab.ByValue val, simlab.ByValue pw);

	public native int transmem(simlab.ByValue m, simlab.ByValue vc);
	
	public native int trans(simlab.ByValue val, simlab.ByValue val2);

	public native int conv(simlab.ByValue convee, simlab.ByValue chunk, simlab.ByValue c_chunk);

	public native int deconv(simlab.ByValue convee, simlab.ByValue chunk, simlab.ByValue c_chunk);

	public native int filter(simlab.ByValue convee, simlab.ByValue chunk, simlab.ByValue c_chunk);

	public native int ifilter(simlab.ByValue convee, simlab.ByValue chunk, simlab.ByValue c_chunk);

	public native int simlab_sin(simlab.ByValue val);

	public native int simlab_cos();

	public native int getPseudoUint(Pointer p, int k);

	public native int getPseudoUintr(Pointer p, int k);

	public native int getPseudoInt(Pointer p, int k);

	public native int getPseudoIntr(Pointer p, int k);

	public native double getPseudoDouble(Pointer p, int k);

	public native double getPseudoDoubler(Pointer p, int k);

	public native int printall();

	public native int simmi(simlab.ByValue s);

	public native int intersect(final simlab.ByValue s);

	public native int reorder( final simlab.ByValue ro );
	public native int invidx();
	public native int sortidx();
	public native int transidx( final simlab.ByValue c, final simlab.ByValue r );
	public native int permute( final simlab.ByValue c, final simlab.ByValue start );
	public native int idx();

	public native int indexer();

	public native int find(final simlab.ByValue s);

	private native int viewer(simlab.ByValue s, simlab.ByValue t);

	public static long BOOLEN = 1;
	public static long DUOLEN = 2;
	public static long QUADLEN = 4;
	public static long UBYTELEN = 8;
	public static long BYTELEN = 9;
	public static long USHORTLEN = 16;
	public static long SHORTLEN = 17;
	public static long UINTLEN = 32;
	public static long INTLEN = 33;
	public static long FLOATLEN = 34;
	public static long ULONGLEN = 64;
	public static long LONGLEN = 65;
	public static long DOUBLELEN = 66;

	boolean where = false;

	public int bb() {
		DoubleBuffer db = bb.asDoubleBuffer();
		System.err.println(bb.limit() + "  " + db.get(1));

		return 0;
	}

	static Map<Long, ByteBuffer> buffers = new HashMap<Long, ByteBuffer>();

	public static class simlab extends Structure implements Cloneable {
		public static class ByValue extends simlab implements Structure.ByValue {
			public ByValue() {
				super();
			}
			
			public ByValue( long cnst ) {
				super( 0, LONGLEN, cnst );
			}
			
			public ByValue( double cnst ) {
				super( 0, DOUBLELEN, Double.doubleToRawLongBits(cnst) );
			}

			public ByValue(long len, long typ, long ptr) {
				super(len, typ, ptr);
			}
		}

		public long buffer;
		public long length;
		public long type;

		public simlab() {
			length = 0;
			type = 0;
			buffer = 0;
		}

		public simlab(long len, long typ, long ptr) {
			length = len;
			type = typ;
			buffer = ptr;
		}
		
		public double getDouble() {
			if( length == 0 && type == 66) {
				return Double.longBitsToDouble(buffer);
			} else if( type == 66 ) {
				return buffers.get(buffer).asDoubleBuffer().get(0);
			} else if( type == 65 ) {
				return (double)buffers.get(buffer).asLongBuffer().get(0);
			} else if( type == 34 ) {
				return (double)buffers.get(buffer).asFloatBuffer().get(0);
			} else if( type == 33 ) {
				return (double)buffers.get(buffer).asIntBuffer().get(0);
			}
			
			return buffer;
		}		
		
		public long getLong() {
			if( length > 0 ) {
				if( type == 66 ) {
					return (long)buffers.get(buffer).asDoubleBuffer().get(0);
				} else if( type == 65 ) {
					return buffers.get(buffer).asLongBuffer().get(0);
				} else if( type == 34 ) {
					return (long)buffers.get(buffer).asFloatBuffer().get(0);
				} else if( type == 33 ) {
					return (long)buffers.get(buffer).asIntBuffer().get(0);
				}
			}
			
			if( type == 66 ) {
				return (long)Double.longBitsToDouble(buffer);
			}
			
			return buffer;
		}

		public int getByteLength() {
			if (type < 8)
				return (int) (type * length) / 8;
			return (int) ((type / 8) * length);
		}

		public byte[] getByteArray(long offset, long length) {
			byte[] bb = new byte[(int) length];
			ByteBuffer bbb = getByteBuffer();
			for (int i = 0; i < length; i++) {
				bb[i] = bbb.get((int) offset + i);
			}

			return bb;
		}

		/*
		 * public ByteBuffer getByteBuffer( long offset, long length ) { if(
		 * buffers.containsKey( buffer ) ) { return } return
		 * getByteBuffer(offset, length); }
		 */

		public ByteBuffer getByteBuffer() {
			ByteBuffer bb = null;
			if (buffers.containsKey(buffer)) {
				bb = buffers.get(buffer);
			} else {
				bb = new Pointer(buffer).getByteBuffer(0, getByteLength());
				buffers.put(buffer, bb);
			}

			return bb;
		}
		
		public Buffer getBuffer() {
			ByteBuffer bb = getByteBuffer();
			if( type == 66 ) return bb.asDoubleBuffer();
			else if( type == 65 ) return bb.asLongBuffer();
			else if( type == 34 ) return bb.asFloatBuffer();
			else if( type == 33 ) return bb.asIntBuffer();
			else if( type == 17 ) return bb.asShortBuffer();
			
			return bb;
		}

		public simlab.ByValue clone() {
			//allocateDirect( 10 );
			return new simlab.ByValue(length, type, buffer);
		}

		// private Pointer p = null;
		/*
		 * private Pointer getThePointer() { Pointer p = null; if(
		 * pointers.containsKey(buffer) ) p = pointers.get(buffer); else { p =
		 * new Pointer(buffer); pointers.put(buffer, p); } /*if( p == null ||
		 * Pointer.nativeValue(p) != buffer ) { p = new Pointer(buffer);
		 * 
		 * if( (type == 8 || type == 9) && buffer != 0 && length > 0 ) { s =
		 * p.getString(0); } else s = buffer + " " + type + " " + length;*
		 * 
		 * return p; }
		 */

		public String getTheString() {
			String s = null;
			// System.err.println("tostring " + buffer + "  " + type + "  " +
			// length);
			if ((type == 8 || type == 9) && buffer != 0 && length > 0) {
				s = Native.getDirectBufferPointer( getByteBuffer() ).getString(0);
				// ).getString(0);
				/*ByteBuffer bb = getByteBuffer();
				byte[] dst = new byte[bb.limit()];
				bb.get(dst);
				s = new String(dst, 0, dst.length - 1);*/
			} else
				s = buffer + " " + type + " " + length;

			return s;
		}

		/*
		 * public boolean equals( simlab sl ) { return length == sl.length &&
		 * type == sl.type; }
		 */
	}

	/*
	 * public static class psimlab extends Structure { public static class
	 * ByValue extends psimlab implements Structure.ByValue { } public Pointer
	 * buffer; public long length; public long type; }
	 * 
	 * public static class bsimlab extends Structure { public static class
	 * ByValue extends bsimlab implements Structure.ByValue { }
	 * 
	 * public bsimlab() {
	 * 
	 * }
	 * 
	 * public bsimlab( ByteBuffer buffer, long length, long type ) { this.buffer
	 * = buffer; this.length = length; this.type = type; }
	 * 
	 * public ByteBuffer buffer; public long length; public long type; }
	 */

	public static int jcrnt_local() {
		simlab.ByValue ps = new simlab.ByValue(0, 66, 14);
		return crnt(ps);
	}

	Object obj = null;

	private long allocateDirect(int size) {
		ByteBuffer bb = ByteBuffer.allocateDirect(size);
		bb.order(ByteOrder.nativeOrder());

		Pointer p = Native.getDirectBufferPointer(bb);
		long pval = Pointer.nativeValue(p);

		buffers.put(pval, bb);

		return pval;
	}
	
	private long allocateDouble( int size ) {
		ByteBuffer bb = ByteBuffer.allocateDirect(size*8);
		bb.order(ByteOrder.nativeOrder());

		Pointer p = Native.getDirectBufferPointer(bb);
		long pval = Pointer.nativeValue(p);
		buffers.put(pval, bb);

		return pval;
	}
	
	File currentDir = null;
	public int cd( final simlab.ByValue dir ) {
		currentDir = new File( dir.getTheString() );
		
		return 1;
	}

	public int viewer(final simlab.ByValue s) {
		long pval = allocateDirect(100);

		crnt(data);
		viewer(s, new simlab.ByValue(0, 65, pval));
		simlab.ByValue sb = getdata();
		data.buffer = sb.buffer;
		data.type = sb.type;
		data.length = sb.length;

		return 1;
	}

	public int view(final simlab.ByValue lstart, final simlab.ByValue lsize) {
		long start = lstart.buffer;
		long size = lsize.buffer;
		if (size == 0) {
			size = data.length - start;
		}
		data.length = size;
		data.buffer = data.buffer + bytelength(data.type, start);

		return 1;
	}

	public int create(final simlab.ByValue sl) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		String className = sl.getTheString();
		Class theclass = Class.forName(className);
		obj = theclass.newInstance();
		data.buffer = 0;
		data.length = objects.size();
		;
		data.type = Byte.MIN_VALUE;
		objects.add(obj);

		return 1;
	}

	public int call(final simlab.ByValue methn, final simlab.ByValue... sl) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		if (data.type == Byte.MIN_VALUE) {
			String methodName = methn.getTheString();
			List<Class> lClass = new ArrayList<Class>();
			List<Object> lObj = new ArrayList<Object>();
			for (simlab.ByValue v : sl) {
				if (v.type == BYTELEN) {
					lClass.add(String.class);
					lObj.add(v.getTheString());
				} else if (v.type == INTLEN || v.type == UINTLEN || v.type == LONGLEN || v.type == ULONGLEN) {
					lClass.add(int.class);
					lObj.add((int) v.buffer);
				} else if (v.type == BOOLEN) {
					lClass.add(boolean.class);
					lObj.add(v.buffer == 1);
				}
			}
			Object obj = objects.get((int) data.length);
			Method meth = obj.getClass().getMethod(methodName, lClass.toArray(new Class[0]));
			meth.invoke(obj, lObj.toArray());
		}

		return 1;
	}
	
	public int reverse( final simlab.ByValue s ) {
		return flip( s );
	}

	public int zero() {
		if (data.type > 0) {
			ByteBuffer bb = data.getByteBuffer();
			int i = 0;
			for (; i < bb.limit(); i += 4) {
				bb.putInt(i, 0);
			}
			for (; i < bb.limit() - 4; i++) {
				bb.put((byte) 0);
			}
		}

		return 0;
	}

	/*
	 * public static void jerm() { simlab s = erm(); //System.err.println(
	 * s.buffer + "   " + s.type + "   " + s.length ); }
	 */

	ByteBuffer bb;
	Set<Buffer> bset = new HashSet<Buffer>();

	public int loadimage() throws IOException {
		/*
		 * String urlstr = null; if (sl.length == 0) { JFileChooser chooser =
		 * new JFileChooser(); if (chooser.showOpenDialog(null) ==
		 * JFileChooser.APPROVE_OPTION) { urlstr =
		 * chooser.getSelectedFile().toURI().toString(); } } else { urlstr =
		 * sl.toString(); }
		 * 
		 * if (urlstr != null) { URL url = new URL(urlstr);
		 */

		ByteBuffer bb = data.getByteBuffer();
		byte[] b2 = new byte[bb.limit()];
		for (int i = 0; i < bb.limit(); i++) {
			b2[i] = bb.get();
		}
		ByteArrayInputStream bais = new ByteArrayInputStream(b2);

		BufferedImage img = ImageIO.read(bais);
		System.out.println(img.getWidth() + " " + img.getHeight());

		int size = img.getWidth() * img.getHeight() * 4;
		long ptr = allocateDirect(size);
		// bb = ByteBuffer.allocateDirect(img.getWidth() * img.getHeight() * 4);
		// bset.add(bb);
		IntBuffer ib = buffers.get(ptr).asIntBuffer();
		for (int y = 0; y < img.getHeight(); y++) {
			for (int x = 0; x < img.getWidth(); x++) {
				ib.put(img.getRGB(x, y));
				// img.get
			}
		}

		// NativeLongByReference nat = new NativeLongByReference();
		// nat.setPointer( Native.getDirectBufferPointer( bb ) );
		// data = new simlab.ByValue(bb.limit() / 4, UINTLEN, ptr);
		data.buffer = ptr;
		data.type = UINTLEN;
		data.length = bb.limit() / 4;

		// }

		return 1;
	}

	public int dumpimage(final simlab.ByValue urlsl, final simlab.ByValue wsl) throws URISyntaxException, IOException {
		final long t = getlen();
		final long w = wsl.buffer;
		final long h = (t / w);
		BufferedImage bi = new BufferedImage((int) w, (int) h, BufferedImage.TYPE_INT_ARGB);

		simlab.ByValue ps = getdata();
		// final Pointer ptr = ps.getThePointer();
		IntBuffer ib = ps.getByteBuffer().asIntBuffer(); // getIntArray(0,
															// t);
		for (int i = 0; i < t; i++) {
			bi.setRGB((int) (i % w), (int) (i / w), ib.get(i));
		}

		String urlstr = urlsl.getTheString();
		File f = urlstr.contains("://") ? new File(new URI(urlstr)) : new File(urlstr);
		String format = urlstr.substring(urlstr.length() - 3);

		ImageIO.write(bi, format, f);

		return 2;
	}

	private AudioFormat getFormat() {
		float sampleRate = 44100;
		int sampleSizeInBits = 16;
		int channels = 1;
		boolean signed = true;
		boolean bigEndian = true;
		return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
	}

	public int shift(simlab.ByValue v) {
		return shift(v, nulldata);
	}

	public int flip() {
		return flip(nulldata);
	}

	public int sin() {
		return simlab_sin(nulldata);
	}
	
	//public int permute( final simlab.ByValue)
	
	public int transidx( final simlab.ByValue c ) {
		crnt(data);
		transidx(c, nulldata);
		data = getdata();
		
		return 1;
	}

	public int trans(simlab.ByValue v) {
		crnt(data);
		trans(v, nulldata);
		data = getdata();
		
		return 1;
	}

	public void record() {
		simlab.ByValue data = getdata();

		final AudioFormat format = getFormat();
		DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
		try {
			TargetDataLine line = (TargetDataLine) AudioSystem.getLine(info);
			line.open(format);
			line.start();

			int length = (int) data.length;
			ByteBuffer bb = data.getByteBuffer();

			int bufferSize = (int) format.getSampleRate() * format.getFrameSize();
			ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
			ShortBuffer sb = buffer.asShortBuffer();

			int total = 0;
			while (total < length) {
				int r = line.read(buffer.array(), 0, buffer.limit());
				if (data.type == 66) {
					DoubleBuffer db = bb.asDoubleBuffer();
					for (int i = total; i < total + r / 2; i++) {
						if (i >= db.limit())
							System.err.println(db.limit() + "   " + i);
						db.put(i, sb.get(i - total));
					}
				}
				total += r / 2;
			}

			line.stop();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
	}

	public void play2() {
		simlab bb = getdata();
		Pointer bbb = stuff2();
		// bsimlab bb3 = stuff3();

		long type = bb.type;

		/*
		 * Pointer ptr = bb.getThePointer(); if (type == 66) { for (int i = 0; i
		 * < 10; i++) { double d = ptr.getDouble(i * 8); }
		 * 
		 * if (bbb != null) { DoubleBuffer db = bbb.getByteBuffer(0, 10 *
		 * 8).asDoubleBuffer(); for (int i = 0; i < 10; i++) { double d =
		 * db.get(i); } } else {
		 * 
		 * } } else if (type == 33) {
		 * 
		 * for (int i = 0; i < 10; i++) { double d = ptr.getInt(i); } } else if
		 * (type == 16) { for (int i = 0; i < 10; i++) { short s =
		 * ptr.getShort(i * 2); char d = ptr.getChar(i); byte b =
		 * ptr.getByte(i); } }
		 */
	}

	public void play() {
		ByteBuffer bb = data.getByteBuffer();

		ByteBuffer audio = ByteBuffer.allocate((int) (data.length * 2));
		ShortBuffer saudio = audio.asShortBuffer();

		if (data.type == 66) {
			DoubleBuffer db = bb.asDoubleBuffer();
			for (int i = 0; i < db.limit(); i++) {
				saudio.put(i, (short) db.get(i));
			}
		}

		InputStream input = new ByteArrayInputStream(audio.array());
		final AudioFormat format = getFormat();
		final int len = audio.limit() / format.getFrameSize();
		final AudioInputStream ais = new AudioInputStream(input, format, len);

		DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
		SourceDataLine line;
		try {
			line = (SourceDataLine) AudioSystem.getLine(info);
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
			ais.close();
			input.close();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void image(final simlab.ByValue ww, final simlab.ByValue ... timer) {
		final String name = "";//sl.getTheString();
		final long w = ww.buffer;
		final long t = data.length;
		final long h = (t / w);
		final BufferedImage bi = new BufferedImage((int) w, (int) h, BufferedImage.TYPE_INT_RGB);
		// final Pointer ptr = data.getPointer();
		final simlab.ByValue slptr = data.clone();

		IntBuffer ib = slptr.getByteBuffer().asIntBuffer(); // getIntArray(0,
															// t);
		for (int i = 0; i < t; i++) {
			bi.setRGB((int) (i % w), (int) (i / w), ib.get(i));
		}

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JFrame frame = new JFrame(name);
				frame.setSize(800, 600);
				final ImageComp c = new ImageComp(name, bi, slptr, (int) w, (int) h);
				frame.add(c);
				// datalib.put(name, current);
				Set<SimComp>	compset;
				if( compmap.containsKey( data.buffer ) ) {
					compset = compmap.get(data.buffer);
				} else {
					compset = new HashSet<SimComp>();
					compmap.put( data.buffer, compset );
				}
				compset.add(c);

				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

				if( timer.length > 1 ) {
					long time = timer[1].buffer;
				
					final Timer tmr = new Timer((int) time, new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							try {
								Simlab.this.run(timer[0]);
							} catch (IllegalArgumentException e1) {
								e1.printStackTrace();
							} catch (IllegalAccessException e1) {
								e1.printStackTrace();
							} catch (InvocationTargetException e1) {
								e1.printStackTrace();
							}
							c.reload();
						}
					});
	
					frame.addWindowListener(new WindowListener() {
						@Override
						public void windowOpened(WindowEvent e) {
						}
	
						@Override
						public void windowIconified(WindowEvent e) {
						}
	
						@Override
						public void windowDeiconified(WindowEvent e) {
						}
	
						@Override
						public void windowDeactivated(WindowEvent e) {
							tmr.stop();
						}
	
						@Override
						public void windowClosing(WindowEvent e) {
						}
	
						@Override
						public void windowClosed(WindowEvent e) {
							tmr.stop();
						}
	
						@Override
						public void windowActivated(WindowEvent e) {
						}
					});
					// tmr.set
					tmr.start();
				}
				frame.setVisible(true);
			}
		});
	}

	public int rugl(final simlab.ByValue sl) {
		List<simlab.ByValue> slist = (List<simlab.ByValue>) objects.get((int) sl.length);

		for (int i = 0; i < slist.size(); i++) {
			simlab.ByValue sb = slist.get(i);
			if (sb.type == Byte.MIN_VALUE) {
				Object o = objects.get((int) sb.length);
				if (o instanceof Method && ((Method) o).getName().equals("fetch")) {
					System.err.println("rugl " + slist.get(i + 1).getTheString());
				}
			}
		}

		return 1;
	}

	public int run(final simlab.ByValue... therunner) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		simlab.ByValue olddata = data.clone();
		simlab.ByValue runner = data.clone();

		if (therunner.length > 0)
			runner = therunner[0];

		// Pointer p = runner.getThePointer();

		if (runner.type == Byte.MIN_VALUE) {
			List<simlab.ByValue> slist = (List<simlab.ByValue>) objects.get((int) runner.length);
			int i = 0;
			while (i < slist.size()) {
				simlab.ByValue sl = slist.get(i);
				Method m = (Method) objects.get((int) sl.length);

				boolean nat = Modifier.isNative(m.getModifiers());

				//System.err.println("run " + m.getName());
				/*
				 * if (sl.type == Byte.MIN_VALUE) { Method m = (Method)
				 * objects.get((int) sl.length); System.err.println("method " +
				 * m.getName()); } else if (sl.type == 8 || sl.type == 9) {
				 * System.err.println(sl.toString()); } else {
				 * System.err.println(data.buffer + "  " + data.type + "  " +
				 * data.length); }
				 */

				if (nat) {
					/*
					 * if( Platform.isWindows() ) {
					 * Long.reverseBytes(data.buffer);
					 * Long.reverseBytes(data.type);
					 * Long.reverseBytes(data.length); }
					 */
					crnt(data);
				}

				int len = m.getParameterTypes().length;
				int ret = 0;
				if (len == 0)
					ret = (Integer) m.invoke(Simlab.this);
				else if (len == 1) {
					simlab.ByValue sb = slist.get(i + 1);
					// System.err.println("with param " + sb.toString());
					Object o = m.invoke(Simlab.this, sb);

					// DoubleBuffer db = null;
					// if( m.getName().equals("set") ) {
					/*
					 * simlab.ByValue sbv = datalib.get("drw"); db =
					 * sbv.getPointer().getByteBuffer(0,
					 * sbv.getByteLength()).asDoubleBuffer();
					 * 
					 * for( int ik = 400; ik < 500; ik++ ) { System.err.print(
					 * db.get(ik) + " " ); } System.err.println();
					 */
					// if( m.getName().equals("add") &&
					// sb.toString().equals("ind") ) {
					/*
					 * sbv = datalib.get("ind"); db =
					 * sbv.getPointer().getByteBuffer(0,
					 * sbv.getByteLength()).asDoubleBuffer();
					 * 
					 * for( int ik = 0; ik < 10; ik++ ) { System.err.print(
					 * db.get(ik) + " u " ); } System.err.println();
					 */
					// }
					// }
					/*
					 * if( db != null ) { for( int ik = 400; ik < 500; ik++ ) {
					 * System.err.print( db.get(ik) + " " ); }
					 * System.err.println(); }
					 */
					ret = (Integer) o;
				} else if (len == 2)
					ret = (Integer) m.invoke(Simlab.this, slist.get(i + 1), slist.get(i + 2));
				else if (len == 3)
					ret = (Integer) m.invoke(Simlab.this, slist.get(i + 1), slist.get(i + 2), slist.get(i + 3));
				else if (len == 4)
					ret = (Integer) m.invoke(Simlab.this, slist.get(i + 1), slist.get(i + 2), slist.get(i + 3), slist.get(i + 4));
				i += ret;

				if (nat) {
					simlab.ByValue sb = getdata();
					data.buffer = sb.buffer;
					data.type = sb.type;
					data.length = sb.length;
					/*
					 * if( Platform.isWindows() ) {
					 * Long.reverseBytes(data.buffer);
					 * Long.reverseBytes(data.type);
					 * Long.reverseBytes(data.length); }
					 */
				}
				i++;
			}
		}

		// buh("ind");
		// buh("calc");
		// buh("drw");

		data.buffer = olddata.buffer;
		data.type = olddata.type;
		data.length = olddata.length;

		return 1;
	}

	public int buh(String name) {
		simlab.ByValue sbv = datalib.get(name);
		DoubleBuffer db = sbv.getByteBuffer().asDoubleBuffer();

		System.err.println(sbv.buffer + " " + sbv.type + "  " + sbv.length);
		for (int ik = 0; ik < 10; ik++) {
			System.err.print(db.get(ik) + " u ");
		}
		System.err.println();

		return 0;
	}

	public void update(String name, final long w) {
		ImageComp sc = (ImageComp)compmap.get(name);
		sc.h = (int) ((sc.h * sc.w) / w);
		sc.w = (int) w;
		sc.bi = new BufferedImage(sc.w, sc.h, BufferedImage.TYPE_INT_RGB);
		sc.reload();
	}

	/*public void trans( final simlab.ByValue... sl ) {
		long w = 0;// sl[0].buffer.getValue().longValue();
		crnt(data);
		jcmdstr("trans " + w);
		ImageComp sc = (ImageComp)compmap.get(data.buffer);
		if (sc != null) {
			if (data.type == 32L) {
				if (w > 0) {
					sc.w = (int) ((sc.h * sc.w) / w);
					sc.h = (int) w;
				} else {
					sc.h = (int) ((sc.h * sc.w) / -w);
					sc.w = (int) -w;
				}
				sc.bi = new BufferedImage(sc.w, sc.h, BufferedImage.TYPE_INT_RGB);
			}
			sc.reload();
		}
	}*/

	public int nil() {
		data.buffer = 0;

		return 0;
	}

	public int echo(simlab.ByValue sl) {
		String val = sl.getTheString();
		System.out.println(val);

		return 1;
	}

	public int welcome() {
		System.out.println("Welcome to Simlab 3.0");

		return 0;
	}

	public void str(final String s) {
		byte[] buffer = s.getBytes();
		bb = ByteBuffer.allocateDirect(buffer.length);
		bset.add(bb);
		bb.put(buffer);
		jcrnt(bb, 8, buffer.length);
	}

	public int read(final simlab.ByValue sl) throws IOException {
		String val = sl.getTheString();
		InputStream stream;
		try {
			URL url = new URL(val);
			stream = url.openStream();
		} catch (MalformedURLException murle) {
			if( currentDir == null ) stream = new FileInputStream(val);
			else stream = new FileInputStream( new File( currentDir, val ) );
		}

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] bb = new byte[1024];
		int r = stream.read(bb);
		int t = 0;
		while (r > 0) {
			t += r;
			baos.write(bb, 0, r);
			r = stream.read(bb);
		}
		data.length = t;
		data.type = 8;
		bb = baos.toByteArray();
		long pval = allocateDirect(t);
		ByteBuffer bbuff = buffers.get(pval);// ByteBuffer.allocateDirect(t);
		for (int i = 0; i < t; i++) {
			bbuff.put(i, bb[i]);
		}
		data.buffer = pval;
		baos.close();

		return 1;
	}

	public void write(final simlab.ByValue s) {
		try {
			// URL url = new URL( s );
			// URLConnection c = url.openConnection();
			// c.setDoOutput( true );
			// OutputStream out = c.getOutputStream();
			FileOutputStream out;
			if( currentDir != null ) out = new FileOutputStream( new File( currentDir, s.getTheString() ) );
			else out = new FileOutputStream(s.getTheString());
			byte[] bb = data.getByteArray(0, data.getByteLength());
			//byte[] bb = ps.getByteArray(0, (int) ps.length);
			out.write(bb);
			out.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void current(final simlab.ByValue sl) {
		data.buffer = sl.buffer;
		data.type = sl.type;
		data.length = sl.length;
	}

	public int store(final simlab.ByValue ps) {
		String name = ps.getTheString();

		// data.buffer = objects.size();
		if (datalib.containsKey(name)) {
			simlab.ByValue erm = datalib.get(name);
			erm.buffer = data.type;
			erm.type = data.type;
			erm.length = data.length;
		} else {
			datalib.put(name, data.clone());
		}
		// if (data.type == Byte.MIN_VALUE) {
		// objects.add(obj);
		// }
		jstore(name, data);

		return 1;
	}

	public int fetch(final simlab.ByValue ps) {
		if (ps.type == BYTELEN) {
			String name = ps.getTheString();
			if (datalib.containsKey(name)) {
				simlab.ByValue tdata = datalib.get(name);
				data.buffer = tdata.buffer;
				data.type = tdata.type;
				data.length = tdata.length;
			}
		} else {
			data.buffer = ps.buffer;
			data.type = ps.type;
			data.length = ps.length;
		}

		return 1;
	}

	public int tail(final simlab.ByValue sl) {
		long chunk = sl.buffer;

		// Pointer p = data.getByteBuffer();
		if (data.type >= 0) {
			int bl = (int) (data.type / 8);
			ByteBuffer bb = data.getByteBuffer();

			if (data.type == DOUBLELEN) {
				DoubleBuffer dbb = bb.asDoubleBuffer();
				int i = (int) (data.length - chunk);
				for (; i < data.length - 1; i++) {
					System.out.printf("%e\t", dbb.get(i));
				}
				System.out.printf("%e\n", dbb.get(i));
			} else if (data.type == FLOATLEN) {
				int i = (int) (data.length - chunk);
				for (; i < data.length - 1; i++) {
					System.out.print(bb.getFloat(i * bl) + "\t");
				}
				System.out.print(bb.getFloat(i * bl) + "\n");
			} else if (data.type == INTLEN) {
				int i = (int) (data.length - chunk);
				for (; i < data.length - 1; i++) {
					System.out.print(bb.getInt(i * bl) + "\t");
				}
				System.out.println(bb.getInt(i * bl));
			}
		} else {
			/*
			 * if (data.type == -INTLEN) { // int bl = (int) (-data.type / 8);
			 * if (data.length >= 0) { for (int i = 0; i < data.length; i +=
			 * chunk) { int k = i; for (; k < Math.min(data.length, i + chunk) -
			 * 1; k++) { // System.out.print(getPseudoIntr(p, k) + "\t"); }
			 * //System.out.println(getPseudoIntr(p, k)); } } else { for (int k
			 * = 0; k < chunk - 1; k++) { // System.out.print(getPseudoInt(p, k)
			 * + "\t"); } //System.out.println(getPseudoInt(p, (int) (chunk -
			 * 1))); } } else if (data.type == -UINTLEN) { if (data.length >= 0)
			 * { for (int i = 0; i < data.length; i += chunk) { int k = i; for
			 * (; k < Math.min(data.length, i + chunk) - 1; k++) {
			 * System.out.print(getPseudoUintr(p, k) + "\t"); }
			 * System.out.println(getPseudoUintr(p, k)); } } else { for (int k =
			 * 0; k < chunk - 1; k++) { System.out.print(getPseudoInt(p, k) +
			 * "\t"); } System.out.println(getPseudoInt(p, (int) (chunk - 1)));
			 * } } else if (data.type == -DOUBLELEN) { if (data.length >= 0) {
			 * for (int i = 0; i < data.length; i += chunk) { int k = i; for (;
			 * k < Math.min(data.length, i + chunk) - 1; k++) {
			 * System.out.print(getPseudoDoubler(p, k) + "\t"); }
			 * System.out.println(getPseudoDoubler(p, k)); } } else { for (int k
			 * = 0; k < chunk - 1; k++) { System.out.print(getPseudoInt(p, k) +
			 * "\t"); } System.out.println(getPseudoInt(p, (int) (chunk - 1)));
			 * } }
			 */
		}

		return 1;
	}

	public int head(final simlab.ByValue sl) {
		long chunk = sl.buffer;

		// Pointer p = data.getThePointer();
		if (data.type >= 0) {
			int bl = (int) (data.type / 8);
			ByteBuffer bb = data.getByteBuffer();

			if (data.type == DOUBLELEN) {
				DoubleBuffer dbb = bb.asDoubleBuffer();
				int i = 0;
				for (; i < chunk - 1; i++) {
					System.out.printf("%e\t", dbb.get(i));
				}
				System.out.printf("%e\n", dbb.get(i));
			} else if (data.type == FLOATLEN) {
				int i = 0;
				for (; i < chunk - 1; i++) {
					System.out.print(bb.getFloat(i * bl) + "\t");
				}
				System.out.print(bb.getFloat(i * bl) + "\n");
			} else if (data.type == INTLEN) {
				int i = 0;
				for (; i < chunk - 1; i++) {
					System.out.print(bb.getInt(i * bl) + "\t");
				}
				System.out.println(bb.getInt(i * bl));
			}
		} else {
			/*
			 * if (data.type == -INTLEN) { // int bl = (int) (-data.type / 8);
			 * if (data.length >= 0) { for (int i = 0; i < data.length; i +=
			 * chunk) { int k = i; for (; k < Math.min(data.length, i + chunk) -
			 * 1; k++) { System.out.print(getPseudoIntr(p, k) + "\t"); }
			 * System.out.println(getPseudoIntr(p, k)); } } else { for (int k =
			 * 0; k < chunk - 1; k++) { System.out.print(getPseudoInt(p, k) +
			 * "\t"); } System.out.println(getPseudoInt(p, (int) (chunk - 1)));
			 * } } else if (data.type == -UINTLEN) { if (data.length >= 0) { for
			 * (int i = 0; i < data.length; i += chunk) { int k = i; for (; k <
			 * Math.min(data.length, i + chunk) - 1; k++) {
			 * System.out.print(getPseudoUintr(p, k) + "\t"); }
			 * System.out.println(getPseudoUintr(p, k)); } } else { for (int k =
			 * 0; k < chunk - 1; k++) { System.out.print(getPseudoInt(p, k) +
			 * "\t"); } System.out.println(getPseudoInt(p, (int) (chunk - 1)));
			 * } } else if (data.type == -DOUBLELEN) { if (data.length >= 0) {
			 * for (int i = 0; i < data.length; i += chunk) { int k = i; for (;
			 * k < Math.min(data.length, i + chunk) - 1; k++) {
			 * System.out.print(getPseudoDoubler(p, k) + "\t"); }
			 * System.out.println(getPseudoDoubler(p, k)); } } else { for (int k
			 * = 0; k < chunk - 1; k++) { System.out.print(getPseudoInt(p, k) +
			 * "\t"); } System.out.println(getPseudoInt(p, (int) (chunk - 1)));
			 * } }
			 */
		}

		return 1;
	}
	
	public int print() {
		if (data.type == UBYTELEN || data.type == BYTELEN) {
			String pstr = data.getTheString();
			System.out.println( pstr );
		} else {
			print( new simlab.ByValue( data.length ) );
		}
		
		return 0;
	}

	public int print(final simlab.ByValue sl) {
		long chunk = sl.buffer;

		// if( data.type != 66 ) {
		// System.err.println(data.buffer + "  " + data.type + "  " +
		// data.length );
		// System.err.println(data.toString());
		// return 1;
		// }

		// Pointer p = data.getThePointer();
		if (data.length == 0) {
			if (data.type == INTLEN) {
				System.out.println(data.buffer);
			} else if (data.type == DOUBLELEN) {
				System.out.println(Double.longBitsToDouble(data.buffer));
			}
		} else if (data.type >= 0) {
			int bl = (int) (data.type / 8);
			ByteBuffer bb = data.getByteBuffer();

			if (data.type == DOUBLELEN) {
				DoubleBuffer dbb = bb.asDoubleBuffer();
				for (int i = 0; i < data.length; i += chunk) {
					int k = i;
					for (; k < Math.min(data.length, i + chunk) - 1; k++) {
						System.out.printf("%e\t", dbb.get(k));
						// System.out.print(dbb.get(k) + "\t");
					}
					System.out.printf("%e\n", dbb.get(k));
				}
			} else if (data.type == FLOATLEN) {
				for (int i = 0; i < data.length; i += chunk) {
					int k = i;
					for (; k < Math.min(data.length, i + chunk) - 1; k++) {
						System.out.print(bb.getFloat(k * bl) + "\t");
					}
					System.out.println(bb.getFloat(k * bl));
				}
			} else if (data.type == INTLEN) {
				for (int i = 0; i < data.length; i += chunk) {
					int k = i;
					for (; k < Math.min(data.length, i + chunk) - 1; k++) {
						System.out.print(bb.getInt(k * bl) + "\t");
					}
					System.out.println(bb.getInt(k * bl));
				}
			} else if (data.type == UBYTELEN || data.type == BYTELEN) {
				System.err.println("never asked for " + data.getTheString() + "  " + data.buffer);
				System.err.println(datalib.get("cview").buffer);
				/*
				 * if( chunk == 0 ) { System.out.println( new
				 * String(bb.toString()) ); } else { byte[] bchunk = new
				 * byte[(int)chunk]; for (int i = 0; i < data.length; i +=
				 * chunk) { //int k = i; bb.get( bchunk ); System.out.println(
				 * new String(bchunk) ); } }
				 */
			}
		} else {
			/*
			 * if (data.type == -INTLEN) { // int bl = (int) (-data.type / 8);
			 * if (data.length >= 0) { for (int i = 0; i < data.length; i +=
			 * chunk) { int k = i; for (; k < Math.min(data.length, i + chunk) -
			 * 1; k++) { System.out.print(getPseudoIntr(p, k) + "\t"); }
			 * System.out.println(getPseudoIntr(p, k)); } } else { for (int k =
			 * 0; k < chunk - 1; k++) { System.out.print(getPseudoInt(p, k) +
			 * "\t"); } System.out.println(getPseudoInt(p, (int) (chunk - 1)));
			 * } } else if (data.type == -UINTLEN) { if (data.length >= 0) { for
			 * (int i = 0; i < data.length; i += chunk) { int k = i; for (; k <
			 * Math.min(data.length, i + chunk) - 1; k++) {
			 * System.out.print(getPseudoUintr(p, k) + "\t"); }
			 * System.out.println(getPseudoUintr(p, k)); } } else { for (int k =
			 * 0; k < chunk - 1; k++) { System.out.print(getPseudoInt(p, k) +
			 * "\t"); } System.out.println(getPseudoInt(p, (int) (chunk - 1)));
			 * } } else if (data.type == -DOUBLELEN) { if (data.length >= 0) {
			 * for (int i = 0; i < data.length; i += chunk) { int k = i; for (;
			 * k < Math.min(data.length, i + chunk) - 1; k++) {
			 * System.out.print(getPseudoDoubler(p, k) + "\t"); }
			 * System.out.println(getPseudoDoubler(p, k)); } } else { for (int k
			 * = 0; k < chunk - 1; k++) { System.out.print(getPseudoInt(p, k) +
			 * "\t"); } System.out.println(getPseudoInt(p, (int) (chunk - 1)));
			 * } }
			 */
		}

		return 1;
	}

	public int back() {
		return 0;
	}

	Map<String, Long> bmap = new HashMap<String, Long>();

	public List<simlab.ByValue> parsePar(StringTokenizer st) {
		List<simlab.ByValue> olist = new ArrayList<simlab.ByValue>();
		tempbuffer.position(0);
		// long offset = 0;
		// long pvalr =
		// Pointer.nativeValue(Native.getDirectBufferPointer(tempbuffer));
		// System.err.println(pvalr);
		while (st.hasMoreTokens()) {
			String str = st.nextToken(endStr);
			if (str.startsWith("\"")) {
				// clist.add( str.getClass() );
				String val = str.substring(1, str.length() - 1);
				// System.err.println("eehehe " + val);

				long pval = 0;
				if (bmap.containsKey(val)) {
					pval = bmap.get(val);
				} else {
					byte[] bytes = val.getBytes();
					// Pointer ptr = nptr.getPointer( tempbuffer.position() );
					pval = allocateDirect(bytes.length);
					bb = buffers.get(pval);// ByteBuffer.allocateDirect(bytes.length);
					for (byte b : bytes) {
						bb.put(b);
					}
					bmap.put(val, pval);
				}
				// tempbuffer.put(bytes);
				// long pval =
				// Pointer.nativeValue(Native.getDirectBufferPointer(bb));
				// long pval = pvalr + offset;
				// offset += bytes.length;

				// ptr.nativeValue( ptr );
				// NativeLongByReference lbr = new NativeLongByReference();
				// lbr.setPointer( ptr );

				// LongByReference lbr = new Longbyre
				simlab.ByValue nsl = new simlab.ByValue(bb.limit(), BYTELEN, pval);
				olist.add(nsl);
			/*} else if (str.startsWith("-")) {
				simlab.ByValue nsl = null;
				try {
					long l = Long.parseLong(str);
					nsl = new simlab.ByValue(0, LONGLEN, -l);
				} catch (Exception e) {
				}

				if (nsl == null) {
					try {
						double d = Double.parseDouble(str);
						nsl = new simlab.ByValue(0, DOUBLELEN, Double.doubleToRawLongBits(-d));
					} catch (Exception e) {
					}
				}
				
				if (nsl != null)
					olist.add(nsl);*/
			} else if (str.startsWith("[")) {
				List<Double> d_vec = new ArrayList<Double>();

				float fval;
				String val = str.substring(1);
				fval = Float.parseFloat(val); // sscanf( result+1, "%e", &fval//
												// );
				d_vec.add((double) fval);

				str = st.nextToken(endStr);
				int len = str.length();
				while (str.charAt(len - 1) != ']') {
					// sscanf( result, "%e", &fval );
					val = str.substring(1);
					fval = Float.parseFloat(val);
					d_vec.add((double) fval);
					str = st.nextToken(endStr);
					len = str.length();
				}
				val = str.substring(0, str.length() - 1);
				fval = Float.parseFloat(val); // sscanf( result, "%e]", &fval );
				d_vec.add((double) fval);

				long pval = allocateDirect(8 * d_vec.size());
				bb = buffers.get(pval); // ByteBuffer.allocateDirect(8 *
										// d_vec.size());
				DoubleBuffer dd = bb.asDoubleBuffer();
				for (double d : d_vec) {
					dd.put(d);
				}
				simlab.ByValue nsl = new simlab.ByValue(d_vec.size(), DOUBLELEN, pval);
				olist.add(nsl);
			} else if (str.contains(".")) {
				double d = Double.parseDouble(str);
				simlab.ByValue nsl = new simlab.ByValue(0, DOUBLELEN, Double.doubleToRawLongBits(d));
				// nsl.type = DOUBLELEN;
				// nsl.length = 0;
				// nsl.buffer = new DoubleByReference(d);
				// clist.add( double.class );
				olist.add(nsl);
			} else if (str.contains("*")) {
				simlab.ByValue nsl = null;

				String[] split = str.split("\\*");
				double d = 1.0;
				long l = 1;
				for (String n : split) {
					boolean lfail = false;
					try {
						long lng = Long.parseLong(n);
						l *= lng;
					} catch (Exception e) {
						lfail = true;
					}

					if (lfail) {
						try {
							double dbl = Double.parseDouble(n);
							d *= dbl;
						} catch (Exception e) {
						}
					}
				}

				if (d > 1.0)
					nsl = new simlab.ByValue(0, DOUBLELEN, Double.doubleToRawLongBits(d * l));
				else
					nsl = new simlab.ByValue(0, LONGLEN, l);

				if (nsl != null)
					olist.add(nsl);
			} else {
				simlab.ByValue nsl = null;
				try {
					long l = Long.parseLong(str);
					nsl = new simlab.ByValue(0, LONGLEN, l);
				} catch (Exception e) {
				}

				if (nsl == null) {
					try {
						double d = Double.parseDouble(str);
						nsl = new simlab.ByValue(0, DOUBLELEN, Double.doubleToRawLongBits(d));
					} catch (Exception e) {
					}
				}

				if (nsl == null) {
					if (datalib.containsKey(str)) {
						nsl = datalib.get(str);
					} else {
						Method[] mm = this.getClass().getMethods();
						Method them = null;
						for (Method me : mm) {
							if (str.equals(me.getName())) {
								them = me;
								break;
							}
						}
						if (them != null) {
							Function f = Function.getFunction("csimlab", str);
							long fp = f == null ? 0 : Pointer.nativeValue(f);
							nsl = new simlab.ByValue(objects.size(), Byte.MIN_VALUE, fp);
							objects.add(them);
						} else {
							nsl = new simlab.ByValue();
							datalib.put(str, nsl);
						}
					}
				}
				if (nsl != null)
					olist.add(nsl);
			}
		}

		return olist;
	}

	private Method getMethod(String fname, int osize) {
		Method m = null;
		try {
			if (osize == 0) {
				try {
					m = Simlab.class.getMethod(fname);
				} catch (Exception e) {
				}
				if (m == null)
					m = Simlab.class.getMethod("simlab_" + fname);
			} else if (osize == 1) {
				try {
					m = Simlab.class.getMethod(fname, simlab.ByValue.class);
				} catch (Exception e) {
				}
				if (m == null)
					m = Simlab.class.getMethod("simlab_" + fname, simlab.ByValue.class);
			} else if (osize == 2) {
				try {
					m = Simlab.class.getMethod(fname, simlab.ByValue.class, simlab.ByValue.class);
				} catch (Exception e) {
				}
				if (m == null)
					m = Simlab.class.getMethod("simlab_" + fname, simlab.ByValue.class, simlab.ByValue.class);
			} else if (osize == 3) {
				try {
					m = Simlab.class.getMethod(fname, simlab.ByValue.class, simlab.ByValue.class, simlab.ByValue.class);
				} catch (Exception e) {
				}
				if (m == null)
					m = Simlab.class.getMethod("simlab_" + fname, simlab.ByValue.class, simlab.ByValue.class, simlab.ByValue.class);
			} else if (osize == 4) {
				try {
					m = Simlab.class.getMethod(fname, simlab.ByValue.class, simlab.ByValue.class, simlab.ByValue.class, simlab.ByValue.class);
				} catch (Exception e) {
				}
				if (m == null)
					m = Simlab.class.getMethod("simlab_" + fname, simlab.ByValue.class, simlab.ByValue.class, simlab.ByValue.class, simlab.ByValue.class);
			}
		} catch (Exception e) {
		}

		if (m == null) {
			try {
				m = Simlab.class.getMethod(fname, simlab.ByValue[].class);
			} catch (NoSuchMethodException e) {
			}

			if (m == null) {
				try {
					m = Simlab.class.getMethod(fname, simlab.ByValue.class, simlab.ByValue[].class);
				} catch (NoSuchMethodException e) {
				}
			}
		}

		return m;
	}

	public static String endStr = " ,)\n";

	public static boolean debug = true;

	public void cmd(simlab.ByValue sl) {
		String s = sl.getTheString();

		StringTokenizer st = new StringTokenizer(s);
		if (st.hasMoreTokens()) {
			String fname = st.nextToken(" (_\n");
			try {
				// clist.toArray( new Class<?>[ clist.size() ]
				// List<Class<?>> clist = new ArrayList<Class<?>>();
				List<simlab.ByValue> olist = parsePar(st);

				// if( olist.size() == 0 ) {
				/*
				 * Class[] cc = new Class[olist.size()]; for( int i = 0; i <
				 * cc.length; i++ ) { cc[i] = simlab.ByValue.class; }
				 */

				// m = Simlab.class.getMethod( fname );

				Method m = getMethod(fname, olist.size());

				if (m == null) {
					simlab.ByValue slval = datalib.get(fname);
					data.buffer = slval.buffer;
					data.type = slval.type;
					data.length = slval.length;
				} else {
					/*
					 * } else if( olist.size() == 1 ) { m =
					 * Simlab.class.getMethod( fname, olist.get(0).getClass() );
					 * }
					 */
					// Object[] args = new Object[] { olist.toArray( new
					// simlab.ByValue[ olist.size() ] ) };
					// if( where ) data = getdata();
					// where = false;
					boolean nv = Modifier.isNative(m.getModifiers());

					if (nv)
						crnt(data);
					if (m.getParameterTypes().length > 0 && m.getParameterTypes()[0] == simlab.ByValue[].class) {
						m.invoke(Simlab.this, new Object[] { olist.toArray(new simlab.ByValue[0]) });
					} else if (m.getParameterTypes().length > 1 && m.getParameterTypes()[1] == simlab.ByValue[].class) {
						List<simlab.ByValue> list = olist.subList(1, olist.size());
						/*
						 * if( list.size() == 0 ) { Object[] stuff = new
						 * Object[] { new simlab.ByValue[0] };
						 * m.invoke(Simlab.this, olist.get(0), new
						 * simlab.ByValue[0] ); } else { Object[] stuff = new
						 * Object[] { list.toArray(new simlab.ByValue[0]) };
						 * m.invoke(Simlab.this, olist.get(0), list.toArray(new
						 * simlab.ByValue[0])); }
						 */
						m.invoke(Simlab.this, olist.get(0), list.toArray(new simlab.ByValue[0]));
					} else if (olist.size() == 0) {
						m.invoke(Simlab.this);
					} else if (olist.size() == 1) {
						m.invoke(Simlab.this, olist.get(0));
					} else if (olist.size() == 2) {
						m.invoke(Simlab.this, olist.get(0), olist.get(1));
					} else if (olist.size() == 3) {
						m.invoke(Simlab.this, olist.get(0), olist.get(1), olist.get(2));
					} else if (olist.size() == 4) {
						m.invoke(Simlab.this, olist.get(0), olist.get(1), olist.get(2), olist.get(3));
					}
					if (nv) {
						simlab.ByValue sb = getdata();
						data.buffer = sb.buffer;
						data.type = sb.type;
						data.length = sb.length;
					}
					// if (m.getName().contains("conv")) {}
				}
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}

			/*
			 * if( m == null ) { byte[] bytes = Native.toByteArray( fname );
			 * Pointer ptr = Native.getDirectBufferPointer( tempbuffer );
			 * NativeLongByReference lbr = new NativeLongByReference();
			 * lbr.setPointer( ptr );
			 * 
			 * if( !where ) crnt( data ); where = true;
			 * 
			 * simlab.ByValue slbv = new simlab.ByValue( bytes.length, BYTELEN,
			 * lbr ); jcmd( slbv ); for( Pointer key : compmap.keySet() ) {
			 * SimComp sc = compmap.get( key ); if( sc != null ) sc.reload(); }
			 * }
			 */

			if( compmap.containsKey(data.buffer) ) {
				for( SimComp sc : compmap.get(data.buffer) ) {
					if( sc != null ) {
						sc.reload();
					}
				}
			}
			
			/*for (long key : compmap.keySet()) {
				SimComp sc = compmap.get(key);
				if (sc != null)
					sc.reload();
			}*/

			/*
			 * ByteBuffer buf = ByteBuffer.allocateDirect(6); buf.put(0, 's');
			 * buf.put(1, 'i'); buf.put(2, 'm'); buf.put(3, 'm'); buf.put(4,
			 * 'i'); buf.put(5, '\n');
			 * 
			 * simlab sl = new simlab();
			 * 
			 * Pointer ptr = Native.getDirectBufferPointer( buf ); //Pointer p =
			 * new Pointer(); sl.buffer = ptr.; sl.type = 8; sl.length =
			 * s.length();
			 * 
			 * cmd( sl );
			 */
		}
	}

	private int bytelength(long type, long length) {
		if (type < 8)
			return (int) (type * length) / 8;
		return (int) ((type / 8) * length);
	}

	public int type(final simlab.ByValue type) {
		long val = type.buffer;
		long newtype;
		long oldtype;

		if (val < 8)
			newtype = val;
		else
			newtype = (val / 8) * 8;
		if (data.type < 8)
			oldtype = data.type;
		else
			oldtype = (data.type / 8) * 8;

		// data = new simlab.ByValue((long) (((long) data.length * oldtype) /
		// (long) newtype), val, data.buffer);
		long newlen = (long) (((long) data.length * oldtype) / (long) newtype);
		data.type = val;
		data.length = newlen;

		return 1;
	}

	public int simlab_new(final simlab.ByValue type, final simlab.ByValue len) {
		long lenval = len.buffer;
		data.type = type.buffer;
		if (lenval == 0) {
			data.length = 0;
		} else {
			int bytelen = bytelength(data.type, lenval);
			long pval = allocateDirect(bytelen);
			data.buffer = pval;
			data.length = lenval;
		}

		return 2;
	}

	public int simlab_new(final simlab.ByValue type) {
		simlab_new(type, new simlab.ByValue(0, 65, 0));

		return 1;
	}

	public int resize(final simlab.ByValue len) {
		long lenval = len.buffer;
		int bytelen = bytelength(data.type, lenval);

		// NativeLongByReference nat = new NativeLongByReference();
		// nat.setPointer( Native.getDirectBufferPointer( bb ) );

		long pval = allocateDirect(bytelen); // Pointer.nativeValue(Native.getDirectBufferPointer(bb));
		// data = new simlab.ByValue(lenval, data.type, pval);
		data.buffer = pval;
		data.length = lenval;

		return 1;
	}

	public int var() {
		for (String s : datalib.keySet()) {
			simlab.ByValue slbv = datalib.get(s);
			System.out.println(s + "  " + slbv.type + "  " + slbv.length);
		}

		return 0;
	}

	public int printtype() {
		System.out.println(data.type);

		return 0;
	}

	public int printname() {
		for (Entry<String, ByValue> e : datalib.entrySet()) {
			System.err.println(e + "  " + data.buffer + " " + data.type);
			if (e.getValue() != null && e.getValue().equals(data)) {
				System.err.println("mu2");
				System.out.println(e.getKey());
				return 0;
			}
		}
		System.err.println("mu");
		return 0;
	}
	
	public int len() {
		data.buffer = data.length;
		data.length = 0;
		data.type = 65;
		
		return 0;
	}
	
	public int getthetype() {
		data.buffer = data.type;
		data.length = 0;
		data.type = 65;
		
		return 0;
	}

	public int printlen() {
		System.out.println(data.length);

		return 0;
	}

	public int printval(final simlab.ByValue... sl) {
		if (data.length > 0) {
		} else {
		}

		return 0;
	}
	
	public int shuffleidx() {
		crnt( data );
		idx();
		data = getdata();
		shuffle();
		
		return 0;
	}
	
	public int shuffle() {
		shuffle( new simlab.ByValue(data.length) );
		
		return 0;
	}
	
	Random r = new Random();
	public int shuffle( final simlab.ByValue chunk ) {
		int ch = (int)chunk.getLong();
		
		if( data.type/8 == 8 ) {
			LongBuffer lb = data.getByteBuffer().asLongBuffer();
			for( int u = 0; u < data.length; u+=ch ) {
				for( int i = u; i < u+ch; i++ ) {
					int k = (int)(r.nextDouble()*data.length);
					long tmp = lb.get(i);
					lb.put(i, lb.get(k));
					lb.put(k, tmp);
				}
			}
		} else if( data.type/8 == 4 ) {
			IntBuffer ib = data.getByteBuffer().asIntBuffer();
			for( int u = 0; u < data.length; u+=ch ) {
				for( int i = u; i < u+ch; i++ ) {
					int k = (int)(r.nextDouble()*data.length);
					int tmp = ib.get(i);
					ib.put(i, ib.get(k));
					ib.put(k, tmp);
				}
			}
		} else if( data.type/8 == 2 ) {
			ShortBuffer sb = data.getByteBuffer().asShortBuffer();
			for( int u = 0; u < data.length; u+=ch ) {
				for( int i = u; i < u+ch; i++ ) {
					int k = (int)(r.nextDouble()*data.length);
					short tmp = sb.get(i);
					sb.put(i, sb.get(k));
					sb.put(k, tmp);
				}
			}
		} else if( data.type/8 == 1 ) {
			ByteBuffer bb = data.getByteBuffer();
			for( int u = 0; u < data.length; u+=ch ) {
				for( int i = u; i < u+ch; i++ ) {
					int k = (int)(r.nextDouble()*data.length);
					byte tmp = bb.get(i);
					bb.put(i, bb.get(k));
					bb.put(k, tmp);
				}
			}
		} else {
			int s = (int)(data.type/8);
			ByteBuffer bb = data.getByteBuffer();
			int sch = s*ch;
			byte[] bs = new byte[s];
			for( int u = 0; u < data.length; u+=sch ) {
				for( int i = u; i < u+sch; i++ ) {
					int k = (int)(r.nextDouble()*data.length);
					bb.get(bs);
					//bb.position();
					//bb.put(i, bb.get(k));
					//bb.put(k, tmp);
				}
			}
		}
		
		return 1;
	}

	public int rand() {
		if (data.type > 0) {
			ByteBuffer bb = data.getByteBuffer();

			if (data.type == 66) {
				DoubleBuffer db = bb.asDoubleBuffer();
				for (int i = 0; i < data.length; i++) {
					db.put(r.nextDouble());
				}
			} else if (data.type == 65) {
				LongBuffer lb = bb.asLongBuffer();
				for (int i = 0; i < data.length; i++) {
					lb.put(r.nextLong());
				}
			} else if (data.type == 34) {
				FloatBuffer fb = bb.asFloatBuffer();
				for (int i = 0; i < data.length; i++) {
					fb.put(r.nextFloat());
				}
			} else if (data.type == 33) {
				IntBuffer ib = bb.asIntBuffer();
				for (int i = 0; i < data.length; i++) {
					ib.put(r.nextInt());
				}
			}
		}

		return 0;
	}
	
	public int dump() throws IOException {
		dump( datalib.get("one") );
		
		return 0;
	}
	
	public int dump( final simlab.ByValue sl ) throws IOException {
		int cval = (int)sl.getLong();
		ByteArrayOutputStream	baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream( baos );
		ByteBuffer	bb = data.getByteBuffer();
		for( int r = 0; r < data.length/cval; r++ ) {
			for( int c = 0; c < cval; c++ ) {
				if( data.type == DOUBLELEN ) {
					ps.printf( "%e\t", bb.asDoubleBuffer().get(r*cval+c) );
				} else if( data.type == LONGLEN || data.type == ULONGLEN ) {
					ps.printf( "%f\t", bb.asLongBuffer().get(r*cval+c) );
				} else if( data.type == FLOATLEN ) {
					ps.printf( "%f\t", bb.asFloatBuffer().get(r*cval+c) );
				} else if( data.type == UINTLEN || data.type == INTLEN ) {
					ps.printf( "%d\t", bb.asIntBuffer().get(r*cval+c) );
				}
			}
			ps.println();
		}
		ps.flush();
		
		byte[] ba = baos.toByteArray();
		long pval = allocateDirect( ba.length );
		bb = buffers.get( pval );
		for( byte b : ba ) {
			bb.put( b );
		}
		data.length = ba.length;
		data.type = 8;
		data.buffer = pval;
		
		ps.close();
		baos.close();
		
		return 1;
	}
	
	public int load() {
		if( data.type == 8 || data.type == 9 ) {
			String str = data.getTheString();
			String[] spl = str.split("\n");
			String[] nspl = spl[0].split("[\t ]+");
			
			int size = spl.length*nspl.length;
			long pval = allocateDouble( size );
			data.buffer = pval;
			data.type = DOUBLELEN;
			data.length = size;
			
			DoubleBuffer dd = buffers.get(pval).asDoubleBuffer();
			for( String sp : spl ) {
				nspl = sp.split("[\t ]+");
				for( String nsp : nspl ) {
					dd.put( Double.parseDouble(nsp) );
				}
			}
		}
		
		return 0;
	}
	
	public int table( final simlab.ByValue t ) {
		final int c = (int)t.buffer;
		final int r = (int)(data.length / c);
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JFrame frame = new JFrame("");
				frame.setSize(800, 600);
				TableComp	tc = new TableComp("", data.clone(), c, r);
				frame.add(tc);
				
				Set<SimComp>	compset;
				if( compmap.containsKey( data.buffer ) ) {
					compset = compmap.get( data.buffer );
				} else {
					compset = new HashSet<SimComp>();
					compmap.put( data.buffer, compset );
				}
				compset.add(tc);
				
				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				frame.setVisible( true );
			}
		});
		
		return 1;
	}

	public int system(final simlab.ByValue s) throws IOException {
		String command = s.getTheString();
		Process p = Runtime.getRuntime().exec(command);
		InputStream is = p.getInputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String line = br.readLine();
		while (line != null) {
			System.out.println(line);

			line = br.readLine();
		}

		return 1;
	}

	public int jinit() {
		datalib.put("null", nulldata);
		datalib.put("bit", new simlab.ByValue(0, 65, 1));
		datalib.put("duo", new simlab.ByValue(0, 65, 2));
		datalib.put("quad", new simlab.ByValue(0, 65, 4));
		datalib.put("ubyte", new simlab.ByValue(0, 65, 8));
		datalib.put("byte", new simlab.ByValue(0, 65, 9));
		datalib.put("ushort", new simlab.ByValue(0, 65, 16));
		datalib.put("short", new simlab.ByValue(0, 65, 17));
		datalib.put("uint", new simlab.ByValue(0, 65, 32));
		datalib.put("int", new simlab.ByValue(0, 65, 33));
		datalib.put("float", new simlab.ByValue(0, 65, 34));
		datalib.put("ulong", new simlab.ByValue(0, 65, 64));
		datalib.put("long", new simlab.ByValue(0, 65, 65));
		datalib.put("double", new simlab.ByValue(0, 65, 66));

		datalib.put("PI", new simlab.ByValue(0, 65, Double.doubleToRawLongBits(Math.PI)));
		datalib.put("e", new simlab.ByValue(0, 65, Double.doubleToRawLongBits(Math.E)));

		datalib.put("true", new simlab.ByValue(0, 1, 1));
		datalib.put("false", new simlab.ByValue(0, 1, 0));
		
		datalib.put("nil", new simlab.ByValue(0, 65, 0));
		datalib.put("one", new simlab.ByValue(0, 65, 1));
		datalib.put("two", new simlab.ByValue(0, 65, 2));

		return 0;
	}

	public int compile(simlab.ByValue fnc) {
		/*
		 * int datasize = (bsize+11)/12; if( data.buffer == 0 ) { data.length =
		 * datasize+1; data.type = 96; data.buffer = (long)new simlab[
		 * data.length ]; } else { int nz = data.length+datasize+1; if( nz >
		 * data.length ) { simlab newsize; newsize.buffer = nz; newsize.type =
		 * 32; newsize.length = 0; resize( newsize ); } } simlab* databuffer =
		 * (simlab*)data.buffer; int ind = data.length-datasize-1; simlab &
		 * subdata = databuffer[ind]; subdata = fnc; if( bsize > 0 ) memcpy(
		 * &databuffer[data.length-datasize], &passnext, bsize );
		 */

		return 0;
	}

	public int interprete(final simlab.ByValue sl) throws SecurityException, NoSuchMethodException {
		// Pointer lbr = sl.getPointer();
		String command = sl.getTheString();

		if (command.startsWith("\"")) {
			String substr = command.substring(1, command.length() - 1);
			byte[] bb = Native.toByteArray(substr);
			tempbuffer.position(0);
			tempbuffer.put(bb);
			// NativeLongByReference nt = new NativeLongByReference();
			long pval = Pointer.nativeValue(Native.getDirectBufferPointer(tempbuffer));
			// nt.setPointer( ptr );
			simlab.ByValue str = new simlab.ByValue(substr.length(), Simlab.BYTELEN, pval);
			echo(str);
		} else {
			List<simlab.ByValue> slist;

			StringTokenizer st = new StringTokenizer(command);
			if (st.hasMoreTokens()) {
				String result = st.nextToken(" (_\n");
				if (data.length == 0 && data.buffer == 0) {
					slist = new ArrayList<simlab.ByValue>();
					data.buffer = 0;
					data.length = objects.size();
					data.type = Byte.MIN_VALUE;
					objects.add(slist);
				} else {
					slist = (List<simlab.ByValue>) objects.get((int) data.length);
				}

				List<simlab.ByValue> olist = parsePar(st);
				Method m = getMethod(result, olist.size());
				if (m != null) {
					simlab.ByValue fnc;
					if (Modifier.isNative(m.getModifiers())) {
						Function f = Function.getFunction("csimlab", m.getName());
						fnc = new simlab.ByValue(objects.size(), Byte.MIN_VALUE, f == null ? 0 : Pointer.nativeValue(f));
					} else
						fnc = new simlab.ByValue(objects.size(), Byte.MIN_VALUE, 0);
					objects.add(m);

					slist.add(fnc);
					slist.addAll(olist);
				}
			}

			// char* result = strtok( command, " (_\n" );
			// int func = dsym( module, result );
			// if( func != 0 /*&& (jobj == 0 || jcls == 0 || func == (long)store
			// || func == (long)fetch || func == (long)Class || func ==
			// (long)Data || func == (long)create)*/ ) {
			// simlab.ByValue fnc;
			// fnc.buffer = func;
			// fnc.type = 32;
			// fnc.length = 0;
			// parseParameters( 0 );
			// compile( fnc, passnext );
			// }
		}
		return 1;
	}

	ByteBuffer tempbuffer = ByteBuffer.allocateDirect(8192);

	// Pointer ptr = Native.getDirectBufferPointer( tempbuffer );
	public int parse2(simlab.ByValue sl) throws IOException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, SecurityException, NoSuchMethodException {
		// simlab.ByValue psl = new simlab.ByValue( data.);
		// NativeLongByReference nat = new NativeLongByReference();
		// nat.setPointer( ptr );

		// Pointer p = new Pointer(sl.buffer);
		String mname = sl.getTheString(); // p.getString(0);
		Method m = this.getClass().getMethod(mname, simlab.ByValue.class);
		parse(m);

		return 1;
	}

	private void parse(Method m) throws IOException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		// BufferedReader br = new BufferedReader(reader);
		String line = bufferedreader.readLine();
		while (line != null && !line.equalsIgnoreCase("quit")) {
			line = line.trim();

			byte[] bb = Native.toByteArray(line);
			tempbuffer.position(0);
			long pval = Pointer.nativeValue(Native.getDirectBufferPointer(tempbuffer));
			tempbuffer.put(bb);

			simlab.ByValue psl = new simlab.ByValue(bb.length, BYTELEN, pval);
			// psl.length = bb.length;
			// cmd( psl );
			m.invoke(this, psl);

			// if (debug) {
			// System.err.println("after running " + line + "  " + data.buffer +
			// "  " + data.type + "  " + data.length);
			// }

			line = bufferedreader.readLine();
		}
	}

	private void ok() {
		List<simlab.ByValue> slist = (List<simlab.ByValue>) objects.get((int) data.length);
		System.err.println("starting " + data.length + " " + slist.size());
		int i = 0;
		while (i < slist.size()) {
			simlab.ByValue sl = slist.get(i);
			if (sl.type == Byte.MIN_VALUE) {
				Method mm = (Method) objects.get((int) sl.length);
			} else if (sl.type == 8 || sl.type == 9) {
				System.err.println(sl.getTheString());
			} else {
				System.err.println(sl.buffer + "  " + sl.type + "  " + sl.length);
			}
			i++;
		}
	}

	private int parse() throws IOException {
		// simlab.ByValue psl = new simlab.ByValue( data.);
		// NativeLongByReference nat = new NativeLongByReference();
		// nat.setPointer( ptr );

		// BufferedReader br = new BufferedReader(reader);

		String line = bufferedreader.readLine();
		while (line != null && !line.equalsIgnoreCase("quit")) {
			line = line.trim();

			byte[] bb = Native.toByteArray(line);
			tempbuffer.position(0);
			long pval = Pointer.nativeValue(Native.getDirectBufferPointer(tempbuffer));
			tempbuffer.put(bb);

			simlab.ByValue psl = new simlab.ByValue(bb.length, BYTELEN, pval);
			// psl.length = bb.length;
			cmd(psl);
			/*
			 * if( reader instanceof InputStreamReader ) {
			 * System.out.println("erm"); System.out.flush(); }
			 */
			line = bufferedreader.readLine();
			// System.err.println(line);
		}

		return 0;
	}

	public int parse(final simlab.ByValue... func) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IOException, IllegalAccessException, InvocationTargetException {
		Reader oldreader = reader;
		BufferedReader oldbufferedreader = bufferedreader;

		if (data.buffer != 0 && (data.type == 8 || data.type == 9)) {
			byte[] bb = data.getByteArray(0, data.getByteLength());
			reader = new InputStreamReader(new ByteArrayInputStream(bb));
			bufferedreader = new BufferedReader(reader);
		}

		/*
		 * data.buffer = 0; data.type = 32; data.length = 0; if (sl.length > 0)
		 * { //Pointer lbr = new Pointer(sl.buffer); String path =
		 * sl.toString(); //lbr.getString(0);
		 * 
		 * //Pointer lbrs = new Pointer(func.buffer); //String funcs =
		 * lbrs.getString(0);
		 * 
		 * InputStream stream = null; if (path != null && !path.equals("this"))
		 * { try { URL url = new URL(path); stream = url.openStream(); } catch
		 * (MalformedURLException e) { e.printStackTrace(); } catch (IOException
		 * e) { e.printStackTrace(); }
		 * 
		 * if (stream == null) { File f = new File(path); if (f.exists()) { try
		 * { stream = new FileInputStream(f); } catch (FileNotFoundException e)
		 * { e.printStackTrace(); } } }
		 * 
		 * if (stream != null) { reader = new InputStreamReader(stream);
		 * bufferedreader = new BufferedReader( reader ); } } }
		 */

		data.buffer = 0;
		data.type = 0;
		data.length = 0;

		Method m;
		if (func.length == 0 || func[0].type == 0) {
			m = Simlab.class.getMethod("cmd", simlab.ByValue.class);
		} else if (func[0].type == Byte.MIN_VALUE) {
			m = (Method) objects.get((int) func[0].length);
		} else {
			final String name = func[0].getTheString();
			m = Simlab.class.getMethod(name, simlab.ByValue.class);
		}
		parse(m);

		reader = oldreader;
		bufferedreader = oldbufferedreader;

		return 0;
	}

	Simple engine = new Simple();
	
	interface SimComp {
		public void reload();
	}
	
	class TableComp extends JComponent implements SimComp {
		JScrollPane	scrollpane;
		JTable		table;
		String		name;
		int			r,c;
		simlab.ByValue	sl;
		
		public TableComp() {
			super();
		}
		
		public TableComp(String name, simlab.ByValue ptr, int w, int h) {
			this.name = name;
			this.sl = ptr;
			this.c = w;
			this.r = h;
			
			final ByteBuffer bb = sl.getByteBuffer(); 
			table = new JTable();
			table.setAutoCreateRowSorter( true );
			table.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );
			table.setColumnSelectionAllowed( true );
			table.setModel( new TableModel() {
				@Override
				public int getRowCount() {
					return r;
				}

				@Override
				public int getColumnCount() {
					return c;
				}

				@Override
				public String getColumnName(int columnIndex) {
					return Integer.toString(columnIndex);
				}

				@Override
				public Class<?> getColumnClass(int columnIndex) {
					if( sl.type == DOUBLELEN ) return Double.class;
					else if( sl.type == INTLEN ) return Integer.class;
		
					return String.class;
				}

				@Override
				public boolean isCellEditable(int rowIndex, int columnIndex) {
					return true;
				}

				@Override
				public Object getValueAt(int rowIndex, int columnIndex) {
					if( sl.type == DOUBLELEN ) return bb.getDouble( (int)((DOUBLELEN/8)*(rowIndex*getColumnCount() + columnIndex)) );
					else if( sl.type == INTLEN ) return bb.getInt( (int)((INTLEN/8)*(rowIndex*getColumnCount() + columnIndex)) );
					return null;
				}

				@Override
				public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
					if( sl.type == DOUBLELEN ) bb.putDouble( (int)((DOUBLELEN/8)*(rowIndex*getColumnCount() + columnIndex)), (Double)aValue );
					else if( sl.type == INTLEN ) bb.putInt( (int)((INTLEN/8)*(rowIndex*getColumnCount() + columnIndex)), (Integer)aValue );
				}

				@Override
				public void addTableModelListener(TableModelListener l) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void removeTableModelListener(TableModelListener l) {
					// TODO Auto-generated method stub
					
				}
				
			});
			scrollpane = new JScrollPane( table );
			
			this.setLayout( new BorderLayout() );
			this.add( scrollpane );
		}

		public void paintComponent(Graphics g) {
			super.paintComponent(g);
		}

		public void reload() {
			table.tableChanged( new TableModelEvent( table.getModel() ) );
		}

		public void repaint() {
			super.repaint();
		}
	};
	
	class ChartComp extends JComponent implements SimComp {
		String name;
		private simlab.ByValue ptr;
		int w;
		int h;

		public ChartComp(String name, BufferedImage bi, simlab.ByValue ptr, int w, int h) {
			this.name = name;
			this.ptr = ptr;
			this.w = w;
			this.h = h;
		}

		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			Buffer b = ptr.getBuffer();
			if( b instanceof DoubleBuffer ) {
				DoubleBuffer db = (DoubleBuffer)b;
				//max()
				for( int i = 0; i < db.limit(); i++ ) {
					//max
				}
			}
		}

		public void reload() {
			int t = w * h;
			// int[] ib = ptr.getIntArray(0, t);
			//System.err.println("ptr" + ptr.length + "  " + ptr.type);
			IntBuffer ib = ptr.getByteBuffer().asIntBuffer();
			if (ib.limit() < t) {
				System.err.println("imgsize error " + ib.limit() + "  " + ptr.getByteLength());
			} else {
				for (int i = 0; i < t; i++) {
					// if( i%13 == 0 ) System.err.println( ib[i] );
					//bi.setRGB((int) (i % w), (int) (i / w), ib.get(i));
				}
				repaint();
			}
		}
	};

	class ImageComp extends JComponent implements SimComp {
		String name;
		BufferedImage bi;
		private simlab.ByValue ptr;
		int w;
		int h;

		public ImageComp(String name, BufferedImage bi, simlab.ByValue ptr, int w, int h) {
			this.name = name;
			this.bi = bi;
			this.ptr = ptr;
			this.w = w;
			this.h = h;
		}

		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g.drawImage(bi, 0, 0, this.getWidth(), this.getHeight(), this);
		}

		public void reload() {
			int t = w * h;
			// int[] ib = ptr.getIntArray(0, t);
			//System.err.println("ptr" + ptr.length + "  " + ptr.type);
			IntBuffer ib = ptr.getByteBuffer().asIntBuffer();
			if (ib.limit() < t) {
				System.err.println("imgsize error " + ib.limit() + "  " + ptr.getByteLength());
			} else {
				for (int i = 0; i < t; i++) {
					// if( i%13 == 0 ) System.err.println( ib[i] );
					bi.setRGB((int) (i % w), (int) (i / w), ib.get(i));
				}
				repaint();
			}
		}

		public void repaint() {
			super.repaint();
		}
	};

	Map<Long, Set<SimComp>> compmap = new HashMap<Long, Set<SimComp>>();

	public class Simple implements ScriptEngine {
		@Override
		public Bindings createBindings() {
			return null;
		}

		@Override
		public Object eval(String script) throws ScriptException {
			return null;
		}

		@Override
		public Object eval(Reader reader) throws ScriptException {
			Simlab.this.reader = reader;
			Simlab.this.bufferedreader = new BufferedReader(reader);
			try {
				Simlab.this.parse();
			} catch (IOException e) {
				e.printStackTrace();
			}
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
			return null;
		}

		@Override
		public Object eval(Reader reader, Bindings n) throws ScriptException {
			return null;
		}

		@Override
		public Object get(String key) {
			return null;
		}

		@Override
		public Bindings getBindings(int scope) {
			return null;
		}

		@Override
		public ScriptContext getContext() {
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
		Map<Point, String> map = new HashMap<Point, String>();
		Point p = new Point(0, 0);

		map.put(p, "simmi1");

		p.x = 1;

		map.put(p, "simmi2");

		for (Point pp : map.keySet()) {
			System.err.println(pp.hashCode());
			System.err.println(pp.x + "  " + pp.y);
			System.err.println(map.get(pp));
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

		/*
		 * ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
		 * List<ScriptEngineFactory> scriptEngineFactories =
		 * scriptEngineManager.getEngineFactories(); for( ScriptEngineFactory
		 * scriptEngineFactory : scriptEngineFactories ) { System.err.println(
		 * scriptEngineFactory.getEngineName() ); }
		 */

		Console console = System.console();
		Simlab simlab = new Simlab();
		simlab.init();
		simlab.welcome();
		simlab.jinit();
		ScriptEngine engine = simlab.getScriptEngine();

		boolean gui = false;
		for (String arg : args) {
			if (arg.equals("--gui")) {
				gui = true;
				break;
			}
		}

		try {
			if (gui) {
				PipedWriter pw = new PipedWriter();
				// BufferedWriter bw = new BufferedWriter( pw );
				// SimConsole simconsole =
				new SimConsole(pw);
				// ByteArrayOutputStream baos = new ByteArrayOutputStream();
				// InputStreamReader ir = new InputStreamReader(in);

				PipedReader pr = new PipedReader(pw);
				// BufferedReader br = new BufferedReader( pr );
				engine.eval(pr);
			}
			if (console == null || args.length > 0) {
				engine.eval(new InputStreamReader(System.in));
			} else {
				engine.eval(console.reader());
			}
		} catch (ScriptException e) {
			e.printStackTrace();
		} catch (IOException e) {
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
		return null;
	}

	@Override
	public List<String> getMimeTypes() {
		return null;
	}

	@Override
	public List<String> getNames() {
		return null;
	}

	@Override
	public String getOutputStatement(String toDisplay) {
		return null;
	}

	@Override
	public Object getParameter(String key) {
		return null;
	}

	@Override
	public String getProgram(String... statements) {
		return null;
	}

	@Override
	public ScriptEngine getScriptEngine() {
		return engine;
	}
}