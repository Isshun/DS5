#include "JNIBridge.hpp"

JNIBridge::JNIBridge(JNIEnv *env, jobject *thisObj) {
  this->env = env;
  this->thisObj = thisObj;
}

void JNIBridge::update() {
   printf("0\n");
   jclass thisClass = (env)->GetObjectClass(*thisObj);
     // Get the Method ID for method "callback", which takes no arg and return void
   jmethodID midCallBack = (env)->GetMethodID(thisClass, "callback", "()V");
   printf("1\n");

   if (NULL == midCallBack) return;
   printf("In C, call back Java's callback()\n");
   // Call back the method (which returns void), baed on the Method ID

      jfieldID fidNumber = (env)->GetFieldID(thisClass, "number", "I");
   if (NULL == fidNumber) return;
 
   printf("2\n");
   
   // Get the int given the Field ID
   jint number = (env)->GetIntField(*thisObj, fidNumber);
   printf("In C, the int is %d\n", number);
 
   // Change the variable
   number++;
   (env)->SetIntField(*thisObj, fidNumber, number);

   printf("3\n");
   
    jmethodID mid = env->GetMethodID( thisClass, "getMap", "()[I");
    jobject mydata = env->CallObjectMethod(*thisObj, mid);
    jintArray * arr = reinterpret_cast<jintArray*>(&mydata);
    long * data = env->GetIntArrayElements(*arr, NULL);
    env->ReleaseIntArrayElements(*arr, data, 0); 

    printf("4\n");
   
   (env)->CallVoidMethod(*thisObj, midCallBack);
}