package alone.in.deepspace.model.job;

import alone.in.deepspace.manager.JobManager;
import alone.in.deepspace.model.Character;

public class JobMove extends Job {

	private JobMove(int x, int y) {
		super(x, y);
	}

	public static Job create(Character character, int x, int y, int stay) {
		Job job = new JobMove(x, y);
		job.setAction(JobManager.Action.MOVE);
		job.setCharacterRequire(character);
		job.setDurationLeft(stay);
		return job;
	}

	@Override
	public Abort check(Character character) {
		return null;
	}

}
