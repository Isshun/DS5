package org.smallbox.faraway.model.job;

import org.smallbox.faraway.manager.JobManager;
import org.smallbox.faraway.model.character.CharacterModel;
import org.smallbox.faraway.model.item.ItemInfo;

public class JobMove extends Job {

	private JobMove(int x, int y) {
		super(null, x, y);
	}

	public static Job create(CharacterModel character, int x, int y, int stay) {
		Job job = new JobMove(x, y);
		job.setAction(JobManager.Action.MOVE);
		job.setCharacterRequire(character);
		job.setDurationLeft(stay);
		return job;
	}

	@Override
	public boolean check(CharacterModel character) {
		return true;
	}

	@Override
	public boolean action(CharacterModel character) {
		if (_durationLeft > 0) {
			decreaseDurationLeft();
			return false;
		}

		JobManager.getInstance().complete(this);
		return true;
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
