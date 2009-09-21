package org.simmi;

import java.awt.Component;
import java.awt.Frame;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import javax.swing.SwingUtilities;

public class CompatUtilities {
	public static Frame getWindowAncestor( Component comp ) {
		return (Frame)SwingUtilities.getWindowAncestor( comp );
	}

	public static String getCharsetString(byte[] buffer, int i, int read, Charset cs) throws UnsupportedEncodingException {
		return new String(buffer, i, read, cs.displayName());
	}
}
