package com.matis.prokaria;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLEventListener;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.RowSorterListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class Fiskur extends JApplet {
	JComponent 		c;
	
	final Color		myred = new Color(200,100,100);
	final Color		myyellow = new Color(200,200,100);
	final Color		mygreen = new Color(100,200,100);
	
	List<Integer>	maleindices = new ArrayList<Integer>();
	List<Integer>	femaleindices = new ArrayList<Integer>();
	
	//List<Tuple> unsortedTupleList;
	List<FishWorker.Tuple>		tuples = new ArrayList<FishWorker.Tuple>();
	
	final Color darkGray = new Color(230, 230, 230);
	final Color lightGray = new Color(250, 250, 250);
	JEditorPane e;
	JComponent subc;
	final Color bgColor = new Color(255, 255, 200);
	Image 			img;
	Image 			mimg;
	BufferedImage 	barimg;
	BufferedImage	xlimg;

	JTable 		table;
	JTable		mtable;
	JTable		ftable;
	JTable		summaryTable;
	JTable		matrixTable;
	JTable		genotypeTable;
	//JTable		matrixRowHeader;
	TableModel	rowHeaderModel;
	TableModel	summaryModel;
	TableModel	matrixModel;
	TableModel	nullModel;
	
	JTabbedPane	tabbedPane;
	SurfaceDraw	sd;
	
	MySorter	currentSorter;
	
    int 			mouseState = -1;
    int				ex, ey;
    
    boolean			d3 = true;	
	boolean 		showing = true;
	JSplitPane 		splitpane;
	FishWorker		fishworker;
 
	public Fiskur() {
		super();

		fishworker = new FishWorker();
		try {
			URL url = this.getClass().getResource("/smooth.png");
			img = ImageIO.read(url);
			url = this.getClass().getResource("/matis.png");
			mimg = ImageIO.read(url);
			url = this.getClass().getResource("/images.jpeg");
			barimg = ImageIO.read(url);
			url = this.getClass().getResource("/xlsx.png");
			xlimg = ImageIO.read(url);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	static {
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
	}
	
	public class MySorter extends TableRowSorter<TableModel> {
		String 		name;
		TableModel	model;
		
		public MySorter( String name, TableModel model ) {
			super( model );
			this.name = name;
			this.model = model;
		}
		
		public int convertRowIndexToModelSuper( String from, int index) {
			if( index >= super.getViewRowCount() ) {
				System.err.println( name + "   " + model.getRowCount() );
				System.err.println( from + "   " + index + "   " );
				System.err.println( name + "   " + super.getViewRowCount() );
				System.err.println( currentSorter.name + "   " + currentSorter.getViewRowCountSuper() );
				//System.err.println( "erm " + super.convertRowIndexToModel( index ) );
				return -1;
			}
			
			return super.convertRowIndexToModel( index );
		}

		public int convertRowIndexToViewSuper(int index) {
			return super.convertRowIndexToView( index );
		}
		
		public int getViewRowCountSuper() {
			return super.getViewRowCount();
		}
	}
	
	public class MyFilter extends RowFilter<TableModel,Integer> {
		String			filterText;
		int				fInd = 0;
		Set<String> 	cropped = new HashSet<String>();
		TableModel		leftModel;
		
		public MyFilter( TableModel leftModel ) {
			super();
			
			this.leftModel = leftModel;
		}
		
		public boolean include( javax.swing.RowFilter.Entry<? extends TableModel, ? extends Integer> entry ) {
			/*String gval = (String)leftModel.getValueAt(entry.getIdentifier(), 0);
			String val = fInd == 0 ? gval : (String)leftModel.getValueAt(entry.getIdentifier(), 1);
			if (filterText != null) {
				if (val != null) {
					boolean b = val.matches(filterText);
					return b;
				}
				return false;
			} else {
				boolean b = cropped.contains(gval);
				if (b)
					return false;
			}*/
			return true;
		}
	}

	/*public double calc(int ix, int iy) {
	    double sum = 0;
	    int NL = pop.getNumLoci();
	    for (int L = 0; L < NL; L++) {
//	      log.info("ix=" + ix + ", iy=" + iy + ", L=" + L);
	      int a = pop.getAllele(ix, L, TYPE);
	      int b = pop.getAllele(ix, L, TYPE2);
	      int c = pop.getAllele(iy, L, TYPE);
	      int d = pop.getAllele(iy, L, TYPE2);
	      if (a == -1 || b == -1 || c == -1 || d == -1)
	        continue; //ignore

	      double x2 = (a == b ? 4: 2); // sum_j x_j^2
	      double y2 = (c == d ? 4: 2); // sum_j y_j^2
	      double xy
	        = (a == c ? 1: 0)
	        + (a == d ? 1: 0)
	        + (b == c ? 1: 0)
	        + (b == d ? 1: 0);
	      double dist = x2 - 2. * xy + y2;
	      sum += dist;
	    }
	    double dij = sum / (4. * NL);
	    return dij;
	  }*/

	public void start() {
		super.start();
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
	}
	
	public void reload() {
		if( matrixModel != null ) {
			matrixTable.setModel( nullModel );
			matrixTable.setModel( matrixModel );
		}
		
		rowHeaderModel = new TableModel() {
			@Override
			public void addTableModelListener(TableModelListener l) {
				
			}

			@Override
			public Class<?> getColumnClass(int columnIndex) {
				return String.class;
			}

			@Override
			public int getColumnCount() {
				return 1;
			}

			@Override
			public String getColumnName(int columnIndex) {
				// TODO Auto-generated method stub
				return "Names";
			}

			@Override
			public int getRowCount() {
				return fishworker.femalefish.size();
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				int r = matrixTable.convertRowIndexToModel(rowIndex );
				return fishworker.femalefish.get( r ).name;
			}

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void removeTableModelListener(
					TableModelListener l) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void setValueAt(Object aValue, int rowIndex, int columnIndex) {}
			
		};
		/*matrixRowHeader.setModel( rowHeaderModel );
		matrixRowHeader.getColumn("Names").setWidth(100);
		matrixRowHeader.getColumn("Names").setMaxWidth(100);
		matrixRowHeader.getColumn("Names").setPreferredWidth(100);*/
		
		/*System.err.println( matrixModel.getRowCount() + "  " + matrixModel.getColumnCount() );
		System.err.println( matrixTable.getRowCount() + "  " + matrixTable.getColumnCount() );
		System.err.println( femalefish.size() + "  " + malefish.size() );*/
		
		table.revalidate();
		table.invalidate();
		table.repaint();
	}
	
	public TableModel femaleModel() {
		return new TableModel() {
			@Override
			public void addTableModelListener(TableModelListener l) {
				// TODO Auto-generated method stub

			}

			@Override
			public Class<?> getColumnClass(int columnIndex) {
				return Double.class;
			}

			@Override
			public int getColumnCount() {
				if (fishworker.femalefish != null)
					return fishworker.femalefish.size();
				return 0;
			}

			@Override
			public String getColumnName(int columnIndex) {
				return fishworker.femalefish.get(columnIndex).name;
			}

			@Override
			public int getRowCount() {
				if (fishworker.malefish != null)
					return fishworker.malefish.size();
				return 0;
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				int val = columnIndex * fishworker.malefish.size() + rowIndex;
				//int rval = table.convertRowIndexToModel( val );
				
				if( val >= 0 && val < fishworker.tupleList.size() ) {
					FishWorker.Tuple tup = fishworker.tupleList.get( val );
					return tup.current();
				}
				
				return -1.0;
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
			public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
				// TODO Auto-generated method stub

			}
		};
	}
	
	public TableModel maleModel() {
		return new TableModel() {
			@Override
			public void addTableModelListener(TableModelListener l) {
				// TODO Auto-generated method stub

			}

			@Override
			public Class<?> getColumnClass(int columnIndex) {
				return Double.class;
			}

			@Override
			public int getColumnCount() {
				if (fishworker.malefish != null)
					return fishworker.malefish.size();
				return 0;
			}

			@Override
			public String getColumnName(int columnIndex) {
				return fishworker.malefish.get(columnIndex).name;
			}

			@Override
			public int getRowCount() {
				if (fishworker.femalefish != null)
					return fishworker.femalefish.size();
				return 0;
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				int val = rowIndex * fishworker.malefish.size() + columnIndex;
				//int rval = table.convertRowIndexToModel( val );
				if( val >= 0 && val < fishworker.tupleList.size() ) {
					FishWorker.Tuple tup = fishworker.tupleList.get( val );
					return tup.current();
				}
				
				return -1.0;
			}

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return false;
			}

			@Override
			public void removeTableModelListener(TableModelListener l) {
				// TODO Auto-generated method stub

			}

			@Override
			public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
				// TODO Auto-generated method stub

			}
		};
	}

	public TableModel femaleGenotypes() {
		return new TableModel() {
			@Override
			public void addTableModelListener(TableModelListener l) {}

			@Override
			public Class<?> getColumnClass(int columnIndex) {
				return Integer.class;
			}

			@Override
			public int getColumnCount() {
				if (fishworker.markers != null)
					return fishworker.markers.size();
				return 0;
			}

			@Override
			public String getColumnName(int columnIndex) {
				return fishworker.markers.get(columnIndex);
			}

			@Override
			public int getRowCount() {
				if (fishworker.femalefish != null)
					return fishworker.femalefish.size();
				return 0;
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				int val = rowIndex * fishworker.markers.size() + columnIndex;
				//int rval = table.convertRowIndexToModel( val );
				if( val >= 0 && val < fishworker.fmatrix.length ) {
					int rval = fishworker.fmatrix[ val ];
					return rval;
				}
				
				return -1;
			}

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return false;
			}

			@Override
			public void removeTableModelListener(TableModelListener l) {}

			@Override
			public void setValueAt(Object aValue, int rowIndex, int columnIndex) {}
		};
	}
	
	public TableModel maleGenotypes() {
		return new TableModel() {
			@Override
			public void addTableModelListener(TableModelListener l) {
				// TODO Auto-generated method stub

			}

			@Override
			public Class<?> getColumnClass(int columnIndex) {
				return Integer.class;
			}

			@Override
			public int getColumnCount() {
				if (fishworker.markers != null)
					return fishworker.markers.size();
				return 0;
			}

			@Override
			public String getColumnName(int columnIndex) {
				return fishworker.markers.get(columnIndex);
			}

			@Override
			public int getRowCount() {
				if (fishworker.malefish != null)
					return fishworker.malefish.size();
				return 0;
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				int val = rowIndex * fishworker.markers.size() + columnIndex;
				//int rval = table.convertRowIndexToModel( val );
				if( val >= 0 && val < fishworker.mmatrix.length ) {
					int rval = fishworker.mmatrix[ val ];
					return rval;
				}
				
				return -1;
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
			public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
				// TODO Auto-generated method stub

			}
		};
	}
	
	public void kintoolExport() throws IOException {
		JFileChooser fc = new JFileChooser();
		if( fc.showSaveDialog( this ) == JFileChooser.APPROVE_OPTION ) {
			File f = fc.getSelectedFile();
			fishworker.kinWrite( f );
		} else {
			File f = File.createTempFile("tmp", ".txt");
			fishworker.kinWrite( f );
			Desktop.getDesktop().open( f );
		}
	}
	
	public void pairRel() {
		/*SysPopFactory.
		pop.
		RMtrxOutbredKonHeg kh = new RMtrxOutbredKonHeg(pop);*/
	}
 	
	public void importStuff() throws IOException {
		JFileChooser	fc = new JFileChooser();
		fc.setMultiSelectionEnabled(true);
		if( fc.showOpenDialog( this ) == JFileChooser.APPROVE_OPTION ) {
			File[] ff = fc.getSelectedFiles();
			
			fishworker.markers.clear();
			fishworker.fishes.clear();
			fishworker.malefish.clear();
			fishworker.femalefish.clear();
			
			if( ff.length > 1 ) {				
				for( File f : ff ) {
					String path = f.getAbsolutePath();
					if( path.endsWith(".xlsx" ) ) {
						if( fishworker.malefish.size() == 0 && fishworker.femalefish.size() == 0 ) {
							fishworker.wbStuffNoSex( path );
						} else {
							fishworker.wbStuff( path );
						}
					} else if( path.endsWith(".xls") ) {
						HSSFWorkbook 	workbook = new HSSFWorkbook( new FileInputStream(f) );
						HSSFSheet 		sheet = workbook.getSheetAt(0);
						
						int r = 0;
						HSSFRow 		row = sheet.getRow(r);
						
						int sexind = -1;
						int sampind = -1;
						int locind = -1;
						int wind = -1;
						int c = 0;
						HSSFCell		cell = row.getCell(c);
						while( cell != null ) {
							String cellval = cell.getStringCellValue();
							if( cellval.equalsIgnoreCase("sex") ) sexind = c;
							else if( cellval.equalsIgnoreCase("sample") ) sampind = c;
							else if( cellval.equalsIgnoreCase("room") ) locind = c;
							else if( cellval.equalsIgnoreCase("weight") ) wind = c;
							
							c++;
							cell = row.getCell(c);
						}
						
						row = sheet.getRow( ++r );
						//Fish cmpf = new Fish( "", 0.0f, 0, true );
						while( row != null ) {
							cell = row.getCell(sexind);
							if( cell != null ) {
								String 	sex = cell.getStringCellValue();
								cell = row.getCell(sampind);
								
								String 	name = null;
								int type = cell.getCellType();
								if( type == HSSFCell.CELL_TYPE_NUMERIC ) {
									name = Integer.toString( (int)cell.getNumericCellValue() );
								} else {
									name = cell.getStringCellValue();
								}
								cell = 	row.getCell(wind);
								type = cell.getCellType();
								double 	weight = 0.0;
								if( type == HSSFCell.CELL_TYPE_NUMERIC ) {
									weight = cell.getNumericCellValue();
								} else {
									String	val = cell.getStringCellValue();
									try {
										weight = Double.parseDouble(val);
									} catch( Exception e ) {
								
									}
								}
								cell = 	row.getCell(locind);
								int		loc = (int)cell.getNumericCellValue();
								
					
								FishWorker.Fish 		tmpf = null;
								if( sex.equalsIgnoreCase("male") ) {
									int 		mind = fishworker.findFish( name ); //fishes.indexOf( cmpf );
									if( mind != -1 ) {
										tmpf = fishworker.fishes.get(mind);
										tmpf.weight = (float)weight;
										tmpf.loc = loc;
										tmpf.male = true;
									} else {
										tmpf = fishworker.new Fish( name, (float)weight, loc, true );
									}
									fishworker.malefish.add( tmpf );
									fishworker.mfactor.add( 0.0f );
								} else if( sex.equalsIgnoreCase("female") ) {
									int 		find = fishworker.findFish( name );
									if( find != -1 ) {
										tmpf = fishworker.fishes.get(find);
										tmpf.weight = (float)weight;
										tmpf.loc = loc;
										tmpf.male = true;
									} else {
										tmpf = fishworker.new Fish( name, (float)weight, loc, true );
									}
									fishworker.femalefish.add( tmpf );
									fishworker.ffactor.add( 0.0f );
								} else {
									
								}
							}
							
							row = sheet.getRow( ++r );	
						}
						
						if( fishworker.fishes.size() > 0 ) {
							fishworker.initGenotypes();
						}
					} else if( path.endsWith(".txt") ) {
						char[]	cc = new char[1024];
						String val = "";
						FileReader fr = new FileReader( f );
						int r = fr.read( cc );
						while( r > 0 ) {
							val += new String( cc, 0, r );
							r = fr.read( cc );
						}
						fishworker.parseData( val, 3 );
					}
				}
			} else {
				File f = fc.getSelectedFile();
				String path = f.getAbsolutePath();
				if( path.endsWith(".xlsx" ) ) {
					fishworker.plainStuff( new FileInputStream(path) );
				}
			}
			
			fishworker.tupleList = fishworker.calcData();
			pairRel();
			reload();
			
			int m = fishworker.findFish( fishworker.malefish, "1" );
			int f = fishworker.findFish( fishworker.femalefish, "2" );
			
			System.err.println("start");
			for( int i = 0; i < fishworker.markers.size(); i++ ) {
				System.err.print( fishworker.mmatrix[m*fishworker.markers.size()+i] + " " );
			}
			System.err.println();
			for( int i = 0; i < fishworker.markers.size(); i++ ) {
				System.err.print( fishworker.fmatrix[f*fishworker.markers.size()+i] + " " );
			}
			System.err.println();
		}
	}
	
	/*class TestTable {
		public void prepare() {
			super.p
		}
	}*/
	
	public static void main( String[] args ) {
		Fiskur fish = new Fiskur();
		if( args.length == 0 ) {
			fish.initGui();
			
			JFrame frame = new JFrame("MateMeRight");
			frame.add( fish.c );
			frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
			frame.setVisible( true );
		}
	}
	
	public void init() {
		Window window = SwingUtilities.windowForComponent(this);
		if (window instanceof JFrame) {
			JFrame frame = (JFrame)window;
			if (!frame.isResizable()) frame.setResizable(true);
		}
		
		initGui();
		this.add( c );
	}
	
	public void initGui() {
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

		ToolTipManager.sharedInstance().setInitialDelay(0);

		this.setLayout(null);
		this.getContentPane().setLayout(null);
		this.setBackground(bgColor);
		this.getContentPane().setBackground(bgColor);

		Dimension d = new Dimension(900, 30);
		e = new JEditorPane();
		e.setEditable(false);
		e.setBackground(new Color(0, 0, 0, 0));
		e.setPreferredSize(d);
		e.setSize(d);
		e.setContentType("text/html");
		// e.setText("<html><body><center>Copyright 2009, Matis, ohf</center></body></html>");
		e.setText("<html><body><center><span style=\"color:gray\">Copyright 2009, Matis, ohf</span></center></body></html>");

		final JLabel		pairrel = new JLabel("Pairwise relatedness");
		
		final JRadioButton	kenheg = new JRadioButton();
		final JRadioButton	maxlik = new JRadioButton();
		
		ButtonGroup		bg = new ButtonGroup();
		bg.add( kenheg );
		bg.add( maxlik );
		maxlik.setSelected( true );
		
		final JButton	importbutton = new JButton();
		final JButton	xlbutton = new JButton();
		final JButton	kgbutton = new JButton();
		
		kenheg.setAction( new AbstractAction("Konovalov&Heg (2008)") {
			@Override
			public void actionPerformed(ActionEvent e) {
				fishworker.curInd = 0;
				
				//JTable table = 
				
				JComponent supcomp = (JComponent)Fiskur.this.splitpane.getLeftComponent();
				for( Component comp1 : supcomp.getComponents() ) {
					if( comp1 instanceof JTabbedPane ) {
						comp1 = ((JTabbedPane)comp1).getSelectedComponent();
						if( comp1 instanceof JScrollPane ) {
							JScrollPane scroll = (JScrollPane)comp1;
							Component comp2 = scroll.getViewport().getView();
							if( comp2 instanceof JTable ) {
								JTable table = (JTable)comp2;
								table.tableChanged( new TableModelEvent(table.getModel()) );
							}
						}
						break;
					}
				}
				
				Component comp1 = ((JTabbedPane)Fiskur.this.splitpane.getRightComponent()).getSelectedComponent();
				if( comp1 instanceof JScrollPane ) {
					JScrollPane scroll = (JScrollPane)comp1;
					Component comp2 = scroll.getViewport().getView();
					if( comp2 instanceof JTable ) {
						JTable table = (JTable)comp2;
						table.tableChanged( new TableModelEvent(table.getModel()) );
					}
				}
			}
		});
		
		maxlik.setAction( new AbstractAction("Lynch&Ritland (1999)") {
			@Override
			public void actionPerformed(ActionEvent e) {
				fishworker.curInd = 1;
				
				JComponent supcomp = (JComponent)Fiskur.this.splitpane.getLeftComponent();
				for( Component comp1 : supcomp.getComponents() ) {
					if( comp1 instanceof JTabbedPane ) {
						comp1 = ((JTabbedPane)comp1).getSelectedComponent();
						if( comp1 instanceof JScrollPane ) {
							JScrollPane scroll = (JScrollPane)comp1;
							Component comp2 = scroll.getViewport().getView();
							if( comp2 instanceof JTable ) {
								JTable table = (JTable)comp2;
								table.tableChanged( new TableModelEvent(table.getModel()) );
							}
						}
						break;
					}
				}

				Component comp1 = ((JTabbedPane)Fiskur.this.splitpane.getRightComponent()).getSelectedComponent();
				if( comp1 instanceof JScrollPane ) {
					JScrollPane scroll = (JScrollPane)comp1;
					Component comp2 = scroll.getViewport().getView();
					if( comp2 instanceof JTable ) {
						JTable table = (JTable)comp2;
						table.tableChanged( new TableModelEvent(table.getModel()) );
					}
				}
			}
		});
		
		subc = new JComponent() {
			public void paintComponent(Graphics g) {
				super.paintComponent(g);

				int h = this.getHeight();
				int w = this.getWidth();

				Graphics2D g2 = (Graphics2D) g;
				g2.setRenderingHint(RenderingHints.KEY_RENDERING,
						RenderingHints.VALUE_RENDER_QUALITY);
				g2.setRenderingHint(RenderingHints.KEY_DITHERING,
						RenderingHints.VALUE_DITHER_ENABLE);
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
						RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
						RenderingHints.VALUE_INTERPOLATION_BICUBIC);

				Paint p = g2.getPaint();
				GradientPaint gp = new GradientPaint(0.0f, 0.0f, lightGray,
						0.0f, h, darkGray);
				g2.setPaint(gp);
				g2.fillRoundRect(0, 0, w, h, 24, 24);
				g2.setPaint(p);
				// g2.drawImage( img, 0, 0, w, h, 0, h, w, 0, this );

				Stroke stroke = g2.getStroke();
				BasicStroke bs = new BasicStroke(2.0f);
				g2.setStroke(bs);
				g2.setColor(lightGray);
				g2.drawRoundRect(0, 0, w - 1, h - 1, 24, 24);
				g2.setStroke(stroke);

				String str = "MateMeRight";
				g2.drawImage(mimg, 15, 15, 30, 30, this);
				g2.setFont(g2.getFont().deriveFont(Font.BOLD, 16.0f));
				g2.setColor(Color.gray);
				g2.drawString(str, 50, 36);
				
				if( tabbedPane.getTitleAt(tabbedPane.getSelectedIndex()).equals("Summary") ) {
					int r = table.getSelectedRow();
					if( r != -1 ) {
						int c = table.getSelectedColumn();
						if( c == 0 || c == 1 ) {
							g2.setFont(g2.getFont().deriveFont(Font.BOLD, 12.0f));
							str = "Best matches for ";
							if( c == 0 ) str += "male ";
							else if( c == 1 ) str += "female ";
							str += "fish ";
							String val = (String)table.getValueAt(r, c);
							str += val;
							int strw = g2.getFontMetrics().stringWidth( str );
							g2.drawString( str, (this.getWidth()-strw)/2, 70 );
						}
					}
				}
				
				g2.setFont(g2.getFont().deriveFont(Font.BOLD, 12.0f));
				str = "Drop barcode image here";
				int strw = g2.getFontMetrics().stringWidth( str );
				g2.drawString( str, this.getWidth()-strw-20, 26 );
				g2.drawImage( barimg, this.getWidth()-barimg.getWidth()-20, 30, this );
				
				//g2.drawImage( xlimg, this.getWidth()-barimg.getWidth()-20, 30, this );
			}
			
			public void setBounds( int x, int y, int w, int h ) {
				super.setBounds(x,y,w,h);
				
				pairrel.setBounds( 400, 15, 180, 25 );
				
				kenheg.setBounds( 400, 40, 180, 25 );
				maxlik.setBounds( 400, 65, 180, 25 );
				
				importbutton.setBounds( 300, 15, 70, h-30 );
				kgbutton.setBounds( w-330, 15, 70, h-30 );
				xlbutton.setBounds( w-250, 15, 70, h-30 );
			}

			public boolean isVisible() {
				return showing && super.isVisible();
			}

			public boolean isShowing() {
				return showing && super.isShowing();
			}
		};
		d = new Dimension(884, 100);
		subc.setPreferredSize(d);
		subc.setSize(d);
		
		Action a = new AbstractAction("") {
			@Override
			public void actionPerformed(ActionEvent e) {
				XSSFWorkbook	wb = new XSSFWorkbook( );
				fishworker.writeWorkbook(wb);
				try {
					File			f = File.createTempFile("tmp", ".xlsx");
					wb.write( new FileOutputStream(f) );
					Desktop.getDesktop().open( f );
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		};
		xlbutton.setAction(a);
		xlbutton.setIcon( new ImageIcon(xlimg.getScaledInstance(56,56, Image.SCALE_SMOOTH)) );
		xlbutton.setToolTipText( "Export to excel" );
		
		kgbutton.setAction( new AbstractAction("<html><center>Export<br>to<br>Kinship<center></html>") {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					kintoolExport();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		importbutton.setAction( new AbstractAction("Import") {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					importStuff();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		subc.add( pairrel );
		subc.add( kenheg );
		subc.add( maxlik );
		subc.add( importbutton );
		subc.add( xlbutton );
		subc.add( kgbutton );

		final JScrollPane scrollpane = new JScrollPane();
		final JScrollPane mscrollpane = new JScrollPane();
		final JScrollPane fscrollpane = new JScrollPane();
		
		mscrollpane.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_NEVER );
		fscrollpane.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_NEVER );
		mscrollpane.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS );
		fscrollpane.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS );

		JPopupMenu popup = new JPopupMenu();
		popup.add(new AbstractAction("Paste") {
			@Override
			public void actionPerformed(ActionEvent e) {
				Object obj = null;
				try {
					obj = Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
				} catch (HeadlessException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (UnsupportedFlavorException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				if (obj != null) {
					String stuff = obj.toString();
					fishworker.parseData( stuff, 3 );
					fishworker.tupleList = fishworker.calcData();
					
					FloatBuffer	fdata = sd.dataBuffer;
					fdata.rewind();
					for( FishWorker.Tuple tup : fishworker.tupleList ) {
						fdata.put( (float)(tup.rank/1000.0) );
					}
					sd.loadData();
					
					reload();
				}
			}
		});
		scrollpane.setComponentPopupMenu( popup );
		
		c = new JComponent() {
			public void paintComponent(Graphics g) {
				super.paintComponent(g);

				int h = this.getHeight();
				int w = this.getWidth();

				Graphics2D g2 = (Graphics2D) g;
				g2.drawImage(img, 0, 0, w, h, this);
			}

			public boolean isVisible() {
				return showing && super.isVisible();
			}

			public boolean isShowing() {
				return showing && super.isShowing();
			}

			public void setBounds(int x, int y, int w, int h) {
				if (subc != null && table != null) {
					//subc.setLocation(Math.max(0, (w - subc.getWidth()) / 2), 8);
					subc.setBounds(15, 15, w-30, 100);
					splitpane.setBounds(15, 125, w-30, h-140);
				}
				super.setBounds(x, y, w, h);
			}
		};
		c.setFont(new Font("Arial", Font.BOLD, 14));
		c.setLayout(null);
		c.add(subc);

		d = new Dimension(900, 600);
		c.setPreferredSize(d);
		c.setSize(d);
		this.add(c);
		this.add(e);

		/*
		 * c = new JComponent() { public void paintComponent( Graphics g ) {
		 * super.paintComponent( g ); } }; c.setLayout( new BorderLayout() );
		 * this.getRootPane().setBackground( Color.white );
		 */

		table = new JTable() {
			Color origColor1 = null;
			Color origColor2 = null;
			Color origSelectColor = null;
			
			public Component prepareRenderer( TableCellRenderer renderer, int row, int column ) {
				Component c = super.prepareRenderer(renderer, row, column);
				
				boolean sel = this.getSelectionModel().isSelectedIndex(row) && this.getColumnModel().getSelectionModel().isSelectedIndex(column);
				boolean rel = row % 2 == 0;
				//if( this.getSel)
				
				boolean set = origColor1 != null && origColor2 != null && origSelectColor != null;
				
				if( !set ) {
					if( !sel ) {
						if( rel ) {
							if( origColor1 == null ) origColor1 = c.getBackground();
						} else {
							if( origColor2 == null ) origColor2 = c.getBackground();
						}
					} else {
						if( origSelectColor == null ) origSelectColor = c.getBackground();
					}
					this.repaint();
				} else {
					Object obj = this.getValueAt(row, column);
					if( column == 3 && obj instanceof Double ) {
						double val = (Double)this.getValueAt(row, column);
						if( val < 0.03 ) c.setBackground( mygreen );
						else if( val < 0.06 ) c.setBackground( myyellow );
						else c.setBackground( myred );
					} else {
						if( sel ) c.setBackground( origSelectColor );
						else {
							if( rel ) c.setBackground( origColor1 );
							else c.setBackground( origColor2 );
						}
						//System.err.println( obj + "  " + this.getModel().getColumnClass(column) );
					}
				}
				
				return c;
			}
		};
		table.setComponentPopupMenu( popup );
		table.setColumnSelectionAllowed( true );
		table.setAutoCreateRowSorter(true);
		TableModel model = new TableModel() {
			@Override
			public void addTableModelListener(TableModelListener arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public Class<?> getColumnClass(int arg0) {
				if (arg0 < 2)
					return String.class;
				else if (arg0 == 2)
					return Double.class;
				else if (arg0 == 3)
					return Float.class;

				return Object.class;
			}

			@Override
			public int getColumnCount() {
				return 4;
			}

			@Override
			public String getColumnName(int arg0) {
				if (arg0 == 0)
					return "Male";
				else if (arg0 == 1)
					return "Female";
				else if (arg0 == 3)
					return "Inbreeding value";
				else if (arg0 == 2)
					return "Performance factor";

				return null;
			}

			@Override
			public int getRowCount() {
				if (fishworker.tupleList != null) {
					return fishworker.tupleList.size();
				}

				return 0;
			}

			@Override
			public Object getValueAt(int arg0, int arg1) {
				if (fishworker.tupleList != null) {
					FishWorker.Tuple t = fishworker.tupleList.get(arg0);
					if (arg1 == 0)
						return t.male.name;
					if (arg1 == 1)
						return t.female.name;
					else if(arg1 == 3)
						return t.current();
					else if(arg1 == 2)
						return t.factor;
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
		table.setModel( model );
		scrollpane.setViewportView(table);
		
		mtable = new JTable() {
			public void sorterChanged( RowSorterEvent re ) {
				super.sorterChanged(re);
				//matrixTable.repaint();
			}
		};
		mtable.setComponentPopupMenu( popup );
		mtable.setColumnSelectionAllowed( true );
		//mtable.setAutoCreateRowSorter(true);
		final TableModel	mmodel = new TableModel() {

			@Override
			public void addTableModelListener(TableModelListener arg0) {}

			@Override
			public Class<?> getColumnClass(int arg0) {
				if (arg0 == 0)
					return String.class;
				else if (arg0 == 1)
					return Float.class;
				else if (arg0 == 2)
					return Float.class;

				return Object.class;
			}

			@Override
			public int getColumnCount() {
				return 3;
			}

			@Override
			public String getColumnName(int arg0) {
				if (arg0 == 0)
					return "Male";
				else if (arg0 == 1)
					return "Performance factor";
				else if (arg0 == 2)
					return "Weight";

				return null;
			}

			@Override
			public int getRowCount() {
				if (fishworker.malefish != null) {
					return fishworker.malefish.size();
				}

				return 0;
			}

			@Override
			public Object getValueAt(int arg0, int arg1) {
				if (arg1 == 0 && arg0 < fishworker.malefish.size()) {
					return fishworker.malefish.get(arg0).name;
				} else if (arg1 == 1 && arg0 < fishworker.mfactor.size() ) {
					return fishworker.mfactor.get(arg0);
				} else if (arg1 == 2 && arg0 < fishworker.malefish.size() ) {
					return fishworker.malefish.get(arg0).weight;
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
		mtable.setModel( mmodel );
		mscrollpane.setViewportView(mtable);
		
		final MySorter mtableSorter = new MySorter("male", mmodel) {
			public int convertRowIndexToModel(int index) {
				return currentSorter.convertRowIndexToModelSuper("male", index);
			}

			public int convertRowIndexToView(int index) {
				return currentSorter.convertRowIndexToViewSuper(index);
			}

			public int getViewRowCount() {
				return currentSorter.getViewRowCountSuper();
				//return leftTableSorter.getViewRowCount();
			}
		};
		currentSorter = mtableSorter;
		mtable.setRowSorter( mtableSorter );
		//MyFilter filter = new MyFilter( mmodel );
		//mtableSorter.setRowFilter( filter );
		
		mtableSorter.addRowSorterListener( new RowSorterListener() {
			@Override
			public void sorterChanged(RowSorterEvent e) {
				currentSorter = (MySorter)e.getSource();
				matrixTable.repaint();
				genotypeTable.repaint();
			}
		});
		
		ftable = new JTable() {
			public void sorterChanged( RowSorterEvent re ) {
				super.sorterChanged(re);
				//matrixTable.repaint();
			}
		};
		ftable.setComponentPopupMenu( popup );
		ftable.setColumnSelectionAllowed( true );
		//ftable.setAutoCreateRowSorter(true);
		final TableModel fmodel = new TableModel() {

			@Override
			public void addTableModelListener(TableModelListener arg0) {}

			public Class<?> getColumnClass(int arg0) {
				if (arg0 == 0)
					return String.class;
				else if (arg0 == 1)
					return Float.class;
				else if (arg0 == 2)
					return Float.class;

				return Object.class;
			}

			@Override
			public int getColumnCount() {
				return 3;
			}

			@Override
			public String getColumnName(int arg0) {
				if (arg0 == 0)
					return "Female";
				else if (arg0 == 1)
					return "Performance Factor";
				else if (arg0 == 2)
					return "Weight";

				return null;
			}

			@Override
			public int getRowCount() {
				if (fishworker.femalefish != null) {
					return fishworker.femalefish.size();
				}

				return 0;
			}

			@Override
			public Object getValueAt(int arg0, int arg1) {
				if( arg1 == 0 && arg0 < fishworker.femalefish.size() ) {
					return fishworker.femalefish.get(arg0).name;
				} else if( arg1 == 1 && arg0 < fishworker.ffactor.size() ) {
					return fishworker.ffactor.get(arg0);
				} else if( arg1 == 2 && arg0 < fishworker.femalefish.size() ) {
					return fishworker.femalefish.get(arg0).weight;
				}
				return null;
			}

			@Override
			public boolean isCellEditable(int arg0, int arg1) {
				return false;
			}

			@Override
			public void removeTableModelListener(TableModelListener arg0) {}

			@Override
			public void setValueAt(Object arg0, int arg1, int arg2) {}

		};
		ftable.setModel( fmodel );
		fscrollpane.setViewportView( ftable );
		
		final MySorter ftableSorter = new MySorter("female",fmodel) {
			public int convertRowIndexToModel(int index) {
				return currentSorter.convertRowIndexToModelSuper("female", index);
			}

			public int convertRowIndexToView(int index) {
				return currentSorter.convertRowIndexToViewSuper(index);
				// leftTableSorter.
			}

			public int getViewRowCount() {
				int rowcount = currentSorter.getViewRowCountSuper();
				return rowcount;
				//return leftTableSorter.getViewRowCount();
			}
		};
		ftable.setRowSorter( ftableSorter );
		//MyFilter nfilter = new MyFilter( fmodel );
		//ftableSorter.setRowFilter( nfilter );
		
		ftableSorter.addRowSorterListener( new RowSorterListener() {
			@Override
			public void sorterChanged(RowSorterEvent e) {
				currentSorter = (MySorter)e.getSource();
				matrixTable.repaint();
				genotypeTable.repaint();
			}
		});
		
		final GLCanvas matrixSurface = new GLCanvas();
		table.getRowSorter().addRowSorterListener( new RowSorterListener() {
			@Override
			public void sorterChanged(RowSorterEvent e) {				
				FloatBuffer	fdata = sd.dataBuffer;
				fdata.rewind();
				for( int i = 0; i < Math.min( fdata.limit(), table.getRowCount() ); i++ ) {
					double val = (Double)table.getValueAt(i, 3);
					fdata.put( i, (float)((val-1.0)/2.0) );
				}
				sd.loadData();
				if( tabbedPane.getTitleAt( tabbedPane.getSelectedIndex() ).equals("Surface") ) matrixSurface.display();
				else if( tabbedPane.getTitleAt( tabbedPane.getSelectedIndex() ).equals("Matrix") ) {
					//System.err.println( "refreshing matrix" );
					//matrixTable.setModel( nullModel );
					//matrixTable.setModel( matrixModel );
				}
			}
		});
		
		mtable.addMouseListener( new MouseAdapter() {
			public void mousePressed( MouseEvent me ) {
				int r = mtable.getSelectedRow();
				
				if( r != -1 ) {
					tuples.clear();
					String str = (String)mtable.getValueAt( r, 0 );
					for( FishWorker.Tuple t : fishworker.tupleList ) {
						if( t.male.equals( str ) ) {
							tuples.add( t );
						}
					}
					Collections.sort( tuples );
					
					summaryTable.setModel( nullModel );
					summaryTable.setModel( summaryModel );
				}
			}
		});
		
		ftable.addMouseListener( new MouseAdapter() {
			public void mousePressed( MouseEvent me ) {
				int r = ftable.getSelectedRow();
				
				if( r != -1 ) {
					tuples.clear();
					String str = (String)ftable.getValueAt( r, 0 );
					for( FishWorker.Tuple t : fishworker.tupleList ) {
						if( t.female.equals( str ) ) {
							tuples.add( t );
						}
					}
					Collections.sort( tuples );
					
					summaryTable.setModel( nullModel );
					summaryTable.setModel( summaryModel );
				}
			}
		});
		
		table.addMouseListener( new MouseAdapter() {
			public void mousePressed( MouseEvent e ) {
				int r = table.getSelectedRow();
				
				if( r != -1 ) {
					tuples.clear();
					int c = table.getSelectedColumn();
					if( c == 0 ) {
						String str = (String)table.getValueAt( r, c );
						for( FishWorker.Tuple t : fishworker.tupleList ) {
							if( t.male.equals( str ) ) {
								tuples.add( t );
							}
						}
					} else if( c == 1 ) {
						String str = (String)table.getValueAt( r, c );
						for( FishWorker.Tuple t : fishworker.tupleList ) {
							if( t.female.equals( str ) ) {
								tuples.add( t );
							}
						}
					}
					Collections.sort( tuples );
					
					summaryTable.setModel( nullModel );
					summaryTable.setModel( summaryModel );
					
					subc.repaint();
					
					if( tabbedPane.getTitleAt(tabbedPane.getSelectedIndex()).equals("Matrix") ) {
						int v = table.convertRowIndexToModel(r);
						
						int cc = v % fishworker.malefish.size();
						int rv = v / fishworker.malefish.size();
						
						//int v = table.convertRowIndexToView( i );
						int rr = matrixTable.convertRowIndexToView( rv );
						if( rr >= 0 && rr < matrixTable.getRowCount() ) {
							matrixTable.setRowSelectionInterval(rr, rr);
							matrixTable.setColumnSelectionInterval(cc, cc);
							matrixTable.scrollRectToVisible( matrixTable.getCellRect(rr, cc, true) );
						}
					}
				}
			}
		});
		
		table.getSelectionModel().addListSelectionListener( new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				int r = table.getSelectedRow();
				
				if( r != -1 ) {
					sd.makeSurface();
					
					int x = r%sd.matSize;
					int y = r/sd.matSize;
					
					sd.makeSurface(x, y, x+1, y+1, true, null);
					matrixSurface.display();
				}
			}
		});

		sd = new SurfaceDraw(25);
		matrixSurface.addGLEventListener( new GLEventListener() {
			
			@Override
			public void reshape(GLAutoDrawable drawable, int y, int x, int w, int h) {
				GL gl = drawable.getGL();
				((Component)drawable).setMinimumSize(new Dimension(0,0));
				
				gl.glMatrixMode( GL.GL_PROJECTION );
            	gl.glLoadIdentity();
            	if( d3 ) {
	            	if (w > h) {
	            		double aspect = w / h;
	            	    gl.glFrustum(-aspect, aspect, -1.0, 1.0, 1.0, 500.0);
	            	} else {
	            		double aspect = h / w;
	            	    gl.glFrustum (-1.0, 1.0, -aspect, aspect, 1.0, 500.0);
	            	}
	        		gl.glTranslatef(0.0f,0.0f,-4.0f);
	        		gl.glMatrixMode( GL.GL_MODELVIEW );
            	} else {
            		gl.glMatrixMode( GL.GL_MODELVIEW );
	            	gl.glLoadIdentity();
	    			gl.glScalef(0.025f, 0.025f, 0.025f);
            	}
            	
            	sd.initMatrix( gl );
			}
			
			@Override
			public void init(GLAutoDrawable drawable) {
				GL gl = drawable.getGL();
				sd.initLights( gl );
				sd.initMatrix( gl );
			}
			
			@Override
			public void displayChanged(GLAutoDrawable arg0, boolean arg1, boolean arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void display(GLAutoDrawable arg0) {
				GL gl = arg0.getGL();
				
				if( mouseState != -1 ) {						
					if( mouseState == 0 ) {
						//Scatter3D.scatterMouseMove(ex, ey, glu, gl);
					} else if( mouseState == 1 ) {
						//Scatter3D.scatterMouseDrag(ex, ey, glu, gl);
					} else if( mouseState == 2 ) {
						sd.mouseRightDrag(gl, ex, ey);
					} else if( mouseState == 10 ) {
						//scatterMouseUp( ex, ey, gl );
					} else if( mouseState == 11 ) {
						//scatterRightMouseUp( ex, ey, glu, gl );
					} else {
						sd.mouseDown(gl, ex, ey, mouseState);
					}
				}
				mouseState = -1;
				
				sd.draw( gl );
			}
		});
		
		matrixSurface.addMouseListener( new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				mouseState = 2 + e.getButton();
				ex = e.getX();
				ey = e.getY();
				matrixSurface.repaint();
			}

			public void mouseReleased(MouseEvent e) {
				if( e.getButton() == MouseEvent.BUTTON3 ) {
					mouseState = 11;
				} else {
					mouseState = 10;
				}
				matrixSurface.repaint();
			}
		});
		
		matrixSurface.addMouseMotionListener( new MouseMotionAdapter() {
			public void mouseMoved(MouseEvent e) {
				ex = e.getX();
				ey = e.getY();
				mouseState = 0;
				matrixSurface.repaint();
			}
			
			public void mouseDragged( MouseEvent e ) {
				if( (e.getModifiersEx() & MouseEvent.BUTTON3_DOWN_MASK) != 0 ) {
					ex = e.getX();
					ey = e.getY();
					mouseState = 2;
					matrixSurface.repaint();
				} else {
					ex = e.getX();
					ey = e.getY();
					mouseState = 1;
					matrixSurface.repaint();
				}
			}
		});

		nullModel = new TableModel() {
			
			@Override
			public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void removeTableModelListener(TableModelListener l) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public int getRowCount() {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public String getColumnName(int columnIndex) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public int getColumnCount() {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public Class<?> getColumnClass(int columnIndex) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public void addTableModelListener(TableModelListener l) {
				// TODO Auto-generated method stub
				
			}
		};
		//matrixModel = maleModel();

		matrixTable = new JTable() {
			/**
			 * 
			 */
			Color origColor = null;
			
			private static final long serialVersionUID = 1L;

			public void sorterChanged( RowSorterEvent re ) {
				super.sorterChanged( re );
				//mtable.repaint();
				//ftable.repaint();
			}
			
			public Component prepareRenderer( TableCellRenderer renderer, int row, int column ) {
				Component c = super.prepareRenderer(renderer, row, column);
				
				if( origColor == null ) origColor = c.getBackground();
				
				Object obj = this.getValueAt(row, column);
				if( obj instanceof Double ) {
					double val = (Double)this.getValueAt(row, column);
					if( val < 0.03 ) c.setBackground( mygreen );
					else if( val < 0.06 ) c.setBackground( myyellow );
					else c.setBackground( myred );
				} else {
					c.setBackground( origColor );
					//System.err.println( obj + "  " + this.getModel().getColumnClass(column) );
				}
				
				return c;
			}
		};
		matrixTable.addMouseListener( new MouseAdapter() {
			public void mousePressed( MouseEvent e ) {
				int row = matrixTable.getSelectedRow();
				if( row != -1 ) {
					int r = matrixTable.convertRowIndexToModel(row);
					int c = matrixTable.getSelectedColumn();
					int i = r * fishworker.malefish.size() + c;
					
					if( i >= 0 && i < table.getModel().getRowCount() ) {
						int v = table.convertRowIndexToView( i );
						if( v >= 0 ) {
							table.setRowSelectionInterval(v, v);
							table.scrollRectToVisible( table.getCellRect(v, 0, true) );
						}
					}
				}
			}
		});
		
		//matrixTable.setAutoCreateRowSorter( true );
		matrixTable.setColumnSelectionAllowed( true );
		matrixTable.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );
		final JScrollPane matrixScroll = new JScrollPane(matrixTable);
		matrixScroll.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS );
		
		//matrixScroll.setRowHeaderView( )
		
		//matrixRowHeader = new JTable();
		//matrixRowHeader.setPreferredSize( new Dimension(100,100) );
		//matrixScroll.setRowHeaderView( matrixRowHeader );
		//matrixScroll.getRowHeader().setPreferredSize( new Dimension(100,100) );

		summaryModel = new TableModel() {
			@Override
			public void addTableModelListener(TableModelListener l) {
			}

			@Override
			public Class<?> getColumnClass(int columnIndex) {
				if( columnIndex == 0 ) return String.class;
				else if( columnIndex == 1 ) return Float.class;
				else if( columnIndex == 2 ) return Double.class;
				
				return String.class;
			}

			@Override
			public int getColumnCount() {
				return 3;
			}

			@Override
			public String getColumnName(int columnIndex) {
				if( columnIndex == 0 ) return "Mate";
				else if( columnIndex == 1 ) return "Performance Rank";
				else if( columnIndex == 2 ) return "Inbreeding Value";
				
				return "";
			}

			@Override
			public int getRowCount() {
				return tuples.size();
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				FishWorker.Tuple t = tuples.get( rowIndex );
				
				boolean b = tuples.size() > 1 && tuples.get(0).male.equals( tuples.get(1).male );
				if( columnIndex == 0 ) {
					if( b ) return t.female.name;
					return t.male.name;
				}
				else if( columnIndex == 1 ) return t.factor;
				else if( columnIndex == 2 ) return t.current();
				
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
			public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
				// TODO Auto-generated method stub
				
			}
			
		};
		summaryTable = new JTable() {
			Color origColor1 = null;
			Color origColor2 = null;
			Color origSelectColor = null;
			
			public Component prepareRenderer( TableCellRenderer renderer, int row, int column ) {
				Component c = super.prepareRenderer(renderer, row, column);
				
				boolean sel = this.getSelectionModel().isSelectedIndex(row);
				boolean rel = row % 2 == 0;
				//if( this.getSel)
				
				boolean set = origColor1 != null && origColor2 != null && origSelectColor != null;
				
				if( !set ) {
					if( !sel ) {
						if( rel ) {
							if( origColor1 == null ) origColor1 = c.getBackground();
						} else {
							if( origColor2 == null ) origColor2 = c.getBackground();
						}
					} else {
						if( origSelectColor == null ) origSelectColor = c.getBackground();
					}
					this.repaint();
				} else {
					Object obj = this.getValueAt(row, column);
					if( column == 2 && obj instanceof Double ) {
						double val = (Double)this.getValueAt(row, column);
						if( val < 0.03 ) c.setBackground( mygreen );
						else if( val < 0.06 ) c.setBackground( myyellow );
						else c.setBackground( myred );
					} else {
						if( sel ) c.setBackground( origSelectColor );
						else {
							if( rel ) c.setBackground( origColor1 );
							else c.setBackground( origColor2 );
						}
						//System.err.println( obj + "  " + this.getModel().getColumnClass(column) );
					}
				}
				
				return c;
			}
		};
		summaryTable.setAutoCreateRowSorter( true );
		JScrollPane		summaryScroll = new JScrollPane( summaryTable );
		
		JScrollPane		infoPanel = null;
		
		genotypeTable = new JTable();
		genotypeTable.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );
		final JScrollPane		genotypeScroll = new JScrollPane( genotypeTable );
		
		tabbedPane = new JTabbedPane( JTabbedPane.BOTTOM );
		
		/*tabbedPane.addTab( "Summary", new VerticalTextIcon("Summary", true), summaryScroll );
		tabbedPane.addTab( "Summary", new VerticalTextIcon("Matrix", true), matrixScroll );
		tabbedPane.addTab( "Summary", new VerticalTextIcon("Surface", true), matrixSurface );
		tabbedPane.addTab( "Summary", new VerticalTextIcon("Info", true), infoPanel );
		tabbedPane.addTab( "Summary", new VerticalTextIcon("Genotypes", true), genotypePanel );*/
		
		tabbedPane.addTab("Summary", summaryScroll);
		tabbedPane.addTab("Matrix", matrixScroll);
		tabbedPane.addTab("Surface", matrixSurface);
		tabbedPane.addTab("Info", infoPanel);
		tabbedPane.addTab("Genotypes", genotypeScroll);
		
		final JTabbedPane	sex = new JTabbedPane( JTabbedPane.LEFT );
		
		sex.addTab( null, new VerticalTextIcon("Both", false), scrollpane );
		sex.addTab( null, new VerticalTextIcon("Male", false), mscrollpane );
		sex.addTab( null, new VerticalTextIcon("Female", false), fscrollpane );
		//sex.addTab("Both", scrollpane );
		//sex.addTab("Male", mscrollpane );
		//sex.addTab("Female", fscrollpane );
		
		tabbedPane.addChangeListener( new ChangeListener() {	
			@Override
			public void stateChanged(ChangeEvent e) {
				int selind = tabbedPane.getSelectedIndex();
				String seltit = tabbedPane.getTitleAt( selind );
				if( seltit.equals("Matrix") ) {
					int v = sex.getSelectedIndex();
					String title = sex.getIconAt(v).toString();
					
					if( title.equals("Both") ) {
						sex.setSelectedIndex(1);
					} else {
						if( v == 1 ) {
							matrixTable.setModel( femaleModel() );							
							matrixScroll.setRowHeaderView( mtable );
							mscrollpane.setViewport( matrixScroll.getRowHeader() );
							mscrollpane.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_NEVER );
						} else {
							matrixTable.setModel( maleModel() );
							matrixScroll.setRowHeaderView( ftable );
							fscrollpane.setViewport( matrixScroll.getRowHeader() );
							fscrollpane.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_NEVER );
						}
						
						MySorter sorter = new MySorter( "matrix", matrixTable.getModel() ) {
							public int convertRowIndexToModel(int index) {
								return currentSorter.convertRowIndexToModelSuper("matrix",index);
							}

							public int convertRowIndexToView(int index) {
								return currentSorter.convertRowIndexToViewSuper(index);
							}
						};
						matrixTable.setRowSorter( sorter );
						
						RowSorterListener rsl = new RowSorterListener() {
							@Override
							public void sorterChanged(RowSorterEvent e) {
								currentSorter = (MySorter)e.getSource();
								mtable.repaint();
								ftable.repaint();
							}
						};
						sorter.addRowSorterListener( rsl );
					}
				} else if( seltit.equals("Genotypes") ) {
					int v = sex.getSelectedIndex();
					String title = sex.getIconAt(v).toString();
					
					if( title.equals("Both") ) {
						sex.setSelectedIndex(1);
					} else {
						if( v == 1 ) {
							genotypeTable.setModel( femaleGenotypes() );
							genotypeScroll.setRowHeaderView( mtable );
							mscrollpane.setViewport( genotypeScroll.getRowHeader() );
							mscrollpane.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_NEVER );
						} else {
							genotypeTable.setModel( maleGenotypes() );
							genotypeScroll.setRowHeaderView( ftable );
							fscrollpane.setViewport( genotypeScroll.getRowHeader() );
							mscrollpane.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_NEVER );
						}
						
						MySorter sorter = new MySorter( "genotype", matrixTable.getModel() ) {
							public int convertRowIndexToModel(int index) {
								return currentSorter.convertRowIndexToModelSuper("genotype",index);
							}

							public int convertRowIndexToView(int index) {
								return currentSorter.convertRowIndexToViewSuper(index);
							}
						};
						genotypeTable.setRowSorter( sorter );
						
						RowSorterListener rsl = new RowSorterListener() {
							@Override
							public void sorterChanged(RowSorterEvent e) {
								currentSorter = (MySorter)e.getSource();
								mtable.repaint();
								ftable.repaint();
							}
						};
						sorter.addRowSorterListener( rsl );
					}
				} else if( seltit.equals("Summary") ) {
					mscrollpane.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_ALWAYS );
					fscrollpane.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_ALWAYS );
				}
				
			}
		});
		
		sex.addChangeListener( new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				int ind = sex.getSelectedIndex();
				String title = sex.getIconAt(ind).toString();
				//System.err.println( ind + "  " + title );
				String subtitle = tabbedPane.getTitleAt(tabbedPane.getSelectedIndex());
				
				if( title.equals("Both") ) {
					if( subtitle.equals("Matrix") ) {
						tabbedPane.setSelectedIndex(0);
					}
				} else if( title.equals("Male") ) {
					currentSorter = mtableSorter;
					if( subtitle.equals("Matrix") ) {
						matrixTable.setModel( femaleModel() );					
						matrixScroll.setRowHeaderView( mtable );
						mscrollpane.setViewport( matrixScroll.getRowHeader() );
					} else if( subtitle.equals("Genotypes") ) {
						genotypeTable.setModel( maleGenotypes() );					
						genotypeScroll.setRowHeaderView( mtable );
						mscrollpane.setViewport( genotypeScroll.getRowHeader() );
					}
					mtable.tableChanged( new TableModelEvent( mmodel ) );
				} else if( title.equals("Female") ) {
					currentSorter = ftableSorter;
					if( subtitle.equals("Matrix") ) {
						matrixTable.setModel( maleModel() );			
						matrixScroll.setRowHeaderView( ftable );
						fscrollpane.setViewport( matrixScroll.getRowHeader() );
					} else if( subtitle.equals("Genotypes") ) {
						genotypeTable.setModel( femaleGenotypes() );					
						genotypeScroll.setRowHeaderView( ftable );
						fscrollpane.setViewport( genotypeScroll.getRowHeader() );
					}
					ftable.tableChanged( new TableModelEvent( fmodel ) );
				}
				
				RowSorterListener rsl = new RowSorterListener() {
					@Override
					public void sorterChanged(RowSorterEvent e) {
						currentSorter = (MySorter)e.getSource();
						mtable.repaint();
						ftable.repaint();
					}
				};
				
				if( subtitle.equals("Matrix") ) {
					MySorter sorter = new MySorter( "matrix", matrixTable.getModel() ) {
						public int convertRowIndexToModel(int index) {
							return currentSorter.convertRowIndexToModelSuper("matrix",index);
						}
	
						public int convertRowIndexToView(int index) {
							return currentSorter.convertRowIndexToViewSuper(index);
						}
					};
					matrixTable.setRowSorter( sorter );
					sorter.addRowSorterListener( rsl );
				} else if( subtitle.equals("Genotypes") ) {
					MySorter sorter = new MySorter( "genotype", genotypeTable.getModel() ) {
						public int convertRowIndexToModel(int index) {
							return currentSorter.convertRowIndexToModelSuper("matrix",index);
						}
	
						public int convertRowIndexToView(int index) {
							return currentSorter.convertRowIndexToViewSuper(index);
						}
					};
					genotypeTable.setRowSorter( sorter );
					sorter.addRowSorterListener( rsl );
				}
			}
		});
		
		JTextField	search = new JTextField();
		JComponent leftComp = new JComponent() {
			
		};
		leftComp.setLayout( new BorderLayout() );
		leftComp.add( sex );
		leftComp.add( search, BorderLayout.SOUTH );

		splitpane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftComp, tabbedPane);
		c.add(splitpane);
		splitpane.setDividerLocation(300);

		ActionMap map = table.getActionMap();
		map.put(TransferHandler.getCutAction().getValue(Action.NAME),
				TransferHandler.getCutAction());
		map.put(TransferHandler.getCopyAction().getValue(Action.NAME),
				TransferHandler.getCopyAction());
		map.put(TransferHandler.getPasteAction().getValue(Action.NAME),
				TransferHandler.getPasteAction());

		InputMap imap = table.getInputMap();
		imap.put(KeyStroke.getKeyStroke("ctrl X"), TransferHandler
				.getCutAction().getValue(Action.NAME));
		imap.put(KeyStroke.getKeyStroke("ctrl C"), TransferHandler
				.getCopyAction().getValue(Action.NAME));
		imap.put(KeyStroke.getKeyStroke("ctrl V"), TransferHandler
				.getPasteAction().getValue(Action.NAME));

		TransferHandler th = new TransferHandler() {
			public int getSourceActions(JComponent c) {
				return TransferHandler.COPY;
			}

			public boolean canImport(TransferHandler.TransferSupport support) {
				return true;
			}

			protected Transferable createTransferable(JComponent c) {
				return new Transferable() {

					@Override
					public Object getTransferData(DataFlavor arg0) throws UnsupportedFlavorException, IOException {
						String ret = "";
						int[] rr = table.getSelectedRows();
						for (int r : rr) {
							ret += table.getValueAt(r, 0).toString() + "\t";
							ret += table.getValueAt(r, 1).toString() + "\t";
							ret += table.getValueAt(r, 2).toString() + "\n";
						}
						return ret;
					}

					@Override
					public DataFlavor[] getTransferDataFlavors() {
						return new DataFlavor[] { DataFlavor.stringFlavor, DataFlavor.javaFileListFlavor };
					}

					@Override
					public boolean isDataFlavorSupported(DataFlavor arg0) {
						if (arg0 == DataFlavor.stringFlavor || arg0 == DataFlavor.javaFileListFlavor ) {
							return true;
						}
						return false;
					}
				};
			}
			
			public boolean isDataFlavorSupported(DataFlavor flavor) {
				if (flavor == DataFlavor.stringFlavor || flavor == DataFlavor.javaFileListFlavor ) {
					return true;
				}
				return false;
			}

			public boolean importData(TransferHandler.TransferSupport support) {
				Object obj = null;
				
				int b = Arrays.binarySearch( support.getDataFlavors(), DataFlavor.javaFileListFlavor, new Comparator<DataFlavor>() {
					@Override
					public int compare(DataFlavor o1, DataFlavor o2) {
						return o1 == o2 ? 1 : 0;
					}
				});
				if( b != -1 ) {
					try {
						obj = support.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
					} catch (UnsupportedFlavorException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				try {
					if( obj != null && obj instanceof File[] ) {
						File[] ff = (File[])obj;
						fishworker.wbStuff( ff[0].getCanonicalPath() );
					} else {
						obj = support.getTransferable().getTransferData(DataFlavor.stringFlavor);
						if (obj != null) {
							String stuff = obj.toString();
							if( stuff.contains("file://") ) {
								URL url = new URL( stuff );
								fishworker.wbStuff( url.getFile() );
							}
							fishworker.parseData( stuff, 3 );
						}
					}
					fishworker.tupleList = fishworker.calcData();
					reload();
				} catch (UnsupportedFlavorException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

				return true;
			}
		};
		table.setTransferHandler( th );
		scrollpane.setTransferHandler( th );
		
		Rectangle r = this.getBounds();
		this.setBounds( r.x, r.y, r.width, r.height );
		
		InputStream in = this.getClass().getResourceAsStream("/matrix.txt");
		String stuff = "";
		byte[] bb = new byte[1024];
		int read;
		try {
			read = in.read( bb );
			while( read > 0 ) {
				stuff += new String( bb, 0, read );
				read = in.read( bb );
			}
			fishworker.parseData( stuff, 3 );
			fishworker.tupleList = fishworker.calcData();
			reload();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public void setBounds(int x, int y, int w, int h) {
		if (c != null) {
			c.setBounds(50, 50, w-100, h-100);
			//c.setLocation(Math.max(0, (w - c.getWidth()) / 2), Math.max(0, (h - c.getHeight()) / 2));
			e.setLocation((w - c.getWidth()) / 2, (h + c.getHeight()) / 2);
			e.setBounds(50, h-50, w-100, 50);
		}
		super.setBounds(x, y, w, h);
	}
	
	public class VerticalTextIcon implements Icon, SwingConstants {
	    private Font font = UIManager.getFont("Label.font");
	    private FontMetrics fm = Toolkit.getDefaultToolkit().getFontMetrics(font); 
	 
	    private String text; 
	    private int width, height;
	    private boolean clockwize; 
	 
	    public VerticalTextIcon(String text, boolean clockwize){ 
	        this.text = text; 
	        width = SwingUtilities.computeStringWidth(fm, text); 
	        height = fm.getHeight();
	        this.clockwize = clockwize; 
	    }
	    
	    public String toString() {
	    	return text;
	    }
	 
	    public void paintIcon(Component c, Graphics g, int x, int y){ 
	        Graphics2D g2 = (Graphics2D)g;
	        g2.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
	        Font oldFont = g.getFont(); 
	        Color oldColor = g.getColor();
	        AffineTransform oldTransform = g2.getTransform(); 
	 
	        g.setFont(font); 
	        g.setColor(Color.black); 
	        if(clockwize){ 
	            g2.translate(x+getIconWidth(), y); 
	            g2.rotate(Math.PI/2); 
	        }else{ 
	            g2.translate(x, y+getIconHeight()); 
	            g2.rotate(-Math.PI/2); 
	        } 
	        g.drawString(text, 0, fm.getLeading()+fm.getAscent()); 
	 
	        g.setFont(oldFont); 
	        g.setColor(oldColor); 
	        g2.setTransform(oldTransform); 
	    } 
	 
	    public int getIconWidth(){ 
	        return height; 
	    } 
	 
	    public int getIconHeight(){ 
	        return width; 
	    }
	}
}
