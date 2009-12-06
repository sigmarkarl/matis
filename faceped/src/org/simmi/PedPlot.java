package org.simmi;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JApplet;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.netbeans.saas.RestResponse;
import org.netbeans.saas.facebook.FacebookSocialNetworkingService;
import org.netbeans.saas.facebook.FacebookSocialNetworkingServiceAuthenticator;

import facebook.socialnetworkingservice.facebookresponse.User;

public class PedPlot extends JApplet implements MouseListener, MouseMotionListener {
	JScrollPane	scrollpane = new JScrollPane();
	JPanel		panel = new JPanel() {
		public void paintComponent( Graphics g ) {
			super.paintComponent(g);
			
			Graphics2D	g2 = (Graphics2D)g;
			g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
			g2.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
			
			for( Component c : this.getComponents() ) {
				if( c instanceof PersonComponent ) {
					PersonComponent person = (PersonComponent)c;
					int strWidth = g.getFontMetrics().stringWidth( person.name );
					g.drawString( person.name, c.getX()+(c.getWidth()-strWidth)/2, c.getY()+c.getHeight()+10 );
					
					g.setColor( Color.darkGray );
					for( PersonComponent child : person.getChildren() ) {						
						g.drawLine( person.getX()+person.getWidth()/2, person.getY()+person.getHeight()/2, child.getX()+child.getWidth()/2, child.getY()+child.getHeight()/2 );
					}
				}
			}
			
			PersonComponent pc = PersonComponent.drag;
			if( pc != null && pc.p != null ) {
				g2.drawLine( pc.getX()+pc.getWidth()/2, pc.getY()+pc.getHeight()/2, pc.getX()+pc.p.x, pc.getY()+pc.p.y );
			}
			
			if( selRect != null ) {
				g2.setColor( selColor );
				g2.fillRect( selRect.x, selRect.y, selRect.width, selRect.height );
			}
		}
	};
	JPopupMenu	popup = new JPopupMenu();
	Point	mouseLoc = new Point(0,0);
	Rectangle selRect = null;
	
