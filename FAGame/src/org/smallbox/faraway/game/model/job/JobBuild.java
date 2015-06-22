package org.smallbox.faraway.game.model.job;

import org.smallbox.faraway.engine.SpriteModel;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.util.Log;
import org.smallbox.faraway.game.manager.JobManager;
import org.smallbox.faraway.engine.SpriteManager;
import org.smallbox.faraway.game.model.character.base.CharacterModel;
import org.smallbox.faraway.game.model.item.MapObjectModel;
import org.smallbox.faraway.game.model.item.StructureModel;

public class JobBuild extends BaseJobModel {
	private static final SpriteModel ICON = SpriteManager.getInstance().getIcon("data/res/ic_build.png");

	private JobBuild(int x, int y) {
		super(null, x, y);
	}

	public static BaseJobModel create(MapObjectModel item) {
		BaseJobModel job = new JobBuild(item.getX(), item.getY());
		job.setItem(item);
		job.setCost(item.getInfo().cost);
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
		StructureModel currentStructure = Game.getWorldManager().getStructure(_posX, _posY);
		MapObjectModel currentItem = Game.getWorldManager().getItem(_posX, _posY);
		if (_item != currentStructure && _item != currentItem) {
			if (_item != currentStructure) {
				Log.warning("Character #" + character.getId() + ": actionBuild on invalid structure");
			} else if (_item != currentItem) {
				Log.warning("Character #" + character.getId() + ": actionBuild on invalid item");
			}
			JobManager.getInstance().quit(this, JobAbortReason.INVALID);
			return true;
		}

		// Build
        CharacterModel.TalentEntry talent = character.getTalent(CharacterModel.TalentType.BUILD);
        _item.addProgress(talent.work());
		if (!_item.isComplete()) {
			Log.debug("Character #" + character.getId() + ": build progress");
			return false;
		}

		// Build complete
		Log.info("Character #" + character.getId() + ": build close");
		JobManager.getInstance().close(this);
		return true;
	}

    @Override
    public double getProgress() { return (double)_item.getProgress() / _item.getInfo().cost; }

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

	@Override
	public SpriteModel getIcon() {
		return ICON;
	}

	@Override
	public void onQuit(CharacterModel character) {

	}
}
