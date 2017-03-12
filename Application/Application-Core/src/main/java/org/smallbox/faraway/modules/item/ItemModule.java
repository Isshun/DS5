package org.smallbox.faraway.modules.item;

import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.module.ModuleSerializer;
import org.smallbox.faraway.core.module.world.model.ConsumableItem;
import org.smallbox.faraway.core.module.world.model.MapObjectModel;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.consumable.ConsumableModule;
import org.smallbox.faraway.modules.consumable.ConsumeJob;
import org.smallbox.faraway.modules.job.JobModel;
import org.smallbox.faraway.modules.job.JobModule;
import org.smallbox.faraway.modules.job.JobModuleObserver;
import org.smallbox.faraway.modules.job.JobTaskReturn;
import org.smallbox.faraway.modules.structure.StructureModule;
import org.smallbox.faraway.modules.world.WorldModule;
import org.smallbox.faraway.util.Log;

import java.util.Collection;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Alex on 26/06/2015.
 */
@ModuleSerializer(ItemModuleSerializer.class)
//@ModuleRenderer(ItemRenderer.class)
public class ItemModule extends GameModule<ItemModuleObserver> {

    @BindModule
    private WorldModule worldModule;

    @BindModule
    private JobModule jobModule;

    @BindModule
    private StructureModule structureModule;

    @BindModule
    private ConsumableModule consumableModule;

//    @BindModule
//    private WorldInteractionModule worldInteractionModule;

    private Collection<UsableItem> _items;

    public Collection<UsableItem> getItems() {
        return _items;
    }

    public interface onUseCallback {
        void onUse(UsableItem item, int durationLeft);
    }

    public ConsumeJob createUseJob(UsableItem item, int totalDuration, onUseCallback callback) {
        return jobModule.createJob(ConsumeJob.class, null, item.getParcel(), job -> {

            job.setMainLabel("Consume " + item.getInfo().label);

            job.addTask("Move", c -> c.moveTo(item.getParcel()) ? JobTaskReturn.TASK_COMPLETE : JobTaskReturn.TASK_CONTINUE);
            job.addTask("Consume", c -> {
                int durationLeft = totalDuration - ++job._duration;
                callback.onUse(item, durationLeft);
                job.setProgress(job._duration, totalDuration);

                if (durationLeft > 0) {
                    return JobTaskReturn.TASK_CONTINUE;
                }

                return JobTaskReturn.TASK_COMPLETE;
            });

            return true;
        });
    }

    @Override
    public void onGameCreate(Game game) {
        _items = new LinkedBlockingQueue<>();

//        worldInteractionModule.addObserver(new WorldInteractionModuleObserver() {
//            public UsableItem _lastItem;
//
//            @Override
//            public void onSelect(GameEvent event, Collection<ParcelModel> parcels) {
//                // Get item on parcel
//                UsableItem item = _items.stream()
//                        .filter(i -> parcels.contains(i.getParcel()))
//                        .findAny()
//                        .orElse(null);
//
//                // Call observers
//                if (item != null) {
//                    notifyObservers(obs -> obs.onSelectItem(event, item));
//                } else if (_lastItem != null) {
//                    notifyObservers(obs -> obs.onDeselectItem(_lastItem));
//                }
//
//                // Store current item
//                _lastItem = item;
//            }
//        });

        jobModule.addObserver(new JobModuleObserver() {
            @Override
            public void onJobCancel(JobModel job) {
                _items.removeIf(item -> item.getBuildJob() == job);
            }

            @Override
            public void onJobComplete(JobModel job) {
                _items.removeIf(item -> item.getBuildJob() == job);
            }
        });

//        jobModule.addJoyCheck(new CheckJoyItem());
    }

    @Override
    public void onGameStart(Game game) {
        _items.stream()
                .filter(item -> item.getBuildProgress() < item.getBuildCost())
                .forEach(this::launchBuild);
    }

    @Override
    protected void onGameUpdate(Game game, int tick) {

        // Create Build jobs
        _items.stream().filter(item -> !item.isComplete()).filter(item -> item.hasAllComponents() && item.getBuildJob() == null)
                .forEach(item -> jobModule.addJob(new BuildJob(item)));

////             Create craft jobs
//            _items.stream().filter(item -> item.isFactory() && item.getFactory().getJob() == null && item.getFactory().scan(_consumableModel))
//                    .forEach(item -> jobModule.addHaulJob(new CraftJob(item)));
    }

    @Override
    public void putObject(ParcelModel parcel, ItemInfo itemInfo, int data, boolean complete) {
        if (itemInfo.isUserItem) {
            UsableItem item = new UsableItem(itemInfo, data);
            item.setParcel(parcel);
            item.init();
            _items.add(item);

            notifyObservers(obs -> obs.onAddItem(parcel, item));
        }
    }

    @Override
    public void removeObject(MapObjectModel mapObjectModel) {
        if (mapObjectModel.isUserItem() && mapObjectModel instanceof UsableItem) {
            _items.remove(mapObjectModel);
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
        _items.add(item);

        launchBuild(item);
    }

    /**
     * Create build job
     *
     * @param item to build
     */
    private void launchBuild(UsableItem item) {
        BuildJob job = new BuildJob(item);
        item.setBuildJob(job);
        jobModule.addJob(job);
    }

    public void addItem(UsableItem item) {
        if (!_items.contains(item)) {
            _items.add(item);

            notifyObservers(obs -> obs.onAddItem(item.getParcel(), item));
        }
    }

    public UsableItem addItem(String itemName, boolean isComplete, int x, int y, int z) {
        return addItem(Application.data.getItemInfo(itemName), isComplete, x, y, z);
    }

    public UsableItem addItem(ItemInfo itemInfo, boolean isComplete, int x, int y, int z) {
        return addItem(itemInfo, isComplete, WorldHelper.getParcel(x, y, z));
    }

    public UsableItem addItem(ItemInfo itemInfo, boolean isComplete, ParcelModel parcel) {
        UsableItem item = new UsableItem(itemInfo);
        item.setParcel(parcel);
        item.setBuildProgress(isComplete ? itemInfo.build.cost : 0);
        item.init();

        _items.add(item);

        notifyObservers(obs -> obs.onAddItem(item.getParcel(), item));

        return item;
    }

    public UsableItem getItem(ParcelModel parcel) {
        for (UsableItem item: _items) {
            if (item.getParcel() == parcel) {
                return item;
            }
        }

        return null;
    }
}
