package org.smallbox.faraway.model.job;

import org.smallbox.faraway.manager.JobManager;
import org.smallbox.faraway.manager.ResourceManager;
import org.smallbox.faraway.manager.ServiceManager;
import org.smallbox.faraway.model.character.CharacterModel;
import org.smallbox.faraway.model.item.ItemBase;

public class JobDestroy extends BaseJob {

	private JobDestroy(int x, int y) {
		super(null, x, y);
	}

	public static BaseJob create(ItemBase item) {
		if (item == null) {
			return null;
		}
		
		BaseJob job = new JobDestroy(item.getX(), item.getY());
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
	public String getType() {
		return "destroy";
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
		return "destroy " + _item.getLabel();
	}

	@Override
	public String getShortLabel() {
		return "destroy " + _item.getLabel();
	}
}
