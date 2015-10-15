package org.smallbox.faraway;

import com.badlogic.gdx.math.Rectangle;

/**
 * Created by Alex on 15/10/2015.
 */
public class GraphicInfo {
    public int 							spriteId;
    public final String packageName;
    public final String path;
    public int x;
    public int y;
    public Rectangle textureRect;

    public GraphicInfo(String packageName, String path) {
        this.packageName = packageName;
        this.path = path;
    }
}
