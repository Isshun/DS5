package org.smallbox.faraway.model.job;

import org.smallbox.faraway.engine.util.Log;
import org.smallbox.faraway.manager.JobManager;
import org.smallbox.faraway.manager.ServiceManager;
import org.smallbox.faraway.model.GameData;
import org.smallbox.faraway.model.ReceiptModel;
import org.smallbox.faraway.model.character.CharacterModel;
import org.smallbox.faraway.model.item.ConsumableItem;
import org.smallbox.faraway.model.item.ItemBase;
import org.smallbox.faraway.model.item.ItemInfo;

import java.util.ArrayList;
import java.util.List;

public class JobCook extends BaseJob {
    private List<ReceiptModel>  _receipts;
    private ReceiptModel        _receipt;
    private int                 _itemPosX;
    private int                 _itemPosY;
    private int                 _targetX;
    private int                 _targetY;

    public enum Status {
        MOVE_TO_INGREDIENT, MOVE_TO_COOKER
    }

    Status _status = Status.MOVE_TO_INGREDIENT;

    ConsumableItem _targetIngredient;
    ConsumableItem _ingredient;

    @Override
    public ConsumableItem getIngredient() {
        return _ingredient;
    }

    @Override
    public void					setItem(ItemBase item) {
        super.setItem(item);

        if (item != null) {
            _itemPosX = item.getX();
            _itemPosY = item.getY();
        }
    }

    @Override
    public CharacterModel.TalentType getTalentNeeded() {
        return CharacterModel.TalentType.COOK;
    }

    private JobCook(ItemInfo.ItemInfoAction action, int x, int y) {
        super(action, x, y);
    }

    public static JobCook create(ItemInfo.ItemInfoAction action, ItemBase item) {
        if (item == null) {
            throw new RuntimeException("Cannot add cook job (item is null)");
        }

        if (action == null) {
            throw new RuntimeException("Cannot add cook job (action is null)");
        }


        JobCook job = new JobCook(action, item.getX(), item.getY());
        job.setItem(item);
        job._receipts = new ArrayList<>();
        for (ItemInfo itemInfo: action.productsItem) {
            for (String r: itemInfo.receipts) {
                ReceiptModel receipt = new ReceiptModel();
                receipt.addReceiptComponent(GameData.getData().getItemInfo(r), 5);
                job._receipts.add(receipt);
            }
        }

        item.addJob(job);

        return job;
    }

    @Override
    public boolean check(CharacterModel character) {
        if (_receipt == null) {
            findWorkableReceipt(character);
        }

        return _receipt != null;
    }

    private void findWorkableReceipt(CharacterModel character) {
        _status = Status.MOVE_TO_INGREDIENT;
        _receipt = null;
        for (ReceiptModel receipt: _receipts) {
            boolean hasComponentOnMap = true;
            for (ReceiptModel.ReceiptComponentModel component: receipt.getComponents()) {
                if (component.item.getQuantity() < component.count && ServiceManager.getWorldMap().getConsumableCount(component.itemInfo) < component.count) {
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
    public boolean action(CharacterModel character) {
        // Wrong call
        if (_item == null || _item != ServiceManager.getWorldMap().getItem(_itemPosX, _itemPosY)) {
            Log.error("Character: actionUse on null job or null job's item or invalid item");
            JobManager.getInstance().quit(this, JobAbortReason.INVALID);
            return false;
        }

        if (_receipt == null) {
            findWorkableReceipt(character);
            return false;
        }

        // Move to ingredient
        if (_status == Status.MOVE_TO_INGREDIENT) {
            //if (character.getX() != _targetX || character.getY() != _targetY)

            if (_targetIngredient == null) {
                findNearestIngredient(character);
                if (_targetIngredient == null) {
                    JobManager.getInstance().quit(this, JobAbortReason.INVALID);
                    return false;
                }
            }

            // Ingredient no longer exists
            if (_targetIngredient != ServiceManager.getWorldMap().getConsumable(_targetX, _targetY)) {
                _targetIngredient = null;
                _status = Status.MOVE_TO_INGREDIENT;
                return false;
            }

            // Move ingredient to cooker
            ReceiptModel.ReceiptComponentModel component = _receipt.getComponent(_targetIngredient.getInfo());
            while (_targetIngredient.getQuantity() > 0 && component.item.getQuantity() < component.count) {
                _targetIngredient.addQuantity(-1);
                component.item.addQuantity(1);
            }
            if (_targetIngredient.getQuantity() <= 0) {
                _targetIngredient.getArea().setConsumable(null);
            }

            // Components still missing
            if (!_receipt.isComplete()) {
                findNearestIngredient(character);
                return false;
            }

            // Receipt is complete
            _status = Status.MOVE_TO_COOKER;
            _posX = _item.getX();
            _posY = _item.getY();
            character.moveTo(this, _item.getX(), _item.getY());
        }

        // Work on cooker
        else {

//            if (_ingredient == null && character.hasInInventory(_targetIngredient)) {
//                character.removeInventory(_targetIngredient);
//                _ingredient = _targetIngredient;
//                _targetIngredient = null;
//                _item.getArea().setConsumable(_ingredient);
//            }

            // Work continue
            if (_cost < _totalCost) {
                _cost = Math.min(_totalCost, _cost + character.work(CharacterModel.TalentType.COOK));
                Log.debug("Character #" + character.getId() + ": working");
                return false;
            }

            // Switch status to MOVE_TO_INGREDIENT
            _targetIngredient = null;
            _ingredient = null;
            _receipt.reset();
            _receipt = null;
            _status = Status.MOVE_TO_INGREDIENT;

            // Current item is done but some remains
            _cost = 0;
            for (ItemInfo itemInfo: _actionInfo.productsItem) {
                ServiceManager.getWorldMap().putItem(itemInfo, _itemPosX, _itemPosY, 0, 100);
            }

            JobManager.getInstance().quit(this);

            // Work is complete
            if (_count++ >= _totalCount) {
                Log.debug("Character #" + character.getId() + ": work close");
                JobManager.getInstance().close(this);
                return true;
            }
        }

        return false;
    }

    private void findNearestIngredient(CharacterModel character) {
        _targetIngredient = null;
        if (_receipt != null) {
            for (ReceiptModel.ReceiptComponentModel component : _receipt.getComponents()) {
                if (component.item.getQuantity() < component.count) {
                    _targetIngredient = ServiceManager.getWorldMap().getFinder().getNearest(component.itemInfo, _item.getX(), _item.getY());
                    _targetX = _targetIngredient.getX();
                    _targetY = _targetIngredient.getY();
                    _posX = _targetX;
                    _posY = _targetY;
                    if (_targetIngredient != null) {
                        _status = Status.MOVE_TO_INGREDIENT;
                        character.moveTo(this, _targetIngredient.getX(), _targetIngredient.getY());
                        return;
                    }
                }
            }
        }
    }

    @Override
    public String getType() {
        return "craft";
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
