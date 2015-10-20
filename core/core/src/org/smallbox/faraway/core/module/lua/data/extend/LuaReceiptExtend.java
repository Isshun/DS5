package org.smallbox.faraway.core.module.lua.data.extend;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.smallbox.faraway.core.data.ReceiptGroupInfo;
import org.smallbox.faraway.core.game.model.GameData;
import org.smallbox.faraway.core.module.lua.DataExtendException;
import org.smallbox.faraway.core.module.lua.LuaModule;
import org.smallbox.faraway.core.module.lua.LuaModuleManager;
import org.smallbox.faraway.core.module.lua.data.LuaExtend;

import java.util.ArrayList;

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
        ReceiptGroupInfo receiptGroupInfo = null;
        for (ReceiptGroupInfo info: GameData.getData().receipts) {
            if (info.name != null && info.name.equals(name)) {
                receiptGroupInfo = info;
            }
        }

        if (receiptGroupInfo == null) {
            receiptGroupInfo = new ReceiptGroupInfo();
            GameData.getData().receipts.add(receiptGroupInfo);
        }

        receiptGroupInfo.name = getString(value, "name", null);
        receiptGroupInfo.label = getString(value, "label", null);

        LuaValue luaProducts = value.get("products");
        if (!luaProducts.isnil()) {
            receiptGroupInfo.receipts = new ArrayList<>();
            for (int i = 1; i <= luaProducts.length(); i++) {
                LuaValue luaProduct = luaProducts.get(i);
                ReceiptGroupInfo.ReceiptInfo receiptInfo = new ReceiptGroupInfo.ReceiptInfo();
                receiptInfo.name = receiptGroupInfo.name;
                receiptInfo.label = receiptGroupInfo.label;

                LuaValue luaItemsProduct = luaProduct.get("items");
                receiptInfo.products = new ArrayList<>();
                for (int j = 1; j <= luaItemsProduct.length(); j++) {
                    LuaValue luaComponent = luaItemsProduct.get(j);
                    ReceiptGroupInfo.ReceiptOutputInfo productItemInfo = new ReceiptGroupInfo.ReceiptOutputInfo();
                    productItemInfo.itemName = luaComponent.get("name").toString();
                    productItemInfo.quantity = luaComponent.get("quantity").toint();
                    receiptInfo.products.add(productItemInfo);
                }

                LuaValue luaComponents = luaProduct.get("components");
                receiptInfo.components = new ArrayList<>();
                for (int j = 1; j <= luaComponents.length(); j++) {
                    LuaValue luaComponent = luaComponents.get(j);
                    ReceiptGroupInfo.ReceiptInputInfo componentInfo = new ReceiptGroupInfo.ReceiptInputInfo();
                    componentInfo.itemName = luaComponent.get("name").toString();
                    componentInfo.quantity = luaComponent.get("quantity").toint();
                    receiptInfo.components.add(componentInfo);
                }

                receiptGroupInfo.receipts.add(receiptInfo);
            }
        }

        System.out.println("Extends receipt from lua: " + receiptGroupInfo.label);
    }
}
