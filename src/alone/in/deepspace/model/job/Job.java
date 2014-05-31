package alone.in.deepspace.model.job;

import java.util.ArrayList;
import java.util.List;

import org.jsfml.graphics.Color;

import alone.in.deepspace.manager.ItemFilter;
import alone.in.deepspace.manager.ItemSlot;
import alone.in.deepspace.manager.JobManager;
import alone.in.deepspace.manager.JobManager.Action;
import alone.in.deepspace.model.BaseItem;
import alone.in.deepspace.model.character.Character;
import alone.in.deepspace.util.Constant;
import alone.in.deepspace.util.Log;

public abstract class Job {

	public static enum JobStatus {
		WAITING, RUNNING, COMPLETE, ABORTED
	}
	
	public static enum Abort {
		NO_COMPONENTS, INTERRUPTE, BLOCKED, NO_LEFT_CARRY, INVALID, DIED
	};

	private static int			_count;
	private int 				_id;
	protected int				_posY;
	protected int 				_posX;
	protected BaseItem			_item;
	protected List<BaseItem>		_carryItems;
	protected ItemFilter 		_filter;
	private JobManager.Action 	_action;
	protected Character 		_character;
	private Character 			_characterRequire;
	private int 				_fail;
	public int 					_blocked;
	protected Abort 				_reason;
	private Color 				_color;
	protected int 				_durationLeft;
	private ItemSlot 			_slot;
	protected int 				_nbUsed;
	protected Action 			_subAction;
	private JobStatus			_status;
	private int _nbBlocked;

	public Job(int x, int y) {
		init();
		_posY = y;
		_posX = x;
	}

	public Job() {
		init();
	}

	private void init() {
		_id = ++_count;
		_item = null;
		_filter = null;
		_action = JobManager.Action.NONE;
		_character = null;
		_status = JobStatus.WAITING;

		Log.debug("Job #" + _id + " create");
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
	public ItemFilter			getItemFilter() { return _filter; }
	public int 					getNbUsed() { return _nbUsed; }
	public JobStatus			getStatus() { return _status; }

	public void					setCharacterRequire(Character character) { _characterRequire = character; }
	public void					setFail(Abort reason, int frame) { _reason = reason; _fail = frame; }
	public void					setBlocked(int frame) { _blocked = frame; _nbBlocked++; }
	public void 				setPosition(int x, int y) { _posX = x; _posY = y; }
	public void 				setSlot(ItemSlot slot) { _slot = slot; }
	public void					setItem(BaseItem item) { _item = item; }
	public void 				setDurationLeft(int duration) { _durationLeft = duration; }
	public void 				setItemFilter(ItemFilter filter) { _filter = filter; }
	public void 				setStatus(JobStatus status) { _status = status; }

	public boolean 				isActive() { return _character != null; }

	public String getLabel() {
		String oss = (_id  < 10 ? "#0" : "#") + _id
				+ " - " + JobManager.getActionName(_action);

		if (_action == Action.TAKE && _filter != null && _filter.matchingItem != null) {
			oss += " " + _filter.matchingItem.label + " in ";
		}

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
			case NO_COMPONENTS:
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
		if (_action == Action.TAKE && _filter != null && _filter.matchingItem != null) {
			oss += " " + _filter.matchingItem.label + " in ";
		}
		if (_item != null) {
			oss += " " + _item.getLabel();
		}
//		if (_durationLeft > 0) {
//			oss += " (" + _durationLeft / Constant.DURATION_MULTIPLIER + "s)";
//		}
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
		case REFILL: _color = Color.GREEN; break;
		case NONE: _color = Color.BLACK; break;
		case USE_INVENTORY:
		case USE: _color = Color.BLUE; break;
		case DESTROY: _color = new Color(200, 20, 20); break;
		case STORE: _color = new Color(180, 100, 255); break;
		case TAKE: _color = new Color(180, 100, 255); break;
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
		_nbUsed++;
	}

	public Action getSubAction() {
		return _subAction;
	}

	public void setSubAction(Action action) {
		_subAction = action;
	}

	public void addCarry(BaseItem item) {
		if (_carryItems == null) {
			_carryItems = new ArrayList<BaseItem>();
		}
		_carryItems.add(item);
	}

	public List<BaseItem> getCarry() {
		return _carryItems;
	}

	public boolean isFinish() {
		return _status == JobStatus.COMPLETE || _status == JobStatus.ABORTED;
	}
	
	public String getFormatedDuration() {
		return "" + _durationLeft / Constant.DURATION_MULTIPLIER + "s left";
	}

	public abstract boolean check(Character character);

	public abstract boolean action(Character character);

	public int getNbBlocked() { return _nbBlocked; }

}
