/*
 * conv.cc
 *
 *  Created on: Feb 4, 2009
 *      Author: root
 */

#include "simlab.h"
#include <stdio.h>

#include <vector>

JNIEXPORT int zero();
extern simlab 	data;
extern c_const<int>		iconst;
extern c_const<float>	fconst;

template <typename T,typename K> T* t_fnd( T* buffer, int length, K* c_buffer, int c_length, T* ret ) {
	for( int k = 0; k < length; k++ ) {
		for( int t = k; t < k+c_length; t++ ) {
			ret[t] += buffer[k]*c_buffer[t-k];
		}
	}
}

template <typename T,typename K> void t_find( T* buffer, int length, K* c_buffer, int c_length ) {
	std::vector<long>	idx;
	//int r = 0;
	printf("%d %d\n", length, c_length);
	for( int i = 0; i < length; i++ ) {
		for( int k = 0; k < c_length; k++ ) {
			if( buffer[i] == c_buffer[k] ) {
				idx.push_back( i );
				break;
			}
		}
	}
	T* ret = new T[ idx.size() ];
	data.buffer = (long)ret;
	data.length = idx.size();
	data.type = sizeof(long);
	zero();

	for( int i = 0; i < idx.size(); i++ ) {
		ret[i] = idx[i];
	}
}

template <typename T,typename K,typename V> class c_conv {
public:
	c_conv( T tbuf, int tlen, K kbuf, int klen, int rlen ) : t(tbuf), tl(tlen), k(kbuf), kl(klen), rl(rlen) {
		rchunk = tl+kl-1;
	}
	virtual V & operator[]( int i ) {
		r = 0;
		int w = i/rchunk;
		int v = i%rchunk;

		int wt = w*tl;
		int wk = w*kl;

		int start = v-(kl-1) < 0 ? 0: v-(kl-1);
		int stop = v >= tl ? tl : v+1;
		for( int u = start; u < stop; u++ ) {
			r += t[wt + u]*k[wk + kl + start - u - 1];
		}
		return r;
	}
	T 	t;
	int tl;
	K	k;
	int kl;
	int rl;

	V	r;
	int rchunk;
};

template <typename T> void t_finder( T buffer, int chunk, int c_chunk, int rlen ) {
	if( data.type < 0 ) {
		if( data.type == -66 ) {
			data.buffer = (long)new c_conv<c_simlab<double&>&,T,double>( *(c_simlab<double&>*)data.buffer, chunk, buffer, c_chunk, rlen );
			data.length = rlen;
		}
	} else {
		if( data.type == 66 ) {
			data.buffer = (long)new c_conv<double*,T,double>( (double*)data.buffer, chunk, buffer, c_chunk, rlen );
			data.type = -66;
			data.length = rlen;
		}
	}
}

JNIEXPORT int finder( simlab convee, simlab chnk, simlab c_chnk ) {
	int ret = 3;
	if( c_chnk.buffer == 0 ) {
		c_chnk.buffer = convee.length;
		ret = 2;
	}
	if( chnk.buffer == 0 ) {
		chnk.buffer = data.length;
		ret = 1;
	}
	int chunk = chnk.buffer;
	int c_chunk = c_chnk.buffer;

	int retchunk = chunk+c_chunk-1;
	int retlen = retchunk*(data.length/chunk);

	if( convee.type < 0 ) {
		if( convee.type == -66 ) t_finder<c_simlab<double&>&>( *(c_simlab<double&>*)convee.buffer, chunk, c_chunk, retlen );
	} else {
		if( convee.type == 66 ) t_finder<double*>( (double*)convee.buffer, chunk, c_chunk, retlen );
	}

	/*if( data.type == 66 ) {
		if( convee.type == 66 ) t_conver( (double*)data.buffer, data.length, chunk, (double*)convee.buffer, convee.length, c_chunk );
		else if( convee.type == 32 ) t_conv( (double*)data.buffer, data.length, chunk, (int*)convee.buffer, convee.length, c_chunk );
		else if( convee.type == 8 ) t_conv( (double*)data.buffer, data.length, chunk, (unsigned char*)convee.buffer, convee.length, c_chunk );
	} else if( data.type == 32 ) {
		if( convee.type == 66 ) t_conv( (int*)data.buffer, data.length, chunk, (double*)convee.buffer, convee.length, c_chunk );
		else if( convee.type == 32 ) t_conv( (int*)data.buffer, data.length, chunk, (int*)convee.buffer, convee.length, c_chunk );
		else if( convee.type == 8 ) t_conv( (int*)data.buffer, data.length, chunk, (unsigned char*)convee.buffer, convee.length, c_chunk );
	} else if( data.type == 8 ) {
		if( convee.type == 66 ) t_conv( (unsigned char*)data.buffer, data.length, chunk, (double*)convee.buffer, convee.length, c_chunk );
		else if( convee.type == 32 ) t_conv( (unsigned char*)data.buffer, data.length, chunk, (int*)convee.buffer, convee.length, c_chunk );
		else if( convee.type == 8 ) t_conv( (unsigned char*)data.buffer, data.length, chunk, (unsigned char*)convee.buffer, convee.length, c_chunk );
	}*/

	return ret;
}

JNIEXPORT int find( simlab findee ) {
	if( data.type == 66 ) {
		if( findee.type == 66 ) t_find( (double*)data.buffer, data.length, (double*)findee.buffer, findee.length );
		//(chunk+c_chunk-1)*(data.length/chunk) )
		else if( findee.type == 32 ) t_find( (double*)data.buffer, data.length, (int*)findee.buffer, findee.length );
		else if( findee.type == 8 ) t_find( (double*)data.buffer, data.length, (unsigned char*)findee.buffer, findee.length );
	} else if( data.type == 32 ) {
		if( findee.type == 66 ) t_find( (int*)data.buffer, data.length, (double*)findee.buffer, findee.length );
		else if( findee.type == 32 ) t_find( (int*)data.buffer, data.length, (int*)findee.buffer, findee.length );
		else if( findee.type == 8 ) t_find( (int*)data.buffer, data.length, (unsigned char*)findee.buffer, findee.length );
	} else if( data.type == 8 || data.type == 9 ) {
		if( findee.type == 66 ) t_find( (unsigned char*)data.buffer, data.length, (double*)findee.buffer, findee.length );
		else if( findee.type == 32 ) t_find( (unsigned char*)data.buffer, data.length, (int*)findee.buffer, findee.length );
		else if( findee.type == 8 || findee.type == 9 ) t_find( (unsigned char*)data.buffer, data.length, (unsigned char*)findee.buffer, findee.length );
	}
	return 1;
}
