package com.matis.eurofir.webservices;

import java.io.IOException;
import java.io.InputStream;

public class FDTP {

	public static String fdtp = "";
	public static byte[] b = new byte[1024];
	
	static {
		InputStream is = FDTP.class.getResourceAsStream("/fdtp.xml");
		try {
			int r = is.read(b);
			while( r > 0 ) {
				fdtp += new String( b, 0, r );
				r = is.read(b);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
