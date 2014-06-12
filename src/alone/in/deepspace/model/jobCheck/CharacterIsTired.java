package alone.in.deepspace.model.jobCheck;

import alone.in.deepspace.Game;
import alone.in.deepspace.manager.JobManager;
import alone.in.deepspace.model.character.Character;
import alone.in.deepspace.model.item.ItemBase;
import alone.in.deepspace.model.item.ItemFilter;
import alone.in.deepspace.model.job.JobUse;

/**
 * Check if character is tired then send to bed
 * 
 */
public class CharacterIsTired implements JobCharacterCheck {

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
