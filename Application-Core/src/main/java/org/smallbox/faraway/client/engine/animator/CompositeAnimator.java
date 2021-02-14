package org.smallbox.faraway.client.engine.animator;

import com.badlogic.gdx.graphics.g2d.Sprite;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public class CompositeAnimator implements IAnimator {
    private Collection<IAnimator> animators;

    public static IAnimator of(IAnimator... animators) {
        CompositeAnimator compositeAnimator = new CompositeAnimator();
        compositeAnimator.animators = Arrays.stream(animators).collect(Collectors.toList());
        return compositeAnimator;
    }

    public Sprite update(Sprite sprite) {
        animators.forEach(animator -> animator.update(sprite));
        return sprite;
    }

}
