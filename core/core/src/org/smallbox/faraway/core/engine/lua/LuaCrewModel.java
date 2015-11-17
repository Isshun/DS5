package org.smallbox.faraway.core.engine.lua;

import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.module.character.model.base.CharacterModel;
import org.smallbox.faraway.core.engine.module.java.ModuleHelper;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Alex on 19/06/2015.
 */
public class LuaCrewModel {
    public int count = 42;

    public LuaCharacterModel add() {
        CharacterModel character = ModuleHelper.getCharacterModule().addRandom(5, 5, Game.getInstance().getInfo().worldFloors - 1);
        return new LuaCharacterModel(character);
    }

    public LuaCharacterModel add(LuaCharacterModel luaCharacter) {
        ModuleHelper.getCharacterModule().add(luaCharacter.character);
        return luaCharacter;
    }

    public List<LuaCharacterModel> list() {
        return ModuleHelper.getCharacterModule().getCharacters().stream().map(LuaCharacterModel::new).collect(Collectors.toList());
    }

    public CharacterModel getSelected() {
        return Game.getInstance().getSelector().getSelectedCharacter();
    }
}
