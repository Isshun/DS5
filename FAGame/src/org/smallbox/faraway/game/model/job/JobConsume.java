package org.smallbox.faraway.game.model.job;

import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.model.MovableModel.Direction;
import org.smallbox.faraway.game.model.character.base.CharacterModel;
import org.smallbox.faraway.game.model.item.ConsumableModel;
import org.smallbox.faraway.util.Log;

public class JobConsume extends BaseJobModel {

	private ConsumableModel _consumable;

	public static JobConsume create(CharacterModel character, ConsumableModel consumable) {
		if (character == null) {
			Log.error("Create ConsumeJob with null characters");
			return null;
		}

		if (consumable == null) {
			Log.error("Create ConsumeJob with null item");
			return null;
		}

		JobConsume job = new JobConsume();
		job.setCharacterRequire(character);
		if (character.getInventory() == consumable) {
			job.setPosition(character.getX(), character.getY());
		} else {
			job.setPosition(consumable.getX(), consumable.getY());
		}
		job.setActionInfo(consumable.getInfo().actions.get(0));
		job.setConsumable(consumable);

		return job;
	}

	private void setConsumable(ConsumableModel consumable) {
		_consumable = consumable;
	}

	@Override
	protected void onStart(CharacterModel character) {
	}

	@Override
	public void onQuit(CharacterModel character) {
	}

	@Override
	public boolean onCheck(CharacterModel character) {
		// Item is null
		if (_consumable == null || !_consumable.isConsumable() || _consumable.getQuantity() <= 0) {
			Log.error("JobConsume: item cannot be null, non consumable or empty");
			_reason = JobAbortReason.INVALID;
			return false;
		}

		// Item is no longer exists
		if (_consumable != _character.getInventory() && _consumable != Game.getWorldManager().getConsumable(_consumable.getX(), _consumable.getY())) {
			_reason = JobAbortReason.INVALID;
			return false;
		}

		return true;
	}

	// TODO: make objects stats table instead switch
	@Override
	public JobActionReturn onAction(CharacterModel character) {
		// Wrong call
		if (_consumable == null || character == null) {
			Log.error("wrong call");
			return JobActionReturn.ABORT;
		}

		if (!check(character)) {
			return JobActionReturn.ABORT;
		}

		Log.debug("Character #" + character.getInfo().getName() + ": actionUse (" + _progress + ")");

		// Character using item
		if (_progress++ < _cost) {
			// Set characters direction
			if (_consumable.getX() > _posX) { character.setDirection(Direction.RIGHT); }
			if (_consumable.getX() < _posX) { character.setDirection(Direction.LEFT); }
			if (_consumable.getY() > _posY) { character.setDirection(Direction.TOP); }
			if (_consumable.getY() < _posY) { character.setDirection(Direction.BOTTOM); }

			// Use item
			_consumable.use(_character, (int) (_cost - _progress));

			return JobActionReturn.CONTINUE;
		}

		return JobActionReturn.FINISH;
	}

	@Override
	protected void onFinish() {
		if (_consumable.getInfo().isConsumable) {
			_consumable.addQuantity(-1);
			if (_consumable.getQuantity() <= 0) {
				Game.getWorldManager().removeConsumable(_consumable);
			}
		}
	}

	@Override
	public String getLabel() {
		if (_actionInfo != null && _actionInfo.label != null) {
			return _actionInfo.label;
		}
		return "use " + _consumable.getLabel();
	}

	@Override
	public String getShortLabel() {
		return "consume " + _consumable.getLabel();
	}

	@Override
	public boolean canBeResume() {
		return false;
	}

	@Override
	public CharacterModel.TalentType getTalentNeeded() {
		return null;
	}
}
