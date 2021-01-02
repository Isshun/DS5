package org.smallbox.faraway.client.controller;

import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.selection.GameSelectionManager;
import org.smallbox.faraway.client.ui.engine.UIEventManager;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIImage;
import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.gameAction.OnGameSelectAction;
import org.smallbox.faraway.core.module.world.model.ConsumableItem;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.consumable.ConsumableModule;

import java.util.Queue;

@GameObject
public class ConsumableInfoController extends AbsInfoLuaController<ConsumableItem> {

    @Inject
    protected GameSelectionManager gameSelectionManager;

    @Inject
    private UIEventManager uiEventManager;

    @Inject
    private ConsumableModule consumableModule;

    @BindLua private UILabel lbLabel;
    @BindLua private UILabel lbQuantity;
    @BindLua private UILabel lbJob;
    @BindLua private UILabel lbName;
    @BindLua private UIImage image;

    @Override
    public void onReloadUI() {
        gameSelectionManager.registerSelection(this);
    }

    @OnGameSelectAction(ConsumableItem.class)
    private void onSelectConsumable(ConsumableItem consumable) {
        setVisible(true);

        if (consumable.getTotalQuantity() <= 0) {
            closePanel();
        }

        lbLabel.setText(consumable.getLabel() + " x" + consumable.getTotalQuantity());
        image.setImage(consumable.getGraphic());
//        lbName.setText(consumableItem.getName());
//        lbQuantity.setText(String.valueOf(consumableItem.getTotalQuantity()));
    }

    @Override
    protected void onDisplayUnique(ConsumableItem consumableItem) {
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
