#include <cstdio>
#include <cstring>
#include <string>
#include <map>
#include <set>
#include <vector>
#include <algorithm>

using namespace std;

class off {
public:
	int 	a,b,c;
	int		r;
	off( int aa, int bb, int cc, int rr ) : a(aa), b(bb), c(cc), r(rr) {}
	bool operator< ( const off & o ) const { return a < o.a; }
};

int gdiff = 2500;
class coff {
public:
	vector<off>	voff;
	int	best1;
	int best2;
	coff() {}
	int getLength() { return voff.size(); }
	int valid() {
		if( voff.size() > 10 ) return true;
		for( unsigned int i = 0; i < voff.size(); i++ ) {
			off & o = voff[i];
			if( o.c > 200 ) return true;
		}
		return false;
	};
	void ssort() {
		sort( voff.begin(), voff.end() );
	}
	void checkLongestCons() {
		int ret = 0;
		int retl = 0;

		int count = 0;
		int subret = 0;
		int lastcount = 0;
		int prev = 0;
		int aprev = 0;
		int maxind = 0;
		int maxval = 0;
		unsigned int i;
		for( i = 0; i < voff.size(); i++ ) {
			off & o = voff[i];
			int adiff = o.b - prev;
			int bdiff = o.a - aprev;

			if (o.c > maxval) {
				maxval = o.c;
				maxind = i;
			}

			if (bdiff > 0 && bdiff > adiff - gdiff && bdiff < adiff + gdiff) {
				count++;
			} else {
				if (count > lastcount) {
					ret = subret;
					retl = i-1;
					lastcount = count;
				}
				count = 0;

				subret = i;
			}

			prev = o.b;
			aprev = o.a;
		}
		if (count > lastcount) {
			ret = subret;
			retl = i-1;
			lastcount = count;
		}

		if (lastcount <= 1) {
			ret = maxind;
		}

		best1 = ret;
		best2 = retl;
	}
	off & getStart() {
		return voff[best1];
	}
	off & getStop() {
		return voff[best2];
	}
};

void checkLengths( const char* fname, map<string,int> & m ) {
	char	bb[2048];
	int		nval;
	FILE*	f = fopen( fname, "r" );

	char* val = fgets( bb, sizeof(bb), f );
	while( val != NULL ) {
		//int r = strlen( bb );
		if( bb[0] == '>' ) {
			bb[12] = '\0';
			sscanf( &bb[21], "%d", &nval );
			//printf("%d\n", nval);
			m[ &bb[1] ] = nval;
		}

		val = fgets( bb, sizeof(bb), f );
	}

	fclose( f );
}

int main( int argc, char* argv[] ) {
	char	buffer[256];
	int		s1, s2, len;
	char	current[256];
	char	temp[256];
	char	cname[64];
	const char*	path = "/home/sigmar/fass/assembly0/454AllContigs.fna";
	strcpy( current, path );

	int PERC = 80;

	sscanf( argv[argc-1], "%d", &PERC );

	for( int i = 1; i < argc-1; i++ ) {
		map<string,coff>	offmap;
		FILE* f = fopen( argv[i], "r" );

		printf("file: %s\n", argv[i] );
		map<string,int>	m1;
		map<string,int> m2;

		int slen = strlen( argv[i] );
		current[26] = argv[i][ slen-2 ];
		printf( "%s\n", current );
		checkLengths( current, m1 );
		current[26] = argv[i][ slen-1 ];
		checkLengths( current, m2 );

		int r = 0;
		vector<string>	sset;
		while( fgets( buffer, sizeof(buffer), f ) != NULL ) {
			if( buffer[0] == '>' ) {
				map<string,coff>::iterator mit = offmap.begin();
				while( mit != offmap.end() ) {
					coff & cff = mit->second;
					cff.ssort();
					cff.checkLongestCons();

					if( cff.valid() ) {
						string str = mit->first;
						/*if( strcmp( "contig00049", str.c_str() ) == 0 ) {
							printf( "erm %d %d\n", cff.best1, cff.best2 );
						}*/
						off & o0 = cff.getStart(); //voff[0];
						off & o1 = cff.getStop(); //voff[cff.voff.size()-1];
						int astart = o0.a;
						int astop = o1.a + o1.c;
						int bstart = o0.b;
						int bstop = o1.b + o1.c;

						int len2 = m1[ str.c_str() ];

						char c = current[13];
						current[13] = '\0';
						int len1 = m2[ &current[2] ];
						//printf( "blah1 %s\n", current );
						//printf( "blah2 %s\n", current );

						int perc = 0;
						for( int i = cff.best1; i <= cff.best2; i++ ) {
							off & e1 = cff.voff[i];
							perc += e1.c;
						}

						if( perc != 0 ) {
							/*printf("%s %s %d %d %d %d\n", str.c_str(), &current[2], cff.valid(), (int)cff.voff.size(), cff.best1, cff.best2);
							for( unsigned int i = 0; i < cff.voff.size(); i++ ) {
								off & o = cff.voff[i];
								printf("%d\n", o.c);
							}*/

							current[13] = c;

							int ia1 = (perc*100)/(len2-astart);
							int ia2 = (perc*100)/(astop);

							bool a1 = ia1 > PERC;
							bool a2 = ia2 > PERC;

							int ib1 = (perc*100)/(len1-bstart);
							int ib2 = (perc*100)/(bstop);

							bool b1 = ib1 > PERC;
							bool b2 = ib2 > PERC;

							if( (a1 && b2) || (a2 && b1) || (a1 && a2) || (b1 && b2) ) {
								/*sprintf( temp, "%d %d %d %d %d", perc, (len2-astart), astop, ia1, ia2 );
								sset.push_back( temp );
								sprintf( temp, "%d %d %d %d %d", perc, (len1-bstart), bstop, ib1, ib2 );
								sset.push_back( temp );*/

								if( r == 1 ) {
									if( len1 >= 100000 ) sprintf( temp, "%s\t(%d)\t%d\t%d\t%s (%d)\t%d\t%d", current, len1, bstart, bstop, str.c_str(), len2, astart, astop );
									else sprintf( temp, "%s\t(%d)\t\t%d\t%d\t%s (%d)\t%d\t%d", current, len1, bstart, bstop, str.c_str(), len2, astart, astop );
								} else {
									if( len1 >= 100000 ) sprintf( temp, "%s\t\t(%d)\t%d\t%d\t%s (%d)\t%d\t%d", current, len1, bstart, bstop, str.c_str(), len2, astart, astop );
									else sprintf( temp, "%s\t\t(%d)\t\t%d\t%d\t%s (%d)\t%d\t%d", current, len1, bstart, bstop, str.c_str(), len2, astart, astop );
								}
								sset.push_back( temp );
							}
						}
					}
					mit++;
				}

				if( strncmp( buffer, current, 13) == 0 ) {
					r = 1;
				} else {
					r = 0;
				}

				if( !r ) {
					if( sset.size() > 1 ) {
						for( vector<string>::iterator it = sset.begin(); it != sset.end(); it++ ) {
							printf( "%s\n", it->c_str() );
						}
					}
					sset.clear();
				}

				offmap.clear();

				strcpy( current, buffer );
				for( unsigned int i = 0; i < sizeof(current); i++ ) {
					if( current[i] == '\0' ) break;
					else if( current[i] == '\n' ) current[i] = '\0';
				}
			} else {
				sscanf( buffer, "%s %d %d %d", cname, &s1, &s2, &len );
				offmap[cname].voff.push_back( off(s1,s2,len,r) );
			}
		}
		fclose( f );
	}

	return 0;
}
