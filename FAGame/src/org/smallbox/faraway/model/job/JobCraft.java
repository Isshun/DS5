package org.smallbox.faraway.model.job;

import org.smallbox.faraway.engine.util.Log;
import org.smallbox.faraway.manager.JobManager;
import org.smallbox.faraway.manager.ServiceManager;
import org.smallbox.faraway.model.character.CharacterModel;
import org.smallbox.faraway.model.item.ItemBase;
import org.smallbox.faraway.model.item.ItemInfo;

public class JobCraft extends BaseJob {

	@Override
	public CharacterModel.TalentType getTalentNeeded() {
		return CharacterModel.TalentType.CRAFT;
	}

	private JobCraft(ItemInfo.ItemInfoAction action, int x, int y) {
		super(action, x, y);
	}

	public static JobCraft create(ItemInfo.ItemInfoAction action, ItemBase item) {
        if (item == null) {
            throw new RuntimeException("Cannot add cook job (item is null)");
        }

        if (action == null) {
            throw new RuntimeException("Cannot add cook job (action is null)");
        }

        JobCraft job = new JobCraft(action, item.getX(), item.getY());
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
			JobManager.getInstance().abort(this, BaseJob.JobAbortReason.INVALID);
			return false;
		}

		// Item is no longer exists
		if (_item != ServiceManager.getWorldMap().getItem(_posX, _posY)) {
			Log.warning("Character #" + character.getId() + ": actionUse on invalide item");
			JobManager.getInstance().abort(this, BaseJob.JobAbortReason.INVALID);
			return true;
		}

		// Character is sleeping
		if (character.getNeeds().isSleeping()) {
			Log.debug("use: sleeping . use canceled");
			return false;
		}

		// Work continue
		if (_cost < _totalCost) {
            _cost = Math.min(_totalCost, _cost + character.work(CharacterModel.TalentType.CRAFT));
			Log.debug("Character #" + character.getId() + ": working");
			return false;
		}

        // Current item is complete but some remains
        if (--_count != 0) {
            _cost = 0;
            setCharacter(null);
            return false;
        }

        // Work is complete
        Log.debug("Character #" + character.getId() + ": work complete");
        JobManager.getInstance().complete(this);
        return true;
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
