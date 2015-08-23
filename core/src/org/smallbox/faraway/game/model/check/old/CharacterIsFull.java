//package org.smallbox.faraway.game.model.check.old;
//
//import org.smallbox.faraway.game.manager.character.JobManager;
//import org.smallbox.faraway.game.model.characters.base.CharacterModel;
//import org.smallbox.faraway.game.model.job.JobModel;
//import org.smallbox.faraway.game.model.job.JobStore;
//
//public class CharacterIsFull implements CharacterCheck {
//
//	@Override
//	public boolean onCreate(JobManager jobManager, CharacterModel characters) {
//		if (characters.isFull()) {
//			JobModel job = JobStore.onCreate(characters);
//			if (job != null) {
//				jobManager.addJob(job, characters);
//				return true;
//			}
//		}
//
//		return false;
//	}
//
//}
