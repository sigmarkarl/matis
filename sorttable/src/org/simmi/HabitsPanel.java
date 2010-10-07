package org.simmi;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.EventObject;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TooManyListenersException;

import javax.management.timer.Timer;
import javax.swing.AbstractAction;
import javax.swing.AbstractCellEditor;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.JViewport;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import org.jdesktop.swingx.JXDatePicker;
import org.simmi.RecipePanel.Recipe;

public class HabitsPanel extends JComponent {
	int 							min = 0;
	final Color 					paleGreen = new Color( 20,230,60,96 ); 
	Map<String,Week>				eatList = new HashMap<String,Week>();
	//JSplitPane						splitpane;
	JToolBar						toolbar;
	JXDatePicker					datepicker;
	JLabel							englabel;
	Calendar						cal;
	LinkedSplitPane					lsplitPane;
	JSplitPane						tsplitPane;
	JComboBox						malCombo;
	List<JComboBox>					combList;
	
	Week							currentWeekObj;
	int								currentWeek;
	int								currentYear;
	
	JTable							timelineDataTable;
	JTable							colHeaderTable;
	boolean							sel = false;
	
	String 							lang;
	Set<String>						allskmt;
	Map<String,Map<String,String>>	skmt;
	
	class Week {
		String[]	d = new String[8];
		
		public Week() {
			for( int i = 0; i < d.length; i++ ) {
				d[i] = "";
			}
		}
		
		public int getMin() {
			int ret = 0;
			for( String s : d ) {
				ret = Math.max( s.length() == 0 ? 0 : s.split("\t").length, ret ); 
			}
			
			return ret;
		}
	};
	
	public long getCurrentTime() {
		Date date = datepicker != null ? datepicker.getDate() : null;
		long time = System.currentTimeMillis();
		if( date != null ) time = date.getTime();
		
		return time;
	}
	
	public String getCurrentCardName() {
		long time = getCurrentTime();
		cal.setTimeInMillis( time );
		int woy = cal.get( Calendar.WEEK_OF_YEAR );
		int yer = cal.get( Calendar.YEAR );
		return woy+"_"+yer;
	}
	
	class RenderComponent extends JComponent {
		JLabel		label = new JLabel();
		JTextField	field = new JTextField();
		
		int size = 250;
		JComboBox	combo = new JComboBox() {
			private boolean layingOut = false;

			public void doLayout() {
				try {
					layingOut = true;
					super.doLayout();
				} finally {
					layingOut = false;
				}
			}

			public Dimension getSize() {
				Dimension sz = super.getSize();
				if (!layingOut) {
					sz.width = Math.max(sz.width, size);
				}
				return sz;
			}
		};
		boolean		selected = false;
		boolean		grayed = false;
		Color		selbg = Color.blue;
		Color		gray = new Color(240,240,240);
		//Dimension d = new Dimension(100,50);
		
		public RenderComponent() {
			super();
			this.setName( "asni" );
			this.setLayout( null );
			
			field.setHorizontalAlignment( JTextField.RIGHT );
			combo.addItem("g");
			
			//System.err.println(c);
			//this.setPreferredSize( d );
			//this.setSize( d );
			this.add( label );
			this.add( field );
			this.add( combo );
			
			setBounds( 0,0,200,100 );
		}
		
		public void addComboItems( Collection<String> items ) {
			combo.removeAllItems();
			combo.addItem("g");
			if( items != null ) {
				for( String item : items ) {
					combo.addItem( item );
				}
			}
		}
		
		public void paintComponent( Graphics g ) {
			super.paintComponent( g );
			if( selected ) {
				g.setColor( selbg );
				g.fillRect(0, 0, this.getWidth(), this.getHeight());
			} else if( grayed ) {
				g.setColor( gray );
				g.fillRect(0, 0, this.getWidth(), this.getHeight());
				//g.setColor( Color.red );
				//g.fillRect(0, 0, label.getWidth(), label.getHeight());
			}
			//size = Math.max(20, this.getPreferredSize().width);
		}
		
		public void setBounds( int x, int y, int w, int h ) {
			super.setBounds(x, y, w, h);
			label.setBounds(0, 0, w, h/2);
			field.setBounds(0, h/2, w/3, h/2);
			combo.setBounds(w/3, h/2, (2*w)/3, h/2);
			
			//label.setBackground( Color.cyan );
		}
	};
	
	class MyEditor extends AbstractCellEditor implements TableCellEditor {
		RenderComponent	rc = new RenderComponent();
		int r, c;
		Map<String,Map<String,String>>	skmt;
		
