package org.smallbox.faraway.module.lua.data.extend;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.smallbox.faraway.data.ReceiptInfo;
import org.smallbox.faraway.game.model.GameData;
import org.smallbox.faraway.module.lua.DataExtendException;
import org.smallbox.faraway.module.lua.LuaModule;
import org.smallbox.faraway.module.lua.LuaModuleManager;
import org.smallbox.faraway.module.lua.data.LuaExtend;

/**
 * Created by Alex on 12/10/2015.
 */
public class LuaReceiptExtend extends LuaExtend {
    @Override
    public boolean accept(String type) {
        return "receipt".equals(type);
    }

    @Override
    public void extend(LuaModuleManager luaModuleManager, LuaModule module, Globals globals, LuaValue value) throws DataExtendException {
        String name = getString(value, "name", null);
        ReceiptInfo receiptInfo = null;
        for (ReceiptInfo info: GameData.getData().receipts) {
            if (info.name != null && info.name.equals(name)) {
                receiptInfo = info;
            }
        }

        if (receiptInfo == null) {
            receiptInfo = new ReceiptInfo();
            GameData.getData().receipts.add(receiptInfo);
        }

        receiptInfo.name = getString(value, "name", null);
        receiptInfo.label = getString(value, "label", null);

        LuaValue luaProducts = value.get("products");
        if (!luaProducts.isnil()) {
            for (int i = 1; i <= luaProducts.length(); i++) {
                LuaValue luaProduct = luaProducts.get(i);
                ReceiptInfo.ReceiptProductInfo productInfo = new ReceiptInfo.ReceiptProductInfo();

                LuaValue luaItemsProduct = luaProduct.get("items");
                for (int j = 1; j <= luaItemsProduct.length(); j++) {
                    LuaValue luaComponent = luaItemsProduct.get(j);
                    ReceiptInfo.ReceiptProductItemInfo productItemInfo = new ReceiptInfo.ReceiptProductItemInfo();
                    productItemInfo.itemName = luaComponent.get("name").toString();
                    productItemInfo.quantity = luaComponent.get("quantity").toint();
                    productInfo.products.add(productItemInfo);
                }

                LuaValue luaComponents = luaProduct.get("components");
                for (int j = 1; j <= luaComponents.length(); j++) {
                    LuaValue luaComponent = luaComponents.get(j);
                    ReceiptInfo.ReceiptProductComponentInfo componentInfo = new ReceiptInfo.ReceiptProductComponentInfo();
                    componentInfo.itemName = luaComponent.get("name").toString();
                    componentInfo.quantity = luaComponent.get("quantity").toint();
                    productInfo.components.add(componentInfo);
                }

                receiptInfo.products.add(productInfo);
            }
        }

        System.out.println("Extends receipt from lua: " + receiptInfo.label);
    }
}
