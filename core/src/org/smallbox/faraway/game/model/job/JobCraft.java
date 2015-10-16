package org.smallbox.faraway.game.model.job;

import org.smallbox.faraway.ItemFactory;
import org.smallbox.faraway.core.drawable.AnimDrawable;
import org.smallbox.faraway.core.drawable.IconDrawable;
import org.smallbox.faraway.data.ReceiptInfo;
import org.smallbox.faraway.game.helper.WorldHelper;
import org.smallbox.faraway.game.model.GameData;
import org.smallbox.faraway.game.model.ReceiptModel;
import org.smallbox.faraway.game.model.area.StorageAreaModel;
import org.smallbox.faraway.game.model.character.base.CharacterModel;
import org.smallbox.faraway.game.model.item.*;
import org.smallbox.faraway.game.module.ModuleHelper;
import org.smallbox.faraway.game.module.ModuleManager;
import org.smallbox.faraway.game.module.base.AreaModule;
import org.smallbox.faraway.util.Log;
import org.smallbox.faraway.util.MoveListener;
import org.smallbox.faraway.util.Utils;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class JobCraft extends BaseBuildJobModel {
    private int                     _itemPosX;
    private int                     _itemPosY;
    private ParcelModel             _storageParcel;
    private StorageAreaModel        _storage;
    public ItemModel                _item;
    public ItemFactory              _factory;

    protected JobCraft(ItemInfo.ItemInfoAction actionInfo, ParcelModel jobParcel) {
        super(actionInfo, jobParcel, new IconDrawable("data/res/ic_craft.png", 0, 0, 32, 32), new AnimDrawable("data/res/actions.png", 0, 160, 32, 32, 7, 10));
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

    public static JobCraft create(ItemModel item) {
        if (item == null) {
            throw new RuntimeException("Cannot add Craft job (item is null)");
        }

        JobCraft job = new JobCraft(null, item.getParcel());
        job.setItem(item);
        item.getFactory().setJob(job);
        job._mainItem = item;
        job._item = item;
        job._factory = item.getFactory();
        job._receipts = new ArrayList<>();
        item.getFactory().getReceipts()
                .forEach(receiptEntry -> receiptEntry.receiptInfo.products
                        .forEach(product -> job._receipts.add(ReceiptModel.createFromReceiptInfo(item, product))));
        job.setStrategy(j -> {
            if (j.getCharacter().getType().needs.joy != null) {
                j.getCharacter().getNeeds().joy += j.getCharacter().getType().needs.joy.change.work;
            }
        });
        job.onCheck(null);

        item.getFactory().scan();
        item.addJob(job);

        return job;
    }

    protected void moveToStorage(CharacterModel character, ParcelModel storageParcel) {
        _status = Status.MOVE_TO_STORAGE;
        _targetParcel = storageParcel;

        character.moveTo(this, _targetParcel, null);

        _message = "Move " + _receipt.getProductsInfo().get(0).item.label + " to storage";
    }

    protected void moveToIngredient(CharacterModel character, ReceiptModel.OrderModel order) {
        ParcelModel parcel = order.consumable.getParcel();
        _targetParcel = parcel;
        _status = Status.MOVE_TO_INGREDIENT;
        character.moveTo(this, parcel, new MoveListener<CharacterModel>() {
            @Override
            public void onReach(CharacterModel character) {
                order.consumable.lock(null);
                order.status = ReceiptModel.OrderModel.Status.CARRY;
                character.addInventory(order.consumable, order.quantity);
                if (order.consumable.getQuantity() == 0) {
                    ModuleHelper.getWorldModule().removeConsumable(order.consumable);
                }

                // Get next consumable (same ingredient)
                if (_receipt.getNextOrder() != null && _receipt.getNextOrder().consumable.getInfo() == order.consumable.getInfo()
                        && _receipt.getNextOrder().quantity + _character.getInventory().getQuantity() <= GameData.config.inventoryMaxQuantity) {
                    _receipt.nextOrder();
                    moveToIngredient(character, _receipt.getCurrentOrder());
                } else {
                    moveToMainItem();
                }

                _message = "Carry " + order.consumable.getInfo().label + " to " + _mainItem.getInfo().label;
            }

            @Override
            public void onFail(CharacterModel character) {
            }

            @Override
            public void onSuccess(CharacterModel character) {
            }
        });
        _message = "Move to " + order.consumable.getInfo().label;
    }

    protected void moveToMainItem() {
        _status = Status.MOVE_TO_FACTORY;

        // Set target parcel
        _targetParcel = _mainItem.getParcel();
        if (_item.getSlots() != null) {
            _targetParcel = WorldHelper.getParcel(_item.getSlots().get(0).getX(), _item.getSlots().get(0).getY());
        }

        // Store component in factory
        _character.moveTo(this, _targetParcel, new MoveListener<CharacterModel>() {
            @Override
            public void onReach(CharacterModel character) {
                if (_receipt != null) {
                    if (character.getInventory() != null && _receipt.getCurrentOrder() != null && character.getInventory().getInfo() == _receipt.getCurrentOrder().consumable.getInfo()) {
                        _receipt.closeCarryingOrders();
                        _receipt.nextOrder();
                        _mainItem.addComponent(character.getInventory());
                        character.setInventory(null);
                    }
                    _status = _receipt.isComplete() ? Status.MAIN_ACTION : Status.WAITING;
                }
            }

            @Override
            public void onFail(CharacterModel character) {
            }

            @Override
            public void onSuccess(CharacterModel character) {
            }
        });
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
            ModuleHelper.getJobModule().quitJob(this, JobAbortReason.INVALID);
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
            _factory.scan();

            // Current item is done
            _progress = 0;
            for (ReceiptInfo.ReceiptProductItemInfo productInfo : _receipt.getProductsInfo()) {
                ConsumableModel productConsumable = new ConsumableModel(productInfo.item);
//                productConsumable.setQuantity(Utils.getRandom(productInfo.quantity));
                productConsumable.setQuantity(productInfo.quantity);

                _factory.addProduct(productInfo.item, productInfo.quantity);

                // Move to storage
                _storageParcel = ((AreaModule) ModuleManager.getInstance().getModule(AreaModule.class)).getNearestFreeStorageParcel(productConsumable, character.getParcel());
                if (_storageParcel != null) {
                    _storage = (StorageAreaModel)_storageParcel.getArea();
                    character.setInventory(productConsumable);
                    moveToStorage(character, _storageParcel);
                    return JobActionReturn.CONTINUE;
                } else {
                    ModuleHelper.getWorldModule().putConsumable(productConsumable, character.getX(), character.getY());
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
            ModuleHelper.getWorldModule().putConsumable(character.getInventory(), character.getX(), character.getY());
            character.setInventory(null);
            return closeOrQuit(character);
        }

        // Store inventory item to storage
        ModuleHelper.getWorldModule().putConsumable(character.getInventory(), parcel.x, parcel.y);
        character.setInventory(null);

        return closeOrQuit(character);
    }

    private JobActionReturn closeOrQuit(CharacterModel character) {

        // Unload factory
        for (ItemFactory.ComponentEntry component: _factory.getComponents()) {
            ConsumableModel consumable = new ConsumableModel(component.itemInfo);
            consumable.setQuantity(component.currentQuantity);
            ModuleHelper.getWorldModule().putConsumable(consumable, _item.getX(), _item.getY());
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
        return _actionInfo != null ? _actionInfo.label : "unk craft";
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
