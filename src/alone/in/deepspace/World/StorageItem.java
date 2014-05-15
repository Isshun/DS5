package alone.in.deepspace.World;

import java.util.ArrayList;
import java.util.List;

public class StorageItem extends UserItem {

	private List<UserItem>	_items;
	
	public StorageItem() {
		super(Type.SPECIAL_STORAGE);
		
		_items = new ArrayList<UserItem>();
	}
	
	public List<UserItem>	getItems() {
		return _items;
	}
	
	public void				addItem(UserItem item) {
		_items.add(item);
	}

	public UserItem getFirst() {
		if (_items.size() > 0) {
			return _items.get(0);
		}
		return null;
	}

	public int getNbItems() {
		return _items.size();
	}
}
