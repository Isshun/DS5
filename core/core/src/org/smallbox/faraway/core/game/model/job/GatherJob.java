package org.smallbox.faraway.core.game.model.job;

import com.badlogic.gdx.ai.pfa.GraphPath;
import org.smallbox.faraway.core.engine.drawable.AnimDrawable;
import org.smallbox.faraway.core.engine.drawable.IconDrawable;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.model.character.base.CharacterModel;
import org.smallbox.faraway.core.game.model.item.ItemInfo;
import org.smallbox.faraway.core.game.model.item.ParcelModel;
import org.smallbox.faraway.core.game.model.item.ResourceModel;
import org.smallbox.faraway.core.game.model.job.abs.JobModel;
import org.smallbox.faraway.core.game.module.ModuleHelper;
import org.smallbox.faraway.core.game.module.path.PathManager;
import org.smallbox.faraway.core.util.Log;
import org.smallbox.faraway.core.util.Utils;

public class GatherJob extends JobModel {
    private ResourceModel     _resource;
    private int             _totalCost;
    private int             _totalProgress;

    @Override
    public CharacterModel.TalentType getTalentNeeded() {
        return CharacterModel.TalentType.GATHER;
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
        job.setItem(resource);
        job.setStrategy(j -> {
            if (j.getCharacter().getType().needs.joy != null) {
                j.getCharacter().getNeeds().joy += j.getCharacter().getType().needs.joy.change.work;
            }
        });
        job._resource = resource;
        job._resource.addJob(job);

        return job;
    }

    @Override
    protected void onStart(CharacterModel character) {
        GraphPath<ParcelModel> path = PathManager.getInstance().getBestApprox(character.getParcel(), _jobParcel);

        if (path != null) {
            _targetParcel = path.get(path.getCount() - 1);

            System.out.println("best path to: " + _targetParcel.x + "x" + _targetParcel.y + " (" + character.getInfo().getFirstName() + ")");
            character.move(path);
        }

        if (_resource != null) {
            _totalCost = _cost * _resource.getQuantity();
        }
    }

    @Override
    public int getProgressPercent() {
        if (_resource != null && _totalCost != 0) {
            return _totalProgress * 100 / _totalCost;
        }
        return 0;
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
            _actionInfo.finalProducts.stream().filter(productInfo -> productInfo.dropRate > Math.random()).forEach(productInfo ->
                    ModuleHelper.getWorldModule().putObject(productInfo.item, _resource.getX(), _resource.getY(), 0, Utils.getRandom(productInfo.quantity)));
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

        Log.debug(character.getInfo().getName() + ": gathering (" + _totalProgress + "/" + _totalCost + ")");

        ResourceModel resource = (ResourceModel)_item;

        _totalProgress++;
        _progress += character.getTalent(CharacterModel.TalentType.GATHER).work();
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
        return "gather " + _item.getLabel();
    }

    @Override
    public String getShortLabel() {
        return "gather" + _item.getLabel();
    }

    @Override
    public ParcelModel getActionParcel() {
        return _resource.getParcel();
    }
}
