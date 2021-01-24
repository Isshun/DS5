package org.smallbox.faraway.client.controller.character;

import org.smallbox.faraway.client.controller.LuaController;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.game.character.model.base.CharacterModel;

@GameObject
public class CharacterInfoInventoryController extends LuaController {

    private CharacterModel _character;

    @Override
    protected void onControllerUpdate() {
//        listInventory.removeAllViews();
//
//        if (_character != null && _character.hasExtra(CharacterInventoryExtra.class)) {
//            _character.getExtra(CharacterInventoryExtra.class).getAll().forEach((itemInfo, quantity) -> {
//                UIFrame view = new UIFrame(null);
//                view.setSize(300, 32);
//
//                if (CollectionUtils.isNotEmpty(itemInfo.graphics)) {
//                    view.addView(UIImage.create(null)
//                            .setImage(itemInfo.graphics.get(0))
//                            .setSize(32, 32));
//                }
//                view.addView(UILabel.create(null)
//                        .setDashedString(itemInfo.label, String.valueOf(quantity), 38)
//                        .setTextColor(0xB4D4D3ff)
//                        .setPosition(36, 7)
//                        .setSize(100, 20));
//
//                listInventory.addView(view);
//            });
//        }
    }

    public void selectCharacter(CharacterModel character) {
        _character = character;
    }

}
