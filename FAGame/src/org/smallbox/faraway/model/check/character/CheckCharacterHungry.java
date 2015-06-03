package org.smallbox.faraway.model.check.character;

import org.smallbox.faraway.Game;
import org.smallbox.faraway.manager.JobManager;
import org.smallbox.faraway.model.character.CharacterModel;
import org.smallbox.faraway.model.check.old.CharacterCheck;
import org.smallbox.faraway.model.item.ItemBase;
import org.smallbox.faraway.model.item.ItemFilter;
import org.smallbox.faraway.model.item.UserItem;
import org.smallbox.faraway.model.job.JobUse;
import org.smallbox.faraway.model.job.JobUseInventory;

/**
 * Created by Alex on 01/06/2015.
 */
public class CheckCharacterHungry extends CharacterCheck {
    public CheckCharacterHungry(CharacterModel character) {
        super(character);
    }

    // TODO: change name by filter
    @Override
    public boolean create(JobManager jobManager) {
		if (_character.getNeeds().isHungry()) {
			ItemFilter filter = ItemFilter.createConsomableFilter();
			filter.effectFood = true;

			// Have item in inventory
			ItemBase item = _character.find(filter);
			if (item != null) {
				jobManager.addJob(JobUseInventory.create(_character, item), _character);
				return true;
			}

			// Take item from storage
			UserItem nearestItem = Game.getWorldFinder().getNearest(filter, _character);
			if (nearestItem != null) {
				nearestItem.setOwner(_character);
				jobManager.addJob(JobUse.create(nearestItem, _character), _character);
				return true;
			}

			// Looking for food dispenser
			ItemFilter factoryFilter = ItemFilter.createFactoryFilter();
			factoryFilter.effectFood = true;
			item = Game.getWorldFinder().getNearest(factoryFilter, _character);
			if (item != null) {
				jobManager.addJob(JobUse.create(item), _character);
				return true;
			}
		}
		return false;
    }
}
