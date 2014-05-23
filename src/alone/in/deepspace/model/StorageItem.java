package alone.in.deepspace.model;

import java.util.ArrayList;
import java.util.List;

import alone.in.deepspace.manager.ItemFilter;

public class StorageItem extends UserItem {

	private List<BaseItem>	_items;
	
	public StorageItem(ItemInfo info) {
		super(info);
		
		_items = new ArrayList<BaseItem>();
	}
	
	public List<BaseItem>	getItems() {
		return _items;
	}
	
	public void				addItem(BaseItem item) {
		_items.add(item);
	}

	public BaseItem getFirst() {
		if (_items.size() > 0) {
			return _items.get(0);
		}
		return null;
	}

	public int getNbItems() {
		return _items.size();
	}

	public boolean contains(ItemFilter filter) {
		for (BaseItem item: _items) {
			if (item.getInfo().matchFilter(filter)) {
				return true;
			}
		}
		return false;
	}

	public BaseItem get(ItemFilter filter) {
		for (BaseItem item: _items) {
			if (item.getInfo().matchFilter(filter)) {
				return item;
			}
		}
		return null;
	}

	public void remove(BaseItem item) {
		_items.remove(item);
	}
}
