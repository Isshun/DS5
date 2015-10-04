package org.smallbox.faraway.game.helper;

import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.model.GameData;
import org.smallbox.faraway.game.model.character.base.CharacterModel;
import org.smallbox.faraway.game.model.item.*;
import org.smallbox.faraway.game.module.GameModule;
import org.smallbox.faraway.game.module.ModuleHelper;
import org.smallbox.faraway.game.module.ModuleManager;
import org.smallbox.faraway.game.module.base.WorldModule;
import org.smallbox.faraway.game.module.path.PathManager;
import org.smallbox.faraway.util.Constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemFinder extends GameModule {
	private int 					_width;
	private int 					_height;
	private WorldModule 			_worldModule;
	private PathManager 			_pathManager;
	private List<ConsumableModel> 	_consumables;
	private List<ItemModel> 		_items;

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
//		int startX = character.getX();
//		int startY = character.getY();
//		int maxX = Math.max(startX, _width - startX);
//		int maxY = Math.max(startY, _height - startY);
//		for (int offsetX = 0; offsetX < maxX; offsetX++) {
//			for (int offsetY = 0; offsetY < maxY; offsetY++) {
//				ParcelModel area = _worldModule.getParcel(startX + offsetX, startY + offsetY);
//
//				// Check on non-existing area
//				if (area == null) {
//					continue;
//				}
//
////				// Private room exists and characters is not allowed
////				if (area.getRoom() != null && area.getRoom().isPrivate() && area.getRoom().getOccupants().contains(characters) == false) {
////					continue;
////				}
//
//				if (filter.isConsomable) {
//					ConsumableModel consumable = WorldHelper.getConsumable(startX + offsetX, startY + offsetY);
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
//				} else {
//					ItemModel item = WorldHelper.getItem(startX + offsetX, startY + offsetY);
//					if (getNearestItemCheck(item, filter)) { return item; }
//
//					item = WorldHelper.getItem(startX - offsetX, startY - offsetY);
//					if (getNearestItemCheck(item, filter)) { return item; }
//
//					item = WorldHelper.getItem(startX + offsetX, startY - offsetY);
//					if (getNearestItemCheck(item, filter)) { return item; }
//
//					item = WorldHelper.getItem(startX - offsetX, startY + offsetY);
//					if (getNearestItemCheck(item, filter)) { return item; }
//				}
//			}
//		}
		return null;
	}

	private boolean getNearestItemCheck(MapObjectModel item, ItemFilter filter) {
		// Item not exists
		if (item == null) {
			return false;
		}

		// Item is blocked
		if (item.getLastBlocked() != -1 && item.getLastBlocked() < Game.getUpdate() + Constant.COUNT_BEFORE_REUSE_BLOCKED_ITEM) {
			return false;
		}

		// Item is not completed
		if (!item.isComplete()) {
			return false;
		}

		// Item don't match filter
		if (!item.matchFilter(filter)) {
			return false;
		}

		return true;
	}

	public ConsumableModel getNearest(ItemInfo info, ParcelModel parcel) {
		return getNearest(info, parcel.x, parcel.y);
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
		return getRandomNearest(filter, character.getX(), character.getY());
	}

	public MapObjectModel getRandomNearest(ItemFilter filter, int x, int y) {
        ParcelModel fromParcel = _worldModule.getParcel(x, y);
        List<? extends MapObjectModel> list = filter.needConsumable ? _consumables : _items;

        // Get matching items
        int start = (int) (Math.random() * list.size());
        int length = list.size();
        int bestDistance = Integer.MAX_VALUE;
        Map<MapObjectModel, Integer> ObjectsMatchingFilter = new HashMap<>();
        for (int i = 0; i < length; i++) {
            MapObjectModel mapObject = list.get((i + start) % length);
            int distance = Math.abs(mapObject.getX() - x) + Math.abs(mapObject.getY() - y);
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
