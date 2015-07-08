package org.smallbox.faraway.game.model.job;

import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.manager.JobManager;
import org.smallbox.faraway.game.model.character.base.CharacterModel;
import org.smallbox.faraway.game.model.item.MapObjectModel;
import org.smallbox.faraway.game.model.item.StructureModel;
import org.smallbox.faraway.util.Log;

public class JobBuild extends BaseJobModel {
	private MapObjectModel _buildItem;

	private JobBuild(int x, int y) {
		super(null, x, y, "data/res/ic_build.png", "data/res/ic_action_build.png");
	}

	public static BaseJobModel create(MapObjectModel item) {
		JobBuild job = new JobBuild(item.getX(), item.getY());
		job.setBuildItem(item);
		job.setCost(item.getInfo().cost);
		job.setStrategy(j -> {
			if (j.getCharacter().getType().needs.joy != null) {
				j.getCharacter().getNeeds().joy += j.getCharacter().getType().needs.joy.change.work;
			}
		});
		return job;
	}

	private void setBuildItem(MapObjectModel buildItem) {
		_buildItem = buildItem;
	}

	@Override
	public boolean onCheck(CharacterModel character) {
		// Item is null
		if (_buildItem == null) {
			_reason = JobAbortReason.INVALID;
			return false;
		}
		
		// TODO: item addBuildJob on structure
		// TODO: OR item is structure
		
		return true;
	}

	@Override
	protected void onFinish() {
		Log.info("Character #" + _character.getId() + ": build close");
	}

	@Override
	public JobActionReturn onAction(CharacterModel character) {
		// Wrong call
		if (_buildItem == null) {
			Log.error("Character: actionBuild on null job or null job's item");
			JobManager.getInstance().quitJob(this, JobAbortReason.INVALID);
			return JobActionReturn.ABORT;
		}

		// Item is no longer exists
		StructureModel currentStructure = Game.getWorldManager().getStructure(_posX, _posY);
		MapObjectModel currentItem = Game.getWorldManager().getItem(_posX, _posY);
		if (_buildItem != currentStructure && _buildItem != currentItem) {
			if (_buildItem != currentStructure) {
				Log.warning("Character #" + character.getId() + ": actionBuild on invalid structure");
			} else if (_item != currentItem) {
				Log.warning("Character #" + character.getId() + ": actionBuild on invalid item");
			}
			JobManager.getInstance().quitJob(this, JobAbortReason.INVALID);
			return JobActionReturn.ABORT;
		}

		// Build
        CharacterModel.TalentEntry talent = character.getTalent(CharacterModel.TalentType.BUILD);
		_buildItem.addProgress(talent.work());
		if (!_buildItem.isComplete()) {
			Log.debug("Character #" + character.getId() + ": build progress");
			return JobActionReturn.CONTINUE;
		}

		return JobActionReturn.FINISH;
	}

    @Override
    public double getProgress() { return (double)_buildItem.getProgress() / _buildItem.getInfo().cost; }

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
		return "build " + _buildItem.getLabel();
	}

	@Override
	public String getShortLabel() {
		return "build " + _buildItem.getLabel();
	}

	@Override
	protected void onStart(CharacterModel character) {
	}

	@Override
	public void onQuit(CharacterModel character) {
	}
}
