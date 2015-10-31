package org.smallbox.faraway.core.game.module.job.model;

import org.smallbox.faraway.core.engine.drawable.IconDrawable;
import org.smallbox.faraway.core.game.GameObserver;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.model.GameData;
import org.smallbox.faraway.core.game.module.area.model.StorageAreaModel;
import org.smallbox.faraway.core.game.module.character.model.base.CharacterModel;
import org.smallbox.faraway.core.game.module.job.model.abs.JobModel;
import org.smallbox.faraway.core.game.module.path.PathManager;
import org.smallbox.faraway.core.game.module.world.model.ConsumableModel;
import org.smallbox.faraway.core.game.module.world.model.ItemInfo;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.core.module.java.ModuleHelper;
import org.smallbox.faraway.core.util.Log;

import java.util.ArrayList;
import java.util.List;

public class StoreJob extends JobModel implements GameObserver {
    private enum Mode {MOVE_TO_CONSUMABLE, MOVE_TO_STORAGE}

    private List<ConsumableModel>   _consumables = new ArrayList<>();
    private StorageAreaModel        _storage;
    private Mode                     _mode;
    private int                     _quantity;
    private ItemInfo                _itemInfo;

    private StoreJob(ParcelModel jobParcel) {
        super(null, jobParcel, new IconDrawable("data/res/ic_haul.png", 0, 0, 32, 32), null);
    }

    public static StoreJob create(ConsumableModel consumable, StorageAreaModel storage) {
        if (consumable == null) {
            Log.error("onCreate JobHaul: consumable cannot be null");
            return null;
        }

         ParcelModel targetParcel = WorldHelper.getNearestWalkable(consumable.getParcel().x, consumable.getParcel().y, true, true, 1, 1);
        if (targetParcel == null) {
            return null;
        }

        StoreJob job = new StoreJob(targetParcel);
        consumable.setStoreJob(job);
        job._storage = storage;
        job._jobParcel = storage.getNearestFreeParcel(consumable);
        job._targetParcel = targetParcel;
        job._mode = Mode.MOVE_TO_CONSUMABLE;
        job._itemInfo = consumable.getInfo();
        job.setStrategy(j -> {
            if (j.getCharacter().getType().needs.joy != null) {
                j.getCharacter().getNeeds().joy += j.getCharacter().getType().needs.joy.change.work;
            }
        });
        job.foundConsumablesAround(consumable);

        // No consumables to haul
        if (job._consumables.isEmpty()) {
            return null;
        }

        return job;
    }

    public void foundConsumablesAround(ConsumableModel firstConsumable) {
        int fromX = firstConsumable.getX() - 5;
        int fromY = firstConsumable.getY() - 5;
        int toX = firstConsumable.getX() + 5;
        int toY = firstConsumable.getY() + 5;

        _consumables.clear();
        _consumables.add(firstConsumable);
        _quantity += firstConsumable.getQuantity();
        for (int x = fromX; x <= toX; x++) {
            for (int y = fromY; y <= toY; y++) {
                ConsumableModel consumable = WorldHelper.getConsumable(x, y);
                if (consumable != null && consumable != firstConsumable
                        && consumable.getLock() == null
                        && consumable.getInfo() == firstConsumable.getInfo()
                        && consumable.getStoreJob() == null
                        && consumable.getQuantity() + _quantity <= GameData.config.inventoryMaxQuantity
                        && PathManager.getInstance().getPath(consumable.getParcel(), firstConsumable.getParcel()) != null) {
                    _quantity += consumable.getQuantity();
                    _consumables.add(consumable);
                    consumable.addJob(this);
                    consumable.setStoreJob(this);
                }
            }
        }
    }

    @Override
    public void onDraw(onDrawCallback callback) {
        for (ConsumableModel consumable: _consumables) {
            callback.onDraw(consumable.getX(), consumable.getY());
        }
    }

    private void refreshJob() {
        // Go to next consumable
        if (!_consumables.isEmpty()) {
            _mode = Mode.MOVE_TO_CONSUMABLE;
            ParcelModel consumableParcel = _consumables.get(0).getParcel();
            ParcelModel targetParcel = WorldHelper.getNearestWalkable(consumableParcel.x, consumableParcel.y, true, true, 1, 1);
            _targetParcel = targetParcel != null ? targetParcel : consumableParcel;
        }

        // Go to storage
        else {
            _mode = Mode.MOVE_TO_STORAGE;
            _targetParcel = _jobParcel;
        }

        if (_character != null) {
            _character.moveTo(this, _targetParcel, null);
        }
    }

    @Override
    public boolean canBeResume() {
        return false;
    }

    @Override
    public CharacterModel.TalentType getTalentNeeded() {
        return CharacterModel.TalentType.STORE;
    }

    @Override
    public void onQuit(CharacterModel character) {
        if (character.getInventory() != null) {
            ModuleHelper.getWorldModule().putConsumable(character.getParcel(), character.getInventory());
            character.setInventory(null);
        }
        _consumables.forEach(consumable -> consumable.lock(null));
    }

