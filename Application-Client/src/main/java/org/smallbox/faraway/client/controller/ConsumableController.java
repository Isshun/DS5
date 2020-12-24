package org.smallbox.faraway.client.controller;

import com.badlogic.gdx.Input;
import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.ui.engine.views.widgets.*;
import org.smallbox.faraway.core.GameShortcut;
import org.smallbox.faraway.core.dependencyInjector.AfterGameLayerInit;
import org.smallbox.faraway.core.dependencyInjector.GameObject;
import org.smallbox.faraway.core.dependencyInjector.Inject;
import org.smallbox.faraway.core.engine.ColorUtils;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.modules.consumable.ConsumableModule;
import org.smallbox.faraway.util.CollectionUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Alex on 26/04/2016.
 */
@GameObject
public class ConsumableController extends LuaController {

    @BindLua private UIList consumableList;

    @Inject
    private ConsumableModule consumableModule;

    @Inject
    private MainPanelController mainPanelController;

    @AfterGameLayerInit
    public void afterGameLayerInit() {
        mainPanelController.addShortcut("Consumables", this);
    }

    @Override
    public void onControllerUpdate() {
        consumableList.removeAllViews();

        Map<ItemInfo, Integer> quantities = new HashMap<>();
        consumableModule.getConsumables().forEach(consumable -> quantities.put(consumable.getInfo(), consumable.getTotalQuantity() + (quantities.getOrDefault(consumable.getInfo(), 0))));

        quantities.forEach((itemInfo, quantity) -> {

            View view = new UIFrame(null).setSize(100, 30);

            view.addView(UILabel.create(null)
                    .setDashedString(itemInfo.label, " x " + quantity, 38)
                    .setTextColor(ColorUtils.COLOR2)
                    .setTextSize(14)
                    .setPadding(12)
                    .setPosition(25, 0));

            if (CollectionUtils.isNotEmpty(itemInfo.graphics)) {
                view.addView(UIImage.create(null)
                        .setImage(itemInfo.graphics.get(0)));
            }

            consumableList.addNextView(view);
        });

        consumableList.switchViews();
    }

    @GameShortcut(key = Input.Keys.I)
    public void onPressT() {
        setVisible(true);
    }
}
