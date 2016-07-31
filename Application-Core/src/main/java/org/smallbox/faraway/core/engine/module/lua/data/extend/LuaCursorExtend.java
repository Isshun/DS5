package org.smallbox.faraway.core.engine.module.lua.data.extend;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.smallbox.faraway.core.engine.module.ModuleBase;
import org.smallbox.faraway.core.engine.module.lua.data.LuaExtend;
import org.smallbox.faraway.core.engine.renderer.GDXRenderer;
import org.smallbox.faraway.core.game.Data;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.ui.UICursor;
import org.smallbox.faraway.ui.engine.views.widgets.UIFrame;

import java.io.File;

/**
 * Created by Alex on 29/09/2015.
 */
public class LuaCursorExtend extends LuaExtend {
    @Override
    public boolean accept(String type) {
        return "cursor".equals(type);
    }

    @Override
    public void extend(ModuleBase module, Globals globals, LuaValue value, File dataDirectory) {
        final UIFrame resItem = new UIFrame(module);
        resItem.setSize(32, 32);
        resItem.setBackgroundColor(value.get("default").get("color").tolong());

        final UIFrame resOdd = new UIFrame(module);
        resOdd.setSize(32, 32);
        resOdd.setBackgroundColor(value.get("odd").get("color").tolong());

        final UIFrame resEden = new UIFrame(module);
        resEden.setSize(32, 32);
        resEden.setBackgroundColor(value.get("eden").get("color").tolong());

        final LuaValue luaOnParcel = value.get("on_parcel");

        Data.getData().cursors.put(value.get("id").toString(), new UICursor() {
            @Override
            protected void onDraw(GDXRenderer renderer, ParcelModel parcel, int x, int y, boolean odd, boolean isPressed) {
                if (isPressed) {
                    renderer.draw(odd ? resOdd : resEden, x, y);

                    if (parcel != null && !luaOnParcel.isnil()) {
                        LuaValue ret = luaOnParcel.call(CoerceJavaToLua.coerce(parcel));
                        if (!ret.isnil() && ret.toboolean()) {
                            renderer.draw(resItem, x, y);
                        }
                    }
                } else {
                    renderer.draw(resItem, x, y);
                }
            }
        });
    }
}