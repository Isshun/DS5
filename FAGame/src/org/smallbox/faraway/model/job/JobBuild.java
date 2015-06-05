package org.smallbox.faraway.model.job;

import org.smallbox.faraway.engine.util.Log;
import org.smallbox.faraway.manager.JobManager;
import org.smallbox.faraway.manager.ResourceManager;
import org.smallbox.faraway.manager.ServiceManager;
import org.smallbox.faraway.model.character.CharacterModel;
import org.smallbox.faraway.model.item.ItemBase;
import org.smallbox.faraway.model.item.StructureItem;
import org.smallbox.faraway.ui.UserInterface;

public class JobBuild extends BaseJob {

	private JobBuild(int x, int y) {
		super(null, x, y);
	}

	public static BaseJob create(ItemBase item) {
		BaseJob job = new JobBuild(item.getX(), item.getY());
		job.setItem(item);
		return job;
	}

	@Override
	public boolean check(CharacterModel character) {
		// Item is null
		if (_item == null) {
			_reason = JobAbortReason.INVALID;
			return false;
		}
		
		// TODO: item build on structure
		// TODO: OR item is structure
		
		return true;
	}

	@Override
	public boolean action(CharacterModel character) {
		// Wrong call
		if (_item == null) {
			Log.error("Character: actionBuild on null job or null job's item");
			JobManager.getInstance().quit(this, JobAbortReason.INVALID);
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
			JobManager.getInstance().quit(this, JobAbortReason.INVALID);
			return true;
		}


		Log.debug("Character #" + character.getId() + ": actionBuild");

		// Build
		ResourceManager.Message result = ResourceManager.getInstance().build(_item);

		if (result == ResourceManager.Message.NO_MATTER) {
			UserInterface.getInstance().displayMessage("not enough matter", _posX, _posY);
			Log.debug("Character #" + character.getId() + ": not enough matter");
			JobManager.getInstance().quit(this, BaseJob.JobAbortReason.NO_BUILD_RESOURCES);
			return true;
		}

		if (result == ResourceManager.Message.BUILD_COMPLETE) {
			Log.debug("Character #" + character.getId() + ": build close");
			JobManager.getInstance().close(this);
			return true;
		}

		if (result == ResourceManager.Message.BUILD_PROGRESS) {
			Log.debug("Character #" + character.getId() + ": build progress");
		}
		return false;
	}

	@Override
	public String getType() {
		return "build";
	}

	@Override
	public boolean canBeResume() {
		return false;
	}

	@Override
	public CharacterModel.TalentType getTalentNeeded() {
		return CharacterModel.TalentType.BUILD;
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
