package org.smallbox.faraway.debug;

import org.apache.commons.lang3.StringUtils;
import org.smallbox.faraway.client.ApplicationClient;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.util.Log;
import org.smallbox.faraway.module.character.CharacterModule;
import org.smallbox.faraway.module.consumable.ConsumableModule;
import org.smallbox.faraway.module.world.WorldModule;

/**
 * Created by Alex on 18/11/2016.
 */
public class DebugModule extends GameModule {

    @BindModule
    private CharacterModule     characterModule;

    @BindModule
    private ConsumableModule    consumableModule;

    @BindModule
    private WorldModule         worldModule;

    @Override
    public void onGameCreate(Game game) {
    }

    public void execute(String command) {
        Log.info("Execute: " + command);

        int x = ApplicationClient.mainRenderer.getViewport().getRelativePosX();
        int y = ApplicationClient.mainRenderer.getViewport().getRelativePosY();
        int z = ApplicationClient.mainRenderer.getViewport().getFloor();
        ParcelModel parcel = worldModule.getParcel(x, y, z);

        switch (StringUtils.trim(command)) {
            case "add character":
                characterModule.addRandom(10, 10, WorldHelper.getCurrentFloor());
                break;
            case "add wood":
                consumableModule.putConsumable(parcel, Application.data.getItemInfo("base.consumable.wood_board"), 100);
                break;
        }
    }
}
