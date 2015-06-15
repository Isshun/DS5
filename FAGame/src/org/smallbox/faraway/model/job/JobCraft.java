package org.smallbox.faraway.model.job;

import org.smallbox.faraway.Game;
import org.smallbox.faraway.OnMoveListener;
import org.smallbox.faraway.engine.util.Log;
import org.smallbox.faraway.manager.JobManager;
import org.smallbox.faraway.manager.ServiceManager;
import org.smallbox.faraway.model.ReceiptModel;
import org.smallbox.faraway.model.character.CharacterModel;
import org.smallbox.faraway.model.item.ConsumableModel;
import org.smallbox.faraway.model.item.ItemInfo;
import org.smallbox.faraway.model.item.MapObjectModel;
import org.smallbox.faraway.model.item.ParcelModel;

import java.util.ArrayList;
import java.util.List;

public class JobCraft extends JobModel {
	protected List<ReceiptModel>_receipts = new ArrayList<>();
	private ReceiptModel        _receipt;
	private int                 _itemPosX;
	private int                 _itemPosY;
	private int                 _targetX;
	private int                 _targetY;

	public enum Status {
		MOVE_TO_INGREDIENT, MOVE_TO_FACTORY, MOVE_TO_STORAGE
	}

	Status _status = Status.MOVE_TO_INGREDIENT;

	ConsumableModel _targetIngredient;
	ConsumableModel _ingredient;

	@Override
	public ConsumableModel getIngredient() {
		return _ingredient;
	}

	@Override
	public void					setItem(MapObjectModel item) {
		super.setItem(item);

		if (item != null) {
			_itemPosX = item.getX();
			_itemPosY = item.getY();
		}
	}

	@Override
	public CharacterModel.TalentType getTalentNeeded() {
		return CharacterModel.TalentType.CRAFT;
	}

	protected JobCraft(ItemInfo.ItemInfoAction action, int x, int y) {
		super(action, x, y);
	}

	public static JobCraft create(ItemInfo.ItemInfoAction action, MapObjectModel item) {
		if (item == null) {
			throw new RuntimeException("Cannot add Craft job (item is null)");
		}

		if (action == null) {
			throw new RuntimeException("Cannot add Craft job (action is null)");
		}


		JobCraft job = new JobCraft(action, item.getX(), item.getY());
		job.setItem(item);
		job._receipts = new ArrayList<>();
		for (ItemInfo.ItemInfoReceipt receiptInfo: action.receipts) {
			job._receipts.add(new ReceiptModel(receiptInfo));
		}

		item.addJob(job);

		return job;
	}

	@Override
	public boolean check(CharacterModel character) {
		if (_receipt == null) {
			findWorkableReceipt(character);
		}

		return _receipt != null;
	}

	private void findWorkableReceipt(CharacterModel character) {
		_status = Status.MOVE_TO_INGREDIENT;
		_receipt = null;
		for (ReceiptModel receipt: _receipts) {
			boolean hasComponentOnMap = true;
			for (ReceiptModel.ReceiptComponentModel component: receipt.getComponents()) {
                int inInventory = character.getInventory() != null && character.getInventory().getInfo() == component.item.getInfo() ? character.getInventory().getQuantity() : 0;
				if (component.item.getQuantity() < component.count && (ServiceManager.getWorldMap().getConsumableCount(component.itemInfo) + inInventory) < component.count) {
					hasComponentOnMap = false;
				}
			}
			if (hasComponentOnMap) {
				_receipt = receipt;
				findNearestIngredient(character);
				return;
			}
		}
	}

	@Override
	public boolean action(CharacterModel character) {
        if (_character == null) {
            Log.error("Action on job with null character");
        }

		// Wrong call
		if (_item == null || _item != ServiceManager.getWorldMap().getItem(_itemPosX, _itemPosY)) {
			Log.error("Character: actionUse on null job or null job's item or invalid item");
			JobManager.getInstance().quit(this, JobAbortReason.INVALID);
			return false;
		}

		if (_receipt == null) {
			findWorkableReceipt(character);
			return _receipt != null;
		}

		// Move to ingredient
		if (_status == Status.MOVE_TO_INGREDIENT) {
            return actionMoveToIngredient(character);
		}

		// Work on factory
		else if (_status == Status.MOVE_TO_FACTORY) {
            return actionMoveToFactory(character);
		}

		// Work on factory
		else if (_status == Status.MOVE_TO_STORAGE) {
            return actionMoveToStorage(character);
		}

		return true;
	}

    private boolean actionMoveToStorage(CharacterModel character) {
        return closeOrQuit(character);
    }

    private boolean actionMoveToFactory(CharacterModel character) {
        // Work continue
        _progress += character.getTalent(CharacterModel.TalentType.CRAFT).work();
        if (_progress < _cost) {
            Log.debug("Character #" + character.getName() + ": Crafting (" + _progress + ")");
            return false;
        }

        // Current item is done
        _progress = 0;
        for (ItemInfo.ItemProductInfo productInfo: _receipt.receiptInfo.products) {
            ConsumableModel consumable = new ConsumableModel(productInfo.itemInfo);
            consumable.setQuantity(productInfo.quantity);

            StorageModel storage = findNearestStorage(character, consumable);
            if (storage != null) {
                character.setInventory(consumable);
                _status = Status.MOVE_TO_STORAGE;
                return false;
            } else {
                Game.getWorldManager().putConsumable(consumable, character.getX(), character.getY());
            }
        }

        return closeOrQuit(character);
    }

