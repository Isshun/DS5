package org.smallbox.faraway.game.model.job;

import org.smallbox.faraway.game.model.character.base.CharacterModel;
import org.smallbox.faraway.game.model.item.*;
import org.smallbox.faraway.game.model.item.ItemInfo.ItemInfoAction;
import org.smallbox.faraway.util.Constant;
import org.smallbox.faraway.util.Log;

public abstract class BaseJobModel {

    private boolean _isJoy;
    protected JobStrategy _strategy;

    public String getActionIcon() {
        return null;
    }

    public String getMessage() {
        return null;
    }

    public void setJoy(boolean isJoy) {
        _isJoy = isJoy;
    }

    public boolean isJoy() {
        return _isJoy;
    }

    public boolean hasCharacter(CharacterModel character) {
        return _character != null && _character == character;
    }

    public void setStrategy(JobStrategy strategy) {
        _strategy = strategy;
    }

    public enum JobActionReturn {
		CONTINUE, QUIT, FINISH, ABORT
	}
	
    public enum JobStatus {
		WAITING, RUNNING, COMPLETE, ABORTED
	}

	public enum JobAbortReason {
		NO_COMPONENTS, INTERRUPT, BLOCKED, NO_LEFT_CARRY, INVALID, DIED, NO_BUILD_RESOURCES
	};

	private static int 			_countInstance;

    protected int 				_id;
	protected int 				_count;
    protected int               _totalCount;
    protected int				_posY = -1;
	protected int 				_posX = -1;
    protected int 	            _limit;
    protected int               _currentLimit;
    protected int 				_fail;
    protected int 				_blocked;
    protected int 				_nbBlocked;
    protected int 				_nbUsed;
    protected int 				_cost;
    protected int 				_durationLeft;
    protected boolean           _isClose;
    protected double 			_progress;
	protected MapObjectModel 	_item;
	protected ItemFilter 		_filter;
	protected ItemInfoAction    _actionInfo;
	protected CharacterModel    _character;
    protected CharacterModel    _characterRequire;
	protected JobAbortReason 	_reason;
    protected ItemSlot 			_slot;
    protected String 			_label;
	protected JobStatus			_status;
    private String              _icon;
    private String              _iconAction;
    protected String            _message;

	public BaseJobModel(ItemInfo.ItemInfoAction actionInfo, int x, int y, String iconPath, String iconActionPath) {
		init();
		_posY = y;
		_posX = x;
        _icon = iconPath;
        _iconAction = iconActionPath;
		if (actionInfo != null) {
			_actionInfo = actionInfo;
			_cost = actionInfo.cost;
			_progress = 0;
		}
	}

	public BaseJobModel() {
		init();
	}

	private void init() {
		_id = ++_countInstance;
		_item = null;
		_filter = null;
		_character = null;
		_status = JobStatus.WAITING;
		_count = 1;
        _limit = -1;
        _label = "none";

		Log.debug("Job #" + _id + " onCreate");
	}

	public String 				getLabel() { return _label; }
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
    public String               getIcon() { return _icon; }
    public String               getIconAction() { return _iconAction; }
    public ConsumableModel      getIngredient() { return null; }
    public double               getSpeedModifier() { return 1; }
    public String               getFormattedDuration() { return "" + _durationLeft / Constant.DURATION_MULTIPLIER + "s left"; }
    public int                  getNbBlocked() { return _nbBlocked; }
    public int                  getCount() { return _count; }
    public int                  getTotalCount() { return _totalCount; }
    public ItemInfo.ItemInfoAction getActionInfo() { return _actionInfo; }

    public void                 setActionInfo(ItemInfo.ItemInfoAction action) { _actionInfo = action; _cost = action.cost; }
    public void                 setLabel(String label) { _label = label; }
    public void                 setLimit(int limit) { _currentLimit = _limit = limit; }
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
    public void                 setCount(int count) { _count = count; }
    public void                 setTotalCount(int count) { _totalCount = count; }

    public boolean 				hasCharacter() { return _character != null; }
    public boolean              canBeResume() { return true; }
    public boolean              isVisibleInUI() { return true; }
    public boolean              isFinish() { return _status == JobStatus.COMPLETE || _status == JobStatus.ABORTED; }

    public void start(CharacterModel character) {
		if (_character == character) {
			return;
		}

		// Remove job from old characters
		if (_character != null) {
			_character.setJob(null);
		}

        // Lock item
        if (_item != null) {
            _item.setOwner(character);
        }

        // Set job to new characters
        _character = character;
		if (character != null) {
            character.setJob(this);

            // Start job
            onStart(character);

            // Move characters to job location
//            if (_posX != -1 && _posY != -1 && (_posX != character.getX() || _posY != character.getY())) {
                character.moveTo(this, _posX, _posY, null);
//            }
        }
	}

    protected abstract void onStart(CharacterModel character);

    public abstract CharacterModel.TalentType getTalentNeeded();

    /**
     *
     * @param character
     */
    public void quit(CharacterModel character) {
        onQuit(character);
        character.setJob(null);
        _character = null;
    }

    public abstract void onQuit(CharacterModel character);

    public void close() {
        if (_isClose) {
            Log.error("Try to close already closed job");
            return;
        }
        _isClose = true;
    }

	public boolean check(CharacterModel character) {
        return onCheck(character);
    }

    /**
     *
     * @param character
     * @return
     */
	public abstract boolean onCheck(CharacterModel character);

    public JobActionReturn action(CharacterModel character) {
        if (_limit != -1 && _currentLimit-- == 0) {
            return JobActionReturn.FINISH;
        }

        JobActionReturn ret = onAction(character);

        if (ret == JobActionReturn.FINISH) {
            onFinish();
        }

        return ret;
    }

    protected abstract void onFinish();

    /**
	 * Launch job onAction
	 * 
	 * @param character
	 * @return true if job is finish (completed or aborted)
	 */
	public abstract JobActionReturn onAction(CharacterModel character);

	@Override
	public String toString() {
		return "#" + _id + " (" + getLabel() + ")";
	}

    public boolean isRunning() { return _character != null; }

    public interface JobStrategy {
        void onAction(BaseJobModel job);
    }
}
