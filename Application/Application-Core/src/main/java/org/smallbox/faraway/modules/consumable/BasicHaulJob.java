//package org.smallbox.faraway.modules.consumable;
//
//import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
//import org.smallbox.faraway.core.module.job.model.abs.JobModel;
//import org.smallbox.faraway.core.module.world.model.ConsumableItem;
//import org.smallbox.faraway.core.module.world.model.ParcelModel;
//
//import java.util.Collection;
//import java.util.Map;
//import java.util.concurrent.ConcurrentLinkedQueue;
//
///**
// * Created by Alex on 09/12/2016.
// */
//public abstract class BasicHaulJob extends JobModel {
//
//    protected Map<ConsumableItem, Integer> _targetConsumables;
//    protected Collection<ConsumableModule.ConsumableJobLock> _locks = new ConcurrentLinkedQueue<>();
//
//    public BasicHaulJob(ItemInfo.ItemInfoAction itemInfoAction, ParcelModel parcelModel) {
//        super(itemInfoAction, parcelModel);
//    }
//
//    public Map<ConsumableItem, Integer> getConsumables() { return _targetConsumables; }
//
//}
