package org.smallbox.faraway.core.game.module.job.model;

import org.smallbox.faraway.core.data.ItemInfo;
import org.smallbox.faraway.core.engine.drawable.IconDrawable;
import org.smallbox.faraway.core.game.GameObserver;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.model.Data;
import org.smallbox.faraway.core.game.module.area.model.StorageAreaModel;
import org.smallbox.faraway.core.game.module.character.model.CharacterTalentExtra;
import org.smallbox.faraway.core.game.module.character.model.base.CharacterModel;
import org.smallbox.faraway.core.game.module.job.model.abs.JobModel;
import org.smallbox.faraway.core.game.module.path.PathManager;
import org.smallbox.faraway.core.game.module.world.model.ConsumableModel;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.core.engine.module.java.ModuleHelper;
import org.smallbox.faraway.core.util.Log;
import org.smallbox.faraway.core.util.MoveListener;

import java.util.ArrayList;
import java.util.List;

public class StoreJob extends JobModel implements GameObserver {
    private enum Mode {MOVE_TO_CONSUMABLE, MOVE_TO_STORAGE}

    private List<ConsumableModel>   _consumables = new ArrayList<>();
    private StorageAreaModel        _storage;
    private Mode                    _mode;
    private int                     _quantity;
    private ItemInfo                _itemInfo;

    private StoreJob(ParcelModel jobParcel) {
        super(null, jobParcel, new IconDrawable("data/res/ic_haul.png", 0, 0, 32, 32), null);
    }

    public static StoreJob create(ConsumableModel consumable, StorageAreaModel storage) {
        assert consumable != null;

        ParcelModel targetParcel = WorldHelper.getNearestWalkable(consumable.getParcel(), 1, 1);
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
                j.getCharacter().getNeeds().addValue("entertainment", j.getCharacter().getType().needs.joy.change.work);
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
        int fromX = firstConsumable.getParcel().x - 5;
        int fromY = firstConsumable.getParcel().y - 5;
        int toX = firstConsumable.getParcel().x + 5;
        int toY = firstConsumable.getParcel().y + 5;
        int z = firstConsumable.getParcel().z;

        _consumables.clear();
        _consumables.add(firstConsumable);
        _quantity += firstConsumable.getQuantity();
        for (int x = fromX; x <= toX; x++) {
            for (int y = fromY; y <= toY; y++) {
                ConsumableModel consumable = WorldHelper.getConsumable(x, y, z);
                if (consumable != null && consumable != firstConsumable
                        && consumable.getLock() == null
                        && consumable.getInfo() == firstConsumable.getInfo()
                        && consumable.getStoreJob() == null
                        && consumable.getQuantity() + _quantity <= Data.config.inventoryMaxQuantity
                        && PathManager.getInstance().hasPath(consumable.getParcel(), firstConsumable.getParcel())) {
                    _quantity += consumable.getQuantity();
                    _consumables.add(consumable);
                    consumable.addJob(this);
                    consumable.setStoreJob(this);
                }
            }
        }
    }

    @Override
    public void draw(onDrawCallback callback) {
        for (ConsumableModel consumable: _consumables) {
            callback.onDraw(consumable.getParcel().x, consumable.getParcel().y, consumable.getParcel().z);
        }
    }

    private void refreshJob() {
        // Go to next consumable
        if (!_consumables.isEmpty()) {
            _mode = Mode.MOVE_TO_CONSUMABLE;
            moveToConsumable(_consumables.get(0));
            return;
        }

        // Go to storage
        else if (_character.getInventory() != null) {
            ParcelModel parcel = _storage.getFreeParcel(_character.getInventory());
            if (parcel != null) {
                moveToStorage(parcel);
                return;
            }
        }

        quit(_character);
    }

