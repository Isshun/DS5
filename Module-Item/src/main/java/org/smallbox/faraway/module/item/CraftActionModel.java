//package org.smallbox.faraway.module.item;
//
//import org.smallbox.faraway.core.game.modelInfo.ItemInfo.ActionInputInfo;
//import org.smallbox.faraway.core.game.modelInfo.ItemInfo.ItemInfoAction;
//import org.smallbox.faraway.core.module.world.model.ConsumableModel;
//import org.smallbox.faraway.util.Log;
//import org.smallbox.faraway.module.consumable.ConsumableModule;
//import org.smallbox.faraway.module.consumable.HaulJob;
//import org.smallbox.faraway.module.item.item.ItemModel;
//import org.smallbox.faraway.module.job.JobModule;
//
//import java.util.*;
//import java.util.stream.Collectors;
//
///**
// * Created by Alex on 29/07/2016.
// */
//public class CraftActionModel {
//    private final JobModule                             _jobModule;
//    private final ConsumableModule                      _consumableModule;
//    private final ItemModel                             _item;
//    private final ItemInfoAction                        _action;
//    private CraftJob                                    _craftJob;
//
//    public CraftActionModel(JobModule jobModule, ConsumableModule consumableModule, ItemModel item, ItemInfoAction action) {
//        Log.info("Create craft action: " + action.label);
//
//        _jobModule = jobModule;
//        _consumableModule = consumableModule;
//        _item = item;
//        _action = action;
//
//        create();
//    }
//
//    private void create() {
//        _craftJob = CraftJob.create(_item, _action);
//
//        // Create haul jobs
//        if (_action.inputs != null) {
//            _action.inputs.forEach(input -> {
//                HaulJob haulJob = new HaulJob(_consumableModule, _item.getParcel(), input.item, input.quantity);
//                haulJob.setOnCompleteListener(() -> {
//                    ConsumableModel consumable = haulJob.getConsumable();
//                    if (consumable != null) {
//                        _inputs.put(input, _inputs.get(input) + consumable.getQuantity());
//                    }
//                });
//                _craftJob.addSubJob(haulJob);
//                _jobModule.addJob(haulJob);
//            });
//        }
//    }
//
//    public void run() {
//        _craftJob.action();
//    }
//
//    public CraftJob getJob() {
//        return _craftJob;
//    }
//
//    public ItemInfoAction getAction() {
//        return _action;
//    }
//
//    public Map<ActionInputInfo, Integer> getInputs() {
//        return _inputs;
//    }
//}
