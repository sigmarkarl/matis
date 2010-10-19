/*
 * arith.cc
 *
 *  Created on: Dec 29, 2008
 *      Author: root
 */

#include "simlab.h"
#include <stdio.h>
#include <math.h>

extern simlab 	data;
extern c_const<int>		iconst;
extern c_const<float>	fconst;

int g_i = 0;
unsigned int g_ui = 0;
long		g_l = 0;
unsigned long	g_ul = 0;
long long	g_ll = 0;
unsigned long long	g_ull = 0;
float	g_f = 0;
double	g_d = 0;

template<> int c_simlab<int>::operator[]( int i ) {
	return g_i;
};

template<> int & c_simlab<int&>::operator[]( int i ) {
	return g_i;
};

template<> unsigned int c_simlab<unsigned int>::operator[]( int i ) {
	return g_ui;
};

template<> unsigned int& c_simlab<unsigned int&>::operator[]( int i ) {
	return g_ui;
};

template<> long long c_simlab<long long>::operator[]( int i ) {
	return g_ll;
};

template<> long long & c_simlab<long long&>::operator[]( int i ) {
	return g_ll;
};

template<> long c_simlab<long>::operator[]( int i ) {
	return g_l;
};

template<> long& c_simlab<long&>::operator[]( int i ) {
	return g_l;
};

template<> unsigned long c_simlab<unsigned long>::operator[]( int i ) {
	return g_ul;
};

template<> unsigned long& c_simlab<unsigned long&>::operator[]( int i ) {
	return g_ul;
};

template<> unsigned long long c_simlab<unsigned long long>::operator[]( int i ) {
	return g_ull;
};

template<> unsigned long long& c_simlab<unsigned long long&>::operator[]( int i ) {
	return g_ull;
};

template<> float c_simlab<float>::operator[]( int i ) {
	return g_f;
};

template<> float& c_simlab<float&>::operator[]( int i ) {
	return g_f;
};

template<> double c_simlab<double>::operator[]( int i ) {
	return g_d;
};

template<> double& c_simlab<double&>::operator[]( int i ) {
	return g_d;
};

template <typename T, typename K> void t_set( T buffer, long length, K value, long vallen ) {
	long len = vallen > length ? vallen : length;

	for( long i = 0; i < len; i++ ) {
		buffer[i] = value[i];
	}
}

template <typename T, typename K, typename U> void t_order( T buffer, long length, K value, long vallen ) {
	int i = 0;
	int l = 0;

	if( length == vallen ) {
		while( i < vallen && l < vallen ) {
			int k = value[l];
			if( k > l ) {
				k = value[k];
				while( k > l ) {
					k = value[k];
				}
				if( k == l ) {
					U	to = buffer[(int)l];
					k = value[l];
					U	ti = buffer[(int)k];

					while( k != l ) {
						buffer[(int)k] = to;
						to = ti;
						k = value[k];
						ti = buffer[(int)k];
						i++;
					}
					buffer[(int)k] = to;
					i++;
				}
			}
			l++;
		}
	} else {
		/*while( i < m-2 && l < m ) {
			double k = fmod( (l*r), m );
			double t = fmod( (l*c), m );
			if( k > l && t > l ) {
				k = fmod( (k*r), m );
				while( k > l ) {
					k = fmod( (k*r), m );
				}
				if( k == l ) {
					T buf = buffer;
					while( dim*len < length ) {
						K	to = buf[(int)l];
						k = fmod( (l*r), m );
						K	ti = buf[(int)k];

						while( k != l ) {
							buf[(int)k] = to;
							to = ti;
							k = fmod( (k*r), m );
							ti = buf[(int)k];
							i++;
						}
						buf[(int)k] = to;
						//heyheyho
						//buf += len;
						dim++;
					}
					i++;
				}
			}
			l++;
		}*/
	}
}

template <typename T, typename K> void t_add( T buffer, int length, K value, int vallen ) {
	int len = vallen > length ? vallen : length;
	for( int i = 0; i < len; i++ ) {
		buffer[i] += value[i];
	}
}

