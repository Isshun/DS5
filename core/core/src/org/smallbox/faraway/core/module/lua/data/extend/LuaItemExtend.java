package org.smallbox.faraway.core.module.lua.data.extend;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.smallbox.faraway.core.GraphicInfo;
import org.smallbox.faraway.core.data.ItemInfo;
import org.smallbox.faraway.core.game.model.GameData;
import org.smallbox.faraway.core.module.lua.DataExtendException;
import org.smallbox.faraway.core.module.lua.LuaModule;
import org.smallbox.faraway.core.module.lua.LuaModuleManager;
import org.smallbox.faraway.core.module.lua.data.LuaExtend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Alex on 29/09/2015.
 */
public class LuaItemExtend extends LuaExtend {
    private static Map<String, LuaValue> _cache = new HashMap<>();

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

        _cache.put(name, value);

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

        if (!value.get("parent").isnil()) {
            itemInfo.parentName = value.get("parent").toString();
            if (_cache.containsKey(itemInfo.parentName)) {
                readItem(itemInfo, _cache.get(itemInfo.parentName));
            } else {
                throw new DataExtendException(DataExtendException.Type.MISSING_PARENT, itemInfo.parentName);
            }
        }
        readItem(itemInfo, value);

        System.out.println("Extends item from lua: " + itemInfo.label);
    }

    private void readItem(ItemInfo itemInfo, LuaValue value) throws DataExtendException {
        itemInfo.name = getString(value, "name", null);
        itemInfo.label = getString(value, "label", null);
        itemInfo.category = getString(value, "category", null);
        itemInfo.type = getString(value, "type", null);

        LuaValue luaGraphics = value.get("graphics");
        if (!luaGraphics.isnil()) {
            if (!luaGraphics.get("path").isnil()) {
                itemInfo.graphics.add(readGraphic(luaGraphics));
            } else if (luaGraphics.length() >= 1 && !luaGraphics.get(1).get("path").isnil()) {
                for (int i = 1; i <= luaGraphics.length(); i++) {
                    itemInfo.graphics.add(readGraphic(luaGraphics.get(i)));
                }
            }
        }
        if (itemInfo.graphics.isEmpty()) {
            itemInfo.graphics.add(new GraphicInfo("base", "/graphics/missing.png"));
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

        if (!value.get("door").isnil()) {
            itemInfo.isDoor = value.get("door").toboolean();
        }

        if (!value.get("build").isnil()) {
            itemInfo.build = new ItemInfo.ItemBuildInfo();
            if (!value.get("build").get("cost").isnil()) {
                itemInfo.build.cost = value.get("build").get("cost").toint();
            } else {
                itemInfo.build.cost = GameData.config.defaultBuildCost;
            }
        }

        if (!value.get("slots").isnil()) {
            itemInfo.slots = new ArrayList<>();
            for (int i = 1; i <= value.get("slots").length(); i++) {
                itemInfo.slots.add(new int[] {value.get("slots").get(i).get(1).toint(), value.get("slots").get(i).get(2).toint()});
            }
        }

        if (!value.get("stack").isnil()) {
            itemInfo.stack = value.get("stack").toint();
        } else {
            itemInfo.stack = GameData.config.storageMaxQuantity;
        }

        if (!value.get("floor").isnil()) {
            itemInfo.isFloor = true;
        }

        if (!value.get("plant").isnil()) {
            readPlantValues(itemInfo, value.get("plant"));
        }

        if (!value.get("light").isnil()) {
            readLightValues(itemInfo, value.get("light"));
        }

        if (!value.get("factory").isnil()) {
            readFactoryValue(itemInfo, value.get("factory"));
        }

        if (!value.get("actions").isnil()) {
            itemInfo.actions = new ArrayList<>();
            if (!value.get("actions").get("type").isnil()) {
                readActionValue(itemInfo, value.get("actions"));
            } else {
                for (int i = 1; i <= value.get("actions").length(); i++) {
                    readActionValue(itemInfo, value.get("actions").get(i));
                }
            }
        }

        if (!value.get("receipts").isnil()) {
            itemInfo.receipts = new ArrayList<>();
            if (!value.get("receipts").get("components").isnil()) {
                readReceiptValue(itemInfo, value.get("receipts"));
            } else {
                for (int i = 1; i <= value.get("receipts").length(); i++) {
                    readReceiptValue(itemInfo, value.get("receipts").get(i));
                }
            }
        }

        // Get category
        if ("consumable".equals(itemInfo.type)) {
            itemInfo.isConsumable = true;
        } else if ("structure".equals(itemInfo.type)) {
            itemInfo.isStructure = true;
            itemInfo.canSupportRoof = !itemInfo.isWalkable || itemInfo.isDoor;
        } else if ("item".equals(itemInfo.type)) {
            itemInfo.isUserItem = true;
        } else if ("resource".equals(itemInfo.type)) {
            itemInfo.isResource = true;
            itemInfo.isRock = itemInfo.actions != null && "mine".equals(itemInfo.actions.get(0).type);
            itemInfo.canSupportRoof = itemInfo.actions != null && "mine".equals(itemInfo.actions.get(0).type);
        } else if ("equipment".equals(itemInfo.type) || itemInfo.equipment != null) {
            itemInfo.isEquipment = true;
            itemInfo.isConsumable = true;
        }
    }

    private void readFactoryValue(ItemInfo itemInfo, LuaValue value) {
        itemInfo.isFactory = true;
        itemInfo.factory = new ItemInfo.ItemInfoFactory();

        if (!value.get("slots").isnil()) {
            if (!value.get("slots").get("inputs").isnil()) {
                itemInfo.factory.inputSlots = new int[] {
                        value.get("slots").get("inputs").get(1).toint(),
                        value.get("slots").get("inputs").get(2).toint()
                };
            }
            if (!value.get("slots").get("outputs").isnil()) {
                itemInfo.factory.outputSlots = new int[] {
                        value.get("slots").get("outputs").get(1).toint(),
                        value.get("slots").get("outputs").get(2).toint()
                };
            }
        }

        if (!value.get("receipts").isnil()) {
            itemInfo.factory.receiptNames = new ArrayList<>();
            for (int i = 1; i <= value.get("receipts").length(); i++) {
                itemInfo.factory.receiptNames.add(value.get("receipts").get(i).toString());
            }
        }
    }

    private void readReceiptValue(ItemInfo itemInfo, LuaValue value) throws DataExtendException {
        ItemInfo.ItemInfoReceipt receipt = new ItemInfo.ItemInfoReceipt();
        receipt.components = new ArrayList<>();

        LuaValue luaComponents = value.get("components");
        if (!luaComponents.isnil()) {
            receipt.products = new ArrayList<>();
            for (int i = 1; i <= luaComponents.length(); i++) {
                ItemInfo.ItemComponentInfo component = new ItemInfo.ItemComponentInfo();

                // Get component item name
                if (!luaComponents.get(i).get("item").isnil()) {
                    component.itemName = getString(luaComponents.get(i), "item", null);
                } else {
                    throw new DataExtendException(DataExtendException.Type.MANDATORY, "receipts.components.item");
                }

                // Get component quantity
                if (!luaComponents.get(i).get("quantity").isnil()) {
                    component.quantity = luaComponents.get(i).get("quantity").toint();
                } else {
                    throw new DataExtendException(DataExtendException.Type.MANDATORY, "receipts.components.quantity");
                }
                receipt.components.add(component);
            }
        }

        itemInfo.receipts.add(receipt);
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
        if (!luaGraphic.get("type").isnil()) {
            graphicInfo.type = GraphicInfo.Type.valueOf(luaGraphic.get("type").toString().toUpperCase());
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

        LuaValue luaEffects = value.get("effects");
        if (!luaEffects.isnil()) {
            action.effects = new ItemInfo.ItemInfoEffects();
            for (int i = 1; i <= luaEffects.length(); i++) {
                if ("energy".equals(luaEffects.get(i).get("type").toString())) {
                    action.effects.energy = luaEffects.get(i).get("quantity").toint();
                }
            }
        }

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
                product.rate = getDouble(luaProducts.get(i), "rate", 1);
                action.products.add(product);
            }
        }

        itemInfo.actions.add(action);
    }

    private void readPlantValues(ItemInfo itemInfo, LuaValue value) {
        itemInfo.isPlant = true;
        itemInfo.plant = new ItemInfo.ItemInfoPlant();
        itemInfo.plant.minMaturity = getDouble(value, "gather", 1);
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
