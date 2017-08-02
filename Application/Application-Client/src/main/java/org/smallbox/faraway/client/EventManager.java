package org.smallbox.faraway.client;

/**
 * Created by Alex on 16/07/2017.
 */
public interface EventManager {
    boolean onMousePress(int x, int y, int button);
    boolean onMouseRelease(int x, int y, int button);
    boolean onMouseMove(int x, int y, boolean pressed);
}