template <typename T, typename K> void t_mul( T t, int tlen, K k, int klen ) {
	for( int i = 0; i < (tlen < klen ? tlen : klen); i++ ) {
		t[i] *= k[i];
	}
}

template <typename T, typename K> void t_sub( T buffer, int length, K value, int vallen ) {
	int len = vallen > length ? vallen : length;
	for( int i = 0; i < len; i++ ) {
		buffer[i] -= value[i];
	}
}

template <typename T, typename K> void t_div( T t, int tlen, K k, int klen ) {
	for( int i = 0; i < (tlen < klen ? tlen : klen); i++ ) {
		t[i] /= k[i];
	}
}

template <typename T, typename K> void t_mod( T t, int tlen, K k, int klen ) {
	for( int i = 0; i < (tlen < klen ? tlen : klen); i++ ) {
		t[i] = fmod( (double)t[i], (double)k[i] );
		//t[i] %= (int)k[i];
	}
}

template <typename T, typename K, typename U> class c_order {
public:
	c_order( T tbuf, int tlen, K kbuf, int klen ) {
		t_order<T,K,U>( tbuf, tlen, kbuf, klen );
	}
};

template <typename T, typename K> class c_set {
public:
	c_set( T tbuf, int tlen, K kbuf, int klen ) {
		t_set<T,K>( tbuf, tlen, kbuf, klen );
	}
};

template <typename T, typename K> class c_mul {
public:
	c_mul( T tbuf, int tlen, K kbuf, int klen ) {
		t_mul<T,K>( tbuf, tlen, kbuf, klen );
	}
};

template <typename T, typename K> class c_add {
public:
	c_add( T tbuf, int tlen, K kbuf, int klen ) {
		t_add<T,K>( tbuf, tlen, kbuf, klen );
	}
};

template <typename T, typename K> class c_sub {
public:
	c_sub( T tbuf, int tlen, K kbuf, int klen ) {
		t_sub<T,K>( tbuf, tlen, kbuf, klen );
	}
};

template <typename T, typename K> class c_div {
public:
	c_div( T tbuf, int tlen, K kbuf, int klen ) {
		t_div<T,K>( tbuf, tlen, kbuf, klen );
	}
};

template <typename T, typename K> class c_mod {
public:
	c_mod( T tbuf, int tlen, K kbuf, int klen ) {
		t_mod<T,K>( tbuf, tlen, kbuf, klen );
	}
};

