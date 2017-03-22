package org.smallbox.faraway.client.controller;

import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.controller.annotation.BindLuaAction;
import org.smallbox.faraway.client.ui.engine.views.widgets.UICheckBox;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIFrame;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIList;
import org.smallbox.faraway.client.ui.engine.views.widgets.View;
import org.smallbox.faraway.core.dependencyInjector.BindComponent;
import org.smallbox.faraway.core.game.Data;
import org.smallbox.faraway.modules.item.UsableItem;

/**
 * Created by Alex on 26/04/2016.
 */
public class ItemInfoFactoryComponentsController extends LuaController {

    @BindComponent
    private Data data;

    @BindLua
    private UIList listComponents;

    @Override
    protected void onControllerUpdate() {
    }

    public void setItem(UsableItem item) {

        item.getFactory().getAcceptedComponents()
                .forEach((itemInfo, accepted) -> {
                    View view = new UIFrame(null).setSize(400, 24);

//                    view.addView(UIImage.create(null)
//                            .setImage(CollectionUtils.isNotEmpty(itemInfo.graphics) ? itemInfo.graphics.get(0) : null));

                    view.addView(UICheckBox.create(null)
                            .setText(itemInfo.label)
                            .setTextColor(0x9afbff)
                            .setChecked(accepted ? UICheckBox.Value.TRUE : UICheckBox.Value.FALSE)
                            .setOnCheckListener(checked -> item.getFactory().setAcceptedComponents(itemInfo, checked == UICheckBox.Value.TRUE))
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
