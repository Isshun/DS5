package alone.in.deepspace.model;

import org.jsfml.graphics.Color;

import alone.in.deepspace.Utils.Constant;
import alone.in.deepspace.Utils.Log;
import alone.in.deepspace.manager.ItemSlot;
import alone.in.deepspace.manager.JobManager;
import alone.in.deepspace.manager.JobManager.Action;

public class Job {

	public static enum Abort {
		NO_MATTER, INTERRUPTE, BLOCKED, NO_LEFT_CARRY, INVALID
	};

	private int 				_id;
	private int					_posY;
	private int 				_posX;
	private BaseItem			_item;
	private JobManager.Action 	_action;
	private Character 			_character;
	private Character 			_characterRequire;
	private int 				_fail;
	public int 					_blocked;
	private Abort 				_reason;
	private Color 				_color;
	private int 				_durationLeft;
	private ItemSlot 			_slot;

	public Job(int id, int x, int y) {
		Log.debug("Job #" + id);
		init(id);
		_posY = y;
		_posX = x;
	}

	public Job(int id) {
		Log.debug("Job #" + id);
		init(id);
	}

	private void init(int id) {
		_id = id;
		_item = null;
		_action = JobManager.Action.NONE;
		_character = null;

		Log.debug("Job #" + id + " done");
	}

	public int					getX() { return _posX; }
	public int					getY() { return _posY; }
	public int					getId() { return _id; }
	public JobManager.Action	getAction() { return _action; }
	public BaseItem				getItem() { return _item; }
	public Character			getCharacter() { return _character; }
	public Character			getCharacterRequire() { return _characterRequire; }
	public int 					getFail() { return _fail; }
	public int 					getBlocked() { return _blocked; }
	public Abort				getReason() { return _reason; }
	public ItemSlot 			getSlot() { return _slot; }
	public Color 				getColor() { return _color; }
	public String 				getActionName() { return JobManager.getActionName(_action); }
	public int 					getDurationLeft() { return _durationLeft; }

	public void					setCharacterRequire(Character character) { _characterRequire = character; }
	public void					setFail(Abort reason, int frame) { _reason = reason; _fail = frame; }
	public void					setBlocked(int frame) { _blocked = frame; }
	public void 				setPosition(int x, int y) { _posX = x; _posY = y; }
	public void 				setSlot(ItemSlot slot) { _slot = slot; }
	public void					setItem(BaseItem item) { _item = item; }
	public void 				setDurationLeft(int duration) { _durationLeft = duration; }

	public String getLabel() {
		String oss = (_id  < 10 ? "#0" : "#") + _id
				+ " - " + JobManager.getActionName(_action);
		if (_item != null) {
			oss += " " + _item.getName();
		}

		if (_action == Action.USE) {
			oss += " (" + _durationLeft / 10 + ")";
		}
		
		if (_character != null) {
			oss += " (" + _character.getName() + ")";
		} else if (_fail > 0) {
			switch (_reason) {
			case BLOCKED:
				oss += " (blocked: #" + _blocked + ")";
				break;
			case INTERRUPTE:
				oss += " (interrupte)";
				break;
			case NO_MATTER:
				oss += " (no matter)";
				break;
			case INVALID:
				oss += " (invalide)";
				break;
			case NO_LEFT_CARRY:
				oss += " (no left carry)";
				break;
			}
		} else {
			oss += " (on queue)";
		}
		return oss.toString();
	}

	public String getShortLabel() {
		String oss = JobManager.getActionName(_action);
		if (_item != null) {
			oss += " " + _item.getLabel();
		}
		if (_action == Action.USE) {
			oss += " (" + _durationLeft / Constant.DURATION_MULTIPLIER + "s)";
		}
		return oss;
	}

	public void setAction(JobManager.Action action) {
		_action = action;

		switch (_action) {
		case BUILD: _color = new Color(170, 128, 64); break;
		case MOVE: _color = Color.CYAN; break;
		case GATHER: _color = Color.GREEN; break;
		case MINING: _color = Color.GREEN; break;
		case WORK: _color = Color.GREEN; break;
		case NONE: _color = Color.BLACK; break;
		case USE: _color = Color.BLUE; break;
		case DESTROY: _color = new Color(200, 20, 20); break;
		case STORE: _color = new Color(180, 100, 255); break;
		}
	}
	public void	setCharacter(Character character) {
		if (_character == character) {
			return;
		}
		
		_character = character;
		if (character != null) {
			character.setJob(this);
		}
		
		if (_item != null) {
			_item.setOwner(character);
		}
	}

	public void decreaseDurationLeft() {
		_durationLeft--;
	}

}
