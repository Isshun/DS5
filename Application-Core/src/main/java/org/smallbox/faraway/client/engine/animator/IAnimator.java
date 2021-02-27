package org.smallbox.faraway.client.engine.animator;

import com.badlogic.gdx.graphics.g2d.Sprite;

public interface IAnimator {
    default void update() {}
    default Sprite update(Sprite sprite) { return null; }
    default boolean isRunning() { return true; }
    default boolean isCompleted() { return !isRunning(); }
}
