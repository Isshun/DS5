package org.smallbox.faraway.game.model.job;

import org.smallbox.faraway.core.drawable.IconDrawable;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.GameObserver;
import org.smallbox.faraway.game.helper.WorldHelper;
import org.smallbox.faraway.game.module.character.JobModule;
import org.smallbox.faraway.game.module.path.PathManager;
import org.smallbox.faraway.game.module.world.AreaModule;
import org.smallbox.faraway.game.model.GameData;
import org.smallbox.faraway.game.model.area.StorageAreaModel;
import org.smallbox.faraway.game.model.character.base.CharacterModel;
import org.smallbox.faraway.game.model.item.ConsumableModel;
import org.smallbox.faraway.game.model.item.ItemInfo;
import org.smallbox.faraway.game.model.item.ParcelModel;
import org.smallbox.faraway.util.Log;

import java.util.ArrayList;
import java.util.List;

public class JobHaul extends BaseJobModel implements GameObserver {
    private enum Mode {MOVE_TO_CONSUMABLE, MOVE_TO_STORAGE}

    private List<ConsumableModel>   _consumables = new ArrayList<>();
    private StorageAreaModel        _storage;
    private ParcelModel 	        _parcel;
    private Mode 			        _mode;
    private int                     _quantity;
    private ItemInfo                _itemInfo;
    private ParcelModel             _consumableParcel;

    private JobHaul(int x, int y) {
        super(null, x, y, new IconDrawable("data/res/ic_haul.png", 0, 0, 32, 32), null);
    }

    public static JobHaul create(ConsumableModel consumable) {
        if (consumable == null) {
            Log.error("onCreate JobHaul: consumable cannot be null");
            return null;
        }

        JobHaul job = new JobHaul(consumable.getX(), consumable.getY());
        job._consumableParcel = consumable.getParcel();
        job._mode = Mode.MOVE_TO_CONSUMABLE;
        job._itemInfo = consumable.getInfo();
        job.setStrategy(j -> {
            if (j.getCharacter().getType().needs.joy != null) {
                j.getCharacter().getNeeds().joy += j.getCharacter().getType().needs.joy.change.work;
            }
        });
        job.getItemAround();

        // No consumables to haul
        if (job._consumables.isEmpty()) {
            return null;
        }

        job.foundStorageParcel(job._consumables.get(0));

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
                ConsumableModel consumable = WorldHelper.getConsumable(x, y);
                if (consumable != null
                        && consumable.getLock() == null
                        && consumable.getInfo() == _itemInfo
                        && consumable.getHaul() == null
                        && consumable.getQuantity() + _quantity <= GameData.config.inventoryMaxQuantity
                        && (consumable.getParcel() == _consumableParcel || PathManager.getInstance().getPath(consumable.getParcel(), _consumableParcel) != null)) {
                    _quantity += consumable.getQuantity();
                    _consumables.add(consumable);
                    consumable.addJob(this);
                    consumable.setHaul(this);
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

    private boolean foundStorageParcel(ConsumableModel consumable) {
        _parcel = null;
        _storage = ((AreaModule)Game.getInstance().getModule(AreaModule.class)).getNearestFreeStorage(consumable, consumable.getParcel());
        if (_storage != null) {
            _parcel = _storage.getNearestFreeParcel(consumable, consumable.getX(), consumable.getY());
            if (_parcel != null) {
                _message = "Move to " + _storage.getName();
                return true;
            }
        }
        return false;
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
            _posX = _parcel.x;
            _posY = _parcel.y;
        }

        if (_character != null) {
            _character.moveTo(this, _posX, _posY, null);
        }
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
    public void onQuit(CharacterModel character) {
        if (character.getInventory() != null) {
            Game.getWorldManager().putConsumable(character.getInventory(), character.getX(), character.getY());
            character.setInventory(null);
        }
        _consumables.forEach(consumable -> consumable.lock(null));
    }

    @Override
    protected void onStart(CharacterModel character) {
        super.onStart(character);

        // Lock items
        _consumables.forEach(c -> {
            c.lock(this);
            c.setHaul(this);
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
        if ((_storage == null || _parcel == null || (_parcel.getConsumable() != null && _parcel.getConsumable().getInfo() != _itemInfo)) && !foundStorageParcel(_consumables.get(0))) {
            _message = "No storage area";
            return false;
        }

        return true;
    }

    @Override
    protected void onFinish() {

    }

    // TODO: add inventory filter
    @Override
    public JobActionReturn onAction(CharacterModel character) {
        if (_storage == null) {
            Log.error("JobHaul: null storage");
            JobModule.getInstance().quitJob(this, JobAbortReason.INVALID);
            return JobActionReturn.ABORT;
        }

        // Reach consumable
        if (_mode == Mode.MOVE_TO_CONSUMABLE) {
            ConsumableModel consumable = _consumables.get(0);
            Log.info("Haul consumable: " + consumable.getInfo().label);

            // TODO: check characters inventory free space
            if (_character.getInventory() == null) {
                _character.setInventory(consumable);
                Game.getWorldManager().removeConsumable(consumable);
            } else if (_character.getInventory().getInfo() == consumable.getInfo()) {
                _character.getInventory().addQuantity(consumable.getQuantity());
                consumable.setQuantity(0);
                if (consumable.isEmpty()) {
                    Game.getWorldManager().removeConsumable(consumable);
                }
            } else {
                Log.error("JobHaul: characters inventory must be empty");
                JobModule.getInstance().quitJob(this, JobAbortReason.INVALID);
                return JobActionReturn.ABORT;
            }
            consumable.removeJob(this);
            consumable.setHaul(null);
            _consumables.remove(0);

            refreshJob();

            return JobActionReturn.CONTINUE;
        }

        // Reach storage
        if (_mode == Mode.MOVE_TO_STORAGE) {
            if (_character.getInventory() == null) {
                Log.error("Character reach storage with empty inventory");
                return JobActionReturn.ABORT;
            }
            if (_parcel != null && _parcel.getConsumable() == null) {
                Game.getWorldManager().putConsumable(_character.getInventory(), _parcel.x, _parcel.y);
                _character.setInventory(null);
                return JobActionReturn.FINISH;
            }
            if (_parcel != null && _parcel.getConsumable().getInfo() == _character.getInventory().getInfo()) {
                int freeQuantity = Math.max(GameData.config.storageMaxQuantity, _parcel.getConsumable().getInfo().stack) - _parcel.getConsumable().getQuantity();
                // Store all inventory consumables on storage area
                if (freeQuantity >= _character.getInventory().getQuantity()) {
                    _parcel.getConsumable().addQuantity(_character.getInventory().getQuantity());
                    _character.setInventory(null);
                    return JobActionReturn.FINISH;
                }
                // Store some inventory consumables
                else {
                    _parcel.getConsumable().addQuantity(freeQuantity);
                    _character.getInventory().addQuantity(-freeQuantity);
                }
            }

            // Found another free storage area
            if (foundStorageParcel(_character.getInventory())) {
                _posX = _parcel.x;
                _posY = _parcel.y;
                Log.info("Continue job to: " + _posX + "x" + _posY + ", left: " + _character.getInventory().getQuantity());
                _character.moveTo(this, _posX, _posY, null);
                return JobActionReturn.CONTINUE;
            }
            // No empty space in any storage
            else {
                Log.error("No empty space in any storage");
                Game.getWorldManager().putConsumable(_character.getInventory(), _character.getX(), _character.getY());
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
            consumable.setHaul(null);
        }
    }

    @Override
    public ParcelModel getActionParcel() {
        return null;
    }

}
