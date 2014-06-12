package alone.in.deepspace.model.room;

import java.util.List;

import alone.in.deepspace.Game;
import alone.in.deepspace.engine.renderer.MainRenderer;
import alone.in.deepspace.engine.ui.OnClickListener;
import alone.in.deepspace.engine.ui.View;
import alone.in.deepspace.manager.ServiceManager;
import alone.in.deepspace.manager.SpriteManager;
import alone.in.deepspace.model.item.ItemInfo;
import alone.in.deepspace.model.item.WorldArea;
import alone.in.deepspace.model.item.WorldResource;
import alone.in.deepspace.model.room.RoomOptions.RoomOption;

public class GardenRoom extends Room {
	private enum State {
		RAW, GROWING, MATURE
	}
	
	private static final double GROW_VALUE = 0.1;
	private ItemInfo 			_currentCulture;
	private List<ItemInfo> 		_cultures;
	private RoomOptions			_options;
	private State 				_state;

	public GardenRoom() {
		super(Type.GARDEN);
		init();
	}

	public GardenRoom(int id) {
		super(id, Type.GARDEN);
		init();
	}

	private void init() {
		_options = new RoomOptions();
		_cultures  = Game.getData().gatherItems;
		for (ItemInfo c: _cultures) {
			final ItemInfo culture = c;
			_options.options.add(new RoomOption("Set " + culture.label,
					SpriteManager.getInstance().getIcon(culture),
					new OnClickListener() {
				@Override
				public void onClick(View view) {
					setCulture(culture);
				}
			}));
		}
		_currentCulture = Game.getData().getRandomGatherItem();
	}

	public void setCulture(ItemInfo culture) {
		if (culture != _currentCulture) {
			_state = State.RAW;
			_currentCulture = culture;
			for (WorldArea area: _areas) {
				ServiceManager.getWorldMap().replaceItem(_currentCulture, area.getX(), area.getY(), 0);
			}
		}
	}
	
	public State getState() { return _state; }

	@Override
	public void update() {
		for (WorldArea area: _areas) {
			if (area.getRessource() == null) {
				ServiceManager.getWorldMap().putItem(_currentCulture, area.getX(), area.getY(), 0, 0);
			}

			WorldResource res = area.getRessource();
			if (res != null && res.isType(_currentCulture) && res.isMature() == false) {
				if ((int)res.getValue() != (int)(res.getValue() + GROW_VALUE)) {
					MainRenderer.getInstance().invalidate(res.getX(), res.getY());
				}
				res.addValue(GROW_VALUE);
			}
		}
	}

	public ItemInfo getCulture() {
		return _currentCulture;
	}

	public RoomOptions getOptions() {
		return _options;
	}
}
