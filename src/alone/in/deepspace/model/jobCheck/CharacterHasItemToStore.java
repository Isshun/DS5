package alone.in.deepspace.model.jobCheck;

import alone.in.deepspace.manager.JobManager;
import alone.in.deepspace.model.Character;
import alone.in.deepspace.model.job.Job;

// Character has item to store
public class CharacterHasItemToStore implements JobCheck {

	@Override
	public void check(JobManager jobManager, Character character) {
		if (character.getCarried().size() > 0) {
			Job job = jobManager.addStoreJob(character);
			if (job != null) {
				character.setJob(job);
				jobManager.addJob(job);
			}
		}
		
		return;
	}

}
