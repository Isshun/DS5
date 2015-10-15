package org.smallbox.faraway.module.lua.data.extend;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.smallbox.faraway.GraphicInfo;
import org.smallbox.faraway.game.model.GameData;
import org.smallbox.faraway.module.lua.DataExtendException;
import org.smallbox.faraway.module.lua.data.LuaExtend;
import org.smallbox.faraway.module.lua.LuaModule;
import org.smallbox.faraway.module.lua.LuaModuleManager;
import org.smallbox.faraway.game.model.item.ItemInfo;

import java.util.ArrayList;

/**
 * Created by Alex on 29/09/2015.
 */
public class LuaItemExtend extends LuaExtend {
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
    public void extend(LuaModuleManager luaModuleManager, LuaModule module, Globals globals, LuaValue value) throws DataExtendException {
        String name = getString(value, "name", null);
        ItemInfo itemInfo = null;
        for (ItemInfo info: GameData.getData().items) {
            if (info.name != null && info.name.equals(name)) {
                itemInfo = info;
            }
        }

        if (itemInfo == null) {
            itemInfo = new ItemInfo();
            GameData.getData().items.add(itemInfo);
        }

        itemInfo.name = getString(value, "name", null);
        itemInfo.label = getString(value, "label", null);
        itemInfo.category = getString(value, "category", null);
        itemInfo.type = getString(value, "type", null);

        // Get category
        if ("consumable".equals(itemInfo.type)) {
            itemInfo.isConsumable = true;
        } else if ("structure".equals(itemInfo.type)) {
            itemInfo.isStructure = true;
        } else if ("item".equals(itemInfo.type)) {
            itemInfo.isUserItem = true;
        } else if ("resource".equals(itemInfo.type)) {
            itemInfo.isResource = true;
        } else if ("equipment".equals(itemInfo.type) || itemInfo.equipment != null) {
            itemInfo.isEquipment = true;
            itemInfo.isConsumable = true;
        } else {
            throw new RuntimeException("unknown item type: " + itemInfo.type);
        }

        LuaValue luaGraphics = value.get("graphics");
        if (!luaGraphics.isnil()) {
            itemInfo.graphics = new ArrayList<>();
            if (!luaGraphics.get("path").isnil()) {
                itemInfo.graphics.add(readGraphic(luaGraphics));
            } else if (luaGraphics.length() >= 1 && !luaGraphics.get(1).get("path").isnil()) {
                for (int i = 1; i <= luaGraphics.length(); i++) {
                    itemInfo.graphics.add(readGraphic(luaGraphics.get(i)));
                }
            }
        }

        if (!value.get("size").isnil()) {
            itemInfo.width = value.get("size").get(1).toint();
            itemInfo.height = value.get("size").get(2).toint();
        } else {
            itemInfo.width = 1;
            itemInfo.height = 1;
        }

        if (!value.get("walkable").isnil()) {
            itemInfo.isWalkable = value.get("walkable").toboolean();
        }

        if (!value.get("stack").isnil()) {
            itemInfo.stack = value.get("stack").toint();
        }

        if (!value.get("plant").isnil()) {
            readPlantValues(itemInfo, value.get("plant"));
        }

        if (!value.get("light").isnil()) {
            readLightValues(itemInfo, value.get("light"));
        }

        if (!value.get("actions").isnil()) {
            itemInfo.actions = new ArrayList<>();
            for (int i = 1; i <= value.get("actions").length(); i++) {
                readActionValue(itemInfo, value.get("actions").get(i));
            }
        }

        System.out.println("Extends item from lua: " + itemInfo.label);
    }

    private GraphicInfo readGraphic(LuaValue luaGraphic) throws DataExtendException {
        GraphicInfo graphicInfo;
        if (!luaGraphic.get("path").isnil()) {
            String path = luaGraphic.get("path").toString();
            graphicInfo = new GraphicInfo(
                    path.substring(1, path.indexOf(']')),
                    path.substring(path.indexOf(']') + 1, path.length())
            );
        } else {
            throw new DataExtendException(DataExtendException.Type.MANDATORY, "graphics.path");
        }
        if (!luaGraphic.get("x").isnil()) {
            graphicInfo.x = luaGraphic.get("x").toint();
        }
        if (!luaGraphic.get("y").isnil()) {
            graphicInfo.y = luaGraphic.get("y").toint();
        }
        return graphicInfo;
    }

    private void readLightValues(ItemInfo itemInfo, LuaValue value) {
        itemInfo.light = 10;
        itemInfo.lightDistance = 4;
    }

    private void readActionValue(ItemInfo itemInfo, LuaValue value) throws DataExtendException {
        ItemInfo.ItemInfoAction action = new ItemInfo.ItemInfoAction();
        action.type = getString(value, "type", null);
        action.cost = getInt(value, "cost", 0);

        LuaValue luaProducts = value.get("products");
        if (!luaProducts.isnil()) {
            action.products = new ArrayList<>();
            for (int i = 1; i <= luaProducts.length(); i++) {
                ItemInfo.ItemProductInfo product = new ItemInfo.ItemProductInfo();

                // Get product item name
                if (!luaProducts.get(i).get("item").isnil()) {
                    product.itemName = getString(luaProducts.get(i), "item", null);
                } else {
                    throw new DataExtendException(DataExtendException.Type.MANDATORY, "actions.products.item");
                }

                // Get product quantity
                if (!luaProducts.get(i).get("quantity").isnil()) {
                    product.quantity = new int[]{
                            luaProducts.get(i).get("quantity").get(1).toint(),
                            luaProducts.get(i).get("quantity").get(2).toint()};
                } else {
                    throw new DataExtendException(DataExtendException.Type.MANDATORY, "actions.products.quantity");
                }
                product.dropRate = getDouble(luaProducts.get(i), "rate", 1);
                action.products.add(product);
            }
        }

        itemInfo.actions.add(action);
    }

    private void readPlantValues(ItemInfo itemInfo, LuaValue value) {
        itemInfo.isPlant = true;
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

}
