package org.smallbox.faraway;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.smallbox.faraway.engine.renderer.GDXRenderer;
import org.smallbox.faraway.game.model.GameData;
import org.smallbox.faraway.game.model.item.ParcelModel;
import org.smallbox.faraway.lua.extend.LuaExtendInterface;
import org.smallbox.faraway.ui.UICursor;
import org.smallbox.faraway.ui.engine.ViewFactory;
import org.smallbox.faraway.ui.engine.view.ColorView;

/**
 * Created by Alex on 29/09/2015.
 */
public class LuaCursorExtend implements LuaExtendInterface {
    @Override
    public boolean accept(String type) {
        return "cursor".equals(type);
    }

    @Override
    public void extend(LuaModuleManager luaModuleManager, Globals globals, LuaValue value) {
        final ColorView resItem = ViewFactory.getInstance().createColorView(32, 32);
        resItem.setBackgroundColor(value.get("default").get("color").tolong());

        final ColorView resOdd = ViewFactory.getInstance().createColorView(32, 32);
        resOdd.setBackgroundColor(value.get("odd").get("color").tolong());

        final ColorView resEden = ViewFactory.getInstance().createColorView(32, 32);
        resEden.setBackgroundColor(value.get("eden").get("color").tolong());

        GameData.getData().cursors.put(value.get("id").toString(), new UICursor() {
            @Override
            protected void onDraw(GDXRenderer renderer, ParcelModel parcel, int x, int y, boolean odd, boolean isPressed) {
                if (isPressed) {
                    renderer.draw(odd ? resOdd : resEden, x, y);
                } else {
                    renderer.draw(resItem, x, y);
                }
            }
        });
    }
}
