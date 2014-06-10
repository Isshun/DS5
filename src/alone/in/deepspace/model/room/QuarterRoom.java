package alone.in.deepspace.model.room;

import alone.in.deepspace.model.item.UserItem;
import alone.in.deepspace.model.item.WorldArea;
import alone.in.deepspace.model.room.RoomOptions.RoomOption;
import alone.in.deepspace.util.StringUtils;

public class QuarterRoom extends Room {
	private RoomOptions			_options;
	private int 				_nbBed;
	private int 				_nbStorage;
	private RoomOption 			_entryBed;
	private RoomOption 			_entryStorage;

	public QuarterRoom() {
		super(Type.QUARTER);
		init();
	}

	public QuarterRoom(int id) {
		super(id, Type.QUARTER);
		init();
	}

	private void init() {
		_options = new RoomOptions();
		
		// Bed
		_entryBed = new RoomOption("bed", null, null);
		_options.options.add(_entryBed);

		// Storage
		_entryStorage = new RoomOption("storage", null, null);
		_options.options.add(_entryStorage);
	}

	@Override
	public void update() {
		_nbStorage = 0;
		_nbBed = 0;
		
		for (WorldArea area: _areas) {
			UserItem item = area.getItem();
			if (item != null) {
				if (item.isBed()) { _nbBed++; }
				//if (item.isStorage()) { _nbStorage++; }
			}
		}
		
		_entryBed.label = StringUtils.getDashedString("Bed", String.valueOf(_nbBed));
		_entryStorage.label = StringUtils.getDashedString("Storage", String.valueOf(_nbStorage));
	}

	public RoomOptions getOptions() {
		return _options;
	}
}
