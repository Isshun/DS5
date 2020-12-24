package org.smallbox.faraway.core.game.modelInfo;

import java.util.List;

public class ReceiptGroupInfo extends ObjectInfo {

    public static class ReceiptInfo {

        public static class ReceiptInputInfo {
            public ItemInfo     item;
            public int          quantity;

            @Override
            public String toString() { return item + " x" + quantity; }
        }

        public static class ReceiptOutputInfo {
            public ItemInfo     item;
            public int[]        quantity;
        }

        public String                   name;
        public String                   label;
        public List<ReceiptOutputInfo>  outputs;
        public List<ReceiptInputInfo>   inputs;
        public int                      cost;
        public String                   icon;
    }

    public int                      cost;
    public String                   label;
    public List<ReceiptInfo>        receipts;
}
