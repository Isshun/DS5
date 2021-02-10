package org.smallbox.faraway.game.world;

import org.apache.commons.lang3.RandomUtils;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.module.GenericGameModule;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@GameObject
public class RadiationModule extends GenericGameModule<Parcel> {
    private Map<Parcel, Float> parcels = new ConcurrentHashMap<>();

    public float getLevel(Parcel parcel) {
        return parcels.computeIfAbsent(parcel, p -> RandomUtils.nextFloat(0, 1));
    }
}