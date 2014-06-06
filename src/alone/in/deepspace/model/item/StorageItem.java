package alone.in.deepspace.model.item;

import java.util.ArrayList;
import java.util.List;

import alone.in.deepspace.manager.ItemFilter;
import alone.in.deepspace.manager.ResourceManager;

public class StorageItem extends UserItem {
	protected List<ItemBase>	_inventory;
	private List<ItemInfo>		_accepts;
	private boolean 			_acceptFood;
	private boolean 			_acceptDrink;
	private boolean 			_acceptConsomable;
	private boolean 			_acceptGarbage;
	
	public StorageItem(ItemInfo info) {
		super(info);
		
		_inventory = new ArrayList<ItemBase>();
		
		if (info.onAction != null && info.onAction.storage > 0) {
			_accepts = new ArrayList<ItemInfo>();
			for (ItemInfo itemProduce: info.onAction.itemsProduce) {
				_accepts.addAll(itemProduce.craftedFromItems);
			}
		}
	}
	
	public List<ItemBase>	getItems() {
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

	public ItemBase get(ItemFilter filter) {
		for (ItemBase item: _inventory) {
			if (item.matchFilter(filter)) {
				return item;
			}
		}
		return null;
	}

	public void remove(ItemBase item) {
		_inventory.remove(item);
	}
	
	public void addInventory(List<ItemBase> items) {
		_inventory.addAll(items);
	}

	public void addInventory(UserItem item) {
		_inventory.add(item);
		
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

	public List<ItemBase> getInventory() {
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

	@Override
	public boolean matchFilter(ItemFilter filter) {
		if (super.matchFilter(filter)) {
			return true;
		}

		// Item produce
		if (filter.isFactory && _info.onAction != null && _info.onAction.itemsProduce != null) {
			for (ItemInfo itemProduce: _info.onAction.itemsProduce) {
				if (itemProduce != null && itemProduce.onAction != null && (filter.itemNeeded == itemProduce || _info.matchFilter(itemProduce.onAction.effects, filter))) {
					// Have components
					for (ItemInfo component: itemProduce.craftedFromItems) {
						if (inventoryContains(component)) {
							filter.itemMatched = itemProduce;
							return true;
						}
					}
				}
			}
		}
		
		return false;
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

}
