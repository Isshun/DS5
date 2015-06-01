package org.smallbox.faraway.model.job;

import org.smallbox.faraway.engine.util.Log;
import org.smallbox.faraway.manager.JobManager;
import org.smallbox.faraway.manager.ServiceManager;
import org.smallbox.faraway.model.character.CharacterModel;
import org.smallbox.faraway.model.item.ItemBase;
import org.smallbox.faraway.model.item.ItemInfo;

public class JobCook extends JobModel {

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
		job.setAction(JobManager.Action.WORK);
		job.setItem(item);
        job.setDurationLeft(action.duration);

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
			JobManager.getInstance().abort(this, JobAbortReason.INVALID);
			return false;
		}

		// Item is no longer exists
		if (_item != ServiceManager.getWorldMap().getItem(_posX, _posY)) {
			Log.warning("Character #" + character.getId() + ": actionUse on invalide item");
			JobManager.getInstance().abort(this, JobAbortReason.INVALID);
			return true;
		}

		// Character is sleeping
		if (character.getNeeds().isSleeping()) {
			Log.debug("use: sleeping . use canceled");
			return false;
		}

		// Work continue
		if (_quantity < _quantityTotal) {
            _quantity = Math.min(_quantityTotal, _quantity + character.work(CharacterModel.TalentType.COOK));
			Log.debug("Character #" + character.getId() + ": working");
			return false;
		}

        // Current item is complete but some remains
        if (--_count != 0) {
            _quantity = 0;
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
