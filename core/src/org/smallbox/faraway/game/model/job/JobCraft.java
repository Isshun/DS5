package org.smallbox.faraway.game.model.job;

import org.smallbox.faraway.ItemFactoryModel;
import org.smallbox.faraway.core.drawable.AnimDrawable;
import org.smallbox.faraway.core.drawable.IconDrawable;
import org.smallbox.faraway.data.ReceiptGroupInfo;
import org.smallbox.faraway.game.helper.WorldHelper;
import org.smallbox.faraway.game.model.OldReceiptModel;
import org.smallbox.faraway.game.model.area.StorageAreaModel;
import org.smallbox.faraway.game.model.character.base.CharacterModel;
import org.smallbox.faraway.game.model.item.*;
import org.smallbox.faraway.game.module.ModuleHelper;
import org.smallbox.faraway.game.module.ModuleManager;
import org.smallbox.faraway.game.module.base.AreaModule;
import org.smallbox.faraway.util.Log;
import org.smallbox.faraway.util.MoveListener;

import java.util.ArrayList;
import java.util.Optional;

public class JobCraft extends BaseBuildJobModel {
    private int                     _itemPosX;
    private int                     _itemPosY;
    private ParcelModel             _storageParcel;
    private StorageAreaModel        _storage;
    public ItemModel                _item;
    public ItemFactoryModel _factory;

    protected JobCraft(ItemInfo.ItemInfoAction actionInfo, ParcelModel jobParcel) {
        super(actionInfo, jobParcel, new IconDrawable("data/res/ic_craft.png", 0, 0, 32, 32), new AnimDrawable("data/res/actions.png", 0, 160, 32, 32, 7, 10));
    }

    @Override
    protected void onStart(CharacterModel character) {
//        int bestDistance = Integer.MAX_VALUE;
//        for (OldReceiptModel receipt: _receipts) {
//            receipt.reset();
//            if (bestDistance > receipt.getTotalDistance() && receipt.hasComponentsOnMap()) {
//                bestDistance = receipt.getTotalDistance();
//                _receipt = receipt;
//            }
//        }

        _receipt = _factory.getCurrentReceipt();

        if (_receipt == null) {
            throw new RuntimeException("Try to start JobCraft but no receipt have enough component");
        }

        // Start receipt and get first component
//        _receipt.start(this);
        moveToIngredient(character, _factory.getNextInput());
    }

    @Override
    public void onQuit(CharacterModel character) {
        if (_receipt != null) {
//            _receipt.close();
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
        item.getFactory().scan();
        item.getFactory().getReceipts().forEach(product -> job._receipts.add(OldReceiptModel.createFromReceiptInfo(item, product.receiptInfo)));
        job.setStrategy(j -> {
            if (j.getCharacter().getType().needs.joy != null) {
                j.getCharacter().getNeeds().joy += j.getCharacter().getType().needs.joy.change.work;
            }
        });
        job.onCheck(null);

        item.addJob(job);

        return job;
    }

    protected void moveToStorage(CharacterModel character, ParcelModel storageParcel) {
        _status = Status.MOVE_TO_STORAGE;
        _targetParcel = storageParcel;

        character.moveTo(this, _targetParcel, null);

//        _message = "Move " + _receipt.getProductsInfo().get(0).item.label + " to storage";
    }

    protected void moveToIngredient(CharacterModel character, ItemFactoryModel.FactoryInputModel factoryInput) {
        ParcelModel parcel = factoryInput.consumable.getParcel();
        factoryInput.consumable.lock(this);
        _targetParcel = parcel;
        _status = Status.MOVE_TO_INGREDIENT;
        character.moveTo(this, parcel, new MoveListener<CharacterModel>() {
            @Override
            public void onReach(CharacterModel character) {
                factoryInput.consumable.lock(null);
//                order.status = OldReceiptModel.OrderModel.Status.CARRY;

                int neededQuantity = Math.min(factoryInput.consumable.getQuantity(), factoryInput.quantity);
                if (neededQuantity > 0) {
                    character.addInventory(factoryInput.consumable, neededQuantity);
                    if (factoryInput.consumable.getQuantity() == 0) {
                        ModuleHelper.getWorldModule().removeConsumable(factoryInput.consumable);
                    }
                }

                // Remove consumable from factory input list
                _factory.getComponents().remove(factoryInput);

//                // Get next consumable (same ingredient)
//                if (_receipt.getNextOrder() != null && _receipt.getNextOrder().consumable.getInfo() == order.consumable.getInfo()
//                        && _receipt.getNextOrder().quantity + _character.getInventory().getQuantity() <= GameData.config.inventoryMaxQuantity) {
//                    _receipt.nextOrder();
//                    moveToIngredient(character, _receipt.getNextInput());
//                } else {
                    moveToMainItem();
//                }

                _message = "Carry " + factoryInput.consumable.getInfo().label + " to " + _mainItem.getInfo().label;
            }

            @Override
            public void onFail(CharacterModel character) {
            }

            @Override
            public void onSuccess(CharacterModel character) {
            }
        });
        _message = "Move to " + factoryInput.consumable.getInfo().label;
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
                if (_factory != null) {
                    if (character.getInventory() != null && _factory.getNextInput() != null && character.getInventory().getInfo() == _factory.getNextInput().consumable.getInfo()) {
                        _factory.addInput(character.getInventory().getInfo(), character.getInventory().getQuantity());
                        _factory.getNextInput();
//                        _mainItem.addComponent(character.getInventory());
                        character.setInventory(null);
                    }
                    _status = _factory.getCurrentReceipt() != null && _factory.getNextInput() == null ? Status.MAIN_ACTION : Status.WAITING;
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
        if (_factory.getCurrentReceipt() == null) {
            _factory.scan();
        }

//        for (OldReceiptModel receipt: _receipts) {
//            if (receipt.hasComponentsOnMap()) {
//                _message = "Waiting";
//                return true;
//            }
//        }

        if (_factory.getCurrentReceipt() != null) {
            _message = "Waiting";
            return true;
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
            moveToIngredient(_character, _factory.getNextInput());
            return JobActionReturn.CONTINUE;
        }

        // Move to storage
        if (_status == Status.MOVE_TO_STORAGE) {
            return onActionStorage(character);
        }

        // Work on factory
        if (_status == Status.MAIN_ACTION) {
            _progress += character.getTalent(CharacterModel.TalentType.CRAFT).work();
            _factory.craft();
            if (_progress < _cost) {
                _message = _actionInfo.label;
                Log.debug("Character #" + character.getInfo().getName() + ": Crafting (" + _progress + ")");
                return JobActionReturn.CONTINUE;
            }

            // Clear factory
            _factory.scan();

            // Current item is done
            _progress = 0;
            for (ReceiptGroupInfo.ReceiptOutputInfo productInfo : _receipt.receiptInfo.products) {
                ConsumableModel productConsumable = new ConsumableModel(productInfo.item);
//                productConsumable.setQuantity(Utils.getRandom(productInfo.quantity));
                productConsumable.setQuantity(productInfo.quantity);

//                _factory.addProduct(productInfo.item, productInfo.quantity);

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
        // TODO: non sense
        for (ItemFactoryModel.FactoryInputModel component: _factory.getComponents()) {
//            ConsumableModel consumable = new ConsumableModel(component.itemInfo);
//            consumable.setQuantity(component.currentQuantity);
            ConsumableModel consumable = component.consumable;
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
            _item.getFactory().setJob(null);
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