template<template<typename T, typename V, typename U> class c_func, typename K> void suborder( K kbuf, long long klen ) {
	//printf("subarith\n");
	if( data.length == -1 ) {

	} else if( data.length == 0 ) {
		/*if( data.type == 66 ) c_func<double*,K>( (double*)&data.buffer, 1, kbuf, klen );
		else if( data.type == 65 ) c_func<long long*,K>( (long long*)&data.buffer, 1, kbuf, klen );
		else if( data.type == 64 ) c_func<unsigned long long*,K>( (unsigned long long*)&data.buffer, 1, kbuf, klen );
		else if( data.type == 34 ) c_func<float*,K>( (float*)&data.buffer, 1, kbuf, klen );
		else if( data.type == 33 ) c_func<int*,K>( (int*)&data.buffer, 1, kbuf, klen );
		else if( data.type == 32 ) {
			c_func<unsigned int*,K>( (unsigned int*)&data.buffer, 1, kbuf, klen );
		}
		else if( data.type == 16 ) c_func<short*,K>( (short*)&data.buffer, 1, kbuf, klen );
	} else if( data.type < 0 ) {
		if( data.type == -66 ) c_func< c_simlab<double&>&,K >( *(c_simlab<double&>*)data.buffer, data.length, kbuf, klen );
		else if( data.type == -65 ) c_func< c_simlab<long long&>&,K >( *(c_simlab<long long&>*)data.buffer, data.length, kbuf, klen );
		else if( data.type == -64 ) c_func< c_simlab<unsigned long long&>&,K >( *(c_simlab<unsigned long long&>*)data.buffer, data.length, kbuf, klen );
		else if( data.type == -34 ) c_func< c_simlab<float&>&,K >( *(c_simlab<float&>*)data.buffer, data.length, kbuf, klen );
		else if( data.type == -33 ) c_func< c_simlab<int&>&,K >( *(c_simlab<int&>*)data.buffer, data.length, kbuf, klen );
		else if( data.type == -32 ) c_func< c_simlab<unsigned int&>&,K >( *(c_simlab<unsigned int&>*)data.buffer, data.length, kbuf, klen );
		else if( data.type == -16 ) c_func< c_simlab<short&>&,K >( *(c_simlab<short&>*)data.buffer, data.length, kbuf, klen );*/
	} else {
		if( data.type == 66 ) c_func<double*,K,double>( (double*)data.buffer, data.length, kbuf, klen );
		else if( data.type == 65 ) c_func<long long*,K,long long>( (long long*)data.buffer, data.length, kbuf, klen );
		else if( data.type == 64 ) c_func<unsigned long long*,K,unsigned long long>( (unsigned long long*)data.buffer, data.length, kbuf, klen );
		else if( data.type == 34 ) c_func<float*,K,float>( (float*)data.buffer, data.length, kbuf, klen );
		else if( data.type == 33 ) c_func<int*,K,int>( (int*)data.buffer, data.length, kbuf, klen );
		else if( data.type == 32 ) c_func<unsigned int*,K,unsigned int>( (unsigned int*)data.buffer, data.length, kbuf, klen );
		else if( data.type == 17 ) c_func<short*,K,short>( (short*)data.buffer, data.length, kbuf, klen );
		else if( data.type == 16 ) c_func<unsigned short*,K,unsigned short>( (unsigned short*)data.buffer, data.length, kbuf, klen );
		else if( data.type == 9 ) c_func<char*,K,char>( (char*)data.buffer, data.length, kbuf, klen );
		else if( data.type == 8 ) c_func<unsigned char*,K,unsigned char>( (unsigned char*)data.buffer, data.length, kbuf, klen );
	}
}

template<template<typename T, typename K, typename U> class c_func> void order( simlab & value ) {
	//printf("arith\n");
	if( value.length == 0 ) {
		if( value.type == 32 ) {
			c_const<unsigned int>	sl( *(unsigned int*)&value.buffer );
			suborder< c_func, c_simlab<unsigned int>& >( sl, data.length );
		} else if( value.type == 33 ) {
			c_const<int>	sl( *(int*)&value.buffer );
			suborder< c_func, c_simlab<int>& >( sl, data.length );
		} else if( value.type == 34 ) {
			c_const<float>	sl( *(float*)&value.buffer );
			suborder< c_func, c_simlab<float>& >( sl, data.length );
		} else if( value.type == 64 ) {
			c_const<unsigned long long>	sl( *(unsigned long long*)&value.buffer );
			suborder< c_func, c_simlab<unsigned long long>& >( sl, data.length );
		} else if( value.type == 65 ) {
			c_const<long long>	sl( *(long long*)&value.buffer );
			suborder< c_func, c_simlab<long long>& >( sl, data.length );
		} else if( value.type == 66 ) {
			c_const<double>	sl( *(double*)&value.buffer );
			suborder< c_func, c_simlab<double>& >( sl, data.length );
		}
	} else if( value.length == -1 ) {
		if( value.type == -66 ) suborder< c_func, c_simlab<double>& >( *(c_simlab<double>*)value.buffer, data.length );
		else if( value.type == -65 ) suborder< c_func, c_simlab<long long>& >( *((c_simlab<long long>*)value.buffer), data.length );
		else if( value.type == -64 ) suborder< c_func, c_simlab<unsigned long long>& >( *((c_simlab<unsigned long long>*)value.buffer), data.length );
		else if( value.type == -34 ) suborder< c_func, c_simlab<float>& >( *((c_simlab<float>*)value.buffer), data.length );
		else if( value.type == -33 ) suborder< c_func, c_simlab<int>& >( *((c_simlab<int>*)value.buffer), data.length );
		else if( value.type == -32 ) suborder< c_func, c_simlab<unsigned int>& >( *((c_simlab<unsigned int>*)value.buffer), data.length );
		else if( value.type == -16 ) suborder< c_func, c_simlab<short>& >( *((c_simlab<short>*)value.buffer), data.length );
	} else {
		if( value.type == 66 ) suborder< c_func, double* >( (double*)value.buffer, data.length );
		else if( value.type == 65 ) suborder< c_func, long long* >( (long long*)value.buffer, data.length );
		else if( value.type == 64 ) suborder< c_func, unsigned long long* >( (unsigned long long*)value.buffer, data.length );
		else if( value.type == 34 ) suborder< c_func, float* >( (float*)value.buffer, data.length );
		else if( value.type == 33 ) suborder< c_func, int* >( (int*)value.buffer, data.length );
		else if( value.type == 32 ) suborder< c_func, unsigned int* >( (unsigned int*)value.buffer, data.length );
		else if( value.type == 17 ) suborder< c_func, short* >( (short*)value.buffer, data.length );
		else if( value.type == 16 ) suborder< c_func, unsigned short* >( (unsigned short*)value.buffer, data.length );
		else if( value.type == 9 ) suborder< c_func, char* >( (char*)value.buffer, data.length );
		else if( value.type == 8 ) suborder< c_func, unsigned char* >( (unsigned char*)value.buffer, data.length );
	}
}

