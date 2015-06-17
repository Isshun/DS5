package org.smallbox.faraway.model.job;

import org.smallbox.faraway.manager.JobManager;
import org.smallbox.faraway.model.GameConfig;
import org.smallbox.faraway.model.character.base.CharacterModel;

public class JobMove extends JobModel {
	private GameConfig.EffectValues _effects;

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
}
