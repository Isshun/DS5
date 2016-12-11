package org.smallbox.faraway.module.itemFactory;

import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.game.modelInfo.ReceiptGroupInfo;
import org.smallbox.faraway.core.module.job.model.abs.JobModel;
import org.smallbox.faraway.core.module.world.model.ConsumableModel;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.module.consumable.BasicHaulJob;
import org.smallbox.faraway.module.consumable.ConsumableModule;
import org.smallbox.faraway.module.consumable.ConsumableStackModel;
import org.smallbox.faraway.module.item.job.CraftJob;
import org.smallbox.faraway.module.item.ItemModel;
import org.smallbox.faraway.module.job.JobModule;
import org.smallbox.faraway.util.Log;

import java.util.*;
import java.util.stream.Collectors;

import static org.smallbox.faraway.core.game.modelInfo.ItemInfo.*;

/**
 * Created by Alex on 15/10/2015.
 */
public class ItemFactoryModel {
    private Map<ItemInfo, ConsumableStackModel>     _inventory = new HashMap<>();
    private final ItemModel                         _item;
    private Map<ItemInfoAction, CraftJob>           _craftJobs = new HashMap<>();
    private FactoryReceiptModel _runningReceipt;
    private List<FactoryReceiptGroupModel> _receiptGroups;
    private List<FactoryReceiptModel> _receipts;
    // TODO: file d'attente de sortie
//    private List<FactoryShoppingItemModel>  _shoppingList;
    private final ItemInfoFactory _factoryInfo;
    private ParcelModel _storageParcel;
    private String                          _message;
    private List<BasicHaulJob> _haulJobs = new LinkedList<>();

    public List<BasicHaulJob> getHaulJobs() { return _haulJobs; }

    public boolean hasRunningReceipt() {
        return _runningReceipt != null;
    }

    public ItemFactoryModel(ItemModel item) {
        _item = item;
        _factoryInfo = item.getInfo().factory;

//        _factoryInfo.actions.stream()
//                .filter(action -> action.type == ItemInfoAction.ActionType.CRAFT)
//                .forEach(action -> _craftJobs.put(action, null));

//        ParcelModel itemParcel = _item.getParcel();
//        _storageParcel = _item.getParcel();
//        if (_factoryInfo.outputSlots != null) {
//            _storageParcel = WorldHelper.getParcel(
//                    itemParcel.x + _factoryInfo.outputSlots[0],
//                    itemParcel.y + _factoryInfo.outputSlots[1],
//                    itemParcel.z);
//        }

        if (_factoryInfo != null) {
            // Create receipt groups from factory info
            _receiptGroups = _factoryInfo.receiptGroups.stream()
                    .map(FactoryReceiptGroupModel::new)
                    .collect(Collectors.toList());

            // Create receipt from each receipt group
            _receipts = _receiptGroups.stream()
                    .flatMap(receiptGroup -> receiptGroup.receiptGroupInfo.receipts.stream().map(receiptInfo -> new FactoryReceiptModel(receiptGroup, receiptInfo)))
                    .collect(Collectors.toList());
        }
    }

    public void run(JobModule jobModule, ConsumableModule consumableModule) {
        Log.info("Run factory for " + _item.getName());

        // Create craft job from ItemInfo actions
        _craftJobs.entrySet().stream()
                .filter(entry -> entry.getValue() == null || entry.getValue().isFinish())
                .forEach(entry -> {
                    CraftJob craftJob = CraftJob.create(consumableModule, _item, entry.getKey());
                    _craftJobs.put(entry.getKey(), craftJob);
                    jobModule.addJob(craftJob);
                    jobModule.addJobs(craftJob.getSubJobs());
                });

        // Run jobs
        _craftJobs.values().forEach(JobModel::action);
    }

    public void addAction(ItemInfoAction action) {
        action.products.forEach(product -> {
            if (!_inventory.containsKey(product.item)) {
                _inventory.put(product.item, new ConsumableStackModel(product.item));
            }
        });
    }

    public void store(ConsumableModel consumable) {
        assert _inventory.containsKey(consumable.getInfo());

        _inventory.get(consumable.getInfo()).addConsumable(consumable);
    }

    public Collection<ConsumableStackModel> getInventory() {
        return _inventory.values();
    }

    public Map<ItemInfoAction, CraftJob> getCraftActions() {
        return _craftJobs;
    }

    public void setRunningReceipt(FactoryReceiptModel receipt) {
        _runningReceipt = receipt;

        if (receipt != null) {
            Log.info("Factory %s going to craft %s", _item, receipt);
            _runningReceipt.initComponents();
        }
    }

    public void addHaulJob(BasicHaulJob job) { _haulJobs.add(job); }

    public static class FactoryReceiptGroupModel {
        public final ReceiptGroupInfo   receiptGroupInfo;
        public final FactoryOutputMode  outputMode;
        public final boolean            auto;
        public final int                cost;
        public int                      mode;
        public boolean                  isActive;

