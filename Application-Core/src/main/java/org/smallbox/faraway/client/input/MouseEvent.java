package org.smallbox.faraway.client.input;

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
