#include <jni.h>
#include "eeg/com_digidactylus_oroserver_JNIWrap.h"
#include "base/SVector.h"
#include "util/mutil.h"
#include "eeg/Analyze.h"
#include <memory>
#include <functional>
#include <iostream>

#include <cstring>

using std::string;
using std::function;
using std::unique_ptr;
using std::shared_ptr;
using std::cout;
using std::endl;

// Helper classes for JNI
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
      m_env->ReleaseStringUTFChars(m_jstr, cstr);
    }

};

// Java string to C/C++ string
const string ToString(JNIEnv *env, jstring jstr)
{
  jstring_deleter deleter(env, jstr);     // using a function object
  unique_ptr<const char, jstring_deleter> pcstr(
                                                env->GetStringUTFChars(jstr, JNI_FALSE),
                                                deleter );
  
  return string( pcstr.get() );
}

// Java string to C/C++ string pointer
shared_ptr<const char> ToStringPtr(JNIEnv *env, jstring jstr)
{
  function<void(const char*)> deleter =   // using a lambda
    [env, jstr](const char *cstr) -> void
    {
    
      env->ReleaseStringUTFChars(jstr, cstr);
    };
  
  return shared_ptr<const char>(
                                env->GetStringUTFChars(jstr, JNI_FALSE),
                                deleter );
}



/*
 * Class:     com_methority_btbrowser2_JNIWrap
 * Method:    callAnalysisJNI
 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;I)I
 */
JNIEXPORT jint JNICALL Java_com_digidactylus_oroserver_JNIWrap_callAnalysisJNI
  (JNIEnv * env, jobject, jstring in, jstring out, jstring mout, jstring min, jstring tb, jstring hint, jstring conf, jint s)
{

  string fileName =   ToString(env, in);
  string resultName = ToString(env, out);
  string outName =    ToString(env, mout);
  string miName =     ToString(env, min);
  string tbName =     ToString(env, tb);
  string hintName =   ToString(env, hint);
  string confName =   ToString(env, conf);
  int split = s;

  int r = MainAnalyze(fileName,
		      resultName,
		      outName,
		      miName,
		      tbName,
		      hintName,
		      confName,
		      split);
  return r;
}
