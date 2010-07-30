package org.simmi;

/*
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * "The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations under
 * the License.
 *
 * The Original Code is ICEpdf 3.0 open source software code, released
 * May 1st, 2009. The Initial Developer of the Original Code is ICEsoft
 * Technologies Canada, Corp. Portions created by ICEsoft are Copyright (C)
 * 2004-2009 ICEsoft Technologies Canada, Corp. All Rights Reserved.
 *
 * Contributor(s): _____________________.
 *
 * Alternatively, the contents of this file may be used under the terms of
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"
 * License), in which case the provisions of the LGPL License are
 * applicable instead of those above. If you wish to allow use of your
 * version of this file only under the terms of the LGPL License and not to
 * allow others to use your version of this file under the MPL, indicate
 * your decision by deleting the provisions above and replace them with
 * the notice and other provisions required by the LGPL License. If you do
 * not delete the provisions above, a recipient may use your version of
 * this file under either the MPL or the LGPL License."
 *
 */

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.icepdf.core.pobjects.graphics.text.LineText;
import org.icepdf.core.pobjects.graphics.text.PageText;
import org.icepdf.core.pobjects.graphics.text.WordText;
import org.icepdf.core.views.DocumentView;
import org.icepdf.core.views.DocumentViewModel;
import org.icepdf.core.views.swing.AbstractPageViewComponent;
import org.icepdf.ri.common.SwingController;
import org.icepdf.ri.common.SwingViewBuilder;
import org.icepdf.ri.common.views.DocumentViewControllerImpl;

/**
 * <p>Use this applet on your site to launch the PDF Viewer in a browser.</p>
 *
 * <p>A sample HTML applet tag for starting this class:</p>
 *
 * <pre>
 *      &lt;applet
 *          width="640"
 *          height="480"
 *          code="examples.applet.ViewerApplet.class"
 *          archive="icepdf.jar, ri_pdf.jar"
 *          alt="whatever"&gt;
 *              &lt;param
 *              name="url"
 *              value="http://www.icesoft.com/products/ICEpdf.pdf"&gt;
 *      &lt;/applet&gt;
 * </pre>
 *
 * <p><b>Note:</b><br/>
 * If you would like to load none local URLs, this class will have to
 * be added to a signed jar.</p>
 *
 * @since 1.0
 */
public class ViewerApplet extends JApplet {
	GiskPanel	giskPanel;
    private static final Logger logger = Logger.getLogger(ViewerApplet.class.toString());
    SwingController controller;
    JSplitPane	splitpane;

    /**
     * Creates an Applet which contains the default viewer.
     */
    public void init() {
    	try {
			initGui( this );
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
    	
    	Window window = SwingUtilities.windowForComponent(this);
		if (window instanceof JFrame) {
			JFrame frame = (JFrame) window;
			if (!frame.isResizable())
				frame.setResizable(true);
		}
    	
    	add( splitpane );
    }
    
    public void checkKeyListeners( JScrollPane scroll, KeyListener key ) {
    	Component comp = scroll.getViewport().getView();
		if( comp != null ) {
			boolean b = false;
			for( KeyListener kl : comp.getKeyListeners() ) {
				if( kl == key ) b = true;
			}
			if( !b ) comp.addKeyListener( key );
		}
		
		java.util.List<AbstractPageViewComponent> pages = controller.getDocumentViewController().getDocumentViewModel().getPageComponents();
        for (AbstractPageViewComponent page : pages) {
        	if( page != null ) {
        		boolean b = false;
				for( KeyListener kl : page.getKeyListeners() ) {
					if( kl == key ) b = true;
				}
				if( !b ) page.addKeyListener( key );
        	}
        }
    }
    
    public void next() {
    	boolean boo = false;
		for( JTextField field : giskPanel.fields ) {
			if( boo && field.isShowing() ) {
				field.requestFocus();
				giskPanel.scrollCompToVis( field.getBounds() );
				break;
			}

			if( field == giskPanel.lastField ) {
				boo = true;
			}
		}
		if( !boo ) {
			giskPanel.lastField = giskPanel.fields[0];
			giskPanel.lastField.requestFocus();
		}
    }
    
    public void initGui( final Container cont ) throws SQLException, ClassNotFoundException {
    	try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
		giskPanel = new GiskPanel( cont );
		
        // Open a url if available
        /*URL documentURL = null;
        String url = getParameter("url");
        if (url == null || url.length() == 0){
            url = "file:///home/sigmar/hafsjor/gogn/arsreikningar_2/Deloitte/Bergur ehf 2008.pdf";
        }
        // resolve the url
        try{
            documentURL = new URL(url);
        }catch(MalformedURLException e){
            e.printStackTrace();
        }*/

        // create a controller and a swing factory
        controller = new SwingController();
        SwingViewBuilder factory = new SwingViewBuilder( controller );
        // add interactive mouse link annotation support via callback
        controller.getDocumentViewController().setAnnotationCallback( new org.icepdf.ri.common.MyAnnotationCallback(controller.getDocumentViewController()));

        giskPanel.filelist.addListSelectionListener( new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				try {
					controller.openDocument( giskPanel.getSelectedFile().toURI().toURL() );
					//giskPanel
					giskPanel.filelist.requestFocus();
					giskPanel.refreshForm();
					giskPanel.current.skranafn = giskPanel.getSelectedFile().getName();
					giskPanel.current.index = giskPanel.filelist.getSelectedIndex();
				} catch (MalformedURLException e1) {
					e1.printStackTrace();
				} catch (SQLException e1) {
					e1.printStackTrace();
				} catch (ClassNotFoundException e1) {
					e1.printStackTrace();
				}
			}
		});
        
