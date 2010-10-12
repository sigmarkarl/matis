#include "simlab.h"
//#include <boost/random.hpp>

//typedef boost::minstd_rand base_generator_type;

extern simlab data;

/*void t_rand( double* buffer, long length, double start, double stop ) {
	base_generator_type 													generator;
	boost::uniform_real<>													uni_dist(start, stop);
	boost::variate_generator<base_generator_type&, boost::uniform_real<> > 	uni(generator, uni_dist);
	for( long i = 0; i < length; i++ ) {
		buffer[i] = uni();
	}
}

void t_rand( int* buffer, long length, double start, double stop ) {
	base_generator_type 													generator;
	boost::uniform_int<>													uni_dist(start, stop);
	boost::variate_generator<base_generator_type&, boost::uniform_int<> > 	uni(generator, uni_dist);
	for( long i = 0; i < length; i++ ) {
		buffer[i] = uni();
	}
}

JNIEXPORT int rndm( simlab start, simlab stop ) {
	if( data.type == 32 ) t_rand( (int*)data.buffer, data.length, 0.0, 0.0 );
	//if( data.type == 34 ) t_rand( (float*)data.buffer, data.length );
	//if( data.type == 64 ) t_rand( (long long*)data.buffer, data.length );
	else if( data.type == 66 ) t_rand( (double*)data.buffer, data.length, 0.0, 1.0 );

	return 0;
}*/
