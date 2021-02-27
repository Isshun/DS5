package org.smallbox.faraway.client.engine.animator;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Interpolation;

import java.util.function.BiConsumer;

public class NewAnimator<T> implements IAnimator {
    private final Interpolation interpolation;
    private final BiConsumer<T, Float> supplier;
    private long startTime;
    private final float fromValue;
    private final float toValue;
    private final T subject;
    private final int duration;
    private float value;

    public NewAnimator(float fromValue, float toValue, int duration, Interpolation interpolation, T subject, BiConsumer<T, Float> supplier) {
        this.interpolation = interpolation;
        this.supplier = supplier;
        this.subject = subject;
        this.fromValue = fromValue;
        this.toValue = toValue;
        this.value = 0;
        this.duration = duration;
    }

    public void update() {
        if (startTime == 0) {
            startTime = System.currentTimeMillis();
        }

//        if (value < 0 || value > 1) {
//            changeValue = -changeValue;
//        }
        value = (System.currentTimeMillis() - startTime) / (float)duration;

        supplier.accept(subject, interpolation.apply(fromValue, toValue, value));
    }

    @Override
    public boolean isRunning() {
        return value < 1;
    }

    @Override
    public Sprite update(Sprite sprite) {
        return null;
    }
}
