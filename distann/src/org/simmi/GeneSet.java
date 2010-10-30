package org.simmi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GeneSet {
	/**
	 * @param args
	 * @throws IOException 
	 */
	
	static Map<String,String>		swapmap = new HashMap<String,String>();
	public static void func1( String[] names, File dir ) throws IOException {
		Map<String,String>		allgenes = new HashMap<String,String>();
		Map<String,Set<String>>	geneset = new HashMap<String,Set<String>>();
		
		PrintStream ps = new PrintStream("/home/sigmar/iron.giant");
		System.setErr( ps );
		
		for( String name : names ) {
			File 			f = new File( dir, name );
			Set<String>		set = new HashSet<String>();
			
			FileReader		fr = new FileReader( f );
			BufferedReader 	br = new BufferedReader( fr );
			String line = br.readLine();
			while( line != null ) {
				String trim = line.trim();
				if( trim.startsWith(">ref") || trim.startsWith(">sp") || trim.startsWith(">pdb") || trim.startsWith(">dbj") || trim.startsWith(">gb") || trim.startsWith(">emb") ) {
					String[] split = trim.split("\\|");
					set.add( split[1] );
					
					if( !allgenes.containsKey( split[1] ) || allgenes.get( split[1] ) == null ) {
						allgenes.put( split[1], split.length > 1 ? split[2].trim() : null );
					}
				}
				
				line = br.readLine();
			}
			
			System.err.println( name + " genes total: " + set.size() );
			geneset.put( name, set );
		}
		
		Set<String>	allset = new HashSet<String>( allgenes.keySet() );
		for( String name : geneset.keySet() ) {
			Set<String>	gset = geneset.get( name );
			allset.retainAll( gset );
		}
		System.err.println( "Core genome size: " + allset.size() );
		
		Set<String>	nameset = null;
		
		/*for( String gname : allset ) {
			System.err.println( gname + "\t" + allgenes.get(gname) );
		}*/
		
		for( String aname : names ) {
			allset = new HashSet<String>( allgenes.keySet() );
			nameset = new HashSet<String>( Arrays.asList(names) );
			nameset.remove(aname);
			for( String tname : nameset ) {
				allset.removeAll( geneset.get(tname) );
			}
			System.err.println( "Genes found only in " + swapmap.get(aname) + "\t" + allset.size() );
			for( String gname : allset ) {
				System.err.println( gname + "\t" + allgenes.get(gname) );
			}
		}
		
		for( String aname : names ) {
			allset = new HashSet<String>( allgenes.keySet() );
			nameset = new HashSet<String>( Arrays.asList(names) );
			nameset.remove(aname);
			allset.removeAll( geneset.get(aname) );
			for( String tname : nameset ) {
				allset.retainAll( geneset.get(tname) );
			}
			
			Set<String>	reset = new HashSet<String>();
			for( String name : nameset ) {
				reset.add( swapmap.get(name) );
			}
			System.err.println( "Genes only in all off " + reset + "\t" + allset.size() );
			for( String gname : allset ) {
				System.err.println( gname + "\t" + allgenes.get(gname) );
			}
		}
		
		for( int i = 0; i < names.length; i++ ) {
			for( int y = i+1; y < names.length; y++ ) {
				allset = new HashSet<String>( allgenes.keySet() );
				nameset = new HashSet<String>( Arrays.asList(names) );
				//nameset.add( names[i] );
				//nameset.add( names[y] );
				nameset.remove( names[i] );
				nameset.remove( names[y] );
				
				allset.removeAll( geneset.get(names[i]) );
				allset.removeAll( geneset.get(names[y]) );
				for( String tname : nameset ) {
					allset.retainAll( geneset.get(tname) );
				}
				
				Set<String>	reset = new HashSet<String>();
				//reset.add( swapmap.get(names[i]) );
				//reset.add( swapmap.get(names[y]) );
				
				for( String name : nameset ) {
					reset.add( swapmap.get(name) );
				}
				System.err.println( "Genes only in all of " + reset + "\t" + allset.size() );
				for( String gname : allset ) {
					System.err.println( gname + "\t" + allgenes.get(gname) );
				}
			}
		}
		
		for( int i = 0; i < names.length; i++ ) {
			for( int y = i+1; y < names.length; y++ ) {
				allset = new HashSet<String>( allgenes.keySet() );
				nameset = new HashSet<String>( Arrays.asList(names) );
				//nameset.add( names[i] );
				//nameset.add( names[y] );
				nameset.remove( names[i] );
				nameset.remove( names[y] );
				
				allset.retainAll( geneset.get(names[i]) );
				allset.retainAll( geneset.get(names[y]) );
				for( String tname : nameset ) {
					allset.removeAll( geneset.get(tname) );
				}
				
				Set<String>	reset = new HashSet<String>();
				reset.add( swapmap.get(names[i]) );
				reset.add( swapmap.get(names[y]) );
				
				/*for( String name : nameset ) {
					reset.add( swapmap.get(name) );
				}*/
				System.err.println( "Genes only in all of " + reset + "\t" + allset.size() );
				for( String gname : allset ) {
					System.err.println( gname + "\t" + allgenes.get(gname) );
				}
			}
		}
		
		for( int i = 0; i < names.length; i++ ) {
			for( int y = i+1; y < names.length; y++ ) {
				for( int k = y+1; k < names.length; k++ ) {
					allset = new HashSet<String>( allgenes.keySet() );
					nameset = new HashSet<String>( Arrays.asList(names) );
					//nameset.add( names[i] );
					//nameset.add( names[y] );
					nameset.remove( names[i] );
					nameset.remove( names[y] );
					nameset.remove( names[k] );
					
					allset.removeAll( geneset.get(names[i]) );
					allset.removeAll( geneset.get(names[y]) );
					allset.removeAll( geneset.get(names[k]) );
					for( String tname : nameset ) {
						allset.retainAll( geneset.get(tname) );
					}
					
					Set<String>	reset = new HashSet<String>();
					/*reset.add( swapmap.get(names[i]) );
					reset.add( swapmap.get(names[y]) );
					reset.add( swapmap.get(names[k]) );*/
					
					for( String name : nameset ) {
						reset.add( swapmap.get(name) );
					}
					
					System.err.println( "Genes only in all of " + reset + "\t" + allset.size() );
					for( String gname : allset ) {
						System.err.println( gname + "\t" + allgenes.get(gname) );
					}
				}
			}
		}
		
		System.err.println( "Unique genes total: " + allgenes.size() );
		
		ps.close();
	}
	
	public static void func2( String[] stuff, File dir, File dir2 ) throws IOException {
		Map<String,String>	aas = new HashMap<String,String>();
		for( String st : stuff ) {
			System.err.println("Unknown genes in " + swapmap.get(st+".out"));
			
			File aa = new File( dir2, st+".fsa" );
			BufferedReader br = new BufferedReader( new FileReader(aa) );
			String line = br.readLine();
			String name = null;
			String ac = "";
			while( line != null ) {
				if( line.startsWith(">") ) {
					if( ac.length() > 0 ) aas.put(name, ac);
					
					ac = "";
					name = line.substring(1).split(" ")[0];
				} else ac += line.trim();
				line = br.readLine();
			}
			br.close();
			
			File ba = new File( dir, st+".out" );
			br = new BufferedReader( new FileReader(ba) );
			line = br.readLine();
			//String name = null;
			//String ac = null;
			while( line != null ) {
				if( line.startsWith("Query=") ) {
					name = line.substring(7).split(" ")[0];
				}
				
				if( line.contains("No hits") ) {
					System.err.println( name + "\t" + aas.get(name) );
				}
				
				line = br.readLine();
			}
			br.close();
		}
	}
	
	public static void func3( String[] names, File dir ) throws IOException {
		FileWriter	fw = new FileWriter( new File( dir, "all.fsa" ) );
		for( String name : names ) {
			File 			f = new File( dir, name+".fsa" );
			BufferedReader 	br = new BufferedReader( new FileReader( f ) );
			String line = br.readLine();
			while( line != null ) {
				if( line.startsWith(">") ) fw.write( ">"+swapmap.get(name+".out")+" "+line.substring(1)+"\n" );
				else fw.write( line+"\n" );
				
				line = br.readLine();
			}
			br.close();
		}
		fw.close();
	}
	
	public static void func4( File dir, String[] stuff ) throws IOException {
		Set<Set<String>>	total = new HashSet<Set<String>>();
		for( String name : stuff ) {
			File f = new File( dir, name+"all.out" );
			BufferedReader	br = new BufferedReader( new FileReader( f ) );
			
			String line = br.readLine();
			while( line != null ) {
				if( line.startsWith("Sequences prod") ) {
					line = br.readLine();
					Set<String>	all = new HashSet<String>();
					while( line != null && !line.startsWith(">") ) {
						String trim = line.trim();
						if( trim.startsWith("scoto") ) {
							String val = trim.substring( 0, trim.indexOf('#')-1 );
							all.add( val );
						}						
						line = br.readLine();
					}
					
					boolean cont = false;
					for( Set<String>	check : total ) {
						for( String aval : all ) {
							if( check.contains(aval) ) {
								cont = true;
								check.addAll( all );
								break;
							}
						}
					}
					if( !cont ) total.add( all );
					
					if( line == null ) break;
				}
				
				line = br.readLine();
			}
		}
		
		HashMap<Set<String>,Set<Map<String,Set<String>>>>	clusterMap = new HashMap<Set<String>,Set<Map<String,Set<String>>>>();
		
		for( Set<String>	t : total ) {
			Set<String>	teg = new HashSet();
			for( String e : t ) {
				String str = e.substring( 0, e.indexOf(' ') );
				teg.add( str );
			}
			
			Set<Map<String,Set<String>>>	setmap;
			if( clusterMap.containsKey( teg ) ) {
				setmap = clusterMap.get( teg );
			} else {
				setmap = new HashSet<Map<String,Set<String>>>();
				clusterMap.put( teg, setmap );
			}
			
			Map<String,Set<String>>	submap = new HashMap<String,Set<String>>();
			setmap.add( submap );
			
			for( String e : t ) {
				String str = e.substring( 0, e.indexOf(' ') );
				Set<String>	set;
				if( submap.containsKey( str ) ) {
					set = submap.get(str);
				} else {
					set = new HashSet<String>();
					submap.put( str, set );	
				}
				set.add( e );
			}
		}
		
		PrintStream ps = new PrintStream( new FileOutputStream("/home/sigmar/out.out") );
		System.setErr( ps );
		for( Set<String> set : clusterMap.keySet() ) {
			Set<Map<String,Set<String>>>	setmap = clusterMap.get( set );
			
			int i = 0;
			for( Map<String,Set<String>>	map : setmap ) {
				for( String s: map.keySet() ) {
					Set<String>	genes = map.get(s);
					i += genes.size();
				}
			}
			System.err.println( "Included in : " + set.size() + " scotos " + set + " containing: " + setmap.size() + "set of genes\ttotal of " + i + " loci" );
			
			i = 0;
			for( Map<String,Set<String>>	map : setmap ) {
				System.err.println("Starting set " + i);
				
				for( String s: map.keySet() ) {
					Set<String>	genes = map.get(s);
					
					System.err.println( "In " + s + " containing " + genes.size() );
					for( String gene : genes ) {
						System.err.println( gene + "\t" + lociMap.get(gene) );
					}
				}
				System.err.println();
				
				i++;
			}
		}
		ps.close();
		
		/*System.err.println( "# clusters: " + total.size() );
		int max = 0;
		for( Set<String> sc : total ) {
			if( sc.size() > max ) max = sc.size();
			System.err.println( "\tcluster size: " + sc.size() );
		}
		System.err.println( "maxsize: " + max );
		
		int[]	ia = new int[max];
		for( Set<String> sc : total ) {
			ia[sc.size()-1]++;
		}
		
		for( int i : ia ) {
			System.err.println( "hist: " + i );
		}*/
	}
	
	static Map<String,String>	lociMap = new HashMap<String,String>();
	public static void func5( String[] stuff, File dir ) throws IOException {
		//Map<String,String>	aas = new HashMap<String,String>();
		for( String st : stuff ) {			
			File ba = new File( dir, st+".out" );
			BufferedReader br = new BufferedReader( new FileReader(ba) );
			String line = br.readLine();
			String name = null;
			while( line != null ) {
				if( line.startsWith("Query= ") ) {
					name = line.substring(7).split(" ")[0];
				}
				
				String prename = swapmap.get(st+".out")+" "+name;
				if( line.contains("No hits") ) {
					lociMap.put( prename, "No match" );
					//System.err.println( prename + "\tNo match" );
				}
				
				if( line.startsWith(">ref") || line.startsWith(">sp") || line.startsWith(">pdb") || line.startsWith(">dbj") || line.startsWith(">gb") || line.startsWith(">emb") ) {
					String[] split = line.split("\\|");
					lociMap.put( prename, split[1] + (split.length > 2 ? "\t" + split[2] : "") );
					//System.err.println( prename + "\t" + split[1] );
				}
				
				line = br.readLine();
			}
			br.close();
		}
	}
	
	public static void main(String[] args) {
		String[]	stuff = {"aa1","aa2","aa4","aa6","aa7","aa8"};
		String[]	names = {"aa1.out","aa2.out","aa4.out","aa6.out","aa7.out","aa8.out"};
		File dir = new File("/home/sigmar/thermus/results/");
		File dir2 = new File("/home/sigmar/thermus/out/");
		
		swapmap.put("aa1.out", "scoto_346");
		swapmap.put("aa2.out", "scoto_2101");
		swapmap.put("aa4.out", "scoto_2127");
		swapmap.put("aa6.out", "scoto_252");
		swapmap.put("aa7.out", "scoto_1572");
		swapmap.put("aa8.out", "scoto_4063");
		
		try {
			//func1( names, dir );
			//func2( stuff, dir, dir2 );
			//func3( stuff, dir2 );
			func5( stuff, dir );
			func4( dir2, stuff );
		} catch( Exception e ) {
			e.printStackTrace();
		}
	}
}
