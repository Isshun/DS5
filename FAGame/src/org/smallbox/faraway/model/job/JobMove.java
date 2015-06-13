package org.smallbox.faraway.model.job;

import org.smallbox.faraway.manager.JobManager;
import org.smallbox.faraway.model.character.CharacterModel;

public class JobMove extends JobModel {

	private JobMove(int x, int y) {
		super(null, x, y);
	}

	public static JobModel create(CharacterModel character, int x, int y, int stay) {
		JobModel job = new JobMove(x, y);
		job.setCharacterRequire(character);
		return job;
	}

	@Override
	public boolean check(CharacterModel character) {
		return true;
	}

	@Override
	public boolean action(CharacterModel character) {
		if (_durationLeft > 0) {
			return false;
		}

		JobManager.getInstance().close(this);
		return true;
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
	public String getLabel() {
		return "move";
	}

	@Override
	public String getShortLabel() {
		return "move";
	}
}
