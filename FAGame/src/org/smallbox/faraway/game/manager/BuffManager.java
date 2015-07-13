package org.smallbox.faraway.game.manager;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;
import org.smallbox.faraway.engine.lua.LuaCharacterModel;
import org.smallbox.faraway.engine.lua.LuaGameModel;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.model.character.BuffModel;
import org.smallbox.faraway.game.model.character.base.CharacterModel;
import org.smallbox.faraway.util.FileUtils;
import org.smallbox.faraway.util.Log;

import java.util.Collections;

/**
 * Created by Alex on 16/06/2015.
 */
public class BuffManager extends BaseManager {
    private final LuaValue  _luaGame;

    public BuffManager() {
        _luaGame = CoerceJavaToLua.coerce(new LuaGameModel(Game.getInstance()));
        _updateInterval = 10;
    }

    @Override
    protected void onUpdate(int tick) {
        for (CharacterModel character: Game.getCharacterManager().getCharacters()) {
            if (character.isAlive()) {
                character.getNeeds().happinessChange = 0;
                int length = character._buffs.size();
                for (int i = 0; i < length; i++) {
                    BuffModel buff = character._buffs.get(i);
                    LuaValue ret = buff.globals.get("OnUpdate").call(_luaGame, buff.luaCharacter);
                    if (!ret.isnil()) {
                        buff.message = ret.get(1).toString();
                        buff.level = ret.get(2).toint();
                        buff.mood = ret.get(3).toint();

                        LuaValue effects = ret.get(4).isnil() ? null : ret.get(4);
                        if (effects != null) {
                            for (int j = 0; j < effects.length(); j++) {
                                if (Math.random() <= effects.get(j + 1).get(2).todouble()) {
                                    Log.notice("apply buff effect: " + effects.get(j + 1).get(1).toString() + " (" + buff.message + ")");
                                    ((DiseaseManager)Game.getInstance().getManager(DiseaseManager.class)).apply(
                                            character,
                                            effects.get(j + 1).get(1).toString(),
                                            effects.get(j + 1).get(3));
                                }
                            }
                        }

                        character.getNeeds().happinessChange += buff.mood;
                    } else {
                        buff.level = 0;
                    }
                }
                Collections.sort(character._buffs, (b1, b2) -> b2.mood - b1.mood);
            }
        }
//        Game.getInstance().notify(observer -> observer.onCloseQuest(quest));
    }

    @Override
    public void onAddCharacter(CharacterModel character) {
        LuaValue luaCharacter = CoerceJavaToLua.coerce(new LuaCharacterModel(character));

        // Load lua scripts
        FileUtils.listRecursively("data/buffs/").stream().filter(file -> file.getName().endsWith(".lua")).forEach(file -> {
            BuffModel buff = new BuffModel();
            buff.globals = JsePlatform.standardGlobals();
            buff.globals.get("dofile").call(LuaValue.valueOf(file.getAbsolutePath()));
            buff.luaCharacter = luaCharacter;

            LuaValue onStart = buff.globals.get("OnStart");
            if (!onStart.isnil()) {
                LuaValue ret = onStart.call(_luaGame, buff.luaCharacter);
                if (!ret.isnil() && !ret.toboolean()) {
                    return;
                }
            }

            character.addBuff(buff);
        });
    }
}
