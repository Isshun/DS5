package org.smallbox.faraway.model.item;

import org.smallbox.faraway.model.job.BaseJob;

public class ItemSlot {
	private BaseJob _job;
	private ItemBase	_item;
	private int			_relX;
	private int			_relY;
	private boolean		_isFree;

	public ItemSlot(ItemBase item, int x, int y) {
		_item = item;
		_relX = x;
		_relY = y;
		_isFree = true;
	}

	public void take(BaseJob job) {
		_job = job;
		_isFree = false;
	}

	public boolean isFree() {
		return _isFree;
	}

	public int getX() {
		return _item.getX() + _relX;
	}

	public int getY() {
		return _item.getY() + _relY;
	}

	public BaseJob getJob() {
		return _job;
	}

	public ItemBase getItem() {
		return _item;
	}

	public void free() {
		_job = null;
		_isFree = true;
	}
}
