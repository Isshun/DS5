package org.smallbox.faraway.game.job;

import org.smallbox.faraway.core.engine.module.ModuleObserver;

public interface JobModuleObserver extends ModuleObserver {
    default void onJobComplete(JobModel job) {}
    default void onJobAbort(JobModel job) {}
    default void onJobCancel(JobModel job) {}
}