        final JPanel	viewerpanel = factory.buildViewerPanel();
        final JScrollPane	scroll = (JScrollPane)controller.getDocumentViewController().getViewContainer();
        KeyAdapter key = new KeyAdapter() {
        	public void keyPressed( KeyEvent e ) {
        		int code = e.getKeyCode();
        		boolean ctrl = (e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) > 0;
        		DocumentView documentView = ((DocumentViewControllerImpl)controller.getDocumentViewController()).getDocumentView();
        		if( code == KeyEvent.VK_F2 ) {
        			checkKeyListeners( scroll, this );
        			controller.goToDeltaPage(documentView.getNextPageIncrement());
        	 		//controller.getDocumentViewController().setCurrentPageNext();
        		} else if( code == KeyEvent.VK_F1 ) {
        			checkKeyListeners( scroll, this );
        			controller.goToDeltaPage(-documentView.getPreviousPageIncrement());
        		} else if( code == KeyEvent.VK_F9 || code == KeyEvent.VK_F8 || (code == KeyEvent.VK_9 && ctrl) ) {
        			checkKeyListeners( scroll, this );
        	        
        			if( cont instanceof JFrame ) {
        				Component comp1 = ((JFrame)cont).getFocusOwner();
        				System.err.println( comp1 );
        			}
        			String seltext = controller.getDocumentViewController().getSelectedText();
        			
        			if( seltext == null || seltext.length() == 0 ) {
        				StringBuilder selectedText = new StringBuilder();
        				int pi = controller.getDocumentViewController().getCurrentPageIndex();
        				PageText pt = controller.getDocument().getPageText(pi);
	                    selectedText.append(pt.getSelected());
	                    
	                    seltext = selectedText.toString();
        			}
        			
        			seltext = seltext.replace(".", "");
        			
        			int i;
        			for( i = 0; i < seltext.length(); i++ ) {
        				char c = seltext.charAt( i );
        				if( c >= '0' && c <= '9' ) break;
        			}
        			
        			if( i < seltext.length() ) {
        				String[] uh = seltext.split("\n");
        				seltext = uh[0];
        			}
        			giskPanel.copyTextToFocus( seltext.trim() );
        			
        			next();
        		} else if( code == KeyEvent.VK_F3 ) {
        			JTextField prevfield = null;
        			for( JTextField field : giskPanel.fields ) {
        				if( field == giskPanel.lastField ) {
        					if( prevfield != null ) {
        						prevfield.requestFocus();
        						giskPanel.scrollCompToVis( prevfield.getBounds() );
        					}
            				break;
        				}
        				
        				prevfield = field;
        			}
        		} else if( code == KeyEvent.VK_F4 ) {
        			next();
        		} else if( code == KeyEvent.VK_F5 ) {
        			checkKeyListeners( scroll, this );
        			
        			boolean shift = (e.getModifiersEx() & KeyEvent.SHIFT_DOWN_MASK) > 0;
        			DocumentViewModel documentViewModel = controller.getDocumentViewController().getDocumentViewModel();
        			if (!documentViewModel.isSelectAll()) {
        				int pi = controller.getDocumentViewController().getCurrentPageIndex();
        				
        	            ArrayList<WeakReference<AbstractPageViewComponent>> selectedPages = documentViewModel.getSelectedPageText();
        	            if (selectedPages != null && selectedPages.size() > 0) {
        	                for (WeakReference<AbstractPageViewComponent> page : selectedPages) {
        	                    AbstractPageViewComponent pageComp = page.get();
        	                    if (pageComp != null) {
        	                        int pageIndex = pageComp.getPageIndex();
        	                        if( pageIndex != pi ) {
        	                        	PageText pt = controller.getDocument().getPageText(pageIndex);
        	                        	pt.clearSelected();
        	                        } else {
        	                        	//controller.getDocumentViewController().getDocumentViewModel().
        	                        	//.addSelectedPageText( controller.getDocumentViewController().getDocumentViewModel().getPageComponents().get(pi) );
        	                        }
        	                    }
        	                }
        	            }
        	                        
    	            	PageText pt = controller.getDocument().getPageText(pi);
                        
                        boolean b = false;
                        WordText lastword = null;
                        for( Object	o : pt.getPageLines() ) {
                        	LineText lt = (LineText)o;
                        	if( b ) {
                        		boolean done = false;
                        		for( Object ow : lt.getWords() ) {
                        			WordText wt = (WordText)ow;
                        			if( !wt.isSelected() ) {
                        				if( lastword != null ) {
                        					if( lastword.isSelected() ) lastword.clearSelected();
                            				else lastword.selectAll();
                        				}
                        				done = true;
                        				break;
                        			}
                        		}
                        		if( done ) break;
                    		}
                        	
                        	boolean done = false;
                        	for( Object ow : lt.getWords() ) {
                        		WordText wt = (WordText)ow;
                        		if( b && !wt.isSelected() ) {
                        			if( lastword != null ) {
                        				if( lastword.isSelected() ) lastword.clearSelected();
                        				else lastword.selectAll();
                        			}
                        			done = true;
                        			break;
                        		}
                        		if( wt.isSelected() ) {
                        			if( !shift ) {
                        				wt.clearSelected();
                        			}
                        			if( b ) {
                        				lastword = wt;
                        			}
                        			b = true;
                        		} else {
                        			wt.clearSelected();
                        			lastword = wt;
                        		}
                        	}
                        	if( done ) break;
                        }
                        
                        viewerpanel.repaint();
                        scroll.repaint();
        			}
        		} else if( code == KeyEvent.VK_F6 ) {
        			checkKeyListeners( scroll, this );
        			
        			boolean shift = (e.getModifiersEx() & KeyEvent.SHIFT_DOWN_MASK) > 0;
        			DocumentViewModel documentViewModel = controller.getDocumentViewController().getDocumentViewModel();
        			if (!documentViewModel.isSelectAll()) {
        				int pi = controller.getDocumentViewController().getCurrentPageIndex();
        				
        	            ArrayList<WeakReference<AbstractPageViewComponent>> selectedPages = documentViewModel.getSelectedPageText();
        	            if (selectedPages != null && selectedPages.size() > 0) {
        	                for (WeakReference<AbstractPageViewComponent> page : selectedPages) {
        	                    AbstractPageViewComponent pageComp = page.get();
        	                    if (pageComp != null) {
        	                        int pageIndex = pageComp.getPageIndex();
        	                        if( pageIndex != pi ) {
        	                        	PageText pt = controller.getDocument().getPageText(pageIndex);
        	                        	pt.clearSelected();
        	                        } else {
        	                        	//controller.getDocumentViewController().getDocumentViewModel().
        	                        	//.addSelectedPageText( controller.getDocumentViewController().getDocumentViewModel().getPageComponents().get(pi) );
        	                        }
        	                    }
        	                }
        	            }
        	                        
    	            	PageText pt = controller.getDocument().getPageText(pi);
                        
                        boolean b = false;
                        boolean noselected = true;
                        for( Object o : pt.getPageLines() ) {
                        	LineText	lt = (LineText)o;
                        	if( b ) {
                        		boolean done = false;
                        		for( Object ow : lt.getWords() ) {
                        			WordText wt = (WordText)ow;
                        			if( !wt.isSelected() ) {
                        				wt.selectAll();
                        				noselected = false;
                        				done = true;
                        				break;
                        			}
                        		}
                        		if( done ) break;
                    		}
                        	
                        	boolean done = false;
                        	for( Object ow : lt.getWords() ) {
                        		WordText wt = (WordText)ow;
                        		if( b && !wt.isSelected() ) {
                        			wt.selectAll();
                        			noselected = false;
                        			done = true;
                        			break;
                        		}
                        		System.err.println(wt.getText());
                        		if(  wt.isSelected() ) {
                        			//wt.setHighlighted( false );
                        			if( !shift ) {
                        				wt.clearSelected();
                        			}
                        			b = true;
                        		} else {
                        			wt.clearSelected();
                        		}
                        	}
                        	if( done ) break;
                        }
                        
                        if( noselected ) {
                        	 for( Object o : pt.getPageLines() ) {
                        		 LineText lt = (LineText)o;
                        		 for( Object ow : lt.getWords() ) {
                        			 WordText wt = (WordText)ow;
                        			 wt.selectAll();
                        			 break;
                        		 }
                        		 break;
                        	 }
                        	 AbstractPageViewComponent abpvc = (AbstractPageViewComponent)controller.getDocumentViewController().getDocumentViewModel().getPageComponents().get(pi);
                        	 controller.getDocumentViewController().getDocumentViewModel().clearSelectedPageText();
                        	 controller.getDocumentViewController().getDocumentViewModel().addSelectedPageText( abpvc );
                        }
                        
                        viewerpanel.repaint();
                        scroll.repaint();
                    }
                }
            } /*else {
            	int pi = controller.getDocumentViewController().getCurrentPageIndex();
            	PageText pt = controller.getDocument().getPageText(pi);
            	for( LineText	lt : pt.getPageLines() ) {
               		 for( WordText wt : lt.getWords() ) {
               			 wt.selectAll();
               			 break;
               		 }
               		 lt.setHasSelected( true );
               		 break;
           	 	}
            	controller.getDocumentViewController().getDocumentViewModel().addSelectedPageText( controller.getDocumentViewController().getDocumentViewModel().getPageComponents().get(pi) );
            	viewerpanel.repaint();
                scroll.repaint();
            }*/
        };
        cont.addKeyListener( key );
        giskPanel.addKeyListener( key );
        giskPanel.filelist.addKeyListener( key );
        
