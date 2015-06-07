package org.smallbox.faraway.model;

import org.smallbox.faraway.model.item.ConsumableItem;
import org.smallbox.faraway.model.item.ItemInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 07/06/2015.
 */
public class ReceiptModel {
    public static class ReceiptComponentModel {
        public final ConsumableItem    item;
        public final ItemInfo          itemInfo;
        public final int               count;

        public ReceiptComponentModel(ConsumableItem item, int count) {
            this.item = item;
            this.itemInfo = item.getInfo();
            this.count = count;
        }
    }

    private List<ReceiptComponentModel> _components = new ArrayList<>();

    public void addReceiptComponent(ItemInfo itemInfo, int count) {
        ConsumableItem component = new ConsumableItem(itemInfo);
        component.setQuantity(0);
        _components.add(new ReceiptComponentModel(component, count));
    }

    public void addComponent(ItemInfo itemInfo, int count) {
        _components.stream().filter(component -> component.itemInfo == itemInfo).forEach(component -> {
            component.item.addQuantity(count);
        });
    }

    public List<ReceiptComponentModel> getComponents() {
        return _components;
    }

    public ReceiptComponentModel getComponent(ItemInfo info) {
        for (ReceiptComponentModel component: _components) {
            if (component.itemInfo == info) {
                return component;
            }
        }
        return null;
    }

    public boolean isComplete() {
        for (ReceiptComponentModel component: _components) {
            if (component.item.getQuantity() < component.count) {
                return false;
            }
        }
        return true;
    }

    public void reset() {
        for (ReceiptComponentModel component: _components) {
            component.item.setQuantity(0);
        }
    }

}
