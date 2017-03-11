package org.smallbox.faraway.modules.character.model.base;

import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.GameException;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.model.MovableModel;
import org.smallbox.faraway.core.game.modelInfo.CharacterInfo;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.module.job.check.old.CharacterCheck;
import org.smallbox.faraway.core.module.room.model.RoomModel;
import org.smallbox.faraway.core.module.world.model.ConsumableItem;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.characterBuff.BuffModel;
import org.smallbox.faraway.modules.character.model.CharacterTalentExtra;
import org.smallbox.faraway.modules.character.model.PathModel;
import org.smallbox.faraway.modules.character.model.TimeTableModel;
import org.smallbox.faraway.modules.job.JobModel;
import org.smallbox.faraway.util.CollectionUtils;
import org.smallbox.faraway.util.Constant;
import org.smallbox.faraway.util.Log;
import org.smallbox.faraway.util.MoveListener;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class CharacterModel extends MovableModel {

    private PathModel _path;
    private Map<ItemInfo, Integer> _inventory2 = new ConcurrentHashMap<>();

    protected CharacterPersonalsExtra           _personals;
    protected CharacterStatsExtra               _stats;
    protected CharacterTalentExtra              _talents;
    protected CharacterNeedsExtra               _needs;

    private TimeTableModel                      _timeTable;
    protected boolean                           _isSelected;
    protected int                               _lag;
    protected RoomModel                         _quarter;
    protected boolean                           _needRefresh;
    protected ConsumableItem                    _inventory;
    protected MoveListener                      _moveListener;
    protected boolean                           _isFaint;
    private double                              _moveStep;
    private Collection<BuffModel>      _buffs;
    private Collection<CharacterCheck>          _needsCheck;
    protected CharacterInfo                     _type;
    private boolean                             _isSleeping;
    private Map<Class, Object>                  _extra = new ConcurrentHashMap<>();

    public CharacterModel(int id, ParcelModel parcel, String name, String lastName, double old, CharacterInfo type) {
        super(id, parcel);

        Log.info("Character #" + id);

        _type = type;
        _buffs = new ConcurrentLinkedQueue<>();
        _needsCheck = new ConcurrentLinkedQueue<>();
        _timeTable = new TimeTableModel(Application.gameManager.getGame().getPlanet().getInfo().dayDuration);
        _lag = (int)(Math.random() * 10);
        _isSelected = false;
        _direction = Direction.NONE;
        _personals = new CharacterPersonalsExtra(name, lastName);
        _personals.setOld(old);

        _talents = new CharacterTalentExtra();

//        _equipments = new ArrayList<>();
//        _equipments.addSubJob(Application.data.getEquipment("base.equipments.regular_shirt"));
//        _equipments.addSubJob(Application.data.getEquipment("base.equipments.regular_pants"));
//        _equipments.addSubJob(Application.data.getEquipment("base.equipments.regular_shoes"));
//        _equipments.addSubJob(Application.data.getEquipment("base.equipments.oxygen_bottle"));
//        _equipments.addSubJob(Application.data.getEquipment("base.equipments.fremen_body"));

        _stats = new CharacterStatsExtra();
        _stats.speed = 1;

        _needs = new CharacterNeedsExtra(_type.needs);

//        Log.info("Character done: " + _info.getName() + " (" + x + ", " + y + ")");
    }

    public <T> T                        getExtra(Class<T> cls) { return (T) _extra.get(cls); }
    public CharacterTalentExtra         getTalents() { return _talents; }
    public CharacterStatsExtra          getStats() { return _stats; }
    public CharacterPersonalsExtra      getPersonals() { return _personals; }
    public JobModel                     getJob() { return _job; }
    public ParcelModel                  getParcel() { return _parcel; }
    public ConsumableItem               getInventory() {
        throw new NotImplementedException();
    }
    public int                          getInventoryQuantity(ItemInfo itemInfo) {
        for (Map.Entry<ItemInfo, Integer> entry: _inventory2.entrySet()) {
            if (entry.getKey().instanceOf(itemInfo)) {
                return entry.getValue();
            }
        }
        return 0;
    }
    public Map<ItemInfo, Integer>       getInventory2() { return _inventory2; }
    public CharacterInfo                getType() { return _type; }
    public TimeTableModel               getTimetable() { return _timeTable; }
    public abstract String              getName();
    public Collection<BuffModel>     getBuffs() { return _buffs; }

    public abstract void                addBodyStats(CharacterStatsExtra stats);
    public void                         addBuff(BuffModel buff) { _buffs.add(buff); }

    public void                         setSelected(boolean selected) { _isSelected = selected; }
    public void                         setIsFaint() { _isFaint = true; }
    public void                         setQuarter(RoomModel quarter) { _quarter = quarter; }
    public void                         setId(int id) { _id = id; }
    public void                         setIsDead() {
        _stats.isAlive = false;
    }
    public void                         setParcel(ParcelModel parcel) {
        if (parcel == null) {
            throw new GameException(CharacterModel.class, "setParcel: cannot be null");
        }
        _parcel = parcel;
    }

    public boolean                      isSelected() { return _isSelected; }
    public boolean                      isAlive() { return _stats.isAlive; }
    public boolean                      isDead() { return !_stats.isAlive; }
    public boolean                      isSleeping() { return _isSleeping; }
    //    public boolean                      isSleeping() { return _job != null && _job instanceof SleepJob && _job.getTargetParcel() == _parcel; }
    public boolean                      needRefresh() { return _needRefresh; }

    public ParcelModel moveApprox(ParcelModel targetParcel, MoveListener<CharacterModel> listener) {
        PathModel path = Application.pathManager.getPath(_parcel, targetParcel, true, false);

        // No path to target parcel
        if (path == null) {
            if (listener != null) {
                listener.onFail(this);
            }
            return null;
        }

        // Move character to target parcel
        move(path, listener);
        return path.getLastParcel();
    }

    public void move(PathModel path) {
        move(path, null);
    }

    public void move(PathModel path, MoveListener<CharacterModel> listener) {
        if (_moveListener != null) {
            Log.debug("[" + getName() + "] Cancel previous move listener");
            _moveListener.onFail(this);
            _moveListener = null;
        }

        if (path != null) {
            // Already on position
            if (path.getLength() == 0) {
                if (listener != null) {
                    listener.onReach(this);
                }
                return;
            }

            _path = path;
            _moveListener = listener;
        }
    }

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

        // Déjà à la position désirée
        if (parcel == _parcel) {
            if (listener != null) {
                listener.onReach(this);
            }
            return true;
        }

        // Déjà entrain de se déplacer vers la postion
        if (_path != null && _path.getLastParcel() == parcel) {
            return false;
        }

        if (_moveListener != null) {
            Log.debug("[" + getName() + "] Cancel previous move listener");
            _moveListener.onFail(this);
            _moveListener = null;
        }

        _path = Application.pathManager.getPath(_parcel, parcel, false, false);
        if (_path != null) {
            _moveListener = listener;
        } else if (listener != null) {
            listener.onFail(this);
        }

        return false;
    }

    public void fixPosition() {
        if (_parcel != null && !_parcel.isWalkable()) {
            Log.warning(getName() + " is stuck !");
            setParcel(WorldHelper.getNearestWalkable(_parcel, 1, 20));
            if (_path != null) {
                _path = null;
                _moveListener = null;
            }
            if (_job != null) {
                _job.quit(this);
                _job = null;
            }
        }
    }

    public void    setJob(JobModel job) {
        assert _job == null;
        assert job != null;

        _job = job;
    }

    public void  longUpdate() {
        _personals.setOld(_personals.getOld() + Constant.CHARACTER_GROW_PER_UPDATE * Constant.SLOW_UPDATE_INTERVAL);

        if (_personals.getOld()> Constant.CHARACTER_MAX_OLD) {
            _stats.isAlive = false;
        }

//        // Find quarter
//        if (_quarter == null) {
//            Game.getRoomManager().take(this, Room.Type.QUARTER);
//        }
    }

    public void        move() {
        if (_path != null) {
            // Character is sleeping
            if (_isSleeping) {
                Log.debug("Character #" + _id + ": sleeping . move canceled");
                return;
            }

            // Increase move progress
            _moveStep = 1 * _stats.speed * (_job != null ? _job.getSpeedModifier() : 1);
            _moveProgress += _moveStep;

            // Character has reach next parcel
            if (_moveProgress >= 1 && _path.getCurrentParcel() != null) {
                _moveProgress = 0;
                setParcel(_path.getCurrentParcel());

                // Move continue, set next parcel + direction
                if (_path.next()) {
                    int fromX = _parcel.x;
                    int fromY = _parcel.y;
                    int toX = _path.getCurrentParcel().x;
                    int toY = _path.getCurrentParcel().y;
                    if (toX > fromX && toY > fromY) _direction = Direction.BOTTOM_RIGHT;
                    else if (toX < fromX && toY > fromY) _direction = Direction.BOTTOM_LEFT;
                    else if (toX > fromX && toY < fromY) _direction = Direction.TOP_RIGHT;
                    else if (toX < fromX && toY < fromY) _direction = Direction.TOP_LEFT;
                    else if (toX > fromX) _direction = Direction.RIGHT;
                    else if (toX < fromX) _direction = Direction.LEFT;
                    else if (toY > fromY) _direction = Direction.BOTTOM;
                    else if (toY < fromY) _direction = Direction.TOP;
                    else _direction = Direction.NONE;
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


        }
    }

    public void            action() {
        if (_job != null) {
            _job.action(this);
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
        return _personals.getFirstName() + " " + _personals.getLastName();
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
        int inventoryQuantity = _inventory2.containsKey(itemInfo) ? _inventory2.get(itemInfo) : 0;
        if (inventoryQuantity + quantity > 0) {
            if (inventoryQuantity + quantity < 0) {
                throw new GameException(CharacterModel.class, "Character inventory quantity cannot be < 0");
            }

            _inventory2.put(itemInfo, inventoryQuantity + quantity);
        } else {
            _inventory2.remove(itemInfo);
        }
    }

    public ConsumableItem takeInventory(ItemInfo itemInfo, int needQuantity) {
        int availableQuantity = getInventoryQuantity(itemInfo);
        int quantityToRemove = Math.min(needQuantity, availableQuantity);

        // Delete consumable from character inventory
        if (needQuantity == availableQuantity) {
            _inventory2.remove(itemInfo);
        }

        // Or update quantity
        else {
            _inventory2.put(itemInfo, availableQuantity - needQuantity);
        }

        return new ConsumableItem(itemInfo, quantityToRemove);
    }

    public <T> T addExtra(T extra) {
        _extra.put(extra.getClass(), extra);
        return extra;
    }
}
