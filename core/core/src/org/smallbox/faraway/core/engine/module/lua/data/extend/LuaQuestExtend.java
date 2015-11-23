package org.smallbox.faraway.core.engine.module.lua.data.extend;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.smallbox.faraway.core.engine.module.lua.data.DataExtendException;
import org.smallbox.faraway.core.engine.module.lua.LuaModule;
import org.smallbox.faraway.core.engine.module.lua.LuaModuleManager;
import org.smallbox.faraway.core.engine.module.lua.data.LuaExtend;
import org.smallbox.faraway.core.game.Data;
import org.smallbox.faraway.core.game.modelInfo.QuestInfo;

/**
 * Created by Alex on 22/11/2015.
 */
public class LuaQuestExtend extends LuaExtend {
    @Override
    public boolean accept(String type) {
        return "quest".equals(type);
    }

    @Override
    public void extend(LuaModuleManager luaModuleManager, LuaModule module, Globals globals, LuaValue value) throws DataExtendException {
        String name = getString(value, "name", null);

        QuestInfo questInfo = null;
        for (QuestInfo info: Data.getData().quests) {
            if (name.equals(info.name)) {
                questInfo = info;
            }
        }

        if (questInfo == null) {
            questInfo = new QuestInfo();
            Data.getData().quests.add(questInfo);
        }

        questInfo.label = getString(value, "label", null);
        questInfo.openMessage = getString(value, "open_message", null);

        if (!value.get("open_options").isnil()) {
            questInfo.openOptions = new String[value.get("open_options").length()];
            for (int i = 1; i <= value.get("open_options").length(); i++) {
                questInfo.openOptions[i-1] = value.get("open_options").get(i).toString();
            }
        }

        if (!value.get("on_check").isnil()) {
            LuaValue onCheckValue = value.get("on_check");
            questInfo.onQuestCheckListener = quest -> onCheckValue.call(CoerceJavaToLua.coerce(quest)).toboolean();
        }

        if (!value.get("on_start").isnil()) {
            LuaValue onStartValue = value.get("on_start");
            questInfo.onQuestStartListener = quest -> onStartValue.call(CoerceJavaToLua.coerce(quest));
        }

        if (!value.get("on_close").isnil()) {
            LuaValue onCloseValue = value.get("on_close");
            questInfo.onQuestCloseListener = quest -> onCloseValue.call(CoerceJavaToLua.coerce(quest)).toboolean();
        }
    }
}