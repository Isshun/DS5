package org.smallbox.faraway.game.characterDisease;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.module.ModuleBase;
import org.smallbox.faraway.core.lua.data.DataExtendException;
import org.smallbox.faraway.core.lua.data.LuaExtend;
import org.smallbox.faraway.core.game.DataManager;

import java.io.File;

@ApplicationObject
public class LuaCharacterDiseaseExtend extends LuaExtend {

    @Override
    public boolean accept(String type) {
        return "disease".equals(type);
    }

    @Override
    public void extend(DataManager dataManager, ModuleBase module, Globals globals, LuaValue value, File dataDirectory) throws DataExtendException {
        DiseaseInfo disease = new DiseaseInfo();

        disease.label = value.get("label").toString();
        disease.name = value.get("id").toString();
        disease.setVisible(value.get("visible").isnil() || value.get("visible").toboolean());

        LuaValue onStart = value.get("on_start");
        LuaValue onUpdate = value.get("on_update");
        disease.setListener(new DiseaseInfo.DiseaseListener() {
            @Override
            public void onStart(CharacterDisease disease) {
                if (!onStart.isnil()) {
                    onStart.call(disease.luaData, disease.luaCharacter);
                }
            }

            @Override
            public void onUpdate(CharacterDisease disease, int update) {
                if (!onUpdate.isnil()) {
                    LuaValue ret = onUpdate.call(disease.luaData, disease.luaCharacter);
                    if (!ret.isnil()) {
                        disease.message = ret.get("message").toString();
                        disease.level = ret.get("level").toint();

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
                        disease.level = 0;
                    }
                }
            }
        });

        dataManager.diseases.add(disease);
    }
}