		public MyEditor( Map<String,Map<String,String>> skmt ) {
			this.skmt = skmt;
			rc.combo.addItemListener( new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					Object selitem = rc.combo.getSelectedItem();
					if( selitem != null ) {
						if( rc.combo.getSelectedItem().equals("g") ) {
							rc.field.setText("100");
						} else {
							rc.field.setText("1");
						}
					}
				}
			});
		}
		
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
			r = row;
			c = column;
			
			rc.selected = isSelected;
			rc.selbg = table.getSelectionBackground();
			if( isSelected ) {
				rc.label.setForeground( Color.white );
			} else {
				rc.label.setForeground( Color.black );
			}
			
			if( value != null ) {
				String[] split = ((String)value).split("\\|");
				
				Map<String,String>	val = RecipePanel.getUnitVal(split[0], skmt);
				if( val != null ) {
					Set<String>	allskmt = new HashSet<String>();
					for( String key : val.keySet() ) {
						allskmt.add( key + " (" + val.get(key) + ")" );
					}
					rc.addComboItems( allskmt );
				} else {
					rc.addComboItems( null );
				}
				
				rc.label.setText( split[0] );
				if( split.length > 1 ) {
					rc.field.setText( split[1] );
					rc.combo.setSelectedItem( split[2] );
				} else {
					rc.field.setText("");
					rc.combo.setSelectedItem("");	
				}
			} else {
				rc.label.setText("");
				rc.field.setText("");
				rc.combo.setSelectedItem("");
			}
			return rc;
		}

		/*@Override
		public void addCellEditorListener(CellEditorListener l) {}

		@Override
		public void cancelCellEditing() {}*/

		public Object getCellEditorValue() {
			return rc.label.getText() + "|" + rc.field.getText() + "|" + rc.combo.getSelectedItem();
		}

		@Override
		public boolean isCellEditable(EventObject anEvent) {
			if (anEvent instanceof MouseEvent) { 
				return ((MouseEvent)anEvent).getClickCount() >= 2;
			}
			return true;
		}

		/*@Override
		public void removeCellEditorListener(CellEditorListener l) {}*/

		public boolean shouldSelectCell(EventObject anEvent) {
			return true;
		}

		/*@Override
		public boolean stopCellEditing() {
			timelineDataModel.setValueAt( getCellEditorValue(), r, c );
			
			return true;
		}*/
	};
	
	public float getSelWeight( Map<String,Integer> foodInd ) {
		float w = 0.0f;
		for( int c : timelineDataTable.getSelectedColumns() ) {
			for( int r : timelineDataTable.getSelectedRows() ) {
				Object val = timelineDataTable.getValueAt(r, c);
				
				if( val != null ) {
					String[] split = ((String)val).split("\\|");
					if( split.length > 2 ) {
						Integer ii = foodInd.get( split[0] );
						
						String measure = split[1];
						String unit = split[2];
						
						float d = Float.parseFloat( measure );
						if( !unit.equals("g") ) {
							String ru = unit;
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
					
					/*int i;
					if( ii == null ) {
						i = stuff.size()-2;
						for( Recipe rep : recipes ) {
							if( split[0].equals( rep.name + " - " + rep.author ) ) break;
							i++;
						}
					} else {
						i = ii;
					}
					
					if( i < stuff.size()+recipes.size()-2 ) {
						Float fval = (Float)DetailPanel.getVal(i, 0, stuff, foodInd, recipes, false);
						if( fval != null ) {
							total += updateEng( fval, split );
						}
						
						Float fvalc = (Float)DetailPanel.getVal(i, 1, stuff, foodInd, recipes, false);
						if( fvalc != null ) {
							totalc += updateEng( fvalc, split );
						}
					}*/
				}
			}
		}
		return w;
	}
	
	public double updateEng( Float fval, String[] split ) {
		double d = (double)fval;
		String ru = split[2];
		float  me = 0.0f;
		
		try {
			me = Float.parseFloat( split[1] );
		} catch( Exception ep ) {
			
		}
		
		int f = ru.indexOf("(");
		int n = ru.indexOf(")");
		if (n > f && f != -1) {
			String subbi = ru.substring(f + 1, n).trim();
			if( subbi.endsWith("g") ) {
				subbi = subbi.substring(0, subbi.length() - 1);
			}

			float fl = 0.0f;
			try {
				fl = Float.parseFloat(subbi);
			} catch (Exception ep) {

			}
			d *= (me*fl)/100.0;
		} else {
			d *= me/100.0;
		}
		//float f = (val * d);
		
		return d;
		
		/*Float fvalc = (Float)DetailPanel.getVal(i, 1, stuff, foodInd, recipes, false);
		if( fvalc != null ) {
			double d = (double)fvalc;
			String ru = split[2];
			float  me = 0.0f;
			
			try {
				me = Float.parseFloat( split[1] );
			} catch( Exception ep ) {
				
			}
			
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
				} catch (Exception ep) {

				}
				d *= (me*fl)/100.0f;
			}
			//float f = (val * d);
			
			totalc += d;
		}*/
	}
	
	public String getSelection() {
		String ret = "";
		
		for( int c : timelineDataTable.getSelectedColumns() ) {
			for( int r : timelineDataTable.getSelectedRows() ) {
				Object val = timelineDataTable.getValueAt(r, c);
				
				if( val != null && val.toString().length() > 0 ) {
					ret += (String)val + "\n";
				}
			}
		}
		
		return ret;
	}
	
	public void updateEngLabel( List<Object[]> stuff, Map<String,Integer> foodInd, List<Recipe> recipes ) {
		double total = 0.0f;
		double totalc = 0.0f;
		for( int c : timelineDataTable.getSelectedColumns() ) {
			for( int r : timelineDataTable.getSelectedRows() ) {
				Object val = timelineDataTable.getValueAt(r, c);
				
				if( val != null ) {
					String[] split = ((String)val).split("\\|");
					Integer ii = foodInd.get( split[0] );
					
					int i;
					if( ii == null ) {
						i = stuff.size()-2;
						for( Recipe rep : recipes ) {
							if( split[0].equals( rep.name + " - " + rep.author ) ) break;
							i++;
						}
					} else {
						i = ii;
					}
					
					if( i < stuff.size()+recipes.size()-2 ) {
						Float fval = (Float)DetailPanel.getVal(i, 0, stuff, foodInd, recipes, false);
						if( fval != null ) {
							total += updateEng( fval, split );
						}
						
						Float fvalc = (Float)DetailPanel.getVal(i, 1, stuff, foodInd, recipes, false);
						if( fvalc != null ) {
							totalc += updateEng( fvalc, split );
						}
					}
				}
			}
		}
		englabel.setText( "Orka í vali: " + Math.round( total*10.0 )/10.0 + " kJ / " + Math.round( totalc*10.0 )/10.0 + " kcal" );
	}
	
	private String currentWeekToText() throws IOException {
		ByteArrayOutputStream 	baos = new ByteArrayOutputStream();
		OutputStreamWriter		osw = new OutputStreamWriter( baos );
		
		cardWrite( osw );
		
		osw.flush();
		baos.flush();
		String ret = baos.toString();
		//System.err.println( ret );
		
		osw.close();
		baos.close();
		return ret;
	}
	
	private void sendToFriend( String yourId, String[] ids ) throws IOException {
		//String wtext = currentWeekToText();
		if( ids.length > 0 ) {
			try {					
				URL url = new URL( "http://test.matis.is/isgem/week.php" );
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
				write += URLEncoder.encode(data, "UTF8")+"&date=";
				data = getCurrentCardName();
				write += URLEncoder.encode(data, "UTF8")+"&week=";
				data = currentWeekToText();
				write += URLEncoder.encode(data, "UTF8");
				
				/*for( Recipe r : repSet ) {
					String rstr = r.toString();
					String ival = Integer.toString( Math.abs( rstr.hashCode()) );
					
					write += "&"+ival+"=";
					write += URLEncoder.encode(rstr, "UTF8");	
				}*/
				
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
				JOptionPane.showMessageDialog(HabitsPanel.this, "Vinir hafa fengið vikukort");
				//} else JOptionPane.showMessageDialog(RecipePanel.this, "Engar uppskriftir valdar");
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else 	JOptionPane.showMessageDialog(HabitsPanel.this, "Engir vinir valdir");
	}
	
	char[]	cbuf = new char[2048];
	public void checkMail( String currentUserId ) throws IOException {
		boolean allowed = true;
		try {
			System.getSecurityManager().checkConnect("test.matis.is", 80);
		} catch( Exception e ) {
			allowed = false;
		}
		
		if( allowed ) {
			URL url = new URL( "http://test.matis.is/isgem/getw.php" );
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");
			
			Integer.toString( Math.abs( this.toString().hashCode()) );
			String write = "user="+currentUserId;
			
			connection.getOutputStream().write( write.getBytes() );
			connection.getOutputStream().flush();
			connection.getOutputStream().close();
			
			byte[] bb = new byte[8192];
			connection.getInputStream().read(bb);
			
			String s = new String( bb );
			final String[] ss = s.split("\n");
			
			//JOptionPane	opt = new JOptionPane(ss.length + " uppskriftir í pósthólfi. Viltu taka við þeim?", JOptionPane.YES_NO_CANCEL_OPTION);
			
			if( ss.length > 1 ) {
			String message = (ss.length-1) + " vikur í pósthólfi. Viltu taka við þeim?";
				int val =  JOptionPane.showConfirmDialog(this, message, "Vikur frá vinum", JOptionPane.YES_NO_CANCEL_OPTION);
				if( val != JOptionPane.CANCEL_OPTION ) {
					for( int i = 0; i < ss.length-1; i++ ) {
						String str = ss[i];
						String splt = str.split("\t")[0];
						
						url = new URL( "http://test.matis.is/isgem/getww.php" );
						connection = (HttpURLConnection)url.openConnection();
						connection.setDoInput(true);
						connection.setDoOutput(true);
						connection.setRequestMethod("POST");
						
						Integer.toString( Math.abs( this.toString().hashCode()) );
						write = "week="+splt;
						
						connection.getOutputStream().write( write.getBytes() );
						connection.getOutputStream().flush();
						connection.getOutputStream().close();
						
						if( val == JOptionPane.YES_OPTION ) {
							//insertRecipe( new InputStreamReader( connection.getInputStream() ) );
							Reader rd = new InputStreamReader( connection.getInputStream() );
							int r = rd.read(cbuf);
							saveString( splt, new String( cbuf,0 ,r ) );
							eatList.remove( splt );
							if( splt.equals( getCurrentCardName() ) ) load();
						}
					}
				}
			}
		}
	}
	
	public HabitsPanel( final String lang, final FriendsPanel fp, final List<Object[]>	stuff, final List<Recipe>	recipes, final Map<String,Integer>	foodInd, Set<String> allskmt, Map<String,Map<String,String>> skmt ) {
		super();
		
		this.lang = lang;
		this.allskmt = allskmt;
		this.skmt = skmt;
		
		this.addComponentListener( new ComponentListener() {
			public void componentShown(ComponentEvent e) {
				try {
					checkMail( fp.currentUserId );
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			public void componentResized(ComponentEvent e) {}
			public void componentMoved(ComponentEvent e) {}
			public void componentHidden(ComponentEvent e) {}
		});
		
		final Dimension prefSize = new Dimension(100,25);
		malCombo = new JComboBox();
		malCombo.setPreferredSize( prefSize );
		malCombo.setMinimumSize( prefSize );
		if( lang.equals("IS") ) {
			malCombo.addItem("Morgunmatur");
			malCombo.addItem("Millimáltíð1");
			malCombo.addItem("Hádegismatur");
			malCombo.addItem("Millimáltíð2");
			malCombo.addItem("Kvöldmatur");
			malCombo.addItem("Millimáltíð3");
		} else {
			malCombo.addItem("Breakfast");
			malCombo.addItem("Between 1");
			malCombo.addItem("Lunch");
			malCombo.addItem("Between 2");
			malCombo.addItem("Dinnter");
			malCombo.addItem("Between 3");
		}
		
		try {
			datepicker = new JXDatePicker();
		} catch( Exception e ) {
			e.printStackTrace();
		}
		englabel = new JLabel( lang.equals("IS") ? "Orka í vali: " : "Energy in selection" );
		
		Dimension d = new Dimension( 300, 25 );
		englabel.setPreferredSize( d );
		englabel.setSize( d );
		/*DateSelectionModel mod = new DateSelectionModel() {
			
		};
		//datepicker.setMonthView(new JXM)*/
		
		JButton	sendfriend = new JButton( new AbstractAction(lang.equals("IS") ? "Senda völdum vinum" : "Send to selected friends") {
			public void actionPerformed(ActionEvent e) {
				try {
					sendToFriend( fp.currentUserId, fp.getSelectedFriendsIds() );
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}			
		});
		
		this.setLayout( new BorderLayout() );
		toolbar = new JToolBar();
		if( datepicker != null ) toolbar.add( datepicker );
		toolbar.add( englabel );
		toolbar.add( sendfriend );
		
		this.add( toolbar, BorderLayout.NORTH );
		
		//splitpane = new JSplitPane( JSplitPane.VERTICAL_SPLIT );
		//this.add( splitpane );
		
		//JTabbedPane timelineTabPane = new JTabbedPane( JTabbedPane.RIGHT );
		final JTable		timelineTable = new JTable() {
			public void setRowSelectionInterval(int r1, int r2) {
				sel = true;
				super.setRowSelectionInterval(r1, r2);
			}

			public void setColumnSelectionInterval(int c1, int c2) {
				sel = true;
				super.setColumnSelectionInterval(c1, c2);
			}

			public void addRowSelectionInterval(int r1, int r2) {
				sel = true;
				super.addRowSelectionInterval(r1, r2);
			}

			public void addColumnSelectionInterval(int c1, int c2) {
				sel = true;
				super.addColumnSelectionInterval(c1, c2);
			}

			public Component prepareRenderer( TableCellRenderer renderer, int row, int column ) {
				Component c = super.prepareRenderer( renderer, row, column );
				//c.setBackground( Color.green );
				//Color bc = c.getBackground();
				/*Object val = this.getValueAt(0, column);
				if( val.equals("Sunnudagur") || val.equals("Laugardagur") ) {
					c.setBackground( paleGreen );
				} else if( this.getSelectedRow() != row ) {
					c.setBackground( Color.white );
				} else {
					c.setBackground( this.getSelectionBackground() );
				}*/
				return c;
			}
		};
		
		timelineDataTable = new JTable() {
			public void setRowSelectionInterval(int r1, int r2) {
				sel = false;
				super.setRowSelectionInterval(r1, r2);
			}

			public void setColumnSelectionInterval(int c1, int c2) {
				sel = false;
				super.setColumnSelectionInterval(c1, c2);
			}

			public void addRowSelectionInterval(int r1, int r2) {
				sel = false;
				super.addRowSelectionInterval(r1, r2);
			}

			public void addColumnSelectionInterval(int c1, int c2) {
				sel = false;
				super.addColumnSelectionInterval(c1, c2);
			}
			
			@Override
			public Component prepareRenderer( TableCellRenderer renderer, int row, int column ) {
				Component c = super.prepareRenderer( renderer, row, column );
				
				/*if( c instanceof RenderComponent ) {
					RenderComponent rc = (RenderComponent)c;
					c.setBackground( Color.green );
					System.err.println("hello " + c.getSize() + c.isShowing() + c.isVisible() + c.getName() + " uu " + rc.label.getText());
				}*/
				//Color bc = c.getBackground();
				/*Object val = timelineTable.getValueAt(0, column);
				if( val.equals("Sat") || val.equals("Sun") ) {
					c.setBackground( paleGreen );
				} else if( this.getSelectedRow() != row ) {
					c.setBackground( Color.white );
				} else {
					c.setBackground( this.getSelectionBackground() );
				}*/
				return c;
			}
		};
		timelineDataTable.setBackground( Color.white );
		timelineDataTable.setRowSelectionAllowed( true );
		timelineDataTable.setColumnSelectionAllowed( true );
		final JScrollPane timelineDataScroll = new JScrollPane( timelineDataTable ) {
			public void paint( Graphics g ) {
				super.paint( g );
				
				if( timelineDataTable.getRowCount() == 0 ) {
					Graphics2D g2 = (Graphics2D)g;
					g2.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
					
					String str = lang.equals("IS") ? "Dragðu fæðutegundir úr tölfunni" : "Drag food from the table";
					String nstr = lang.equals("IS") ? "til vinstri hingað" : "to the left to here";
					int strw = g.getFontMetrics().stringWidth( str );
					int nstrw = g.getFontMetrics().stringWidth( nstr );
					g.setColor( Color.lightGray );
					g.drawString(str, (this.getWidth()-strw)/2, this.getHeight()/2-5);
					g.drawString(nstr, (this.getWidth()-nstrw)/2, this.getHeight()/2+10);
				}
			}
		};
		timelineDataScroll.setBackground( Color.white );
		timelineDataScroll.getViewport().setBackground( Color.white );
		
		timelineDataTable.getColumnModel().addColumnModelListener( new TableColumnModelListener() {
			public void columnAdded(TableColumnModelEvent e) {}

			public void columnMarginChanged(ChangeEvent e) {
				Enumeration<TableColumn> 	tcs = timelineDataTable.getColumnModel().getColumns();
				int i = 0;
				while( tcs.hasMoreElements() ) {
					TableColumn tc = tcs.nextElement();
					timelineTable.getColumnModel().getColumn(i++).setPreferredWidth(tc.getPreferredWidth());
				}
			}

			public void columnMoved(TableColumnModelEvent e) {
				timelineTable.moveColumn( e.getFromIndex(), e.getToIndex() );		
			}

			public void columnRemoved(TableColumnModelEvent e) {}

			public void columnSelectionChanged(ListSelectionEvent e) {}
		});
		
		timelineDataTable.addKeyListener( new KeyListener() {
			public void keyTyped(KeyEvent e) {
				
			}
			
			public void keyReleased(KeyEvent e) {
				
			}
			
			public void keyPressed(KeyEvent e) {
				if( e.getKeyCode() == KeyEvent.VK_DELETE ) {
					String tstr = getCurrentCardName();
					Week list = eatList.get( tstr );
					if( list == null ) {
						list = new Week();
						eatList.put( tstr, list );
						setCurrentWeek( list );
					}
					
					int[] cc = timelineDataTable.getSelectedColumns();
					int[] rr = timelineDataTable.getSelectedRows();
					for( int c : cc ) {
						String s = list.d[c];
						String[] ss = s.split("\t");
						for( int r : rr ) {
							if( r < ss.length ) ss[r] = "";
						}
						
						s = "";
						boolean first = true;
						for( String str : ss ) {
							if( first ) s += str;  
							else s += "\t"+str;
							
							first = false;
						}
						
						list.d[c] = s;
					}
					timelineDataTable.tableChanged( new TableModelEvent( timelineDataTable.getModel() ) );
					try {
						save( tstr );
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		
		JPopupMenu	popup = new JPopupMenu();
		popup.add( new AbstractAction("Eyða röð") {
			public void actionPerformed(ActionEvent e) {
				String tstr = getCurrentCardName();
				Week list = eatList.get( tstr );
				if( list == null ) {
					list = new Week();
					setCurrentWeek( list );
					eatList.put( tstr, list );
				}
				
				int[] rr = timelineDataTable.getSelectedRows();
				Set<Integer>	iset = new HashSet<Integer>();
				for( int i : rr ) {
					iset.add( i );
				}
				for( int i = 0; i < list.d.length; i++ ) {
					String s = list.d[i];
					String[] split = s.split("\t");
					System.err.println( split.length );
					String res = "";
					int k = 0;
					for( String str : split ) {
						if( !iset.contains(k) ) {
							if( res.length() == 0 ) res += str;
							else res += "\t"+str; 
						}
						k++;
					}
					list.d[i] = res;
				}
				
				updateMin( list );
				timelineDataTable.tableChanged( new TableModelEvent( timelineDataTable.getModel() ) );
				colHeaderTable.tableChanged( new TableModelEvent( colHeaderTable.getModel() ) );
				try {
					save( tstr );
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		timelineDataTable.setComponentPopupMenu(popup);
		
		JComponent topComp = new JComponent() {};
		topComp.setLayout( new BorderLayout() );
		
		//topTable.setShowGrid( true );
		//topTable.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );
		//table.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );
		//scrollPane.setViewportView( table );
		
		//table.getTableHeader().setVisible( false );
		//table.setTableHeader( null );
		
		/*scrollPane.setVerticalScrollBarPolicy( ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS );
		scrollPane.setHorizontalScrollBarPolicy( ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS );
		scrollPane.setRowHeaderView( leftTable );
		leftScrollPane.setViewport( scrollPane.getRowHeader() );
		leftScrollPane.setVerticalScrollBarPolicy( ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER );
		leftScrollPane.setHorizontalScrollBarPolicy( ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS );*/
		
		topComp.add( timelineTable );
		topComp.add( timelineDataTable.getTableHeader(), BorderLayout.SOUTH );
		
		JViewport	spec = new JViewport() {
			public void setView( Component view ) {
				if( !(view instanceof JTableHeader) ) super.setView( view );
			}
		};
		spec.setView( topComp );
		
		timelineDataScroll.setVerticalScrollBarPolicy( ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED );
		timelineDataScroll.setHorizontalScrollBarPolicy( ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER );
		
		timelineDataScroll.setColumnHeader( spec );
		
		JScrollPane timelineScroll = new JScrollPane();
		timelineScroll.setViewport( timelineDataScroll.getColumnHeader() );
		timelineScroll.setHorizontalScrollBarPolicy( ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER );
		
		DropTarget dropTarget = new DropTarget() {
			public boolean isActive() {
				return true;
			}
		};
		timelineDataScroll.setDropTarget( dropTarget );
		
		colHeaderTable = new JTable();
		//colHeaderTable.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );
		//colHeaderTable.setMinimumSize( new Dimension(50,20) );
		colHeaderTable.setRowHeight( 50 );
		
		TableCellRenderer renderer = new DefaultTableCellRenderer() {
		//TableCellRenderer	renderer = new TableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				JComboBox comb = combList.get(row);
				comb.setSelectedItem( value );
				return comb;
			}
		};
		//colHeaderTable.setDefaultRenderer( String.class, renderer );
		
		malCombo.setEditable( true );
		DefaultCellEditor editor = new DefaultCellEditor( malCombo );
		editor.setClickCountToStart( 2 );
		colHeaderTable.setDefaultEditor( String.class, editor );
		
		timelineDataScroll.setRowHeaderView( colHeaderTable );
		JScrollPane	colHeaderScroll = new JScrollPane( timelineDataScroll.getRowHeader() );
		colHeaderScroll.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_NEVER );
		colHeaderScroll.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );
		
		timelineDataScroll.getRowHeader().setBackground( Color.white );
		
		LinkedSplitPane	timelineSplit = new LinkedSplitPane( JSplitPane.VERTICAL_SPLIT, timelineScroll, timelineDataScroll );
		lsplitPane = new LinkedSplitPane( JSplitPane.VERTICAL_SPLIT, colHeaderTable.getTableHeader(), colHeaderScroll );
		timelineSplit.setLinkedSplitPane( lsplitPane );
		lsplitPane.setLinkedSplitPane( timelineSplit );
		timelineScroll.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_NEVER );
		
		tsplitPane = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, lsplitPane, timelineSplit ) {
			public void setDividerLocation( double proportionalLocation ) {
				super.setDividerLocation(proportionalLocation);
				/*colHeaderTable.getColumnModel().getColumn(0).setPreferredWidth( 100 );
				colHeaderTable.getColumnModel().getColumn(0).setWidth( 100 );
				malCombo.setPreferredSize( prefSize );
				malCombo.setSize( prefSize );*/
			}
		};
		tsplitPane.setBackground( Color.white );
		
		/*if( lang.equals("IS") ) {
			timelineTabPane.addTab("Inn", timelineSplit);
			timelineTabPane.addTab("Út", null);
		} else {
			timelineTabPane.addTab("In", timelineSplit);
			timelineTabPane.addTab("Out", null);
		}*/
		
		Calendar now = Calendar.getInstance();
		cal = new GregorianCalendar();
		cal.set( Calendar.YEAR, now.get( Calendar.YEAR ) );
		cal.set( Calendar.MONTH, 0 );
		cal.set( Calendar.DAY_OF_MONTH, 1 );
		
		//Date d = new Date(Date.);
		TableModel	timelineModel = new TableModel() {
			public void addTableModelListener(TableModelListener arg0) {
				
			}

			public Class<?> getColumnClass(int arg0) {
				return String.class;
			}

			public int getColumnCount() {
				return 7;
			}

			public String getColumnName(int arg1) {
				//final long time = getCurrentTime();
				//cal.setTimeInMillis( time );
				//return cal.get( Calendar.WEEK_OF_YEAR ) + "";
				
				if( lang.equals("IS") ) {
					if( arg1 == 0 ) return "Sunnudagur";
					else if( arg1 == 1 ) return "Mánudagur";
					else if( arg1 == 2 ) return "Þriðjudagur";
					else if( arg1 == 3 ) return "Miðvikudagur";
					else if( arg1 == 4 ) return "Fimmtudagur";
					else if( arg1 == 5 ) return "Föstudagur";
					else if( arg1 == 6 ) return "Laugardagur";
				} else {
					if( arg1 == 0 ) return "Sunday";
					else if( arg1 == 1 ) return "Monday";
					else if( arg1 == 2 ) return "Tuesday";
					else if( arg1 == 3 ) return "Wednesday";
					else if( arg1 == 4 ) return "Thursday";
					else if( arg1 == 5 ) return "Friday";
					else if( arg1 == 6 ) return "Saturday";
				}
				
				return "";
			}

			public int getRowCount() {
				return 1;
			}

			public Object getValueAt(int arg0, int arg1) {
				/*if( arg0 == 0 ) {
					if( arg1 == 0 ) return "Sunnudagur";
					else if( arg1 == 1 ) return "Mánudagur";
					else if( arg1 == 2 ) return "Þriðjudagur";
					else if( arg1 == 3 ) return "Miðvikudagur";
					else if( arg1 == 4 ) return "Fimmtudagur";
					else if( arg1 == 5 ) return "Föstudagur";
					else if( arg1 == 6 ) return "Laugardagur";
				} else {	*/				
					final long time = getCurrentTime();
					cal.setTimeInMillis( time );
					int weekday = cal.get( Calendar.DAY_OF_WEEK )-1;
					int val = (arg1-weekday);
					cal.setTimeInMillis( time+val*Timer.ONE_DAY );
					int mday = cal.get( Calendar.DAY_OF_MONTH );
					int mnum = cal.get( Calendar.MONTH );
					
					if( lang.equals("IS") ) {
						if( mnum == 0 ) return mday + ". janúar";
						else if( mnum == 1 ) return mday + ". febrúar";
						else if( mnum == 2 ) return mday + ". mars";
						else if( mnum == 3 ) return mday + ". apríl";
						else if( mnum == 4 ) return mday + ". maí";
						else if( mnum == 5 ) return mday + ". júní";
						else if( mnum == 6 ) return mday + ". júlí";
						else if( mnum == 7 ) return mday + ". ágúst";
						else if( mnum == 8 ) return mday + ". september";
						else if( mnum == 9 ) return mday + ". október";
						else if( mnum == 10 ) return mday + ". nóvember";
						else if( mnum == 11 ) return mday + ". desember";
					} else {
						if( mnum == 0 ) return mday + ". january";
						else if( mnum == 1 ) return mday + ". february";
						else if( mnum == 2 ) return mday + ". mars";
						else if( mnum == 3 ) return mday + ". april";
						else if( mnum == 4 ) return mday + ". may";
						else if( mnum == 5 ) return mday + ". june";
						else if( mnum == 6 ) return mday + ". july";
						else if( mnum == 7 ) return mday + ". august";
						else if( mnum == 8 ) return mday + ". september";
						else if( mnum == 9 ) return mday + ". oktober";
						else if( mnum == 10 ) return mday + ". november";
						else if( mnum == 11 ) return mday + ". december";
					}
				//}
				//cal.setTimeInMillis( time+arg1*Timer.ONE_DAY );
				//String str = CompatUtilities.getDateString( cal, arg0 == 1 );
				return "";
			}

			public boolean isCellEditable(int arg0, int arg1) {
				return false;
			}

			public void removeTableModelListener(TableModelListener arg0) {}

			public void setValueAt(Object arg0, int arg1, int arg2) {}
		};
		timelineTable.setModel( timelineModel );
		//timelineTable.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );
		timelineTable.setRowSelectionAllowed( false );
		timelineTable.setColumnSelectionAllowed( true );
		
		timelineTable.getColumnModel().getSelectionModel().addListSelectionListener( new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				boolean ss = sel;
				sel = false;
				if (!ss) {
					int rc = timelineDataTable.getRowCount();
					if( rc > 0 ) timelineDataTable.setRowSelectionInterval(0, rc-1);
					
					int selcol = timelineTable.getSelectedColumn();
					timelineDataTable.setColumnSelectionInterval(selcol, selcol);
					
					int[] selcols = timelineTable.getSelectedColumns();
					for( int i : selcols ) {
						timelineDataTable.addColumnSelectionInterval( i, i );
					}
					sel = false;
					
					updateEngLabel( stuff, foodInd, recipes );
				}
			}
		});
		timelineDataTable.getColumnModel().getSelectionModel().addListSelectionListener( new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				boolean ss = sel;
				sel = true;
				if (ss) {
					int[] cc = timelineDataTable.getSelectedColumns();
					if( cc != null && cc.length > 0 ) {
						//timelineTable.setColumnSelectionInterval(selcol, selcol);
						for (int c : cc) {
							if (c == cc[0])
								timelineTable.setColumnSelectionInterval(c, c);
							else
								timelineTable.addColumnSelectionInterval(c, c);
							sel = true;
						}
						
						//System.err.println( timelineDataTable.getSelectedRow() );
						//System.err.println( timelineDataTable.getSelectedColumn() );
						
						updateEngLabel( stuff, foodInd, recipes );
					}
				}
			}
		});
		colHeaderTable.getSelectionModel().addListSelectionListener( new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				boolean ss = sel;
				sel = false;
				if (!ss) {
					int cc = timelineDataTable.getColumnCount();
					if( cc > 0 ) timelineDataTable.setColumnSelectionInterval(0, cc-1);
					
					int selrow = colHeaderTable.getSelectedRow();
					if( selrow >= 0 && selrow < timelineDataTable.getRowCount() ) {
						timelineDataTable.setRowSelectionInterval(selrow, selrow);
					}
					
					int[] selrows = colHeaderTable.getSelectedRows();
					for( int i : selrows ) {
						if( i >= 0 && i < timelineDataTable.getRowCount() ) timelineDataTable.addRowSelectionInterval( i, i );
					}
					sel = false;
					
					updateEngLabel( stuff, foodInd, recipes );
				}
			}
		});
		
		timelineDataTable.getSelectionModel().addListSelectionListener( new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				boolean ss = sel;
				sel = true;
				if (ss) {
					int[] rr = timelineDataTable.getSelectedRows();
					if( rr != null && rr.length > 0 ) {
						//timelineTable.setColumnSelectionInterval(selcol, selcol);
						for (int r : rr) {
							if (r == rr[0])
								colHeaderTable.setRowSelectionInterval(r, r);
							else
								colHeaderTable.addRowSelectionInterval(r, r);
							sel = true;
						}
						
						//System.err.println( timelineDataTable.getSelectedRow() );
						//System.err.println( timelineDataTable.getSelectedColumn() );
						
						updateEngLabel( stuff, foodInd, recipes );
					}
				}
			}
		});
		
		timelineDataTable.setRowHeight( 50 );
		
		try {
			load();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		timelineDataTable.setRowSelectionAllowed( true );
		timelineDataTable.setColumnSelectionAllowed( true );

		//timelineDataTable.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );
		
		try {
			dropTarget.addDropTargetListener( new DropTargetListener(){
				public void dropActionChanged(DropTargetDragEvent dtde) {}
			
				public void drop(DropTargetDropEvent dtde) {
					String tstr = getCurrentCardName();
					
					Point loc = dtde.getLocation();
					Point offset = timelineDataScroll.getViewport().getViewPosition();
					Point p = new Point( offset.x + loc.x, offset.y + loc.y );
					int c = timelineDataTable.columnAtPoint( p );
					boolean intable = timelineDataTable.contains(p);
					Week list = eatList.get( tstr );
					if( list == null ) {
						list = new Week();
						setCurrentWeek( list );
						eatList.put( tstr, list );
					}
					String val;
					try {
						Object o = dtde.getTransferable().getTransferData( DataFlavor.stringFlavor );
						if( o != null ) {
							val = o.toString();
							String[] spl = val.split("\n");
							int r = timelineDataTable.getRowCount();
							if( intable ) r = timelineDataTable.rowAtPoint( p );
							
							String[] dayfood = list.d[c].split("\t");
							String[] bef = CompatUtilities.copyOfRange( dayfood, 0, Math.min(r,dayfood.length) );
							
							list.d[c] = "";
							int rr = 0;
							boolean first = true;
							for( String b : bef ) {
								if( list.d[c].length() == 0 ) {
									if( b.length() == 0 ) list.d[c] += " ";
									else list.d[c] += b;
								}
								else list.d[c] += "\t" + b;
								
								first = false;
								rr++;
							}
							
							for( ; rr < r; rr++ ) {
								list.d[c] += "\t";
								first = false;
							}
							//if( rr < r || bef.length == 1 ) bb = true;
							
							for( String str : spl ) {
								String[] subspl = str.split("\t");
								if( subspl.length > 0 ) {
									String vstr = subspl[0]+"|100|g";
									if( first ) {
										list.d[c] += vstr;
										first = false;
									} else {
										list.d[c] += "\t" + vstr;
									}
								}
								
								rr++;
							}
							
							for( int i = rr; i < dayfood.length; i++ ) {
								String b = dayfood[i];
								if( list.d[c].length() == 0 ) list.d[c] += b;
								else list.d[c] += "\t" + b;
							}
						
							int minsplit = list.d[c].split("\t").length;
							if( minsplit > min ) {
								updateMin( minsplit );
							}
							
							timelineDataTable.tableChanged( new TableModelEvent( timelineDataTable.getModel() ) );
							colHeaderTable.tableChanged( new TableModelEvent( colHeaderTable.getModel() ) );
							
							save( tstr );
						}
					} catch (UnsupportedFlavorException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				public void dragOver(DropTargetDragEvent dtde) {}
				public void dragExit(DropTargetEvent dte) {}
				public void dragEnter(DropTargetDragEvent dtde) {}
			});
		} catch (TooManyListenersException e) {
			e.printStackTrace();
		}
		
		JComponent drawer = new JComponent() {
			public void paintComponent( Graphics g ) {
				super.paintComponent( g );
			}
		};
		JScrollPane timelineDrawScroll = new JScrollPane( drawer );
		
		if( datepicker != null ) {
			datepicker.addPropertyChangeListener( new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent evt) {
					try {
						load();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
		}
		
		this.add( tsplitPane );
		
		
		//splitpane.setTopComponent( timelineTabPane );
		//splitpane.setBottomComponent( timelineDrawScroll );
	}
	
	public void updateMin( int minsplit ) {
		min = minsplit;
		combList = new ArrayList<JComboBox>();
		Dimension dim = new Dimension(100,25);
		for( int i = 0; i < min; i++ ) {
			JComboBox combo = new JComboBox();
			combo.setPreferredSize( dim );
			combo.setSize( dim );
			combList.add( combo );
			for( int k = 0; k < malCombo.getItemCount(); k++ ) {
				combo.addItem( malCombo.getItemAt(k) );
			}
		}
	}
	
	public void updateMin( Week w ) {
		min = w.getMin();
		combList = new ArrayList<JComboBox>();
		Dimension dim = new Dimension(100,25);
		for( int i = 0; i < min; i++ ) {
			JComboBox combo = new JComboBox();
			combo.setPreferredSize( dim );
			combo.setSize( dim );
			combList.add( combo );
			for( int k = 0; k < malCombo.getItemCount(); k++ ) {
				combo.addItem( malCombo.getItemAt(k) );
			}
		}
	}
	
	public void setCurrentWeek( final Week w ) {
		updateMin( w );
		final TableModel timelineDataModel = new TableModel() {
			public void addTableModelListener(TableModelListener l) {}

			public Class<?> getColumnClass(int columnIndex) {
				return String.class;
			}

			public int getColumnCount() {
				return 7;
			}

			public String getColumnName(int arg1) {
				//final long time = getCurrentTime();
				//cal.setTimeInMillis( time );
				//return cal.get( Calendar.WEEK_OF_YEAR ) + "";
				
				if( lang.equals("IS") ) {
					if( arg1 == 0 ) return "Sunnudagur";
					else if( arg1 == 1 ) return "Mánudagur";
					else if( arg1 == 2 ) return "Þriðjudagur";
					else if( arg1 == 3 ) return "Miðvikudagur";
					else if( arg1 == 4 ) return "Fimmtudagur";
					else if( arg1 == 5 ) return "Föstudagur";
					else if( arg1 == 6 ) return "Laugardagur";
				} else {
					if( arg1 == 0 ) return "Sunday";
					else if( arg1 == 1 ) return "Monday";
					else if( arg1 == 2 ) return "Tuesday";
					else if( arg1 == 3 ) return "Wednesday";
					else if( arg1 == 4 ) return "Thursday";
					else if( arg1 == 5 ) return "Friday";
					else if( arg1 == 6 ) return "Saturday";
				}
				
				return "";
			}

			public int getRowCount() {
				return min;
			}

			public Object getValueAt(int rowIndex, int columnIndex) {
				if( w != null ) {
					String[] split = w.d[columnIndex].split("\t");
					if( rowIndex < split.length ) {
						return split[rowIndex];
					}
				}
				return null;
			}

			public boolean isCellEditable(int rowIndex, int columnIndex) {
				String val = (String)this.getValueAt( rowIndex, columnIndex );
				if( val != null && val.length() > 3 ) return true;
				return false;
			}

			public void removeTableModelListener(TableModelListener l) {}
			
			public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
				String[] split = w.d[columnIndex].split("\t");
				if( rowIndex < split.length ) {
					split[rowIndex] = (String)aValue;
				}
				w.d[columnIndex] = split[0];
				for( int i = 1; i < split.length; i++ ) {
					w.d[columnIndex] += "\t" + split[i];
				}
				
				try {
					String tstr = getCurrentCardName();
					save( tstr );
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		timelineDataTable.setModel( timelineDataModel );
		
		final RenderComponent	rc = new RenderComponent();
		rc.addComboItems( allskmt );
		TableCellRenderer renderer = new TableCellRenderer() {			
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				//rc.setBounds(0, 0, table.getRowHeight(row), table.getColumnModel().getColumn(column).getWidth());
				rc.selected = isSelected;
				rc.grayed = row % 2 == 0;
				rc.selbg = table.getSelectionBackground();
				if( isSelected ) {
					rc.label.setForeground( Color.white );
				} else {
					rc.label.setForeground( Color.black );
				}
				
				if( value != null ) {
					String[] split = ((String)value).split("\\|");
					rc.label.setText( split[0] );
					if( split.length >= 3 && split[0].length() > 0 ) {
						rc.field.setText( split[1] );
						/*if( split[0].equals("BANANAR") ) {
							System.err.println( "BANANA "+split[2] );
							for( int i = 0; i < rc.combo.getItemCount(); i++ ) {
								String str = (String)rc.combo.getItemAt(i);
								if( str.startsWith("ltl") ) System.err.println( "item " + str );
							}
						}*/
						rc.combo.setSelectedItem( split[2] );
						rc.field.setVisible(true);
						rc.combo.setVisible(true);
					} else {
						rc.field.setText( "" );
						rc.combo.setSelectedItem( "" );
						rc.field.setVisible(false);
						rc.combo.setVisible(false);
					}
				} else {
					rc.label.setText( "" );
					rc.field.setText( "" );
					rc.combo.setSelectedItem( "" );
					rc.field.setVisible(false);
					rc.combo.setVisible(false);
				}
				//System.err.println( row+" "+column+""+rc.getBounds() );
				Rectangle rect = table.getCellRect(row, column, true);
				if( rc.getWidth() != rect.width || rc.getHeight() != rect.height ) {
					//System.err.println( "set w " + rect.width + "  " + rc.getWidth() );
					//System.err.println( "set h " + rect.height + "  " + rc.getHeight() );
					//System.err.println( "set uuuh " + rc.label.getBounds() );
					Dimension d = new Dimension( rect.width, rect.height );
					rc.setPreferredSize( d );
					rc.setMinimumSize( d );
					rc.setBounds( 0, 0, rect.width, rect.height );
					//table.repaint();
				}
				
				return rc;
			}
		};
		timelineDataTable.setDefaultRenderer(String.class, renderer);
		
		TableCellEditor editor = new MyEditor( skmt );
		timelineDataTable.setDefaultEditor( String.class, editor );
		
		//timelineDataTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		
		final TableModel typeModel = new TableModel() {
			public void addTableModelListener(TableModelListener l) {}
			public Class<?> getColumnClass(int columnIndex) {
				return String.class;
			}

			public int getColumnCount() {
				return 1;
			}

			public String getColumnName(int columnIndex) {
				final long time = getCurrentTime();
				cal.setTimeInMillis( time );
				int week = cal.get( Calendar.WEEK_OF_YEAR );
				
				return lang.equals("IS") ? "Máltíð - " + week + ". vika" : "Food for - " + week + ". week";
			}

			public int getRowCount() {
				return timelineDataModel.getRowCount();
			}

			public Object getValueAt(int rowIndex, int columnIndex) {
				if( w != null ) {
					String[] split = w.d[7].split("\t");
					if( rowIndex < split.length ) {
						return split[rowIndex];
					}
				}
				return null;
			}

			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return true;
			}

			public void removeTableModelListener(TableModelListener l) {}
			public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
				String s = w.d[7];
				String[] split = s.split("\t");
				s = "";
				boolean first = true;
				int i = 0;
				for( ; i < Math.min( split.length, rowIndex ); i++ ) {
					if( first ) {
						s += split[i];
						first = false;
					} else s += "\t"+split[i];
				}
				for( ; i < rowIndex; i++ ) {
					if( first ) {
						first = false;
					} else s += "\t";
				}
				if( first ) {
					s += aValue;
					first = false;
				} else s += "\t"+aValue;
				for( i = rowIndex+1; i < split.length; i++ ) {
					if( first ) {
						s += split[i];
						first = false;
					} else s += "\t"+split[i];
				}
				w.d[7] = s;
				
				try {
					String tstr = getCurrentCardName();
					save( tstr );
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		colHeaderTable.setModel( typeModel );
		
		/*int pwd = 100;
		colHeaderTable.getColumnModel().getColumn(0).setPreferredWidth( pwd );
		colHeaderTable.getColumnModel().getColumn(0).setWidth( pwd );
		colHeaderTable.getColumnModel().getColumn(0).setMinWidth( pwd );*/
		
		colHeaderTable.setPreferredScrollableViewportSize( colHeaderTable.getPreferredSize() );
		
		//tsplitPane.add
		//colHeaderTable.getColumnModel().getColumn(0).setMaxWidth( pwd );
	}
	
	public void load() throws IOException {
		String tstr = getCurrentCardName();
		min = 0;
		Week w = null;
		if( eatList.containsKey( tstr ) ) {
			w = eatList.get( tstr );
		} else {
			w = new Week();
			setCurrentWeek( w );
			try {
				File f = new File( System.getProperty("user.home"), ".isgem" );
				f = new File( f, "weeks" );
				if( f.exists() ) {
					f = new File( f, tstr );
					if( f.exists() ) {
						FileReader		fr = new FileReader( f );
						BufferedReader	br = new BufferedReader( fr );
						
						int i = 0;
						String line = br.readLine();
						while( line != null && i < 8 ) {
							if( i == 0 ) w.d[7] += line;
							else w.d[i-1] += line;
							line = br.readLine();
							i++;
						}
						eatList.put( tstr, w );
					}
				}
			} catch( SecurityException e ) {
				
			}
		}
		setCurrentWeek( w );
	}
	
	public void cardWrite( Writer w ) throws IOException {
		for( int r = 0; r < colHeaderTable.getRowCount(); r++ ) {
			Object val = colHeaderTable.getValueAt(r, 0);
			if( val != null ) {
				String valstr = val.toString();
				if( r != 0 ) w.write( "\t" + valstr );
				else w.write( valstr );
			}
		}
		w.write("\n");
		for( int c = 0; c < timelineDataTable.getColumnCount(); c++ ) {
			for( int r = 0; r < timelineDataTable.getRowCount(); r++ ) {
				Object val = timelineDataTable.getValueAt(r, c);
				if( val != null ) {
					String valstr = val.toString();
					if( r != 0 ) w.write( "\t" + valstr );
					else w.write( valstr );
				}
			}
			w.write("\n");
		}
	}
	
	public void saveString( String name, String value ) throws IOException {
		File f = new File( System.getProperty("user.home"), ".isgem" );
		f = new File( f, "weeks" );
		if( !f.exists() ) f.mkdirs();
		
		f = new File( f, name );
		FileWriter	fw = new FileWriter( f );
		fw.write( value );
		fw.close();
	}
	
	public void save( String name ) throws IOException {
		File f = new File( System.getProperty("user.home"), ".isgem" );
		f = new File( f, "weeks" );
		if( !f.exists() ) f.mkdirs();
		
		f = new File( f, name );
		FileWriter	fw = new FileWriter( f );
		cardWrite( fw );
		fw.close();
	}
}
