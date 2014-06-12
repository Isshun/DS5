package alone.in.deepspace.manager;

import alone.in.deepspace.Game;
import alone.in.deepspace.model.character.Character;
import alone.in.deepspace.model.item.ItemFilter;
import alone.in.deepspace.model.item.UserItem;
import alone.in.deepspace.model.item.WorldArea;
import alone.in.deepspace.util.Constant;

public class WorldFinder {
	
	private int 				_width;
	private int 				_height;
	private WorldArea[][][] 	_areas;
	private WorldManager 		_worldManager;

	public WorldFinder(WorldManager worldManager, WorldArea[][][] areas) {
		_areas = areas;
		_width = worldManager.getWidth();
		_height = worldManager.getHeight();
		_worldManager = worldManager;
		Game.setWorldFinder(this);
	}

	public UserItem getNearest(ItemFilter filter, Character character) {
		int startX = character.getX();
		int startY = character.getY();
		int maxX = Math.max(startX, _width - startX);
		int maxY = Math.max(startY, _height - startY);
		for (int offsetX = 0; offsetX < maxX; offsetX++) {
			for (int offsetY = 0; offsetY < maxY; offsetY++) {
				WorldArea area = _worldManager.getArea(startX + offsetX, startY + offsetY);

				// Check on non-existing area
				if (area == null) {
					continue;
				}
				
				// Private room exists and character is not allowed
				if (area.getRoom() != null && area.getRoom().isPrivate() && area.getRoom().getOccupants().contains(character) == false) {
					continue;
				}
				
				UserItem item = _worldManager.getItem(startX + offsetX, startY + offsetY);
				if (getNearestItemCheck(item, filter)) { return item; }

				item = _worldManager.getItem(startX - offsetX, startY - offsetY);
				if (getNearestItemCheck(item, filter)) { return item; }

				item = _worldManager.getItem(startX + offsetX, startY - offsetY);
				if (getNearestItemCheck(item, filter)) { return item; }

				item = _worldManager.getItem(startX - offsetX, startY + offsetY);
				if (getNearestItemCheck(item, filter)) { return item; }
			}
		}
		return null;
	}

	private boolean getNearestItemCheck(UserItem item, ItemFilter filter) {
		// Item not exists
		if (item == null) {
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
	public UserItem 			find(ItemFilter filter) {
		for (int x = 0; x < _width; x++) {
			for (int y = 0; y < _height; y++) {
				UserItem item = _areas[x][y][0].getItem();
				if (item != null && item.matchFilter(filter)) {
					return item;
				}
			}
		}
		return null;
	}
}
