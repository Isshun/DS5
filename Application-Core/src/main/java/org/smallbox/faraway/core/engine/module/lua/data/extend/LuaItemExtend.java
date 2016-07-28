package org.smallbox.faraway.core.engine.module.lua.data.extend;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.engine.module.ModuleBase;
import org.smallbox.faraway.core.engine.module.lua.data.DataExtendException;
import org.smallbox.faraway.core.engine.module.lua.data.LuaExtend;
import org.smallbox.faraway.core.game.Data;
import org.smallbox.faraway.core.game.modelInfo.GraphicInfo;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
            case "ground":
            case "liquid":
            case "resource":
            case "structure":
            case "consumable":
            case "network_item":
                return true;
        }
        return false;
    }

    @Override
    public void extend(ModuleBase module, Globals globals, LuaValue value) throws DataExtendException {
        String name = getString(value, "name", null);

        _cache.put(name, value);

        ItemInfo itemInfo = null;
        for (ItemInfo info: Data.getData().items) {
            if (info.name != null && info.name.equals(name)) {
                itemInfo = info;
            }
        }

        if (itemInfo == null) {
            itemInfo = new ItemInfo();
            Data.getData().items.add(itemInfo);
        }

//        if (!value.get("parent").isnil()) {
//            itemInfo.parentName = value.get("parent").toString();
//            if (_cache.containsKey(itemInfo.parentName)) {
//                readItem(itemInfo, _cache.get(itemInfo.parentName));
//            } else {
//                throw new DataExtendException(DataExtendException.Type.MISSING_PARENT, itemInfo.parentName);
//            }
//        }

        readItem(itemInfo, value);

//        Log.info("Extends item from lua: " + itemInfo.label);
    }

    private void readItem(ItemInfo itemInfo, LuaValue value) throws DataExtendException {
        itemInfo.name = getString(value, "name", null);
        itemInfo.label = getString(value, "label", null);
        itemInfo.category = getString(value, "category", null);
        itemInfo.type = getString(value, "type", null);

        if (!value.get("size").isnil()) {
            itemInfo.width = value.get("size").get(1).toint();
            itemInfo.height = value.get("size").get(2).toint();
        } else {
            itemInfo.width = 1;
            itemInfo.height = 1;
        }

        LuaValue luaGraphics = value.get("graphics");
        if (!luaGraphics.isnil()) {
            itemInfo.graphics.clear();
            if (!luaGraphics.get("path").isnil()) {
                itemInfo.graphics.add(readGraphic(luaGraphics, itemInfo));
            } else if (luaGraphics.length() >= 1 && !luaGraphics.get(1).get("path").isnil()) {
                for (int i = 1; i <= luaGraphics.length(); i++) {
                    itemInfo.graphics.add(readGraphic(luaGraphics.get(i), itemInfo));
                }
            }
        }
        if (itemInfo.graphics.isEmpty()) {
            itemInfo.graphics.add(new GraphicInfo("base", "/graphics/missing.png"));
        }

        itemInfo.isWalkable = getBoolean(value, "walkable", itemInfo.isWalkable);
        itemInfo.health = getInt(value, "health", itemInfo.health);
        itemInfo.networkName = getString(value, "network", itemInfo.networkName);
        itemInfo.isGround = "ground".equals(getString(value, "type", null));
        itemInfo.isLiquid = "liquid".equals(getString(value, "type", null));
        itemInfo.isLinkDown = getBoolean(value, "is_link_down", false);
        itemInfo.isWall = getBoolean(value, "is_wall", itemInfo.isWall);
        itemInfo.isDoor = getBoolean(value, "door", itemInfo.isDoor);
        itemInfo.color = getInt(value, "color", 0x000000);

        itemInfo.permeability = getDouble(value, "permeability", 1);

        if (!value.get("liquid").isnil()) {
            itemInfo.surfaceName = value.get("liquid").get("surface").toString();
        }

        if (!value.get("networks").isnil()) {
            itemInfo.networks = new ArrayList<>();
            for (int i = 1; i <= value.get("networks").length(); i++) {
                ItemInfo.NetworkItemInfo networkItemInfo = new ItemInfo.NetworkItemInfo();
                networkItemInfo.name = getString(value.get("networks").get(i), "network", null);
                networkItemInfo.distance = getInt(value.get("networks").get(i), "distance", 0);
                itemInfo.networks.add(networkItemInfo);
            }
        }

        itemInfo.build = new ItemInfo.ItemBuildInfo();
        if (!value.get("build").isnil()) {
            itemInfo.build.cost = getInt(value.get("build"), "cost", 0);

            LuaValue componentsValue = value.get("components");
            if (!componentsValue.isnil()) {
                for (int i = 1; i <= componentsValue.length(); i++) {
                    ItemInfo.ItemBuildInfo.ItemBuildComponentInfo componentInfo = new ItemInfo.ItemBuildInfo.ItemBuildComponentInfo();
                    Data.getData().getAsync(componentsValue.get(i).get("id").toString(), component -> componentInfo.component = component);
                    componentInfo.count = componentsValue.get(i).get("count").toint();
                    itemInfo.build.components.add(componentInfo);

                    itemInfo.slots.add(new int[] {value.get("slots").get(i).get(1).toint(), value.get("slots").get(i).get(2).toint()});
                }
            }
        }

        if (!value.get("slots").isnil()) {
            itemInfo.slots = new ArrayList<>();
            for (int i = 1; i <= value.get("slots").length(); i++) {
                itemInfo.slots.add(new int[] {value.get("slots").get(i).get(1).toint(), value.get("slots").get(i).get(2).toint()});
            }
        }

        itemInfo.stack = getInt(value, "stack", Application.getInstance().getConfig().game.storageMaxQuantity);

        if (!value.get("floor").isnil()) {
            itemInfo.isFloor = true;
            itemInfo.isWalkable = true;
            itemInfo.isRamp = getBoolean(value.get("floor"), "ramp", false);
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

        // Effects on consumable
        if (!value.get("consume").isnil()) {
            itemInfo.consume = new ItemInfo.ItemConsumeInfo();
            itemInfo.consume.cost = getInt(value.get("consume"), "cost", 10);
            itemInfo.consume.count = getInt(value.get("consume"), "count", 1);
            itemInfo.consume.effects = new ItemInfo.ItemInfoEffects();
            readEffectValues(itemInfo.consume.effects, value.get("consume").get("effects"));
        }

        // Read passive effects
        LuaValue luaEffects = value.get("effects");
        if (!luaEffects.isnil()) {
            itemInfo.effects = new ItemInfo.ItemInfoEffects();
            readPassiveEffectValues(itemInfo.effects, luaEffects);
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
            if (value.get("receipts").length() == 0) {
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
        } else if ("network_item".equals(itemInfo.type)) {
            itemInfo.isNetworkItem = true;
        } else if ("resource".equals(itemInfo.type)) {
            itemInfo.isResource = true;
            itemInfo.isRock = itemInfo.actions != null && itemInfo.actions.get(0).type == ItemInfo.ItemInfoAction.ActionType.MINE;
            itemInfo.canSupportRoof = itemInfo.actions != null && itemInfo.actions.get(0).type == ItemInfo.ItemInfoAction.ActionType.MINE;
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
            itemInfo.factory.receipts = new ArrayList<>();
            for (int i = 1; i <= value.get("receipts").length(); i++) {
                LuaValue luaReceipt = value.get("receipts").get(i);
                ItemInfo.FactoryGroupReceiptInfo factoryGroupReceiptInfo = new ItemInfo.FactoryGroupReceiptInfo(luaReceipt.get("receipt").toString());
                factoryGroupReceiptInfo.auto = getBoolean(luaReceipt, "auto", false);
                factoryGroupReceiptInfo.cost = getInt(luaReceipt, "cost", -1);
                factoryGroupReceiptInfo.output = "network".equals(getString(luaReceipt, "output", null)) ? ItemInfo.FactoryOutputMode.NETWORK : ItemInfo.FactoryOutputMode.GROUND;
                itemInfo.factory.receipts.add(factoryGroupReceiptInfo);
            }
        }
    }

    private void readReceiptValue(ItemInfo itemInfo, LuaValue value) throws DataExtendException {
        ItemInfo.ItemInfoReceipt receipt = new ItemInfo.ItemInfoReceipt();

        receipt.label = getString(value, "label", null);
        receipt.icon = getString(value, "icon", null);

        receipt.components = new ArrayList<>();
        LuaValue luaComponents = value.get("components");
        if (!luaComponents.isnil()) {
            if (luaComponents.length() == 0) {
                readReceiptComponentValue(receipt.components, luaComponents);
            } else {
                for (int i = 1; i <= luaComponents.length(); i++) {
                    readReceiptComponentValue(receipt.components, luaComponents.get(i));
                }
            }
        }

        itemInfo.receipts.add(receipt);
    }

    private void readReceiptComponentValue(List<ItemInfo.ItemComponentInfo> components, LuaValue luaComponent) throws DataExtendException {
        ItemInfo.ItemComponentInfo component = new ItemInfo.ItemComponentInfo();

        // Get component item name
        if (!luaComponent.get("item").isnil()) {
            component.itemName = getString(luaComponent, "item", null);
        } else {
            throw new DataExtendException(DataExtendException.Type.MANDATORY, "receipts.components.item");
        }

        // Get component quantity
        if (!luaComponent.get("quantity").isnil()) {
            component.quantity = luaComponent.get("quantity").toint();
        } else {
            throw new DataExtendException(DataExtendException.Type.MANDATORY, "receipts.components.quantity");
        }
        components.add(component);
    }

    private GraphicInfo readGraphic(LuaValue luaGraphic, ItemInfo itemInfo) throws DataExtendException {
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

        graphicInfo.width = itemInfo.width * 32;
        graphicInfo.height = itemInfo.height * 32;

        return graphicInfo;
    }

    private void readLightValues(ItemInfo itemInfo, LuaValue value) {
        itemInfo.light = 10;
        itemInfo.lightDistance = 4;
    }

    private void readActionValue(ItemInfo itemInfo, LuaValue value) throws DataExtendException {
        ItemInfo.ItemInfoAction action = new ItemInfo.ItemInfoAction();
        action.type = ItemInfo.ItemInfoAction.ActionType.valueOf(getString(value, "type", null).toUpperCase());
        action.cost = getInt(value, "cost", 0);

        if (!value.get("inputs").isnil()) {
            action.inputs = new ArrayList<>();
            readLuaComposite(value.get("inputs"), input -> {
                ItemInfo.ActionInputInfo inputInfo = new ItemInfo.ActionInputInfo();
                inputInfo.networkName = getString(input, "network", null);
                inputInfo.itemName = getString(input, "item", null);
                inputInfo.quantity = getInt(input, "quantity", 1);
                action.inputs.add(inputInfo);
            });
        }

        LuaValue luaEffects = value.get("effects");
        if (!luaEffects.isnil()) {
            action.effects = new ItemInfo.ItemInfoEffects();
            readEffectValues(action.effects, luaEffects);
        }

        action.products = new ArrayList<>();
        readLuaComposite(value.get("products"), luaProduct -> {
            ItemInfo.ItemProductInfo product = new ItemInfo.ItemProductInfo();

            // Get product item name
            if (!luaProduct.get("item").isnil()) {
                product.itemName = getString(luaProduct, "item", null);
            } else {
//                throw new DataExtendException(DataExtendException.Type.MANDATORY, "actions.products.item");
            }

            // Get product quantity
            if (!luaProduct.get("quantity").isnil()) {
                product.quantity = readLuaInterval(luaProduct.get("quantity"));
            } else {
//                throw new DataExtendException(DataExtendException.Type.MANDATORY, "actions.products.quantity");
            }
            product.rate = getDouble(luaProduct, "rate", 1);
            action.products.add(product);
        });

        itemInfo.actions.add(action);
    }

    private int[] readLuaInterval(LuaValue luaValue) {
        if (luaValue.istable() && luaValue.length() > 0) {
            return new int[]{ luaValue.get(1).toint(), luaValue.get(2).toint()};
        } else {
            return new int[]{ luaValue.toint(), luaValue.toint()};
        }
    }

    private interface LuaCompositeCallback {
        void onEach(LuaValue value);
    }

    private void readLuaComposite(LuaValue composite, LuaCompositeCallback callback) {
        if (composite != null && !composite.isnil()) {
            if (composite.istable() && composite.length() > 0) {
                for (int i = 1; i <= composite.length(); i++) {
                    callback.onEach(composite.get(i));
                }
            } else {
                callback.onEach(composite);
            }
        }
    }

    private void readEffectValues(ItemInfo.ItemInfoEffects effects, LuaValue luaEffects) {
        for (int i = 1; i <= luaEffects.length(); i++) {
            String type = luaEffects.get(i).get("type").toString();
            int quantity = luaEffects.get(i).get("quantity").toint();
            if ("energy".equals(type)) effects.energy = quantity;
            if ("food".equals(type)) effects.food = quantity;
            if ("drink".equals(type)) effects.drink = quantity;
        }
    }

    private void readPassiveEffectValues(ItemInfo.ItemInfoEffects effects, LuaValue luaEffects) {
        for (int i = 1; i <= luaEffects.length(); i++) {
            LuaValue effect = luaEffects.get(i);
            String type = effect.get("type").toString();
            if ("oxygen".equals(type)) {
                effects.oxygen = effect.get("value").todouble();
                effects.pressure = effect.get("pressure").toint();
            }
        }
    }

    private void readPlantValues(ItemInfo itemInfo, LuaValue value) {
        itemInfo.isPlant = true;
        itemInfo.plant = new ItemInfo.ItemInfoPlant();
        itemInfo.plant.growing = 1 / getDouble(value, "growing", 2000);
        itemInfo.plant.nourish = 1 / getDouble(value, "nourish", 500);
        itemInfo.plant.oxygen = getDouble(value, "oxygen", 0);

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
