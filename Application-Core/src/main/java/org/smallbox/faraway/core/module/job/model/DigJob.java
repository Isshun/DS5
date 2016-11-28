package org.smallbox.faraway.core.module.job.model;

import org.apache.commons.lang3.NotImplementedException;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.module.character.model.CharacterTalentExtra;
import org.smallbox.faraway.core.module.character.model.PathModel;
import org.smallbox.faraway.core.module.character.model.base.CharacterModel;
import org.smallbox.faraway.core.module.job.model.abs.JobModel;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.util.Log;
import org.smallbox.faraway.util.MoveListener;

public class DigJob extends JobModel {
    private int                 _totalCost;
    private double              _current;
    private ItemInfo            _itemProduct;
    private ItemInfo            _rockInfo;
    private ParcelModel         _parcelToRemoveGround;
    private ItemInfo            _groundInfo;

    private DigJob(ItemInfo.ItemInfoAction actionInfo, ParcelModel jobParcel) {
        super(actionInfo, jobParcel);
//        super(actionInfo, jobParcel, new IconDrawable("data/res/ic_mining.png", 0, 0, 32, 32), new AnimDrawable("data/res/actions.png", 0, 0, 32, 32, 8, 1));
    }

    public static DigJob create(ParcelModel parcel, ItemInfo rockInfo, ItemInfo itemProduct) {
        return create(parcel, rockInfo, itemProduct, null, null);
    }

    public static DigJob create(ParcelModel parcel, ItemInfo rockInfo, ItemInfo itemProduct, ParcelModel parcelToRemoveGround, ItemInfo groundInfo) {
        assert rockInfo != null;

        if (rockInfo.actions != null) {
            for (ItemInfo.ItemInfoAction action: rockInfo.actions) {
                if (action.type == ItemInfo.ItemInfoAction.ActionType.MINE) {
                    DigJob job = new DigJob(action, parcel);
                    job.setOnActionListener(() -> {
                        if (job.getCharacter().getType().needs.joy != null) {
                            job.getCharacter().getNeeds().addValue("entertainment", job.getCharacter().getType().needs.joy.change.work);
                        }
                    });
                    job._jobParcel = parcel;
                    job._jobParcel.setDigJob(job);
                    job._rockInfo = rockInfo;
                    job._itemProduct = itemProduct;
                    job._parcelToRemoveGround = parcelToRemoveGround;
                    job._groundInfo = groundInfo;
                    job._totalCost = job._cost * 10;
                    job._label = "Mine " + rockInfo.label;
                    job._message = "Move to resource";
                    return job;
                }
            }
        }

        return null;
    }

    @Override
    protected void onStart(CharacterModel character) {
        PathModel path = Application.pathManager.getPath(character.getParcel(), _jobParcel, true, _parcelToRemoveGround != null);

        if (path != null) {
            _targetParcel = path.getLastParcel();

            Log.info("best path to: " + _targetParcel.x + "x" + _targetParcel.y + " (" + character.getPersonals().getFirstName() + ")");
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
        Log.info("isJobLaunchable job: " + this);

        // Resource no longer exists
        if (_rockInfo != _jobParcel.getRockInfo()) {
            _status = JobStatus.INVALID;
            _reason = JobAbortReason.INVALID;
            return JobCheckReturn.ABORT;
        }

        if (!Application.pathManager.hasPath(character.getParcel(), _jobParcel, true, _parcelToRemoveGround != null)) {
            _status = JobStatus.BLOCKED;
            return JobCheckReturn.STAND_BY;
        }

        return JobCheckReturn.OK;
    }

    @Override
    public JobActionReturn onAction(CharacterModel character) {
        assert _actionInfo.type == ItemInfo.ItemInfoAction.ActionType.MINE;

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
        throw new NotImplementedException("");

//        Log.info("Mine complete");
//
//        _jobParcel.setRockInfo(null);
//        Application.notify(observer -> observer.onRemoveRock(_jobParcel));
//
//        if (_actionInfo.products != null) {
//            _actionInfo.products.stream().filter(productInfo -> productInfo.rate > Math.random()).forEach(productInfo -> {
//                ModuleHelper.getWorldModule().putObject(_jobParcel, productInfo.item, Utils.getRandom(productInfo.quantity));
//            });
//        }
//
//        if (_parcelToRemoveGround != null ) {
//            ModuleHelper.getWorldModule().replaceGround(_parcelToRemoveGround, _groundInfo);
//        }
//
//        if (_itemProduct != null) {
//            ModuleHelper.getWorldModule().putObject(_jobParcel, _itemProduct, 10, true);
//        }
    }

    @Override
    protected void onFinish() {
        if (_jobParcel.getDigJob() == this) {
            _jobParcel.setDigJob(null);
        }
    }

    @Override
    public String getLabel() {
        return "Mine " + _rockInfo.label;
    }

    @Override
    public CharacterTalentExtra.TalentType getTalentNeeded() {
        return CharacterTalentExtra.TalentType.MINE;
    }

    @Override
    public void draw(onDrawCallback callback) {
        callback.onDraw(_jobParcel.x, _jobParcel.y, _jobParcel.z);
    }

}