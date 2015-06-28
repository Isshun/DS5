package org.smallbox.faraway.game.model.job;

import org.smallbox.faraway.game.model.GameConfig;
import org.smallbox.faraway.game.model.character.base.CharacterModel;
import org.smallbox.faraway.util.Log;

public class JobMove extends BaseJobModel {
	private GameConfig.EffectValues _effects;
    private int 					_distance;
    private double 					_speedModifier = 1;

    private JobMove(int x, int y) {
		super(null, x, y, "data/res/ic_dump.png", "data/res/ic_action_dump.png");
	}

	public static JobMove create(CharacterModel character, int x, int y) {
		JobMove job = new JobMove(x, y);
		job.setCharacterRequire(character);
		return job;
	}

	@Override
	public boolean onCheck(CharacterModel character) {
		return true;
	}

	@Override
	protected void onFinish() {
		Log.info("JobMove: character reach position");
	}

	@Override
	public JobActionReturn onAction(CharacterModel character) {

		// If job has EffectValues, update character needs
		if (_effects != null) {
			character.getNeeds().updateNeeds(_effects);
		}

//		if (_durationLeft > 0) {
//			return false;
//		}

		// Job is done
		if (character.getX() != _posX || character.getY() != _posY) {
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

	public void setEffects(GameConfig.EffectValues effects) {
		_effects = effects;
	}

    protected void onStart(CharacterModel character){
        _distance = character != null ? Math.abs(character.getX() - _posX) + Math.abs(character.getY() - _posY) : 0;
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
