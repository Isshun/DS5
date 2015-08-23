package org.smallbox.faraway.engine.lua;

import org.smallbox.faraway.game.model.GameData;
import org.smallbox.faraway.game.model.character.AndroidModel;
import org.smallbox.faraway.game.model.character.DroidModel;
import org.smallbox.faraway.game.model.character.HumanModel;
import org.smallbox.faraway.util.Utils;

/**
 * Created by Alex on 20/06/2015.
 */
public class LuaFactoryModel {

    public LuaCharacterModel createCharacter(String type) {
        switch (type) {
            case "human":
                return new LuaCharacterModel(new HumanModel(Utils.getUUID(), 5, 5, null, null, 16));
            case "android":
                return new LuaCharacterModel(new AndroidModel(Utils.getUUID(), 5, 5, null, null, 16));
            case "droid":
                return new LuaCharacterModel(new DroidModel(Utils.getUUID(), 5, 5, null, null, 16));
        }
        return null;
    }

    public LuaConsumableModel createConsumable(String itemName, int quantity) {
        return new LuaConsumableModel(GameData.getData().getItemInfo(itemName), quantity);
    }
}
