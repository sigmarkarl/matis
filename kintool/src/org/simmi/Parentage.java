package org.simmi;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Graphics;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.JApplet;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Parentage extends JApplet {
	BufferedImage	xlsImg;
	JComponent		xlsComp;
	XSSFWorkbook	workbook;
	int				villur = 0;
	
	public Parentage() {
		super();
		
		try {
			xlsImg = ImageIO.read(this.getClass().getResource("/xlsx.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
		
		this.getContentPane().setBackground( Color.white );
		xlsComp = new JComponent() {
			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(xlsImg, 0, 0, this);
			}
		};
		
		JComponent c = new JComponent() {
			public void setBounds(int x, int y, int w, int h) {
				super.setBounds(x, y, w, h);
				xlsComp.setBounds( (this.getWidth() - xlsImg.getWidth() ) / 2, (this.getHeight() - xlsImg.getHeight()) / 2, xlsImg.getWidth(), xlsImg.getHeight());
			}
		};
		c.add( xlsComp );
		
		JComponent v = new JComponent() {};
		v.setLayout( new BorderLayout() );
		final JSpinner 	spinner = new JSpinner( new SpinnerNumberModel(0, 0, 3, 1));
		spinner.addChangeListener( new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				villur = (Integer)spinner.getValue();
			}
		});
		JLabel		label = new JLabel( "Villur: " );
		v.add( spinner );
		v.add( label, BorderLayout.WEST );
		this.setLayout( new BorderLayout() );
		this.add( c );
		this.add( v, BorderLayout.SOUTH );
		
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
						load( f.getCanonicalPath() );
						
						int i = 0;
						File nf = File.createTempFile("tmp", ".xlsx");
						/*File nf = new File( System.getProperty("user.home"), "tmp"+i+".xlsx" );
						while( !nf.canWrite() && i < 10 ) {
							i++;
							nf = new File( System.getProperty("user.home"), "tmp"+i+".xlsx" );
						}*/
						workbook.write( new FileOutputStream( nf ) );
						Desktop.getDesktop().open( nf );
						
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
									load( f.getCanonicalPath() );
									int i = 0;
									File nf = File.createTempFile("tmp", ".xlsx");
									//File nf = new File( System.getProperty("user.home"), "tmp"+i+".xlsx" );
									/*while( nf.exists() && !nf.canWrite() && i < 10 ) {
										i++;
										nf = new File( System.getProperty("user.home"), "tmp"+i+".xlsx" );
									}*/
									workbook.write( new FileOutputStream( nf ) );
								}
							}
						} else {
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
									load( f.getCanonicalPath() );
									JFileChooser fc = new JFileChooser( f.getParentFile() );
									if( fc.showSaveDialog( Parentage.this.xlsComp ) == JFileChooser.APPROVE_OPTION ) {
										workbook.write( new FileOutputStream( fc.getSelectedFile() ) );
									}
								}
							}
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
		xlsComp.setTransferHandler(th);
	}
	
	public void load( String filename ) throws IOException {
		workbook = new XSSFWorkbook( filename );
		XSSFSheet		m_sheet = workbook.getSheet("Mæður");
		XSSFSheet		f_sheet = workbook.getSheet("Feður");
		XSSFSheet		b_sheet = workbook.getSheet("Afkvæmi");
		XSSFSheet		p_sheet = workbook.createSheet("Foreldrar");
		
		int			cellcount = 0;
		XSSFRow 	b_row = b_sheet.getRow(0);
		while( b_row.getCell(cellcount) != null ) cellcount++;
		//markercount -= 2;
		System.err.println( cellcount );
		
		Set<XSSFRow>	set = new HashSet<XSSFRow>();
		List<XSSFRow>	mlist = new ArrayList<XSSFRow>();
		List<XSSFRow>	flist = new ArrayList<XSSFRow>();
		
		int rr = 0;
		XSSFRow rrow = p_sheet.createRow(rr++);
		
		int br = 1;
		b_row = b_sheet.getRow(br++);		
		while( b_row != null ) {
			if( b_row.getCell(0) != null && b_row.getCell(0).getStringCellValue().length() > 0 ) {
				String bname = b_row.getCell(0).getStringCellValue();
				System.err.println( bname );
				XSSFCell rcell = rrow.createCell( 0 );
				//rcell.setCellType( XSSFCell.CELL_TYPE_STRING );
				rcell.setCellValue( bname );
				rrow = p_sheet.createRow(rr++);
				
				mlist.clear();
				int mr = 1;
				XSSFRow 	m_row = m_sheet.getRow(mr++);
				while( m_row != null ) {
					if( m_row.getCell(0) != null && m_row.getCell(0).getStringCellValue().length() > 0 ) {
						int c = 2;
						
						int dcount = 0;
						int	mcount = 0;
						while( c < cellcount ) {
							XSSFCell	b_a1 = b_row.getCell(c);
							XSSFCell	b_a2 = b_row.getCell(c+1);
							XSSFCell	m_a1 = m_row.getCell(c-1);
							XSSFCell	m_a2 = m_row.getCell(c);
							
							boolean nb = m_a1 == null || m_a2 == null || b_a1 == null || b_a2 == null;
							
							if( !nb ) {
								String ba1 = b_a1.getStringCellValue();
								String ba2 = b_a2.getStringCellValue();
								String ma1 = m_a1.getStringCellValue();
								String ma2 = m_a2.getStringCellValue();
							
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
						if( mcount+dcount >= (cellcount-2)/2-villur ) {
							XSSFCell cell = m_row.getCell(0);
							
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
					if( f_row.getCell(0) != null && f_row.getCell(0).getStringCellValue().length() > 0 ) {
						int c = 2;
						
						int dcount = 0;
						int	mcount = 0;
						while( c < cellcount ) {
							XSSFCell	b_a1 = b_row.getCell(c);
							XSSFCell	b_a2 = b_row.getCell(c+1);
							XSSFCell	f_a1 = f_row.getCell(c-1);
							XSSFCell	f_a2 = f_row.getCell(c);
							
							boolean nb = f_a1 == null || f_a2 == null || b_a1 == null || b_a2 == null;
							
							if( !nb ) {
								String ba1 = b_a1.getStringCellValue();
								String ba2 = b_a2.getStringCellValue();
								String fa1 = f_a1.getStringCellValue();
								String fa2 = f_a2.getStringCellValue();
							
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
						if( mcount+dcount >= (cellcount-2)/2-villur ) {
							XSSFCell cell = f_row.getCell(0);
							
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
						int c = 2;
						int dcount = 0;
						int	mcount = 0;
						while( c < cellcount ) {
							XSSFCell	b_a1 = b_row.getCell(c);
							XSSFCell	b_a2 = b_row.getCell(c+1);
							XSSFCell	m_a1 = mrow.getCell(c-1);
							XSSFCell	m_a2 = mrow.getCell(c);
							XSSFCell	f_a1 = frow.getCell(c-1);
							XSSFCell	f_a2 = frow.getCell(c);
							
							String ba1 = b_a1 == null || b_a1.getStringCellValue().length() == 0 ? null : b_a1.getStringCellValue();
							String ba2 = b_a2 == null || b_a2.getStringCellValue().length() == 0 ? null : b_a2.getStringCellValue();
							String ma1 = m_a1 == null || m_a1.getStringCellValue().length() == 0 ? null : m_a1.getStringCellValue();
							String ma2 = m_a2 == null || m_a2.getStringCellValue().length() == 0 ? null : m_a2.getStringCellValue();
							String fa1 = f_a1 == null || f_a1.getStringCellValue().length() == 0 ? null : f_a1.getStringCellValue();
							String fa2 = f_a2 == null || f_a2.getStringCellValue().length() == 0 ? null : f_a2.getStringCellValue();
							
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
												|| (fa1.equals( ba2 ) && ma1.equals( ba1 ) || ma2.equals( ba1 ))
												|| (fa2.equals( ba1 ) && (ma1.equals( ba2 ) || ma2.equals( ba2 ))) 
												|| (fa2.equals( ba2 ) && ma1.equals( ba1 ) || ma2.equals( ba1 ));
								
								if( db ) {
									dcount++;
								}
							}
							
							c += 2;
						}
						if( mcount+dcount >= (cellcount-2)/2-villur ) {
							XSSFCell mcell = mrow.getCell(0);
							XSSFCell fcell = frow.getCell(0);
							
							if( mcell != null && fcell != null ) {
								String mname = mcell.getStringCellValue();
								String fname = fcell.getStringCellValue();
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
					XSSFCell cell = r.getCell(0);
					
					if( cell != null ) {
						String name = cell.getStringCellValue();
						System.err.println( "\t"+name );
						
						rcell = rrow.createCell( 1 );
						rcell.setCellValue( name );
						rrow = p_sheet.createRow(rr++);
					}
				}
					
				for( XSSFRow r : flist ) {
					XSSFCell cell = r.getCell(0);
					
					if( cell != null ) {
						String name = cell.getStringCellValue();
						System.err.println( "\t\t"+name );
						
						rcell = rrow.createCell( 2 );
						rcell.setCellValue( name );
						rrow = p_sheet.createRow(rr++);
					}
				}
			}
			
			b_row = b_sheet.getRow(br++);
		}
		
		JFileChooser jfc = new JFileChooser();
		if( jfc.showSaveDialog( this ) == JFileChooser.APPROVE_OPTION ) {
			File f = jfc.getSelectedFile();
			FileOutputStream fos = new FileOutputStream(f);
			workbook.write( fos );
			fos.close();
			
			Desktop.getDesktop().open( f );
		} else {
			File f = File.createTempFile("tmp", ".xlsx");
			FileOutputStream fos = new FileOutputStream(f);
			workbook.write( fos );
			fos.close();
		
			Desktop.getDesktop().open( f );
		}
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
			new Parentage().load( "/home/sigmar/Desktop/hestar.xlsx" );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
