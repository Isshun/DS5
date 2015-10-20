package org.smallbox.faraway.core.game.module.job.model;

import org.smallbox.faraway.core.game.module.world.model.ItemFactoryModel;
import org.smallbox.faraway.core.engine.drawable.AnimDrawable;
import org.smallbox.faraway.core.engine.drawable.IconDrawable;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.model.OldReceiptModel;
import org.smallbox.faraway.core.game.module.area.model.StorageAreaModel;
import org.smallbox.faraway.core.game.module.character.model.base.CharacterModel;
import org.smallbox.faraway.core.game.module.world.model.ConsumableModel;
import org.smallbox.faraway.core.game.module.world.model.ItemInfo;
import org.smallbox.faraway.core.game.module.world.model.ItemModel;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.core.game.module.job.model.abs.BaseBuildJobModel;
import org.smallbox.faraway.core.module.java.ModuleHelper;
import org.smallbox.faraway.core.util.Log;
import org.smallbox.faraway.core.util.MoveListener;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

public class CraftJob extends BaseBuildJobModel {
    private int                     _itemPosX;
    private int                     _itemPosY;
    private ParcelModel             _storageParcel;
    private StorageAreaModel        _storage;
    public ItemModel                _item;
    public ItemFactoryModel         _factory;
    private double                  _current;

    protected CraftJob(ItemInfo.ItemInfoAction actionInfo, ParcelModel jobParcel) {
        super(actionInfo, jobParcel, new IconDrawable("data/res/ic_craft.png", 0, 0, 32, 32), new AnimDrawable("data/res/actions.png", 0, 160, 32, 32, 7, 10));
    }

    @Override
    protected void onStart(CharacterModel character) {
        _receipt = _factory.getCurrentReceipt();
        _cost = _receipt.receiptInfo.cost;

        if (_receipt == null) {
            throw new RuntimeException("Try to start CraftJob but no receipt have enough component");
        }

        // Start receipt and get first component
//        _receipt.start(this);
        moveToIngredient(character, _factory.getNextInput());
    }

    @Override
    public void onQuit(CharacterModel character) {
        if (_receipt != null) {
            _receipt = null;
        }
    }

    @Override
    public void                    setItem(ItemModel item) {
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

    public static CraftJob create(ItemModel item) {
        if (item == null) {
            throw new RuntimeException("Cannot add Craft job (item is null)");
        }

        CraftJob job = new CraftJob(null, item.getParcel());
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

    protected void moveToIngredient(CharacterModel character, ItemFactoryModel.FactoryShoppingItemModel input) {
        ItemInfo info = input.consumable.getInfo();
        ParcelModel parcel = input.consumable.getParcel();
        input.consumable.lock(this);
        _targetParcel = parcel;
        _status = Status.MOVE_TO_INGREDIENT;
        character.moveTo(this, parcel, new MoveListener<CharacterModel>() {
            @Override
            public void onReach(CharacterModel character) {
                input.consumable.lock(null);
//                order.status = OldReceiptModel.OrderModel.Status.CARRY;

                int neededQuantity = Math.min(input.consumable.getQuantity(), input.quantity);
                if (neededQuantity > 0) {
                    character.addInventory(input.consumable, neededQuantity);
                    if (input.consumable.getQuantity() == 0) {
                        ModuleHelper.getWorldModule().removeConsumable(input.consumable);
                    }
                }

                // Remove consumable from factory input list
                _factory.getShoppingList().remove(input);

                // Move to next input (if same ingredient), or get back to factory
                Optional<ItemFactoryModel.FactoryShoppingItemModel> optionalNextInput = _factory.getShoppingList().stream().filter(i -> i.consumable.getInfo() == info).findFirst();
                if (optionalNextInput.isPresent() && optionalNextInput.get().quantity + character.getInventory().getQuantity() <= info.stack) {
                    moveToIngredient(character, optionalNextInput.get());
                } else {
                    moveToMainItem();
                }

                _message = "Carry " + input.consumable.getInfo().label + " to " + _mainItem.getInfo().label;
            }

            @Override
            public void onFail(CharacterModel character) {
            }

            @Override
            public void onSuccess(CharacterModel character) {
            }
        });
        _message = "Move to " + input.consumable.getInfo().label;
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
                    if (character.getInventory() != null) {
                        int quantityNeeded = Math.min(character.getInventory().getQuantity(), _factory.getQuantityNeeded(character.getInventory().getInfo()));
                        if (quantityNeeded > 0) {
                            // Add components to factory
                            _factory.addComponent(character.getInventory().getInfo(), quantityNeeded);

                            // Remove components from character's inventory
                            if (character.getInventory().getQuantity() > quantityNeeded) {
                                character.getInventory().addQuantity(-quantityNeeded);
                            } else {
                                character.setInventory(null);
                            }
                        }
                    }

                    if (_factory.getNextInput() != null) {
                        _status = Status.MOVE_TO_INGREDIENT;
                        moveToIngredient(character, _factory.getNextInput());
                    } else {
                        _status = Status.MAIN_ACTION;
                    }
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

        if (_factory != null && _factory.getStorageParcel().getConsumable() != null) {
            _message = "Factory is full";
            return false;
        }

        if (_factory.getCurrentReceipt() == null) {
            _message = "Missing components";
            return false;
        }

        _message = "Waiting";
        return true;
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
            _current += character.getTalent(CharacterModel.TalentType.CRAFT).work();;
            _progress = _current / _cost;

            if (_current < _cost) {
                _message = _receipt.receiptInfo.label;
                Log.debug("Character #" + character.getInfo().getName() + ": Crafting (" + _progress + ")");
                return JobActionReturn.CONTINUE;
            }

            _factory.craft();

            // Clear factory
            _factory.clear();

            return closeOrQuit(character);
        }

        return JobActionReturn.CONTINUE;
    }

    private JobActionReturn onActionStorage(CharacterModel character) {
        if (_storage == null) {
            return closeOrQuit(character);
        }

        ParcelModel parcel = _storage.getNearestFreeParcel(character.getInventory(), character.getParcel());
        if (parcel == null) {
            Log.warning("No free space in _storage model");
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
        for (ItemFactoryModel.FactoryShoppingItemModel component: _factory.getShoppingList()) {
//            ConsumableModel consumable = new ConsumableModel(component.itemInfo);
//            consumable.setQuantity(component.currentQuantity);
            ConsumableModel consumable = component.consumable;
            ModuleHelper.getWorldModule().putConsumable(consumable, _item.getX(), _item.getY());
        }

        // TODO: wrong location
        // Switch status to MOVE_TO_INGREDIENT
//        _receipt.reset();
//        _receipt = null;
        _status = Status.MOVE_TO_INGREDIENT;

//        onCheck(character);

        return JobActionReturn.FINISH;

//        // Work is complete
//        if (_current >= _cost) {
//            //Log.debug("Character #" + characters.getId() + ": work close");
//            _item.getFactory().setJob(null);
//            return JobActionReturn.FINISH;
//        }
//
//        // Some remains
//        else {
//            return JobActionReturn.QUIT;
//        }
    }

    @Override
    public String getLabel() {
        return _factory != null && _factory.getMessage() != null ? _factory.getMessage() : "unk craft";
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
