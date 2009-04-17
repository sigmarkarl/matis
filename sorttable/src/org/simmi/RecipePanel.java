package org.simmi;

import java.awt.Dimension;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TooManyListenersException;

import javax.swing.DropMode;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class RecipePanel extends JSplitPane {
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
			ingredients.add( new RecipeIngredient( "Vatn", 100, "g" ) );
		}
		
		public void setDescription( String desc ) {
			this.desc = desc;
		}
	};
	
	List<Recipe>	recipes;
	
	public RecipePanel( final String lang ) {
		super( JSplitPane.VERTICAL_SPLIT );
		
		recipes = new ArrayList<Recipe>();
		Recipe drullukaka = new Recipe( "Drullukaka", "Rugl", "Sigmar Stefansson" );
		drullukaka.setDescription( "Þegar piparkökur bakast<br>kökugerðarmaður tekur<br>fyrst af öllu steikarpottinn<br>og svo kíló margarín<p>"+
				"<a href=\"http://www.matis.is\">Matís</a>" );
		recipes.add( drullukaka );
		recipes.add( new Recipe( "Jarðaberjasulta", "Rugl", "Sveinn Margeirsson" ) );
		
		JScrollPane	recipeScroll = new JScrollPane();
		final JTable		recipeTable = new JTable();
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
					if( arg0 == 0 ) return "Hópur";
					else if( arg0 == 1 ) return "Nafn";
					else if( arg0 == 2 ) return "Höfundur";
				} else {
					if( arg0 == 0 ) return "Group";
					else if( arg0 == 1 ) return "Name";
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
					else if( arg1 == 2 ) return rep.group;
					else if( arg1 == 1 ) return rep.author;
				}
				return null;
			}

			@Override
			public boolean isCellEditable(int arg0, int arg1) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void removeTableModelListener(TableModelListener arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void setValueAt(Object arg0, int arg1, int arg2) {
				// TODO Auto-generated method stub
				
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
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void removeTableModelListener(TableModelListener arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void setValueAt(Object arg0, int arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
			
		};
		final JTable		recipeDetailTable = new JTable();
		final JEditorPane	recipeInfo = new JEditorPane();
		JScrollPane			recipeInfoScroll = new JScrollPane( recipeInfo );
		//final JTextArea	recipeInfo = new JTextArea();
		//recipeInfo.set
		recipeTable.getSelectionModel().addListSelectionListener( new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				int r = recipeTable.getSelectedRow();
				if( r >= 0 && r < recipeTable.getRowCount() ) {
					Recipe rep = recipes.get(r);
					recipeInfo.setContentType("text/html");
					if( rep.desc != null ) {
						recipeInfo.setText( "<html>"+rep.desc+"</html>" );
					} else {
						recipeInfo.setText( "<html></html>" );
					}
				}
				
				recipeDetailTable.revalidate();
				recipeDetailTable.repaint();
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
		recipeDetailTable.setDropTarget( dropTarget );
		try {
			recipeDetailTable.getDropTarget().addDropTargetListener( new DropTargetListener() {
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
								String[] lines = obj.toString().split("\\n");
								for( String line : lines ) {
									String[] vals = line.split("\\t");
									rep.ingredients.add( new RecipeIngredient( vals[1], 100, "g" ) );
								}
								recipeDetailTable.revalidate();
								recipeDetailTable.repaint();
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
		JSplitPane  recipeInfoSplit = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, recipeImage, recipeInfoScroll );
		//JSplitPane	recipe = new JSplitPane( JSplitPane.VERTICAL_SPLIT, recipeSplit, recipeInfoSplit );
		
		this.setTopComponent( recipeSplit );
		this.setBottomComponent( recipeInfoSplit );
	}
}
