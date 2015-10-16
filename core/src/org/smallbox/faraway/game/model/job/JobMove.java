package org.smallbox.faraway.game.model.job;

import org.smallbox.faraway.core.drawable.AnimDrawable;
import org.smallbox.faraway.core.drawable.IconDrawable;
import org.smallbox.faraway.game.model.character.base.CharacterModel;
import org.smallbox.faraway.game.model.item.ParcelModel;
import org.smallbox.faraway.util.Log;

public class JobMove extends BaseJobModel {
    private int 					_distance;
    private double 					_speedModifier = 1;

    private JobMove(ParcelModel jobParcel) {
		super(null, jobParcel, new IconDrawable("data/res/ic_dump.png", 0, 0, 32, 32), new AnimDrawable("data/res/actions.png", 0, 128, 32, 32, 7, 10));
	}

	public static JobMove create(CharacterModel character, ParcelModel jobParcel) {
		JobMove job = new JobMove(jobParcel);
		job.setCharacterRequire(character);
		return job;
	}

	@Override
	public boolean onCheck(CharacterModel character) {
		return true;
	}

	@Override
	protected void onFinish() {
		Log.info("JobMove: characters reach position");
	}

	@Override
	public JobActionReturn onAction(CharacterModel character) {

//		if (_durationLeft > 0) {
//			return false;
//		}

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
	public CharacterModel.TalentType getTalentNeeded() {
		return CharacterModel.TalentType.HAUL;
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
        _distance = character != null ? Math.abs(character.getX() - _targetParcel.x) + Math.abs(character.getY() - _targetParcel.y) : 0;
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
