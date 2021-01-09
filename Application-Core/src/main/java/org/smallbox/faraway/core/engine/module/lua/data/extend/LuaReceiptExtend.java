package org.smallbox.faraway.core.engine.module.lua.data.extend;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.engine.module.ModuleBase;
import org.smallbox.faraway.core.engine.module.lua.data.DataExtendException;
import org.smallbox.faraway.core.engine.module.lua.data.LuaExtend;
import org.smallbox.faraway.core.game.Data;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.game.modelInfo.ReceiptGroupInfo;

import java.io.File;
import java.util.ArrayList;

@ApplicationObject
public class LuaReceiptExtend extends LuaExtend {

    @Override
    public boolean accept(String type) {
        return "receipt".equals(type);
    }

    @Override
    public void extend(Data data, ModuleBase module, Globals globals, LuaValue value, File dataDirectory) throws DataExtendException {
        String name = getString(value, "name", null);
        ReceiptGroupInfo receiptGroupInfo = new ReceiptGroupInfo();

        data.add(name, receiptGroupInfo);
        data.receipts.add(receiptGroupInfo);

        readString(value, "name", v -> receiptGroupInfo.name = v);
        readString(value, "label", v -> receiptGroupInfo.label = v);
        readInt(value, "cost", v -> receiptGroupInfo.cost = v, 1);

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
                        ReceiptGroupInfo.ReceiptInfo.ReceiptInputInfo componentInfo = new ReceiptGroupInfo.ReceiptInfo.ReceiptInputInfo();
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
                        ReceiptGroupInfo.ReceiptInfo.ReceiptOutputInfo productItemInfo = new ReceiptGroupInfo.ReceiptInfo.ReceiptOutputInfo();
                        readAsync(luaComponent, "name", ItemInfo.class, itemInfo -> productItemInfo.item = itemInfo);
                        if (luaComponent.get("quantity").istable()) {
                            productItemInfo.quantity = new int[] {
                                    luaComponent.get("quantity").get(1).toint(),
                                    luaComponent.get("quantity").get(2).toint()
                            };
                        } else {
                            productItemInfo.quantity = new int[] {
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
