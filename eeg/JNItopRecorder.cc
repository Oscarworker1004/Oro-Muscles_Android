#include <memory>
#include <functional>
#include <iostream>
#include <fstream>

#include <jni.h>

#include "base/SVector.h"
#include "src/RecordCtrl.h"
#include "eeg/com_digidactylus_recorder_JNIWrapRec.h"   // auto-generated by `javac -h`

using std::string;
using std::function;
using std::unique_ptr;
using std::shared_ptr;
using std::cout;
using std::endl;


// Global object

RecordCtrl gRecCtrl;

class jstring_deleter
{
    JNIEnv *m_env;
    jstring m_jstr;

public:

    jstring_deleter(JNIEnv *env, jstring jstr)
        : m_env(env)
        , m_jstr(jstr)
    {
    }

    void operator()(const char *cstr)
    {
        cout << "[DEBUG] Releasing " << cstr << endl;
        m_env->ReleaseStringUTFChars(m_jstr, cstr);
    }

};

const string ToString(JNIEnv *env, jstring jstr)
{
    jstring_deleter deleter(env, jstr);     // using a function object
    unique_ptr<const char, jstring_deleter> pcstr(
            env->GetStringUTFChars(jstr, JNI_FALSE),
            deleter );

    return string( pcstr.get() );
}
shared_ptr<const char> ToStringPtr(JNIEnv *env, jstring jstr)
{
    function<void(const char*)> deleter =   // using a lambda
        [env, jstr](const char *cstr) -> void
        {
            cout << "[DEBUG] Releasing " << cstr << endl;
            env->ReleaseStringUTFChars(jstr, cstr);
        };

    return shared_ptr<const char>(
            env->GetStringUTFChars(jstr, JNI_FALSE),
            deleter );
}



/*
 * Class:     com_digidactylus_recorder_JNIWrapRec
 * Method:    callStartRecUSB
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_com_digidactylus_recorder_JNIWrapRec_callStartRecUSB
  (JNIEnv * env, jobject obj, jstring info)
{

  string inf = ToString(env, info);
  if (inf != "")
    gRecCtrl.SetFileName(inf);
  gRecCtrl.StartRec();
  
  return 0;
}

/*
 * Class:     com_digidactylus_recorder_JNIWrapRec
 * Method:    callStopRecUSB
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_com_digidactylus_recorder_JNIWrapRec_callStopRecUSB
  (JNIEnv * env, jobject obj, jstring info)
{
  gRecCtrl.StopRec();
  return 0;
}

/*
 * Class:     com_digidactylus_recorder_JNIWrapRec
 * Method:    callStartVideo
 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_com_digidactylus_recorder_JNIWrapRec_callStartVideo
  (JNIEnv * env, jobject obj, jstring device, jstring url, jstring fileName, jstring info)
{
  //FILE * p = fopen("test.txt", "w");
  //FILE * p = fopen("/sdcard/DCIM/Documents/test.txt", "w");
  //FILE * p = fopen("/data/user/0/com.digidactylus.recorder/files/ctest.txt", "w");

  //if (p == NULL)
  //FILE * p = fopen("/data/data/com.digidactylus.recorder/files/ctest-live.txt", "w");
  string fn = ToString(env, fileName);
  FILE * p = fopen(fn.c_str(), "w");

  fprintf(p, "This is a TEST of the LIVE System!!!\n");
  
  fclose(p);
  
  /*
  float mydata[4] = {0.0f};

  ofstream file;
  file.open("/sdcard/DCIM/Documents/Data.txt", ios::app);

  // For example
  file<<mydata[1];

  file.close();
  */
  
  return 0;
}

/*
 * Class:     com_digidactylus_recorder_JNIWrapRec
 * Method:    callStopVideo
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_com_digidactylus_recorder_JNIWrapRec_callStopVideo
  (JNIEnv * env, jobject obj, jstring info)
{
  cout << "Loging" << endl;
  return 0;
}

/*
 * Class:     com_digidactylus_recorder_JNIWrapRec
 * Method:    callGetFrames
 * Signature: (I)[D
 */
JNIEXPORT jdoubleArray JNICALL Java_com_digidactylus_recorder_JNIWrapRec_callGetFrames
  (JNIEnv * env, jobject obj, jint num)
{

  int i;
  
  svec<double> out;
  //out.resize(num, 0.);
  try {
    int chunks = gRecCtrl.GetData(out, num);
    //
  }

  catch (...) {
  }
  
  
  int nsize = out.isize();

  if (nsize == 0)
    return NULL;

  jdoubleArray result = env->NewDoubleArray(nsize);

  jdouble *data = (jdouble *)malloc(nsize*sizeof(jdouble));

  for (i=0; i<nsize; i++) {
    data[i] = out[i];
  }
  
  env->SetDoubleArrayRegion(result, 0, nsize, data);
  
  free(data);
      
  return result;
}


