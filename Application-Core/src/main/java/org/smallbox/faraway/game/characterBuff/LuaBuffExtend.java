package org.smallbox.faraway.game.characterBuff;

import org.luaj.vm2.*;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.smallbox.faraway.core.dependencyInjector.DependencyManager;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.game.DataManager;
import org.smallbox.faraway.core.lua.data.DataExtendException;
import org.smallbox.faraway.core.lua.data.LuaExtend;
import org.smallbox.faraway.core.module.ModuleBase;
import org.smallbox.faraway.game.character.model.base.CharacterModel;
import org.smallbox.faraway.game.characterBuff.handler.BuffHandler;
import org.smallbox.faraway.game.characterDisease.DiseaseInfo;
import org.smallbox.faraway.util.log.Log;

import java.io.File;

@ApplicationObject
public class LuaBuffExtend extends LuaExtend {

    @Override
    public boolean accept(String type) {
        return "buff".equals(type);
    }

    @Override
    public void extend(DataManager dataManager, ModuleBase module, Globals globals, LuaValue value, File dataDirectory) throws DataExtendException {
        String id = getString(value, "id", null);
        if (id == null) {
            return;
        }

        for (BuffInfo buff: dataManager.buffs) {
            if (id.equals(buff.getName())) {
                return;
            }
        }

        BuffInfo buff = new BuffInfo();

        buff.setName(id);
        buff.setVisible(value.get("visible").isnil() || value.get("visible").toboolean());
        buff.setDuration(value.get("duration").isnil() ? value.get("duration").toint() : 0);

        // TODO: tmp
        if (value.get("levels").isnil()) {
            return;
        }

        if (!value.get("class").isnil()) {
            try {
                buff.handler = (BuffHandler) Class.forName(value.get("class").tojstring()).newInstance();
                DependencyManager.getInstance().register(buff.handler);
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                Log.error(e);
            }
        }

        if (!value.get("on_get_level").isnil()) {
            buff.onGetLevel = new BuffInfo.OnGetLevel() {

                private Varargs varargs;

                @Override
                public int getLevel(CharacterModel character) {

                    if (varargs == null) {
                        LocVars[] locVars = ((LuaClosure)value.get("on_get_level")).p.locvars;
                        LuaValue[] args = new LuaValue[locVars.length];
                        for (int i = 0; i < locVars.length; i++) {
                            String argName = locVars[i].varname.tojstring().toLowerCase();
                            Log.info("Inject: " + argName);

                            // Inject le personnage ayant le buff
                            if ("character".equals(argName)) {
                                args[i] = CoerceJavaToLua.coerce(character);
                            }

                            // Récupère un objet depuis l'injecteur de dependance
                            else {
                                // TODO
//                                Object object = Application.dependencyInjector.getObjects().stream()
//                                        .filter(obj -> argName.equals(obj.getClass().getSimpleName().toLowerCase()))
//                                        .findAny().orElse(null);
//
//                                Object gameObject = Application.dependencyInjector.getGameObjects().stream()
//                                        .filter(obj -> argName.equals(obj.getClass().getSimpleName().toLowerCase()))
//                                        .findAny().orElse(null);
//
//                                if (object != null) {
//                                    args[i] = CoerceJavaToLua.coerce(object);
//                                }
//
//                                else if (gameObject != null) {
//                                    args[i] = CoerceJavaToLua.coerce(gameObject);
//                                }
//
//                                else {
//                                    throw new GameException(LuaBuffExtend.class, "on_get_level DI error: " + argName);
//                                }
                            }
                        }
                        varargs = LuaValue.varargsOf(args);
                    }

                    Varargs ret = value.get("on_get_level").invoke(varargs);

                    return ret.toint(1);
                }
            };
        }

        if (!value.get("levels").isnil()) {
            LuaValue luaLevels = value.get("levels");
            if (!luaLevels.isnil()) {
                for (int i = 1; i <= luaLevels.length(); i++) {
                    LuaValue luaLevel = luaLevels.get(i);
                    BuffInfo.BuffLevelInfo levelInfo = new BuffInfo.BuffLevelInfo();
                    levelInfo.level = i;
                    levelInfo.message = luaLevel.get("message").tojstring();
                    levelInfo.mood = luaLevel.get("mood").optint(0);

                    LuaValue luaEffects = luaLevel.get("effects");
                    if (!luaEffects.isnil()) {
                        for (int j = 1; j <= luaEffects.length(); j++) {
                            levelInfo.effects.add(readBuffEffect(dataManager, luaEffects.get(j)));
                        }
                    }

                    buff.levels.add(levelInfo);
                }
            }
        }

        dataManager.buffs.add(buff);
    }

    private BuffInfo.BuffEffectInfo readBuffEffect(DataManager dataManager, LuaValue luaEffect) {
        BuffInfo.BuffEffectInfo effectInfo = new BuffInfo.BuffEffectInfo();

        // Add type
        switch (luaEffect.get("type").tojstring()) {

            case "need":
                effectInfo.needs.put(luaEffect.get("id").tojstring(), luaEffect.get("value").todouble());
                break;

            case "disease":
                dataManager.getAsync(luaEffect.get("disease").tojstring(), DiseaseInfo.class, disease -> effectInfo.disease = disease);
                break;

        }

        // Add rate
        effectInfo.rate = luaEffect.get("rate").optdouble(1);

        return effectInfo;
    }
}
