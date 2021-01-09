package org.smallbox.faraway.client.lua.extend.impl;

import org.luaj.vm2.LuaValue;
import org.smallbox.faraway.client.lua.extend.LuaUICompositeExtend;
import org.smallbox.faraway.client.ui.engine.views.View;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIGrid;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.engine.module.ModuleBase;

@ApplicationObject
public class LuaUIGridExtend extends LuaUICompositeExtend {

    @Override
    public boolean accept(String type) {
        return "grid".equals(type);
    }

    @Override
    protected void readSpecific(LuaValue value, View view) {
    }

    @Override
    protected View createViewFromType(ModuleBase module, LuaValue value) {
        UIGrid grid = new UIGrid(module);

        readInt(value, "columns", grid::setColumns);
        readInt(value, "row_height", grid::setRowHeight);
        readInt(value, "column_width", grid::setColumnWidth);

        return grid;
    }

}