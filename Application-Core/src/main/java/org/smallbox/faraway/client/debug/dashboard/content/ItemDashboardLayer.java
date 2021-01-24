package org.smallbox.faraway.client.debug.dashboard.content;

import org.smallbox.faraway.client.debug.dashboard.DashboardLayerBase;
import org.smallbox.faraway.client.renderer.BaseRenderer;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.game.item.ItemModule;
import org.smallbox.faraway.game.item.UsableItem;

@GameObject
public class ItemDashboardLayer extends DashboardLayerBase {
    @Inject private ItemModule itemModule;

    @Override
    protected void onDraw(BaseRenderer renderer, int frame) {
        itemModule.getAll().forEach(item -> drawDebugItem(renderer, item));
    }

    private void drawDebugItem(BaseRenderer renderer, UsableItem item) {
        StringBuilder sb = new StringBuilder();
        sb.append(item.getName()).append(" ").append(item.getParcel().x).append("x").append(item.getParcel().y);

        if (item.getFactory() != null) {
            sb.append(" factory: ").append(item.getFactory().getMessage());
            if (item.getFactory().getRunningReceipt() != null) {
                sb.append(" cost remaining: ").append(item.getFactory().getRunningReceipt().getCostRemaining());
            }
        }

        if (item.getInventory() != null) {
            sb.append(" inventory: ");
            item.getInventory().forEach(consumable -> sb.append(consumable.getLabel()).append("x").append(consumable.getFreeQuantity()).append(" "));
        }

        drawDebug(renderer, "Item", sb.toString());
    }

}
