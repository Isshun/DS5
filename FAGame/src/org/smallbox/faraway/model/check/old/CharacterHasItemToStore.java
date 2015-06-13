//package org.smallbox.faraway.model.check.old;
//
//import org.smallbox.faraway.manager.JobManager;
//import org.smallbox.faraway.model.character.CharacterModel;
//import org.smallbox.faraway.model.job.JobModel;
//import org.smallbox.faraway.model.job.JobStore;
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
