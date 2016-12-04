package org.smallbox.faraway.module.item;

import org.apache.commons.lang3.NotImplementedException;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.module.character.model.CharacterTalentExtra;
import org.smallbox.faraway.core.module.character.model.base.CharacterModel;
import org.smallbox.faraway.core.module.job.model.abs.JobModel;
import org.smallbox.faraway.core.module.world.model.ConsumableModel;
import org.smallbox.faraway.module.consumable.ConsumableModule;
import org.smallbox.faraway.module.consumable.HaulJob;
import org.smallbox.faraway.module.item.item.FactoryReceiptModel;
import org.smallbox.faraway.module.item.item.ItemModel;
import org.smallbox.faraway.module.item.item.ItemSlot;
import org.smallbox.faraway.util.Log;
import org.smallbox.faraway.util.MoveListener;
import org.smallbox.faraway.util.Utils;

import java.util.Map;
import java.util.stream.Collectors;

public class CraftJob extends JobModel {
    protected final ItemModel                   _item;
    protected final ItemFactoryModel            _factory;
    protected final FactoryReceiptModel _receipt;
//    protected final ItemFactoryModel.FactoryReceiptGroupModel _order;
    protected ItemSlot                          _slot;
    protected double                            _current;
    private Map<ItemInfo.ActionInputInfo, Integer> _inputs;

    public static CraftJob create(ConsumableModule consumableModule, ItemModel item, ItemInfo.ItemInfoAction action) {
        CraftJob job = new CraftJob(consumableModule, item, action);
        job.setAction(action);
        job.setOnActionListener(() -> {
            if (job.getCharacter() != null && job.getCharacter().getType().needs.joy != null) {
                job.getCharacter().getNeeds().addValue("entertainment", job.getCharacter().getType().needs.joy.change.work);
            }
        });
        return job;
    }

    private CraftJob(ConsumableModule consumableModule, ItemModel item, ItemInfo.ItemInfoAction action) {
        super(action, item.getParcel());
//        super(action, item.getParcel(), new IconDrawable("data/res/ic_craft.png", 0, 0, 32, 32), new AnimDrawable("data/res/actions.png", 0, 160, 32, 32, 7, 10));
        assert item.getFactory() != null;

        _item = item;
        _targetParcel = item.getParcel();
        _factory = item.getFactory();
        _receipt = _factory.getActiveReceipt();

        // Create inputs map and haul jobs
        _inputs = action.inputs.stream()
                .peek(input -> {
                    HaulJob haulJob = HaulJob.create(consumableModule, _item.getParcel(), input.item, input.quantity);
                    haulJob.setOnCompleteListener(() -> {
                        _inputs.put(input, _inputs.get(input) + haulJob.getCurrentQuantity());
                    });
                    addSubJob(haulJob);
                })
                .collect(Collectors.toMap(input -> input, input -> 0));

//        _order = _receipt.receiptGroup;
//        _cost = _order.cost != -1 ? _receipt.receiptGroup.cost : _receipt.receiptInfo.cost;
//        _isAuto = _order.auto;
        _label = action.label;
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
    public JobCheckReturn onCheck(CharacterModel character) {
//        if (_factory.getStorageParcel().getConsumable() != null) {
//            _message = "Factory is full";
//            return JobCheckReturn.STAND_BY;
//        }
//
//        if (!_receipt.isComponentsAvailable(this)) {
//            _message = "Missing components";
//            return JobCheckReturn.ABORT;
//        }
//
//        if (character != null && !Application.pathManager.hasPath(character.getParcel(), _item.getParcel())) {
//            return JobCheckReturn.STAND_BY;
//        }

        _message = "Waiting";
        return JobCheckReturn.OK;
    }

    @Override
    protected void onStart(CharacterModel character) {
//        for (FactoryReceiptModel.FactoryShoppingItemModel shoppingItem: _receipt.getShoppingList()) {
//            if (shoppingItem.consumable.getJob() != null && shoppingItem.consumable.getJob() != this) {
//                throw new RuntimeException("Shopping item are already been locked");
//            }
//        }
//
//        // Lock items from current receipt shopping list
//        _receipt.getShoppingList().forEach(shoppingItem -> {if (shoppingItem.consumable.getJob() == this) shoppingItem.consumable.setJob(null);});
//
//        // Move character to first receipt component
//        if (_receipt.getNextInput() != null) {
//            moveToIngredient(character, _receipt.getNextInput());
//        } else {
//            moveToMainItem();
//        }
    }

    @Override
    public JobActionReturn onAction(CharacterModel character) {
        _message = _factory.getMessage();

        // Work on factory
        // TODO
//        if (_receipt != null && _receipt.isFull()) {
            _current += character != null ? character.getTalents().get(CharacterTalentExtra.TalentType.CRAFT).work() : 1;
            _progress = _current / _cost;
            _message = "Crafting";
            _factory.setMessage("Crafting");

            if (_current < _cost) {
                Log.debug("Character #" + (character != null ? character.getPersonals().getName() : "auto") + ": Crafting (" + _progress + ")");
                return JobActionReturn.CONTINUE;
            }
//        }

        if (_onCompleteListener != null) {
            _onCompleteListener.onComplete();
        }

        return JobActionReturn.COMPLETE;
    }

    @Override
    public void onQuit(CharacterModel character) {
        if (_receipt != null) {
            _receipt.getShoppingList().forEach(shoppingItem -> {if (shoppingItem.consumable.getJob() == this) shoppingItem.consumable.setJob(null);});
        }
    }

    @Override
    protected void onComplete() {
        if (_factory != null) {
            _actionInfo.products.forEach(product -> {
                int quantity = Utils.getRandom(product.quantity);
                for (int i = 0; i < quantity; i++) {
                    _factory.store(new ConsumableModel(product.item));
                }
            });
        }

//        // Current item is done
//        for (ReceiptGroupInfo.ReceiptOutputInfo productInfo : _receipt.receiptInfo.outputs) {
//            // Put consumables on the ground
//            if (_order.outputMode == ItemInfo.FactoryOutputMode.GROUND) {
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
//            if (_order.outputMode == ItemInfo.FactoryOutputMode.NETWORK) {
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
    }

    protected void moveToIngredient(CharacterModel character, FactoryReceiptModel.FactoryShoppingItemModel input) {
        throw new NotImplementedException("");

//        ConsumableModel consumable = input.consumable;
//        ItemInfo info = consumable.getInfo();
//        _targetParcel = consumable.getParcel();
//        character.moveTo(_targetParcel, new MoveListener<CharacterModel>() {
//            @Override
//            public void onReach(CharacterModel character) {
//                consumable.setJob(null);
//
//                int neededQuantity = Math.min(consumable.getQuantity(), input.quantity);
//                if (neededQuantity > 0) {
//                    character.createInventoryFromConsumable(consumable, neededQuantity);
//                    if (consumable.getQuantity() == 0) {
//                        ModuleHelper.getWorldModule().removeConsumable(consumable);
//                    } else {
//                        consumable.setJob(null);
//                    }
//                }
//
//                // Remove consumable from factory input list
//                _receipt.getShoppingList().remove(input);
//
//                // Move to next input (if same ingredient), or get back to factory
//                Optional<FactoryReceiptModel.FactoryShoppingItemModel> optionalNextInput = _receipt.getShoppingList().stream().filter(i -> i.consumable.getInfo() == info).findFirst();
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

    public boolean hasComponents() {
        return false;
    }

    public Map<ItemInfo.ActionInputInfo, Integer> getInputs() {
        return _inputs;
    }
}
