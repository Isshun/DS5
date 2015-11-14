package org.smallbox.faraway.core.game.module.job.model;

import org.smallbox.faraway.core.engine.drawable.AnimDrawable;
import org.smallbox.faraway.core.engine.drawable.IconDrawable;
import org.smallbox.faraway.core.game.model.Data;
import org.smallbox.faraway.core.game.module.character.model.CharacterTalentExtra;
import org.smallbox.faraway.core.game.module.character.model.PathModel;
import org.smallbox.faraway.core.game.module.character.model.base.CharacterModel;
import org.smallbox.faraway.core.game.module.job.model.abs.JobModel;
import org.smallbox.faraway.core.game.module.path.PathManager;
import org.smallbox.faraway.core.game.module.world.model.BuildableMapObject;
import org.smallbox.faraway.core.game.module.world.model.ConsumableModel;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.core.module.java.ModuleHelper;
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
    private ConsumableModel                             _currentConsumable;
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
            if (_currentConsumable == consumable) {
                _currentConsumable = null;
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
        _character = character;

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
        _currentConsumable = _potentialConsumables.get(0).consumable;
        _currentConsumable.lock(this);
        _message = "Move to " + _currentConsumable.getInfo().label;

        _targetParcel = _currentConsumable.getParcel();
        _character.moveTo(_targetParcel, new MoveListener<CharacterModel>() {
            @Override
            public void onReach(CharacterModel character) {
                if (!_potentialConsumables.isEmpty() && _potentialConsumables.get(0).consumable == _currentConsumable) {
                    ConsumableModel currentConsumable = _currentConsumable;
                    int missingQuantity = _component.neededQuantity - _component.currentQuantity;
                    if (_currentConsumable.getQuantity() <= missingQuantity) {
                        _character.addInventory(new ConsumableModel(_currentConsumable.getInfo()), _currentConsumable.getQuantity());
                        _currentConsumable.setQuantity(0);
                    } else {
                        _character.addInventory(new ConsumableModel(_currentConsumable.getInfo()), missingQuantity);
                        _currentConsumable.setQuantity(_currentConsumable.getQuantity() - missingQuantity);
                    }
                    // TODO: java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
                    _potentialConsumables.remove(0);
                    _currentConsumable.lock(null);
                    _currentConsumable = null;

                    if (currentConsumable.getQuantity() == 0) {
                        ModuleHelper.getWorldModule().removeConsumable(currentConsumable);
                    }

                    // Get next component
                    if (!_potentialConsumables.isEmpty() && _character.getInventory().getQuantity() < missingQuantity && _character.getInventory().getQuantity() < Data.config.inventoryMaxQuantity) {
                        moveToComponent();
                    } else {
                        moveToMainItem();
                    }
                }
            }

            @Override
            public void onFail(CharacterModel movable) {
                System.out.println("HaulJob: character cannot reach component");
                quit(_character);
            }
        });
    }

    protected void moveToMainItem() {
        _message = "Bring " + _character.getInventory().getInfo().label + " to " + _buildItem.getInfo().label;

        // TODO: Reliquat
        _targetParcel = _buildItem.getParcel();

        // Store component in factory
        _character.moveTo(_buildItem.getParcel(), new MoveListener<CharacterModel>() {
            @Override
            public void onReach(CharacterModel character) {
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
