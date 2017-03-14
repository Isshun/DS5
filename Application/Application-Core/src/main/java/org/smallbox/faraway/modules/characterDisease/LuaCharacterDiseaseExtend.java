package org.smallbox.faraway.modules.characterDisease;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.engine.module.ModuleBase;
import org.smallbox.faraway.core.engine.module.lua.data.DataExtendException;
import org.smallbox.faraway.core.engine.module.lua.data.LuaExtend;

import java.io.File;

/**
 * Created by Alex on 14/10/2015.
 */
public class LuaCharacterDiseaseExtend extends LuaExtend {

    @Override
    public boolean accept(String type) {
        return "disease".equals(type);
    }

    @Override
    public void extend(ModuleBase module, Globals globals, LuaValue value, File dataDirectory) throws DataExtendException {
        DiseaseInfo disease = new DiseaseInfo();

        disease.label = value.get("label").toString();
        disease.name = value.get("name").toString();
        disease.setVisible(value.get("visible").isnil() || value.get("visible").toboolean());

        LuaValue onStart = value.get("on_start");
        LuaValue onUpdate = value.get("on_update");
        disease.setListener(new DiseaseInfo.DiseaseListener() {
            @Override
            public void onStart(CharacterDisease data) {
                if (!onStart.isnil()) {
                    onStart.call(data.luaData, data.luaCharacter);
                }
            }

            @Override
            public void onUpdate(CharacterDisease data, int update) {
                if (!onUpdate.isnil()) {
                    LuaValue ret = onUpdate.call(data.luaData, data.luaCharacter);
                    if (!ret.isnil()) {
                        data.message = ret.get("message").toString();
                        data.level = ret.get("level").toint();

//                        if (!ret.get("on_click").isnil()) {
//                            buff.onClickListener = view -> ret.get("on_click").call();
//                        }

//                        if (!ret.get("effect").isnil()) {
//                            LuaValue luaEffects = ret.get("effect");
//                            for (int j = 0; j < luaEffects.length(); j++) {
//                                if (Math.random() <= luaEffects.get(j + 1).get(2).todouble()) {
//                                    printNotice("apply buff effect: " + luaEffects.get(j + 1).get(1).toString() + " (" + buff.message + ")");
//                                    ((CharacterDiseaseModule) Application.moduleManager.getModule(CharacterDiseaseModule.class)).apply(
//                                            org.smallbox.faraway.core.module.room.model,
//                                            luaEffects.get(j + 1).get(1).toString(),
//                                            luaEffects.get(j + 1).get(3));
//                                }
//                            }
//                        }

                    } else {
                        data.level = 0;
                    }
                }
            }
        });

        Application.data.diseases.add(disease);
    }
}
