#include <jni.h> 
#include <stdio.h> 
#include "TestJNI.h" 
#include "Render.hpp"
#include "JNIBridge.hpp"

void display( long * data ) {
}

JNIEXPORT void JNICALL Java_alone_in_deepspace_Main_init(JNIEnv *env, jobject thisObj) 
{ 
  JNIBridge bridge = JNIBridge(env, &thisObj);

  Render render = Render(&bridge);
  render.init();

  return; 
}
