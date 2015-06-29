package org.smallbox.faraway.game.model.job;

import org.smallbox.faraway.engine.SpriteManager;
import org.smallbox.faraway.engine.SpriteModel;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.model.character.base.CharacterModel;
import org.smallbox.faraway.game.model.item.ItemInfo;
import org.smallbox.faraway.game.model.item.ResourceModel;
import org.smallbox.faraway.util.Log;

public class JobMining extends BaseJobModel {
	private ResourceModel _resource;

	private JobMining(ItemInfo.ItemInfoAction actionInfo, int x, int y) {
		super(actionInfo, x, y, "data/res/ic_mining.png", "data/res/ic_action_mining.png");
	}

	public static BaseJobModel create(ResourceModel res) {
		// Resource is not minable
		if (res == null) {
			return null;
		}

		if (res.getInfo().actions != null) {
			for (ItemInfo.ItemInfoAction action: res.getInfo().actions) {
				if ("mine".equals(action.type)) {
					JobMining job = new JobMining(action, res.getX(), res.getY());
					job.setItem(res);
					job._resource = res;
					return job;
				}
			}
		}

		return null;
	}

	@Override
	public boolean onCheck(CharacterModel character) {
		// Item is null
		if (_item == null) {
			_reason = JobAbortReason.INVALID;
			return false;
		}
		
		// Item is no longer exists
		if (_item != Game.getWorldManager().getResource(_item.getX(), _item.getY())) {
			_reason = JobAbortReason.INVALID;
			return false;
		}
		
//		// Resource is depleted
//		if (_item.getMatterSupply() <= 0) {
//			_reason = JobAbortReason.INVALID;
//			return false;
//		}

//		// No space left in inventory
//		if (!character.hasInventorySpaceLeft()) {
//			_reason = JobAbortReason.NO_LEFT_CARRY;
//			return false;
//		}

		return true;
	}

	@Override
	protected void onFinish() {
		Log.info("Mine complete");
		Game.getWorldManager().removeResource(_resource);
		_actionInfo.products.stream().filter(productInfo -> productInfo.dropRate > Math.random()).forEach(productInfo -> {
			Game.getWorldManager().putObject(productInfo.itemInfo, _resource.getX(), _resource.getY(), 0, productInfo.quantity);
		});
	}

	@Override
	public JobActionReturn onAction(CharacterModel character) {
		// Wrong call
		if (_item == null) {
			Log.error("Character: actionMine on null job or null job's item");
			return JobActionReturn.ABORT;
		}
		
		if (!_item.isResource()) {
			Log.error("Character: actionMine on non resource");
			return JobActionReturn.ABORT;
		}

		if (!"mine".equals(_actionInfo.type)) {
			Log.error("Character: actionMine on non minable item");
			return JobActionReturn.ABORT;
		}

        ResourceModel resource = (ResourceModel)_item;

        CharacterModel.TalentEntry talent = character.getTalent(CharacterModel.TalentType.MINE);
        resource.addQuantity(-talent.work());

		// Check if resource is depleted
        if (!resource.isDepleted()) {
			Log.debug("Mine progress");
			return JobActionReturn.CONTINUE;
		}

		return JobActionReturn.FINISH;
	}

	@Override
	public String getLabel() {
		return "Mine " + _item.getLabel();
	}

	@Override
	public String getShortLabel() {
		return "Mine " + _item.getLabel();
	}

    @Override
    public CharacterModel.TalentType getTalentNeeded() {
        return CharacterModel.TalentType.MINE;
    }

	@Override
    public String getIcon() {
        return "data/res/ic_mine.png";
    }

	@Override
	protected void onStart(CharacterModel character) {
	}

	@Override
	public void onQuit(CharacterModel character) {
	}
}