template<template<typename T, typename V> class c_func, typename K> void subarith( K kbuf, long klen ) {
	//printf("subarith\n");
	if( data.length == -1 ) {

	} else if( data.length == 0 ) {
		if( data.type == 66 ) c_func<double*,K>( (double*)&data.buffer, 1, kbuf, klen );
		else if( data.type == 65 ) c_func<long long*,K>( (long long*)&data.buffer, 1, kbuf, klen );
		else if( data.type == 64 ) c_func<unsigned long long*,K>( (unsigned long long*)&data.buffer, 1, kbuf, klen );
		else if( data.type == 34 ) c_func<float*,K>( (float*)&data.buffer, 1, kbuf, klen );
		else if( data.type == 33 ) c_func<int*,K>( (int*)&data.buffer, 1, kbuf, klen );
		else if( data.type == 32 ) {
			c_func<unsigned int*,K>( (unsigned int*)&data.buffer, 1, kbuf, klen );
		}
		else if( data.type == 16 ) c_func<short*,K>( (short*)&data.buffer, 1, kbuf, klen );
	} else if( data.type < 0 ) {
		if( data.type == -66 ) c_func< c_simlab<double&>&,K >( *(c_simlab<double&>*)data.buffer, data.length, kbuf, klen );
		else if( data.type == -65 ) c_func< c_simlab<long long&>&,K >( *(c_simlab<long long&>*)data.buffer, data.length, kbuf, klen );
		else if( data.type == -64 ) c_func< c_simlab<unsigned long long&>&,K >( *(c_simlab<unsigned long long&>*)data.buffer, data.length, kbuf, klen );
		else if( data.type == -34 ) c_func< c_simlab<float&>&,K >( *(c_simlab<float&>*)data.buffer, data.length, kbuf, klen );
		else if( data.type == -33 ) c_func< c_simlab<int&>&,K >( *(c_simlab<int&>*)data.buffer, data.length, kbuf, klen );
		else if( data.type == -32 ) c_func< c_simlab<unsigned int&>&,K >( *(c_simlab<unsigned int&>*)data.buffer, data.length, kbuf, klen );
		else if( data.type == -16 ) c_func< c_simlab<short&>&,K >( *(c_simlab<short&>*)data.buffer, data.length, kbuf, klen );
	} else {
		if( data.type == 66 ) c_func<double*,K>( (double*)data.buffer, data.length, kbuf, klen );
		else if( data.type == 65 ) c_func<long long*,K>( (long long*)data.buffer, data.length, kbuf, klen );
		else if( data.type == 64 ) c_func<unsigned long long*,K>( (unsigned long long*)data.buffer, data.length, kbuf, klen );
		else if( data.type == 34 ) c_func<float*,K>( (float*)data.buffer, data.length, kbuf, klen );
		else if( data.type == 33 ) c_func<int*,K>( (int*)data.buffer, data.length, kbuf, klen );
		else if( data.type == 32 ) c_func<unsigned int*,K>( (unsigned int*)data.buffer, data.length, kbuf, klen );
		else if( data.type == 17 ) c_func<short*,K>( (short*)data.buffer, data.length, kbuf, klen );
		else if( data.type == 16 ) c_func<unsigned short*,K>( (unsigned short*)data.buffer, data.length, kbuf, klen );
		else if( data.type == 9 ) c_func<char*,K>( (char*)data.buffer, data.length, kbuf, klen );
		else if( data.type == 8 ) c_func<unsigned char*,K>( (unsigned char*)data.buffer, data.length, kbuf, klen );
	}
}

