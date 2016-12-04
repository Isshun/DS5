package org.smallbox.faraway.core.engine.module.lua.data.extend;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.engine.module.ModuleBase;
import org.smallbox.faraway.core.engine.module.lua.data.DataExtendException;
import org.smallbox.faraway.core.engine.module.lua.data.LuaExtend;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.module.world.model.ReceiptGroupInfo;

import java.io.File;
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
    public void extend(ModuleBase module, Globals globals, LuaValue value, File dataDirectory) throws DataExtendException {
        String name = getString(value, "name", null);
        ReceiptGroupInfo receiptGroupInfo = null;
        for (ReceiptGroupInfo info: Application.data.receipts) {
            if (info.name != null && info.name.equals(name)) {
                receiptGroupInfo = info;
            }
        }

        if (receiptGroupInfo == null) {
            receiptGroupInfo = new ReceiptGroupInfo();
            Application.data.add(name, receiptGroupInfo);
            Application.data.receipts.add(receiptGroupInfo);
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
                        readAsync(luaInput, "name", ItemInfo.class, itemInfo -> componentInfo.item = itemInfo);
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
                        readAsync(luaComponent, "name", ItemInfo.class, itemInfo -> productItemInfo.item = itemInfo);
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
