package org.smallbox.faraway.module.job;

import org.smallbox.faraway.core.engine.module.ModuleObserver;
import org.smallbox.faraway.core.module.job.model.abs.JobModel;

/**
 * Created by Alex on 20/07/2016.
 */
public interface JobModuleObserver extends ModuleObserver {
    default void onJobComplete(JobModel job) {}
    default void onJobAbort(JobModel job) {}
    default void onJobCancel(JobModel job) {}
}
