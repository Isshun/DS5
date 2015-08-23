package org.smallbox.faraway.game.model.job;

import org.smallbox.faraway.core.drawable.AnimDrawable;
import org.smallbox.faraway.core.drawable.IconDrawable;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.helper.WorldHelper;
import org.smallbox.faraway.game.manager.path.PathManager;
import org.smallbox.faraway.game.model.character.base.CharacterModel;
import org.smallbox.faraway.game.model.item.ItemInfo;
import org.smallbox.faraway.game.model.item.ParcelModel;
import org.smallbox.faraway.game.model.item.ResourceModel;
import org.smallbox.faraway.util.Log;
import org.smallbox.faraway.util.Utils;

public class JobMining extends BaseJobModel {
    private ResourceModel 	    _resource;

	private JobMining(ItemInfo.ItemInfoAction actionInfo, int x, int y) {
		super(actionInfo, x, y, new IconDrawable("data/res/ic_mining.png", 0, 0, 32, 32), new AnimDrawable("data/res/actions.png", 0, 0, 32, 32, 8, 1));
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
					job.setStrategy(j -> {
                        if (j.getCharacter().getType().needs.joy != null) {
                            j.getCharacter().getNeeds().joy += j.getCharacter().getType().needs.joy.change.work;
                        }
                    });
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
		System.out.println("check job: " + this);

		// Item is null
		if (_item == null) {
			_reason = JobAbortReason.INVALID;
			return false;
		}

		if ((_parcel == null || _parcel.isBlocked()) && getFreeParcel() == null) {
			_reason = JobAbortReason.BLOCKED;
			return false;
		}
		
		// Item is no longer exists
		if (_item != WorldHelper.getResource(_item.getX(), _item.getY())) {
			_reason = JobAbortReason.INVALID;
			return false;
		}

		if (!PathManager.getInstance().hasPath(character.getParcel(), _item.getParcel())) {
			return false;
		}
		
//		// Resource is depleted
//		if (_item.getMatterSupply() <= 0) {
//			_reason = JobAbortReason.INVALID;
//			return false;
//		}

//		// No space left in inventory
//		if (!characters.hasInventorySpaceLeft()) {
//			_reason = JobAbortReason.NO_LEFT_CARRY;
//			return false;
//		}

		return true;
	}

	private ParcelModel getFreeParcel() {
        int x = _item.getX();
        int y = _item.getY();
        ParcelModel parcel = null;

        // Corner
        if (!WorldHelper.isBlocked(x - 1, y - 1)) parcel = WorldHelper.getParcel(x-1, y-1);
        if (!WorldHelper.isBlocked(x + 1, y - 1)) parcel = WorldHelper.getParcel(x+1, y-1);
        if (!WorldHelper.isBlocked(x-1, y+1)) parcel = WorldHelper.getParcel(x-1, y+1);
        if (!WorldHelper.isBlocked(x+1, y+1)) parcel = WorldHelper.getParcel(x+1, y+1);

        // Cross
        if (!WorldHelper.isBlocked(x, y-1)) parcel = WorldHelper.getParcel(x, y-1);
        if (!WorldHelper.isBlocked(x, y+1)) parcel = WorldHelper.getParcel(x, y+1);
        if (!WorldHelper.isBlocked(x-1, y)) parcel = WorldHelper.getParcel(x-1, y);
        if (!WorldHelper.isBlocked(x+1, y)) parcel = WorldHelper.getParcel(x+1, y);

        _parcel = parcel;
        if (parcel != null) {
            _posX = parcel.x;
            _posY = parcel.y;
        }

		return parcel;
	}

	@Override
	protected void onFinish() {
		Log.info("Mine complete");
		Game.getWorldManager().removeResource(_resource);

        if (_actionInfo.finalProducts != null) {
            _actionInfo.finalProducts.stream().filter(productInfo -> productInfo.dropRate > Math.random()).forEach(productInfo -> {
                Game.getWorldManager().putObject(productInfo.itemInfo, _resource.getX(), _resource.getY(), 0, Utils.getRandom(productInfo.quantity));
            });
        }
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

        _progress += character.getTalent(CharacterModel.TalentType.MINE).work();
        if (_progress < _cost) {
            return JobActionReturn.CONTINUE;
        }

        // Remove a single unit
        _progress = 0;
        resource.addQuantity(-1);
        if (_actionInfo.products != null) {
            _actionInfo.products.stream().filter(productInfo -> productInfo.dropRate > Math.random()).forEach(productInfo ->
                    Game.getWorldManager().putObject(productInfo.itemInfo, _resource.getX(), _resource.getY(), 0, Utils.getRandom(productInfo.quantity)));
        }

		// Check if resource is depleted
        if (!resource.isDepleted()) {
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
    public ParcelModel getActionParcel() {
        return _resource.getParcel();
    }

    @Override
    public CharacterModel.TalentType getTalentNeeded() {
        return CharacterModel.TalentType.MINE;
    }

    @Override
    public void onDraw(onDrawCallback callback) {
        callback.onDraw(_resource.getX(), _resource.getY());
    }

}