        // Rename this mess
        public FactoryReceiptGroupModel(ReceiptGroupInfo receiptGroupInfo) {
            this.receiptGroupInfo = receiptGroupInfo;
            this.auto = false;
            this.cost = receiptGroupInfo.cost;
            this.outputMode = FactoryOutputMode.GROUND;
        }
    }

    public void setMessage(String message) { _message = message; }

    public List<FactoryReceiptGroupModel>   getOrders() { return _receiptGroups; }
    public List<FactoryReceiptModel>        getReceipts() { return _receipts; }
    public List<FactoryReceiptGroupModel>   getReceiptGroups() { return _receiptGroups; }
    public ReceiptGroupInfo                 getCurrentReceiptGroup() { return _runningReceipt != null ? _runningReceipt.receiptGroup.receiptGroupInfo : null; }
    public ReceiptGroupInfo.ReceiptInfo     getCurrentReceiptInfo() { return _runningReceipt != null ? _runningReceipt.receiptInfo : null; }
    public FactoryReceiptModel              getRunningReceipt() { return _runningReceipt; }
    public ParcelModel                      getStorageParcel() { return _storageParcel; }
    public String                           getMessage() { return _message; }

    public void moveReceipt(ReceiptGroupInfo receiptGroupInfo, int offset) {
        Optional<FactoryReceiptGroupModel> optionalEntry = _receiptGroups.stream().filter(entry -> entry.receiptGroupInfo == receiptGroupInfo).findFirst();
        if (optionalEntry.isPresent()) {
            int position = _receiptGroups.indexOf(optionalEntry.get()) + offset;
            _receiptGroups.remove(optionalEntry.get());
            _receiptGroups.add(Math.min(Math.max(position, 0), _receiptGroups.size()), optionalEntry.get());
        }
    }

    public void clear() {
//        throw new NotImplementedException("");

//        _message = "Stand-by";
//
//        if (_runningReceipt != null) {
//            _runningReceipt.getShoppingList().forEach(component -> ModuleHelper.getWorldModule().putConsumable(_item.getParcel(), component.consumable));
//            _runningReceipt.clear();
//            _runningReceipt = null;
//        }
//
//        _job = null;
    }

    // TODO: Do not use with locked consumables
    public boolean scan(ConsumableModule consumableModule) {
////        Log.info("scan");
//
//        long time = System.currentTimeMillis();
//        _runningReceipt = null;
//
//        if (_storageParcel != null && _storageParcel.getConsumable() != null) {
//            _message = "Factory is full";
//            return false;
//        }
//
//        // List itemInfo needed in all receiptGroups
//        Set <ItemInfo> allInputs = new HashSet<>();
//        _receipts.stream()
//                .filter(receipt -> receipt.receiptGroup.isActive)
//                .forEach(receipt -> allInputs.addAll(receipt.receiptInfo.inputs.stream().map(inputInfo -> inputInfo.item).collect(Collectors.toList())));
//
//        // Get distance for all of them
//        List<PotentialConsumable> potentials = new ArrayList<>();
//        allInputs.forEach(inputInfo ->
//                consumableModule.getConsumables().stream()
//                        .filter(consumable -> consumable.getInfo().instanceOf(inputInfo))
//                        .filter(consumable -> consumable.getParcel().isWalkable())
//                        .filter(consumable -> consumable.getJob() == null)
//                        .forEach(consumable -> {
//                            PathModel path = Application.pathManager.getPath(_item.getParcel(), consumable.getParcel(), false, false);
//                            if (path != null) {
//                                potentials.addSubJob(new PotentialConsumable(consumable, path.getLength()));
//                            }
//                        }));
//        Collections.sort(potentials, (c1, c2) -> c1.distance - c2.distance);
//
//        // For each receipt, find total distance between factory and components
//        _receipts.stream().filter(receipt -> receipt.receiptGroup.isActive).forEach(receiptEntry -> receiptEntry.setPotentialComponents(potentials));
//
//        // Get receipt group based on components availability
//        FactoryReceiptGroupModel bestOrder = null;
//        for (FactoryReceiptGroupModel receiptGroup: _receiptGroups) {
//            if (receiptGroup.isActive) {
//                for (FactoryReceiptModel receipt : _receipts) {
//                    if (bestOrder == null && receipt.receiptGroup == receiptGroup && receipt.enoughComponents) {
//                        bestOrder = receiptGroup;
//                    }
//                }
//            }
//        }
//        if (bestOrder == null) {
//            _message = "Missing components";
//            return false;
//        }
//
//        // Get receipt based on components distance
//        int bestDistance = Integer.MAX_VALUE;
//        _runningReceipt = null;
//        for (FactoryReceiptModel receipt: _receipts) {
//            if (receipt.enoughComponents && receipt.receiptGroup == bestOrder && receipt.totalDistance < bestDistance) {
//                _runningReceipt = receipt;
//            }
//        }
//        if (_runningReceipt == null) {
//            throw new RuntimeException("_runningReceipt cannot be null");
//        }
//
//        // Fill components list
//        _runningReceipt.prepare(potentials);
//
//        _message = "Refilling";
//
//        return true;
////        Log.info("total time: " + (System.currentTimeMillis() - time) + "ms");
        return false;
    }
}
