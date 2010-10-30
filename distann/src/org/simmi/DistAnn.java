package org.simmi;

import java.awt.Dimension;
import java.io.File;

import javax.swing.JApplet;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

public class DistAnn extends JApplet {
	public void init() {
		Dimension d = new Dimension( 100,30 );
		final JProgressBar	p = new JProgressBar();
		p.setIndeterminate( true );
		p.setPreferredSize( d );
		p.setSize( d );
		final JLabel	l = new JLabel();
		l.setPreferredSize( d );
		l.setSize( d );
		JComponent 		c = new JComponent() { 
			public void setBounds( int x, int y, int w, int h ) {
				super.setBounds(x, y, w, h);
				int pw = p.getWidth();
				int ph = p.getHeight();
				l.setBounds((w-pw)/2, (h-ph)/2-20, pw, ph);
				p.setBounds((w-pw)/2, (h-ph)/2+20, pw, ph);
			}
		};
		c.setLayout( null );
		
		c.add( p );
		c.add( l );
		
		String home = System.getProperty("user.home");
		File f = new File(home, ".distann");
		if( !f.exists() ) {
			f.mkdirs();
		}
		
		String osname = System.getProperty("os.name");
		if( f.exists() ) {
			File prodigal = new File( f, "Prodigal" );
			
		}
	}
}
