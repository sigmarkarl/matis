package org.simmi;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.swing.JApplet;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;

public class MApplet extends JApplet {
	public void init() {
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
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
		
		JDesktopPane	desktoppane = new JDesktopPane();
		
		/*Console console = null;
		try {
			console = new Console();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		console.setBounds(100, 100, 400, 300);
		console.setVisible( true );*/
		
		final JTextArea	textarea = new JTextArea();
		
		final PipedOutputStream	pos = new PipedOutputStream();
		try {
			final PipedInputStream pis = new PipedInputStream( pos );
			System.setIn( pis );
			textarea.addCaretListener( new CaretListener() {
				@Override
				public void caretUpdate(CaretEvent e) {
					try {
						if( e.getDot() > 0 ) {
							String text = textarea.getText( e.getDot()-1, 1);
							pos.write( text.getBytes() );
							if( text.equals("\n") ) {
								//pis.
								//System.err.println("flushing");
								pos.flush();
							}
						}
					} catch (BadLocationException be) {
						be.printStackTrace();
					} catch (IOException ie) {					
						ie.printStackTrace();
					}
				}
			});
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		JInternalFrame frame = new JInternalFrame("simmi",true,true,true,true);
		frame.setBounds(100, 100, 800, 600);
		frame.add( textarea );
		desktoppane.add( frame );
		frame.setVisible( true );
		
		this.add( desktoppane );
		
		Simlab  simlab = new Simlab();
		final ScriptEngine engine = simlab.getScriptEngine();
		new Thread() {
			public void run() {
				try {
					engine.eval( new InputStreamReader( System.in ) );
				} catch (ScriptException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();
	}
}
