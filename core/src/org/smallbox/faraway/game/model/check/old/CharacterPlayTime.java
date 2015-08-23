//package org.smallbox.faraway.game.model.check.old;
//
//import org.smallbox.faraway.util.Constant;
//import org.smallbox.faraway.game.manager.character.JobManager;
//import org.smallbox.faraway.game.manager.ServiceManager;
//import org.smallbox.faraway.game.model.characters.base.CharacterModel;
//import org.smallbox.faraway.game.model.item.UserItem;
//import org.smallbox.faraway.game.model.job.JobUse;
//
//// Play with random object
//public class CharacterPlayTime implements CharacterCheck {
//
//	@Override
//	public boolean onCreate(JobManager jobManager, CharacterModel characters) {
//		if ((int)(Math.random() * 100) <= Constant.CHANCE_TO_GET_MEETING_AREA_WHEN_JOBLESS) {
//			return false;
//		}
//
//		UserItem toy = Game.getWorldManager().getRandomToy(characters.getX(), characters.getY());
//		if (toy == null) {
//			return false;
//		}
//
//		jobManager.addJob(JobUse.onCreate(toy, characters), characters);
//		return true;
//	}
//}
