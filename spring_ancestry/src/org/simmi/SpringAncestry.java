package org.simmi;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.swing.JApplet;
import javax.swing.JComponent;
import javax.swing.Timer;
import javax.swing.ToolTipManager;

import org.netbeans.saas.RestConnection;
import org.netbeans.saas.RestResponse;

public class SpringAncestry extends JApplet implements MouseListener, MouseMotionListener, KeyListener {
	//private final String 		apiKey = "718cf98c47efb1c0d8e90d555f935fe5";
	//private final String 		secret = "a577c7e1001e9b730c7615c08099e3a8";
	private final String apiKey = "9f0b87644ca81c8ce4b78288c08f1fe7";
	private final String secret = "57a58d9a6bd517c025ec94d4702a139a";

	private String 				result = null;
	private RelationMatrix		relationMatrix;
	private RelationMatrix		friendMatrix;
	byte[]						buffer = new byte[200000];
	JComponent					canvas;
	boolean						showing = false;
	Random 						rnd = new Random();
	User						selectedUser = null;
	double						horn = 0.0;
	boolean						showPhotos = false;
	
	static Map<String,String>	months;
	static Map<String,String>	days;
	
	static {
		months = new HashMap<String,String>();
		days = new HashMap<String,String>();
		
		months.put("January", "01");
		months.put("February", "02");
		months.put("March", "03");
		months.put("April", "04");
		months.put("May", "05");
		months.put("June", "06");
		months.put("July", "07");
		months.put("August", "08");
		months.put("September", "09");
		months.put("October", "10");
		months.put("November", "11");
		months.put("December", "12");
		
		days.put("1", "01");
		days.put("2", "02");
		days.put("3", "03");
		days.put("4", "04");
		days.put("5", "05");
		days.put("6", "06");
		days.put("7", "07");
		days.put("8", "08");
		days.put("9", "09");
		days.put("10", "10");
		days.put("11", "11");
		days.put("12", "12");
		days.put("13", "13");
		days.put("14", "14");
		days.put("15", "15");
		days.put("16", "16");
		days.put("17", "17");
		days.put("18", "18");
		days.put("19", "19");
		days.put("20", "20");
		days.put("21", "21");
		days.put("22", "22");
		days.put("23", "23");
		days.put("24", "24");
		days.put("25", "25");
		days.put("26", "26");
		days.put("27", "27");
		days.put("28", "28");
		days.put("29", "29");
		days.put("30", "30");
		days.put("31", "31");
		days.put("1,", "01");
		days.put("2,", "02");
		days.put("3,", "03");
		days.put("4,", "04");
		days.put("5,", "05");
		days.put("6,", "06");
		days.put("7,", "07");
		days.put("8,", "08");
		days.put("9,", "09");
		days.put("10,", "10");
		days.put("11,", "11");
		days.put("12,", "12");
		days.put("13,", "13");
		days.put("14,", "14");
		days.put("15,", "15");
		days.put("16,", "16");
		days.put("17,", "17");
		days.put("18,", "18");
		days.put("19,", "19");
		days.put("20,", "20");
		days.put("21,", "21");
		days.put("22,", "22");
		days.put("23,", "23");
		days.put("24,", "24");
		days.put("25,", "25");
		days.put("26,", "26");
		days.put("27,", "27");
		days.put("28,", "28");
		days.put("29,", "29");
		days.put("30,", "30");
		days.put("31,", "31");
	};
	
	private static String dateChange( String val ) {
		String ret = "";
		String[] split = val.split(" ");
		if( split.length > 1 ) {
			ret =  days.get(split[1])+months.get(split[0]);
			if( split.length > 2 ) {
				ret += split[2];
			}
		}
		return ret;
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch(Throwable th) {}
    }
	
	public SpringAncestry() {
		super();
	}
	
	private class RelationMatrix {
		int[]	vals;
		User[]	users;
		int		size;
		
		RelationMatrix( int size ) {
			this( new User[ size ] );
		}
		
		RelationMatrix( User[] users ) {
			this.size = users.length;
			this.vals = new int[ (size*(size-1))/2 ];
			this.users = users;
		}
		
		User getUser( int i ) {
			return users[i];
		}
		
		void setUser( int i, User user ) {
			users[i] = user;
		}
		
		void set( int i, int v ) {
			vals[i] = v;
		}
		
		void set( int c, int r, int v ) {
			if( r < c ) {
				int i = r*(size-1)-(r*(r+1))/2 + c - 1;
				if( i < vals.length ) vals[ i ] = v;
				//else System.err.println( r + "  " + c + "  " + i + "  " + size + "  " + vals.length );
			} else if( c < r ) {
				int i = c*(size-1)-(c*(c+1))/2 + r - 1;
				if( i < vals.length ) vals[ i ] = v;
				//else System.err.println( r + "  " + c + "  " + i + "  " + size + "  " + vals.length );
			}
		}
		
		int get( int i ) {
			return vals[i];
		}
		
		int get( int c, int r ) {
			if( r < c ) {
				int i = r*(size-1)-(r*(r+1))/2 + c - 1;
				if( i < vals.length ) return vals[ i ];
				//else System.err.println( r + "  " + c + "  " + i + "  " + size );
			} else if( c < r ) {
				int i = c*(size-1)-(c*(c+1))/2 + r - 1;
				if( i < vals.length ) return vals[ i ];
				//else System.err.println( r + "  " + c + "  " + i + "  " + size );
			}
			return -1;
		}
	};
	
	public Relative fetchRelative( int val ) {
		Relative rel = null;
		if( relMap.containsKey( val ) ) {
			rel = relMap.get( val );
		}
		
		if( rel == null ) {
			rel = new Relative();
			rel.id = val;
			
			relMap.put( val, rel );
		}
		return rel;
	}
	
	public void parseRelatives( InputStream in ) throws IOException {
		Reader r = new BufferedReader( new InputStreamReader(in) );
		StreamTokenizer st = new StreamTokenizer( r );
		
		Relative rel = null;
		st.nextToken();
		while( st.ttype != StreamTokenizer.TT_EOF ) {
			if( st.ttype == StreamTokenizer.TT_NUMBER ) {
				rel = fetchRelative( (int)st.nval );
				st.nextToken();
				Relative father = fetchRelative( (int)st.nval );
				st.nextToken();
				Relative mother = fetchRelative( (int)st.nval );
				
				rel.father = father;
				rel.mother = mother;
				
				father.children.add( rel );
				mother.children.add( rel );
				
				st.nextToken();
			} else {
				rel.name = st.sval;
				while( st.nextToken() == StreamTokenizer.TT_WORD ) {
					rel.name += " "+st.sval;
				}
			}
		}
	}
	
