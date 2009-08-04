package org.simmi;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SocialDistance {
	private final String apiKey = "e18e41fe84964fe36d39332f9fd9450b";
	private final String secret = "b27705900c8e4373dc3683397b8cbaff";
	
	Map<String,Object>	fbMap = new HashMap<String,Object>();
	
	static {
        System.setProperty("http.agent", System.getProperty("user.name") + " (from NetBeans IDE)");
    }
	
	public static class User {
		String	name;
		String	birthd;
		
		public User( String name, String birthd ) {
			this.name = name;
			this.birthd = birthd;
		}
	};
	
	public SocialDistance( String[] split ) throws IOException {		
		for( String str : split ) {
			String[] spl = str.split("=");
			fbMap.put( spl[0], spl[1] );
		}
		
		String ustr = (String)fbMap.get("fb_sig_user");
		String fstr = ustr + "," + ((String)fbMap.get("fb_sig_friends")).replace("%2C", ",");
			
		String[] friends = fstr.split( "," );
		//List<String>	flist = Arrays.asList( friends );
		
		Map<String,User>			umap = new HashMap<String,User>();
		File 						f = new File("/tmp/users");
		if( f.exists() ) {
			FileReader					fr = new FileReader( f );
			BufferedReader				br = new BufferedReader( fr );
			String 						line = br.readLine();
			while( line != null && !line.contains("<xml") ) {
				String[] cols = line.split("\t");
				if( cols.length > 2 ) {
					umap.put( cols[0], new User( cols[1], cols[2] ) );
				} else if( cols.length > 1 ) {
					umap.put( cols[0], new User( cols[1], null ) );
				}
				line = br.readLine();
			}
			br.close();
			fr.close();
		}
		
		String users = getUserInfo( fstr, "name,birthday" );
		String[] usernames = users.split("<user>");
		
		for( String val : usernames ) {
			int nind = val.indexOf("<name>");
			if( nind >= 0 ) {
				int neind = val.indexOf("</name>", nind);
				String user = val.substring( nind+6, neind );
				//String id = friends[i++];
				
				int uind = val.indexOf("<uid>");
				int ueind = val.indexOf("</uid>", uind);
				String uid = val.substring( uind+5, ueind );
					
				int bind = val.indexOf("<birthday>");
				if( bind >= 0 ) {
					int beind = val.indexOf("</birthday>", bind);
					String bd = val.substring(bind+10, beind);
					umap.put( uid, new User( user, bd ) );
				} else {
					umap.put( uid, new User( user, null ) );
				}
			}
		}
		
		FileWriter fw = new FileWriter( f );
		for( String id : umap.keySet() ) {
			User user = umap.get(id);
			if( user.birthd == null ) fw.write( id + "\t" + user.name + "\n" );
			else fw.write( id + "\t" + user.name + "\t" + user.birthd + "\n" );
		}
		fw.close();
		
		Set<String>	fset = new HashSet<String>();
		for( String user : friends ) {
			f = new File("/tmp/"+user );
			if( f.exists() ) {
				FileReader fr = new FileReader( f );
				BufferedReader br = new BufferedReader( fr );
				String line = br.readLine();
				while( line != null && !line.contains("xml") ) {
					fset.add( line );
					line = br.readLine();
				}
				br.close();
				fr.close();
			}
			
			String uids1 = user;
			String uids2 = user == friends[0] ? friends[1] : friends[0];
			for( String frnd : friends ) {
				if( frnd != friends[0] && !frnd.equals( user ) ) {
					uids1 += ","+user;
					uids2 += ","+frnd;
				}
			}
			String res = getAreFriends(uids1, uids2);
			
			if( !res.contains("error") ) {
				String[] asplt = res.split("<uid2>");
				for( String af : asplt ) {
					int i1 = af.indexOf("</uid2>");
					if( i1 >= 0 ) {
						int a1 = af.indexOf("<are_friends>");
						String frnd = af.substring(0,i1);
						if( af.charAt(a1+13) == '0' ) fset.remove( frnd );
						else fset.add( frnd );
					}
				}
			}
			
			fw = new FileWriter( f );
			//fw.write( res );
			//fw.write("auli");
			for( String friend : fset ) {
				fw.write( friend + "\n" );
			}
			fw.close();
			
			fset.clear();
		}	
	}
	
	public SocialDistance( String s ) throws IOException {
		this( s.split( "&" ) );
	}
	
	public static class Uid {
		String birthd;
		String id;
		
		public Uid( String id, String birthd ) {
			this.id = id;
			this.birthd = birthd;
		}
	}
	
	public static String search( String u1, String u2 ) {
		if( u1 == u2 ) return "Distance 0";
		
		
		
		return "Infinite Distance";
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String user = System.getProperty("search");
		try {
			user = URLDecoder.decode( user, "UTF-8" );
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		if( user != null ) {
			String me = System.getProperty("user");
			
			Set<Uid>					uids = new HashSet<Uid>();
			//Map<String,User>			umap = new HashMap<String,User>();
			File f = new File("/tmp/users");
			if( f.exists() ) {
				try {
					FileReader 		fr = new FileReader( f );
					BufferedReader	br = new BufferedReader( fr );
					
					String line = br.readLine();
					while( line != null ) {
						String[] split = line.split( "\t" );
						if( split.length > 2 ) {
							if( split[1].equals(user) ) {
								uids.add( new Uid(split[0],split[2]) );
							}
							//umap.put( split[0], new User(split[1], split[2]) );
						}
						line = br.readLine();
					}
					br.close();
					fr.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if( uids.size() > 0 ) {
				for( Uid uid : uids ) {
					Set<String>	su1 = new HashSet<String>();
					Set<String>	su2 = new HashSet<String>();
					
					su1.add( me );
					su2.add( uid.id );
					
					if( su1.contains( uid.id ) ) {
						System.out.println( "<fb:profile-pic uid=\""+uid.id+"\" /><fb:name uid=\""+uid.id+"\" /><br>" );
						System.out.println( "has distance 0" );
					} else {
						Set<String>	tu1 = new HashSet<String>();
						Set<String>	tu2 = new HashSet<String>();
						
						int s1;
						int s2;
						
						Set<String>	friends = new HashSet<String>();
						try {
							int d = 0;
							do {
								s1 = su1.size();
								s2 = su2.size();	
								
								tu1.clear();
								tu2.clear();
								
								/*for( String s : su1 ) {
									File fl = new File( "/tmp/"+s );
									if( fl.exists() ) {
										FileReader fr = new FileReader( fl );
										BufferedReader br = new BufferedReader( fr );
										String line = br.readLine();
										//if( line.split("\t").length > 1 ) System.err.println("ufffi "+s);
										while( line != null ) {
											/*if( tu1.contains( line ) ) {
												Object obj = tu1.get( line );
												if( obj instanceof String ) {
													Set<String>	ss = new HashSet<String>();
													ss.add( (String)obj );
													ss.add( s );
													tu1.put( line, ss );
												} else if( obj instanceof HashSet ) {
													Set<String>	ss = (Set<String>)obj;
													ss.add( s );
												}
											} else *
											tu1.add( line );								
											line = br.readLine();
										}
										br.close();
										fr.close();
									}
								}*/
								
								for( String s : su2 ) {
									File fl = new File( "/tmp/"+s );
									if( fl.exists() ) {
										FileReader fr = new FileReader( fl );
										BufferedReader br = new BufferedReader( fr );
										String line = br.readLine();
										while( line != null ) {
											if( line.equals( me ) ) friends.add( s );
											
											/*if( tu2.containsKey( line ) ) {
												Object obj = tu2.get( line );
												if( obj instanceof String ) {
													Set<String>	ss = new HashSet<String>();
													ss.add( (String)obj );
													ss.add( s );
													tu2.put( line, ss );
												} else if( obj instanceof HashSet ) {
													Set<String>	ss = (Set<String>)obj;
													ss.add( s );
												}
											} else */
											tu2.add( line );			
											line = br.readLine();
										}
										br.close();
										fr.close();
									}
								}
								
								//su1.putAll(m)
								//su1.addAll( tu1 );
								su2.addAll( tu2 );
								
								//su1.
								
								d++;
								/*if( su1.contains( uid.id ) ) {
									nofriends = false;
									/*Object obj = su1.get( uid.id );
									if( obj instanceof String ) {
										
									}*
									System.out.println( "Distance: " + (d) );
									break;
								} else*/ 
								if( friends.size() > 0 ) {
									System.out.println( "<fb:profile-pic uid=\""+uid.id+"\" /><fb:name uid=\""+uid.id+"\" /><br>" );
									System.out.println( "has distance " + (d) );
									if( d > 1 ) {
										System.out.println( "<br>through your friend(s)<br>" );
										for( String friend : friends ) {
											//User u = umap.get( friend );
											System.out.println( "<fb:profile-pic uid=\""+friend+"\" /><fb:name uid=\""+friend+"\" /><br>" );
										}
									}
									break;
								}
							} while( /*su1.size() > s1 ||*/ su2.size() > s2 );
														
							if( friends.size() == 0 ) System.out.println( "Distance unknown");
						} catch ( Exception e ) {
							e.printStackTrace();
						}
						
						//System.out.println( search( me, uid.id ) );
					}
					
					break;
				}
			} else {
				String ret = user + " not found";
				System.out.println( ret );
				
				/*try {
					String user1 = new String( user.getBytes("ISO-8859-1") );
					String user2 = new String( user.getBytes("UTF-8") );
					
					System.out.println( user1 );
					System.out.println( user2 );
				} catch (UnsupportedEncodingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				/*System.out.println( "<br>Ögmundur Þórðar<br>" );
				
				try {
					System.out.write( ret.getBytes("ISO-8859-1") );
					System.out.write( ret.getBytes("UTF-8") );
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
			}
			
			/*File f = new File("/tmp/bb");
			try {				
				byte[]	bb = new byte[16768];
				FileInputStream fis = new FileInputStream( f );
				int r = fis.read(bb);
				fis.close();
				
				String ret = new String( bb, 0, r );
				
				System.out.println( ret );
				FileWriter	fw = new FileWriter( new File("/tmp/ber") );
				fw.write( ret );
				/*for( String id : umap.keySet() ) {
					String uname = umap.get(id);
					fw.write( id + "\t" + uname + "\n" );
				}
				fw.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}*/
		} else {		
		/*byte[]	bb = new byte[ 8192 ];
		int rr = 0;
		try {
			int r = System.in.read( bb );
			while( r != -1 ) {
				rr += r;
				r = System.in.read( bb, rr, bb.length-rr );
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String s = new String( bb, 0 , rr );
		System.err.println( s );*/
		//SocialDistance sd = new SocialDistance( s );
		
			try {
				SocialDistance sd = new SocialDistance( args );
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
		
	public String sign( String[][] params ) throws IOException {
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
	
	private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch(Throwable th) {}
    }
	
	private String getAreFriends( String uids1, String uids2 ) {
		String format = null;
		
		String v = "1.0";
        String method = "facebook.friends.areFriends";
        //FacebookSocialNetworkingServiceAuthenticator.login();
        String callId = String.valueOf(System.currentTimeMillis());
        String sessionKey = (String)fbMap.get("fb_sig_session_key");
        String sig;
        RestResponse rr = null;
		try {
			sig = sign( new String[][]{{"api_key", apiKey}, {"session_key", sessionKey}, {"call_id", callId}, {"v", v},  {"uids1", uids1}, {"uids2", uids2}, {"format", format}, {"method", method}});
			String[][] pathParams = new String[][]{};
	        String[][] queryParams = new String[][]{{"api_key", "" + apiKey + ""}, {"session_key", sessionKey}, {"call_id", callId}, {"sig", sig}, {"v", v},  {"uids1", uids1}, {"uids2", uids2}, {"format", format}, {"method", method}};
	        RestConnection conn = new RestConnection("http://api.facebook.com/restserver.php", pathParams, queryParams);
	        sleep(1000);
	        rr = conn.get(null);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		String ret = rr.getDataAsString();		
		return ret;
	}
	
	private String getUserInfo( String uids, String fields ) {
		String format = null;
		
		String v = "1.0";
        String method = "facebook.users.getInfo";
        //FacebookSocialNetworkingServiceAuthenticator.login();
        String callId = String.valueOf(System.currentTimeMillis());
        String sessionKey = (String)fbMap.get("fb_sig_session_key");
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
		
		String ret = rr.getDataAsString();		
		return ret;
	}
	
	public class RestConnection {
	    private HttpURLConnection conn;
	    private String date;

	    /** Creates a new instance of RestConnection */
	    public RestConnection(String baseUrl) {
	        this(baseUrl, null, null);
	    }

	    /** Creates a new instance of RestConnection */
	    public RestConnection(String baseUrl, String[][] params) {
	        this(baseUrl, null, params);
	    }

	    /** Creates a new instance of RestConnection */
	    public RestConnection(String baseUrl, String[][] pathParams, String[][] params) {
	        try {
	            String urlStr = baseUrl;
	            if (pathParams != null && pathParams.length > 0) {
	                urlStr = replaceTemplateParameters(baseUrl, pathParams);
	            }
	            URL url = new URL(encodeUrl(urlStr, params));
	            conn = (HttpURLConnection) url.openConnection();
	            conn.setDoInput(true);
	            conn.setDoOutput(true);
	            conn.setUseCaches(false);
	            conn.setDefaultUseCaches(false);
	            conn.setAllowUserInteraction(true);

	            SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
	            date = format.format(new Date());
	            conn.setRequestProperty("Date", date);
	        } catch (Exception ex) {
	            Logger.getLogger(RestConnection.class.getName()).log(Level.SEVERE, null, ex);
	        }
	    }

	    public void setAuthenticator(Authenticator authenticator) {
	        Authenticator.setDefault(authenticator);
	    }

	    public String getDate() {
	        return date;
	    }

	    public RestResponse get() throws IOException {
	        return get(null);
	    }

	    public RestResponse get(String[][] headers) throws IOException {
	        conn.setRequestMethod("GET");
	        return connect(headers, null);
	    }

	    public RestResponse head() throws IOException {
	        return get(null);
	    }

	    public RestResponse head(String[][] headers) throws IOException {
	        conn.setRequestMethod("HEAD");
	        return connect(headers, null);
	    }

	    public RestResponse put(String[][] headers) throws IOException {
	        return put(headers, (InputStream) null);
	    }

	    public RestResponse put(String[][] headers, String data) throws IOException {
	        InputStream is = null;
	        if(data != null)
	            is = new ByteArrayInputStream(data.getBytes("UTF-8"));
	        return put(headers, is);
	    }

	    public RestResponse put(String[][] headers, InputStream is) throws IOException {
	        conn.setRequestMethod("PUT");
	        return connect(headers, is);
	    }
	    
	    public RestResponse post(String[][] headers) throws IOException {
	        return post(headers, (InputStream) null);
	    }

	    public RestResponse post(String[][] headers, String data) throws IOException {
	        InputStream is = null;
	        if(data != null)
	            is = new ByteArrayInputStream(data.getBytes("UTF-8"));
	        return post(headers, is);
	    }
	    
	    public RestResponse post(String[][] headers, InputStream is) throws IOException {
	        conn.setRequestMethod("POST");
	        return connect(headers, is);
	    }

	    /**
	     * Used by post method whose contents are like form input
	     */
	    public RestResponse post(String[][] headers, String[][] params) throws IOException {
	        conn.setRequestMethod("POST");
	        conn.setRequestProperty("ContentType", "application/x-www-form-urlencoded");
	        String data = encodeParams(params);
	        return connect(headers, new ByteArrayInputStream(data.getBytes("UTF-8")));
	    }

	    public RestResponse delete(String[][] headers) throws IOException {
	        conn.setRequestMethod("DELETE");
	        return connect(headers, null);
	    }

	    /**
	     * @param baseUrl
	     * @param params
	     * @return response
	     */
	    private RestResponse connect(String[][] headers,
	            InputStream data) throws IOException {
	        try {
	            // Send data
	            setHeaders(headers);

	            String method = conn.getRequestMethod();
	            
	            byte[] buffer = new byte[1024];
	            int count = 0;
	            
	            if (method.equals("PUT") || method.equals("POST")) {
	                if (data != null) {
	                    conn.setDoOutput(true);
	                    OutputStream os = conn.getOutputStream();
	                    
	                    while ((count = data.read(buffer)) != -1) {
	                        os.write(buffer, 0, count);
	                    }
	                    os.flush();
	                }
	            }

	            RestResponse response = new RestResponse();
	            InputStream is = conn.getInputStream();
	         
	            while ((count = is.read(buffer)) != -1) {
	                response.write(buffer, 0, count);
	            }

	            response.setResponseCode(conn.getResponseCode());
	            response.setResponseMessage(conn.getResponseMessage());
	            response.setContentType(conn.getContentType());
	            response.setContentEncoding(conn.getContentEncoding());
	            response.setLastModified(conn.getLastModified());
	            
	            return response;
	        } catch (Exception e) {
	            String errMsg = "Cannot connect to :" + conn.getURL();
	            try {
	                BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
	                String line;
	                StringBuffer buf = new StringBuffer();
	                while ((line = rd.readLine()) != null) {
	                    buf.append(line);
	                    buf.append('\n');
	                }
	                errMsg = buf.toString();
	            } finally {
	                throw new IOException(errMsg);
	            }
	        }
	    }

	    private String replaceTemplateParameters(String baseUrl, String[][] pathParams) {
	        String url = baseUrl;
	        if (pathParams != null) {
	            for (int i = 0; i < pathParams.length; i++) {
	                String key = pathParams[i][0];
	                String value = pathParams[i][1];
	                if (value == null) {
	                    value = "";
	                }
	                url = url.replace(key, value);
	            }
	        }
	        return url;
	    }

	    private String encodeUrl(String baseUrl, String[][] params) {
	        String encodedParams = encodeParams(params);
	        if (encodedParams.length() > 0) {
	            encodedParams = "?"+ encodedParams;
	        }
	        return baseUrl + encodedParams;
	    }

	    private String encodeParams(String[][] params) {
	        String p = "";

	        if (params != null) {
	            for (int i = 0; i < params.length; i++) {
	                String key = params[i][0];
	                String value = params[i][1];

	                if (value != null) {
	                    try {
	                        p += key + "=" + URLEncoder.encode(value, "UTF-8") + "&";
	                    } catch (UnsupportedEncodingException ex) {
	                        Logger.getLogger(RestConnection.class.getName()).log(Level.SEVERE, null, ex);
	                    }
	                }
	            }
	            if (p.length() > 0) {
	                p = p.substring(0, p.length() - 1);
	            }
	        }

	        return p;
	    }

	    private void setHeaders(String[][] headers) {
	        if (headers != null) {
	            for (int i = 0; i < headers.length; i++) {
	                conn.setRequestProperty(headers[i][0], headers[i][1]);
	            }
	        }
	    }
	};
	
	public class RestResponse {
	    private ByteArrayOutputStream os;
	    private String contentType = "text/plain";
	    private String contentEncoding;
	    private int responseCode;
	    private String responseMsg;
	    private long lastModified;

	    
	    public RestResponse() {
	        os = new ByteArrayOutputStream();
	    }
	    
	    public RestResponse(byte[] bytes) throws IOException {
	        this();

	        byte[] buffer = new byte[1024];
	        int count = 0;
	        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
	        while ((count = bis.read(buffer)) != -1) {
	            write(buffer, 0, count);
	        }
	    }

	    public void setContentType(String contentType) {
	        this.contentType = contentType;
	    }
	    
	    public String getContentType() {
	        return contentType;
	    }

	    public void setContentEncoding(String contentEncoding) {
	        this.contentEncoding = contentEncoding;
	    }
	    
	    public void setResponseMessage(String msg) {
	        this.responseMsg = msg;
	    }
	    
	    public String getResponseMessage() {
	        return responseMsg;
	    }
	    
	    public void setResponseCode(int code) {
	        this.responseCode = code;
	    }
	    
	    public int getResponseCode() {
	        return responseCode;
	    }
	    
	    public void setLastModified(long lastModified) {
	        this.lastModified = lastModified;
	    }
	    
	    public long getLastModified() {
	        return lastModified;
	    }
	    
	    public void write(byte[] bytes, int start, int length) {
	        os.write(bytes, start, length);
	    }
	    
	    public byte[] getDataAsByteArray() {
	        return os.toByteArray();
	    }
	    
	    public String getDataAsString() {
	        try {
	        	
	        	//return URLDecoder.decode( os.toString(), "UTF-8" );
	            return os.toString("UTF-8");
	        } catch (Exception ex) {
	            Logger.getLogger(RestConnection.class.getName()).log(Level.SEVERE, null, ex);
	        }
	        
	        return null;
	    }
	    
	    public OutputStream getOutputStream() {
	        return os;
	    }

	    /* public <T> T getDataAsObject(Class<T> jaxbClass) throws JAXBException {
	        return getDataAsObject(jaxbClass, jaxbClass.getPackage().getName());
	    }
	 
	    public <T> T getDataAsObject(Class<T> clazz, String packageName) throws JAXBException {
	        JAXBContext jc = JAXBContext.newInstance(packageName);
	        Unmarshaller u = jc.createUnmarshaller();
	        Object obj = u.unmarshal(new StreamSource(new StringReader(getDataAsString())));
	        
	        if (obj instanceof JAXBElement) {
	            return (T) ((JAXBElement) obj).getValue();
	        } else {
	            return (T) obj;
	        } 
	    }*/
	};
}
