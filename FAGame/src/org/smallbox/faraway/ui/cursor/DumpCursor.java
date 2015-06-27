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
public class DumpCursor extends UserInterfaceCursor {
    private final ColorView RES_ITEM;

    public DumpCursor() {
        super();

        RES_ITEM = ViewFactory.getInstance().createColorView(32, 32);
        RES_ITEM.setBackgroundColor(new Color(150, 150, 150, 140));
    }

    @Override
    protected void onDraw(GFXRenderer renderer, ParcelModel parcel, int x, int y, boolean odd) {
        renderer.draw(odd ? RES_ODD : RES_EDEN, x, y);
        if (parcel != null && parcel.getItem() != null || parcel.getStructure() != null) {
            RES_ITEM.draw(renderer, x, y);
        }
    }
}
