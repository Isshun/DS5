package org.smallbox.faraway.modules;

import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.consumable.ConsumableModule;
import org.smallbox.faraway.modules.job.JobModule;
import org.smallbox.faraway.modules.world.WorldModule;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Alex on 28/02/2017.
 */
public class DigModule extends GameModule {

    @BindModule
    private WorldModule worldModule;

    @BindModule
    private JobModule jobModule;

    @BindModule
    private ConsumableModule consumableModule;

    private Map<ParcelModel, BasicDigJob> _parcels = new ConcurrentHashMap<>();

    @Override
    public void onGameCreate(Game game) {
    }

    @Override
    public void onGameStart(Game game) {
        worldModule.getParcelList().stream()
                .filter(parcel -> parcel.z == 1)
                .filter(parcel -> !_parcels.containsKey(parcel))
                .filter(parcel -> parcel.getRockInfo() != null)
                .forEach(parcel -> {
                    BasicDigJob digJob = BasicDigJob.create(consumableModule, worldModule, parcel);
                    if (digJob != null) {
                        _parcels.put(parcel, digJob);
                        jobModule.addJob(digJob);
                    }
                });
    }

    @Override
    protected void onGameUpdate(Game game, int tick) {
    }

}
