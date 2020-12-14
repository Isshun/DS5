package org.smallbox.faraway.modules;

import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.engine.module.ModuleObserver;
import org.smallbox.faraway.core.module.world.model.BuildableMapObject;
import org.smallbox.faraway.core.module.world.model.MapObjectModel;
import org.smallbox.faraway.modules.building.BasicBuildJob;
import org.smallbox.faraway.modules.building.BasicRepairJob;
import org.smallbox.faraway.modules.consumable.ConsumableModule;
import org.smallbox.faraway.modules.job.JobModule;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Alex on 16/03/2017.
 */
public abstract class BuildItemModule<T extends ModuleObserver> extends GameModule<T>  {

    protected void createBuildJobs(JobModule jobModule, ConsumableModule consumableModule, Collection<? extends BuildableMapObject> mapObjects) {
        List<MapObjectModel> objectsInBuildJob = jobModule.getJobs().stream()
                .filter(job -> job instanceof BasicBuildJob)
                .map(job -> (BasicBuildJob)job)
                .map(BasicBuildJob::getObject)
                .collect(Collectors.toList());

        // Crée les hauling jobs
        mapObjects.stream()
                .filter(mapObject -> !mapObject.isComplete())
                .filter(mapObject -> !mapObject.hasAllComponents())
                .forEach(mapObject ->
                        mapObject._components.forEach((itemInfo, pair) -> {
                            if (pair.availableQuantity < pair.neededQuantity) {
                                consumableModule.createHaulToFactoryJobs(mapObject, itemInfo, pair.neededQuantity - pair.availableQuantity);
                            }
                        }));

        // Crée les build jobs
        mapObjects.stream()
                .filter(structure -> !structure.isComplete())
                .filter(BuildableMapObject::hasAllComponents)
                .filter(structure -> !objectsInBuildJob.contains(structure))
                .forEach(structure -> BasicBuildJob.buildStructure(jobModule, structure));
    }

    protected void createRepairJobs(JobModule jobModule, Collection<? extends BuildableMapObject> mapObjects) {
        List<MapObjectModel> objectsInRepairJob = jobModule.getJobs().stream()
                .filter(job -> job instanceof BasicRepairJob)
                .map(job -> (BasicRepairJob)job)
                .map(BasicRepairJob::getObject)
                .collect(Collectors.toList());

        // Crée les repair jobs
        mapObjects.stream()
                .filter(mapObject -> mapObject.getHealth() < mapObject.getMaxHealth())
                .filter(mapObject -> !objectsInRepairJob.contains(mapObject))
                .forEach(mapObject -> BasicRepairJob.repairStructure(jobModule, mapObject));
    }

}
