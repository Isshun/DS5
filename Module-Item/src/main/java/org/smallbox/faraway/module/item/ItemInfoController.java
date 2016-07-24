package org.smallbox.faraway.module.item;

import org.smallbox.faraway.core.BindModule;
import org.smallbox.faraway.core.game.BindLua;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.module.character.controller.LuaController;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.module.item.item.ItemModel;
import org.smallbox.faraway.module.world.WorldInteractionModule;
import org.smallbox.faraway.module.world.WorldInteractionModuleObserver;
import org.smallbox.faraway.ui.engine.views.widgets.UILabel;

import java.util.Collection;

/**
 * Created by Alex on 26/04/2016.
 */
public class ItemInfoController extends LuaController {
    @BindLua private UILabel        lbName;

    @BindModule("")
    private ItemModule _module;

    @Override
    public void gameStart(Game game) {
        _module.addObserver(new ItemModuleObserver() {
            @Override
            public void onSelectItem(ItemModel item) {
                setVisible(true);
                refreshInfo(item);
            }
        });
    }

    public void refreshInfo(ItemModel consumable) {
        lbName.setText(consumable.getLabel());
    }
}
