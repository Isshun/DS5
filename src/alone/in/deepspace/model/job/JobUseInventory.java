package alone.in.deepspace.model.job;

import alone.in.deepspace.manager.JobManager;
import alone.in.deepspace.manager.ResourceManager;
import alone.in.deepspace.manager.JobManager.Action;
import alone.in.deepspace.model.BaseItem;
import alone.in.deepspace.model.Character;
import alone.in.deepspace.util.Constant;
import alone.in.deepspace.util.Log;

public class JobUseInventory extends Job {

	private JobUseInventory(int x, int y) {
		super(x, y);
	}

	public static Job create(Character character, BaseItem item) {
		if (!item.getInfo().isConsomable) {
			return null;
		}
		
		Job job = new JobUseInventory(character.getX(), character.getY());
		job.setAction(JobManager.Action.USE_INVENTORY);
		job.setItem(item);
		job.setCharacterRequire(character);
		job.setDurationLeft(item.getInfo().onAction.duration);

		return job;
	}

	@Override
	public Abort check(Character character) {
		// Item is null
		if (_item == null) {
			return Abort.INVALID;
		}
		
		return null;
	}

	@Override
	public boolean action(Character character) {
		if (_item == null) {
			JobManager.getInstance().abort(this, Job.Abort.INVALID);
			Log.error("actionUseInventory: invalid job");
			return true;
		}
		
		if (character.getInventory().contains(_item) == false) {
			JobManager.getInstance().abort(this, Job.Abort.INVALID);
			Log.error("actionUseInventory: item is missing from inventory");
			return true;
		}
		
		// Update resource manager
		if (_nbUsed == 0) {
			if (_item.getInfo().isFood) {
				ResourceManager.getInstance().addFood(-1);
			}
		}
		
		// TODO: immediate use
		for (int i = 0; i < _item.getInfo().onAction.duration * Constant.DURATION_MULTIPLIER; i++) {
			_item.use(character, i);
		}

		character.removeInventory(_item);
		JobManager.getInstance().complete(this);
		return true;
	}

}
