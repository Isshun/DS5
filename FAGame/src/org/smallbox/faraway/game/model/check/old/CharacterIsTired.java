//package org.smallbox.faraway.game.model.check.old;
//
//import org.smallbox.faraway.game.Game;
//import org.smallbox.faraway.game.manager.JobManager;
//import org.smallbox.faraway.game.model.character.base.CharacterModel;
//import org.smallbox.faraway.game.model.item.ItemBase;
//import org.smallbox.faraway.game.model.item.ItemFilter;
//import org.smallbox.faraway.game.model.job.JobUse;
//
///**
// * Check if character is tired then send to bed
// *
// */
//public class CharacterIsTired implements CharacterCheck {
//
//	@Override
//	public boolean onCreate(JobManager jobManager, CharacterModel character) {
//		if (character.getNeeds().isExhausted()) {
//			ItemFilter filter = ItemFilter.createUsableFilter();
//			filter.effectEnergy = true;
//
//			// Character has quarters
//			if (character.getQuarter() != null) {
//				ItemBase item = character.getQuarter().find(filter);
//				if (item != null) {
//					jobManager.addJob(JobUse.onCreate(item, character), character);
//					return true;
//				}
//			}
//
//			// No quarters or no usable bed in quarters
//			ItemBase item = Game.getWorldFinder().getNearest(filter, character);
//			if (item != null) {
//				jobManager.addJob(JobUse.onCreate(item, character), character);
//				return true;
//			}
//		}
//
//		return false;
//	}
//
//}
