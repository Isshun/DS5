package org.smallbox.faraway.model.job;

import org.smallbox.faraway.manager.JobManager;
import org.smallbox.faraway.manager.ServiceManager;
import org.smallbox.faraway.model.character.base.CharacterModel;
import org.smallbox.faraway.model.item.MapObjectModel;

public class JobDump extends JobModel {

	private JobDump(int x, int y) {
		super(null, x, y);
	}

	public static JobModel create(MapObjectModel objectModel) {
		if (objectModel == null) {
			return null;
		}
		
		JobModel job = new JobDump(objectModel.getX(), objectModel.getY());
		job.setItem(objectModel);
		job.setCost(objectModel.getInfo().cost);
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
		if (_item != ServiceManager.getWorldMap().getItem(_posX, _posY) && _item != ServiceManager.getWorldMap().getStructure(_posX, _posY)) {
			_reason = JobAbortReason.INVALID;
			return false;
		}

		return true;
	}

	@Override
	public boolean action(CharacterModel character) {
        _item.addProgress(-character.getTalent(CharacterModel.TalentType.BUILD).work());
        _progress = _cost - _item.getProgress();
		if (!_item.isDump()) {
			return false;
		}

		ServiceManager.getWorldMap().destroy(_item);
		JobManager.getInstance().close(this);
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
		return "Dump " + _item.getLabel();
	}

	@Override
	public String getShortLabel() {
		return "Dump " + _item.getLabel();
	}

}
