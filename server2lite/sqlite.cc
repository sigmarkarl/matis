#include <sqlite3.h>
#include <jni.h>

extern "C" JNIEXPORT jint JNICALL Java_org_simmi_Server2Lite_exec( JNIEnv *env, jclass cls, jstring str ) {
    jboolean iscopy = 1;
    const char* c = env->GetStringUTFChars( str, &iscopy );

    char*   err;
    sqlite3*    pDb;
    sqlite3_open( "/home/sigmar/isgem.db", &pDb );
    sqlite3_exec( pDb, c, NULL, 0, &err);
    sqlite3_close( pDb );
    //printf( "%s\n", c );
    env->ReleaseStringUTFChars( str, c );

    return 0;
}