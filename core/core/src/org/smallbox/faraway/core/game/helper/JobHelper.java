package org.smallbox.faraway.core.game.helper;

import org.smallbox.faraway.core.data.ItemInfo;
import org.smallbox.faraway.core.game.model.Data;
import org.smallbox.faraway.core.game.module.job.model.DumpJob;
import org.smallbox.faraway.core.game.module.job.model.GatherJob;
import org.smallbox.faraway.core.game.module.job.model.DigJob;
import org.smallbox.faraway.core.game.module.job.model.abs.JobModel;
import org.smallbox.faraway.core.game.module.world.model.MapObjectModel;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.core.game.module.world.model.resource.PlantModel;
import org.smallbox.faraway.core.module.java.ModuleHelper;
import org.smallbox.faraway.core.util.Log;

/**
 * Created by Alex on 06/07/2015.
 */
public class JobHelper {

    public static JobModel createCutJob(int x, int y, int z) {
        PlantModel res = WorldHelper.getResource(x, y, z);
        if (res == null) {
            return null;
        }
        return GatherJob.create(res, GatherJob.Mode.CUT);
    }

    public static JobModel createGatherJob(int x, int y, int z) {
        PlantModel res = WorldHelper.getResource(x, y, z);
        if (res == null) {
            return null;
        }
        return GatherJob.create(res, GatherJob.Mode.HARVEST);
    }

    public static DigJob createMiningJob(int x, int y, int z, boolean ramp, ParcelModel parcelToRemoveGround, ItemInfo groundInfo) {
        ParcelModel parcel = WorldHelper.getParcel(x, y, z);
        if (parcel == null) {
            return null;
        }

        if (parcel.getRockInfo() == null) {
            return null;
        }

//        ResourceModel res = WorldHelper.getResource(x, y, z);
//        if (res == null) {
//            return null;
//        }

        ItemInfo itemProduct = null;
        if (ramp) {
            itemProduct = Data.getData().getItemInfo("base.structure.ramp");
        }

        return DigJob.create(parcel, parcel.getRockInfo(), itemProduct, parcelToRemoveGround, groundInfo);
    }

    public static void addGatherJob(int x, int y, int z, boolean removeOnComplete) {
        JobModel job = createGatherJob(x, y, z);
        if (job != null) {
            ModuleHelper.getJobModule().addJob(job);
        }
    }

    public static void addMineJob(int x, int y, int z, boolean ramp) {
        JobModel job = createMiningJob(x, y, z, ramp, null, null);
        if (job != null) {
            ModuleHelper.getJobModule().addJob(job);
        }
    }

    public static JobModel addGather(PlantModel resource, GatherJob.Mode mode) {
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
}
