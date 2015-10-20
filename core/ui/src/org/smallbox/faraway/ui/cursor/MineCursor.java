package org.smallbox.faraway.ui.cursor;

import org.smallbox.faraway.core.engine.Color;
import org.smallbox.faraway.core.engine.renderer.GDXRenderer;
import org.smallbox.faraway.core.game.model.item.ItemInfo;
import org.smallbox.faraway.core.game.model.item.ParcelModel;
import org.smallbox.faraway.ui.UICursor;
import org.smallbox.faraway.ui.engine.views.UIFrame;

/**
 * Created by Alex on 27/06/2015.
 */
public class MineCursor extends UICursor {
    private final UIFrame RES_ITEM;

    public MineCursor() {
        super();

        RES_ITEM = new UIFrame(32, 32);
        RES_ITEM.setBackgroundColor(new Color(255, 200, 180, 140));
    }

    @Override
    protected void onDraw(GDXRenderer renderer, ParcelModel parcel, int x, int y, boolean odd, boolean isPressed) {
        if (isPressed) {
            renderer.draw(odd ? RES_ODD : RES_EDEN, x, y);
            if (parcel != null && parcel.getResource() != null && parcel.getResource().getInfo().actions != null) {
                for (ItemInfo.ItemInfoAction action: parcel.getResource().getInfo().actions) {
                    if ("mine".equals(action.type)) {
                        RES_ITEM.draw(renderer, x, y);
                        break;
                    }
                }
            }
        } else {
            renderer.draw(RES_ITEM, x, y);
        }
    }
}
