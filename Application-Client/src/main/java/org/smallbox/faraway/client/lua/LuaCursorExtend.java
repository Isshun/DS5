package org.smallbox.faraway.client.lua;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIFrame;
import org.smallbox.faraway.core.engine.module.ModuleBase;
import org.smallbox.faraway.core.engine.module.lua.data.LuaExtend;
import org.smallbox.faraway.core.game.Data;

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
    public void extend(Data data, ModuleBase module, Globals globals, LuaValue value, File dataDirectory) {
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

// TODO
        //        Application.data.cursors.put(value.get("id").toString(), new UICursor() {
//            @Override
//            protected void onDraw(GDXLayer layer, ParcelModel parcel, int x, int y, boolean odd, boolean isPressed) {
//                if (isPressed) {
//                    renderer.drawPixel(odd ? resOdd : resEden, x, y);
//
//                    if (parcel != null && !luaOnParcel.isnil()) {
//                        LuaValue ret = luaOnParcel.call(CoerceJavaToLua.coerce(parcel));
//                        if (!ret.isnil() && ret.toboolean()) {
//                            renderer.drawPixel(resItem, x, y);
//                        }
//                    }
//                } else {
//                    renderer.drawPixel(resItem, x, y);
//                }
//            }
//        });
    }
}