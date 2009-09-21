package org.simmi;

import java.awt.Component;
import java.awt.Window;
import java.nio.charset.Charset;

import javax.swing.SwingUtilities;

public class CompatUtilities {
	public static Window getWindowAncestor( Component comp ) {
		return SwingUtilities.getWindowAncestor( comp );
	}

	public static String getCharsetString(byte[] buffer, int i, int read, Charset cs) {
		return new String(buffer, i, read, cs);
	}
}