//=============================================================


/*
 * Class:     com_digidactylus_recorder_JNIWrapRec
 * Method:    callSetDataStream
 * Signature: ([BI)V
 */
JNIEXPORT void JNICALL Java_com_digidactylus_recorder_JNIWrapRec_callSetDataStream
  (JNIEnv * env, jobject obj, jbyteArray array, jint len)
{
  jbyte *lib;                                                                                             
                                       
  lib = env->GetByteArrayElements(array, 0);                                                                     
  
  //int ssize = env->GetArrayLength(array);

  svec<char> buf;
  buf.resize(len, 0);
  int i;
                                      
  for (i=0; i<len; i++)
    buf[i] = lib[i];

  
  gRecCtrl.SetDataBuffer(buf);

  env->ReleaseByteArrayElements(array, lib, JNI_ABORT);

}

/*
 * Class:     com_digidactylus_recorder_JNIWrapRec
 * Method:    callSaveCSVData
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_digidactylus_recorder_JNIWrapRec_callSaveCSVData
  (JNIEnv * env, jobject obj, jstring name)
{
  string fileName = ToString(env, name);
  gRecCtrl.WriteCSV(fileName);
}


//=============================================================================
/*
 * Class:     com_digidactylus_recorder_JNIWrapRec
 * Method:    callSetWorkingDir
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_digidactylus_recorder_JNIWrapRec_callSetWorkingDir
  (JNIEnv * env, jobject obj , jstring dir)
{
}

/*
 * Class:     com_digidactylus_recorder_JNIWrapRec
 * Method:    callSaveAsCSV
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_digidactylus_recorder_JNIWrapRec_callSaveAsCSV
  (JNIEnv * env, jobject obj, jstring name)
{
}

/*
 * Class:     com_digidactylus_recorder_JNIWrapRec
 * Method:    callProcessData
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_digidactylus_recorder_JNIWrapRec_callProcessData
  (JNIEnv * env, jobject obj, jstring name)
{
}

/*
 * Class:     com_digidactylus_recorder_JNIWrapRec
 * Method:    callFeedData
 * Signature: ([BI)V
 */
JNIEXPORT void JNICALL Java_com_digidactylus_recorder_JNIWrapRec_callFeedData
  (JNIEnv * env, jobject obj, jbyteArray array, jint len)
{
  if (len < 0) {
    gRecCtrl.Reset();
    return;
  }
  
  jbyte *lib;                                                                                             
                                       
  lib = env->GetByteArrayElements(array, 0);                                                                     
  
  //int ssize = env->GetArrayLength(array);

  svec<char> buf;
  buf.resize(len, 0);
  int i;
                                      
  for (i=0; i<len; i++)
    buf[i] = lib[i];

  
  gRecCtrl.SetDataBuffer(buf);

  env->ReleaseByteArrayElements(array, lib, JNI_ABORT);


}

/*
 * Class:     com_digidactylus_recorder_JNIWrapRec
 * Method:    callGetProcessed
 * Signature: ()[I
 */
JNIEXPORT jdoubleArray JNICALL Java_com_digidactylus_recorder_JNIWrapRec_callGetProcessed
(JNIEnv * env, jobject obj, jint n)
{
  int i;
  
  svec<double> out;
  //out.resize(num, 0.);
  try {
    int chunks = gRecCtrl.GetData(out, n);
    //
  }

  catch (...) {
  }
  
  
  int nsize = out.isize();

  if (nsize == 0)
    return NULL;

  jdoubleArray result = env->NewDoubleArray(nsize);

  jdouble *data = (jdouble *)malloc(nsize*sizeof(jdouble));

  for (i=0; i<nsize; i++) {
    data[i] = out[i];
  }
  
  env->SetDoubleArrayRegion(result, 0, nsize, data);
  
  free(data);
      
  return result;

}



/*
 * Class:     com_digidactylus_recorder_JNIWrapRec
 * Method:    callReset
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_digidactylus_recorder_JNIWrapRec_callReset
  (JNIEnv * env, jobject obj)
{
  gRecCtrl.Reset();
}

/*
 * Class:     com_digidactylus_recorder_JNIWrapRec
 * Method:    callIsDataValid
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_digidactylus_recorder_JNIWrapRec_callIsDataValid
  (JNIEnv * env, jobject obj)
{
  bool b = gRecCtrl.IsGood();

  if (b)
    return 1;
  else
    return 0;
}