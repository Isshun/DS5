package alone.in.DeepSpace.Models;

import alone.in.DeepSpace.Managers.JobManager;
import alone.in.DeepSpace.Utils.Log;

public class Job {

	private int 				_id;
	private int					_posY;
	private int 				_posX;
	private BaseItem			_item;
	private BaseItem.Type 		_itemType;
	private JobManager.Action 	_action;
	private Character 			_character;
	private Character 			_characterRequire;

	public Job(int id, int x, int y) {
	  Log.debug("Job #" + id);

	  _id = id;
	  _posY = y;
	  _posX = x;
	  _item = null;
	  _itemType = BaseItem.Type.NONE;
	  _action = JobManager.Action.NONE;
	  _character = null;

	  Log.debug("Job #" + id + " done");
	}

	public void setAction(JobManager.Action action) { _action = action; }
	public void	setItemType(BaseItem.Type itemType) { _itemType = itemType; }
	public void	setItem(BaseItem item) { _item = item; }
	public void	setCharacter(Character character) { _character = character; }
	public void	setCharacterRequire(Character character) { _characterRequire = character; }

	public int					getX() { return _posX; }
	public int					getY() { return _posY; }
	public int					getId() { return _id; }
	public JobManager.Action	getAction() { return _action; }
	public BaseItem.Type		getItemType() { return _itemType; }
	public BaseItem				getItem() { return _item; }
	public Character			getCharacter() { return _character; }
	public Character			getCharacterRequire() { return _characterRequire; }
}
