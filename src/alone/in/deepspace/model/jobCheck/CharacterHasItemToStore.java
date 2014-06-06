package alone.in.deepspace.model.jobCheck;

import alone.in.deepspace.manager.JobManager;
import alone.in.deepspace.model.character.Character;
import alone.in.deepspace.model.job.JobStore;

// Character has item to store
public class CharacterHasItemToStore implements JobCharacterCheck {

	@Override
	public boolean create(JobManager jobManager, Character character) {
		if (character.getInventory().size() > 0) {
			jobManager.addJob(JobStore.create(character), character);
			return true;
		}
		
		return false;
	}

}
