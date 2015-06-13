package org.smallbox.faraway.manager;

import org.smallbox.faraway.Game;
import org.smallbox.faraway.engine.util.Constant;
import org.smallbox.faraway.model.character.CharacterModel;
import org.smallbox.faraway.model.item.*;

public class WorldFinder {

	private int 				_width;
	private int 				_height;
	private ParcelModel[][][] 	_areas;
	private WorldManager 		_worldManager;

	public WorldFinder(WorldManager worldManager, ParcelModel[][][] areas) {
		_areas = areas;
		_width = worldManager.getWidth();
		_height = worldManager.getHeight();
		_worldManager = worldManager;
		Game.setWorldFinder(this);
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
		if (item.isComplete() == false) {
			return false;
		}

		// Item don't match filter
		if (item.matchFilter(filter) == false) {
			return false;
		}

		return true;
	}

	// TODO
	public ItemModel find(ItemFilter filter) {
		for (int x = 0; x < _width; x++) {
			for (int y = 0; y < _height; y++) {
				ItemModel item = _areas[x][y][0].getItem();
				if (item != null && item.matchFilter(filter)) {
					return item;
				}
			}
		}
		return null;
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
}
