package alone.in.deepspace.model;

import java.util.ArrayList;
import java.util.List;

import alone.in.deepspace.manager.ItemFilter;

public class StorageItem extends UserItem {

	
	public StorageItem(ItemInfo info) {
		super(info);
	}
	
	public List<BaseItem>	getItems() {
		return _inventory;
	}
	
	public void				addItem(BaseItem item) {
		_inventory.add(item);
	}

	public BaseItem getFirst() {
		if (_inventory.size() > 0) {
			return _inventory.get(0);
		}
		return null;
	}

	public int getNbItems() {
		return _inventory.size();
	}

	public boolean contains(ItemFilter filter) {
		for (BaseItem item: _inventory) {
			if (item.matchFilter(filter)) {
				return true;
			}
		}
		return false;
	}

	public BaseItem get(ItemFilter filter) {
		for (BaseItem item: _inventory) {
			if (item.matchFilter(filter)) {
				return item;
			}
		}
		return null;
	}

	public void remove(BaseItem item) {
		_inventory.remove(item);
	}
}
