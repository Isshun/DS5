package org.smallbox.faraway.client.ui.engine;

import org.smallbox.faraway.client.render.layer.GDXRenderer;
import org.smallbox.faraway.client.render.Viewport;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIFrame;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.engine.ColorUtils;
import org.smallbox.faraway.core.module.world.model.ParcelModel;

public abstract class UICursor {
    protected UIFrame RES_ODD;
    protected UIFrame RES_EDEN;

    public UICursor() {
        RES_ODD = new UIFrame(null);
        RES_ODD.setSize(32, 32);
        RES_ODD.setBackgroundColor(ColorUtils.fromHex(100, 255, 100, 20));
        RES_EDEN = new UIFrame(null);
        RES_EDEN.setSize(32, 32);
        RES_EDEN.setBackgroundColor(ColorUtils.fromHex(100, 255, 100, 40));
    }


    void    draw(GDXRenderer renderer, Viewport viewport, int startX, int startY, int toX, int toY, boolean isPressed) {
        startX = Math.max(startX, 0);
        startY = Math.max(startY, 0);
        toX = Math.min(toX, Application.gameManager.getGame().getInfo().worldWidth);
        toY = Math.min(toY, Application.gameManager.getGame().getInfo().worldHeight);

//        for (int x = startX; x <= toX; x++) {
//            for (int y = startY; y <= toY; y++) {
//                onDraw(layer, ModuleHelper.getWorldModule().getParcel(x, y, WorldHelper.getCurrentFloor()), x * 32 + viewport.getPosX(), y * 32 + viewport.getPosY(), (x + y) % 2 == 0, isPressed);
//            }
//        }
    }

    protected abstract void onDraw(GDXRenderer renderer, ParcelModel parcel, int x, int y, boolean odd, boolean isPressed);
}
