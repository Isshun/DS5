package org.smallbox.faraway.core.game.module.job.model.abs;

import org.smallbox.faraway.core.data.ItemInfo;
import org.smallbox.faraway.core.data.ItemInfo.ItemInfoAction;
import org.smallbox.faraway.core.engine.drawable.GDXDrawable;
import org.smallbox.faraway.core.game.helper.ItemFinder;
import org.smallbox.faraway.core.game.model.ObjectModel;
import org.smallbox.faraway.core.game.module.character.model.CharacterTalentExtra;
import org.smallbox.faraway.core.game.module.character.model.base.CharacterModel;
import org.smallbox.faraway.core.game.module.world.model.ItemFilter;
import org.smallbox.faraway.core.game.module.world.model.ItemSlot;
import org.smallbox.faraway.core.game.module.world.model.MapObjectModel;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.core.game.module.world.model.item.ItemModel;
import org.smallbox.faraway.core.module.java.ModuleManager;
import org.smallbox.faraway.core.util.Log;

public abstract class JobModel extends ObjectModel {
    public void onDraw(onDrawCallback callback) {
        callback.onDraw(_jobParcel.x, _jobParcel.y);
    }

    public void setTargetParcel(ParcelModel targetParcel) { _targetParcel = targetParcel; }

    public interface onDrawCallback {
        void onDraw(int x, int y);
    }

    public enum JobActionReturn {
        CONTINUE, QUIT, FINISH, ABORT
    }

    public enum JobStatus {
        WAITING, RUNNING, COMPLETE, BLOCKED, INVALID, MISSING_COMPONENT, ABORTED
    }

    public enum JobAbortReason {
        NO_COMPONENTS, INTERRUPT, BLOCKED, NO_LEFT_CARRY, INVALID, DIED, NO_BUILD_RESOURCES
    };

    private static int          _countInstance;

    protected int               _id;
    protected ParcelModel       _parcel;
    protected int               _limit;
    protected int               _currentLimit;
    protected int               _fail;
    protected int               _blocked;
    protected int               _nbBlocked;
    protected int               _nbUsed;
    protected int               _cost;
    protected int               _durationLeft;
    protected boolean           _isFinish;
    protected double            _progress;
    protected ItemModel         _item;
    protected ItemFilter        _filter;
    protected ItemInfoAction    _actionInfo;
    protected CharacterModel    _character;
    protected CharacterModel    _characterRequire;
    protected JobAbortReason    _reason;
    protected ItemSlot          _slot;
    protected String            _label;
    protected JobStatus         _status;
    private GDXDrawable         _iconDrawable;
    private GDXDrawable         _actionDrawable;
    protected String            _message;
    protected ParcelModel       _jobParcel;
    protected ParcelModel       _targetParcel;
    private boolean             _isEntertainment;
    protected JobStrategy       _strategy;
    protected ItemFinder        _finder;
    private boolean _isCreate;

    public JobModel(ItemInfo.ItemInfoAction actionInfo, ParcelModel targetParcel, GDXDrawable iconDrawable, GDXDrawable actionDrawable) {
        init();
        _jobParcel = targetParcel;
        _targetParcel = targetParcel;
        _iconDrawable = iconDrawable;
        _actionDrawable = actionDrawable;
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
        _limit = -1;
        _label = "none";
        _finder = (ItemFinder) ModuleManager.getInstance().getModule(ItemFinder.class);

        Log.debug("Job #" + _id + " onCreate");
    }

