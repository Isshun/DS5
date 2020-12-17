package org.smallbox.faraway.modules.job;

import org.smallbox.faraway.core.GameException;
import org.smallbox.faraway.common.ObjectModel;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo.ItemInfoAction;
import org.smallbox.faraway.core.module.world.model.ItemFilter;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.character.model.CharacterSkillExtra;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;
import org.smallbox.faraway.util.Log;

import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Base class for job
 */
public abstract class JobModel extends ObjectModel {

    public enum JobCheckReturn {
        OK, STAND_BY, ABORT, BLOCKED
    }

    public enum JobStatus {
        JOB_INITIALIZED, JOB_WAITING, JOB_RUNNING, JOB_COMPLETE, JOB_BLOCKED, JOB_INVALID, JOB_MISSING_COMPONENT
    }

    public enum JobAbortReason {
        NO_COMPONENTS, INTERRUPT, BLOCKED, NO_LEFT_CARRY, INVALID, DIED, NO_BUILD_RESOURCES
    }

    protected int               _limit;
    protected int               _fail;
    protected int               _blocked;
    protected int               _cost = 1;
    protected boolean           _isClose;
    protected double            _progress;
    protected ItemFilter        _filter;
    protected ItemInfoAction    _actionInfo;
    protected CharacterModel    _character;
    protected CharacterModel    _characterRequire;
    protected JobAbortReason    _reason;
    protected String            _label;
    protected JobStatus         _status = JobStatus.JOB_INITIALIZED;
    protected String            _message;
    protected ParcelModel       _jobParcel;
    protected ParcelModel       _targetParcel;
    protected ParcelModel       _startParcel;
    protected boolean           _isEntertainment;
    protected boolean           _isAuto;
    protected String            _mainLabel = "";
    protected Object            _data;
    protected boolean           _visible = true;
    protected JobTaskReturn     _lastTaskReturn;

    public JobModel(ItemInfo.ItemInfoAction actionInfo, ParcelModel targetParcel) {
        init();
        _jobParcel = targetParcel;
        _targetParcel = targetParcel;
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
        _filter = null;
        _status = JobStatus.JOB_INITIALIZED;
        _limit = -1;
        _label = "none";

        Log.debug("Job #" + _id + " create new " + getClass().getSimpleName());
    }

    public String                   getMessage() { return _message; }
    public String                   getLabel() { return _mainLabel; }
    public String                   getMainLabel() { return _mainLabel; }
    public int                      getId() { return _id; }
    public CharacterModel           getCharacter() { return _character; }
    public CharacterModel           getCharacterRequire() { return _characterRequire; }
    public int                      getFail() { return _fail; }
    public int                      getBlocked() { return _blocked; }
    public JobAbortReason           getReason() { return _reason; }
    public double                   getQuantity() { return _progress; }
    public double                   getProgress() { return Math.min(_progress, 1); }
    public JobStatus                getStatus() { return _status; }
    public double                   getSpeedModifier() { return 1; }
    public ParcelModel              getTargetParcel() { return _targetParcel; }
    public ParcelModel              getStartParcel() { return _startParcel; }
    public ParcelModel              getJobParcel() { return _jobParcel; }
    public ItemInfo.ItemInfoAction  getAction() { return _actionInfo; }
    public Object                   getData() { return _data; }
    public JobTaskReturn            getLastReturn() { return _lastTaskReturn; }

    public void                     setEntertainment(boolean isEntertainment) { _isEntertainment = isEntertainment; }
    public void                     setAction(ItemInfo.ItemInfoAction action) { _actionInfo = action; _cost = action.cost; _isAuto = _actionInfo.auto; }
    public void                     setLabel(String label) { _label = label; }
    public void                     setQuantity(int quantity) { _progress = quantity; }
    public void                     setCost(int quantityTotal) { _cost = quantityTotal; }
    public void                     setCharacterRequire(CharacterModel character) { _characterRequire = character; }
    public void                     setVisible(boolean visible) { _visible = visible; }
    public void                     setData(Object data) { _data = data; }
    public void                     setMainLabel(String mainLabel) { _mainLabel = mainLabel; }
    public void                     setProgress(double current, double  total) { _progress = current / total; }

    public boolean                  isClose() { return _isClose; }
    public boolean                  isOpen() { return !_isClose; }
    public boolean                  isEntertainment() { return _isEntertainment; }
    public boolean                  isAuto() { return _isAuto; }
    public boolean                  isVisible() { return _visible; }

    protected boolean onNewInit() { return true; }
    protected boolean onFirstStart() { return true; }
    protected JobCheckReturn onCheck(CharacterModel character) { return JobCheckReturn.OK; }
    protected void onUpdate() {}
    protected void onQuit(CharacterModel character) {}
    protected void onClose() {}

