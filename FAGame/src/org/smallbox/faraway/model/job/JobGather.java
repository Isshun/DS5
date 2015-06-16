package org.smallbox.faraway.model.job;

import org.smallbox.faraway.SpriteModel;
import org.smallbox.faraway.engine.util.Log;
import org.smallbox.faraway.manager.JobManager;
import org.smallbox.faraway.manager.ServiceManager;
import org.smallbox.faraway.manager.SpriteManager;
import org.smallbox.faraway.model.character.CharacterModel;
import org.smallbox.faraway.model.item.ItemInfo;
import org.smallbox.faraway.model.item.ResourceModel;

public class JobGather extends JobModel {
	private static final SpriteModel ICON = SpriteManager.getInstance().getIcon("data/res/ic_gather.png");

	private ResourceModel 	_resource;
	private int 			_totalCost;
	private int 			_totalProgress;

	@Override
	public CharacterModel.TalentType getTalentNeeded() {
		return CharacterModel.TalentType.GATHER;
	}

	private JobGather(ItemInfo.ItemInfoAction action, int x, int y) {
		super(action, x, y);
	}

	public static JobModel create(ResourceModel resource) {
		// Resource is not gatherable
		if (resource == null || resource.getInfo().actions == null || resource.getInfo().actions.isEmpty() || !"gather".equals(resource.getInfo().actions.get(0).type)) {
			return null;
		}


		JobGather job = new JobGather(resource.getInfo().actions.get(0), resource.getX(), resource.getY());
		job.setItem(resource);
		job._resource = resource;
		job._resource.setJob(job);

		return job;
	}

	@Override
	public void onCharacterAssign(CharacterModel character) {
		if (_resource != null) {
			_totalCost = _cost * _resource.getQuantity();
		}
	}

	@Override
	public int getProgressPercent() {
		if (_resource != null) {
			return _totalProgress * 100 / _totalCost;
		}
		return 0;
	}

	@Override
	public boolean check(CharacterModel character) {
		// Item is null
		if (_resource == null) {
			_reason = JobAbortReason.INVALID;
			return false;
		}

		// Item is no longer exists
		if (_resource != ServiceManager.getWorldMap().getResource(_resource.getX(), _resource.getY())) {
			_reason = JobAbortReason.INVALID;
			return false;
		}

//		// Resource is depleted
//		if (_resource.isDepleted()) {
//			_reason = Abort.INVALID;
//			return false;
//		}

//		// No space left in inventory
//		if (character.hasInventorySpaceLeft() == false) {
//			_reason = JobAbortReason.NO_LEFT_CARRY;
//			return false;
//		}

		return true;
	}

	@Override
	public boolean action(CharacterModel character) {
		// Wrong call
		if (_resource == null) {
			Log.error("Character: actionGather on null job or null job's item");
			JobManager.getInstance().quit(this, JobAbortReason.INVALID);
			return true;
		}

		if (_resource.getInfo().actions.get(0) == null) {
			Log.error("Character: actionGather on non gatherable item");
			JobManager.getInstance().quit(this, JobAbortReason.INVALID);
			return true;
		}

        Log.debug(character.getName() + ": gathering (" + _totalProgress + "/" + _totalCost + ")");

//		CharacterModel.TalentEntry talent = character.getTalent(CharacterModel.TalentType.GATHER);
//		double quantity = -talent.work();
//		_resource.addQuantity(quantity);

		++_totalProgress;
		if (++_progress < _cost) {
			return false;
		}

        // Add product items
        _progress = 0;
        for (ItemInfo.ItemProductInfo productInfo: _resource.getInfo().actions.get(0).products) {
            ServiceManager.getWorldMap().putObject(productInfo.itemInfo, _posX, _posY, 0, productInfo.quantity);
            //character.addComponent(new UserItem(info));
            Log.info(character.getName() + ": product " + productInfo.itemInfo.name);
        }
        _resource.addQuantity(-1);

        // Close job if resource is depleted
        if (_resource.getQuantity() <= 0) {
            _resource.setJob(null);
            JobManager.getInstance().close(this);
            ServiceManager.getWorldMap().removeResource(_resource);
            return true;
        }

        return false;
	}

    @Override
    public String getType() {
        return "gather";
    }

    @Override
	public String getLabel() {
		return "gather " + _item.getLabel();
	}

	@Override
	public String getShortLabel() {
		return "gather" + _item.getLabel();
	}

	@Override
	public SpriteModel getIcon() {
		return ICON;
	}
}
