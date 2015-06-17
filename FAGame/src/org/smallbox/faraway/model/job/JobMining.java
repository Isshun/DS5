package org.smallbox.faraway.model.job;

import org.smallbox.faraway.SpriteModel;
import org.smallbox.faraway.engine.util.Log;
import org.smallbox.faraway.manager.JobManager;
import org.smallbox.faraway.manager.ServiceManager;
import org.smallbox.faraway.manager.SpriteManager;
import org.smallbox.faraway.model.character.base.CharacterModel;
import org.smallbox.faraway.model.item.ItemInfo;
import org.smallbox.faraway.model.item.ResourceModel;

public class JobMining extends JobModel {
    private static final SpriteModel ICON = SpriteManager.getInstance().getIcon("data/res/ic_mine.png");

	private JobMining(ItemInfo.ItemInfoAction actionInfo, int x, int y) {
		super(actionInfo, x, y);
	}

	public static JobModel create(ResourceModel res) {
		// Resource is not minable
		if (res == null) {
			return null;
		}

		if (res.getInfo().actions != null) {
			for (ItemInfo.ItemInfoAction action: res.getInfo().actions) {
				if ("mine".equals(action.type)) {
					JobMining job = new JobMining(action, res.getX(), res.getY());
					job.setItem(res);
					return job;
				}
			}
		}

		return null;
	}

	@Override
	public boolean check(CharacterModel character) {
		// Item is null
		if (_item == null) {
			_reason = JobAbortReason.INVALID;
			return false;
		}
		
		// Item is no longer exists
		if (_item != ServiceManager.getWorldMap().getResource(_item.getX(), _item.getY())) {
			_reason = JobAbortReason.INVALID;
			return false;
		}
		
//		// Resource is depleted
//		if (_item.getMatterSupply() <= 0) {
//			_reason = JobAbortReason.INVALID;
//			return false;
//		}

		// No space left in inventory
		if (!character.hasInventorySpaceLeft()) {
			_reason = JobAbortReason.NO_LEFT_CARRY;
			return false;
		}

		return true;
	}

	@Override
	public boolean action(CharacterModel character) {
		// Wrong call
		if (_item == null) {
			Log.error("Character: actionMine on null job or null job's item");
			JobManager.getInstance().quit(this, JobAbortReason.INVALID);
			return true;
		}
		
		if (!_item.isResource()) {
			Log.error("Character: actionMine on non resource");
			JobManager.getInstance().quit(this, JobAbortReason.INVALID);
			return true;
		}

		if (!"mine".equals(_actionInfo.type)) {
			Log.error("Character: actionMine on non minable item");
			JobManager.getInstance().quit(this, JobAbortReason.INVALID);
			return true;
		}

        ResourceModel resource = (ResourceModel)_item;

        CharacterModel.TalentEntry talent = character.getTalent(CharacterModel.TalentType.MINE);
        resource.addQuantity(-talent.work());
        if (!resource.isDepleted()) {
			Log.debug("Mine progress");
			return false;
		}

		// Resource is depleted
		Log.info("Mine complete");
		ServiceManager.getWorldMap().removeResource(resource);
		_actionInfo.products.stream().filter(productInfo -> productInfo.dropRate > Math.random()).forEach(productInfo -> {
			ServiceManager.getWorldMap().putObject(productInfo.itemInfo, resource.getX(), resource.getY(), 0, productInfo.quantity);
		});
		JobManager.getInstance().close(this);

		return true;
	}

	@Override
	public String getType() {
		return "mine";
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
    public SpriteModel getIcon() {
        return ICON;
    }
}
