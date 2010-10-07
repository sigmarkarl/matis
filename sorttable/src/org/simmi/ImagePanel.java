package org.simmi;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JProgressBar;
import javax.swing.JTable;

public class ImagePanel extends JComponent {
	Image	img;
	final ByteBuffer	ba = ByteBuffer.allocate(1000000);
	final String startTag = "imgurl";
	Set<String>	imageNames;
	Map<String,Image>	imageCache;
	Map<String,String>	imageNameCache;
	JTable				leftTable;
	int					orientation;
	JProgressBar		progressbar;
	String				imgUrl = null;
	String				lang;
	
	public ImagePanel( final JTable leftTable, String lang ) {
		super();
		this.addMouseListener( new MouseAdapter() {
			public void mousePressed( MouseEvent e ) {
				int r = leftTable.getSelectedRow();
				//int rr = leftTable.convertRowIndexToModel(r);
				if( r >= 0 && r < leftTable.getRowCount() ) {
					Object obj = leftTable.getValueAt(r, 0);
					if( obj != null ) {
						String s = obj.toString();
						//System.err.println("hey " + s );
						
						if( !imageNameCache.containsKey(s) ) runThread( s );
					}
				}
			}
		});
		this.leftTable = leftTable;
		
		this.lang = lang;
		imageNames = new HashSet<String>();
		imageCache = new HashMap<String,Image>();
		imageNameCache = new HashMap<String,String>();
		 
		InputStream inputStream = this.getClass().getResourceAsStream("/myndir.txt");
		BufferedReader br = new BufferedReader( new InputStreamReader( inputStream ) );
		String line;
		try {
			line = br.readLine();
			while( line != null ) {
				imageNames.add( line );
				line = br.readLine();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		progressbar = new JProgressBar();
		progressbar.setVisible( false );
		progressbar.setIndeterminate( true );
		progressbar.setString(lang.equals("IS") ? "Sæki mynd" : "Fetch image");
		progressbar.setValue( 0 );
		progressbar.setStringPainted( true );
		this.add( progressbar );
	}
	
	public void setBounds( int x, int y, int w, int h ) {
		super.setBounds(x, y, w, h);
		
		Component[] cc = this.getComponents();
		Component c = null;
		for( Component tc : cc ) {
			if( tc instanceof JButton ) {
				c = tc;
				break;
			}
		}
		
		if( c != null ) {
			if( orientation == 0 ) {
				c.setLocation(9, 9);
			} else if( orientation == 1 ) {
				c.setLocation(this.getWidth()-41, 9);
			} else if( orientation == 2 ) {
				c.setLocation(this.getWidth()-41, this.getHeight()-41);
			} else {
				c.setLocation(9, this.getHeight()-41);
			}
		}
		progressbar.setBounds(this.getWidth()/2-75, this.getHeight()/2-10, 150, 20);
	}
	
	public void drawString( Graphics g, String str, int h ) {
		String[] split = str.split("\n");
		for( String s : split ) {
			int strw = g.getFontMetrics().stringWidth( s );
			g.drawString( s, (this.getWidth()-strw)/2, this.getHeight()/2+g.getFontMetrics().getHeight()*(h-split.length/2) );
			h++;
		}
	}
	
	public void paintComponent( Graphics g ) {
		super.paintComponent(g);
		
		Graphics2D	g2 = (Graphics2D)g;
		g2.setRenderingHint( RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY );
		g2.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
		if( img != null ) {
			int iw = img.getWidth(this);
			int ih = img.getHeight(this);
			int w = this.getWidth();
			int h = this.getHeight();
			
			if( w*ih > iw*h ) {
				int rw = (iw*h)/ih;
				g.drawImage(img, (w-rw)/2, 0, rw, this.getHeight(), this );
			} else {
				int rh = (ih*w)/iw;
				g.drawImage(img, 0, (h-rh)/2, this.getWidth(), rh, this );
			}
		} else if( !progressbar.isVisible() ) {
			boolean b = false;
			int r = leftTable.getSelectedRow();
			if( r >= 0 && r < leftTable.getRowCount() ) {
				Object obj = leftTable.getValueAt(r, 0);
				if( obj != null ) {
					String s = obj.toString();
					b = imageNameCache.containsKey( s );
				}
			}
			
			g.setColor( Color.lightGray );
			if( b ) {
				String str = lang.equals("IS") ? "Engin mynd" : "No image";
				drawString( g, str, 0 );
			} else {
				String str = lang.equals("IS") ? "Engin mynd\nSmelltu hér til að sækja mynd á google" : "No image\nClick here to fetch on google";
				drawString( g, str, 0 );
			}
		} else if( imgUrl != null ) {
			drawString( g, imgUrl, -1 );
		}
	}
	
	public void getImage( String val, int index ) throws IOException {
		//path = "http://test.matis.is/isgem/myndir/"+val;//URLEncoder.encode(iName,"UTF-8");		
		Image	image = null;
		if( imageCache.containsKey(val) ) {
			image = imageCache.get(val);
		} else {
			URL url = new URL( val );
			image = ImageIO.read(url);
			imageCache.put(val, image);
		}		
		//imageNameCache.put(oname, val);
		
		int sr = leftTable.getSelectedRow();
		if( index == sr ) {
			progressbar.setVisible( false );
			img = image;
			ImagePanel.this.repaint();
		} else {
			if( sr >= 0 && sr < leftTable.getRowCount() ) {
				Object obj = leftTable.getValueAt(sr, 0);
				if( obj != null ) {
					String s = (String)obj;
					if( val.equals(imageNameCache.get(s)) ) {
						progressbar.setVisible( false );
						img = image;
						ImagePanel.this.repaint();
					}
				}
			}
		}
	}
	
	Set<String>	vals = new HashSet<String>();
	Thread t = null;
	public void threadRun( final String val, final int index ) {
		if( !vals.contains( val ) ) {
			vals.add( val );
			
			imgUrl = val;
			ImagePanel.this.repaint();
			
			t = new Thread() {
				public void run() {					
					try {
						getImage( val, index );
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					vals.remove( val );
					if( vals.size() == 0 ) progressbar.setVisible( false );
					
					if( !imageCache.containsKey(val) ) imageCache.put(val, null);
				}
			};
			t.start();
			progressbar.setVisible( true );
		} else {
			imgUrl = val;
			progressbar.setVisible( true );
		}
	}

	public void tryName( String oName ) {
		if( imageNameCache.containsKey(oName) ) {
			String imgUrl = imageNameCache.get(oName);
			if( imageCache.containsKey(imgUrl) ) {
				Image image = imageCache.get(imgUrl);

				progressbar.setVisible( false );
				this.img = image;
			} else {
				this.imgUrl = imgUrl;
				progressbar.setVisible( true );
				img = null;
			}
		} else {			
			/*boolean b = true;
			try {
				URL url = new URL( oName );
			} catch( Exception e ) {
				b = false;
			}
			
			if( b ) {
				threadRun(oName,oName,leftTable.getSelectedRow());
			} else {*/
				//path = "http://test.matis.is/isgem/myndir/"+oName;
				
				String lName = oName.toLowerCase();
				String name = lName.replace('á', 'a');
				name = name.replace('ó', 'o');
				name = name.replace('ú', 'u');
				name = name.replace('ý', 'y');
				name = name.replace('í', 'i');
				name = name.replace('é', 'e');
				name = name.replace('ð', 'd');
				name = name.replace('þ', 't');
				name = name.replace("æ", "ae");
				
				List<String> oSpl = Arrays.asList( lName.split("[, ]+") );
				List<String> nSpl = Arrays.asList( name.split("[, ]+") );
				
				Set<String>	ign = new HashSet<String>();
				ign.add("hrar");
				ign.add("sodin");
				ign.add("sodinn");
				ign.add("jpg");
				ign.add("sosa");
				
				int max = 0;
				String val = null;
				for( String iName : imageNames ) {
					String[] spl = iName.toLowerCase().split("[\\._ 0123456789]+");
					
					int count = 0;
					for( String iStr : spl ) {
						if( !ign.contains(iStr) && (oSpl.contains(iStr) || nSpl.contains(iStr)) ) count++;
					}
					
					if( count > max ) {
						max = count;
						val = iName;
					}
				}
				
				if( val != null ) {
					String path = "http://test.matis.is/isgem/myndir/"+val;
					if( imageCache.containsKey(path) ) {
						img = imageCache.get(path);
						imageNameCache.put(oName, path);
						progressbar.setVisible( false );
					} else {
						imageNameCache.put(oName, path);
						threadRun(path,leftTable.getSelectedRow());
					}
				}
			//}
		}
	}
	
	public void runThread( final String str ) {
		if( imageNameCache.containsKey(str) ) {
			//String urlstr = imageNameCache.get(str);
			img = imageCache.get( str );
			ImagePanel.this.repaint();
		} else {
			final int imindex = leftTable.getSelectedRow();
			Thread t = new Thread() {
				public void run() {
					URL url;
					String urlstr = null;
					try {
						//url = new URL("http://localhost:5001/images?hl=en&q="+URLEncoder.encode(str, "UTF-8") );
						//url = new URL("http://search.live.com/images/results.aspx?q="+str);
						String vstr = str.replace(",", "");
						vstr = vstr.replace(' ', '+');
						vstr = URLEncoder.encode(vstr, "UTF-8");
						url = new URL("http://images.google.com/images?hl=en&q="+vstr ); //+"&btnG=Search+Images&gbv=2" ); //&btnG=Search+Images" );//hl=en&q=Orange");//+str);
						System.err.println( "searching for " + url.toString() );
						URLConnection connection = null;
						connection = url.openConnection();
						//Proxy proxy = new Proxy( Type.HTTP, new InetSocketAddress("proxy.decode.is",8080) );
						//connection = url.openConnection( proxy );
						//connection.setDoOutput( true );
						if( connection instanceof HttpURLConnection ) {
							HttpURLConnection httpConnection = (HttpURLConnection)connection;
							httpConnection.setRequestProperty("Host", "images.google.com" );
							httpConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.6) Gecko/2009020518 Ubuntu/9.04 (jaunty) Firefox/3.0.6" );
							httpConnection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,**;q=0.8" );
							httpConnection.setRequestProperty("Accept-Language", "en-us,en;q=0.5" );
							httpConnection.setRequestProperty("Accept-Encoding", "gzip,deflate" );
							httpConnection.setRequestProperty("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7" );
							httpConnection.setRequestProperty("Keep-Alive", "300" );
							httpConnection.setRequestProperty("Connection", "keep-alive" );
						}
						InputStream stream = connection.getInputStream();
						stream = new GZIPInputStream( stream );
						
						int total = 0;
						int read = stream.read(ba.array(), total, ba.limit()-total );
						total = 0;
						while( read > 0 ) {
							total += read;
							read = stream.read( ba.array(), total, ba.limit()-total );
						}
						stream.close();
						
						String result = new String( ba.array(), 0, total);
						int index = result.indexOf( startTag );
						int val = result.indexOf("http:", index); //index+startTag.length();
						
						int stop = result.indexOf( "\\x26", val );
						if( stop == -1 ) {
							stop = result.indexOf( '&', val );
						}
						
						urlstr = result.substring(val, val+20);
						if( stop != -1 ) {
							urlstr = result.substring( val, stop );
						}
						System.err.println( urlstr );
						
						/*while( index > 0 && (result.charAt(val) != 'h' || !(urlstr.endsWith("jpg") || urlstr.endsWith("png") || urlstr.endsWith("gif"))) ) {
							index = result.indexOf( startTag, val );
							val = index+startTag.length();
							stop = result.indexOf( '&', val );
							urlstr = result.substring( val, stop );
						}*/
						
						if( stop > 0 ) {
							urlstr = urlstr.replace("%20", " ").replace("%2520", " ");
							
							vals.add( urlstr );
							imgUrl = urlstr;
							ImagePanel.this.repaint();
							
							imageNameCache.put( str, urlstr );
							getImage( urlstr, imindex );
							/*url = new URL( urlstr );
							connection = url.openConnection();
							stream = connection.getInputStream();
							Image image = ImageIO.read(stream);
							imageCache.put(urlstr,image);
							imageNameCache.put( str, urlstr );
							
							if( imindex == leftTable.getSelectedRow() ) {
								progressbar.setVisible( false );
								img = image;
								ImagePanel.this.repaint();
							}*/
						}
					} catch (MalformedURLException e1) {
						e1.printStackTrace();
					} catch (IOException e2) {
						e2.printStackTrace();
					}
					
					if( urlstr != null ) {
						vals.remove( urlstr );
						if( !imageCache.containsKey( urlstr ) ) {
							imageCache.put( urlstr, null );
						}
					}
					if( vals.size() == 0 ) {
						progressbar.setVisible( false );
					}
					ImagePanel.this.repaint();
				}
			};
			t.start();
			imgUrl = null;
			progressbar.setVisible( true );
			this.repaint();
		}
	}
}
