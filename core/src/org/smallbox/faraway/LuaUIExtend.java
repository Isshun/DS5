package org.smallbox.faraway;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.smallbox.faraway.lua.extend.LuaExtendInterface;
import org.smallbox.faraway.ui.UserInterface;
import org.smallbox.faraway.ui.engine.view.UIFrame;

/**
 * Created by Alex on 29/09/2015.
 */
public class LuaUIExtend implements LuaExtendInterface {
    @Override
    public boolean accept(String type) {
        switch (type) {
            case "view":
            case "list":
            case "grid":
            case "label":
                return true;
        }
        return false;
    }

    @Override
    public void extend(LuaModuleManager luaModuleManager, Globals globals, LuaValue value) {
        UIFrame frame = new UIFrame(-1, -1);
        frame.addView(LuaLayoutFactory.createView(luaModuleManager, globals, value));
        UserInterface.getInstance()._views.add(frame);
    }
}
