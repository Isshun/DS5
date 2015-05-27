package org.smallbox.faraway.model.jobCheck;

import org.smallbox.faraway.Game;
import org.smallbox.faraway.manager.JobManager;
import org.smallbox.faraway.model.character.Character;
import org.smallbox.faraway.model.item.ItemBase;
import org.smallbox.faraway.model.item.ItemFilter;
import org.smallbox.faraway.model.job.JobUse;

/**
 * Check if character is tired then send to bed
 * 
 */
public class CharacterIsTired implements CharacterCheck {

	@Override
	public boolean create(JobManager jobManager, Character character) {
		if (character.getNeeds().isTired()) {
			ItemFilter filter = ItemFilter.createUsableFilter();
			filter.effectEnergy = true;
			
			// Character has quarters
			if (character.getQuarter() != null) {
				ItemBase item = character.getQuarter().find(filter);
				if (item != null) {
					jobManager.addJob(JobUse.create(item, character), character);
					return true;
				}
			}
			
			// No quarters or no usable bed in quarters
			ItemBase item = Game.getWorldFinder().getNearest(filter, character);
			if (item != null) {
				jobManager.addJob(JobUse.create(item, character), character);
				return true;
			}
		}
		
		return false;
	}

}
