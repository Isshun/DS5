package org.smallbox.faraway.modules.item;

import org.smallbox.faraway.common.NotImplementedException;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.engine.module.GenericGameModule;
import org.smallbox.faraway.core.game.Data;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.module.world.model.MapObjectModel;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.building.BasicDumpJob;
import org.smallbox.faraway.modules.building.BuildJobFactory;
import org.smallbox.faraway.modules.consumable.ConsumableModule;
import org.smallbox.faraway.modules.item.job.UseJob;
import org.smallbox.faraway.modules.job.JobModel;
import org.smallbox.faraway.modules.job.JobModule;
import org.smallbox.faraway.modules.job.JobModuleObserver;
import org.smallbox.faraway.modules.structure.StructureModule;
import org.smallbox.faraway.modules.world.WorldModule;
import org.smallbox.faraway.util.log.Log;

@GameObject
public class ItemModule extends GenericGameModule<UsableItem, ItemModuleObserver> {

    @Inject
    private WorldModule worldModule;

    @Inject
    private JobModule jobModule;

    @Inject
    private StructureModule structureModule;

    @Inject
    private ConsumableModule consumableModule;

    @Inject
    private BuildJobFactory buildJobFactory;

    @Inject
    private Data data;

    /**
     * Crée un UseJob
     *
     * @param item
     * @param totalDuration
     * @param callback
     * @return
     */
    public UseJob createUseJob(UsableItem item, double totalDuration, UseJob.OnUseCallback callback) {
        UseJob useJob = new UseJob(this, item, totalDuration, callback);
        jobModule.addJob(useJob);
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
    public void putObject(ParcelModel parcel, ItemInfo itemInfo, int data, boolean complete) {
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

    public void addPattern(ParcelModel parcel, ItemInfo itemInfo) {
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
        throw new NotImplementedException();
    }

    public void addItem(UsableItem item) {
        if (!contains(item)) {
            add(item);

            notifyObservers(obs -> obs.onAddItem(item.getParcel(), item));
        }
    }

    public UsableItem addItem(String itemName, boolean isComplete, int x, int y, int z) {
        return addItem(data.getItemInfo(itemName), isComplete, x, y, z);
    }

    public UsableItem addItem(ItemInfo itemInfo, boolean isComplete, int x, int y, int z) {
        return addItem(itemInfo, isComplete, WorldHelper.getParcel(x, y, z));
    }

    public UsableItem addItem(ItemInfo itemInfo, boolean isComplete, ParcelModel parcel) {
        UsableItem item = new UsableItem(itemInfo);
        item.setParcel(parcel);
        item.setBuildProgress(isComplete ? itemInfo.build.cost : 0);
        item.init();

        add(item);

        notifyObservers(obs -> obs.onAddItem(item.getParcel(), item));

        return item;
    }

    public UsableItem getItem(ParcelModel parcel) {
        for (UsableItem item: getAll()) {
            if (item.getParcel() == parcel) {
                return item;
            }
        }

        return null;
    }

    public void dumpItem(UsableItem item) {
        if (jobModule.getJobs().stream().noneMatch(job -> job instanceof BasicDumpJob && ((BasicDumpJob)job).getObject() == item)) {
            jobModule.addJob(new BasicDumpJob(this, item));
        }
    }
}
