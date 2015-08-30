package org.smallbox.faraway.game.helper;

import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.module.character.JobManager;
import org.smallbox.faraway.game.model.character.base.CharacterModel;
import org.smallbox.faraway.game.model.item.ItemInfo;
import org.smallbox.faraway.game.model.item.ItemModel;
import org.smallbox.faraway.game.model.item.MapObjectModel;
import org.smallbox.faraway.game.model.item.ResourceModel;
import org.smallbox.faraway.game.model.job.*;
import org.smallbox.faraway.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 06/07/2015.
 */
public class JobHelper {

    public static BaseJobModel createGatherJob(int x, int y, boolean removeOnComplete) {
        ResourceModel res = WorldHelper.getResource(x, y);
        if (res == null) {
            return null;
        }
        return JobGather.create(res, removeOnComplete);
    }

    public static BaseJobModel createMiningJob(int x, int y) {
        ResourceModel res = WorldHelper.getResource(x, y);
        if (res == null) {
            return null;
        }
        return JobMining.create(res);
    }

    public static void addGatherJob(int x, int y, boolean removeOnComplete) {
        BaseJobModel job = createGatherJob(x, y, removeOnComplete);
        if (job != null) {
            JobManager.getInstance().addJob(job);
        }
    }

    public static void addMineJob(int x, int y) {
        BaseJobModel job = createMiningJob(x, y);
        if (job != null) {
            JobManager.getInstance().addJob(job);
        }
    }

    public static void addStoreJob(CharacterModel character) {
//		BaseJob job = JobHaul.onCreate(characters);
//		if (job != null) {
//			addJob(job);
//		}
//		return job;
        throw new RuntimeException("not implemented");
    }

    public static void addUseJob(ItemModel item) {
        BaseJobModel job = JobUse.create(item);
        if (job != null) {
            JobManager.getInstance().addJob(job);
        }
    }

    public static BaseJobModel addBuildJob(MapObjectModel item) {
        if (item == null) {
            Log.error("JobManager: build on null item");
            return null;
        }

        if (item.isComplete()) {
            Log.error("Build item: already close, nothing to do");
            return null;
        }

        BaseJobModel job = JobBuild.create(item);
        JobManager.getInstance().addJob(job);

        return job;
    }

    public static BaseJobModel addGather(ResourceModel resource, boolean removeOnComplete) {
        if (resource == null) {
            Log.error("JobManager: gather on null area");
            return null;
        }

        // return if job already exist for this item
        for (BaseJobModel job: JobManager.getInstance().getJobs()) {
            if (job.getItem() == resource) {
                return null;
            }
        }

        BaseJobModel job = JobGather.create(resource, removeOnComplete);
        JobManager.getInstance().addJob(job);

        return job;
    }

    public static void addDumpJob(MapObjectModel item) {
        BaseJobModel job = JobDump.create(item);
        if (job != null) {
            JobManager.getInstance().addJob(job);
        }
    }

    public static void addJob(ItemModel item, ItemInfo.ItemInfoAction action) {
        switch (action.type) {
            case "cook":
                JobManager.getInstance().addJob(JobCook.create(action, item));
                break;
            case "craft":
                JobManager.getInstance().addJob(JobCraft.create(action, item));
                break;
        }
    }

    public static void	removeJob(MapObjectModel item) {
        List<BaseJobModel> toRemove = new ArrayList<>();

        for (BaseJobModel job: JobManager.getInstance().getJobs()) {
            if (job.getItem() == item) {
                toRemove.add(job);
            }
        }

        for (BaseJobModel job: toRemove) {
            JobManager.getInstance().removeJob(job);
        }
    }

    public static BaseJobModel addBuildJob(ItemInfo info, int x, int y) {
        MapObjectModel item = null;

        // Structure
        if (info.isStructure) {
            MapObjectModel current = WorldHelper.getStructure(x, y);
            if (current != null && current.getInfo().equals(info)) {
                Log.error("Build structure: already exist on this area");
                return null;
            }
            item = Game.getWorldManager().putObject(info, x, y, 0, 0);
        }

        // Item
        else if (info.isUserItem) {
            MapObjectModel current = WorldHelper.getItem(x, y);
            if (current != null && current.getInfo().equals(info)) {
                Log.error("Build item: already exist on this area");
                return null;
            } else if (current != null) {
                Log.error("JobManager: add build on non null item");
                return null;
            } else {
                item = Game.getWorldManager().putObject(info, x, y, 0, 0);
            }
        }

        // Resource
        else if (info.isResource) {
            MapObjectModel currentItem = WorldHelper.getItem(x, y);
            MapObjectModel currentRessource = WorldHelper.getResource(x, y);
            if (currentRessource != null && currentRessource.getInfo().equals(info)) {
                Log.error("Build item: already exist on this area");
                return null;
            } else if (currentItem != null) {
                Log.error("JobManager: add build on non null item");
                return null;
            } else {
                item = Game.getWorldManager().putObject(info, x, y, 0, 0);
            }
        }

        return addBuildJob(item);
    }

}
