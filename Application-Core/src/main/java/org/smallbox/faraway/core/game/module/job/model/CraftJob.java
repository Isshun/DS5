package org.smallbox.faraway.core.game.module.job.model;

import org.apache.commons.lang3.NotImplementedException;
import org.smallbox.faraway.core.engine.drawable.AnimDrawable;
import org.smallbox.faraway.core.engine.drawable.IconDrawable;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.game.module.character.model.CharacterTalentExtra;
import org.smallbox.faraway.core.game.module.character.model.base.CharacterModel;
import org.smallbox.faraway.core.game.module.job.model.abs.JobModel;
import org.smallbox.faraway.core.game.module.path.PathManager;
import org.smallbox.faraway.core.game.module.world.model.ConsumableModel;
import org.smallbox.faraway.core.game.module.world.model.ItemSlot;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.core.game.module.world.model.ReceiptGroupInfo;
import org.smallbox.faraway.core.game.module.world.model.item.ItemFactoryModel;
import org.smallbox.faraway.core.game.module.world.model.item.ItemFactoryReceiptModel;
import org.smallbox.faraway.core.game.module.world.model.item.ItemModel;
import org.smallbox.faraway.core.util.Log;
import org.smallbox.faraway.core.util.MoveListener;
import org.smallbox.faraway.core.util.Utils;

import java.util.Optional;

public class CraftJob extends JobModel {
    protected final ItemModel                   _item;
    protected final ItemFactoryModel            _factory;
    protected final ItemFactoryReceiptModel     _receipt;
    protected final ItemFactoryModel.OrderEntry _order;
    protected ItemSlot                          _slot;
    protected double                            _current;

    public CraftJob(ItemModel item) {
        super(null, item.getParcel(), new IconDrawable("data/res/ic_craft.png", 0, 0, 32, 32), new AnimDrawable("data/res/actions.png", 0, 160, 32, 32, 7, 10));
        assert item.getFactory() != null;
        assert item.getFactory().getJob() == null;
        assert item.getFactory().getActiveReceipt() != null;

        _item = item;
        _factory = item.getFactory();
        _factory.setJob(this);
        _receipt = _factory.getActiveReceipt();
        _order = _receipt.order;
        _cost = _order.cost != -1 ? _receipt.order.cost : _receipt.receiptInfo.cost;
        _auto = _order.auto;
        _label = _order.receiptGroupInfo.label;
    }

    @Override
    public boolean isVisible() {
        return _character != null;
    }

    @Override
    public CharacterTalentExtra.TalentType getTalentNeeded() {
        return CharacterTalentExtra.TalentType.CRAFT;
    }

    @Override
    protected void onCreate() {
        setStrategy(j -> {
            if (j.getCharacter() != null && j.getCharacter().getType().needs.joy != null) {
                j.getCharacter().getNeeds().addValue("entertainment", j.getCharacter().getType().needs.joy.change.work);
            }
        });
    }

    @Override
    public JobCheckReturn onCheck(CharacterModel character) {
        if (_factory.getStorageParcel().getConsumable() != null) {
            _message = "Factory is full";
            return JobCheckReturn.STAND_BY;
        }

        if (!_receipt.isComponentsAvailable(this)) {
            _message = "Missing components";
            return JobCheckReturn.ABORT;
        }

        if (character != null && !PathManager.getInstance().hasPath(character.getParcel(), _item.getParcel())) {
            return JobCheckReturn.STAND_BY;
        }

        _message = "Waiting";
        return JobCheckReturn.OK;
    }

    @Override
    protected void onStart(CharacterModel character) {
        for (ItemFactoryReceiptModel.FactoryShoppingItemModel shoppingItem: _receipt.getShoppingList()) {
            if (shoppingItem.consumable.getLock() != null && shoppingItem.consumable.getLock() != this) {
                throw new RuntimeException("Shopping item are already been locked");
            }
        }

        // Lock items from current receipt shopping list
        _receipt.getShoppingList().forEach(shoppingItem -> {if (shoppingItem.consumable.getLock() == this) shoppingItem.consumable.lock(null);});

        // Move character to first receipt component
        if (_receipt.getNextInput() != null) {
            moveToIngredient(character, _receipt.getNextInput());
        } else {
            moveToMainItem();
        }
    }

    @Override
    public JobActionReturn onAction(CharacterModel character) {
        _message = _factory.getMessage();

        // Work on factory
        if (_receipt != null && _receipt.isFull()) {
            _current += character != null ? character.getTalents().get(CharacterTalentExtra.TalentType.CRAFT).work() : 1;
            _progress = _current / _cost;
            _message = "Crafting";
            _factory.setMessage("Crafting");

            if (_current < _cost) {
                Log.debug("Character #" + (character != null ? character.getPersonals().getName() : "auto") + ": Crafting (" + _progress + ")");
                return JobActionReturn.CONTINUE;
            }
        }

        return JobActionReturn.COMPLETE;
    }

