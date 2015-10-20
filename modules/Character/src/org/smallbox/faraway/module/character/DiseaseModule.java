package org.smallbox.faraway.module.character;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;
import org.smallbox.faraway.core.engine.lua.LuaCharacterModel;
import org.smallbox.faraway.core.engine.lua.LuaGameModel;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.module.character.model.DiseaseModel;
import org.smallbox.faraway.core.game.module.character.model.base.CharacterModel;
import org.smallbox.faraway.core.module.GameModule;
import org.smallbox.faraway.core.module.java.ModuleHelper;
import org.smallbox.faraway.core.util.FileUtils;

/**
 * Created by Alex on 16/06/2015.
 */
public class DiseaseModule extends GameModule {
    private LuaValue                  _luaGame;

    @Override
    protected void onLoaded() {
        _luaGame = CoerceJavaToLua.coerce(new LuaGameModel(Game.getInstance()));
        _updateInterval = 10;
    }

    @Override
    protected void onUpdate(int tick) {
        ModuleHelper.getCharacterModule().getCharacters().stream().filter(CharacterModel::isAlive).forEach(character -> {
            character.getNeeds().happinessChange = 0;
            for (DiseaseModel disease : character._diseases) {
                LuaValue onUpdate = disease.globals.get("OnUpdate");
                if (!onUpdate.isnil()) {
                    LuaValue ret = disease.globals.get("OnUpdate").call(_luaGame, disease.luaCharacter, disease.data);
                    if (!ret.isnil()) {
                        disease.message = ret.toString();
                    }
                }
            }
        });
//        Game.getInstance().notify(observer -> observer.onCloseQuest(quest));
    }

    public void apply(CharacterModel character, String name, LuaValue data) {
        if (character.getDisease(name) != null) {
            character.getDisease(name).data = data;
        } else {
            // Load lua scripts
            FileUtils.listRecursively("data/diseases/").stream().filter(file -> file.getName().equals(name + ".lua")).forEach(file -> {
                DiseaseModel disease = new DiseaseModel(name);
                disease.globals = JsePlatform.standardGlobals();
                disease.globals.get("dofile").call(LuaValue.valueOf(file.getAbsolutePath()));
                disease.luaCharacter = CoerceJavaToLua.coerce(new LuaCharacterModel(character));
                disease.data = data;

                LuaValue onStart = disease.globals.get("OnStart");
                if (!onStart.isnil()) {
                    onStart.call(_luaGame, disease.luaCharacter, data);
                }

                character.addDisease(disease);
            });
        }
    }
}
