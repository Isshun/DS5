package org.smallbox.faraway.game.manager;

import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;
import org.smallbox.faraway.engine.lua.LuaCharacterModel;
import org.smallbox.faraway.engine.lua.LuaGameModel;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.model.BuffModel;
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

    private Map<BuffModel, LuaValue> _buffs;

    public BuffManager() {
        _buffs = new HashMap<>();
        _updateInterval = 10;
    }

    @Override
    protected void onUpdate(int tick) {
        LuaValue luaGame = CoerceJavaToLua.coerce(new LuaGameModel(Game.getInstance()));

        for (CharacterModel character: Game.getCharacterManager().getCharacters()) {
            if (character.isAlive()) {
                character.getNeeds().happinessChange = 0;
                LuaValue luaCharacter = CoerceJavaToLua.coerce(new LuaCharacterModel(character));
                for (BuffModel buff : character._buffs) {
                    LuaValue ret = _buffs.get(buff).get("OnUpdate").call(luaGame, luaCharacter);
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
//                                    BuffEffect effect = getEffect(effects.get(i + 1).get(1).toString());
//                                    if (effect != null) {
//                                        effect.buffEffect.onEffect(character);
//                                    }
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

//    private BuffEffect getEffect(String effectName) {
//        for (BuffEffect effect: EFFECTS) {
//            if (effect.name.equals(effectName)) {
//                return effect;
//            }
//        }
//        return null;
//    }

    @Override
    public void onAddCharacter(CharacterModel character) {
        try {
            for (File file : new File("data/buffs/scripted/").listFiles()) {
                if (file.getName().endsWith(".lua")) {
                    // Load lua script
                    BuffModel buff = new BuffModel();
                    LuaValue globals = JsePlatform.standardGlobals();
                    globals.get("dofile").call(LuaValue.valueOf("data/buffs/scripted/" + file.getName()));

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
