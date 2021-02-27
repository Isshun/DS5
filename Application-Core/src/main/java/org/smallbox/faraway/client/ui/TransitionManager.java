package org.smallbox.faraway.client.ui;

import org.smallbox.faraway.client.engine.animator.IAnimator;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.callback.gameEvent.OnGameLongUpdate;

import java.util.ArrayList;
import java.util.Collection;

@ApplicationObject
public class TransitionManager {
    private final Collection<IAnimator> animators = new ArrayList<>();

    @OnGameLongUpdate
    private void clean() {
        animators.removeIf(IAnimator::isCompleted);
    }

    public void add(IAnimator animation) {
        animators.add(animation);
    }

    public void draw() {
        animators.stream().filter(IAnimator::isRunning).forEach(IAnimator::update);
    }

}
