//package org.smallbox.faraway.model.check.old;
//
//import org.smallbox.faraway.manager.JobManager;
//import org.smallbox.faraway.model.character.base.CharacterModel;
//import org.smallbox.faraway.model.job.JobModel;
//import org.smallbox.faraway.model.job.JobStore;
//
//public class CharacterIsFull implements CharacterCheck {
//
//	@Override
//	public boolean create(JobManager jobManager, CharacterModel character) {
//		if (character.isFull()) {
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
