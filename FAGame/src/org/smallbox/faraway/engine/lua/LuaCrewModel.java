package org.smallbox.faraway.engine.lua;

import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.model.character.base.CharacterModel;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Alex on 19/06/2015.
 */
public class LuaCrewModel {
    public LuaCharacterModel add() {
        CharacterModel character = Game.getCharacterManager().addRandom(5, 5);
        return new LuaCharacterModel(character);
    }

    public LuaCharacterModel add(LuaCharacterModel luaCharacter) {
        Game.getCharacterManager().add(luaCharacter.character);
        return luaCharacter;
    }

    public List<LuaCharacterModel> list() {
        return Game.getCharacterManager().getCharacters().stream().map(LuaCharacterModel::new).collect(Collectors.toList());
    }
}
