//package org.smallbox.faraway.game.model.check.old;
//
//import org.smallbox.faraway.game.manager.JobManager;
//import org.smallbox.faraway.game.model.character.base.CharacterModel;
//import org.smallbox.faraway.game.model.job.JobModel;
//import org.smallbox.faraway.game.model.job.JobStore;
//
//public class CharacterIsFull implements CharacterCheck {
//
//	@Override
//	public boolean onCreate(JobManager jobManager, CharacterModel character) {
//		if (character.isFull()) {
//			JobModel job = JobStore.onCreate(character);
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
