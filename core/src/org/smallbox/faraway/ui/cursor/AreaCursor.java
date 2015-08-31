package org.smallbox.faraway.ui.cursor;

import org.smallbox.faraway.engine.Color;
import org.smallbox.faraway.engine.renderer.GDXRenderer;
import org.smallbox.faraway.game.model.item.ParcelModel;
import org.smallbox.faraway.ui.UICursor;
import org.smallbox.faraway.ui.engine.ViewFactory;
import org.smallbox.faraway.ui.engine.view.ColorView;

/**
 * Created by Alex on 31/08/2015.
 */
public class AreaCursor extends UICursor {
    private final ColorView RES_ITEM;

    public AreaCursor() {
        super();

        RES_ITEM = ViewFactory.getInstance().createColorView(32, 32);
        RES_ITEM.setBackgroundColor(new Color(200, 255, 100, 120));
    }

    @Override
    protected void onDraw(GDXRenderer renderer, ParcelModel parcel, int x, int y, boolean odd, boolean isPressed) {
        if (isPressed) {
            renderer.draw(odd ? RES_ODD : RES_EDEN, x, y);
        } else {
            renderer.draw(RES_ITEM, x, y);
        }
    }
}