template<template<typename T, typename K> class c_func> void arith( simlab & value ) {
	//printf("arith\n");
	if( value.length == 0 ) {
		if( value.type == 32 ) {
			c_const<unsigned int>	sl( *(unsigned int*)&value.buffer );
			subarith< c_func, c_simlab<unsigned int>& >( sl, data.length );
		} else if( value.type == 33 ) {
			c_const<int>	sl( *(int*)&value.buffer );
			subarith< c_func, c_simlab<int>& >( sl, data.length );
		} else if( value.type == 34 ) {
			c_const<float>	sl( *(float*)&value.buffer );
			subarith< c_func, c_simlab<float>& >( sl, data.length );
		} else if( value.type == 64 ) {
			c_const<unsigned long long>	sl( *(unsigned long long*)&value.buffer );
			subarith< c_func, c_simlab<unsigned long long>& >( sl, data.length );
		} else if( value.type == 65 ) {
			c_const<long long>	sl( *(long long*)&value.buffer );
			subarith< c_func, c_simlab<long long>& >( sl, data.length );
		} else if( value.type == 66 ) {
			c_const<double>	sl( *(double*)&value.buffer );
			subarith< c_func, c_simlab<double>& >( sl, data.length );
		}
	} else if( value.length == -1 ) {
		if( value.type == -66 ) subarith< c_func, c_simlab<double>& >( *(c_simlab<double>*)value.buffer, data.length );
		else if( value.type == -65 ) subarith< c_func, c_simlab<long long>& >( *((c_simlab<long long>*)value.buffer), data.length );
		else if( value.type == -64 ) subarith< c_func, c_simlab<unsigned long long>& >( *((c_simlab<unsigned long long>*)value.buffer), data.length );
		else if( value.type == -34 ) subarith< c_func, c_simlab<float>& >( *((c_simlab<float>*)value.buffer), data.length );
		else if( value.type == -33 ) subarith< c_func, c_simlab<int>& >( *((c_simlab<int>*)value.buffer), data.length );
		else if( value.type == -32 ) subarith< c_func, c_simlab<unsigned int>& >( *((c_simlab<unsigned int>*)value.buffer), data.length );
		else if( value.type == -16 ) subarith< c_func, c_simlab<short>& >( *((c_simlab<short>*)value.buffer), data.length );
	} else {
		if( value.type == 66 ) subarith< c_func, double* >( (double*)value.buffer, data.length );
		else if( value.type == 65 ) subarith< c_func, long long* >( (long long*)value.buffer, data.length );
		else if( value.type == 64 ) subarith< c_func, unsigned long long* >( (unsigned long long*)value.buffer, data.length );
		else if( value.type == 34 ) subarith< c_func, float* >( (float*)value.buffer, data.length );
		else if( value.type == 33 ) subarith< c_func, int* >( (int*)value.buffer, data.length );
		else if( value.type == 32 ) subarith< c_func, unsigned int* >( (unsigned int*)value.buffer, data.length );
		else if( value.type == 17 ) subarith< c_func, short* >( (short*)value.buffer, data.length );
		else if( value.type == 16 ) subarith< c_func, unsigned short* >( (unsigned short*)value.buffer, data.length );
		else if( value.type == 9 ) subarith< c_func, char* >( (char*)value.buffer, data.length );
		else if( value.type == 8 ) subarith< c_func, unsigned char* >( (unsigned char*)value.buffer, data.length );
	}
}

