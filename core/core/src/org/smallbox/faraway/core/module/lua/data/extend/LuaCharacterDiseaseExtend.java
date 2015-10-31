package org.smallbox.faraway.core.module.lua.data.extend;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.smallbox.faraway.core.game.model.GameData;
import org.smallbox.faraway.core.game.module.character.model.DiseaseCharacterModel;
import org.smallbox.faraway.core.game.module.character.model.DiseaseModel;
import org.smallbox.faraway.core.module.lua.DataExtendException;
import org.smallbox.faraway.core.module.lua.LuaModule;
import org.smallbox.faraway.core.module.lua.LuaModuleManager;
import org.smallbox.faraway.core.module.lua.data.LuaExtend;

/**
 * Created by Alex on 14/10/2015.
 */
public class LuaCharacterDiseaseExtend extends LuaExtend {
    @Override
    public boolean accept(String type) {
        return "disease".equals(type);
    }

    @Override
    public void extend(LuaModuleManager luaModuleManager, LuaModule module, Globals globals, LuaValue value) throws DataExtendException {
        DiseaseModel disease = new DiseaseModel();

        disease.label = value.get("label").toString();
        disease.name = value.get("name").toString();
        disease.setVisible(value.get("visible").isnil() || value.get("visible").toboolean());

        LuaValue onStart = value.get("on_start");
        LuaValue onUpdate = value.get("on_update");
        disease.setListener(new DiseaseModel.DiseaseListener() {
            @Override
            public void onStart(DiseaseCharacterModel data) {
                if (!onStart.isnil()) {
                    onStart.call(data.luaData, data.luaCharacter);
                }
            }

            @Override
            public void onUpdate(DiseaseCharacterModel data, int update) {
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
//                                    ((DiseaseModule) ModuleManager.getInstance().getModule(DiseaseModule.class)).apply(
//                                            model,
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

        GameData.getData().diseases.add(disease);
    }
}
