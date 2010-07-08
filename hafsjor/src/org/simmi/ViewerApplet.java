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
import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.MalformedURLException;
import java.sql.SQLException;
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

import org.icepdf.core.views.DocumentView;
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
    	initGui( this );
    	
    	Window window = SwingUtilities.windowForComponent(this);
		if (window instanceof JFrame) {
			JFrame frame = (JFrame) window;
			if (!frame.isResizable())
				frame.setResizable(true);
		}
    	
    	add( splitpane );
    }
    
    public void initGui( Container cont ) {
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
		
		giskPanel = new GiskPanel();
		
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
				} catch (MalformedURLException e1) {
					e1.printStackTrace();
				} catch (SQLException e1) {
					e1.printStackTrace();
				} catch (ClassNotFoundException e1) {
					e1.printStackTrace();
				}
			}
		});
        
        final JScrollPane	scroll = (JScrollPane)controller.getDocumentViewController().getViewContainer();
        KeyAdapter key = new KeyAdapter() {
        	public void keyPressed( KeyEvent e ) {
        		int code = e.getKeyCode();
        		DocumentView documentView = ((DocumentViewControllerImpl)controller.getDocumentViewController()).getDocumentView();
        		if( code == KeyEvent.VK_F1 ) {
        			controller.goToDeltaPage(documentView.getNextPageIncrement());
        	 		//controller.getDocumentViewController().setCurrentPageNext();
        		} else if( code == KeyEvent.VK_F2 ) {
        			controller.goToDeltaPage(-documentView.getPreviousPageIncrement());
        		} else if( code == KeyEvent.VK_F3 ) {
        			Component comp = scroll.getViewport().getView();
        			if( comp != null ) {
        				comp.removeKeyListener( this );
        				comp.addKeyListener( this );
        			}
        			
        			java.util.List<AbstractPageViewComponent> pages = controller.getDocumentViewController().getDocumentViewModel().getPageComponents();
        	        for (AbstractPageViewComponent page : pages) {
        	        	if( page != null ) {
        	        		page.removeKeyListener( this );
        	        		page.addKeyListener( this );
        	        	}
        	        }
        			
        			String seltext = controller.getDocumentViewController().getSelectedText();
        			giskPanel.copyTextToFocus( seltext.trim() );
        		} else if( code == KeyEvent.VK_F4 ) {
        			boolean boo = false;
        			for( JTextField field : giskPanel.fields ) {
        				if( boo ) {
        					field.requestFocus();
        					giskPanel.scrollCompToVis( field.getBounds() );
            				break;
            			}

        				if( field == giskPanel.lastField ) {
        					boo = true;
        				}
        			}
        		}
        	}
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
        
        JPanel	viewerpanel = factory.buildViewerPanel();
        viewerpanel.addKeyListener( key );
        
        scroll.addKeyListener( key );
        scroll.getViewport().addKeyListener( key );
        
        //scroll.getViewport().getView().addKeyListener( key );
        
        splitpane.setRightComponent( viewerpanel );
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
		va.initGui( frame );
		frame.add( va.splitpane );
		
		frame.setVisible( true );
	}
}
