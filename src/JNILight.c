#include <jni.h>
#include <stdio.h>
#include "alone_in_deepspace_JNILight.h"
 
JNIEXPORT void JNICALL
Java_alone_in_deepspace_JNILight_helloJNI(JNIEnv *env, jobject obj)
{
	printf("Hello\n");
	return;
}