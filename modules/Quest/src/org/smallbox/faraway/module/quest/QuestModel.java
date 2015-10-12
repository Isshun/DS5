package org.smallbox.faraway.module.quest;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;

/**
 * Created by Alex on 31/08/2015.
 */
public class QuestModel {
    public final LuaValue globals;
    public final String     fileName;
    public boolean          isOpen;
    public String           message;
    public String[]         options;
    public int              optionIndex;

    public QuestModel(String fileName) {
        this.globals = JsePlatform.standardGlobals();
        this.fileName = fileName;
        this.isOpen = true;
    }
}
