package org.smallbox.faraway.core.module.job.model;

import org.apache.commons.lang3.NotImplementedException;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.modules.character.model.CharacterTalentExtra;
import org.smallbox.faraway.modules.character.model.PathModel;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;
import org.smallbox.faraway.core.module.job.model.abs.JobModel;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.flora.model.PlantItem;
import org.smallbox.faraway.util.Log;

public class GatherJob extends JobModel {
    public enum Mode {PLANT_SEED, NOURISH, HARVEST, CUT}

    private Mode                _mode;
    private PlantItem _plant;
    private int                 _totalCost;
    private double              _current;

    @Override
    public CharacterTalentExtra.TalentType getTalentNeeded() {
        return CharacterTalentExtra.TalentType.GATHER;
    }

    private GatherJob(ItemInfo.ItemInfoAction actionInfo, ParcelModel jobParcel) {
        super(actionInfo, jobParcel);
//        super(actionInfo, jobParcel, new IconDrawable("data/res/ic_gather.png", 0, 0, 32, 32), new AnimDrawable("data/res/action_gather.png", 0, 0, 32, 32, 7, 10));
    }

    public static JobModel create(PlantItem plant, Mode mode) {
        assert plant != null;

        ItemInfo info = plant.getInfo();

        // Resource has no actions
        if (info.actions == null || info.actions.isEmpty()) {
            return null;
        }

        // Resource is not gatherable
        boolean hasGatherAction = false;
        for (ItemInfo.ItemInfoAction action: info.actions) {
            if (action.type == ItemInfo.ItemInfoAction.ActionType.GATHER) {
                hasGatherAction = true;
            }
        }
        if (!hasGatherAction) {
            return null;
        }

        GatherJob job = new GatherJob(info.actions.get(0), plant.getParcel());
        job.setOnActionListener(() -> {
            if (job.getCharacter().getType().needs.joy != null) {
                job.getCharacter().getNeeds().addValue("entertainment", job.getCharacter().getType().needs.joy.change.work);
            }
        });
        job._plant = plant;
        job._plant.addJob(job);
        job._plant.setJob(job);
        job._totalCost = job._cost;
        job._mode = mode;

        switch (mode) {
            case PLANT_SEED: job._label = "Plant seed"; break;
            case NOURISH: job._label = "Nourish"; break;
            case HARVEST: job._label = "Harvest"; break;
            case CUT: job._label = "Cut"; break;
        }

        return job;
    }

    @Override
    public JobCheckReturn onCheck(CharacterModel character) {
        // Resource is no longer exists
        if (_plant != _plant.getParcel().getPlant()) {
            _reason = JobAbortReason.INVALID;
            return JobCheckReturn.ABORT;
        }

        if (_mode == Mode.NOURISH && _plant.isMature()) {
            return JobCheckReturn.ABORT;
        }

        if (!Application.pathManager.hasPath(character.getParcel(), _plant.getParcel())) {
            return JobCheckReturn.STAND_BY;
        }

        return JobCheckReturn.OK;
    }

    @Override
    protected void onStart(CharacterModel character) {
        PathModel path = Application.pathManager.getPath(character.getParcel(), _jobParcel, true, false);

        if (path != null) {
            _targetParcel = path.getLastParcel();

            Log.info("best path to: " + _targetParcel.x + "x" + _targetParcel.y + " (" + character.getPersonals().getFirstName() + ")");
            character.move(path);
        }
    }

    @Override
    public JobActionReturn onAction(CharacterModel character) {
        // Wrong call
        if (_plant == null) {
            Log.error("Character: actionGather on null job or null job's item");
            return JobActionReturn.ABORT;
        }

        if (_plant.getInfo().actions.get(0) == null) {
            Log.error("Character: actionGather on non gatherable item");
            return JobActionReturn.ABORT;
        }

        _current += character.getTalents().get(CharacterTalentExtra.TalentType.GATHER).work();
        _progress = _current / _totalCost;
        if (_current < _totalCost) {
            return JobActionReturn.CONTINUE;
        }

        return JobActionReturn.COMPLETE;
    }

    @Override
    protected void onComplete() {
        throw new NotImplementedException("");

//        Log.info("Gather complete");
//
//        if (_mode == Mode.PLANT_SEED) {
//            _plant.setSeed(true);
//            _plant.setNourish(1);
//        }
//
//        if (_mode == Mode.NOURISH) {
//            _plant.setNourish(Math.min(1, _plant.getNourish() + 0.5));
//        }
//
//        if (_mode == Mode.HARVEST || _mode == Mode.CUT) {
//            double maturity = _plant.getMaturity();
//
//            // Create consumable from resource
//            if (_actionInfo.products != null) {
//                _actionInfo.products.stream().filter(productInfo -> productInfo.rate > Math.random()).forEach(productInfo -> {
//                    ModuleHelper.getWorldModule().putConsumable(_plant.getParcel(), productInfo.item, (int) Math.round(Utils.getRandom(productInfo.quantity) * maturity));
//                });
//            }
//
//            // Remove resource from parcel function of mode or resource info
//            if (_mode == Mode.CUT || _plant.getInfo().plant.cutOnGathering) {
//                ModuleHelper.getWorldModule().remove(_plant);
//            }
//        }
    }

    @Override
    protected void onFinish() {
        if (_plant != null && _plant.getJob() == this) {
            _plant.removeJob(this);
            _plant.setJob(null);
        }
    }
}
