package org.smallbox.faraway.game.model.job;

import org.smallbox.faraway.core.drawable.AnimDrawable;
import org.smallbox.faraway.core.drawable.IconDrawable;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.helper.WorldHelper;
import org.smallbox.faraway.game.model.ReceiptModel;
import org.smallbox.faraway.game.model.area.StorageAreaModel;
import org.smallbox.faraway.game.model.character.base.CharacterModel;
import org.smallbox.faraway.game.model.item.ConsumableModel;
import org.smallbox.faraway.game.model.item.ItemInfo;
import org.smallbox.faraway.game.model.item.ItemModel;
import org.smallbox.faraway.game.model.item.ParcelModel;
import org.smallbox.faraway.game.module.ModuleManager;
import org.smallbox.faraway.game.module.character.JobModule;
import org.smallbox.faraway.game.module.world.AreaModule;
import org.smallbox.faraway.util.Log;
import org.smallbox.faraway.util.Utils;

import java.util.stream.Collectors;

public class JobCraft extends BaseBuildJobModel {
	private int                     _itemPosX;
	private int                     _itemPosY;
    private ParcelModel             _storageParcel;
    private StorageAreaModel        _storage;

    protected JobCraft(ItemInfo.ItemInfoAction actionInfo, int x, int y) {
        super(actionInfo, x, y, new IconDrawable("data/res/ic_craft.png", 0, 0, 32, 32), new AnimDrawable("data/res/actions.png", 0, 160, 32, 32, 7, 10));
    }

	@Override
	protected void onStart(CharacterModel character) {
        int bestDistance = Integer.MAX_VALUE;
        for (ReceiptModel receipt: _receipts) {
            receipt.reset();
            if (bestDistance > receipt.getTotalDistance() && receipt.hasComponentsOnMap()) {
                bestDistance = receipt.getTotalDistance();
                _receipt = receipt;
            }
        }

        if (_receipt == null) {
            throw new RuntimeException("Try to start JobCraft but no receipt have enough component");
        }

        // Start receipt and get first component
        _receipt.start(this);
        moveToIngredient(character, _receipt.getCurrentOrder());
    }

	@Override
	public void onQuit(CharacterModel character) {
        if (_receipt != null) {
            _receipt.close();
            _receipt = null;
        }
	}

	@Override
	public void					setItem(ItemModel item) {
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

	public static JobCraft create(ItemInfo.ItemInfoAction action, ItemModel item) {
		if (item == null) {
			throw new RuntimeException("Cannot add Craft job (item is null)");
		}

		if (action == null) {
			throw new RuntimeException("Cannot add Craft job (onAction is null)");
		}

		JobCraft job = new JobCraft(action, item.getX(), item.getY());
		job.setItem(item);
		job._mainItem = item;
		job._receipts = action.receipts.stream().map(receiptInfo -> ReceiptModel.createFromReceiptInfo(item, receiptInfo)).collect(Collectors.toList());
        job.setStrategy(j -> {
            if (j.getCharacter().getType().needs.joy != null) {
                j.getCharacter().getNeeds().joy += j.getCharacter().getType().needs.joy.change.work;
            }
        });
        job.onCheck(null);

		item.addJob(job);

        return job;
	}

	@Override
	public boolean onCheck(CharacterModel character) {
        for (ReceiptModel receipt: _receipts) {
            if (receipt.hasComponentsOnMap()) {
                _message = "Waiting";
                return true;
            }
        }
        _message = "Missing components";
        return false;
	}

	@Override
	protected void onFinish() {
	}

	@Override
	public JobActionReturn onAction(CharacterModel character) {
        if (_character == null) {
            Log.error("Action on job with null characters");
        }

		// Wrong call
		if (_item == null || _item != WorldHelper.getItem(_itemPosX, _itemPosY)) {
			Log.error("Character: actionUse on null job or null job's item or invalid item");
			JobModule.getInstance().quitJob(this, JobAbortReason.INVALID);
			return JobActionReturn.ABORT;
		}

		// Move to ingredient
		if (_status == Status.WAITING) {
            moveToIngredient(_character, _receipt.getCurrentOrder());
            return JobActionReturn.CONTINUE;
		}

        // Move to storage
        if (_status == Status.MOVE_TO_STORAGE) {
            return onActionStorage(character);
        }

		// Work on factory
        if (_status == Status.MAIN_ACTION) {
            _progress += character.getTalent(CharacterModel.TalentType.CRAFT).work();
            if (_progress < _cost) {
                _message = _actionInfo.label;
                Log.debug("Character #" + character.getInfo().getName() + ": Crafting (" + _progress + ")");
                return JobActionReturn.CONTINUE;
            }

            // Clear factory
            _mainItem.getComponents().clear();

            // Current item is done
            _progress = 0;
            for (ItemInfo.ItemProductInfo productInfo : _receipt.getProductsInfo()) {
                ConsumableModel productConsumable = new ConsumableModel(productInfo.itemInfo);
                productConsumable.setQuantity(Utils.getRandom(productInfo.quantity));

                // Move to storage
                _storageParcel = ((AreaModule) ModuleManager.getInstance().getModule(AreaModule.class)).getNearestFreeStorageParcel(productConsumable, character.getParcel());
                if (_storageParcel != null) {
                    _storage = (StorageAreaModel)_storageParcel.getArea();
                    character.setInventory(productConsumable);
                    moveToStorage(character, _storageParcel);
                    return JobActionReturn.CONTINUE;
                } else {
                    Game.getWorldManager().putConsumable(productConsumable, character.getX(), character.getY());
                }
            }
            return closeOrQuit(character);
        }

        return JobActionReturn.CONTINUE;
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
        Game.getWorldManager().putConsumable(character.getInventory(), parcel.x, parcel.y);
        character.setInventory(null);

		return closeOrQuit(character);
    }

    private JobActionReturn closeOrQuit(CharacterModel character) {

        // Unload factory
        for (ConsumableModel component: _mainItem.getComponents()) {
            Game.getWorldManager().putConsumable(component, _mainItem.getX(), _mainItem.getY());
        }

        // TODO: wrong location
		// Switch status to MOVE_TO_INGREDIENT
//		_receipt.reset();
//		_receipt = null;
		_status = Status.MOVE_TO_INGREDIENT;

        onCheck(character);

		// Work is complete
		if (_count++ >= _totalCount) {
			//Log.debug("Character #" + characters.getId() + ": work close");
			return JobActionReturn.FINISH;
		}

		// Some remains
		else {
			return JobActionReturn.QUIT;
		}
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
