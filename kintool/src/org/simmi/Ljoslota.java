package org.simmi;

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
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JApplet;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.TransferHandler;

import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Ljoslota extends JApplet {
	BufferedImage	xlsImg;
	JComponent		xlsComp;
	XSSFWorkbook	workbook;
	
	public Ljoslota() {
		super();
		
		try {
			xlsImg = ImageIO.read(this.getClass().getResource("/xlsx.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void init() {
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
		this.add( c );
		
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
						load( f.getCanonicalPath(), null );
						
						/*int i = 0;
						File nf = File.createTempFile("tmp", ".xlsx");
						/*File nf = new File( System.getProperty("user.home"), "tmp"+i+".xlsx" );
						while( !nf.canWrite() && i < 10 ) {
							i++;
							nf = new File( System.getProperty("user.home"), "tmp"+i+".xlsx" );
						}*
						workbook.write( new FileOutputStream( nf ) );
						Desktop.getDesktop().open( nf );*/
						
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
									//XSSFWorkbook	wb = new XSSFWorkbook();
									load( f.getCanonicalPath(), null );
									/*int i = 0;
									File nf = File.createTempFile("tmp", ".xlsx");
									//File nf = new File( System.getProperty("user.home"), "tmp"+i+".xlsx" );
									/*while( nf.exists() && !nf.canWrite() && i < 10 ) {
										i++;
										nf = new File( System.getProperty("user.home"), "tmp"+i+".xlsx" );
									}*
									workbook.write( new FileOutputStream( nf ) );*/
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
									load( f.getCanonicalPath(), null );
									/*JFileChooser fc = new JFileChooser( f.getParentFile() );
									if( fc.showSaveDialog( Ljoslota.this.xlsComp ) == JFileChooser.APPROVE_OPTION ) {
										workbook.write( new FileOutputStream( fc.getSelectedFile() ) );
									}*/
								}
							}
						}
					}
				} catch (UnsupportedFlavorException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
	
				return true;
			}
		};
		xlsComp.setTransferHandler(th);
	}
	
	public void load( String filename, XSSFWorkbook wb ) throws Exception {
		workbook = new XSSFWorkbook( filename );
		XSSFSheet		sheet = workbook.getSheet("Basic");
		
		
		XSSFCellStyle yellowStyle = workbook.createCellStyle();
		yellowStyle.setFillPattern( XSSFCellStyle.FINE_DOTS );
		yellowStyle.setFillForegroundColor( IndexedColors.YELLOW.getIndex() );
		
		XSSFCellStyle coralStyle = workbook.createCellStyle();
		coralStyle.setFillPattern( XSSFCellStyle.FINE_DOTS );
		coralStyle.setFillForegroundColor( IndexedColors.CORAL.getIndex() );
		
		
		int			cellcount = 0;
		int 		startrow = 0;
		
		XSSFRow 	row = sheet.getRow( startrow );
		while( row != null ) {
			XSSFCell 	cell = null;
			while( row != null ) {
				cell = row.getCell( 1 );
				//System.err.println( cell.getStringCellValue() );
				if( cell != null && cell.getCellType() == XSSFCell.CELL_TYPE_STRING && cell.getStringCellValue().length() > 0 ) {
					//System.err.println( cell.getStringCellValue() );
					//System.err.println( cell.getCellStyle().getFillForegroundXSSFColor().toString() );
					//System.err.println( cell.getCellComment() );
					//System.err.println( cell.getRawValue() );
					//System.err.println( cell.getRichStringCellValue() );
					
					//cell.getCellStyle().setfillbac
					
					//cell.getCellStyle().setFillPattern( XSSFCellStyle.FINE_DOTS );
					//cell.getCellStyle().setFillBackgroundColor( IndexedColors.CORAL.getIndex() );
					//cell.getCellStyle().setFillForegroundColor( IndexedColors.RED.getIndex() );
					break;
				}
				row = sheet.getRow( ++startrow );
			}
			
			if( true ) {//row.getCell(1) != null ) {
				System.err.println( "startrow " + startrow );
				
				Map<Short,Integer>	hist = new HashMap<Short,Integer>();
				Map<Short,Integer>	mm = new HashMap<Short,Integer>();
				for( int i = 8; i < 28; i+=2 ) {
					hist.clear();
					int r = startrow;
					row = sheet.getRow( r );
					while( row != null && row.getCell(1) != null && row.getCell(1).getCellType() == XSSFCell.CELL_TYPE_STRING && row.getCell(1).getStringCellValue().length() > 0 ) {
						XSSFCell cell1 = row.getCell( i );
						XSSFCell cell2 = row.getCell( i+1 );
						
						if( cell1 != null && cell1.getCellType() == XSSFCell.CELL_TYPE_NUMERIC && cell2 != null && cell2.getCellType() == XSSFCell.CELL_TYPE_NUMERIC ) {
							short 	s1 = (short)cell1.getNumericCellValue();
							short 	s2 = (short)cell2.getNumericCellValue();
							
							/*if( s1 == s2 && s1 != 0 ) {
								if( hist.containsKey(s1) ) {
									int val = hist.get( s1 );
									hist.put( s1, val+1 );
								} else {
									hist.put( s1, 1 );
								}
								hist.put( (short)-s1, Integer.MAX_VALUE );
							} else {*/
								if( s1 != (short)0 ) {
									if( hist.containsKey(s1) ) {
										int val = hist.get( s1 );
										hist.put( s1, val+1 );
									} else {
										hist.put( s1, 1 );
									}
								}
								
								if( s2 != (short)0 ) {
									if( hist.containsKey(s2) ) {
										int val = hist.get( s2 );
										hist.put( s2, val+1 );
									} else {
										hist.put( s2, 1 );
									}
								}
							//}
						} else {
							System.err.println( "outofbounds" );
						}
						
						row = sheet.getRow( ++r );
					}
					
					while( hist.size() > 4 ) {
						int min = Collections.min( hist.values() );
						
						short sval = -1;
						for( short s : hist.keySet() ) {
							if( hist.get(s) == min  ) {
								sval = s;
								break;
							}
						}
						
						if( sval >= 0 ) {
							hist.remove( sval );
						}
					}
					
					if( hist.size() > 0 ) {
					
						int max = Collections.max( hist.values() );	
						short sval = -1;
						for( short s : hist.keySet() ) {
							if( hist.get(s) == max  ) {
								sval = s;
								break;
							}
						}
						
						row = sheet.getRow( startrow-2 );
						XSSFCell p1cell1 = row.getCell( i );
						if( p1cell1 == null ) p1cell1 = row.createCell( i );
						XSSFCell p1cell2 = row.getCell( i+1 );
						if( p1cell2 == null ) p1cell2 = row.createCell( i+1 );
						
						row = sheet.getRow( startrow-1 );
						XSSFCell p2cell1 = row.getCell( i );
						if( p2cell1 == null ) p2cell1 = row.createCell( i );
						XSSFCell p2cell2 = row.getCell( i+1 );
						if( p2cell2 == null ) p2cell2 = row.createCell( i+1 );
						
						r = startrow;
						row = sheet.getRow( r );
						
						p1cell1.setCellStyle( yellowStyle );
						p1cell2.setCellStyle( yellowStyle );
						
						p2cell1.setCellStyle( yellowStyle );
						p2cell2.setCellStyle( yellowStyle );
						
						p1cell1.setCellValue( (double)sval );
						
						short[]	svals = new short[4];
						svals[2] = sval;
						
						mm.clear();
						while( row != null && row.getCell(1) != null && row.getCell(1).getCellType() == XSSFCell.CELL_TYPE_STRING && row.getCell(1).getStringCellValue().length() > 0 ) {
							XSSFCell cell1 = row.getCell( i );
							XSSFCell cell2 = row.getCell( i+1 );
							
							if( cell1 != null && cell1.getCellType() == XSSFCell.CELL_TYPE_NUMERIC && cell2 != null && cell2.getCellType() == XSSFCell.CELL_TYPE_NUMERIC ) {
								short 	s1 = (short)cell1.getNumericCellValue();
								short 	s2 = (short)cell2.getNumericCellValue();
								
								if( s1 == sval ) {
									if( mm.containsKey(s2) ) {
										int val = mm.get( s2 );
										mm.put( s2, val+1 );
									} else {
										mm.put( s2, 1 );
									}
								} else if( s2 == sval ) {
									if( mm.containsKey(s1) ) {
										int val = mm.get( s1 );
										mm.put( s1, val+1 );
									} else {
										mm.put( s1, 1 );
									}
								}
							}
							
							row = sheet.getRow( ++r );
						}
						
						while( mm.size() > 2 ) {
							int min = Collections.min( mm.values() );
							
							sval = -1;
							for( short s : mm.keySet() ) {
								if( mm.get(s) == min  ) {
									sval = s;
									break;
								}
							}
							
							if( sval >= 0 ) {
								mm.remove( sval );
							}
						}
						
						XSSFCell[] 	cells = { p2cell1, p2cell2, p1cell1, p1cell2 };
						
						if( mm.size() == 1 ) {							
							sval = -1;
							int mmax = 0;
							for( short s : hist.keySet() ) {
								int hval = hist.get(s);
								if( hval == max && s != svals[2]  ) {
									sval = s;
									break;
								} else if( hval < max && hval > mmax ) {
									sval = s;
									mmax = hval;
								}
							}
							
							for( short s : mm.keySet() ) {
								svals[0] = s;
								cells[0].setCellValue( (double)s );
							}
							
							svals[1] = sval;
							cells[1].setCellValue( (double)sval );
						} else {
							int 		k = 0;
							for( short s : mm.keySet() ) {
								svals[k] = s;
								cells[k].setCellValue( (double)s );
								k++;
							}
						}
						
						r = startrow;
						row = sheet.getRow( r );
						
						mm.clear();
						while( row != null && row.getCell(1) != null && row.getCell(1).getCellType() == XSSFCell.CELL_TYPE_STRING && row.getCell(1).getStringCellValue().length() > 0 ) {
							XSSFCell cell1 = row.getCell( i );
							XSSFCell cell2 = row.getCell( i+1 );
							
							if( cell1 != null && cell1.getCellType() == XSSFCell.CELL_TYPE_NUMERIC && cell2 != null && cell2.getCellType() == XSSFCell.CELL_TYPE_NUMERIC ) {
								short 	s1 = (short)cell1.getNumericCellValue();
								short 	s2 = (short)cell2.getNumericCellValue();
								
								if( s1 == svals[0] || s1 == svals[1] ) {
									//if( s2 != sval && s2 != svals[0] && s2 != svals[1] ) {
										if( mm.containsKey(s2) ) {
											int val = mm.get( s2 );
											mm.put( s2, val+1 );
										} else {
											mm.put( s2, 1 );
										}
									//}
								} 
								
								if( s2 == svals[0] || s2 == svals[1] ) {
									//if( s1 != sval && s1 != svals[0] && s1 != svals[1]  ) { 
										if( mm.containsKey(s1) ) {
											int val = mm.get( s1 );
											mm.put( s1, val+1 );
										} else {
											mm.put( s1, 1 );
										}	
									//}
								}
			 				}
							
							row = sheet.getRow( ++r );
						}
						
						/*if( svals[0] == 174 && svals[1] == 162 ) {
							System.err.println("simmi");
						}*/
						
						if( mm.size() == 0 ) {
							p1cell2.setCellValue( (double)sval );
							svals[3] = sval;
						} else {
							if( mm.size() > 2 ) {
								if( svals[0] == svals[2] ) mm.remove(svals[1]);
								else if( svals[1] == svals[2] ) mm.remove(svals[0]);
							}
							while( mm.size() > 2 ) {
								int min = Collections.min( mm.values() );
								
								sval = -1;
								for( short s : mm.keySet() ) {
									if( mm.get(s) == min  ) {
										sval = s;
										break;
									}
								}
								
								if( sval >= 0 ) {
									mm.remove( sval );
								}
							}
							
							if( mm.size() == 1 ) {
								for( short s : mm.keySet() ) {
									p1cell2.setCellValue( (double)s );
									svals[3] = s;
									break;
								}
							} else {
								int k = 2;
								for( short s : mm.keySet() ) {
									//p1cell2.setCellValue( (double)s );
									cells[k].setCellValue( (double)s );
									svals[k] = s;
									k++;
								}
							}
						}
						
						
						r = startrow;
						row = sheet.getRow( r );
							
						while( row != null && row.getCell(1) != null && row.getCell(1).getCellType() == XSSFCell.CELL_TYPE_STRING && row.getCell(1).getStringCellValue().length() > 0 ) {
							XSSFCell cell1 = row.getCell( i );
							XSSFCell cell2 = row.getCell( i+1 );
							
							if( cell1 != null && cell1.getCellType() == XSSFCell.CELL_TYPE_NUMERIC && cell2 != null && cell2.getCellType() == XSSFCell.CELL_TYPE_NUMERIC ) {
								short 	s1 = (short)cell1.getNumericCellValue();
								short 	s2 = (short)cell2.getNumericCellValue();
								
								if(  s1 == (short)0 || !hist.containsKey( s1 ) ) {
									cell1.setCellStyle( coralStyle );
								}
								
								if(  s2 == (short)0 || !hist.containsKey( s2 ) ) {					
									cell2.setCellStyle( coralStyle );
								}
								
								if( (s1 == svals[0] || s1 == svals[1]) && !(s1 == svals[2] || s1 == svals[3]) && !(s2 == svals[2] || s2 == svals[3]) ) {
									cell2.setCellStyle( coralStyle );
									
									if( r < 5 ) {
										System.err.println( svals[0] + "  "  + svals[1] + "  "  + svals[2] + "  "  + svals[3] );
									}
								}
								if( (s2 == svals[0] || s2 == svals[1]) && !(s2 == svals[2] || s2 == svals[3]) && !(s1 == svals[2] || s1 == svals[3]) ) {
									cell1.setCellStyle( coralStyle );
									
									if( r < 5 ) {
										System.err.println( s1 + "  "  + s2 );
										System.err.println( svals[0] + "  "  + svals[1] + "  "  + svals[2] + "  "  + svals[3] );
									}
								}
							}
							
							row = sheet.getRow( ++r );
						}
					} else {
						System.err.println( "hey " + r + "  " + (i-8) );
					}
				}
				
				if( row != null ) 
					startrow = row.getRowNum();
			}
		}
		
		/*while( cell != null && cell.getStringCellValue() != null && cell.getStringCellValue().length() > 0 ) {
			cell = row.getCell( ++startrow );
			cell.set
		}*/
		
		//while( row.getCell(cellcount) != null ) cellcount++;
		
		//markercount -= 2;
		
		/*Set<XSSFRow>	set = new HashSet<XSSFRow>();
		List<XSSFRow>	mlist = new ArrayList<XSSFRow>();
		List<XSSFRow>	flist = new ArrayList<XSSFRow>();
		
		int rr = 0;
		XSSFRow rrow = p_sheet.createRow(rr++);
		
		int br = 1;
		b_row = b_sheet.getRow(br++);		
		while( b_row != null ) {
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
				if( mcount+dcount == (cellcount-2)/2 ) {
					XSSFCell cell = m_row.getCell(0);
					
					if( cell != null ) {
						mlist.add(m_row);
					}
				}
				m_row = m_sheet.getRow(mr++);
			}
			
			flist.clear();
			int fr = 1;
			XSSFRow 	f_row = f_sheet.getRow(fr++);
			while( f_row != null ) {
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
				if( mcount+dcount == (cellcount-2)/2 ) {
					XSSFCell cell = f_row.getCell(0);
					
					if( cell != null ) {
						flist.add(f_row);
					}
				}
				f_row = f_sheet.getRow(fr++);
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
					if( mcount+dcount == (cellcount-2)/2 ) {
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
			
			b_row = b_sheet.getRow(br++);
		}*/
		
		//File f = File.createTempFile("tmp", ".xlsx");
		//FileOutputStream fos = new FileOutputStream(f);
		//workbook.write( fos );
		//Desktop.getDesktop().open( f );
		
		JFileChooser fc = new JFileChooser( System.getProperty("user.home") );
		if( fc.showSaveDialog( Ljoslota.this.xlsComp ) == JFileChooser.APPROVE_OPTION ) {
			File f = fc.getSelectedFile();
			FileOutputStream fos = new FileOutputStream( f );
			workbook.write( fos );
			fos.close();
			Desktop.getDesktop().open( f );
		}
	}	
	
	public static void main( String[] args ) {
		try {
			new Parentage().load( "/home/sigmar/Desktop/hestar.xlsx" );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
