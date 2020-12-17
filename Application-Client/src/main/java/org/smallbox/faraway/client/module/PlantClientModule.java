package org.smallbox.faraway.client.module;

import org.smallbox.faraway.client.PlantCommon;
import org.smallbox.faraway.common.CharacterCommon;
import org.smallbox.faraway.core.dependencyInjector.GameObject;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@GameObject
public class PlantClientModule {

    public Map<Long, PlantCommon> plants = new ConcurrentHashMap<>();

    public void update(PlantCommon plant) {
        plants.put(plant.id, plant);
    }
}