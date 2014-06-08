package alone.in.deepspace.model.jobCheck;

import alone.in.deepspace.manager.JobManager;
import alone.in.deepspace.model.character.Character;
import alone.in.deepspace.model.job.Job;
import alone.in.deepspace.model.job.JobStore;

// Character has item to store
public class CharacterHasItemToStore implements JobCharacterCheck {

	@Override
	public boolean create(JobManager jobManager, Character character) {
		if (character.getInventory().size() > 0) {
			Job job = JobStore.create(character);
			if (job != null) {
				jobManager.addJob(job, character);
				return true;
			}
		}
		
		return false;
	}

}
