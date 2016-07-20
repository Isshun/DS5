package org.smallbox.faraway.module.structure;

import org.smallbox.faraway.core.BindModule;
import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.game.module.job.model.BuildJob;
import org.smallbox.faraway.core.game.module.job.model.HaulJob;
import org.smallbox.faraway.core.game.module.path.PathManager;
import org.smallbox.faraway.module.world.*;
import org.smallbox.faraway.core.game.module.world.model.MapObjectModel;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.core.game.module.world.model.StructureModel;
import org.smallbox.faraway.core.game.module.world.model.item.ItemModel;
import org.smallbox.faraway.module.job.JobModule;
import org.smallbox.faraway.module.job.JobModuleObserver;

import java.util.Collection;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Alex on 26/06/2015.
 */
public class StructureModule extends GameModule<StructureModuleObserver> {
    @BindModule("")
    private PathManager _path;

    @BindModule("base.module.world")
    private WorldModule _world;

    @BindModule("base.module.jobs")
    private JobModule _jobs;

    private Collection<StructureModel> _structures;

    public Collection<StructureModel>           getStructures() { return _structures; }

    @Override
    protected void onGameCreate(Game game) {
        game.getRenders().add(new StructureBottomRenderer(this));
        game.getRenders().add(new StructureTopRenderer(this));
//        getSerializers().add(new WorldModuleSerializer(this));
    }

    @Override
    protected void onGameStart(Game game) {
        _structures = new LinkedBlockingQueue<>();

        _jobs.addObserver(new JobModuleObserver() {
        });

        _world.addObserver(new WorldModuleObserver() {
            @Override
            public MapObjectModel putObject(ParcelModel parcel, ItemInfo itemInfo, int data, boolean complete) {
                return null;
            }

            @Override
            public void onAddParcel(ParcelModel parcel) {
                if (parcel.hasStructure()) {
                    _structures.add(parcel.getStructure());
                }
            }

            @Override
            public void onAddItem(ParcelModel parcel, ItemModel item) {
            }

            @Override
            public void onRemoveItem(ParcelModel parcel, ItemModel item) {
            }
        });
    }

    @Override
    protected void onGameUpdate(Game game, int tick) {
        if (tick % 10 == 0) {
            // Create hauling jobs
            _structures.stream().filter(structure -> !structure.isComplete())
                    .forEach(item -> item.getComponents().stream()
                            .filter(component -> component.currentQuantity < component.neededQuantity && component.job == null)
                            .forEach(component -> _jobs.addJob(new HaulJob(item, component))));

            // Create Build jobs
            _structures.stream().filter(structure -> !structure.isComplete()).filter(item -> item.hasAllComponents() && item.getBuildJob() == null)
                    .forEach(item -> _jobs.addJob(new BuildJob(item)));
        }
    }

    public void removeStructure(StructureModel structure) {
        if (structure != null && structure.getParcel() != null) {
            ParcelModel parcel = structure.getParcel();
            moveStructureToParcel(parcel, null);

            _jobs.onCancelJobs(structure.getParcel(), structure);
            _path.resetAround(structure.getParcel());

            notifyObservers(observer -> observer.onRemoveStructure(parcel, structure));
        }
    }

    private void moveStructureToParcel(ParcelModel parcel, StructureModel structure) {
        parcel.setStructure(structure);
        if (structure != null) {
            structure.setParcel(parcel);
        }
    }

    @Override
    public void onCancelJobs(ParcelModel parcel, Object object) {
        if (parcel.hasStructure() && !parcel.getStructure().isComplete() && (object == null || object instanceof StructureModel)) {
            StructureModel structure = parcel.getStructure();
            parcel.setStructure(null);
            notifyObservers(observer -> observer.onRemoveStructure(parcel, structure));
        }
    }

    @Override
    public void removeObject(MapObjectModel mapObjectModel) {
        if (mapObjectModel.isStructure() && mapObjectModel instanceof StructureModel) {
            removeStructure((StructureModel) mapObjectModel);
        }
    }

    @Override
    public void putObject(ParcelModel parcel, ItemInfo itemInfo, int data, boolean complete) {
        if (itemInfo.isStructure) {
            putStructure(parcel, itemInfo, data, complete);
        }
    }

    private StructureModel putStructure(ParcelModel parcel, ItemInfo itemInfo, int matterSupply, boolean complete) {
        if (parcel.getStructure() == null || parcel.getStructure().isFloor()) {
            StructureModel structure = new StructureModel(itemInfo);
            structure.addProgress(complete ? itemInfo.cost : 0);
            structure.setComplete(complete);
            if (structure.getInfo().receipts != null && structure.getInfo().receipts.size() > 0) {
                structure.setReceipt(structure.getInfo().receipts.get(0));
            }
            moveStructureToParcel(parcel, structure);
            _structures.add(structure);

            _path.resetAround(structure.getParcel());

            notifyObservers(observer -> observer.onAddStructure(structure));

            return structure;
        }

        return null;
    }
}
