// Write C++ code here.
//
// Do not forget to dynamically load the C++ library into your application.
//
// For instance,
//
// In MainActivity.java:
//    static {
//       System.loadLibrary("recorder");
//    }
//
// Or, in MainActivity.kt:
//    companion object {
//      init {
//         System.loadLibrary("recorder")
//      }
//    }

#include <jni.h>
#include "eeg/JNItopRecorder.cc"
#include "src/RecordCtrl.cc"
#include "base/FileParser.cc"
#include "base/StringUtil.cc"
#include "base/ErrorHandling.cc"
#include "base/Config.cc"
#include "base/Processes.cc"
#include "util/mutil.cc"
#include "signal/Smooth.cc"
#include "signal/Bin2CSV.cc"
#include "signal/Filters.cc"
#include "signal/USBReader.cc"
#include "signal/VideoWrap.cc"
#include "extern/filter-c/filter.cc"
#include "extern/attitude_estimator/attitude_estimator.cc"
#include "math/Rotation.cc"
#include "src/UnivData.cc"
