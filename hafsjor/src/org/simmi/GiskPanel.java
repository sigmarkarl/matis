package org.simmi;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

import org.apache.tika.Tika;
import org.apache.tika.config.TikaConfig;

public class GiskPanel extends JComponent {
	JButton		openbutton;
	JScrollPane	scrollpane;
	JScrollPane	subscroll;
	JList		filelist;
	String		path;
	JSplitPane	splitpane;
	
	JLabel		userLabel;
	JComponent	comp;
	
	SimField	lastField = null;
	class SimField extends JTextField {
		public void requestFocus() {
			super.requestFocus();
			
			lastField = this;
		}
	};
	
	final JLabel		nameLabel = new JLabel("Nafn fyrirtækis:");
	final SimField		nameField = new SimField();
	final JLabel		ktLabel = new JLabel("Kt fyrirtækis:");
	final SimField		ktField = new SimField();
	final JLabel		yearLabel = new JLabel("Reikningsár:");
	final SimField		yearField = new SimField();
	
	final JLabel		endLabel = new JLabel("Nafn endurskoðanda:");
	final SimField		endField = new SimField();
	final JLabel		endktLabel = new JLabel("Kt endurskoðanda:");
	final SimField		endktField = new SimField();
	
	final JLabel		endfLabel = new JLabel("Nafn endursk ft:");
	final SimField		endfField = new SimField();
	final JLabel		endfktLabel = new JLabel("Kt endursk ft:");
	final SimField		endfktField = new SimField();
	
	final JLabel		stjfLabel = new JLabel("Stjórnarfm:");
	final SimField		stjfField = new SimField();
	final JLabel		frmkvLabel = new JLabel("Framkvæmdarstj:");
	final SimField		frmkvField = new SimField();
	
	final JLabel		stjfktLabel = new JLabel("Stjórnarfm kt:");
	final SimField		stjfktField = new SimField();
	final JLabel		frmkvktLabel = new JLabel("Framkvæmdarstj kt:");
	final SimField		frmkvktField = new SimField();
	
	final JLabel		stj1Label = new JLabel("Stjórnarm 1:");
	final SimField		stj1Field = new SimField();
	final JLabel		stj2Label = new JLabel("Stjórnarm 2:");
	final SimField		stj2Field = new SimField();
	final JLabel		stj3Label = new JLabel("Stjórnarm 3:");
	final SimField		stj3Field = new SimField();
	
	final JLabel		stjkt1Label = new JLabel("Stjm 1 kt:");
	final SimField		stjkt1Field = new SimField();
	final JLabel		stjkt2Label = new JLabel("Stjm 2 kt:");
	final SimField		stjkt2Field = new SimField();
	final JLabel		stjkt3Label = new JLabel("Stjm 3 kt:");
	final SimField		stjkt3Field = new SimField();
	
	final JLabel		gjldmLabel = new JLabel("Gjaldm:");
	final SimField		gjldmField = new SimField();
	final SimField		gjldmField2 = new SimField();
	final JLabel		reksthLabel = new JLabel("Rekstrarhagn:");
	final SimField		reksthField = new SimField();
	final SimField		reksthField2 = new SimField();
	final JLabel		ebidtaLabel = new JLabel("Ebidta:");
	final SimField		ebidtaField = new SimField();
	final SimField		ebidtaField2 = new SimField();
	final JLabel		hagnLabel = new JLabel("Hagn ársins:");
	final SimField		hagnField = new SimField();
	final SimField		hagnField2 = new SimField();
	final JLabel		fjartLabel = new JLabel("Fjármunatekj:");
	final SimField		fjartField = new SimField();
	final SimField		fjartField2 = new SimField();
	final JLabel		fastafLabel = new JLabel("Fastafjárm:");
	final SimField		fastafField = new SimField();
	final SimField		fastafField2 = new SimField();
	final JLabel		aflahLabel = new JLabel("Aflaheimild:");
	final SimField		aflahField = new SimField();
	final SimField		aflahField2 = new SimField();
	final JLabel		veltufLabel = new JLabel("Veltufjárm:");
	final SimField		veltufField = new SimField();
	final SimField		veltufField2 = new SimField();
	final JLabel		eignirLabel = new JLabel("Eignir:");
	final SimField		eignirField = new SimField();
	final SimField		eignirField2 = new SimField();
	final JLabel		eigidfLabel = new JLabel("Eigið fé:");
	final SimField		eigidfField = new SimField();
	final SimField		eigidfField2 = new SimField();
	final JLabel		skuldbLabel = new JLabel("Skuldb og vlan:");
	final SimField		skuldbField = new SimField();
	final SimField		skuldbField2 = new SimField();
	final JLabel		langtskLabel = new JLabel("Langtímask:");
	final SimField		langtskField = new SimField();
	final SimField		langtskField2 = new SimField();
	final JLabel		skammtskLabel = new JLabel("Skammtímask:");
	final SimField		skammtskField = new SimField();
	final SimField		skammtskField2 = new SimField();
	final JLabel		handbfLabel = new JLabel("Handb fé til rekstrar:");
	final SimField		handbfField = new SimField();
	final SimField		handbfField2 = new SimField();
	final JLabel		fjfhLabel = new JLabel("Fjárf hreyf:");
	final SimField		fjfhField = new SimField();
	final SimField		fjfhField2 = new SimField();
	final JLabel		fjmhLabel = new JLabel("Fjárm hreyf:");
	final SimField		fjmhField = new SimField();
	final SimField		fjmhField2 = new SimField();
	final JLabel		hbfaLabel = new JLabel("Handb fé í ársl:");
	final SimField		hbfaField = new SimField();
	final SimField		hbfaField2 = new SimField();
	final JLabel		skuldLabel = new JLabel("Skuldir:");
	final SimField		skuldField = new SimField();
	final SimField		skuldField2 = new SimField();
	final JLabel		skeigLabel = new JLabel("Skld og eigidf:");
	final SimField		skeigField = new SimField();
	final SimField		skeigField2 = new SimField();
	
	final JLabel		avoLabel = new JLabel("Ávöxtunarkrafa:");
	final SimField		avoField = new SimField();
	
	final JLabel		skraLabel = new JLabel("Skrásetjarni:");
	final SimField		skraField = new SimField();
	
