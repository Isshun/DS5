package org.smallbox.faraway.game.model.room;

import org.smallbox.faraway.util.StringUtils;
import org.smallbox.faraway.game.model.item.ItemModel;
import org.smallbox.faraway.game.model.item.ParcelModel;
import org.smallbox.faraway.game.model.room.RoomOptions.RoomOption;

public class QuarterRoom extends RoomModel {
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
		
		for (ParcelModel area: _parcels) {
			ItemModel item = area.getItem();
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