    private boolean actionMoveToIngredient(CharacterModel character) {
        //if (character.getX() != _targetX || character.getY() != _targetY)

        if (_targetIngredient == null) {
            findNearestIngredient(character);
            if (_targetIngredient == null) {
                return closeOrQuit(character);
            }
        }

        // Ingredient no longer exists
        if (_targetIngredient != ServiceManager.getWorldMap().getConsumable(_targetX, _targetY)) {
            _targetIngredient = null;
            _status = Status.MOVE_TO_INGREDIENT;
            return false;
        }

        // Move ingredient to Crafter
        ReceiptModel.ReceiptComponentModel component = _receipt.getComponent(_targetIngredient.getInfo());
        while (_targetIngredient.getQuantity() > 0 && component.item.getQuantity() < component.count) {
            _targetIngredient.addQuantity(-1);
            component.item.addQuantity(1);
        }
        if (_targetIngredient.getQuantity() <= 0) {
            _targetIngredient.getArea().setConsumable(null);
        }

        // Components still missing
        if (!_receipt.hasComponents()) {
            findNearestIngredient(character);
            return false;
        }

        // Receipt is complete
        _status = Status.MOVE_TO_FACTORY;
        _posX = _item.getX();
        _posY = _item.getY();
        character.moveTo(this, _item.getX(), _item.getY(), new OnMoveListener() {
            @Override
            public void onReach(JobModel job, CharacterModel character) {
                for (ReceiptModel.ReceiptComponentModel component: _receipt.getComponents()) {
                    character.setInventory(null);
                    _item.addComponent(component.item);
                }
            }

            @Override
            public void onFail(JobModel job, CharacterModel character) {
                for (ReceiptModel.ReceiptComponentModel component: _receipt.getComponents()) {
                    character.setInventory(null);
                    Game.getWorldManager().putConsumable(component.item, character.getX(), character.getY());
                }
            }
        });

        return false;
    }

    private boolean closeOrQuit(CharacterModel character) {

		// Switch status to MOVE_TO_INGREDIENT
		_targetIngredient = null;
		_ingredient = null;
		_receipt.reset();
		_receipt = null;
		_status = Status.MOVE_TO_INGREDIENT;

		// Work is complete
		if (_count++ >= _totalCount) {
			//Log.debug("Character #" + character.getId() + ": work close");
			JobManager.getInstance().close(this);
			return true;
		}

		// Som remains
		else {
			JobManager.getInstance().quit(this);
			return false;
		}
	}

	private void findNearestIngredient(CharacterModel character) {
		_targetIngredient = null;
		if (_receipt != null) {
			for (ReceiptModel.ReceiptComponentModel component : _receipt.getComponents()) {
				if (component.item.getQuantity() < component.count) {
					_targetIngredient = ServiceManager.getWorldMap().getFinder().getNearest(component.itemInfo, _item.getX(), _item.getY());
					if (_targetIngredient != null) {
						_targetX = _targetIngredient.getX();
						_targetY = _targetIngredient.getY();
						_posX = _targetX;
						_posY = _targetY;
						_status = Status.MOVE_TO_INGREDIENT;
						character.moveTo(this, _targetIngredient.getX(), _targetIngredient.getY(), new OnMoveListener() {
							@Override
							public void onReach(JobModel job, CharacterModel character) {
								for (ReceiptModel.ReceiptComponentModel component : _receipt.getComponents()) {
                                    character.setInventory(component.item);
									_item.addComponent(component.item);
								}
							}

							@Override
							public void onFail(JobModel job, CharacterModel character) {
							}
						});
						return;
					}
				}
			}
		}
	}

	private StorageModel findNearestStorage(CharacterModel character, ConsumableModel consumable) {
		// Looking for free storage area for consumable
		StorageModel storage = Game.getAreaManager().getNearestFreeStorage(consumable, character.getX(), character.getY());
		if (storage == null) {
			return null;
		}

		// Get free parcel from storage
		ParcelModel parcel = storage.getNearestFreeParcel(consumable, character.getX(), character.getY());
		if (parcel == null) {
			return null;
		}

		// Move to parcel
		_posX = parcel.getX();
		_posY = parcel.getY();

        _status = Status.MOVE_TO_STORAGE;
        character.moveTo(this, _posX, _posY, new OnMoveListener() {
            @Override
            public void onReach(JobModel job, CharacterModel character) {
				ParcelModel parcel = storage.getNearestFreeParcel(consumable, character.getX(), character.getY());
				if (parcel != null) {
					Game.getWorldManager().putConsumable(consumable, parcel.getX(), parcel.getY());
				} else {
					Log.warning("No free space in storage area");
					Game.getWorldManager().putConsumable(consumable, _posX, _posY);
				}
				character.setInventory(null);
            }

            @Override
            public void onFail(JobModel job, CharacterModel character) {
            }
        });

		return storage;
	}

	@Override
	public String getType() {
		return "craft";
	}

	@Override
	public String getLabel() {
		return _actionInfo.label;
	}

	@Override
	public String getShortLabel() {
		return "work";
	}

}
