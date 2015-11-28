package org.smallbox.faraway.core.engine.module.lua.data.extend;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.smallbox.faraway.core.engine.module.lua.LuaModule;
import org.smallbox.faraway.core.engine.module.lua.LuaModuleManager;
import org.smallbox.faraway.core.engine.module.lua.data.DataExtendException;
import org.smallbox.faraway.core.engine.module.lua.data.LuaExtend;
import org.smallbox.faraway.core.game.Data;
import org.smallbox.faraway.core.game.module.world.model.ReceiptGroupInfo;

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
        for (ReceiptGroupInfo info: Data.getData().receipts) {
            if (info.name != null && info.name.equals(name)) {
                receiptGroupInfo = info;
            }
        }

        if (receiptGroupInfo == null) {
            receiptGroupInfo = new ReceiptGroupInfo();
            Data.getData().receipts.add(receiptGroupInfo);
        }

        receiptGroupInfo.name = getString(value, "name", null);
        receiptGroupInfo.label = getString(value, "label", null);
        receiptGroupInfo.cost = getInt(value, "cost", 1);

        LuaValue luaReceipts = value.get("receipts");
        if (!luaReceipts.isnil()) {
            receiptGroupInfo.receipts = new ArrayList<>();
            for (int i = 1; i <= luaReceipts.length(); i++) {
                LuaValue luaReceipt = luaReceipts.get(i);
                ReceiptGroupInfo.ReceiptInfo receiptInfo = new ReceiptGroupInfo.ReceiptInfo();
                receiptInfo.name = receiptGroupInfo.name;
                receiptInfo.label = receiptGroupInfo.label;
                receiptInfo.cost = getInt(luaReceipt, "cost", receiptGroupInfo.cost);

                receiptInfo.inputs = new ArrayList<>();
                if (!luaReceipt.get("inputs").isnil()) {
                    LuaValue luaInputs = luaReceipt.get("inputs");
                    for (int j = 1; j <= luaInputs.length(); j++) {
                        LuaValue luaInput = luaInputs.get(j);
                        ReceiptGroupInfo.ReceiptInputInfo componentInfo = new ReceiptGroupInfo.ReceiptInputInfo();
                        componentInfo.itemName = luaInput.get("name").toString();
                        componentInfo.quantity = luaInput.get("quantity").toint();
                        receiptInfo.inputs.add(componentInfo);
                    }
                }

                receiptInfo.outputs = new ArrayList<>();
                if (!luaReceipt.get("outputs").isnil()) {
                    LuaValue luaOutputs = luaReceipt.get("outputs");
                    for (int j = 1; j <= luaOutputs.length(); j++) {
                        LuaValue luaComponent = luaOutputs.get(j);
                        ReceiptGroupInfo.ReceiptOutputInfo productItemInfo = new ReceiptGroupInfo.ReceiptOutputInfo();
                        productItemInfo.itemName = luaComponent.get("name").toString();
                        if (luaComponent.get("quantity").istable()) {
                            productItemInfo.quantity = new int[]{
                                    luaComponent.get("quantity").get(1).toint(),
                                    luaComponent.get("quantity").get(2).toint()
                            };
                        } else {
                            productItemInfo.quantity = new int[]{
                                    luaComponent.get("quantity").toint(),
                                    luaComponent.get("quantity").toint()
                            };
                        }
                        receiptInfo.outputs.add(productItemInfo);
                    }
                }

                receiptGroupInfo.receipts.add(receiptInfo);
            }
        }

//        Log.info("Extends receipt from lua: " + receiptGroupInfo.label);
    }
}
