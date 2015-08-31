package org.smallbox.faraway.ui.cursor;

import org.smallbox.faraway.engine.Color;
import org.smallbox.faraway.engine.renderer.GDXRenderer;
import org.smallbox.faraway.game.model.item.ParcelModel;
import org.smallbox.faraway.ui.UICursor;
import org.smallbox.faraway.ui.engine.ViewFactory;
import org.smallbox.faraway.ui.engine.view.ColorView;

/**
 * Created by Alex on 27/06/2015.
 */
public class GatherCursor extends UICursor {
    private final ColorView RES_ITEM;

    public GatherCursor() {
        super();

        RES_ITEM = ViewFactory.getInstance().createColorView(32, 32);
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
