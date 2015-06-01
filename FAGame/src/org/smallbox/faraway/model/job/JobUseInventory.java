package org.smallbox.faraway.model.job;

import org.smallbox.faraway.engine.util.Constant;
import org.smallbox.faraway.engine.util.Log;
import org.smallbox.faraway.manager.JobManager;
import org.smallbox.faraway.manager.ResourceManager;
import org.smallbox.faraway.model.character.CharacterModel;
import org.smallbox.faraway.model.item.ItemBase;
import org.smallbox.faraway.model.item.ItemInfo;
import org.smallbox.faraway.model.item.UserItem;

public class JobUseInventory extends Job {

	private JobUseInventory(ItemInfo.ItemInfoAction action, int x, int y) {
		super(action, x, y);
	}

	public static Job create(CharacterModel character, ItemBase item) {
		if (!item.getInfo().isConsomable) {
			return null;
		}
		
		Job job = new JobUseInventory(item.getInfo().actions.get(0), character.getX(), character.getY());
		job.setAction(JobManager.Action.USE_INVENTORY);
		job.setItem(item);
		job.setCharacterRequire(character);
		job.setDurationLeft(item.getInfo().actions.get(0).duration);

		return job;
	}

	@Override
	public boolean check(CharacterModel character) {
		// Item is null
		if (_item == null) {
			_reason = JobAbortReason.INVALID;
			return false;
		}
		
		return true;
	}

	@Override
	public boolean action(CharacterModel character) {
		if (_item == null) {
			JobManager.getInstance().abort(this, Job.JobAbortReason.INVALID);
			Log.error("actionUseInventory: invalid job");
			return true;
		}
		
		if (character.getInventory().contains(_item) == false) {
			JobManager.getInstance().abort(this, Job.JobAbortReason.INVALID);
			Log.error("actionUseInventory: item is missing from inventory");
			return true;
		}
		
		// Update resource manager
		if (_nbUsed == 0) {
			ResourceManager.getInstance().add(_item.getInfo());
		}
		
		// TODO: immediate use
		for (int i = 0; i < _item.getInfo().actions.get(0).duration * Constant.DURATION_MULTIPLIER; i++) {
			_item.use(character, i);
		}

		character.removeInventory((UserItem)_item);
		JobManager.getInstance().complete(this);
		return true;
	}

	@Override
	public String getLabel() {
		return "use " + _item.getLabel();
	}

	@Override
	public String getShortLabel() {
		return "use" + _item.getLabel();
	}
}
