package org.smallbox.faraway.core.game.helper;

import org.smallbox.faraway.core.game.module.character.model.base.CharacterModel;
import org.smallbox.faraway.core.game.module.job.model.*;
import org.smallbox.faraway.core.game.module.job.model.abs.JobModel;
import org.smallbox.faraway.core.game.module.world.model.MapObjectModel;
import org.smallbox.faraway.core.game.module.world.model.item.ItemModel;
import org.smallbox.faraway.core.game.module.world.model.resource.ResourceModel;
import org.smallbox.faraway.core.module.java.ModuleHelper;
import org.smallbox.faraway.core.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 06/07/2015.
 */
public class JobHelper {

    public static JobModel createCutJob(int x, int y) {
        ResourceModel res = WorldHelper.getResource(x, y);
        if (res == null) {
            return null;
        }
        return CutJob.create(res);
    }

    public static JobModel createGatherJob(int x, int y) {
        ResourceModel res = WorldHelper.getResource(x, y);
        if (res == null) {
            return null;
        }
        return GatherJob.create(res);
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

    public static void addStoreJob(CharacterModel character) {
//        BaseJob job = JobHaul.onCreate(characters);
//        if (job != null) {
//            addJob(job);
//        }
//        return job;
        throw new RuntimeException("not implemented");
    }

    public static void addUseJob(ItemModel item) {
        JobModel job = UseJob.create(item);
        if (job != null) {
            ModuleHelper.getJobModule().addJob(job);
        }
    }

//    public static BaseJobModel addBuildJob(MapObjectModel item) {
//        if (item == null) {
//            Log.error("JobModule: build on null item");
//            return null;
//        }
//
//        if (item.isComplete()) {
//            Log.error("Build item: already close, nothing to do");
//            return null;
//        }
//
//        BaseJobModel job = JobBuild.create(item);
//        ModuleHelper.getJobModule().addJob(job);
//
//        return job;
//    }

    public static JobModel addGather(ResourceModel resource) {
        if (resource == null) {
            Log.error("JobModule: gather on null model");
            return null;
        }

        // return if job already exist for this item
        for (JobModel job: ModuleHelper.getJobModule().getJobs()) {
            if (job.getItem() == resource) {
                return null;
            }
        }

        JobModel job = GatherJob.create(resource);
        ModuleHelper.getJobModule().addJob(job);

        return job;
    }

    public static void addDumpJob(MapObjectModel item) {
        JobModel job = DumpJob.create(item);
        if (job != null) {
            ModuleHelper.getJobModule().addJob(job);
        }
    }

//    public static void addJob(ItemModel item, ItemInfo.ItemInfoAction action) {
//        switch (action.type) {
//            case "cook":
//                ModuleHelper.getJobModule().addJob(JobCook.create(item));
//                break;
//            case "craft":
//                ModuleHelper.getJobModule().addJob(CraftJob.create(item));
//                break;
//        }
//    }

    public static void    removeJob(MapObjectModel item) {
        List<JobModel> toRemove = new ArrayList<>();

        for (JobModel job: ModuleHelper.getJobModule().getJobs()) {
            if (job.getItem() == item) {
                toRemove.add(job);
            }
        }

        for (JobModel job: toRemove) {
            ModuleHelper.getJobModule().removeJob(job);
        }
    }

//
//    public static BaseJobModel addBuildJob(ItemInfo info, int x, int y) {
//        MapObjectModel item = null;
//
//        // Structure
//        if (info.isStructure) {
//            MapObjectModel current = WorldHelper.getStructure(x, y);
//            if (current != null && current.getInfo().equals(info)) {
//                Log.error("Build structure: already exist on this model");
//                return null;
//            }
//            item = ModuleHelper.getWorldModule().putObject(info, x, y, 0, 0);
//        }
//
//        // Item
//        else if (info.isUserItem) {
//            MapObjectModel current = WorldHelper.getItem(x, y);
//            if (current != null && current.getInfo().equals(info)) {
//                Log.error("Build item: already exist on this model");
//                return null;
//            } else if (current != null) {
//                Log.error("JobModule: add build on non null item");
//                return null;
//            } else {
//                item = ModuleHelper.getWorldModule().putObject(info, x, y, 0, 0);
//            }
//        }
//
//        // Resource
//        else if (info.isResource) {
//            MapObjectModel currentItem = WorldHelper.getItem(x, y);
//            MapObjectModel currentResource = WorldHelper.getResource(x, y);
//            if (currentResource != null && currentResource.getInfo().equals(info)) {
//                Log.error("Build item: already exist on this model");
//                return null;
//            } else if (currentItem != null) {
//                Log.error("JobModule: add build on non null item");
//                return null;
//            } else {
//                item = ModuleHelper.getWorldModule().putObject(info, x, y, 0, 0);
//            }
//        }
//
//        return addBuildJob(item);
//    }

}
