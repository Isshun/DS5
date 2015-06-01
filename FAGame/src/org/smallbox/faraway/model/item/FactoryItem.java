package org.smallbox.faraway.model.item;

import org.smallbox.faraway.manager.ResourceManager;
import org.smallbox.faraway.model.character.CharacterModel;
import org.smallbox.faraway.model.job.Job;

import java.util.ArrayList;
import java.util.List;

public class FactoryItem extends UserItem {

	private	Job					_refillJob;
	private double				_itemProduceState;
	protected List<UserItem>	_inventory;
	private List<ItemInfo>		_accepts;

	public FactoryItem(ItemInfo info) {
		super(info);

		_inventory = new ArrayList<>();
		if (info.actions != null && info.storage > 0) {
			_accepts = new ArrayList<>();
			for (ItemInfo.ItemInfoAction action: info.actions) {
				for (ItemInfo itemProduce : action.productsItem) {
					_accepts.addAll(itemProduce.craftedFromItems);
				}
			}
		}
	}

	@Override
	public UserItem use(CharacterModel character, int durationLeft) {
		super.use(character, durationLeft);

		if (_info.actions != null && _info.actions.get(0).productsItem != null) {
			ItemInfo itemToProduce = _info.actions.get(0).productsItem.get(0);
			_itemProduceState += (double)itemToProduce.craftedQuantitfy / _info.actions.get(0).duration;
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
		if (filter.lookingForFactory && _info.actions != null) {
            for (ItemInfo.ItemInfoAction action: _info.actions) {
                if (action.productsItem != null) {
                    // Factory has no free slots
                    if (filter.needFreeSlot && hasFreeSlot() == false) {
                        return false;
                    }

                    for (ItemInfo productsItem: action.productsItem) {
                        if (productsItem != null && productsItem.actions != null && (filter.itemNeeded == productsItem || _info.matchFilter(action.effects, filter))) {
                            // Have components
                            for (ItemInfo component: productsItem.craftedFromItems) {
                                if (inventoryContains(component)) {
                                    filter.itemMatched = productsItem;
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
		}

		return false;
	}

	public boolean needRefill() {
		return _info.actions != null && _inventory.size() < _info.storage;
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
