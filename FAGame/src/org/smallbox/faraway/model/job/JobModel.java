package org.smallbox.faraway.model.job;

import org.smallbox.faraway.SpriteModel;
import org.smallbox.faraway.engine.util.Constant;
import org.smallbox.faraway.engine.util.Log;
import org.smallbox.faraway.model.character.CharacterModel;
import org.smallbox.faraway.model.item.*;
import org.smallbox.faraway.model.item.ItemInfo.ItemInfoAction;

public abstract class JobModel {

	public boolean canBeResume() {
        return true;
    }

    public abstract CharacterModel.TalentType getTalentNeeded();

    public boolean isVisibleInUI() {
        return true;
    }

	public SpriteModel getIcon() {
		return null;
	}

	public ConsumableModel getIngredient() {
		return null;
	}

	public static enum JobStatus {
		WAITING, RUNNING, COMPLETE, ABORTED
	}
	
	public static enum JobAbortReason {
		NO_COMPONENTS, INTERRUPT, BLOCKED, NO_LEFT_CARRY, INVALID, DIED, NO_BUILD_RESOURCES
	};

	private static int 			_countInstance;
	private int 				_id;
	protected int 				_count;
    protected int               _totalCount;
    protected int				_posY;
	protected int 				_posX;
	protected MapObjectModel 	_item;
	protected ItemFilter 		_filter;
	protected ItemInfoAction    _actionInfo;
	protected CharacterModel    _character;
	private CharacterModel      _characterRequire;
	private int 				_fail;
	public int 					_blocked;
	protected JobAbortReason 	_reason;
	protected int 				_durationLeft;
	private ItemSlot 			_slot;
	protected int 				_nbUsed;
	protected JobStatus			_status;
	private int 				_nbBlocked;
    protected double _progress;
    protected int _cost;

	public JobModel(ItemInfo.ItemInfoAction actionInfo, int x, int y) {
		init();
		_posY = y;
		_posX = x;
		if (actionInfo != null) {
			_actionInfo = actionInfo;
			_cost = actionInfo.cost;
			_progress = 0;
		}
	}

	public JobModel() {
		init();
	}

	private void init() {
		_id = ++_countInstance;
		_item = null;
		_filter = null;
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
	public MapObjectModel 		getItem() { return _item; }
	public CharacterModel       getCharacter() { return _character; }
	public CharacterModel       getCharacterRequire() { return _characterRequire; }
	public int 					getFail() { return _fail; }
	public int 					getBlocked() { return _blocked; }
	public JobAbortReason		getReason() { return _reason; }
	public String 				getReasonString() { return _reason != null ? _reason.toString() : "no reason"; }
	public ItemSlot 			getSlot() { return _slot; }
	public ItemFilter			getItemFilter() { return _filter; }
	public int 					getNbUsed() { return _nbUsed; }
	public double               getQuantity() { return _progress; }
	public int 					getQuantityTotal() { return _cost; }
	public int 					getProgressPercent() { return (int)(getProgress() * 100); }
	public double               getProgress() { return (double) _progress / _cost; }
	public JobStatus			getStatus() { return _status; }

    public void                 setQuantity(int quantity) { _progress = quantity; }
    public void 				setCost(int quantityTotal) { _cost = quantityTotal; }
    public void					setCharacterRequire(CharacterModel character) { _characterRequire = character; }
	public void					setFail(JobAbortReason reason, int frame) { _reason = reason; _fail = frame; }
	public void					setBlocked(int frame) { _blocked = frame; _nbBlocked++; }
	public void 				setPosition(int x, int y) { _posX = x; _posY = y; }
	public void 				setSlot(ItemSlot slot) { _slot = slot; }
	public void					setItem(MapObjectModel item) { _item = item; }
	public void 				setItemFilter(ItemFilter filter) { _filter = filter; }
	public void 				setStatus(JobStatus status) { _status = status; }

	public boolean 				hasCharacter() { return _character != null; }

	public void	setCharacter(CharacterModel character) {
		if (_character == character) {
			return;
		}

		// Remove job from old character
		if (_character != null) {
			_character.setJob(null);
		}

		// Set job to new character
		_character = character;
		if (character != null) {
			character.setJob(this);
		}

		// Lock item
		if (_item != null) {
			_item.setOwner(character);
		}

		onCharacterAssign(character);
	}

	// TODO abstract
	protected void onCharacterAssign(CharacterModel character){}

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
		_cost = action.cost;
	}

	public void setCount(int count) {
		_count = count;
	}

	public int getCount() {
		return _count;
	}

	public void setTotalCount(int count) {
        _totalCount = count;
	}

	public int getTotalCount() {
		return _totalCount;
	}

	@Override
	public String toString() {
		return "#" + _id + " (" + getLabel() + ")";
	}
}