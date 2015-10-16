package org.smallbox.faraway;

import com.badlogic.gdx.ai.pfa.GraphPath;
import org.smallbox.faraway.core.drawable.AnimDrawable;
import org.smallbox.faraway.core.drawable.IconDrawable;
import org.smallbox.faraway.game.model.GameData;
import org.smallbox.faraway.game.model.MovableModel;
import org.smallbox.faraway.game.model.character.base.CharacterModel;
import org.smallbox.faraway.game.model.item.BuildableMapObject;
import org.smallbox.faraway.game.model.item.ConsumableModel;
import org.smallbox.faraway.game.model.item.ParcelModel;
import org.smallbox.faraway.game.model.job.BaseJobModel;
import org.smallbox.faraway.game.module.ModuleHelper;
import org.smallbox.faraway.game.module.base.BuildJob;
import org.smallbox.faraway.game.module.path.PathManager;
import org.smallbox.faraway.util.MoveListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Alex on 09/10/2015.
 */
public class GetComponentJob extends org.smallbox.faraway.game.model.job.BaseJobModel {
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

    public GetComponentJob(BuildableMapObject item, BuildableMapObject.ComponentModel component) {
        super(null, item.getParcel(), new IconDrawable("data/res/ic_build.png", 0, 0, 32, 32), new AnimDrawable("data/res/actions.png", 0, 64, 32, 32, 7, 10));

        _buildItem = item;
        _component = component;
        _component.job = this;
    }

    @Override
    public String getShortLabel() {
        return "Get " + _component.info.label;
    }

    @Override
    public ParcelModel getActionParcel() {
        return null;
    }

    @Override
    public CharacterModel.TalentType getTalentNeeded() {
        return CharacterModel.TalentType.BUILD;
    }

    @Override
    public boolean onCheck(CharacterModel character) {
        for (ConsumableModel consumable: ModuleHelper.getWorldModule().getConsumables()) {
            if (consumable.getInfo() == _component.info) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onFinish() {
    }

    @Override
    protected void onStart(CharacterModel character) {
        _character = character;
        _potentialConsumables = new ArrayList<>();
        ModuleHelper.getWorldModule().getConsumables().stream()
                .filter(consumable -> consumable.getInfo() == _component.info)
                .filter(consumable -> consumable.getParcel().isWalkable())
                .forEach(consumable -> {
                    GraphPath<ParcelModel> path = PathManager.getInstance().getPath(_buildItem.getParcel(), consumable.getParcel());
                    if (path != null) {
                        _potentialConsumables.add(new PotentialConsumable(consumable, path.getCount()));
                    }
                });
        Collections.sort(_potentialConsumables, (c1, c2) -> c2.distance - c1.distance);

        if (!_potentialConsumables.isEmpty()) {
            moveToComponent();
        }
    }

    private void moveToComponent() {
        _currentConsumable = _potentialConsumables.get(0).consumable;
        _currentConsumable.lock(this);
        _message = "Move to " + _currentConsumable.getInfo().label;

        // TODO: reliquat
        _targetParcel = _currentConsumable.getParcel();

        _character.moveTo(this, _currentConsumable.getParcel(), new MoveListener<CharacterModel>() {
            @Override
            public void onReach(CharacterModel movable) {
                int missingQuantity = _component.neededQuantity - _component.currentQuantity;
                if (_currentConsumable.getQuantity() <= missingQuantity) {
                    _character.addInventory(new ConsumableModel(_currentConsumable.getInfo()), _currentConsumable.getQuantity());
                    ModuleHelper.getWorldModule().removeConsumable(_currentConsumable);
                } else {
                    _character.addInventory(new ConsumableModel(_currentConsumable.getInfo()), missingQuantity);
                    _currentConsumable.setQuantity(_currentConsumable.getQuantity() - missingQuantity);
                }
                _potentialConsumables.remove(0);
                _currentConsumable.lock(null);
                _currentConsumable = null;

                // Get next component
                if (!_potentialConsumables.isEmpty() && _character.getInventory().getQuantity() < missingQuantity && _character.getInventory().getQuantity() < GameData.config.inventoryMaxQuantity) {
                    moveToComponent();
                } else {
                    moveToMainItem();
                }
            }

            @Override
            public void onFail(CharacterModel movable) {

            }

            @Override
            public void onSuccess(CharacterModel movable) {

            }
        });
    }

    protected void moveToMainItem() {
        _message = "Carry " + _character.getInventory().getInfo().label + " to " + _buildItem.getInfo().label;

        // TODO: Reliquat
        _targetParcel = _buildItem.getParcel();

        // Store component in factory
        _character.moveTo(this, _buildItem.getParcel(), new MoveListener<CharacterModel>() {
            @Override
            public void onReach(CharacterModel movable) {
                _buildItem.addComponent(_character.getInventory());

                // Clear inventory if consumable has been depleted
                if (_character.getInventory().getQuantity() == 0) {
                    _character.setInventory(null);
                }

                // By-pass JobModule to start BuildJob without delay
                if (_buildItem.hasAllComponents()) {
                    ModuleHelper.getJobModule().addJob(new BuildJob(_buildItem));
                }

                _return = _component.currentQuantity == _component.neededQuantity ? JobActionReturn.FINISH : JobActionReturn.QUIT;
            }

            @Override
            public void onFail(CharacterModel movable) {

            }

            @Override
            public void onSuccess(CharacterModel movable) {

            }
        });
    }

    @Override
    public JobActionReturn onAction(CharacterModel character) {
        return _return;
    }

}
