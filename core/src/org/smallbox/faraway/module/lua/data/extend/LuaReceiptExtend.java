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
                productInfo.itemName = luaProduct.get("name").toString();

                LuaValue luaComponents = luaProduct.get("components");
                for (int j = 1; j < luaComponents.length(); j++) {
                    LuaValue luaComponent = luaComponents.get(j);
                    ReceiptInfo.ReceiptProductComponentInfo componentInfo = new ReceiptInfo.ReceiptProductComponentInfo();
                    componentInfo.itemName = luaComponent.get("name").toString();
                    componentInfo.quantity = luaComponent.get("quantity").toint();
                    productInfo.components.add(componentInfo);
                }

                LuaValue luaQuantity = luaProduct.get("quantity");
                if (!luaQuantity.isnil()) {
                    productInfo.quantity = new int[]{luaQuantity.get(1).toint(), luaQuantity.get(luaQuantity.length() == 2 ? 2 : 1).toint()};
                } else {
                    throw new DataExtendException(DataExtendException.Type.MANDATORY, "products.items.quantity");
                }
            }
        }

        System.out.println("Extends receipt from lua: " + receiptInfo.label);
    }
}
