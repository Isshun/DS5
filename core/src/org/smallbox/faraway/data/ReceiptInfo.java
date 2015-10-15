package org.smallbox.faraway.data;

import org.smallbox.faraway.game.model.item.ItemInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 12/10/2015.
 */
public class ReceiptInfo {
    public static class ReceiptProductComponentInfo {
        public String       itemName;
        public ItemInfo     item;
        public int          quantity;
    }

    public static class ReceiptProductItemInfo {
        public String       itemName;
        public ItemInfo     item;
        public int          quantity;
    }

    public static class ReceiptProductInfo {
        public List<ReceiptProductItemInfo> products = new ArrayList<>();
        public List<ReceiptProductComponentInfo> components = new ArrayList<>();
    }

    public String                   name;
    public String                   label;
    public List<ReceiptProductInfo> products = new ArrayList<>();
}
