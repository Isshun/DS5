package org.smallbox.faraway.game.model.job;

import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.manager.RelationManager;
import org.smallbox.faraway.game.model.MovableModel.Direction;
import org.smallbox.faraway.game.model.character.base.CharacterModel;
import org.smallbox.faraway.game.model.item.ItemInfo;
import org.smallbox.faraway.game.model.item.ItemSlot;
import org.smallbox.faraway.game.model.item.MapObjectModel;
import org.smallbox.faraway.util.Log;

import java.util.List;

public class JobUse extends BaseJobModel {

    @Override
    public boolean canBeResume() {
        return false;
    }

	@Override
	public CharacterModel.TalentType getTalentNeeded() {
		return null;
	}

	@Override
	protected void onStart(CharacterModel character) {
	}

	@Override
	public void onQuit(CharacterModel character) {
	}

	private JobUse() {
		super();
	}

	public static JobUse create(MapObjectModel item) {
		if (item == null || !item.hasFreeSlot()) {
			return null;
		}

        ItemInfo.ItemInfoAction infoAction = item.getInfo().actions.get(0);

		JobUse job = new JobUse();
		ItemSlot slot = item.takeSlot(job);
		if (slot != null) {
			job.setSlot(slot);
			job.setPosition(slot.getX(), slot.getY());
		} else {
			job.setPosition(item.getX(), item.getY());
		}
		job.setActionInfo(infoAction);
		job.setItem(item);
        job.setCost(infoAction.cost);

		return job;
	}

	public static JobUse create(MapObjectModel item, CharacterModel character) {
		if (character == null) {
			return null;
		}

		JobUse job = create(item);
		if (job != null) {
			job.setCharacterRequire(character);
		}
		
		return job;
	}

	// TODO: make objects stats table instead switch
	@Override
	public JobActionReturn onAction(CharacterModel character) {
		// Wrong call
		if (_item == null || character == null) {
			Log.error("wrong call");
			return JobActionReturn.ABORT;
		}
		
		// Item not reached
		if (character.getX() != _posX || character.getY() != _posY) {
			return JobActionReturn.ABORT;
		}

		// Character is sleeping
		if (character.isSleeping() && !_item.isSleepingItem()) {
			Log.debug("use: sleeping . use canceled");
			return JobActionReturn.QUIT;
		}
		
		if (!check(character)) {
			return JobActionReturn.ABORT;
		}
		
		Log.debug("Character #" + character.getName() + ": actionUse");

		// Character using item
		if (_progress++ < _cost) {
			// Set running
			_status = JobStatus.RUNNING;
			
			// Item is use by 2 or more characters
			if (_item.getNbFreeSlots() + 1 < _item.getNbSlots()) {
				character.getNeeds().addRelation(1);
				List<ItemSlot> slots = _item.getSlots();
				for (ItemSlot slot: slots) {
					CharacterModel slotCharacter = slot.getJob() != null ? slot.getJob().getCharacter() : null;
					((RelationManager)Game.getInstance().getManager(RelationManager.class)).meet(character, slotCharacter);
				}
			}

			// Set characters direction
			if (_item.getX() > _posX) { character.setDirection(Direction.RIGHT); }
			if (_item.getX() < _posX) { character.setDirection(Direction.LEFT); }
			if (_item.getY() > _posY) { character.setDirection(Direction.TOP); }
			if (_item.getY() < _posY) { character.setDirection(Direction.BOTTOM); }

            // Use item
            _item.use(_character, (int) (_cost - _progress));

            return JobActionReturn.CONTINUE;
		}

        if (_item.isSleepingItem()) {
            _character.getNeeds().setSleeping(false);
        }

		return JobActionReturn.FINISH;
	}

	@Override
	public boolean onCheck(CharacterModel character) {
		// Item is null
		if (_item == null) {
			_reason = JobAbortReason.INVALID;
			return false;
		}
		
		// Item is no longer exists
		if (_item.isConsumable()) {
			if (_item != Game.getWorldManager().getConsumable(_item.getX(), _item.getY())) {
				_reason = JobAbortReason.INVALID;
				return false;
			}
		} else {
			if (_item != Game.getWorldManager().getItem(_item.getX(), _item.getY())) {
				_reason = JobAbortReason.INVALID;
				return false;
			}
		}

		if (_item.getQuantity() <= 0) {
			return false;
		}

//		// No space left in inventory
//		if (_item.isFactory() && characters.hasInventorySpaceLeft() == false) {
//			_reason = JobAbortReason.NO_LEFT_CARRY;
//			return false;
//		}
		
		return true;
	}

	@Override
	protected void onFinish() {

	}

	@Override
	public String getLabel() {
        if (_actionInfo != null && _actionInfo.label != null) {
            return _actionInfo.label;
        }
		return "use " + _item.getLabel();
	}

	@Override
	public String getShortLabel() {
		return "use " + _item.getLabel();
	}

}
