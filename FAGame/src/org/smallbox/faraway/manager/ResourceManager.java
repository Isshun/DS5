package org.smallbox.faraway.manager;

import org.smallbox.faraway.Game;
import org.smallbox.faraway.Strings;
import org.smallbox.faraway.model.ToolTips;
import org.smallbox.faraway.model.item.ItemBase;
import org.smallbox.faraway.model.item.ItemInfo;
import org.smallbox.faraway.engine.renderer.MainRenderer;

public class ResourceManager {

	private static ResourceManager _self;

//	private ResourceData	_o2Use;
//	private ResourceData	_o2Supply;
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

	public Message build(ItemBase item) {
		if (_matter.value == 0) {
			return Message.NO_MATTER;
		}

		MainRenderer.getInstance().invalidate(item.getX(), item.getY());

		if (item.isComplete() == false) {
			_matter.value--;
			item.setMatterSupply(item.getMatterSupply() + 1);
		}

		// BUILD_COMPLETE
		if (item.isComplete()) {

			// Remove power use
			if (item.getPower() != 0) {
				item.setPowerSupply(_power.value >= item.getPower() ? item.getPower() : _power.value);
				_power.value -= item.getPower();
			}

			// O2
			if (item.getInfo().cost != null) {
				//_o2Supply.value -= item.getInfo().cost.o2;
			}

			if (item.getLight() > 0) {
				((MainRenderer)MainRenderer.getInstance()).refreshLight(item);
			}
			
			return Message.BUILD_COMPLETE;
		}

		// BUILD_PROGRESS
		else {
			return Message.BUILD_PROGRESS;
		}
	}
	
	// TODO
	public void refreshWater() {
//		WorldMap worldmap = ServiceManager.getWorldMap();
//		int width = worldmap.getWidth();
//		int height = ServiceManager.getWorldMap().getWidth();
//		int water = _water;
//
//		// Re-active working garden
//		for (int x = 0; x < width; x++) {
//			for (int y = 0; y < height; y++) {
//				StructureItem structure = worldmap.getStructure(x, y);
//				Room room = RoomManager.getInstance().get(x, y);
//				if (structure != null && structure.isType(BaseItem.Type.STRUCTURE_GREENHOUSE) && structure.isWorking() && room != null && room.isType(Type.GARDEN)) {
//					structure.setWorking(water-- > 0);
//					MainRenderer.getInstance().invalidate(x, y);
//				}
//			}
//		}
//
//		// Active non-working garden
//		for (int x = 0; x < width; x++) {
//			for (int y = 0; y < height; y++) {
//				StructureItem structure = worldmap.getStructure(x, y);
//				Room room = RoomManager.getInstance().get(x, y);
//				if (structure != null && structure.isType(BaseItem.Type.STRUCTURE_GREENHOUSE) && structure.isWorking() == false && room != null && room.isType(Type.GARDEN)) {
//					structure.setWorking(water-- > 0);
//					MainRenderer.getInstance().invalidate(x, y);
//				}
//			}
//		}
//
//		// Active other areas
//		for (int x = 0; x < width; x++) {
//			for (int y = 0; y < height; y++) {
//				StructureItem structure = worldmap.getStructure(x, y);
//				Room room = RoomManager.getInstance().get(x, y);
//				if (structure != null && structure.isType(BaseItem.Type.STRUCTURE_GREENHOUSE) && (room == null || room.isType(Type.GARDEN) == false)) {
//					structure.setWorking(water-- > 0);
//					MainRenderer.getInstance().invalidate(x, y);
//				}
//			}
//		}
	}

//	public int 	getO2() { return (int) (_o2Use == 0 ? 100 : _o2Supply >= _o2Use ? 100 : _o2Supply * 100.0f / _o2Use); }
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

	public void onLongUpdate() {
	}

	public ResourceData getGasoline() {
		return _gasoline;
	}

	public ResourceData getO2() {
		return _oxygen;
	}
}
