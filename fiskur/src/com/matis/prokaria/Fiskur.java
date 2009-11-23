package com.matis.prokaria;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
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
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLEventListener;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JApplet;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ToolTipManager;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.RowSorterListener;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Fiskur extends JApplet {
	JComponent 		c;
	
	List<String> 	markers;
	List<String> 	malefish;
	List<String> 	femalefish;
	List<Float>		ffactor;
	List<Float>		mfactor;
	
	int[] mmatrix;
	int[] fmatrix;
	List<Tuple> tupleList;
	//List<Tuple> unsortedTupleList;
	List<Tuple>	tuples = new ArrayList<Tuple>();
	
	final Color darkGray = new Color(230, 230, 230);
	final Color lightGray = new Color(250, 250, 250);
	JEditorPane e;
	JComponent subc;
	final Color bgColor = new Color(255, 255, 200);
	Image 			img;
	Image 			mimg;
	BufferedImage 	barimg;

	JTable 		table;
	JTable		summaryTable;
	JTable		matrixTable;
	JTable		matrixRowHeader;
	TableModel	rowHeaderModel;
	TableModel	summaryModel;
	TableModel	matrixModel;
	TableModel	nullModel;
	
	JTabbedPane	tabbedPane;
	SurfaceDraw	sd;
	
    int 			mouseState = -1;
    int				ex, ey;
    
    boolean			d3 = true;	
	boolean 		showing = true;

	JSplitPane splitpane;

	public Fiskur() {
		super();

		try {
			URL url = this.getClass().getResource("/smooth.png");
			img = ImageIO.read(url);
			url = this.getClass().getResource("/matis.png");
			mimg = ImageIO.read(url);
			url = this.getClass().getResource("/images.jpeg");
			barimg = ImageIO.read(url);
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

	public void parseData(String data) {
		String[] split = data.split("\\n");
		String[] vals = split[0].split("\\t");
		int r = (split.length - 1);
		int c = (vals.length - 3);

		markers = new ArrayList<String>();
		for (int i = 0; i < c; i++) {
			markers.add(vals[i + 3]);
		}

		malefish = new ArrayList<String>();
		femalefish = new ArrayList<String>();
		mfactor = new ArrayList<Float>();
		ffactor = new ArrayList<Float>();

		for (int i = 0; i < r; i++) {
			vals = split[i + 1].split("\\t");

			if (vals[0].equalsIgnoreCase("pcod1") ) {
				femalefish.add(vals[1]);
				ffactor.add( Float.parseFloat( vals[2] ) );
			} else {
				malefish.add(vals[1]);
				mfactor.add( Float.parseFloat( vals[2] ) );
			}
			
			
		}

		fmatrix = new int[femalefish.size() * c];
		mmatrix = new int[malefish.size() * c];

		int f = 0;
		int m = 0;

		for (int i = 0; i < r; i++) {
			vals = split[i + 1].split("\\t");

			if (vals[0].equalsIgnoreCase("pcod1")) {
				for (int k = 0; k < c; k++) {
					fmatrix[f * c + k] = Integer.parseInt(vals[k + 3]);
				}
				f++;
			} else {
				for (int k = 0; k < c; k++) {
					mmatrix[m * c + k] = Integer.parseInt(vals[k + 3]);
				}
				m++;
			}
		}
	}

	public class Tuple implements Comparable<Tuple> {
		String 	male;
		String 	female;
		int 	rank;
		float	factor;

		public Tuple(String f1, String f2, int r, float f) {
			male = f2;
			female = f1;
			rank = r;
			factor = f;
		}

		@Override
		public int compareTo(Tuple arg0) {
			return arg0.rank - rank;
		}
	};

	public List<Tuple> calcData() {
		int mr = malefish.size();
		int fr = femalefish.size();
		int c = markers.size();

		List<Tuple> tupleList = new ArrayList<Tuple>();

		for (int i = 0; i < fr; i++) {
			String n1 = femalefish.get(i);
			float  ff = ffactor.get(i);
			for (int k = 0; k < mr; k++) {
				String n2 = malefish.get(k);
				float  mf = mfactor.get(k);
				int rank = 0;
				for (int u = 0; u < c; u++) {
					rank += Math.abs(fmatrix[i * c + u] - mmatrix[k * c + u]);
				}
				tupleList.add(new Tuple(n1, n2, rank, mf+ff));
			}
		}
		//unsortedTupleList = tupleList;

		//tupleList = new ArrayList<Tuple>( tupleList );
		//Collections.sort(tupleList);

		return tupleList;
	}

	public void start() {
		super.start();
		try {
			UIManager
					.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
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
		matrixTable.setModel( nullModel );
		matrixTable.setModel( matrixModel );
		
		matrixTable.getRowSorter().addRowSorterListener( new RowSorterListener() {
			@Override
			public void sorterChanged(RowSorterEvent e) {
				//System.err.println( "sortch" );
				
				matrixRowHeader.setModel( nullModel );
				matrixRowHeader.setModel( rowHeaderModel );
				
				//matrixScroll.invalidate();
				//matrixScroll.revalidate();
				//matrixScroll.repaint();
				
				//matrixRowHeader.invalidate();
				//matrixRowHeader.revalidate();
				//matrixRowHeader.repaint();
			}
		});
		
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
				return femalefish.size();
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				int r = matrixTable.convertRowIndexToModel(rowIndex );
				return femalefish.get( r );
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
			public void setValueAt(Object aValue, int rowIndex,
					int columnIndex) {
				// TODO Auto-generated method stub
				
			}
			
		};
		matrixRowHeader.setModel( rowHeaderModel );
		matrixRowHeader.getColumn("Names").setWidth(100);
		matrixRowHeader.getColumn("Names").setMaxWidth(100);
		matrixRowHeader.getColumn("Names").setPreferredWidth(100);
		
		System.err.println( matrixModel.getRowCount() + "  " + matrixModel.getColumnCount() );
		System.err.println( matrixTable.getRowCount() + "  " + matrixTable.getColumnCount() );
		System.err.println( femalefish.size() + "  " + malefish.size() );
		
		table.revalidate();
		table.invalidate();
		table.repaint();
	}

	public void init() {
		try {
			UIManager
					.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
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

		final JScrollPane scrollpane = new JScrollPane();

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
					parseData(stuff);
					tupleList = calcData();
					
					FloatBuffer	fdata = sd.dataBuffer;
					fdata.rewind();
					for( Tuple tup : tupleList ) {
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

		table = new JTable();
		table.setComponentPopupMenu( popup );
		table.setColumnSelectionAllowed( true );
		table.setAutoCreateRowSorter(true);
		table.setModel(new TableModel() {

			@Override
			public void addTableModelListener(TableModelListener arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public Class<?> getColumnClass(int arg0) {
				if (arg0 < 2)
					return String.class;
				else if (arg0 == 2)
					return Integer.class;
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
				else if (arg0 == 2)
					return "Similarity Rank";
				else if (arg0 == 3)
					return "K Factor";

				return null;
			}

			@Override
			public int getRowCount() {
				if (tupleList != null) {
					return tupleList.size();
				}

				return 0;
			}

			@Override
			public Object getValueAt(int arg0, int arg1) {
				if (tupleList != null) {
					Tuple t = tupleList.get(arg0);
					if (arg1 == 0)
						return t.male;
					if (arg1 == 1)
						return t.female;
					else if(arg1 == 2)
						return t.rank;
					else if(arg1 == 3)
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

		});
		scrollpane.setViewportView(table);
		
		final GLCanvas matrixSurface = new GLCanvas();
		table.getRowSorter().addRowSorterListener( new RowSorterListener() {
			@Override
			public void sorterChanged(RowSorterEvent e) {				
				FloatBuffer	fdata = sd.dataBuffer;
				fdata.rewind();
				for( int i = 0; i < table.getRowCount(); i++ ) {
					int val = (Integer)table.getValueAt(i, 2);
					fdata.put( (float)((val-100)/200.0) );
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
		
		table.addMouseListener( new MouseAdapter() {
			public void mousePressed( MouseEvent e ) {
				int r = table.getSelectedRow();
				
				if( r != -1 ) {
					tuples.clear();
					int c = table.getSelectedColumn();
					if( c == 0 ) {
						String str = (String)table.getValueAt( r, c );
						for( Tuple t : tupleList ) {
							if( t.male.equals( str ) ) {
								tuples.add( t );
							}
						}
					} else if( c == 1 ) {
						String str = (String)table.getValueAt( r, c );
						for( Tuple t : tupleList ) {
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
						
						int cc = v % malefish.size();
						int rv = v / malefish.size();
						
						//int v = table.convertRowIndexToView( i );
						int rr = matrixTable.convertRowIndexToView( rv );
						matrixTable.setRowSelectionInterval(rr, rr);
						matrixTable.setColumnSelectionInterval(cc, cc);
						matrixTable.scrollRectToVisible( matrixTable.getCellRect(rr, cc, true) );
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
		matrixModel = new TableModel() {
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
				if (malefish != null)
					return malefish.size();
				return 0;
			}

			@Override
			public String getColumnName(int columnIndex) {
				return malefish.get(columnIndex);
			}

			@Override
			public int getRowCount() {
				if (femalefish != null)
					return femalefish.size();
				return 0;
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				int val = rowIndex * malefish.size() + columnIndex;
				//int rval = table.convertRowIndexToModel( val );
				Tuple tup = tupleList.get( val );
				return tup.rank;
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

		matrixTable = new JTable(matrixModel);
		
		matrixTable.addMouseListener( new MouseAdapter() {
			public void mousePressed( MouseEvent e ) {
				int row = matrixTable.getSelectedRow();
				if( row != -1 ) {
					int r = matrixTable.convertRowIndexToModel(row);
					int c = matrixTable.getSelectedColumn();
					int i = r * malefish.size() + c;
					
					int v = table.convertRowIndexToView( i );
					table.setRowSelectionInterval(i, i);
					table.scrollRectToVisible( table.getCellRect(i, 0, true) );
				}
			}
		});
		
		matrixTable.setAutoCreateRowSorter( true );
		matrixTable.setColumnSelectionAllowed( true );
		matrixTable.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );
		final JScrollPane matrixScroll = new JScrollPane(matrixTable);
		
		matrixRowHeader = new JTable();
		//matrixRowHeader.setPreferredSize( new Dimension(100,100) );
		matrixScroll.setRowHeaderView( matrixRowHeader );
		matrixScroll.getRowHeader().setPreferredSize( new Dimension(100,100) );

		summaryModel = new TableModel() {
			@Override
			public void addTableModelListener(TableModelListener l) {
			}

			@Override
			public Class<?> getColumnClass(int columnIndex) {
				if( columnIndex == 0 ) return String.class;
				else if( columnIndex == 1 ) return Integer.class;
				else if( columnIndex == 2 ) return Float.class;
				
				return String.class;
			}

			@Override
			public int getColumnCount() {
				return 3;
			}

			@Override
			public String getColumnName(int columnIndex) {
				if( columnIndex == 0 ) return "Mate";
				else if( columnIndex == 1 ) return "Rank";
				else if( columnIndex == 2 ) return "Factor";
				
				return "";
			}

			@Override
			public int getRowCount() {
				return tuples.size();
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				Tuple t = tuples.get( rowIndex );
				
				boolean b = tuples.size() > 1 && tuples.get(0).male.equals( tuples.get(1).male );
				if( columnIndex == 0 ) {
					if( b ) return t.female;
					return t.male;
				}
				else if( columnIndex == 1 ) return t.rank;
				else if( columnIndex == 2 ) return t.factor;
				
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
		summaryTable = new JTable();
		JScrollPane		summaryScroll = new JScrollPane( summaryTable );
		
		tabbedPane = new JTabbedPane( JTabbedPane.BOTTOM );
		tabbedPane.addTab("Summary", summaryScroll);
		tabbedPane.addTab("Matrix", matrixScroll);
		tabbedPane.addTab("Surface", matrixSurface);
		
		JTextField	search = new JTextField();
		JComponent leftComp = new JComponent() {
			
		};
		leftComp.setLayout( new BorderLayout() );
		leftComp.add( scrollpane );
		leftComp.add( search, BorderLayout.SOUTH );

		splitpane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollpane, tabbedPane);
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

		this.add(c);

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
				System.err.println(b);
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
						wbStuff( ff[0].getCanonicalPath() );
					} else {
						obj = support.getTransferable().getTransferData(DataFlavor.stringFlavor);
						if (obj != null) {
							String stuff = obj.toString();
							if( stuff.contains("file://") ) {
								URL url = new URL( stuff );
								wbStuff( url.getFile() );
							}
							parseData(stuff);
							tupleList = calcData();
							
							reload();
						}
					}	
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
			parseData(stuff);
			tupleList = calcData();
			reload();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public void wbStuff( String path ) throws IOException {
		XSSFWorkbook 	wb = new XSSFWorkbook( path );
		XSSFSheet		ws = wb.getSheetAt(0);
		
		malefish = new ArrayList<String>();
		femalefish = new ArrayList<String>();
		mfactor = new ArrayList<Float>();
		ffactor = new ArrayList<Float>();
		
		int i = 0;
		XSSFRow			wr = ws.getRow( i++ );
		
		int k = 3;
		int c = 0;
		markers = new ArrayList<String>();
		XSSFCell wc = wr.getCell(k++);
		while( wc != null ) {
			markers.add( wc.getStringCellValue() );
			c++;
			wc = wr.getCell(k++);
		}
		
		wr = ws.getRow( i++ );
		while( wr != null ) {
			wc = wr.getCell(0);
			if ( wc.getStringCellValue().equalsIgnoreCase("pcod1") ) {
				femalefish.add( wr.getCell(1).getStringCellValue() );
				ffactor.add( (float)wr.getCell(2).getNumericCellValue() );
			} else {
				malefish.add( wr.getCell(1).getStringCellValue() );
				mfactor.add( (float)(wr.getCell(2).getNumericCellValue()) );
			}
			
			wr = ws.getRow( i++ );
		}

		fmatrix = new int[femalefish.size() * c];
		mmatrix = new int[malefish.size() * c];

		int f = 0;
		int m = 0;
		
		while( wr != null ) {
			if (wr.getCell(0).getStringCellValue().equalsIgnoreCase("pcod1")) {
				k = 3;
				wc = wr.getCell(k++);
				while( wc != null ) {
					fmatrix[m * c + k] = (int)wc.getNumericCellValue();
					wc = wr.getCell(k++);
				}
				f++;
			} else {
				k = 3;
				wc = wr.getCell(k++);
				while( wc != null ) {
					mmatrix[m * c + k] = (int)wc.getNumericCellValue();
					wc = wr.getCell(k++);
				}
				f++;
				m++;
			}
			
			wr = ws.getRow( i++ );
		}	
		
		tupleList = calcData();
		
		table.revalidate();
		table.invalidate();
		table.repaint();
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
}
