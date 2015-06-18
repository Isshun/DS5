package org.smallbox.faraway.game.model.item;

import org.smallbox.faraway.game.model.room.RoomModel;
import org.smallbox.faraway.ui.AreaModel;

public class ParcelModel {
	private ItemModel 		_item;
	private ConsumableModel _consumable;
	private StructureModel 	_structure;
	private ResourceModel 	_resource;
	private int				_oxygen;
	private int				_x;
	private int				_y;
	private int 			_z;
	private double 			_light;
	private int 			_lightPass;
	private RoomModel 		_room;
	private boolean 		_isStorage;
	private int				_lightSource;
	private AreaModel 		_area;
	private double 			_rubble;
	private double 			_dirt;
	private double 			_blood;
	private double 			_snow;

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
	public void				setLightSource(int value) { _lightSource = value; }
	public void 			setArea(AreaModel area) { _area = area; }
	public void 			setBlood(double blood) { _blood = blood; }
	public void 			setDirt(double dirt) { _dirt = dirt; }
	public void 			setRubble(double rubble) { _rubble = rubble; }
	public void 			setSnow(double snow) { _snow = snow; }

	public ItemModel 		getItem() { return _item; }
	public StructureModel 	getStructure() { return _structure; }
	public ResourceModel 	getResource() { return _resource; }
	public int				getOxygen() { return _oxygen; }
	public int				getX() { return _x; }
	public int				getY() { return _y; }
	public int				getZ() { return _z; }
	public double 			getLight() { return _light; }
	public int 				getLightPass() { return _lightPass; }
	public RoomModel 		getRoom() { return _room; }
	public double 			getBlood() { return _blood; }
	public double 			getDirt() { return _dirt; }
	public double 			getRubble() { return _rubble; }
	public double 			getSnow() { return _snow; }
	public int				getLightSource() { return _lightSource; }
	public ConsumableModel 	getConsumable() { return _consumable; }
	public AreaModel 		getArea() { return _area; }
	public boolean			isStorage() { return _isStorage; }
	public boolean 			isEmpty() { return _resource == null && _item == null && _structure == null; }
	public boolean 			canSupportRoof() { return (_structure != null && (_structure.isWall() || _structure.isDoor())) || (_resource != null && _resource.isRock()); }
	public boolean			hasLightSource() { return _lightSource > 0; }
	public boolean 			hasSnow() { return _snow > 0; }
	public boolean 			hasRubble() { return _rubble > 0; }
	public boolean 			hasDirt() { return _dirt > 0; }
	public boolean 			hasBlood() { return _blood > 0; }

	public void				setItem(ItemModel item) {
		_item = item;
		if (item != null) {
			item.setParcel(this);
			item.setX(_x);
			item.setY(_y);
		}
	}

	public void				setStructure(StructureModel structure) {
		_structure = structure;
		if (structure != null) {
			structure.setParcel(this);
			structure.setX(_x);
			structure.setY(_y);
		}
	}

	public void 			setResource(ResourceModel resource) {
		_resource = resource;
		if (resource != null) {
			resource.setParcel(this);
			resource.setX(_x);
			resource.setY(_y);
		}
	}

	public void 			setConsumable(ConsumableModel consumable) {
		_consumable = consumable;
		if (consumable != null) {
			consumable.setParcel(this);
			consumable.setX(_x);
			consumable.setY(_y);
		}
	}

	public ItemModel 		getRootItem() {
		if (_item != null && _item.getX() == _x && _item.getY() == _y) {
			return _item;
		}
		return null;
	}

	public double getSealing() {
		if (_structure != null) {
			return _structure.getSealing();
		}
		if (_resource != null) {
			return _resource.getSealing();
		}
		return 0;
	}
}
