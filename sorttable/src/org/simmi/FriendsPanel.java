package org.simmi;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.AccessController;
import java.security.MessageDigest;
import java.security.PrivilegedAction;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;

import org.netbeans.saas.RestConnection;
import org.netbeans.saas.RestResponse;

public class FriendsPanel extends SimSplitPane {
	private final String apiKey = "d8993947d6a37b4bf754d2a578025c31";
	private final String secret = "c9577f5b3a6c03abb63ebdadb39feea5";
	
	String currentUser;
	String currentUserId = "0";
	
	List<Object[]>	friendList = new ArrayList<Object[]>();
	JCompatTable table = new JCompatTable();
	
	ImageIcon	offIcon;
	ImageIcon	onIcon;
	
	TableModel	model;
	TableModel	nullmodel;
	
	String		sessionKey;
	String		lang;
	MyPanel		mypanel;
	
	public String sign(String[][] params) throws IOException {
        return sign( secret, params);
    }
	
	private String getToken() throws IOException {
        String token = null;
        String method = "facebook.auth.createToken";
        String v = "1.0";
        
        String sig = sign(secret,
                new String[][]{
                    {"method", method},
                    {"api_key", apiKey},
                    {"v", v}
                });

        RestConnection conn = new RestConnection(
                "http://api.facebook.com/restserver.php",
                new String[][]{
                    {"method", method},
                    {"api_key", apiKey},
                    {"sig", sig},
                    {"v", v}
                });
        String result = conn.get().getDataAsString();

        try {
            token = result.substring(result.indexOf("<auth_createToken_response"),
                    result.indexOf("</auth_createToken_response>"));
            token = token.substring(token.indexOf(">") + 1);
        } catch (Exception ex) {
            throw new IOException("Failed to get session token: " + result);
        }

        String loginUrl = "http://www.facebook.com/login.php?api_key=" +
                apiKey + "&v=" + v + "&auth_token=" + token;

        try {
			CompatUtilities.browse( new URI( loginUrl ) );
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
        /*if (JOptionPane.showInputDialog(null,
                "Please log into your Facebook account using the following URL to authorize this application and click OK after you are done:",
                "Facebook Authorization Dialog",
                JOptionPane.INFORMATION_MESSAGE,
                null,
                null,
                loginUrl) == null) {
            throw new IOException("Authorizatoin declined");
        }*/
		if( JOptionPane.showConfirmDialog(FriendsPanel.this, lang.equals("IS") ? "Vinsamlegast skráðu þig inná Facebook" : "Log in to Facebook", lang.equals("IS") ? "Facebook innskráning" : "Facebook login", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION ) return token;

        return null;
    }
	
	public void login() throws IOException {
        if (sessionKey == null) {
            String token = getToken();

            if( token != null ) {
	            String method = "facebook.auth.getSession";
	            String v = "1.0";
	
	            String sig = sign(secret,
	                    new String[][]{
	                        {"method", method},
	                        {"v", v},
	                        {"api_key", apiKey},
	                        {"auth_token", token}
	                    });
	
	            RestConnection conn = new RestConnection(
	                    "http://api.facebook.com/restserver.php",
	                    new String[][]{
	                        {"method", method},
	                        {"api_key", apiKey},
	                        {"sig", sig},
	                        {"v", v},
	                        {"auth_token", token}
	                    });
	
	            String result = conn.get().getDataAsString();
	
	            try {
	                sessionKey = result.substring(result.indexOf("<session_key>") + 13,
	                    result.indexOf("</session_key>"));
	
	                currentUserId = result.substring(result.indexOf("<uid>") + 5,
	                        result.indexOf("</uid>"));
	                /*sessionSecret = result.substring(result.indexOf("<secret>") + 8,
	                    result.indexOf("</secret>"));*/
	            } catch (Exception ex) {
	                throw new IOException("Failed to get session key and secret: " + result);
	            }
            }
        }
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
	
	public String getSelectedSex() {
		/*String sex = "";
		int rr = 0;
		int r = table.getSelectedRow();
		if( r >= 0 && r < table.getRowCount() ) {
			rr = table.convertRowIndexToModel(r);
		}
		if( rr < friendList.size() ) {
			Object[] obj = friendList.get(rr);
			sex = (String)obj[4];
		}
		return sex;*/
		
		return mypanel.sexfield.getSelectedItem().toString();
	}
	
	public int getSelectedAge() {
		/*int age = 0;
		int rr = 0;
		int r = table.getSelectedRow();
		if( r >= 0 && r < table.getRowCount() ) {
			rr = table.convertRowIndexToModel(r);
		}
		
		if( rr < friendList.size() ) {
			Object[] obj = friendList.get(rr);
			Calendar now = Calendar.getInstance();
			final Calendar cal = new GregorianCalendar();
			if( obj[3] != null ) cal.setTime( (Date)obj[3] );
			int factor = 0;
			if( now.get(Calendar.DAY_OF_YEAR) < cal.get(Calendar.DAY_OF_YEAR) ) {
		          factor = -1; //birthday not celebrated
		    }
		    age = now.get(Calendar.YEAR) - cal.get(Calendar.YEAR) + factor;
		}
		return age;*/
		
		int age = 40;
		Object o = mypanel.agefield.getValue();
		if( o instanceof Number ) {
			age = ((Number)o).intValue();
		}
		return age;
	}
	
	public String[] getSelectedFriendsIds() {
		List<String>	ret = new ArrayList<String>();
		for( Object[] obj : friendList ) {
			if( (Boolean)obj[1] ) ret.add( (String)obj[0] );
		}
		return ret.toArray( new String[0] );
	}
	
	private String getUserInfo( String sessionKey, String uids, String fields ) {
		String format = null;
		
		String v = "1.0";
        String method = "facebook.users.getInfo";
        String callId = String.valueOf(System.currentTimeMillis());
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
	
	private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch(Throwable th) {}
    }
	
	public String getFriendsXml( String sessionKey, String currentUser ) {
		String format = null;
		String flid = null;
		
		String version = "1.0";
        String method = "facebook.friends.get";
        String callId = String.valueOf(System.currentTimeMillis());
        
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
			
			if( !res.contains("error_response" ) ) {
				String uds = res.substring( res.indexOf("<uid>")+5 ).replace("</uid>\n  <uid>", ",");
				int ind = uds.lastIndexOf(',');
				
				String uids = currentUser;
				if( ind > 0 ) {
					uids += ","+uds.substring(0, ind);
				} else {
					System.err.println( uds );
				}
			
				return getUserInfo( sessionKey, uids, "name,birthday,pic,sex,is_app_user");
			}
        }
        return null;
	}

	public void parseFriendsXml( String uinfo ) {
		String startTag = "<user>";
		String stopTag = "</user>";
		int ustart = uinfo.indexOf(startTag);
		
		friendList.clear();
		
		if( ustart != -1 ) {
			final String[] uval = uinfo.substring( ustart+startTag.length(), uinfo.lastIndexOf(stopTag) ).split(stopTag+"\n  "+startTag);
			for( int i = 0; i < uval.length; i++ ) {
				final Object[]	fres = new Object[7];
				friendList.add( fres );
				
				fres[1] = Boolean.FALSE;
				String xmling = uval[i];
				final String userid = xmling.substring( xmling.indexOf("<uid>")+5, xmling.lastIndexOf("</uid>") );
				fres[0] = userid;
				String uname = xmling.substring( xmling.indexOf("<name>")+6, xmling.lastIndexOf("</name>") );
				if( i == 0 ) {
					currentUser = uname;
					currentUserId = userid;
				}
				fres[2] = uname;
				
				int start = xmling.indexOf("<sex>")+5;
				int stop = xmling.lastIndexOf("</sex>");
				if( stop > start  && start > 0 ) {
					String sex = xmling.substring( start, stop );
					fres[4] = sex.equalsIgnoreCase("male") ? "Karl" : "Kona";
				}
				
				start = xmling.indexOf("<is_app_user>")+13;
				stop = xmling.lastIndexOf("</is_app_user>");
				if( stop > start  && start > 0 ) {
					String appUser = xmling.substring( start, stop );
					fres[6] = appUser.equals("1") ? onIcon : offIcon;
				}
				
				start = xmling.indexOf("<pic>") + 5;
				stop = xmling.indexOf("</pic>");
				if( stop != -1 ) {
					final String urlstr = xmling.substring( start, stop );
					//System.err.println( stop );
					Thread t = new Thread() {
						public void run() {
							try {
								File f = new File( System.getProperty("user.home"), ".isgem" );
								f = new File( f, "friends" );
								BufferedImage img = null;
								if( !f.exists() ) {
									f.mkdirs();
									f = new File( f, userid+".png" );
									URL url = new URL( urlstr );
									InputStream in = url.openStream();
									img = ImageIO.read( in );								
									in.close();
									
									ImageIO.write( img, "png", f );
								} else {
									f = new File( f, userid+".png" );
									if( f.exists() ) {
										img = ImageIO.read( f );
									} else {
										URL url = new URL( urlstr );
										InputStream in = url.openStream();
										img = ImageIO.read( in );
										in.close();
										
										ImageIO.write( img, "png", f );
									}
								}
								
								fres[5] = new ImageIcon( img );
								
								table.revalidate();
								table.repaint();
								/*if( table.getModel() != nullmodel ) {
									table.setAutoCreateColumnsFromModel( false );
									table.setModel( nullmodel );
								}
								if( model != null && model.getRowCount() > 0 ) {
									table.setAutoCreateColumnsFromModel( true );
									table.setModel( model );
									System.err.println( model.getColumnCount() + "  " + model.getRowCount() );
									table.getColumnModel().getColumn(4).setCellRenderer(new IconRenderer());
									table.getColumnModel().getColumn(5).setCellRenderer(new IconRenderer());
								}*/
							} catch (MalformedURLException e) {
								fres[5] = new ImageIcon();
								e.printStackTrace();
							} catch (IOException e) {
								fres[5] = new ImageIcon();
								e.printStackTrace();
							}
						}
					};
					t.start();
				} else {
					fres[5] = new ImageIcon();
				}
				
				start = xmling.indexOf("<birthday>") + 10;
				stop = xmling.indexOf("</birthday>");
				if( stop > start  && start > 0 ) {
					try {
						fres[3] = DateFormat.getDateInstance( DateFormat.LONG, Locale.US ).parse( xmling.substring( start, stop ) );
					} catch (ParseException e) {
						fres[3] = new Date(0);
						e.printStackTrace();
					}
					//fres[1] = DateFormat.
					//parse( xmling.substring( start, stop ) );
				} else {
					fres[3] = new Date(0);
				}
			}
		} else {
			System.err.println( uinfo );
		}
	}
	
	public class IconRenderer extends DefaultTableCellRenderer {
		@Override
		public JLabel getTableCellRendererComponent( JTable table, Object icon, boolean isSelected, boolean hasFocus, int row, int column ) {
			ImageIcon icon_image = (ImageIcon)icon;
			setIcon(icon_image);
			setHorizontalAlignment(JLabel.CENTER);
			return this;
		}
	};
	
	public void createFriendsModel( String sessionKey, String currentUser ) {
		String xml = null;
		
		if( sessionKey != null && sessionKey.length() > 0 ) {
			xml = getFriendsXml( sessionKey, currentUser );
		}
		
		if( xml != null && sessionKey != null && sessionKey.length() > 0 ) {
			File f = new File( System.getProperty("user.home"), ".isgem" );
			f = new File( f, "friends" );
			if( !f.exists() ) {
				f.mkdirs();
			}
			f = new File( f, "friends.xml" );
			try {
				FileWriter	fw = new FileWriter( f );
				fw.write( xml );
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			try {
				File f = new File( System.getProperty("user.home"), ".isgem" );
				f = new File( f, "friends" );
				if( f.exists() ) {
					f = new File( f, "friends.xml" );
					if( f.exists() ) {
						try {
							FileInputStream	fis = new FileInputStream( f );
							InputStreamReader	isr = new InputStreamReader( fis );
							int r = isr.read( cbuf );
							if( r > 0 ) {
								xml = new String( cbuf, 0, r );
							}
							isr.close();
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			} catch( Exception e ) {
				
			}
		}
		if( xml != null ) parseFriendsXml( xml );
	}
	
	public void selectMe( final MyPanel mypanel, int r ) {		
		if( r != -1 ) {
			Object o = table.getValueAt(r, 1);
			if( o != null && o instanceof String ) {
				String s = (String)o;
				mypanel.namefield.setText( s );
			}
			
			o = table.getValueAt(r, 2);
			if( o != null && o instanceof Date ) {
				Date d = (Date)o;
				Calendar dateOfBirth = new GregorianCalendar();
				dateOfBirth.setTime(d);
				Calendar today = Calendar.getInstance();
				int age = today.get(Calendar.YEAR) - dateOfBirth.get(Calendar.YEAR);
				dateOfBirth.add(Calendar.YEAR, age);
				if (today.before(dateOfBirth)) {
				    age--;
				}
				mypanel.agefield.setValue( age );
			}
			
			o = table.getValueAt(r, 3);
			if( o != null && o instanceof String ) {
				String s = (String)o;
				mypanel.sexfield.setSelectedItem( s );
			}
			
			o = table.getValueAt(r, 4);
			if( o != null && o instanceof ImageIcon ) {
				ImageIcon	img = (ImageIcon)o;
				mypanel.myimage = img.getImage();
				mypanel.repaint();
			}
		}
	}
	
	char[]	cbuf = new char[50000];
	public FriendsPanel( String sessionKey0, final String currentUser, final String lang ) {
		super();
		this.setDividerLocation( 500 );
		
		this.currentUser = lang.equals("IS") ? "Velja höfund" : "Choose author";
		this.sessionKey = sessionKey0;
		this.setBackground( Color.white );
		
		nullmodel = new TableModel() {
			public void setValueAt(Object aValue, int rowIndex, int columnIndex) {}
			
			public void removeTableModelListener(TableModelListener l) {}
			
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return false;
			}
			
			public Object getValueAt(int rowIndex, int columnIndex) {
				return null;
			}
			
			public int getRowCount() {
				return 0;
			}
			
			public String getColumnName(int columnIndex) {
				return null;
			}
			
			public int getColumnCount() {
				return 0;
			}
			
			public Class<?> getColumnClass(int columnIndex) {
				return null;
			}
			
			public void addTableModelListener(TableModelListener l) {}
		};
		
		Color cl = new Color( 0,0,0,0 );
		
		BufferedImage	on = new BufferedImage(24, 24, BufferedImage.TYPE_INT_ARGB );
		Graphics2D g = (Graphics2D)on.getGraphics();
		g.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
		g.setColor( cl );
		g.fillRect(0, 0, 24, 24);
		g.setColor( Color.green );
		g.fillOval(0, 0, 24, 24);
		g.dispose();
		onIcon = new ImageIcon( on );
		
		BufferedImage	off = new BufferedImage(24, 24, BufferedImage.TYPE_INT_ARGB );
		g = (Graphics2D)off.getGraphics();
		g.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
		g.setColor( cl );
		g.fillRect(0, 0, 24, 24);
		g.setColor( Color.red );
		g.fillOval(0, 0, 24, 24);
		g.dispose();
		offIcon = new ImageIcon( off );
		
		createFriendsModel( sessionKey, currentUser );
		model = new TableModel() {
			public void addTableModelListener(TableModelListener arg0) {}

			public Class<?> getColumnClass(int arg0) {				
				if( arg0 == 0 ) return Boolean.class;
				else if( arg0 == 1 ) return String.class;
				else if( arg0 == 2 ) return Date.class;
				else if( arg0 == 3 ) return String.class;
				else if( arg0 == 4 ) return Icon.class;
				else if( arg0 == 5 ) return Icon.class;
				return null;
			}

			public int getColumnCount() {				
				return 6;
			}

			public String getColumnName(int arg0) {
				if( lang.equals("IS") ) {
					if( arg0 == 0 ) return "Val";
					else if( arg0 == 1 ) return "Nafn";
					else if( arg0 == 2 ) return "Fæðingardagur";
					else if( arg0 == 3 ) return "Kyn";
					else if( arg0 == 4 ) return "Mynd";
					else if( arg0 == 5 ) return "Notandi";
				} else {
					if( arg0 == 0 ) return "Choice";
					else if( arg0 == 1 ) return "Name";
					else if( arg0 == 2 ) return "DOB";
					else if( arg0 == 3 ) return "Gender";
					else if( arg0 == 4 ) return "Image";
					else if( arg0 == 5 ) return "User";
				}
				return "";
			}

			public int getRowCount() {
				return friendList.size();
			}

			public Object getValueAt(int arg0, int arg1) {
				Object[] obj = friendList.get(arg0);
				Object o = obj[arg1+1];
				return o;
			}

			public boolean isCellEditable(int arg0, int arg1) {
				if( arg1 == 0 && friendList.size() > 0 ) return true;
				return false;
			}

			public void removeTableModelListener(TableModelListener arg0) {}

			public void setValueAt(Object arg0, int arg1, int arg2) {
				Object[] obj = friendList.get(arg1);
				obj[arg2+1] = arg0;
			}
		};
		updateModel();
		table.setAutoCreateRowSorter( true );
		table.setRowHeight( 76 );
		
		Object[]	me = null;
		if( friendList.size() > 0 ) me = friendList.get(0);
		URL url = this.getClass().getResource("/re.png");
		ImageIcon icon = new ImageIcon( url );
		ImageIcon imic = null;
		if( me != null && me.length > 5 ) imic = (ImageIcon)me[5];
		Image		img = null;
		if( imic != null ) img = imic.getImage();
		else {
			BufferedImage bimg = new BufferedImage(150, 200, BufferedImage.TYPE_INT_RGB);
			Graphics2D g2 = (Graphics2D)bimg.getGraphics();
			g2.setColor( Color.blue );
			g2.fillRect(0, 0, bimg.getWidth(), bimg.getHeight());
			g2.dispose();
			
			img = bimg;
		}
		mypanel = new MyPanel( img, icon, lang );		
		table.addMouseListener( new MouseAdapter() {
			public void mousePressed( MouseEvent me ) {
				if( me.getClickCount() == 2 ) {
					int r = table.getSelectedRow();
					selectMe( mypanel, r );
				}
			}
		});
		
		JPopupMenu popup = new JPopupMenu();
		Action action = new AbstractAction(lang.equals("IS") ? "Velja mig" : "Select as current user") {
			public void actionPerformed(ActionEvent e) {
				int r = table.getSelectedRow();
				selectMe( mypanel, r );
			}
		};
		popup.add( action );
		popup.addSeparator();
		action = new AbstractAction(lang.equals("IS") ? "Sýna alla" : "Show all") {
			public void actionPerformed(ActionEvent e) {
				for( int i : table.getSelectedRows() ) {
					int k = table.convertRowIndexToModel(i);
					Object[] obj = friendList.get(k);
					obj[1] = Boolean.TRUE;
				}
				table.revalidate();
				table.repaint();
			}
		};
		//if( lang.equals("EN") ) action.
		popup.add( action );
		action = new AbstractAction("Fela Alla") {
			public void actionPerformed(ActionEvent e) {
				for( int i : table.getSelectedRows() ) {
					int k = table.convertRowIndexToModel(i);
					Object[] obj = friendList.get(k);
					obj[1] = Boolean.FALSE;
				}
				table.revalidate();
				table.repaint();
			}
		};
		popup.add( action );
		action = new AbstractAction(lang.equals("IS") ? "Viðsnúa vali" : "Swap selection") {
			public void actionPerformed(ActionEvent e) {
				int[] rows = table.getSelectedRows();
				table.selectAll();
				for( int r : rows ) {
					table.removeRowSelectionInterval(r, r);
				}
			}
		};
		popup.add( action );
		popup.addSeparator();
		popup.add( new AbstractAction(lang.equals("IS") ? "Bjóða völdum vinum" : "Invite selected friends") {
			public void actionPerformed(ActionEvent e) {
				List<String>	id = new ArrayList<String>();
				for( Object[] obj : friendList ) {
					if( obj[5] == offIcon && (Boolean)obj[1] ) id.add( (String)obj[0] );
				}
				
				if( id.size() > 0 ) {
					JOptionPane.showMessageDialog(FriendsPanel.this, lang.equals("IS") ? "Vinum hefur verið boðið að nota Matísgem" : "Friends have been invited to use this program");
				} else {
					JOptionPane.showMessageDialog(FriendsPanel.this, lang.equals("IS") ? "Enginn vinur sem ekki er notandi Matísgem hefur verið valinn" : "No program user has been selected");
				}
			}
		});
		popup.addSeparator();
		popup.add( new AbstractAction("Uppfæra vinalista") {
			public void actionPerformed(ActionEvent e) {
				try {
					login();
					FriendsPanel.this.createFriendsModel(FriendsPanel.this.sessionKey, FriendsPanel.this.currentUserId);
					selectMe( mypanel, 0 );
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		table.setComponentPopupMenu( popup );
		
		JScrollPane	scrollpane = new JScrollPane() {
			public void paint(Graphics g) {
				super.paint( g );
				
				if( table.getRowCount() == 0 ) {
					Graphics2D g2 = (Graphics2D)g;
					g2.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
					
					String str = lang.equals("IS") ? "Hægri smelltu til að" : "Right-click to get a";
					String nstr = lang.equals("IS") ? "fá upp vallista" : "list of choices";
					int strw = g.getFontMetrics().stringWidth( str );
					int nstrw = g.getFontMetrics().stringWidth( nstr );
					g.setColor( Color.lightGray );
					g.drawString(str, (this.getWidth()-strw)/2, this.getHeight()/2-5);
					g.drawString(nstr, (this.getWidth()-nstrw)/2, this.getHeight()/2+10);
				}
			}
		};
		scrollpane.getViewport().setBackground( Color.white );
		scrollpane.setComponentPopupMenu( popup );
		scrollpane.setViewportView( table );
		
		this.setRightComponent( scrollpane );
		this.setLeftComponent( mypanel );
		
		final JButton	button = mypanel.rotatebutton;
		button.addActionListener( new ActionListener() {	
			public void actionPerformed(ActionEvent e) {
				mypanel.orientation = (mypanel.orientation+1)%4;
				
				if( mypanel.orientation%2 == 1 ) {
					swapComponents();
				}
				
				int orientation = FriendsPanel.this.getOrientation() == JSplitPane.HORIZONTAL_SPLIT ? JSplitPane.VERTICAL_SPLIT : JSplitPane.HORIZONTAL_SPLIT;
				FriendsPanel.this.setOrientation( orientation );
				//DetailPanel.
			}
		});
	}
	
	public void updateFriends( final String sessionKey, final String currentUser ) {
		this.nullModel();
		AccessController.doPrivileged(new PrivilegedAction<Object>() {
			public Object run() {
				FriendsPanel.this.createFriendsModel(sessionKey, currentUser);
				return null;
			}
		});
		this.updateModel();
	}
	
	public void nullModel() {
		if( table.getModel() != nullmodel ) {
			table.setModel( nullmodel );
		}
		table.repaint();
	}

	public void updateModel() {
		if( table.getModel() != nullmodel ) {
			table.setModel( nullmodel );
		}
		table.setModel( model );
		table.getColumnModel().getColumn(0).setMaxWidth(50);
		table.getColumnModel().getColumn(3).setMaxWidth(100);
		table.getColumnModel().getColumn(4).setCellRenderer(new IconRenderer());
		table.getColumnModel().getColumn(5).setCellRenderer(new IconRenderer());
		table.getColumnModel().getColumn(5).setMaxWidth(100);
		table.repaint();
	}
}
