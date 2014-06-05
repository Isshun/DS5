package alone.in.deepspace.model.room;

import java.util.ArrayList;
import java.util.List;

import alone.in.deepspace.manager.ServiceManager;
import alone.in.deepspace.model.item.ItemInfo;
import alone.in.deepspace.model.item.UserItem;
import alone.in.deepspace.model.item.WorldArea;
import alone.in.deepspace.util.StringUtils;

public class QuarterRoom extends Room {
	private ItemInfo 			_currentCulture;
	private List<RoomOption>	_options;
	private int 				_nbBed;
	private int 				_nbStorage;
	private RoomOption 			_entryBed;
	private RoomOption 			_entryStorage;

	public QuarterRoom(int x, int y) {
		super(Type.QUARTER, x, y);

		_options = new ArrayList<RoomOption>();
		
		// Bed
		_entryBed = new RoomOption("bed", null, null);
		_options.add(_entryBed);

		// Storage
		_entryStorage = new RoomOption("storage", null, null);
		_options.add(_entryStorage);
	}

	public void setCulture(ItemInfo culture) {
		_currentCulture = culture;
		for (WorldArea area: _areas) {
			ServiceManager.getWorldMap().replaceItem(_currentCulture, area.getX(), area.getY());
		}
	}

	@Override
	public void update() {
		_nbStorage = 0;
		_nbBed = 0;
		
		for (WorldArea area: _areas) {
			UserItem item = area.getItem();
			if (item != null) {
				if (item.isBed()) { _nbBed++; }
				if (item.isStorage()) { _nbStorage++; }
			}
		}
		
		_entryBed.label = StringUtils.getDashedString("Bed", String.valueOf(_nbBed));
		_entryStorage.label = StringUtils.getDashedString("Storage", String.valueOf(_nbStorage));
	}

	public ItemInfo getCulture() {
		return _currentCulture;
	}

	public List<RoomOption> getOptions() {
		return _options;
	}
}
