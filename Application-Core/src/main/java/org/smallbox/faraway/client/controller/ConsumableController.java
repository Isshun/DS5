package org.smallbox.faraway.client.controller;

import org.apache.commons.collections4.CollectionUtils;
import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.ui.widgets.UIFrame;
import org.smallbox.faraway.client.ui.widgets.UIList;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.AfterGameLayerInit;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.game.consumable.ConsumableModule;

import java.util.HashMap;
import java.util.Map;

@GameObject
public class ConsumableController extends LuaController {

    @BindLua private UIList consumableList;
    @Inject private ConsumableModule consumableModule;

    @AfterGameLayerInit
    public void afterGameLayerInit() {
    }

    @Override
    public void onControllerUpdate() {
        consumableList.removeAllViews();

        Map<ItemInfo, Integer> quantities = new HashMap<>();
        consumableModule.getAll().forEach(consumable -> quantities.put(consumable.getInfo(), consumable.getTotalQuantity() + (quantities.getOrDefault(consumable.getInfo(), 0))));

        quantities.forEach((itemInfo, quantity) -> {

            UIFrame view = consumableList.createFromTemplate(UIFrame.class);

            view.findLabel("lb_consumable").setDashedString(itemInfo.label, " x " + quantity, 38);

            if (CollectionUtils.isNotEmpty(itemInfo.graphics)) {
                view.findImage("img_consumable").setImage(itemInfo.graphics.get(0));
            }

            consumableList.addNextView(view);
        });

        consumableList.switchViews();
    }

}
