package org.smallbox.faraway.game.model.job;

import org.smallbox.faraway.WorldHelper;
import org.smallbox.faraway.game.manager.JobManager;
import org.smallbox.faraway.game.model.ReceiptModel;
import org.smallbox.faraway.game.model.character.base.CharacterModel;
import org.smallbox.faraway.game.model.item.MapObjectModel;
import org.smallbox.faraway.util.Log;

import java.util.ArrayList;

public class JobBuild extends BaseBuildJobModel {

	private JobBuild(int x, int y) {
		super(null, x, y, "data/res/ic_build.png", "data/res/ic_action_build.png");
	}

    @Override
    protected void onStart(CharacterModel character) {
        int bestDistance = Integer.MAX_VALUE;
        for (ReceiptModel receipt: _receipts) {
            receipt.reset();
            if (bestDistance > receipt.getTotalDistance() && receipt.hasComponentsOnMap()) {
                bestDistance = receipt.getTotalDistance();
                _receipt = receipt;
            }
        }

        if (_receipt == null) {
            throw new RuntimeException("Try to start JobCraft but no receipt have enough component");
        }

        // Start receipt and get first component
        _receipt.start(this);
		if (_receipt.getCurrentOrder() != null) {
			moveToIngredient(character, _receipt.getCurrentOrder());
		} else {
			moveToMainItem();
		}
    }

    @Override
    public void onQuit(CharacterModel character) {
        if (_receipt != null) {
            _receipt.close();
            _receipt = null;
        }
    }

    @Override
    public CharacterModel.TalentType getTalentNeeded() {
        return CharacterModel.TalentType.BUILD;
    }

	public static BaseJobModel create(MapObjectModel item) {
        if (item == null) {
            throw new RuntimeException("Cannot add Craft job (item is null)");
        }

        JobBuild job = new JobBuild(item.getX(), item.getY());
        job._mainItem = item;
        job._mainItem.addJob(job);
        job._mainItem.setJobBuild(job);
        job._receipts = new ArrayList<>();
        job._receipts.add(ReceiptModel.createFromComponentInfo(item, item.getInfo().components));
		job.setCost(item.getInfo().cost);
		job.setStrategy(j -> {
            if (j.getCharacter().getType().needs.joy != null) {
                j.getCharacter().getNeeds().joy += j.getCharacter().getType().needs.joy.change.work;
            }
        });
        job.onCheck(null);

		return job;
	}

	@Override
	public boolean onCheck(CharacterModel character) {
        for (ReceiptModel receipt: _receipts) {
            if (receipt.hasComponentsOnMap()) {
                _message = "Waiting";
                return true;
            }
        }
        _message = "Missing components";
        return false;
	}

	@Override
	protected void onFinish() {
		Log.info("Character #" + _character.getId() + ": build close");
        _mainItem.removeJob(this);
        _mainItem.setJobBuild(null);
	}

	@Override
	public JobActionReturn onAction(CharacterModel character) {
		if (_character == null) {
			Log.error("Action on job with null characters");
		}

		// Wrong call
		if (_mainItem == null) {
			Log.error("Character: actionBuild on null job or null job's item");
			JobManager.getInstance().quitJob(this, JobAbortReason.INVALID);
			return JobActionReturn.ABORT;
		}

		// Item is no longer exists
		if (_mainItem != WorldHelper.getStructure(_posX, _posY) && _mainItem != WorldHelper.getItem(_posX, _posY)) {
            Log.warning("Character #" + character.getId() + ": actionBuild on invalid mapObject");
			JobManager.getInstance().quitJob(this, JobAbortReason.INVALID);
			return JobActionReturn.ABORT;
		}

		// Move to ingredient
		if (_status == Status.WAITING) {
			moveToIngredient(_character, _receipt.getCurrentOrder());
			return JobActionReturn.CONTINUE;
		}

		// Build
		CharacterModel.TalentEntry talent = character.getTalent(CharacterModel.TalentType.BUILD);
		_mainItem.addProgress(talent.work());
		if (!_mainItem.isComplete()) {
			Log.debug("Character #" + character.getId() + ": build progress");
			return JobActionReturn.CONTINUE;
		}

		return JobActionReturn.FINISH;
	}

    @Override
    public double getProgress() { return (double)_mainItem.getProgress() / _mainItem.getInfo().cost; }

	@Override
	public boolean canBeResume() {
		return false;
	}

	@Override
	public String getLabel() {
		return "build " + _mainItem.getLabel();
	}

	@Override
	public String getShortLabel() {
		return "build " + _mainItem.getLabel();
	}
}
