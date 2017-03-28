package org.smallbox.faraway.modules.character.model.base;

import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.GameException;
import org.smallbox.faraway.core.game.model.MovableModel;
import org.smallbox.faraway.core.game.modelInfo.CharacterInfo;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.module.job.check.old.CharacterCheck;
import org.smallbox.faraway.core.module.path.PathManager;
import org.smallbox.faraway.core.module.world.model.ConsumableItem;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.character.model.PathModel;
import org.smallbox.faraway.modules.job.JobModel;
import org.smallbox.faraway.modules.room.model.RoomModel;
import org.smallbox.faraway.util.CollectionUtils;
import org.smallbox.faraway.util.Log;
import org.smallbox.faraway.util.MoveListener;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class CharacterModel extends MovableModel {

    public boolean                              _isAlive = true;
    private PathModel                           _path;
    private Map<ItemInfo, Integer>              _inventory;
    protected int                               _lag;
    protected RoomModel                         _quarter;
    protected boolean                           _needRefresh;
    protected MoveListener                      _moveListener;
    protected boolean                           _isFaint;
    private double                              _moveStep;
    private Collection<CharacterCheck>          _needsCheck;
    protected CharacterInfo                     _type;
    private boolean                             _isSleeping;
    protected Map<Class, Object>                _extra = new ConcurrentHashMap<>();

    public CharacterModel(int id, CharacterInfo characterInfo, ParcelModel parcel, String name, String lastName, double old) {
        super(id, parcel);

        Log.info("Character #" + id);

        _inventory = new ConcurrentHashMap<>();
        _type = characterInfo;
        _needsCheck = new ConcurrentLinkedQueue<>();
        _lag = (int)(Math.random() * 10);
        _direction = Direction.NONE;

//        _equipments = new ArrayList<>();
//        _equipments.addSubJob(Application.data.getEquipment("base.equipments.regular_shirt"));
//        _equipments.addSubJob(Application.data.getEquipment("base.equipments.regular_pants"));
//        _equipments.addSubJob(Application.data.getEquipment("base.equipments.regular_shoes"));
//        _equipments.addSubJob(Application.data.getEquipment("base.equipments.oxygen_bottle"));
//        _equipments.addSubJob(Application.data.getEquipment("base.equipments.fremen_body"));

//        Log.info("Character done: " + _info.getName() + " (" + x + ", " + y + ")");
    }

    public <T> T                        getExtra2(Class<T> cls) { return (T) _extra.get(cls); }
    public <T> T                        getExtra(Class<T> cls) { return (T) _extra.get(cls); }
    public JobModel                     getJob() { return _job; }
    public ParcelModel                  getParcel() { return _parcel; }
    public ConsumableItem               getInventory() {
        throw new NotImplementedException();
    }
    public int                          getInventoryQuantity(ItemInfo itemInfo) {
        for (Map.Entry<ItemInfo, Integer> entry: _inventory.entrySet()) {
            if (entry.getKey().instanceOf(itemInfo)) {
                return entry.getValue();
            }
        }
        return 0;
    }

    public Map<ItemInfo, Integer>       getInventory2() { return _inventory; }
    public CharacterInfo                getType() { return _type; }
    public abstract String              getName();

    public abstract void                addBodyStats(CharacterStatsExtra stats);

    public void                         setIsFaint() { _isFaint = true; }
    public void                         setQuarter(RoomModel quarter) { _quarter = quarter; }
    public void                         setIsDead() { _isAlive = false; }
    public void                         setParcel(ParcelModel parcel) {
        if (parcel == null) {
            throw new GameException(CharacterModel.class, "setParcel: cannot be null");
        }
        _parcel = parcel;
    }

    public boolean                      isAlive() { return _isAlive; }
    public boolean                      isDead() { return !_isAlive; }
    public boolean                      isSleeping() { return _isSleeping; }
    public boolean                      isFree() { return getJob() == null && _path == null; }
    //    public boolean                      isSleeping() { return _job != null && _job instanceof SleepJob && _job.getTargetParcel() == _parcel; }
    public boolean                      needRefresh() { return _needRefresh; }

    /**
     * Déplace le personnage à la position demandée
     *
     * @param parcel Destination
     * @return True si le personnage est déjà à la position voulue
     */
    public boolean moveTo(ParcelModel parcel) {
        assert parcel != null;

        return moveTo(parcel, null);
    }

    public boolean moveTo(ParcelModel parcel, MoveListener<CharacterModel> listener) {
        assert parcel != null;

        // Déjà entrain de se déplacer vers la postion
        if (_path != null && _path.getLastParcel() == parcel) {
            return false;
        }

        // Déjà à la position désirée
        if (_path == null && parcel == _parcel) {
            if (listener != null) {
                listener.onReach(this);
            }
            return true;
        }

        if (_moveListener != null) {
            Log.debug("[" + getName() + "] Cancel previous move listener");
            _moveListener.onFail(this);
            _moveListener = null;
        }

        _path = Application.dependencyInjector.getObject(PathManager.class).getPath(_parcel, parcel, false, false);
        _moveProgress2 = 0;
        if (_path != null) {
            _moveListener = listener;
        } else if (listener != null) {
            listener.onFail(this);
        }

        return false;
    }

    public void    setJob(JobModel job) {
        assert _job == null;
        assert job != null;

        _job = job;
    }

    public void        move() {
        if (_path != null) {
            // Character is sleeping
            if (_isSleeping) {
                Log.debug("Character #" + _id + ": sleeping . move canceled");
                return;
            }

            // Increase move progress
            _moveStep = Application.config.game.characterSpeed / Application.gameManager.getGame().getTickPerHour();
//            _moveStep = 1 * getExtra(CharacterStatsExtra.class).speed * (_job != null ? _job.getSpeedModifier() : 1);

            // Character has reach next parcel
            if (_moveProgress >= 1 && _path.getCurrentParcel() != null) {
                _moveProgress = 0;

                // Move continue, set next parcel + direction
                if (_path.next()) {
                    int fromX = _parcel.x;
                    int fromY = _parcel.y;
                    int toX = _path.getCurrentParcel().x;
                    int toY = _path.getCurrentParcel().y;
                    _direction = getDirection(fromX, fromY, toX, toY);

                    setParcel(_path.getCurrentParcel());
                }

                // Move state, set path to null and call listener
                else {
                    Log.info(getName() + " Move state (" + _path.getFirstParcel().x + "x" + _path.getFirstParcel().y + "x" + _path.getFirstParcel().z + " to " + _path.getLastParcel().x + "x" + _path.getLastParcel().y + "x" + _path.getLastParcel().z + ")");
                    _path = null;

                    if (_moveListener != null) {
                        Log.info(getName() + " Move state: call onReach");
                        MoveListener listener = _moveListener;
                        _moveListener = null;
                        listener.onReach(this);
                    }
                }
            }

            _moveProgress += _moveStep;
            _moveProgress2 += _moveStep;
        }
    }

    private Direction getDirection(int fromX, int fromY, int toX, int toY) {
        if (toX > fromX && toY > fromY) return Direction.BOTTOM_RIGHT;
        if (toX < fromX && toY > fromY) return Direction.BOTTOM_LEFT;
        if (toX > fromX && toY < fromY) return Direction.TOP_RIGHT;
        if (toX < fromX && toY < fromY) return Direction.TOP_LEFT;
        if (toX > fromX) return Direction.RIGHT;
        if (toX < fromX) return Direction.LEFT;
        if (toY > fromY) return Direction.BOTTOM;
        if (toY < fromY) return Direction.TOP;
        return Direction.NONE;
    }

    public void            action(double hourInterval) {
        if (_job != null) {
            _job.action(this, hourInterval);
        }
    }

    public void setSleeping(boolean isSleeping) {
        _isSleeping = isSleeping;
    }

    public void clearJob(JobModel job) {
        if (_job != job) {
            throw new GameException(CharacterModel.class, "clearJob: job not match character current job", _job, job, this);
        }

        _job = null;
        _moveListener = null;
        _path = null;
    }

    public String toString() {
        if (hasExtra(CharacterPersonalsExtra.class)) {
            return getExtra2(CharacterPersonalsExtra.class).getFirstName() + " " + getExtra2(CharacterPersonalsExtra.class).getLastName();
        }
        return "no name";
    }

    public void addNeed(CharacterCheck check) {
        if (CollectionUtils.notContains(_needsCheck, check)) {
            _needsCheck.add(check);
        }
    }

    public void removeNeed(CharacterCheck check) {
        _needsCheck.remove(check);
    }

    public boolean hasNeed(CharacterCheck check) {
        return _needsCheck.contains(check);
    }

    public Collection<CharacterCheck> getChecks() {
        return _needsCheck;
    }

    public void addInventory(String itemName, int quantity) {
        addInventory(Application.data.getItemInfo(itemName), quantity);
    }

    public void addInventory(ItemInfo itemInfo, int quantity) {
        int inventoryQuantity = _inventory.getOrDefault(itemInfo, 0);
        if (inventoryQuantity + quantity > 0) {
            if (inventoryQuantity + quantity < 0) {
                throw new GameException(CharacterModel.class, "Character inventory quantity cannot be < 0");
            }

            _inventory.put(itemInfo, inventoryQuantity + quantity);
        } else {
            _inventory.remove(itemInfo);
        }
    }

    public ConsumableItem takeInventory(ItemInfo itemInfo, int needQuantity) {
        int availableQuantity = getInventoryQuantity(itemInfo);
        int quantityToRemove = Math.min(needQuantity, availableQuantity);

        // Delete consumable from character inventory
        if (needQuantity == availableQuantity) {
            _inventory.remove(itemInfo);
        }

        // Or update quantity
        else {
            _inventory.put(itemInfo, availableQuantity - needQuantity);
        }

        return new ConsumableItem(itemInfo, quantityToRemove);
    }

    public PathModel getPath() {
        return _path;
    }

    public boolean hasExtra(Class<?> cls) {
        return _extra.containsKey(cls);
    }
}
