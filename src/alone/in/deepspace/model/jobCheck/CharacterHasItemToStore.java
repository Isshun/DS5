package alone.in.deepspace.model.jobCheck;

import alone.in.deepspace.manager.JobManager;
import alone.in.deepspace.model.character.Character;
import alone.in.deepspace.model.job.Job;
import alone.in.deepspace.model.job.JobStore;

// Character has item to store
public class CharacterHasItemToStore implements JobCheck {

	@Override
	public Job create(JobManager jobManager, Character character) {
		if (character.getInventory().size() > 0) {
			return JobStore.create(character);
		}
		
		return null;
	}

}