    private void moveToConsumable(ConsumableModel consumable) {
        Log.info("Haul consumable: " + consumable.getInfo().label);

        _targetParcel = WorldHelper.getNearestWalkable(consumable.getParcel(), 0, 1);
        _character.moveTo(_targetParcel, new MoveListener<CharacterModel>() {
            @Override
            public void onReach(CharacterModel movable) {
                // TODO: check characters inventory free space
                if (_character.getInventory() == null) {
                    _character.addInventory(consumable, consumable.getQuantity());
                    ModuleHelper.getWorldModule().removeConsumable(consumable);
                } else if (_character.getInventory().getInfo() == consumable.getInfo()) {
                    _character.getInventory().addQuantity(consumable.getQuantity());
                    consumable.setQuantity(0);
                    if (consumable.isEmpty()) {
                        ModuleHelper.getWorldModule().removeConsumable(consumable);
                    }
                } else {
                    Log.error("JobHaul: characters inventory must be empty");
                    quit(_character);
                }
                consumable.removeJob(StoreJob.this);
                consumable.setStoreJob(null);
                consumable.lock(null);
                _consumables.remove(consumable);

                refreshJob();
            }

            @Override
            public void onFail(CharacterModel movable) {
                _reason = JobAbortReason.BLOCKED;
                quit(_character);
            }
        });
    }

    private void moveToStorage(ParcelModel parcel) {
        _targetParcel = parcel;
        _character.moveTo(_targetParcel, new MoveListener<CharacterModel>() {
            @Override
            public void onReach(CharacterModel movable) {
                if (_targetParcel.getConsumable() == null) {
                    ModuleHelper.getWorldModule().putConsumable(_targetParcel, _character.getInventory());
                    _character.setInventory(null);
                    complete();
                    return;
                }
                if (_targetParcel.getConsumable().getInfo() == _character.getInventory().getInfo()) {
                    int freeQuantity = _targetParcel.getConsumable().getInfo().stack - _targetParcel.getConsumable().getQuantity();
                    // Store all inventory consumables on storage model
                    if (freeQuantity >= _character.getInventory().getQuantity()) {
                        _targetParcel.getConsumable().addQuantity(_character.getInventory().getQuantity());
                        _character.setInventory(null);
                        complete();
                        return;
                    }
                    // Store some inventory consumables
                    else {
                        _targetParcel.getConsumable().addQuantity(freeQuantity);
                        _character.getInventory().addQuantity(-freeQuantity);
                    }
                }

                refreshJob();
            }

            @Override
            public void onFail(CharacterModel movable) {
                _reason = JobAbortReason.BLOCKED;
                quit(_character);
            }
        });
    }

    @Override
    public CharacterTalentExtra.TalentType getTalentNeeded() {
        return CharacterTalentExtra.TalentType.STORE;
    }

    @Override
    public JobCheckReturn onCheck(CharacterModel character) {
        // No consumable  to haul
        if (_consumables.isEmpty()) {
            return JobCheckReturn.ABORT;
        }

        if (!_storage.hasFreeSpace(_itemInfo, _quantity)) {
            return JobCheckReturn.ABORT;
        }

        // No free space in storage
        if (_jobParcel == null || (_jobParcel.getConsumable() != null && _jobParcel.getConsumable().getInfo() != _itemInfo)) {
            _jobParcel = _storage.getNearestFreeParcel(_consumables.get(0));
            if (_jobParcel == null) {
                _message = "No storage model";
                return JobCheckReturn.ABORT;
            }
        }

        // No path from consumable to storage
        if (!PathManager.getInstance().hasPath(_targetParcel, _jobParcel)) {
            _message = "No path to storage";
            return JobCheckReturn.ABORT;
        }

        // No path from character to consumable
        if (!PathManager.getInstance().hasPath(character.getParcel(), _targetParcel)) {
            _message = "No path to consumable";
            return JobCheckReturn.STAND_BY;
        }

        return JobCheckReturn.OK;
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
    public JobActionReturn onAction(CharacterModel character) {
        return JobActionReturn.CONTINUE;
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
    protected void onFinish() {
        _consumables.forEach(consumable -> consumable.setStoreJob(null));
    }

    @Override
    public String getLabel() {
        return "Store " + _itemInfo.label;
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
}
