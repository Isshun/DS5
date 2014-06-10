package alone.in.deepspace.model.item;

import alone.in.deepspace.model.room.Room;

public class WorldArea {
	private UserItem 		_item;
	private StructureItem 	_structure;
	private WorldResource 	_ressource;
	private int				_oxygen;
	private int				_x;
	private int				_y;
	private double 			_light;
	private int 			_lightPass;
	private int 			_z;
	private Room 			_room;
	private boolean 		_isStorage;

	public WorldArea(int x, int y, int z) {
		_oxygen = (int) (Math.random() % 100);
		_light = 0;
		_x = x;
		_y = y;
		_z = z;
		_isStorage = false;
	}

	public void 			addLight(double value) { _light += value; }

	public void				setOxygen(int oxygen) { _oxygen = oxygen; }
	public void 			setLight(double value) { _light = value; }
	public void 			setLightPass(int pass) { _lightPass = pass; }
	public void 			setRoom(Room room) { _room = room; }
	public void 			setStorage(boolean isStorage) { _isStorage = isStorage; }

	public UserItem			getItem() { return _item; }
	public StructureItem	getStructure() { return _structure; }
	public WorldResource	getRessource() { return _ressource; }
	public int				getOxygen() { return _oxygen; }
	public int				getX() { return _x; }
	public int				getY() { return _y; }
	public int				getZ() { return _z; }
	public double 			getLight() { return _light; }
	public int 				getLightPass() { return _lightPass; }
	public Room				getRoom() { return _room; }
	public boolean			isStorage() { return _isStorage; }

	public void				setItem(UserItem item) {
		_item = item;
		if (item != null) {
			item.setArea(this);
			item.setX(_x);
			item.setY(_y);
		}
	}

	public void				setStructure(StructureItem structure) {
		_structure = structure;
		if (structure != null) {
			structure.setArea(this);
			structure.setX(_x);
			structure.setY(_y);
		}
	}

	public void				setRessource(WorldResource ressource) {
		_ressource = ressource;
		if (ressource != null) {
			ressource.setArea(this);
			ressource.setX(_x);
			ressource.setY(_y);
		}
	}
}
