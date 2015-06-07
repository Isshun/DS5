package org.smallbox.faraway.model.item;

public class UserItem extends ItemBase {

	public UserItem(ItemInfo info, int id) { super(info, id); }

	public UserItem(ItemInfo info) {
		super(info);
	}

	public boolean isBed() {
		return _info.isBed;
	}

	public boolean isLight() {
		return _info.light > 0;
	}
}
