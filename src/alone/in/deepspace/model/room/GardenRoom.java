package alone.in.deepspace.model.room;

import alone.in.deepspace.manager.ServiceManager;
import alone.in.deepspace.model.item.ItemInfo;
import alone.in.deepspace.model.item.WorldArea;

public class GardenRoom extends Room {

	private ItemInfo _culture;

	public GardenRoom(int x, int y) {
		super(Type.GARDEN, x, y);
		
		_culture = ServiceManager.getData().getItemInfo("base.seaweed1");
	}

	public void setCulture(ItemInfo culture) {
		_culture = culture;
		for (WorldArea area: _areas) {
			ServiceManager.getWorldMap().replaceItem(_culture, area.getX(), area.getY());
		}
	}

	@Override
	public void update() {
		for (WorldArea area: _areas) {
			if (area.getRessource() == null) {
				ServiceManager.getWorldMap().putItem(_culture, area.getX(), area.getY());
			}
			
			if (area.getRessource() != null && area.getRessource().isType(_culture)) {
				area.getRessource().addValue(0.1);
			}
		}
	}

	public ItemInfo getCulture() {
		return _culture;
	}
}
