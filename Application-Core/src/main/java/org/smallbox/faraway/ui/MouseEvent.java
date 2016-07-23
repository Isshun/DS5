package org.smallbox.faraway.ui;

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
}
