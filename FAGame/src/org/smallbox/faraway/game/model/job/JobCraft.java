package org.smallbox.faraway.game.model.job;

import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.OnMoveListener;
import org.smallbox.faraway.game.model.StorageModel;
import org.smallbox.faraway.game.model.item.*;
import org.smallbox.faraway.util.Log;
import org.smallbox.faraway.game.manager.JobManager;
import org.smallbox.faraway.game.model.ReceiptModel;
import org.smallbox.faraway.game.model.character.base.CharacterModel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class JobCraft extends BaseJobModel {
	protected List<ReceiptModel>    _receipts = new ArrayList<>();
	private ReceiptModel            _receipt;
	private int                     _itemPosX;
	private int                     _itemPosY;
	private int                     _targetX;
	private int                     _targetY;
	private StorageModel 		    _storage;
	private ParcelModel 		    _storageParcel;
    private List<ConsumableModel>   _potentialConsumables;
    protected ItemModel             _factory;
    protected Status                _status;
    protected String                _message;

    public void addConsumable(ConsumableModel consumable) {
        for (ReceiptModel receipt: _receipts) {
            receipt.addConsumable(consumable);
        }
    }

    public void removeConsumable(ConsumableModel consumable) {
        for (ReceiptModel receipt: _receipts) {
            receipt.removeConsumable(consumable);
        }
    }

    public enum Status {
		MOVE_TO_INGREDIENT, MOVE_TO_FACTORY, MOVE_TO_STORAGE
	}

	@Override
	protected void onStart(CharacterModel character) {
        int bestDistance = Integer.MAX_VALUE;
        for (ReceiptModel receipt: _receipts) {
            if (bestDistance > receipt.getTotalDistance() && receipt.hasComponentsOnMap()) {
                bestDistance = receipt.getTotalDistance();
                _receipt = receipt;
            }
        }

        if (_receipt == null) {
            throw new RuntimeException("Try to start JobCraft but no receipt have enough component");
        }

        _receipt.start();
        moveToIngredient(character, _receipt.getCurrentComponent().consumable);
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
		job._factory = (ItemModel)item;
		job._receipts = action.receipts.stream().map(receiptInfo -> new ReceiptModel((ItemModel) item, receiptInfo)).collect(Collectors.toList());

		item.addJob(job);

		return job;
	}

	@Override
	public boolean onCheck(CharacterModel character) {
        for (ReceiptModel receipt: _receipts) {
            if (receipt.hasComponentsOnMap()) {
                return true;
            }
        }
        return false;
	}

	@Override
	protected void onFinish() {

	}

	private boolean findWorkableReceipt(CharacterModel character) {
//		_status = Status.MOVE_TO_INGREDIENT;
//		_receipt = null;
//		for (ReceiptModel receipt: _receipts) {
//			boolean hasComponentsOnMap = true;
//			for (ReceiptModel.ReceiptComponentModel component: receipt.getComponents()) {
//                int inInventory = character.getInventory() != null && character.getInventory().getInfo() == component.item.getInfo() ? character.getInventory().getQuantity() : 0;
//				if (component.item.getQuantity() < component.count && (Game.getWorldManager().getConsumableCount(component.itemInfo) + inInventory) < component.count) {
//					hasComponentsOnMap = false;
//				}
//			}
//			if (hasComponentsOnMap) {
//				_receipt = receipt;
//				findNearestIngredient(character);
//				return true;
//			}
//		}
        return false;
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

//        if (_receipt != null && !_receipt.hasComponentsOnMap()) {
//            _status = Status.FIND_RECEIPT;
//            return JobActionReturn.CONTINUE;
//        }
//
//        if (_status == Status.FIND_RECEIPT) {
//            findWorkableReceipt(character);
//            if (_receipt == null) {
//                return JobActionReturn.QUIT;
//            }
//            return JobActionReturn.CONTINUE;
//        }

		// Move to ingredient
		if (_status == Status.MOVE_TO_INGREDIENT) {
            return onActionIngredient(character);
		}

		// Work on factory
		if (_status == Status.MOVE_TO_FACTORY) {
            return onActionFactory(character);
		}

		// Work on factory
		if (_status == Status.MOVE_TO_STORAGE) {
            return onActionStorage(character);
		}

		return JobActionReturn.ABORT;
	}

    private JobActionReturn onActionFactory(CharacterModel character) {
        if (!_receipt.hasComponentsInFactory()) {
            moveToIngredient(character, _receipt.getCurrentComponent().consumable);
            return JobActionReturn.CONTINUE;
        }

        // Work continue
        _progress += character.getTalent(CharacterModel.TalentType.CRAFT).work();
        if (_progress < _cost) {
            Log.debug("Character #" + character.getInfo().getName() + ": Crafting (" + _progress + ")");
            return JobActionReturn.CONTINUE;
        }

        // Clear factory
        _factory.getComponents().clear();

        // Current item is done
        _progress = 0;
        for (ItemInfo.ItemProductInfo productInfo: _receipt.getInfo().products) {
            ConsumableModel productConsumable = new ConsumableModel(productInfo.itemInfo);
            productConsumable.setQuantity(productInfo.quantity);

            if (findNearestStorage(character, productConsumable)) {
                character.setInventory(productConsumable);
                _status = Status.MOVE_TO_STORAGE;
                return JobActionReturn.CONTINUE;
            } else {
                Game.getWorldManager().putConsumable(productConsumable, character.getX(), character.getY());
            }
        }

        return closeOrQuit(character);
    }

    private JobActionReturn onActionStorage(CharacterModel character) {
        if (_storage == null) {
            return closeOrQuit(character);
        }

		ParcelModel parcel = _storage.getNearestFreeParcel(character.getInventory(), character.getX(), character.getY());
		if (parcel == null) {
			Log.warning("No free space in _storage area");
			Game.getWorldManager().putConsumable(character.getInventory(), character.getX(), character.getY());
            character.setInventory(null);
            return closeOrQuit(character);
		}

        // Store inventory item to storage
        Game.getWorldManager().putConsumable(character.getInventory(), parcel.getX(), parcel.getY());
        character.setInventory(null);

		return closeOrQuit(character);
    }
//
//    private void moveToFactory(CharacterModel character) {
//        _status = Status.MOVE_TO_FACTORY;
//        _posX = _item.getX();
//        _posY = _item.getY();
//        character.moveTo(this, _item.getX(), _item.getY(), new OnMoveListener() {
//            @Override
//            public void onReach(BaseJobModel job, CharacterModel character) {
//                for (ReceiptModel.ReceiptComponentModel component : _receipt.getComponents()) {
//                    _item.addComponent(component.item);
//                    character.setInventory(null);
//                }
//            }
//
//            @Override
//            public void onFail(BaseJobModel job, CharacterModel character) {
//                for (ReceiptModel.ReceiptComponentModel component : _receipt.getComponents()) {
//                    Game.getWorldManager().putConsumable(component.item, character.getX(), character.getY());
//                    character.setInventory(null);
//                }
//            }
//
//            @Override
//            public void onSuccess(BaseJobModel job, CharacterModel character) {
//            }
//        });
//    }

    /**
     * Action when character reach ingredient
     *
     * @param character
     * @return
     */
    private JobActionReturn onActionIngredient(CharacterModel character) {
        //if (character.getX() != _targetX || character.getY() != _targetY)

        ReceiptModel.NeededComponent currentComponent = _receipt.getCurrentComponent();

//        if (currentComponent == null) {
//            findNearestIngredient(character);
//            if (currentComponent == null) {
//                return closeOrQuit(character);
//            }
//        }

        // Ingredient no longer exists
        if (currentComponent.consumable != Game.getWorldManager().getConsumable(_targetX, _targetY)) {
            throw new RuntimeException("Consumable no longer exists, JobCraft have to lock consumable !!");
//            _status = Status.MOVE_TO_INGREDIENT;
//            return JobActionReturn.CONTINUE;
        }

        // Take all consumable units
        if (currentComponent.quantity == currentComponent.consumable.getQuantity()) {
            character.setInventory(currentComponent.consumable);
            Game.getWorldManager().removeConsumable(currentComponent.consumable);
        } else {
            ConsumableModel consumable = new ConsumableModel(currentComponent.consumable.getInfo());
            consumable.setQuantity(currentComponent.quantity);
            character.setInventory(consumable);
            currentComponent.consumable.addQuantity(-currentComponent.quantity);
        }
        _status = Status.MOVE_TO_FACTORY;
        _posX = _factory.getX();
        _posY = _factory.getY();
        _message = "Carry " + currentComponent.consumable.getInfo().label + " to " + _factory.getInfo().label;

        // Store component in factory
        character.moveTo(this, _factory.getParcel(), new OnMoveListener() {
            @Override
            public void onReach(BaseJobModel job, CharacterModel character) {
                _factory.addComponent(character.getInventory());
                character.setInventory(null);
                _receipt.nextComponent();
            }

            @Override
            public void onFail(BaseJobModel job, CharacterModel character) {
            }

            @Override
            public void onSuccess(BaseJobModel job, CharacterModel character) {
            }
        });

        return JobActionReturn.CONTINUE;
    }

    private JobActionReturn closeOrQuit(CharacterModel character) {

        // Unload factory
        for (ConsumableModel component: _factory.getComponents()) {
            Game.getWorldManager().putConsumable(component, _factory.getX(), _factory.getY());
        }

		// Switch status to MOVE_TO_INGREDIENT
		_receipt.reset();
		_receipt = null;
		_status = Status.MOVE_TO_INGREDIENT;

		// Work is complete
		if (_count++ >= _totalCount) {
			//Log.debug("Character #" + character.getId() + ": work close");
			return JobActionReturn.FINISH;
		}

		// Some remains
		else {
			return JobActionReturn.QUIT;
		}
	}
//
//	private void findNearestIngredient(CharacterModel character) {
//		_targetIngredient = null;
//		if (_receipt != null) {
//			for (ReceiptModel.ReceiptComponentModel component : _receipt.getComponents()) {
//				if (component.item.getQuantity() < component.count) {
//					_targetIngredient = ((WorldFinder)Game.getInstance().getManager(WorldFinder.class)).getNearest(component.itemInfo, _item.getX(), _item.getY());
//					if (_targetIngredient != null) {
//                        moveToIngredient(character, _targetIngredient);
//						return;
//					}
//				}
//			}
//		}
//	}

    private void moveToIngredient(CharacterModel character, ConsumableModel targetIngredient) {
        _targetX = targetIngredient.getX();
        _targetY = targetIngredient.getY();
        _posX = _targetX;
        _posY = _targetY;
        _status = Status.MOVE_TO_INGREDIENT;
        character.moveTo(this, targetIngredient.getX(), targetIngredient.getY(), null);

        _message = "Move to " + targetIngredient.getInfo().label;
    }

    private boolean findNearestStorage(CharacterModel character, ConsumableModel consumable) {
		// Looking for free _storage area for consumable
		_storage = Game.getAreaManager().getNearestFreeStorage(consumable, character.getParcel());
		if (_storage == null) {
			return false;
		}

		// Get free _storageParcel from _storage
		_storageParcel = _storage.getNearestFreeParcel(consumable, character.getX(), character.getY());
		if (_storageParcel == null) {
			return false;
		}

		// Move to storage
        moveToStorage(character, _storageParcel);

		return true;
	}

    private void moveToStorage(CharacterModel character, ParcelModel storageParcel) {
        _posX = storageParcel.getX();
        _posY = storageParcel.getY();

        _status = Status.MOVE_TO_STORAGE;
        character.moveTo(this, _posX, _posY, null);

        _message = "Move " + _receipt.getInfo().products.get(0).itemInfo.label + " to storage";
    }

    @Override
	public String getLabel() {
		return _actionInfo.label;
	}

	@Override
	public String getShortLabel() {
		return "work";
	}

    @Override
    public String getMessage() {
        return _message;
    }

}
