package org.smallbox.faraway.core.data;

import org.smallbox.faraway.core.game.model.item.ItemInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 12/10/2015.
 */
public class ReceiptGroupInfo {
    public static class ReceiptInputInfo {
        public String       itemName;
        public ItemInfo     item;
        public int          quantity;
    }

    public static class ReceiptOutputInfo {
        public String       itemName;
        public ItemInfo     item;
        public int          quantity;
    }

    public static class ReceiptInfo {
        public String                   name;
        public String                   label;
        public List<ReceiptOutputInfo>  products;
        public List<ReceiptInputInfo>   components;
    }

    public String                   name;
    public String                   label;
    public List<ReceiptInfo>        receipts;
}
