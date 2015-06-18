package org.smallbox.faraway.game.model.job;

import org.smallbox.faraway.game.manager.JobManager;
import org.smallbox.faraway.game.model.GameConfig;
import org.smallbox.faraway.game.model.character.base.CharacterModel;

public class JobMove extends JobModel {
	private GameConfig.EffectValues _effects;
    private int _distance;
    private double _speedModifier;

    private JobMove(int x, int y) {
		super(null, x, y);
	}

	public static JobMove create(CharacterModel character, int x, int y) {
		JobMove job = new JobMove(x, y);
		job.setCharacterRequire(character);
		return job;
	}

	@Override
	public boolean check(CharacterModel character) {
		return true;
	}

	@Override
	public boolean action(CharacterModel character) {

		// If job has EffectValues, update character needs
		if (_effects != null) {
			character.getNeeds().updateNeeds(_effects);
		}

//		if (_durationLeft > 0) {
//			return false;
//		}

		// Job is done
		if (character.getX() == _posX && character.getY() == _posY) {
			JobManager.getInstance().close(this);
			return true;
		}

		return false;
	}

	@Override
	public String getType() {
		return "move";
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

    protected void onCharacterAssign(CharacterModel character){
        _distance = character != null ? Math.abs(character.getX() - _posX) + Math.abs(character.getY() - _posY) : 0;
    }

    public double               getProgress() {
        if (_character != null && _distance != 0) {
            int distance = Math.abs(_character.getX() - _posX) + Math.abs(_character.getY() - _posY);
            return (double)distance / _distance;
        }
        return 0;
    }

    @Override
    public double getSpeedModifier() {
        return _speedModifier;
    }

    public void setSpeedModifier(double speedModifier) {
        _speedModifier = speedModifier;
    }
}
