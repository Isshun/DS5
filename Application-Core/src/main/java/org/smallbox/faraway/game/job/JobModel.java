package org.smallbox.faraway.game.job;

import com.badlogic.gdx.graphics.Color;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo.ItemInfoAction;
import org.smallbox.faraway.game.character.model.CharacterSkillExtra;
import org.smallbox.faraway.game.character.model.base.CharacterModel;
import org.smallbox.faraway.game.job.taskAction.PrerequisiteTaskAction;
import org.smallbox.faraway.game.job.taskAction.TechnicalTaskAction;
import org.smallbox.faraway.game.world.ObjectModel;
import org.smallbox.faraway.game.world.Parcel;
import org.smallbox.faraway.util.log.Log;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class JobModel extends ObjectModel {
    private final Parcel targetParcel;
    private final Collection<JobModel> subJob = new ConcurrentLinkedQueue<>();
    private final Collection<Parcel> acceptedParcels = new ConcurrentLinkedQueue<>();
    private final Queue<JobTask> tasks = new ConcurrentLinkedQueue<>();
    private final Queue<TechnicalTaskAction> initTasks = new ConcurrentLinkedQueue<>();
    private final Queue<PrerequisiteTaskAction> prerequisiteTasks = new ConcurrentLinkedQueue<>();
    private final Queue<TechnicalTaskAction> closeTasks = new ConcurrentLinkedQueue<>();
    private final Map<CharacterModel, JobCharacterStatus> statusMap = new ConcurrentHashMap<>();
    private JobStatus status = JobStatus.JOB_INITIALIZED;
    private double totalDuration;
    private double duration;
    private String mainLabel = "";
    private String label;
    private String icon;
    private Color color;
    private float moveSpeed = 1;
    private LocalDateTime blocked;
    private ItemInfoAction actionInfo;
    private CharacterModel character;
    private CharacterModel characterRequire;
    private boolean visible = true;
    private boolean isAuto;
    private boolean optional;
    private CharacterSkillExtra.SkillType skillType;

    public JobModel(Parcel targetParcel) {
        this.targetParcel = targetParcel;
    }

    public JobModel(Parcel targetParcel, ItemInfoAction actionInfo) {
        init();
        this.targetParcel = targetParcel;
        if (actionInfo != null) {
            this.actionInfo = actionInfo;
        }
    }

    private void init() {
        status = JobStatus.JOB_INITIALIZED;

        Log.debug("Job #" + _id + " create new " + getClass().getSimpleName());
    }

    public Collection<TechnicalTaskAction> getInitTasks() {
        return initTasks;
    }

    public Collection<TechnicalTaskAction> getCloseTasks() {
        return closeTasks;
    }

    public double getDuration() {
        return duration;
    }

    public void addProgression(double progression) {
        duration += progression;
    }

    public void addAcceptedParcel(Parcel acceptedParcel) {
        this.acceptedParcels.add(acceptedParcel);
    }

    public Collection<Parcel> getAcceptedParcels() {
        return acceptedParcels;
    }

    public void setMoveSpeed(float moveSpeed) {
        this.moveSpeed = moveSpeed;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getIcon() {
        return icon;
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
        this.blocked = _blocked;
    }

    public String getLabel() {
        return mainLabel;
    }

    public String getMainLabel() {
        return mainLabel;
    }

    public CharacterModel getCharacter() {
        return character;
    }

    public CharacterModel getCharacterRequire() {
        return characterRequire;
    }

    public LocalDateTime getBlocked() {
        return blocked;
    }

    public boolean isBlocked() {
        return status == JobStatus.JOB_BLOCKED;
    }

    public boolean isAvailable() {
        return status == JobStatus.JOB_INITIALIZED || status == JobStatus.JOB_WAITING;
    }

    public boolean isFree() {
        return character == null;
    }

    public double getProgress() {
        return Math.min(duration / totalDuration, 1);
    }

    public JobStatus getStatus() {
        return status;
    }

    public JobCharacterStatus getStatusForCharacter(CharacterModel character) {
        return statusMap.get(character);
    }

    public double getSpeedModifier() {
        return 1;
    }

    public Parcel getTargetParcel() {
        return targetParcel;
    }

    public ItemInfo.ItemInfoAction getAction() {
        return actionInfo;
    }

    public void setAction(ItemInfo.ItemInfoAction action) {
        actionInfo = action;
        isAuto = actionInfo.auto;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setCharacterRequire(CharacterModel character) {
        characterRequire = character;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void setMainLabel(String mainLabel) {
        this.mainLabel = mainLabel;
    }

    public void setCharacter(CharacterModel character) {
        this.character = character;
    }

    public boolean isClose() {
        return status == JobStatus.JOB_COMPLETE;
    }

    public boolean isOpen() {
        return status != JobStatus.JOB_COMPLETE;
    }

    public boolean isAuto() {
        return isAuto;
    }

    public boolean isVisible() {
        return visible;
    }

    public boolean onNewInit() {
        return true;
    }

    protected boolean onFirstStart() {
        return true;
    }

    protected JobCheckReturn onCheck(CharacterModel character) {
        return JobCheckReturn.OK;
    }

    protected void onUpdate() {
    }

    protected void onClose() {
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

    protected boolean onCheck() {
        return true;
    }

    public Collection<JobTask> getTasks() {
        return tasks;
    }

    public JobTask lastTask() {
        return tasks.peek();
    }

    public JobTask nextTask() {
        return tasks.poll();
    }

    public void addTask(JobTask jobTask) {
        if (tasks.isEmpty()) {
            label = jobTask.label;
        }

        tasks.add(jobTask);
    }

    public boolean hasStatus(JobStatus status) {
        return this.status == status;
    }

    public void setAcceptedParcel(Collection<Parcel> acceptedParcels) {
        this.acceptedParcels.clear();
        this.acceptedParcels.addAll(acceptedParcels);
    }

    public void setTotalDuration(double totalDuration) {
        this.totalDuration = totalDuration;
    }

    public double getTotalDuration() {
        return totalDuration;
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

    public Collection<JobModel> getSubJob() {
        return subJob;
    }

    public void setStatus(CharacterModel character, JobCharacterStatus statusForCharacter) {
        statusMap.put(character, statusForCharacter);
    }

}