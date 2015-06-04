package org.smallbox.faraway.model.room;

import org.smallbox.faraway.engine.util.StringUtils;
import org.smallbox.faraway.model.item.UserItem;
import org.smallbox.faraway.model.item.WorldArea;
import org.smallbox.faraway.model.room.RoomOptions.RoomOption;

public class QuarterRoom extends Room {
	private RoomOptions			_options;
	private int 				_nbBed;
	private int 				_nbStorage;
	private RoomOption 			_entryBed;
	private RoomOption 			_entryStorage;

	public QuarterRoom() {
		super(RoomType.QUARTER);
		init();
	}

	public QuarterRoom(int id) {
		super(id, RoomType.QUARTER);
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