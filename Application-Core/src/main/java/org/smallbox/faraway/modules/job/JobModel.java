package org.smallbox.faraway.modules.job;

import com.badlogic.gdx.graphics.Color;
import org.smallbox.faraway.client.manager.SoundManager;
import org.smallbox.faraway.common.ObjectModel;
import org.smallbox.faraway.core.GameException;
import org.smallbox.faraway.core.dependencyInjector.DependencyManager;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo.ItemInfoAction;
import org.smallbox.faraway.core.module.world.model.ItemFilter;
import org.smallbox.faraway.core.module.world.model.Parcel;
import org.smallbox.faraway.modules.building.BuildJob;
import org.smallbox.faraway.modules.character.model.CharacterSkillExtra;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;
import org.smallbox.faraway.modules.job.taskAction.PrerequisiteTaskAction;
import org.smallbox.faraway.modules.job.taskAction.TechnicalTaskAction;
import org.smallbox.faraway.modules.storage.StoreJob;
import org.smallbox.faraway.util.log.Log;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class JobModel extends ObjectModel {

    public double _time;
    private String icon;
    private long soundId;
    private Color color;
    private final Collection<JobModel> subJob = new ConcurrentLinkedQueue<>();
    private boolean exactParcel;
    private boolean optional;
    private float moveSpeed = 1;

    public void setMoveSpeed(float moveSpeed) {
        this.moveSpeed = moveSpeed;
    }

    public long getSoundId() {
        return soundId;
    }

    public void setSoundId(long soundId) {
        this.soundId = soundId;
    }

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

    public boolean isExactParcel() {
        return exactParcel;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public void executeInitTasks() {
        initTasks.forEach(technicalTaskAction -> technicalTaskAction.onExecuteTask(this));
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

    public void setExactParcel(boolean exactParcel) {
        this.exactParcel = exactParcel;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    public boolean isOptional() {
        return optional;
    }

    public float getMoveSpeed() {
        return moveSpeed;
    }

    public void setStatus(JobStatus jobStatus) {
        this.status = jobStatus;
    }

    public void setBlockedUntil(LocalDateTime _blocked) {
        this._blocked = _blocked;
    }

    protected int               _limit;
    protected int               _fail;
    protected LocalDateTime _blocked;
    protected int               _cost = 1;
    protected boolean           _isClose;
    protected double            _progress;
    protected ItemFilter        _filter;
    protected ItemInfoAction    _actionInfo;
    protected CharacterModel character;
    protected CharacterModel    _characterRequire;
    protected JobAbortReason    _reason;
    protected String            _label;
    protected JobStatus status = JobStatus.JOB_INITIALIZED;
    protected String            _message;
    public Parcel _targetParcel;
    protected boolean           _isEntertainment;
    protected boolean           _isAuto;
    protected String            _mainLabel = "";
    protected Object            _data;
    protected boolean           _visible = true;
    protected JobTaskReturn     _lastTaskReturn;
    protected Map<CharacterModel, JobCharacterStatus> statusMap = new ConcurrentHashMap<>();
    private CharacterSkillExtra.SkillType skillType;

    public JobModel(ItemInfo.ItemInfoAction actionInfo, Parcel targetParcel) {
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
        status = JobStatus.JOB_INITIALIZED;
        _limit = -1;
        _label = "none";

        Log.debug("Job #" + _id + " create new " + getClass().getSimpleName());
    }

    public String                   getMessage() { return _message; }
    public String                   getLabel() { return _mainLabel; }
    public String                   getMainLabel() { return _mainLabel; }
    public CharacterModel           getCharacter() { return character; }
    public CharacterModel           getCharacterRequire() { return _characterRequire; }
    public int                      getFail() { return _fail; }
    public LocalDateTime getBlocked() { return _blocked; }
    public boolean isBlocked() { return status == JobStatus.JOB_BLOCKED; }
    public boolean isAvailable() { return status == JobStatus.JOB_INITIALIZED || status == JobStatus.JOB_WAITING; }
    public boolean isFree() { return character == null; }
    public JobAbortReason           getReason() { return _reason; }
    public double                   getQuantity() { return _progress; }
    public double                   getProgress() { return Math.min(_progress, 1); }
    public JobStatus                getStatus() { return status; }
    public JobCharacterStatus getStatusForCharacter(CharacterModel character) { return statusMap.get(character); }
    public double                   getSpeedModifier() { return 1; }
    public Parcel getTargetParcel() { return _targetParcel; }
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
    public void                     setCharacter(CharacterModel character) { this.character = character; }

    public boolean                  isClose() { return _isClose; }
    public boolean                  isOpen() { return !_isClose; }
    public boolean                  isEntertainment() { return _isEntertainment; }
    public boolean                  isAuto() { return _isAuto; }
    public boolean                  isVisible() { return _visible; }

    public boolean onNewInit() { return true; }
    protected boolean onFirstStart() { return true; }
    protected JobCheckReturn onCheck(CharacterModel character) { return JobCheckReturn.OK; }
    protected void onUpdate() {}
    protected void onClose() {}

    public void start(CharacterModel character) {
        Log.debug("Start job " + this + " by " + (character != null ? character.getName() : "auto"));

        if (_isClose) {
            throw new GameException(JobModel.class, "Job is close");
        }

        if (status == JobStatus.JOB_INITIALIZED) {
            status = JobStatus.JOB_WAITING;
            onFirstStart();
        }

        if (_isAuto && character != null) {
            throw new GameException(JobModel.class, "cannot assign character to auto job");
        }

//        // Remove job from old characters
//        if (_character != null) {
//            quit(_character);
//        }
        if (this.character != null) {
            throw new GameException(JobModel.class, "start: Task is already assigned to a character");
        }

        // Set job to new characters
        this.character = character;
        if (character != null) {
            character.setJob(this);
        }

        status = JobStatus.JOB_RUNNING;
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
     */
    public void clearCharacter(CharacterModel character, LocalDateTime currentTime) {
        if (this.character == character) {
            this.character = null;
            this.status = JobStatus.JOB_WAITING;
            Log.debug("Character cleared from job: " + this);

            character.clearJob(this, currentTime);
        }
    }

    /**
     * La tache est fermée (terminée ou annulée)
     * @param currentTime
     */
    public void close(LocalDateTime currentTime) {

        if (character != null) {
            Log.debug("Complete job " + this + " by " + character.getName());
            clearCharacter(character, currentTime);
        }

        onClose();

        closeTasks.forEach(technicalTaskAction -> technicalTaskAction.onExecuteTask(this));

        _isClose = true;
        status = JobStatus.JOB_COMPLETE;

        DependencyManager.getInstance().getDependency(SoundManager.class).stop(soundId);
    }

    public boolean check() {
        return onCheck();
    }

    public void update() {
        onUpdate();
    }

    protected boolean onCheck() { return true; }

    private final Queue<JobTask> _tasks = new ConcurrentLinkedQueue<>();
    private final Queue<TechnicalTaskAction> initTasks = new ConcurrentLinkedQueue<>();
    private final Queue<PrerequisiteTaskAction> prerequisiteTasks = new ConcurrentLinkedQueue<>();
    private final Queue<TechnicalTaskAction> closeTasks = new ConcurrentLinkedQueue<>();

    public Collection<JobTask> getTasks() {
        return _tasks;
    }

    public void addTask(JobTask jobTask) {
        if (_tasks.isEmpty()) {
            _label = jobTask.label;
        }

        _tasks.add(jobTask);
    }

    public boolean hasStatus(JobStatus status) {
        return this.status == status;
    }

    public boolean hasReason(JobAbortReason reason) {
        return this._reason == reason;
    }

    public interface ParcelCallback {
        Parcel getParcel();
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
    public void action(CharacterModel character, double hourInterval, LocalDateTime currentTime) {

        if (isClose()) {
            throw new GameException(JobModel.class, "Cannot call action on finished job");
        }

        if (status != JobStatus.JOB_RUNNING) {
            throw new GameException(JobModel.class, "Status must be JOB_RUNNING");
        }

        // Execute les taches à la suite tant que le retour est TASK_COMPLETE
        while (!_tasks.isEmpty()) {

            switch (actionTask(character, _tasks.peek(), hourInterval, currentTime)) {

                // Task isn't complete
                case TASK_CONTINUE:
                    if (soundId == 0) {
                        soundId = DependencyManager.getInstance().getDependency(SoundManager.class).start();
                    }
                    return;

                // Task return TASK_COMPLETED_STOP, stop to execute action until next update
                case TASK_COMPLETED_STOP:
                    _tasks.poll();
                    return;

                // Task is complete, take next task
                case TASK_COMPLETED:
                    _tasks.poll();
                    break;

                // Task return TASK_ERROR, immediatly close job
                case TASK_ERROR:
                    close(currentTime);
                    return;

            }

        }

        // All tasks has been executed
        close(currentTime);
    }

    /**
     * Execute la tache passé en paramètre
     *
     * @param character CharacterModel
     * @param task JobTask
     * @return JobTaskReturn
     */
    private JobTaskReturn actionTask(CharacterModel character, JobTask task, double hourInterval, LocalDateTime localDateTime) {
        Log.debug(JobModel.class, "actionTask: (taks: %s, job: %s)", task.label, this);

        if (task.technicalAction != null) {
            task.technicalAction.onExecuteTask(this);
            return task.taskReturn;
        }

        if (task.startTime == null) {
            task.init(localDateTime);
        }

        task.action(character, hourInterval, localDateTime);
        _lastTaskReturn = task.getStatus(character, hourInterval, localDateTime);
        _label = task.label;

        Log.debug(JobModel.class, "actionTask return: %s", _lastTaskReturn);

        return _lastTaskReturn;
    }

    public Collection<JobModel> getSubJob() {
        return subJob;
    }

}