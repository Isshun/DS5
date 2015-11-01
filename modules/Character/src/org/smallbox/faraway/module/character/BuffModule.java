package org.smallbox.faraway.module.character;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.smallbox.faraway.core.engine.lua.LuaCharacterModel;
import org.smallbox.faraway.core.engine.lua.LuaGameModel;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.model.GameData;
import org.smallbox.faraway.core.game.module.character.model.BuffCharacterModel;
import org.smallbox.faraway.core.game.module.character.model.base.CharacterModel;
import org.smallbox.faraway.core.module.GameModule;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Alex on 16/06/2015.
 */
public class BuffModule extends GameModule {
    private Map<CharacterModel, List<BuffCharacterModel>>   _characters;
    private List<BuffCharacterModel>                        _charactersData;

    @Override
    protected void onLoaded() {
        _characters = new HashMap<>();
        _charactersData = new ArrayList<>();
        _updateInterval = 10;
    }

    @Override
    protected void onUpdate(int tick) {
        _charactersData.forEach(buff -> {buff.check(tick); buff.update(tick);});
        for (Map.Entry<CharacterModel, List<BuffCharacterModel>> entry: _characters.entrySet()) {
            Collections.sort(entry.getValue(), (b1, b2) -> {
                if (b1.level == 0) return 1;
                if (b2.level == 0) return -1;
                return b2.mood - b1.mood;
            });
        }
    }

    @Override
    public void onAddCharacter(CharacterModel character) {
        LuaValue luaCharacter = CoerceJavaToLua.coerce(character);

        List<BuffCharacterModel> dataList = GameData.getData().buffs.stream().map(buff -> new BuffCharacterModel(buff, luaCharacter, character)).collect(Collectors.toList());
        dataList.forEach(BuffCharacterModel::start);

        _characters.put(character, dataList);
        _charactersData.addAll(dataList);
    }

    public List<BuffCharacterModel> getActiveBuffs(CharacterModel character) {
        return _characters.get(character);
    }

}
