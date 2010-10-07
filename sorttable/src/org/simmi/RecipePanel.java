package org.simmi;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TooManyListenersException;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.DefaultCellEditor;
import javax.swing.DropMode;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

public class RecipePanel extends JSplitPane {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	final JCompatTable		recipeTable = new JCompatTable();
	final JCompatTable		recipeDetailTable = new JCompatTable();
	Map<String,Integer>	foodInd;
	FriendsPanel		fp;
	Map<String,Map<String,String>> skmt = new HashMap<String,Map<String,String>>();
	Set<String>			allskmt = new HashSet<String>();
	JComboBox 			skmtCombo;
	int					clearCombo = -1;
	char[]				cbuf = new char[2048];
	String				lang;
	
	public static Map<String,String>	getUnitVal( String stuff, Map<String,Map<String,String>> skmt ) {
		Map<String,String>	values = null;
		
		if( skmt.containsKey( stuff.toLowerCase() ) ) {
			values = skmt.get( stuff.toLowerCase() );
		} else {
			String store = "";
			int max = 0;
			
			List<String> pars = Arrays.asList( stuff.toLowerCase().split("[, ]+") );
			for( String str : skmt.keySet() ) {
				List<String> vals = Arrays.asList( str.split("[, ]+") );
				
				int count = 0;
				for( String a : pars ) {
					if( !(a.contains("steikt") || a.contains("soðin")) && vals.contains(a) ) {
						count++;
					}
				}
				
				if( count > max ) {
					max = count;
					store = str;
				}
			}
			
			if( store != null && store.length() > 0 ) values = skmt.get( store );
		}
		
		return values;
	}
	
	public class RecipeIngredient {
		String				stuff;
		float				measure;
		String				unit;
		Map<String,String>	values;
		JComboBox			cellEdit;
		
		public RecipeIngredient( String stuff, float measure, String unit ) {
			this.stuff = stuff;
			this.measure = measure;
			this.unit = unit;
			
			values = getUnitVal( stuff, skmt );
			
			cellEdit = new JComboBox();
			
			cellEdit.addItem("g");
			if( values != null ) {
				for( String str : values.keySet() ) {
					cellEdit.addItem( str + " ("+values.get(str)+")" );
				}
			}
			
			cellEdit.setSelectedItem( unit );
		}
		
		public float getValue( float val ) {
			float d = this.measure;
			if (!this.unit.equals("g")) {
				String ru = this.unit;
				int f = ru.indexOf("(");
				int n = ru.indexOf(")");
				if (n > f && f != -1) {
					String subbi = ru.substring(f + 1, n);
					if( subbi.endsWith("g") ) {
						subbi = subbi.substring(0, subbi.length() - 1);
					}

					float fl = 0.0f;
					try {
						fl = Float.parseFloat(subbi);
					} catch (Exception e) {

					}
					d *= fl;
				}
			}
			//tot += d;

			float f = (val * d);
			
			return f;
		}
	};
	
	public class Recipe {
		String					group;
		String					name;
		String					author;
		String					desc;
		List<RecipeIngredient>	ingredients;
		
		public Recipe( String name, String group, String author ) {
			this.name = name;
			this.group = group;
			this.author = author;
			
			ingredients = new ArrayList<RecipeIngredient>();
		}
		
		public Recipe( String author ) {
			this.name = lang.equals("IS") ? "Velja nafn" : "Choose name";
			this.group = lang.equals("IS") ? "Velja hóp" : "Choose group";
			this.author = author;
			
			ingredients = new ArrayList<RecipeIngredient>();
		}
		
		public void addIngredient( String stuff, float measure, String unit ) {
			ingredients.add( new RecipeIngredient(stuff, measure, unit) );
		}
		
		public void setDescription( String desc ) {
			this.desc = desc;
		}
		
		public String toString() {
			String ret = name+"\n"+group+"\n"+author+"\n\n";
			for( RecipeIngredient ri : ingredients ) {
				ret += ri.stuff + "\t" + ri.measure + "\t" + ri.unit + "\n";
			}
			if( desc != null ) ret += "\n"+desc;
			return ret;
		}
		
		public void destroy() {
			File f = new File( System.getProperty("user.home"), ".isgem" );
			f = new File( f, "recipes" );
			if( f.exists() ) {
				String fname = Integer.toString( Math.abs( this.toString().hashCode()) );
				f = new File( f, fname );
				f.delete();
			}
		}
		
