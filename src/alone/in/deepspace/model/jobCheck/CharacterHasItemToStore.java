package alone.in.deepspace.model.jobCheck;

import alone.in.deepspace.manager.JobManager;
import alone.in.deepspace.manager.ServiceManager;
import alone.in.deepspace.model.Character;
import alone.in.deepspace.model.Job;
import alone.in.deepspace.model.StorageItem;

// Character has item to store
public class CharacterHasItemToStore implements JobCheck {

	@Override
	public void check(JobManager jobManager, Character character) {
		if (character.getCarried().size() > 0) {
			StorageItem storage = ServiceManager.getWorldMap().getNearestStorage(character.getX(), character.getY(), character.getCarried().get(0));
			if (storage != null) {
				Job job = jobManager.createStoreJob(character, storage);
				if (job != null) {
					character.setJob(job);
					jobManager.addJob(job);
				}
			}
		}
		
		return;
	}

}
