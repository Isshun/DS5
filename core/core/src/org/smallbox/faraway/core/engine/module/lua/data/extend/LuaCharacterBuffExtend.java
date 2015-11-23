package org.smallbox.faraway.core.engine.module.lua.data.extend;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.engine.module.lua.data.DataExtendException;
import org.smallbox.faraway.core.engine.module.lua.LuaModule;
import org.smallbox.faraway.core.engine.module.lua.LuaModuleManager;
import org.smallbox.faraway.core.engine.module.lua.data.LuaExtend;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.Data;
import org.smallbox.faraway.core.game.module.character.model.BuffCharacterModel;
import org.smallbox.faraway.core.game.module.character.model.BuffInfo;
import org.smallbox.faraway.core.game.module.character.model.DiseaseCharacterModel;
import org.smallbox.faraway.core.game.module.character.model.DiseaseInfo;
import org.smallbox.faraway.core.util.Log;

/**
 * Created by Alex on 14/10/2015.
 */
public class LuaCharacterBuffExtend extends LuaExtend {
    @Override
    public boolean accept(String type) {
        return "buff".equals(type);
    }

    @Override
    public void extend(LuaModuleManager luaModuleManager, LuaModule module, Globals globals, LuaValue value) throws DataExtendException {
        String name = getString(value, "name", null);
        if (name == null) {
            return;
        }

        for (BuffInfo buff: Data.getData().buffs) {
            if (name.equals(buff.getName())) {
                return;
            }
        }

        BuffInfo buff = new BuffInfo();

        buff.setName(name);
        buff.setVisible(value.get("visible").isnil() || value.get("visible").toboolean());
        buff.setDuration(value.get("duration").isnil() ? value.get("duration").toint() : 0);

        LuaValue onStart = value.get("on_start");
        LuaValue onCheck = value.get("on_check");
        LuaValue onUpdate = value.get("on_update");
        LuaValue onUpdateHourly = value.get("on_update_hourly");
        buff.setListener(new BuffInfo.BuffListener() {
            @Override
            public void onStart(BuffCharacterModel data) {
                data.luaData.set("duration", new LuaTable());
                data.luaData.get("duration").set("tick", 0);
                data.luaData.get("duration").set("hour", 0);
                data.luaData.get("duration").set("day", 0);
                if (!onStart.isnil()) {
                    onStart.call(data.luaData, data.luaCharacter);
                }
            }

            @Override
            public void onCheck(BuffCharacterModel data, int tick) {
                if (!onCheck.isnil()) {
                    LuaValue ret = onCheck.call(data.luaData, data.luaCharacter);

                    // Buff has not expire
                    if (data.active && data.buff.getDuration() > 0 && data.buff.getDuration() < (tick - data.startTick)) {
                        return;
                    }

                    // Check if buff is active
                    data.active = !ret.isnil() && ret.toboolean();
                    data.startTick = !data.active ? 0 : data.startTick == 0 ? tick : data.startTick;
                }
            }

            @Override
            public void onUpdate(BuffCharacterModel data, int tick) {
                if (data.active) {

                    // Update duration
                    long duration = tick - data.startTick;
                    long durationHour = duration / Application.getInstance().getConfig().game.tickPerHour;
                    long durationDay = durationHour / Game.getInstance().getPlanet().getInfo().dayDuration;
                    data.luaData.get("duration").set("tick", duration);
                    data.luaData.get("duration").set("hour", durationHour);
                    data.luaData.get("duration").set("day", durationDay);

                    // Call lua methode
                    if (!onUpdate.isnil()) {
                        LuaValue ret = onUpdate.call(data.luaData, data.luaCharacter);
                        if (!ret.isnil()) {
                            data.message = ret.get("message").toString();
                            data.level = ret.get("level").toint();
                            data.mood = ret.get("mood").toint();

                            if (!ret.get("effects").isnil()) {
                                LuaValue luaEffects = ret.get("effects");
                                for (int j = 1; j <= luaEffects.length(); j++) {
                                    LuaValue luaEffect = luaEffects.get(j);

                                    // Get rate
                                    double rate = 1;
                                    if (!luaEffect.get("rate").isnil()) {
                                        rate = luaEffect.get("rate").todouble();
                                    }

                                    // Get data
                                    LuaValue effectData = null;
                                    if (!luaEffect.get("data").isnil()) {
                                        effectData = luaEffect.get("data");
                                    }

                                    // Apply effect function of rate
                                    if (Math.random() <= rate) {

                                        // Apply disease
                                        if ("disease".equals(luaEffect.get("type").toString())) {
                                            String diseaseName = luaEffect.get("disease").toString();
                                            DiseaseInfo disease = Data.getData().getDisease(diseaseName);
                                            if (disease != null) {
                                                // Add new disease to character
                                                if (!data.character.hasDisease(diseaseName)) {
                                                    data.character.addDisease(new DiseaseCharacterModel(disease, data.luaCharacter, data.character, effectData));
                                                }
                                                // Update existing disease with new data
                                                else {
                                                    data.character.getDisease(diseaseName).luaData = effectData;
                                                }
                                            } else {
                                                Log.error("Unable to find disease: " + luaEffect.get("disease").toString());
                                            }
                                        }

                                        // Apply faint
                                        if ("faint".equals(luaEffect.get("type").toString()) && data.character.isAlive()) {
                                            data.character.getStats().isFaint = true;
                                        }

                                        // Apply death
                                        if ("death".equals(luaEffect.get("type").toString()) && data.character.isAlive()) {
//                                            data.character.getStats().isAlive = false;
                                        }
                                    }
                                }
                            }

                            data.character.getNeeds().happinessChange += data.mood;
                        }
                    }
                } else {
                    data.level = 0;
                }
            }

            @Override
            public void onUpdateHourly(BuffCharacterModel data, int update) {
                if (!onUpdateHourly.isnil() && data.active) {
                    onUpdateHourly.call(data.luaData, data.luaCharacter);
                }
            }
        });

        Data.getData().buffs.add(buff);
    }
}
