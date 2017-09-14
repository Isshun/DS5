package org.smallbox.faraway.modules.job;

import org.smallbox.faraway.common.ModuleObserver;

/**
 * Created by Alex on 20/07/2016.
 */
public interface JobModuleObserver extends ModuleObserver {
    default void onJobComplete(JobModel job) {}
    default void onJobAbort(JobModel job) {}
    default void onJobCancel(JobModel job) {}
}
