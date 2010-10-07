package org.simmi;

import java.awt.Graphics;
import java.awt.Image;
import java.text.NumberFormat;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class MyPanel extends JComponent {
	Image			myimage;
	JButton			rotatebutton;
	
	JLabel			namelabel;
	JTextField		namefield;
	
	JLabel			agelabel;
	JSpinner		agefield;
	
	JLabel					weightlabel;
	JFormattedTextField		weightfield;
	
	JLabel					heightlabel;
	JFormattedTextField		heightfield;
	
	JLabel			sexlabel;
	JComboBox		sexfield;
	
	JLabel			hreyfing;
	
	JLabel			rest;
	JSpinner		resthours;
	
	JLabel			easy;
	JSpinner		easyhours;
	
	JLabel			medium;
	JSpinner		mediumhours;
	
	JLabel			hard;
	JSpinner		hardhours;
	
	JLabel			restlabel;
	JLabel			enlabel;
	
	int				orientation;
	
	boolean			stateInactive = false;
	
	public void moveForward( JSpinner val, JSpinner[] arr ) {
		int i = 0;
		JSpinner tmp = arr[0];
		while( tmp != val ) {
			i++;
			JSpinner spn = arr[i];
			arr[i] = tmp;
			tmp = spn;
		}
		arr[0] = tmp;
		
		int u = (Integer)arr[0].getValue();
		int d = (Integer)arr[1].getValue();
		int t = (Integer)arr[2].getValue();
		int q = (Integer)arr[3].getValue();
		
		if( u + d > 24 ) {
			t = 0;
			q = 0;
			d = (24-u);
		} else if( u + d + t > 24 ) {
			q = 0;
			t = (24-(u+d));
		} else if( u + d + t + q > 24 ) {
			q = (24-(u+d+t));
		} else if( u + d + t + q < 24 ) {
			q = (24-(u+d+t));
		}
		
		arr[1].setValue( d );
		arr[2].setValue( t );
		arr[3].setValue( q );
	}
	
	public MyPanel( Image	image, ImageIcon 	icon, String lang ) {
		super();
		
		myimage = image;
		
		rotatebutton = new JButton( icon );
		rotatebutton.setBounds( 9,9,32,32 );
		
		namelabel = new JLabel( lang.equals("IS") ? "Nafn:" : "Name:" );
		namefield = new JTextField();
		agelabel = new JLabel( lang.equals("IS") ? "Aldur:" : "Age" );
		agefield = new JSpinner( new SpinnerNumberModel(40,1,100,1) );
		weightlabel = new JLabel( lang.equals("IS") ? "Þyngd:" : "Weight" );
		weightfield = new JFormattedTextField( NumberFormat.getNumberInstance() );
		heightlabel = new JLabel( lang.equals("IS") ? "Hæð:" : "Height" );
		heightfield = new JFormattedTextField( NumberFormat.getNumberInstance() );
		sexlabel = new JLabel( lang.equals("IS") ? "Kyn:" : "Gender" );
		sexfield = new JComboBox( lang.equals("IS") ? new String[] {"Karl","Kona"} : new String[] {"Male","Female"} );
		
		restlabel = new JLabel(lang.equals("IS") ? "Hreyfing (klst.)" : "Action (hours)");
		
		SpinnerNumberModel restspmod = new SpinnerNumberModel(24,0,24,1);
		SpinnerNumberModel easyspmod = new SpinnerNumberModel(0,0,24,1);
		SpinnerNumberModel medspmod = new SpinnerNumberModel(0,0,24,1);
		SpinnerNumberModel hardspmod = new SpinnerNumberModel(0,0,24,1);
		
		rest = new JLabel(lang.equals("IS") ? "Hvíld:" : "Rest");
		resthours = new JSpinner( restspmod );
		
		easy = new JLabel(lang.equals("IS") ? "Létt hreyfing:" : "Light action");
		easyhours = new JSpinner( easyspmod );
		
		medium = new JLabel(lang.equals("IS") ? "Miðlungs hreyfing:" : "Medium action");
		mediumhours = new JSpinner( medspmod );
		
		hard = new JLabel(lang.equals("IS") ? "Mikil hreyfing:" : "Heavy action");
		hardhours = new JSpinner( hardspmod );
		
		final JSpinner[]	sp = {resthours,easyhours,mediumhours,hardhours};
		
		resthours.addChangeListener( new ChangeListener() {
			public void stateChanged(ChangeEvent ce) {
				if( stateInactive != true ) {
					stateInactive = true;
					moveForward( resthours, sp );
					stateInactive = false;
				}
			}
		});
		
		easyhours.addChangeListener( new ChangeListener() {
			public void stateChanged(ChangeEvent ce) {
				if( stateInactive != true ) {
					stateInactive = true;
					moveForward( easyhours, sp );		
					stateInactive = false;
				}
			}
		});
		
		mediumhours.addChangeListener( new ChangeListener() {
			public void stateChanged(ChangeEvent ce) {
				if( stateInactive != true ) {
					stateInactive = true;
					moveForward( mediumhours, sp );
					stateInactive = false;
				}
			}
		});
		
		hardhours.addChangeListener( new ChangeListener() {
			public void stateChanged(ChangeEvent ce) {
				if( stateInactive != true ) {
					stateInactive = true;
					moveForward( hardhours, sp );					
					stateInactive = false;
				}
			}
		});
		
		enlabel = new JLabel();
		
		this.add( rotatebutton );
		this.add( namelabel );
		this.add( namefield );
		this.add( agelabel );
		this.add( agefield );
		this.add( weightlabel );
		this.add( weightfield );
		this.add( heightlabel );
		this.add( heightfield );
		this.add( sexlabel );
		this.add( sexfield );
		
		this.add( rest );
		this.add( resthours );
		
		this.add( easy );
		this.add( easyhours );
		
		this.add( medium );
		this.add( mediumhours );
		
		this.add( hard );
		this.add( hardhours );
		
		this.add( restlabel );
		this.add( enlabel );
	}
	
	public void paintComponent( Graphics g ) {
		super.paintComponent( g );
		
		if( myimage != null ) {
			int y = this.getHeight()/2-200;
			int h = 200;
			int w = (myimage.getWidth(this)*200)/myimage.getHeight(this);
			int x = (this.getWidth()-w)/2;
			g.drawImage( myimage, x, y, w, h, this );
		}
	}
	
	public void setBounds( int x, int y, int w, int h ) {
		super.setBounds(x, y, w, h);
		
		namelabel.setBounds( w/2-150, h/2+5, 50, 25 );
		namefield.setBounds( w/2-100, h/2+5, 250, 25 );
		agelabel.setBounds( w/2-150, h/2+35, 50, 25 );
		agefield.setBounds( w/2-100, h/2+35, 100, 25 );
		sexlabel.setBounds( w/2+10, h/2+35, 50, 25 );
		sexfield.setBounds( w/2+50, h/2+35, 100, 25 );
		weightlabel.setBounds( w/2-150, h/2+65, 50, 25 );
		weightfield.setBounds( w/2-100, h/2+65, 100, 25 );
		heightlabel.setBounds( w/2+10, h/2+65, 50, 25 );
		heightfield.setBounds( w/2+50, h/2+65, 100, 25 );
		
		restlabel.setBounds(w/2-20,h/2+95,100,25);
		
		rest.setBounds( w/2-150, h/2+125, 100, 25 );
		resthours.setBounds( w/2-50, h/2+125, 50, 25 );
		easy.setBounds( w/2+10, h/2+125, 100, 25 );
		easyhours.setBounds( w/2+100, h/2+125, 50, 25 );
		medium.setBounds( w/2-150, h/2+155, 100, 25 );
		mediumhours.setBounds( w/2-50, h/2+155, 50, 25 );
		hard.setBounds( w/2+10, h/2+155, 100, 25 );
		hardhours.setBounds( w/2+100, h/2+155, 50, 25 );
	}
	
	public void saveMyInfo() {
		
	}
}
