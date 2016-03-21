package org.simmi.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.NumberCell;
import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DoubleBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;

import elemental.client.Browser;
import elemental.events.Event;
import elemental.events.EventListener;
import elemental.events.MessageEvent;
import elemental.html.Console;
import elemental.html.ProgressElement;
import elemental.html.WebSocket;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Seab implements EntryPoint {
	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	private static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network "
			+ "connection and try again.";

	//static String host = "ws://127.0.0.1:8887";
	static String host = "ws://130.208.252.239:8887";
	
	/**
	 * Create a remote service proxy to talk to the server-side Greeting service.
	 */
	private final GreetingServiceAsync greetingService = GWT.create(GreetingService.class);

	public native WebSocket newWebSocket( String host ) /*-{
		return new WebSocket( host );
	}-*/;
	
	public native String getEventData( Event evt ) /*-{
		return evt.data;
	}-*/;
	
	public native void drawCogChart( JavaScriptObject arr, Element el ) /*-{	
	    var data;
	    var options = {
	        title: 'COG groups',
	        fontSize: '12',
	        width: 920,
	    	height: 600,
	        isStacked: true,
	        hAxis: {
	        	direction: -1,
          		slantedText: true,
          		slantedTextAngle:-90
          	}
	    };
	    //function drawChart() {
	      data = $wnd.google.visualization.arrayToDataTable( arr );
	
	      var chart = new $wnd.google.visualization.ColumnChart( el );
	      chart.draw(data, options);
	    //}
//	    window.onresize = function() {
//	    	cd.style.width = window.innerWidth;
//	    	cd.style.height = window.innerHeight;
//	    	options.width = window.innerWidth;
//	    	options.height = window.innerHeight;
//	    	var chart = new google.visualization.ColumnChart( cd );
//	    	chart.draw(data, options);
//	    }
	}-*/;
	
	public native void drawPancoreChart( JavaScriptObject arr, Element el ) /*-{
		var data;
	    var options = {
	        title: 'Pan-core genome',
	        width: 800,
	    	height: 600,
	    	hAxis: {
	        	direction: -1,
          		slantedText: true,
          		slantedTextAngle:-90
          	}
	    };
	    //var drawChart = function() {
	      $wnd.console.log( "bleh" );
	      data = $wnd.google.visualization.arrayToDataTable( arr );
	      $wnd.console.log( data );
	      var chart = new $wnd.google.visualization.LineChart( el );
	      chart.draw(data, options);
	    //}
		
		//$wnd.google.load("visualization", "1", {packages:["corechart"]});
	    //$wnd.google.setOnLoadCallback(drawChart);
	    
//	    $wnd.onresize = function() {
//	    	el.style.width = $wnd.innerWidth;
//	    	el.style.height = $wnd.innerHeight;
//	    	options.width = $wnd.innerWidth;
//	    	options.height = $wnd.innerHeight;
//	    	var chart = new $wnd.google.visualization.LineChart( el );
//	    	chart.draw(data, options);
//	    }
	}-*/;
	
	public void checkTreeItem( CheckBox cb, TreeItem ti, boolean check ) {
		CheckBox wcb = (CheckBox)ti.getWidget();
		if( wcb == cb ) {
			check = true;
		} else if( check ) {
			wcb.setValue(cb.getValue());
		}
		for( int i = 0; i < ti.getChildCount(); i++ ) {
			TreeItem subti = ti.getChild(i);
			checkTreeItem(cb, subti, check);
		}
	}
	
	public native String atob( String dataurl ) /*-{
		var d = atob( dataurl );
		return d;
	}-*/;
	
	public native ProgressElement newProgressElement() /*-{
		return new ProgressBar();
	}-*/;
	
	class Sequence {
		public Sequence( String id, String name, String cazy, String ec, String go, String cog, String spec, String blast ) {
			this.id = id;
			this.name = name;
			this.cazy = cazy;
			this.ec = ec;
			this.go = go;
			this.cog = cog;
			this.spec = spec;
			this.blastresult = blast;
			req = false;
		}
		
		public Sequence( String[] split ) {
			id = split[0];
			name = split[1];
			symbol = split[2];
			cazy = split[5];
			cog = split[4];
			go = split[6];
			ec = split[3];
			kegg = split[7];
			spec = split[8];
			copies = Integer.parseInt( split[9] );
			if( split.length > 10 ) {
				blastresult = atob( split[10] );
				int i = blastresult.indexOf("Expect =");
				int k = blastresult.indexOf(",", i+1);
				try {
					String evalstr = blastresult.substring(i+9, k);
					Browser.getWindow().getConsole().log( evalstr );
					evalue = Double.parseDouble(evalstr);
					Browser.getWindow().getConsole().log( evalue + "" );
				} catch( Exception e ) {}
			}
			req = false;
		}
		
		String id;
		String name;
		String symbol;
		String cazy;
		String cog;
		String go;
		String ec;
		String kegg;
		String spec;
		boolean req;
		int		copies;
		String blastresult;
		
		double evalue;
	}
	
	public String getTreeString( TreeItem tree ) {
		String ret = "";
		for( int i = 0; i < tree.getChildCount(); i++ ) {
			TreeItem ti = tree.getChild(i);
			CheckBox wcb = (CheckBox)ti.getWidget();
			if( ti.getChildCount() == 0 && wcb.getValue() ) {
				if( ret.length() == 0 ) ret = ti.getText();
				else ret += ","+ti.getText();
			} else {
				String addstr = getTreeString( ti );
				if( ret.length() == 0 ) ret = addstr;
				else if( addstr.length() > 0 ) ret += ","+addstr;
			}
		}
		return ret;
	}
	
	elemental.html.Window myPopup = null;
	String treestr = null;
	
	public void reloadData( final ListBox spec, final ClickHandler click, final TreeItem ti, final TreeItem tikegg ) {
		spec.clear();
		ti.removeItems();
		tikegg.removeItems();
		
		final WebSocket ws = newWebSocket( host );
		ws.setOnmessage( new EventListener() {
			@Override
			public void handleEvent(Event evt) {
				String message = getEventData( evt );
				Browser.getWindow().getConsole().log( message );
				if( message.startsWith("specs:") ) {
					int k = message.indexOf("cazy:");
					int h = message.indexOf("kegg:");
					if( k == -1 ) k = message.length();
					if( h == -1 ) h = message.length();
					
					Browser.getWindow().getConsole().log( message );
					
					String[] specsplit = message.substring(6,Math.min(k,h)).split(",");
					for( String specstr : specsplit ) {
						spec.addItem( specstr );
					}
					
					if( k < message.length() ) {
						String[] split = message.substring(k+5,h).split(",");
						Map<String,List<String>> mlist = new HashMap<String,List<String>>();
						for( String sp : split ) {
							if( sp.length() > 1 ) {
								int i = 0;
								while( i < sp.length() ) {
									char c = sp.charAt(i);
									if( c < '0' || c > '9' ) i++;
									else break;
								}
								String sub = sp.substring(0,i);
								List<String> ls;
								if( mlist.containsKey(sub) ) {
									ls = mlist.get(sub);
								} else {
									ls = new ArrayList<String>();
									mlist.put(sub, ls);
								}
								ls.add( sp );
							}
						}
					
						Browser.getWindow().getConsole().log( "msize " + mlist.size() );
						for( String mstr : mlist.keySet() ) {
							List<String> smlist = mlist.get(mstr);
							if( smlist.size() == 1 ) {
								CheckBox ghs = new CheckBox( mstr );
								ghs.addClickHandler( click );
								TreeItem ghti = new TreeItem( ghs );
								/*for( String str : smlist ) {
									CheckBox check = new CheckBox( str );
									TreeItem subti = new TreeItem( check );
									ghti.addItem( subti );
								}*/
								ti.addItem( ghti );
							} else {
								CheckBox ghs = new CheckBox( mstr );
								ghs.addClickHandler( click );
								TreeItem ghti = new TreeItem( ghs );
								for( String str : smlist ) {
									CheckBox check = new CheckBox( str );
									TreeItem subti = new TreeItem( check );
									ghti.addItem( subti );
								}
								ti.addItem( ghti );
							}
						}
					}
					
					if( h < message.length() ) {
						String[] nsplit = message.substring(h+5,message.length()).split(",");
						for( String kegg : nsplit ) {
							CheckBox check = new CheckBox( kegg );
							TreeItem subti = new TreeItem( check );
							tikegg.addItem( subti );
						}
					}
					ws.close();
				}
			}
		});
		ws.setOnopen( new EventListener() {
			@Override
			public void handleEvent(Event evt) {
				ws.send("specs:");
			}
		});
		ws.setOnerror( new EventListener() {
			@Override
			public void handleEvent(Event evt) {
				Browser.getWindow().getConsole().log("error " + evt);
			}
		});
	}
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		final elemental.html.Window wnd = Browser.getWindow();
		wnd.addEventListener("message", new EventListener() {
			@Override
			public void handleEvent(Event evt) {
				MessageEvent me = (MessageEvent) evt;
				String dstr = (String) me.getData();

				Console console = Browser.getWindow().getConsole();
				console.log("okbleh");
				console.log(dstr);

				if (dstr.equals("ready")) {
					//me.get
					elemental.html.Window source = myPopup;//me.getSource();
					console.log(dstr + " soso2 ");
					
					String myurl = "";
					try {
						console.log( myPopup );
						myurl = myPopup.getLocation().getHref();
						console.log( "moki " + myurl + "  " + myPopup );
					} catch( Exception e) {
						console.log( "mokifail " );
					}
					
					String surl = "";
					try {
						console.log( source );
						surl = source.getLocation().getHref();
						console.log( "moki2 " + surl + "  " + source );
					} catch( Exception e) {
						console.log( "moki2fail " );
					}
					
					//elemental.html.Window source = myPopup;// me.getSource();
					//console.log( myPopup.getName() );
					//console.log(source.getName());

					console.log("about to post tree");
					console.log("posting tree " + treestr);
					if (treestr != null) {
						console.log("posting tree");
						source.postMessage(treestr, "*");
						treestr = null;
					}
				}/* else if (dstr.startsWith("propagate")) {
					int fi = dstr.indexOf('{');
					int li = dstr.indexOf('}');
					String substr = dstr.substring(fi + 1, li);
					String[] split = substr.split(",");
					Set<String> splitset = new HashSet<String>(Arrays.asList(split));
					for (Sequence seq : val) {
						SequenceOld so = (SequenceOld) seq;
						String name = seq.getName();
						// console.log("trying "+name);
						if (splitset.contains(name))
							so.setSelected(true);
					}
					draw(xstart, ystart);
				}*/
			}
		}, true);
		
		if( Window.Location.getParameterMap().keySet().contains("host") ) {
			host = Window.Location.getParameter("host");
			//String tree = URL.decode( enctree );
		}
		
		//final String host = "ws://130.208.252.239:8887";
		//final String prot = "[\"protocolOne\"]";
			
		final Tree tree = new Tree();
		final ClickHandler click = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				for( int i = 0; i < tree.getItemCount(); i++ ) {
					TreeItem ti = tree.getItem(i);
					checkTreeItem( (CheckBox)event.getSource(), ti, false );
				}
			}
		};
		
		CheckBox allCazy = new CheckBox("All CAZY");
		final TreeItem ti = new TreeItem( allCazy );
		allCazy.addClickHandler( click );
		
		CheckBox allCog = new CheckBox("All COG");
		TreeItem ticog = new TreeItem( allCog );
		allCog.addClickHandler( click );
		
		CheckBox allKegg = new CheckBox("All KEGG");
		final TreeItem tikegg = new TreeItem( allKegg );
		allKegg.addClickHandler( click );
		
		tree.addItem( ti );
		tree.addItem( ticog );
		tree.addItem( tikegg );
		
		/*CheckBox ghs = new CheckBox("GHs");
		ghs.addClickHandler( click );
		CheckBox pls = new CheckBox("PLs");
		pls.addClickHandler( click );
		TreeItem ghti = new TreeItem( ghs );
		for( int i = 0; i < 3; i++ ) {
			CheckBox check = new CheckBox( "GH "+i );
			TreeItem subti = new TreeItem( check );
			ghti.addItem( subti );
		}
		TreeItem plti = new TreeItem( pls );
		for( int i = 0; i < 3; i++ ) {
			CheckBox check = new CheckBox( "PL "+i );
			TreeItem subti = new TreeItem( check );
			plti.addItem( subti );
		}
		ti.addItem( ghti );
		ti.addItem( plti );*/
		
		final ListBox	spec = new ListBox( true );
		/*spec.addItem("MAT493");
		spec.addItem("MAT4553");
		spec.addItem("MAT4555");
		spec.addItem("MAT4685");
		spec.addItem("MAT4696");
		spec.addItem("MAT4699");
		spec.addItem("MAT4705");
		spec.addItem("MAT4716");
		spec.addItem("MAT4717");
		spec.addItem("MAT4721");
		spec.addItem("MAT4725");
		spec.addItem("MAT4726");
		spec.addItem("MAT4784");*/
		spec.setSize(120+"px", 180+"px");
		
		//String[] cazy = new String[] {};
		reloadData( spec, click, ti, tikegg );
		
		final RootPanel rp = RootPanel.get();
		Style st = rp.getElement().getStyle();
		st.setBorderWidth(0.0, Unit.PX);
		st.setMargin(0.0, Unit.PX);
		st.setPadding(0.0, Unit.PX);
		
		int w = Window.getClientWidth();
		int h = Window.getClientHeight();
		//rp.setWidth( w+"px" );
		
		final CellTable<Sequence> table = new CellTable<Sequence>(50);
		final MultiSelectionModel<Sequence> multisel = new MultiSelectionModel<Sequence>();
		table.setSelectionModel( multisel );
		final VerticalPanel vp = new VerticalPanel();
		
		final TextArea ta = new TextArea();
		int cw = 320;
		int ch = 170;
		ta.setSize(cw+"px", ch+"px");
		
		final TextArea results = new TextArea();
		results.getElement().setId("mytextarea");
		//ßresults.getElement().getStyle().set
		results.setReadOnly( true );
		results.setSize(w+"px", "320px");
		
		Button blast = new Button("Blast");
		blast.addClickHandler( new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				final WebSocket ws = newWebSocket( host );
				ws.setOnopen( new EventListener() {		
					@Override
					public void handleEvent(Event evt) {
						Browser.getWindow().getConsole().log("log");
						ws.send( ta.getText() );
					}
				});
				ws.setOnmessage( new EventListener() {		
					@Override
					public void handleEvent(Event evt) {
						String str = getEventData( evt );
						Browser.getWindow().getConsole().log( str );
						results.setText( results.getText()+str );
					}
				});
				ws.setOnerror( new EventListener() {
					@Override
					public void handleEvent(Event evt) {
						
					}
				});
			}
		});
		
		vp.setWidth(w+"px");
		//vp.setHorizontalAlignment( VerticalPanel.ALIGN_CENTER );
		//vp.setVerticalAlignment( VerticalPanel.ALIGN_MIDDLE );
		//HorizontalPanel hp = new HorizontalPanel();
		//hp.add(  );
		
	    TextColumn<Sequence> nameColumn = new TextColumn<Sequence>() {
	      @Override
	      public String getValue(Sequence contact) {
	        return contact.name;
	      }
	    };
	    nameColumn.setSortable( true );
	    TextColumn<Sequence> symbolColumn = new TextColumn<Sequence>() {
		      @Override
		      public String getValue(Sequence contact) {
		        return contact.symbol;
		      }
		};
		symbolColumn.setSortable( true );
	    
		SafeHtmlCell	cazycell = new SafeHtmlCell();
		Column<Sequence,SafeHtml> cazyColumn = new Column<Sequence,SafeHtml>( cazycell ) {
	      @Override
	      public SafeHtml getValue(Sequence contact) {
	    	SafeHtmlBuilder sb = new SafeHtmlBuilder();
    		String cazystr = contact.cazy;
    		if( cazystr.equals("null") ) {
    			sb.appendHtmlConstant("");
    		} else {
	    		int i = cazystr.indexOf("(");
	    		String cazy = cazystr.substring(0,i);
	    		String eval = cazystr.substring(i);
	    		String html =  "<a title=\""+cazystr+"\" target=\"_blank\" href=\"http://www.cazy.org/"+cazy.toUpperCase()+".html\">"+cazy+"</a>"+eval;
	    		sb.appendHtmlConstant(html);
    		}
	        return sb.toSafeHtml();
	      }
		};
		cazyColumn.setSortable( true );
	    
	    /*TextColumn<Sequence> ecColumn = new TextColumn<Sequence>() {
	      @Override
	      public String getValue(Sequence contact) {
	        return contact.ec;
	      }
		};*/
	    SafeHtmlCell	eccell = new SafeHtmlCell();
		Column<Sequence,SafeHtml> ecColumn = new Column<Sequence,SafeHtml>( eccell ) {
	      @Override
	      public SafeHtml getValue(Sequence contact) {
	    	SafeHtmlBuilder sb = new SafeHtmlBuilder();
    		String ec = contact.ec;
    		if( ec.startsWith(":") ) ec = ec.substring(1);
    		String html = ec.equals("null") ? "": "<a title=\""+ec+"\" target=\"_blank\" href=\"http://www.enzyme-database.org/query.php?ec="+ec+"\">"+ec+"</a>";
    		sb.appendHtmlConstant(html);
	        return sb.toSafeHtml();
	      }
		};
		ecColumn.setSortable( true );
		
		/*SafeHtmlCell	gocell = new SafeHtmlCell();
		Column<Sequence,SafeHtml> goColumn = new Column<Sequence,SafeHtml>( gocell ) {
	      @Override
	      public SafeHtml getValue(Sequence contact) {
	    	SafeHtmlBuilder sb = new SafeHtmlBuilder();
	        sb.appendHtmlConstant( contact.go );
	        return sb.toSafeHtml();
	      }
		};
		goColumn.setSortable( true );*/
		
		SafeHtmlCell	keggcell = new SafeHtmlCell();
		Column<Sequence,SafeHtml> keggColumn = new Column<Sequence,SafeHtml>( keggcell ) {
		      @Override
		      public SafeHtml getValue(Sequence contact) {
		    	  SafeHtmlBuilder sb = new SafeHtmlBuilder();
		    	  String[] ss = contact.kegg.substring( 1,contact.kegg.length()-1).split(",");
		    	  for( String s : ss ) {
		    		  String strim = s.trim();
		    		  int k = strim.indexOf('=');
		    		  
		    		  sb.appendHtmlConstant( "<a title=\""+strim.substring(k+1)+"\" target=\"_blank\" href=\"http://www.genome.jp/kegg-bin/show_pathway?map=map"+strim.substring(0,k)+"&show_description=show\">"+strim.substring(0,k)+"</a><br>" );
		    	  }
			      return sb.toSafeHtml();
		      }
		};
		keggColumn.setSortable( true );
			
		TextColumn<Sequence> cogColumn = new TextColumn<Sequence>() {
	      @Override
	      public String getValue(Sequence contact) {
	        return contact.cog;
	      }
		};
		cogColumn.setSortable( true );
		
		SafeHtmlCell	gocell = new SafeHtmlCell();
		Column<Sequence,SafeHtml> goColumn = new Column<Sequence,SafeHtml>( gocell ) {
	      @Override
	      public SafeHtml getValue(Sequence contact) {
	    	SafeHtmlBuilder sb = new SafeHtmlBuilder();
	    	String[] gosplit = contact.go.split(",");
	    	for( String go : gosplit ) {
	    		int k = go.indexOf('-');
	    		String goid = go.substring(0,k);
	    		String goname = go.substring(k+1);
	    		String html = "<a title=\""+goname+"\" target=\"_blank\" href=\"http://amigo2.berkeleybop.org/amigo/medial_search?q="+goid.replace(":", "%3A")+"\">"+goid+"</a><br>";
	    		sb.appendHtmlConstant(html);
	    	}
	        return sb.toSafeHtml();
	      }
		};
		goColumn.setSortable( true );
		
		NumberCell numcell = new NumberCell(NumberFormat.getScientificFormat());
		//numcell.
		Column<Sequence, Number> evColumn = new Column<Sequence, Number>( numcell ) {
		      @Override
		      public Number getValue(Sequence contact) {
		        return contact.evalue;
		      }
			};
		evColumn.setSortable( true );
		TextColumn<Sequence> specColumn = new TextColumn<Sequence>() {
		      @Override
		      public String getValue(Sequence contact) {
		        return contact.spec;
		      }
			};
			specColumn.setSortable( true );
			
			NumberCell copycell = new NumberCell(NumberFormat.getDecimalFormat());
			//numcell.
			Column<Sequence, Number> copyColumn = new Column<Sequence, Number>( copycell ) {
			      @Override
			      public Number getValue(Sequence contact) {
			        return contact.copies;
			      }
				};
			copyColumn.setSortable( true );
		
		table.addColumn( nameColumn, "Name" );
		table.addColumn( symbolColumn, "Symbol" );
		table.addColumn( cazyColumn, "Cazy" );
		table.addColumn( ecColumn, "EC" );
		table.addColumn( keggColumn, "KEGG" );
		table.addColumn( cogColumn, "COG" );
		table.addColumn( goColumn, "GO" );
		table.addColumn( evColumn, "evalue" );
		table.addColumn( specColumn, "Species" );
		table.addColumn( copyColumn, "Copies" );
		
		//for( int i = 0; i < spec.getItemCount(); i++ ) {
		//	final String specstr = spec.getItemText(i);
			
			/*TextColumn<Sequence> specColumn = new TextColumn<Sequence>() {
			      @Override
			      public String getValue(Sequence contact) {
			        return specstr;
			      }
				};
				specColumn.setSortable( true );*/
			Column<Sequence, Boolean> cColumn = new Column<Sequence,Boolean>( new CheckboxCell() ) {

				@Override
				public Boolean getValue(Sequence object) {
					return object.req;
				}
				
			};
				table.addColumn( cColumn, "Request sequence" );
		//}
			
		Label label = new Label("evalue:");
		final DoubleBox eval = new DoubleBox();
		eval.setValue(0.00001);
		eval.setText("0.00001");
		
		ScrollPanel treesp = new ScrollPanel( tree );
		treesp.setSize(320+"px", 180+"px");
		
		HorizontalPanel hp = new HorizontalPanel();
		hp.setSpacing(3);
		hp.add( treesp );
		hp.add( spec );
		hp.add( ta );
		hp.add( label );
		hp.add( eval );
		
		final List<Sequence> l = new ArrayList<Sequence>();
		//l.addAll( Arrays.asList( new Sequence[] {new Sequence("name","cazy","ec"), new Sequence("a","q","r"), new Sequence("n","c","e"), new Sequence("n","c","e")} ) );
		
		ListDataProvider<Sequence> dataProvider = new ListDataProvider<Sequence>();

	    // Connect the table to the data provider.
	    dataProvider.addDataDisplay(table);

	    // Add the data to the data provider, which automatically pushes it to the
	    // widget.
	    final List<Sequence> list = dataProvider.getList();
	    for (Sequence contact : l) {
	      list.add(contact);
	    }
	    
	    ListHandler<Sequence> columnSortHandler = new ListHandler<Sequence>(list);
        columnSortHandler.setComparator(nameColumn,
            new Comparator<Sequence>() {
              public int compare(Sequence o1, Sequence o2) {
                if (o1 == o2) {
                  return 0;
                }

                // Compare the name columns.
                if (o1 != null) {
                  return (o2 != null) ? o1.name.compareTo(o2.name) : 1;
                }
                return -1;
              }
            });
        table.addColumnSortHandler(columnSortHandler);
        ListHandler<Sequence> cazySortHandler = new ListHandler<Sequence>(list);
        cazySortHandler.setComparator(cazyColumn,
            new Comparator<Sequence>() {
              public int compare(Sequence o1, Sequence o2) {
                if (o1 == o2) {
                  return 0;
                }

                // Compare the name columns.
                if (o1 != null) {
                  return (o2 != null) ? o1.cazy.compareTo(o2.cazy) : 1;
                }
                return -1;
              }
            });
        table.addColumnSortHandler(cazySortHandler);
        ListHandler<Sequence> ecSortHandler = new ListHandler<Sequence>(list);
        ecSortHandler.setComparator(ecColumn,
            new Comparator<Sequence>() {
              public int compare(Sequence o1, Sequence o2) {
                if (o1 == o2) {
                  return 0;
                }

                // Compare the name columns.
                if (o1 != null) {
                  return (o2 != null) ? o1.ec.compareTo(o2.ec) : 1;
                }
                return -1;
              }
            });
        table.addColumnSortHandler(ecSortHandler);
        ListHandler<Sequence> goSortHandler = new ListHandler<Sequence>(list);
        goSortHandler.setComparator(goColumn,
            new Comparator<Sequence>() {
              public int compare(Sequence o1, Sequence o2) {
                if (o1 == o2) {
                  return 0;
                }

                // Compare the name columns.
                if (o1 != null) {
                  return (o2 != null) ? o1.go.compareTo(o2.go) : 1;
                }
                return -1;
              }
            });
        table.addColumnSortHandler(goSortHandler);
        ListHandler<Sequence> cogSortHandler = new ListHandler<Sequence>(list);
        cogSortHandler.setComparator(cogColumn,
            new Comparator<Sequence>() {
              public int compare(Sequence o1, Sequence o2) {
                if (o1 == o2) {
                  return 0;
                }

                // Compare the name columns.
                if (o1 != null) {
                  return (o2 != null) ? o1.cog.compareTo(o2.cog) : 1;
                }
                return -1;
              }
            });
        table.addColumnSortHandler(cogSortHandler);
        
        ListHandler<Sequence> evalSortHandler = new ListHandler<Sequence>(list);
        evalSortHandler.setComparator(evColumn,
            new Comparator<Sequence>() {
              public int compare(Sequence o1, Sequence o2) {
                if (o1 == o2) {
                  return 0;
                }

                // Compare the name columns.
                if (o1 != null) {
                  return (o2 != null) ? Double.compare(o1.evalue, o2.evalue) : -1;
                }
                return -1;
              }
            });
        table.addColumnSortHandler(evalSortHandler);
        
        ListHandler<Sequence> specSortHandler = new ListHandler<Sequence>(list);
        specSortHandler.setComparator(specColumn,
            new Comparator<Sequence>() {
              public int compare(Sequence o1, Sequence o2) {
                if (o1 == o2) {
                  return 0;
                }

                // Compare the name columns.
                if (o1 != null) {
                  return (o2 != null) ? o1.spec.compareTo(o2.spec) : 1;
                }
                return -1;
              }
            });
        table.addColumnSortHandler(specSortHandler);

	        // We know that the data is sorted alphabetically by default.
	    //table.getColumnSortList().push(nameColumn);

	    
		/*table.setRowCount(l.size(), true);
		table.setVisibleRange(0, 3);
		
		AsyncDataProvider<Sequence> dataProvider = new AsyncDataProvider<Sequence>() {
		      @Override
		      protected void onRangeChanged(HasData<Sequence> display) {
		        final Range range = display.getVisibleRange();

		        // Get the ColumnSortInfo from the table.
		        final ColumnSortList sortList = table.getColumnSortList();

		        // This timer is here to illustrate the asynchronous nature of this data
		        // provider. In practice, you would use an asynchronous RPC call to
		        // request data in the specified range.
		        new Timer() {
		          @Override
		          public void run() {
		            int start = range.getStart();
		            int end = start + range.getLength();
		            // This sorting code is here so the example works. In practice, you
		            // would sort on the server.
		            Collections.sort(l, new Comparator<Sequence>() {
		              public int compare(Sequence o1, Sequence o2) {
		                if (o1 == o2) {
		                  return 0;
		                }

		                // Compare the name columns.
		                int diff = -1;
		                if (o1 != null) {
		                  diff = (o2 != null) ? o1.name.compareTo(o2.name) : 1;
		                }
		                return sortList.get(0).isAscending() ? diff : -diff;
		              }
		            });
		            List<Sequence> dataInRange = l.subList(start, end);

		            // Push the data back into the list.
		            table.setRowData(start, dataInRange);
		          }
		        }.schedule(2000);
		      }
		    };

		    // Connect the list to the data provider.
		    dataProvider.addDataDisplay(table);*/
		
	    Button clear = new Button("Clear");
	    clear.addClickHandler( new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Set<Sequence> selset = multisel.getSelectedSet();
				Browser.getWindow().getConsole().log("sel size: "+selset.size());
				if( selset == null || selset.size() == 0 ) list.clear();
				else list.removeAll( selset );
				
				multisel.clear();
			}
		});
	    
	    final DialogBox pp = new DialogBox( true );
		final VerticalPanel	vsp = new VerticalPanel();
		vsp.setSpacing( 10 );
		vsp.setHorizontalAlignment( VerticalPanel.ALIGN_CENTER );
		final ProgressElement pe = (ProgressElement)DOM.createElement("progress");
		
		pe.getStyle().setWidth(400+"px");
		pe.getStyle().setHeight(50+"px");
		
		//Widget sp = new Widget();
		final SimplePanel sp = new SimplePanel();
		sp.setSize(400+"px", 50+"px");
		sp.getElement().appendChild( (Node)pe );
	        
		Button fetch = new Button("Fetch");
		fetch.addClickHandler( new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				vsp.clear();
				pp.clear();
				
				final WebSocket ws = newWebSocket( host );
				ws.setOnopen( new EventListener() {
					@Override
					public void handleEvent(Event evt) {
						vsp.add( new Label("Fetching...") );
						vsp.add( sp );
						pp.add( vsp );
						pp.center();
						
						String blast = ta.getText();
						if( blast.length() > 0 ) {
							if( blast.startsWith(">") ) {
								String pars = "evalue:"+eval.getValue().toString();
								String str = "";
								for( int i = 0; i < spec.getItemCount(); i++ ) {
									if( spec.isItemSelected(i) ) {
										String item = spec.getItemText(i);
										if( str.length() == 0 ) str += item;
										else str += ","+item;
									}
								}
								if( str.length() > 0 ) pars += "spec:"+str;
								ws.send( pars );
								ws.send( blast );
							} else ws.send("query:"+blast);
						} else {
							String treestr = "";
							for( int i = 0; i < tree.getItemCount(); i++ ) {
								TreeItem ti = tree.getItem(i);
								CheckBox wcb = (CheckBox)ti.getWidget();
								if( ti.getChildCount() == 0 && wcb.getValue() ) {
									if( treestr.length() == 0 ) treestr = ti.getText();
									else treestr += ","+ti.getText();
								} else {
									String addstr = getTreeString( ti );
									if( treestr.length() == 0 ) treestr = addstr;
									else if( addstr.length() > 0 ) treestr += ","+addstr;
								}
							}
							
							String str = "";
							for( int i = 0; i < spec.getItemCount(); i++ ) {
								if( spec.isItemSelected(i) ) {
									if( str == null || str.length() == 0 ) str += spec.getItemText(i);
									else str += "," + spec.getItemText(i);
								}
							}
							String qstr = "query:"+treestr;
							if( str != null && str.length() > 0 ) qstr += "spec:"+str;
							ws.send( qstr );
						}
					}
				});
				ws.setOnmessage( new EventListener() {
					@Override
					public void handleEvent(Event evt) {
						String message = getEventData( evt );
						if( message.contains("new connection") ) {
							
						} else if( message.equals("close") ) {
							ws.close();
						} else {
							pp.hide();
							String[] split = message.split("\n");
							Browser.getWindow().getConsole().log( message );
							for( String spl : split ) {
								String[] tsplit = spl.split("\t");
								Sequence seq = new Sequence( tsplit );
								list.add( seq );
							}
						}
					}
				});
				ws.setOnerror( new EventListener() {
					@Override
					public void handleEvent(Event evt) {
						pp.hide();
					}
				});
			}
		});
		
		table.setWidth(w+"px");
		
		final HorizontalPanel buttonparent = new HorizontalPanel();
		buttonparent.setWidth(w+"px");
		buttonparent.setHorizontalAlignment( HorizontalPanel.ALIGN_CENTER );
		
		final HorizontalPanel buttonhp = new HorizontalPanel();
		buttonhp.setHorizontalAlignment( HorizontalPanel.ALIGN_CENTER );
		buttonhp.setSpacing(5);
		buttonhp.add( fetch );
		buttonhp.add( clear );
		
		buttonparent.add( buttonhp );
		
		Button reqButton = new Button("Request sequences");
		reqButton.addClickHandler( new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				final WebSocket ws = newWebSocket( host );
				ws.setOnopen( new EventListener() {
					@Override
					public void handleEvent(Event evt) {
						String str = null;
						Set<Sequence> sset = multisel.getSelectedSet();
						for( Sequence seq : sset ) {
							if( str == null ) str = seq.id;
							else str += ","+seq.id;
						}
						ws.send("request:"+str);
					}
				});
				ws.setOnmessage( new EventListener() {
					@Override
					public void handleEvent(Event evt) {
						String message = getEventData( evt );
						if( message.contains("new connection") ) {
							
						} else {
							PopupPanel pp = new PopupPanel();
							pp.setSize(640+"px", 400+"px");
							pp.setAutoHideEnabled( true );
							TextArea ta = new TextArea();
							ta.setText( message );
							pp.add( ta );
							pp.center();
							ws.close();
						}
					}
				});
				ws.setOnerror( new EventListener() {
					@Override
					public void handleEvent(Event evt) {}
				});
			}
		});
		buttonhp.add( reqButton );
		
		Button pancore = new Button("Pan core");
		pancore.addClickHandler( new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				final WebSocket ws = newWebSocket( host );
				ws.setOnopen( new EventListener() {
					@Override
					public void handleEvent(Event evt) {
						/*String str = null;
						Set<Sequence> sset = multisel.getSelectedSet();
						for( Sequence seq : sset ) {
							if( str == null ) str = seq.id;
							else str += ","+seq.id;
						}*/
						ws.send("pancore:");
					}
				});
				ws.setOnmessage( new EventListener() {
					@Override
					public void handleEvent(Event evt) {
						String message = getEventData( evt );
						if( message.contains("new connection") ) {
							
						} else {
							//atob(message);
							SimplePanel sip = new SimplePanel();
							//vp.add( sip );
							
							Browser.getWindow().getConsole().log( message );
							JSONValue jval = JSONParser.parseLenient( message );
							
							drawPancoreChart( jval.isArray().getJavaScriptObject(), sip.getElement() );
							
							//ScrollPanel sp = new ScrollPanel( sip );
							//sp.add( img );
							PopupPanel pop = new PopupPanel();
							pop.setAutoHideEnabled( true );
							pop.add( sip );
							//pop.setSize("512px", "384px");
							pop.center();
							ws.close();
						}
					}
				});
				ws.setOnerror( new EventListener() {
					@Override
					public void handleEvent(Event evt) {}
				});
			}
		});
		buttonhp.add( pancore );
		
		Button cogchart = new Button("Cog chart");
		cogchart.addClickHandler( new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				final WebSocket ws = newWebSocket( host );
				ws.setOnopen( new EventListener() {
					@Override
					public void handleEvent(Event evt) {
						/*String str = null;
						Set<Sequence> sset = multisel.getSelectedSet();
						for( Sequence seq : sset ) {
							if( str == null ) str = seq.id;
							else str += ","+seq.id;
						}*/
						ws.send("cogchart:");
					}
				});
				ws.setOnmessage( new EventListener() {
					@Override
					public void handleEvent(Event evt) {
						String message = getEventData( evt );
						if( message.contains("new connection") ) {
							
						} else {
							//atob(message);
							SimplePanel sip = new SimplePanel();
							//vp.add( sip );
							
							Browser.getWindow().getConsole().log( message );
							JSONValue jval = JSONParser.parseLenient( message );
							
							//ScrollPanel sp = new ScrollPanel( sip );
							//sp.add( img );
							PopupPanel pop = new PopupPanel();
							pop.setWidth("960px");
							pop.setAutoHideEnabled( true );
							pop.add( sip );
							//pop.setSize("512px", "384px");
							
							drawCogChart( jval.isArray().getJavaScriptObject(), sip.getElement() );
							pop.center();
							
							ws.close();
						}
					}
				});
				ws.setOnerror( new EventListener() {
					@Override
					public void handleEvent(Event evt) {}
				});
			}
		});
		buttonhp.add( cogchart );
		
		Button aniMatrix = new Button("ANIMatrix");
		aniMatrix.addClickHandler( new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				vsp.clear();
				pp.clear();
				
				final WebSocket ws = newWebSocket( host );
				ws.setOnopen( new EventListener() {
					@Override
					public void handleEvent(Event evt) {
						vsp.add( new Label("Fetching...") );
						vsp.add( sp );
						pp.add( vsp );
						pp.center();
						
						String str = "";
						for( int i = 0; i < spec.getItemCount(); i++ ) {
							if( spec.isItemSelected(i) ) {
								String item = spec.getItemText(i);
								if( str.length() == 0 ) str += item;
								else str += ","+item;
							}
						}
						ws.send("anim:"+str);
					}
				});
				ws.setOnmessage( new EventListener() {
					@Override
					public void handleEvent(Event evt) {
						String message = getEventData( evt );
						if( message.contains("new connection") ) {
							
						} else {
							pp.clear();
							
							//pp.hide();
							//atob(message);
							String dataurl = "data:image/png;base64,"+message;
							final Image img = new Image( dataurl );
							final ScrollPanel sp = new ScrollPanel( img );
							img.addLoadHandler( new LoadHandler() {
								@Override
								public void onLoad(LoadEvent event) {
									int nw = img.getWidth()/2;
									int nh = img.getHeight()/2;
									
									img.setSize(nw+"px", nh+"px");
									
									sp.setSize( Math.min(800,nw)+"px", Math.min(600,nh)+"px");
									//sp.add( img );
									pp.center();
								}
							});
							
							pp.add( sp );
							pp.center();
							ws.close();
						}
					}
				});
				ws.setOnerror( new EventListener() {
					@Override
					public void handleEvent(Event evt) { pp.hide(); }
				});
			}
		});
		buttonhp.add( aniMatrix );
		
		Button neighbourBut = new Button("Neighbourhood");
		neighbourBut.addClickHandler( new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				final WebSocket ws = newWebSocket( host );
				ws.setOnopen( new EventListener() {
					@Override
					public void handleEvent(Event evt) {
						String str = null;
						Set<Sequence> sset = multisel.getSelectedSet();
						for( Sequence seq : sset ) {
							if( str == null ) str = seq.id;
							else str += ","+seq.id;
						}
						ws.send("neigh:"+str);
					}
				});
				ws.setOnmessage( new EventListener() {
					@Override
					public void handleEvent(Event evt) {
						String message = getEventData( evt );
						if( message.contains("new connection") ) {
							
						} else {
							//atob(message);
							String dataurl = "data:image/png;base64,"+message;
							final Image img = new Image( dataurl );
							final ScrollPanel sp = new ScrollPanel( img );
							final DialogBox pop = new DialogBox();
							pop.clear();
							pop.setAutoHideEnabled( true );
							
							img.addLoadHandler( new LoadHandler() {
								@Override
								public void onLoad(LoadEvent event) {
									int nw = img.getWidth()/2;
									int nh = img.getHeight()/2;
									img.setSize(nw+"px", nh+"px");
									sp.setSize(800+"px", (nh+30)+"px");
									//sp.add( img );
									pop.center();
								}
							});
							pop.add( sp );
							pop.center();
							ws.close();
						}
					}
				});
				ws.setOnerror( new EventListener() {
					@Override
					public void handleEvent(Event evt) {}
				});
			}
		});
		buttonhp.add( neighbourBut );
		
		Button statBut = new Button("Statistics");
		statBut.addClickHandler( new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				final WebSocket ws = newWebSocket( host );
				ws.setOnopen( new EventListener() {
					@Override
					public void handleEvent(Event evt) {
						String str = "";
						for( int i = 0; i < spec.getItemCount(); i++ ) {
							if( spec.isItemSelected(i) ) {
								String item = spec.getItemText(i);
								if( str.length() == 0 ) str += item;
								else str += ","+item;
							}
						}
						ws.send("stat:"+str);
					}
				});
				ws.setOnmessage( new EventListener() {
					@Override
					public void handleEvent(Event evt) {
						String message = getEventData( evt );
						if( message.contains("new connection") ) {
							
						} else {
							String htmlstr = atob(message);
							//String dataurl = "data:text/html;base64,"+message;
							HTML html = new HTML( htmlstr );
							
							ScrollPanel sp = new ScrollPanel( html );
							sp.setSize(800+"px", 600+"px");
							//sp.add( img );
							DialogBox pop = new DialogBox();
							pop.setAutoHideEnabled( true );
							pop.add( sp );
							pop.center();
							ws.close();
						}
					}
				});
				ws.setOnerror( new EventListener() {
					@Override
					public void handleEvent(Event evt) {}
				});
			}
		});
		buttonhp.add( statBut );
		
		//final Button bb = new Button();
		Button drawTree = new Button("Draw tree");
		drawTree.addClickHandler( new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				final WebSocket ws = newWebSocket( host );
				ws.setOnopen( new EventListener() {
					@Override
					public void handleEvent(Event evt) {
						String str = null;
						Set<Sequence> sset = multisel.getSelectedSet();
						for( Sequence seq : sset ) {
							if( str == null ) str = seq.id;
							else str += ","+seq.id;
						}
						ws.send("tree:"+str);
					}
				});
				ws.setOnmessage( new EventListener() {
					@Override
					public void handleEvent(Event evt) {
						String message = getEventData( evt );
						if( message.contains("new connection") ) {
							
						} else {
							treestr = message;
							//bb.click();
							//click( bb.getElement() );
							//Browser.getWindow().getConsole().log( myPopup.getLocation().getProtocol() );
							ws.close();
						}
					}
				});
				ws.setOnerror( new EventListener() {
					@Override
					public void handleEvent(Event evt) {}
				});
			}
		});
		buttonhp.add( drawTree );
		
		/*bb.setVisible(false);
		buttonhp.add( bb );
		bb.addClickHandler( new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				final String thehost = Browser.getWindow().getLocation().getHost();
				myPopup = Browser.getWindow().open("http://webconnectron.appspot.com/Treedraw.html?callback="+thehost,"TreeDraw","_blank");
			}
		});
		
		/*final DialogBox pp = new DialogBox( true );
		final VerticalPanel	vsp = new VerticalPanel();
		vsp.setSpacing( 10 );
		vsp.setHorizontalAlignment( VerticalPanel.ALIGN_CENTER );
		final ProgressElement pe = (ProgressElement)DOM.createElement("progress");
		
		pe.getStyle().setWidth(400+"px");
		pe.getStyle().setHeight(50+"px");
		
		//Widget sp = new Widget();
		final SimplePanel sp = new SimplePanel();
		sp.setSize(400+"px", 50+"px");
		sp.getElement().appendChild( (Node)pe );*/
		
		//final HTML html = HTML.wrap( pe );
		
		//final HTML html = new HTML("<progress></progress>");
		//pp.setAutoHideEnabled( true );
		
		Button geneAtlas = new Button("Gene atlas");
		geneAtlas.addClickHandler( new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Button button = new Button("Generate atlas");
				
				HorizontalPanel hp = new HorizontalPanel();
				hp.setSpacing( 10 );
				final RadioButton reference = new RadioButton("ref");
				final RadioButton noref = new RadioButton("ref");
				reference.setText("Reference");
				noref.setText("No reference");
				hp.add( reference );
				hp.add( noref );
				
				HorizontalPanel chp = new HorizontalPanel();
				chp.setSpacing( 10 );
				final RadioButton relation = new RadioButton("color");
				final RadioButton synteny = new RadioButton("color");
				relation.setText("Relation color");
				synteny.setText("Synteny color");
				chp.add( relation );
				chp.add( synteny );
				
				reference.setValue( true );
				relation.setValue( true );
				
				vsp.clear();
				//vsp.add( new Label("...") );
				//vsp.add( sp );
				
				vsp.add( hp );
				vsp.add( chp );
				vsp.add( button );
				
				pp.clear();
				//sp.getElement().appendChild( pe );
				pp.add( vsp );
				pp.center();
				
				button.addClickHandler( new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						final WebSocket ws = newWebSocket( host );
						ws.setOnopen( new EventListener() {
							@Override
							public void handleEvent(Event evt) {
								/*String str = null;
								Set<Sequence> sset = multisel.getSelectedSet();
								for( Sequence seq : sset ) {
									if( str == null ) str = seq.id;
									else str += ","+seq.id;
								}*/
								
								vsp.clear();
								vsp.add( new Label("Generating atlas ...") );
								vsp.add( sp );
								
								pp.clear();
								//sp.getElement().appendChild( pe );
								pp.add( vsp );
								pp.center();
								
								int selind = spec.getSelectedIndex();
								String specstr = selind == -1 ? "" : spec.getItemText( selind );
								if( relation.getValue() ) ws.send("geneatlas:"+specstr);
								else if( synteny.getValue() ) {
									String kegg = "";
									//asdf
									
									for( int i = 0; i < tree.getItemCount(); i++ ) {
										TreeItem ti = tree.getItem(i);
										CheckBox wcb = (CheckBox)ti.getWidget();
										if( ti.getChildCount() == 0 && wcb.getValue() ) {
											if( kegg.length() == 0 ) kegg = ti.getText();
											else kegg += ","+ti.getText();
										} else {
											String addstr = getTreeString( ti );
											if( kegg.length() == 0 ) kegg = addstr;
											else if( addstr.length() > 0 ) kegg += ","+addstr;
										}
									}
									
									if( kegg.length() > 0 ) kegg = "kegg:"+kegg;
									ws.send("syntgrad:"+specstr+kegg);
								}
							}
						});
						ws.setOnmessage( new EventListener() {
							@Override
							public void handleEvent(Event evt) {
								final String message = getEventData( evt );
								if( message.contains("new connection") ) {
									
								} else {
									//atob(message);
									String dataurl = "data:image/png;base64,"+message;
									final Image img = new Image( dataurl );
									final ScrollPanel scp = new ScrollPanel( img );
									img.addLoadHandler( new LoadHandler() {		
										@Override
										public void onLoad(LoadEvent event) {
											int nw = img.getWidth()/2;
											int nh = img.getHeight()/2;
											
											Browser.getWindow().getConsole().log("browse " + nw + "  " + nh + "  " + message);
											
											img.setSize(nw+"px", nh+"px");
											
											//scp.getElement().removeAllChildren();
											scp.setSize( Math.min(800,nw)+"px", Math.min(600,nh)+"px");
											//sp.add( img );
											//PopupPanel pop = new PopupPanel();
											pp.center();
										}
									});
									pp.clear();
									pp.add( scp );
									pp.center();
									
									ws.close();
								}
							}
						});
						ws.setOnerror( new EventListener() {
							@Override
							public void handleEvent(Event evt) {}
						});
					}
				});
			}
		});
		buttonhp.add( geneAtlas );
		
		Button antismash = new Button("Antismash");
		antismash.addClickHandler( new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				String specstr = spec.getItemText( spec.getSelectedIndex() );
				Set<String> newspecs = new HashSet<String>( Arrays.asList( new String[] {"MAT4699","MAT4721","MAT4725","MAT4726"} ) );
				if( !newspecs.contains(specstr) ) {
					specstr += "_GenBan";
				}
				Browser.getWindow().open("http://130.208.252.240/new_antismash_results/"+specstr, "_blank");
			}
		});
		buttonhp.add( antismash );
		
		Button ipath = new Button("iPath2");
		ipath.addClickHandler( new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				/*String specstr = spec.getItemText( spec.getSelectedIndex() );
				Set<String> newspecs = new HashSet<String>( Arrays.asList( new String[] {"MAT4699","MAT4721","MAT4725","MAT4726"} ) );
				if( !newspecs.contains(specstr) ) {
					specstr += "_GenBan";
				}*/
				Browser.getWindow().open("http://pathways.embl.de/iPath2.cgi", "iPath2");
				
				Set<String> ts = new TreeSet<String>();
				for( Sequence s : list ) {
					if( s.cog != null && s.cog.length() > 0 ) ts.add( s.cog );
					if( s.ec != null && s.ec.length() > 0 ) ts.add( "EC"+s.ec );
				}
				TextArea ta = new TextArea();
				String str = "";
				for( String s : ts ) {
					str += s + "\t#ff0000\n";
				}
				ta.setText( str );
				
				//ScrollPanel sp = new ScrollPanel( ta );
				ta.setSize(400+"px", 300+"px");
				//sp.add( img );
				PopupPanel pop = new PopupPanel();
				pop.setAutoHideEnabled( true );
				pop.add( ta );
				pop.center();
			}
		});
		buttonhp.add( ipath );
		
		multisel.addSelectionChangeHandler( new Handler() {
			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
				Set<Sequence> selset = multisel.getSelectedSet();
				String str = "";
				for( Sequence seq : selset ) {
					str += seq.blastresult;
				}
				results.setText(str);
			}
		});
		
		final SimplePanel spanc = new SimplePanel();
		spanc.getElement().getStyle().setTextAlign( TextAlign.RIGHT );
		final Anchor anchor = new Anchor();
		spanc.add( anchor );
		anchor.setHorizontalAlignment( HasHorizontalAlignment.ALIGN_RIGHT );
		/*anchor.getElement().getStyle().setTextAlign( com.google.gwt.dom.client.Style.TextAlign.RIGHT );
		anchor.getElement().getStyle().setBackgroundColor( "#00aaaaaa" );
		anchor.getElement().getStyle().setColor("#00aaaaff");
		//anchor.getElement().getStyle().set("#00aaaaff");*/
		greetingService.greetServer( GWT.getHostPageBaseURL(), "", new AsyncCallback<String>() {
			@Override
			public void onSuccess(String result) {
				if( result.contains("login") ) anchor.setText( "Login" );
				else anchor.setText( "Logout" );
				anchor.setHref( result );
			}
			
			@Override
			public void onFailure(Throwable caught) {}
		});
		
		HTML title = new HTML("<h2>Blast Server</h2>");
		title.addClickHandler( new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				DialogBox db = new DialogBox();
				db.setAutoHideEnabled( true );
				
				Label lb = new Label("Server url:");
				final TextBox tb = new TextBox();
				tb.setValue( host );
				
				HorizontalPanel hp = new HorizontalPanel();
				hp.setVerticalAlignment( HorizontalPanel.ALIGN_MIDDLE );
				hp.add( lb );
				hp.add( tb );
				
				db.addCloseHandler( new CloseHandler<PopupPanel>() {
					@Override
					public void onClose(CloseEvent<PopupPanel> event) {
						host = tb.getValue();
						
						reloadData( spec, click, ti, tikegg );
					}
				});
				
				db.add( hp );
				db.center();
			}
		});
		title.getElement().getStyle().setPaddingLeft(10.0, Unit.PX);
		//title.setWidth(100+"px");
		final HorizontalPanel hpa = new HorizontalPanel();
		hpa.setWidth(w+"px");
		
		final HorizontalPanel hpa1 = new HorizontalPanel();
		final HorizontalPanel hpa2 = new HorizontalPanel();
		hpa2.setWidth("100%");
		hpa1.setVerticalAlignment( HorizontalPanel.ALIGN_MIDDLE );
		hpa2.setVerticalAlignment( HorizontalPanel.ALIGN_MIDDLE );
		
		hpa2.setHorizontalAlignment( HorizontalPanel.ALIGN_RIGHT );
		hpa2.getElement().getStyle().setTextAlign( TextAlign.RIGHT );
		
		hpa.add( hpa1 );
		hpa.add( hpa2 );
		
		//hpa.setVerticalAlignment( HorizontalPanel.ALIGN_MIDDLE );
		//hpa.setHorizontalAlignment( HorizontalPanel.ALIGN_LEFT );
		//hpa.getElement().getStyle().setFloat( Float.LEFT );
		
		hpa.getElement().getStyle().setPaddingTop(10.0, Unit.PX);
		hpa.getElement().getStyle().setPaddingLeft(10.0, Unit.PX);
		hpa.getElement().getStyle().setPaddingRight(10.0, Unit.PX);
		
		hpa.getElement().getStyle().setProperty("background", "linear-gradient(gray,white)");
		//hpa.getElement().getStyle().setPaddingRight(20.0, Unit.PX);
		//hpa.setWidth(w+"px");
		//hpa.setHorizontalAlignment( HorizontalPanel.ALIGN_RIGHT );
		
		Image img = new Image( "seabiotech.png" );
		Image mimg = new Image( "matis.png" );
		mimg.setSize(64+"px", 64+"px");
		
		hpa1.add( img );
		hpa1.add( title );
		hpa2.add( mimg );
		hpa2.add( spanc );
		vp.add( hpa );
		
		vp.add( hp );
		vp.add( buttonparent );
		vp.add( table );
		//vp.add( reqButton );
		//vp.add( ta );
		//vp.add( blast );
		vp.add( results );
		
		Window.addResizeHandler( new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				int w = Window.getClientWidth();
				int h = Window.getClientHeight();
				hpa.setWidth(w+"px");
				vp.setWidth(w+"px");
				results.setWidth(w+"px");
				table.setWidth(w+"px");
				buttonparent.setWidth(w+"px");
				
				wnd.getConsole().log( anchor.getElement().getStyle().getWidth() );
			}
		});
		
		rp.add( vp );
	}
}
