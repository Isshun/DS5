package org.smallbox.faraway.core.game.module.job.model.abs;

import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.data.ItemInfo;
import org.smallbox.faraway.core.data.ItemInfo.ItemInfoAction;
import org.smallbox.faraway.core.engine.drawable.GDXDrawable;
import org.smallbox.faraway.core.engine.renderer.MainRenderer;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.helper.ItemFinder;
import org.smallbox.faraway.core.game.model.ObjectModel;
import org.smallbox.faraway.core.game.module.character.model.CharacterTalentExtra;
import org.smallbox.faraway.core.game.module.character.model.base.CharacterModel;
import org.smallbox.faraway.core.game.module.world.model.ItemFilter;
import org.smallbox.faraway.core.game.module.world.model.ItemSlot;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.core.module.java.ModuleManager;
import org.smallbox.faraway.core.util.Log;

public abstract class JobModel extends ObjectModel {
    public interface onDrawCallback {
        void onDraw(int x, int y);
    }

    public enum JobCheckReturn {
        OK, STAND_BY, ABORT, BLOCKED
    }

    public enum JobActionReturn {
        CONTINUE, QUIT, COMPLETE, ABORT, BLOCKED
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
    protected int               _cost = 1;
    protected int               _durationLeft;
    protected boolean           _isFinish;
    protected double            _progress;
    protected ItemFilter        _filter;
    protected ItemInfoAction    _actionInfo;
    protected CharacterModel    _character;
    protected CharacterModel    _characterRequire;
    protected JobAbortReason    _reason;
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
    private boolean             _isCreate;
    protected boolean           _auto;

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
    public int                      getId() { return _id; }
    public CharacterModel           getCharacter() { return _character; }
    public CharacterModel           getCharacterRequire() { return _characterRequire; }
    public int                      getFail() { return _fail; }
    public int                      getBlocked() { return _blocked; }
    public JobAbortReason           getReason() { return _reason; }
    public String                   getReasonString() { return _reason != null ? _reason.toString() : "no reason"; }
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
    public void                     setItemFilter(ItemFilter filter) { _filter = filter; }
    public void                     setStatus(JobStatus status) { _status = status; }

    public boolean                  hasCharacter(CharacterModel character) { return _character != null && _character == character; }
    public boolean                  hasCharacter() { return _character != null; }
    public boolean                  isVisibleInUI() { return true; }
    public boolean                  isFinish() { return _isFinish; }
    public boolean                  isEntertainment() { return _isEntertainment; }
    public boolean                  isCreate() { return _isCreate; }
    public boolean                  isVisible() { return true; }
    public boolean                  isRunning() { return _character != null; }
    public boolean                  isAuto() { return _auto; }

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

        // Set job to new characters
        _character = character;
        if (character != null) {
            character.setJob(this);

            // Start job
            onStart(character);
        }
    }

    public void draw(onDrawCallback callback) {
        if (_jobParcel != null) {
            callback.onDraw(_jobParcel.x, _jobParcel.y);
        }
    }

    protected void onCreate() {}
    protected abstract JobCheckReturn onCheck(CharacterModel character);
    protected void onStart(CharacterModel character) {}
    protected abstract JobActionReturn onAction(CharacterModel character);
    protected void onQuit(CharacterModel character) {}
    protected void onComplete() {}
    protected void onFinish() {}

    public abstract CharacterTalentExtra.TalentType getTalentNeeded();

    /**
     *
     * @param character
     */
    public void quit(CharacterModel character) {
        assert _character == character;

        if (_character != null) {
            onQuit(character);
            if (character.getJob() == this) {
                character.setJob(null);
            }
            _character = null;
            _status = JobStatus.WAITING;

            Application.getInstance().notify(observer -> observer.onJobQuit(this, character));
        }
    }

    public void complete() {
        onComplete();
        finish();
    }

    public void finish() {
        assert !_isFinish;

        _isFinish = true;

        if (_character != null) {
            quit(_character);
        }

        onFinish();

        Application.getInstance().notify(observer -> observer.onJobFinish(this));
    }

    public boolean check(CharacterModel character) {
        if (_auto && character != null) {
            return false;
        }

        JobCheckReturn ret = onCheck(character);
        if (ret == JobCheckReturn.BLOCKED) {
            _fail = MainRenderer.getFrame();
        }
        if (ret == JobCheckReturn.ABORT) {
            finish();
        }
        return ret == JobCheckReturn.OK;
    }

    public JobActionReturn action(CharacterModel character) {
        if (_limit != -1 && _currentLimit-- == 0) {
            return JobActionReturn.COMPLETE;
        }

        // Launch strategy
        if (_strategy != null) {
            _strategy.onAction(this);
        }

        JobActionReturn ret = onAction(character);

        if (ret == JobActionReturn.ABORT) {
            finish();
        }

        if (ret == JobActionReturn.COMPLETE) {
            complete();
        }

        if (ret == JobActionReturn.QUIT) {
            quit(_character);
        }

        return ret;
    }

    public interface JobStrategy {
        void onAction(JobModel job);
    }
}
