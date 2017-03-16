package org.smallbox.faraway.client.ui.engine;

import com.badlogic.gdx.Input;
import org.smallbox.faraway.MouseEvent;
import org.smallbox.faraway.client.ui.engine.views.widgets.View;
import org.smallbox.faraway.core.engine.GameEventListener;

/**
 * Created by Alex on 16/08/2016.
 */
public class GameEvent {
    public MouseEvent mouseEvent;
    public boolean consumed;
    public View view;

    public GameEvent(MouseEvent mouseEvent) {
        this.mouseEvent = mouseEvent;
        this.consumed = false;
    }

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
