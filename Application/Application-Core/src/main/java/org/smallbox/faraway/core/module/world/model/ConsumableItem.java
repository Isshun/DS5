package org.smallbox.faraway.core.module.world.model;

import org.smallbox.faraway.core.GameException;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;
import org.smallbox.faraway.modules.consumable.ConsumableModule;
import org.smallbox.faraway.modules.job.JobModel;
import org.smallbox.faraway.util.Log;

import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Alex on 03/06/2015.
 */
public class ConsumableItem extends MapObjectModel {
    private int _freeQuantity = 1;
    private JobModel        _job;
    private Collection<ConsumableModule.ConsumableJobLock>    _locks = new ConcurrentLinkedQueue<>();

    public ConsumableItem(ItemInfo info) {
        super(info);
    }

    public ConsumableItem(ItemInfo info, int quantity) {
        super(info);
        _freeQuantity = quantity;
    }

    public void addQuantity(int quantity) {

        Log.debug(ConsumableItem.class, "AddQuantity (consumable: %s, quantity: %d, quantity to add: %d)", this, _freeQuantity, quantity);

        _freeQuantity += quantity;
        _needRefresh = true;

        if (_freeQuantity < 0) {
            throw new GameException(ConsumableItem.class, "freeQuantity cannot be < 0", this, _freeQuantity);
        }
    }

    public int getFreeQuantity() {
        return _freeQuantity;
    }

    public void setQuantity(int quantity) {

        Log.debug(ConsumableItem.class, "SetQuantity (consumable: %s, quantity: %d, quantity to set: %d)", this, _freeQuantity, quantity);

        _freeQuantity = quantity;
        _needRefresh = true;

        if (_freeQuantity < 0) {
            throw new GameException(ConsumableItem.class, "freeQuantity cannot be < 0", this, _freeQuantity);
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
            if (filter.effectRelation && _info.consume.effects.relation == 0) return false;
            return true;
        }
        return false;
    }

    public boolean isEmpty() { return _freeQuantity <= 0; }
    public void setJob(JobModel job) { _job = job; }
    public JobModel getJob() { return _job; }

    public void fixPosition() {
        if (_parcel != null && !_parcel.isWalkable()) {
            ParcelModel parcel = WorldHelper.getNearestFreeParcel(_parcel, true, false);
            if (parcel != null) {
                _parcel = parcel;
            }
        }
    }

    @Override
    public String toString() { return _info.name + " at " + _parcel; }

    public void removeQuantity(int quantity) {
        _freeQuantity = _freeQuantity - quantity;

        if (_freeQuantity < 0) {
            throw new GameException(ConsumableItem.class, "freeQuantity cannot be < 0", this, _freeQuantity);
        }
    }

    public void addLock(ConsumableModule.ConsumableJobLock lock) {
        Log.debug(ConsumableItem.class, "addLock: %s", lock);

        _locks.add(lock);
    }

    public boolean hasLock() {
        return !_locks.isEmpty();
    }

    public int getTotalQuantity() {
        return _freeQuantity + _locks.stream().mapToInt(lock -> lock.quantity).sum();
    }

    public void removeLock(ConsumableModule.ConsumableJobLock lock) {
        Log.debug(ConsumableItem.class, "removeLock: %s", lock);

        if (!_locks.contains(lock)) {
            throw new GameException(ConsumableItem.class, "RemoveLock: lock doesn't exists in consumable");
        }

        _locks.remove(lock);
    }
}
