package alone.in.deepspace.model.job;

import alone.in.deepspace.manager.JobManager;
import alone.in.deepspace.manager.ResourceManager;
import alone.in.deepspace.manager.ServiceManager;
import alone.in.deepspace.model.character.Character;
import alone.in.deepspace.model.item.ItemBase;

public class JobDestroy extends Job {

	private JobDestroy(int x, int y) {
		super(x, y);
	}

	public static Job create(ItemBase item) {
		if (item == null) {
			return null;
		}
		
		Job job = new JobDestroy(item.getX(), item.getY());
		job.setAction(JobManager.Action.DESTROY);
		job.setItem(item);
		return job;
	}

	@Override
	public boolean check(Character character) {
		// Item is null
		if (_item == null) {
			_reason = Abort.INVALID;
			return false;
		}
		
		// Item is no longer exists
		if (_item != ServiceManager.getWorldMap().getItem(_item.getX(), _item.getY()) && _item != ServiceManager.getWorldMap().getStructure(_item.getX(), _item.getY())) {
			_reason = Abort.INVALID;
			return false;
		}

		return true;
	}

	@Override
	public boolean action(Character character) {
		ResourceManager.getInstance().addMatter(1);
		ServiceManager.getWorldMap().removeItem(_item);
		JobManager.getInstance().complete(this);
		return true;
	}

}
