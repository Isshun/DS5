package org.smallbox.faraway.core.ui.cursor;

import org.smallbox.faraway.core.engine.Color;
import org.smallbox.faraway.core.engine.renderer.GDXRenderer;
import org.smallbox.faraway.core.game.model.item.ParcelModel;
import org.smallbox.faraway.core.ui.UICursor;
import org.smallbox.faraway.core.ui.engine.views.UIFrame;

/**
 * Created by Alex on 27/06/2015.
 */
public class GatherCursor extends UICursor {
    private final UIFrame RES_ITEM;

    public GatherCursor() {
        super();

        RES_ITEM = new UIFrame(32, 32);
        RES_ITEM.setBackgroundColor(new Color(150, 255, 80, 140));
    }

    @Override
    protected void onDraw(GDXRenderer renderer, ParcelModel parcel, int x, int y, boolean odd, boolean isPressed) {
        if (isPressed) {
            renderer.draw(odd ? RES_ODD : RES_EDEN, x, y);
            if (parcel != null && parcel.getResource() != null && parcel.getResource().canBeHarvested()) {
                RES_ITEM.draw(renderer, x, y);
            }
        } else {
            renderer.draw(RES_ITEM, x, y);
        }
    }
}
