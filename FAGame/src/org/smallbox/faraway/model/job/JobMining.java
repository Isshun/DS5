package org.smallbox.faraway.model.job;

import org.smallbox.faraway.SpriteModel;
import org.smallbox.faraway.engine.util.Log;
import org.smallbox.faraway.manager.JobManager;
import org.smallbox.faraway.manager.ServiceManager;
import org.smallbox.faraway.manager.SpriteManager;
import org.smallbox.faraway.model.ProfessionModel;
import org.smallbox.faraway.model.character.CharacterModel;
import org.smallbox.faraway.model.item.ItemInfo;
import org.smallbox.faraway.model.item.WorldResource;

public class JobMining extends BaseJob {

    private static final SpriteModel ICON = SpriteManager.getInstance().getIcon("data/res/ic_mine.png");

	private JobMining(ItemInfo.ItemInfoAction actionInfo, int x, int y) {
		super(actionInfo, x, y);
	}

	public static BaseJob create(WorldResource res) {
		// Resource is not minable
		if (res == null) {
			return null;
		}

		if (res.getInfo().actions != null) {
			for (ItemInfo.ItemInfoAction action: res.getInfo().actions) {
				if ("mine".equals(action.type)) {
					BaseJob job = new JobMining(action, res.getX(), res.getY());
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
		if (character.hasInventorySpaceLeft() == false) {
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
		
		if (_item.isResource() == false) {
			Log.error("Character: actionMine on non resource");
			JobManager.getInstance().quit(this, JobAbortReason.INVALID);
			return true;
		}

		if (!"mine".equals(_actionInfo.type)) {
			Log.error("Character: actionMine on non minable item");
			JobManager.getInstance().quit(this, JobAbortReason.INVALID);
			return true;
		}

//		// Character is full: cancel current job
//		if (character.getInventoryLeftSpace() <= 0) {
//			JobManager.getInstance().quit(this, BaseJob.JobAbortReason.NO_LEFT_CARRY);
//			return true;
//		}

		// TODO
		int value = ServiceManager.getWorldMap().gather((WorldResource)_item, character.getProfessionScore(ProfessionModel.Type.NONE));

		Log.debug("mine: " + value);

//		ResourceManager.getInstance().addMatter(value);

        WorldResource gatheredItem = (WorldResource)_item;

        if (_character.work(CharacterModel.TalentType.MINE, gatheredItem)) {
			ServiceManager.getWorldMap().removeResource(gatheredItem);
            if (_actionInfo.dropRate >= Math.random()) {
                for (ItemInfo item : _actionInfo.productsItem) {
                    ServiceManager.getWorldMap().putItem(item, gatheredItem.getX(), gatheredItem.getY(), 0, 100);
                }
            }
            JobManager.getInstance().close(this);
			return true;
		}

//        for (ItemInfo itemInfo: _actionInfo.productsItem) {
//            character.addInventory(new UserItem(itemInfo));
//        }

		return false;
	}

	@Override
	public String getType() {
		return "mine";
	}

	@Override
	public String getLabel() {
		return "mine " + _item.getLabel();
	}

	@Override
	public String getShortLabel() {
		return "mine " + _item.getLabel();
	}

    @Override
    public CharacterModel.TalentType getTalentNeeded() {
        return CharacterModel.TalentType.MINE;
    }

    public SpriteModel getIcon() {
        return ICON;
    }
}
