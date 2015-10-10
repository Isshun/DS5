package org.smallbox.faraway.module.lua.data.extend;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.smallbox.faraway.module.lua.data.LuaExtendInterface;
import org.smallbox.faraway.module.lua.LuaModule;
import org.smallbox.faraway.module.lua.LuaModuleManager;
import org.smallbox.faraway.game.model.item.ItemInfo;

import java.util.ArrayList;

/**
 * Created by Alex on 29/09/2015.
 */
public class LuaItemExtend implements LuaExtendInterface {
    @Override
    public boolean accept(String type) {
        switch (type) {
            case "item":
            case "resource":
            case "structure":
            case "consumable":
                return true;
        }
        return false;
    }

    @Override
    public void extend(LuaModuleManager luaModuleManager, LuaModule module, Globals globals, LuaValue value) {
        ItemInfo itemInfo = new ItemInfo();

        itemInfo.name = getString(value, "name", null);
        itemInfo.label = getString(value, "label", null);
        itemInfo.category = getString(value, "category", null);

        if (!value.get("size").isnil()) {
            itemInfo.width = value.get("size").get(1).toint();
            itemInfo.height = value.get("size").get(2).toint();
        } else {
            itemInfo.width = 1;
            itemInfo.height = 1;
        }

        if (!value.get("plant").isnil()) {
            readPlantValues(itemInfo, value.get("plant"));
        }

        if (!value.get("actions").isnil()) {
            itemInfo.actions = new ArrayList<>();
            for (int i = 1; i <= value.get("actions").length(); i++) {
                readActionValue(itemInfo, value.get("actions").get(i));
            }
        }

        System.out.println("Extends item from lua: " + itemInfo.label);
    }

    private void readActionValue(ItemInfo itemInfo, LuaValue value) {
        ItemInfo.ItemInfoAction action = new ItemInfo.ItemInfoAction();
        action.type = getString(value, "type", null);
        action.cost = getInt(value, "cost", 0);

        LuaValue luaProducts = value.get("products");
        if (!luaProducts.isnil()) {
            action.products = new ArrayList<>();
            for (int i = 1; i <= luaProducts.length(); i++) {
                ItemInfo.ItemProductInfo product = new ItemInfo.ItemProductInfo();
                product.item = getString(luaProducts.get(i), "item", null);
                product.quantity = new int[] {
                        luaProducts.get(i).get("quantity").get(1).toint(),
                        luaProducts.get(i).get("quantity").get(2).toint()
                };
                product.dropRate = getDouble(luaProducts.get(i), "rate", 1);
                action.products.add(product);
            }
        }

        itemInfo.actions.add(action);
    }

    private void readPlantValues(ItemInfo itemInfo, LuaValue value) {
        itemInfo.plant = new ItemInfo.ItemInfoPlant();
        itemInfo.plant.mature = getInt(value, "mature", 1);
        itemInfo.plant.growing = getDouble(value, "growing", 1);

        if (!value.get("states").isnil()) {
            readPlantStatesValues(itemInfo, value.get("states"));
        }
    }

    private void readPlantStatesValues(ItemInfo itemInfo, LuaValue values) {
        itemInfo.plant.states = new ArrayList<>();
        for (int i = 1; i <= values.length(); i++) {
            ItemInfo.ItemInfoPlant.GrowingInfo growingInfo = new ItemInfo.ItemInfoPlant.GrowingInfo();
            growingInfo.name = getString(values.get(i), "name", null);
            growingInfo.value = getDouble(values.get(i), "value", 0);

            if (!values.get(i).get("temperature").isnil()) {
                growingInfo.temperature = new int[] {
                        values.get(i).get("temperature").get(1).toint(),
                        values.get(i).get("temperature").get(2).toint()
                };
            }

            if (!values.get(i).get("light").isnil()) {
                growingInfo.light = new int[] {
                        values.get(i).get("light").get(1).toint(),
                        values.get(i).get("light").get(2).toint()
                };
            }

            itemInfo.plant.states.add(growingInfo);
        }
    }

    private double getDouble(LuaValue value, String key, double defaultValue) {
        return !value.get(key).isnil() ? value.get(key).todouble() : defaultValue;
    }

    private int getInt(LuaValue value, String key, int defaultValue) {
        return !value.get(key).isnil() ? value.get(key).toint() : defaultValue;
    }

    private String getString(LuaValue value, String key, String defaultValue) {
        return !value.get(key).isnil() ? value.get(key).toString() : defaultValue;
    }

}
