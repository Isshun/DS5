package org.smallbox.faraway.core.engine.lua;

import org.smallbox.faraway.core.game.module.character.model.base.CharacterModel;
import org.smallbox.faraway.core.module.java.ModuleHelper;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Alex on 19/06/2015.
 */
public class LuaVisitorModel {
    public LuaCharacterModel add() {
        CharacterModel character = ModuleHelper.getCharacterModule().addRandom(5, 5);
        return new LuaCharacterModel(character);
    }

    public LuaCharacterModel add(LuaCharacterModel luaCharacter) {
        ModuleHelper.getCharacterModule().add(luaCharacter.character);
        return luaCharacter;
    }

    public void remove(LuaCharacterModel luaCharacter) {
        ModuleHelper.getCharacterModule().remove(luaCharacter.character);
    }

    public List<LuaCharacterModel> list() {
        return ModuleHelper.getCharacterModule().getCharacters().stream().map(LuaCharacterModel::new).collect(Collectors.toList());
    }
}
