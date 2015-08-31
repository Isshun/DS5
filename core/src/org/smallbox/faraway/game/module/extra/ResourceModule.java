package org.smallbox.faraway.game.module.extra;

import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.model.ToolTips;
import org.smallbox.faraway.game.model.item.ItemInfo;
import org.smallbox.faraway.game.module.GameModule;
import org.smallbox.faraway.util.Strings;

public class ResourceModule extends GameModule {

	public static class ResourceData {
		final public String label;
		public int 			value;
		public ToolTips.ToolTip tooltip;

		public ResourceData(String label, ToolTips.ToolTip tooltip) {
			this.label = label;
			this.tooltip = tooltip;
		}

	}

	private static ResourceModule _self;

	private ResourceData 	_science;
	private ResourceData	_power;
	private ResourceData	_food;
	private ResourceData 	_water;
	private ResourceData 	_gasoline;
	private ResourceData 	_spice;

	private ResourceData _oxygen;

	public enum Message {NONE, NO_MATTER, BUILD_COMPLETE, BUILD_PROGRESS};


	@Override
	protected void onLoaded() {
		_science = new ResourceData(Strings.LB_SCIENCE, ToolTips.RES_SCIENCE);
		_power = new ResourceData(Strings.LB_POWER, ToolTips.RES_POWER);
		_spice = new ResourceData("spice", ToolTips.RES_SPICE);
		_food = new ResourceData(Strings.LB_FOOD, ToolTips.RES_FOOD);
		_gasoline = new ResourceData(Strings.LB_GASOLINE, ToolTips.RES_GASOLINE);
		_water = new ResourceData(Strings.LB_WATER, ToolTips.RES_WATER);
		_oxygen = new ResourceData("o2", ToolTips.RES_OXYGEN);
	}

	// TODO
	public void refreshWater() {
	}

	public ResourceData getScience() { return _science; }
	public ResourceData	getPower() { return _power; }
	public ResourceData	getSpice() { return _spice; }
	public ResourceData	getWater() { return _water; }
	public ResourceData getFood() { return _food; }

	public void setSpice(int spice) { _spice.value = spice; }
	public void setWater(int water) { _water.value = water; refreshWater(); }
	public void setScience(int science) { _science.value = science; }
	public void addScience(int science) { _science.value += science; }
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
