package org.smallbox.faraway.core.game.module.job.model;

import org.apache.commons.lang3.NotImplementedException;
import org.smallbox.faraway.core.engine.drawable.AnimDrawable;
import org.smallbox.faraway.core.engine.drawable.IconDrawable;
import org.smallbox.faraway.core.game.module.character.model.CharacterTalentExtra;
import org.smallbox.faraway.core.game.module.character.model.PathModel;
import org.smallbox.faraway.core.game.module.character.model.base.CharacterModel;
import org.smallbox.faraway.core.game.module.job.model.abs.JobModel;
import org.smallbox.faraway.core.game.module.path.PathManager;
import org.smallbox.faraway.core.game.module.world.model.BuildableMapObject;
import org.smallbox.faraway.core.game.module.world.model.ConsumableModel;
import org.smallbox.faraway.core.util.Log;
import org.smallbox.faraway.core.util.MoveListener;
import org.smallbox.faraway.core.util.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Alex on 09/10/2015.
 */
public class HaulJob extends JobModel {
    public static class PotentialConsumable {
        public ConsumableModel  consumable;
        public int              distance;

        public PotentialConsumable(ConsumableModel consumable, int distance) {
            this.consumable = consumable;
            this.distance = distance;
        }
    }

    private final BuildableMapObject                    _buildItem;
    private final BuildableMapObject.ComponentModel     _component;
    private ConsumableModel                             _currentConsumable;
    private List<PotentialConsumable>                   _potentialConsumables;
    private JobActionReturn                             _return = JobActionReturn.CONTINUE;

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
//                    PathModel path = PathManager.getInstance().getPath(_buildItem.getParcel(), consumable.getParcel(), false, false);
//                    _potentialConsumables.add(new PotentialConsumable(consumable, path != null ? path.getLength() : -1));
//                });
//        Collections.sort(_potentialConsumables, (c1, c2) -> c1.distance - c2.distance);
    }

    public BuildableMapObject getBuildItem() {
        return _buildItem;
    }

    public void addPotentialConsumable(ConsumableModel consumable) {
        if (consumable.getInfo() == _component.info) {
            PathModel path = PathManager.getInstance().getPath(_buildItem.getParcel(), consumable.getParcel(), false, false);
            _potentialConsumables.add(new PotentialConsumable(consumable, path != null ? path.getLength() : -1));
            Collections.sort(_potentialConsumables, (c1, c2) -> c1.distance - c2.distance);
        }
    }

    public void removePotentialConsumable(ConsumableModel consumable) {
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

        // Check if path exist
        if (!PathManager.getInstance().hasPath(character.getParcel(), _buildItem.getParcel(), true, true)) {
            return JobCheckReturn.STAND_BY;
        }

        // Check potential consumable
        if (_potentialConsumables.isEmpty()) {
            _status = JobStatus.MISSING_COMPONENT;
            return JobCheckReturn.STAND_BY;
        }

        for (PotentialConsumable potentialConsumable: _potentialConsumables) {
            if (potentialConsumable.consumable.getLock() == null && PathManager.getInstance().hasPath(character.getParcel(), potentialConsumable.consumable.getParcel())) {
                return JobCheckReturn.OK;
            }
        }

        return JobCheckReturn.STAND_BY;
    }

    @Override
    protected void onStart(CharacterModel character) {
        if (!moveToNextComponent()) {
            quit(character);
        }
    }

    @Override
    public JobActionReturn onAction(CharacterModel character) {
        return _return;
    }

    @Override
    public void onQuit(CharacterModel character) {
        throw new NotImplementedException("");

//        if (character.getInventory() != null) {
//            ModuleHelper.getWorldModule().putConsumable(character.getParcel(), character.getInventory());
//            character.setInventory(null);
//        }
//        _currentConsumable = null;
//        _potentialConsumables.forEach(potentialConsumable -> {
//            if (potentialConsumable.consumable.getLock() == this) {
//                potentialConsumable.consumable.lock(null);
//            }
//        });
    }

    @Override
    protected void onFinish() {
    }

    private void moveToComponent(ConsumableModel currentConsumable) {
        Log.info("Haul job: move to component");

        currentConsumable.lock(this);
        _message = "Move to " + currentConsumable.getInfo().label;
        Log.debug(_character.getName() + " moveToComponent " + currentConsumable.getId());

        _targetParcel = currentConsumable.getParcel();
        _character.moveTo(_targetParcel, new MoveListener<CharacterModel>() {
            @Override
            public void onReach(CharacterModel character) {
                if (_targetParcel.getConsumable() == currentConsumable) {
                    int missingQuantity = (_component.neededQuantity - _component.currentQuantity);
                    if (currentConsumable.getQuantity() <= missingQuantity) {
                        _character.addInventory(new ConsumableModel(currentConsumable.getInfo()), currentConsumable.getQuantity());
                        currentConsumable.setQuantity(0);
                    } else {
                        _character.addInventory(new ConsumableModel(currentConsumable.getInfo()), missingQuantity);
                        currentConsumable.setQuantity(currentConsumable.getQuantity() - missingQuantity);
                    }
                }

                // TODO: java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
                _potentialConsumables.removeIf(potential -> potential.consumable == currentConsumable);
                currentConsumable.lock(null);
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

        // Character has enough item to complete job
        if (_character.getInventoryQuantity() >= (_component.neededQuantity - _component.currentQuantity)) {
            return false;
        }

        // Character cannot haul more item
        if (_character.getInventoryQuantity() >= Utils.getInventoryMaxQuantity(_component.info)) {
            return false;
        }

        for (PotentialConsumable potentialConsumable: _potentialConsumables) {
            if (PathManager.getInstance().hasPath(_character.getParcel(), potentialConsumable.consumable.getParcel(), true, false)) {
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
//                    ModuleHelper.getJobModule().addJob(new BuildJob(_buildItem));
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