template<template<typename T, typename V> class c_func, typename K> void subiarith( K kbuf, int klen ) {
	if( data.length == -1 ) {

	} else if( data.type < 0 ) {
		if( data.type == -32 ) c_func< c_simlab<unsigned int&>&,K >( *(c_simlab<unsigned int&>*)data.buffer, data.length, kbuf, klen );
		else if( data.type == -16 ) c_func< c_simlab<short&>&,K >( *(c_simlab<short&>*)data.buffer, data.length, kbuf, klen );
		else if( data.type == -33 ) c_func< c_simlab<int&>&,K >( *(c_simlab<int&>*)data.buffer, data.length, kbuf, klen );
		else if( data.type == -34 ) c_func< c_simlab<float&>&,K >( *(c_simlab<float&>*)data.buffer, data.length, kbuf, klen );
		else if( data.type == -64 ) c_func< c_simlab<unsigned long long&>&,K >( *(c_simlab<unsigned long long&>*)data.buffer, data.length, kbuf, klen );
		else if( data.type == -65 ) c_func< c_simlab<long long&>&,K >( *(c_simlab<long long&>*)data.buffer, data.length, kbuf, klen );
		else if( data.type == -66 ) c_func< c_simlab<double&>&,K >( *(c_simlab<double&>*)data.buffer, data.length, kbuf, klen );
	} else {
		if( data.type == 32 ) c_func<unsigned int*,K>( (unsigned int*)data.buffer, data.length, kbuf, klen );
		else if( data.type == 16 ) c_func<short*,K>( (short*)data.buffer, data.length, kbuf, klen );
		else if( data.type == 33 ) c_func<int*,K>( (int*)data.buffer, data.length, kbuf, klen );
		else if( data.type == 34 ) c_func<float*,K>( (float*)data.buffer, data.length, kbuf, klen );
		else if( data.type == 64 ) c_func<unsigned long long*,K>( (unsigned long long*)data.buffer, data.length, kbuf, klen );
		else if( data.type == 65 ) c_func<long long*,K>( (long long*)data.buffer, data.length, kbuf, klen );
		else if( data.type == 66 ) c_func<double*,K>( (double*)data.buffer, data.length, kbuf, klen );
	}
}

template<template<typename T, typename K> class c_func> void iarith( simlab & value ) {
	if( value.length == 0 ) {
		if( value.type == 32 ) {
			c_const<int>	sl( *(int*)&value.buffer );
			subiarith< c_func, c_simlab<int>& >( sl, data.length );
		}
	} else if( value.length == -1 ) {
		if( value.type == -32 ) subiarith< c_func, c_simlab<int>& >( *((c_simlab<int>*)value.buffer), data.length );
		else if( value.type == -16 ) subiarith< c_func, c_simlab<short>& >( *((c_simlab<short>*)value.buffer), data.length );
	} else {
		if( value.type == 32 ) subiarith< c_func, int* >( (int*)value.buffer, data.length );
		else if( value.type == 16 ) subiarith< c_func, short* >( (short*)value.buffer, data.length );
	}
}

JNIEXPORT int simmi( simlab value ) {
	printf("erm %d %d %d %d\n", (int)value.buffer, (int)data.buffer, (int)data.type, (int)data.length );

	return 1;
}

JNIEXPORT int reorder( simlab value ) {
	order< c_order >( value );

	return 1;
}

JNIEXPORT int set( simlab value ) {
	arith< c_set >( value );

	return 1;
}

JNIEXPORT int add( simlab value ) {
	arith< c_add >( value );
	return 1;
}

JNIEXPORT int mul( simlab value ) {
	arith< c_mul >( value );
	return 1;
}

JNIEXPORT int sub( simlab value ) {
	arith< c_sub >( value );
	return 1;
}

JNIEXPORT int simlab_div( simlab value ) {
	arith< c_div >( value );
	return 1;
}

JNIEXPORT int mod( simlab value ) {
	arith< c_mod >( value );
	return 1;
}
