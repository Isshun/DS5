package org.smallbox.faraway.game.model.job;

import org.smallbox.faraway.WorldHelper;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.model.character.base.CharacterModel;
import org.smallbox.faraway.game.model.item.MapObjectModel;

public class JobDump extends BaseJobModel {
	private MapObjectModel 	_dumpObject;

	private JobDump(int x, int y) {
		super(null, x, y, "data/res/ic_dump.png", "data/res/ic_action_dump.png");
	}

	public static BaseJobModel create(MapObjectModel objectModel) {
		if (objectModel == null) {
			return null;
		}
		
		JobDump job = new JobDump(objectModel.getX(), objectModel.getY());
		job.setDumpObject(objectModel);
		job.setCost(objectModel.getInfo().cost);
		job.setStrategy(j -> {
			if (j.getCharacter().getType().needs.joy != null) {
				j.getCharacter().getNeeds().joy += j.getCharacter().getType().needs.joy.change.work;
			}
		});
		return job;
	}

	public void setDumpObject(MapObjectModel dumpObject) {
		_dumpObject = dumpObject;
	}

	@Override
	public boolean onCheck(CharacterModel character) {
		// Item is null
		if (_dumpObject == null) {
			_reason = JobAbortReason.INVALID;
			return false;
		}
		
		// Item is no longer exists
		if (_dumpObject != WorldHelper.getItem(_posX, _posY) && _dumpObject != WorldHelper.getStructure(_posX, _posY)) {
			_reason = JobAbortReason.INVALID;
			return false;
		}

		return true;
	}

	@Override
	protected void onFinish() {
		Game.getWorldManager().remove(_dumpObject);
	}

	@Override
	public JobActionReturn onAction(CharacterModel character) {
		_dumpObject.addProgress(-character.getTalent(CharacterModel.TalentType.BUILD).work());
        _progress = _cost - _dumpObject.getProgress();
		return _dumpObject.isDump() ? JobActionReturn.FINISH : JobActionReturn.CONTINUE;
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
		return "Dump " + _dumpObject.getLabel();
	}

	@Override
	public String getShortLabel() {
		return "Dump " + _dumpObject.getLabel();
	}

}
