package org.smallbox.faraway.modules.itemFactory;

import org.smallbox.faraway.common.ObjectModel;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.game.modelInfo.ReceiptGroupInfo;
import org.smallbox.faraway.core.module.world.model.ConsumableItem;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.consumable.ConsumableModule;
import org.smallbox.faraway.modules.item.UsableItem;
import org.smallbox.faraway.modules.job.JobModel;
import org.smallbox.faraway.util.log.Log;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static org.smallbox.faraway.core.game.modelInfo.ItemInfo.FactoryOutputMode;
import static org.smallbox.faraway.core.game.modelInfo.ItemInfo.ItemInfoFactory;

public class ItemFactoryModel {
    private final UsableItem _item;
    private FactoryReceiptModel _runningReceipt;
    private List<FactoryReceiptGroupModel> _receiptGroups;
    private List<FactoryReceiptModel> _receipts;
    // TODO: file d'attente de sortie
//    private List<FactoryShoppingItemModel>  _shoppingList;
    private final ItemInfoFactory _factoryInfo;
    private ParcelModel _storageParcel;
    private String _message;
    private JobModel _craftJob;
    private int _costRemaining;
    private Map<ItemInfo, Boolean> acceptedComponents = new ConcurrentHashMap<>();

    public Map<ItemInfo, Boolean> getAcceptedComponents() {
        return acceptedComponents;
    }

    public JobModel getCraftJob() { return _craftJob; }

    public void setCraftJob(JobModel craftJob) { _craftJob = craftJob; }

    public boolean hasRunningReceipt() {
        return _runningReceipt != null;
    }

    public ItemFactoryModel(UsableItem item) {
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
                    .map(receiptGroupInfo -> new FactoryReceiptGroupModel(this, receiptGroupInfo))
                    .collect(Collectors.toList());

            // Create receipt from each receipt group
            _receipts = _receiptGroups.stream()
                    .flatMap(receiptGroup -> receiptGroup.receiptGroupInfo.receipts.stream().map(receiptInfo -> new FactoryReceiptModel(receiptGroup, receiptInfo)))
                    .collect(Collectors.toList());

            List<ItemInfo> acceptedItems = _receipts.stream()
                    .flatMap(receipt -> receipt.receiptInfo.inputs.stream())
                    .map(receiptInputInfo -> receiptInputInfo.item)
                    .distinct()
                    .collect(Collectors.toList());

            Application.data.consumables.stream()
                    .filter(itemInfo -> acceptedItems.stream().anyMatch(itemInfo::instanceOf))
                    .forEach(itemInfo -> acceptedComponents.put(itemInfo, true));

        }
    }

    /**
     * En se basant sur la recette actuelle retourne la quantité restant à fournir pour le consomable passé en paramètre
     * @param itemInfo
     * @return
     */
    public int getQuantityNeeded(ItemInfo itemInfo) {
        if (_runningReceipt == null) {
            throw new RuntimeException("Cannot call method if factory has no running receipt");
        }

        int neededQuantity = _runningReceipt.receiptInfo.inputs.stream()
                .filter(inputInfo -> itemInfo.instanceOf(inputInfo.item))
                .mapToInt(inputInfo -> inputInfo.quantity)
                .sum();

        int currentQuantity = getCurrentQuantity(itemInfo);

        return neededQuantity - currentQuantity;
    }

    /**
     * Retourne la quantity d'un consomable présent dans l'inventaire
     * @param itemInfo
     * @return
     */
    public int getCurrentQuantity(ItemInfo itemInfo) {
        if (_runningReceipt == null) {
            throw new RuntimeException(("Cannot call method if factory has no running receipt"));
        }

        return _item.getInventory().stream()
                .filter(consumable -> consumable.getInfo().instanceOf(itemInfo))
                .mapToInt(ConsumableItem::getFreeQuantity)
                .sum();
    }

    public void setRunningReceipt(FactoryReceiptModel receipt) {
        _runningReceipt = receipt;

        if (_runningReceipt != null) {
            _runningReceipt.initComponents();
        }
    }

    /**
     * Vérifie si la quantité de composants présent dans l'inventaire est suffisant pour lancer la construction
     *
     * @return true si la quantité est suffisante
     */
    public boolean hasEnoughComponents() {
        if (_runningReceipt == null) {
            throw new RuntimeException("Cannot call method if factory has no running receipt");
        }

        for (ReceiptGroupInfo.ReceiptInfo.ReceiptInputInfo input: _runningReceipt.receiptInfo.inputs) {
            if (getQuantityNeeded(input.item) > 0) {
                return false;
            }
        }

        return true;
    }

    public void setAcceptedComponents(ItemInfo itemInfo, boolean accepted) {
        acceptedComponents.put(itemInfo, accepted);
    }

    public static class FactoryReceiptGroupModel extends ObjectModel {
        public final ReceiptGroupInfo   receiptGroupInfo;
        public final FactoryOutputMode  outputMode;
        public final boolean            auto;
        public final int                cost;
        public final ItemFactoryModel   factory;
        public int                      mode;
        public boolean                  isActive;

        // Rename this mess
        public FactoryReceiptGroupModel(ItemFactoryModel factory, ReceiptGroupInfo receiptGroupInfo) {
            this.factory = factory;
            this.receiptGroupInfo = receiptGroupInfo;
            this.auto = false;
            this.cost = receiptGroupInfo.cost;
            this.outputMode = FactoryOutputMode.GROUND;
        }
    }

    public void setMessage(String message) {
        Log.debug("[Factory] %s -> %s", _item, message);
        _message = message;
    }

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
