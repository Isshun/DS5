#include "JNIBridge.hpp"

JNIBridge::JNIBridge(JNIEnv *env, jobject *thisObj) {
  this->env = env;
  this->thisObj = thisObj;
}

int JNIBridge::update(long** map) {
   printf("0\n");
   jclass thisClass = (env)->GetObjectClass(*thisObj);
     // Get the Method ID for method "callback", which takes no arg and return void
/*   jmethodID midCallBack = (env)->GetMethodID(thisClass, "callback", "()V");
   printf("1\n");

   if (NULL == midCallBack) return;
   printf("In C, call back Java's callback()\n");
   // Call back the method (which returns void), baed on the Method ID
   */

      jfieldID fidNumber = (env)->GetFieldID(thisClass, "number", "I");
    //if (NULL == fidNumber) return;
 
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

    for (int x = 0; x < 250; x++) {
      for (int y = 0; y < 250; y++) {
        map[x][y] = data[x * 250 + y];
      }
    }

    env->ReleaseIntArrayElements(*arr, data, 0);

    printf("4\n");
   /*
   (env)->CallVoidMethod(*thisObj, midCallBack);
   */
   
   return number;
}

/*
// initialize the Container class
    jclass c_Container = (*env)->GetObjectClass(env, jContainer);

    // initialize the Get Parameter Map method of the Container class
    jmethodID m_GetParameterMap = (*env)->GetMethodID(env, c_Container, "getParameterMap", "()Ljava/util/Map;");

    // call said method to store the parameter map in jParameterMap
    jobject jParameterMap =  (*env)->CallObjectMethod(env, jContainer, m_GetParameterMap);

    // initialize the Map interface
    jclass c_Map = env->FindClass("java/util/Map");

    // initialize the Get Size method of Map
    jmethodID m_GetSize = (*env)->GetMethodID(env, c_Map, "size", "()I");

    // Get the Size and store it in jSize; the value of jSize should be 1
    int jSize = (*env)->CallIntMethod(env, jParameterMap, m_GetSize);
*/