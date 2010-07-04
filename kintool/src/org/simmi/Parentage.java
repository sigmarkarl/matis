package org.simmi;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Window;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.JApplet;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JSpinner;
import javax.swing.JToolBar;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Parentage extends JApplet {
	BufferedImage	xlsImg;
	JComponent		xlsComp;
	//XSSFWorkbook	workbook;
	static int		villur = 0;
	
	public Parentage() {
		super();
		
		try {
			xlsImg = ImageIO.read(this.getClass().getResource("/xlsx.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	static String lof = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
	public void init() {
		try {
			UIManager.setLookAndFeel(lof);
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
		
		Window window = SwingUtilities.windowForComponent(this);
		if (window instanceof JFrame) {
			JFrame frame = (JFrame)window;
			if (!frame.isResizable()) frame.setResizable(true);
		}
		
		final JProgressBar progressbar = new JProgressBar();
		JToolBar	toolbar = new JToolBar() {
			
		};
		//toolbar.setLayout( new BorderLayout() );
		AbstractAction action = new AbstractAction("Open") {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser	filechooser = new JFileChooser();
				if( filechooser.showOpenDialog( Parentage.this ) == JFileChooser.APPROVE_OPTION ) {
					File f = filechooser.getSelectedFile();
					try {
						load( f.getCanonicalPath(), progressbar );
						/*JFileChooser fc = new JFileChooser( f.getParentFile() );
						if( fc.showSaveDialog( Parentage.this.xlsComp ) == JFileChooser.APPROVE_OPTION ) {
							f = fc.getSelectedFile();
							workbook.write( new FileOutputStream( f ) );
						} else {
							f = File.createTempFile("tmp", ".xlsx");
							workbook.write( new FileOutputStream( f ) );
							Desktop.getDesktop().open( f );
						}*/
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		};
		toolbar.add( action );
		
		this.getContentPane().setBackground( Color.white );
		xlsComp = new JComponent() {
			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(xlsImg, (this.getWidth()-xlsImg.getWidth())/2, (this.getHeight()-xlsImg.getHeight())/2, this);
			}
		};
		
		JComponent c = new JComponent() {
			public void setBounds(int x, int y, int w, int h) {
				super.setBounds(x, y, w, h);
				if( xlsImg != null ) xlsComp.setBounds( (this.getWidth() - xlsImg.getWidth() ) / 2, (this.getHeight() - xlsImg.getHeight()) / 2, xlsImg.getWidth(), xlsImg.getHeight());
			}
		};
		c.setLayout( new BorderLayout() );
		c.add( xlsComp );
		//c.add( progressbar, BorderLayout.SOUTH );
		
		JComponent v = new JComponent() {};
		v.setLayout( new BorderLayout() );
		final JSpinner 	spinner = new JSpinner( new SpinnerNumberModel(0, 0, 3, 1));
		spinner.addChangeListener( new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				villur = (Integer)spinner.getValue();
			}
		});
		JLabel		label = new JLabel( "Villur: ", SwingConstants.RIGHT );
		v.add( spinner, BorderLayout.EAST );
		v.add( label );
		v.setPreferredSize( new Dimension(100,25) );
		toolbar.add( v );
		this.setLayout( new BorderLayout() );
		this.add( c );
		this.add( progressbar, BorderLayout.SOUTH );
		this.add( toolbar, BorderLayout.NORTH );
		
		TransferHandler th = new TransferHandler() {
			@Override
			public int getSourceActions(JComponent c) {
				return TransferHandler.COPY_OR_MOVE;
			}

			@Override
			public boolean canImport(TransferHandler.TransferSupport support) {
				return true;
			}

			@Override
			protected Transferable createTransferable(JComponent c) {
				return new Transferable() {

					@Override
					public Object getTransferData(DataFlavor arg0)
							throws UnsupportedFlavorException, IOException {
						if (arg0 == DataFlavor.getTextPlainUnicodeFlavor()) {
							return null;
						} else {
							return null;
						}
					}

					@Override
					public DataFlavor[] getTransferDataFlavors() {
						return new DataFlavor[] { DataFlavor.getTextPlainUnicodeFlavor() };
					}

					@Override
					public boolean isDataFlavorSupported(DataFlavor arg0) {
						if (arg0 == DataFlavor.getTextPlainUnicodeFlavor()) {
							return true;
						}
						return false;
					}
				};
			}

			@Override
			public boolean importData(TransferHandler.TransferSupport support) {
				Object obj = null;

				int b = Arrays.binarySearch(support.getDataFlavors(),
						DataFlavor.javaFileListFlavor,
						new Comparator<DataFlavor>() {
							@Override
							public int compare(DataFlavor o1, DataFlavor o2) {
								return o1 == o2 ? 1 : 0;
							}
						});
				System.err.println(b);
				if (b != -1) {
					try {
						obj = support.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
					} catch (UnsupportedFlavorException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				try {
					if (obj != null && obj instanceof List) {
						List<File>	l = (List<File>)obj;
						File f = l.get(0);
						load( f.getCanonicalPath(), progressbar );
						
						int i = 0;
						File nf = File.createTempFile("tmp", ".xlsx");
						/*File nf = new File( System.getProperty("user.home"), "tmp"+i+".xlsx" );
						while( !nf.canWrite() && i < 10 ) {
							i++;
							nf = new File( System.getProperty("user.home"), "tmp"+i+".xlsx" );
						}*/
						
						/*
						//workbook.write( new FileOutputStream( nf ) );
						//Desktop.getDesktop().open( nf );
						 */
						
						//JFileChooser fc = new JFileChooser( System.getProperty("user.home") );
						/*if( fc.showSaveDialog( Report.this.xlsComp ) == JFileChooser.APPROVE_OPTION ) {
							workbook.write( new FileOutputStream( fc.getSelectedFile() ) );
						}*/
					} else {
						if( true ) {
							obj = support.getTransferable().getTransferData(DataFlavor.stringFlavor);
							if ( obj != null ) {
								String stuff = obj.toString();
								if (stuff.contains("file://")) {
									URL url = new URL(stuff);
									File f = new File( URLDecoder.decode( url.getFile(), "UTF-8" ) );
									//URLDecoder.decode( f, "UTF-8" )
									
									villur = 0;
									load( f.getCanonicalPath(), progressbar );
									villur = 1;
									load( f.getCanonicalPath(), progressbar );
									//villur = 2;
									//load( f.getCanonicalPath() );
									
									//int i = 0;
									//File nf = File.createTempFile("tmp", ".xlsx");
									//File nf = new File( System.getProperty("user.home"), "tmp"+i+".xlsx" );
									/*while( nf.exists() && !nf.canWrite() && i < 10 ) {
										i++;
										nf = new File( System.getProperty("user.home"), "tmp"+i+".xlsx" );
									}*/
									//workbook.write( new FileOutputStream( nf ) );
								}
							}
						}/* else {
							//char[] cc = new char[256];
							//Reader r = DataFlavor.getTextPlainUnicodeFlavor().getReaderForText(support.getTransferable());
							//int read = r.read(cc);
							obj = support.getTransferable().getTransferData(DataFlavor.stringFlavor);
							if ( obj != null ) {
								String stuff = obj.toString();
								if (stuff.contains("file://")) {
									URL url = new URL(stuff);
									File f = new File( URLDecoder.decode( url.getFile(), "UTF-8" ) );
									//URLDecoder.decode( f, "UTF-8" )
									load( f.getCanonicalPath(), progressbar );
									JFileChooser fc = new JFileChooser( f.getParentFile() );
									if( fc.showSaveDialog( Parentage.this.xlsComp ) == JFileChooser.APPROVE_OPTION ) {
										workbook.write( new FileOutputStream( fc.getSelectedFile() ) );
									}
								}
							}
						}*/
					}
				} catch (UnsupportedFlavorException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

				return true;
			}
		};
		xlsComp.setTransferHandler(th);
	}
	
	public static String cellVal( XSSFCell cell ) {
		if( cell == null ) return null;
		else if( cell.getCellType() == XSSFCell.CELL_TYPE_STRING ) {
			return cell.getStringCellValue().length() == 0 ? null : cell.getStringCellValue();
		} else if( cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC ) return Double.toString( cell.getNumericCellValue() );
		
		return null;
	}
	
	public static XSSFWorkbook subload( InputStream inputStream, JProgressBar pbar ) throws IOException {
		if( pbar != null ) {
			pbar.setString("Opening excel file ...");
			pbar.setStringPainted( true );
			pbar.setIndeterminate( true );
			pbar.setValue(0);
		}
		XSSFWorkbook workbook = new XSSFWorkbook( inputStream );
		XSSFSheet		m_sheet = workbook.getSheet("Mæður");
		if( m_sheet == null ) m_sheet = workbook.getSheetAt( 0 );
		XSSFSheet		f_sheet = workbook.getSheet("Feður");
		if( f_sheet == null ) f_sheet = workbook.getSheetAt( 1 );
		XSSFSheet		b_sheet = workbook.getSheet("Afkvæmi");
		if( b_sheet == null ) b_sheet = workbook.getSheetAt( 2 );
		XSSFSheet		p_sheet = workbook.createSheet("Foreldrar");
		
		final Set<XSSFRow>	set = new HashSet<XSSFRow>();
		final List<XSSFRow>	mlist = new ArrayList<XSSFRow>();
		final List<XSSFRow>	flist = new ArrayList<XSSFRow>();
		
		int 		counter = 0;
		XSSFRow b_row = b_sheet.getRow(counter++);
		int			cellcount = 0;
		XSSFCell	b_cell = b_row.getCell(cellcount++);
		while( b_cell != null && (b_cell.getCellType() != XSSFCell.CELL_TYPE_STRING || b_cell.getStringCellValue().length() > 0) ) {
			b_cell = b_row.getCell(cellcount++);
		}
		cellcount = cellcount - 1;
		b_row = b_sheet.getRow(counter++);
		while( b_row != null ) {
			int cnum = 0;
			b_cell = b_row.getCell(cnum);
			while( b_cell != null && (b_cell.getCellType() != XSSFCell.CELL_TYPE_STRING || b_cell.getStringCellValue().length() > 0) ) {
				cnum++;
				b_cell = b_row.getCell(cnum);
			}
			//if( cnum > cellcount ) cellcount = cnum;
			if( cnum < 1 ) break;
			
			b_row = b_sheet.getRow(counter++);
		}
		counter = counter - 2;
		
		if( pbar != null ) {
			pbar.setIndeterminate( false );
			pbar.setMinimum(0);
			pbar.setMaximum(counter);
			pbar.setValue(0);
		}

		/*XSSFRow 	b_row = b_sheet.getRow(0);
		while( b_row.getCell(cellcount) != null ) cellcount++;
		//markercount -= 2;
		cellcount = 37;*/
		
		int rr = 0;
		XSSFRow rrow = p_sheet.createRow(rr++);
		int br = 1;
		b_row = b_sheet.getRow(br++);		
		while( b_row != null ) {
			Cell cell = b_row.getCell(0);
			if( cell != null && (cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC || cell.getStringCellValue().length() > 0) ) {
				if( pbar != null ) {
					pbar.setValue( br );
					pbar.setString( Integer.toString(br) );
				}
				
				cell = b_row.getCell(0);
				String bname = cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC ? Double.toString(cell.getNumericCellValue()) : cell.getStringCellValue();
				System.err.println( bname );
				XSSFCell rcell = rrow.createCell( 0 );
				//rcell.setCellType( XSSFCell.CELL_TYPE_STRING );
				rcell.setCellValue( bname );
				rrow = p_sheet.createRow(rr++);
				
				mlist.clear();
				int mr = 1;
				XSSFRow 	m_row = m_sheet.getRow(mr++);
				while( m_row != null ) {
					cell = m_row.getCell(0);
					if( cell != null && (cell.getCellType() == Cell.CELL_TYPE_NUMERIC || cell.getStringCellValue().length() > 0) ) {
						int c = 1;
						
						int dcount = 0;
						int	mcount = 0;
						while( c < cellcount ) {
							XSSFCell	b_a1 = b_row.getCell(c);
							XSSFCell	b_a2 = b_row.getCell(c+1);
							XSSFCell	m_a1 = m_row.getCell(c);
							XSSFCell	m_a2 = m_row.getCell(c+1);
							
							boolean nb = m_a1 == null || m_a2 == null || b_a1 == null || b_a2 == null;
							
							if( !nb ) {
								String ba1;
								if( b_a1.getCellType() == XSSFCell.CELL_TYPE_STRING ) ba1 = b_a1.getStringCellValue();
								else if( b_a1.getCellType() == XSSFCell.CELL_TYPE_NUMERIC ) ba1 = Double.toString( b_a1.getNumericCellValue() );
								else ba1 = "";
								
								String ba2;
								if( b_a2.getCellType() == XSSFCell.CELL_TYPE_STRING ) ba2 = b_a2.getStringCellValue();
								else if( b_a2.getCellType() == XSSFCell.CELL_TYPE_NUMERIC ) ba2 = Double.toString( b_a2.getNumericCellValue() );
								else ba2 = "";
								
								String ma1;
								if( m_a1.getCellType() == XSSFCell.CELL_TYPE_STRING ) ma1 = m_a1.getStringCellValue();
								else if( m_a1.getCellType() == XSSFCell.CELL_TYPE_NUMERIC ) ma1 = Double.toString( m_a1.getNumericCellValue() );
								else ma1 = "";

								String ma2;
								if( m_a2.getCellType() == XSSFCell.CELL_TYPE_STRING ) ma2 = m_a2.getStringCellValue();
								else if( m_a2.getCellType() == XSSFCell.CELL_TYPE_NUMERIC ) ma2 = Double.toString( m_a2.getNumericCellValue() );
								else ma2 = "";
								
								/*String ba1 = b_a1.getStringCellValue();
								String ba2 = b_a2.getStringCellValue();
								String ma1 = m_a1.getStringCellValue();
								String ma2 = m_a2.getStringCellValue();*/
							
								boolean mb = ma1.equals("") || ma2.equals("") || ba1.equals("") || ba2.equals("");
								boolean db = ma1.equals( ba1 ) || ma1.equals( ba2 ) || ma2.equals( ba1 ) || ma2.equals( ba2 );
								
								if( mb ) {
									mcount++;
								} else if( db ) {
									dcount++;
								}
							} else {
								mcount++;
							}
							
							c += 2;
						}
						if( mcount+dcount >= (cellcount-1)/2-villur ) {
							cell = m_row.getCell(0);
							
							if( cell != null ) {
								mlist.add(m_row);
							}
						}
					}
					m_row = m_sheet.getRow(mr++);
				}
				
				flist.clear();
				int fr = 1;
				XSSFRow 	f_row = f_sheet.getRow(fr++);
				while( f_row != null ) {
					cell = f_row.getCell(0);
					if( cell != null && (cell.getCellType() == Cell.CELL_TYPE_NUMERIC || cell.getStringCellValue().length() > 0) ) {
						int c = 1;
						
						int dcount = 0;
						int	mcount = 0;
						while( c < cellcount ) {
							XSSFCell	b_a1 = b_row.getCell(c);
							XSSFCell	b_a2 = b_row.getCell(c+1);
							XSSFCell	f_a1 = f_row.getCell(c);
							XSSFCell	f_a2 = f_row.getCell(c+1);
							
							boolean nb = f_a1 == null || f_a2 == null || b_a1 == null || b_a2 == null;
							
							if( !nb ) {
								String ba1;
								if( b_a1.getCellType() == XSSFCell.CELL_TYPE_STRING ) ba1 = b_a1.getStringCellValue();
								else if( b_a1.getCellType() == XSSFCell.CELL_TYPE_NUMERIC ) ba1 = Double.toString( b_a1.getNumericCellValue() );
								else ba1 = "";
								
								String ba2;
								if( b_a2.getCellType() == XSSFCell.CELL_TYPE_STRING ) ba2 = b_a2.getStringCellValue();
								else if( b_a2.getCellType() == XSSFCell.CELL_TYPE_NUMERIC ) ba2 = Double.toString( b_a2.getNumericCellValue() );
								else ba2 = "";
								
								String fa1;
								if( f_a1.getCellType() == XSSFCell.CELL_TYPE_STRING ) fa1 = f_a1.getStringCellValue();
								else if( f_a1.getCellType() == XSSFCell.CELL_TYPE_NUMERIC ) fa1 = Double.toString( f_a1.getNumericCellValue() );
								else fa1 = "";

								String fa2;
								if( f_a2.getCellType() == XSSFCell.CELL_TYPE_STRING ) fa2 = f_a2.getStringCellValue();
								else if( f_a2.getCellType() == XSSFCell.CELL_TYPE_NUMERIC ) fa2 = Double.toString( f_a2.getNumericCellValue() );
								else fa2 = "";
								
								/*String ba1 = b_a1.getStringCellValue();
								String ba2 = b_a2.getStringCellValue();
								String fa1 = f_a1.getStringCellValue();
								String fa2 = f_a2.getStringCellValue();*/
							
								boolean mb = fa1.equals("") || fa2.equals("") || ba1.equals("") || ba2.equals("");
								boolean db = fa1.equals( ba1 ) || fa1.equals( ba2 ) || fa2.equals( ba1 ) || fa2.equals( ba2 );
								
								if( mb ) {
									mcount++;
								} else if( db ) {
									dcount++;
								}
							} else {
								mcount++;
							}
							
							c += 2;
						}
						if( mcount+dcount >= (cellcount-1)/2-villur ) {
							cell = f_row.getCell(0);
							
							if( cell != null ) {
								flist.add(f_row);
							}
						}
						f_row = f_sheet.getRow(fr++);
					}
				}
				
				set.clear();
				for( XSSFRow mrow : mlist ) {
					for( XSSFRow frow : flist ) {
						int c = 1;
						int dcount = 0;
						int	mcount = 0;
						while( c < cellcount ) {
							XSSFCell	b_a1 = b_row.getCell(c);
							XSSFCell	b_a2 = b_row.getCell(c+1);
							XSSFCell	m_a1 = mrow.getCell(c);
							XSSFCell	m_a2 = mrow.getCell(c+1);
							XSSFCell	f_a1 = frow.getCell(c);
							XSSFCell	f_a2 = frow.getCell(c+1);
							
							String ba1 = cellVal( b_a1 );
							String ba2 = cellVal( b_a2 );
							String ma1 = cellVal( m_a1 );
							String ma2 = cellVal( m_a2 );
							String fa1 = cellVal( f_a1 );
							String fa2 = cellVal( f_a2 );
							
							/*String ba1 = b_a1 == null || b_a1.getStringCellValue().length() == 0 ? null : b_a1.getStringCellValue();
							String ba2 = b_a2 == null || b_a2.getStringCellValue().length() == 0 ? null : b_a2.getStringCellValue();
							String ma1 = m_a1 == null || m_a1.getStringCellValue().length() == 0 ? null : m_a1.getStringCellValue();
							String ma2 = m_a2 == null || m_a2.getStringCellValue().length() == 0 ? null : m_a2.getStringCellValue();
							String fa1 = f_a1 == null || f_a1.getStringCellValue().length() == 0 ? null : f_a1.getStringCellValue();
							String fa2 = f_a2 == null || f_a2.getStringCellValue().length() == 0 ? null : f_a2.getStringCellValue();*/
							
							boolean nb = ma1 == null || ma2 == null || fa1 == null || fa2 == null || ba1 == null || ba2 == null;
							if( nb ) {
								boolean mb = false;
								
								if( ba1 == null && ba2 == null ) {
									mb = true;
								} else if( ba1 == null ) {
									mb = fa1 == null || fa2 == null || ma1 == null || ma2 == null || fa1.equals(ba2) || fa2.equals(ba2) || ma1.equals(ba2) || ma2.equals(ba2);
								} else if( ba2 == null ) {
									mb = fa1 == null || fa2 == null || ma1 == null || ma2 == null || fa1.equals(ba1) || fa2.equals(ba1) || ma1.equals(ba1) || ma2.equals(ba1);
								} else {
									if( fa1 == null && fa2 == null ) {
										mb = ma1 == null || ma2 == null || ma1.equals(ba1) || ma2.equals(ba1) || ma1.equals(ba2) || ma2.equals(ba2);
									} else if( fa1 == null ) {
										mb = ma1 == null || ma2 == null || ma1.equals(ba1) || ma2.equals(ba1) || ma1.equals(ba2) || ma2.equals(ba2);
									} else if( fa2 == null ) {
										mb = ma1 == null || ma2 == null || ma1.equals(ba1) || ma2.equals(ba1) || ma1.equals(ba2) || ma2.equals(ba2);
									}
								}								
				
								if( mb ) {
									mcount++;
								}
							} else {
								boolean db = 	   (fa1.equals( ba1 ) && (ma1.equals( ba2 ) || ma2.equals( ba2 ))) 
												|| (fa1.equals( ba2 ) && (ma1.equals( ba1 ) || ma2.equals( ba1 )))
												|| (fa2.equals( ba1 ) && (ma1.equals( ba2 ) || ma2.equals( ba2 ))) 
												|| (fa2.equals( ba2 ) && (ma1.equals( ba1 ) || ma2.equals( ba1 )));
								
								if( db ) {
									dcount++;
								}
							}
							
							c += 2;
						}
						if( mcount+dcount >= (cellcount-1)/2-villur ) {
							XSSFCell mcell = mrow.getCell(0);
							XSSFCell fcell = frow.getCell(0);
							
							if( mcell != null && fcell != null ) {
								String mname = mcell.getCellType() == Cell.CELL_TYPE_NUMERIC ? Double.toString( mcell.getNumericCellValue() ) : mcell.getStringCellValue();
								String fname = fcell.getCellType() == Cell.CELL_TYPE_NUMERIC ? Double.toString( fcell.getNumericCellValue() ) : fcell.getStringCellValue();
								System.err.println( "\t"+mname+"\t"+fname );
								
								rcell = rrow.createCell( 1 );
								rcell.setCellValue( mname );
								rcell = rrow.createCell( 2 );
								rcell.setCellValue( fname );
								rrow = p_sheet.createRow(rr++);
								
								set.add( mrow );
								set.add( frow );
							}
						}
					}
				}
				
				flist.removeAll( set );
				mlist.removeAll( set );
				for( XSSFRow r : mlist ) {
					cell = r.getCell(0);
					
					if( cell != null ) {
						String name = cell.getCellType() == Cell.CELL_TYPE_NUMERIC ? Double.toString( cell.getNumericCellValue() ) : cell.getStringCellValue();
						System.err.println( "\t"+name );
						
						rcell = rrow.createCell( 1 );
						rcell.setCellValue( name );
						rrow = p_sheet.createRow(rr++);
					}
				}
					
				for( XSSFRow r : flist ) {
					cell = r.getCell(0);
					
					if( cell != null ) {
						String name = cell.getCellType() == Cell.CELL_TYPE_NUMERIC ? Double.toString( cell.getNumericCellValue() ) : cell.getStringCellValue();
						System.err.println( "\t\t"+name );
						
						rcell = rrow.createCell( 2 );
						rcell.setCellValue( name );
						rrow = p_sheet.createRow(rr++);
					}
				}
			}
			
			b_row = b_sheet.getRow(br++);
		}
		
		if( pbar != null ) {
			pbar.setValue( counter );
			pbar.setString( Integer.toString(counter) );
		}
		
		return workbook;
	}
	
	public void load( final String filename, final JProgressBar	pbar ) {
		new Thread() {
			public void run() {
				try {
					XSSFWorkbook workbook = subload( new FileInputStream( filename ), pbar );
					JFileChooser jfc = new JFileChooser();
					if( jfc.showSaveDialog( Parentage.this ) == JFileChooser.APPROVE_OPTION ) {
						File f = jfc.getSelectedFile();
						FileOutputStream fos = new FileOutputStream(f);
						workbook.write( fos );
						fos.close();
						//Desktop.getDesktop().open( f );
					} else {
						File f = File.createTempFile("tmp", ".xlsx");
						FileOutputStream fos = new FileOutputStream(f);
						workbook.write( fos );
						fos.close();
						Desktop.getDesktop().open( f );
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}.start();
	}
	
	/*int fr = 1;					
	XSSFRow 	f_row = f_sheet.getRow(fr++);
	while( f_row != null ) {
		c = 2;
		dcount = 0;
		mcount = 0;
		while( c < cellcount ) {
			XSSFCell	b_a1 = b_row.getCell(c);
			XSSFCell	b_a2 = b_row.getCell(c+1);
			XSSFCell	m_a1 = m_row.getCell(c-1);
			XSSFCell	m_a2 = m_row.getCell(c);
			XSSFCell	f_a1 = f_row.getCell(c-1);
			XSSFCell	f_a2 = f_row.getCell(c);
			
			boolean nb = f_a1 == null || f_a2 == null || b_a1 == null || b_a2 == null;
			
			if( !nb ) {
				String ba1 = b_a1.getStringCellValue();
				String ba2 = b_a2.getStringCellValue();
				String ma1 = m_a1.getStringCellValue();
				String ma2 = m_a2.getStringCellValue();
				String fa1 = f_a1.getStringCellValue();
				String fa2 = f_a2.getStringCellValue();
			
				boolean mb = ma1.equals("") || ma2.equals("") || ba1.equals("") || ba2.equals("");
				boolean db = ma1.equals( ba1 ) || ma1.equals( ba2 ) || ma2.equals( ba1 ) || ma2.equals( ba2 );
				
				if( mb ) {
					mcount++;
				} else if( db ) {
					dcount++;
				}
			} else {
				mcount++;
			}
			
			c += 2;
		}
		
		if( mcount+dcount == (cellcount-2)/2 ) {
			cell = f_row.getCell(0);
			
			if( cell != null ) {
				String fname = cell.getStringCellValue();
				System.err.println( "\t\t"+fname );
			}
		}
			
		f_row = f_sheet.getRow(fr++);
	}*/
	
	public static void main( String[] args ) {
		try {
			XSSFWorkbook workbook = Parentage.subload( System.in, null );
			workbook.write( System.out );
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