	Map<Integer,Relative>	relMap = new HashMap<Integer,Relative>();
	Relative[]				relArray;
	List<Relative>			relList = new ArrayList<Relative>();
	public class Relative extends JComponent {
		int				id;
		String			name;
		
		double			x;
		double			y;
		double			z;
		double			vx;
		double			vy;
		double			vz;
		
		public Relative() {
			x = 400.0*(rnd.nextDouble()-0.5);
			y = 400.0*(rnd.nextDouble()-0.5);
			z = 400.0*(rnd.nextDouble()-0.5);
			vx = 0.0;
			vy = 0.0;
			vz = 0.0;
			
			children = new HashSet<Relative>();
		}
		
		final Color c1 = new Color( 200,200,255 );
		final Color c2 = new Color( 100,100,255 );
		
		public void paintComponent( Graphics g ) {
			super.paintComponent( g );
			
			/*if( name.startsWith("Sigmar") || name.startsWith("Sigrún") || name.startsWith("Guðrún") ) {
				g.setColor( Color.red );
				g.fillOval( 0, 0, this.getWidth()-1, this.getHeight()-1 );
			} else {*/
				g.setColor( c1 );
				g.fillOval( 0, 0, this.getWidth()-1, this.getHeight()-1 );
			//}
			g.setColor( c2 );
			g.drawOval( 0, 0, this.getWidth()-2, this.getHeight()-2 );
		}
		
		public boolean isVisible() {
			return super.isVisible() && showing;
		}
		
		public boolean isShowing() {
			return super.isShowing() && showing;
		}
		
		Relative		father;
		Relative		mother;
		Set<Relative>	children;
	};
	
	public class User extends JComponent implements MouseListener {
		String				id;
		String				name;
		String				birthday;
		BufferedImage		img;
		double				x;
		double				y;
		double				z;
		double				vx;
		double				vy;
		double				vz;
		double				prox;
		Set<User>			friends = new HashSet<User>();
		
		final Color[] cc = { new Color( 0,0,255 ), new Color( 50,50,255 ), new Color( 100,100,255 ), new Color( 150,150,255 ),new Color( 200,200,255 ) };
		
		public User() {
			super();
			this.addMouseListener( this );
		}
		
		public void paintComponent( Graphics g ) {
			super.paintComponent( g );
			
			/*if( name.startsWith("Sigmar") || name.startsWith("Sigrún") || name.startsWith("Guðrún") ) {
				g.setColor( Color.red );
				g.fillOval( 0, 0, this.getWidth()-1, this.getHeight()-1 );
			} else {*/
			
			if( !showPhotos ) {
				double val = 10.0;
				int k = 0;
				while( prox > val ) {
					val *= 2.0;
					k++;
				}
				
				k = Math.min(cc.length-2, k);
				
				g.setColor( cc[k+1] );
				g.fillOval( 0, 0, this.getWidth()-1, this.getHeight()-1 );
				g.setColor( cc[k] );
				g.drawOval( 0, 0, this.getWidth()-2, this.getHeight()-2 );
			}
		}
		
		public boolean isVisible() {
			return super.isVisible() && showing;
		}
		
