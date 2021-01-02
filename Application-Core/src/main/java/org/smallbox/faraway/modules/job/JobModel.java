package org.smallbox.faraway.modules.job;

import com.badlogic.gdx.graphics.Color;
import org.smallbox.faraway.common.ObjectModel;
import org.smallbox.faraway.core.GameException;
import org.smallbox.faraway.core.dependencyInjector.DependencyInjector;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo.ItemInfoAction;
import org.smallbox.faraway.core.module.world.model.ItemFilter;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.building.BuildJob;
import org.smallbox.faraway.modules.character.CharacterMoveModule;
import org.smallbox.faraway.modules.character.model.CharacterSkillExtra;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;
import org.smallbox.faraway.modules.job.taskAction.PrerequisiteTaskAction;
import org.smallbox.faraway.modules.job.taskAction.TechnicalTaskAction;
import org.smallbox.faraway.modules.storage.StoreJob;
import org.smallbox.faraway.util.log.Log;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class JobModel extends ObjectModel {

    public double _time;
    private String icon;
    private Color color;
    private Collection<JobModel> subJob = new ConcurrentLinkedQueue<>();

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getIcon() {
//        if (this instanceof BasicHaulJob) return "[base]/graphics/jobs/ic_haul.png";
        if (this instanceof StoreJob) return "[base]/graphics/jobs/ic_store.png";
//        if (this instanceof BasicCraftJob) return "[base]/graphics/jobs/ic_craft.png";
        if (this instanceof BuildJob) return "[base]/graphics/jobs/ic_build.png";
//        if (this instanceof BasicRepairJob) return "[base]/graphics/jobs/ic_build.png";
        return icon;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public void executeInitTasks() {
        initTasks.forEach(TechnicalTaskAction::onExecuteTask);
    }

    public void addSubJob(JobModel job) {
        subJob.add(job);
    }

    public boolean isSubJobCompleted() {
        return subJob.stream().allMatch(JobModel::isClose);
    }

    public boolean initConditionalCompleted() {
        return this.prerequisiteTasks.stream().allMatch(PrerequisiteTaskAction::onExecuteTask);
    }

    public void unblock() {
        _status = JobStatus.JOB_WAITING;
        _blocked = null;
    }

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
    protected LocalDateTime _blocked;
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
    public ParcelModel       _targetParcel;
    protected boolean           _isEntertainment;
    protected boolean           _isAuto;
    protected String            _mainLabel = "";
    protected Object            _data;
    protected boolean           _visible = true;
    protected JobTaskReturn     _lastTaskReturn;
    private CharacterSkillExtra.SkillType skillType;

    public JobModel(ItemInfo.ItemInfoAction actionInfo, ParcelModel targetParcel) {
        init();
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
    public LocalDateTime getBlocked() { return _blocked; }
    public boolean isBlocked() { return _status == JobStatus.JOB_BLOCKED; }
    public boolean isAvailable() { return _status == JobStatus.JOB_INITIALIZED || _status == JobStatus.JOB_WAITING; }
    public boolean isFree() { return _character == null; }
    public JobAbortReason           getReason() { return _reason; }
    public double                   getQuantity() { return _progress; }
    public double                   getProgress() { return Math.min(_progress, 1); }
    public JobStatus                getStatus() { return _status; }
    public double                   getSpeedModifier() { return 1; }
    public ParcelModel              getTargetParcel() { return _targetParcel; }
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
    public void block(CharacterModel character, LocalDateTime blocked) {
        quit(character);
        _blocked = blocked;
        _status = JobStatus.JOB_BLOCKED;
    }
    public void                     setCharacter(CharacterModel character) { _character = character; }

    public boolean                  isClose() { return _isClose; }
    public boolean                  isOpen() { return !_isClose; }
    public boolean                  isEntertainment() { return _isEntertainment; }
    public boolean                  isAuto() { return _isAuto; }
    public boolean                  isVisible() { return _visible; }

    public boolean onNewInit() { return true; }
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

    public boolean checkCharacterAccepted(CharacterModel character) {
        return true;
    }

    public CharacterSkillExtra.SkillType getSkillType() {
        return skillType;
    }

    public void setSkillType(CharacterSkillExtra.SkillType skillType) {
        this.skillType = skillType;
    }

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

        closeTasks.forEach(TechnicalTaskAction::onExecuteTask);

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
    private Queue<TechnicalTaskAction> initTasks = new ConcurrentLinkedQueue<>();
    private Queue<PrerequisiteTaskAction> prerequisiteTasks = new ConcurrentLinkedQueue<>();
    private Queue<TechnicalTaskAction> closeTasks = new ConcurrentLinkedQueue<>();

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

    public interface ParcelCallback {
        ParcelModel getParcel();
    }

    /**
     * Add move task
     */
    public void addMoveTask(String label, ParcelCallback parcelCallback) {
        if (_tasks.isEmpty()) {
            _label = label;
        }

        _tasks.add(new JobTask(label, (character, hourInterval) -> {
            ParcelModel targetParcel = parcelCallback.getParcel();
            Objects.requireNonNull(targetParcel);

            if (character.getPath() == null || character.getPath().getLastParcel() != targetParcel) {
                DependencyInjector.getInstance().getDependency(CharacterMoveModule.class).move(character, targetParcel, true);
                return JobTaskReturn.TASK_CONTINUE;
            }

            if (character.getParcel() == character.getPath().getLastParcelCharacter()) {
                return JobTaskReturn.TASK_COMPLETE;
            }

            return JobTaskReturn.TASK_CONTINUE;
        }));
    }

    public void addTechnicalTask(TechnicalTaskAction technicalTaskAction) {
        _tasks.add(new JobTask("Technical", technicalTaskAction));
    }

    public void addInitTask(TechnicalTaskAction technicalTaskAction) {
        initTasks.add(technicalTaskAction);
    }

    public void addPrerequisiteTask(PrerequisiteTaskAction jobTechnicalTaskAction) {
        prerequisiteTasks.add(jobTechnicalTaskAction);
    }

    public void addCloseTask(TechnicalTaskAction technicalTaskAction) {
        closeTasks.add(technicalTaskAction);
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

        if (task.technicalAction != null) {
            task.technicalAction.onExecuteTask();
            return JobTaskReturn.TASK_COMPLETE;
        }

        _lastTaskReturn = task.action.onExecuteTask(character, hourInterval);
        _label = task.label;

        Log.debug(JobModel.class, "actionTask return: %s", _lastTaskReturn);

        return _lastTaskReturn;
    }

    public Collection<JobModel> getSubJob() {
        return subJob;
    }

}