package org.smallbox.faraway.game.item;

import org.smallbox.faraway.game.item.job.OnUseCallback;
import org.smallbox.faraway.game.item.job.UseJobFactory;
import org.smallbox.faraway.util.GameException;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.DataManager;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.module.SuperGameModule;
import org.smallbox.faraway.core.world.model.MapObjectModel;
import org.smallbox.faraway.game.area.AreaModule;
import org.smallbox.faraway.game.building.BuildJobFactory;
import org.smallbox.faraway.game.consumable.ConsumableModule;
import org.smallbox.faraway.game.item.job.UseJob;
import org.smallbox.faraway.game.job.JobModel;
import org.smallbox.faraway.game.job.JobModule;
import org.smallbox.faraway.game.job.JobModuleObserver;
import org.smallbox.faraway.game.structure.StructureModule;
import org.smallbox.faraway.game.world.Parcel;
import org.smallbox.faraway.game.world.WorldHelper;
import org.smallbox.faraway.game.world.WorldModule;
import org.smallbox.faraway.util.log.Log;

@GameObject
public class ItemModule extends SuperGameModule<UsableItem, ItemModuleObserver> {
    @Inject private WorldModule worldModule;
    @Inject private JobModule jobModule;
    @Inject private StructureModule structureModule;
    @Inject private ConsumableModule consumableModule;
    @Inject private BuildJobFactory buildJobFactory;
    @Inject private DataManager dataManager;
    @Inject private UseJobFactory useJobFactory;

    public UseJob createUseJob(UsableItem item, OnUseCallback callback) {
        UseJob useJob = useJobFactory.create(item, callback);
        jobModule.add(useJob);
        return useJob;
    }

    @Override
    public void onGameCreate(Game game) {
        jobModule.addObserver(new JobModuleObserver() {
            @Override
            public void onJobCancel(JobModel job) {
                getAll().removeIf(item -> item.getBuildJob() == job);
            }

            @Override
            public void onJobComplete(JobModel job) {
                getAll().removeIf(item -> item.getBuildJob() == job);
            }
        });

//        jobModule.addJoyCheck(new CheckJoyItem());
    }

    @Override
    public void onGameStart(Game game) {
        getAll().stream()
                .filter(item -> item.getBuildValue() < item.getBuildCost())
                .forEach(this::launchBuild);
    }

    @Override
    protected void onModuleUpdate(Game game) {
//        createBuildJobs(jobModule, consumableModule, buildJobFactory, _items);
//        createRepairJobs(jobModule, _items);
    }

    @Override
    public void putObject(Parcel parcel, ItemInfo itemInfo, int data, boolean complete) {
        if (itemInfo.isUserItem) {
            UsableItem item = new UsableItem(itemInfo, data);
            item.setParcel(parcel);
            item.init();
            add(item);

            notifyObservers(obs -> obs.onAddItem(parcel, item));
        }
    }

    @Override
    public void removeObject(MapObjectModel mapObjectModel) {
        if (mapObjectModel.isUserItem() && mapObjectModel instanceof UsableItem) {
            remove((UsableItem) mapObjectModel);
            jobModule.onCancelJobs(mapObjectModel.getParcel(), mapObjectModel);

            notifyObservers(obs -> obs.onRemoveItem(mapObjectModel.getParcel(), (UsableItem) mapObjectModel));
        }
    }

    public void addPattern(Parcel parcel, ItemInfo itemInfo) {
        Log.info("Add pattern for %s at position %s", itemInfo, parcel);

        // Create item
        UsableItem item = new UsableItem(itemInfo);
        item.setParcel(parcel);
        item.init();
        item.setBuildProgress(0);
        add(item);
    }

    /**
     * Create build job
     *
     * @param item to build
     */
    private void launchBuild(UsableItem item) {
        throw new GameException(AreaModule.class, "Not implemented");
    }

    public void addItem(UsableItem item) {
        if (!contains(item)) {
            add(item);

            notifyObservers(obs -> obs.onAddItem(item.getParcel(), item));
        }
    }

    public UsableItem addItem(String itemName, boolean isComplete, int x, int y, int z) {
        return addItem(dataManager.getItemInfo(itemName), isComplete, x, y, z);
    }

    public UsableItem addItem(ItemInfo itemInfo, boolean isComplete, int x, int y, int z) {
        return addItem(itemInfo, isComplete, WorldHelper.getParcel(x, y, z));
    }

    public UsableItem addItem(ItemInfo itemInfo, boolean isComplete, Parcel parcel) {
        UsableItem item = new UsableItem(itemInfo);
        item.setParcel(parcel);
        item.setBuildProgress(isComplete ? itemInfo.build.cost : 0);
        item.init();

        add(item);

        worldModule.refreshGlue(parcel);

        notifyObservers(obs -> obs.onAddItem(item.getParcel(), item));

        return item;
    }

    public UsableItem getItem(Parcel parcel) {
        for (UsableItem item: getAll()) {
            if (item.getParcel() == parcel) {
                return item;
            }
        }

        return null;
    }

//    public void dumpItem(UsableItem item) {
//        if (jobModule.getAll().stream().noneMatch(job -> job instanceof BasicDumpJob && ((BasicDumpJob)job).getObject() == item)) {
//            jobModule.add(new BasicDumpJob(this, item));
//        }
//    }
}