	SimField[]	fields = {nameField,ktField,yearField,endField,endktField,endfField,endfktField,stjfField,frmkvField,stjfktField,frmkvktField,stj1Field,stj2Field,stj3Field,stjkt1Field,stjkt2Field,stjkt3Field,gjldmField,gjldmField2,reksthField,reksthField2,ebidtaField,ebidtaField2,hagnField,hagnField2,fjartField,fjartField2,fastafField,fastafField2,aflahField,aflahField2,veltufField,veltufField2,eignirField,eignirField2,eigidfField,eigidfField2,skuldbField,skuldbField2,langtskField,langtskField2,skammtskField,skammtskField2,handbfField,handbfField2,fjfhField,fjfhField2,fjmhField,fjmhField2,hbfaField,hbfaField2,skuldField,skuldField2,skeigField,skeigField2,skraField};
	
	Map<String,Arsuppl>	upplMap = new HashMap<String,Arsuppl>();
	
	static class Arsuppl {
		String	name;
		String	kt;
		String	year;
		String	endursk;
		String	endurkt;
		String	endurskf;
		String	endurktf;
		String	stjf;
		String	stjfkt;
		String	frmkv;
		String	frmkvkt;
		String	stj1;
		String	stj2;
		String	stj3;
		String	stjkt1;
		String	stjkt2;
		String	stjkt3;
		String	gjldm;
		String	reksth;
		String	ebidta;
		String	hagn;
		String	fjart;
		String	fastaf;
		String	aflah;
		String	veltuf;
		String	eignir;
		String	eigidf;
		String	skuldb;
		String	langtsk;
		String	skammtsk;
		String	handbf;
		String	fjfh;
		String	fjmh;
		String	hbfa;
		String	skuld;
		String	skeig;
		
		String	reksth2;
		String	ebidta2;
		String	hagn2;
		String	fjart2;
		String	fastaf2;
		String	aflah2;
		String	veltuf2;
		String	eignir2;
		String	eigidf2;
		String	skuldb2;
		String	langtsk2;
		String	skammtsk2;
		String	handbf2;
		String	fjfh2;
		String	fjmh2;
		String	hbfa2;
		String	skuld2;
		String	skeig2;
		
		String	avox;
		String	skra;
		String	skranafn;
		int		index;
		
		void clear() {
			name = "";
			kt = "";
			year = "";
			endursk = "";
			endurkt = "";
			endurskf = "";
			endurktf = "";
			stjf = "";
			stjfkt = "";
			frmkv = "";
			frmkvkt = "";
			stj1 = "";
			stj2 = "";
			stj3 = "";
			stjkt1 = "";
			stjkt2 = "";
			stjkt3 = "";
			gjldm = "";
			reksth = "";
			ebidta = "";
			hagn = "";
			fjart = "";
			fastaf = "";
			aflah = "";
			veltuf = "";
			eignir = "";
			eigidf = "";
			skuldb = "";
			langtsk = "";
			skammtsk = "";
			handbf = "";
			fjfh = "";
			fjmh = "";
			hbfa = "";
			skuld = "";
			skeig = "";
			
			reksth2 = "";
			ebidta2 = "";
			hagn2 = "";
			fjart2 = "";
			fastaf2 = "";
			aflah2 = "";
			veltuf2 = "";
			eignir2 = "";
			eigidf2 = "";
			skuldb2 = "";
			langtsk2 = "";
			skammtsk2 = "";
			handbf2 = "";
			fjfh2 = "";
			fjmh2 = "";
			hbfa2 = "";
			skuld2 = "";
			skeig2 = "";
			
			avox = "";
			skra = "";
			skranafn = "";
			index = -1;
		}
	};
	Arsuppl	current = new Arsuppl();
	
	public File getSelectedFile() throws MalformedURLException {
		return new File(path + filelist.getSelectedValue());
	}
	
	public void scrollCompToVis( Rectangle rect ) {
		comp.scrollRectToVisible( rect );
	}
	
	public void copyTextToFocus( String focus ) {
		JTextField fld = null;
		for( JTextField field : fields ) {
			if( field.hasFocus() ) {
				fld = field;
				break;
			}
		}
		
		if( fld == null ) fld = lastField;
		if( fld != null ) {
			fld.requestFocus();
			fld.setText( focus );
		}
	}
	
