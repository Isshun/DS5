package org.smallbox.faraway.game.model.job;

import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.OnMoveListener;
import org.smallbox.faraway.game.model.GameData;
import org.smallbox.faraway.game.model.MovableModel;
import org.smallbox.faraway.game.model.ReceiptModel;
import org.smallbox.faraway.game.model.character.base.CharacterModel;
import org.smallbox.faraway.game.model.item.ItemInfo;
import org.smallbox.faraway.game.model.item.ItemModel;
import org.smallbox.faraway.game.model.item.MapObjectModel;
import org.smallbox.faraway.game.model.item.ParcelModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 11/07/2015.
 */
public abstract class BaseBuildJobModel extends BaseJobModel {
    protected List<ReceiptModel>    _receipts = new ArrayList<>();
    protected ReceiptModel          _receipt;
    protected Status                _status;
    public MapObjectModel           _mainItem;

    public BaseBuildJobModel(ItemInfo.ItemInfoAction actionInfo, int x, int y, String iconPath, String iconActionPath) {
        super(actionInfo, x, y, iconPath, iconActionPath);
    }

    public enum Status {
        WAITING, MAIN_ACTION, MOVE_TO_INGREDIENT, MOVE_TO_FACTORY, MOVE_TO_STORAGE
    }

    protected void moveToIngredient(CharacterModel character, ReceiptModel.OrderModel order) {
        ParcelModel parcel = order.consumable.getParcel();
        _posX = parcel.x;
        _posY = parcel.y;
        _status = Status.MOVE_TO_INGREDIENT;
        character.moveTo(this, parcel, new OnMoveListener() {
            @Override
            public void onReach(BaseJobModel job, MovableModel movable) {
                order.consumable.lock(null);
                order.status = ReceiptModel.OrderModel.Status.CARRY;
                character.addInventory(order.consumable, order.quantity);
                if (order.consumable.getQuantity() == 0) {
                    Game.getWorldManager().removeConsumable(order.consumable);
                }

                // Get next consumable (same ingredient)
                if (_receipt.getNextOrder() != null && _receipt.getNextOrder().consumable.getInfo() == order.consumable.getInfo()
                        && _receipt.getNextOrder().quantity + _character.getInventory().getQuantity() <= GameData.config.inventoryMaxQuantity) {
                    _receipt.nextOrder();
                    moveToIngredient(character, _receipt.getCurrentOrder());
                } else {
                    moveToMainItem();
                }
            }

            @Override
            public void onFail(BaseJobModel job, MovableModel movable) {
            }

            @Override
            public void onSuccess(BaseJobModel job, MovableModel movable) {
            }
        });
        _message = "Move to " + order.consumable.getInfo().label;
    }

    protected void moveToMainItem() {
        _status = Status.MOVE_TO_FACTORY;
        _posX = _mainItem.getX();
        _posY = _mainItem.getY();
        _message = "Carry " + _character.getInventory().getInfo().label + " to " + _mainItem.getInfo().label;

        // Store component in factory
        _character.moveTo(this, _mainItem.getParcel(), new OnMoveListener<CharacterModel>() {
            @Override
            public void onReach(BaseJobModel job, CharacterModel character) {
                _receipt.closeCarryingOrders();
                _receipt.nextOrder();
                _mainItem.addComponent(character.getInventory());
                character.setInventory(null);
                _status = _receipt.isComplete() ? Status.MAIN_ACTION : Status.WAITING;
            }

            @Override
            public void onFail(BaseJobModel job, CharacterModel character) {
            }

            @Override
            public void onSuccess(BaseJobModel job, CharacterModel character) {
            }
        });
    }

    protected void moveToStorage(CharacterModel character, ParcelModel storageParcel) {
        _status = Status.MOVE_TO_STORAGE;
        _posX = storageParcel.x;
        _posY = storageParcel.y;

        character.moveTo(this, _posX, _posY, null);

        _message = "Move " + _receipt.getProductsInfo().get(0).itemInfo.label + " to storage";
    }
}
