//package org.smallbox.faraway.game.model.check.old;
//
//import org.smallbox.faraway.game.manager.JobManager;
//import org.smallbox.faraway.game.model.character.base.CharacterModel;
//import org.smallbox.faraway.game.model.job.JobModel;
//import org.smallbox.faraway.game.model.job.JobStore;
//
//// Character has item to store
//public class CharacterHasItemToStore implements CharacterCheck {
//
//	@Override
//	public boolean create(JobManager jobManager, CharacterModel character) {
//		if (character.getComponents().size() > 0) {
//			JobModel job = JobStore.create(character);
//			if (job != null) {
//				jobManager.addJob(job, character);
//				return true;
//			}
//		}
//
//		return false;
//	}
//
//}
