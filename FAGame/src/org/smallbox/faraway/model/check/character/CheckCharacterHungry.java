package org.smallbox.faraway.model.check.character;

import org.smallbox.faraway.Game;
import org.smallbox.faraway.model.character.CharacterModel;
import org.smallbox.faraway.model.check.old.CharacterCheck;
import org.smallbox.faraway.model.item.ConsumableModel;
import org.smallbox.faraway.model.item.ItemFilter;
import org.smallbox.faraway.model.job.JobConsume;
import org.smallbox.faraway.model.job.JobModel;

/**
 * Created by Alex on 01/06/2015.
 */
public class CheckCharacterHungry extends CharacterCheck {

	@Override
	public boolean check(CharacterModel character) {
		return character.getNeeds().isHungry();
	}

	// TODO: change name by filter
	@Override
	public JobModel create(CharacterModel character) {
		ItemFilter filter = ItemFilter.createConsomableFilter();
		filter.effectFood = true;

		// Get consumable on inventory
		if (character.getInventory() != null && character.getInventory().matchFilter(filter)) {
			return JobConsume.create(character, character.getInventory());
		}

		// Get consumable on map
		ConsumableModel nearestItem = (ConsumableModel)Game.getWorldFinder().getNearest(filter, character);
		if (nearestItem != null && nearestItem.hasFreeSlot()) {
			return JobConsume.create(character, nearestItem);
		}

		return null;
	}
}
