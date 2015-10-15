package org.smallbox.faraway.module.character;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.smallbox.faraway.engine.lua.LuaCharacterModel;
import org.smallbox.faraway.engine.lua.LuaGameModel;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.model.GameData;
import org.smallbox.faraway.game.model.character.BuffCharacterModel;
import org.smallbox.faraway.game.model.character.base.CharacterModel;
import org.smallbox.faraway.game.module.GameModule;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Alex on 16/06/2015.
 */
public class BuffModule extends GameModule {
    private Map<CharacterModel, List<BuffCharacterModel>>   _characters;
    private List<BuffCharacterModel>                        _charactersData;
    private LuaValue                                        _luaGame;

    @Override
    protected void onLoaded() {
        _characters = new HashMap<>();
        _charactersData = new ArrayList<>();
        _luaGame = CoerceJavaToLua.coerce(new LuaGameModel(Game.getInstance()));
        _updateInterval = 10;
    }

    @Override
    protected void onUpdate(int tick) {
        _charactersData.forEach(buff -> buff.update(tick));
        for (Map.Entry<CharacterModel, List<BuffCharacterModel>> entry: _characters.entrySet()) {
            Collections.sort(entry.getValue(), (b1, b2) -> {
                if (b1.level == 0) return 1;
                if (b2.level == 0) return -1;
                return b2.mood - b1.mood;
            });
        }

//        _charactersData.forEach(data -> {
//            if (data.character.isAlive()) {
//                data.character.getNeeds().happinessChange = 0;
//
//                GameData.getData().buffs.forEach(buff -> buff.update(data, tick));
//
////                int length = character.getBuffs().size();
////                for (int i = 0; i < length; i++) {
////                    BuffModel buff = character.getBuffs().get(i);
////                    LuaValue ret = buff.globals.get("OnUpdate").call(_luaGame, buff.luaCharacter);
////                }
////                Collections.sort(character.getBuffs(), (b1, b2) -> b2.mood - b1.mood);
////            }
//            }
//        });

//        Game.getInstance().notify(observer -> observer.onCloseQuest(quest));
    }

    @Override
    public void onAddCharacter(CharacterModel character) {
        LuaValue luaCharacter = CoerceJavaToLua.coerce(new LuaCharacterModel(character));

        List<BuffCharacterModel> dataList = GameData.getData().buffs.stream().map(buff -> new BuffCharacterModel(buff, luaCharacter, character)).collect(Collectors.toList());
        dataList.forEach(BuffCharacterModel::start);

        _characters.put(character, dataList);
        _charactersData.addAll(dataList);

//        // Load lua scripts
//        FileUtils.listRecursively("data/buffs/").stream().filter(file -> file.getName().endsWith(".lua")).forEach(file -> {
//            try {
//                BuffModel buff = new BuffModel();
//                buff.globals = JsePlatform.standardGlobals();
//                buff.globals.load("function main(g)\n game = g\n end", "main").call();
//                buff.globals.get("main").call(CoerceJavaToLua.coerce(new org.smallbox.faraway.module.lua.luaModel.LuaGameModel(null, new LuaEventsModel(), UserInterface.getInstance(), null)));
//                buff.globals.load(new FileReader(file), file.getName()).call();
////            buff.globals.get("dofile").call(LuaValue.valueOf(file.getAbsolutePath()));
//                buff.luaCharacter = luaCharacter;
//
//                LuaValue onStart = buff.globals.get("OnStart");
//                if (!onStart.isnil()) {
//                    LuaValue ret = onStart.call(_luaGame, buff.luaCharacter);
//                    if (!ret.isnil() && !ret.toboolean()) {
//                        return;
//                    }
//                }
//
//                character.addBuff(buff);
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }
//        });
    }

    public List<BuffCharacterModel> getActiveBuffs(CharacterModel character) {
        return _characters.get(character);
    }

}
