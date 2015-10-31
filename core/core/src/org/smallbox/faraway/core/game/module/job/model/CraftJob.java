package org.smallbox.faraway.core.game.module.job.model;

import org.smallbox.faraway.core.data.ItemInfo;
import org.smallbox.faraway.core.engine.drawable.AnimDrawable;
import org.smallbox.faraway.core.engine.drawable.IconDrawable;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.module.area.model.StorageAreaModel;
import org.smallbox.faraway.core.game.module.character.model.base.CharacterModel;
import org.smallbox.faraway.core.game.module.job.model.abs.JobModel;
import org.smallbox.faraway.core.game.module.world.model.*;
import org.smallbox.faraway.core.game.module.world.model.item.ItemFactoryModel;
import org.smallbox.faraway.core.game.module.world.model.item.ItemFactoryReceiptModel;
import org.smallbox.faraway.core.game.module.world.model.item.ItemModel;
import org.smallbox.faraway.core.module.java.ModuleHelper;
import org.smallbox.faraway.core.util.Log;
import org.smallbox.faraway.core.util.MoveListener;

import java.util.Optional;

public class CraftJob extends JobModel {
    protected int                       _itemPosX;
    protected int                       _itemPosY;
    protected StorageAreaModel          _storage;
    protected ItemModel _item;
    protected ItemFactoryModel _factory;
    protected double                    _current;
    protected Status                    _status;
    protected ItemFactoryReceiptModel _receipt;

    public enum Status {
        WAITING, MAIN_ACTION, MOVE_TO_INGREDIENT, MOVE_TO_FACTORY, MOVE_TO_STORAGE
    }

    public CraftJob(ItemModel item) {
        super(null, item.getParcel(), new IconDrawable("data/res/ic_craft.png", 0, 0, 32, 32), new AnimDrawable("data/res/actions.png", 0, 160, 32, 32, 7, 10));
        _item = item;
    }

    @Override
    protected void onCreate() {
        _factory = _item.getFactory();
        _factory.setJob(this);
        _factory.scan();

        setStrategy(j -> {
            if (j.getCharacter().getType().needs.joy != null) {
                j.getCharacter().getNeeds().joy += j.getCharacter().getType().needs.joy.change.work;
            }
        });
    }

    @Override
    protected void onStart(CharacterModel character) {
        _receipt = _factory.getActiveReceipt();

        if (_receipt == null) {
            throw new RuntimeException("Try to start CraftJob without active receipt");
        }

        for (ItemFactoryReceiptModel.FactoryShoppingItemModel shoppingItem: _receipt.getShoppingList()) {
            if (shoppingItem.consumable.getLock() != null && shoppingItem.consumable.getLock() != this) {
                throw new RuntimeException("Shopping item are already been locked");
            }
        }

        // Lock items from current receipt shopping list
        _receipt.getShoppingList().forEach(shoppingItem -> {if (shoppingItem.consumable.getLock() == this) shoppingItem.consumable.lock(null);});

        _cost = _receipt.receiptInfo.cost;

        // Move character to first receipt component
        moveToIngredient(character, _receipt.getNextInput());
    }

    @Override
    public void onQuit(CharacterModel character) {
        if (_receipt != null) {
            _receipt.getShoppingList().forEach(shoppingItem -> {if (shoppingItem.consumable.getLock() == this) shoppingItem.consumable.lock(null);});
        }
    }

    @Override
    protected void onFinish() {
        // Unload factory
        _factory.clear();
    }

