package org.smallbox.faraway.core.game.module.job.model;

import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.engine.drawable.AnimDrawable;
import org.smallbox.faraway.core.engine.drawable.IconDrawable;
import org.smallbox.faraway.core.engine.module.java.ModuleHelper;
import org.smallbox.faraway.core.game.module.character.model.CharacterTalentExtra;
import org.smallbox.faraway.core.game.module.character.model.PathModel;
import org.smallbox.faraway.core.game.module.character.model.base.CharacterModel;
import org.smallbox.faraway.core.game.module.job.model.abs.JobModel;
import org.smallbox.faraway.core.game.module.path.PathManager;
import org.smallbox.faraway.core.game.module.world.model.BuildableMapObject;
import org.smallbox.faraway.core.game.module.world.model.ConsumableModel;
import org.smallbox.faraway.core.util.Log;
import org.smallbox.faraway.core.util.MoveListener;

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
    private PotentialConsumable                         _currentConsumable;
    private List<PotentialConsumable>                   _potentialConsumables;
    private JobActionReturn                             _return = JobActionReturn.CONTINUE;

    public HaulJob(BuildableMapObject item, BuildableMapObject.ComponentModel component) {
        super(null, item.getParcel(), new IconDrawable("data/res/ic_build.png", 0, 0, 32, 32), new AnimDrawable("data/res/actions.png", 0, 64, 32, 32, 7, 10));

        _label = "Build " + item.getInfo().label;
        _message = "Move to " + component.info.label;
        _buildItem = item;
        _component = component;
        _component.job = this;

        // Create potentials consumables
        _potentialConsumables = new ArrayList<>();
        ModuleHelper.getWorldModule().getConsumables().stream()
                .filter(consumable -> consumable.getInfo() == _component.info)
                .forEach(consumable -> {
                    PathModel path = PathManager.getInstance().getPath(_buildItem.getParcel(), consumable.getParcel(), false, false);
                    _potentialConsumables.add(new PotentialConsumable(consumable, path != null ? path.getLength() : -1));
                });
        Collections.sort(_potentialConsumables, (c1, c2) -> c1.distance - c2.distance);
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
            // If current consumable exists (then job is running), move character to next component (or quit if empty)
            if (_currentConsumable != null && _currentConsumable.consumable == consumable) {
                _currentConsumable = null;
                _character.cancelMove();
                if (!_potentialConsumables.isEmpty()) {
                    moveToComponent();
                } else {
                    quit(_character);
                }
            }
            _potentialConsumables.removeIf(potentialConsumable -> potentialConsumable.consumable == consumable);
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
        if (!_potentialConsumables.isEmpty()) {
            moveToComponent();
        } else {
            quit(character);
        }
    }

    @Override
    public JobActionReturn onAction(CharacterModel character) {
        return _return;
    }

    @Override
    public void onQuit(CharacterModel character) {
        if (character.getInventory() != null) {
            ModuleHelper.getWorldModule().putConsumable(character.getParcel(), character.getInventory());
            character.setInventory(null);
        }
        _currentConsumable = null;
        _potentialConsumables.forEach(potentialConsumable -> {
            if (potentialConsumable.consumable.getLock() == this) {
                potentialConsumable.consumable.lock(null);
            }
        });
    }

    @Override
    protected void onFinish() {
    }

    private void moveToComponent() {
        _currentConsumable = _potentialConsumables.get(0);
        _currentConsumable.consumable.lock(this);
        _message = "Move to " + _currentConsumable.consumable.getInfo().label;
        Log.debug(_character.getName() + " moveToComponent " + _currentConsumable.consumable.getId());

        _targetParcel = _currentConsumable.consumable.getParcel();
        _character.moveTo(_targetParcel, new MoveListener<CharacterModel>() {
            @Override
            public void onReach(CharacterModel character) {
                int missingQuantity = _component.neededQuantity - _component.currentQuantity;

                if (_potentialConsumables.contains(_currentConsumable)) {
                    _potentialConsumables.remove(_currentConsumable);
                    if (_currentConsumable.consumable.getQuantity() <= missingQuantity) {
                        _character.addInventory(new ConsumableModel(_currentConsumable.consumable.getInfo()), _currentConsumable.consumable.getQuantity());
                        _currentConsumable.consumable.setQuantity(0);
                    } else {
                        _character.addInventory(new ConsumableModel(_currentConsumable.consumable.getInfo()), missingQuantity);
                        _currentConsumable.consumable.setQuantity(_currentConsumable.consumable.getQuantity() - missingQuantity);
                    }
                }

                ConsumableModel currentConsumable = _currentConsumable.consumable;
                // TODO: java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
                _currentConsumable.consumable.lock(null);
                _currentConsumable = null;

                if (currentConsumable.getQuantity() == 0) {
                    ModuleHelper.getWorldModule().removeConsumable(currentConsumable);
                }

                // Get next component
                if (!_potentialConsumables.isEmpty() && _character.getInventory() != null && _character.getInventory().getQuantity() < missingQuantity && _character.getInventory().getQuantity() < Application.getInstance().getConfig().game.inventoryMaxQuantity) {
                    moveToComponent();
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

    protected void moveToMainItem() {
        _message = "Bring " + _character.getInventory().getInfo().label + " to " + _buildItem.getInfo().label;

        // Move to build item
        _targetParcel = _buildItem.getParcel();
        _character.moveTo(_buildItem.getParcel(), new MoveListener<CharacterModel>() {
            @Override
            public void onReach(CharacterModel character) {
                // Store component in factory
                _buildItem.addComponent(_character.getInventory());

                // Clear inventory if consumable has been depleted
                if (_character.getInventory().getQuantity() == 0) {
                    _character.setInventory(null);
                }

                // By-pass JobModule to start BuildJob without delay
                if (_buildItem.hasAllComponents()) {
                    ModuleHelper.getJobModule().addJob(new BuildJob(_buildItem));
                }

                _return = _component.currentQuantity == _component.neededQuantity ? JobActionReturn.COMPLETE : JobActionReturn.QUIT;
            }

            @Override
            public void onFail(CharacterModel movable) {
                quit(_character);
            }
        });
    }
}
