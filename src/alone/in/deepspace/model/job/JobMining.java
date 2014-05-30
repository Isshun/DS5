package alone.in.deepspace.model.job;

import alone.in.deepspace.manager.JobManager;
import alone.in.deepspace.manager.ServiceManager;
import alone.in.deepspace.model.Character;
import alone.in.deepspace.model.WorldResource;

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
