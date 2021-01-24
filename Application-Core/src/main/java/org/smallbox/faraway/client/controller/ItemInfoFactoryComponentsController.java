package org.smallbox.faraway.client.controller;

import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.controller.annotation.BindLuaAction;
import org.smallbox.faraway.client.ui.engine.views.View;
import org.smallbox.faraway.client.ui.engine.views.widgets.UICheckBox;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIFrame;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIList;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.Data;
import org.smallbox.faraway.modules.item.UsableItem;

@GameObject
public class ItemInfoFactoryComponentsController extends LuaController {
    @Inject private Data data;

    @BindLua
    private UIList listComponents;

    public void setItem(UsableItem item) {

        item.getFactory().getAcceptedComponents()
                .forEach((itemInfo, accepted) -> {
                    UIFrame view = new UIFrame(null);
                    view.setSize(400, 24);

//                    view.addView(UIImage.create(null)
//                            .setImage(CollectionUtils.isNotEmpty(itemInfo.graphics) ? itemInfo.graphics.get(0) : null));

                    view.addView(UICheckBox.create(null)
                            .setText(itemInfo.label)
                            .setTextColor(0x9afbffff)
                            .setChecked(accepted)
                            .setOnCheckListener((checked, clickOnBox) ->item.getFactory().setAcceptedComponents(itemInfo, checked))
                            .setSize(100, 20));

                    listComponents.addNextView(view);
                });

        listComponents.switchViews();
    }

    @BindLuaAction
    private void onCloseComponents(View view) {
        setVisible(false);
    }
}
