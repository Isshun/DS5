package org.smallbox.faraway.core.game.module.job.model.abs;

import org.smallbox.faraway.core.game.module.world.model.ItemFactoryReceiptModel;
import org.smallbox.faraway.core.game.model.OldReceiptModel;
import org.smallbox.faraway.core.game.module.world.model.ConsumableModel;
import org.smallbox.faraway.core.game.module.world.model.ItemInfo;
import org.smallbox.faraway.core.game.module.world.model.MapObjectModel;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.core.engine.drawable.GDXDrawable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 11/07/2015.
 */
public abstract class BaseBuildJobModel extends JobModel {
    protected List<OldReceiptModel>     _receipts = new ArrayList<>();
    protected ItemFactoryReceiptModel   _receipt;
    protected Status                    _status;
    public MapObjectModel _mainItem;

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

//    public OldReceiptModel getReceipt() {
//        return _receipt;
//    }

    public void addConsumable(ConsumableModel consumable) {
        for (OldReceiptModel receipt: _receipts) {
            receipt.addConsumable(consumable);
        }
        if (_receipt == null) {
            onCheck(null);
        }
    }

    public void removeConsumable(ConsumableModel consumable) {
        for (OldReceiptModel receipt: _receipts) {
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
