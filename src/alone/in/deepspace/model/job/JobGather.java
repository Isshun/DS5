package alone.in.deepspace.model.job;

import alone.in.deepspace.manager.JobManager;
import alone.in.deepspace.manager.JobManager.Action;
import alone.in.deepspace.manager.ResourceManager;
import alone.in.deepspace.manager.ServiceManager;
import alone.in.deepspace.model.BaseItem;
import alone.in.deepspace.model.Profession;
import alone.in.deepspace.model.WorldResource;
import alone.in.deepspace.model.character.Character;
import alone.in.deepspace.util.Log;

public class JobGather extends Job {

	private JobGather(int x, int y) {
		super(x, y);
	}

	public static Job create(WorldResource ressource) {
		// Resource is not gatherable
		if (ressource == null || ressource.getInfo().onGather == null) {
			return null;
		}

		Job job = new JobGather(ressource.getX(), ressource.getY());
		job.setAction(Action.GATHER);
		job.setItem(ressource);

		return job;
	}

	@Override
	public boolean check(Character character) {
		// Item is null
		if (_item == null) {
			_reason = Abort.INVALID;
			return false;
		}

		// Item is no longer exists
		if (_item != ServiceManager.getWorldMap().getRessource(_item.getX(), _item.getY())) {
			_reason = Abort.INVALID;
			return false;
		}

		// Resource is depleted
		if (_item.getMatterSupply() <= 0) {
			_reason = Abort.INVALID;
			return false;
		}

		// No space left in inventory
		if (character.hasInventorySpaceLeft() == false) {
			_reason = Abort.NO_LEFT_CARRY;
			return false;
		}

		return true;
	}

	@Override
	public boolean action(Character character) {
		// Wrong call
		if (_item == null) {
			Log.error("Character: actionGather on null job or null job's item");
			JobManager.getInstance().abort(this, Abort.INVALID);
			return true;
		}

		if (_item.getInfo().onGather == null) {
			Log.error("Character: actionGather on non gatherable item");
			JobManager.getInstance().abort(this, Abort.INVALID);
			return true;
		}


		// Character is full: cancel current job
		if (character.getInventoryLeftSpace() <= 0) {
			JobManager.getInstance().abort(this, Abort.NO_LEFT_CARRY);
			return true;
		}

		// TODO
		int value = ServiceManager.getWorldMap().gather(_item, character.getProfessionScore(Profession.Type.NONE));

		Log.debug("gather: " + value);

		ResourceManager.getInstance().addMatter(value);

		if (_item.getMatterSupply() == 0) {
			JobManager.getInstance().complete(this);
			return true;
		}

		character.addInventory(new BaseItem(_item.getInfo().onGather.itemProduce));
		
		return false;
	}

}
