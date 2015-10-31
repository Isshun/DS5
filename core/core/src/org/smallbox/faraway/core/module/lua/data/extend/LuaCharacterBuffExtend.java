package org.smallbox.faraway.core.module.lua.data.extend;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.smallbox.faraway.core.game.model.GameData;
import org.smallbox.faraway.core.game.module.character.model.BuffCharacterModel;
import org.smallbox.faraway.core.game.module.character.model.BuffModel;
import org.smallbox.faraway.core.game.module.character.model.DiseaseCharacterModel;
import org.smallbox.faraway.core.game.module.character.model.DiseaseModel;
import org.smallbox.faraway.core.module.lua.DataExtendException;
import org.smallbox.faraway.core.module.lua.LuaModule;
import org.smallbox.faraway.core.module.lua.LuaModuleManager;
import org.smallbox.faraway.core.module.lua.data.LuaExtend;

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
        BuffModel buff = new BuffModel();

        buff.setVisible(value.get("visible").isnil() || value.get("visible").toboolean());

        LuaValue onStart = value.get("on_start");
        LuaValue onUpdate = value.get("on_update");
        buff.setListener(new BuffModel.BuffListener() {
            @Override
            public void onStart(BuffCharacterModel data) {
                if (!onStart.isnil()) {
                    onStart.call(data.luaData, data.luaCharacter);
                }
            }

            @Override
            public void onUpdate(BuffCharacterModel data, int update) {
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
                                double rate = 1;
                                if (!luaEffect.get("rate").isnil()) {
                                    rate = luaEffect.get("rate").todouble();
                                }
                                if (Math.random() <= rate) {
                                    if (!data.character.hasDisease(luaEffect.get("disease").toString())) {
                                        DiseaseModel disease = GameData.getData().getDisease(luaEffect.get("disease").toString());
                                        data.character.addDisease(new DiseaseCharacterModel(disease, data.luaCharacter, data.character));
                                    }
                                }
                            }
                        }

                        data.character.getNeeds().happinessChange += data.mood;
                    } else {
                        data.level = 0;
                    }
                }
            }
        });

        GameData.getData().buffs.add(buff);
    }
}
