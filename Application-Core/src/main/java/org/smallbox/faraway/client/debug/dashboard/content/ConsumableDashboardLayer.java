package org.smallbox.faraway.client.debug.dashboard.content;

import org.smallbox.faraway.client.debug.dashboard.DashboardLayerBase;
import org.smallbox.faraway.client.renderer.BaseRenderer;
import org.smallbox.faraway.client.renderer.MapRenderer;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.game.consumable.ConsumableModule;
import org.smallbox.faraway.game.world.Parcel;

@GameObject
public class ConsumableDashboardLayer extends DashboardLayerBase {
    @Inject private ConsumableModule consumableModule;

    @Override
    protected void onDraw(BaseRenderer renderer, int frame) {
        if (consumableModule != null && consumableModule.getAll() != null) {
            consumableModule.getAll().forEach(consumable -> {
                Parcel parcel = consumable.getParcel();
                drawDebug(renderer, "Consumable", consumable.getInfo().label + " x " + consumable.getTotalQuantity() + " at " + (parcel != null ? parcel : "???"));
//                consumables.put(consumable.getInfo(), quantity + consumable.getFreeQuantity());
            });

//            consumables.forEach((key, value) -> drawDebugConsumableInfo(renderer, key, value));
        }
    }

    private void drawDebugConsumableInfo(MapRenderer renderer, ItemInfo itemInfo, int quantity) {
        drawDebug(renderer, "Consumable", itemInfo.label + " x " + quantity);
    }

}
