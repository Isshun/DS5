package org.smallbox.faraway.model.job;

import org.smallbox.faraway.manager.JobManager;
import org.smallbox.faraway.manager.ResourceManager;
import org.smallbox.faraway.manager.ServiceManager;
import org.smallbox.faraway.model.Profession;
import org.smallbox.faraway.model.character.Character;
import org.smallbox.faraway.model.item.UserItem;
import org.smallbox.faraway.model.item.WorldResource;
import org.smallbox.faraway.engine.util.Log;

public class JobMining extends Job {

	private JobMining(int x, int y) {
		super(x, y);
	}

	public static Job create(WorldResource res) {
		// Resource is not minable
		if (res == null || res.getInfo().onMine == null) {
			return null;
		}
		
		Job job = new JobMining(res.getX(), res.getY());
		job.setAction(JobManager.Action.MINING);
		job.setItem(res);
		
		return null;
	}

	@Override
	public boolean check(Character character) {
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
	public boolean action(Character character) {
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

		if (gatheredItem.getInfo().onMine == null) {
			Log.error("Character: actionMine on non minable item");
			JobManager.getInstance().abort(this, JobAbortReason.INVALID);
			return true;
		}

		// Character is full: cancel current job
		if (character.getInventoryLeftSpace() <= 0) {
			JobManager.getInstance().abort(this, Job.JobAbortReason.NO_LEFT_CARRY);
			return true;
		}

		// TODO
		int value = ServiceManager.getWorldMap().gather((WorldResource)_item, character.getProfessionScore(Profession.Type.NONE));

		Log.debug("mine: " + value);

		ResourceManager.getInstance().addMatter(value);

		if (gatheredItem.getMatterSupply() == 0) {
			ServiceManager.getWorldMap().removeResource(gatheredItem);
			JobManager.getInstance().complete(this);
			return true;
		}
		
		character.addInventory(new UserItem(gatheredItem.getInfo().onMine.itemProduce));
		
		return false;
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
