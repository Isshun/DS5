package org.smallbox.faraway.model.check.character;

import org.smallbox.faraway.Game;
import org.smallbox.faraway.manager.JobManager;
import org.smallbox.faraway.model.character.CharacterModel;
import org.smallbox.faraway.model.check.old.CharacterCheck;
import org.smallbox.faraway.model.item.ConsumableItem;
import org.smallbox.faraway.model.item.ItemBase;
import org.smallbox.faraway.model.item.ItemFilter;
import org.smallbox.faraway.model.job.JobUse;
import org.smallbox.faraway.model.job.JobConsume;

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

			// Take item on floor
			ConsumableItem nearestItem = (ConsumableItem)Game.getWorldFinder().getNearest(filter, _character);
			if (nearestItem != null && nearestItem.hasFreeSlot()) {
				jobManager.addJob(JobConsume.create(_character, nearestItem), _character);
				return true;
			}

//			// Looking for food dispenser
//			ItemFilter factoryFilter = ItemFilter.createFactoryFilter();
//			factoryFilter.effectFood = true;
//			item = Game.getWorldFinder().getNearest(factoryFilter, _character);
//			if (item != null) {
//				jobManager.addJob(JobUse.create(item), _character);
//				return true;
//			}
		}
		return false;
    }
}
