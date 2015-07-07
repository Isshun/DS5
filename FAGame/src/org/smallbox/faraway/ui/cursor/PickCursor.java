package org.smallbox.faraway.ui.cursor;

import org.smallbox.faraway.engine.Color;
import org.smallbox.faraway.engine.GFXRenderer;
import org.smallbox.faraway.game.model.item.ParcelModel;
import org.smallbox.faraway.ui.UserInterfaceCursor;
import org.smallbox.faraway.ui.engine.ColorView;
import org.smallbox.faraway.ui.engine.ViewFactory;

/**
 * Created by Alex on 27/06/2015.
 */
public class PickCursor extends UserInterfaceCursor {
    private final ColorView RES_ITEM;

    public PickCursor() {
        super();

        RES_ITEM = ViewFactory.getInstance().createColorView(32, 32);
        RES_ITEM.setBackgroundColor(new Color(255, 220, 40, 160));
    }

    @Override
    protected void onDraw(GFXRenderer renderer, ParcelModel parcel, int x, int y, boolean odd, boolean isPressed) {
        if (isPressed) {
            renderer.draw(odd ? RES_ODD : RES_EDEN, x, y);
            if (parcel != null && parcel.getConsumable() != null) {
                RES_ITEM.draw(renderer, x, y);
            }
        } else {
            renderer.draw(RES_ITEM, x, y);
        }
    }
}
