package alone.in.deepspace.model;

import java.util.List;


public class UserItem extends BaseItem {
	public UserItem(ItemInfo info, int id) {
		super(info, id);
	}

	public UserItem(ItemInfo info) {
		super(info);
	}

	public void addInventory(List<BaseItem> items) {
		_inventory.addAll(items);
	}

}
