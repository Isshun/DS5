package org.smallbox.faraway.game.manager;

import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;
import org.smallbox.faraway.engine.lua.LuaCharacterModel;
import org.smallbox.faraway.engine.lua.LuaGameModel;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.model.BuffModel;
import org.smallbox.faraway.game.model.DiseaseModel;
import org.smallbox.faraway.game.model.character.base.CharacterModel;
import org.smallbox.faraway.util.Log;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Alex on 16/06/2015.
 */
public class DiseaseManager extends BaseManager {

    private Map<DiseaseModel, LuaValue> _diseases;

    public DiseaseManager() {
        _diseases = new HashMap<>();
        _updateInterval = 10;
    }

    @Override
    protected void onUpdate(int tick) {
        LuaValue luaGame = CoerceJavaToLua.coerce(new LuaGameModel(Game.getInstance()));

        for (CharacterModel character: Game.getCharacterManager().getCharacters()) {
            if (character.isAlive()) {
                character.getNeeds().happinessChange = 0;
                LuaValue luaCharacter = CoerceJavaToLua.coerce(new LuaCharacterModel(character));
                for (DiseaseModel disease : character._diseases) {
                    LuaValue ret = _diseases.get(disease).get("OnUpdate").call(luaGame, luaCharacter, disease.data);
                    if (!ret.isnil()) {
                        disease.message = ret.toString();
                    }
                }
            }
        }
//        Game.getInstance().notify(observer -> observer.onCloseQuest(quest));
    }

    public void apply(CharacterModel character, String name, LuaValue data) {
        DiseaseModel disease = character.getDisease(name);
        if (disease != null) {
            disease.data = data;
        } else {
            try {
                for (File file : new File("data/diseases/").listFiles()) {
                    if (file.getName().equals(name + ".lua")) {
                        // Load lua script
                        disease = new DiseaseModel(name);
                        disease.data = data;
                        LuaValue globals = JsePlatform.standardGlobals();
                        globals.get("dofile").call(LuaValue.valueOf("data/diseases/" + file.getName()));

                        _diseases.put(disease, globals);
                        character.addDisease(disease);

                        LuaValue luaGame = CoerceJavaToLua.coerce(new LuaGameModel(Game.getInstance()));
                        LuaValue luaCharacter = CoerceJavaToLua.coerce(new LuaCharacterModel(character));
                        LuaValue onStart = globals.get("OnStart");
                        if (!onStart.isnil()) {
                            onStart.call(luaGame, luaCharacter, data);
                        }
                    }
                }
            } catch (LuaError error) {
                error.printStackTrace();
                Log.error(error.getMessage());
            }
        }
    }

    @Override
    public void onAddCharacter(CharacterModel character) {
    }
}
