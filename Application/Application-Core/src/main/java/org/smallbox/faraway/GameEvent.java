package org.smallbox.faraway;

import org.smallbox.faraway.core.engine.GameEventListener;

/**
 * Created by Alex on 16/08/2016.
 */
public class GameEvent {
    public MouseEvent mouseEvent;
    public boolean consumed;

    public GameEvent(MouseEvent mouseEvent) {
        this.mouseEvent = mouseEvent;
        this.consumed = false;
    }

    public GameEvent(GameEventListener.Key key) {
        this.consumed = false;
    }

    public void consume() {
        this.consumed = true;
    }

    public boolean isAlive() {
        return !this.consumed;
    }
}