		public void save() throws IOException {
			File f = new File( System.getProperty("user.home"), ".isgem" );
			f = new File( f, "recipes" );
			if( !f.exists() ) {
				f.mkdirs();
			}
			String str = this.toString();
			String fname = Integer.toString( Math.abs( str.hashCode() ) );
			f = new File( f, fname );
			FileWriter	fw = new FileWriter( f );
			fw.write( str );
			fw.close();
		}
		
		public float getWeight() {
			float w = 0.0f;
			for( RecipeIngredient ri : ingredients ) {
				float d = ri.measure;
				if (!ri.unit.equals("g")) {
					String ru = ri.unit;
					int f = ru.indexOf("(");
					int n = ru.indexOf(")");
					if (n > f && f != -1) {
						String subbi = ru.substring(f + 1, n);
						if (subbi.endsWith("g"))
							subbi = subbi.substring(0, subbi.length() - 1);

						float fl = 0.0f;
						try {
							fl = Float.parseFloat(subbi);
						} catch (Exception e) {

						}
						d *= fl;
					}
				}
				w += d;
			}
			return w;
		}
	};
	
	public void fillSkmt() throws IOException {
		InputStream is = this.getClass().getResourceAsStream("/skmt.txt");
		
		BufferedReader br = new BufferedReader( new InputStreamReader(is, "UTF-8") );
		String line = br.readLine();
		while( line != null ) {
			String[] split = line.split("\\t");
			if( split.length > 1 && split[1].length() > 0 ) {
				Map<String,String> var = new HashMap<String,String>();
				String[] subspl = split[1].split("g,[ ]+");
				for( String str : subspl ) {
					String[] aspl = str.replace('=', '-').split("-");
					if( aspl.length > 1 ) {
						String val = aspl[1];
						if( !str.equals(subspl[subspl.length-1]) ) val+="g";
						var.put(aspl[0], val);
						
						allskmt.add( aspl[0] + " ("+val+")" );
					}
				}
				skmt.put(split[0].toLowerCase(), var);
			}
			line = br.readLine();
		}
		br.close();
	}
	
	List<Recipe>	recipes;
	Recipe			currentRecipe;
	JDialog			dialog;
	JCompatTable	mailTable;
	JScrollPane		scrollPane;
	
	public void checkMail() throws IOException {
		try {
			System.getSecurityManager().checkConnect("test.matis.is", 80);
			URL url = new URL( "http://test.matis.is/isgem/getr.php" );
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");
			
			Integer.toString( Math.abs( this.toString().hashCode()) );
			String write = "user="+fp.currentUserId;
			
			connection.getOutputStream().write( write.getBytes() );
			connection.getOutputStream().flush();
			connection.getOutputStream().close();
			
			byte[] bb = new byte[8192];
			connection.getInputStream().read(bb);
			
			String s = new String( bb );
			final String[] ss = s.split("\n");
			
			//JOptionPane	opt = new JOptionPane(ss.length + " uppskriftir í pósthólfi. Viltu taka við þeim?", JOptionPane.YES_NO_CANCEL_OPTION);
			
			if( ss.length > 1 ) {
			String message = (ss.length-1) + " uppskriftir í pósthólfi. Viltu taka við þeim?";
				int val =  JOptionPane.showConfirmDialog(this, message, "Uppskriftir frá vinum", JOptionPane.YES_NO_CANCEL_OPTION);
				if( val != JOptionPane.CANCEL_OPTION ) {
					for( int i = 0; i < ss.length-1; i++ ) {
						String str = ss[i];
						String splt = str.split("\t")[0];
						
						url = new URL( "http://test.matis.is/isgem/getd.php" );
						connection = (HttpURLConnection)url.openConnection();
						connection.setDoInput(true);
						connection.setDoOutput(true);
						connection.setRequestMethod("POST");
						
						Integer.toString( Math.abs( this.toString().hashCode()) );
						write = "recipe="+splt;
						
						connection.getOutputStream().write( write.getBytes() );
						connection.getOutputStream().flush();
						connection.getOutputStream().close();
						
						if( val == JOptionPane.YES_OPTION ) {
							insertRecipe( new InputStreamReader( connection.getInputStream() ) );
						}
					}
				}
			}
		} catch( Exception e ) {
			
		}
			
		/*if( ss.length > 1 ) {
			TableModel model = new TableModel() {
				@Override
				public void addTableModelListener(TableModelListener l) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public Class<?> getColumnClass(int columnIndex) {
					return String.class;
				}

				@Override
				public int getColumnCount() {
					return 2;
				}

				@Override
				public String getColumnName(int columnIndex) {
					return "";
				}

				@Override
				public int getRowCount() {
					return ss.length-1;
				}

				@Override
				public Object getValueAt(int rowIndex, int columnIndex) {
					String[] str = ss[rowIndex].split("\t");
					if( columnIndex < str.length ) return str[columnIndex];
					
					return "";
				}

				@Override
				public boolean isCellEditable(int rowIndex, int columnIndex) {
					// TODO Auto-generated method stub
					return false;
				}

				@Override
				public void removeTableModelListener(TableModelListener l) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void setValueAt(Object aValue, int rowIndex,
						int columnIndex) {
				}
				
			};
			mailTable.setModel( model );
			dialog.setVisible( true );
		}*/
	}
	
