package org.smallbox.faraway.core.game.module.job.model;

import org.smallbox.faraway.core.engine.drawable.AnimDrawable;
import org.smallbox.faraway.core.engine.drawable.IconDrawable;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.module.character.model.PathModel;
import org.smallbox.faraway.core.game.module.character.model.TalentExtra;
import org.smallbox.faraway.core.game.module.character.model.base.CharacterModel;
import org.smallbox.faraway.core.game.module.job.model.abs.JobModel;
import org.smallbox.faraway.core.game.module.path.PathManager;
import org.smallbox.faraway.core.data.ItemInfo;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.core.game.module.world.model.resource.ResourceModel;
import org.smallbox.faraway.core.module.java.ModuleHelper;
import org.smallbox.faraway.core.util.Log;
import org.smallbox.faraway.core.util.Utils;

public class GatherJob extends JobModel {
    private ResourceModel       _resource;
    private int                 _totalCost;
    private double              _current;

    @Override
    public TalentExtra.TalentType getTalentNeeded() {
        return TalentExtra.TalentType.GATHER;
    }

    private GatherJob(ItemInfo.ItemInfoAction actionInfo, ParcelModel jobParcel) {
        super(actionInfo, jobParcel, new IconDrawable("data/res/ic_gather.png", 0, 0, 32, 32), new AnimDrawable("data/res/action_gather.png", 0, 0, 32, 32, 7, 10));
    }

    public static JobModel create(ResourceModel resource) {
        // Resource is not gatherable
        if (resource == null || resource.getInfo().actions == null || resource.getInfo().actions.isEmpty() || !"gather".equals(resource.getInfo().actions.get(0).type)) {
            return null;
        }


        GatherJob job = new GatherJob(resource.getInfo().actions.get(0), resource.getParcel());
        job.setStrategy(j -> {
            if (j.getCharacter().getType().needs.joy != null) {
                j.getCharacter().getNeeds().joy += j.getCharacter().getType().needs.joy.change.work;
            }
        });
        job._resource = resource;
        job._resource.addJob(job);
        job._totalCost = job._cost;

        return job;
    }

    @Override
    protected void onStart(CharacterModel character) {
        PathModel path = PathManager.getInstance().getBestApprox(character.getParcel(), _jobParcel);

        if (path != null) {
            _targetParcel = path.getLastParcel();

            System.out.println("best path to: " + _targetParcel.x + "x" + _targetParcel.y + " (" + character.getInfo().getFirstName() + ")");
            character.move(path);
        }
    }

    @Override
    public boolean onCheck(CharacterModel character) {
        // Item is null
        if (_resource == null) {
            _reason = JobAbortReason.INVALID;
            return false;
        }

        // Item is no longer exists
        if (_resource != WorldHelper.getResource(_resource.getX(), _resource.getY())) {
            _reason = JobAbortReason.INVALID;
            return false;
        }

//        // Resource is depleted
//        if (_resource.isDepleted()) {
//            _reason = Abort.INVALID;
//            return false;
//        }

//        // No space left in inventory
//        if (characters.hasInventorySpaceLeft() == false) {
//            _reason = JobAbortReason.NO_LEFT_CARRY;
//            return false;
//        }

        return true;
    }

    @Override
    protected void onFinish() {
        Log.info("Gather complete");
        _resource.removeJob(null);

        if (_resource.getInfo().plant.cutOnGathering) {
            ModuleHelper.getWorldModule().removeResource(_resource);
        }

        if (_actionInfo.finalProducts != null) {
            _actionInfo.finalProducts.stream().filter(productInfo -> productInfo.rate > Math.random()).forEach(productInfo ->
                    ModuleHelper.getWorldModule().putObject(_resource.getParcel(), productInfo.item, Utils.getRandom(productInfo.quantity)));
        }
    }

    @Override
    public void onActionDo() {
        if (_current > 0) {
            _progress += 0.01;
        }
    }

    @Override
    public JobActionReturn onAction(CharacterModel character) {
        // Wrong call
        if (_resource == null) {
            Log.error("Character: actionGather on null job or null job's item");
            ModuleHelper.getJobModule().quitJob(this, JobAbortReason.INVALID);
            return JobActionReturn.ABORT;
        }

        if (_resource.getInfo().actions.get(0) == null) {
            Log.error("Character: actionGather on non gatherable item");
            ModuleHelper.getJobModule().quitJob(this, JobAbortReason.INVALID);
            return JobActionReturn.ABORT;
        }

        _current += character.getTalents().get(TalentExtra.TalentType.GATHER).work();
//        _progress = _current / _totalCost;
        if (_current < _totalCost) {
            return JobActionReturn.CONTINUE;
        }

        // Remove a single unit
        _resource.setQuantity(0);
        if (_actionInfo.products != null) {
            _actionInfo.products.stream().filter(productInfo -> productInfo.rate > Math.random()).forEach(productInfo ->
                    ModuleHelper.getWorldModule().putObject(_resource.getParcel(), productInfo.item, Utils.getRandom(productInfo.quantity)));
        }

        return JobActionReturn.FINISH;
    }

    @Override
    public String getLabel() {
        return "gather " + _resource.getLabel();
    }

    @Override
    public String getShortLabel() {
        return "gather" + _resource.getLabel();
    }

    @Override
    public ParcelModel getActionParcel() {
        return _resource.getParcel();
    }
}
