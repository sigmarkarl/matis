package org.simmi;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class TestReq {
	public static byte[] buffer = new byte[2048];
	
	public static void main( String[] args ) {
		String xml = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n";
		xml += "<facebook-relation>\n";
		xml += "<facebook-user id=\"1\" name=\"Þórður Kristjánsson\" dob=\"03041965\" />";
		xml += "<facebook-friend id=\"558456339\" name=\"Páll Arnar Erlendsson\" dob=\"31011979\" />";
		xml += "<facebook-friend id=\"558723036\" name=\"Stella María Sigurðardóttir\" dob=\"20031970\" />";
		xml += "<facebook-friend id=\"558930370\" name=\"Reynir Scheving\" dob=\"15051979\" />";
		xml += "<facebook-friend id=\"559635012\" name=\"Hreinn Pálsson\" dob=\"25061976\" />";
		xml += "<facebook-friend id=\"566440343\" name=\"Garpur Dagsson\" dob=\"16061967\" />";
		xml += "<facebook-friend id=\"571388066\" name=\"Olafur Arthursson\" dob=\"26021974\" />";
		xml += "<facebook-friend id=\"573644548\" name=\"Kristín Helgadóttir\" dob=\"01071982\" />";
		xml += "<facebook-friend id=\"579276334\" name=\"Elís Ingi Benediktsson\" dob=\"02011981\" />";
		xml += "<facebook-friend id=\"580479997\" name=\"Gísli Magnússon\" dob=\"11101969\" />";
		xml += "</facebook-relation>\n";
		
		URL url = null;
		try {
			//url = new URL( "https://localhost:5001/IAdmin/AdminFB.jsp" );
			url = new URL( "https://islendingabok.is/IAdmin/AdminFB.jsp" );
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
				int r = httpConnection.getInputStream().read( buffer );
				
				if( r > 0 ) {
					System.err.println( new String( buffer, "ISO-8859-1" ) );
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
	}
}
