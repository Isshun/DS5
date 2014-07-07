#ifndef JNIBRIDGE_HPP
#define JNIBRIDGE_HPP

#include <jni.h> 

class JNIBridge
{
private:
  JNIEnv*     env;
  jobject*    thisObj;
  JNIBridge*  bridge;
  
public:
    JNIBridge(JNIEnv *env, jobject *thisObj);
    int update(long** map);
};

#endif
