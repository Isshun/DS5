package org.smallbox.faraway.client.controller;

import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.lua.BindLua;
import org.smallbox.faraway.core.module.world.model.ConsumableItem;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.consumable.ConsumableModule;

import java.util.List;

/**
 * Created by Alex on 26/04/2016.
 */
public class ConsumableInfoController extends AbsInfoLuaController<ConsumableItem> {

    @BindModule
    private ConsumableModule consumableModule;

    @BindLua
    private UILabel lbLabel;

    @BindLua
    private UILabel lbQuantity;

    @BindLua
    private UILabel lbJob;

    @BindLua
    private UILabel lbName;

    @Override
    protected void onDisplayUnique(ConsumableItem consumableItem) {
        lbLabel.setText(consumableItem.getLabel());
        lbName.setText(consumableItem.getName());
        lbQuantity.setText(String.valueOf(consumableItem.getQuantity()));
    }

    @Override
    protected void onDisplayMultiple(List<ConsumableItem> list) {
        lbLabel.setText("MULTIPLE");
    }

    @Override
    protected ConsumableItem getObjectOnParcel(ParcelModel parcel) {
        return consumableModule.getConsumable(parcel);
    }
}
