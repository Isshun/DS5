package org.smallbox.faraway.game.model.item;

import org.smallbox.faraway.game.model.job.BaseJobModel;


public class ResourceModel extends ItemModel {
	private double 	        _quantity;
	private int 	        _tile;
	private int 	        _totalQuantity;
	private BaseJobModel    _job;
    private double          _growRate;

    public ResourceModel(ItemInfo info, int id) {
		super(info, id);

		if (_info.plant != null) {
			_totalQuantity = _info.plant.mature;
		}
    }

	public ResourceModel(ItemInfo info) {
		super(info);

		if (_info.plant != null) {
			_totalQuantity = _info.plant.mature;
		}
    }

	public void addQuantity(double quantity) {
		_quantity = Math.max(0, _quantity + quantity);
		_needRefresh = true;
	}

	public void	setValue(double quantity) {
		_quantity = quantity;
		_needRefresh = true;
	}
	
	public double getQuantity(int max) {return Math.min(_quantity, max);}

	public double getTotalQuantity() {return _totalQuantity;}

	public int getQuantity() { return (int)_quantity; }

	public double getRealQuantity() { return _quantity; }

	public boolean isRock() {
		return _info.isRock;
	}

	public boolean isSolid() {
		return _info.isRock;
	}

	public void setTile(int tile) {
		_tile = tile;
	}

	public int getTile() {
		return _tile;
	}

	public boolean canBeMined() {
		return _info.isRock;
	}

	public boolean canBeHarvested() {
		return _info.isPlant;
	}

    public boolean isDepleted() {
		return _quantity < 1;
	}

	public boolean isMature() {
		return _quantity >= _totalQuantity;
	}

	public void setJob(BaseJobModel job) {
		_job = job;
	}

	public boolean hasNoJob() {
		return _job == null;
	}

    public void setGrowRate(double growRate) {
        _growRate = growRate;
    }

    public double getGrowRate() {
        return _growRate;
    }
}
