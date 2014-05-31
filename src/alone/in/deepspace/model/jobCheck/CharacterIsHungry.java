package alone.in.deepspace.model.jobCheck;

import alone.in.deepspace.manager.ItemFilter;
import alone.in.deepspace.manager.JobManager;
import alone.in.deepspace.manager.ServiceManager;
import alone.in.deepspace.model.BaseItem;
import alone.in.deepspace.model.StorageItem;
import alone.in.deepspace.model.character.Character;
import alone.in.deepspace.model.job.Job;
import alone.in.deepspace.model.job.JobTake;
import alone.in.deepspace.model.job.JobUse;
import alone.in.deepspace.model.job.JobUseInventory;

// TODO: change name by filter
public class CharacterIsHungry implements JobCheck {

	@Override
	public Job create(JobManager jobManager, Character character) {
		if (character.getNeeds().isHungry()) {
			ItemFilter filter = new ItemFilter(true, true);
			filter.food = true;

			// Have item in inventory
			BaseItem item = character.find(filter);
			if (item != null) {
				return JobUseInventory.create(character, item);
			}

			// Take item from storage
			StorageItem storage = ServiceManager.getWorldMap().findStorageContains(filter, character.getX(), character.getY());
			if (storage != null) {
				return JobTake.create(character, storage, filter);
			}

			// Looking for food dispenser
			for (int x = 0; x < ServiceManager.getWorldMap().getWidth(); x++) {
				for (int y = 0; y < ServiceManager.getWorldMap().getHeight(); y++) {
					item = ServiceManager.getWorldMap().getItem(x, y);
					if (item != null && item.matchFilter(filter)) {
						return JobUse.create(item);
					}
				}
			}
		}
		return null;
	}
}
