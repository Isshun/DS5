package org.smallbox.faraway.model.item;

import org.smallbox.faraway.model.room.Room;

public class WorldArea {
	private UserItem 		_item;
	private ConsumableItem 	_consumable;
	private StructureItem 	_structure;
	private WorldResource 	_resource;
	private int				_oxygen;
	private int				_x;
	private int				_y;
	private double 			_light;
	private int 			_lightPass;
	private int 			_z;
	private Room 			_room;
	private boolean 		_isStorage;
	private int				_lightSource;

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
	public WorldResource 	getResource() { return _resource; }
	public int				getOxygen() { return _oxygen; }
	public int				getX() { return _x; }
	public int				getY() { return _y; }
	public int				getZ() { return _z; }
	public double 			getLight() { return _light; }
	public int 				getLightPass() { return _lightPass; }
	public Room				getRoom() { return _room; }
	public boolean			isStorage() { return _isStorage; }
	public boolean			hasLightSource() { return _lightSource > 0; }
	public int				getLightSource() { return _lightSource; }
	public void				setLightSource(int value) { _lightSource = value; }
	
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

	public void 			setResource(WorldResource resource) {
		_resource = resource;
		if (resource != null) {
			resource.setArea(this);
			resource.setX(_x);
			resource.setY(_y);
		}
	}

	public boolean isEmpty() {
		return _resource == null && _item == null && _structure == null;
	}

	public UserItem getRootItem() {
		if (_item != null && _item.getX() == _x && _item.getY() == _y) {
			return _item;
		}
		return null;
	}

	public ConsumableItem getConsumable() {
		return _consumable;
	}

	public void setConsumable(ConsumableItem consumable) {
		_consumable = consumable;
	}
}
