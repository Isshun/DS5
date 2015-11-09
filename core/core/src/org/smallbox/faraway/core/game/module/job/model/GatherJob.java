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
import org.smallbox.faraway.core.util.Utils;

public class GatherJob extends JobModel {
    public enum Mode {PLANT_SEED, NOURISH, HARVEST, CUT}

    private Mode                _mode;
    private ResourceModel       _resource;
    private int                 _totalCost;
    private double              _current;

    @Override
    public CharacterTalentExtra.TalentType getTalentNeeded() {
        return CharacterTalentExtra.TalentType.GATHER;
    }

    private GatherJob(ItemInfo.ItemInfoAction actionInfo, ParcelModel jobParcel) {
        super(actionInfo, jobParcel, new IconDrawable("data/res/ic_gather.png", 0, 0, 32, 32), new AnimDrawable("data/res/action_gather.png", 0, 0, 32, 32, 7, 10));
    }

    public static JobModel create(ResourceModel resource, Mode mode) {
        assert resource != null;
        assert resource.isPlant();

        // Resource is not gatherable
        if (resource.getInfo().actions == null || resource.getInfo().actions.isEmpty() || !"gather".equals(resource.getInfo().actions.get(0).type)) {
            return null;
        }

        GatherJob job = new GatherJob(resource.getInfo().actions.get(0), resource.getParcel());
        job.setStrategy(j -> {
            if (j.getCharacter().getType().needs.joy != null) {
                j.getCharacter().getNeeds().addValue("entertainment", j.getCharacter().getType().needs.joy.change.work);
            }
        });
        job._resource = resource;
        job._resource.addJob(job);
        job._resource.setJob(job);
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
        if (_resource != _resource.getParcel().getResource()) {
            _reason = JobAbortReason.INVALID;
            return JobCheckReturn.ABORT;
        }

        if (_mode == Mode.NOURISH && _resource.getPlant().isMature()) {
            return JobCheckReturn.ABORT;
        }

        if (!PathManager.getInstance().hasPath(character.getParcel(), _resource.getParcel())) {
            return JobCheckReturn.STAND_BY;
        }

        return JobCheckReturn.OK;
    }

    @Override
    protected void onStart(CharacterModel character) {
        PathModel path = PathManager.getInstance().getPath(character.getParcel(), _jobParcel, true, false);

        if (path != null) {
            _targetParcel = path.getLastParcel();

            System.out.println("best path to: " + _targetParcel.x + "x" + _targetParcel.y + " (" + character.getPersonals().getFirstName() + ")");
            character.move(path);
        }
    }

    @Override
    public JobActionReturn onAction(CharacterModel character) {
        // Wrong call
        if (_resource == null) {
            Log.error("Character: actionGather on null job or null job's item");
            return JobActionReturn.ABORT;
        }

        if (_resource.getInfo().actions.get(0) == null) {
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
        Log.info("Gather complete");

        if (_mode == Mode.PLANT_SEED) {
            _resource.getPlant().setSeed(true);
            _resource.getPlant().setNourish(1);
        }

        if (_mode == Mode.NOURISH) {
            _resource.getPlant().setNourish(Math.min(1, _resource.getPlant().getNourish() + 0.5));
        }

        if (_mode == Mode.HARVEST || _mode == Mode.CUT) {
            double maturity = _resource.getPlant().getMaturity();

            // Create consumable from resource
            if (_actionInfo.products != null) {
                _actionInfo.products.stream().filter(productInfo -> productInfo.rate > Math.random()).forEach(productInfo -> {
                    ModuleHelper.getWorldModule().putConsumable(_resource.getParcel(), productInfo.item, (int) Math.round(Utils.getRandom(productInfo.quantity) * maturity));
                });
            }

            // Remove resource from parcel function of mode or resource info
            if (_mode == Mode.CUT || _resource.getInfo().plant.cutOnGathering) {
                ModuleHelper.getWorldModule().removeResource(_resource);
            }
        }
    }

    @Override
    protected void onFinish() {
        if (_resource != null && _resource.getJob() == this) {
            _resource.removeJob(this);
            _resource.setJob(null);
        }
    }
}
