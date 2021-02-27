package org.smallbox.faraway.client.engine.animator;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public class SerialAnimator implements IAnimator {
    private Collection<IAnimator> animators;

    public static IAnimator of(IAnimator... animators) {
        SerialAnimator compositeAnimator = new SerialAnimator();
        compositeAnimator.animators = Arrays.stream(animators).collect(Collectors.toList());
        return compositeAnimator;
    }

    public void update() {
        animators.stream().filter(IAnimator::isRunning).findFirst().ifPresent(IAnimator::update);
    }

    @Override
    public boolean isRunning() {
        return animators.stream().anyMatch(IAnimator::isRunning);
    }
}
