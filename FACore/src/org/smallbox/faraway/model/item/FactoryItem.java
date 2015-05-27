package org.smallbox.faraway.model.item;

import java.util.ArrayList;
import java.util.List;

import org.smallbox.faraway.manager.ResourceManager;
import org.smallbox.faraway.model.character.Character;
import org.smallbox.faraway.model.job.Job;

public class FactoryItem extends UserItem {

	private	Job					_refillJob;
	private double				_itemProduceState;
	protected List<UserItem>	_inventory;
	private List<ItemInfo>		_accepts;

	public FactoryItem(ItemInfo info) {
		super(info);

		_inventory = new ArrayList<UserItem>();
		if (info.onAction != null && info.onAction.storage > 0) {
			_accepts = new ArrayList<ItemInfo>();
			for (ItemInfo itemProduce: info.onAction.itemsProduce) {
				_accepts.addAll(itemProduce.craftedFromItems);
			}
		}
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

	@Override
	public boolean matchFilter(ItemFilter filter) {
		if (super.matchFilter(filter)) {
			return true;
		}

		// Filter looking for factory
		if (filter.lookingForFactory && _info.onAction != null && _info.onAction.itemsProduce != null) {

			// Factory has no free slots
			if (filter.needFreeSlot && hasFreeSlot() == false) {
				return false;
			}

			for (ItemInfo itemProduce: _info.onAction.itemsProduce) {
				if (itemProduce != null && itemProduce.onAction != null && (filter.itemNeeded == itemProduce || _info.matchFilter(itemProduce.onAction.effects, filter))) {
					// Have components
					for (ItemInfo component: itemProduce.craftedFromItems) {
						if (inventoryContains(component)) {
							filter.itemMatched = itemProduce;
							return true;
						}
					}
				}
			}
		}

		return false;
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

	private boolean inventoryContains(ItemInfo info) {
		for (ItemBase item: _inventory) {
			if (item.getInfo() == info) {
				return true;
			}
		}
		return false;
	}

	public List<UserItem> getInventory() {
		return _inventory;
	}

	protected ItemBase takeFromInventory(ItemInfo itemInfo) {
		for (ItemBase item: _inventory) {
			if (item.getInfo() == itemInfo) {
				_inventory.remove(item);
				return item;
			}
		}
		return null;
	}

	public void addInventory(List<UserItem> items) {
		_inventory.addAll(items);
	}

	public void addInventory(UserItem item) {
		_inventory.add(item);

		ResourceManager.getInstance().add(item.getInfo());
	}

	public boolean accept(ItemBase item) {
		if (item == null) {
			return false;
		}

		if (_accepts != null && !_accepts.isEmpty() && !_accepts.contains(item.getInfo())) { return false; }

		return true;
	}

}
