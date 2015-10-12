package org.smallbox.faraway.module.character;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;
import org.smallbox.faraway.engine.lua.LuaCharacterModel;
import org.smallbox.faraway.engine.lua.LuaGameModel;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.model.character.BuffModel;
import org.smallbox.faraway.game.model.character.base.CharacterModel;
import org.smallbox.faraway.game.module.GameModule;
import org.smallbox.faraway.game.module.ModuleHelper;
import org.smallbox.faraway.game.module.ModuleManager;
import org.smallbox.faraway.module.lua.luaModel.LuaEventsModel;
import org.smallbox.faraway.ui.UserInterface;
import org.smallbox.faraway.util.FileUtils;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Collections;

/**
 * Created by Alex on 16/06/2015.
 */
public class BuffModule extends GameModule {
    private LuaValue  _luaGame;

    @Override
    protected void onLoaded() {
        _luaGame = CoerceJavaToLua.coerce(new LuaGameModel(Game.getInstance()));
        _updateInterval = 10;
    }

    @Override
    protected void onUpdate(int tick) {
        for (CharacterModel character: ModuleHelper.getCharacterModule().getCharacters()) {
            if (character.isAlive()) {
                character.getNeeds().happinessChange = 0;
                int length = character.getBuffs().size();
                for (int i = 0; i < length; i++) {
                    BuffModel buff = character.getBuffs().get(i);
                    LuaValue ret = buff.globals.get("OnUpdate").call(_luaGame, buff.luaCharacter);
                    if (!ret.isnil()) {
                        buff.message = ret.get("message").toString();
                        buff.level = ret.get("level").toint();
                        buff.mood = ret.get("mood").toint();

                        if (!ret.get("on_click").isnil()) {
                            buff.onClickListener = view -> ret.get("on_click").call();
                        }

                        if (!ret.get("effect").isnil()) {
                            LuaValue luaEffects = ret.get("effect");
                            for (int j = 0; j < luaEffects.length(); j++) {
                                if (Math.random() <= luaEffects.get(j + 1).get(2).todouble()) {
                                    printNotice("apply buff effect: " + luaEffects.get(j + 1).get(1).toString() + " (" + buff.message + ")");
                                    ((DiseaseModule) ModuleManager.getInstance().getModule(DiseaseModule.class)).apply(
                                            character,
                                            luaEffects.get(j + 1).get(1).toString(),
                                            luaEffects.get(j + 1).get(3));
                                }
                            }
                        }

                        character.getNeeds().happinessChange += buff.mood;
                    } else {
                        buff.level = 0;
                    }
                }
                Collections.sort(character.getBuffs(), (b1, b2) -> b2.mood - b1.mood);
            }
        }
//        Game.getInstance().notify(observer -> observer.onCloseQuest(quest));
    }

    @Override
    public void onAddCharacter(CharacterModel character) {
        LuaValue luaCharacter = CoerceJavaToLua.coerce(new LuaCharacterModel(character));

        // Load lua scripts
        FileUtils.listRecursively("data/buffs/").stream().filter(file -> file.getName().endsWith(".lua")).forEach(file -> {
            try {
                BuffModel buff = new BuffModel();
                buff.globals = JsePlatform.standardGlobals();
                buff.globals.load("function main(g)\n game = g\n end", "main").call();
                buff.globals.get("main").call(CoerceJavaToLua.coerce(new org.smallbox.faraway.module.lua.luaModel.LuaGameModel(null, new LuaEventsModel(), UserInterface.getInstance(), null)));
                buff.globals.load(new FileReader(file), file.getName()).call();
//            buff.globals.get("dofile").call(LuaValue.valueOf(file.getAbsolutePath()));
                buff.luaCharacter = luaCharacter;

                LuaValue onStart = buff.globals.get("OnStart");
                if (!onStart.isnil()) {
                    LuaValue ret = onStart.call(_luaGame, buff.luaCharacter);
                    if (!ret.isnil() && !ret.toboolean()) {
                        return;
                    }
                }

                character.addBuff(buff);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        });
    }
}
