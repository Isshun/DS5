package org.smallbox.faraway.core.module.job.model;

import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.module.world.model.Parcel;
import org.smallbox.faraway.modules.character.model.CharacterSkillExtra;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;
import org.smallbox.faraway.modules.job.JobCheckReturn;
import org.smallbox.faraway.modules.job.JobModel;
import org.smallbox.faraway.modules.job.task.MoveTask;
import org.smallbox.faraway.util.log.Log;

public class MoveJob extends JobModel {
    private int                     _distance;
    private double                     _speedModifier = 1;

    private MoveJob(Parcel jobParcel) {
        super(null, jobParcel);

        setMainLabel("Move to " + jobParcel.x + "x" + jobParcel.y + " (f" + jobParcel.z + ")");
        addTask(new MoveTask("Move", () -> jobParcel));

//        super(null, jobParcel, new IconDrawable("data/res/ic_dump.png", 0, 0, 32, 32), new AnimDrawable("data/res/actions.png", 0, 128, 32, 32, 7, 10));
    }

    public static MoveJob create(CharacterModel character, Parcel jobParcel) {
        MoveJob job = new MoveJob(jobParcel);
        job.setCharacterRequire(character);
        return job;
    }

    @Override
    public JobCheckReturn onCheck(CharacterModel character) {
        return JobCheckReturn.OK;
    }

    @Override
    protected void onClose() {
        Log.info("MoveJob: characters reach position");
    }

    @Override
    public boolean checkCharacterAccepted(CharacterModel character) {
        return true;
    }

    @Override
    public CharacterSkillExtra.SkillType getSkillType() {
        return null;
    }

    @Override
    public Parcel getTargetParcel() {
        return null;
    }

    protected void onStart(CharacterModel character){
        _distance = character != null ? WorldHelper.getApproxDistance(character.getParcel(), _targetParcel) : 0;
    }

    public double getProgress() { return (double)(_limit - 0) / _limit; }

    @Override
    public double getSpeedModifier() {
        return _speedModifier;
    }

    public void setSpeedModifier(double speedModifier) {
        _speedModifier = speedModifier;
    }

}
