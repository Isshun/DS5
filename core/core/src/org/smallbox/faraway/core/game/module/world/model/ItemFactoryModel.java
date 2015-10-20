package org.smallbox.faraway.core.game.module.world.model;

import com.badlogic.gdx.ai.pfa.GraphPath;
import org.smallbox.faraway.core.PotentialConsumable;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.module.job.model.CraftJob;
import org.smallbox.faraway.core.module.java.ModuleHelper;
import org.smallbox.faraway.core.game.module.path.PathManager;
import org.smallbox.faraway.core.util.Utils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Alex on 15/10/2015.
 */
public class ItemFactoryModel {
    public static class OrderEntry {
        public final ReceiptGroupInfo               receiptGroupInfo;
        public int                                  mode;
        public boolean                              isActive;

        public OrderEntry(ReceiptGroupInfo receiptGroupInfo) {
            this.receiptGroupInfo = receiptGroupInfo;
        }
    }

    public static class FactoryShoppingItemModel {
        public ConsumableModel  consumable;
        public int              quantity;

        public FactoryShoppingItemModel(ConsumableModel consumable, int quantity) {
            this.consumable = consumable;
            this.quantity = quantity;
        }
    }

    public static class FactoryComponentModel {
        public ItemInfo         itemInfo;
        public int              currentQuantity;
        public int              totalQuantity;

        public FactoryComponentModel(ItemInfo itemInfo, int quantity) {
            this.itemInfo = itemInfo;
            this.totalQuantity = quantity;
        }
    }

