package org.smallbox.faraway.module.item;

import org.smallbox.faraway.core.BindModule;
import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.game.module.job.model.BuildJob;
import org.smallbox.faraway.core.game.module.job.model.CraftJob;
import org.smallbox.faraway.core.game.module.job.model.HaulJob;
import org.smallbox.faraway.module.world.WorldModule;
import org.smallbox.faraway.module.world.WorldModuleObserver;
import org.smallbox.faraway.core.game.module.world.model.*;
import org.smallbox.faraway.core.game.module.world.model.item.ItemModel;
import org.smallbox.faraway.module.job.JobModule;
import org.smallbox.faraway.module.structure.StructureModule;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Alex on 26/06/2015.
 */
public class ItemModule extends GameModule<ItemModuleObserver> {
    @BindModule("base.module.world")
    private WorldModule _world;

    @BindModule("base.module.jobs")
    private JobModule _jobs;

    @BindModule("base.module.structure")
    private StructureModule _structureModel;

    private Collection<ItemModel> _items;

    public Collection<ItemModel> getItems() {
        return _items;
    }

    @Override
    protected void onGameCreate(Game game) {
        _items = new LinkedBlockingQueue<>();
        _world.addObserver(new WorldModuleObserver() {
            @Override
            public MapObjectModel putObject(ParcelModel parcel, ItemInfo itemInfo, int data, boolean complete) {
                return null;
            }

            @Override
            public void onAddParcel(ParcelModel parcel) {
                if (parcel.hasItem()) {
                    _items.add(parcel.getItem());
                }
            }

            @Override
            public void onRemoveItem(ParcelModel parcel, ItemModel item) {
            }

            @Override
            public void onAddItem(ParcelModel parcel, ItemModel item) {
            }
        });
    }

    @Override
    protected void onGameUpdate(Game game, int tick) {
        if (tick % 10 == 0) {
            // Create hauling jobs
            _items.stream().filter(item -> !item.isComplete())
                    .forEach(item -> item.getComponents().stream().filter(component -> component.currentQuantity < component.neededQuantity && component.job == null)
                            .forEach(component -> _jobs.addJob(new HaulJob(item, component))));

            // Create Build jobs
            _items.stream().filter(item -> !item.isComplete()).filter(item -> item.hasAllComponents() && item.getBuildJob() == null)
                    .forEach(item -> _jobs.addJob(new BuildJob(item)));

            // Create craft jobs
            _items.stream().filter(item -> item.isFactory() && item.getFactory().getJob() == null && item.getFactory().scan())
                    .forEach(item -> _jobs.addJob(new CraftJob(item)));
        }
    }

    @Override
    public void onItemComplete(ItemModel item) {
    }

    @Override
    public void putObject(ParcelModel parcel, ItemInfo itemInfo, int data, boolean complete) {
    }

    @Override
    public void removeObject(MapObjectModel mapObjectModel) {
        if (mapObjectModel.isUserItem() && mapObjectModel instanceof ItemModel) {
            _items.remove(mapObjectModel);
            _jobs.onCancelJobs(mapObjectModel.getParcel(), mapObjectModel);
        }
    }
}
