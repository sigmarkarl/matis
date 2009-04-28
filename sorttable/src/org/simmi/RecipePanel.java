package org.simmi;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TooManyListenersException;

import javax.swing.AbstractAction;
import javax.swing.DefaultCellEditor;
import javax.swing.DropMode;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
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
import javax.swing.table.TableModel;

public class RecipePanel extends JSplitPane {
	final JTable		recipeTable = new JTable();
	final JTable		recipeDetailTable = new JTable();
	Map<String,Integer>	foodInd;
	
	public class RecipeIngredient {
		String	stuff;
		float	measure;
		String	unit;
		
		public RecipeIngredient( String stuff, float measure, String unit ) {
			this.stuff = stuff;
			this.measure = measure;
			this.unit = unit;
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
			String fname = Integer.toString( Math.abs( str.hashCode()) );
			f = new File( f, fname );
			FileWriter	fw = new FileWriter( f );
			fw.write( str );
			fw.close();
		}
	};
	
	List<Recipe>	recipes;
	Recipe			currentRecipe;
	
	public RecipePanel( final String lang, final JTable table, final JTable leftTable, final Map<String,Integer> foodNameInd ) throws IOException {
		super( JSplitPane.VERTICAL_SPLIT );
		this.setDividerLocation( 300 );
		
		foodInd = foodNameInd;
		
		char[]	cbuf = new char[2048];
		recipes = new ArrayList<Recipe>();
		
		File f = new File( System.getProperty("user.home"), ".isgem" );
		f = new File( f, "recipes" );
		File[] ff = f.listFiles();
		if( ff != null ) {
			for( File file : ff ) {
				FileReader	fr = new FileReader( file );
				int read = fr.read(cbuf);
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
					recipes.add( rep );
				}
				
				if( spl.length >= 2 ) {
					String[] split = spl[1].split("\n");
					for( String str : split ) {
						String[] subspl = str.split("\t");
						if( subspl.length > 2 ) {
							rep.ingredients.add( new RecipeIngredient(subspl[0], Float.parseFloat(subspl[1]), subspl[2]) );
						} else {
							System.err.println( "ok " + str );
						}
					}
				}
				
				if( spl.length >= 3 ) {
					rep.desc = "";
					int i;
					for( i = 2; i < spl.length-1; i++ ) {
						System.err.println("jospl " + spl[i] );
						rep.desc += spl[i] + "\n\n";
					}
					System.err.println("jospl " + spl[i] );
					rep.desc += spl[i];
				}
				
				
				//String str = rep.toString();
				//System.err.println( str );
				
				fr.close();
			}
		}
		
		JScrollPane	recipeScroll = new JScrollPane();
		
		JPopupMenu	popup = new JPopupMenu();
		popup.add( new AbstractAction("Nýja uppskrift"){
			@Override
			public void actionPerformed(ActionEvent e) {
				recipes.add( new Recipe("Velja nafn", "Velja hóp", "Velja höfund") );
				recipeTable.revalidate();
				recipeTable.repaint();
				
				table.tableChanged( new TableModelEvent( table.getModel() ) );
				leftTable.tableChanged( new TableModelEvent( leftTable.getModel() ) );
			}
		});
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
				if( arg1 == 0 || arg1 == 2 ) return true;
				return false;
			}

			@Override
			public void removeTableModelListener(TableModelListener arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void setValueAt(Object arg0, int arg1, int arg2) {
				Recipe rep = recipes.get(arg1);
				rep.destroy();
				if( arg2 == 0 ) rep.name = arg0.toString();
				else if( arg2 == 2 ) rep.author = arg0.toString();
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
				int rr = recipeTable.convertRowIndexToModel(r);
				if( rr >= 0 && rr < recipes.size() ) {
					Recipe rep = recipes.get(rr);
					rep.destroy();
					RecipeIngredient ri = rep.ingredients.get( arg1 );
					if( arg2 == 1 ) {
						ri.measure = (Float)arg0;
					} else {
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
		
		popup.add( new AbstractAction("Eyða uppskrift/um") {
			@Override
			public void actionPerformed(ActionEvent e) {
				int[]	rr = recipeTable.getSelectedRows();
				Set<Recipe>	remSet = new HashSet<Recipe>();
				for( int r : rr ) {
					int ri = recipeTable.convertRowIndexToModel(r);
					remSet.add( recipes.get(ri) );
				}
				recipes.removeAll( remSet );
				for( Recipe rep : remSet ) {
					rep.destroy();
				}
				recipeTable.revalidate();
				recipeTable.repaint();
				
				recipeDetailTable.revalidate();
				recipeDetailTable.repaint();
				
				table.tableChanged( new TableModelEvent( table.getModel() ) );
				leftTable.tableChanged( new TableModelEvent( leftTable.getModel() ) );				
			}
		});
		
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
		
		JComboBox comboBox = new JComboBox( new Object[] {"g","mg","L"} );
		TableCellEditor cellEditor = new DefaultCellEditor( comboBox );
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
		JSplitPane	recipeSplit = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, recipeScroll, recipeDetailScroll );
		recipeSplit.setDividerLocation( 300 );
		JSplitPane  recipeInfoSplit = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, recipeImage, recipeInfoPane );
		//JSplitPane	recipe = new JSplitPane( JSplitPane.VERTICAL_SPLIT, recipeSplit, recipeInfoSplit );
		
		this.setTopComponent( recipeSplit );
		this.setBottomComponent( recipeInfoSplit );
	}
}
