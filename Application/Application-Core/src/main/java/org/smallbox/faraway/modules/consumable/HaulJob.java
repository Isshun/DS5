package org.smallbox.faraway.modules.consumable;

import org.apache.commons.lang3.NotImplementedException;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.module.character.model.CharacterTalentExtra;
import org.smallbox.faraway.core.module.character.model.PathModel;
import org.smallbox.faraway.core.module.character.model.base.CharacterModel;
import org.smallbox.faraway.core.module.job.model.abs.JobModel;
import org.smallbox.faraway.core.module.world.model.BuildableMapObject;
import org.smallbox.faraway.core.module.world.model.ConsumableItem;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.util.Log;
import org.smallbox.faraway.util.MoveListener;
import org.smallbox.faraway.util.Utils;

import java.util.Collections;
import java.util.List;

/**
 * Created by Alex on 09/10/2015.
 */
public class HaulJob extends JobModel {
    private final ItemInfo _itemInfo;

    public static HaulJob create(ConsumableModule consumableModule, ParcelModel targetParcel, ItemInfo item, int quantity) {
        return new HaulJob(consumableModule, targetParcel, item, quantity);
    }

    public HaulJob(ConsumableModule consumableModule, ParcelModel targetParcel, ItemInfo itemInfo, int quantity) {
        _quantity = quantity;
        _targetParcel = targetParcel;
        _itemInfo = itemInfo;
        _label = "Haul " + itemInfo.label + " to " + targetParcel.x + "x" + targetParcel.y;
        _message = "Haul " + itemInfo.label + " to " + targetParcel.x + "x" + targetParcel.y;
        _consumableModule = consumableModule;
        _consumable = consumableModule.find(itemInfo);
        _consumable.setJob(this);
    }

    public boolean isActive() {
        return _consumable != null;
    }

    public boolean isComplete() {
        return false;
    }

    public ConsumableItem getConsumable() {
        return _consumable;
    }

    public static class PotentialConsumable {
        public ConsumableItem consumable;
        public int              distance;

        public PotentialConsumable(ConsumableItem consumable, int distance) {
            this.consumable = consumable;
            this.distance = distance;
        }
    }

    private final ConsumableModule _consumableModule;
    private int _currentQuantity;
    private ConsumableItem _consumable;
    private int                                     _quantity;
    private BuildableMapObject                      _buildItem;
    private BuildableMapObject.ComponentModel       _component;
    private ConsumableItem _currentConsumable;
    private List<PotentialConsumable>                   _potentialConsumables;
    private JobActionReturn                             _return = JobActionReturn.CONTINUE;

    public int          getCurrentQuantity() { return _currentQuantity; }
    public int          getRealQuantity() { return _quantity; }

    public HaulJob(BuildableMapObject item, BuildableMapObject.ComponentModel component) {
        throw new NotImplementedException("");

//        super(null, item.getParcel(), new IconDrawable("data/res/ic_build.png", 0, 0, 32, 32), new AnimDrawable("data/res/actions.png", 0, 64, 32, 32, 7, 10));
//
//        _label = "Build " + item.getInfo().label;
//        _message = "Move to " + component.info.label;
//        _buildItem = item;
//        _component = component;
//        _component.job = this;
//
//        // Create potentials consumables
//        _potentialConsumables = new ArrayList<>();
//        ModuleHelper.getWorldModule().getConsumables().stream()
//                .filter(consumable -> consumable.getInfo() == _component.info)
//                .forEach(consumable -> {
//                    PathModel path = Application.pathManager.getPath(_buildItem.getParcel(), consumable.getParcel(), false, false);
//                    _potentialConsumables.addSubJob(new PotentialConsumable(consumable, path != null ? path.getLength() : -1));
//                });
//        Collections.sort(_potentialConsumables, (c1, c2) -> c1.distance - c2.distance);
    }

    public BuildableMapObject getBuildItem() {
        return _buildItem;
    }

    public void addPotentialConsumable(ConsumableItem consumable) {
        if (consumable.getInfo() == _component.info) {
            PathModel path = Application.pathManager.getPath(_buildItem.getParcel(), consumable.getParcel(), false, false);
            _potentialConsumables.add(new PotentialConsumable(consumable, path != null ? path.getLength() : -1));
            Collections.sort(_potentialConsumables, (c1, c2) -> c1.distance - c2.distance);
        }
    }

