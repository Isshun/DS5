package org.smallbox.faraway.module.item;

import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.game.module.job.model.abs.JobModel;
import org.smallbox.faraway.core.game.module.world.model.ConsumableModel;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.core.game.module.world.model.ReceiptGroupInfo;
import org.smallbox.faraway.core.util.Log;
import org.smallbox.faraway.module.consumable.ConsumableModule;
import org.smallbox.faraway.module.consumable.ConsumableStackModel;
import org.smallbox.faraway.module.item.item.ItemFactoryReceiptModel;
import org.smallbox.faraway.module.item.item.ItemModel;
import org.smallbox.faraway.module.job.JobModule;

import java.util.*;

import static org.smallbox.faraway.core.game.modelInfo.ItemInfo.*;

/**
 * Created by Alex on 15/10/2015.
 */
public class ItemFactoryModel {
    private Map<ItemInfo, ConsumableStackModel>     _inventory = new HashMap<>();
    private final ItemModel                         _item;
    private Map<ItemInfoAction, CraftJob>           _craftJobs = new HashMap<>();
    private ItemFactoryReceiptModel _activeReceipt;
    private List<OrderEntry>                _orderEntries;
    private List<ItemFactoryReceiptModel>   _receiptEntries;
    // TODO: file d'attente de sortie
//    private List<FactoryShoppingItemModel>  _shoppingList;
    private final ItemInfo _info;
    private ParcelModel _storageParcel;
    private String                          _message;

    public ItemFactoryModel(ItemModel item) {
        _item = item;
        _info = item.getInfo();

        _info.actions.stream()
                .filter(action -> action.type == ItemInfoAction.ActionType.CRAFT)
                .forEach(action -> _craftJobs.put(action, null));

//        ParcelModel itemParcel = _item.getParcel();
//        _storageParcel = _item.getParcel();
//        if (_info.outputSlots != null) {
//            _storageParcel = WorldHelper.getParcel(
//                    itemParcel.x + _info.outputSlots[0],
//                    itemParcel.y + _info.outputSlots[1],
//                    itemParcel.z);
//        }
//
//        if (_info.receipts != null) {
//            // Create order from receiptInfo
//            _orderEntries = _info.receipts.stream().map(OrderEntry::new).collect(Collectors.toList());
//
//            // Create receipt org.smallbox.faraway.core.game.module.room.model for each order
//            _receiptEntries = new ArrayList<>();
//            _orderEntries.forEach(order -> _receiptEntries.addAll(order.receiptGroupInfo.receipts.stream()
//                    .map(receipt -> new ItemFactoryReceiptModel(order, receipt))
//                    .collect(Collectors.toList())));
//        }
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

    public static class OrderEntry {
        public final ReceiptGroupInfo   receiptGroupInfo;
        public final FactoryOutputMode  output;
        public final boolean            auto;
        public final int                cost;
        public int                      mode;
        public boolean                  isActive;

        // Rename this mess
        public OrderEntry(FactoryGroupReceiptInfo receiptGroupInfo) {
            this.receiptGroupInfo = receiptGroupInfo.receipt;
            this.auto = receiptGroupInfo.auto;
            this.cost = receiptGroupInfo.cost;
            this.output = receiptGroupInfo.output;
        }
    }

    public void setMessage(String message) { _message = message; }

    public List<OrderEntry>                 getOrders() { return _orderEntries; }
    public List<ItemFactoryReceiptModel>    getReceipts() { return _receiptEntries; }
    public ReceiptGroupInfo                 getCurrentReceiptGroup() { return _activeReceipt != null ? _activeReceipt.receiptGroupInfo : null; }
    public ReceiptGroupInfo.ReceiptInfo     getCurrentReceiptInfo() { return _activeReceipt != null ? _activeReceipt.receiptInfo : null; }
    public ItemFactoryReceiptModel          getActiveReceipt() { return _activeReceipt; }
    public ParcelModel                      getStorageParcel() { return _storageParcel; }
    public String                           getMessage() { return _message; }

    public void moveReceipt(ReceiptGroupInfo receiptGroupInfo, int offset) {
        Optional<OrderEntry> optionalEntry = _orderEntries.stream().filter(entry -> entry.receiptGroupInfo == receiptGroupInfo).findFirst();
        if (optionalEntry.isPresent()) {
            int position = _orderEntries.indexOf(optionalEntry.get()) + offset;
            _orderEntries.remove(optionalEntry.get());
            _orderEntries.add(Math.min(Math.max(position, 0), _orderEntries.size()), optionalEntry.get());
        }
    }

    public void clear() {
//        throw new NotImplementedException("");

//        _message = "Stand-by";
//
//        if (_activeReceipt != null) {
//            _activeReceipt.getShoppingList().forEach(component -> ModuleHelper.getWorldModule().putConsumable(_item.getParcel(), component.consumable));
//            _activeReceipt.clear();
//            _activeReceipt = null;
//        }
//
//        _job = null;
    }

    // TODO: Do not use with locked consumables
    public boolean scan(ConsumableModule consumableModule) {
////        Log.info("scan");
//
//        long time = System.currentTimeMillis();
//        _activeReceipt = null;
//
//        if (_storageParcel != null && _storageParcel.getConsumable() != null) {
//            _message = "Factory is full";
//            return false;
//        }
//
//        // List itemInfo needed in all receipts
//        Set <ItemInfo> allInputs = new HashSet<>();
//        _receiptEntries.stream()
//                .filter(receipt -> receipt.order.isActive)
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
//        _receiptEntries.stream().filter(receipt -> receipt.order.isActive).forEach(receiptEntry -> receiptEntry.setPotentialComponents(potentials));
//
//        // Get receipt group based on components availability
//        OrderEntry bestOrder = null;
//        for (OrderEntry order: _orderEntries) {
//            if (order.isActive) {
//                for (ItemFactoryReceiptModel receipt : _receiptEntries) {
//                    if (bestOrder == null && receipt.order == order && receipt.enoughComponents) {
//                        bestOrder = order;
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
//        _activeReceipt = null;
//        for (ItemFactoryReceiptModel receipt: _receiptEntries) {
//            if (receipt.enoughComponents && receipt.order == bestOrder && receipt.totalDistance < bestDistance) {
//                _activeReceipt = receipt;
//            }
//        }
//        if (_activeReceipt == null) {
//            throw new RuntimeException("_activeReceipt cannot be null");
//        }
//
//        // Fill components list
//        _activeReceipt.prepare(potentials);
//
//        _message = "Refilling";
//
//        return true;
////        Log.info("total time: " + (System.currentTimeMillis() - time) + "ms");
        return false;
    }
}
