package alone.in.deepspace.model.job;

import alone.in.deepspace.manager.JobManager;
import alone.in.deepspace.model.BaseItem;
import alone.in.deepspace.model.Character;

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

}
