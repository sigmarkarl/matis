package com.matis.eurofir.webservices;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class Authentication {
	static String	apiKey = "0123456789ABCDEFGHJK";
	static String	secret = "0123456789ABCDEFGHJKLMNOPQRSTUVXYZ!@#$%^";
	
	public Authentication() {
	}
	
	public static void test( String sign ) throws NoSuchAlgorithmException {
		String[]	testargs = {"simmi","joi"};
		String		result = getSignature( testargs, sign );
		System.err.println( result );
	}
	
	public static String getSignature( String[] args, String sign ) throws NoSuchAlgorithmException {
		Arrays.sort( args );
		String result = secret;
		for( String arg : args ) {
			result += arg;
		}
		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] dig = md.digest( result.getBytes() );
		BigInteger	big = new BigInteger( dig );
		
		return big.toString(16);
	}
	
	public static void run( String[] args, String sign ) throws NoSuchAlgorithmException {
		String result = getSignature( args, sign );
		if( sign.equals( result ) ) {
			System.err.println( "success" );
		} else {
			//InputStream is = Authentication.class.getResourceAsStream(name);
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
		}
	}

}
