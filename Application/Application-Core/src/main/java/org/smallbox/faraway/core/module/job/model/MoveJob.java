package org.smallbox.faraway.core.module.job.model;

import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.module.character.model.CharacterTalentExtra;
import org.smallbox.faraway.core.module.character.model.base.CharacterModel;
import org.smallbox.faraway.core.module.job.model.abs.JobModel;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.util.Log;

public class MoveJob extends JobModel {
    private int                     _distance;
    private double                     _speedModifier = 1;

    private MoveJob(ParcelModel jobParcel) {
        super(null, jobParcel);
//        super(null, jobParcel, new IconDrawable("data/res/ic_dump.png", 0, 0, 32, 32), new AnimDrawable("data/res/actions.png", 0, 128, 32, 32, 7, 10));
    }

    public static MoveJob create(CharacterModel character, ParcelModel jobParcel) {
        MoveJob job = new MoveJob(jobParcel);
        job.setCharacterRequire(character);
        return job;
    }

    @Override
    public JobCheckReturn onCheck(CharacterModel character) {
        return JobCheckReturn.OK;
    }

    @Override
    protected void onFinish() {
        Log.info("MoveJob: characters reach position");
    }

    @Override
    public JobActionReturn onAction(CharacterModel character) {

        // Job is done
        if (character.getParcel() != _targetParcel) {
            return JobActionReturn.CONTINUE;
        }

        return JobActionReturn.COMPLETE;
    }

    @Override
    public CharacterTalentExtra.TalentType getTalentNeeded() {
        return null;
    }

    @Override
    public boolean isVisibleInUI() {
        return false;
    }

    @Override
    public ParcelModel getTargetParcel() {
        return null;
    }

    protected void onStart(CharacterModel character){
        _distance = character != null ? WorldHelper.getApproxDistance(character.getParcel(), _targetParcel) : 0;
    }

    public double               getProgress() { return (double)(_limit - _currentLimit) / _limit; }

    @Override
    public double getSpeedModifier() {
        return _speedModifier;
    }

    @Override
    public void onQuit(CharacterModel character) {

    }

    public void setSpeedModifier(double speedModifier) {
        _speedModifier = speedModifier;
    }
}