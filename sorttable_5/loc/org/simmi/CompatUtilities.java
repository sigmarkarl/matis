package org.simmi;

import java.awt.Component;
import java.awt.Frame;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Calendar;

import javax.swing.SwingUtilities;

import org.jdesktop.swingx.decorator.SortOrder;

public class CompatUtilities {
	public static String lof = "";
	public static SortOrder UNSORTED = SortOrder.UNSORTED;
	
	public static void updateLof() {
	}
	
	public static Frame getWindowAncestor( Component comp ) {
		return (Frame)SwingUtilities.getWindowAncestor( comp );
	}

	public static String getCharsetString(byte[] buffer, int i, int read, Charset cs) throws UnsupportedEncodingException {
		return new String(buffer, i, read, cs.displayName());
	}
	
	public static void browse( URL url ) throws IOException, URISyntaxException {
		browse( url.toURI() );
	}
	
	public static void browse( URI uri ) {
		System.err.println( "No browsing available" );
	}
	
	public static String getDateString( Calendar cal, boolean val ) {
		/*if( arg0 == 1 ) str = cal.get(Calendar.DAY_OF_MONTH) + ". "+ cal.getDisplayName( Calendar.MONTH, Calendar.SHORT, Locale.getDefault());
		else str = cal.getDisplayName( Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault());*/
		return "";
	}

	public static String[] copyOfRange(String[] dayfood, int i, int min) {
		String[] ret = new String[ min-i ];
		for( int k = i; k < min; k++ ) {
			ret[k-i] = dayfood[k];
		}
		return ret;
	}
}
