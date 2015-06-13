package org.smallbox.faraway.model.job;

import org.smallbox.faraway.Game;
import org.smallbox.faraway.engine.util.Log;
import org.smallbox.faraway.manager.JobManager;
import org.smallbox.faraway.model.character.CharacterModel;
import org.smallbox.faraway.model.item.ItemFilter;
import org.smallbox.faraway.model.item.ItemModel;

public class JobTake extends JobModel {

	private JobTake(int x, int y) {
		super(null, x, y);
	}

	public static JobModel create(ItemModel item) {
		Log.debug("create take job");

		JobTake job = new JobTake(item.getX(), item.getY());
		job.setItem(item);

		return job;
	}

	public static JobModel create(CharacterModel character, ItemFilter filter) {
		Log.debug("create take job");

		JobTake job = new JobTake(0, 0);
		job.setItemFilter(filter);
		job.setCharacterRequire(character);

		return job;
	}

	@Override
	public boolean check(CharacterModel character) {
		// No space left in inventory
		if (character.hasInventorySpaceLeft() == false) {
			_reason = JobAbortReason.NO_LEFT_CARRY;
			return false;
		}
//
//		// Item is not in storage nor on the floor
//		if (_storage == null && _item == null) {
//			_reason = JobAbortReason.INVALID;
//			return false;
//		}
//
//		// Check for item in storage room
//		if (_storage != null && checkInStorage()) {
//			return true;
//		}

		// Check for item on floor
		else if (_item != null && checkOnFloor()) {
			return true;
		}

		return false;
	}

	private boolean checkOnFloor() {
		// Item is not on target location
		if (_item != Game.getWorldManager().getItem(_posX, _posY)) {
			_reason = JobAbortReason.INVALID;
			return false;
		}
		return true;
	}

	private boolean checkInStorage() {
		// filter is null
		if (_filter == null) {
			_reason = JobAbortReason.INVALID;
			return false;
		}

//		// Storage not contains requested item
//		if (_storage.contains(_filter) == false) {
//			_reason = JobAbortReason.INVALID;
//			return false;
//		}

		return true;
	}

	@Override
	public boolean action(CharacterModel character) {
//		// Take item in storage room
//		if (_storage != null) {
//			if (_filter == null) {
//				JobManager.getInstance().quit(this, BaseJob.JobAbortReason.INVALID);
//				Log.error("actionTake: invalid job");
//				return true;
//			}
//
//			if (character.hasInventorySpaceLeft() && _storage.contains(_filter)) {
//				UserItem neededItem = _storage.take(_filter);
//				character.addComponent(neededItem);
//			}
//
//			JobManager.getInstance().close(this);
//			return true;
//		}

//		// Take item on floor
//		else if (_item != null) {
//			if (character.hasInventorySpaceLeft() && _item == Game.getWorldManager().getItem(_posX, _posY)) {
//				UserItem neededItem = Game.getWorldManager().takeItem(_posX, _posY);
//				character.addComponent(neededItem);
//			}
//
//			JobManager.getInstance().close(this);
//			return true;
//		}

		JobManager.getInstance().quit(this, JobModel.JobAbortReason.INVALID);
		Log.error("actionTake: invalid job");
		return true;
	}

	@Override
	public String getType() {
		return "take";
	}

	@Override
	public boolean canBeResume() {
		return false;
	}

	@Override
	public CharacterModel.TalentType getTalentNeeded() {
		return CharacterModel.TalentType.HAUL;
	}

	@Override
	public String getLabel() {
		if (_filter.itemMatched != null) {
			return "take " + _filter.itemMatched.label;
		}
		return "take";
	}

	@Override
	public String getShortLabel() {
		if (_item != null) {
			return "take " + _item.getLabel();
		}
		if (_filter != null && _filter.itemMatched != null) {
			return "take " + _filter.itemMatched.label;
		}
		return "take";
	}
}
