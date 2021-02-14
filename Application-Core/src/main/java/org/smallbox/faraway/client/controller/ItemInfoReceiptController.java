package org.smallbox.faraway.client.controller;

import com.badlogic.gdx.Input;
import org.apache.commons.collections4.CollectionUtils;
import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.ui.widgets.UICheckBox;
import org.smallbox.faraway.client.ui.widgets.UILabel;
import org.smallbox.faraway.client.ui.widgets.UIList;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.game.world.Parcel;
import org.smallbox.faraway.game.itemFactory.ItemFactoryModel;

import java.util.Queue;

@GameObject
public class ItemInfoReceiptController extends AbsInfoLuaController<ItemFactoryModel.FactoryReceiptGroupModel> {

    @BindLua private UILabel lbName;
    @BindLua private UIList listActions;
    @Inject private ItemInfoController itemInfoController;

    @Deprecated
    public boolean onKeyPress(int key) {
        if (key == Input.Keys.ESCAPE && CollectionUtils.isNotEmpty(listSelected)) {
            itemInfoController.setVisible(true);
            listSelected.clear();
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
                    cbReceipt.setChecked(receipt.isActive);
                    cbReceipt.setOnCheckListener((checked, clickOnBox) ->receipt.isActive = checked);
                    listActions.addView(cbReceipt);
                });

    }

    @Override
    protected void onDisplayMultiple(Queue<ItemFactoryModel.FactoryReceiptGroupModel> objects) {

    }

    @Override
    public ItemFactoryModel.FactoryReceiptGroupModel getObjectOnParcel(Parcel parcel) {
        return null;
    }
}
