package org.smallbox.faraway.model.job;

import org.smallbox.faraway.Game;
import org.smallbox.faraway.OnMoveListener;
import org.smallbox.faraway.engine.util.Log;
import org.smallbox.faraway.manager.JobManager;
import org.smallbox.faraway.manager.ServiceManager;
import org.smallbox.faraway.model.ReceiptModel;
import org.smallbox.faraway.model.character.CharacterModel;
import org.smallbox.faraway.model.item.ConsumableModel;
import org.smallbox.faraway.model.item.MapObjectModel;
import org.smallbox.faraway.model.item.ItemInfo;

import java.util.ArrayList;
import java.util.List;

public class JobCook extends JobModel {
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

    ConsumableModel _targetIngredient;
    ConsumableModel _ingredient;

    @Override
    public ConsumableModel getIngredient() {
        return _ingredient;
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
        return CharacterModel.TalentType.COOK;
    }

    private JobCook(ItemInfo.ItemInfoAction action, int x, int y) {
        super(action, x, y);
    }

    public static JobCook create(ItemInfo.ItemInfoAction action, MapObjectModel item) {
        if (item == null) {
            throw new RuntimeException("Cannot add cook job (item is null)");
        }

        if (action == null) {
            throw new RuntimeException("Cannot add cook job (action is null)");
        }


        JobCook job = new JobCook(action, item.getX(), item.getY());
        job.setItem(item);
        job._receipts = new ArrayList<>();
        for (ItemInfo.ItemInfoReceipt receiptInfo: action.receipts) {
            job._receipts.add(new ReceiptModel(receiptInfo));
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
            return _receipt != null;
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
            if (!_receipt.hasComponents()) {
                findNearestIngredient(character);
                return false;
            }

            // Receipt is complete
            _status = Status.MOVE_TO_COOKER;
            _posX = _item.getX();
            _posY = _item.getY();
            character.moveTo(this, _item.getX(), _item.getY(), new OnMoveListener() {
                @Override
                public void onReach(JobModel job, CharacterModel character) {
                    for (ReceiptModel.ReceiptComponentModel component: _receipt.getComponents()) {
                        character.setInventory(null);
                        _item.addComponent(component.item);
                    }
                }

                @Override
                public void onFail(JobModel job, CharacterModel character) {
                    for (ReceiptModel.ReceiptComponentModel component: _receipt.getComponents()) {
                        character.setInventory(null);
                        Game.getWorldManager().putConsumable(component.item, character.getX(), character.getY());
                    }
                }
            });
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
            if (_progress < _cost) {
                _progress = Math.min(_cost, _progress + character.work(CharacterModel.TalentType.COOK));
                Log.debug("Character #" + character.getName() + ": cooking (" + _progress + ")");
                return false;
            }

            // Current item is done
            _progress = 0;
            for (ItemInfo.ItemProductInfo productInfo: _receipt.receiptInfo.products) {
                ConsumableModel consumable = new ConsumableModel(productInfo.itemInfo);
                consumable.setQuantity(productInfo.quantity);
                character.setInventory(consumable);
                //((ItemModel)_item).addCraft(consumable);
                //ServiceManager.getWorldMap().putObject(itemInfo, _itemPosX, _itemPosY, 0, 100);
            }

            return closeOrQuit(character);
        }

        return false;
    }

    private boolean closeOrQuit(CharacterModel character) {

        // Switch status to MOVE_TO_INGREDIENT
        _targetIngredient = null;
        _ingredient = null;
        _receipt.reset();
        _receipt = null;
        _status = Status.MOVE_TO_INGREDIENT;

        // Work is complete
        if (_count++ >= _totalCount) {
            //Log.debug("Character #" + character.getId() + ": work close");
            JobManager.getInstance().close(this);
            return true;
        }

        // Som remains
        else {
            JobManager.getInstance().quit(this);
            return false;
        }
    }

    private void findNearestIngredient(CharacterModel character) {
        _targetIngredient = null;
        if (_receipt != null) {
            for (ReceiptModel.ReceiptComponentModel component : _receipt.getComponents()) {
                if (component.item.getQuantity() < component.count) {
                    _targetIngredient = ServiceManager.getWorldMap().getFinder().getNearest(component.itemInfo, _item.getX(), _item.getY());
                    if (_targetIngredient != null) {
                        _targetX = _targetIngredient.getX();
                        _targetY = _targetIngredient.getY();
                        _posX = _targetX;
                        _posY = _targetY;
                        _status = Status.MOVE_TO_INGREDIENT;
                        character.moveTo(this, _targetIngredient.getX(), _targetIngredient.getY(), new OnMoveListener() {
                            @Override
                            public void onReach(JobModel job, CharacterModel character) {
                                for (ReceiptModel.ReceiptComponentModel component : _receipt.getComponents()) {
                                    _item.addComponent(component.item);
                                }
                            }

                            @Override
                            public void onFail(JobModel job, CharacterModel character) {
                            }
                        });
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
