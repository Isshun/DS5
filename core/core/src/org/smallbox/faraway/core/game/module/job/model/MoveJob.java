package org.smallbox.faraway.core.game.module.job.model;

import org.smallbox.faraway.core.engine.drawable.AnimDrawable;
import org.smallbox.faraway.core.engine.drawable.IconDrawable;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.module.character.model.CharacterTalentExtra;
import org.smallbox.faraway.core.game.module.character.model.base.CharacterModel;
import org.smallbox.faraway.core.game.module.job.model.abs.JobModel;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.core.util.Log;

public class MoveJob extends JobModel {
    private int                     _distance;
    private double                     _speedModifier = 1;

    private MoveJob(ParcelModel jobParcel) {
        super(null, jobParcel, new IconDrawable("data/res/ic_dump.png", 0, 0, 32, 32), new AnimDrawable("data/res/actions.png", 0, 128, 32, 32, 7, 10));
    }

    public static MoveJob create(CharacterModel character, ParcelModel jobParcel) {
        MoveJob job = new MoveJob(jobParcel);
        job.setCharacterRequire(character);
        return job;
    }

    @Override
    public boolean onCheck(CharacterModel character) {
        return true;
    }

    @Override
    protected void onFinish() {
        Log.info("MoveJob: characters reach position");
    }

    @Override
    public JobActionReturn onAction(CharacterModel character) {

//        if (_durationLeft > 0) {
//            return false;
//        }

        // Job is done
        if (character.getParcel() != _targetParcel) {
            return JobActionReturn.CONTINUE;
        }

        return JobActionReturn.FINISH;
    }

    @Override
    public boolean canBeResume() {
        return false;
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
    public String getShortLabel() {
        return "move";
    }

    @Override
    public ParcelModel getActionParcel() {
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
