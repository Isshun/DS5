package org.smallbox.faraway.core.module.world.model;

import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.game.modelInfo.ObjectInfo;

import java.util.List;

/**
 * Created by Alex on 12/10/2015.
 */
public class ReceiptGroupInfo extends ObjectInfo {
    public static class ReceiptInputInfo {
        public String       itemName;
        public ItemInfo     item;
        public int          quantity;
    }

    public static class ReceiptOutputInfo {
        public String       itemName;
        public ItemInfo     item;
        public int[]        quantity;
    }

    public static class ReceiptInfo {
        public String                   name;
        public String                   label;
        public List<ReceiptOutputInfo>  outputs;
        public List<ReceiptInputInfo>   inputs;
        public int                      cost;
    }

    public int                      cost;
    public String                   label;
    public List<ReceiptInfo>        receipts;
}
