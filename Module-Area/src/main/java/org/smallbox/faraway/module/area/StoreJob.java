package org.smallbox.faraway.module.area;

import org.apache.commons.lang3.NotImplementedException;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.game.GameObserver;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.module.area.model.StorageAreaModel;
import org.smallbox.faraway.core.module.character.model.CharacterTalentExtra;
import org.smallbox.faraway.core.module.character.model.base.CharacterModel;
import org.smallbox.faraway.core.module.job.model.abs.JobModel;
import org.smallbox.faraway.core.module.world.model.ConsumableItem;
import org.smallbox.faraway.core.module.world.model.ParcelModel;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Move consumable to storage area
 */
public class StoreJob extends JobModel implements GameObserver {
    private enum Mode {MOVE_TO_CONSUMABLE, MOVE_TO_STORAGE}

    private Queue<ConsumableItem>  _consumables = new LinkedBlockingQueue<>();
    private StorageAreaModel        _storageArea;
    private ParcelModel             _storageParcel;
    private Mode                    _mode;
    private int                     _quantity;
    private ItemInfo                _itemInfo;

    private StoreJob(ParcelModel jobParcel) {
        super(null, jobParcel);
//        super(null, jobParcel, new IconDrawable("data/res/ic_haul.png", 0, 0, 32, 32), null);
    }

    public static StoreJob create(ConsumableItem consumable, ParcelModel parcel) {
        return create(consumable, null, parcel);
    }

    public static StoreJob create(ConsumableItem consumable, StorageAreaModel storage) {
        return create(consumable, storage, null);
    }

    public static StoreJob create(ConsumableItem firstConsumable, StorageAreaModel storage, ParcelModel parcel) {
        assert firstConsumable != null;

        ParcelModel targetParcel = WorldHelper.getNearestWalkable(firstConsumable.getParcel(), 1, 1);
        if (targetParcel == null) {
            return null;
        }

        StoreJob storeJob = new StoreJob(targetParcel);
        firstConsumable.setJob(storeJob);
        storeJob._storageArea = storage;
        storeJob._storageParcel = parcel;
        storeJob._jobParcel = storage != null ? storage.getNearestFreeParcel(firstConsumable) : parcel;
        storeJob._targetParcel = targetParcel;
        storeJob._mode = Mode.MOVE_TO_CONSUMABLE;
        storeJob._itemInfo = firstConsumable.getInfo();

        // Set onStart listener
        storeJob.setOnStartListener(() -> {
            // Lock items
            storeJob._consumables.forEach(consumable -> consumable.setJob(storeJob));
            storeJob.refreshJob();
        });

        // Set onAction listener
        storeJob.setOnActionListener(() -> {
            if (storeJob.getCharacter().getType().needs.joy != null) {
                storeJob.getCharacter().getNeeds().addValue("entertainment", storeJob.getCharacter().getType().needs.joy.change.work);
            }
        });

        // Set onComplete listener
        storeJob.setOnCompleteListener(() -> storeJob._consumables.forEach(consumable -> consumable.setJob(null)));

        storeJob.foundConsumablesAround(firstConsumable);

        // No consumables to haul
        if (storeJob._consumables.isEmpty()) {
            return null;
        }

        return storeJob;
    }

