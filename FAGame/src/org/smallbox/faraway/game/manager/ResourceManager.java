package org.smallbox.faraway.game.manager;

import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.Strings;
import org.smallbox.faraway.game.model.ToolTips;
import org.smallbox.faraway.game.model.item.ItemInfo;

public class ResourceManager extends BaseManager {

	private static ResourceManager _self;

	private ResourceData	_matter;
	private ResourceData	_power;
	private ResourceData	_food;
	private ResourceData 	_water;
	private ResourceData 	_gasoline;
	private ResourceData 	_spice;

	private ResourceData _oxygen;

	public enum Message {NONE, NO_MATTER, BUILD_COMPLETE, BUILD_PROGRESS};

	public ResourceManager() {
		_matter = new ResourceData(Strings.LB_MATTER, ToolTips.RES_MATTER);
		_power = new ResourceData(Strings.LB_POWER, ToolTips.RES_POWER);
		_spice = new ResourceData("spice", ToolTips.RES_SPICE);
		_food = new ResourceData(Strings.LB_FOOD, ToolTips.RES_FOOD);
		_gasoline = new ResourceData(Strings.LB_GASOLINE, ToolTips.RES_GASOLINE);
		_water = new ResourceData(Strings.LB_WATER, ToolTips.RES_WATER);
		_oxygen = new ResourceData("o2", ToolTips.RES_OXYGEN);
	}

	public static ResourceManager	getInstance() {
		if (_self == null) {
			_self = new ResourceManager();
		}
		return _self;
	}

	// TODO
	public void refreshWater() {
	}

	public ResourceData getMatter() { return _matter; }
	public ResourceData	getPower() { return _power; }
	public ResourceData	getSpice() { return _spice; }
	public ResourceData	getWater() { return _water; }
	public ResourceData getFood() { return _food; }

	public void setSpice(int spice) { _spice.value = spice; }
	public void setWater(int water) { _water.value = water; refreshWater(); }
	public void	setMatter(int matter) { _matter.value = matter; }

	public void addMatter(int value) { _matter.value += value; }
	public void addWater(int value) { _water.value += value; refreshWater(); }

	public boolean isLowFood() {
		return _food.value < Game.getCharacterManager().getCount();
	}

	public void remove(ItemInfo info) {
		if (info.isFood) { _food.value--; }
	}

	public void add(ItemInfo info) {
		if (info.isFood) { _food.value++; }
	}

	@Override
	protected void onUpdate(int tick) {
	}

	public ResourceData getGasoline() {
		return _gasoline;
	}

	public ResourceData getO2() {
		return _oxygen;
	}
}
