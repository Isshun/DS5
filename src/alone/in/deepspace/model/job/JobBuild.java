package alone.in.deepspace.model.job;

import alone.in.deepspace.manager.JobManager;
import alone.in.deepspace.manager.ResourceManager;
import alone.in.deepspace.manager.ServiceManager;
import alone.in.deepspace.model.character.Character;
import alone.in.deepspace.model.item.ItemBase;
import alone.in.deepspace.model.item.StructureItem;
import alone.in.deepspace.ui.UserInterface;
import alone.in.deepspace.util.Log;

public class JobBuild extends Job {

	private JobBuild(int x, int y) {
		super(x, y);
	}

	public static Job create(ItemBase item) {
		Job job = new JobBuild(item.getX(), item.getY());
		job.setAction(JobManager.Action.BUILD);
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
		
		// TODO: item build on structure
		// TODO: OR item is structure
		
		return true;
	}

	@Override
	public boolean action(Character character) {
		// Wrong call
		if (_item == null) {
			Log.error("Character: actionBuild on null job or null job's item");
			JobManager.getInstance().abort(this, Abort.INVALID);
			return true;
		}

		// Item is no longer exists
		StructureItem currentStructure = ServiceManager.getWorldMap().getStructure(_posX, _posY);
		ItemBase currentItem = ServiceManager.getWorldMap().getItem(_posX, _posY);
		if (_item != currentStructure && _item != currentItem) {
			if (_item != currentStructure) {
				Log.warning("Character #" + character.getId() + ": actionBuild on invalide structure");
			} else if (_item != currentItem) {
				Log.warning("Character #" + character.getId() + ": actionBuild on invalide item");
			}
			JobManager.getInstance().abort(this, Abort.INVALID);
			return true;
		}


		Log.debug("Character #" + character.getId() + ": actionBuild");

		// Build
		ResourceManager.Message result = ResourceManager.getInstance().build(_item);

		if (result == ResourceManager.Message.NO_MATTER) {
			UserInterface.getInstance().displayMessage("not enough matter", _posX, _posY);
			Log.debug("Character #" + character.getId() + ": not enough matter");
			JobManager.getInstance().abort(this, Job.Abort.NO_BUILD_RESOURCES);
			return true;
		}

		if (result == ResourceManager.Message.BUILD_COMPLETE) {
			Log.debug("Character #" + character.getId() + ": build complete");
			JobManager.getInstance().complete(this);
			return true;
		}

		if (result == ResourceManager.Message.BUILD_PROGRESS) {
			Log.debug("Character #" + character.getId() + ": build progress");
		}
		return false;
	}

	@Override
	public String getLabel() {
		return "build " + _item.getLabel();
	}

	@Override
	public String getShortLabel() {
		return "build " + _item.getLabel();
	}
}