    public void foundConsumablesAround(ConsumableItem firstConsumable) {
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
                ConsumableItem consumable = WorldHelper.getConsumable(x, y, z);
                if (consumable != null && consumable != firstConsumable
                        && consumable.getJob() == null
                        && consumable.getInfo() == firstConsumable.getInfo()
                        && consumable.getQuantity() + _quantity <= Application.configurationManager.game.inventoryMaxQuantity
                        && Application.pathManager.hasPath(consumable.getParcel(), firstConsumable.getParcel())) {
                    _quantity += consumable.getQuantity();
                    _consumables.add(consumable);
                    consumable.addJob(this);
                    consumable.setJob(this);
                }
            }
        }
    }

    @Override
    public void draw(onDrawCallback callback) {
        for (ConsumableItem consumable: _consumables) {
            callback.onDraw(consumable.getParcel().x, consumable.getParcel().y, consumable.getParcel().z);
        }
    }

    private void refreshJob() {
//        throw new NotImplementedException("");

//        // Go to next consumable
//        if (!_consumables.isEmpty()) {
//            _mode = Mode.MOVE_TO_CONSUMABLE;
//            moveToConsumable(_consumables.peek());
//            return;
//        }
//
//        // Go to storage
//        else if (_character.getInventory() != null) {
//            if (_storageArea != null) {
//                ParcelModel parcel = _storageArea.getNearestFreeParcel(_character.getInventory());
//                if (parcel != null) {
//                    moveToStorage(parcel);
//                    return;
//                }
//            }
//            if (_storageParcel != null) {
//                ParcelModel parcel = _storageParcel.accept(_itemInfo, 1)
//                        ? _storageParcel
//                        : WorldHelper.getNearestFreeParcel(_storageParcel, _itemInfo, 1);
//                if (parcel != null) {
//                    moveToStorage(parcel);
//                    return;
//                }
//            }
//        }
//
//        quit(_character);
    }

    private void moveToConsumable(ConsumableItem consumable) {
        throw new NotImplementedException("");

//        Log.info("Haul consumable: " + consumable.getInfo().label);
//
//        _targetParcel = WorldHelper.getNearestWalkable(consumable.getParcel(), 0, 1);
//        _character.moveTo(_targetParcel, new MoveListener<CharacterModel>() {
//            @Override
//            public void onReach(CharacterModel movable) {
//                // TODO: isJobLaunchable characters inventory free space
//                if (_character.getInventory() == null) {
//                    _character.createInventoryFromConsumable(consumable, consumable.getQuantity());
//                    ModuleHelper.getWorldModule().removeConsumable(consumable);
//                } else if (_character.getInventory().getInfo() == consumable.getInfo()) {
//                    _character.getInventory().addQuantity(consumable.getQuantity());
//                    consumable.setQuantity(0);
//                    if (consumable.isEmpty()) {
//                        ModuleHelper.getWorldModule().removeConsumable(consumable);
//                    }
//                } else {
//                    Log.error("JobHaul: characters inventory must be empty");
//                    quit(_character);
//                }
//                consumable.removeJob(StoreJob.this);
//                consumable.setStoreJob(null);
//                consumable.setJob(null);
//                _consumables.remove(consumable);
//
//                refreshJob();
//            }
//
//            @Override
//            public void onFail(CharacterModel movable) {
//                _reason = JobAbortReason.BLOCKED;
//                quit(_character);
//            }
//        });
    }

    private void moveToStorage(ParcelModel parcel) {
        throw new NotImplementedException("");

//        _targetParcel = parcel;
//        _character.moveTo(_targetParcel, new MoveListener<CharacterModel>() {
//            @Override
//            public void onReach(CharacterModel movable) {
//                if (_targetParcel.getConsumable() == null) {
//                    ModuleHelper.getWorldModule().putConsumable(_targetParcel, _character.getInventory());
//                    _character.setInventory(null);
//                    complete();
//                    return;
//                }
//                if (_targetParcel.getConsumable().getInfo() == _character.getInventory().getInfo()) {
//                    int freeQuantity = _targetParcel.getConsumable().getInfo().stack - _targetParcel.getConsumable().getQuantity();
//                    // Store all inventory consumables on storage org.smallbox.faraway.core.module.room.model
//                    if (freeQuantity >= _character.getInventory().getQuantity()) {
//                        _targetParcel.getConsumable().addQuantity(_character.getInventory().getQuantity());
//                        _character.setInventory(null);
//                        complete();
//                        return;
//                    }
//                    // Store some inventory consumables
//                    else {
//                        _targetParcel.getConsumable().addQuantity(freeQuantity);
//                        _character.getInventory().addQuantity(-freeQuantity);
//                    }
//                }
//
//                refreshJob();
//            }
//
//            @Override
//            public void onFail(CharacterModel movable) {
//                _reason = JobAbortReason.BLOCKED;
//                quit(_character);
//            }
//        });
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

        if (_storageArea == null && _storageParcel == null) {
            return JobCheckReturn.ABORT;
        }

        if (_storageArea != null && !_storageArea.hasFreeSpace(_itemInfo, 1)) {
            return JobCheckReturn.ABORT;
        }

        if (_storageParcel != null && !_storageParcel.accept(_itemInfo, 1)) {
            _storageParcel = WorldHelper.getNearestFreeParcel(_storageParcel, _itemInfo, 1);
            if (_storageParcel == null) {
                return JobCheckReturn.ABORT;
            }
        }

        // No free space in storage
        if (_storageArea != null && (_jobParcel == null || _jobParcel.accept(_itemInfo, 1))) {
            _jobParcel = _storageArea.getNearestFreeParcel(_consumables.peek());
            if (_jobParcel == null) {
                _message = "No storage org.smallbox.faraway.core.module.room.model";
                return JobCheckReturn.ABORT;
            }
        }

        // No path from consumable to storage
        if (!Application.pathManager.hasPath(_targetParcel, _jobParcel)) {
            _message = "No path to storage";
            return JobCheckReturn.ABORT;
        }

        // No path from character to consumable
        if (!Application.pathManager.hasPath(character.getParcel(), _targetParcel)) {
            _message = "No path to consumable";
            return JobCheckReturn.STAND_BY;
        }

        return JobCheckReturn.OK;
    }

    @Override
    public JobActionReturn onAction(CharacterModel character) {
        return JobActionReturn.CONTINUE;
    }

    @Override
    public void onQuit(CharacterModel character) {
//        throw new NotImplementedException("");

//        if (character.getInventory() != null) {
//            ModuleHelper.getWorldModule().putConsumable(character.getParcel(), character.getInventory());
//            character.setInventory(null);
//        }
//        _consumables.forEach(consumable -> consumable.setJob(null));
    }

    @Override
    public String getLabel() {
        return "Store " + _itemInfo.label;
    }
}
