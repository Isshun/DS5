package org.smallbox.faraway.data;

import org.smallbox.faraway.game.model.item.ItemInfo;

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
        public List<ReceiptOutputInfo>  products = new ArrayList<>();
        public List<ReceiptInputInfo>   components = new ArrayList<>();
    }

    public String                   name;
    public String                   label;
    public List<ReceiptInfo>        receipts = new ArrayList<>();
}
