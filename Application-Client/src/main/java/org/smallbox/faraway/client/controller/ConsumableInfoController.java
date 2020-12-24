package org.smallbox.faraway.client.controller;

import org.smallbox.faraway.client.SelectionManager;
import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.ui.engine.UIEventManager;
import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.module.world.model.ConsumableItem;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.consumable.ConsumableModule;

import java.util.Queue;

/**
 * Created by Alex on 26/04/2016.
 */
@GameObject
public class ConsumableInfoController extends AbsInfoLuaController<ConsumableItem> {

    @Inject
    protected SelectionManager selectionManager;

    @Inject
    private UIEventManager uiEventManager;

    @Inject
    private ConsumableModule consumableModule;

    @BindLua private UILabel lbLabel;
    @BindLua private UILabel lbQuantity;
    @BindLua private UILabel lbJob;
    @BindLua private UILabel lbName;

    @Override
    public void onReloadUI() {
        selectionManager.registerSelection(this);
    }

    @Override
    protected void onDisplayUnique(ConsumableItem consumable) {
        if (consumable.getTotalQuantity() <= 0) {
            closePanel();
        }

        lbLabel.setText(consumable.getLabel() + " x" + consumable.getTotalQuantity());
//        lbName.setText(consumableItem.getName());
//        lbQuantity.setText(String.valueOf(consumableItem.getTotalQuantity()));
    }

    @Override
    protected void onDisplayMultiple(Queue<ConsumableItem> objects) {
        lbLabel.setText("MULTIPLE");
    }

    @Override
    public ConsumableItem getObjectOnParcel(ParcelModel parcel) {
        return consumableModule.getConsumable(parcel);
    }
}
