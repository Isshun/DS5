package alone.in.deepspace.Managers;

import alone.in.deepspace.World.BaseItem;
import alone.in.deepspace.World.WorldRenderer;

public class ResourceManager {

	private static ResourceManager _self;

	int	_o2Use;
	int	_o2Supply;
	int	_matter;
	int	_power;

	private int _spice;

	public enum Message {NONE, NO_MATTER, BUILD_COMPLETE, BUILD_PROGRESS};

	private ResourceManager() {
		_matter = 0;
		_power = 0;
		_o2Use = 0;
		_o2Supply = 0;
	}

	public static ResourceManager	getInstance() {
		if (_self == null) {
			_self = new ResourceManager();
		}
		return _self;
	}

	public Message build(BaseItem item) {
		if (_matter == 0) {
			return Message.NO_MATTER;
		}

		WorldRenderer.getInstance().invalidate(item.getX(), item.getY());

		if (item.isComplete() == false) {
			_matter--;
			item.setMatterSupply(item.getMatterSupply() + 1);
		}

		// BUILD_COMPLETE
		if (item.isComplete()) {

			// Remove power use
			if (item.getPower() != 0) {
				item.setPowerSupply(_power >= item.getPower() ? item.getPower() : _power);
				_power -= item.getPower();
			}

			// remove O2 use
			if (item.isType(BaseItem.Type.STRUCTURE_FLOOR)) {
				_o2Use++;
			}

			if (item.isType(BaseItem.Type.ENVIRONMENT_O2_RECYCLER)) {
				_o2Supply += 100;
			}

			// TODO
			if (item.getType() == BaseItem.Type.ARBORETUM_TREE_1 || item.getType() == BaseItem.Type.ARBORETUM_TREE_9) {
				_o2Supply += 10;
			}

			return Message.BUILD_COMPLETE;
		}

		// BUILD_PROGRESS
		else {
			return Message.BUILD_PROGRESS;
		}
	}

	public int 	getO2() { return (int) (_o2Use == 0 ? 100 : _o2Supply >= _o2Use ? 100 : _o2Supply * 100.0f / _o2Use); }
	public int 	getMatter() { return _matter; }
	public int 	getPower() { return _power; }
	public int 	getSpice() { return _spice; }

	public void setSpice(int spice) { _spice = spice; }
	public void	setMatter(int matter) { _matter = matter; }

	public void addMatter(int value) { _matter += value; }
}
