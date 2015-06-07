package org.smallbox.faraway.model.item;

import org.smallbox.faraway.model.job.BaseJob;


public class WorldResource extends UserItem {
	private double 	_quantity;
	private int 	_tile;
	private int 	_doubleRender;
	private BaseJob _job;

	public WorldResource(ItemInfo info, int id) {
		super(info, id);
	}

	public WorldResource(ItemInfo info) {
		super(info);
	}

	public void addQuantity(double quantity) {
		_quantity += quantity;}

	public void	setValue(double value) {
		_quantity = value;}
	
	public double getQuantity(int max) {return Math.min(_quantity, max);}

	@Override
	public int getQuantity() { return (int)_quantity; }
	

	public boolean isRock() {
		return "base.res_rock".equals(_info.name);
	}

	public void setTile(int tile) {
		_tile = tile;
	}

	public int getTile() {
		return _tile;
	}

	public void setDoubleRender(int b) {
		_doubleRender = b;
	}

	public int getDoubleRender() {
		return _doubleRender;
	}

	public boolean canBeMined() {
		return "base.res_rock".equals(_info.name);
	}

	public boolean canBeHarvested() {
		return _info.name.contains("base.seaweed");
	}

	public int gatherMatter(int maxValue) {
		int value = (int)Math.min(maxValue, _quantity);
		_quantity -= value;
		return value;
	}

	public boolean isDepleted() {
		return _quantity < 1;
	}

	public boolean isMature() {
		return _quantity >= _info.actions.get(0).mature;
	}

	public void setJob(BaseJob job) {
		_job = job;
	}

	public boolean hasNoJob() {
		return _job == null;
	}

	public boolean isGrass() {
		return "base.grass".equals(_info.name);
	}

}
