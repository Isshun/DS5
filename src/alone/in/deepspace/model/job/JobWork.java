package alone.in.deepspace.model.job;

import alone.in.deepspace.manager.JobManager;
import alone.in.deepspace.manager.ServiceManager;
import alone.in.deepspace.model.BaseItem;
import alone.in.deepspace.model.Character;
import alone.in.deepspace.model.StorageItem;

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
	public Abort check(Character character) {
		return null;
	}
}
