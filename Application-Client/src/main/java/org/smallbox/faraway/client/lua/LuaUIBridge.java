package org.smallbox.faraway.client.lua;

import org.smallbox.faraway.client.ApplicationClient;
import org.smallbox.faraway.client.ui.engine.views.widgets.*;
import org.smallbox.faraway.core.engine.module.lua.LuaModule;

/**
 * Created by Alex on 12/04/2016.
 */
public class LuaUIBridge {
    private final LuaModule _module;

    public View find(String id) { return ApplicationClient.uiManager.findById(id); }
    public boolean  isVisible(String id) { return ApplicationClient.uiManager.isVisible(id); }

    public LuaUIBridge(LuaModule module) {
        _module = module;
    }

    // Factories
    public View     createView() { return new UIFrame(_module); }
    public UILabel  createLabel() { return new UILabel(_module); }
    public UIImage createImage() { return new UIImage(_module); }
    public UIGrid createGrid() { return new UIGrid(_module); }
    public UIList createList() { return new UIList(_module); }
}
