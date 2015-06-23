//package org.smallbox.faraway.game.model.item;
//
//import org.smallbox.faraway.game.manager.ResourceManager;
//import org.smallbox.faraway.game.model.character.base.CharacterModel;
//import org.smallbox.faraway.game.model.job.BaseJob;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class FactoryItem extends ItemModel {
//
//	private BaseJob _refillJob;
//	private double				_itemProduceState;
//	protected List<ItemModel>	_inventory;
//	private List<ItemInfo>		_accepts;
//
//	public FactoryItem(ItemInfo info) {
//		super(info);
//
//		_inventory = new ArrayList<>();
//		if (info.actions != null && info.storage > 0) {
//			_accepts = new ArrayList<>();
//			for (ItemInfo.ItemInfoAction onAction: info.actions) {
//				for (ItemInfo itemProduce : onAction.productsItem) {
//					_accepts.addAll(itemProduce.craftedFromItems);
//				}
//			}
//		}
//	}
//
//	@Override
//	public ItemModel use(CharacterModel character, int durationLeft) {
//		super.use(character, durationLeft);
//
//		if (_info.actions != null && _info.actions.getRoom(0).productsItem != null) {
//			ItemInfo itemToProduce = _info.actions.getRoom(0).productsItem.getRoom(0);
//			_itemProduceState += (double)itemToProduce.craftedQuantitfy / _info.actions.getRoom(0).cost;
//			if (_itemProduceState >= 1) {
//				_itemProduceState -= 1;
//
//				// TODO: getRoom most common component
//				MapObjectModel component = takeFromInventory(itemToProduce.craftedFromItems.getRoom(0));
//				if (component != null) {
//					ResourceManager.getInstance().remove(component.getInfo());
//					ResourceManager.getInstance().add(itemToProduce);
//					return new ItemModel(itemToProduce);
//				}
//			}
//		}
//
//		return null;
//	}
//
//	@Override
//	public boolean matchFilter(ItemFilter filter) {
//		if (super.matchFilter(filter)) {
//			return true;
//		}
//
//		// Filter looking for factory
//		if (filter.lookingForFactory && _info.actions != null) {
//            for (ItemInfo.ItemInfoAction onAction: _info.actions) {
//                if (onAction.productsItem != null) {
//                    // Factory has no free slots
//                    if (filter.needFreeSlot && hasFreeSlot() == false) {
//                        return false;
//                    }
//
//                    for (ItemInfo productsItem: onAction.productsItem) {
//                        if (productsItem != null && productsItem.actions != null && (filter.itemNeeded == productsItem || _info.matchFilter(onAction.effects, filter))) {
//                            // Have components
//                            for (ItemInfo component: productsItem.craftedFromItems) {
//                                if (inventoryContains(component)) {
//                                    filter.itemMatched = productsItem;
//                                    return true;
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//		}
//
//		return false;
//	}
//
//	public boolean needRefill() {
//		return _info.actions != null && _inventory.size() < _info.storage;
//	}
//
//	public boolean isWaitForRefill() {
//		return _refillJob != null && _refillJob.isFinish() == false;
//	}
//
//	public BaseJob getRefillJob() {
//		return _refillJob;
//	}
//
//	public void setRefillJob(BaseJob job) {
//		_refillJob = job;
//	}
//
//	private boolean inventoryContains(ItemInfo info) {
//		for (MapObjectModel item: _inventory) {
//			if (item.getInfo() == info) {
//				return true;
//			}
//		}
//		return false;
//	}
//
//	public List<ItemModel> getComponents() {
//		return _inventory;
//	}
//
//	protected MapObjectModel takeFromInventory(ItemInfo itemInfo) {
//		for (MapObjectModel item: _inventory) {
//			if (item.getInfo() == itemInfo) {
//				_inventory.remove(item);
//				return item;
//			}
//		}
//		return null;
//	}
//
//	public void addComponent(List<ItemModel> items) {
//		_inventory.addAll(items);
//	}
//
//	public void addComponent(ItemModel item) {
//		_inventory.add(item);
//
//		ResourceManager.getInstance().add(item.getInfo());
//	}
//
//	public boolean accept(MapObjectModel item) {
//		if (item == null) {
//			return false;
//		}
//
//		if (_accepts != null && !_accepts.isEmpty() && !_accepts.contains(item.getInfo())) { return false; }
//
//		return true;
//	}
//
//}
