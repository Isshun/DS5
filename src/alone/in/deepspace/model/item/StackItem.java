package alone.in.deepspace.model.item;

import java.util.ArrayList;
import java.util.List;

import alone.in.deepspace.Game;
import alone.in.deepspace.util.Log;

public class StackItem extends UserItem {
	private static final int MAX_SIZE = 10;
	
	private List<UserItem>	_items;
	private ItemInfo		_type;
	private int 			_size;
	
	public StackItem() {
		super(Game.getData().getItemInfo("base.stack"));

		_items = new ArrayList<UserItem>();
	}

	@Override
	public boolean isStack() {
		return true;
	}

	public boolean contains(ItemInfo info) {
		return _type == info;
	}

	public boolean hasSpaceLeft() {
		return _items.size() < MAX_SIZE;
	}

	public void add(UserItem item) {
		if (_type != null && _type != item.getInfo()) {
			Log.error("Cannot add UserItem to Stack: type not match");
			return;
		}
		
		_items.add(item);
		_type = item.getInfo();
		_size++;
	}
	
	/**
	 * @return Number of item contained in stack
	 */
	public int 		size() { return _size; }
	public ItemInfo	getType() { return _type; }
}
