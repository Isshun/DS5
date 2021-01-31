package org.smallbox.faraway.game.itemFactory;

import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.game.modelInfo.ReceiptGroupInfo;
import org.smallbox.faraway.game.consumable.Consumable;

import java.util.List;

public class FactoryReceiptModel {

    public boolean isActive;

//    public boolean hasEnoughComponents() {
//        return _components.stream().allMatch(component -> component.currentQuantity >= component.totalQuantity);
//    }

    public static class FactoryShoppingItemModel {
        public Consumable consumable;
        public int              quantity;

        public FactoryShoppingItemModel(Consumable consumable, int quantity) {
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

        @Override
        public String toString() { return itemInfo + " " + currentQuantity + " / " + totalQuantity; }
    }

//    private List<FactoryComponentModel>         _components;
    private List<FactoryShoppingItemModel>      _shoppingList;
    private boolean                             _isFull;
    private double                              _costRemaining;

    public final ItemFactoryModel.FactoryReceiptGroupModel receiptGroup;
    public final ReceiptGroupInfo.ReceiptInfo   receiptInfo;
    public int                                  totalDistance;
    public boolean                              enoughComponents;

    public FactoryReceiptModel(ItemFactoryModel.FactoryReceiptGroupModel factoryReceiptGroup, ReceiptGroupInfo.ReceiptInfo receiptInfo) {
        this.receiptGroup = factoryReceiptGroup;
        this.receiptInfo = receiptInfo;
    }

//    public List<FactoryComponentModel>      getComponents() { return _components; }
    public List<FactoryShoppingItemModel>   getShoppingList() { return _shoppingList; }
    public boolean                          isFull() { return _isFull; }
    public double                           getCostRemaining() { return _costRemaining; }
    public int                              getCost() { return receiptInfo.cost; }
    public double                           setCostRemaining(int costRemaining) { _costRemaining = costRemaining; return _costRemaining; }

    public double craft(double value) {
        return _costRemaining -= value;
    }

    public void initComponents() {
        _costRemaining = receiptInfo.cost;
//        _components = receiptInfo.inputs.stream()
//                .map(receiptInputInfo -> new FactoryComponentModel(receiptInputInfo.item, receiptInputInfo.quantity))
//                .collect(Collectors.toList());
    }

//    public void setPotentialComponents(List<PotentialConsumable> potentialComponents) {
//        this.totalDistance = 0;
//        this.enoughComponents = true;
//
//        for (ReceiptGroupInfo.ReceiptInfo.ReceiptInputInfo inputInfo: this.receiptInfo.inputs) {
//            List<PotentialConsumable> potentials = potentialComponents.stream()
//                    .filter(potentialConsumable -> potentialConsumable.itemInfo.instanceOf(inputInfo.item))
//                    .sorted(Comparator.comparingInt(o -> o.distance))
//                    .collect(Collectors.toList());
//            int quantity = 0;
//            for (PotentialConsumable potential: potentials) {
//                if (quantity < inputInfo.quantity) {
//                    this.totalDistance += potential.distance;
//                    quantity += potential.consumable.getFreeQuantity();
//                }
//            }
//            if (quantity < inputInfo.quantity) {
//                this.enoughComponents = false;
//            }
//        }
//
//        Log.info("Set potential components for receipt entry: " + this.receiptInfo.label + " (distance: " + this.totalDistance + ", enough: " + this.enoughComponents + ")");
//    }

    public void clear() {
//        _components.clear();
        _shoppingList.clear();
    }

    public FactoryShoppingItemModel getNextInput() {
        return !_shoppingList.isEmpty() ? _shoppingList.get(0) : null;
    }

    @Override
    public String toString() { return receiptInfo.label + receiptInfo.inputs; }

}
