package alone.in.deepspace.model.room;

import java.util.ArrayList;
import java.util.List;

import alone.in.deepspace.manager.ResourceManager;
import alone.in.deepspace.model.item.ItemBase;
import alone.in.deepspace.model.item.ItemFilter;
import alone.in.deepspace.model.item.ItemInfo;
import alone.in.deepspace.model.item.UserItem;
import alone.in.deepspace.model.item.WorldArea;

public class StorageRoom extends Room {
	protected List<UserItem>	_inventory;
	private List<ItemInfo>		_accepts;
	private boolean 			_acceptFood;
	private boolean 			_acceptDrink;
	private boolean 			_acceptConsomable;
	private boolean 			_acceptGarbage;

	public StorageRoom() {
		super(Type.STORAGE);
		init();
	}

	public StorageRoom(int id) {
		super(id, Type.STORAGE);
		init();
	}

	private void init() {
		_inventory = new ArrayList<UserItem>();
		
		_acceptConsomable = true;
		_acceptDrink = true;
		_acceptFood = true;
		_acceptGarbage = true;

		//		if (info.onAction != null && info.onAction.storage > 0) {
		//			_accepts = new ArrayList<ItemInfo>();
		//			for (ItemInfo itemProduce: info.onAction.itemsProduce) {
		//				_accepts.addAll(itemProduce.craftedFromItems);
		//			}
		//		}

	}

	@Override
	public void update() {
		//		_nbStorage = 0;
		//		_nbBed = 0;
		//		
		//		for (WorldArea area: _areas) {
		//			UserItem item = area.getItem();
		//			if (item != null) {
		//				if (item.isBed()) { _nbBed++; }
		//				if (item.isStorage()) { _nbStorage++; }
		//			}
		//		}
		//		
		//		_entryBed.label = StringUtils.getDashedString("Bed", String.valueOf(_nbBed));
		//		_entryStorage.label = StringUtils.getDashedString("Storage", String.valueOf(_nbStorage));
	}

	//	public List<RoomOption> getOptions() {
	////		return _options;
	//	}

	public List<UserItem>	getItems() {
		return _inventory;
	}

	public ItemBase getFirst() {
		if (_inventory.size() > 0) {
			return _inventory.get(0);
		}
		return null;
	}

	public int getNbItems() {
		return _inventory.size();
	}

	public boolean contains(ItemFilter filter) {
		for (ItemBase item: _inventory) {
			if (item.matchFilter(filter)) {
				return true;
			}
		}
		return false;
	}

	public UserItem get(ItemFilter filter) {
		for (UserItem item: _inventory) {
			if (item.matchFilter(filter)) {
				return item;
			}
		}
		return null;
	}

	public void remove(ItemBase item) {
		_inventory.remove(item);
		
		for (WorldArea area: _areas) {
			if (area.getItem() == item) {
				area.setItem(null);
			}
		}
	}

	public void addInventory(List<UserItem> items) {
		for (UserItem item: items) {
			addInventory(item);
		}
	}

	public void addInventory(UserItem item) {
		_inventory.add(item);

		for (WorldArea area: _areas) {
			if (area.getItem() == null) {
				area.setItem(item);
				break;
			}
		}
		
		if (item.isFood()) {
			ResourceManager.getInstance().addFood(1);
		}
	}

	private boolean inventoryContains(ItemInfo info) {
		for (ItemBase item: _inventory) {
			if (item.getInfo() == info) {
				return true;
			}
		}
		return false;
	}

	public List<UserItem> getInventory() {
		return _inventory;
	}

	protected ItemBase takeFromInventory(ItemInfo itemInfo) {
		for (ItemBase item: _inventory) {
			if (item.getInfo() == itemInfo) {
				_inventory.remove(item);
				return item;
			}
		}
		return null;
	}

	public void setStorageFilter(boolean acceptFood, boolean acceptDrink, boolean acceptConsomable, boolean acceptGarbage) {
		_acceptFood = acceptFood;
		_acceptDrink = acceptDrink;
		_acceptConsomable = acceptConsomable;
		_acceptGarbage = acceptGarbage;
	}

	public boolean acceptFood() { return _acceptFood; }
	public boolean acceptDrink() { return _acceptDrink; }
	public boolean acceptConsomable() { return _acceptConsomable; }
	public boolean acceptGarbage() { return _acceptGarbage; }

	public boolean accept(ItemBase item) {
		if (item == null) {
			return false;
		}

		if (!_acceptFood && item.isFood()) {return false; }
		if (!_acceptDrink && item.isDrink()) {return false; }
		if (!_acceptConsomable && item.isConsomable()) {return false; }
		if (!_acceptGarbage && item.isGarbage()) {return false; }
		if (_accepts != null && !_accepts.isEmpty() && !_accepts.contains(item.getInfo())) { return false; }

		return true;
	}

	@Override
	public void addArea(WorldArea area) {
		super.addArea(area);
		area.setStorage(true);
	}

	@Override
	public void removeArea(WorldArea area) {
		super.removeArea(area);
		area.setStorage(false);
	}
}