    public void start(CharacterModel character) {
        Log.debug("Start job " + this + " by " + (character != null ? character.getName() : "auto"));

        if (_isClose) {
            throw new GameException(JobModel.class, "Job is close");
        }

        if (_status == JobStatus.JOB_INITIALIZED) {
            _status = JobStatus.JOB_WAITING;
            onFirstStart();
        }

        if (_isAuto && character != null) {
            throw new GameException(JobModel.class, "cannot assign character to auto job");
        }

//        // Remove job from old characters
//        if (_character != null) {
//            quit(_character);
//        }
        if (_character != null) {
            throw new GameException(JobModel.class, "start: Task is already assigned to a character");
        }

        // Set job to new characters
        _character = character;
        if (character != null) {
            character.setJob(this);
        }

        _status = JobStatus.JOB_RUNNING;
    }

    public abstract boolean checkCharacterAccepted(CharacterModel character);
    public abstract CharacterSkillExtra.SkillType getSkillType();

    /**
     * Retire le personnage de la tache, mais celle-ci continue
     *
     * @param character CharacterModel
     */
    public void quit(CharacterModel character) {
        assert character == _character;
        onQuit(character);

        assert character.getJob() == this;
        character.clearJob(this);

        _character = null;
        _status = JobStatus.JOB_WAITING;

        Log.debug("Quit job " + this + " by " + character.getName());
    }

    /**
     * La tache est fermée (terminée ou annulée)
     */
    public void close() {

        if (_character != null) {
            Log.debug("Complete job " + this + " by " + _character.getName());
            quit(_character);
        }

        onClose();

        _isClose = true;
        _status = JobStatus.JOB_COMPLETE;
    }

    public void check() {
        if (!_isClose && !onCheck()) {
            close();
        }
    }

    public void update() {
        onUpdate();
    }

    protected boolean onCheck() { return true; }

    public boolean check(CharacterModel character) {
        if (_isAuto && character != null) {
            return false;
        }

        JobCheckReturn ret = onCheck(character);

//        // TODO
//        if (ret == JobCheckReturn.JOB_BLOCKED) {
//            _fail = MainLayer.getFrame();
//        }

        if (ret == JobCheckReturn.ABORT) {
            close();
        }

        return ret == JobCheckReturn.OK;
    }

    private Queue<JobTask> _tasks = new ConcurrentLinkedQueue<>();

    public Collection<JobTask> getTasks() {
        return _tasks;
    }

    /**
     * Add custom task action
     *
     * @param label Label
     * @param jobTaskAction Action
     */
    public void addTask(String label, JobTask.JobTaskAction jobTaskAction) {

        if (_tasks.isEmpty()) {
            _label = label;
        }

        _tasks.add(new JobTask(label, jobTaskAction));
    }

    /**
     * Add move task
     *
     * @param label Label
     * @param parcel Parcel to move
     */
    public void addMoveTask(String label, ParcelModel parcel) {

        if (_tasks.isEmpty()) {
            _label = label;
        }

        _tasks.add(new JobTask(label, (character, hourInterval) -> {
            if (parcel != null && character.moveTo(parcel)) {
                return JobTaskReturn.TASK_COMPLETE;
            }
            if (parcel != null && character.getPath() != null && character.getPath().getLastParcel() == parcel) {
                return JobTaskReturn.TASK_CONTINUE;
            }
            return JobTaskReturn.TASK_ERROR;
        }));
    }

    /**
     * Add technical task
     * Technical task are basically a standard task with no return status
     *
     * @param label Label
     * @param jobTechnicalTaskAction Action
     */
    public void addTechnicalTask(String label, JobTechnicalTask.JobTechnicalTaskAction jobTechnicalTaskAction) {

        if (_tasks.isEmpty()) {
            _label = label;
        }

        _tasks.add(new JobTechnicalTask(label, jobTechnicalTaskAction));
    }

    /**
     * Execute les taches présentes dans le job
     *
     * @param character CharacterModel
     */
    public void action(CharacterModel character, double hourInterval) {

        if (isClose()) {
            throw new GameException(JobModel.class, "Cannot call action on finished job");
        }

        if (_status != JobStatus.JOB_RUNNING) {
            throw new GameException(JobModel.class, "Status must be JOB_RUNNING");
        }

        // Execute les taches à la suite tant que le retour est TASK_COMPLETE
        while (!_tasks.isEmpty()) {

            switch (actionTask(character, _tasks.peek(), hourInterval)) {

                // Task isn't complete
                case TASK_CONTINUE:
                    return;

                // Task is complete, take next task
                case TASK_COMPLETE:
                    _tasks.poll();
                    break;

                // Task return TASK_ERROR, immediatly close job
                case TASK_ERROR:
                    close();
                    return;

            }

        }

        // All tasks has been executed
        close();
    }

    /**
     * Execute la tache passé en paramètre
     *
     * @param character CharacterModel
     * @param task JobTask
     * @return JobTaskReturn
     */
    private JobTaskReturn actionTask(CharacterModel character, JobTask task, double hourInterval) {
        Log.debug(JobModel.class, "actionTask: (taks: %s, job: %s)", task.label, this);

        _lastTaskReturn = task.action.onExecuteTask(character, hourInterval);
        _label = task.label;

        Log.debug(JobModel.class, "actionTask return: %s", _lastTaskReturn);

        return _lastTaskReturn;
    }

}