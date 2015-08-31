package org.smallbox.faraway.game.model.job;

import org.smallbox.faraway.core.drawable.AnimDrawable;
import org.smallbox.faraway.core.drawable.IconDrawable;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.helper.WorldHelper;
import org.smallbox.faraway.game.model.character.base.CharacterModel;
import org.smallbox.faraway.game.model.item.ItemInfo;
import org.smallbox.faraway.game.model.item.ParcelModel;
import org.smallbox.faraway.game.model.item.ResourceModel;
import org.smallbox.faraway.game.module.character.JobModule;
import org.smallbox.faraway.util.Log;
import org.smallbox.faraway.util.Utils;

public class JobGather extends BaseJobModel {
	private ResourceModel 	_resource;
	private int 			_totalCost;
	private int 			_totalProgress;
	private boolean 		_removeOnComplete;

	@Override
	public CharacterModel.TalentType getTalentNeeded() {
		return CharacterModel.TalentType.GATHER;
	}

	private JobGather(ItemInfo.ItemInfoAction actionInfo, int x, int y) {
		super(actionInfo, x, y, new IconDrawable("data/res/ic_gather.png", 0, 0, 32, 32), new AnimDrawable("data/res/action_gather.png", 0, 0, 32, 32, 7, 10));
	}

	public static BaseJobModel create(ResourceModel resource, boolean removeOnComplete) {
		// Resource is not gatherable
		if (resource == null || resource.getInfo().actions == null || resource.getInfo().actions.isEmpty() || !"gather".equals(resource.getInfo().actions.get(0).type)) {
			return null;
		}


		JobGather job = new JobGather(resource.getInfo().actions.get(0), resource.getX(), resource.getY());
		job.setItem(resource);
		job.setStrategy(j -> {
			if (j.getCharacter().getType().needs.joy != null) {
				j.getCharacter().getNeeds().joy += j.getCharacter().getType().needs.joy.change.work;
			}
		});
		job._resource = resource;
		job._resource.addJob(job);
		job._removeOnComplete = removeOnComplete;

		return job;
	}

	@Override
	public void onStart(CharacterModel character) {
		super.onStart(character);
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

//		// Resource is depleted
//		if (_resource.isDepleted()) {
//			_reason = Abort.INVALID;
//			return false;
//		}

//		// No space left in inventory
//		if (characters.hasInventorySpaceLeft() == false) {
//			_reason = JobAbortReason.NO_LEFT_CARRY;
//			return false;
//		}

		return true;
	}

	@Override
	protected void onFinish() {
		Log.info("Gather complete");
		_resource.removeJob(null);

		if (_removeOnComplete) {
			Game.getWorldManager().removeResource(_resource);
		}

		if (_actionInfo.finalProducts != null) {
			_actionInfo.finalProducts.stream().filter(productInfo -> productInfo.dropRate > Math.random()).forEach(productInfo ->
					Game.getWorldManager().putObject(productInfo.itemInfo, _resource.getX(), _resource.getY(), 0, Utils.getRandom(productInfo.quantity)));
		}
	}

    @Override
	public JobActionReturn onAction(CharacterModel character) {
		// Wrong call
		if (_resource == null) {
			Log.error("Character: actionGather on null job or null job's item");
			JobModule.getInstance().quitJob(this, JobAbortReason.INVALID);
			return JobActionReturn.ABORT;
		}

		if (_resource.getInfo().actions.get(0) == null) {
			Log.error("Character: actionGather on non gatherable item");
			JobModule.getInstance().quitJob(this, JobAbortReason.INVALID);
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
