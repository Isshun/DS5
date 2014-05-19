package alone.in.deepspace.model;

import java.util.ArrayList;
import java.util.List;

public class StorageItem extends UserItem {

	private List<BaseItem>	_items;
	
	public StorageItem() {
		
		// TODO
		super(new ItemInfo());
		
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
}
