package org.smallbox.faraway.core.game.helper;

import org.smallbox.faraway.core.game.module.job.model.DumpJob;
import org.smallbox.faraway.core.game.module.job.model.GatherJob;
import org.smallbox.faraway.core.game.module.job.model.MineJob;
import org.smallbox.faraway.core.game.module.job.model.abs.JobModel;
import org.smallbox.faraway.core.game.module.world.model.MapObjectModel;
import org.smallbox.faraway.core.game.module.world.model.resource.ResourceModel;
import org.smallbox.faraway.core.module.java.ModuleHelper;
import org.smallbox.faraway.core.util.Log;

/**
 * Created by Alex on 06/07/2015.
 */
public class JobHelper {

    public static JobModel createCutJob(int x, int y) {
        ResourceModel res = WorldHelper.getResource(x, y);
        if (res == null) {
            return null;
        }
        return GatherJob.create(res, GatherJob.Mode.CUT);
    }

    public static JobModel createGatherJob(int x, int y) {
        ResourceModel res = WorldHelper.getResource(x, y);
        if (res == null) {
            return null;
        }
        return GatherJob.create(res, GatherJob.Mode.HARVEST);
    }

    public static JobModel createMiningJob(int x, int y) {
        ResourceModel res = WorldHelper.getResource(x, y);
        if (res == null) {
            return null;
        }
        return MineJob.create(res);
    }

    public static void addGatherJob(int x, int y, boolean removeOnComplete) {
        JobModel job = createGatherJob(x, y);
        if (job != null) {
            ModuleHelper.getJobModule().addJob(job);
        }
    }

    public static void addMineJob(int x, int y) {
        JobModel job = createMiningJob(x, y);
        if (job != null) {
            ModuleHelper.getJobModule().addJob(job);
        }
    }

    public static JobModel addGather(ResourceModel resource, GatherJob.Mode mode) {
        if (resource == null) {
            Log.error("JobModule: gather on null model");
            return null;
        }

        // Job already exists for this resource
        if (resource.getJob() != null) {
            return null;
        }

        JobModel job = GatherJob.create(resource, mode);
        ModuleHelper.getJobModule().addJob(job);

        return job;
    }

    public static void addDumpJob(MapObjectModel item) {
        JobModel job = DumpJob.create(item);
        if (job != null) {
            ModuleHelper.getJobModule().addJob(job);
        }
    }
}
