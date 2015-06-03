package org.smallbox.faraway.model.item;

import org.smallbox.faraway.model.job.BaseJob;


public class WorldResource extends UserItem {
	private double	_value;
	private int 	_tile;
	private int 	_doubleRender;
	private BaseJob _job;

	public WorldResource(ItemInfo info, int id) {
		super(info, id);
	}

	public WorldResource(ItemInfo info) {
		super(info);
	}

	public void addValue(double d) {_value += d;}

	public void	setValue(double value) {_value = value;}
	
	public double getValue(int max) {return Math.min(_value, max);}
	public double getValue() { return _value; }
	

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
		int value = (int)Math.min(maxValue, _value);
		_value -= value;
		return value;
	}

	public boolean isDepleted() {
		return _value < 1;
	}

	public boolean isMature() {
		return _value >= _info.actions.get(0).mature;
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
