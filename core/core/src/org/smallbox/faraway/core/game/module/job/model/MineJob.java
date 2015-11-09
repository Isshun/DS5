package org.smallbox.faraway.core.game.module.job.model;

import org.smallbox.faraway.core.data.ItemInfo;
import org.smallbox.faraway.core.engine.drawable.AnimDrawable;
import org.smallbox.faraway.core.engine.drawable.IconDrawable;
import org.smallbox.faraway.core.game.module.character.model.CharacterTalentExtra;
import org.smallbox.faraway.core.game.module.character.model.PathModel;
import org.smallbox.faraway.core.game.module.character.model.base.CharacterModel;
import org.smallbox.faraway.core.game.module.job.model.abs.JobModel;
import org.smallbox.faraway.core.game.module.path.PathManager;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.core.game.module.world.model.resource.ResourceModel;
import org.smallbox.faraway.core.module.java.ModuleHelper;
import org.smallbox.faraway.core.util.Log;
import org.smallbox.faraway.core.util.MoveListener;
import org.smallbox.faraway.core.util.Utils;
import org.smallbox.faraway.ui.GameActionExtra;

public class MineJob extends JobModel {
    private ResourceModel       _resource;
    private int                 _totalCost;
    private double              _current;
    private ItemInfo            _itemProduct;

    private MineJob(ItemInfo.ItemInfoAction actionInfo, ParcelModel jobParcel) {
        super(actionInfo, jobParcel, new IconDrawable("data/res/ic_mining.png", 0, 0, 32, 32), new AnimDrawable("data/res/actions.png", 0, 0, 32, 32, 8, 1));
    }

    public static JobModel create(ResourceModel resource, ItemInfo itemProduct) {
        assert resource != null;

        if (resource.getInfo().actions != null) {
            for (ItemInfo.ItemInfoAction action: resource.getInfo().actions) {
                if ("mine".equals(action.type)) {
                    MineJob job = new MineJob(action, resource.getParcel());
                    job.setStrategy(j -> {
                        if (j.getCharacter().getType().needs.joy != null) {
                            j.getCharacter().getNeeds().addValue("entertainment", j.getCharacter().getType().needs.joy.change.work);
                        }
                    });
                    job._resource = resource;
                    job._itemProduct = itemProduct;
                    job._totalCost = job._cost * job._resource.getRock().getQuantity();
                    job._label = "Mine " + resource.getInfo().label;
                    job._message = "Move to resource";
                    return job;
                }
            }
        }

        return null;
    }

    @Override
    protected void onStart(CharacterModel character) {
        PathModel path = PathManager.getInstance().getPath(character.getParcel(), _jobParcel, true, true);

        if (path != null) {
            _targetParcel = path.getLastParcel();

            System.out.println("best path to: " + _targetParcel.x + "x" + _targetParcel.y + " (" + character.getPersonals().getFirstName() + ")");
            character.move(path, new MoveListener<CharacterModel>() {
                @Override
                public void onReach(CharacterModel character) {
                }

                @Override
                public void onFail(CharacterModel character) {
                }
            });
        }
    }

    @Override
    public JobCheckReturn onCheck(CharacterModel character) {
        System.out.println("check job: " + this);

        // Resource no longer exists
        if (_resource != _resource.getParcel().getResource()) {
            _status = JobStatus.INVALID;
            _reason = JobAbortReason.INVALID;
            return JobCheckReturn.ABORT;
        }

        if (!PathManager.getInstance().hasPath(character.getParcel(), _resource.getParcel(), true, true)) {
            _status = JobStatus.BLOCKED;
            return JobCheckReturn.STAND_BY;
        }

        return JobCheckReturn.OK;
    }

    @Override
    public JobActionReturn onAction(CharacterModel character) {
        // Wrong call
        if (_resource == null) {
            Log.error("Character: actionMine on null job or null job's item");
            return JobActionReturn.ABORT;
        }

        if (!_resource.isResource()) {
            Log.error("Character: actionMine on non resource");
            return JobActionReturn.ABORT;
        }

        if (!"mine".equals(_actionInfo.type)) {
            Log.error("Character: actionMine on non minable item");
            return JobActionReturn.ABORT;
        }

        _message = "Mining";

        _current += character.getTalents().get(CharacterTalentExtra.TalentType.MINE).work();
        _progress = _current / _totalCost;
        if (_current < _totalCost) {
            return JobActionReturn.CONTINUE;
        }

        return JobActionReturn.COMPLETE;
    }
    @Override
    protected void onComplete() {
        Log.info("Mine complete");
        ModuleHelper.getWorldModule().removeResource(_resource);

        if (_actionInfo.products != null) {
            _actionInfo.products.stream().filter(productInfo -> productInfo.rate > Math.random()).forEach(productInfo -> {
                ModuleHelper.getWorldModule().putObject(_resource.getParcel(), productInfo.item, Utils.getRandom(productInfo.quantity));
            });
        }

        if (_itemProduct != null) {
            ModuleHelper.getWorldModule().putObject(_resource.getParcel(), _itemProduct, 10, true);
        }
    }

    @Override
    protected void onFinish() {
        if (_resource != null && _resource.getJob() == this) {
            _resource.removeJob(this);
            _resource.setJob(null);
        }
    }

    @Override
    public String getLabel() {
        return "Mine " + _resource.getLabel();
    }

    @Override
    public CharacterTalentExtra.TalentType getTalentNeeded() {
        return CharacterTalentExtra.TalentType.MINE;
    }

    @Override
    public void draw(onDrawCallback callback) {
        callback.onDraw(_resource.getParcel().x, _resource.getParcel().y);
    }

}
