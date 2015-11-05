package org.smallbox.faraway.core.game.module.world.model.item;

import org.smallbox.faraway.core.data.ItemInfo;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.model.NetworkModel;
import org.smallbox.faraway.core.game.module.job.model.abs.JobModel;
import org.smallbox.faraway.core.game.module.world.model.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ItemModel extends BuildableMapObject {
    private int                         _targetTemperature = 21;
    private boolean                     _isFunctional = true;
    private boolean                     _isActive = true;
    private int                         _potencyUse;
    private List<ItemSlot>              _slots;
    private int                         _nbFreeSlot = -1;
    private int                         _nbSlot;
    private ItemFactoryModel            _factory;
    private int[]                       _storageSlot;
    private List<NetworkObjectModel>    _networkObjects;


    public ItemModel(ItemInfo info, ParcelModel parcel, int id) {
        super(info, id);
        _parcel = parcel;
        initSlots();
        init(info);
    }

    public ItemModel(ItemInfo info, ParcelModel parcel) {
        super(info);
        _parcel = parcel;
        initSlots();
        init(info);
    }

    public int                      getTargetTemperature() { return _targetTemperature; }
    public int                      getValue() { return 15; }
    public int                      getPotencyUse() { return _potencyUse; }
    public List<ItemSlot>           getSlots() { return _slots; }
    public int                      getNbFreeSlots() { return _nbFreeSlot; }
    public int                      getNbSlots() { return _nbSlot; }
    public ItemFactoryModel         getFactory() { return _factory; }
    public List<NetworkObjectModel> getNetworkObjects() { return _networkObjects; }

    public boolean                  hasFreeSlot() { return _nbFreeSlot == -1 || _nbFreeSlot > 0; }
    public boolean                  isFunctional() { return _isFunctional; }
    public boolean                  isActive() { return _isActive; }
    public boolean                  isBed() { return _info.isBed; }

    public void                     setTargetTemperature(int targetTemperature) { _targetTemperature = targetTemperature; }
    public void                     setFunctional(boolean isFunctional) { _isFunctional = isFunctional; }
    public void                     setPotencyUse(int potencyUse) { _potencyUse = potencyUse; }

    @Override
    public void             setParcel(ParcelModel parcel) {
        super.setParcel(parcel);
        initSlots();
    }

    private void init(ItemInfo info) {
        // Initialize factory extra object
        if (info.factory != null) {
            _factory = new ItemFactoryModel(this, info.factory);
            if (info.factory.outputSlots != null) {
                _storageSlot = info.factory.outputSlots;
            }
        }

        // Initialize network extra objects
        if (info.networks != null) {
            _networkObjects = info.networks.stream().map(network -> new NetworkObjectModel(network.network)).collect(Collectors.toList());
        }
    }

    public void initSlots() {
        _slots = _info.slots != null
                ? _info.slots.stream().map(slot -> new ItemSlot(this, WorldHelper.getParcel(_parcel.x + slot[0], _parcel.y + slot[1]))).filter(slot -> slot.getParcel() != null && slot.getParcel().isWalkable()).collect(Collectors.toList())
                : Collections.singletonList(new ItemSlot(this, _parcel));
        _nbSlot = _nbFreeSlot = _slots.size();
    }

    public ItemSlot takeSlot(JobModel job) {
        if (_slots != null) {
            for (ItemSlot slot : _slots) {
                if (slot.isFree()) {
                    slot.take(job);
                    _nbFreeSlot--;
                    return slot;
                }
            }
        }
        return null;
    }

    public void releaseSlot(ItemSlot slot) {
        slot.free();
        _nbFreeSlot = (int)_slots.stream().filter(ItemSlot::isFree).count();
    }

    @Override
    public boolean matchFilter(ItemFilter filter) {
        // Filter need free slots but item is busy
        if (filter.needFreeSlot && !hasFreeSlot()) {
            return false;
        }

        if (!_isComplete) {
            return false;
        }

        return super.matchFilter(filter);
    }

    public boolean isStorageParcel(ParcelModel parcel) {
        return _storageSlot != null && _parcel.x + _storageSlot[0] == parcel.x && _parcel.y + _storageSlot[1] == parcel.y;
    }
}
