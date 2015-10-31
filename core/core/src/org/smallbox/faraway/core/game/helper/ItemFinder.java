package org.smallbox.faraway.core.game.helper;

import org.smallbox.faraway.core.data.ItemInfo;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.model.GameData;
import org.smallbox.faraway.core.game.module.character.model.base.CharacterModel;
import org.smallbox.faraway.core.game.module.path.PathManager;
import org.smallbox.faraway.core.game.module.world.WorldModule;
import org.smallbox.faraway.core.game.module.world.model.*;
import org.smallbox.faraway.core.game.module.world.model.item.ItemModel;
import org.smallbox.faraway.core.module.GameModule;
import org.smallbox.faraway.core.module.java.ModuleHelper;
import org.smallbox.faraway.core.module.java.ModuleManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemFinder extends GameModule {
    private int                     _width;
    private int                     _height;
    private WorldModule             _worldModule;
    private PathManager             _pathManager;
    private List<ConsumableModel>   _consumables;
    private List<ItemModel>         _items;

    @Override
    protected void onLoaded() {
        _items = new ArrayList<>();
        _consumables = new ArrayList<>();
        _pathManager = (PathManager) ModuleManager.getInstance().getModule(PathManager.class);
        _worldModule = (WorldModule) ModuleManager.getInstance().getModule(WorldModule.class);
        _width = Game.getInstance().getInfo().worldWidth;
        _height = Game.getInstance().getInfo().worldHeight;
    }

    public MapObjectModel getNearest(ItemFilter filter, CharacterModel character) {
        if (filter.needItem) {
            int bestDistance = Integer.MAX_VALUE;
            ItemModel bestItem = null;
            for (ItemModel item: ModuleHelper.getWorldModule().getItems()) {
                if (item.matchFilter(filter)) {
                    int distance = WorldHelper.getApproxDistance(item.getParcel(), character.getParcel());
                    if (bestDistance > distance) {
                        bestDistance = distance;
                        bestItem = item;
                    }
                }
            }
            return bestItem;
        }

        if (filter.needConsumable) {
            int bestDistance = Integer.MAX_VALUE;
            ConsumableModel bestConsumable = null;
            for (ConsumableModel consumable: ModuleHelper.getWorldModule().getConsumables()) {
                if (consumable.matchFilter(filter)) {
                    int distance = WorldHelper.getApproxDistance(consumable.getParcel(), character.getParcel());
                    if (bestDistance > distance) {
                        bestDistance = distance;
                        bestConsumable = consumable;
                    }
                }
            }
            return bestConsumable;
        }
//
//        int startX = model.getX();
//        int startY = model.getY();
//        int maxX = Math.max(startX, _width - startX);
//        int maxY = Math.max(startY, _height - startY);
//        for (int offsetX = 0; offsetX < maxX; offsetX++) {
//            for (int offsetY = 0; offsetY < maxY; offsetY++) {
//                ParcelModel model = _worldModule.getParcel(startX + offsetX, startY + offsetY);
//
//                // Check on non-existing model
//                if (model == null) {
//                    continue;
//                }
//
////                // Private room exists and characters is not allowed
////                if (model.getRoom() != null && model.getRoom().isPrivate() && model.getRoom().getOccupants().contains(characters) == false) {
////                    continue;
////                }
//
//                if (filter.isConsomable) {
//                    ConsumableModel consumable = WorldHelper.getConsumable(startX + offsetX, startY + offsetY);
//                    if (getNearestItemCheck(consumable, filter)) { return consumable; }
//
//                    consumable = WorldHelper.getConsumable(startX - offsetX, startY - offsetY);
//                    if (getNearestItemCheck(consumable, filter)) { return consumable; }
//
//                    consumable = WorldHelper.getConsumable(startX + offsetX, startY - offsetY);
//                    if (getNearestItemCheck(consumable, filter)) { return consumable; }
//
//                    consumable = WorldHelper.getConsumable(startX - offsetX, startY + offsetY);
//                    if (getNearestItemCheck(consumable, filter)) { return consumable; }
//                } else {
//                    ItemModel item = WorldHelper.getItem(startX + offsetX, startY + offsetY);
//                    if (getNearestItemCheck(item, filter)) { return item; }
//
//                    item = WorldHelper.getItem(startX - offsetX, startY - offsetY);
//                    if (getNearestItemCheck(item, filter)) { return item; }
//
//                    item = WorldHelper.getItem(startX + offsetX, startY - offsetY);
//                    if (getNearestItemCheck(item, filter)) { return item; }
//
//                    item = WorldHelper.getItem(startX - offsetX, startY + offsetY);
//                    if (getNearestItemCheck(item, filter)) { return item; }
//                }
//            }
//        }
        return null;
    }

    public ConsumableModel getNearest(ItemInfo info, int startX, int startY) {
        int maxX = Math.max(startX, _width - startX);
        int maxY = Math.max(startY, _height - startY);
        for (int offsetX = 0; offsetX < maxX; offsetX++) {
            for (int offsetY = 0; offsetY < maxY; offsetY++) {
                ParcelModel area = _worldModule.getParcel(startX + offsetX, startY + offsetY);
                if (area != null && area.getConsumable() != null && area.getConsumable().getInfo() == info && area.getConsumable().getLock() == null) {
                    return area.getConsumable();
                }
                area = _worldModule.getParcel(startX - offsetX, startY + offsetY);
                if (area != null && area.getConsumable() != null && area.getConsumable().getInfo() == info && area.getConsumable().getLock() == null) {
                    return area.getConsumable();
                }
                area = _worldModule.getParcel(startX + offsetX, startY - offsetY);
                if (area != null && area.getConsumable() != null && area.getConsumable().getInfo() == info && area.getConsumable().getLock() == null) {
                    return area.getConsumable();
                }
                area = _worldModule.getParcel(startX - offsetX, startY - offsetY);
                if (area != null && area.getConsumable() != null && area.getConsumable().getInfo() == info && area.getConsumable().getLock() == null) {
                    return area.getConsumable();
                }
            }
        }
        return null;
    }

    public MapObjectModel getRandomNearest(ItemFilter filter, CharacterModel character) {
        return getRandomNearest(filter, character.getParcel());
    }

    public MapObjectModel getRandomNearest(ItemFilter filter, ParcelModel fromParcel) {
        List<? extends MapObjectModel> list = filter.needConsumable ? _consumables : _items;

        // Get matching items
        int start = (int) (Math.random() * list.size());
        int length = list.size();
        int bestDistance = Integer.MAX_VALUE;
        Map<MapObjectModel, Integer> ObjectsMatchingFilter = new HashMap<>();
        for (int i = 0; i < length; i++) {
            MapObjectModel mapObject = list.get((i + start) % length);
            int distance = Math.abs(mapObject.getX() - fromParcel.x) + Math.abs(mapObject.getY() - fromParcel.y);
            if (mapObject.matchFilter(filter) && _pathManager.getPath(fromParcel, mapObject.getParcel()) != null) {
                ObjectsMatchingFilter.put(mapObject, distance);
                if (bestDistance > distance) {
                    bestDistance = distance;
                }
            }
        }

        // Take first item at acceptable distance
        for (Map.Entry<MapObjectModel, Integer> entry: ObjectsMatchingFilter.entrySet()) {
            if (entry.getValue() <= bestDistance + GameData.config.maxNearDistance) {
                return entry.getKey();
            }
        }

        return null;
    }

    @Override
    protected void onUpdate(int tick) {
    }

    @Override
    public void onAddItem(ItemModel item) {
        _items.add(item);
    }

    @Override
    public void onAddConsumable(ConsumableModel consumable) {
        _consumables.add(consumable);
    }

    @Override
    public void onRemoveItem(ItemModel item) {
        _items.remove(item);
    }

    @Override
    public void onRemoveConsumable(ConsumableModel consumable) {
        _consumables.remove(consumable);
    }
}
