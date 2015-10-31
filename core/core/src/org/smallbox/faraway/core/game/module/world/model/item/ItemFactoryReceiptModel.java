package org.smallbox.faraway.core.game.module.world.model.item;

import org.smallbox.faraway.core.PotentialConsumable;
import org.smallbox.faraway.core.game.module.world.model.ConsumableModel;
import org.smallbox.faraway.core.data.ItemInfo;
import org.smallbox.faraway.core.game.module.world.model.ReceiptGroupInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Alex on 17/10/2015.
 */
public class ItemFactoryReceiptModel {
    public static class FactoryShoppingItemModel {
        public ConsumableModel consumable;
        public int              quantity;

        public FactoryShoppingItemModel(ConsumableModel consumable, int quantity) {
            this.consumable = consumable;
            this.quantity = quantity;
        }
    }

    public static class FactoryComponentModel {
        public ItemInfo itemInfo;
        public int              currentQuantity;
        public int              totalQuantity;

        public FactoryComponentModel(ItemInfo itemInfo, int quantity) {
            this.itemInfo = itemInfo;
            this.totalQuantity = quantity;
        }
    }

    private List<FactoryComponentModel>         _components;
    private List<FactoryShoppingItemModel>      _shoppingList;
    private boolean                             _allComponentsPresent;

    public final ItemFactoryModel.OrderEntry    order;
    public final ReceiptGroupInfo.ReceiptInfo receiptInfo;
    public final ReceiptGroupInfo               receiptGroupInfo;
    public int                                  totalDistance;
    public boolean                              enoughComponents;

    public ItemFactoryReceiptModel(ItemFactoryModel.OrderEntry order, ReceiptGroupInfo.ReceiptInfo receiptInfo) {
        this.order = order;
        this.receiptGroupInfo = order.receiptGroupInfo;
        this.receiptInfo = receiptInfo;
    }

    public List<FactoryComponentModel>      getComponents() { return _components; }
    public List<FactoryShoppingItemModel>   getShoppingList() { return _shoppingList; }

    public void setPotentialComponents(List<PotentialConsumable> potentialComponents) {
        this.totalDistance = 0;
        this.enoughComponents = true;

        for (ReceiptGroupInfo.ReceiptInputInfo inputInfo: this.receiptInfo.inputs) {
            List<PotentialConsumable> potentials = potentialComponents.stream()
                    .filter(potentialConsumable -> potentialConsumable.itemInfo.instanceOf(inputInfo.item))
                    .sorted((o1, o2) -> o1.distance - o2.distance)
                    .collect(Collectors.toList());
            int quantity = 0;
            for (PotentialConsumable potential: potentials) {
                if (quantity < inputInfo.quantity) {
                    this.totalDistance += potential.distance;
                    quantity += potential.consumable.getQuantity();
                }
            }
            if (quantity < inputInfo.quantity) {
                this.enoughComponents = false;
            }
        }

        System.out.println("Set potential components for receipt entry: " + this.receiptInfo.label + " (distance: " + this.totalDistance + ", enough: " + this.enoughComponents + ")");
    }

    public void clear() {
        _components.clear();
        _shoppingList.clear();
    }

    public int getQuantityNeeded(ItemInfo itemInfo) {
        for (FactoryComponentModel component: _components) {
            if (itemInfo.instanceOf(component.itemInfo)) {
                return component.totalQuantity - component.currentQuantity;
            }
        }
        return 0;
    }

    public void addComponent(ItemInfo itemInfo, int quantity) {
            // Add current components to component list
            for (FactoryComponentModel component: _components) {
                if (itemInfo.instanceOf(component.itemInfo)) {
                    component.currentQuantity += quantity;
                    break;
                }
            }

            // Check if all components is present
            _allComponentsPresent = true;
            for (FactoryComponentModel component: _components) {
                if (component.currentQuantity < component.totalQuantity) {
                    _allComponentsPresent = false;
                }
            }
    }

    public void prepare(List<PotentialConsumable> componentsDistance) {
        _components = receiptInfo.inputs.stream()
                .map(receiptInputInfo -> new FactoryComponentModel(receiptInputInfo.item, receiptInputInfo.quantity))
                .collect(Collectors.toList());

        // Fill consumables shopping list
        _shoppingList = new ArrayList<>();
        receiptInfo.inputs.forEach(receiptInputInfo -> {
            int quantity = 0;
            for (PotentialConsumable potential: componentsDistance) {
                if (potential.itemInfo.instanceOf(receiptInputInfo.item) && quantity < receiptInputInfo.quantity) {
                    int neededQuantity = Math.min(receiptInputInfo.quantity - quantity, potential.consumable.getQuantity());
                    _shoppingList.add(new FactoryShoppingItemModel(potential.consumable, neededQuantity));
                    quantity += neededQuantity;
                }
            }
        });

        System.out.println("shopping list");
        _shoppingList.forEach(input -> {
            System.out.println(input.consumable.getInfo().label + " x" + input.quantity + " (" + input.consumable.getParcel().x + "x" + input.consumable.getParcel().y + ")");
        });
    }

    public FactoryShoppingItemModel getNextInput() {
        return !_shoppingList.isEmpty() ? _shoppingList.get(0) : null;
    }

}
