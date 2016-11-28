package org.smallbox.faraway.client.ui;

import org.smallbox.faraway.core.engine.GameEventListener;

/**
 * Created by Alex on 23/07/2016.
 */
public class MouseEvent {
    public boolean consumed;
    public int x;
    public int y;
    public GameEventListener.MouseButton button;
    public GameEventListener.Action action;

    public MouseEvent(int x, int y, GameEventListener.MouseButton button, GameEventListener.Action action) {
        this.x = x;
        this.y = y;
        this.button = button;
        this.action = action;
    }

    public MouseEvent() {
    }
}
