//package org.smallbox.faraway.core.module.job.model.abs;
//
//import FactoryReceiptModel;
//import org.smallbox.faraway.core.game.model.OldReceiptModel;
//import org.smallbox.faraway.core.module.world.model.ConsumableItem;
//import org.smallbox.faraway.core.data.ItemInfo;
//import org.smallbox.faraway.core.module.world.model.MapObjectModel;
//import org.smallbox.faraway.core.module.world.model.ParcelModel;
//import org.smallbox.faraway.client.drawable.GDXDrawable;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Created by Alex on 11/07/2015.
// */
//public abstract class BaseBuildJobModel extends JobModel {
////    protected List<OldReceiptModel>     _receipts = new ArrayList<>();
//    protected FactoryReceiptModel   _receipt;
//    public MapObjectModel               _mainItem;
//
//    public BaseBuildJobModel(ItemInfo.ItemInfoAction actionInfo, ParcelModel jobParcel, GDXDrawable iconPath, GDXDrawable iconActionPath) {
//        super(actionInfo, jobParcel, iconPath, iconActionPath);
//    }
//
//
////    public OldReceiptModel getReceipt() {
////        return _receipt;
////    }
//
////    public void addConsumable(ConsumableItem consumable) {
////        for (OldReceiptModel receipt: _receipts) {
////            receipt.addConsumable(consumable);
////        }
////        if (_receipt == null) {
////            onCheck(null);
////        }
////    }
////
////    public void removeConsumable(ConsumableItem consumable) {
////        for (OldReceiptModel receipt: _receipts) {
////            receipt.removeConsumable(consumable);
////        }
////        if (_receipt == null) {
////            onCheck(null);
////        }
////    }
//
//    @Override
//    public ParcelModel getTargetParcel() {
//        return _mainItem.getParcel();
//    }
//
//}