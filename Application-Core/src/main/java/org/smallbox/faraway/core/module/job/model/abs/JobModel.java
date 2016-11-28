package org.smallbox.faraway.core.module.job.model.abs;

import org.smallbox.faraway.util.CollectionUtils;
import org.smallbox.faraway.core.engine.drawable.GDXDrawable;
import org.smallbox.faraway.client.renderer.MainRenderer;
import org.smallbox.faraway.core.game.model.ObjectModel;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo.ItemInfoAction;
import org.smallbox.faraway.core.module.character.model.CharacterTalentExtra;
import org.smallbox.faraway.core.module.character.model.base.CharacterModel;
import org.smallbox.faraway.core.module.world.model.ItemFilter;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.util.Log;

import java.util.LinkedList;
import java.util.List;

public abstract class JobModel extends ObjectModel {

    public List<JobModel> getSubJobs() {
        return _subJobs;
    }

    public interface OnStartListener {
        void onStart();
    }

    public interface OnActionListener {
        void onAction();
    }

    public interface OnCompleteListener {
        void onComplete();
    }

    private List<JobModel> _subJobs = new LinkedList<>();
    private OnStartListener _onStartListener;

    public void cancel() {
        onCancel();
        finish();
    }

    protected void onCancel() {}

    public boolean isActive() {
        return true;
    }

    public void addSubJob(JobModel subJob) {
        _subJobs.add(subJob);
    }

    public interface onDrawCallback {
        void onDraw(int x, int y, int z);
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
    protected long              _startTime;
    protected long              _endTime;
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
    protected OnActionListener _onActionListener;
    private boolean             _isCreate;
    protected boolean _isAuto;
    protected OnCompleteListener _onCompleteListener;

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
        _status = JobStatus.WAITING;
        _limit = -1;
        _label = "none";

        Log.debug("Job #" + _id + " onGameCreate");
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
    public long                     getStartTime() { return _startTime; }
    public long                     getEndTime() { return _endTime; }
    public ItemInfo.ItemInfoAction  getAction() { return _actionInfo; }

    public void                     setEntertainment(boolean isEntertainment) { _isEntertainment = isEntertainment; }
    public void                     setAction(ItemInfo.ItemInfoAction action) { _actionInfo = action; _cost = action.cost; _isAuto = _actionInfo.auto; }
    public void                     setLabel(String label) { _label = label; }
    public void                     setLimit(int limit) { _currentLimit = _limit = limit; }
    public void                     setQuantity(int quantity) { _progress = quantity; }
    public void                     setCost(int quantityTotal) { _cost = quantityTotal; }
    public void                     setCharacterRequire(CharacterModel character) { _characterRequire = character; }
    public void                     setFail(JobAbortReason reason, int frame) { _reason = reason; _fail = frame; }
    public void                     setBlocked(int frame) { _blocked = frame; _nbBlocked++; }
    public void                     setItemFilter(ItemFilter filter) { _filter = filter; }
    public void                     setStatus(JobStatus status) { _status = status; }
    public void                     setOnStartListener(OnStartListener onStartListener) { _onStartListener = onStartListener; }
    public void                     setOnActionListener(OnActionListener onActionListener) { _onActionListener = onActionListener; }
    public void                     setOnCompleteListener(OnCompleteListener onCompleteListener) { _onCompleteListener = onCompleteListener; }

    public boolean                  hasTargetParcel() { return _targetParcel != null; }
    public boolean                  hasJobParcel() { return _jobParcel != null; }
    public boolean                  hasCharacter(CharacterModel character) { return _character != null && _character == character; }
    public boolean                  hasCharacter() { return _character != null; }
    public boolean                  hasCharacterReady() { return _character != null && _character.getParcel() == _targetParcel; }
    public boolean                  isVisibleInUI() { return true; }
    public boolean                  isFinish() { return _isFinish; }
    public boolean                  isOpen() { return !_isFinish; }
    public boolean                  isEntertainment() { return _isEntertainment; }
    public boolean                  isCreate() { return _isCreate; }
    public boolean                  isVisible() { return true; }
    public boolean                  isRunning() { return _character != null; }
    public boolean                  isAuto() { return _isAuto; }

    public void create() {
        _isCreate = true;
        onCreate();
    }

    public void start(CharacterModel character) {
        Log.debug("Start job " + this + " by " + (character != null ? character.getName() : "auto"));

//        assert character != null;
//        assert character.getJob() == null;

        if (_isAuto && character != null) {
            Log.error("cannot assign character to auto job");
        }

        // Remove job from old characters
        if (_character != null) {
            quit(_character);
        }

        // Set job to new characters
        _character = character;
        if (character != null) {
            character.setJob(this);
        }

        if (_onStartListener != null) {
            _onStartListener.onStart();
        }

        onStart(character);
    }

    public void draw(onDrawCallback callback) {
        if (_jobParcel != null) {
            callback.onDraw(_jobParcel.x, _jobParcel.y, _jobParcel.z);
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
        assert character == _character;
        onQuit(character);

        assert character.getJob() == this;
        character.clearJob(this);

        _character = null;
        _status = JobStatus.WAITING;

        Log.debug("Quit job " + this + " by " + character.getName());
    }

    public void complete() {
        onComplete();
        finish();
    }

    public void finish() {
        assert !_isFinish;

        _isFinish = true;

        if (_character != null) {
            Log.debug("Complete job " + this + " by " + _character.getName());
            quit(_character);
        }

        onFinish();
    }

    public boolean check(CharacterModel character) {
        if (_isAuto && character != null) {
            return false;
        }

        // Job have sub jobs
        if (CollectionUtils.isNotEmpty(_subJobs)) {
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

    public void action() {
        action(_character);
    }

    public JobActionReturn action(CharacterModel character) {
        if (isFinish()) {
            Log.error("Cannot call action on finished job");
        }

        // Job is not auto and have no character ready
        if (!_isAuto && !hasCharacterReady()) {
            return JobActionReturn.CONTINUE;
        }

        // Job have sub jobs
        _subJobs.removeIf(JobModel::isFinish);
        if (CollectionUtils.isNotEmpty(_subJobs)) {
            return JobActionReturn.CONTINUE;
        }

        if (_limit != -1 && _currentLimit-- == 0) {
            return JobActionReturn.COMPLETE;
        }

        // Launch strategy
        if (_onActionListener != null) {
            _onActionListener.onAction();
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

    public int getProgressPercent() {
        return (int)(_progress * 100);
    }
}