package org.smallbox.faraway.modules.hauling;

import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.consumable.ConsumableModule;
import org.smallbox.faraway.modules.job.JobModule;
import org.smallbox.faraway.modules.world.WorldModule;

/**
 * Created by Alex on 02/03/2017.
 */
public class HaulingModule extends GameModule {

    @BindModule
    private WorldModule worldModule;

    @BindModule
    private ConsumableModule consumableModule;

    @BindModule
    private JobModule jobModule;

    @Override
    public void onGameCreate(Game game) {
    }

    @Override
    public void onGameUpdate(Game game) {
        ParcelModel storageParcel = worldModule.getParcel(10, 10, 1);

//        consumableModule.getConsumables().stream()
//                .filter(consumable -> consumable.getQuantity() > 0)
//                .filter(consumable -> consumable.getParcel() != storageParcel)
//                .forEach(consumable -> {
//                    BasicHaulJob.toParcel(consumableModule, jobModule, consumable.getInfo(), Collections.singletonMap(consumable, consumable.getQuantity()), storageParcel, consumable.getQuantity());
//                });

    }

}
