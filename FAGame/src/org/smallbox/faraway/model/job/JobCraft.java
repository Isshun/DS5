package org.smallbox.faraway.model.job;

import org.smallbox.faraway.engine.util.Log;
import org.smallbox.faraway.manager.JobManager;
import org.smallbox.faraway.manager.ServiceManager;
import org.smallbox.faraway.model.character.CharacterModel;
import org.smallbox.faraway.model.item.ItemBase;
import org.smallbox.faraway.model.item.ItemInfo;

public class JobCraft extends Job {

	private JobCraft(ItemInfo.ItemInfoAction action, int x, int y) {
		super(action, x, y);
	}

	public static Job create(ItemInfo.ItemInfoAction action, ItemBase item) {
		if (item == null) {
			return null;
		}
		
		Job job = new JobCraft(action, item.getX(), item.getY());
		job.setAction(JobManager.Action.WORK);
		job.setItem(item);
		return job;
	}

	@Override
	public boolean check(CharacterModel character) {
		return true;
	}

	@Override
	public boolean action(CharacterModel character) {
		// Wrong call
		if (_item == null) {
			Log.error("Character: actionUse on null job or null job's item");
			JobManager.getInstance().abort(this, Job.JobAbortReason.INVALID);
			return false;
		}

		// Item is no longer exists
		if (_item != ServiceManager.getWorldMap().getItem(_posX, _posY)) {
			Log.warning("Character #" + character.getId() + ": actionUse on invalide item");
			JobManager.getInstance().abort(this, Job.JobAbortReason.INVALID);
			return true;
		}

		// Character is sleeping
		if (character.getNeeds().isSleeping()) {
			Log.debug("use: sleeping . use canceled");
			return false;
		}

		// Work continue
		if (character.getNeeds().getWorkRemain() > 0) {
			character.getNeeds().setWorkRemain(character.getNeeds().getWorkRemain() - 1);
			Log.debug("Character #" + character.getId() + ": working");
			return false;
		}
		
		// Work is complete
		Log.debug("Character #" + character.getId() + ": work complete");
		JobManager.getInstance().complete(this);
		return true;
	}

	@Override
	public String getLabel() {
		return _actionInfo.label;
	}

	@Override
	public String getShortLabel() {
		return "work";
	}

}
