//package org.smallbox.faraway.game.model.check.old;
//
//import org.smallbox.faraway.game.manager.character.JobManager;
//import org.smallbox.faraway.game.model.characters.base.CharacterModel;
//import org.smallbox.faraway.game.model.job.JobModel;
//import org.smallbox.faraway.game.model.job.JobStore;
//
//// Character has item to store
//public class CharacterHasItemToStore implements CharacterCheck {
//
//	@Override
//	public boolean onCreate(JobManager jobManager, CharacterModel characters) {
//		if (characters.getComponents().size() > 0) {
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
