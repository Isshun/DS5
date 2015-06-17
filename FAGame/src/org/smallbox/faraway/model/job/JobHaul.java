package org.smallbox.faraway.model.job;

import org.smallbox.faraway.Game;
import org.smallbox.faraway.engine.util.Log;
import org.smallbox.faraway.manager.JobManager;
import org.smallbox.faraway.model.GameData;
import org.smallbox.faraway.model.character.base.CharacterModel;
import org.smallbox.faraway.model.item.ConsumableModel;
import org.smallbox.faraway.model.item.ItemInfo;
import org.smallbox.faraway.model.item.ParcelModel;

import java.util.ArrayList;
import java.util.List;

public class JobHaul extends JobModel {
    private enum Mode {MOVE_TO_CONSUMABLE, MOVE_TO_STORAGE}

    private List<ConsumableModel>   _consumables = new ArrayList<>();
    private StorageModel	        _storage;
    private ParcelModel 	        _parcel;
    private Mode 			        _mode;
    private int                     _quantity;
    private ItemInfo                _itemInfo;

    private JobHaul(int x, int y) {
        super(null, x, y);
    }

    public static JobHaul create(ConsumableModel consumable) {
        if (consumable == null) {
            Log.error("create JobHaul: consumable cannot be null");
            return null;
        }

        JobHaul job = new JobHaul(consumable.getX(), consumable.getY());
        job._mode = Mode.MOVE_TO_CONSUMABLE;
        job._itemInfo = consumable.getInfo();
        job.getItemAround();

        // No consumables to haul
        if (job._consumables.isEmpty()) {
            return null;
        }

        job.foundStorageParcel(job._consumables.get(0));
        job.refreshJob();

        return job;
    }

    public void getItemAround() {
        getItemAround(_posX, _posY);
    }

    private void getItemAround(int startX, int startY) {
        int fromX = startX - 5;
        int fromY = startY - 5;
        int toX = startX + 5;
        int toY = startY + 5;
        for (int x = fromX; x <= toX; x++) {
            for (int y = fromY; y <= toY; y++) {
                ConsumableModel c = Game.getWorldManager().getConsumable(x, y);
                if (c != null && c.getInfo() == _itemInfo && c.getHaul() == null && _quantity + c.getQuantity() <= GameData.config.inventoryMaxQuantity) {
                    c.setHaul(this);
                    _quantity += c.getQuantity();
                    _consumables.add(c);
                }
            }
        }
    }

    private boolean foundStorageParcel(ConsumableModel consumable) {
        _storage = Game.getInstance().getAreaManager().getNearestFreeStorage(consumable, consumable.getX(), consumable.getY());
        if (_storage != null) {
            _parcel = _storage.getNearestFreeParcel(consumable, consumable.getX(), consumable.getY());
            if (_parcel != null) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean check(CharacterModel character) {
        // Consumable has moved
        if (_mode == Mode.MOVE_TO_CONSUMABLE && !_consumables.get(0).matchPosition(_posX, _posY)) {
            _consumables.remove(0);
            refreshJob();
            return true;
        }

        // Check storage area
        if (_storage != null && _parcel != null) {
            return true;
        }

        // Get storage area
        if (foundStorageParcel(_consumables.get(0))) {
            return true;
        }

        // Unable to find free storage
        _reason = JobModel.JobAbortReason.INVALID;
        return false;
    }

    // TODO: add inventory filter
    @Override
    public boolean action(CharacterModel character) {
        if (_storage == null) {
            Log.error("JobHaul: null storage");
            JobManager.getInstance().quit(this, JobAbortReason.INVALID);
            return true;
        }

        // Reach consumable
        if (_mode == Mode.MOVE_TO_CONSUMABLE) {
            ConsumableModel consumable = _consumables.get(0);
            Log.info("Haul consumable: " + consumable.getInfo().label);

            consumable.getParcel().setConsumable(null);
            if (_character.getInventory() == null) {
                _character.setInventory(consumable);
            } else if (_character.getInventory().getInfo() == consumable.getInfo()) {
                _character.getInventory().addQuantity(consumable.getQuantity());
            } else {
                Log.error("JobHaul: character inventory must be empty");
                JobManager.getInstance().quit(this, JobAbortReason.INVALID);
                return true;
            }
            _consumables.remove(0);

            refreshJob();

            return false;
        }

        // Reach storage
        if (_mode == Mode.MOVE_TO_STORAGE) {
            if (_character.getInventory() == null) {
                Log.error("Character reach storage with empty inventory");
                JobManager.getInstance().close(this);
                return true;
            }
            if (_parcel != null && _parcel.getConsumable() == null) {
                Game.getWorldManager().putConsumable(_character.getInventory(), _parcel.getX(), _parcel.getY());
                _character.setInventory(null);
                JobManager.getInstance().close(this);
                return true;
            }
            if (_parcel != null && _parcel.getConsumable().getInfo() == _character.getInventory().getInfo()) {
                int freeQuantity = GameData.config.storageMaxQuantity - _parcel.getConsumable().getQuantity();
                // Store all inventory consumables on storage area
                if (freeQuantity >= _character.getInventory().getQuantity()) {
                    _parcel.getConsumable().addQuantity(_character.getInventory().getQuantity());
                    _character.setInventory(null);
                    JobManager.getInstance().close(this);
                    return true;
                }
                // Store some inventory consumables
                else {
                    _parcel.getConsumable().addQuantity(freeQuantity);
                    _character.getInventory().addQuantity(-freeQuantity);
                }
            }

            // Found another free storage area
            if (foundStorageParcel(_character.getInventory())) {
                _posX = _parcel.getX();
                _posY = _parcel.getY();
                Log.info("Continue job to: " + _posX + "x" + _posY + ", left: " + _character.getInventory().getQuantity());
                _character.moveTo(this, _posX, _posY, null);
                return false;
            }
            // No empty space in any storage
            else {
                Log.error("No empty space in any storage");
                Game.getWorldManager().putConsumable(_character.getInventory(), _character.getX(), _character.getY());
                _character.setInventory(null);
                JobManager.getInstance().close(this);
            }
        }

        return true;
    }

    private void refreshJob() {
        // Go to next consumable
        if (!_consumables.isEmpty()) {
            _mode = Mode.MOVE_TO_CONSUMABLE;
            _posX = _consumables.get(0).getX();
            _posY = _consumables.get(0).getY();
        }

        // Go to storage
        else {
            _mode = Mode.MOVE_TO_STORAGE;
            _posX = _parcel.getX();
            _posY = _parcel.getY();
        }

        if (_character != null) {
            _character.moveTo(this, _posX, _posY, null);
        }
    }

    @Override
    public String getType() {
        return "store";
    }

    @Override
    public boolean canBeResume() {
        return false;
    }

    @Override
    public CharacterModel.TalentType getTalentNeeded() {
        return CharacterModel.TalentType.HAUL;
    }

    @Override
    public String getLabel() {
        return "store";
    }

    @Override
    public String getShortLabel() {
        return "store";
    }

}
