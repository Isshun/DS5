package org.smallbox.faraway.game.model.job;

import org.smallbox.faraway.game.helper.WorldHelper;
import org.smallbox.faraway.game.model.GameData;
import org.smallbox.faraway.game.model.MovableModel;
import org.smallbox.faraway.game.model.ReceiptModel;
import org.smallbox.faraway.game.model.character.base.CharacterModel;
import org.smallbox.faraway.game.model.item.*;
import org.smallbox.faraway.game.module.ModuleHelper;
import org.smallbox.faraway.util.MoveListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 11/07/2015.
 */
public abstract class BaseBuildJobModel extends BaseJobModel {
    protected List<ReceiptModel>    _receipts = new ArrayList<>();
    protected ReceiptModel          _receipt;
    protected Status                _status;
    public MapObjectModel           _mainItem;

    public BaseBuildJobModel(ItemInfo.ItemInfoAction actionInfo, ParcelModel jobParcel, GDXDrawable iconPath, GDXDrawable iconActionPath) {
        super(actionInfo, jobParcel, iconPath, iconActionPath);
    }

    public enum Status {
        WAITING, MAIN_ACTION, MOVE_TO_INGREDIENT, MOVE_TO_FACTORY, MOVE_TO_STORAGE
    }

    @Override
    public void onDraw(onDrawCallback callback) {
        callback.onDraw(_mainItem.getX(), _mainItem.getY());
    }

    public ReceiptModel getReceipt() {
        return _receipt;
    }

    public void addConsumable(ConsumableModel consumable) {
        for (ReceiptModel receipt: _receipts) {
            receipt.addConsumable(consumable);
        }
        if (_receipt == null) {
            onCheck(null);
        }
    }

    public void removeConsumable(ConsumableModel consumable) {
        for (ReceiptModel receipt: _receipts) {
            receipt.removeConsumable(consumable);
        }
        if (_receipt == null) {
            onCheck(null);
        }
    }

    @Override
    public ParcelModel getActionParcel() {
        return _mainItem.getParcel();
    }

}
