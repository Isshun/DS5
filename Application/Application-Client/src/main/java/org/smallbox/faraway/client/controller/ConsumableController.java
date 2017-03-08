package org.smallbox.faraway.client.controller;

import org.smallbox.faraway.client.controller.annotation.BindLuaController;
import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIList;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.modules.consumable.ConsumableModule;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Alex on 26/04/2016.
 */
public class ConsumableController extends LuaController {

    @BindLua private UIList consumableList;

    @BindModule
    private ConsumableModule consumableModule;

    @BindLuaController
    private MainPanelController mainPanelController;

    @Override
    public void onReloadUI() {
        mainPanelController.addShortcut("Consumables", this);
    }

    @Override
    public void onNewGameUpdate(Game game) {
        if (consumableList != null) {
            consumableList.clear();

            Map<ItemInfo, Integer> quantities = new HashMap<>();
            consumableModule.getConsumables().forEach(consumable -> quantities.put(consumable.getInfo(), consumable.getFreeQuantity() + (quantities.containsKey(consumable.getInfo()) ? quantities.get(consumable.getInfo()) : 0)));

            quantities.entrySet().forEach(entry -> consumableList.addView(UILabel.create(null).setText(entry.getKey().label + " x " + entry.getValue()).setSize(100, 20)));
        }
    }
}
