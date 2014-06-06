package alone.in.deepspace.model.jobCheck;

import alone.in.deepspace.manager.ItemFilter;
import alone.in.deepspace.manager.JobManager;
import alone.in.deepspace.manager.ServiceManager;
import alone.in.deepspace.model.character.Character;
import alone.in.deepspace.model.item.ItemBase;
import alone.in.deepspace.model.item.StorageItem;
import alone.in.deepspace.model.job.JobTake;
import alone.in.deepspace.model.job.JobUse;
import alone.in.deepspace.model.job.JobUseInventory;

// TODO: change name by filter
public class CharacterIsHungry implements JobCharacterCheck {

	@Override
	public boolean create(JobManager jobManager, Character character) {
		if (character.getNeeds().isHungry()) {
			ItemFilter filter = new ItemFilter(true, true);
			filter.effectFood = true;

			// Have item in inventory
			ItemBase item = character.find(filter);
			if (item != null) {
				jobManager.addJob(JobUseInventory.create(character, item), character);
				return true;
			}

			// Take item from storage
			StorageItem storage = ServiceManager.getWorldMap().findStorageContains(filter, character.getX(), character.getY());
			if (storage != null) {
				jobManager.addJob(JobTake.create(character, storage, filter), character);
				return true;
			}

			// Looking for food dispenser
			for (int x = 0; x < ServiceManager.getWorldMap().getWidth(); x++) {
				for (int y = 0; y < ServiceManager.getWorldMap().getHeight(); y++) {
					item = ServiceManager.getWorldMap().getItem(x, y);
					if (item != null && item.matchFilter(filter)) {
						jobManager.addJob(JobUse.create(item), character);
						return true;
					}
				}
			}
		}
		return false;
	}
}
