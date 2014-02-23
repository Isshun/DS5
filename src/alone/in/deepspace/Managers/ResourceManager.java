package alone.in.deepspace.Managers;

import alone.in.deepspace.Models.Room;
import alone.in.deepspace.Models.Room.Type;
import alone.in.deepspace.World.BaseItem;
import alone.in.deepspace.World.StructureItem;
import alone.in.deepspace.World.WorldMap;
import alone.in.deepspace.World.WorldRenderer;

public class ResourceManager {

	private static ResourceManager _self;

	int	_o2Use;
	int	_o2Supply;
	int	_matter;
	int	_power;

	private int _spice;

	private int _water;

	public enum Message {NONE, NO_MATTER, BUILD_COMPLETE, BUILD_PROGRESS};

	private ResourceManager() {
		_matter = 0;
		_power = 0;
		_o2Use = 0;
		_o2Supply = 0;
	}

	public static ResourceManager	getInstance() {
		if (_self == null) {
			_self = new ResourceManager();
		}
		return _self;
	}

	public Message build(BaseItem item) {
		if (_matter == 0) {
			return Message.NO_MATTER;
		}

		WorldRenderer.getInstance().invalidate(item.getX(), item.getY());

		if (item.isComplete() == false) {
			_matter--;
			item.setMatterSupply(item.getMatterSupply() + 1);
		}

		// BUILD_COMPLETE
		if (item.isComplete()) {

			// Remove power use
			if (item.getPower() != 0) {
				item.setPowerSupply(_power >= item.getPower() ? item.getPower() : _power);
				_power -= item.getPower();
			}

			// remove O2 use
			if (item.isType(BaseItem.Type.STRUCTURE_FLOOR)) {
				_o2Use++;
			}

			if (item.isType(BaseItem.Type.ENVIRONMENT_O2_RECYCLER)) {
				_o2Supply += 100;
			}

			// TODO
			if (item.getType() == BaseItem.Type.ARBORETUM_TREE_1 || item.getType() == BaseItem.Type.ARBORETUM_TREE_9) {
				_o2Supply += 10;
			}

			return Message.BUILD_COMPLETE;
		}

		// BUILD_PROGRESS
		else {
			return Message.BUILD_PROGRESS;
		}
	}

	public void update() {
		WorldMap worldmap = WorldMap.getInstance();
		int width = worldmap.getWidth();
		int height = WorldMap.getInstance().getWidth();

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				StructureItem structure = worldmap.getStructure(x, y);
				Room room = RoomManager.getInstance().get(x, y);
				if (structure != null && structure.isType(BaseItem.Type.STRUCTURE_GREENHOUSE) && structure.isWorking() && room != null && room.isType(Type.GARDEN)) {
					worldmap.putItem(BaseItem.Type.RES_1, x, y, 10);
//					WorldRenderer.getInstance().invalidate(x, y);
				}
			}
		}
	}
	
	public void refreshWater() {
		WorldMap worldmap = WorldMap.getInstance();
		int width = worldmap.getWidth();
		int height = WorldMap.getInstance().getWidth();
		int water = _water;

		// Re-active working garden
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				StructureItem structure = worldmap.getStructure(x, y);
				Room room = RoomManager.getInstance().get(x, y);
				if (structure != null && structure.isType(BaseItem.Type.STRUCTURE_GREENHOUSE) && structure.isWorking() && room != null && room.isType(Type.GARDEN)) {
					structure.setWorking(water-- > 0);
					WorldRenderer.getInstance().invalidate(x, y);
				}
			}
		}

		// Active non-working garden
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				StructureItem structure = worldmap.getStructure(x, y);
				Room room = RoomManager.getInstance().get(x, y);
				if (structure != null && structure.isType(BaseItem.Type.STRUCTURE_GREENHOUSE) && structure.isWorking() == false && room != null && room.isType(Type.GARDEN)) {
					structure.setWorking(water-- > 0);
					WorldRenderer.getInstance().invalidate(x, y);
				}
			}
		}

		// Active other areas
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				StructureItem structure = worldmap.getStructure(x, y);
				Room room = RoomManager.getInstance().get(x, y);
				if (structure != null && structure.isType(BaseItem.Type.STRUCTURE_GREENHOUSE) && (room == null || room.isType(Type.GARDEN) == false)) {
					structure.setWorking(water-- > 0);
					WorldRenderer.getInstance().invalidate(x, y);
				}
			}
		}
	}

	public int 	getO2() { return (int) (_o2Use == 0 ? 100 : _o2Supply >= _o2Use ? 100 : _o2Supply * 100.0f / _o2Use); }
	public int 	getMatter() { return _matter; }
	public int 	getPower() { return _power; }
	public int 	getSpice() { return _spice; }
	public int 	getWater() { return _water; }

	public void setSpice(int spice) { _spice = spice; }
	public void setWater(int water) { _water = water; refreshWater(); }
	public void	setMatter(int matter) { _matter = matter; }

	public void addMatter(int value) { _matter += value; }
	public void addWater(int value) { _water += value; refreshWater(); }
	public void addSpice(int value) { _spice += value; }
	public void addPower(int value) { _power += value; }
}
