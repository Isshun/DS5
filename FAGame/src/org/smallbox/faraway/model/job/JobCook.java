package org.smallbox.faraway.model.job;

import org.smallbox.faraway.engine.util.Log;
import org.smallbox.faraway.manager.JobManager;
import org.smallbox.faraway.manager.ServiceManager;
import org.smallbox.faraway.model.character.CharacterModel;
import org.smallbox.faraway.model.item.ItemBase;
import org.smallbox.faraway.model.item.ItemInfo;

public class JobCook extends BaseJob {

	@Override
	public CharacterModel.TalentType getTalentNeeded() {
		return CharacterModel.TalentType.COOK;
	}

	private JobCook(ItemInfo.ItemInfoAction action, int x, int y) {
		super(action, x, y);
	}

	public static JobCook create(ItemInfo.ItemInfoAction action, ItemBase item) {
		if (item == null) {
			throw new RuntimeException("Cannot add cook job (item is null)");
		}

		if (action == null) {
			throw new RuntimeException("Cannot add cook job (action is null)");
		}

		JobCook job = new JobCook(action, item.getX(), item.getY());
		job.setItem(item);

		item.addJob(job);

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
			JobManager.getInstance().quit(this, JobAbortReason.INVALID);
			return false;
		}

		// Item is no longer exists
		if (_item != ServiceManager.getWorldMap().getItem(_posX, _posY)) {
			Log.warning("Character #" + character.getId() + ": actionUse on invalide item");
			JobManager.getInstance().quit(this, JobAbortReason.INVALID);
			return true;
		}

		// Work continue
		if (_cost < _totalCost) {
            _cost = Math.min(_totalCost, _cost + character.work(CharacterModel.TalentType.COOK));
			Log.debug("Character #" + character.getId() + ": working");
			return false;
		}

        // Current item is close but some remains
		_cost = 0;
		for (ItemInfo itemInfo: _actionInfo.productsItem) {
			ServiceManager.getWorldMap().putItem(itemInfo, _posX, _posY, 0, 100);
//                _character.addInventory(new UserItem(itemInfo));
		}

		JobManager.getInstance().quit(this);

        // Work is complete
		if (_count++ >= _totalCount) {
			Log.debug("Character #" + character.getId() + ": work close");
			JobManager.getInstance().close(this);
			return true;
		}

		return false;
	}

    @Override
    public String getType() {
        return "craft";
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
