package org.smallbox.faraway.game;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * Created by Alex on 31/08/2015.
 */
public class MyClassLoader extends URLClassLoader {
    public MyClassLoader(URL[] urls) {
        super(urls);
    }

    @Override
    public void addURL(URL url) {
        super.addURL(url);
    }
}
