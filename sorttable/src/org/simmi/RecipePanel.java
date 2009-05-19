package org.simmi;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;
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
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableModel;

public class RecipePanel extends JSplitPane {
	final JTable		recipeTable = new JTable();
	final JTable		recipeDetailTable = new JTable();
	Map<String,Integer>	foodInd;
	FriendsPanel		fp;
	Map<String,Map<String,String>> skmt = new HashMap<String,Map<String,String>>();
	JComboBox 			skmtCombo;
	int					clearCombo = -1;
	char[]				cbuf = new char[2048];
	
	public class RecipeIngredient {
		String	stuff;
		float	measure;
		String	unit;
		Map<String,String>	values;
		
		public RecipeIngredient( String stuff, float measure, String unit ) {
			this.stuff = stuff;
			this.measure = measure;
			this.unit = unit;
			
			if( skmt.containsKey( stuff.toLowerCase() ) ) {
				values = skmt.get( stuff.toLowerCase() );
			} else {
				String store = null;
				int max = 0;
				
				List<String> pars = Arrays.asList( stuff.toLowerCase().split("[, ]+") );
				for( String str : skmt.keySet() ) {
					List<String> vals = Arrays.asList( str.split("[, ]+") );
					
					int count = 0;
					for( String a : pars ) {
						if( vals.contains(a) ) count++;
					}
					
					if( count > max ) {
						max = count;
						store = str;
					}
				}
				
				if( store != null ) values = skmt.get( store );
			}
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
	};
	
