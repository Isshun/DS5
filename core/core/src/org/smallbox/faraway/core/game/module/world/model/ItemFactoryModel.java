package org.smallbox.faraway.core.game.module.world.model;

import com.badlogic.gdx.ai.pfa.GraphPath;
import org.smallbox.faraway.core.PotentialConsumable;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.module.character.model.PathModel;
import org.smallbox.faraway.core.game.module.job.model.CraftJob;
import org.smallbox.faraway.core.game.module.path.PathManager;
import org.smallbox.faraway.core.module.java.ModuleHelper;
import org.smallbox.faraway.core.util.Utils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Alex on 15/10/2015.
 */
public class ItemFactoryModel {
    public static class OrderEntry {
        public final ReceiptGroupInfo   receiptGroupInfo;
        public int                      mode;
        public boolean                  isActive;

        public OrderEntry(ReceiptGroupInfo receiptGroupInfo) {
            this.receiptGroupInfo = receiptGroupInfo;
        }
    }

    private final ItemModel                 _item;
    private CraftJob                        _job;
    private ItemFactoryReceiptModel         _activeReceipt;
    private List<OrderEntry>                _orderEntries;
    private List<ItemFactoryReceiptModel>   _receiptEntries;
    // TODO: file d'attente de sortie
//    private List<FactoryShoppingItemModel>  _shoppingList;
    private final ItemInfo.ItemInfoFactory  _info;
    private ParcelModel                     _storageParcel;
    private String                          _message;

    public ItemFactoryModel(ItemModel item, ItemInfo.ItemInfoFactory factoryInfo) {
        _item = item;
        _info = factoryInfo;

        _storageParcel = _item.getParcel();
        if (_info.outputSlots != null) {
            _storageParcel = WorldHelper.getParcel(_item.getParcel().x + _info.outputSlots[0], _item.getParcel().y + _info.outputSlots[1]);
        }

        if (_info.receipts != null) {
            // Create order from receiptInfo
            _orderEntries = _info.receipts.stream().map(OrderEntry::new).collect(Collectors.toList());

            // Create receipt model for each order
            _receiptEntries = new ArrayList<>();
            _orderEntries.forEach(order -> _receiptEntries.addAll(order.receiptGroupInfo.receipts.stream()
                    .map(receipt -> new ItemFactoryReceiptModel(order, receipt))
                    .collect(Collectors.toList())));
        }
    }

    public void setJob(CraftJob job) { _job = job; }
    public void setMessage(String message) { _message = message; }

    public CraftJob                         getJob() { return _job; }
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

    public void craft() {
        if (_activeReceipt != null) {
            _message = "Crafting";

            // Current item is done
            for (ReceiptGroupInfo.ReceiptOutputInfo productInfo : _activeReceipt.receiptInfo.outputs) {
//                ConsumableModel productConsumable = new ConsumableModel(productInfo.item);
//                productConsumable.setQuantity(Utils.getRandom(productInfo.quantity));

//                // Move to storage
//                StorageAreaModel bestStorage = ((AreaModule)ModuleManager.getInstance().getModule(AreaModule.class)).getBestStorage(productConsumable);
//                if (bestStorage != null && productConsumable.getStorage() != bestStorage) {
//                    ModuleHelper.getJobModule().addJob(StoreJob.create(productConsumable, bestStorage));
//
////                _factory.addProduct(productInfo.item, productInfo.quantity);
//
//                _storageParcel = ((AreaModule) ModuleManager.getInstance().getModule(AreaModule.class)).getNearestFreeStorageParcel(productConsumable, character.getParcel());
//                if (_storageParcel != null) {
//                    _storage = (StorageAreaModel)_storageParcel.getArea();
//                    character.setInventory(productConsumable);
//                    moveToStorage(character, _storageParcel);
//                    return JobActionReturn.CONTINUE;
//                } else {
//                }
                ParcelModel parcel = _item.getParcel();
                if (_item.getInfo().factory.outputSlots != null) {
                    parcel = WorldHelper.getParcel(
                            _item.getParcel().x + _item.getInfo().factory.outputSlots[0],
                            _item.getParcel().y + _item.getInfo().factory.outputSlots[1]);
                }
                System.out.println("Factory: put crafted consumable on " + parcel.x + "x" + parcel.y);
                ModuleHelper.getWorldModule().putConsumable(parcel, productInfo.item, Utils.getRandom(productInfo.quantity));
            }

//            _activeReceipt.receiptInfo.outputs.forEach(productInfo -> _outputsList.add(new FactoryOutputModel(productInfo.item, productInfo.quantity)));

            _message = "Stand-by";
            _activeReceipt.clear();
        }
    }