	static Color selColor = new Color( 0,128,255,32 );
	
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public PedPlot() throws HeadlessException {
    	super();
    	try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
			SwingUtilities.updateComponentTreeUI( this );
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

    public void init() {
        this.add( initgui() );
        
        FacebookSocialNetworkingServiceAuthenticator.apiKey = "c16dc113d6f8353b42b099736eb1566c";
        FacebookSocialNetworkingServiceAuthenticator.secret = "62a7e6844a9f52bf926f35a500583c06";
        FacebookSocialNetworkingServiceAuthenticator.sessionKey = this.getParameter("fb_sig_session_key");
        FacebookSocialNetworkingServiceAuthenticator.sessionSecret = FacebookSocialNetworkingServiceAuthenticator.secret;
        
        System.err.println( FacebookSocialNetworkingServiceAuthenticator.apiKey + "   " + FacebookSocialNetworkingServiceAuthenticator.sessionKey + "   " + FacebookSocialNetworkingServiceAuthenticator.secret );
        
        if( FacebookSocialNetworkingServiceAuthenticator.secret != null ) {
        	initfacebook();
        }
    }

    public void initfacebook() {
    	new Thread() {
    		public void run() {
		        try {
		            String format = null;
		            String flid = null;
		
		            List<Long>	uids = null;
		            RestResponse result = FacebookSocialNetworkingService.friendsGet(format, flid);
		            if (result.getDataAsObject(facebook.socialnetworkingservice.facebookresponse.FriendsGetResponse.class) instanceof facebook.socialnetworkingservice.facebookresponse.FriendsGetResponse) {
		                facebook.socialnetworkingservice.facebookresponse.FriendsGetResponse resultObj = result.getDataAsObject(facebook.socialnetworkingservice.facebookresponse.FriendsGetResponse.class);
		                uids = resultObj.getUid();
		            //resultObj.get
		            } else if (result.getDataAsObject(facebook.socialnetworkingservice.facebookresponse.ErrorResponse.class) instanceof facebook.socialnetworkingservice.facebookresponse.ErrorResponse) {
		                facebook.socialnetworkingservice.facebookresponse.ErrorResponse resultObj = result.getDataAsObject(facebook.socialnetworkingservice.facebookresponse.ErrorResponse.class);
		            }
		            //TODO - Uncomment the print Statement below to print result.
		            //System.out.println("The SaasService returned: " + result.getDataAsString());
		
		            /*String myUid = "";
		            try {
		                String format3 = null;
		
		                RestResponse result3 = FacebookSocialNetworkingService.usersGetLoggedInUser(format3);
		                if (result3.getDataAsObject(facebook.socialnetworkingservice.facebookresponse.UsersGetLoggedInUserResponse.class) instanceof facebook.socialnetworkingservice.facebookresponse.UsersGetLoggedInUserResponse) {
		                    facebook.socialnetworkingservice.facebookresponse.UsersGetLoggedInUserResponse result3Obj = result3.getDataAsObject(facebook.socialnetworkingservice.facebookresponse.UsersGetLoggedInUserResponse.class);
		                    myUid = Long.toString( result3Obj.getValue() );
		                } else if (result3.getDataAsObject(facebook.socialnetworkingservice.facebookresponse.ErrorResponse.class) instanceof facebook.socialnetworkingservice.facebookresponse.ErrorResponse) {
		                    facebook.socialnetworkingservice.facebookresponse.ErrorResponse result3Obj = result3.getDataAsObject(facebook.socialnetworkingservice.facebookresponse.ErrorResponse.class);
		                }
		                //TODO - Uncomment the print Statement below to print result.
		                //System.out.println("The SaasService returned: " + result3.getDataAsString());
		            } catch (Exception ex) {
		                ex.printStackTrace();
		            }*/
		
		            List<User> users = new ArrayList<User>();
		            String uidStr = "";
		            for( long uid : uids ) {
		            	uidStr += Long.toString(uid)+",";
		            }
		            uidStr = uidStr.substring(0, uidStr.length()-1);
		            String fields = "first_name,pic,sex";
		
		            result = FacebookSocialNetworkingService.usersGetinfo(uidStr, fields, format);
		            /*if (result.getDataAsObject(facebook.socialnetworkingservice.facebookresponse.UsersGetInfoResponse.class) instanceof facebook.socialnetworkingservice.facebookresponse.UsersGetInfoResponse) {
		                facebook.socialnetworkingservice.facebookresponse.UsersGetInfoResponse resultObj = result.getDataAsObject(facebook.socialnetworkingservice.facebookresponse.UsersGetInfoResponse.class);
		                users = resultObj.getUser();
		            } else if (result.getDataAsObject(facebook.socialnetworkingservice.facebookresponse.ErrorResponse.class) instanceof facebook.socialnetworkingservice.facebookresponse.ErrorResponse) {
		                facebook.socialnetworkingservice.facebookresponse.ErrorResponse resultObj = result.getDataAsObject(facebook.socialnetworkingservice.facebookresponse.ErrorResponse.class);
		            }*/
		        //TODO - Uncomment the print Statement below to print result.
		            String resultStr = result.getDataAsString();
		            System.out.println("The SaasService returned: "+resultStr);
		            
		            int k = 0;
		            int start = resultStr.indexOf("<user>");
		            while( start != -1 ) {
		            	int end = resultStr.indexOf("</user>",start);
		            	
		            	int i = resultStr.indexOf("<first_name>",start);
		            	i+=12;
		            	int i2 = resultStr.indexOf("</first_name>",i);
	            		String name = resultStr.substring( i, i2 );
	            		i = resultStr.indexOf("<pic>",i2);
	            		i+=5;
	            		i2 = resultStr.indexOf("</pic>", i);
	            		String url = resultStr.substring( i,i2 );
	            		i = resultStr.indexOf("<sex>",i2);
	            		i+=5;
	            		i2 = resultStr.indexOf("</sex>", i);
	            		String sex = "";
	            		if( i < end && i2 < end ) sex = resultStr.substring( i,i2 );
	            		
	            		PersonComponent person = new PersonComponent( name, sex.equals("male") ? true : false, new Point(k++*PersonComponent.size,0), url );
	            		panel.add( person );
	            		panel.repaint();
		            	//}
		            	//User user = new User();
		            	//user.setPic( new JAXBElement<String>() );
		            	//users.add( user );
		            	
		            	start = resultStr.indexOf("<user>", end);
		            }
		
		            /*for( User user : users ) {
		            	Image img = ImageIO.read( new URL(user.getPic().getValue()) );
		            	PersonComponent person = new PersonComponent( user.getFirstName(), new Point(0,0) );
		            	person.setImage( img );
		            	this.add( person );
		            }*/
		            
		        /*try {
		        String uid = "";
		        String aids = "";
		        String format2 = null;
		
		        RestResponse result2 = FacebookSocialNetworkingService.photosGetAlbums(uid, aids, format2);
		        if (result2.getDataAsObject(facebook.socialnetworkingservice.facebookresponse.PhotosGetAlbumsResponse.class) instanceof facebook.socialnetworkingservice.facebookresponse.PhotosGetAlbumsResponse) {
		        facebook.socialnetworkingservice.facebookresponse.PhotosGetAlbumsResponse result2Obj = result2.getDataAsObject(facebook.socialnetworkingservice.facebookresponse.PhotosGetAlbumsResponse.class);
		        } else if (result2.getDataAsObject(facebook.socialnetworkingservice.facebookresponse.ErrorResponse.class) instanceof facebook.socialnetworkingservice.facebookresponse.ErrorResponse) {
		        facebook.socialnetworkingservice.facebookresponse.ErrorResponse result2Obj = result2.getDataAsObject(facebook.socialnetworkingservice.facebookresponse.ErrorResponse.class);
		        }
		        //TODO - Uncomment the print Statement below to print result.
		        System.out.println("The SaasService returned: "+result2.getDataAsString());
		        } catch (Exception ex) {
		        ex.printStackTrace();
		        }
		
		        try {
		        String subjId = "";
		        String aid = "";
		        String pids = "";
		        String format1 = null;
		
		        RestResponse result1 = FacebookSocialNetworkingService.photosGet(subjId, aid, pids, format1);
		        if (result1.getDataAsObject(facebook.socialnetworkingservice.facebookresponse.PhotosGetResponse.class) instanceof facebook.socialnetworkingservice.facebookresponse.PhotosGetResponse) {
		        facebook.socialnetworkingservice.facebookresponse.PhotosGetResponse result1Obj = result1.getDataAsObject(facebook.socialnetworkingservice.facebookresponse.PhotosGetResponse.class);
		        } else if (result1.getDataAsObject(facebook.socialnetworkingservice.facebookresponse.ErrorResponse.class) instanceof facebook.socialnetworkingservice.facebookresponse.ErrorResponse) {
		        facebook.socialnetworkingservice.facebookresponse.ErrorResponse result1Obj = result1.getDataAsObject(facebook.socialnetworkingservice.facebookresponse.ErrorResponse.class);
		        }
		        //TODO - Uncomment the print Statement below to print result.
		        System.out.println("The SaasService returned: "+result1.getDataAsString());
		        } catch (Exception ex) {
		        ex.printStackTrace();
		        }*/
		
		        } catch (Exception ex) {
		            ex.printStackTrace();
		        }
    		}
    	}.start();
    	System.err.println("starting facebook thread");
    }

    Point p;
    public JComponent initgui() {
    	panel.setBackground( Color.white );
    	panel.setLayout( null );
        panel.setPreferredSize(new Dimension(1000, 1000));
        panel.addMouseListener( this );
        panel.addMouseMotionListener( this );
        scrollpane.setViewportView( panel );
        
        /*panel.addMouseListener( new MouseAdapter() {
        	public void mousePressed( MouseEvent e ) {
        		p = e.getPoint();
        	}
        });*/
        
        popup.add( new AbstractAction("Add Female") {
			public void actionPerformed(ActionEvent e) {
				panel.add( new PersonComponent( "eve", false, mouseLoc, null ) );
				panel.repaint();
			}
        });
        popup.add( new AbstractAction("Add Male") {
			public void actionPerformed(ActionEvent e) {
				panel.add( new PersonComponent( "adam", true, mouseLoc, null ) );
				panel.repaint();
			}
        });
        panel.setComponentPopupMenu(popup);
        
        return scrollpane;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        PedPlot pedplot = new PedPlot();
        JFrame frame = new JFrame("");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add( pedplot.initgui() );
        pedplot.initfacebook();
        frame.setVisible(true);
    }

	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mousePressed(MouseEvent e) {
		p = e.getPoint();
		PersonComponent.selectedList.clear();
		for( Component c : panel.getComponents() ) {
			if( c instanceof PersonComponent ) {
				((PersonComponent)c).selected = false;
			}
		}
		panel.repaint();
	}

	public void mouseReleased(MouseEvent e) {
		if( selRect != null ) {
			for( Component c : panel.getComponents() ) {
				if( c instanceof PersonComponent ) {
					PersonComponent pc = (PersonComponent)c;
					if( pc.getBounds().intersects(selRect) ) {
						pc.selected = true;
						PersonComponent.selectedList.add(pc);
					}
					else pc.selected = false;
				}
			}
			selRect = null;
		}
		panel.repaint();
	}

	public void mouseDragged(MouseEvent e) {
		Point np = e.getPoint();
		if( e.isShiftDown() ) {
			Rectangle rect = panel.getVisibleRect();			
			rect.translate( p.x-np.x, p.y-np.y );
			panel.scrollRectToVisible( rect );
		} else {
			selRect = new Rectangle( Math.min(p.x, np.x), Math.min(p.y, np.y), Math.abs(p.x-np.x), Math.abs(p.y-np.y) );
			panel.repaint();
		}
		//p = np;
	}

	public void mouseMoved(MouseEvent e) {
		mouseLoc = e.getPoint();		
	}
}