	public void fillSkmt() throws IOException {
		InputStream is = this.getClass().getResourceAsStream("skmt.txt");
		BufferedReader br = new BufferedReader( new InputStreamReader(is) );
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
	JTable			mailTable;
	JScrollPane		scrollPane;
	
	public void checkMail() throws IOException {
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
			
			insertRecipe( new InputStreamReader( connection.getInputStream() ) );
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
	
	JTable	theTable;
	JTable	theLeftTable;
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
				System.err.println("jospl " + spl[i] );
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
	
	public RecipePanel( final FriendsPanel fp, final String lang, final JTable table, final JTable leftTable, final Map<String,Integer> foodNameInd ) throws IOException {
		super( JSplitPane.VERTICAL_SPLIT );
		this.setDividerLocation( 300 );
		
		theTable = table;
		theLeftTable = leftTable;
		
		mailTable = new JTable();
		scrollPane = new JScrollPane( mailTable );
		dialog = new JDialog( SwingUtilities.getWindowAncestor( this ) );
		dialog.setSize(400, 300);
		dialog.add( scrollPane );
		
		this.addComponentListener( new ComponentListener(){
		
			@Override
			public void componentShown(ComponentEvent e) {
				try {
					checkMail();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		
			@Override
			public void componentResized(ComponentEvent e) {
				// TODO Auto-generated method stub
				
			}
		
			@Override
			public void componentMoved(ComponentEvent e) {
				// TODO Auto-generated method stub
				
			}
		
			@Override
			public void componentHidden(ComponentEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		fillSkmt();
		
		this.fp = fp;
		foodInd = foodNameInd;
		recipes = new ArrayList<Recipe>();
		
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
		
		JScrollPane	recipeScroll = new JScrollPane();
		
		AbstractAction nu = new AbstractAction("Nýja uppskrift"){
			@Override
			public void actionPerformed(ActionEvent e) {
				recipes.add( new Recipe("Velja nafn", "Velja hóp", fp.currentUser) );
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
			@Override
			public void addTableModelListener(TableModelListener arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public Class<?> getColumnClass(int arg0) {
				return String.class;
			}

			@Override
			public int getColumnCount() {
				return 3;
			}

			@Override
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

			@Override
			public int getRowCount() {
				return recipes.size();
			}

			@Override
			public Object getValueAt(int arg0, int arg1) {
				if( arg0 < recipes.size() ) {
					Recipe rep = recipes.get(arg0);
					if( arg1 == 0 ) return rep.name;
					else if( arg1 == 1 ) return rep.group;
					else if( arg1 == 2 ) return rep.author;
				}
				return null;
			}

			@Override
			public boolean isCellEditable(int arg0, int arg1) {
				if( arg1 == 0 || arg1 == 1 ) return true;
				return false;
			}

			@Override
			public void removeTableModelListener(TableModelListener arg0) {}

			@Override
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

			@Override
			public void addTableModelListener(TableModelListener arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public Class<?> getColumnClass(int arg0) {
				if( arg0 == 0 ) return String.class;
				else if( arg0 == 1 ) return Float.class;
				else if( arg0 == 2 ) return String.class;
				return null;
			}

			@Override
			public int getColumnCount() {
				return 3;
			}

			@Override
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

			@Override
			public int getRowCount() {
				int r = recipeTable.getSelectedRow();
				if( r >= 0 && r < recipes.size() ) {
					Recipe rep = recipes.get(r);
					return rep.ingredients.size();
				}
				return 0;
			}

			@Override
			public Object getValueAt(int arg0, int arg1) {
				int r = recipeTable.getSelectedRow();
				if( r >= 0 && r < recipes.size() ) {
					Recipe rep = recipes.get(r);
					RecipeIngredient repi = rep.ingredients.get(arg0);
					if( arg1 == 0 ) return repi.stuff;
					else if( arg1 == 1 ) return repi.measure;
					else if( arg1 == 2 ) return repi.unit;
				}
				return null;
			}

			@Override
			public boolean isCellEditable(int arg0, int arg1) {
				if( arg1 == 1 || arg1 == 2 ) return true;
				return false;
			}

			@Override
			public void removeTableModelListener(TableModelListener arg0) {}

			@Override
			public void setValueAt(Object arg0, int arg1, int arg2) {
				int r = recipeTable.getSelectedRow();
				
				int nr = recipeDetailTable.getSelectedRow();
				if( arg2 == 2 && nr != -1 ) {
					String item = (String)arg0;
					if( item != null ) {
						if( !item.equals("g") ) recipeDetailTable.setValueAt(1.0f, nr, 1);
						else recipeDetailTable.setValueAt(100.0f, nr, 1);
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
					}
					try {
						rep.save();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
		};
		
		AbstractAction eu = new AbstractAction("Eyða uppskrift/um") {
			@Override
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
			@Override
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
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else 	JOptionPane.showMessageDialog(RecipePanel.this, "Engir vinir valdir");
			}
		};
		popup.add( du );
		
		recipeDetailTable.addKeyListener( new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
				
			}
		
			@Override
			public void keyReleased(KeyEvent e) {
				
			}
		
			@Override
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
						// TODO Auto-generated catch block
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
		recipeDetailTable.setDefaultEditor( String.class, cellEditor );
		//recipeDetailTable.setce
		
		final JEditorPane	recipeInfo = new JEditorPane();
		recipeInfo.setEditable( false );
		recipeInfo.addFocusListener( new FocusListener(){
		
			@Override
			public void focusLost(FocusEvent e) {
				if( currentRecipe != null ) {
					currentRecipe.destroy();
					currentRecipe.desc = recipeInfo.getText();
					try {
						currentRecipe.save();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		
			@Override
			public void focusGained(FocusEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		recipeInfo.addHyperlinkListener( new HyperlinkListener() {
			@Override
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
					try {
						Desktop.getDesktop().browse( e.getURL().toURI() );
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (URISyntaxException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
		
		JScrollPane			recipeInfoScroll = new JScrollPane( recipeInfo );
		final JTabbedPane			recipeInfoPane = new JTabbedPane();
		recipeInfoPane.setTabPlacement( JTabbedPane.RIGHT );
		recipeInfoPane.addTab("Skoða", recipeInfoScroll);
		recipeInfoPane.addTab("Breyta", null);
		recipeInfoPane.addChangeListener( new ChangeListener() {
			@Override
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
		recipeTable.getSelectionModel().addListSelectionListener( new ListSelectionListener() {
			@Override
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
					leftTable.setRowSelectionInterval( mi, mi );
					
					Rectangle cellRect = leftTable.getCellRect(mi, 0, false);
					Rectangle visRect = table.getVisibleRect();
					visRect.y = cellRect.y;
					table.scrollRectToVisible( visRect );
				}
				
				recipeDetailTable.revalidate();
				recipeDetailTable.repaint();
			}
		});
		
		recipeDetailTable.getSelectionModel().addListSelectionListener( new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				int r = recipeDetailTable.getSelectedRow();
				
				int ri = recipeDetailTable.convertRowIndexToModel(r);
				Object obj = recipeDetailTable.getValueAt(ri, 0);
				if( obj != null && foodInd.containsKey( obj.toString() ) ) {
					int i = foodInd.get( obj );
					int mi = leftTable.convertRowIndexToView( i );
					if( mi >= 0 && mi < leftTable.getRowCount() ) {
						leftTable.setRowSelectionInterval( mi, mi );

						Rectangle cellRect = leftTable.getCellRect(mi, 0, false);
						Rectangle visRect = table.getVisibleRect();
						visRect.y = cellRect.y;
						table.scrollRectToVisible( visRect );
					}
					
					RecipeIngredient rip = currentRecipe.ingredients.get(ri);
					clearCombo = r;
					skmtCombo.removeAllItems();
					if( rip.values != null ) {
						for( String str : rip.values.keySet() ) {
							skmtCombo.addItem( str + " ("+rip.values.get(str)+")" );
						}
					}
					skmtCombo.addItem("g");
				}
			}
		});
		
		recipeDetailTable.setAutoCreateRowSorter( true );
		JScrollPane recipeDetailScroll = new JScrollPane( recipeDetailTable );
		recipeDetailTable.setModel( recipeDetailModel );
		recipeDetailTable.setDropMode( DropMode.INSERT_ROWS );
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
				@Override
				public void dragEnter(DropTargetDragEvent dtde) {
					
				}

				@Override
				public void dragExit(DropTargetEvent dte) {
					
				}

				@Override
				public void dragOver(DropTargetDragEvent dtde) {
					
				}

				@Override
				public void drop(DropTargetDropEvent dtde) {
					Object obj;
					try {
						obj = dtde.getTransferable().getTransferData( DataFlavor.stringFlavor );
						if( obj != null ) {
							int r = recipeTable.getSelectedRow();
							if( r >= 0 && r < recipes.size() ) {
								Recipe rep = recipes.get(r);
								
								rep.destroy();
								
								String[] lines = obj.toString().split("\\n");
								for( String line : lines ) {
									String[] vals = line.split("\\t");
									rep.ingredients.add( new RecipeIngredient( vals[1], 100, "g" ) );
								}
								recipeDetailTable.revalidate();
								recipeDetailTable.repaint();
								
								rep.save();
							}
						}
					} catch (UnsupportedFlavorException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				@Override
				public void dropActionChanged(DropTargetDragEvent dtde) {
					
				}
				
			});
		} catch (TooManyListenersException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//recipeDetailTable.create
		
		JComponent	recipeImage = new JComponent() {
			
		};
		recipeImage.setPreferredSize( new Dimension(100,100) );
		
		JButton	addRecipeButton = new JButton( nu );
		addRecipeButton.setText("");
		addRecipeButton.setToolTipText("Nýja Uppskrift");
		addRecipeButton.setIcon( new ImageIcon( ImageIO.read( this.getClass().getResource("/nu.png") ) ) );
		JButton	removeRecipeButton = new JButton( eu );
		removeRecipeButton.setText("");
		removeRecipeButton.setToolTipText("Eyða Uppskrift");
		removeRecipeButton.setIcon( new ImageIcon( ImageIO.read( this.getClass().getResource("/eu.png") ) ) );
		JButton	shareRecipeButton = new JButton( du );
		shareRecipeButton.setText("");
		shareRecipeButton.setToolTipText("Deila Uppskrift");
		shareRecipeButton.setIcon( new ImageIcon( ImageIO.read( this.getClass().getResource("/du.png") ) ) );
		JComponent recipeButtons = new JComponent() {};
		recipeButtons.setLayout( new FlowLayout() );
		recipeButtons.add( addRecipeButton );
		recipeButtons.add( removeRecipeButton );
		recipeButtons.add( shareRecipeButton );
		
		JComponent recipeTmpScroll = new JComponent() {};
		recipeTmpScroll.setLayout( new BorderLayout() );
		recipeTmpScroll.add( recipeScroll );
		recipeTmpScroll.add( recipeButtons, BorderLayout.SOUTH );
		
		JSplitPane	recipeSplit = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, recipeTmpScroll, recipeDetailScroll );
		recipeSplit.setDividerLocation( 300 );
		JSplitPane  recipeInfoSplit = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, recipeImage, recipeInfoPane );
		//JSplitPane	recipe = new JSplitPane( JSplitPane.VERTICAL_SPLIT, recipeSplit, recipeInfoSplit );
		
		this.setTopComponent( recipeSplit );
		this.setBottomComponent( recipeInfoSplit );
	}
}
