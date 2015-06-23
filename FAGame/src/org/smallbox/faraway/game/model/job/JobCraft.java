package org.smallbox.faraway.game.model.job;

import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.OnMoveListener;
import org.smallbox.faraway.game.model.StorageModel;
import org.smallbox.faraway.util.Log;
import org.smallbox.faraway.game.manager.JobManager;
import org.smallbox.faraway.game.model.ReceiptModel;
import org.smallbox.faraway.game.model.character.base.CharacterModel;
import org.smallbox.faraway.game.model.item.ConsumableModel;
import org.smallbox.faraway.game.model.item.ItemInfo;
import org.smallbox.faraway.game.model.item.MapObjectModel;
import org.smallbox.faraway.game.model.item.ParcelModel;

import java.util.ArrayList;
import java.util.List;

public class JobCraft extends BaseJobModel {
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
	protected void onStart(CharacterModel character) {
	}

	@Override
	public void onQuit(CharacterModel character) {
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
		super(action, x, y, "data/res/ic_craft.png", "data/res/ic_action_craft.png");
	}

	public static JobCraft create(ItemInfo.ItemInfoAction action, MapObjectModel item) {
		if (item == null) {
			throw new RuntimeException("Cannot add Craft job (item is null)");
		}

		if (action == null) {
			throw new RuntimeException("Cannot add Craft job (onAction is null)");
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
	public boolean onCheck(CharacterModel character) {
		if (_receipt == null) {
			findWorkableReceipt(character);
		}

		return _receipt != null;
	}

	@Override
	protected void onFinish() {

	}

	private void findWorkableReceipt(CharacterModel character) {
		_status = Status.MOVE_TO_INGREDIENT;
		_receipt = null;
		for (ReceiptModel receipt: _receipts) {
			boolean hasComponentOnMap = true;
			for (ReceiptModel.ReceiptComponentModel component: receipt.getComponents()) {
                int inInventory = character.getInventory() != null && character.getInventory().getInfo() == component.item.getInfo() ? character.getInventory().getQuantity() : 0;
				if (component.item.getQuantity() < component.count && (Game.getWorldManager().getConsumableCount(component.itemInfo) + inInventory) < component.count) {
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
	public JobActionReturn onAction(CharacterModel character) {
        if (_character == null) {
            Log.error("Action on job with null character");
        }

		// Wrong call
		if (_item == null || _item != Game.getWorldManager().getItem(_itemPosX, _itemPosY)) {
			Log.error("Character: actionUse on null job or null job's item or invalid item");
			JobManager.getInstance().quit(this, JobAbortReason.INVALID);
			return JobActionReturn.ABORT;
		}

		if (_receipt == null) {
			findWorkableReceipt(character);
			if (_receipt == null) {
				return JobActionReturn.QUIT;
			}
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

		return JobActionReturn.ABORT;
	}

    private JobActionReturn actionMoveToStorage(CharacterModel character) {
        return closeOrQuit(character);
    }

    private JobActionReturn actionMoveToFactory(CharacterModel character) {
        // Work continue
        _progress += character.getTalent(CharacterModel.TalentType.CRAFT).work();
        if (_progress < _cost) {
            Log.debug("Character #" + character.getName() + ": Crafting (" + _progress + ")");
            return JobActionReturn.CONTINUE;
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
                return JobActionReturn.CONTINUE;
            } else {
                Game.getWorldManager().putConsumable(consumable, character.getX(), character.getY());
            }
        }

        return closeOrQuit(character);
    }

    private JobActionReturn actionMoveToIngredient(CharacterModel character) {
        //if (character.getX() != _targetX || character.getY() != _targetY)

        if (_targetIngredient == null) {
            findNearestIngredient(character);
            if (_targetIngredient == null) {
                return closeOrQuit(character);
            }
        }

        // Ingredient no longer exists
        if (_targetIngredient != Game.getWorldManager().getConsumable(_targetX, _targetY)) {
            _targetIngredient = null;
            _status = Status.MOVE_TO_INGREDIENT;
            return JobActionReturn.CONTINUE;
        }

        // Move ingredient to Crafter
        ReceiptModel.ReceiptComponentModel component = _receipt.getComponent(_targetIngredient.getInfo());
        while (_targetIngredient.getQuantity() > 0 && component.item.getQuantity() < component.count) {
            _targetIngredient.addQuantity(-1);
            component.item.addQuantity(1);
        }
        if (_targetIngredient.getQuantity() <= 0) {
            _targetIngredient.getParcel().setConsumable(null);
        }

        // Components still missing
        if (!_receipt.hasComponents()) {
            findNearestIngredient(character);
            return JobActionReturn.CONTINUE;
        }

        // Receipt is complete
        _status = Status.MOVE_TO_FACTORY;
        _posX = _item.getX();
        _posY = _item.getY();
        character.moveTo(this, _item.getX(), _item.getY(), new OnMoveListener() {
            @Override
            public void onReach(BaseJobModel job, CharacterModel character) {
                for (ReceiptModel.ReceiptComponentModel component: _receipt.getComponents()) {
                    character.setInventory(null);
                    _item.addComponent(component.item);
                }
            }

            @Override
            public void onFail(BaseJobModel job, CharacterModel character) {
                for (ReceiptModel.ReceiptComponentModel component: _receipt.getComponents()) {
                    character.setInventory(null);
                    Game.getWorldManager().putConsumable(component.item, character.getX(), character.getY());
                }
            }
        });

        return JobActionReturn.CONTINUE;
    }

    private JobActionReturn closeOrQuit(CharacterModel character) {

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
			return JobActionReturn.FINISH;
		}

		// Some remains
		else {
			JobManager.getInstance().quit(this);
			return JobActionReturn.CONTINUE;
		}
	}

	private void findNearestIngredient(CharacterModel character) {
		_targetIngredient = null;
		if (_receipt != null) {
			for (ReceiptModel.ReceiptComponentModel component : _receipt.getComponents()) {
				if (component.item.getQuantity() < component.count) {
					_targetIngredient = Game.getWorldManager().getFinder().getNearest(component.itemInfo, _item.getX(), _item.getY());
					if (_targetIngredient != null) {
						_targetX = _targetIngredient.getX();
						_targetY = _targetIngredient.getY();
						_posX = _targetX;
						_posY = _targetY;
						_status = Status.MOVE_TO_INGREDIENT;
						character.moveTo(this, _targetIngredient.getX(), _targetIngredient.getY(), new OnMoveListener() {
							@Override
							public void onReach(BaseJobModel job, CharacterModel character) {
								for (ReceiptModel.ReceiptComponentModel component : _receipt.getComponents()) {
                                    character.setInventory(component.item);
									_item.addComponent(component.item);
								}
							}

							@Override
							public void onFail(BaseJobModel job, CharacterModel character) {
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
		StorageModel storage = Game.getAreaManager().getNearestFreeStorage(consumable, character.getParcel());
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
            public void onReach(BaseJobModel job, CharacterModel character) {
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
            public void onFail(BaseJobModel job, CharacterModel character) {
            }
        });

		return storage;
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
