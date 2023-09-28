/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_digidactylus_recorder_JNIWrapRec */

#ifndef _Included_com_digidactylus_recorder_JNIWrapRec
#define _Included_com_digidactylus_recorder_JNIWrapRec
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_digidactylus_recorder_JNIWrapRec
 * Method:    callStartRecUSB
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_com_digidactylus_recorder_JNIWrapRec_callStartRecUSB
  (JNIEnv *, jobject, jstring);

/*
 * Class:     com_digidactylus_recorder_JNIWrapRec
 * Method:    callStopRecUSB
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_com_digidactylus_recorder_JNIWrapRec_callStopRecUSB
  (JNIEnv *, jobject, jstring);

/*
 * Class:     com_digidactylus_recorder_JNIWrapRec
 * Method:    callStartVideo
 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_com_digidactylus_recorder_JNIWrapRec_callStartVideo
  (JNIEnv *, jobject, jstring, jstring, jstring, jstring);

/*
 * Class:     com_digidactylus_recorder_JNIWrapRec
 * Method:    callStopVideo
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_com_digidactylus_recorder_JNIWrapRec_callStopVideo
  (JNIEnv *, jobject, jstring);

/*
 * Class:     com_digidactylus_recorder_JNIWrapRec
 * Method:    callGetFrames
 * Signature: (I)[D
 */
JNIEXPORT jdoubleArray JNICALL Java_com_digidactylus_recorder_JNIWrapRec_callGetFrames
  (JNIEnv *, jobject, jint);

/*
 * Class:     com_digidactylus_recorder_JNIWrapRec
 * Method:    callReset
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_digidactylus_recorder_JNIWrapRec_callReset
  (JNIEnv *, jobject);

/*
 * Class:     com_digidactylus_recorder_JNIWrapRec
 * Method:    callIsDataValid
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_digidactylus_recorder_JNIWrapRec_callIsDataValid
  (JNIEnv *, jobject);

/*
 * Class:     com_digidactylus_recorder_JNIWrapRec
 * Method:    callSetWorkingDir
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_digidactylus_recorder_JNIWrapRec_callSetWorkingDir
  (JNIEnv *, jobject, jstring);

/*
 * Class:     com_digidactylus_recorder_JNIWrapRec
 * Method:    callSaveAsCSV
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_digidactylus_recorder_JNIWrapRec_callSaveAsCSV
  (JNIEnv *, jobject, jstring);

/*
 * Class:     com_digidactylus_recorder_JNIWrapRec
 * Method:    callProcessData
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_digidactylus_recorder_JNIWrapRec_callProcessData
  (JNIEnv *, jobject, jstring);

/*
 * Class:     com_digidactylus_recorder_JNIWrapRec
 * Method:    callFeedData
 * Signature: ([BI)V
 */
JNIEXPORT void JNICALL Java_com_digidactylus_recorder_JNIWrapRec_callFeedData
  (JNIEnv *, jobject, jbyteArray, jint);

/*
 * Class:     com_digidactylus_recorder_JNIWrapRec
 * Method:    callGetProcessed
 * Signature: (I)[D
 */
JNIEXPORT jdoubleArray JNICALL Java_com_digidactylus_recorder_JNIWrapRec_callGetProcessed
  (JNIEnv *, jobject, jint);

#ifdef __cplusplus
}
#endif
#endif
