package alone.in.deepspace.model.item;

import alone.in.deepspace.model.job.Job;

public class ItemSlot {
	private Job			_job;
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

	public void take(Job job) {
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

	public Job getJob() {
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