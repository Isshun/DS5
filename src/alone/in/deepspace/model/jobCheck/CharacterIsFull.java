package alone.in.deepspace.model.jobCheck;

import alone.in.deepspace.manager.JobManager;
import alone.in.deepspace.model.Character;
import alone.in.deepspace.model.job.Job;
import alone.in.deepspace.model.job.JobStore;

public class CharacterIsFull implements JobCheck {

	@Override
	public Job create(JobManager jobManager, Character character) {
		if (character.isFull()) {
			return JobStore.create(character);
		}
		
		return null;
	}

}
