package org.smallbox.faraway.client.controller;

import com.badlogic.gdx.Input;
import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.controller.annotation.BindLuaAction;
import org.smallbox.faraway.client.selection.GameSelectionManager;
import org.smallbox.faraway.client.ui.engine.UIEventManager;
import org.smallbox.faraway.client.ui.engine.views.View;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIImage;
import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.core.GameShortcut;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.gameAction.OnGameSelectAction;
import org.smallbox.faraway.core.module.world.model.ConsumableItem;
import org.smallbox.faraway.core.module.world.model.Parcel;
import org.smallbox.faraway.modules.consumable.ConsumableModule;

import java.util.Queue;

@GameObject
public class ConsumableInfoController extends AbsInfoLuaController<ConsumableItem> {
    @Inject protected GameSelectionManager gameSelectionManager;
    @Inject private UIEventManager uiEventManager;
    @Inject private ConsumableModule consumableModule;
    @Inject private MainPanelController mainPanelController;

    @BindLua private UILabel lbName;
    @BindLua private UILabel lbQuantity;
    @BindLua private UILabel lbJob;
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

        lbName.setText(consumable.getLabel());
        lbQuantity.setText(String.valueOf(consumable.getTotalQuantity()));
        image.setImage(consumable.getGraphic());
//        lbName.setText(consumableItem.getName());
//        lbQuantity.setText(String.valueOf(consumableItem.getTotalQuantity()));
    }

    @Override
    protected void onDisplayUnique(ConsumableItem consumableItem) {
    }

    @Override
    protected void onDisplayMultiple(Queue<ConsumableItem> objects) {
        lbName.setText("MULTIPLE");
    }

    @Override
    public ConsumableItem getObjectOnParcel(Parcel parcel) {
        return consumableModule.getConsumable(parcel);
    }

    @BindLuaAction
    private void onClose(View view) {
        mainPanelController.setVisible(true);
    }

    @GameShortcut(key = Input.Keys.ESCAPE)
    private void onClose() {
        mainPanelController.setVisible(true);
    }
}
