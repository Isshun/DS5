package org.smallbox.faraway.modules.structure;

import org.smallbox.faraway.common.NotImplementedException;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.engine.module.GenericGameModule;
import org.smallbox.faraway.core.game.Data;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.module.path.PathManager;
import org.smallbox.faraway.core.module.world.model.MapObjectModel;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.core.module.world.model.StructureItem;
import org.smallbox.faraway.modules.building.BuildJobFactory;
import org.smallbox.faraway.modules.consumable.ConsumableModule;
import org.smallbox.faraway.modules.job.JobModel;
import org.smallbox.faraway.modules.job.JobModule;
import org.smallbox.faraway.modules.job.JobModuleObserver;
import org.smallbox.faraway.modules.world.WorldModule;
import org.smallbox.faraway.util.log.Log;

@GameObject
public class StructureModule extends GenericGameModule<StructureItem, StructureModuleObserver> {

    @Inject
    private PathManager pathManager;

    @Inject
    private WorldModule worldModule;

    @Inject
    private JobModule jobModule;

    @Inject
    private ConsumableModule consumableModule;

    @Inject
    private BuildJobFactory buildJobFactory;

    @Inject
    private Data data;

    @Override
    public void onGameCreate(Game game) {
//        worldInteractionModule.addObserver(new WorldInteractionModuleObserver() {
//            public StructureItem _lastStructure;
//
//            @Override
//            public void onSelect(GameEvent event, Collection<ParcelModel> parcels) {
//                // Get structure on parcel
//                StructureItem structure = _structures.stream()
//                        .filter(s -> parcels.contains(s.getParcel()))
//                        .findAny()
//                        .orElse(null);
//
//                // Call observers
//                if (structure != null) {
//                    notifyObservers(obs -> obs.onSelectStructure(structure));
//                } else if (_lastStructure != null) {
//                    notifyObservers(obs -> obs.onDeselectStructure(_lastStructure));
//                }
//
//                // Store current structure
//                _lastStructure = structure;
//            }
//        });

        jobModule.addObserver(new JobModuleObserver() {
            @Override
            public void onJobCancel(JobModel job) {
                modelList.removeIf(item -> item.getBuildJob() == job);
            }

            @Override
            public void onJobComplete(JobModel job) {
                modelList.removeIf(item -> item.getBuildJob() == job);
            }
        });
    }

    @Override
    public void onGameStart(Game game) {
    }

    @Override
    protected void onModuleUpdate(Game game) {
//        createBuildJobs(jobModule, consumableModule, buildJobFactory, _structures);
//        createRepairJobs(jobModule, _structures);
    }

    public void removeStructure(StructureItem structure) {
        if (structure != null && structure.getParcel() != null) {
            ParcelModel parcel = structure.getParcel();
            moveStructureToParcel(parcel, null);

            jobModule.onCancelJobs(structure.getParcel(), structure);
            pathManager.refreshConnections(structure.getParcel());

            notifyObservers(observer -> observer.onRemoveStructure(parcel, structure));
        }
    }

    private void moveStructureToParcel(ParcelModel parcel, StructureItem structure) {
        parcel.setItem(structure);
        if (structure != null) {
            structure.setParcel(parcel);
        }
    }

    @Override
    public void onCancelJobs(ParcelModel parcel, Object object) {
        StructureItem structure = parcel.getItem(StructureItem.class);
        if (structure != null && !structure.isComplete() && (object == null || object instanceof StructureItem)) {
            parcel.removeItem(structure);
            notifyObservers(observer -> observer.onRemoveStructure(parcel, structure));
        }
    }

    @Override
    public void removeObject(MapObjectModel mapObjectModel) {
        if (mapObjectModel.isStructure() && mapObjectModel instanceof StructureItem) {
            removeStructure((StructureItem) mapObjectModel);
        }
    }

    @Override
    public void putObject(ParcelModel parcel, ItemInfo itemInfo, int data, boolean complete) {
        if (itemInfo.isStructure) {
            putStructure(parcel, itemInfo, data, complete);
        }
    }

    private StructureItem putStructure(ParcelModel parcel, ItemInfo itemInfo, int matterSupply, boolean complete) {
        if (parcel.hasItem(StructureItem.class) || parcel.getItem(StructureItem.class).isFloor()) {
            StructureItem structure = new StructureItem(itemInfo);
            structure.addProgress(complete ? itemInfo.cost : 0);
//            structure.setComplete(complete);

            if (structure.getInfo().receipts != null && structure.getInfo().receipts.size() > 0) {
                structure.setReceipt(structure.getInfo().receipts.get(0));
            }

            moveStructureToParcel(parcel, structure);
            modelList.add(structure);

            pathManager.refreshConnections(structure.getParcel());

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
    private void launchBuild(StructureItem structure) {
        throw new NotImplementedException();
    }

    public void addPattern(ParcelModel parcel, ItemInfo itemInfo) {
        Log.info("Add pattern for %s at position %s", itemInfo, parcel);

        // Create structure
        StructureItem structure = new StructureItem(itemInfo);
        structure.setBuildProgress(0);
        structure.setParcel(parcel);
        modelList.add(structure);
    }

    public StructureItem addStructure(String itemName, int x, int y, int z) {
        ItemInfo itemInfo = data.getItemInfo(itemName);
        StructureItem structure = new StructureItem(itemInfo);
        return addStructure(structure, x, y, z);
    }

    public StructureItem addStructure(StructureItem structure, int x, int y, int z) {
        ParcelModel parcel = worldModule.getParcel(x, y, z);
        if (parcel != null) {
            structure.setParcel(parcel);
            modelList.add(structure);
        }
        return structure;
    }

    public StructureItem getStructure(ParcelModel parcel) {
        for (StructureItem structure: modelList) {
            if (structure.getParcel() == parcel) {
                return structure;
            }
        }
        return null;
    }
}
