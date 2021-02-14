package org.smallbox.faraway.game.consumable;

import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.world.model.ItemFilter;
import org.smallbox.faraway.core.world.model.MapObjectModel;
import org.smallbox.faraway.game.storage.StoreJob;
import org.smallbox.faraway.game.world.Parcel;
import org.smallbox.faraway.game.world.WorldHelper;
import org.smallbox.faraway.util.GameException;
import org.smallbox.faraway.util.log.Log;

import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Consumable extends MapObjectModel {
    private StoreJob storeJob;
    private int actualQuantity;
    private int _totalQuantity;
    private final Collection<ConsumableModule.ConsumableJobLock>    _locks = new ConcurrentLinkedQueue<>();
    private int gridPosition;

    public Consumable(ItemInfo info) {
        super(info);
    }

    public Consumable(ItemInfo info, int quantity) {
        super(info);
        _totalQuantity = actualQuantity = quantity;
    }

    public Consumable(ItemInfo info, int quantity, int gridPosition) {
        super(info);
        _totalQuantity = actualQuantity = quantity;
        this.gridPosition = gridPosition;
    }

    public StoreJob getStoreJob() {
        return storeJob;
    }

    public void setStoreJob(StoreJob storeJob) {
        this.storeJob = storeJob;
    }

    public void removeStoreJob(StoreJob jobToRemove) {
        if (this.storeJob == jobToRemove) {
            this.storeJob = null;
        }
    }

    public int addQuantity(int quantity) {

        Log.debug(Consumable.class, "AddQuantity (consumable: %s, quantity: %d, quantity to add: %d)", this, actualQuantity, quantity);

        actualQuantity += quantity;
        _totalQuantity = actualQuantity + _locks.stream().mapToInt(l -> l.quantity).sum();

        if (_totalQuantity > getInfo().stack) {
            int diff = _totalQuantity - getInfo().stack;
            _totalQuantity = getInfo().stack;
            actualQuantity -= diff;
            return diff;
        }

        if (actualQuantity < 0) {
            throw new GameException(Consumable.class, "freeQuantity cannot be < 0", this, actualQuantity);
        }

        return 0;
    }

    public int getActualQuantity() {
        return actualQuantity;
    }

    public void setQuantity(int quantity) {

        Log.debug(Consumable.class, "SetQuantity (consumable: %s, quantity: %d, quantity to set: %d)", this, actualQuantity, quantity);

        actualQuantity = quantity;
        _totalQuantity = actualQuantity + _locks.stream().mapToInt(l -> l.quantity).sum();

        if (actualQuantity < 0) {
            throw new GameException(Consumable.class, "freeQuantity cannot be < 0", this, actualQuantity);
        }
    }

    @Override
    public boolean matchFilter(ItemFilter filter) {
        if (_info.consume != null && _info.consume.effects != null) {
            if (filter.effectDrink && _info.consume.effects.drink == 0) return false;
            if (filter.effectFood && _info.consume.effects.food == 0) return false;
            if (filter.effectEnergy && _info.consume.effects.energy == 0) return false;
            if (filter.effectEntertainment && _info.consume.effects.entertainment == 0) return false;
            if (filter.effectHappiness && _info.consume.effects.happiness == 0) return false;
            if (filter.effectHealth && _info.consume.effects.health == 0) return false;
            return !(filter.effectRelation && _info.consume.effects.relation == 0);
        }
        return false;
    }

    public boolean isEmpty() { return actualQuantity <= 0; }

    public void fixPosition() {
        if (_parcel != null && !_parcel.isWalkable()) {
            Parcel parcel = WorldHelper.getNearestFreeParcel(_parcel, true, false);
            if (parcel != null) {
                _parcel = parcel;
            }
        }
    }

    @Override
    public String toString() { return _info.name + " at " + _parcel; }

    public void removeQuantity(int quantity) {
        actualQuantity -= quantity;
        _totalQuantity -= quantity;

        if (actualQuantity < 0 || _totalQuantity < 0) {
            throw new GameException(Consumable.class, "quantity cannot be < 0", this, actualQuantity, _totalQuantity);
        }
    }

    public void addLock(ConsumableModule.ConsumableJobLock lock) {
        Log.debug(Consumable.class, "addLock: %s", lock);

        _locks.add(lock);
        _totalQuantity = actualQuantity + _locks.stream().mapToInt(l -> l.quantity).sum();
    }

    public boolean hasLock() {
        return !_locks.isEmpty();
    }

    public boolean hasLock(ConsumableModule.ConsumableJobLock lock) {
        return _locks.contains(lock);
    }

    public int getTotalQuantity() {
        return _totalQuantity;
    }

    public void removeLock(ConsumableModule.ConsumableJobLock lock) {
        Log.debug(Consumable.class, "removeLock: %s", lock);

        if (!_locks.contains(lock)) {
            throw new GameException(Consumable.class, "RemoveLock: lock doesn't exists in consumable");
        }

        _locks.remove(lock);
        _totalQuantity = actualQuantity + _locks.stream().mapToInt(l -> l.quantity).sum();
    }

    public int getGridPosition() {
        return gridPosition;
    }

    public int getFreeSpace() {
        return _info.stack - _totalQuantity;
    }
}
