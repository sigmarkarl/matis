package org.simmi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.tika.Tika;
import org.apache.tika.config.TikaConfig;

public class ArsParse {
	static Connection 		con;
	
	public static Connection connect() throws SQLException, ClassNotFoundException {
		if( con == null || con.isClosed() ) {
			con =  Uthlutun.connect();
		}
		return con;
	}
	
	static int fcount = 0;
	public static void sub( Tika t, File fl ) throws SQLException, ClassNotFoundException, IOException {	
		File[] files = fl.listFiles( new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				if( pathname.getName().endsWith(".pdf") || pathname.getName().endsWith(".PDF") || pathname.isDirectory() ) return true;
				return false;
			}
		});
		
		String	sql = "insert into [hafsjor].[dbo].[ars_temp] ([f_heiti],[f_kt],[e_heiti],[e_kt],[ef_heiti],[ef_kt],[ath],[artal],[skra_nafn],[path],[hagn],[hagnfyrr],[eign],[eignfyrr]) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)"; // where heiti = 'Sæfari'";
		PreparedStatement 	ps = connect().prepareStatement(sql);
		
		int ecount = 0;
		for( File f : files ) {
			if( f.isDirectory() ) sub( t, f );
			else /*if( f.getName().startsWith("Vestri") )*/ {
				fcount++;
				
				ps.setString(1, "");
				ps.setString(2, "");
				ps.setString(3, "");
				ps.setString(4, "");
				ps.setString(5, "");
				ps.setString(6, "");
				ps.setString(7, "");
				ps.setString(8, "");
				ps.setString(9, "");
				ps.setString(10, "");
				ps.setString(11, "");
				ps.setString(12, "");
				ps.setString(13, "");
				ps.setString(14, "");
				
				String 	fname = f.getName().substring(0, Math.min(50, f.getName().length()));
				String	parname = f.getParentFile().getName();
				parname = parname.substring(0, Math.min(50,parname.length()) );
				Reader 		rd = t.parse( f );
			
				boolean inside = false;
				boolean inarit = false;
				boolean inhagn = false;
				boolean ineign = false;
				int passtskyr = 0;
				
				BufferedReader br = new BufferedReader( rd );
				String line = null;
				try {
					line = br.readLine();
				} catch( Exception e ) {
					e.printStackTrace();
				}
				
				if( line == null ) {
					inside = true;
					ps.setString(7, "io error");
				}
				
				while( line != null ) {
					line = line.trim();
					String woSpace = line.toLowerCase().replace(" ", "");
					if( woSpace.startsWith("uppl") && !inside ) {						
						inside = true;						
						line = br.readLine();
						line = br.readLine().trim();
						woSpace = line.toLowerCase().replace(" ", "");
						String[] split = line.split("[\t ]+");
						
						String fkt = "";
						String fheiti = "";
						if( split[0].length() < 4 ) {
							if( woSpace.length() > 11 ) {
								fkt = woSpace.substring(0,11);
								fheiti = woSpace.substring(11);
							}
						} else {
							fkt = split[0];
							if( split.length > 1 ) {
								fheiti = split[1];
								for( int i = 2; i < split.length; i++ ) {
									fheiti += " "+split[i];
								}
							}
						}
						
						fheiti = fheiti.substring(0, Math.min(50, fheiti.length()));
						fheiti = fheiti.replace("ehf", " ehf");
						fkt = fkt.substring(0, Math.min(50, fkt.length()));
						ps.setString(1, fheiti);
						ps.setString(2, fkt);
						
						String ar = "";
						while( !woSpace.startsWith("reikningsár") ) {
							try {
								line = br.readLine();
								woSpace = line.toLowerCase().replace(" ", "");
							} catch( Exception e ) {
								System.err.println("simmi3");
								line = null;
							}
							if( line != null ) {
								line = line.trim();
							} else break;
						}
						
						if( line == null ) {
							ecount++;
							break;
						}
						String next = br.readLine();
						
						String efheiti = "";
						String efkt = "";
						
						int c = 0;
						while( line != null && !woSpace.startsWith("endursk") && c < 10 ) {								
							line = br.readLine();
							woSpace = line.toLowerCase().replace(" ", "");
							c++;
						}
						
						if( line != null ) {
							line = br.readLine();
						}
						
						if( line != null ) {
							line = br.readLine();
						}
						
						if( line != null ) {
							line = line.trim();
							woSpace = line.toLowerCase().replace(" ", "");
							split = line.split("[\t ]+");
							
							if( split[0].length() < 10 && woSpace.length() > 10 ) {
								if( woSpace.charAt(6) == '-' ) {
									efkt = woSpace.substring(0,11);
									efheiti = woSpace.substring(11);
								} else {
									efkt = woSpace.substring(0,10);
									efheiti = woSpace.substring(10);
								}
								efheiti = efheiti.replace("ehf", " ehf");
							} else {
								if( split.length > 1 ) {
									String kt = split[0];
									String heiti = split[1];
									for( int i = 2; i < split.length; i++ ) {
										heiti += " "+split[i];
									}
									efheiti = heiti;
									efkt = kt;
								}
							}
							
							efheiti = efheiti.substring(0, Math.min(50, efheiti.length()));
							efkt = efkt.substring(0, Math.min(50, efkt.length()));
						}
						
						if( next != null ) {
							next = next.trim();
							String nwoSpace = next.replace(" ", "");
							String[] wspl = next.split("[\t ]+");
							if( wspl[0].length() < 2 ) {
								ar = nwoSpace;
							} else {
								ar = wspl[0];
							}
						}
						
						//if( efheiti.length() == 0 ) efheiti = "Deloitte";
						ps.setString(5, efheiti);
						ps.setString(6, efkt);
						ps.setString(8, ar);
						//ps.execute();
						
					} else if( woSpace.startsWith("áritun") && !inarit ) {
						String prev = "";
						while( !(woSpace.startsWith("endursk") || woSpace.startsWith("löggilturendursk")) && !woSpace.contains("rekstrarreikn") && !woSpace.contains("skýr.") ) {
							prev = woSpace;
							try {
								line = br.readLine();
							} catch( Exception e ) {
								System.err.println("simmi2");
								line = null;
							}
							if( line != null ) {
								line = line.trim();
								woSpace = line.toLowerCase().replace(" ", "");
							}
							else break;
						}
						
						if( woSpace.contains("skýr.") ) {
							passtskyr = 1;
						} else {
							String nafn = prev.substring(0, Math.min(50, prev.length()));
							if( nafn.contains("son") || nafn.contains("dóttir") ) {
								ps.setString(3, nafn);
								inarit = true;
							}
						}
					} else if( passtskyr > 0 && !inhagn && (woSpace.startsWith("ársniðurstaða") || ((woSpace.startsWith("(tap") || woSpace.startsWith("tap") || woSpace.startsWith("hagnaður") || woSpace.startsWith("rekstrarafkoma")) && woSpace.contains("ársins"))) ) {
						stuff( br, line, woSpace, ps, 11, 12 );
						inhagn = true;
						
						/*String nafn = prev.substring(0, Math.min(50, prev.length()));
						if( nafn.contains("son") || nafn.contains("dóttir") ) {
							ps.setString(3, nafn);					
							break;
						}*/
					} else if( !ineign && passtskyr > 1 && woSpace.startsWith("eignir") ) {
						stuff( br, line, woSpace, ps, 13, 14 );
						ineign = true;
						//inhagn = true;
					} else {
						if( woSpace.contains("skýr.") ) {
							passtskyr++;
						}
					}
						/*else if( !in && (((woSpace.startsWith("(Eignir") || woSpace.startsWith("tap") || woSpace.startsWith("hagnaður") || woSpace.startsWith("rekstrarafkoma")) && woSpace.contains("ársins"))) ) {
					}
												
					}*/
					
					/* System.err.println( line );
					}*/
					
					//if( count++ > 10 ) break;
					try {
						line = br.readLine();
					} catch( Exception e ) {
						System.err.println("simmi");
						line = null;
					}
				}
				
				rd.close();
				if( !inside ) {
					ps.setString(7, "parse error");
					
					//fcount++;
					//System.err.println( ""+f.getName() );
				}
				ps.setString(9, fname);
				ps.setString(10, parname);
				ps.execute();
			}
			
			/*String res = "";
			int r = rd.read(cbuf);
			while( r > 0 ) {
				res += new String( cbuf, 0, r );
				r = rd.read(cbuf);
			}
			
			String subs = res.substring( 0, Math.min(res.length(),2048) );
			subs.*/
			//System.out.println( res.substring(0, Math.min(res.length(),2048) ) );
		}
		ps.close();
	}
	
	public static void stuff( BufferedReader br, String line, String woSpace, PreparedStatement ps, int ind1, int ind2 ) throws SQLException {
		String prev = "";
		/*while( !line.startsWith("endursk") ) {
			prev = line;
			try {
				line = br.readLine();
			} catch( Exception e ) {
				System.err.println("simmi3");
				line = null;
			}
			if( line != null ) {
				line = line.trim();
				woSpace = line.replace(" ", "");
			}
			else break;
		}*/
		
		int	i = 0;
		char c = line.charAt( i++ );
		char pc = ' ';
		char pc2 = ' ';
		while( (c > '9' || c < '0') && i < line.length()-1 ) {
			pc = c;
			c = line.charAt( i++ );
		}
		
		int ei = i;
		if( ei < line.length() ) {
			char c2 = line.charAt( ei++ );
			while( ((c2 <= '9' && c2 >= '0') || c2 == '.') && ei < line.length() ) {
				c2 = line.charAt( ei++ );
			}
			
			if( (c2 <= '9' && c2 >= '0') || c2 == '.' ) {
				ei++;
			}
		}
		
		int	si = ei;
		if( si < line.length() ) {
			c = line.charAt( si++ );
			while( (c > '9' || c < '0') && si < line.length()-1 ) {
				pc2 = c;
				c = line.charAt( si++ );
			}
		}
		
		int sei = si;
		if( sei < line.length() ) {
			char c2 = line.charAt( sei++ );
			while( ((c2 <= '9' && c2 >= '0') || c2 == '.') && sei < line.length() ) {
				c2 = line.charAt( sei++ );
			}
			
			if( (c2 <= '9' && c2 >= '0') || c2 == '.' ) {
				sei++;
			}
		}
		
		while( i == line.length() - 1 ) {
			try {
				line = br.readLine();
			} catch( Exception e ) {
				System.err.println("simmi4");
				line = null;
			}
			if( line != null ) {
				line = line.trim();
				woSpace = line.toLowerCase().replace(" ", "");
			}
			else break;
			
			i = 0;
			ei = 0;
			si = 0;
			sei = 0;
			if( line.length() > 0 ) {
				c = line.charAt( i++ );
				while( (c > '9' || c < '0') && i < line.length()-1 ) {
					c = line.charAt( i++ );
				}
				
				ei = i;
				if( ei < line.length() ) {
					char c2 = line.charAt( ei++ );
					while( ((c2 <= '9' && c2 >= '0') || c2 == '.') && ei < line.length() ) {
						c2 = line.charAt( ei++ );
					}
					
					if( (c2 <= '9' && c2 >= '0') || c2 == '.' ) {
						ei++;
					}
				}
				
				si = ei;
				if( si < line.length() ) {
					c = line.charAt( si++ );
					while( (c > '9' || c < '0') && si < line.length() ) {
						c = line.charAt( si++ );
					}
				}
				
				sei = si;
				if( sei < line.length() ) {
					char c2 = line.charAt( sei++ );
					while( ((c2 <= '9' && c2 >= '0') || c2 == '.') && sei < line.length() ) {
						c2 = line.charAt( sei++ );
					}
					
					if( (c2 <= '9' && c2 >= '0') || c2 == '.' ) {
						sei++;
					}
				}
			}
		}
		
		String hagn = line.substring(Math.max(0,i-1), Math.min( Math.max(0, ei-1), line.length()));
		if( pc == '(' ) hagn = "-"+hagn;
		
		if( hagn.length() > 1 ) {
			System.err.println();
		}
		
		String hagnf = line.substring(Math.max(0,si-1), Math.min( Math.max(0, sei-1 ), line.length()));
		if( pc2 == '(' ) hagnf = "-"+hagnf;
		
		ps.setString(ind1, hagn);
		ps.setString(ind2, hagnf);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TikaConfig config = TikaConfig.getDefaultConfig();
		Tika t = new Tika( config );
		
		List<String> parsers = new ArrayList<String>(config.getParsers().keySet());
		Collections.sort(parsers);
		Iterator<String> it = parsers.iterator();
		System.out.println("Mime type parsers:");
		while( it.hasNext() ) {
			System.out.println(" " + it.next());
		}
		try {
			File fl = new File("/home/sigmar/hafsjor/gogn/arsreikningar_2/");
			sub( t, fl );
			System.err.println( "tot " + fcount );
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}
