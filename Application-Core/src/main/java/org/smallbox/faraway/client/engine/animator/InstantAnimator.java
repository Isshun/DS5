package org.smallbox.faraway.client.engine.animator;

import com.badlogic.gdx.graphics.g2d.Sprite;

import java.util.function.BiConsumer;

public class InstantAnimator<T> implements IAnimator {
    private final BiConsumer<T, Float> supplier;
    private final T subject;
    private boolean running = true;

    public InstantAnimator(T subject, BiConsumer<T, Float> supplier) {
        this.supplier = supplier;
        this.subject = subject;
    }

    public void update() {
        supplier.accept(subject, 0f);
        running = false;
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public Sprite update(Sprite sprite) {
        return null;
    }
}
