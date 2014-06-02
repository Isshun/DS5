package alone.in.deepspace.model.item;

import alone.in.deepspace.manager.ResourceManager;
import alone.in.deepspace.model.character.Character;
import alone.in.deepspace.model.job.Job;

public class FactoryItem extends StorageItem {

	private	Job				_refillJob;
	private double			_itemProduceState;

	public FactoryItem(ItemInfo info) {
		super(info);
	}
	
	@Override
	public UserItem use(Character character, int durationLeft) {
		super.use(character, durationLeft);
		
		if (_info.onAction != null && _info.onAction.itemsProduce != null) {
			ItemInfo itemToProduce = _info.onAction.itemsProduce.get(0);
			_itemProduceState += (double)itemToProduce.craftedQuantitfy / _info.onAction.duration;
			if (_itemProduceState >= 1) {
				_itemProduceState -= 1;
				
				// TODO: get most common component
				ItemBase component = takeFromInventory(itemToProduce.craftedFromItems.get(0));
				if (component != null) {
					ResourceManager.getInstance().remove(component.getInfo());
					ResourceManager.getInstance().add(itemToProduce);
					return new UserItem(itemToProduce);
				}
			}
		}
		
		return null;
	}
	
	public boolean needRefill() {
		return _info.onAction != null && _inventory.size() < _info.onAction.storage;
	}

	public boolean isWaitForRefill() {
		return _refillJob != null && _refillJob.isFinish() == false;
	}

	public Job getRefillJob() {
		return _refillJob;
	}

	public void setRefillJob(Job job) {
		_refillJob = job;
	}


}
