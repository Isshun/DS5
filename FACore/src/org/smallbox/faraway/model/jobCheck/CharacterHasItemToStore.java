package org.smallbox.faraway.model.jobCheck;

import org.smallbox.faraway.manager.JobManager;
import org.smallbox.faraway.model.character.Character;
import org.smallbox.faraway.model.job.Job;
import org.smallbox.faraway.model.job.JobStore;

// Character has item to store
public class CharacterHasItemToStore implements CharacterCheck {

	@Override
	public boolean create(JobManager jobManager, Character character) {
		if (character.getInventory().size() > 0) {
			Job job = JobStore.create(character);
			if (job != null) {
				jobManager.addJob(job, character);
				return true;
			}
		}
		
		return false;
	}

}
