package org.smallbox.faraway.client.controller.character;

import org.smallbox.faraway.client.controller.LuaController;
import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.ui.widgets.CompositeView;
import org.smallbox.faraway.client.ui.widgets.UIGrid;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.game.character.model.CharacterInventoryExtra;
import org.smallbox.faraway.game.character.model.base.CharacterModel;

@GameObject
public class CharacterInfoInventoryController extends LuaController {

    @BindLua private UIGrid gridInventory;

    private CharacterModel _character;

    @Override
    protected void onControllerUpdate() {
        if (_character != null && _character.hasExtra(CharacterInventoryExtra.class)) {
            _character.getExtra(CharacterInventoryExtra.class).getAll().forEach(inventoryConsumable -> {
                CompositeView inventoryView = gridInventory.createFromTemplate(CompositeView.class);

                inventoryView.findImage("img_inventory").setImage(inventoryConsumable.getGraphic());
                inventoryView.findLabel("lb_inventory").setText(String.valueOf(inventoryConsumable.getTotalQuantity()));

                gridInventory.addNextView(inventoryView);
            });
        }
        gridInventory.switchViews();
    }

    public void selectCharacter(CharacterModel character) {
        _character = character;
    }

}
