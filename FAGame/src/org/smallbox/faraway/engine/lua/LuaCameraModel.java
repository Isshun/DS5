package org.smallbox.faraway.engine.lua;

import org.smallbox.faraway.game.Game;

/**
 * Created by Alex on 20/06/2015.
 */
public class LuaCameraModel {
    public void move(int x, int y) {
        Game.getInstance().getViewport().moveTo(x, y);
    }
}