    @Override
    public void onDraw(onDrawCallback callback) {
        callback.onDraw(_item.getX(), _item.getY());
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

    @Override
    public boolean onCheck(CharacterModel character) {
        if (_factory.getActiveReceipt() == null) {
            _factory.scan();
        }

        if (_factory != null && _factory.getStorageParcel().getConsumable() != null) {
            _message = "Factory is full";
            return false;
        }

        if (_factory.getActiveReceipt() == null) {
            _message = "Missing components";
            return false;
        }

        _message = "Waiting";
        return true;
    }

    @Override
    public JobActionReturn onAction(CharacterModel character) {
        _message = _factory.getMessage();

        if (_character == null) {
            Log.error("Action on job with null characters");
        }

        // Move to ingredient
        if (_status == Status.WAITING) {
            moveToIngredient(_character, _receipt.getNextInput());
            return JobActionReturn.CONTINUE;
        }

//        // Move to storage
//        if (_status == Status.MOVE_TO_STORAGE) {
//            return onActionStorage(character);
//        }

        // Work on factory
        if (_status == Status.MAIN_ACTION) {
            _current += character.getTalent(CharacterModel.TalentType.CRAFT).work();;
            _progress = _current / _cost;
            _factory.setMessage("Crafting");

            if (_current < _cost) {
                Log.debug("Character #" + character.getInfo().getName() + ": Crafting (" + _progress + ")");
                return JobActionReturn.CONTINUE;
            }

            _factory.craft();

            return JobActionReturn.FINISH;
        }

        return JobActionReturn.CONTINUE;
    }

//    private JobActionReturn onActionStorage(CharacterModel character) {
//        if (_storage == null) {
//            return closeOrQuit(character);
//        }
//
//        ParcelModel parcel = _storage.getNearestFreeParcel(character.getInventory(), character.getParcel());
//        if (parcel == null) {
//            Log.warning("No free space in _storage model");
//            ModuleHelper.getWorldModule().putConsumable(character.getInventory(), character.getX(), character.getY());
//            character.setInventory(null);
//            return closeOrQuit(character);
//        }
//
//        // Store inventory item to storage
//        ModuleHelper.getWorldModule().putConsumable(character.getInventory(), parcel.x, parcel.y);
//        character.setInventory(null);
//
//        return closeOrQuit(character);
//    }

    protected void moveToIngredient(CharacterModel character, ItemFactoryReceiptModel.FactoryShoppingItemModel input) {
        ItemInfo info = input.consumable.getInfo();
        ParcelModel parcel = input.consumable.getParcel();
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
                    } else {
                        input.consumable.lock(null);
                    }
                }

                // Remove consumable from factory input list
                _receipt.getShoppingList().remove(input);

                // Move to next input (if same ingredient), or get back to factory
                Optional<ItemFactoryReceiptModel.FactoryShoppingItemModel> optionalNextInput = _receipt.getShoppingList().stream().filter(i -> i.consumable.getInfo() == info).findFirst();
                if (optionalNextInput.isPresent() && optionalNextInput.get().quantity + character.getInventory().getQuantity() <= info.stack) {
                    moveToIngredient(character, optionalNextInput.get());
                } else {
                    moveToMainItem();
                }

                _message = "Carry " + input.consumable.getInfo().label + " to " + _item.getInfo().label;
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
        _targetParcel = _item.getParcel();
        if (_item.getSlots() != null) {
            _targetParcel = WorldHelper.getParcel(_item.getSlots().get(0).getX(), _item.getSlots().get(0).getY());
        }

        // Store component in factory
        _character.moveTo(this, _targetParcel, new MoveListener<CharacterModel>() {
            @Override
            public void onReach(CharacterModel character) {
                if (_receipt != null) {
                    if (character.getInventory() != null) {
                        int quantityNeeded = Math.min(character.getInventory().getQuantity(), _receipt.getQuantityNeeded(character.getInventory().getInfo()));
                        if (quantityNeeded > 0) {
                            // Add components to factory
                            _receipt.addComponent(character.getInventory().getInfo(), quantityNeeded);

                            // Remove components from character's inventory
                            if (character.getInventory().getQuantity() > quantityNeeded) {
                                character.getInventory().addQuantity(-quantityNeeded);
                            } else {
                                character.setInventory(null);
                            }
                        }
                    }

                    if (_receipt.getNextInput() != null) {
                        _status = Status.MOVE_TO_INGREDIENT;
                        moveToIngredient(character, _receipt.getNextInput());
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
    public String getLabel() {
        return _factory != null && _factory.getMessage() != null ? _factory.getMessage() : "unk craft";
    }

    @Override
    public String getShortLabel() {
        return "work";
    }

    @Override
    public ParcelModel getActionParcel() {
        return _item.getParcel();
    }

    @Override
    public String getMessage() {
        return _message;
    }

}
