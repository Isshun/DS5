package org.smallbox.faraway.module.extra;

/**
 * Created by Alex on 08/11/2015.
 */
public class JNIBridge {
    static { System.loadLibrary("JNIBridge"); }
    public native void onAddResource();
//    public native void onAddResource(int x, int y, String path);
}