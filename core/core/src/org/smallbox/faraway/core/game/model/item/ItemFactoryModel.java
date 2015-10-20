package org.smallbox.faraway.core.game.model.item;

import com.badlogic.gdx.ai.pfa.GraphPath;
import org.smallbox.faraway.core.PotentialConsumable;
import org.smallbox.faraway.core.data.ReceiptGroupInfo;
import org.smallbox.faraway.core.game.model.job.CraftJob;
import org.smallbox.faraway.core.game.module.ModuleHelper;
import org.smallbox.faraway.core.game.module.path.PathManager;

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

    public static class FactoryInputModel {
        public ConsumableModel  consumable;
        public int              quantity;

        public FactoryInputModel(ConsumableModel consumable, int quantity) {
            this.consumable = consumable;
            this.quantity = quantity;
        }
    }

    public static class FactoryMagicModel {
        public ItemInfo         itemInfo;
        public int              currentQuantity;
        public int              totalQuantity;

        public FactoryMagicModel(ItemInfo itemInfo, int quantity) {
            this.itemInfo = itemInfo;
            this.totalQuantity = quantity;
        }
    }

    public static class FactoryOutputModel {
        public ItemInfo     itemInfo;
        public int          quantity;

        public FactoryOutputModel(ItemInfo itemInfo, int quantity) {
            this.itemInfo = itemInfo;
            this.quantity = quantity;
        }
    }

    private ParcelModel                     _parcel;
    private CraftJob _job;
    private ItemFactoryReceiptModel         _currentReceipt;
    private List<OrderEntry>                _orderEntries;
    private List<ItemFactoryReceiptModel>   _receiptEntries;
    private List<FactoryMagicModel>         _magicsList;
    private List<FactoryInputModel>         _inputsList;
    private List<FactoryOutputModel>        _outputsList = new ArrayList<>();
    private final ItemInfo.ItemInfoFactory  _info;

    public ItemFactoryModel(ParcelModel parcel, ItemInfo.ItemInfoFactory factoryInfo) {
        _parcel = parcel;
        _info = factoryInfo;
        if (_info.receipts != null) {
            _orderEntries = _info.receipts.stream().map(OrderEntry::new).collect(Collectors.toList());
        }
    }

    public void setJob(CraftJob job) { _job = job; }
    public void setParcel(ParcelModel parcel) { _parcel = parcel; }

    public CraftJob getJob() { return _job; }
    public List<OrderEntry> getOrders() { return _orderEntries; }
    public List<ItemFactoryReceiptModel> getReceipts() { return _receiptEntries; }
    public List<FactoryMagicModel> getMagics() { return _magicsList; }
    public List<FactoryInputModel> getComponents() { return _inputsList; }
    public List<FactoryOutputModel> getProducts() { return _outputsList; }
    public ReceiptGroupInfo getCurrentReceiptGroup() { return _currentReceipt != null ? _currentReceipt.receiptGroupInfo : null; }
    public ReceiptGroupInfo.ReceiptInfo getCurrentReceiptInfo() { return _currentReceipt != null ? _currentReceipt.receiptInfo : null; }
    public ItemFactoryReceiptModel getCurrentReceipt() { return _currentReceipt; }

    public void addInput(ItemInfo itemInfo, int quantity) {
        for (FactoryMagicModel magic: _magicsList) {
            if (magic.itemInfo == itemInfo) {
                magic.currentQuantity += quantity;

                // Remove potential consumable from receipt if consumable has been depleted
                if (_currentReceipt != null) {
                    if (!_inputsList.isEmpty() && _inputsList.get(0).consumable.getQuantity() == 0) {
                        _inputsList.remove(0);
                    }
                }
                return;
            }
        }
    }

    public void craft() {
        if (_currentReceipt != null) {
            _currentReceipt.receiptInfo.products.forEach(productInfo -> _outputsList.add(new FactoryOutputModel(productInfo.item, productInfo.quantity)));
        }
        _magicsList.clear();
        _inputsList.clear();
    }

    public void scan() {
        System.out.println("scan");
        long time = System.currentTimeMillis();

        // List components for all receipts
        Set <ItemInfo> allInputs = new HashSet<>();
        _receiptEntries = new ArrayList<>();
        for (ReceiptGroupInfo receiptGroup: _info.receipts) {
            for (ReceiptGroupInfo.ReceiptInfo receipt: receiptGroup.receipts) {
                _receiptEntries.add(new ItemFactoryReceiptModel(receiptGroup, receipt));
                allInputs.addAll(receipt.components.stream().map(input -> input.item).collect(Collectors.toList()));
            }
        }

        // Get distance for components list
        List<PotentialConsumable> componentsDistance = new ArrayList<>();
        for (ItemInfo inputInfo : allInputs) {
            ModuleHelper.getWorldModule().getConsumables().stream()
                    .filter(consumable -> inputInfo == consumable.getInfo())
                    .filter(consumable -> consumable.getParcel().isWalkable())
                    .forEach(consumable -> {
                        GraphPath<ParcelModel> path = PathManager.getInstance().getPath(_parcel, consumable.getParcel());
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
            _magicsList = new ArrayList<>();
            _currentReceipt.receiptInfo.components.forEach(receiptInputInfo -> {
                _magicsList.add(new FactoryMagicModel(receiptInputInfo.item, receiptInputInfo.quantity));
            });

            // Fill inputs consumable list
            _inputsList = new ArrayList<>();
            _currentReceipt.receiptInfo.components.forEach(receiptInputInfo -> {
                int quantity = 0;
                for (PotentialConsumable potential: componentsDistance) {
                    if (quantity < receiptInputInfo.quantity) {
                        int neededQuantity = Math.min(receiptInputInfo.quantity - quantity, potential.consumable.getQuantity());
                        _inputsList.add(new FactoryInputModel(potential.consumable, neededQuantity));
                        quantity += neededQuantity;
                    }
                }
            });

            System.out.println("inputs list");
            _inputsList.forEach(input -> {
                System.out.println(input.consumable.getInfo().label + " x" + input.quantity + " (" + input.consumable.getParcel().x + "x" + input.consumable.getParcel().y + ")");
            });
        } else {
            System.out.println("no available receipt");
        }

        System.out.println("total time: " + (System.currentTimeMillis() - time) + "ms");
    }

    public FactoryInputModel getNextInput() {
        return !_inputsList.isEmpty() ? _inputsList.get(0) : null;
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
