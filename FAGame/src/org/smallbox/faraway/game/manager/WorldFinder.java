package org.smallbox.faraway.game.manager;

import org.smallbox.faraway.PathHelper;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.model.GameData;
import org.smallbox.faraway.game.model.character.base.CharacterModel;
import org.smallbox.faraway.game.model.item.*;
import org.smallbox.faraway.util.Constant;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorldFinder extends BaseManager {
	private int 					_width;
	private int 					_height;
	private WorldManager 			_worldManager;
	private PathHelper 				_pathManager;
	private List<ConsumableModel> 	_consumables;
	private List<ItemModel> 		_items;

    @Override
    protected void onCreate() {
        _items = new ArrayList<>();
        _consumables = new ArrayList<>();
        _pathManager = (PathHelper) Game.getInstance().getManager(PathHelper.class);
        _worldManager = (WorldManager) Game.getInstance().getManager(WorldManager.class);
        _width = _worldManager.getWidth();
        _height = _worldManager.getHeight();
    }

    public MapObjectModel getNearest(ItemFilter filter, CharacterModel character) {
		int startX = character.getX();
		int startY = character.getY();
		int maxX = Math.max(startX, _width - startX);
		int maxY = Math.max(startY, _height - startY);
		for (int offsetX = 0; offsetX < maxX; offsetX++) {
			for (int offsetY = 0; offsetY < maxY; offsetY++) {
				ParcelModel area = _worldManager.getParcel(startX + offsetX, startY + offsetY);

				// Check on non-existing area
				if (area == null) {
					continue;
				}

//				// Private room exists and character is not allowed
//				if (area.getRoom() != null && area.getRoom().isPrivate() && area.getRoom().getOccupants().contains(character) == false) {
//					continue;
//				}

				if (filter.isConsomable) {
					ConsumableModel consumable = _worldManager.getConsumable(startX + offsetX, startY + offsetY);
                    if (getNearestItemCheck(consumable, filter)) { return consumable; }

                    consumable = _worldManager.getConsumable(startX - offsetX, startY - offsetY);
                    if (getNearestItemCheck(consumable, filter)) { return consumable; }

                    consumable = _worldManager.getConsumable(startX + offsetX, startY - offsetY);
                    if (getNearestItemCheck(consumable, filter)) { return consumable; }

                    consumable = _worldManager.getConsumable(startX - offsetX, startY + offsetY);
                    if (getNearestItemCheck(consumable, filter)) { return consumable; }
				} else {
					ItemModel item = _worldManager.getItem(startX + offsetX, startY + offsetY);
					if (getNearestItemCheck(item, filter)) { return item; }

					item = _worldManager.getItem(startX - offsetX, startY - offsetY);
					if (getNearestItemCheck(item, filter)) { return item; }

					item = _worldManager.getItem(startX + offsetX, startY - offsetY);
					if (getNearestItemCheck(item, filter)) { return item; }

					item = _worldManager.getItem(startX - offsetX, startY + offsetY);
					if (getNearestItemCheck(item, filter)) { return item; }
				}
			}
		}
		return null;
	}

	private boolean getNearestItemCheck(MapObjectModel item, ItemFilter filter) {
		// Item not exists
		if (item == null) {
			return false;
		}

        if (item.getQuantity() <= 0) {
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

	public ConsumableModel getNearest(ItemInfo info, int startX, int startY) {
		int maxX = Math.max(startX, _width - startX);
		int maxY = Math.max(startY, _height - startY);
		for (int offsetX = 0; offsetX < maxX; offsetX++) {
			for (int offsetY = 0; offsetY < maxY; offsetY++) {
				ParcelModel area = _worldManager.getParcel(startX + offsetX, startY + offsetY);
				if (area != null && area.getConsumable() != null && area.getConsumable().getInfo() == info) {
					return area.getConsumable();
				}
				area = _worldManager.getParcel(startX - offsetX, startY + offsetY);
				if (area != null && area.getConsumable() != null && area.getConsumable().getInfo() == info) {
					return area.getConsumable();
				}
				area = _worldManager.getParcel(startX + offsetX, startY - offsetY);
				if (area != null && area.getConsumable() != null && area.getConsumable().getInfo() == info) {
					return area.getConsumable();
				}
				area = _worldManager.getParcel(startX - offsetX, startY - offsetY);
				if (area != null && area.getConsumable() != null && area.getConsumable().getInfo() == info) {
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
        ParcelModel fromParcel = _worldManager.getParcel(x, y);
        List<? extends MapObjectModel> list = filter.isConsomable ? _consumables : _items;

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
