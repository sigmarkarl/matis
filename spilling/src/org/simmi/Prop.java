package org.simmi;

import java.awt.Color;
import java.awt.Container;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class Prop extends JComponent {
	JLabel			nLabel;
	JLabel			ktLabel;
	JLabel			hmLabel;
	JLabel			textLabel;
	JTextField		name;
	JTextField		kt;
	JTextField		home;
	JEditorPane		text;
	
	public Corp		currentCorp = null;
	Color lightGray = new Color(240,240,240);
	
	public Prop() {
		nLabel = new JLabel("Nafn:");
		ktLabel = new JLabel("Kennitala:");
		hmLabel = new JLabel("Heimili:");
		textLabel = new JLabel("Texti:");
		name = new JTextField();
		kt = new JTextField();
		home = new JTextField();
		text = new JEditorPane();
		text.setEditable( true );
		
		this.add(nLabel);
		this.add(ktLabel);
		this.add(hmLabel);
		this.add(textLabel);
		this.add(name);
		this.add(kt);
		this.add(home);
		this.add(text);
		
		this.setLayout( null );
		this.setSize(400, 300);
		
		KeyListener kl = new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				if( e.getKeyCode() == KeyEvent.VK_ENTER ) {
					Container ct = Prop.this.getParent();
					ct.remove( Prop.this );
					ct.repaint();
				}
			}
		};
		name.addKeyListener( kl );
		kt.addKeyListener( kl );
		home.addKeyListener( kl );
		text.addKeyListener( kl );
	}
	
	public void paintComponent( Graphics g ) {
		super.paintComponent( g );
		
		Graphics2D	g2 = (Graphics2D)g;
		GradientPaint gp = new GradientPaint(0, 0, lightGray, this.getWidth(), this.getHeight(), Color.lightGray);
		g2.setPaint( gp );
		g2.fillRoundRect(0, 0, this.getWidth(), this.getHeight(), 20, 20 );
	}
	
	public void setBounds( int x, int y, int w, int h ) {
		nLabel.setBounds( 5, 5, 90, 25 );
		ktLabel.setBounds( 5, 35, 90, 25 );
		hmLabel.setBounds( 5, 65, 90, 25 );
		textLabel.setBounds( 5, 95, 90, 25 );
		name.setBounds( 100, 5, 295, 25 );
		kt.setBounds( 100, 35, 295, 25 );
		home.setBounds( 100, 65, 295, 25 );
		text.setBounds( 100, 95, 295, 225 );
		
		super.setBounds( x,y,w,h );
	}
}
