package alone.in.deepspace.Managers;

import alone.in.deepspace.Models.BaseItem;

public class ResourceManager {

	  int	_o2Use;
	  int	_o2Supply;
	  int	_matter;
	  int	_power;
	private static ResourceManager _self;
	
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

		  _matter--;
		  item._matterSupply++;

		  // BUILD_COMPLETE
		  if (item._matterSupply >= item.getMatter()) {

			// Remove power use
			if (item.power != 0) {
			  item.powerSupply = _power >= item.power ? item.power : _power;
			  _power -= item.power;
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

		public void setMatter(int matter) { _matter = matter; }
		  public void addMatter(int value) { _matter += value; }

		  public int getO2() { return (int) (_o2Use == 0 ? 100 : _o2Supply >= _o2Use ? 100 : _o2Supply * 100.0f / _o2Use); }

		public int getMatter() {
			return _matter;
		}

		public int getPower() {
			return _power;
		}

}
