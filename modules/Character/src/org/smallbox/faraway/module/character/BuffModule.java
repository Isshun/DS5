package org.smallbox.faraway.module.character;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.engine.module.java.ModuleManager;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.Data;
import org.smallbox.faraway.core.game.module.character.CharacterModule;
import org.smallbox.faraway.core.game.module.character.model.BuffCharacterModel;
import org.smallbox.faraway.core.game.module.character.model.base.CharacterModel;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Alex on 16/06/2015.
 */
public class BuffModule extends GameModule {
    private Map<CharacterModel, List<BuffCharacterModel>>   _characters;
    private List<BuffCharacterModel>                        _charactersData;

    @Override
    protected void onGameStart(Game game) {
        _characters = new HashMap<>();
        _charactersData = new ArrayList<>();
        _updateInterval = 10;
        ((CharacterModule) ModuleManager.getInstance().getModule(CharacterModule.class)).getCharacters().forEach(this::addCharacter);
    }

    private void addCharacter(CharacterModel character) {
        LuaValue luaCharacter = CoerceJavaToLua.coerce(character);

        List<BuffCharacterModel> dataList = Data.getData().buffs.stream().map(buff -> new BuffCharacterModel(buff, luaCharacter, character)).collect(Collectors.toList());
        dataList.forEach(BuffCharacterModel::start);

        _characters.put(character, dataList);
        _charactersData.addAll(dataList);
    }

    @Override
    protected void onUpdate(int tick) {
        _charactersData.forEach(buff -> {buff.check(tick); buff.update(tick);});
        for (Map.Entry<CharacterModel, List<BuffCharacterModel>> entry: _characters.entrySet()) {
            Collections.sort(entry.getValue(), (b1, b2) -> b2.mood - b1.mood);
        }
    }

    @Override
    public void onAddCharacter(CharacterModel character) {
        addCharacter(character);
    }

    public List<BuffCharacterModel> getActiveBuffs(CharacterModel character) {
        return _characters.get(character);
    }

}
