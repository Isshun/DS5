package alone.in.deepspace.model.item;

public class UserItem extends ItemBase {
	public UserItem(ItemInfo info, int id) {
		super(info, id);
	}

	public UserItem(ItemInfo info) {
		super(info);
	}

	public boolean isBed() {
		return _info.onAction != null && _info.onAction.effects != null && _info.onAction.effects.energy > 0;
	}

	public boolean isStack() {
		return false;
	}
}
