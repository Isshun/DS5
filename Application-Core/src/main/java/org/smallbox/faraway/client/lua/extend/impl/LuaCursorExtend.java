package org.smallbox.faraway.client.lua.extend.impl;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.smallbox.faraway.client.ui.widgets.UIFrame;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.game.DataManager;
import org.smallbox.faraway.core.lua.data.LuaExtend;
import org.smallbox.faraway.core.module.ModuleBase;

import java.io.File;

@ApplicationObject
public class LuaCursorExtend extends LuaExtend {

    @Override
    public boolean accept(String type) {
        return "cursor".equals(type);
    }

    @Override
    public void extend(DataManager dataManager, ModuleBase module, Globals globals, LuaValue value, File dataDirectory) {
        final UIFrame resItem = new UIFrame(module);
        resItem.setSize(32, 32);
        resItem.getStyle().setBackgroundColor(value.get("default").get("color").toint());

        final UIFrame resOdd = new UIFrame(module);
        resOdd.setSize(32, 32);
        resOdd.getStyle().setBackgroundColor(value.get("odd").get("color").toint());

        final UIFrame resEden = new UIFrame(module);
        resEden.setSize(32, 32);
        resEden.getStyle().setBackgroundColor(value.get("eden").get("color").toint());

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