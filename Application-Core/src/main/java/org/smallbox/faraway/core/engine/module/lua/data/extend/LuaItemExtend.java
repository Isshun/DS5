package org.smallbox.faraway.core.engine.module.lua.data.extend;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.engine.module.ModuleBase;
import org.smallbox.faraway.core.engine.module.lua.data.DataExtendException;
import org.smallbox.faraway.core.engine.module.lua.data.LuaExtend;
import org.smallbox.faraway.core.game.Data;
import org.smallbox.faraway.core.game.modelInfo.GraphicInfo;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.game.modelInfo.NetworkInfo;
import org.smallbox.faraway.core.game.modelInfo.ReceiptGroupInfo;

import java.io.File;
import java.util.*;

public class LuaItemExtend extends LuaExtend {

    private static Map<String, LuaValue> _cache = new HashMap<>();

    @Override
    public boolean accept(String type) {
        switch (type) {
            case "plant":
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
    public void extend(Data data, ModuleBase module, Globals globals, LuaValue value, File dataDirectory) throws DataExtendException {
        String name = getString(value, "name", null);

        _cache.put(name, value);

        ItemInfo itemInfo = null;
        for (ItemInfo info: data.items) {
            if (info.name != null && info.name.equals(name)) {
                itemInfo = info;
            }
        }

        if (itemInfo == null) {
            itemInfo = new ItemInfo();
            itemInfo.dataDirectory = dataDirectory;
            data.items.add(itemInfo);
            data.add(name, itemInfo);
        }

        if (!value.get("parent").isnil()) {
            itemInfo.parentName = value.get("parent").toString();
            ItemInfo finalItemInfo = itemInfo;
            data.getAsync(itemInfo.parentName, ItemInfo.class, parentItemInfo -> finalItemInfo.parent = parentItemInfo);
        }

        readItem(data, itemInfo, value);

//        Log.info("Extends item from lua: " + itemInfo.label);
    }

    private void readItem(Data data, ItemInfo itemInfo, LuaValue value) throws DataExtendException {
        itemInfo.name = getString(value, "name", null);
        itemInfo.label = getString(value, "label", null);
        itemInfo.category = value.get("category").optjstring(null);
        itemInfo.subCategory = value.get("sub_category").optjstring(null);
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
            GraphicInfo graphicInfo = new GraphicInfo("base", "/graphics/missing.png");
            graphicInfo.width = 32;
            graphicInfo.height = 32;
            itemInfo.graphics.add(graphicInfo);
        }

        itemInfo.isWalkable = getBoolean(value, "walkable", itemInfo.isWalkable);
        itemInfo.health = getInt(value, "health", itemInfo.health);

        readAsync(value, "network", NetworkInfo.class, networkInfo -> itemInfo.network = networkInfo);

        itemInfo.isGround = "ground".equals(getString(value, "type", null));
        itemInfo.isLiquid = "liquid".equals(getString(value, "type", null));
        itemInfo.isLinkDown = getBoolean(value, "is_link_down", false);
        itemInfo.isWall = getBoolean(value, "is_wall", itemInfo.isWall);
        itemInfo.isDoor = getBoolean(value, "door", itemInfo.isDoor);
        itemInfo.color = getInt(value, "color", 0x000000ff);

        itemInfo.permeability = getDouble(value, "permeability", 1);

        if (!value.get("liquid").isnil()) {
            data.getAsync(value.get("liquid").get("surface").toString(), ItemInfo.class, surfaceInfo -> itemInfo.surface = surfaceInfo);
        }

        if (!value.get("networks").isnil()) {
            itemInfo.networks = new ArrayList<>();
            for (int i = 1; i <= value.get("networks").length(); i++) {
                ItemInfo.NetworkItemInfo networkItemInfo = new ItemInfo.NetworkItemInfo();
                data.getAsync(getString(value.get("networks").get(i), "network", null), NetworkInfo.class, networkInfo -> networkItemInfo.network = networkInfo);
                networkItemInfo.distance = getInt(value.get("networks").get(i), "distance", 0);
                itemInfo.networks.add(networkItemInfo);
            }
        }

        itemInfo.build = new ItemInfo.ItemBuildInfo();
        if (!value.get("build").isnil()) {
            itemInfo.build.cost = value.get("build").get("cost").optdouble(0);

            LuaValue componentsValue = value.get("build").get("components");
            if (!componentsValue.isnil()) {
                for (int i = 1; i <= componentsValue.length(); i++) {
                    ItemInfo.ItemBuildInfo.ItemBuildComponentInfo componentInfo = new ItemInfo.ItemBuildInfo.ItemBuildComponentInfo();
                    data.getAsync(componentsValue.get(i).get("item").toString(), ItemInfo.class, component -> componentInfo.component = component);
                    componentInfo.quantity = componentsValue.get(i).get("quantity").toint();
                    itemInfo.build.components.add(componentInfo);

//                    itemInfo.slots.add(new int[] {value.get("slots").get(i).get(1).toint(), value.get("slots").get(i).get(2).toint()});
                }
            }
        }

        if (!value.get("slots").isnil()) {
            itemInfo.slots = new ArrayList<>();
            for (int i = 1; i <= value.get("slots").length(); i++) {
                itemInfo.slots.add(new int[] {value.get("slots").get(i).get(1).toint(), value.get("slots").get(i).get(2).toint()});
            }
        }

        // TODO
//        itemInfo.stack = getInt(value, "stack", Application.config.game.storageMaxQuantity);
        itemInfo.stack = getInt(value, "stack", 10);

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
            readFactoryValue(data, itemInfo, value.get("factory"));
        }

        // Effects on consumable
        if (!value.get("consume").isnil()) {
            itemInfo.consume = new ItemInfo.ItemActionInfo();
            itemInfo.consume.type = "consume";
            itemInfo.consume.cost = getInt(value.get("consume"), "cost", 10);
            itemInfo.consume.duration = value.get("consume").get("duration").optdouble(0);
            itemInfo.consume.count = getInt(value.get("consume"), "count", 1);
            itemInfo.consume.effects = new ItemInfo.ItemInfoEffects();
            readEffectValues(itemInfo.consume.effects, value.get("consume").get("effects"));
        }

        // Effects on item
        if (!value.get("use").isnil()) {
            itemInfo.use = new ItemInfo.ItemActionInfo();
            itemInfo.use.type = "use";
            itemInfo.use.cost = getInt(value.get("use"), "cost", 10);
            itemInfo.use.duration = value.get("use").get("duration").optdouble(0);
            itemInfo.use.count = getInt(value.get("use"), "count", 1);
            itemInfo.use.effects = new ItemInfo.ItemInfoEffects();
            readEffectValues(itemInfo.use.effects, value.get("use").get("effects"));
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

    private void readFactoryValue(Data data, ItemInfo itemInfo, LuaValue value) {
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
            itemInfo.factory.receiptGroups = new HashSet<>();
            for (int i = 1; i <= value.get("receipts").length(); i++) {
                data.getAsync(value.get("receipts").get(i).get("receipt").tojstring(), ReceiptGroupInfo.class,
                        receiptGroupInfo -> itemInfo.factory.receiptGroups.add(receiptGroupInfo));
            }
        }
    }

    private void readReceiptValue(ItemInfo itemInfo, LuaValue value) throws DataExtendException {
        ReceiptGroupInfo.ReceiptInfo receipt = new ReceiptGroupInfo.ReceiptInfo();

        receipt.label = getString(value, "label", null);
        receipt.icon = getString(value, "icon", null);

        receipt.inputs = new ArrayList<>();
        LuaValue luaComponents = value.get("components");
        if (!luaComponents.isnil()) {
            if (luaComponents.length() == 0) {
                readReceiptComponentValue(receipt.inputs, luaComponents);
            } else {
                for (int i = 1; i <= luaComponents.length(); i++) {
                    readReceiptComponentValue(receipt.inputs, luaComponents.get(i));
                }
            }
        }

        itemInfo.receipts.add(receipt);
    }

    private void readReceiptComponentValue(List<ReceiptGroupInfo.ReceiptInfo.ReceiptInputInfo> inputs, LuaValue luaComponent) throws DataExtendException {
        ReceiptGroupInfo.ReceiptInfo.ReceiptInputInfo input = new ReceiptGroupInfo.ReceiptInfo.ReceiptInputInfo();

        // Get component item name
        if (!luaComponent.get("item").isnil()) {
            readAsync(luaComponent, "item", ItemInfo.class, componentItemInfo -> input.item = componentItemInfo);
        } else {
            throw new DataExtendException(DataExtendException.Type.MANDATORY, "receipts.components.item");
        }

        // Get component quantity
        if (!luaComponent.get("quantity").isnil()) {
            input.quantity = luaComponent.get("quantity").toint();
        } else {
            throw new DataExtendException(DataExtendException.Type.MANDATORY, "receipts.components.quantity");
        }

        inputs.add(input);
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
        if (!luaGraphic.get("tile_width").isnil()) {
            graphicInfo.tileWidth = luaGraphic.get("tile_width").toint();
        }
        if (!luaGraphic.get("tile_height").isnil()) {
            graphicInfo.tileHeight = luaGraphic.get("tile_height").toint();
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
        action.label = getString(value, "label", null);
        action.auto = value.get("auto").optboolean(false);

        action.inputs = new ArrayList<>();
        if (!value.get("inputs").isnil()) {
            readLuaComposite(value.get("inputs"), luaInput -> {
                ItemInfo.ActionInputInfo inputInfo = new ItemInfo.ActionInputInfo();
                readAsync(luaInput, "network", NetworkInfo.class, inputNetworkInfo -> inputInfo.network = inputNetworkInfo);
                readAsync(luaInput, "item", ItemInfo.class, inputItemInfo -> inputInfo.item = inputItemInfo);
                inputInfo.quantity = getInt(luaInput, "quantity", 1);
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
            ItemInfo.ItemProductInfo productInfo = new ItemInfo.ItemProductInfo();
            readAsync(luaProduct, "item", ItemInfo.class, productItemInfo -> productInfo.item = productItemInfo);
            productInfo.quantity = getIntInterval(luaProduct, "quantity", new int[] {1, 1});
            productInfo.rate = getDouble(luaProduct, "rate", 1);
            action.products.add(productInfo);
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

    private void readPlantValues(ItemInfo itemInfo, LuaValue plantValue) {
        itemInfo.isPlant = true;
        itemInfo.plant = new ItemInfo.ItemInfoPlant();
//        itemInfo.plant.growing = 1 / getDouble(plantValue, "growing", 2000);
//        itemInfo.plant.nourish = 1 / getDouble(plantValue, "nourish", 500);
        itemInfo.plant.growing = getDouble(plantValue, "growing", 1);
        itemInfo.plant.nourish = getDouble(plantValue, "nourish", 1);
//        itemInfo.plant.oxygen = getDouble(value, "oxygen", 0);

        if (!plantValue.get("temperature").isnil()) {
            itemInfo.plant.temperature = new ItemInfo.ItemInfoPlant.PlantRangeInfo();
            readInt(plantValue.get("temperature"), "min", value -> itemInfo.plant.temperature.min = value);
            readInt(plantValue.get("temperature"), "best", value -> itemInfo.plant.temperature.best = value);
            readInt(plantValue.get("temperature"), "max", value -> itemInfo.plant.temperature.max = value);
        }

        if (!plantValue.get("states").isnil()) {
            readPlantStatesValues(itemInfo, plantValue.get("states"));
        }
    }

    private void readPlantStatesValues(ItemInfo itemInfo, LuaValue values) {
        itemInfo.plant.states = new ArrayList<>();
        for (int i = 1; i <= values.length(); i++) {
            ItemInfo.ItemInfoPlant.GrowingInfo growingInfo = new ItemInfo.ItemInfoPlant.GrowingInfo();
            growingInfo.name = getString(values.get(i), "name", null);
            growingInfo.value = getDouble(values.get(i), "value", 0);

            readLua(values.get(i), "temperature", value -> growingInfo.temperature = new double[] { value.get(1).todouble(), value.get(2).todouble() });
            readLua(values.get(i), "light", value -> growingInfo.light = new double[] { value.get(1).todouble(), value.get(2).todouble() });
            readLua(values.get(i), "oxygen", value -> growingInfo.oxygen = new double[] { value.get(1).todouble(), value.get(2).todouble() });
            readLua(values.get(i), "moisture", value -> growingInfo.moisture = new double[] { value.get(1).todouble(), value.get(2).todouble() });

            itemInfo.plant.states.add(growingInfo);
        }
    }

}