    public String                   getMessage() { return _message; }
    public String                   getLabel() { return _label; }
    public abstract String          getShortLabel();
    public abstract ParcelModel     getActionParcel();
    public int                      getId() { return _id; }
    public MapObjectModel           getItem() { return _item; }
    public CharacterModel           getCharacter() { return _character; }
    public CharacterModel           getCharacterRequire() { return _characterRequire; }
    public int                      getFail() { return _fail; }
    public int                      getBlocked() { return _blocked; }
    public JobAbortReason           getReason() { return _reason; }
    public String                   getReasonString() { return _reason != null ? _reason.toString() : "no reason"; }
    public ItemSlot                 getSlot() { return _slot; }
    public double                   getQuantity() { return _progress; }
    public double                   getProgress() { return Math.min(_progress, 1); }
    public JobStatus                getStatus() { return _status; }
    public GDXDrawable              getIconDrawable() { return _iconDrawable; }
    public GDXDrawable              getActionDrawable() { return _actionDrawable; }
    public double                   getSpeedModifier() { return 1; }
    public ParcelModel              getTargetParcel() { return _targetParcel; }
    public ParcelModel              getJobParcel() { return _jobParcel; }
    public ItemInfo.ItemInfoAction  getActionInfo() { return _actionInfo; }

    public void                     setEntertainment(boolean isEntertainment) { _isEntertainment = isEntertainment; }
    public void                     setStrategy(JobStrategy strategy) { _strategy = strategy; }
    public void                     setActionInfo(ItemInfo.ItemInfoAction action) { _actionInfo = action; _cost = action.cost; }
    public void                     setLabel(String label) { _label = label; }
    public void                     setLimit(int limit) { _currentLimit = _limit = limit; }
    public void                     setQuantity(int quantity) { _progress = quantity; }
    public void                     setCost(int quantityTotal) { _cost = quantityTotal; }
    public void                     setCharacterRequire(CharacterModel character) { _characterRequire = character; }
    public void                     setFail(JobAbortReason reason, int frame) { _reason = reason; _fail = frame; }
    public void                     setBlocked(int frame) { _blocked = frame; _nbBlocked++; }
    public void                     setSlot(ItemSlot slot) { _slot = slot; }
    public void                     setItem(ItemModel item) { _item = item; }
    public void                     setItemFilter(ItemFilter filter) { _filter = filter; }
    public void                     setStatus(JobStatus status) { _status = status; }

    public boolean                  canBeResume() { return true; }
    public boolean                  hasCharacter(CharacterModel character) { return _character != null && _character == character; }
    public boolean                  hasCharacter() { return _character != null; }
    public boolean                  isVisibleInUI() { return true; }
    public boolean                  isFinish() { return _status == JobStatus.COMPLETE || _status == JobStatus.ABORTED; }
    public boolean                  isEntertainment() { return _isEntertainment; }
    public boolean                  isCreate() { return _isCreate; }

    public void create() {
        _isCreate = true;
        onCreate();
    }

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
//            if (_posX != -1 && _posY != -1 && (_posX != model.getX() || _posY != model.getY())) {
//            }
        }
    }

    protected void onCreate() {}
    protected abstract boolean onCheck(CharacterModel character);
    protected abstract void onStart(CharacterModel character);
    public abstract JobActionReturn onAction(CharacterModel character);
    public void onActionDo() {}
    protected void onQuit(CharacterModel character) {}
    protected abstract void onFinish();


    public abstract CharacterTalentExtra.TalentType getTalentNeeded();

    /**
     *
     * @param character
     */
    public void quit(CharacterModel character) {
        onQuit(character);
        if (character.getJob() == this) {
            character.setJob(null);
        }
        _character = null;
    }

    public void finish() {
        onFinish();
        if (_isFinish) {
            Log.error("Try to close already closed job");
            return;
        }
        _isFinish = true;
    }

    public boolean check(CharacterModel character) {
        return onCheck(character);
    }

    public JobActionReturn action(CharacterModel character) {
        if (_limit != -1 && _currentLimit-- == 0) {
            return JobActionReturn.FINISH;
        }

        // Launch strategy
        if (_strategy != null) {
            _strategy.onAction(this);
        }

        JobActionReturn ret = onAction(character);

        if (ret == JobActionReturn.FINISH) {
            onFinish();
        }

        return ret;
    }

    public boolean isRunning() { return _character != null; }

    public interface JobStrategy {
        void onAction(JobModel job);
    }
}
