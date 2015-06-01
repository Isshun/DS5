package org.smallbox.faraway.model.job;

import org.smallbox.faraway.engine.util.Log;
import org.smallbox.faraway.manager.JobManager;
import org.smallbox.faraway.manager.ResourceManager;
import org.smallbox.faraway.manager.ServiceManager;
import org.smallbox.faraway.model.ProfessionModel;
import org.smallbox.faraway.model.character.CharacterModel;
import org.smallbox.faraway.model.item.ItemInfo;
import org.smallbox.faraway.model.item.UserItem;
import org.smallbox.faraway.model.item.WorldResource;

public class JobMining extends JobModel {

	private JobMining(ItemInfo.ItemInfoAction actionInfo, int x, int y) {
		super(actionInfo, x, y);
	}

	public static JobModel create(WorldResource res) {
		// Resource is not minable
		if (res == null) {
			return null;
		}

		if (res.getInfo().actions != null) {
			for (ItemInfo.ItemInfoAction action: res.getInfo().actions) {
				if ("mine".equals(action.type)) {
					JobModel job = new JobMining(action, res.getX(), res.getY());
					job.setAction(JobManager.Action.MINING);
					job.setItem(res);
				}
			}
		}

		return null;
	}

	@Override
	public boolean check(CharacterModel character) {
		// Item is null
		if (_item == null) {
			_reason = JobAbortReason.INVALID;
			return false;
		}
		
		// Item is no longer exists
		if (_item != ServiceManager.getWorldMap().getRessource(_item.getX(), _item.getY())) {
			_reason = JobAbortReason.INVALID;
			return false;
		}
		
		// Resource is depleted
		if (_item.getMatterSupply() <= 0) {
			_reason = JobAbortReason.INVALID;
			return false;
		}

		// No space left in inventory
		if (character.hasInventorySpaceLeft() == false) {
			_reason = JobAbortReason.NO_LEFT_CARRY;
			return false;
		}

		return true;
	}

	@Override
	public boolean action(CharacterModel character) {
		// Wrong call
		if (_item == null) {
			Log.error("Character: actionMine on null job or null job's item");
			JobManager.getInstance().abort(this, JobAbortReason.INVALID);
			return true;
		}
		
		if (_item.isRessource() == false) {
			Log.error("Character: actionMine on non resource");
			JobManager.getInstance().abort(this, JobAbortReason.INVALID);
			return true;
		}

		WorldResource gatheredItem = (WorldResource)_item;

		if (!"mine".equals(_actionInfo)) {
			Log.error("Character: actionMine on non minable item");
			JobManager.getInstance().abort(this, JobAbortReason.INVALID);
			return true;
		}

		// Character is full: cancel current job
		if (character.getInventoryLeftSpace() <= 0) {
			JobManager.getInstance().abort(this, JobModel.JobAbortReason.NO_LEFT_CARRY);
			return true;
		}

		// TODO
		int value = ServiceManager.getWorldMap().gather((WorldResource)_item, character.getProfessionScore(ProfessionModel.Type.NONE));

		Log.debug("mine: " + value);

		ResourceManager.getInstance().addMatter(value);

		if (gatheredItem.getMatterSupply() == 0) {
			ServiceManager.getWorldMap().removeResource(gatheredItem);
			JobManager.getInstance().complete(this);
			return true;
		}

        for (ItemInfo itemInfo: _actionInfo.productsItem) {
            character.addInventory(new UserItem(itemInfo));
        }

		return false;
	}

	@Override
	public String getType() {
		return "mine";
	}

	@Override
	public String getLabel() {
		return "mine " + _item.getLabel();
	}

	@Override
	public String getShortLabel() {
		return "mine " + _item.getLabel();
	}
}
