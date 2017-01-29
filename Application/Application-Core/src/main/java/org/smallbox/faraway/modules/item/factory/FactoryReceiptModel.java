package org.smallbox.faraway.modules.item.factory;

import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.game.modelInfo.ReceiptGroupInfo;
import org.smallbox.faraway.core.module.world.model.ConsumableItem;

import java.util.List;

/**
 * Created by Alex on 17/10/2015.
 */
public class FactoryReceiptModel {

    public boolean isActive;

//    public boolean hasEnoughComponents() {
//        return _components.stream().allMatch(component -> component.currentQuantity >= component.totalQuantity);
//    }

    public static class FactoryShoppingItemModel {
        public ConsumableItem consumable;
        public int              quantity;

        public FactoryShoppingItemModel(ConsumableItem consumable, int quantity) {
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
    private int                                 _costRemaining;

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
    public int                              getCostRemaining() { return _costRemaining; }
    public void                             setCostRemaining(int costRemaining) { _costRemaining = costRemaining; }

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
//                    quantity += potential.consumable.getQuantity();
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

//    public void addComponent(ItemInfo itemInfo, int quantity) {
//            // Add current components to component list
//            for (FactoryComponentModel component: _components) {
//                if (itemInfo.instanceOf(component.itemInfo)) {
//                    component.currentQuantity += quantity;
//                    break;
//                }
//            }
//
//            // Check if all components is present
//            _isFull = true;
//            for (FactoryComponentModel component: _components) {
//                if (component.currentQuantity < component.totalQuantity) {
//                    _isFull = false;
//                }
//            }
//    }

//    public void prepare(List<PotentialConsumable> potentials) {
//        _isFull = true;
//        _components = receiptInfo.inputs.stream()
//                .map(receiptInputInfo -> new FactoryComponentModel(receiptInputInfo.item, receiptInputInfo.quantity))
//                .collect(Collectors.toList());
//
////        // Fill consumables shopping list
////        _shoppingList = new ArrayList<>();
////        if (!_components.isEmpty()) {
////            _isFull = false;
////            receiptInfo.inputs.forEach(receiptInputInfo -> {
////                int quantity = 0;
////                for (PotentialConsumable potential: potentials) {
////                    if (potential.itemInfo.instanceOf(receiptInputInfo.item) && quantity < receiptInputInfo.quantity) {
////                        int neededQuantity = Math.min(receiptInputInfo.quantity - quantity, potential.consumable.getQuantity());
////                        _shoppingList.add(new FactoryShoppingItemModel(potential.consumable, neededQuantity));
////                        quantity += neededQuantity;
////                    }
////                }
////            });
////        }
//    }

    public boolean isComponentsAvailable(CraftJob job) {
        for (FactoryShoppingItemModel shoppingItem: _shoppingList) {
            if (shoppingItem.consumable.getJob() != null && shoppingItem.consumable.getJob() != job) {
                return false;
            }
            if (shoppingItem.consumable.getQuantity() < shoppingItem.quantity) {
                return false;
            }
        }
        return true;
    }

    public FactoryShoppingItemModel getNextInput() {
        return !_shoppingList.isEmpty() ? _shoppingList.get(0) : null;
    }

    @Override
    public String toString() { return receiptInfo.label; }

}