        for( JTextField field : giskPanel.fields ) {
        	field.addKeyListener( key );
        }
        
        // build viewer component and add it to the applet content pane.
        
        splitpane = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT );
        splitpane.setLeftComponent( giskPanel );
        splitpane.setRightComponent( viewerpanel );
        splitpane.setOneTouchExpandable( true );
        
        Dimension d = new Dimension(0,0);
        viewerpanel.setPreferredSize( d );
        viewerpanel.setMinimumSize( d );
        
        splitpane.addKeyListener( key );        
        viewerpanel.addKeyListener( key );
        
        giskPanel.splitpane.addKeyListener( key );
        giskPanel.scrollpane.addKeyListener( key );
        
        scroll.addKeyListener( key );
        scroll.getViewport().addKeyListener( key );
        
        //scroll.getViewport().getView().addKeyListener( key );
        //add( giskPanel, BorderLayout.WEST );
        //add( factory.buildViewerPanel() );
    }

    /**
     * Dispose of the document.
     */
    public void destroy() {
        if (controller != null) {
            controller.dispose();
            controller = null;
        }
        getContentPane().removeAll();
    }
    
    public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		frame.setSize( 800,600 );
		
		ViewerApplet va = new ViewerApplet();
		try {
			va.initGui( frame );
			frame.add( va.splitpane );
			frame.setVisible( true );
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}
