#include <cstdio>
#include <cstdlib>
#include <cstring>
#include <set>
#include <map>
#include <string>
#include <vector>
#include <iostream>
#include <fstream>
#include <boost/foreach.hpp>
#include <boost/algorithm/string.hpp>
#include <boost/lexical_cast.hpp>
#include <curl/curl.h>

using namespace std;
using namespace boost;

void rands() {
	set<int>		iset;
	vector<string>	snames;

	char	c[256];
	FILE* f = fopen( "../folk.txt", "r" );
	while( fgets( c, sizeof(c), f ) != NULL ) {
		int strl = strlen( c );
		c[strl-1] = 0;
		snames.push_back( c );
	}

	while( iset.size() < snames.size() ) {
		iset.insert( rand() );
	}

	int i = 0;
	BOOST_FOREACH( int r, iset ) {
		cout << snames[i++] << "\t" << r << endl;
	}

	fclose( f );
}

void html( set<string> & names, const char* query ) {
	cout << "<p>Vinsamlegast kjósið tvo öryggistrúnaðarmenn<p>";
	cout << "<form method=\"POST\" action=\"vote\">";
	cout << "<table>";
	int i = 0;
	BOOST_FOREACH( string name, names ) {
		if( i%3 == 0 )	{
			if( i > 0 ) cout << "</tr>";
			cout << "<tr>";
		}
		cout << "<td>";
		cout << "<input type=\"checkbox\" name=\"nafn\" ";
		cout << "value=\"" << name << "\"/>" << name;
		cout << "</td>";

		i++;
	}
	cout << "</table><p>";
	cout << "<input type=\"submit\" value=\"Kjósa\" name=\"" << query << "\"/>";
	cout << "</form>";
}

void params( int argc, char **argv ) {
	for( int i = 0; i < argc; i++ ) {
		printf( "%s\n", argv[i] );
	}
}

int main(int argc, char **argv) {
	//rands();

	map<string,int>	stoi;
	map<int,string> itos;
	set<string>		names;
	set<int>		iset;

	char	c[256];
	FILE* f = fopen( "ids.txt", "r" );
	while( fgets( c, sizeof(c), f ) != NULL ) {
		int strl = strlen( c );
		c[strl-1] = 0;
		vector<string>	split;
		boost::split( split, c, is_any_of("\t") );

		int val = lexical_cast<int>( split[2] );
		stoi[split[0]] = val;
		itos[val] = split[0];
		names.insert( split[0] );
		iset.insert( val );
	}

	CURL *curl = curl_easy_init();
	cout << "Content-type: text/html; charset=utf-8\n\n";

	string	post;
	cin >> post;
	int rlen;
	char* res = NULL;
	if( post.length() ) res = curl_easy_unescape( curl, post.c_str(), post.length(), &rlen );

	//if( res == NULL ) cout << "resnull";
	//else cout << "notnull";
	//char* query = getenv( "QUERY_STRING" );
	//printf( "erm %s\n", res );
	//params( argc, argv );

	cout << "<html>";
	cout << "<body>";
	cout << "<center><p>";
	if( res ) {
		vector<string>	split;
		boost::split( split, res, is_any_of("&") );

		if( split.size() > 2 ) {
			vector<string>	subsplit;
			const char*	c1 = split[0].c_str();
			boost::split( subsplit, c1, is_any_of("=") );
			boost::algorithm::replace_all( subsplit[1], "+", " " );
			string name1 = subsplit[1];

			subsplit.clear();
			const char*	c2 = split[1].c_str();
			boost::split( subsplit, c2, is_any_of("=") );
			boost::algorithm::replace_all( subsplit[1], "+", " " );
			string name2 = subsplit[1];

			cout << "Takk fyrir að kjósa<p>";
			cout << "Þú kaust:<br>";
			cout << name1 << "<br>";
			cout << name2 << "<br>";

			int id = 0;
			const char* istr = split[ split.size()-1 ].c_str();
			subsplit.clear();
			boost::split( subsplit, istr, is_any_of("=") );
			try {
				id = lexical_cast<int>( subsplit[0] );
			} catch( bad_lexical_cast & ) {
				id = 0;
			}

			if( iset.find( id ) != iset.end() ) {
				//cout << "skrif";
				string kjosandi = "/results/" + itos[ id ];
				ofstream myfile;
				myfile.open( kjosandi.c_str() );
				myfile << name1 << endl;
				myfile << name2 << endl;
				myfile.close();
			}
		} else {
			const char* query = split[ split.size()-1 ].c_str();
			cout << "Veldu tvo<br>";
			html( names, query );
		}
	} else {
		char* query = getenv( "QUERY_STRING" );
		html( names, query );
	}
	cout << "</center>";
	cout << "</body>";
	cout << "</html>";
}
