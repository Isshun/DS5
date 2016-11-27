package org.smallbox.faraway.debug;

import org.apache.commons.lang3.StringUtils;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.BindModule;
import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.core.util.Log;
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

    public void execute(String command) {
        Log.info("Execute: " + command);

        int x = Application.gameManager.getGame().getViewport().getRelativePosX();
        int y = Application.gameManager.getGame().getViewport().getRelativePosY();
        int z = Application.gameManager.getGame().getViewport().getFloor();
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
