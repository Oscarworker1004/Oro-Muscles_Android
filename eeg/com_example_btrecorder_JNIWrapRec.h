/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_example_btrecorder_JNIWrapRec */

#ifndef _Included_com_example_btrecorder_JNIWrapRec
#define _Included_com_example_btrecorder_JNIWrapRec
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_example_btrecorder_JNIWrapRec
 * Method:    callStartRecUSB
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_com_example_btrecorder_JNIWrapRec_callStartRecUSB
  (JNIEnv *, jobject, jstring);

/*
 * Class:     com_example_btrecorder_JNIWrapRec
 * Method:    callStopRecUSB
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_com_example_btrecorder_JNIWrapRec_callStopRecUSB
  (JNIEnv *, jobject, jstring);

/*
 * Class:     com_example_btrecorder_JNIWrapRec
 * Method:    callStartVideo
 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_com_example_btrecorder_JNIWrapRec_callStartVideo
  (JNIEnv *, jobject, jstring, jstring, jstring, jstring);

/*
 * Class:     com_example_btrecorder_JNIWrapRec
 * Method:    callStopVideo
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_com_example_btrecorder_JNIWrapRec_callStopVideo
  (JNIEnv *, jobject, jstring);

/*
 * Class:     com_example_btrecorder_JNIWrapRec
 * Method:    callGetFrames
 * Signature: (I)[D
 */
JNIEXPORT jdoubleArray JNICALL Java_com_example_btrecorder_JNIWrapRec_callGetFrames
  (JNIEnv *, jobject, jint);

#ifdef __cplusplus
}
#endif
#endif
