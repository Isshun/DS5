package org.smallbox.faraway.model;

import org.smallbox.faraway.model.item.ConsumableModel;
import org.smallbox.faraway.model.item.ItemInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 07/06/2015.
 */
public class ReceiptModel {
    public final ItemInfo.ItemInfoReceipt receiptInfo;

    public static class ReceiptComponentModel {
        public final ConsumableModel    item;
        public final ItemInfo           itemInfo;
        public int                      count;

        public ReceiptComponentModel(ConsumableModel item, int count) {
            this.item = item;
            this.itemInfo = item.getInfo();
            this.count = count;
        }
    }

    private List<ReceiptComponentModel> _components = new ArrayList<>();

    public ReceiptModel(ItemInfo.ItemInfoReceipt receiptInfo) {
        this.receiptInfo = receiptInfo;

        for (ItemInfo.ItemComponentInfo componentInfo: receiptInfo.components) {
            ConsumableModel component = new ConsumableModel(componentInfo.itemInfo);
            component.setQuantity(0);
            _components.add(new ReceiptComponentModel(component, componentInfo.quantity));
        }
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

    public boolean hasComponents() {
        for (ReceiptComponentModel component: _components) {
            if (component.item.getQuantity() < component.count) {
                return false;
            }
        }
        return true;
    }

    public void reset() {
        _components.clear();
    }

}
