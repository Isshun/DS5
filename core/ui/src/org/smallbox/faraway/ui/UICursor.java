package org.smallbox.faraway.ui;

import org.smallbox.faraway.core.engine.renderer.Viewport;
import org.smallbox.faraway.core.engine.Color;
import org.smallbox.faraway.core.engine.renderer.GDXRenderer;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.core.engine.module.java.ModuleHelper;
import org.smallbox.faraway.ui.engine.views.widgets.UIFrame;

public abstract class UICursor {
    protected UIFrame RES_ODD;
    protected UIFrame RES_EDEN;

    public UICursor() {
        RES_ODD = new UIFrame(32, 32);
        RES_ODD.setBackgroundColor(new Color(100, 255, 100, 20));
        RES_EDEN = new UIFrame(32, 32);
        RES_EDEN.setBackgroundColor(new Color(100, 255, 100, 40));
    }


    void    draw(GDXRenderer renderer, Viewport viewport, int startX, int startY, int toX, int toY, boolean isPressed) {
        startX = Math.max(startX, 0);
        startY = Math.max(startY, 0);
        toX = Math.min(toX, Game.getInstance().getInfo().worldWidth);
        toY = Math.min(toY, Game.getInstance().getInfo().worldHeight);

        for (int x = startX; x <= toX; x++) {
            for (int y = startY; y <= toY; y++) {
                onDraw(renderer, ModuleHelper.getWorldModule().getParcel(x, y), x * 32 + viewport.getPosX(), y * 32 + viewport.getPosY(), (x + y) % 2 == 0, isPressed);
            }
        }
    }

    protected abstract void onDraw(GDXRenderer renderer, ParcelModel parcel, int x, int y, boolean odd, boolean isPressed);

}
