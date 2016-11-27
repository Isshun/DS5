package org.smallbox.faraway.core.engine.lua;

import org.smallbox.faraway.core.Application;

/**
 * Created by Alex on 20/06/2015.
 */
public class LuaCameraModel {
    public void move(int x, int y) {
        Application.gameManager.getGame().getViewport().moveTo(x, y);
    }
}
