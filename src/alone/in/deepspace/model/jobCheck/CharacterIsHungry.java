package alone.in.deepspace.model.jobCheck;

import alone.in.deepspace.Game;
import alone.in.deepspace.manager.JobManager;
import alone.in.deepspace.manager.ServiceManager;
import alone.in.deepspace.model.character.Character;
import alone.in.deepspace.model.item.ItemBase;
import alone.in.deepspace.model.item.ItemFilter;
import alone.in.deepspace.model.job.JobTake;
import alone.in.deepspace.model.job.JobUse;
import alone.in.deepspace.model.job.JobUseInventory;
import alone.in.deepspace.model.room.StorageRoom;

// TODO: change name by filter
public class CharacterIsHungry implements JobCharacterCheck {

	@Override
	public boolean create(JobManager jobManager, Character character) {
		if (character.getNeeds().isHungry()) {
			ItemFilter consomableItemFilter = ItemFilter.createConsomableFilter();
			consomableItemFilter.effectFood = true;

			// Have item in inventory
			ItemBase item = character.find(consomableItemFilter);
			if (item != null) {
				jobManager.addJob(JobUseInventory.create(character, item), character);
				return true;
			}

			// Take item from storage
			StorageRoom storage = Game.getRoomManager().findStorageContains(consomableItemFilter, character.getX(), character.getY());
			if (storage != null) {
				jobManager.addJob(JobTake.create(character, storage, consomableItemFilter), character);
				return true;
			}

			// Looking for food dispenser
			ItemFilter factoryFilter = ItemFilter.createFactoryFilter();
			factoryFilter.effectFood = true;
			item = ServiceManager.getWorldMap().getNearest(factoryFilter, character);
			if (item != null) {
				jobManager.addJob(JobUse.create(item), character);
				return true;
			}
		}
		return false;
	}
}
