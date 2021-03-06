package org.smallbox.faraway.client.engine.animator;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Interpolation;

import java.util.Random;
import java.util.function.BiConsumer;

public class Animator implements IAnimator {
    private final Interpolation interpolation;
    private final BiConsumer<Sprite, Float> spriteSupplier;
    private final float fromValue;
    private final float toValue;
    private float changeValue;
    private float value;

    public Animator(float fromValue, float toValue, float changeValue, Interpolation interpolation, BiConsumer<Sprite, Float> spriteSupplier) {
        this.interpolation = interpolation;
        this.spriteSupplier = spriteSupplier;
        this.fromValue = fromValue;
        this.toValue = toValue;
        this.changeValue = changeValue * (new Random().nextBoolean() ? 1 : -1);
        this.value = (float) Math.random();
    }

    public Sprite update(Sprite sprite) {
        if (value < 0 || value > 1) {
            changeValue = -changeValue;
        }
        value += changeValue;

        spriteSupplier.accept(sprite, interpolation.apply(fromValue, toValue, value));

        return sprite;
    }

}
