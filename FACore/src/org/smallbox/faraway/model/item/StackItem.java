package org.smallbox.faraway.model.item;

import java.util.ArrayList;
import java.util.List;

import org.smallbox.faraway.Game;
import org.smallbox.faraway.engine.util.Log;

public class StackItem extends UserItem {
	private static final int MAX_SIZE = 10;
	
	private List<UserItem>	_items;
	private ItemInfo		_stackedInfo;
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
		return _stackedInfo == info;
	}

	public boolean hasSpaceLeft() {
		return _items.size() < MAX_SIZE;
	}

	public void add(UserItem item) {
		if (_stackedInfo != null && _stackedInfo != item.getInfo()) {
			Log.error("Cannot add UserItem to Stack: type not match");
			return;
		}
		
		_items.add(item);
		_stackedInfo = item.getInfo();
		_size++;
	}
	
	public UserItem take() {
		if (_items.isEmpty()) {
			Log.error("Cannot take item on empty stack");
			return null;
		}
		
		_size--;
		return _items.get(0);
	}
	
	/**
	 * @return Number of item contained in stack
	 */
	public int 		size() { return _size; }
	public ItemInfo	getStackedInfo() { return _stackedInfo; }
	
	@Override
	public boolean matchFilter(ItemFilter filter) {
		// Stack is empty
		if (_size == 0) {
			return false;
		}
		
		// Filter need free slots
		if (filter.needFreeSlot) {
			return false;
		}
		
		// Filter looking for item
		if (filter.lookingForItem) {

			// Filter on item
			if (filter.itemNeeded == _stackedInfo) {
				filter.itemMatched = _stackedInfo;
				return true;
			}

			if (_stackedInfo.onAction != null && _stackedInfo.matchFilter(_stackedInfo.onAction.effects, filter)) {
				filter.itemMatched = _stackedInfo;
				return true;
			}
		}
		
		return false;
	}
}
