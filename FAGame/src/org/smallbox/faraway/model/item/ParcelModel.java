package org.smallbox.faraway.model.item;

import org.smallbox.faraway.model.room.RoomModel;

public class ParcelModel {
	private ItemModel _item;
	private ConsumableModel _consumable;
	private StructureModel _structure;
	private ResourceModel _resource;
	private int				_oxygen;
	private int				_x;
	private int				_y;
	private double 			_light;
	private int 			_lightPass;
	private int 			_z;
	private RoomModel _room;
	private boolean 		_isStorage;
	private int				_lightSource;

	public ParcelModel(int x, int y, int z) {
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
	public void 			setRoom(RoomModel room) { _room = room; }
	public void 			setStorage(boolean isStorage) { _isStorage = isStorage; }

	public ItemModel getItem() { return _item; }
	public StructureModel getStructure() { return _structure; }
	public ResourceModel getResource() { return _resource; }
	public int				getOxygen() { return _oxygen; }
	public int				getX() { return _x; }
	public int				getY() { return _y; }
	public int				getZ() { return _z; }
	public double 			getLight() { return _light; }
	public int 				getLightPass() { return _lightPass; }
	public RoomModel getRoom() { return _room; }
	public boolean			isStorage() { return _isStorage; }
	public boolean			hasLightSource() { return _lightSource > 0; }
	public int				getLightSource() { return _lightSource; }
	public void				setLightSource(int value) { _lightSource = value; }
	public boolean 			canSupportRoof() { return (_structure != null && (_structure.isWall() || _structure.isDoor())) || (_resource != null && _resource.isRock()); }

	public void				setItem(ItemModel item) {
		_item = item;
		if (item != null) {
			item.setArea(this);
			item.setX(_x);
			item.setY(_y);
		}
	}

	public void				setStructure(StructureModel structure) {
		_structure = structure;
		if (structure != null) {
			structure.setArea(this);
			structure.setX(_x);
			structure.setY(_y);
		}
	}

	public void 			setResource(ResourceModel resource) {
		_resource = resource;
		if (resource != null) {
			resource.setArea(this);
			resource.setX(_x);
			resource.setY(_y);
		}
	}

	public void setConsumable(ConsumableModel consumable) {
		_consumable = consumable;
		if (consumable != null) {
			consumable.setArea(this);
			consumable.setX(_x);
			consumable.setY(_y);
		}
	}

	public boolean isEmpty() {
		return _resource == null && _item == null && _structure == null;
	}

	public ItemModel getRootItem() {
		if (_item != null && _item.getX() == _x && _item.getY() == _y) {
			return _item;
		}
		return null;
	}

	public ConsumableModel getConsumable() {
		return _consumable;
	}
}