	public void appendFiles( File dir, List<String> filelist, int rlen ) {
		File[] files = dir.listFiles( new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				if( pathname.getName().endsWith(".pdf") || pathname.getName().endsWith(".PDF") || pathname.isDirectory() ) return true;
				return false;
			}
		});
		
		for( File f : files ) {
			if( f.isDirectory() ) appendFiles( f, filelist, rlen );
			else {
				filelist.add( f.getAbsolutePath().substring(rlen) );
			}
		}
	}
	
	public ListModel openDir() {
		JFileChooser	jfc = new JFileChooser();
		ListModel		lm = null;
		jfc.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
		if( jfc.showOpenDialog( this ) == JFileChooser.APPROVE_OPTION ) {
			File selfile = jfc.getSelectedFile();
			
			path = selfile.getAbsolutePath();
			final List<String>	filenamelist = new ArrayList<String>();
			appendFiles(selfile, filenamelist, path.length() );
			
			lm = new ListModel() {
				@Override
				public void removeListDataListener(ListDataListener l) {}
				
				@Override
				public int getSize() {
					return filenamelist.size();
				}
				
				@Override
				public Object getElementAt(int index) {
					return filenamelist.get(index);
				}
				
				@Override
				public void addListDataListener(ListDataListener l) {}
			};
		}
		
		return lm;
	}
	
	public void refreshForm() throws MalformedURLException, SQLException, ClassNotFoundException {
		String filename = getSelectedFile().getName();
		
		Arsuppl	uppl;
		if( upplMap.containsKey(filename) ) {
			uppl = upplMap.get( filename );
		} else {
			uppl = new Arsuppl();
			fetchDb( uppl );
			if( uppl.skra == null || uppl.skra.length() == 0 ) {
				uppl.skra = userLabel.getText();
			}
			upplMap.put( filename, uppl );
		}
		
		current = uppl;
		fillForm();
	}
	
	Connection con;
	public Connection connect() throws SQLException, ClassNotFoundException {
		if( con == null || con.isClosed() ) {
			con =  Uthlutun.connect();
		}
		return con;
	}
	
	public void fetchPdf( File f ) throws IOException, SQLException {
		Arsuppl uppl;
		if( upplMap.containsKey( f.getName() ) ) {
			uppl = upplMap.get( f.getName() );
			uppl.clear();
			
			if( uppl.skranafn == null || uppl.skranafn.length() == 0 ) {
				uppl.skranafn = getSelectedFile().getName();
			}
		} else {
			uppl = new Arsuppl();
			uppl.skranafn = getSelectedFile().getName();
		}
		
		uppl.skra = userLabel.getText();
		ArsParse.func( t, f, null, uppl );
		upplMap.put( f.getName(), uppl );
		current = uppl;
	}
	
	public void updateCurrent() {
		 current.name = nameField.getText( );
		 current.kt = ktField.getText( );
		 current.year = yearField.getText( );
		 current.endursk = endField.getText( );
		 current.endurkt = endktField.getText( );
		 current.endurskf = endfField.getText( );
		 current.endurktf = endfktField.getText( );
		 current.stjf = stjfField.getText( );
		 current.stjfkt = stjfktField.getText( );
		 current.frmkv = frmkvField.getText( );
		 current.frmkvkt = frmkvktField.getText( );
		 
		 current.stj1 = stj1Field.getText( );
		 current.stj2 = stj2Field.getText( );
		 current.stj3 = stj3Field.getText( );
		 current.stjkt1 = stjkt1Field.getText( );
		 current.stjkt2 = stjkt2Field.getText( );
		 current.stjkt3 = stjkt3Field.getText( );
		 
		 current.gjldm = gjldmField.getText( );
		 current.reksth = reksthField.getText( );
		 current.ebidta = ebidtaField.getText( );
		 current.hagn = hagnField.getText( );
		 current.fjart = fjartField.getText( );
		 current.fjfh = fjfhField.getText( );
		 current.fjmh = fjmhField.getText( );
		
		 current.fastaf = fastafField.getText( );
		 current.aflah = aflahField.getText( );
		 current.veltuf = veltufField.getText( );
		 current.eignir = eignirField.getText( );
		 current.eigidf = eigidfField.getText( );
		 
		 current.skuldb = skuldbField.getText( );
		 current.langtsk = langtskField.getText( );
		 current.skammtsk = skammtskField.getText( );
		 
		 current.handbf = handbfField.getText( );
		 current.hbfa = hbfaField.getText( );
		 current.skuld = skuldField.getText();
		 current.skeig = skeigField.getText();
		 
		 
		 current.reksth2 = reksthField2.getText( );
		 current.ebidta2 = ebidtaField2.getText( );
		 current.hagn2 = hagnField2.getText( );
		 current.fjart2 = fjartField2.getText( );
		 current.fjfh2 = fjfhField2.getText( );
		 current.fjmh2 = fjmhField2.getText( );
		
		 current.fastaf2 = fastafField2.getText( );
		 current.aflah2 = aflahField2.getText( );
		 current.veltuf2 = veltufField2.getText( );
		 current.eignir2 = eignirField2.getText( );
		 current.eigidf2 = eigidfField2.getText( );
		 
		 current.skuldb2 = skuldbField2.getText( );
		 current.langtsk2 = langtskField2.getText( );
		 current.skammtsk2 = skammtskField2.getText( );
		 
		 current.handbf2 = handbfField2.getText();
		 current.hbfa2 = hbfaField2.getText();
		 current.skuld2 = skuldField2.getText();
		 current.skeig2 = skeigField2.getText();
		 
		 
		 current.avox = avoField.getText();
		 current.skra = skraField.getText();
	}
	
	public void fillForm() {
		 nameField.setText( current.name );
		 ktField.setText( current.kt );
		 yearField.setText( current.year );
		 endField.setText( current.endursk );
		 endktField.setText( current.endurkt );
		 endfField.setText( current.endurskf );
		 endfktField.setText( current.endurktf );
		 stjfField.setText( current.stjf );
		 stjfktField.setText( current.stjfkt );
		 frmkvField.setText( current.frmkv );
		 frmkvktField.setText( current.frmkvkt );
		 
		 stj1Field.setText( current.stj1 );
		 stj2Field.setText( current.stj2 );
		 stj3Field.setText( current.stj3 );
		 stjkt1Field.setText( current.stjkt1 );
		 stjkt2Field.setText( current.stjkt2 );
		 stjkt3Field.setText( current.stjkt3 );
		 
		 gjldmField.setText( current.gjldm );
		 
		 reksthField.setText( current.reksth );
		 ebidtaField.setText( current.ebidta );
		 hagnField.setText( current.hagn );
		 fjartField.setText( current.fjart );
		 fjfhField.setText( current.fjfh );
		 fjmhField.setText( current.fjmh );
		
		 fastafField.setText( current.fastaf );
		 aflahField.setText( current.aflah );
		 veltufField.setText( current.veltuf );
		 eignirField.setText( current.eignir );
		 eigidfField.setText( current.eigidf );
		 
		 skuldbField.setText( current.skuldb );
		 langtskField.setText( current.langtsk );
		 skammtskField.setText( current.skammtsk );
		 
		 handbfField.setText( current.handbf );
		 hbfaField.setText( current.hbfa );
		 skuldField.setText( current.skuld );
		 skeigField.setText( current.skeig );
		 
		 
		 reksthField2.setText( current.reksth2 );
		 ebidtaField2.setText( current.ebidta2 );
		 hagnField2.setText( current.hagn2 );
		 fjartField2.setText( current.fjart2 );
		 fjfhField2.setText( current.fjfh2 );
		 fjmhField2.setText( current.fjmh2 );
		
		 fastafField2.setText( current.fastaf2 );
		 aflahField2.setText( current.aflah2 );
		 veltufField2.setText( current.veltuf2 );
		 eignirField2.setText( current.eignir2 );
		 eigidfField2.setText( current.eigidf2 );
		 
		 skuldbField2.setText( current.skuldb2 );
		 langtskField2.setText( current.langtsk2 );
		 skammtskField2.setText( current.skammtsk2 );
		 
		 handbfField2.setText( current.handbf2 );
		 hbfaField2.setText( current.hbfa2 );
		 skuldField2.setText( current.skuld2 );
		 skeigField2.setText( current.skeig2 );
		 
		 
		 avoField.setText( current.avox );
		 skraField.setText( current.skra );
	}
	
	public void fetchDb( Arsuppl current ) throws SQLException, ClassNotFoundException, MalformedURLException {
		connect();
		PreparedStatement ps = con.prepareStatement("select * from [hafsjor].[dbo].[skyrslur] where skranafn = ?"); //ar = ? and (heiti_fyr = ? or kt_fyr = ?)");
		ps.setString( 1, getSelectedFile().getName() );
		//ps.setString( 1, yearField.getText() );
		//ps.setString( 2, nameField.getText() );
		//ps.setString( 3, ktField.getText() );
		ResultSet rs = ps.executeQuery();
		
		while( rs.next() ) {
			current.year = rs.getString("ar");
			current.name = rs.getString("heiti_fyr");
			current.kt = rs.getString("kt_fyr");
			
			current.endursk = rs.getString("nafn_endurskodanda");
			current.endurkt = rs.getString("kt_endurskodanda");
			current.endurskf = rs.getString("endurskodunar_fyr");
			current.endurktf = rs.getString("kt_endurskodunar_fyr");
			current.stjf = rs.getString("stjornarformadur");
			current.stjfkt = rs.getString("kt_stjornarformadur");
			current.frmkv = rs.getString("framkvaemdarstjori");
			current.frmkvkt = rs.getString("kt_framkvaemdarstjori");
			current.stj1 = rs.getString("smadur_1");
			current.stjkt1 = rs.getString("kt_smadur_1");
			current.stj2 = rs.getString("smadur_2");
			current.stjkt2 = rs.getString("kt_smadur_2");
			//current.stj3 = rs.getString("smadur_3");
			//current.stjkt3 = rs.getString("kt_smadur_3");
			
			if( current.year.contains("_fyrra") ) {
				current.reksth2 = rs.getString("rekstrarhagnadur");
				current.ebidta2 = rs.getString("ebitda");
				current.hagn2 = rs.getString("hagnadur_arsins");
				current.fjart2 = rs.getString("fjarmunatekjur");
				current.fastaf2 = rs.getString("fastafjarmunir");
				current.aflah2 = rs.getString("aflaheimildir");
				current.veltuf2 = rs.getString("veltufjarmunir");
				current.eignir2 = rs.getString("eignir");
				current.eigidf2 = rs.getString("eigid_fe");
				current.skuldb2 = rs.getString("skuldbindingar_og_vikjandi_lan");
				current.langtsk2 = rs.getString("langtimaskuldir");
				current.skammtsk2 = rs.getString("skammtimaskuldir");
				current.handbf2 = rs.getString("handbaert_fe_til_rekstrar");
				current.fjfh2 = rs.getString("fjarfestingarhreyfingar");
				current.fjmh2 = rs.getString("fjarmognunnarhreyfingar");
				current.hbfa2 = rs.getString("handbaert_fe_i_arslok");
				current.skuld2 = rs.getString("skuldir");
				current.skeig2 = rs.getString("skuldur_og_eigid_fe");
			} else {
				current.reksth = rs.getString("rekstrarhagnadur");
				current.ebidta = rs.getString("ebitda");
				current.hagn = rs.getString("hagnadur_arsins");
				current.fjart = rs.getString("fjarmunatekjur");
				current.fastaf = rs.getString("fastafjarmunir");
				current.aflah = rs.getString("aflaheimildir");
				current.veltuf = rs.getString("veltufjarmunir");
				current.eignir = rs.getString("eignir");
				current.eigidf = rs.getString("eigid_fe");
				current.skuldb = rs.getString("skuldbindingar_og_vikjandi_lan");
				current.langtsk = rs.getString("langtimaskuldir");
				current.skammtsk = rs.getString("skammtimaskuldir");
				current.handbf = rs.getString("handbaert_fe_til_rekstrar");
				current.fjfh = rs.getString("fjarfestingarhreyfingar");
				current.fjmh = rs.getString("fjarmognunnarhreyfingar");
				current.hbfa = rs.getString("handbaert_fe_i_arslok");
				current.skuld = rs.getString("skuldir");
				current.skeig = rs.getString("skuldur_og_eigid_fe");
			}
			
			// her var current.handbf
			current.avox = rs.getString("avoxtunarkrafa");
			
			current.skra = rs.getString("skraningaradili");
			current.skranafn = rs.getString("skranafn");
			
			if( current.skranafn == null || current.skranafn.length() == 0 ) current.skranafn = this.getSelectedFile().getName();
		}
	}
	
	public void fillGreen() throws SQLException, ClassNotFoundException {
		greenList.clear();
		
		connect();
		PreparedStatement ps = con.prepareStatement("select distinct(skranafn) from [hafsjor].[dbo].[skyrslur]");
		ResultSet rs = ps.executeQuery();
		
		Set<String>	sset = new HashSet<String>();
		while( rs.next() ) {
			sset.add( rs.getString(1) );
		}

		for( int i = 0; i < filelist.getModel().getSize(); i++ ) {
			String fpath = (String)filelist.getModel().getElementAt(i);
			File f = new File( path + fpath );
			if( sset.contains( f.getName() ) ) {
				greenList.add(i);
			}
		}
		
		rs.close();
		ps.close();		
	}
	
	public double stuff( String str ) throws NumberFormatException {
		boolean	neg = false;
		if( str.contains("(") ) {
			neg = true;
		}
		//String nstr = str.replace(".", "");
		String nstr = str.replace("(", "");
		nstr = nstr.replace(")", "");
		double ret = (nstr != null && nstr.length() > 0) ? Double.parseDouble(nstr) : 0.0;
		
		if( neg ) ret = -ret;
		
		return ret;
	}
	
	Set<Integer>	greenList = new HashSet<Integer>();
	Tika 					t;
	public GiskPanel( final Container cont ) throws SQLException, ClassNotFoundException {
		super();
		this.setLayout( new BorderLayout() );
		
		TikaConfig config = TikaConfig.getDefaultConfig();
		t = new Tika( config );
		
		splitpane = new JSplitPane( JSplitPane.VERTICAL_SPLIT );
		splitpane.setOneTouchExpandable( true );
		
		final Color alph = new Color( 128,128,255,128 );
		filelist = new JList() {
			public void paintComponent( Graphics g ) {
				super.paintComponent( g );
			}
		};
		filelist.setCellRenderer( new DefaultListCellRenderer() {
			public Component getListCellRendererComponent( JList list, Object value, int index, boolean isSelected, boolean cellHasFocus ) {
				Component comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				
				if( greenList.contains(index) ) {
					comp.setBackground( alph );
				}
				
				return comp;
			}
		});
		//filelist.getCellRenderer();
		filelist.addMouseListener( new MouseAdapter() {
			public void mousePressed( MouseEvent e ) {
				filelist.requestFocus();
			}
		});
		scrollpane = new JScrollPane( filelist );
		openbutton = new JButton( new AbstractAction("Opna") {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					filelist.setModel( openDir() );
					fillGreen();
				} catch (SQLException e1) {
					e1.printStackTrace();
				} catch (ClassNotFoundException e1) {
					e1.printStackTrace();
				}
			}
		});
		
		comp = new JComponent() {
			public void setBounds( int x, int y, int w, int h ) {			
				super.setBounds(x, y, w, h);
				
				nameLabel.setBounds( 0,0,100,25 );
				nameField.setBounds( 100,0,250,25 );
				ktLabel.setBounds( 0,30,100,25 );
				ktField.setBounds( 100,30,250,25 );
				yearLabel.setBounds( 0,60,100,25 );
				yearField.setBounds( 100,60,250,25 );
				
				endLabel.setBounds(0, 120, 100, 25);
				endField.setBounds(100, 120, 250, 25);
				endktLabel.setBounds(0, 150, 100, 25);
				endktField.setBounds(100, 150, 250, 25);
				
				endfLabel.setBounds(0, 180, 100, 25);
				endfField.setBounds(100, 180, 250, 25);
				endfktLabel.setBounds(0, 210, 100, 25);
				endfktField.setBounds(100, 210, 250, 25);
				
				stjfLabel.setBounds(0, 270, 100, 25);
				stjfField.setBounds(100, 270, 250, 25);
				frmkvLabel.setBounds(0, 300, 100, 25);
				frmkvField.setBounds(100, 300, 250, 25);
				
				stj1Label.setBounds(0, 330, 100, 25);
				stj1Field.setBounds(100, 330, 250, 25);
				stj2Label.setBounds(0, 360, 100, 25);
				stj2Field.setBounds(100, 360, 250, 25);
				stj3Label.setBounds(0, 390, 100, 25);
				stj3Field.setBounds(100, 390, 250, 25);
				
				stjkt1Label.setBounds(0, 330, 100, 25);
				stjkt1Field.setBounds(100, 330, 250, 25);
				stjkt2Label.setBounds(0, 360, 100, 25);
				stjkt2Field.setBounds(100, 360, 250, 25);
				stjkt3Label.setBounds(0, 390, 100, 25);
				stjkt3Field.setBounds(100, 390, 250, 25);
				
				gjldmLabel.setBounds(0, 450, 100, 25);
				gjldmField.setBounds(100, 450, 250, 25);
				
				int fwidth = 150;
				reksthLabel.setBounds(0, 480, 100, 25);
				reksthField.setBounds(100, 480, fwidth, 25);
				ebidtaLabel.setBounds(0, 510, 100, 25);
				ebidtaField.setBounds(100, 510, fwidth, 25);
				hagnLabel.setBounds(0, 540, 100, 25);
				hagnField.setBounds(100, 540, fwidth, 25);
				fjartLabel.setBounds(0, 570, 100, 25);
				fjartField.setBounds(100, 570, fwidth, 25);
				fastafLabel.setBounds(0, 600, 100, 25);
				fastafField.setBounds(100, 600, fwidth, 25);
				aflahLabel.setBounds(0, 630, 100, 25);
				aflahField.setBounds(100, 630, fwidth, 25);
				veltufLabel.setBounds(0, 660, 100, 25);
				veltufField.setBounds(100, 660, fwidth, 25);
				eignirLabel.setBounds(0, 720, 100, 25);
				eignirField.setBounds(100, 720, fwidth, 25);
				eigidfLabel.setBounds(0, 750, 100, 25);
				eigidfField.setBounds(100, 750, fwidth, 25);
				skuldbLabel.setBounds(0, 780, 100, 25);
				skuldbField.setBounds(100, 780, fwidth, 25);
				langtskLabel.setBounds(0, 810, 100, 25);
				langtskField.setBounds(100, 810, fwidth, 25);
				skammtskLabel.setBounds(0, 840, 100, 25);
				skammtskField.setBounds(100, 840, fwidth, 25);
				handbfLabel.setBounds(0, 870, 100, 25);
				handbfField.setBounds(100, 870, fwidth, 25);
				fjfhLabel.setBounds(0, 900, 100, 25);
				fjfhField.setBounds(100, 900, fwidth, 25);
				fjmhLabel.setBounds(0, 930, 100, 25);
				fjmhField.setBounds(100, 930, fwidth, 25);
				hbfaLabel.setBounds(0, 960, 100, 25);
				hbfaField.setBounds(100, 960, fwidth, 25);
				skuldLabel.setBounds(0, 990, 100, 25);
				skuldField.setBounds(100, 990, fwidth, 25);
				skeigLabel.setBounds(0, 1020, 100, 25);
				skeigField.setBounds(100, 1020, fwidth, 25);
				
				avoLabel.setBounds(0, 1050, 100, 25);
				avoField.setBounds(100, 1050, 250, 25);
				
				gjldmField2.setBounds(400, 450, 0, 0);
				
				int strt = 300;
				reksthField2.setBounds(strt, 480, fwidth, 25);
				ebidtaField2.setBounds(strt, 510, fwidth, 25);
				hagnField2.setBounds(strt, 540, fwidth, 25);
				fjartField2.setBounds(strt, 570, fwidth, 25);
				fastafField2.setBounds(strt, 600, fwidth, 25);
				aflahField2.setBounds(strt, 630, fwidth, 25);
				veltufField2.setBounds(strt, 660, fwidth, 25);
				eignirField2.setBounds(strt, 720, fwidth, 25);
				eigidfField2.setBounds(strt, 750, fwidth, 25);
				skuldbField2.setBounds(strt, 780, fwidth, 25);
				langtskField2.setBounds(strt, 810, fwidth, 25);
				skammtskField2.setBounds(strt, 840, fwidth, 25);
				handbfField2.setBounds(strt, 870, fwidth, 25);
				fjfhField2.setBounds(strt, 900, fwidth, 25);
				fjmhField2.setBounds(strt, 930, fwidth, 25);
				hbfaField2.setBounds(strt, 960, fwidth, 25);
				skuldField2.setBounds(strt, 990, fwidth, 25);
				skeigField2.setBounds(strt, 1020, fwidth, 25);
			}
		};
		
		comp.addMouseListener( new MouseAdapter() {
			public void mousePressed( MouseEvent m ) {
				if( cont instanceof JFrame ) {
					Component comp1 = ((JFrame)cont).getFocusOwner();
    				System.err.println( comp1 );
				}
			}
		});
		
		comp.add( nameLabel );
		comp.add( nameField );
		comp.add( ktLabel );
		comp.add( ktField );
		comp.add( yearLabel );
		comp.add( yearField );
		
		comp.add( endLabel );
		comp.add( endField );
		comp.add( endktLabel );
		comp.add( endktField );
		
		comp.add( endfLabel );
		comp.add( endfField );
		comp.add( endfktLabel );
		comp.add( endfktField );
		
		comp.add( stjfLabel );
		comp.add( stjfField );
		comp.add( frmkvLabel );
		comp.add( frmkvField );
		
		comp.add( stj1Label );
		comp.add( stj1Field );
		comp.add( stj2Label );
		comp.add( stj2Field );
		comp.add( stj3Label );
		comp.add( stj3Field );
		
		comp.add(		gjldmLabel );
		comp.add(	gjldmField );
		comp.add(		reksthLabel );
		comp.add(	reksthField );
		comp.add(		ebidtaLabel );
		comp.add(	ebidtaField );
		comp.add(		hagnLabel );
		comp.add(	hagnField );
		comp.add(		fjartLabel );
		comp.add(	fjartField );
		comp.add(		fastafLabel );
		comp.add(	fastafField );
		comp.add(		aflahLabel );
		comp.add(	aflahField );
		comp.add(		veltufLabel );
		comp.add(	veltufField );
		comp.add(		eignirLabel );
		comp.add(	eignirField );
		comp.add(		eigidfLabel );
		comp.add(	eigidfField );
		comp.add(		skuldbLabel );
		comp.add(	skuldbField );
		comp.add(		langtskLabel );
		comp.add(	langtskField );
		comp.add(		skammtskLabel );
		comp.add(	skammtskField );
		comp.add(		handbfLabel );
		comp.add(	handbfField );
		comp.add(		fjfhLabel );
		comp.add(	fjfhField );
		comp.add(		fjmhLabel );
		comp.add(	fjmhField );
		comp.add( hbfaLabel );
		comp.add( hbfaField );
		comp.add( skuldLabel );
		comp.add( skuldField );
		comp.add( skeigLabel );
		comp.add( skeigField );
		
		comp.add( avoLabel );
		comp.add( avoField );
		
		comp.add( gjldmField2 );
		comp.add( reksthField2 );
		comp.add( ebidtaField2 );
		comp.add( hagnField2 );
		comp.add( fjartField2 );
		comp.add( fastafField2 );
		comp.add( aflahField2 );
		comp.add( veltufField2 );
		comp.add( eignirField2 );
		comp.add( eigidfField2 );
		comp.add( skuldbField2 );
		comp.add( langtskField2 );
		comp.add( skammtskField2 );
		comp.add( handbfField2 );
		comp.add( fjfhField2 );
		comp.add( fjmhField2 );
		comp.add( hbfaField2 );
		comp.add( skuldField2 );
		comp.add( skeigField2 );
		
		comp.setPreferredSize( new Dimension(400,1100) );
		
		userLabel = new JLabel( System.getProperty("user.name") );
		JButton vist = new JButton( new AbstractAction("Vista í grunn") {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					connect();
					PreparedStatement ps = con.prepareStatement("select * from [hafsjor].[dbo].[skyrslur] where skranafn = ?");
					ps.setString( 1, getSelectedFile().getName() );
					ResultSet rs = ps.executeQuery();
					
					boolean update = false;
					while( rs.next() ) {
						update = true;
						break;
					}
					ps.close();
					
					updateCurrent();
					if( update ) {
						ps = con.prepareStatement("update [hafsjor].[dbo].[skyrslur] set ar = ?, " +
								"gjaldmidill = ?, " +
								"heiti_fyr = ?, " +
								"kt_fyr = ?, " +
								"nafn_endurskodanda = ?, " +
								"kt_endurskodanda = ?, " +
								"endurskodunar_fyr = ?, " +
								"kt_endurskodunar_fyr = ?, " +
								"stjornarformadur = ?, " +
								"kt_stjornarformadur = ?, " +
								"smadur_1 = ?, " +
								"kt_smadur_1 = ?, " +
								"smadur_2 = ?, " +
								"kt_smadur_2 = ?, " +
								"framkvaemdarstjori = ?, " +
								"kt_framkvaemdarstjori = ?, " +
								"rekstrarhagnadur = ?, " +
								"ebitda = ?, " +
								"hagnadur_arsins = ?, " +
								"fjarmunatekjur = ?, " +
								"fastafjarmunir = ?, " +
								"aflaheimildir = ?, " +
								"veltufjarmunir = ?, " +
								"eignir = ?, " +
								"eigid_fe = ?, " +
								"skuldbindingar_og_vikjandi_lan = ?, " +
								"langtimaskuldir = ?, " +
								"skammtimaskuldir = ?, " +
								"handbaert_fe_til_rekstrar = ?, " +
								"fjarfestingarhreyfingar = ?, " +
								"fjarmognunnarhreyfingar = ?, " +
								"handbaert_fe_i_arslok = ?, " +
								"avoxtunarkrafa = ?, " +
								"skuldir = ?, " +
								"skuldur_og_eigid_fe = ? " +
								"where skranafn = ? and ar not like ?");
						ps.setString( 1, current.year );
						ps.setString( 2, "ISK" );
						ps.setString( 3, current.name );
						ps.setString( 4, current.kt );
						ps.setString( 5, current.endursk );
						ps.setString( 6, current.endurkt );
						ps.setString( 7, current.endurskf );
						ps.setString( 8, current.endurktf );
						ps.setString( 9, current.stjf );
						ps.setString( 10, current.stjfkt );
						ps.setString( 11, current.stj1 );
						ps.setString( 12, current.stjkt1 );
						ps.setString( 13, current.stj2 );
						ps.setString( 14, current.stjkt2 );
						ps.setString( 15, current.frmkv );
						ps.setString( 16, current.frmkvkt );
						ps.setDouble( 17, stuff(current.reksth) );
						ps.setDouble( 18, stuff(current.ebidta) );
						ps.setDouble( 19, stuff(current.hagn) );
						ps.setDouble( 20, stuff(current.fjart) );
						ps.setDouble( 21, stuff(current.fastaf) );
						ps.setDouble( 22, stuff(current.aflah) );
						ps.setDouble( 23, stuff(current.veltuf) );
						ps.setDouble( 24, stuff(current.eignir) );
						ps.setDouble( 25, stuff(current.eigidf) );
						ps.setDouble( 26, stuff(current.skuldb) );
						ps.setDouble( 27, stuff(current.langtsk) );
						ps.setDouble( 28, stuff(current.skammtsk) );
						ps.setDouble( 29, stuff(current.handbf) );
						ps.setDouble( 30, stuff(current.fjfh) );
						ps.setDouble( 31, stuff(current.fjmh) );
						ps.setDouble( 32, stuff(current.hbfa) );
						ps.setDouble( 33, stuff(current.avox) );
						ps.setDouble( 34, stuff(current.skuld) );
						ps.setDouble( 35, stuff(current.skeig) );
						ps.setString( 36, current.skranafn );
						ps.setString( 37, "%_fyrra" );
						ps.execute();
						ps.close();
						
						ps = con.prepareStatement("update [hafsjor].[dbo].[skyrslur] set ar = ?, " +
								"gjaldmidill = ?, " +
								"heiti_fyr = ?, " +
								"kt_fyr = ?, " +
								"nafn_endurskodanda = ?, " +
								"kt_endurskodanda = ?, " +
								"endurskodunar_fyr = ?, " +
								"kt_endurskodunar_fyr = ?, " +
								"stjornarformadur = ?, " +
								"kt_stjornarformadur = ?, " +
								"smadur_1 = ?, " +
								"kt_smadur_1 = ?, " +
								"smadur_2 = ?, " +
								"kt_smadur_2 = ?, " +
								"framkvaemdarstjori = ?, " +
								"kt_framkvaemdarstjori = ?, " +
								"rekstrarhagnadur = ?, " +
								"ebitda = ?, " +
								"hagnadur_arsins = ?, " +
								"fjarmunatekjur = ?, " +
								"fastafjarmunir = ?, " +
								"aflaheimildir = ?, " +
								"veltufjarmunir = ?, " +
								"eignir = ?, " +
								"eigid_fe = ?, " +
								"skuldbindingar_og_vikjandi_lan = ?, " +
								"langtimaskuldir = ?, " +
								"skammtimaskuldir = ?, " +
								"handbaert_fe_til_rekstrar = ?, " +
								"fjarfestingarhreyfingar = ?, " +
								"fjarmognunnarhreyfingar = ?, " +
								"handbaert_fe_i_arslok = ?, " +
								"avoxtunarkrafa = ?, " +
								"skuldir = ?, " +
								"skuldur_og_eigid_fe = ? " +
								"where skranafn = ? and ar like ?");
						ps.setString( 1, current.year+"_fyrra" );
						ps.setString( 2, "ISK" );
						ps.setString( 3, current.name );
						ps.setString( 4, current.kt );
						ps.setString( 5, current.endursk );
						ps.setString( 6, current.endurkt );
						ps.setString( 7, current.endurskf );
						ps.setString( 8, current.endurktf );
						ps.setString( 9, current.stjf );
						ps.setString( 10, current.stjfkt );
						ps.setString( 11, current.stj1 );
						ps.setString( 12, current.stjkt1 );
						ps.setString( 13, current.stj2 );
						ps.setString( 14, current.stjkt2 );
						ps.setString( 15, current.frmkv );
						ps.setString( 16, current.frmkvkt );
						ps.setDouble( 17, stuff(current.reksth2) );
						ps.setDouble( 18, stuff(current.ebidta2) );
						ps.setDouble( 19, stuff(current.hagn2) );
						ps.setDouble( 20, stuff(current.fjart2) );
						ps.setDouble( 21, stuff(current.fastaf2) );
						ps.setDouble( 22, stuff(current.aflah2) );
						ps.setDouble( 23, stuff(current.veltuf2) );
						ps.setDouble( 24, stuff(current.eignir2) );
						ps.setDouble( 25, stuff(current.eigidf2) );
						ps.setDouble( 26, stuff(current.skuldb2) );
						ps.setDouble( 27, stuff(current.langtsk2) );
						ps.setDouble( 28, stuff(current.skammtsk2) );
						ps.setDouble( 29, stuff(current.handbf2) );
						ps.setDouble( 30, stuff(current.fjfh2) );
						ps.setDouble( 31, stuff(current.fjmh2) );
						ps.setDouble( 32, stuff(current.hbfa2) );
						ps.setDouble( 33, stuff(current.avox) );
						ps.setDouble( 34, stuff(current.skuld2) );
						ps.setDouble( 35, stuff(current.skeig2) );
						ps.setString( 36, current.skranafn );
						ps.setString( 37, "%_fyrra" );
						ps.execute();
						
						ps.close();
						
						JOptionPane.showMessageDialog(GiskPanel.this, "Tókst að uppfæra" );
					} else {
						ps = con.prepareStatement("insert into [hafsjor].[dbo].[skyrslur] (ar," +
								"gjaldmidill," +
								"heiti_fyr," +
								"kt_fyr," +
								"nafn_endurskodanda," +
								"kt_endurskodanda," +
								"endurskodunar_fyr," +
								"kt_endurskodunar_fyr," +
								"stjornarformadur," +
								"kt_stjornarformadur," +
								"smadur_1," +
								"kt_smadur_1," +
								"smadur_2," +
								"kt_smadur_2," +
								"framkvaemdarstjori," +
								"kt_framkvaemdarstjori," +
								"rekstrarhagnadur," +
								"ebitda," +
								"hagnadur_arsins," +
								"fjarmunatekjur," +
								"fastafjarmunir," +
								"aflaheimildir," +
								"veltufjarmunir," +
								"eignir," +
								"eigid_fe," +
								"skuldbindingar_og_vikjandi_lan," +
								"langtimaskuldir," +
								"skammtimaskuldir," +
								"handbaert_fe_til_rekstrar," +
								"fjarfestingarhreyfingar," +
								"fjarmognunnarhreyfingar," +
								"handbaert_fe_i_arslok," +
								"avoxtunarkrafa," +
								"skuldir," +
								"skuldur_og_eigid_fe," +
								"skraningaradili," +
								"skranafn) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
						ps.setString( 1, current.year );
						ps.setString( 2, "ISK" );
						ps.setString( 3, current.name );
						ps.setString( 4, current.kt );
						ps.setString( 5, current.endursk );
						ps.setString( 6, current.endurkt );
						ps.setString( 7, current.endurskf );
						ps.setString( 8, current.endurktf );
						ps.setString( 9, current.stjf );
						ps.setString( 10, current.stjfkt );
						ps.setString( 11, current.stj1 );
						ps.setString( 12, current.stjkt1 );
						ps.setString( 13, current.stj2 );
						ps.setString( 14, current.stjkt2 );
						ps.setString( 15, current.frmkv );
						ps.setString( 16, current.frmkvkt );
						
						ps.setDouble( 17, stuff(current.reksth) );
						ps.setDouble( 18, stuff(current.ebidta) );
						ps.setDouble( 19, stuff(current.hagn) );
						ps.setDouble( 20, stuff(current.fjart) );
						ps.setDouble( 21, stuff(current.fastaf) );
						ps.setDouble( 22, stuff(current.aflah) );
						ps.setDouble( 23, stuff(current.veltuf) );
						ps.setDouble( 24, stuff(current.eignir) );
						ps.setDouble( 25, stuff(current.eigidf) );
						ps.setDouble( 26, stuff(current.skuldb) );
						ps.setDouble( 27, stuff(current.langtsk) );
						ps.setDouble( 28, stuff(current.skammtsk) );
						ps.setDouble( 29, stuff(current.handbf) );
						ps.setDouble( 30, stuff(current.fjfh) );
						ps.setDouble( 31, stuff(current.fjmh) );
						ps.setDouble( 32, stuff(current.hbfa) );
						ps.setDouble( 33, stuff(current.skuld) );
						ps.setDouble( 34, stuff(current.skeig) );
						
						ps.setDouble( 35, stuff(current.avox) );
						ps.setString( 36, current.skra );
						ps.setString( 37, current.skranafn );
						ps.execute();
						
						ps.setString( 1, current.year+"_fyrra" );
						ps.setDouble( 17, stuff(current.reksth2) );
						ps.setDouble( 18, stuff(current.ebidta2) );
						ps.setDouble( 19, stuff(current.hagn2) );
						ps.setDouble( 20, stuff(current.fjart2) );
						ps.setDouble( 21, stuff(current.fastaf2) );
						ps.setDouble( 22, stuff(current.aflah2) );
						ps.setDouble( 23, stuff(current.veltuf2) );
						ps.setDouble( 24, stuff(current.eignir2) );
						ps.setDouble( 25, stuff(current.eigidf2) );
						ps.setDouble( 26, stuff(current.skuldb2) );
						ps.setDouble( 27, stuff(current.langtsk2) );
						ps.setDouble( 28, stuff(current.skammtsk2) );
						ps.setDouble( 29, stuff(current.handbf2) );
						ps.setDouble( 30, stuff(current.fjfh2) );
						ps.setDouble( 31, stuff(current.fjmh2) );
						ps.setDouble( 32, stuff(current.hbfa2) );
						ps.setDouble( 33, stuff(current.skuld2) );
						ps.setDouble( 34, stuff(current.skeig2) );
						ps.execute();
						
						greenList.add( current.index );
						filelist.repaint();
						
						JOptionPane.showMessageDialog(GiskPanel.this, "Tókst" );
					}
				} catch (SQLException e1) {
					JOptionPane.showMessageDialog( GiskPanel.this, e1.getMessage() );
				} catch (ClassNotFoundException e1) {
					JOptionPane.showMessageDialog( GiskPanel.this, e1.getMessage() );
				} catch (MalformedURLException e1) {
					JOptionPane.showMessageDialog( GiskPanel.this, e1.getMessage() );
				} catch (NumberFormatException e1) {
					JOptionPane.showMessageDialog( GiskPanel.this, e1.getMessage() );
				}
			}
		});
		
		JButton	fetchGrunn = new JButton( new AbstractAction("Sækja í grunn") {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					fetchDb( current );
					fillForm();
				} catch (SQLException e1) {
					e1.printStackTrace();
				} catch (ClassNotFoundException e1) {
					e1.printStackTrace();
				} catch (MalformedURLException e2) {
					e2.printStackTrace();
				}
			}
		});
		
		JButton	fetchPfd = new JButton( new AbstractAction("Sækja í pdf") {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					fetchPdf( getSelectedFile() );
					fillForm();
				} catch (MalformedURLException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		});
		
		JComponent compcomp = new JComponent() {};
		subscroll = new JScrollPane( comp );
		subscroll.getViewport().setBackground( Color.white );
		//subscroll.setBackground( Color.white );
		compcomp.setLayout( new BorderLayout() );
		compcomp.add( subscroll );
		
		JComponent buttComp = new JComponent() {};
		buttComp.setLayout( new FlowLayout() );
		buttComp.add( userLabel );
		buttComp.add( vist );
		buttComp.add( fetchGrunn );
		buttComp.add( fetchPfd );
		compcomp.add( buttComp, BorderLayout.SOUTH );
		
		splitpane.setTopComponent( compcomp );
		splitpane.setBottomComponent( scrollpane );
		
		JComponent buttComp2 = new JComponent() {};
		buttComp2.setLayout( new FlowLayout() );
		buttComp2.add( openbutton );
		this.add( buttComp2, BorderLayout.SOUTH );
		this.add( splitpane );
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {		
		JFrame frame = new JFrame();
		frame.setSize( 800,600 );
		GiskPanel gisk;
		try {
			gisk = new GiskPanel( frame );
			frame.add( gisk );
			frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
			frame.setVisible( true );
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}
