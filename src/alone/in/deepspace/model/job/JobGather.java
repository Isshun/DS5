package alone.in.deepspace.model.job;

import alone.in.deepspace.manager.JobManager.Action;
import alone.in.deepspace.manager.ServiceManager;
import alone.in.deepspace.model.Character;
import alone.in.deepspace.model.WorldResource;

public class JobGather extends Job {

	private JobGather(int x, int y) {
		super(x, y);
	}

	public static Job create(WorldResource ressource) {
		// Resource is not gatherable
		if (ressource == null || ressource.getInfo().onGather == null) {
			return null;
		}
		
		Job job = new JobGather(ressource.getX(), ressource.getY());
		job.setAction(Action.GATHER);
		job.setItem(ressource);
		
		return job;
	}

	@Override
	public Abort check(Character character) {
		// Item is null
		if (_item == null) {
			return Abort.INVALID;
		}
		
		// Item is no longer exists
		if (_item != ServiceManager.getWorldMap().getRessource(_item.getX(), _item.getY())) {
			return Abort.INVALID;
		}
		
		// Resource is depleted
		if (_item.getMatterSupply() <= 0) {
			return Abort.INVALID;
		}

		// No space left in inventory
		if (character.hasInventorySpaceLeft() == false) {
			return Abort.NO_LEFT_CARRY;
		}

		return null;
	}

}
