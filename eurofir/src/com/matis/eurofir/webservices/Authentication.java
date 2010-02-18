package com.matis.eurofir.webservices;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.util.Arrays;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;

import org.apache.commons.codec.digest.DigestUtils;

public class Authentication {
	static String	apiKey = "0123456789ABCDEFGHJK";
	static String	secret = "0123456789ABCDEFGHJKLMNOPQRSTUVXYZ!@#$%^";
	
	public Authentication() {
	}
	
	public static void test( String sign ) throws NoSuchAlgorithmException {
		String[]	testargs = {"simmi","joi"};
		String		result = getMD5Signature( testargs );
		System.err.println( result );
	}
	
	public static String getMD5Signature( String[] args ) throws NoSuchAlgorithmException {
		return getSignature(args, "MD5");
	}
	
	public static String getSHA1Signature( String[] args ) throws NoSuchAlgorithmException {
		return getSignature(args, "SHA-1");
	}
	
	public static String getEuroFIRSignature( SortedMap<String,String>	smap ) throws NoSuchAlgorithmException {
		String result = secret;
		for( String key : smap.keySet() ) {
			String value = smap.get(key);
			result += key+value;
		}
		MessageDigest md = MessageDigest.getInstance( "SHA-1" );
		byte[] dig = md.digest( result.getBytes() );
		BigInteger	big = new BigInteger( dig );
		
		//Signature s = Signature.getInstance("SHA-1");
		//s.
		
		/*dig = md.digest( "".getBytes() );
		BigInteger	bi = new BigInteger( dig );
		System.err.println( bi.toString(16) );*/
		
		return big.toString(16);
	}
	
	public static String getEuroFIRSignatureJakarta( SortedMap<String,String>	smap ) throws NoSuchAlgorithmException {
		String result = secret;
		for( String key : smap.keySet() ) {
			String value = smap.get(key);
			value = value.replaceAll("[\\s]", "");
			result += key+value;
		}
		
		String ret = DigestUtils.shaHex( result );
		//System.err.println( result );
		//System.err.println( DigestUtils.shaHex( "" ) );
			
		return ret;
	}
	
	public static String getSignature( String[] args, String type ) throws NoSuchAlgorithmException {
		Arrays.sort( args );
		String result = secret;
		for( String arg : args ) {
			result += arg;
		}
		MessageDigest md = MessageDigest.getInstance( type );
		byte[] dig = md.digest( result.getBytes() );
		BigInteger	big = new BigInteger( dig );
		
		return big.toString(16);
	}
	
	public static void run( String[] args, String sign ) throws NoSuchAlgorithmException, IOException {
		String result = getSHA1Signature( args );
		if( sign.equals( result ) ) {
			System.out.println( "success" );
		} else {
			byte[]	bb = new byte[1024];
			InputStream is = Authentication.class.getResourceAsStream( "/auth_error.xml" );
			int r = is.read( bb );
			while( r > 0 ) {
				System.out.write(bb, 0, r);
				r = is.read( bb );
			}
		}	
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String sign = System.getProperty("api_signature");
		try {
			//test( sign );
			run( args, sign );
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
