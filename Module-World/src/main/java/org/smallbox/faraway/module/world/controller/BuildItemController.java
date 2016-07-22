package org.smallbox.faraway.module.world.controller;

import org.smallbox.faraway.core.engine.renderer.SpriteManager;
import org.smallbox.faraway.core.game.BindLua;
import org.smallbox.faraway.core.game.Data;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.game.module.character.controller.LuaController;
import org.smallbox.faraway.module.world.UIBuildModule;
import org.smallbox.faraway.ui.engine.views.widgets.*;

/**
 * Created by Alex on 22/07/2016.
 */
public class BuildItemController extends LuaController {
    @BindLua
    private UIGrid itemGrid;

    public void create(UIBuildModule uiBuildModule, BuildController buildController) {
        Data.getData().getItems().stream()
                .filter(item -> item.isUserItem)
                .forEach(item -> {
                    UIFrame frame = new UIFrame(null);
                    frame.setSize(300, 32);
                    frame.setOnClickListener(() -> uiBuildModule.selectItem(item));

                    UIImage image = new UIImage(null);
                    image.setImage(SpriteManager.getInstance().getIcon(item));
                    frame.addView(image);

                    UILabel label = new UILabel(null);
                    label.setMargin(14, 0, 0, 38);
                    label.setText(item.label);
                    frame.addView(label);

                    itemGrid.addView(frame);
                });
//            for (ItemInfo itemInfo: Data.getData().items) {
//                if (itemInfo.receipts != null && !itemInfo.receipts.isEmpty() && parentName.equals(itemInfo.parentName)) {
//                    View icStructure = UIImage.create(this).setImage(itemInfo.receipts.get(0).icon).setSize(32, 32);
//                    icStructure.setOnClickListener(() -> selectStructure(icStructure, itemInfo));
//                    categoryGrid.addView(icStructure);
//                }
//                if (itemInfo == _currentStructure) {
//                    listEntry.setBackgroundColor(0x349394);
//                }
//            }
//            listEntry.addView(categoryGrid);
//            _mainList.addView(listEntry);
//        }

    }
}