    public void removePotentialConsumable(ConsumableItem consumable) {
        if (consumable.getInfo() == _component.info) {
            // Remove consumable from potentials
            _potentialConsumables.removeIf(potentialConsumable -> potentialConsumable.consumable == consumable);

            // If current consumable is the removed one, move character to next component (or quit job if empty)
            if (_currentConsumable == consumable) {
                _currentConsumable = null;
                _character.cancelMove();
                if (!moveToNextComponent()) {
                    quit(_character);
                }
            }
        }
    }

    @Override
    public CharacterTalentExtra.TalentType getTalentNeeded() {
        return CharacterTalentExtra.TalentType.BUILD;
    }

    @Override
    public JobCheckReturn onCheck(CharacterModel character) {
        // Check if parcel is walkable
        if (!_targetParcel.isWalkable()) {
            _status = JobStatus.BLOCKED;
            return JobCheckReturn.BLOCKED;
        }

//        // Check if path exist
//        if (!Application.pathManager.hasPath(character.getParcel(), _buildItem.getParcel(), true, true)) {
//            return JobCheckReturn.STAND_BY;
//        }
//
//        // Check potential consumable
//        if (_potentialConsumables.isEmpty()) {
//            _status = JobStatus.MISSING_COMPONENT;
//            return JobCheckReturn.STAND_BY;
//        }
//
//        for (PotentialConsumable potentialConsumable: _potentialConsumables) {
//            if (potentialConsumable.consumable.getJob() == null && Application.pathManager.hasPath(character.getParcel(), potentialConsumable.consumable.getParcel())) {
//                return JobCheckReturn.OK;
//            }
//        }

        if (_consumable != null && (_consumable.getJob() == null || _consumable.getJob() == this) && Application.pathManager.hasPath(character.getParcel(), _consumable.getParcel())) {
            return JobCheckReturn.OK;
        }

        return JobCheckReturn.STAND_BY;
    }

    public class OnReachItemListener implements MoveListener<CharacterModel> {
        @Override
        public void onReach(CharacterModel movable) {
        }

        @Override
        public void onFail(CharacterModel movable) {
        }
    }

    @Override
    protected void onStart(CharacterModel character) {
        moveToConsumable(character, _consumable);
//        if (!moveToNextComponent()) {
//            quit(character);
//        }
    }

    private void moveToConsumable(CharacterModel character, ConsumableItem consumable) {
        consumable.setJob(this);
        character.moveTo(_consumable.getParcel(), new MoveListener<CharacterModel>() {
            @Override
            public void onReach(CharacterModel character) {
                if (consumable.getQuantity() <= _quantity) {
                    consumable.setQuantity(0);
                    consumable.setParcel(null);
                    _currentQuantity += consumable.getQuantity();
                } else {
                    consumable.addQuantity(-_quantity);
                    _currentQuantity += _quantity;
                }
                consumable.setJob(null);
                character.addInventory(_itemInfo, _currentQuantity);
//                character.createInventoryFromConsumable(_consumable, _consumable.getQuantity());

                moveToItem(character);
            }

            @Override
            public void onFail(CharacterModel movable) {
                consumable.setJob(null);
                quit(character);
            }
        });
    }

    private void moveToItem(CharacterModel character) {
        character.moveTo(_targetParcel, new MoveListener<CharacterModel>() {
            @Override
            public void onReach(CharacterModel character) {
                _character.setInventory(null);
                _consumable.setJob(null);

                if (_onCompleteListener != null) {
                    _onCompleteListener.onComplete();
                }

                finish();
                quit(character);
            }

            @Override
            public void onFail(CharacterModel character) {
                _character.setInventory(null);
                _consumable.setJob(null);
                _consumable.setParcel(character.getParcel());
                quit(character);
            }
        });
    }

    @Override
    public JobActionReturn onAction(CharacterModel character) {
        return _return;
    }

