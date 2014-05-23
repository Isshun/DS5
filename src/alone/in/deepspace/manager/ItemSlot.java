package alone.in.deepspace.manager;

import alone.in.deepspace.model.BaseItem;
import alone.in.deepspace.model.Job;

public class ItemSlot {
	private Job			_job;
	private BaseItem	_item;
	private int			_relX;
	private int			_relY;

	public ItemSlot(BaseItem item, int x, int y) {
		_item = item;
		_relX = x;
		_relY = y;
	}

	public void take(Job job) {
		_job = job;
	}

	public void release() {
		_job = null;
		_item.releaseSlot(this);
	}

	public boolean isFree() {
		return _job == null;
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
}
