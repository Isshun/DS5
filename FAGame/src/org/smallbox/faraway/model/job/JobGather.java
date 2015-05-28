package org.smallbox.faraway.model.job;

import org.smallbox.faraway.engine.util.Log;
import org.smallbox.faraway.manager.JobManager;
import org.smallbox.faraway.manager.JobManager.Action;
import org.smallbox.faraway.manager.ResourceManager;
import org.smallbox.faraway.manager.ServiceManager;
import org.smallbox.faraway.model.ProfessionModel;
import org.smallbox.faraway.model.character.CharacterModel;
import org.smallbox.faraway.model.item.UserItem;
import org.smallbox.faraway.model.item.WorldResource;

public class JobGather extends Job {

	private WorldResource	_resource;

	private JobGather(int x, int y) {
		super(x, y);
	}

	public static Job create(WorldResource resource) {
		// Resource is not gatherable
		if (resource == null || resource.getInfo().onGather == null) {
			return null;
		}

		JobGather job = new JobGather(resource.getX(), resource.getY());
		job.setAction(Action.GATHER);
		job.setItem(resource);
		job._resource = resource;
		job._resource.setJob(job);

		return job;
	}

	@Override
	public boolean check(CharacterModel character) {
		// Item is null
		if (_resource == null) {
			_reason = JobAbortReason.INVALID;
			return false;
		}

		// Item is no longer exists
		if (_resource != ServiceManager.getWorldMap().getRessource(_resource.getX(), _resource.getY())) {
			_reason = JobAbortReason.INVALID;
			return false;
		}

//		// Resource is depleted
//		if (_resource.isDepleted()) {
//			_reason = Abort.INVALID;
//			return false;
//		}

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
		if (_resource == null) {
			Log.error("Character: actionGather on null job or null job's item");
			JobManager.getInstance().abort(this, JobAbortReason.INVALID);
			return true;
		}

		if (_resource.getInfo().onGather == null) {
			Log.error("Character: actionGather on non gatherable item");
			JobManager.getInstance().abort(this, JobAbortReason.INVALID);
			return true;
		}


		// Character is full: cancel current job
		if (character.getInventoryLeftSpace() <= 0) {
			JobManager.getInstance().abort(this, JobAbortReason.NO_LEFT_CARRY);
			return true;
		}

		// TODO
		int value = ServiceManager.getWorldMap().gather(_resource, Math.max(character.getProfessionScore(ProfessionModel.Type.NONE), character.getLeftSpace()));

		Log.debug("gather: " + value);

		ResourceManager.getInstance().addMatter(value);

		for (int i = 0; i < value; i++) {
			character.addInventory(new UserItem(_resource.getInfo().onGather.itemProduce));
		}
		
		if (_resource.isDepleted()) {
			_resource.setJob(null);
			JobManager.getInstance().complete(this);
			ServiceManager.getWorldMap().removeResource(_resource);
			return true;
		}

		return false;
	}

	@Override
	public String getLabel() {
		return "gather " + _item.getLabel();
	}

	@Override
	public String getShortLabel() {
		return "gather" + _item.getLabel();
	}
}
