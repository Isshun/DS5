package org.smallbox.faraway.client.ui.engine;

import org.smallbox.faraway.client.ui.engine.views.widgets.View;

/**
 * Created by Alex on 16/08/2016.
 */
public class GameEvent {
    public boolean consumed;
    public View view;

    public GameEvent(int key) {
        this.consumed = false;
    }

    public void consume() {
        this.consumed = true;
    }

    public boolean isAlive() {
        return !this.consumed;
    }
}
