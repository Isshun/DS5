package org.smallbox.faraway;

import org.smallbox.faraway.data.ReceiptInfo;
import org.smallbox.faraway.game.model.item.ItemInfo;
import org.smallbox.faraway.game.model.job.JobCraft;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Alex on 15/10/2015.
 */
public class ItemFactory {
    public static class ReceiptEntry {
        public ReceiptInfo  receiptInfo;
        public int          mode;
        public boolean      isActive;

        public ReceiptEntry(ReceiptInfo receiptInfo) {
            this.receiptInfo = receiptInfo;
        }
    }

    public static class ComponentEntry {
        public ItemInfo     itemInfo;
        public int          currentQuantity;
        public int          totalQuantity;

        public ComponentEntry(ItemInfo itemInfo, int totalQuantity) {
            this.itemInfo = itemInfo;
            this.totalQuantity = totalQuantity;
        }
    }

    public static class ProductEntry {
        public ItemInfo     itemInfo;
        public int          quantity;

        public ProductEntry(ItemInfo itemInfo, int quantity) {
            this.itemInfo = itemInfo;
            this.quantity = quantity;
        }
    }

    private JobCraft                        _job;
    private ReceiptInfo.ReceiptProductInfo  _bestReceiptProduct;
    private List<ReceiptEntry>              _receiptEntries;
    private List<ComponentEntry>            _componentsEntries;
    private List<ProductEntry>              _productsEntries = new ArrayList<>();

    public ItemFactory(ItemInfo.ItemInfoFactory factoryInfo) {
        if (factoryInfo.receipts != null) {
            _receiptEntries = factoryInfo.receipts.stream().map(ReceiptEntry::new).collect(Collectors.toList());
        }
    }

    public void setJob(JobCraft job) { _job = job; }
    public JobCraft getJob() { return _job; }
    public List<ReceiptEntry> getReceipts() { return _receiptEntries; }
    public List<ComponentEntry> getComponents() { return _componentsEntries; }
    public List<ProductEntry> getProducts() { return _productsEntries; }

    public void addProduct(ItemInfo itemInfo, int quantity) {
        for (ProductEntry productEntry: _productsEntries) {
            if (productEntry.itemInfo == itemInfo) {
                productEntry.quantity += quantity;
                return;
            }
        }

        _productsEntries.add(new ProductEntry(itemInfo, quantity));
    }

    public void scan() {
        System.out.print("scan");
        _bestReceiptProduct = _receiptEntries.get(0).receiptInfo.products.get(0);
        _componentsEntries = _bestReceiptProduct.components.stream().map(component -> new ComponentEntry(component.item, component.quantity)).collect(Collectors.toList());
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