    @Override
    protected void onStart(CharacterModel character) {
        // Lock items
        _consumables.forEach(c -> {
            c.lock(this);
        });
        refreshJob();
    }

    @Override
    public boolean onCheck(CharacterModel character) {
        // No consumable  to haul
        if (_consumables.isEmpty()) {
            return false;
        }

        // No available storage
        if (_storage == null) {
            _message = "No storage model";
            return false;
        }

        // No free space in storage
        if (_jobParcel == null || (_jobParcel.getConsumable() != null && _jobParcel.getConsumable().getInfo() != _itemInfo)) {
            _jobParcel = _storage.getNearestFreeParcel(_consumables.get(0));
            if (_jobParcel == null) {
                _message = "No storage model";
                return false;
            }
        }

        return true;
    }

    @Override
    protected void onFinish() {
        _consumables.forEach(consumable -> consumable.setStoreJob(null));
    }

    // TODO: add inventory filter
    @Override
    public JobActionReturn onAction(CharacterModel character) {
        if (_storage == null) {
            Log.error("JobHaul: null storage");
            ModuleHelper.getJobModule().quitJob(this, JobAbortReason.INVALID);
            return JobActionReturn.ABORT;
        }

        // Reach consumable
        if (_mode == Mode.MOVE_TO_CONSUMABLE) {
            ConsumableModel consumable = _consumables.get(0);
            Log.info("Haul consumable: " + consumable.getInfo().label);

            // TODO: check characters inventory free space
            if (_character.getInventory() == null) {
                _character.setInventory(consumable);
                ModuleHelper.getWorldModule().removeConsumable(consumable);
            } else if (_character.getInventory().getInfo() == consumable.getInfo()) {
                _character.getInventory().addQuantity(consumable.getQuantity());
                consumable.setQuantity(0);
                if (consumable.isEmpty()) {
                    ModuleHelper.getWorldModule().removeConsumable(consumable);
                }
            } else {
                Log.error("JobHaul: characters inventory must be empty");
                ModuleHelper.getJobModule().quitJob(this, JobAbortReason.INVALID);
                return JobActionReturn.ABORT;
            }
            consumable.removeJob(this);
            consumable.setStoreJob(null);
            consumable.lock(null);
            _consumables.remove(consumable);

            refreshJob();

            return JobActionReturn.CONTINUE;
        }

        // Reach storage
        if (_mode == Mode.MOVE_TO_STORAGE) {
            if (_character.getInventory() == null) {
                Log.error("Character reach storage with empty inventory");
                return JobActionReturn.ABORT;
            }
            if (_targetParcel != null && _targetParcel.getConsumable() == null) {
                ModuleHelper.getWorldModule().putConsumable(_targetParcel, _character.getInventory());
                _character.setInventory(null);
                return JobActionReturn.FINISH;
            }
            if (_targetParcel != null && _targetParcel.getConsumable().getInfo() == _character.getInventory().getInfo()) {
                int freeQuantity = _targetParcel.getConsumable().getInfo().stack - _targetParcel.getConsumable().getQuantity();
                // Store all inventory consumables on storage model
                if (freeQuantity >= _character.getInventory().getQuantity()) {
                    _targetParcel.getConsumable().addQuantity(_character.getInventory().getQuantity());
                    _character.setInventory(null);
                    return JobActionReturn.FINISH;
                }
                // Store some inventory consumables
                else {
                    _targetParcel.getConsumable().addQuantity(freeQuantity);
                    _character.getInventory().addQuantity(-freeQuantity);
                }
            }

            // Found another free storage model
            _targetParcel = _jobParcel = _storage.getNearestFreeParcel(_character.getInventory());
            if (_targetParcel != null) {
                Log.info("Continue job to: " + _targetParcel.x + "x" + _targetParcel.y + ", left: " + _character.getInventory().getQuantity());
                _character.moveTo(this, _targetParcel, null);
                return JobActionReturn.CONTINUE;
            }
            // No empty space in any storage
            else {
                Log.error("No empty space in any storage");
                ModuleHelper.getWorldModule().putConsumable(_character.getParcel(), _character.getInventory());
                _character.setInventory(null);
                return JobActionReturn.ABORT;
            }
        }

        return JobActionReturn.FINISH;
    }

    @Override
    public String getLabel() {
        return "Store " + _itemInfo.label;
    }

    @Override
    public String getShortLabel() {
        return "store";
    }

    // TODO
    @Override
    public void onAddConsumable(ConsumableModel consumable){
    }

    @Override
    public void onRemoveConsumable(ConsumableModel consumable){
        if (_consumables.contains(consumable)) {
            _consumables.remove(consumable);
            consumable.removeJob(this);
            consumable.setStoreJob(null);
            if (consumable.getLock() == this) {
                consumable.lock(null);
            }
        }
    }

    @Override
    public ParcelModel getActionParcel() {
        return null;
    }

}
