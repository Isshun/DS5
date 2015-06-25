package org.smallbox.faraway.game.model.job;

import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.model.character.base.CharacterModel;
import org.smallbox.faraway.game.model.item.MapObjectModel;

public class JobDump extends BaseJobModel {

	private JobDump(int x, int y) {
		super(null, x, y, "data/res/ic_dump.png", "data/res/ic_action_dump.png");
	}

	public static BaseJobModel create(MapObjectModel objectModel) {
		if (objectModel == null) {
			return null;
		}
		
		BaseJobModel job = new JobDump(objectModel.getX(), objectModel.getY());
		job.setItem(objectModel);
		job.setCost(objectModel.getInfo().cost);
		return job;
	}

	@Override
	public boolean onCheck(CharacterModel character) {
		// Item is null
		if (_item == null) {
			_reason = JobAbortReason.INVALID;
			return false;
		}
		
		// Item is no longer exists
		if (_item != Game.getWorldManager().getItem(_posX, _posY) && _item != Game.getWorldManager().getStructure(_posX, _posY)) {
			_reason = JobAbortReason.INVALID;
			return false;
		}

		return true;
	}

	@Override
	protected void onFinish() {
		Game.getWorldManager().remove(_item);
	}

	@Override
	public JobActionReturn onAction(CharacterModel character) {
        _item.addProgress(-character.getTalent(CharacterModel.TalentType.BUILD).work());
        _progress = _cost - _item.getProgress();
		return _item.isDump() ? JobActionReturn.FINISH : JobActionReturn.CONTINUE;
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
	protected void onStart(CharacterModel character) {
	}

	@Override
	public void onQuit(CharacterModel character) {
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
