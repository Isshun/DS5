package org.smallbox.faraway.model.job;

import org.smallbox.faraway.Color;
import org.smallbox.faraway.engine.util.Constant;
import org.smallbox.faraway.engine.util.Log;
import org.smallbox.faraway.manager.JobManager;
import org.smallbox.faraway.manager.JobManager.Action;
import org.smallbox.faraway.model.character.CharacterModel;
import org.smallbox.faraway.model.item.ItemBase;
import org.smallbox.faraway.model.item.ItemFilter;
import org.smallbox.faraway.model.item.ItemInfo;
import org.smallbox.faraway.model.item.ItemSlot;

public abstract class JobModel {

	public static enum JobStatus {
		WAITING, RUNNING, COMPLETE, ABORTED
	}
	
	public static enum JobAbortReason {
		NO_COMPONENTS, INTERRUPTE, BLOCKED, NO_LEFT_CARRY, INVALID, DIED, NO_BUILD_RESOURCES
	};

	private static int 			_countInstance;
	private int 				_id;
	protected int 				_count;
	protected int				_posY;
	protected int 				_posX;
	protected ItemBase			_item;
	protected ItemFilter 		_filter;
	private JobManager.Action 	_action;
	protected ItemInfo.ItemInfoAction _actionInfo;
	protected CharacterModel _character;
	private CharacterModel _characterRequire;
	private int 				_fail;
	public int 					_blocked;
	protected JobAbortReason 	_reason;
	private Color 				_color;
	protected int 				_durationLeft;
	private ItemSlot 			_slot;
	protected int 				_nbUsed;
	protected Action 			_subAction;
	protected JobStatus			_status;
	private int 				_nbBlocked;
    protected double            _quantity;
    protected int               _quantityTotal;
	private boolean 			_hasDuration;

	public JobModel(ItemInfo.ItemInfoAction actionInfo, int x, int y) {
		init();
		_posY = y;
		_posX = x;
        _actionInfo = actionInfo;
        _quantityTotal = actionInfo.quantity;
        _quantity = 0;
	}

	public JobModel() {
		init();
	}

	private void init() {
		_id = ++_countInstance;
		_item = null;
		_filter = null;
		_action = JobManager.Action.NONE;
		_character = null;
		_status = JobStatus.WAITING;
		_count = 1;

		Log.debug("Job #" + _id + " create");
	}

	public abstract String 		getLabel();
	public abstract String 		getShortLabel();
	public int					getX() { return _posX; }
	public int					getY() { return _posY; }
	public int					getId() { return _id; }
	public JobManager.Action	getAction() { return _action; }
	public ItemBase				getItem() { return _item; }
	public CharacterModel       getCharacter() { return _character; }
	public CharacterModel       getCharacterRequire() { return _characterRequire; }
	public int 					getFail() { return _fail; }
	public int 					getBlocked() { return _blocked; }
	public JobAbortReason		getReason() { return _reason; }
	public ItemSlot 			getSlot() { return _slot; }
	public Color 				getColor() { return _color; }
	public String 				getActionName() { return JobManager.getActionName(_action); }
	public int 					getDurationLeft() { return _durationLeft; }
	public ItemFilter			getItemFilter() { return _filter; }
	public int 					getNbUsed() { return _nbUsed; }
	public double               getQuantity() { return _quantity; }
	public int 					getQuantityTotal() { return _quantityTotal; }
	public int 					getProgressPercent() { return (int)((double) _quantity / _quantityTotal * 100); }
	public double               getProgress() { return (double) _quantity / _quantityTotal; }
	public JobStatus			getStatus() { return _status; }

	public void					setCharacterRequire(CharacterModel character) { _characterRequire = character; }
	public void					setFail(JobAbortReason reason, int frame) { _reason = reason; _fail = frame; }
	public void					setBlocked(int frame) { _blocked = frame; _nbBlocked++; }
	public void 				setPosition(int x, int y) { _posX = x; _posY = y; }
	public void 				setSlot(ItemSlot slot) { _slot = slot; }
	public void					setItem(ItemBase item) { _item = item; }
	public void 				setDurationLeft(int duration) { if (duration > 0) { _hasDuration = true; } _durationLeft = duration; }
	public void 				setItemFilter(ItemFilter filter) { _filter = filter; }
	public void 				setStatus(JobStatus status) { _status = status; }

	public boolean 				hasCharacter() { return _character != null; }
	public boolean 				hasDuration() { return _hasDuration; }

//	public String getLabel() {
//		String oss = (_id  < 10 ? "#0" : "#") + _id
//				+ " - " + JobManager.getActionName(_action);
//
//		if (_action == Action.TAKE && _filter != null && _filter.itemMatched != null) {
//			oss += " " + _filter.itemMatched.label + " in ";
//		}
//
//		if (_item != null) {
//			oss += " " + _item.getName();
//		}
//		
//		if (_action == Action.USE) {
//			oss += " (" + _durationLeft / 10 + ")";
//		}
//		
//		if (_character != null) {
//			oss += " (" + _character.getName() + ")";
//		} else if (_fail > 0) {
//			switch (_reason) {
//			case BLOCKED: 		oss += " (blocked: #" + _blocked + ")"; break;
//			case INTERRUPTE: 	oss += " (interrupte)"; break;
//			case NO_BUILD_RESOURCES:
//			case NO_COMPONENTS: oss += " (no matter)"; break;
//			case INVALID: 		oss += " (invalide)"; break;
//			case NO_LEFT_CARRY: oss += " (no left carry)"; break;
//			case DIED: 			oss += " (died)"; break;
//			default: break;
//			}
//		} else {
//			oss += " (on queue)";
//		}
//		return oss.toString();
//	}
//
//	public String getShortLabel() {
//		String oss = JobManager.getActionName(_action);
//		if (_action == Action.TAKE && _filter != null && _filter.itemMatched != null) {
//			oss += " " + _filter.itemMatched.label + " in ";
//		}
//		if (_item != null) {
//			oss += " " + _item.getLabel();
//		}
////		if (_durationLeft > 0) {
////			oss += " (" + _durationLeft / Constant.DURATION_MULTIPLIER + "s)";
////		}
//		return oss;
//	}

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
	public void	setCharacter(CharacterModel character) {
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

	public boolean isFinish() {
		return _status == JobStatus.COMPLETE || _status == JobStatus.ABORTED;
	}
	
	public String getFormatedDuration() {
		return "" + _durationLeft / Constant.DURATION_MULTIPLIER + "s left";
	}

	public abstract boolean check(CharacterModel character);

	/**
	 * Launch job action
	 * 
	 * @param character
	 * @return true if job is finish (completed or aborted)
	 */
	public abstract boolean action(CharacterModel character);

	public int getNbBlocked() { return _nbBlocked; }

    public abstract String getType();

    public ItemInfo.ItemInfoAction getActionInfo() {
        return _actionInfo;
    }

	public boolean isFree() {
		return _character == null;
	}

	public int getDistance(CharacterModel character) {
		return Math.abs(character.getX() - _posX) + Math.abs(character.getY() - _posY);
	}

	public void setActionInfo(ItemInfo.ItemInfoAction action) {
		_actionInfo = action;
	}

	public void setCount(int count) {
		_count = count;
	}

	public int getCount() {
		return _count;
	}
}
