package org.smallbox.faraway.module.quest;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.smallbox.faraway.core.engine.module.ModuleBase;
import org.smallbox.faraway.core.engine.module.lua.data.DataExtendException;
import org.smallbox.faraway.core.engine.module.lua.data.LuaExtend;
import org.smallbox.faraway.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LuaQuestExtend extends LuaExtend {
    private List<QuestInfo> _questInfos = new ArrayList<>();

    @Override
    public boolean accept(String type) {
        return "quest".equals(type);
    }

    @Override
    public void extend(ModuleBase module, Globals globals, LuaValue value, File dataDirectory) throws DataExtendException {
        String id = getString(value, "id", null);

        Log.debug("Extend quest: " + id);

        QuestInfo questInfo = null;
        for (QuestInfo info: _questInfos) {
            if (id.equals(info.name)) {
                questInfo = info;
            }
        }

        if (questInfo == null) {
            questInfo = new QuestInfo();
            _questInfos.add(questInfo);
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
            questInfo.onQuestStartListener = (quest, optionIndex) -> quest.isOpen = onStartValue.call(CoerceJavaToLua.coerce(quest), LuaValue.valueOf(optionIndex)).toboolean();
        }

        if (!value.get("on_close").isnil()) {
            LuaValue onCloseValue = value.get("on_close");
            questInfo.onQuestCloseListener = quest -> onCloseValue.call(CoerceJavaToLua.coerce(quest)).toboolean();
        }
    }

    public List<QuestInfo> getQuestInfos() {
        return _questInfos;
    }
}