package org.smallbox.faraway.module.structure;

import org.smallbox.faraway.GameEvent;
import org.smallbox.faraway.core.BindModule;
import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.game.module.job.model.BuildJob;
import org.smallbox.faraway.core.game.module.job.model.abs.JobModel;
import org.smallbox.faraway.core.game.module.path.PathManager;
import org.smallbox.faraway.core.game.module.world.model.MapObjectModel;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.core.game.module.world.model.StructureModel;
import org.smallbox.faraway.module.job.JobModule;
import org.smallbox.faraway.module.job.JobModuleObserver;
import org.smallbox.faraway.module.world.WorldInteractionModule;
import org.smallbox.faraway.module.world.WorldInteractionModuleObserver;
import org.smallbox.faraway.module.world.WorldModule;

import java.util.Collection;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Alex on 26/06/2015.
 */
public class StructureModule extends GameModule<StructureModuleObserver> {
    @BindModule
    private PathManager _path;

    @BindModule
    private WorldModule _world;

    @BindModule
    private JobModule _jobs;

    @BindModule
    private WorldInteractionModule _worldInteraction;

    private Collection<StructureModel> _structures;

    public Collection<StructureModel> getStructures() { return _structures; }

    @Override
    protected void onGameCreate(Game game) {
        _structures = new LinkedBlockingQueue<>();

        game.addRender(new StructureBottomRenderer());
        game.addRender(new StructureTopRenderer());
        game.addSerializer(new StructureModuleSerializer(this, _world));

        _worldInteraction.addObserver(new WorldInteractionModuleObserver() {
            public StructureModel _lastStructure;

            @Override
            public void onSelect(GameEvent event, Collection<ParcelModel> parcels) {
                // Get structure on parcel
                StructureModel structure = _structures.stream()
                        .filter(s -> parcels.contains(s.getParcel()))
                        .findAny()
                        .orElse(null);

                // Call observers
                if (structure != null) {
                    notifyObservers(obs -> obs.onSelectStructure(structure));
                } else if (_lastStructure != null) {
                    notifyObservers(obs -> obs.onDeselectStructure(_lastStructure));
                }

                // Store current structure
                _lastStructure = structure;
            }
        });

        _jobs.addObserver(new JobModuleObserver() {
            @Override
            public void onJobCancel(JobModel job) {
                _structures.removeIf(item -> item.getBuildJob() == job);
            }

            @Override
            public void onJobComplete(JobModel job) {
                _structures.removeIf(item -> item.getBuildJob() == job);
            }
        });
    }

    @Override
    protected void onGameStart(Game game) {
        _structures.stream()
                .filter(item -> item.getBuildProgress() < item.getBuildCost())
                .forEach(this::launchBuild);
    }

    @Override
    protected void onGameUpdate(Game game, int tick) {
        if (tick % 10 == 0) {
// TODO
            //            // Create hauling jobs
//            _structures.stream().filter(structure -> !structure.isComplete())
//                    .forEach(item -> item.getComponents().stream()
//                            .filter(component -> component.currentQuantity < component.neededQuantity && component.job == null)
//                            .forEach(component -> _jobs.addJob(new HaulJob(item, component))));

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
//            structure.setComplete(complete);
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

    /**
     * Create build job
     *
     * @param structure to build
     */
    private void launchBuild(StructureModel structure) {
        BuildJob job = new BuildJob(structure);
        structure.setBuildJob(job);
        _jobs.addJob(job);
    }

    public void addPattern(ParcelModel parcel, ItemInfo itemInfo) {
        // Create structure
        StructureModel structure = new StructureModel(itemInfo);
        structure.setBuildProgress(0);
        structure.setParcel(parcel);
        _structures.add(structure);

        launchBuild(structure);
    }
}
