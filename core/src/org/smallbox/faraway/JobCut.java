package org.smallbox.faraway;

import com.badlogic.gdx.ai.pfa.GraphPath;
import org.smallbox.faraway.core.drawable.AnimDrawable;
import org.smallbox.faraway.core.drawable.IconDrawable;
import org.smallbox.faraway.game.helper.WorldHelper;
import org.smallbox.faraway.game.model.character.base.CharacterModel;
import org.smallbox.faraway.game.model.item.ItemInfo;
import org.smallbox.faraway.game.model.item.ParcelModel;
import org.smallbox.faraway.game.model.item.ResourceModel;
import org.smallbox.faraway.game.model.job.BaseJobModel;
import org.smallbox.faraway.game.module.ModuleHelper;
import org.smallbox.faraway.game.module.path.PathManager;
import org.smallbox.faraway.util.Log;
import org.smallbox.faraway.util.MoveListener;
import org.smallbox.faraway.util.Utils;

/**
 * Created by Alex on 15/10/2015.
 */
public class JobCut extends BaseJobModel {
    private ResourceModel _resource;

    private JobCut(ItemInfo.ItemInfoAction actionInfo, ParcelModel jobParcel) {
        super(actionInfo, jobParcel, new IconDrawable("data/res/ic_cut.png", 0, 0, 32, 32), new AnimDrawable("data/res/actions.png", 0, 0, 32, 32, 8, 1));
    }

    public static BaseJobModel create(ResourceModel res) {
        // Resource is not cut-able
        if (res == null) {
            return null;
        }

        if (res.getInfo().actions != null) {
            for (ItemInfo.ItemInfoAction action: res.getInfo().actions) {
                if ("cut".equals(action.type)) {
                    JobCut job = new JobCut(action, res.getParcel());
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

        if ((_parcel == null || !_parcel.isWalkable()) && getFreeParcel() == null) {
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
            _targetParcel = parcel;
        }

        return parcel;
    }

    @Override
    protected void onStart(CharacterModel character) {
        GraphPath<ParcelModel> path = PathManager.getInstance().getBestApprox(character.getParcel(), _jobParcel);

        if (path != null) {
            _targetParcel = path.get(path.getCount() - 1);
            System.out.println("best path to: " + _targetParcel.x + "x" + _targetParcel.y + " (" + character.getInfo().getFirstName() + ")");
            character.move(path);
        }
    }

    @Override
    protected void onFinish() {
        Log.info("Cut complete");
        ModuleHelper.getWorldModule().removeResource(_resource);

        if (_actionInfo.finalProducts != null) {
            _actionInfo.finalProducts.stream().filter(productInfo -> productInfo.dropRate > Math.random())
                    .forEach(productInfo -> ModuleHelper.getWorldModule().putObject(productInfo.item, _resource.getX(), _resource.getY(), 0, Utils.getRandom(productInfo.quantity)));
        }
    }

    @Override
    public JobActionReturn onAction(CharacterModel character) {
        // Wrong call
        if (_item == null) {
            Log.error("Character: action cut on null job or null job's item");
            return JobActionReturn.ABORT;
        }

        if (!_item.isResource()) {
            Log.error("Character: action cut on non resource");
            return JobActionReturn.ABORT;
        }

        if (!"cut".equals(_actionInfo.type)) {
            Log.error("Character: action cut on non cut-able item");
            return JobActionReturn.ABORT;
        }

        ResourceModel resource = (ResourceModel)_item;

        _progress += character.getTalent(CharacterModel.TalentType.CUT).work();
        if (_progress < _cost) {
            return JobActionReturn.CONTINUE;
        }

        // Remove a single unit
        _progress = 0;
        resource.addQuantity(-1);
        if (_actionInfo.products != null) {
            _actionInfo.products.stream().filter(productInfo -> productInfo.dropRate > Math.random()).forEach(productInfo ->
                    ModuleHelper.getWorldModule().putObject(productInfo.item, _resource.getX(), _resource.getY(), 0, Utils.getRandom(productInfo.quantity)));
        }

        // Check if resource is depleted
        if (!resource.isDepleted()) {
            return JobActionReturn.CONTINUE;
        }

        return JobActionReturn.FINISH;
    }

    @Override
    public String getLabel() {
        return "Cut " + _item.getLabel();
    }

    @Override
    public String getShortLabel() {
        return "Cut " + _item.getLabel();
    }

    @Override
    public ParcelModel getActionParcel() {
        return _resource.getParcel();
    }

    @Override
    public CharacterModel.TalentType getTalentNeeded() {
        return CharacterModel.TalentType.CUT;
    }

    @Override
    public void onDraw(onDrawCallback callback) {
        callback.onDraw(_resource.getX(), _resource.getY());
    }
}
