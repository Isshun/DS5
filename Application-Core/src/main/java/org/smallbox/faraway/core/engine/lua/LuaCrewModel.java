package org.smallbox.faraway.core.engine.lua;

import org.apache.commons.lang3.NotImplementedException;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;

import java.util.List;

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
        throw new RuntimeException("Not implemented");
//        return Application.gameManager.getGame().getSelector().getSelectedCharacter();
    }
}
