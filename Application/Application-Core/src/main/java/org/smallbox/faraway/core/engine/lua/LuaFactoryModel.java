package org.smallbox.faraway.core.engine.lua;

import org.smallbox.faraway.core.Application;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Created by Alex on 20/06/2015.
 */
public class LuaFactoryModel {

    public LuaCharacterModel createCharacter(String type) {
        throw new NotImplementedException();

//        switch (type) {
//            case "human":
//                return new LuaCharacterModel(new HumanModel(Utils.getUUID(), WorldHelper.getParcel(5, 5), null, null, 16));
//            case "android":
//                return new LuaCharacterModel(new AndroidModel(Utils.getUUID(), WorldHelper.getParcel(5, 5), null, null, 16));
//            case "droid":
//                return new LuaCharacterModel(new DroidModel(Utils.getUUID(), WorldHelper.getParcel(5, 5), null, null, 16));
//        }
//        return null;
    }

    public LuaConsumableModel createConsumable(String itemName, int quantity) {
        return new LuaConsumableModel(Application.data.getItemInfo(itemName), quantity);
    }
}
