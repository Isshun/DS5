package org.smallbox.faraway.game.manager;

import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;
import org.smallbox.faraway.engine.lua.LuaCharacterModel;
import org.smallbox.faraway.engine.lua.LuaGameModel;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.model.character.BuffModel;
import org.smallbox.faraway.game.model.character.base.CharacterModel;
import org.smallbox.faraway.util.Log;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Alex on 16/06/2015.
 */
public class BuffManager extends BaseManager {
    private final LuaValue                  _luaGame;
    private Map<BuffModel, LuaValue>        _buffs;
    private Map<CharacterModel, LuaValue>   _luaCharacters;

    public BuffManager() {
        _luaGame = CoerceJavaToLua.coerce(new LuaGameModel(Game.getInstance()));
        _luaCharacters = new HashMap<>();
        _buffs = new HashMap<>();
        _updateInterval = 10;
    }

    @Override
    protected void onUpdate(int tick) {
        for (CharacterModel character: Game.getCharacterManager().getCharacters()) {
            if (character.isAlive()) {
                character.getNeeds().happinessChange = 0;
                for (BuffModel buff : character._buffs) {
                    LuaValue ret = _buffs.get(buff).get("OnUpdate").call(_luaGame, _luaCharacters.get(character));
                    if (!ret.isnil()) {
                        buff.message = ret.get(1).toString();
                        buff.level = ret.get(2).toint();
                        buff.mood = ret.get(3).toint();

                        LuaValue effects = ret.get(4).isnil() ? null : ret.get(4);
                        if (effects != null) {
                            for (int i = 0; i < effects.length(); i++) {
                                if (Math.random() <= effects.get(i + 1).get(2).todouble()) {
                                    Log.notice("apply buff effect: " + effects.get(i + 1).get(1).toString() + " (" + buff.message + ")");
                                    ((DiseaseManager)Game.getInstance().getManager(DiseaseManager.class)).apply(
                                            character,
                                            effects.get(i + 1).get(1).toString(),
                                            effects.get(i + 1).get(3));
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
        _luaCharacters.put(character, CoerceJavaToLua.coerce(new LuaCharacterModel(character)));

        try {
            for (File file : new File("data/buffs/").listFiles()) {
                if (file.getName().endsWith(".lua")) {
                    // Load lua script
                    BuffModel buff = new BuffModel();
                    LuaValue globals = JsePlatform.standardGlobals();
                    globals.get("dofile").call(LuaValue.valueOf("data/buffs/" + file.getName()));

                    LuaValue onStart = globals.get("OnStart");
                    if (!onStart.isnil()) {
                        onStart.call(_luaGame, _luaCharacters.get(character));
                    }

                    _buffs.put(buff, globals);
                    character.addBuff(buff);
                }
            }
        } catch (LuaError error) {
            error.printStackTrace();
            Log.error(error.getMessage());
        }
    }
}
