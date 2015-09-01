package org.smallbox.faraway.game.module.panels;

import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.helper.WorldHelper;
import org.smallbox.faraway.game.model.item.ConsumableModel;
import org.smallbox.faraway.game.model.item.ItemInfo;
import org.smallbox.faraway.game.module.GameUIModule;
import org.smallbox.faraway.game.module.ModuleManager;
import org.smallbox.faraway.game.module.UIWindow;
import org.smallbox.faraway.ui.engine.ViewFactory;
import org.smallbox.faraway.ui.engine.view.FrameLayout;
import org.smallbox.faraway.ui.engine.view.UILabel;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Alex on 01/09/2015.
 */
public class PanelStorageModule extends GameUIModule {
    private static class PanelStorageModuleWindow extends UIWindow {

        @Override
        protected void onCreate(UIWindow window, FrameLayout content) {
        }

        @Override
        protected void onRefresh(int update) {
            if (update % 10 == 0) {
                Map<ItemInfo, Integer> items = new HashMap<>();
                for (int x = 0; x < Game.getInstance().getInfo().worldWidth; x++) {
                    for (int y = 0; y < Game.getInstance().getInfo().worldHeight; y++) {
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
                    lbItem.setTextSize(14);
                    lbItem.setText(itemInfo.label + " (" + items.get(itemInfo) + ")");
                    lbItem.setPosition(10, 10 + 20 * index++);
                    addView(lbItem);
                }
            }
        }

        @Override
        protected String getContentLayout() {
            return "panels/storage";
        }
    }

    @Override
    protected void onLoaded() {
        ((PanelModule)ModuleManager.getInstance().getModule(PanelModule.class)).addShortcut("Storage", new PanelStorageModuleWindow());
    }

    @Override
    protected void onUpdate(int tick) {
    }
}
