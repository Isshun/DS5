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
public class BuildCursor extends UserInterfaceCursor {
    private final ColorView RES_ITEM;

    public BuildCursor() {
        super();

        RES_ITEM = ViewFactory.getInstance().createColorView(32, 32);
        RES_ITEM.setBackgroundColor(new Color(200, 255, 100, 120));
    }

    @Override
    protected void onDraw(GFXRenderer renderer, ParcelModel parcel, int x, int y, boolean odd) {
        renderer.draw(odd ? RES_ODD : RES_EDEN, x, y);
    }
}
