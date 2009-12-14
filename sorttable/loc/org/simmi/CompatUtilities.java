package org.simmi;

import java.awt.Component;
import java.awt.Desktop;
import java.awt.Window;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.Locale;

import javax.swing.SortOrder;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class CompatUtilities {
	static String lof = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
	public static SortOrder UNSORTED = SortOrder.UNSORTED;
	
	public static void updateLof() {
		try {
			UIManager.setLookAndFeel(lof);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static Window getWindowAncestor( Component comp ) {
		return SwingUtilities.getWindowAncestor( comp );
	}

	public static String getCharsetString(byte[] buffer, int i, int read, Charset cs) {
		return new String(buffer, i, read, cs);
	}
	
	public static void browse( URL url ) {
		try {
			Desktop.getDesktop().browse( url.toURI() );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static String getDateString( Calendar cal, boolean val ) {
		if( val ) return cal.get(Calendar.DAY_OF_MONTH) + ". "+ cal.getDisplayName( Calendar.MONTH, Calendar.SHORT, Locale.getDefault());
		else return cal.getDisplayName( Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault());
	}
}
