package org.smallbox.faraway.ui.cursor;

import org.smallbox.faraway.engine.Color;
import org.smallbox.faraway.engine.renderer.GDXRenderer;
import org.smallbox.faraway.game.model.item.ParcelModel;
import org.smallbox.faraway.ui.UICursor;
import org.smallbox.faraway.ui.engine.views.UIFrame;

/**
 * Created by Alex on 27/06/2015.
 */
public class DebugCursor extends UICursor {
    private final UIFrame RES_ITEM;

    public DebugCursor() {
        super();

        RES_ITEM = new UIFrame(32, 32);
        RES_ITEM.setBackgroundColor(new Color(200, 255, 100, 120));
    }

    @Override
    protected void onDraw(GDXRenderer renderer, ParcelModel parcel, int x, int y, boolean odd, boolean isPressed) {
        renderer.draw(odd ? RES_ODD : RES_EDEN, x, y);
        if (parcel != null && parcel.getResource() != null && parcel.getResource().isRock()) {
            RES_ITEM.draw(renderer, x, y);
        }
    }
}
