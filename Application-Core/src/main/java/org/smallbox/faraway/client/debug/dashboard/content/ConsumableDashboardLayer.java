package org.smallbox.faraway.client.debug.dashboard.content;

import org.smallbox.faraway.client.debug.dashboard.DashboardLayerBase;
import org.smallbox.faraway.client.render.GDXRenderer;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.module.world.model.Parcel;
import org.smallbox.faraway.modules.consumable.ConsumableModule;

import java.util.HashMap;
import java.util.Map;

@GameObject
public class ConsumableDashboardLayer extends DashboardLayerBase {
    @Inject private ConsumableModule consumableModule;

    @Override
    protected void onDraw(GDXRenderer renderer, int frame) {
        if (consumableModule != null && consumableModule.getAll() != null) {
            Map<ItemInfo, Integer> consumables = new HashMap<>();
            consumableModule.getAll().forEach(consumable -> {
                int quantity = consumables.getOrDefault(consumable.getInfo(), 0);
                Parcel parcel = consumable.getParcel();
                drawDebug(renderer, "Consumable", consumable.getInfo().label + " x " + quantity + " at " + (parcel != null ? parcel : "???"));
//                consumables.put(consumable.getInfo(), quantity + consumable.getFreeQuantity());
            });

//            consumables.forEach((key, value) -> drawDebugConsumableInfo(renderer, key, value));
        }
    }

    private void drawDebugConsumableInfo(GDXRenderer renderer, ItemInfo itemInfo, int quantity) {
        drawDebug(renderer, "Consumable", itemInfo.label + " x " + quantity);
    }

}