	JCompatTable	theTable;
	JCompatTable	theLeftTable;
	public void insertRecipe( Reader r ) throws IOException {
		int read = r.read(cbuf);
		if( read > 0 ) {
			String recipe = new String( cbuf, 0, read );
			String[] spl = recipe.split("\n\n");
			
			Recipe rep = null;
			if( spl.length >= 1 ) {
				String[] split = spl[0].split("\n");
				String about = split[2];
				for( int i = 3; i < split.length; i++ ) {
					about += split[i];
				}
				rep = new Recipe( split[0], split[1], split[2] );
			}
			
			if( spl.length >= 2 ) {
				String[] split = spl[1].split("\n");
				for( String str : split ) {
					String[] subspl = str.split("\t");
					if( subspl.length > 2 ) {
						rep.ingredients.add( new RecipeIngredient(subspl[0], Float.parseFloat(subspl[1]), subspl[2]) );
					}
				}
			}
			
			if( spl.length >= 3 ) {
				rep.desc = "";
				int i;
				for( i = 2; i < spl.length-1; i++ ) {
					rep.desc += spl[i] + "\n\n";
				}
				//System.err.println("jospl " + spl[i] );
				rep.desc += spl[i];
			}
			
			if( spl.length >= 1 ) {
				boolean t = true;
				int i = Math.abs( rep.toString().hashCode() );
				for( Recipe rr : recipes ) {
					int ri = Math.abs( rr.toString().hashCode() );
					if( ri == i ) {
						t = false;
						break;
					}
				}
				if( t ) recipes.add( rep );
			}
		
			recipeTable.tableChanged( new TableModelEvent( recipeTable.getModel() ) );
			theTable.tableChanged( new TableModelEvent( theTable.getModel() ) );
			theLeftTable.tableChanged( new TableModelEvent( theLeftTable.getModel() ) );
		}
	}
	
	public void insertRepInfo( Object obj ) throws IOException {
		int r = recipeTable.getSelectedRow();
		Recipe rep = null;
		if( r >= 0 && r < recipes.size() ) {
			rep = recipes.get(r);								
			rep.destroy();
		} else {
			rep = new Recipe( fp.currentUser );
			recipes.add( rep );
			
			int ri = recipes.size()-1;
			recipeTable.tableChanged( new TableModelEvent( recipeTable.getModel() ) );
			theTable.tableChanged( new TableModelEvent( theTable.getModel() ) );
			theLeftTable.tableChanged( new TableModelEvent( theLeftTable.getModel() ) );
			recipeTable.setRowSelectionInterval(ri, ri);
		}
			
		String[] lines = obj.toString().split("\\n");
		for( String line : lines ) {
			String[] vals = line.split("\\t");
			rep.ingredients.add( new RecipeIngredient( vals[0], 100, "g" ) );
		}
		recipeDetailTable.revalidate();
		recipeDetailTable.repaint();
		
		rep.save();
	}
	
