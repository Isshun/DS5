package org.smallbox.faraway.client.controller.character;

import org.smallbox.faraway.client.controller.LuaController;
import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.ui.engine.views.widgets.*;
import org.smallbox.faraway.core.dependencyInjector.GameObject;
import org.smallbox.faraway.modules.character.model.CharacterInventoryExtra;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;
import org.smallbox.faraway.util.CollectionUtils;

/**
 * Created by Alex on 06/03/2017.
 */
@GameObject
public class CharacterInfoInventoryController extends LuaController {

    @BindLua
    private UIList listInventory;

    private CharacterModel _character;

    @Override
    protected void onControllerUpdate() {
        listInventory.removeAllViews();

        if (_character != null && _character.hasExtra(CharacterInventoryExtra.class)) {
            _character.getExtra(CharacterInventoryExtra.class).getAll().forEach((itemInfo, quantity) -> {
                View view = new UIFrame(null);
                view.setSize(300, 32);

                if (CollectionUtils.isNotEmpty(itemInfo.graphics)) {
                    view.addView(UIImage.create(null)
                            .setImage(itemInfo.graphics.get(0))
                            .setSize(32, 32));
                }
                view.addView(UILabel.create(null)
                        .setDashedString(itemInfo.label, String.valueOf(quantity), 38)
                        .setTextColor(0xB4D4D3ff)
                        .setPosition(36, 7)
                        .setSize(100, 20));

                listInventory.addView(view);
            });
        }
    }

    public void selectCharacter(CharacterModel character) {
        _character = character;
    }
}