    public void clear() {
        if (_activeReceipt != null) {
            _activeReceipt.getShoppingList().forEach(component -> ModuleHelper.getWorldModule().putConsumable(_item.getParcel(), component.consumable));
            _activeReceipt.clear();
            _activeReceipt = null;
        }
        _job = null;
    }

    // TODO: ne pas utiliser les consomables lock�
    public void scan() {
//        System.out.println("scan");

        long time = System.currentTimeMillis();
        _activeReceipt = null;

        if (_storageParcel != null && _storageParcel.getConsumable() != null) {
            _message = "Factory is full";
            return;
        }

        // List itemInfo needed in all receipts
        Set <ItemInfo> allInputs = new HashSet<>();
        _receiptEntries.stream()
                .filter(receipt -> receipt.order.isActive)
                .forEach(receipt -> allInputs.addAll(receipt.receiptInfo.inputs.stream().map(inputInfo -> inputInfo.item).collect(Collectors.toList())));

        // Get distance for all of them
        List<PotentialConsumable> componentsDistance = new ArrayList<>();
        allInputs.forEach(inputInfo ->
                ModuleHelper.getWorldModule().getConsumables().stream()
                        .filter(consumable -> consumable.getInfo().instanceOf(inputInfo))
                        .filter(consumable -> consumable.getParcel().isWalkable())
                        .filter(consumable -> consumable.getLock() == null)
                        .forEach(consumable -> {
                            PathModel path = PathManager.getInstance().getPath(_item.getParcel(), consumable.getParcel());
                            if (path != null) {
                                componentsDistance.add(new PotentialConsumable(consumable, path.getLength()));
                            }
                        }));
        Collections.sort(componentsDistance, (c1, c2) -> c2.distance - c1.distance);

        // For each receipt, find total distance between factory and components
        _receiptEntries.stream().filter(receipt -> receipt.order.isActive).forEach(receiptEntry -> receiptEntry.setPotentialComponents(componentsDistance));

        // Get receipt group based on components availability
        OrderEntry bestOrder = null;
        for (OrderEntry order: _orderEntries) {
            if (order.isActive) {
                for (ItemFactoryReceiptModel receipt : _receiptEntries) {
                    if (bestOrder == null && receipt.order == order && receipt.enoughComponents) {
                        bestOrder = order;
                    }
                }
            }
        }
        if (bestOrder == null) {
            _message = "Missing components";
            return;
        }

        // Get receipt based on components distance
        int bestDistance = Integer.MAX_VALUE;
        _activeReceipt = null;
        for (ItemFactoryReceiptModel receipt: _receiptEntries) {
            if (receipt.enoughComponents && receipt.order == bestOrder && receipt.totalDistance < bestDistance) {
                _activeReceipt = receipt;
            }
        }
        if (_activeReceipt == null) {
            throw new RuntimeException("_activeReceipt cannot be null");
        }

        // Fill components list
        _activeReceipt.prepare(componentsDistance);

        _message = "Refilling";

//        System.out.println("total time: " + (System.currentTimeMillis() - time) + "ms");
    }
}
