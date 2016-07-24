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

    @BindModule("")
    private ConsumableModule _module;

    @BindModule("")
    private WorldInteractionModule _worldInteraction;

    @Override
    public void gameStart(Game game) {
        _module.addObserver(new ConsumableModuleObserver() {
            @Override
            public void onDeselectConsumable(ConsumableModel consumable) {
                setVisible(false);
            }

            @Override
            public void onSelectConsumable(ConsumableModel consumable) {
                setVisible(true);
                refreshInfo(consumable);
            }
        });
    }

    public void refreshInfo(ConsumableModel consumable) {
        lbName.setText(consumable.getLabel());
        lbQuantity.setText("Quantity: " + consumable.getQuantity());
    }
}
