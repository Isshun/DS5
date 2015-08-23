package org.smallbox.faraway.ui.panel;

import org.smallbox.faraway.engine.Color;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.helper.WorldHelper;
import org.smallbox.faraway.game.model.item.ConsumableModel;
import org.smallbox.faraway.game.model.item.ItemInfo;
import org.smallbox.faraway.ui.UserInterface;
import org.smallbox.faraway.ui.engine.ViewFactory;
import org.smallbox.faraway.ui.engine.view.UILabel;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Alex on 03/06/2015.
 */
public class PanelResources extends BasePanel {
    private static final int FRAME_WIDTH = 180;
    private static final int FRAME_HEIGHT = 200;

    public PanelResources() {
        super(UserInterface.Mode.NONE, null, 0, 64, FRAME_WIDTH, FRAME_HEIGHT, null);
        setBackgroundColor(new Color(55, 55, 55));
        setAlwaysVisible(true);
    }

    @Override
    public void onCreate(ViewFactory viewFactory) {
    }

    @Override
    public void onRefresh(int refresh) {
        if (refresh % 10 == 0) {
            Map<ItemInfo, Integer> items = new HashMap<>();
            for (int x = 0; x < Game.getWorldManager().getWidth(); x++) {
                for (int y = 0; y < Game.getWorldManager().getHeight(); y++) {
                    ConsumableModel consumable = WorldHelper.getConsumable(x, y);
                    if (consumable != null) {
                        if (!items.containsKey(consumable.getInfo())) {
                            items.put(consumable.getInfo(), 0);
                        }
                        items.put(consumable.getInfo(), items.get(consumable.getInfo()) + consumable.getQuantity());
                    }
                }
            }

            removeAllViews();
            int index = 0;
            for (ItemInfo itemInfo: items.keySet()) {
                UILabel lbItem = ViewFactory.getInstance().createTextView();
                lbItem.setCharacterSize(14);
                lbItem.setString(itemInfo.label + " (" + items.get(itemInfo) + ")");
                lbItem.setPosition(10, 10 + 20 * index++);
                addView(lbItem);
            }
        }
    }
}
