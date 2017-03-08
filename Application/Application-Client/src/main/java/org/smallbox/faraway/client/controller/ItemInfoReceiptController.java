package org.smallbox.faraway.client.controller;

import org.smallbox.faraway.client.controller.annotation.BindLuaController;
import org.smallbox.faraway.client.ui.engine.views.widgets.UICheckBox;
import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIList;
import org.smallbox.faraway.core.engine.GameEventListener;
import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.itemFactory.ItemFactoryModel;
import org.smallbox.faraway.util.CollectionUtils;

import java.util.List;

/**
 * Created by Alex on 11/12/2016.
 */
public class ItemInfoReceiptController extends AbsInfoLuaController<ItemFactoryModel.FactoryReceiptGroupModel> {

    @BindLua private UILabel lbName;

    @BindLua private UIList listActions;

    @BindLuaController
    private ItemInfoController itemInfoController;

    @Override
    public boolean onKeyPress(GameEventListener.Key key) {
        if (key == GameEventListener.Key.ESCAPE && CollectionUtils.isNotEmpty(list)) {
            itemInfoController.setVisible(true);
            list = null;
            return true;
        }
        return false;
    }

    @Override
    protected void onDisplayUnique(ItemFactoryModel.FactoryReceiptGroupModel receiptGroup) {
        lbName.setText(receiptGroup.receiptGroupInfo.label);

        receiptGroup.factory.getReceipts().stream()
                .filter(receipt -> receipt.receiptGroup == receiptGroup)
                .forEach(receipt -> {
                    UICheckBox cbReceipt = UICheckBox.create(null);
                    cbReceipt.setText(receipt.receiptInfo.inputs.toString());
                    cbReceipt.setSize(100, 20);
                    cbReceipt.setPosition(20, 0);
                    cbReceipt.setChecked(receipt.isActive ? UICheckBox.Value.TRUE : UICheckBox.Value.FALSE);
                    cbReceipt.setOnCheckListener(checked -> receipt.isActive = checked == UICheckBox.Value.TRUE);
                    listActions.addView(cbReceipt);
                });

    }

    @Override
    protected void onDisplayMultiple(List<ItemFactoryModel.FactoryReceiptGroupModel> list) {

    }

    @Override
    protected ItemFactoryModel.FactoryReceiptGroupModel getObjectOnParcel(ParcelModel parcel) {
        return null;
    }
}
