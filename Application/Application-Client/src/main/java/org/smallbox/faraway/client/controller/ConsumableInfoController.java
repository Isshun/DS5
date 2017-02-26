package org.smallbox.faraway.client.controller;

import com.sun.glass.ui.Cursor;
import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.lua.BindLua;
import org.smallbox.faraway.core.module.world.model.ConsumableItem;
import org.smallbox.faraway.modules.consumable.ConsumableModule;
import org.smallbox.faraway.modules.consumable.ConsumableModuleObserver;

/**
 * Created by Alex on 26/04/2016.
 */
public class ConsumableInfoController extends LuaController {
    @BindLua private UILabel        lbName;
    @BindLua private UILabel        lbQuantity;
    @BindLua private UILabel        lbJob;

    @BindModule
    private ConsumableModule consumableModule;

//    @BindModule
//    private WorldInteractionModule worldInteractionModule;

    private ConsumableItem _consumable;

    @Override
    public void onGameStart(Game game) {
        consumableModule.addObserver(new ConsumableModuleObserver() {
            @Override
            public void onDeselectConsumable(ConsumableItem consumable) {
                Cursor.setVisible(false);
            }

            @Override
            public void onSelectConsumable(ConsumableItem consumable) {
                selectConsumable(consumable);
            }
        });
    }

    @Override
    public void onNewGameUpdate(Game game) {
        refreshConsumable();
    }

    private void refreshConsumable() {
        if (_consumable != null) {
            lbName.setText(_consumable.getLabel());
            lbQuantity.setText("Quantity: " + _consumable.getQuantity());
            lbJob.setText("Job: " + (_consumable.getJob() != null ? _consumable.getJob().getLabel() : "no job"));
        }
    }

    private void selectConsumable(ConsumableItem consumable) {
        Cursor.setVisible(true);
        _consumable = consumable;
    }
}