		public boolean isShowing() {
			return super.isShowing() && showing;
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mousePressed(MouseEvent e) {
			selectedUser = this;
			horn = 0.1;
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
	};
	
	public String sign(String[][] params) throws IOException {
        return sign( secret, params);
    }
	
	private static String sign(String secret, String[][] params) throws IOException{
        try {
            TreeMap<String, String> map = new TreeMap<String, String>();

            for (int i = 0; i < params.length; i++) {
                String key = params[i][0];
                String value = params[i][1];

                if (value != null) {
                    map.put(key, value);  
                }
            }

            String signature = "";
            Set<Map.Entry<String, String>> entrySet = map.entrySet();
            for (Map.Entry<String, String> entry : entrySet) {
                signature += entry.getKey() + "=" + entry.getValue();
            }

            signature += secret;

            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] sum = md.digest(signature.getBytes("UTF-8"));
            BigInteger bigInt = new BigInteger(1, sum);

            return bigInt.toString(16);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }
	
	private String getUserInfo( String uids, String fields ) {
		String format = null;
		
		String v = "1.0";
        String method = "facebook.users.getInfo";
        //FacebookSocialNetworkingServiceAuthenticator.login();
        String callId = String.valueOf(System.currentTimeMillis());
        String sessionKey = this.getParameter("fb_sig_session_key");
        String sig;
        RestResponse rr = null;
		try {
			sig = sign( new String[][]{{"api_key", apiKey}, {"session_key", sessionKey}, {"call_id", callId}, {"v", v},  {"uids", uids}, {"fields", fields}, {"format", format}, {"method", method}});
			String[][] pathParams = new String[][]{};
	        String[][] queryParams = new String[][]{{"api_key", "" + apiKey + ""}, {"session_key", sessionKey}, {"call_id", callId}, {"sig", sig}, {"v", v},  {"uids", uids}, {"fields", fields}, {"format", format}, {"method", method}};
	        RestConnection conn = new RestConnection("http://api.facebook.com/restserver.php", pathParams, queryParams);
	        sleep(1000);
	        rr = conn.get(null);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return rr.getDataAsString();
	}
	
	public void rekja() {
		
	}
	
	public String relCall( String xml ) {
		System.err.println( "relcall " + xml.substring(0, 50) );
		URL 		url = null;
		try {
			//url = new URL( "https://localhost:5001/IAdmin/AdminFB.jsp" );
			//url = new URL( "https://islendingabok.is/IAdmin/AdminFB.jsp" );
			url = new URL( "http://fb.decode.is/fbredir/index.php" );
			URLConnection connection = url.openConnection();
			if( connection instanceof HttpURLConnection ) {
				HttpURLConnection httpConnection = (HttpURLConnection)connection;
				if( connection instanceof HttpsURLConnection ) {
					HttpsURLConnection httpsConnection = (HttpsURLConnection)connection;
					
					TrustManager[] trustAllCerts = new TrustManager[]{ new X509TrustManager() {
							public java.security.cert.X509Certificate[] getAcceptedIssuers() {
								return null;
							}
		
							public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
							}
		
							public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
							}
						}
					};
					SSLContext sc = SSLContext.getInstance("SSL");
					sc.init(null, trustAllCerts, new java.security.SecureRandom());
					
					httpsConnection.setHostnameVerifier( new HostnameVerifier() {
						public boolean verify(String rserver, SSLSession sses) {
							if (!rserver.equals(sses.getPeerHost())){
								System.out.println( "certificate does not match host but continuing anyway" );
							}
							return true;
						}
					});
					
					String encoding = new sun.misc.BASE64Encoder().encode("thordur:latrar".getBytes());
			        httpsConnection.setRequestProperty ("Authorization", "Basic " + encoding);
			           
					httpsConnection.setSSLSocketFactory( sc.getSocketFactory() );
				}
				httpConnection.setRequestMethod("POST");
				httpConnection.setDoInput( true );
				httpConnection.setDoOutput( true );
				String uenc = "fbxml="+URLEncoder.encode( xml, "ISO-8859-1" );
				httpConnection.getOutputStream().write( uenc.getBytes() );
				
				int total = 0;
				InputStream inputStream = httpConnection.getInputStream();
				int r = inputStream.read( buffer );
				while( r != -1 ) {
					total += r;
					r = inputStream.read( buffer, total, buffer.length-total );
				}
				
				if( total > 0 ) {
					System.err.println("woah!");
					return new String( buffer, 0, total );
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public void iterGraph() {
		double damp = 0.95;
		double u = 10000.0;
		double kf = 0.0000001;
		cx = 0.0;
		cy = 0.0;
		cz = 0.0;
		
		for( Relative rel1 : relArray ) {
			double fx = 0;
			double fy = 0;
			double fz = 0;
			double bound = 0;
			
			/*for( Relative rel2 : relList ) {
				double dx = (rel2.x-rel1.x);
				double dy = (rel2.y-rel1.y);
				double dz = (rel2.z-rel1.z);
				
				double r = Math.sqrt( dx*dx + dy*dy + dz*dz );
				double r3 = r*r*r;
				fx -= u*dx/r3;
				fy -= u*dy/r3;
				fz -= u*dz/r3;
			}*/
		
			double gorm = 10.0;
			for( Relative rel2 : rel1.children ) {
				double dx = (rel2.x-rel1.x);
				double dy = (rel2.y-rel1.y);
				double dz = (rel2.z-rel1.z);
				
				double r = Math.sqrt( dx*dx + dy*dy + dz*dz );
				double r3 = r*r*r;
				fx -= u*dx/r3;
				fy -= u*dy/r3;
				fz -= u*dz/r3;
				
				double rel = 1.0;
				bound += rel;
				double k = 0.0;
				if( rel > 0 ) {
					double ival = 1.0/rel;
					double rval = Math.exp( 5.0*(Math.exp( ival )-1.0) );
					k = kf*( rval );
				}
				//double k = 0.01;
				
				fx += k*(dx-gorm);
				fy += k*(dy-gorm);
				fz += k*(dz-gorm);
			}
			
			if( rel1.id > 0 ) {
				double dx = (rel1.father.x-rel1.x);
				double dy = (rel1.father.y-rel1.y);
				double dz = (rel1.father.z-rel1.z);
				
				double r = Math.sqrt( dx*dx + dy*dy + dz*dz );
				double r3 = r*r*r;
				fx -= u*dx/r3;
				fy -= u*dy/r3;
				fz -= u*dz/r3;
				
				double rel = 1.0;
				bound += rel;
				double k = 0.0;
				if( rel > 0 ) {
					double ival = 1.0/rel;
					double rval = Math.exp( 5.0*(Math.exp( ival )-1.0) );
					k = kf*( rval );
				}
				
				fx += k*(dx-gorm);
				fy += k*(dy-gorm);
				fz += k*(dz-gorm);
				
				dx = (rel1.mother.x-rel1.x);
				dy = (rel1.mother.y-rel1.y);
				dz = (rel1.mother.z-rel1.z);
				
				r = Math.sqrt( dx*dx + dy*dy + dz*dz );
				r3 = r*r*r;
				fx -= u*dx/r3;
				fy -= u*dy/r3;
				fz -= u*dz/r3;
				
				rel = 1.0;
				bound += rel;
				k = 0.0;
				if( rel > 0 ) {
					double ival = 1.0/rel;
					double rval = Math.exp( 5.0*(Math.exp( ival )-1.0) );
					k = kf*( rval );
				}
				
				fx += k*(dx-gorm);
				fy += k*(dy-gorm);
				fz += k*(dz-gorm);
			
				rel1.vx = (rel1.vx+fx)*damp;
				rel1.vy = (rel1.vy+fy)*damp;
				rel1.vz = (rel1.vz+fz)*damp;
			
				rel1.x += rel1.vx;
				rel1.y += rel1.vy;
				rel1.z += rel1.vz;
				
				if( bound > 0 ) {
					cx += rel1.x;
					cy += rel1.y;
					cz += rel1.z;
				}
			} else {
				rel1.vx = 0.0;
				rel1.vy = 0.0;
				rel1.vz = 0.0;
			
				rel1.x = 0.0;
				rel1.y = 0.0;
				rel1.z = 0.0;
			}
		}
		
		int len = relMap.size();
		cx /= len;
		cy /= len;
		cz /= len;
	}
	
	public void prepareGraph() {
		double damp = 0.99;
		double u = 10.0;
		cx = 0.0;
		cy = 0.0;
		cz = 0.0;
		
		for( int i = 0; i < 100; i++ ) {
			int bb = 0;
			for( Relative rel1 : relArray ) {
				double fx = 0;
				double fy = 0;
				double fz = 0;
				double bound = 0;
				
				if( bb++ % 10 == 0 ) System.err.println( "uff " + bb ); 
				
				for( Relative rel2 : relArray ) {						
					double dx = (rel2.x-rel1.x);
					double dy = (rel2.y-rel1.y);
					double dz = (rel2.z-rel1.z);
					
					double r = Math.sqrt( dx*dx + dy*dy + dz*dz );
					double r3 = r*r*r;
					fx -= u*dx/r3;
					fy -= u*dy/r3;
					fz -= u*dz/r3;
				}
				
				for( Relative rel2 : rel1.children ) {
					double dx = (rel2.x-rel1.x);
					double dy = (rel2.y-rel1.y);
					double dz = (rel2.z-rel1.z);
					//double rel = relationMatrix.get(y, i);
					double rel = 1.0;
					bound += rel;
					double k = 0.0;
					if( rel > 0 ) {
						double ival = 1.0/rel;
						double rval = Math.exp( 5.0*(Math.exp( ival )-1.0) );
						k = 0.00001*( rval );
					}
					
					fx += k*dx;
					fy += k*dy;
					fz += k*dz;
				}
				
				if( rel1.id > 0 ) {
					double dx = (rel1.father.x-rel1.x);
					double dy = (rel1.father.y-rel1.y);
					double dz = (rel1.father.z-rel1.z);
					
					double r = Math.sqrt( dx*dx + dy*dy + dz*dz );
					double r3 = r*r*r;
					fx -= u*dx/r3;
					fy -= u*dy/r3;
					fz -= u*dz/r3;
					
					double rel = 1.0;
					bound += rel;
					double k = 0.0;
					if( rel > 0 ) {
						double ival = 1.0/rel;
						double rval = Math.exp( 5.0*(Math.exp( ival )-1.0) );
						k = 0.0000001*( rval );
					}
					
					fx += k*dx;
					fy += k*dy;
					fz += k*dz;
					
					dx = (rel1.mother.x-rel1.x);
					dy = (rel1.mother.y-rel1.y);
					dz = (rel1.mother.z-rel1.z);
					
					r = Math.sqrt( dx*dx + dy*dy + dz*dz );
					r3 = r*r*r;
					fx -= u*dx/r3;
					fy -= u*dy/r3;
					fz -= u*dz/r3;
					
					rel = 1.0;
					bound += rel;
					k = 0.0;
					if( rel > 0 ) {
						double ival = 1.0/rel;
						double rval = Math.exp( 5.0*(Math.exp( ival )-1.0) );
						k = 0.0000001*( rval );
					}
					
					fx += k*dx;
					fy += k*dy;
					fz += k*dz;
				
					rel1.vx = (rel1.vx+fx)*damp;
					rel1.vy = (rel1.vy+fy)*damp;
					rel1.vz = (rel1.vz+fz)*damp;
				
					rel1.x += rel1.vx;
					rel1.y += rel1.vy;
					rel1.z += rel1.vz;
					
					if( bound > 0 ) {
						cx += rel1.x;
						cy += rel1.y;
						cz += rel1.z;
					}
				} else {
					rel1.vx = 0.0;
					rel1.vy = 0.0;
					rel1.vz = 0.0;
				
					rel1.x = 0.0;
					rel1.y = 0.0;
					rel1.z = 0.0;
				}
			}
			
			int len = relMap.size();
			cx /= len;
			cy /= len;
			cz /= len;
		}
	}
	
	public void writeZipResults( final File f ) {		
		Thread t = new Thread() {
			public void run() {
				try {
					final Map<String,Map<String,Integer>>	umap = new HashMap<String,Map<String,Integer>>();
			    	
					FileOutputStream fout = new FileOutputStream( f );
					ZipOutputStream	zout = new ZipOutputStream( fout );
					
					User[]	users = relationMatrix.users;
					for( int i = 0; i < users.length-1; i++ ) {
						User u = users[i];
						String xml = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n";
						xml += "<facebook-relation>\n";
						if( u == null || u.name == null || u.id == null || u.birthday == null ) {
							System.err.println( u + "  bleh " + u.birthday );
						}
						String uname = new String( u.name.getBytes(), "ISO-8859-1" );
						xml += "<facebook-user id=\""+u.id+"\" name=\""+uname+"\" dob=\""+u.birthday+"\" />\n";
						for( int y = i+1; y < users.length; y++ ) {
							User u2 = users[y];
							String u2name = new String( u2.name.getBytes(), "ISO-8859-1" );
							xml += "<facebook-friend id=\""+u2.id+"\" name=\""+u2name+"\" dob=\""+u2.birthday+"\" />\n";
						}
						xml += "</facebook-relation>\n";
						String stuff = relCall( xml );
						
						Map<String,Integer>	currentMap = new HashMap<String,Integer>();
						
		        		//newUser( stuff, i, rnd, currentMap, u );
		        		//umap.put( u.name, currentMap );
						
						ZipEntry ze = new ZipEntry( u.id+".xml" );
						zout.putNextEntry( ze );
						zout.write( stuff.getBytes() );
						zout.closeEntry();
					}
					
					User user = users[users.length-1];
					user.x = 400.0*(rnd.nextDouble()-0.5);
					user.y = 400.0*(rnd.nextDouble()-0.5);
					user.z = 400.0*(rnd.nextDouble()-0.5);
					user.vx = 0;
					user.vy = 0;
					user.vz = 0;
					
					/*Set<User>	usersLeft = new HashSet<User>();
					usersLeft.addAll( Arrays.asList(users) );
					while( usersLeft.size() > 0 ) {
						for( User u : usersLeft ) {
							if( u.img == null ) Thread.sleep(100);
							else {
								usersLeft.remove( u );
								ZipEntry ze = new ZipEntry( u.id+".png" );
								zout.putNextEntry( ze );
								ImageIO.write( u.img, "png", zout);
								zout.closeEntry();
							}
						}
					}*/
					
					/*for( int i = 0; i < users.length; i++ ) {
						User u = users[i];
						ZipEntry ze = new ZipEntry( u.id+".png" );
						zout.putNextEntry( ze );
						while( u.img == null ) {
							Thread.sleep(100);
						}
						ImageIO.write( u.img, "png", zout );
						//zout.write( stuff.getBytes() );
						zout.closeEntry();
					}*/
					
					System.err.println("closing zip");
					zout.close();
					fout.close();
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} /*catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
			}
		};
		t.run();
	}
	
	public void readZipResults(final File f) {
		final Map<String,Map<String,Integer>>	umap = new HashMap<String,Map<String,Integer>>();
		final Map<String,User>	missMap = new HashMap<String,User>();
		
		final Map<String,BufferedImage>	imgMap = new HashMap<String,BufferedImage>();
		
		int total = -1;
    	int i = 0;
    	int count = 0;
    	try {
        	ZipFile	zfile = new ZipFile( f );
        	Enumeration<? extends ZipEntry>	entries = zfile.entries();
        	while( entries.hasMoreElements() ) {
        		ZipEntry entry = entries.nextElement();
        		if( entry.getName().endsWith(".xml") ) {
	        		InputStream in = zfile.getInputStream( entry );
	        		int totalread = 0;
	        		int r = in.read( buffer );
	        		while( r != -1 ) {
	        			totalread += r;
	        			r = in.read( buffer, totalread, buffer.length-totalread );
	        		}
	        		String xml = new String( buffer, 0, totalread );
	        		
	        		if( total == -1 ) {
	        			String[] lines = xml.split("\n");
	        			
	        			total = 1;
	        			
	        			for( String line : lines ) {
	        				if( line.contains("<facebook-friend") ) {
	        					int idStart = line.indexOf( "id=\"" )+4;
	        	        		int idStop = line.indexOf( "\"", idStart );
	        	        		String id = line.substring( idStart, idStop );
	        	        		
	        	        		int nameStart = line.indexOf( "name=\"" )+6;
	        	        		int nameStop = line.indexOf( "\"", nameStart );
	        	        		String name = line.substring( nameStart, nameStop );
	        	        		
	        	        		int dobStart = line.indexOf( "dob=\"" )+4;
	        	        		int dobStop = line.indexOf( "\"", dobStart );
	        	        		String dob = line.substring( dobStart, dobStop );
	        					
	        	        		User suser = new User();
	        	        		suser.id = id;
	        	        		suser.name = name;
	        	        		suser.birthday = dateChange(dob);
	        	        		
	        					missMap.put(name, suser);
	        	        		
	        					total++;
	        				}
	        			}
	        			relationMatrix = new RelationMatrix( total );
	        			//friendMatrix = new RelationMatrix( total );
	        		}
	        		
	        		Map<String,Integer>	currentMap = new HashMap<String,Integer>();
	        		User user = newUser( xml, count, rnd, currentMap, null );
	        		umap.put( user.name, currentMap );
	        		
	        		i+=--total;
	        		count++;
        		} else if( entry.getName().endsWith(".png") ) {
        			String[] kspl = entry.getName().split("\\.");
        			BufferedImage img = ImageIO.read( zfile.getInputStream( entry ) );
        			imgMap.put(kspl[0], img);
        		}
        	}
    	} catch( IOException e ) {
    		e.printStackTrace();
    	}
    	
    	for( String name : umap.keySet() ) {
    		missMap.remove( name );
    	}
    
    	for( String name : missMap.keySet() ) {
    		relationMatrix.users[count++] = missMap.get( name );
		}
    	
    	try {
	    	for( i = 0; i < relationMatrix.users.length; i++ ) {
	    		User u = relationMatrix.users[i];
	    		if( imgMap.containsKey(u.id) ) {
	    			u.img = imgMap.get(u.id);
	    		} else u.img = new BufferedImage(1,1,BufferedImage.TYPE_INT_ARGB);
	    		
	    		if( u.id != null && u.id.length() > 0 ) {
		    		File tf = new File( f.getParent(), u.id+".png" );
		    		if( !tf.exists() ) {
			    		FileOutputStream fos = new FileOutputStream( tf );
						ImageIO.write(u.img, "png", fos );
						fos.close();
		    		}
		    		
		    		String tt = "<html><center><img src=\""+tf.toURI().toURL()+"\"/><br>"+u.name+"</center></html>";
		    		//System.err.println( tt );
					u.setToolTipText( tt );
	    		} else {
	    			System.err.println( u.name );
	    			u.setToolTipText( u.name );
	    		}
	    		canvas.add( u );
	    	}
    	} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	for( int y = 0; y < relationMatrix.users.length-1; y++ ) {
    		User uy = relationMatrix.users[y];
    		Map<String,Integer>	nmap = umap.get(uy.name);
    		for( int x = y+1; x < relationMatrix.users.length; x++ ) {
    			User ux = relationMatrix.users[x];
    			if( nmap.containsKey( ux.name ) ) {
    				int v = nmap.get( ux.name );
					relationMatrix.set( x, y, v );
    			}
    		}
    	}
	}
	
	double hhx;
	double hhy;
	double hhz;
	double cx;
	double cy;
	double cz;
	boolean shift = false;
	int drawType = 2;
	public void init() {
		this.getContentPane().setBackground( Color.white );
		ToolTipManager.sharedInstance().setInitialDelay(0);
		
		canvas = new JComponent() {
			public void paintComponent( Graphics g ) {
				super.paintComponent( g );
				
				Graphics2D	g2 = (Graphics2D)g;
				g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
				g2.setRenderingHint( RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY );
				
				if( drawType == 0 ) {
					int w = this.getWidth();
					int h = this.getHeight();
					int size = relationMatrix.size;
					for( int r = 0; r < size; r++ ) {
						for( int c = 0; c < size; c++ ) {
							//if( c == r ) {
							g.drawRect( (c*w)/size, (r*h)/size, w/size, h/size );
							g.drawString( Integer.toString( relationMatrix.get(c, r) ), (c*w)/size+5, ((r+1)*h)/size-2 );
							//}
						}
					}
				} else if( drawType == 1 ) {
					showing = false;
					
					iterGraph();
					
					double hx = hhx;
					double hy = hhy;
					double hz = hhz;
					
					double d = 400.0;
					double zval = d;
					double mval = d * 1.5;
					
					if (np != null && p != null) {
						if (!shift)
							hy += (np.x - p.x) / 100.0;
						hx += (np.y - p.y) / 100.0;
						if (shift)
							hz += (np.x - p.x) / 100.0;
					}

					double cosx = Math.cos(hx);
					double sinx = Math.sin(hx);
					double cosy = Math.cos(hy);
					double siny = Math.sin(hy);
					double cosz = Math.cos(hz);
					double sinz = Math.sin(hz);
					
					for( Relative rel : relList ) {
						double xx = (rel.x - cx) * cosy - (rel.z - cz) * siny;
						double yy = (rel.y - cy);
						double zz = (rel.x - cx) * siny + (rel.z - cz) * cosy;

						double nx = xx;
						double ny = yy * cosx - zz * sinx;
						double nz = yy * sinx + zz * cosx; // cz;

						double lx = nx * cosz + ny * sinz;
						double ly = ny * cosz - nx * sinz;
						double lz = nz + 400.0;

						double dz = lz;
						double dx = (lx * mval) / (zval + dz);
						double dy = (ly * mval) / (zval + dz);

						int size = (int) (d * 20 / (zval + dz));
						
						int x = (int)dx+this.getWidth()/2;
						int y = (int)dy+this.getHeight()/2;
						rel.setBounds( x, y, size, size );
					}
					showing = true;
				} else {
					if( relationMatrix != null ) {
						showing = false;
						double damp = 0.95;
						double u = 1000.0;
						
						//int 	rsize = relationMatrix.size+1;
						//int		uval = 0;
						
						cx = 0.0;
						cy = 0.0;
						cz = 0.0;
						
						/*if( selectedUser != null ) {
							User ui = selectedUser;
							ui.x = 0;
							ui.y = 0;
							ui.z = 0;
							
							/*double hz = Math.atan2(ui.x, ui.y);
							double hy = Math.atan2( ui.x, ui.y );
							//if( hy < 0 ) hy += Math.PI;
							//else hy -= Math.PI;
							//if( hy > Math.PI ) hy -= Math.PI;
							//else if( hy < -Math.PI ) hy += Math.PI;
							//double hy = -Math.atan2( Math.sqrt( ui.x*ui.x+ui.y*ui.y ), ui.z );
							
							//double val = 0.05;
							if( hhy > hy+horn ) {
								double d = hhy - horn + Math.PI;
								double h = Math.floor( d / (2.0*Math.PI) );
								double nd = d - h * (2.0*Math.PI) - Math.PI;
								
								hhy = nd;
							} else if( hhy < hy-horn ) {		
								double d = hhy + horn + Math.PI;
								double h = Math.floor( d / (2.0*Math.PI) );
								double nd = d - h * (2.0*Math.PI) - Math.PI;
								
								hhy = nd;
							} else {
								horn /= 2.0;
							}
							/*if( hhy > hy+val ) hhy -= val;
							else if( hhy < hy-val ) hhy += val;
							else k++;
							if( hhz > hz+val ) hhz -= val;
							else if( hhz < hz-val ) hhz +=val;
							else k++;*/
							
							//if( horn < 0.005 ) selectedUser = null;
							
							/*double cosx = Math.cos(-hhx);
							double sinx = Math.sin(-hhx);
							double cosy = Math.cos(-hhy);
							double siny = Math.sin(-hhy);
							double cosz = Math.cos(-hhz);
							double sinz = Math.sin(-hhz);
								
							double lx = (0.0 - cx) * cosz + (0.0 - cy) * sinz;
							double ly = (0.0 - cy) * cosz - (0.0 - cx) * sinz;
							double lz = (-400.0 - cz);
							
							double nx = lx;
							double ny = ly * cosy - lz * siny;
							double nz = ly * siny + lz * cosy; // cz;
		
							double xx = nx * cosx - nz * sinx;
							double yy = ny;
							double zz = nx * sinx + nz * cosx;
		
							double vz = zz;//lz;
							double vx = xx;//(lx * mval) / (zval + dz);
							double vy = yy;//(ly * mval) / (zval + dz);
								
							double dx = (vx-ui.x);
							double dy = (vy-ui.y);
							double dz = (vz-ui.z);
							
							double rel = 1.0;
							//double bound = rel;
							double k = 0.0;
							if( rel > 0 ) {
								double ival = 1.0/rel;
								double rval = Math.exp( 5.0*(Math.exp( ival )-1.0) );
								k = 0.0000001*( rval );
							}
							//if( uval++ % rsize == 0 ) System.err.println( rval + "   " + rel + "   " + ival );
							
							double fx = k*dx;
							double fy = k*dy;
							double fz = k*dz;
					
							ui.vx = (ui.vx+fx)*damp;
							ui.vy = (ui.vy+fy)*damp;
							ui.vz = (ui.vz+fz)*damp;*/
						//}
						
						User[]	users = relationMatrix.users;
						for( int i = 0; i < users.length; i++ ) {
							User ui = users[i];
							
							//if( ui == selectedUser ) {								
								double dx = (-ui.x);
								double dy = (-ui.y);
								double dz = (-ui.z);
								
							/*	ui.vx = (ui.vx+0.00001*dx)*damp;
								ui.vy = (ui.vy+0.00001*dy)*damp;
								ui.vz = (ui.vz+0.00001*dz)*damp;*/
							//}
							
							double fx = 0;
							double fy = 0;
							double fz = 0;
							double bound = 0;
							ui.prox = Double.MAX_VALUE;
							for( int y = 0; y < users.length; y++ ) {
								if( y != i ) {
									User uy = users[y];
									dx = (uy.x-ui.x);
									dy = (uy.y-ui.y);
									dz = (uy.z-ui.z);
									
									double r = Math.sqrt( dx*dx + dy*dy + dz*dz );
									ui.prox = Math.min(ui.prox, r);
									
									double r3 = r*r*r;
									fx -= u*dx/r3;
									fy -= u*dy/r3;
									fz -= u*dz/r3;
									
									double rel = relationMatrix.get(y, i);
									//bound += rel;
									double k = 0.0;
									if( rel > 0 ) {
										double ival = 1.0/rel;
										double rval = (Math.exp( ival )-1.0);
										k = 0.001*( rval );
										//k = 0.00001*Math.max( 1, 10-k );
									}
									//if( uval++ % rsize == 0 ) System.err.println( rval + "   " + rel + "   " + ival );
									
									fx += k*dx;
									fy += k*dy;
									fz += k*dz;
								}
							}
							
							ui.vx = (ui.vx+fx)*damp;
							ui.vy = (ui.vy+fy)*damp;
							ui.vz = (ui.vz+fz)*damp;
							
							//System.err.println( ui.x + "  " + ui.y );
							//System.err.println( ui.vx + "  " + ui.vy );
							
							ui.x += ui.vx;
							ui.y += ui.vy;
							ui.z += ui.vz;
							
							/*if( bound > 0 ) {
								cx += ui.x;
								cy += ui.y;
								cz += ui.z;
							}*/
							
							//g.setColor( Color.red );
							//g.fillOval( (int)(ui.x)+this.getWidth()/2, (int)(ui.y)+this.getHeight()/2, 10, 10 );
						}
						
						//cx /= users.length;
						//cy /= users.length;
						//cz /= users.length;
						
						double hx = hhx;
						double hy = hhy;
						double hz = hhz;
						
						double d = 400.0;
						double zval = d;
						double mval = d * 1.5;
						
						if (np != null && p != null) {
							if (!shift)
								hy += (np.x - p.x) / 100.0;
							hx += (np.y - p.y) / 100.0;
							if (shift)
								hz += (np.x - p.x) / 100.0;
						}

						double cosx = Math.cos(hx);
						double sinx = Math.sin(hx);
						double cosy = Math.cos(hy);
						double siny = Math.sin(hy);
						double cosz = Math.cos(hz);
						double sinz = Math.sin(hz);
						
						for( int i = 0; i < users.length; i++ ) {
							User ui = users[i];
							
							double xx = (ui.x - cx) * cosy - (ui.z - cz) * siny;
							double yy = (ui.y - cy);
							double zz = (ui.x - cx) * siny + (ui.z - cz) * cosy;
		
							double nx = xx;
							double ny = yy * cosx - zz * sinx;
							double nz = yy * sinx + zz * cosx; // cz;
		
							double lx = nx * cosz + ny * sinz;
							double ly = ny * cosz - nx * sinz;
							double lz = nz + 400.0;
		
							double dz = lz;
							double dx = (lx * mval) / (zval + dz);
							double dy = (ly * mval) / (zval + dz);

							int size = (int) (d * 20 / (zval + dz));
							
							int x = (int)dx+this.getWidth()/2;
							int y = (int)dy+this.getHeight()/2;
							ui.setBounds( x, y, size, size );
							
							if( showPhotos ) g2.drawImage( ui.img, x-size, y-size, 2*size, 2*size, this );
						}
						showing = true;
					}
				}
			}
			
			public Point getToolTipLocation( MouseEvent me ) {
				return me.getPoint();
			}
			
			public String getToolTipText( MouseEvent me ) {
				int w = this.getWidth();
				int h = this.getHeight();
				if( relationMatrix != null ) {
					int x = (me.getX()*relationMatrix.size)/w;
					int y = (me.getY()*relationMatrix.size)/h;
					
					//System.err.println( x + "  " + y );
					return relationMatrix.users[x].name + "<br>" + relationMatrix.users[y].name;
				}
				return "";
			}
		};
		Timer	timer = new Timer( 100, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				canvas.repaint();
			}
		});
		timer.start();
		
		canvas.addMouseMotionListener( this );
		canvas.addMouseListener( this );
		canvas.addKeyListener( this );
		this.addKeyListener( this );
		//canvas.setToolTipText("sim");
		this.add( canvas );
			
		String homedict = System.getProperty("user.home");
		File homef = new File( homedict, ".springancestry" );
		if( !homef.exists() ) homef.mkdirs();
		if( drawType == 1 ) {
			File f = new File( homef, "stuff.txt" );
			FileInputStream fis;
			try {
				fis = new FileInputStream( f );
				parseRelatives( fis );
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			int i = 0;
			relArray = new Relative[ relMap.size() ];
			for( Integer id : relMap.keySet() ) {
				relArray[i++] = relMap.get( id );
			}
			
			for( Integer id : relMap.keySet() ) {
				if( id > 0 ) {
					Relative rel = relMap.get(id);
					//System.err.println( rel.name + "  " + rel.father.name + "  " + rel.mother.name );
					canvas.add( rel );
					rel.setToolTipText( rel.name );
					relList.add( rel );
				}
			}
			//prepareGraph();
		} else {
			String format = null;
			String flid = null;
			
			String version = "1.0";
	        String method = "facebook.friends.get";
	        //FacebookSocialNetworkingServiceAuthenticator.login();
	        String callId = String.valueOf(System.currentTimeMillis());
	        String sessionKey = this.getParameter("fb_sig_session_key");
	        String currentUser = this.getParameter("fb_sig_user");
	        
	        String fname = null;
	        String uinfo = null;
	        if( sessionKey != null && currentUser != null ) {
		        String sig;
		        RestResponse rr = null;
				try {
					sig = sign( new String[][]{{"api_key", apiKey}, {"session_key", sessionKey}, {"call_id", callId}, {"v", version}, {"format", format}, {"flid", flid}, {"method", method}});
					String[][] pathParams = new String[][]{};
			        String[][] queryParams = new String[][]{{"api_key", "" + apiKey + ""}, {"session_key", sessionKey}, {"call_id", callId}, {"sig", sig}, {"v", version}, {"format", format}, {"flid", flid}, {"method", method}};
			        RestConnection conn = new RestConnection("http://api.facebook.com/restserver.php", pathParams, queryParams);
			        sleep(1000);
			        rr = conn.get(null);
				} catch (IOException e) {
					e.printStackTrace();
				}
				String res = rr.getDataAsString();
				System.err.println( res );
				
				String uds = res.substring( res.indexOf("<uid>")+5 ).replace("</uid>\n  <uid>", ",");
				String uids = currentUser+","+uds.substring(0, uds.lastIndexOf(','));
				
				uinfo = getUserInfo(uids, "name,birthday,pic");
				byte[] sum = null;
				try {
					MessageDigest md = MessageDigest.getInstance("MD5");
					sum = md.digest(uinfo.getBytes("UTF-8"));
				} catch (NoSuchAlgorithmException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		        BigInteger bigInt = new BigInteger(1, sum);
		        fname = bigInt.toString(16);
	        } else {
	     		fname = "result";//"41e9ace889745ef4e576d2bdd5162543";
	     			//"result";//"fa99d6d596fc1733db8b75920a766e1a";
	     		
	     		File f = new File( homef, fname+".zip" );
	     		if( !f.exists() ) {
		     		File xml = new File( homef, "result.xml" );
	     			if( xml.exists() ) {
		     			try {
		     				List<User>	userList = new ArrayList<User>();
		     				
		     				int i = 0;
		     				BufferedReader	reader = new BufferedReader( new FileReader( xml ) );
		     				String line = reader.readLine();
		     				
		     				while( line != null ) {
		     					if( line.contains("facebook-user") || line.contains("facebook-friend") ) {
		     						User user = new User();
		     						userList.add( user );
		     						user.id = ++i+"";
		     						int ni1 = line.indexOf("name=\"")+6;
		     						int ni2 = line.indexOf("\"",ni1);
		     						user.name = line.substring(ni1, ni2);
		     						int di1 = line.indexOf("dob=\"")+5;
		     						int di2 = line.indexOf("\"",di1);
		     						user.birthday = line.substring(di1, di2);
		     					}
		     					line = reader.readLine();
		     				}
		     				reader.close();
		     				User[] users = userList.toArray( new User[0] );
		     				relationMatrix = new RelationMatrix( users );
		     				writeZipResults(f);
		     			} catch (FileNotFoundException e1) {
		     				// TODO Auto-generated catch block
		     				e1.printStackTrace();
		     			} catch (IOException e) {
		     				// TODO Auto-generated catch block
		     				e.printStackTrace();
		     			}
		     		}
	     		}
	        }
	        
	        final File f = new File( homef, fname+".zip" );
	        if( !f.exists() ) {
				String startTag = "<user>";
				String stopTag = "</user>";
				final String[] uval = uinfo.substring( uinfo.indexOf(startTag)+startTag.length(), uinfo.lastIndexOf(stopTag) ).split(stopTag+"\n  "+startTag);
				final User[] users = new User[ uval.length ];
				for( int i = 0; i < uval.length; i++ ) {
					final User user = new User();
					String xmling = uval[i];
					user.id = xmling.substring( xmling.indexOf("<uid>")+5, xmling.lastIndexOf("</uid>") );
					user.name = xmling.substring( xmling.indexOf("<name>")+6, xmling.lastIndexOf("</name>") );
					
					int start = xmling.indexOf("<pic>") + 5;
					int stop = xmling.indexOf("</pic>");
					if( stop != -1 ) {
						final String urlstr = xmling.substring( start, stop );
						
						Thread t = new Thread() {
							public void run() {
								try {
									System.err.println( "trying " + urlstr );
									URL url = new URL( urlstr );
									InputStream in = url.openStream();
									user.img = ImageIO.read( in );
									in.close();
								} catch (MalformedURLException e) {
									user.img = new BufferedImage( 1,1,BufferedImage.TYPE_4BYTE_ABGR );
									e.printStackTrace();
								} catch (IOException e) {
									user.img = new BufferedImage( 1,1,BufferedImage.TYPE_4BYTE_ABGR );
									e.printStackTrace();
								}
							}
						};
						t.start();
					} else {
						user.img = new BufferedImage( 1,1,BufferedImage.TYPE_4BYTE_ABGR ); 
					}
					
					start = xmling.indexOf("<birthday>") + 10;
					stop = xmling.indexOf("</birthday>");
					if( stop > start  && start > 0 ) {
						user.birthday = dateChange( xmling.substring( start, stop ) );
					} else {
						user.birthday = "";
						System.err.println( "hello" + i + xmling );
					}
					users[i] = user;
				}
				
				relationMatrix = new RelationMatrix( users );
				writeZipResults( f );
	        } else {
	        	readZipResults( f );
	        }
		}
	}
	
	public User newUser( String xml, int count, Random rnd, Map<String,Integer> currentMap, User user ) {
		int ind = xml.indexOf("<facebook-user");
		int idStart = xml.indexOf( "id=\"", ind )+4;
		int idStop = xml.indexOf( "\"", idStart );
		String id = xml.substring( idStart, idStop );
		
		int nameStart = xml.indexOf( "name=\"", ind )+6;
		int nameStop = xml.indexOf( "\"", nameStart );
		String name = xml.substring( nameStart, nameStop );
		
		int dobStart = xml.indexOf( "dob=\"", ind )+4;
		int dobStop = xml.indexOf( "\"", dobStart );
		String dob = xml.substring( dobStart, dobStop );
		
		if( user == null ) user = new User();
		user.id = id;
		user.name = name;
		user.birthday = dateChange(dob);
		user.x = 400.0*(rnd.nextDouble()-0.5);
		user.y = 400.0*(rnd.nextDouble()-0.5);
		user.z = 400.0*(rnd.nextDouble()-0.5);
		user.vx = 0;
		user.vy = 0;
		user.vz = 0;
		
		String[] lines = xml.split("\n");
		relationMatrix.users[count] = user;
		
		String relStr = "relation=\"";
		for( String line : lines ) {
			if( line.contains("<facebook-friend") ) {
				nameStart = line.indexOf( "name=\"", 0 )+6;
        		nameStop = line.indexOf( "\"", nameStart );
        		name = line.substring( nameStart, nameStop );
				
				int rel = line.indexOf(relStr)+relStr.length();
				int relEnd = line.indexOf("\"", rel);
				int v = Integer.parseInt( line.substring( rel, relEnd ) );
				
				currentMap.put( name, v );
			}
		}
		
		return user;
	}
	
	public void update( Graphics g ) {
		super.update( g );
	}
	
	public void paint( Graphics g ) {
		super.paint( g );
	}
	
	Point 	np;
	Point	p;
	
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		this.requestFocus();
		p = e.getPoint();
		np = p;
		shift = (e.getModifiersEx() & MouseEvent.SHIFT_DOWN_MASK) != 0;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (!shift) {
			double d = (np.x - p.x) / 100.0 + hhy + Math.PI;
			double h = Math.floor( d / (2.0*Math.PI) );
			double nd = d - h * (2.0*Math.PI) - Math.PI;
			hhy = nd; //(np.x - p.x) / 100.0 + hhy;
		} else {
			double d = (np.x - p.x) / 100.0 + hhz + Math.PI;
			double h = Math.floor( d / (2.0*Math.PI) );
			double nd = d - h * (2.0*Math.PI) - Math.PI;
			hhz = nd;
		}
		
		double d = (np.y - p.y) / 100.0 + hhx + Math.PI;
		double h = Math.floor( d / (2.0*Math.PI) );
		double nd = d - h * (2.0*Math.PI) - Math.PI;
		hhx = nd;
		
		p = null;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		np = e.getPoint();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if( KeyEvent.getModifiersExText( e.getModifiersEx() ).contains("Ctrl") ) showPhotos = true;
	}

	@Override
	public void keyReleased(KeyEvent e) {
		showPhotos = false;		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
}
