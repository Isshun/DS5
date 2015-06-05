package org.smallbox.faraway.model.job;

import org.smallbox.faraway.engine.util.Log;
import org.smallbox.faraway.manager.JobManager;
import org.smallbox.faraway.manager.ResourceManager;
import org.smallbox.faraway.model.character.CharacterModel;
import org.smallbox.faraway.model.item.ItemBase;
import org.smallbox.faraway.model.item.ItemInfo;
import org.smallbox.faraway.model.item.UserItem;

public class JobUseInventory extends BaseJob {

	@Override
	public boolean canBeResume() {
		return false;
	}

	@Override
	public CharacterModel.TalentType getTalentNeeded() {
		return null;
	}

	private JobUseInventory(ItemInfo.ItemInfoAction action, int x, int y) {
		super(action, x, y);
	}

	public static BaseJob create(CharacterModel character, ItemBase item) {
		if (!item.getInfo().isConsomable) {
			return null;
		}
		
		BaseJob job = new JobUseInventory(item.getInfo().actions.get(0), character.getX(), character.getY());
		job.setItem(item);
		job.setCharacterRequire(character);

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
			JobManager.getInstance().quit(this, BaseJob.JobAbortReason.INVALID);
			Log.error("actionUseInventory: invalid job");
			return true;
		}
		
		if (character.getInventory().contains(_item) == false) {
			JobManager.getInstance().quit(this, BaseJob.JobAbortReason.INVALID);
			Log.error("actionUseInventory: item is missing from inventory");
			return true;
		}
		
		// Update resource manager
		if (_nbUsed == 0) {
			ResourceManager.getInstance().add(_item.getInfo());
		}
		
		// TODO: immediate use
		for (int i = 0; i < _item.getInfo().actions.get(0).cost; i++) {
			_item.use(character, i);
		}

		character.removeInventory((UserItem)_item);
		JobManager.getInstance().close(this);
		return true;
	}

	@Override
	public String getType() {
		return "use_inventory";
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