    private final ItemModel                 _item;
    private CraftJob                        _job;
    private ItemFactoryReceiptModel         _currentReceipt;
    private List<OrderEntry>                _orderEntries;
    private List<ItemFactoryReceiptModel>   _receiptEntries;
    private List<FactoryComponentModel>     _components;
    private List<FactoryShoppingItemModel>  _shoppingList;
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
            _orderEntries = _info.receipts.stream().map(OrderEntry::new).collect(Collectors.toList());
        }
    }

    public void setJob(CraftJob job) { _job = job; }

    public CraftJob                         getJob() { return _job; }
    public List<OrderEntry>                 getOrders() { return _orderEntries; }
    public List<ItemFactoryReceiptModel>    getReceipts() { return _receiptEntries; }
    public List<FactoryComponentModel>      getComponents() { return _components; }
    public List<FactoryShoppingItemModel>   getShoppingList() { return _shoppingList; }
    public ReceiptGroupInfo                 getCurrentReceiptGroup() { return _currentReceipt != null ? _currentReceipt.receiptGroupInfo : null; }
    public ReceiptGroupInfo.ReceiptInfo     getCurrentReceiptInfo() { return _currentReceipt != null ? _currentReceipt.receiptInfo : null; }
    public ItemFactoryReceiptModel          getCurrentReceipt() { return _currentReceipt; }
    public ParcelModel                      getStorageParcel() { return _storageParcel; }
    public String                           getMessage() { return _message; }

    public int getQuantityNeeded(ItemInfo itemInfo) {
        if (_currentReceipt != null) {
            for (FactoryComponentModel component: _components) {
                if (itemInfo.instanceOf(component.itemInfo)) {
                    return component.totalQuantity - component.currentQuantity;
                }
            }
        }
        return 0;
    }

    public void moveReceipt(ReceiptGroupInfo receiptGroupInfo, int offset) {
        Optional<OrderEntry> optionalEntry = _orderEntries.stream().filter(entry -> entry.receiptGroupInfo == receiptGroupInfo).findFirst();
        if (optionalEntry.isPresent()) {
            int position = _orderEntries.indexOf(optionalEntry.get()) + offset;
            _orderEntries.remove(optionalEntry.get());
            _orderEntries.add(Math.min(Math.max(position, 0), _orderEntries.size()), optionalEntry.get());
        }
    }

    public void addComponent(ItemInfo itemInfo, int quantity) {
        if (_currentReceipt != null) {

            // Add current components to component list
            for (FactoryComponentModel component: _components) {
                if (itemInfo.instanceOf(component.itemInfo)) {
                    component.currentQuantity += quantity;
                    break;
                }
            }

            // Check if all components is present
            boolean allComponentsPresent = true;
            for (FactoryComponentModel component: _components) {
                if (component.currentQuantity < component.totalQuantity) {
                    allComponentsPresent = false;
                }
            }
            if (allComponentsPresent) {
                _message = _currentReceipt.receiptGroupInfo.label;
            }
        }
    }

    public void craft() {
        if (_currentReceipt != null) {
            _message = _currentReceipt.receiptGroupInfo.label;

            // Current item is done
            for (ReceiptGroupInfo.ReceiptOutputInfo productInfo : _currentReceipt.receiptInfo.outputs) {
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
                ModuleHelper.getWorldModule().putConsumable(parcel, productInfo.item, Utils.getRandom(productInfo.quantity));
            }

//            _currentReceipt.receiptInfo.outputs.forEach(productInfo -> _outputsList.add(new FactoryOutputModel(productInfo.item, productInfo.quantity)));

            _message = "Stand-by";
        }

        _components.clear();
        _shoppingList.clear();
    }

    public void clear() {
        _currentReceipt = null;
        _components.clear();
        _shoppingList.clear();
        _job = null;
    }

    public void scan() {
        System.out.println("scan");
        long time = System.currentTimeMillis();
        _currentReceipt = null;

        if (_storageParcel != null && _storageParcel.getConsumable() != null) {
            _message = "Factory is full";
            return;
        }

        // List components for all receipts
        Set <ItemInfo> allInputs = new HashSet<>();
        _receiptEntries = new ArrayList<>();
        for (OrderEntry order: _orderEntries) {
            if (order.isActive) {
                for (ReceiptGroupInfo.ReceiptInfo receipt : order.receiptGroupInfo.receipts) {
                    _receiptEntries.add(new ItemFactoryReceiptModel(order.receiptGroupInfo, receipt));
                    allInputs.addAll(receipt.inputs.stream().map(input -> input.item).collect(Collectors.toList()));
                }
            }
        }

        // Get distance for components list
        List<PotentialConsumable> componentsDistance = new ArrayList<>();
        for (ItemInfo inputInfo : allInputs) {
            ModuleHelper.getWorldModule().getConsumables().stream()
                    .filter(consumable -> consumable.getInfo().instanceOf(inputInfo))
                    .filter(consumable -> consumable.getParcel().isWalkable())
                    .forEach(consumable -> {
                        GraphPath<ParcelModel> path = PathManager.getInstance().getPath(_item.getParcel(), consumable.getParcel());
                        if (path != null) {
                            componentsDistance.add(new PotentialConsumable(consumable, path.getCount()));
                        }
                    });
        }
        Collections.sort(componentsDistance, (c1, c2) -> c2.distance - c1.distance);

        // Set current receipt based on components availability
        _receiptEntries.forEach(receiptEntry -> receiptEntry.setPotentialComponents(componentsDistance));
        Optional<ItemFactoryReceiptModel> optionalReceipt = _receiptEntries.stream().filter(r -> r.enoughComponents).sorted((r1, r2) -> r1.totalDistance - r2.totalDistance).findFirst();
        _currentReceipt = optionalReceipt.isPresent() ? optionalReceipt.get() : null;

        // Fill consumables order list
        if (_currentReceipt != null) {

            // Fill magics list
            _components = new ArrayList<>();
            _currentReceipt.receiptInfo.inputs.forEach(receiptInputInfo -> {
                _components.add(new FactoryComponentModel(receiptInputInfo.item, receiptInputInfo.quantity));
            });

            // Fill inputs consumable list
            _shoppingList = new ArrayList<>();
            _currentReceipt.receiptInfo.inputs.forEach(receiptInputInfo -> {
                int quantity = 0;
                for (PotentialConsumable potential: componentsDistance) {
                    if (potential.itemInfo.instanceOf(receiptInputInfo.item) && quantity < receiptInputInfo.quantity) {
                        int neededQuantity = Math.min(receiptInputInfo.quantity - quantity, potential.consumable.getQuantity());
                        _shoppingList.add(new FactoryShoppingItemModel(potential.consumable, neededQuantity));
                        quantity += neededQuantity;
                    }
                }
            });

            System.out.println("inputs list");
            _shoppingList.forEach(input -> {
                System.out.println(input.consumable.getInfo().label + " x" + input.quantity + " (" + input.consumable.getParcel().x + "x" + input.consumable.getParcel().y + ")");
            });

            _message = "Refilling";
        } else {
            _message = "Missing components";
        }

        System.out.println("total time: " + (System.currentTimeMillis() - time) + "ms");
    }

    public FactoryShoppingItemModel getNextInput() {
        return !_shoppingList.isEmpty() ? _shoppingList.get(0) : null;
    }

//    // Get potential consumables on WorldModule
//    private void scanComponents(List<ReceiptInfo.ReceiptProductComponentInfo> componentsInfo) {
//        if (componentsInfo != null) {
//            _componentsInfo = componentsInfo;
//
//            // Add components to receipt
//            Map<ItemInfo, Integer> componentsDistance = new HashMap<>();
//            for (ReceiptInfo.ReceiptProductComponentInfo componentInfo : componentsInfo) {
//                componentsDistance.put(componentInfo.item, componentInfo.quantity);
//            }
//
//            _potentialComponents.clear();
//            ModuleHelper.getWorldModule().getConsumables().stream()
//                    .filter(consumable -> componentsDistance.containsKey(consumable.getInfo()))
//                    .filter(consumable -> consumable.getParcel().isWalkable())
//                    .forEach(consumable -> {
//                        GraphPath<ParcelModel> path = PathManager.getInstance().getPath(_factory.getParcel(), consumable.getParcel());
//                        if (path != null) {
//                            _potentialComponents.add(new PotentialConsumable(consumable, path.getCount()));
//                        }
//                    });
//            Collections.sort(_potentialComponents, (c1, c2) -> c2.distance - c1.distance);
//        }
//    }

}
