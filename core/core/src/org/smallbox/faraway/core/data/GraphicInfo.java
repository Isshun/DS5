package org.smallbox.faraway.core.data;

import com.badlogic.gdx.math.Rectangle;

/**
 * Created by Alex on 15/10/2015.
 */
public class GraphicInfo {
    public enum Type { NONE, TERRAIN, WALL, DOOR, STRUCTURE, PLANT }

    public int              spriteId = -1;
    public final String     packageName;
    public final String     path;
    public int              x;
    public int              y;
    public int              width;
    public int              height;
    public Type             type = Type.NONE;
    public Rectangle        textureRect;

    public GraphicInfo(String packageName, String path) {
        this.packageName = packageName;
        this.path = path;
    }
}
