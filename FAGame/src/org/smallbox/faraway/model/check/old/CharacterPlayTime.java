//package org.smallbox.faraway.model.check.old;
//
//import org.smallbox.faraway.engine.util.Constant;
//import org.smallbox.faraway.manager.JobManager;
//import org.smallbox.faraway.manager.ServiceManager;
//import org.smallbox.faraway.model.character.base.CharacterModel;
//import org.smallbox.faraway.model.item.UserItem;
//import org.smallbox.faraway.model.job.JobUse;
//
//// Play with random object
//public class CharacterPlayTime implements CharacterCheck {
//
//	@Override
//	public boolean create(JobManager jobManager, CharacterModel character) {
//		if ((int)(Math.random() * 100) <= Constant.CHANCE_TO_GET_MEETING_AREA_WHEN_JOBLESS) {
//			return false;
//		}
//
//		UserItem toy = ServiceManager.getWorldMap().getRandomToy(character.getX(), character.getY());
//		if (toy == null) {
//			return false;
//		}
//
//		jobManager.addJob(JobUse.create(toy, character), character);
//		return true;
//	}
//}
