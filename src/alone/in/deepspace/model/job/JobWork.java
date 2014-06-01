package alone.in.deepspace.model.job;

import alone.in.deepspace.manager.JobManager;
import alone.in.deepspace.manager.ServiceManager;
import alone.in.deepspace.model.BaseItem;
import alone.in.deepspace.model.character.Character;
import alone.in.deepspace.util.Log;

public class JobWork extends Job {

	private JobWork(int x, int y) {
		super(x, y);
	}

	public static Job create(BaseItem item) {
		if (item == null) {
			return null;
		}
		
		Job job = new JobWork(item.getX(), item.getY());
		job.setAction(JobManager.Action.WORK);
		job.setItem(item);
		return job;
	}

	@Override
	public boolean check(Character character) {
		return true;
	}

	@Override
	public boolean action(Character character) {
		// Wrong call
		if (_item == null) {
			Log.error("Character: actionUse on null job or null job's item");
			JobManager.getInstance().abort(this, Job.Abort.INVALID);
			return false;
		}

		// Item is no longer exists
		if (_item != ServiceManager.getWorldMap().getItem(_posX, _posY)) {
			Log.warning("Character #" + character.getId() + ": actionUse on invalide item");
			JobManager.getInstance().abort(this, Job.Abort.INVALID);
			return true;
		}

		// Character is sleeping
		if (character.getNeeds().isSleeping()) {
			Log.debug("use: sleeping . use canceled");
			return false;
		}

		// Work continue
		if (character.getNeeds().getWorkRemain() > 0) {
			character.getNeeds().setWorkRemain(character.getNeeds().getWorkRemain() - 1);
			Log.debug("Character #" + character.getId() + ": working");
			return false;
		}
		
		// Work is complete
		Log.debug("Character #" + character.getId() + ": work complete");
		JobManager.getInstance().complete(this);
		return true;
	}
}