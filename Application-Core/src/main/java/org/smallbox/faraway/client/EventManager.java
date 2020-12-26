package org.smallbox.faraway.client;

public interface EventManager {
    boolean onMousePress(int x, int y, int button);
    boolean onMouseRelease(int x, int y, int button);
    boolean onMouseMove(int x, int y, boolean pressed);
}
