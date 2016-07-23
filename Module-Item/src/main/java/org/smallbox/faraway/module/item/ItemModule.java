package org.smallbox.faraway.module.item;

import org.smallbox.faraway.core.BindModule;
import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.engine.renderer.MainRenderer;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.game.module.job.model.BuildJob;
import org.smallbox.faraway.core.game.module.job.model.HaulJob;
import org.smallbox.faraway.module.consumable.ConsumableModule;
import org.smallbox.faraway.module.item.job.CheckJoyItem;
import org.smallbox.faraway.module.world.WorldModule;
import org.smallbox.faraway.core.game.module.world.model.*;
import org.smallbox.faraway.module.item.item.ItemModel;
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

    @BindModule("base.module.consumable")
    private ConsumableModule _consumableModel;

    private Collection<ItemModel> _items;

    public Collection<ItemModel> getItems() {
        return _items;
    }

    @Override
    protected void onGameCreate(Game game) {
        getSerializers().add(new ItemModuleSerializer(this, _world));
        game.getRenders().add(new ItemRenderer(this));

        _items = new LinkedBlockingQueue<>();
    }

    @Override
    protected void onGameStart(Game game) {
        _jobs.addJoyCheck(new CheckJoyItem());
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
            _items.stream().filter(item -> item.isFactory() && item.getFactory().getJob() == null && item.getFactory().scan(_consumableModel))
                    .forEach(item -> _jobs.addJob(new CraftJob(item)));
        }
    }

    @Override
    public void putObject(ParcelModel parcel, ItemInfo itemInfo, int data, boolean complete) {
        if (itemInfo.isUserItem) {
            ItemModel item = new ItemModel(itemInfo, parcel, data);
            _items.add(item);

            notifyObservers(obs -> obs.onAddItem(parcel, item));
        }
    }

    @Override
    public void removeObject(MapObjectModel mapObjectModel) {
        if (mapObjectModel.isUserItem() && mapObjectModel instanceof ItemModel) {
            _items.remove(mapObjectModel);
            _jobs.onCancelJobs(mapObjectModel.getParcel(), mapObjectModel);

            notifyObservers(obs -> obs.onRemoveItem(mapObjectModel.getParcel(), (ItemModel) mapObjectModel));
        }
    }

    public void addItem(ParcelModel parcel, ItemInfo itemInfo) {
        _items.add(new ItemModel(itemInfo, parcel));
    }
}