    @Override
    public void onQuit(CharacterModel character) {
        character.addInventory(_itemInfo, -_currentQuantity);

//        if (character.getInventory() != null) {
//            ModuleHelper.getWorldModule().putConsumable(character.getParcel(), character.getInventory());
//            character.setInventory(null);
//        }
//        _currentConsumable = null;
//        _potentialConsumables.forEach(potentialConsumable -> {
//            if (potentialConsumable.consumable.getJob() == this) {
//                potentialConsumable.consumable.setJob(null);
//            }
//        });
    }

    @Override
    protected void onFinish() {
    }

    private void moveToComponent(ConsumableItem currentConsumable) {
        Log.info("Haul job: move to component");

        currentConsumable.setJob(this);
        _message = "Move to " + currentConsumable.getInfo().label;
        Log.debug(_character.getName() + " moveToComponent " + currentConsumable.getId());

        _targetParcel = currentConsumable.getParcel();
        _character.moveTo(_targetParcel, new MoveListener<CharacterModel>() {
            @Override
            public void onReach(CharacterModel character) {
                if (_targetParcel.getItem(ConsumableItem.class) == currentConsumable) {
                    int missingQuantity = (_component.neededQuantity - _component.currentQuantity);
                    if (currentConsumable.getQuantity() <= missingQuantity) {
                        _character.createInventoryFromConsumable(new ConsumableItem(currentConsumable.getInfo()), currentConsumable.getQuantity());
                        currentConsumable.setQuantity(0);
                    } else {
                        _character.createInventoryFromConsumable(new ConsumableItem(currentConsumable.getInfo()), missingQuantity);
                        currentConsumable.setQuantity(currentConsumable.getQuantity() - missingQuantity);
                    }
                }

                // TODO: java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
                _potentialConsumables.removeIf(potential -> potential.consumable == currentConsumable);
                currentConsumable.setJob(null);
                _currentConsumable = null;

                // Get next component
                if (moveToNextComponent()) {
                    return;
                }

                if (character.getInventory() != null && character.getInventory().getInfo() == _component.info) {
                    moveToMainItem();
                    return;
                }

                _return = JobActionReturn.QUIT;
            }

            @Override
            public void onFail(CharacterModel movable) {
                Log.info("HaulJob: character cannot reach component");
                quit(_character);
            }
        });
    }

    private boolean moveToNextComponent() {
        // Character has wrong item in inventory
        if (_character.getInventory() != null && _character.getInventory().getInfo() != _component.info) {
            return false;
        }

        // Character has enough item to state job
        if (_character.getInventoryQuantity() >= (_component.neededQuantity - _component.currentQuantity)) {
            return false;
        }

        // Character cannot haul more item
        if (_character.getInventoryQuantity() >= Utils.getInventoryMaxQuantity(_component.info)) {
            return false;
        }

        for (PotentialConsumable potentialConsumable: _potentialConsumables) {
            if (Application.pathManager.hasPath(_character.getParcel(), potentialConsumable.consumable.getParcel(), true, false)) {
                _currentConsumable = potentialConsumable.consumable;
                moveToComponent(potentialConsumable.consumable);
                return true;
            }
        }

        return false;
    }

    protected void moveToMainItem() {
        throw new NotImplementedException("");

//        Log.info("Haul job: move to main item");
//        _message = "Bring " + _character.getInventory().getInfo().label + " to " + _buildItem.getInfo().label;
//
//        // Move to build item
//        _targetParcel = _buildItem.getParcel();
//        _character.moveTo(_buildItem.getParcel(), new MoveListener<CharacterModel>() {
//            @Override
//            public void onReach(CharacterModel character) {
//                // Store component in factory
//                _buildItem.addComponent(_character.getInventory());
//
//                // Clear inventory if consumable has been depleted
//                if (_character.getInventory().getQuantity() == 0) {
//                    _character.setInventory(null);
//                }
//
//                // By-pass JobModule to start BuildJob without delay
//                if (_buildItem.hasAllComponents()) {
//                    ModuleHelper.getJobModule().addHaulJob(new BuildJob(_buildItem));
//                }
//
//                _return = _component.currentQuantity == _component.neededQuantity ? JobActionReturn.COMPLETE : JobActionReturn.QUIT;
//            }
//
//            @Override
//            public void onFail(CharacterModel movable) {
//                quit(_character);
//            }
//        });
    }
}
