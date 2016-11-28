package org.smallbox.faraway.core.engine.lua;

import org.apache.commons.lang3.NotImplementedException;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.module.character.model.base.CharacterModel;

import java.util.List;

/**
 * Created by Alex on 19/06/2015.
 */
public class LuaCrewModel {
    public int count = 42;

    public LuaCharacterModel add() {
        throw new NotImplementedException("");

//        CharacterModel character = ModuleHelper.getCharacterModule().addRandom(5, 5, Application.gameManager.getGame().getInfo().worldFloors - 1);
//        return new LuaCharacterModel(character);
    }

    public LuaCharacterModel add(LuaCharacterModel luaCharacter) {
        throw new NotImplementedException("");

//        ModuleHelper.getCharacterModule().add(luaCharacter.character);
//        return luaCharacter;
    }

    public List<LuaCharacterModel> list() {
        throw new NotImplementedException("");

//        return ModuleHelper.getCharacterModule().getCharacters().stream().map(LuaCharacterModel::new).collect(Collectors.toList());
    }

    public CharacterModel getSelected() {
        return Application.gameManager.getGame().getSelector().getSelectedCharacter();
    }
}