    @Override
    public void onQuit(CharacterModel character) {
        if (_receipt != null) {
            _receipt.getShoppingList().forEach(shoppingItem -> {if (shoppingItem.consumable.getLock() == this) shoppingItem.consumable.lock(null);});
        }
    }

    @Override
    protected void onComplete() {
        throw new NotImplementedException("");

//        // Current item is done
//        for (ReceiptGroupInfo.ReceiptOutputInfo productInfo : _receipt.receiptInfo.outputs) {
//            // Put consumables on the ground
//            if (_order.output == ItemInfo.FactoryOutputMode.GROUND) {
//                ParcelModel parcel = _item.getParcel();
//                if (_item.getInfo().factory.outputSlots != null) {
//                    parcel = WorldHelper.getParcel(
//                            _item.getParcel().x + _item.getInfo().factory.outputSlots[0],
//                            _item.getParcel().y + _item.getInfo().factory.outputSlots[1],
//                            _item.getParcel().z);
//                }
//                Log.info("Factory: put crafted consumable on ground");
//                ModuleHelper.getWorldModule().putConsumable(parcel, productInfo.item, Utils.getRandom(productInfo.quantity));
//            }
//
//            // Put consumables on item network
//            if (_order.output == ItemInfo.FactoryOutputMode.NETWORK) {
//                Log.info("Factory: put crafted consumable in network");
//                if (_item.getNetworkConnections() != null) {
//                    _item.getNetworkConnections().stream()
//                            .filter(networkObject -> networkObject.getNetwork() != null && networkObject.getNetwork().accept(productInfo.item))
//                            .forEach(networkObject -> networkObject.getNetwork().addQuantity(Utils.getRandom(productInfo.quantity)));
//                }
//            }
//        }
    }

    @Override
    protected void onFinish() {
        // Unload factory
        _factory.clear();
    }

    protected void moveToIngredient(CharacterModel character, ItemFactoryReceiptModel.FactoryShoppingItemModel input) {
        throw new NotImplementedException("");

//        ConsumableModel consumable = input.consumable;
//        ItemInfo info = consumable.getInfo();
//        _targetParcel = consumable.getParcel();
//        character.moveTo(_targetParcel, new MoveListener<CharacterModel>() {
//            @Override
//            public void onReach(CharacterModel character) {
//                consumable.lock(null);
//
//                int neededQuantity = Math.min(consumable.getQuantity(), input.quantity);
//                if (neededQuantity > 0) {
//                    character.addInventory(consumable, neededQuantity);
//                    if (consumable.getQuantity() == 0) {
//                        ModuleHelper.getWorldModule().removeConsumable(consumable);
//                    } else {
//                        consumable.lock(null);
//                    }
//                }
//
//                // Remove consumable from factory input list
//                _receipt.getShoppingList().remove(input);
//
//                // Move to next input (if same ingredient), or get back to factory
//                Optional<ItemFactoryReceiptModel.FactoryShoppingItemModel> optionalNextInput = _receipt.getShoppingList().stream().filter(i -> i.consumable.getInfo() == info).findFirst();
//                if (optionalNextInput.isPresent() && optionalNextInput.get().quantity + character.getInventoryQuantity() <= info.stack) {
//                    moveToIngredient(character, optionalNextInput.get());
//                } else {
//                    moveToMainItem();
//                }
//
//                _message = "Carry " + consumable.getInfo().label + " to " + _item.getInfo().label;
//            }
//
//            @Override
//            public void onFail(CharacterModel character) {
//                Log.info("CraftJob: character cannot reach factory");
//                quit(character);
//            }
//        });
//        _message = "Move to " + input.consumable.getInfo().label;
    }

    protected void moveToMainItem() {
        // Set target parcel
        _slot = _item.takeSlot(this);
        _targetParcel = _slot != null ? _slot.getParcel() : _item.getParcel();

        // Store component in factory
        _character.moveTo(_targetParcel, new MoveListener<CharacterModel>() {
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

                    if (!_receipt.isFull() && _receipt.getNextInput() != null) {
                        moveToIngredient(character, _receipt.getNextInput());
                    }
                }
            }

            @Override
            public void onFail(CharacterModel character) {
                Log.info("CraftJob: character cannot reach factory");
                quit(character);
            }
        });
    }
}
