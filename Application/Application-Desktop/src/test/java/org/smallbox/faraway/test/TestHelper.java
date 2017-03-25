package org.smallbox.faraway.test;

import org.smallbox.faraway.modules.job.JobModel;
import org.smallbox.faraway.modules.job.JobModule;

/**
 * Created by Alex on 23/03/2017.
 */
public class TestHelper {
    public static <T extends JobModel> T getFirstJob(JobModule jobModule, Class<T> cls) {
        return (T)jobModule.getJobs().stream().filter(cls::isInstance).findFirst().get();
    }
}
