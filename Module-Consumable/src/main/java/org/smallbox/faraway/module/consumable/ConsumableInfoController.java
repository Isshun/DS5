package org.smallbox.faraway.module.consumable;

import org.smallbox.faraway.core.BindModule;
import org.smallbox.faraway.core.game.BindLua;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.module.character.controller.LuaController;
import org.smallbox.faraway.core.game.module.world.model.ConsumableModel;
import org.smallbox.faraway.module.world.WorldInteractionModule;
import org.smallbox.faraway.ui.engine.views.widgets.UILabel;

/**
 * Created by Alex on 26/04/2016.
 */
public class ConsumableInfoController extends LuaController {
    @BindLua private UILabel        lbName;
    @BindLua private UILabel        lbQuantity;
    @BindLua private UILabel        lbJob;

    @BindModule
    private ConsumableModule _module;

    @BindModule
    private WorldInteractionModule _worldInteraction;
    private ConsumableModel _consumable;

    @Override
    public void onGameStart(Game game) {
        _module.addObserver(new ConsumableModuleObserver() {
            @Override
            public void onDeselectConsumable(ConsumableModel consumable) {
                setVisible(false);
            }

            @Override
            public void onSelectConsumable(ConsumableModel consumable) {
                selectConsumable(consumable);
            }
        });
    }

    @Override
    protected void onGameUpdate(Game game) {
        if (isVisible()) {
            refreshConsumable();
        }
    }

    private void refreshConsumable() {
        if (_consumable != null) {
            lbName.setText(_consumable.getLabel());
            lbQuantity.setText("Quantity: " + _consumable.getQuantity());
            lbJob.setText("Job: " + (_consumable.getJob() != null ? _consumable.getJob().getLabel() : "no job"));
        }
    }

    private void selectConsumable(ConsumableModel consumable) {
        setVisible(true);
        _consumable = consumable;
    }
}