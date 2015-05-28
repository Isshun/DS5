package org.smallbox.faraway.model.job;

import org.smallbox.faraway.manager.JobManager;
import org.smallbox.faraway.manager.ResourceManager;
import org.smallbox.faraway.manager.ServiceManager;
import org.smallbox.faraway.model.character.CharacterModel;
import org.smallbox.faraway.model.item.ItemBase;

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
	public boolean check(CharacterModel character) {
		// Item is null
		if (_item == null) {
			_reason = JobAbortReason.INVALID;
			return false;
		}
		
		// Item is no longer exists
		if (_item != ServiceManager.getWorldMap().getItem(_item.getX(), _item.getY()) && _item != ServiceManager.getWorldMap().getStructure(_item.getX(), _item.getY())) {
			_reason = JobAbortReason.INVALID;
			return false;
		}

		return true;
	}

	@Override
	public boolean action(CharacterModel character) {
		ResourceManager.getInstance().addMatter(1);
		ServiceManager.getWorldMap().destroy(_item);
		JobManager.getInstance().complete(this);
		return true;
	}

	@Override
	public String getLabel() {
		return "destroy " + _item.getLabel();
	}

	@Override
	public String getShortLabel() {
		return "destroy " + _item.getLabel();
	}
}