	public RecipePanel( final FriendsPanel fp, final String lang, final JCompatTable table, final JCompatTable leftTable, final Map<String,Integer> foodNameInd ) throws IOException {
		super( JSplitPane.VERTICAL_SPLIT );
		this.setDividerLocation( 300 );
		this.setOneTouchExpandable( true );
		this.lang = lang;
		
		theTable = table;
		theLeftTable = leftTable;
		
		mailTable = new JCompatTable();
		scrollPane = new JScrollPane( mailTable );
		dialog = new JDialog( CompatUtilities.getWindowAncestor( this ) );
		dialog.setSize(400, 300);
		dialog.add( scrollPane );
		
		this.addComponentListener( new ComponentListener(){
			public void componentShown(ComponentEvent e) {
				try {
					checkMail();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			public void componentResized(ComponentEvent e) {}
			public void componentMoved(ComponentEvent e) {}
			public void componentHidden(ComponentEvent e) {}
		});
		
		/*new Thread() {
			public void run() {
				try {
					fillSkmt();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}.run();*/
		fillSkmt();
		
		this.fp = fp;
		foodInd = foodNameInd;
		recipes = new ArrayList<Recipe>();
		
		try {
			File f = new File( System.getProperty("user.home"), ".isgem" );
			f = new File( f, "recipes" );
			File[] ff = f.listFiles();
			if( ff != null ) {
				for( File file : ff ) {
					FileReader	fr = new FileReader( file );
					insertRecipe( fr );	
					//String str = rep.toString();
					//System.err.println( str );
					
					fr.close();
				}
			}
		} catch( SecurityException se ) {
			
		}
		
		JScrollPane	recipeScroll = new JScrollPane();
		recipeScroll.getViewport().setBackground( Color.white );
		
		AbstractAction nu = new AbstractAction("Nýja uppskrift") {
			public void actionPerformed(ActionEvent e) {
				recipes.add( new Recipe(fp.currentUser) );
				recipeTable.revalidate();
				recipeTable.repaint();
				
				table.tableChanged( new TableModelEvent( table.getModel() ) );
				leftTable.tableChanged( new TableModelEvent( leftTable.getModel() ) );
			}
		};
		
		JPopupMenu	popup = new JPopupMenu();
		popup.add( nu );		
		recipeScroll.setComponentPopupMenu( popup );
		recipeTable.setComponentPopupMenu( popup );
		
		TableModel recipeTableModel = new TableModel() {
			public void addTableModelListener(TableModelListener arg0) {
				// TODO Auto-generated method stub
				
			}

			public Class<?> getColumnClass(int arg0) {
				return String.class;
			}

			public int getColumnCount() {
				return 3;
			}

			public String getColumnName(int arg0) {
				if( lang.equals("IS") ) {
					if( arg0 == 0 ) return "Nafn";
					else if( arg0 == 1 ) return "Hópur";
					else if( arg0 == 2 ) return "Höfundur";
				} else {
					if( arg0 == 0 ) return "Name";
					else if( arg0 == 1 ) return "Group";
					else if( arg0 == 2 ) return "Owner";
				}
				return null;
			}

			public int getRowCount() {
				return recipes.size();
			}

			public Object getValueAt(int arg0, int arg1) {
				if( arg0 < recipes.size() ) {
					Recipe rep = recipes.get(arg0);
					if( arg1 == 0 ) return rep.name;
					else if( arg1 == 1 ) return rep.group;
					else if( arg1 == 2 ) return rep.author;
				}
				return null;
			}

			public boolean isCellEditable(int arg0, int arg1) {
				if( arg1 == 0 || arg1 == 1 ) return true;
				return false;
			}

			public void removeTableModelListener(TableModelListener arg0) {}
			public void setValueAt(Object arg0, int arg1, int arg2) {
				Recipe rep = recipes.get(arg1);
				rep.destroy();
				if( arg2 == 0 ) rep.name = arg0.toString();
				else if( arg2 == 1 ) rep.group = arg0.toString();
				try {
					rep.save();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		recipeTable.setModel( recipeTableModel );
		recipeTable.setAutoCreateRowSorter( true );
		recipeScroll.setViewportView( recipeTable );
		
		TableModel	recipeDetailModel = new TableModel() {
			public void addTableModelListener(TableModelListener arg0) {
				// TODO Auto-generated method stub
				
			}

			public Class<?> getColumnClass(int arg0) {
				if( arg0 == 0 ) return String.class;
				else if( arg0 == 1 ) return Float.class;
				else if( arg0 == 2 ) return String.class;
				return null;
			}

			public int getColumnCount() {
				return 3;
			}

			public String getColumnName(int arg0) {
				if( lang.equals("IS") ) {
					if( arg0 == 0 ) return "Efni";
					else if( arg0 == 1 ) return "Magn";
					else if( arg0 == 2 ) return "Eining";
				} else {
					if( arg0 == 0 ) return "Stuff";
					else if( arg0 == 1 ) return "Value";
					else if( arg0 == 2 ) return "Unit";
				}
				return null;
			}

			public int getRowCount() {
				int r = recipeTable.getSelectedRow();
				if( r >= 0 && r < recipes.size() ) {
					Recipe rep = recipes.get(r);
					return rep.ingredients.size();
				}
				return 0;
			}

			public Object getValueAt(int arg0, int arg1) {
				int r = recipeTable.getSelectedRow();
				if( r >= 0 && r < recipes.size() ) {
					Recipe rep = recipes.get(r);
					if( arg0 >= 0 && arg0 < rep.ingredients.size() ) {
						RecipeIngredient repi = rep.ingredients.get(arg0);
						if( arg1 == 0 ) return repi.stuff;
						else if( arg1 == 1 ) return repi.measure;
						else if( arg1 == 2 ) return repi.unit;
					}
				}
				return null;
			}

			public boolean isCellEditable(int arg0, int arg1) {
				if( arg1 == 1 || arg1 == 2 ) return true;
				return false;
			}

			public void removeTableModelListener(TableModelListener arg0) {}
			public void setValueAt(Object arg0, int arg1, int arg2) {
				int r = recipeTable.getSelectedRow();
				
				int nr = recipeDetailTable.getSelectedRow();
				if( arg2 == 2 && nr != -1 ) {
					String item = (String)arg0;
					if( item != null ) {
						int rr = recipeTable.convertRowIndexToModel(r);
						if( rr >= 0 && rr < recipes.size() ) {
							Recipe rep = recipes.get(rr);
							RecipeIngredient ri = rep.ingredients.get( arg1 );
							if( !ri.unit.equals( item ) ) { 
								if( !item.equals("g") ) recipeDetailTable.setValueAt(1.0f, nr, 1);
								else recipeDetailTable.setValueAt(100.0f, nr, 1);
							}
						}
					}
						
					recipeDetailTable.repaint();
				}
				
				int rr = recipeTable.convertRowIndexToModel(r);
				if( rr >= 0 && rr < recipes.size() ) {
					Recipe rep = recipes.get(rr);
					rep.destroy();
					RecipeIngredient ri = rep.ingredients.get( arg1 );
					if( arg2 == 1 ) {
						ri.measure = (Float)arg0;
					} else if( arg0 != null ) {
						ri.unit = arg0.toString();
						
						ri.cellEdit.setSelectedItem( ri.unit );
					}
					try {
						rep.save();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			
		};
		
		AbstractAction eu = new AbstractAction("Eyða uppskrift/um") {
			public void actionPerformed(ActionEvent e) {
				int[]	rr = recipeTable.getSelectedRows();
				Set<Recipe>	remSet = new HashSet<Recipe>();
				for( int r : rr ) {
					if( r >= 0 && r < recipeTable.getRowCount() ) {
						int ri = recipeTable.convertRowIndexToModel(r);
						remSet.add( recipes.get(ri) );
					}
				}
				recipes.removeAll( remSet );
				for( Recipe rep : remSet ) {
					rep.destroy();
				}
				recipeTable.revalidate();
				recipeTable.repaint();
				
				currentRecipe = null;
				
				recipeDetailTable.revalidate();
				recipeDetailTable.repaint();
				
				table.tableChanged( new TableModelEvent( table.getModel() ) );
				leftTable.tableChanged( new TableModelEvent( leftTable.getModel() ) );				
			}
		};
		popup.add( eu );
		popup.addSeparator();
		
		AbstractAction du = new AbstractAction("Senda völdum vinum") {
			public void actionPerformed(ActionEvent arg0) {
				String[]	ids = fp.getSelectedFriendsIds();
				if( ids.length > 0 ) {
					try {
						Set<Recipe>	repSet = new HashSet<Recipe>();
						int[] rr = recipeTable.getSelectedRows();
						for( int r : rr ) {
							int rm = recipeTable.convertRowIndexToModel(r);
							repSet.add( recipes.get(rm) );
						}
						
						if( repSet.size() > 0 ) {
							String yourId = fp.currentUserId;
							
							URL url = new URL( "http://test.matis.is/isgem/recipe.php" );
							HttpURLConnection connection = (HttpURLConnection)url.openConnection();
							connection.setDoInput(true);
							connection.setDoOutput(true);
							connection.setRequestMethod("POST");
							
							Integer.toString( Math.abs( this.toString().hashCode()) );
							String write = "user="+yourId+"&friends=";
							
							String data = "";
							for( String str : ids ) {
								if( str.equals( ids[ids.length-1] ) ) data += str;
								else data += str+";";
							}
							write += URLEncoder.encode(data, "UTF8")+"&recipes=";
							
							data = "";
							int i = 0;
							for( Recipe r : repSet ) {
								String rstr = r.toString();
								String ival = Integer.toString( Math.abs( rstr.hashCode()) );
								if( i < repSet.size()-1 ) data += ival+";";
								else data += ival;
								
								i++;
							}
							write += URLEncoder.encode(data, "UTF8");
							
							for( Recipe r : repSet ) {
								String rstr = r.toString();
								String ival = Integer.toString( Math.abs( rstr.hashCode()) );
								
								write += "&"+ival+"=";
								write += URLEncoder.encode(rstr, "UTF8");	
							}
							
							connection.getOutputStream().write( write.getBytes() );
							connection.getOutputStream().flush();
							connection.getOutputStream().close();
							
							byte[] bb = new byte[128];
							connection.getInputStream().read(bb);
							/*for( String id : ids ) {
								String val = id+"_";
								connection.getOutputStream().write( id.getBytes() );
							}
							for( Recipe rep : repSet ) {
								String str = rep.toString();
								connection.getOutputStream().write( str.getBytes() );
							}*/
							//connection.disconnect();
							JOptionPane.showMessageDialog(RecipePanel.this, "Vinir hafa fengið uppskriftir");
						} else JOptionPane.showMessageDialog(RecipePanel.this, "Engar uppskriftir valdar");
					} catch (MalformedURLException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else 	JOptionPane.showMessageDialog(RecipePanel.this, "Engir vinir valdir");
			}
		};
		popup.add( du );
		
		recipeDetailTable.setRowHeight( 20 );
		recipeDetailTable.addKeyListener( new KeyListener() {
			public void keyTyped(KeyEvent e) {
				
			}
			
			public void keyReleased(KeyEvent e) {
				
			}
		
			public void keyPressed(KeyEvent e) {
				if( e.getKeyCode() == KeyEvent.VK_DELETE ) {
					int r = recipeTable.getSelectedRow();
					int fr = recipeTable.convertRowIndexToModel(r);
					Recipe rep = recipes.get( fr );
					
					rep.destroy();
					
					Set<RecipeIngredient>	setRep = new HashSet<RecipeIngredient>();
					int[]	rr = recipeDetailTable.getSelectedRows();
					for( int tr : rr ) {
						int sr = recipeDetailTable.convertRowIndexToModel(tr);
						setRep.add( rep.ingredients.get( sr ) );	
					}
					
					rep.ingredients.removeAll(setRep);
					try {
						rep.save();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					
					recipeDetailTable.revalidate();
					recipeDetailTable.repaint();
				}
			}
		});
		
		skmtCombo = new JComboBox( new Object[] {"g"} );
		TableCellEditor cellEditor = new DefaultCellEditor( skmtCombo );
		
		/*skmtCombo.addItemListener( new ItemListener() {
			Object old = null;
			@Override
			public void itemStateChanged(ItemEvent e) {
				int r = recipeDetailTable.getSelectedRow();
				if( clearCombo != -1 && clearCombo == r ) {
					Object obj = recipeDetailTable.getValueAt(r, 2);
					if( e.getStateChange() == ItemEvent.DESELECTED ) {
						if( !obj.equals("g") ) {
							recipeDetailTable.setValueAt(2.0f, r, 1);
						}
						else recipeDetailTable.setValueAt(100.0f, r, 1);
						
						recipeDetailTable.repaint();
					}
				}
			}
		});*/
		//cellEditor.
		/*cellEditor.addCellEditorListener( new CellEditorListener() {
			@Override
			public void editingStopped(ChangeEvent e) {
				int r = recipeTable.getSelectedRow();
				if( r >= 0 && r < recipeTable.getRowCount() ) {
					try {
						Recipe rep = recipes.get(r);
						rep.save();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		
			@Override
			public void editingCanceled(ChangeEvent e) {}
		});*/
		
		TableCellRenderer renderer = new TableCellRenderer() {
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				int rr = recipeTable.getSelectedRow();
				if( rr >= 0 && rr < recipeTable.getRowCount() ) {
					int rrr = recipeTable.convertRowIndexToModel( rr );
					Recipe r = recipes.get( rrr );
					
					rr = recipeDetailTable.convertRowIndexToModel( row );
					
					if( rr >= 0 && rr < r.ingredients.size() ) {
						RecipeIngredient rip = r.ingredients.get( rr );
						return rip.cellEdit;
					}
				}
				return null;
			}
		};
		
		recipeDetailTable.setDefaultEditor( String.class, cellEditor );
		//recipeDetailTable.setDefaultRenderer( String.class, renderer );
		//recipeDetailTable.setce
		
		final JEditorPane	recipeInfo = new JEditorPane();
		recipeInfo.setEditable( false );
		recipeInfo.addFocusListener( new FocusListener(){
			public void focusLost(FocusEvent e) {
				if( currentRecipe != null ) {
					currentRecipe.destroy();
					currentRecipe.desc = recipeInfo.getText();
					try {
						currentRecipe.save();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}

			public void focusGained(FocusEvent e) {}
		});
		
		recipeInfo.addHyperlinkListener( new HyperlinkListener() {
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
					try {
						CompatUtilities.browse( e.getURL() );
					} catch (URISyntaxException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		
		JScrollPane			recipeInfoScroll = new JScrollPane( recipeInfo ) {
			public void paint( Graphics g ) {
				super.paint( g );
				
				if( recipeInfo.getText().length() < 100 ) {
					Graphics2D g2 = (Graphics2D)g;
					g2.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
	
					String str = lang.equals("IS") ? "Settu inn upplýsingar um uppskriftina hér" : "Put recipe information here";
					int strw = g.getFontMetrics().stringWidth( str );
					g.setColor( Color.lightGray );
					g.drawString(str, (this.getWidth()-strw)/2, this.getHeight()/2-5);
				}
			}
		};
		recipeInfoScroll.getViewport().setBackground( Color.white );
		
		final JTabbedPane			recipeInfoPane = new JTabbedPane();
		recipeInfoPane.setTabPlacement( JTabbedPane.RIGHT );
		recipeInfoPane.addTab(lang.equals("IS") ? "Skoða" : "View", recipeInfoScroll);
		recipeInfoPane.addTab(lang.equals("IS") ? "Breyta" : "Change", null);
		recipeInfoPane.addChangeListener( new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if( recipeInfoPane.getSelectedIndex() == 0 ) {
					String str = recipeInfo.getText();
					recipeInfo.setContentType( "text/html" );
					recipeInfo.setEditable( false );
					recipeInfo.setText( str );
				} else if( recipeInfoPane.getSelectedIndex() == 1 ) {
					String str = recipeInfo.getText();
					recipeInfo.setContentType( "text/plain" );
					recipeInfo.setEditable( true );
					recipeInfo.setText( str );
				}
			}
		});
		//final JTextArea	recipeInfo = new JTextArea();
		//recipeInfo.set
		
		final JScrollPane recipeDetailScroll = new JScrollPane( recipeDetailTable ) {
			public void paint( Graphics g ) {
				super.paint(g);
				
				if( recipeDetailTable.getRowCount() == 0 ) {
					Graphics2D g2 = (Graphics2D)g;
					g2.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );

					String str = lang.equals("IS") ? "Dragðu fæðutegundir úr tölfunni" : "Drag food from food table";
					String nstr = lang.equals("IS") ? "til vinstri hingað" : "on the left to here";
					int strw = g.getFontMetrics().stringWidth( str );
					int nstrw = g.getFontMetrics().stringWidth( nstr );
					g.setColor( Color.lightGray );
					g.drawString(str, (this.getWidth()-strw)/2, this.getHeight()/2-5);
					g.drawString(nstr, (this.getWidth()-nstrw)/2, this.getHeight()/2+10);
				}
			}
		};
		recipeDetailScroll.getViewport().setBackground( Color.white );
		
		recipeTable.getSelectionModel().addListSelectionListener( new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				int r = recipeTable.getSelectedRow();
				if( r >= 0 && r < recipeTable.getRowCount() ) {
					int cr = recipeTable.convertRowIndexToModel(r);
					if( cr >= 0 && cr < recipes.size() ) {
						Recipe rep = recipes.get(cr);
						recipeInfo.setContentType("text/html");
						if( rep.desc != null ) {
							recipeInfo.setText( "<html>"+rep.desc+"</html>" );
						} else {
							recipeInfo.setText( "<html></html>" );
						}
						currentRecipe = rep;
					}
					
					int mi = leftTable.convertRowIndexToView( leftTable.getModel().getRowCount() - recipes.size() + cr );
					if( mi >= 0 && mi < leftTable.getRowCount() ) {
						leftTable.setRowSelectionInterval( mi, mi );
					
						Rectangle cellRect = leftTable.getCellRect(mi, 0, false);
						Rectangle visRect = table.getVisibleRect();
						visRect.y = cellRect.y;
						table.scrollRectToVisible( visRect );
					}
				}
				recipeDetailTable.revalidate();
				recipeDetailScroll.repaint();
				recipeDetailTable.repaint();
			}
		});
		
		recipeDetailTable.getSelectionModel().addListSelectionListener( new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				int r = recipeDetailTable.getSelectedRow();
				
				if( r != -1 ) {
					int ri = recipeDetailTable.convertRowIndexToModel(r);
					Object obj = recipeDetailTable.getValueAt(ri, 0);
					if( obj != null && foodInd.containsKey( obj.toString() ) ) {
						//System.err.println( "reppi " + obj );
						
						int i = foodInd.get( obj );
						int mi = leftTable.convertRowIndexToView( i );
						if( mi >= 0 && mi < leftTable.getRowCount() ) {
							leftTable.setRowSelectionInterval( mi, mi );
	
							Rectangle cellRect = leftTable.getCellRect(mi, 0, false);
							Rectangle visRect = table.getVisibleRect();
							visRect.y = cellRect.y;
							table.scrollRectToVisible( visRect );
						}
						
						clearCombo = r;
						skmtCombo.removeAllItems();
						if( currentRecipe != null && currentRecipe.ingredients != null ) {
							RecipeIngredient rip = currentRecipe.ingredients.get(ri);
							if( rip.values != null ) {
								for( String str : rip.values.keySet() ) {
									String addval = str + " ("+rip.values.get(str)+")";
									skmtCombo.addItem( addval );
								}
							}
						} else {
							System.err.println("somethingsomething null");
						}
						skmtCombo.addItem("g");
					} else {
						skmtCombo.removeAllItems();
						skmtCombo.addItem("g");
					}
				}
			}
		});
		
		recipeDetailTable.setAutoCreateRowSorter( true );		
		recipeDetailTable.setModel( recipeDetailModel );
		recipeDetailTable.setDropMode2( DropMode.INSERT_ROWS );
		
		TableColumn unitcolumn = lang.equals("IS") ? recipeDetailTable.getColumn("Eining") : recipeDetailTable.getColumn("Unit");;
		unitcolumn.setCellRenderer( renderer );
		DropTarget dropTarget = new DropTarget() {
			public boolean isActive() {
				return true;
			}
			
			/*public FlavorMap getFlavorMap() {
				return null;
			}
			
			public void drop( DropTargetDragEvent dtde ) {
				try {
					Object obj = dtde.getTransferable().getTransferData( DataFlavor.stringFlavor );
					System.err.println( obj );
				} catch (UnsupportedFlavorException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}*/
		};
		recipeDetailScroll.setDropTarget( dropTarget );
		try {
			recipeDetailScroll.getDropTarget().addDropTargetListener( new DropTargetListener() {
				public void dragEnter(DropTargetDragEvent dtde) {}

				public void dragExit(DropTargetEvent dte) {}

				public void dragOver(DropTargetDragEvent dtde) {}

				public void drop(DropTargetDropEvent dtde) {
					Object obj;
					try {
						obj = dtde.getTransferable().getTransferData( DataFlavor.stringFlavor );
						if( obj != null ) {
							insertRepInfo( obj );
						}
					} catch (UnsupportedFlavorException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				public void dropActionChanged(DropTargetDragEvent dtde) {}
			});
		} catch (TooManyListenersException e1) {
			e1.printStackTrace();
		}
		
		//recipeDetailTable.create
		
		JComponent	recipeImage = new JComponent() {
			
		};
		recipeImage.setPreferredSize( new Dimension(100,100) );
		
		final JButton	addRecipeButton = new JButton( nu );
		addRecipeButton.setText("");
		addRecipeButton.setToolTipText("Nýja uppskrift");
		final JButton	removeRecipeButton = new JButton( eu );
		removeRecipeButton.setText("");
		removeRecipeButton.setToolTipText("Eyða uppskrift");
		final JButton	shareRecipeButton = new JButton( du );
		shareRecipeButton.setText("");
		shareRecipeButton.setToolTipText("Deila uppskrift með vinum");
		JComponent recipeButtons = new JComponent() {};
		recipeButtons.setLayout( new FlowLayout() );
		recipeButtons.add( addRecipeButton );
		recipeButtons.add( removeRecipeButton );
		recipeButtons.add( shareRecipeButton );
		
		addRecipeButton.setIcon( new ImageIcon( ImageIO.read( this.getClass().getResource("/nu.png") ) ) );
		removeRecipeButton.setIcon( new ImageIcon( ImageIO.read( this.getClass().getResource("/eu.png") ) ) );
		shareRecipeButton.setIcon( new ImageIcon( ImageIO.read( this.getClass().getResource("/du.png") ) ) );
		
		JComponent recipeTmpScroll = new JComponent() {};
		recipeTmpScroll.setLayout( new BorderLayout() );
		recipeTmpScroll.add( recipeScroll );
		recipeTmpScroll.add( recipeButtons, BorderLayout.SOUTH );
		
		JSplitPane	recipeSplit = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, recipeTmpScroll, recipeDetailScroll );
		recipeSplit.setDividerLocation( 300 );
		//JSplitPane  recipeInfoSplit = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, recipeImage, recipeInfoPane );
		
		//JSplitPane	recipe = new JSplitPane( JSplitPane.VERTICAL_SPLIT, recipeSplit, recipeInfoSplit );
		
		this.setTopComponent( recipeSplit );
		this.setBottomComponent( recipeInfoPane );
	}
}
