package org.smallbox.faraway.game.model.check.character;

import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.manager.WorldFinder;
import org.smallbox.faraway.game.model.character.base.CharacterModel;
import org.smallbox.faraway.game.model.check.old.CharacterCheck;
import org.smallbox.faraway.game.model.item.ConsumableModel;
import org.smallbox.faraway.game.model.item.ItemFilter;
import org.smallbox.faraway.game.model.job.JobConsume;
import org.smallbox.faraway.game.model.job.BaseJobModel;

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
	public BaseJobModel create(CharacterModel character) {
		ItemFilter filter = ItemFilter.createConsomableFilter();
		filter.effectFood = true;

		// Get consumable on inventory
		if (character.getInventory() != null && character.getInventory().matchFilter(filter)) {
			return JobConsume.create(character, character.getInventory());
		}

		// Get consumable on map
		ConsumableModel nearestItem = (ConsumableModel)((WorldFinder)Game.getInstance().getManager(WorldFinder.class)).getNearest(filter, character);
		if (nearestItem != null && nearestItem.hasFreeSlot()) {
			return JobConsume.create(character, nearestItem);
		}

		return null;
	}
}
