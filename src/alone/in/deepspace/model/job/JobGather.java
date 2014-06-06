package alone.in.deepspace.model.job;

import alone.in.deepspace.manager.JobManager;
import alone.in.deepspace.manager.JobManager.Action;
import alone.in.deepspace.manager.ResourceManager;
import alone.in.deepspace.manager.ServiceManager;
import alone.in.deepspace.model.Profession;
import alone.in.deepspace.model.character.Character;
import alone.in.deepspace.model.item.UserItem;
import alone.in.deepspace.model.item.WorldResource;
import alone.in.deepspace.util.Log;

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
	public boolean check(Character character) {
		// Item is null
		if (_resource == null) {
			_reason = Abort.INVALID;
			return false;
		}

		// Item is no longer exists
		if (_resource != ServiceManager.getWorldMap().getRessource(_resource.getX(), _resource.getY())) {
			_reason = Abort.INVALID;
			return false;
		}

//		// Resource is depleted
//		if (_resource.isDepleted()) {
//			_reason = Abort.INVALID;
//			return false;
//		}

		// No space left in inventory
		if (character.hasInventorySpaceLeft() == false) {
			_reason = Abort.NO_LEFT_CARRY;
			return false;
		}

		return true;
	}

	@Override
	public boolean action(Character character) {
		// Wrong call
		if (_resource == null) {
			Log.error("Character: actionGather on null job or null job's item");
			JobManager.getInstance().abort(this, Abort.INVALID);
			return true;
		}

		if (_resource.getInfo().onGather == null) {
			Log.error("Character: actionGather on non gatherable item");
			JobManager.getInstance().abort(this, Abort.INVALID);
			return true;
		}


		// Character is full: cancel current job
		if (character.getInventoryLeftSpace() <= 0) {
			JobManager.getInstance().abort(this, Abort.NO_LEFT_CARRY);
			return true;
		}

		// TODO
		int value = ServiceManager.getWorldMap().gather(_resource, Math.max(character.getProfessionScore(Profession.Type.NONE), character.getLeftSpace()));

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
}
